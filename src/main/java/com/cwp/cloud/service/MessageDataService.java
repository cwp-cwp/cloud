package com.cwp.cloud.service;

import com.cwp.cloud.bean.*;
import com.cwp.cloud.bean.user.*;
import com.cwp.cloud.mapper.MessageDataMapper;
import com.cwp.cloud.mapper.ModifyResultsMapper;
import com.cwp.cloud.mapper.UserMapper;
import com.cwp.cloud.utils.IdWorker;
import com.cwp.cloud.utils.JsonUtils;
import com.cwp.cloud.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消息处理业务逻辑层
 * Created by chen_wp on 2019-09-20.
 */
@Component
@Transactional
public class MessageDataService {

    @Autowired
    private MessageDataMapper messageDataMapper;

    @Autowired
    private ModifyResultsMapper modifyResultsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IdWorker idWorker;

    public void saveMessageData(MessageData messageData) {
        this.messageDataMapper.saveMessageData(messageData);
        if (messageData.getParkSpaceImages() != null && messageData.getParkSpaceImages().size() > 0) {
            for (ParkSpaceImages images : messageData.getParkSpaceImages()) {
                images.setPatrolCarNumber(messageData.getPatrolCarNumber());
                // TODO 由于使用了 mycat 分库分表，所以这里需要单独设置 id，还需要使用 parentId 做关联，使同一个 parentId 的数据被分配到同一个数据库节点
                images.setId(idWorker.nextId());
                images.setParentId(messageData.getId());
                this.messageDataMapper.saveParkSpaceImages(images);
            }
//            this.messageDataMapper.saveParkSpaceImages(messageData);
        }
    }

    public List<MessageData> getMessageData2(String patrolCarNumber, String areaName, String batchNumber, String parkNumber, String type, String pushStatus, Tag tag, String startTime, String endTime) {
        if (tag == null) {
            return this.messageDataMapper.getMessageData2(patrolCarNumber, areaName, batchNumber, parkNumber, type, pushStatus, null, startTime, endTime);
        } else {
            return this.messageDataMapper.getMessageData2(patrolCarNumber, areaName, batchNumber, parkNumber, type, pushStatus, String.valueOf(tag), startTime, endTime);
        }
    }

    public void updateCarNumber(String patrolCarNumber, String areaName, String batchNumber, String parkNumber, String newCarNumber) {
        this.messageDataMapper.updateCarNumber(patrolCarNumber, areaName, batchNumber, parkNumber, newCarNumber);
    }

    public List<String> getBatchNumByPatrolCarNumBetweenTime(String patrolCarNumber, String startTime, String endTime) {
        return this.messageDataMapper.getBatchNumByPatrolCarNumBetweenTime(patrolCarNumber, startTime, endTime);
    }

    public void saveBatchNumber(BatchNumber batchNumber) {
        this.messageDataMapper.saveBatchNumber(batchNumber);
    }

    public List<MessageData> getAllMessageDataByBatchNumber(String batchNumber) {
        return this.messageDataMapper.getAllMessageDataByBatchNumber(batchNumber);
    }

    public void updatePushStatusAndTime(MessageData messageData) {
        this.messageDataMapper.updatePushStatusAndTime(messageData);
    }

    public String getLastNewCarNumber(String patrolCarNumber, String parkNumber, String startTime, String endTime) {
        return this.messageDataMapper.getLastNewCarNumber(patrolCarNumber, parkNumber, startTime, endTime);
    }

    public List<MessageData> getMessageDataByUserId(int userId, OrderBy orderBy) {
        List<MessageData> messageDataList = this.messageDataMapper.getMessageDataByUserId(userId, String.valueOf(orderBy));
        for (MessageData messageData : messageDataList) {
            List<ParkSpaceImages> images = this.messageDataMapper.getImagesByBatchNumAndParkNum(messageData.getBatchNumber(), messageData.getParkNumber());
            messageData.setParkSpaceImages(images);
        }
        return messageDataList;
    }

    public List<ParkSpaceImages> getImagesByBatchNumAndParkNum(String batchNumber, String parkNumber) {
        List<ParkSpaceImages> parkSpaceImagesList = this.messageDataMapper.getImagesByBatchNumAndParkNum(batchNumber, parkNumber);
        return parkSpaceImagesList;
    }

    public List<MessageData> getUnPushMessageData(String startTime, String endTime) {
        return this.messageDataMapper.getUnPushMessageData(startTime, endTime);
    }

    /**
     * 修改车牌
     *
     * @param userId       操作员 id
     * @param newCarNumber 新车牌
     * @param batchNumber  批次号
     * @param parkNumber   车位号
     */
    public synchronized void modifyCarNumber(int userId, String newCarNumber, String batchNumber, String parkNumber) {
        LogUtils.info3("当前操作信息 userId = " + userId + " newCarNumber = " + newCarNumber + " batchNumber = " + batchNumber + " parkNumber = " + parkNumber);
        if (userId == 1) { // TODO 管理员不在这里修改车牌
            return;
        }
        if (newCarNumber == null || batchNumber == null || parkNumber == null || newCarNumber.trim().equals("") || batchNumber.trim().equals("") || parkNumber.trim().equals("")) {
            return;
        }
        String messageId = this.messageDataMapper.getMessageIdByBatchNumAndParkNum(batchNumber, parkNumber);
        if (messageId == null || messageId.trim().equals("")) {
            return;
        }
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
        // TODO 跳过逻辑
        if (newCarNumber != null && newCarNumber.trim().equals("跳过")) {
            this.messageDataMapper.updateAuditCarNumAndAuditTime(messageId, userId, newCarNumber, time); // 更新自己的任务表的那一条数据
            this.messageDataMapper.updateOwnFeedbackResult(messageId, userId, String.valueOf(FeedbackResult.SKIP), time); // 反馈结果为 跳过
            // 记录到异常表
            MessageData messageData = this.messageDataMapper.getMessageDataByMsgId(messageId);
            ModifyResults modifyResults = new ModifyResults(idWorker.nextId(), messageId, messageData.getPatrolCarNumber(), messageData.getBatchNumber(), messageData.getParkNumber(), messageData.getCarNumber(), userId, "", newCarNumber, time);
            this.modifyResultsMapper.saveModifyResults(modifyResults);
            LogUtils.info3("成功跳过...");
            // 比对另外 n 个操作员的修改结果, 如果队友已经修改了，并且没跳过，那就将队友的那一条数据也拉入异常记录表
            List<Working> anotherWorkingList = this.messageDataMapper.getWorkingByMsgIdAndUserId(messageId, userId); // 队友的修改数据
            if (anotherWorkingList != null && anotherWorkingList.size() == 1) { // 这种情况是有一个队友已经修改了
                String otherAuditCarNumber = anotherWorkingList.get(0).getAuditCarNumber(); // 另外一个操作员的修改结果
                if (otherAuditCarNumber != null && !otherAuditCarNumber.trim().equals("跳过")) { // 队友没跳过进入以下逻辑操作队友(如果队友也跳过，执行上面的逻辑保存自己的结果就可以，不需要操作队友)
                    ModifyResults teamModifyResults = new ModifyResults(idWorker.nextId(), messageId, messageData.getPatrolCarNumber(), messageData.getBatchNumber(), messageData.getParkNumber(), messageData.getCarNumber(), anotherWorkingList.get(0).getUserId(), "", anotherWorkingList.get(0).getAuditCarNumber(), anotherWorkingList.get(0).getAuditTime());
                    this.modifyResultsMapper.saveModifyResults(teamModifyResults);
                    this.messageDataMapper.updateOwnFeedbackResult(messageId, anotherWorkingList.get(0).getUserId(), String.valueOf(FeedbackResult.TEAMMATESKIP), time);
                    LogUtils.info3("成功跳过，并且把一个队友拉进坑...");
                }
            }
            if (anotherWorkingList != null && anotherWorkingList.size() == 2) { // 这种情况就是前面两个人修改结果不一致，并且都没跳过，但是第三个人选择跳过
                for (Working w : anotherWorkingList) {
                    ModifyResults teamModifyResults = new ModifyResults(idWorker.nextId(), messageId, messageData.getPatrolCarNumber(), messageData.getBatchNumber(), messageData.getParkNumber(), messageData.getCarNumber(), w.getUserId(), "", w.getAuditCarNumber(), w.getAuditTime());
                    this.modifyResultsMapper.saveModifyResults(teamModifyResults);
                    this.messageDataMapper.updateOwnFeedbackResult(messageId, w.getUserId(), String.valueOf(FeedbackResult.TEAMMATESKIP), time);
                    LogUtils.info3("成功跳过，并且把两个队友拉进坑...");
                }
            }
            return;
        }
        // TODO　不跳过逻辑
        this.messageDataMapper.updateAuditCarNumAndAuditTime(messageId, userId, newCarNumber, time); // 修改作业表的审核车牌
        // 每修改完一个，就比对另外 n 个操作员的修改结果，看看是否一致
        List<Working> anotherWorkingList = this.messageDataMapper.getWorkingByMsgIdAndUserId(messageId, userId);
        LogUtils.info3("另外 n 个操作员的记录 = " + JsonUtils.objectToJson(anotherWorkingList));
        if (anotherWorkingList != null && anotherWorkingList.size() == 1) { // size == 1 说明是第一次分配
            String otherAuditCarNumber = anotherWorkingList.get(0).getAuditCarNumber(); // 另外一个操作员的修改结果
            LogUtils.info3("另外一个操作员的 otherAuditCarNumber = " + otherAuditCarNumber);
            if (!"".equals(otherAuditCarNumber.trim()) && otherAuditCarNumber.trim().equals("跳过")) { // 自己没跳过，但队友跳过
                MessageData messageData = this.messageDataMapper.getMessageDataByMsgId(messageId);
                ModifyResults modifyResults = new ModifyResults(idWorker.nextId(), messageId, messageData.getPatrolCarNumber(), messageData.getBatchNumber(), messageData.getParkNumber(), messageData.getCarNumber(), userId, "", newCarNumber, time);
                this.modifyResultsMapper.saveModifyResults(modifyResults);
                this.messageDataMapper.updateOwnFeedbackResult(messageId, userId, String.valueOf(FeedbackResult.TEAMMATESKIP), time);
                LogUtils.info3("队友跳过了, 成功被队友拉进坑...");
            } else if (!"".equals(otherAuditCarNumber.trim()) && otherAuditCarNumber.equals(newCarNumber)) { // 不为空说明另外一个操作员已经修改了他自己的那一条作业数据
                // 两个操作员都一致，就修改数据库最终结果
                this.messageDataMapper.updateCarNumberByMessageId(messageId, newCarNumber, time);
                this.messageDataMapper.updateFeedbackResult(messageId, String.valueOf(FeedbackResult.YES), time);
            } else if (!"".equals(otherAuditCarNumber.trim()) && !otherAuditCarNumber.equals(newCarNumber)) { // 不为空并且结果还不一致，就得介入第三个操作员
                // 剔除分配过的那两个操作员
                Integer anotherUserId = this.messageDataMapper.getAnotherUserByMsgIdAnUserId(messageId, userId);
                List<User> userList = this.userMapper.getCheckInUser();
                List<User> otherUserList = new ArrayList<>();
                for (User user : userList) {
                    if (user.getId() != userId && user.getId() != anotherUserId) {
                        otherUserList.add(user);
                    }
                }
                Integer otherUserId = this.getUserId(otherUserList); // 随机抽取到的第三个操作员 id
                // 分配该数据给第三个操作员
//                String carNumber = this.messageDataMapper.getCarNumberByMessageId(messageId);
                MessageData messageData = this.messageDataMapper.getMessageDataByMsgId(messageId);
                Working working = new Working(idWorker.nextId(), messageData.getId(), messageId, otherUserId, time, messageData.getCarNumber(), "", "", null, "", 2);
                this.messageDataMapper.saveWorking(working);
                LogUtils.info3("分配给第三个操作员 working = " + JsonUtils.objectToJson(working));
            }
        }
        if (anotherWorkingList != null && anotherWorkingList.size() == 2) { // size == 2 说明是第二次分配
            // 比对另外两个操作员的修改结果, 如果有其中一个是相同的即可
            boolean flag = false;
            for (Working working : anotherWorkingList) {
                String carNumber = working.getAuditCarNumber();
                if (carNumber != null && !"".equals(carNumber.trim()) && carNumber.equals(newCarNumber)) {
                    this.messageDataMapper.updateCarNumberByMessageId(messageId, newCarNumber, time);
                    this.messageDataMapper.updateFeedbackResultByUser(messageId, working.getUserId(), userId, String.valueOf(FeedbackResult.YES), time);
                    this.messageDataMapper.updateFeedbackResultByUser2(messageId, working.getUserId(), userId, String.valueOf(FeedbackResult.NO), time);
                    flag = true;
                }
            }
            if (!flag) { // 如果没有一个是相同的, 那就 gg 了
                LogUtils.info3("三个操作员修改结果都不相同...gg...");
                MessageData messageData = this.messageDataMapper.getMessageDataByMsgId(messageId);
                for (Working working : anotherWorkingList) {
                    ModifyResults modifyResults = new ModifyResults(idWorker.nextId(), messageId, messageData.getPatrolCarNumber(), messageData.getBatchNumber(), messageData.getParkNumber(), messageData.getCarNumber(), working.getUserId(), "", working.getAuditCarNumber(), working.getAuditTime());
                    this.modifyResultsMapper.saveModifyResults(modifyResults);
                }
                ModifyResults modifyResults = new ModifyResults(idWorker.nextId(), messageId, messageData.getPatrolCarNumber(), messageData.getBatchNumber(), messageData.getParkNumber(), messageData.getCarNumber(), userId, "", newCarNumber, time);
                this.modifyResultsMapper.saveModifyResults(modifyResults);
                this.messageDataMapper.updateFeedbackResult(messageId, String.valueOf(FeedbackResult.UNKNOWN), time);
            }
        }
    }

    private Integer getUserId(List<User> checkInUser) {
        if (checkInUser.size() == 0) {
            return 1;
        }
        int count = checkInUser.size();
        int a = (int) (Math.random() * count + 1);
        return checkInUser.get(a - 1).getId();
    }

    public List<String> getMessageIdByBatchNumAndPatCarNum(String batchNumber, String patrolCarNumber) {
        return this.messageDataMapper.getMessageIdByBatchNumAndPatCarNum(batchNumber, patrolCarNumber);
    }

    public List<ModifyResults> getModifyResults(Integer userId, String startTime, String endTime) {
        return this.modifyResultsMapper.getModifyResults(userId, startTime, endTime);
    }

    public List<ModifyResults> getDistinctModifyResults(String startTime, String endTime) {
        return this.modifyResultsMapper.getDistinctModifyResults(startTime, endTime);
    }

    public List<CheckUserInfo> getCheckUserInfoList(String messageId) {
        return this.modifyResultsMapper.getCheckUserInfoList(messageId);
    }

    public MessageData getMessageDataByMsgId(String messageId) {
        return this.messageDataMapper.getMessageDataByMsgId(messageId);
    }
}
