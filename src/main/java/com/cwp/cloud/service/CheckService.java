package com.cwp.cloud.service;

import com.cwp.cloud.bean.Distribution;
import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.PageResult;
import com.cwp.cloud.bean.Status;
import com.cwp.cloud.bean.user.*;
import com.cwp.cloud.mapper.MessageDataMapper;
import com.cwp.cloud.mapper.ModifyResultsMapper;
import com.cwp.cloud.mapper.UserMapper;
import com.cwp.cloud.utils.DateUtils;
import com.cwp.cloud.utils.IdWorker;
import com.cwp.cloud.utils.JsonUtils;
import com.cwp.cloud.utils.LogUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 任务管理业务逻辑层
 * Created by chen_wp on 2019-09-23.
 */
@Component
@Transactional
public class CheckService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageDataMapper messageDataMapper;

    @Autowired
    private ModifyResultsMapper modifyResultsMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 任务分配机制，操作员手动获取，不由系统自动分配
     * 先查找已经分配过一次的数据，如果没有，就查找未分配过的数据
     */
    public synchronized void getOneTask(Integer userId, String batchNumber) {
        LogUtils.info4("进入分配任务方法... userId = " + userId + " batchNumber = " + batchNumber);
        if (userId == 1) {
            return; // TODO 管理员不在这里获取任务
        }
        // 签退之后就不会再发任务，不然一直都会有最后一条任务没做完
        User user = this.userMapper.getUserById(userId);
        if ((user != null && user.getWorkStatus() != null && !user.getWorkStatus().trim().equals("checkIn")) || (user != null && (user.getWorkStatus() == null || user.getWorkStatus().trim().equals("")))) {
            return;
        }
        // 因为两个人审核结果不相同，会分配给第三个人，如果没有第三个人，就会分配给管理员，所以先从管理员那获取任务(这个任务肯定是两个人审核结果不一致的任务)
        List<Working> adminWorkings = this.messageDataMapper.getWorkingFromAdmin(DateUtils.getDateStartPoint(-1), DateUtils.getDateEndPoint(0));
        LogUtils.info4("管理员的任务 adminWorkings = " + JsonUtils.objectToJson(adminWorkings));
        for (Working w : adminWorkings) {
            List<Working> workingByMessageId = this.messageDataMapper.getWorkingByMessageId(w.getMessageId());
            List<Integer> userIds = new ArrayList<>();
            for (Working w1 : workingByMessageId) {
                userIds.add(w1.getUserId());
            }
            LogUtils.info4("这一组任务的 userIds = " + JsonUtils.objectToJson(userIds));
            if (!userIds.contains(userId)) {
                this.messageDataMapper.updateWorkingUserId(w.getMessageId(), userId);
                this.messageDataMapper.updateDisTrAndTimeAndThreeUserId(w.getMessageId(), String.valueOf(Distribution.YES), 3, userId);
                LogUtils.info4("成功从管理员处获取到两人不一致的任务...原任务 working = " + JsonUtils.objectToJson(w) + " 重新分配给 user = " + JsonUtils.objectToJson(user));
                return;
            }
        }
        // 到这里如果没有 return，那就是没获取到两个人审核结果不一致的数据， 接下来获取一条已经分配过一次的数据
        MessageData oneDistributionMessageData = this.messageDataMapper.getOneDistrMsgDataByUserId(userId, batchNumber, DateUtils.getDateStartPoint(-1), DateUtils.getDateEndPoint(0));
        if (oneDistributionMessageData != null) { // 不为空说明获取到了
            String allocateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
            Working working = new Working(idWorker.nextId(), oneDistributionMessageData.getId(), oneDistributionMessageData.getMessageId(), userId, allocateTime, oneDistributionMessageData.getCarNumber(), "", "", null, "", 1);
            this.messageDataMapper.saveWorking(working);
            this.messageDataMapper.updateDisTrAndTimeAndTwoUserId(oneDistributionMessageData.getMessageId(), String.valueOf(Distribution.YES), 2, userId);
            LogUtils.info4("分配过一次的数据 oneDistributionMessageData = " + JsonUtils.objectToJson(oneDistributionMessageData) + " 该数据再分配给 user = " + JsonUtils.objectToJson(user));
        } else { // 没获取到，那就获取一条未分配过的数据
            MessageData oneUnDistributionMessageData = this.messageDataMapper.getOneUnDistributionMessageData(DateUtils.getDateStartPoint(-1), DateUtils.getDateEndPoint(0));
            LogUtils.info4("==========================================================");
            oneUnDistributionMessageData = this.handle(oneUnDistributionMessageData);
            LogUtils.info4("==========================================================");
            if (oneUnDistributionMessageData == null) {
                return;
            }
            String allocateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
            Working working = new Working(idWorker.nextId(), oneUnDistributionMessageData.getId(), oneUnDistributionMessageData.getMessageId(), userId, allocateTime, oneUnDistributionMessageData.getCarNumber(), "", "", null, "", 1);
            this.messageDataMapper.saveWorking(working);
            this.messageDataMapper.updateDisTrAndTimeAndOneUserId(oneUnDistributionMessageData.getMessageId(), String.valueOf(Distribution.YES), 1, userId);
            LogUtils.info4("从未分配过的数据 oneUnDistributionMessageData = " + JsonUtils.objectToJson(oneUnDistributionMessageData) + " 成功分配给 user = " + JsonUtils.objectToJson(user));
        }
    }

    /**
     * 每获取到一条数据，都先比对距离该数据最近的一条数据的 newCarNumber, 如果只相差一个字符，就认为该数据是上一条数据的结果
     *
     * @param messageData 传入的获取到的数据
     * @return 返回一条重新获取到的数据
     */
    private MessageData handle(MessageData messageData) {
        LogUtils.info4("传入的 messageData = " + JsonUtils.objectToJson(messageData));
        if (messageData == null) {
            return null;
        }
        boolean flag = false;
        String beforeNewCarNumber = this.messageDataMapper.getBeforeNewCarNumber(messageData.getPatrolCarNumber(), messageData.getParkNumber(), messageData.getPhotographTime(), DateUtils.getDateStartPoint(-1), DateUtils.getDateEndPoint(0));
        LogUtils.info4("上一次的 newCarNumber = " + beforeNewCarNumber);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
        if (messageData.getCarNumber().trim().equals("无") && beforeNewCarNumber != null && beforeNewCarNumber.trim().equals("无")) { // 上一次是 "无", 这一次也是 "无", 就认为这一次是 "无"
            this.messageDataMapper.updateCarNumberByMessageId(messageData.getMessageId(), beforeNewCarNumber, time);
            LogUtils.info4("两个都是无，比对成功...");
            flag = true;
        } else { // 如果只相差一个字符，也认为这一次的车牌是上一次的 newCarNumber
            String[] split = messageData.getCarNumber().split("/");
            for (String carNumber : split) {
                if (beforeNewCarNumber != null && !"无".equals(beforeNewCarNumber) && beforeNewCarNumber.length() == carNumber.length()) {
                    int count = 0;
                    for (int i = 0; i < beforeNewCarNumber.length(); i++) {
                        if (beforeNewCarNumber.charAt(i) != carNumber.charAt(i)) {
                            count++;
                        }
                    }
                    if (count <= 1) {
                        this.messageDataMapper.updateCarNumberByMessageId(messageData.getMessageId(), beforeNewCarNumber, time);
                        LogUtils.info4("只相差一个字符，比对成功...");
                        flag = true;
                    }
                }
            }
        }
        if (flag) {
            messageData = this.messageDataMapper.getOneUnDistributionMessageData(DateUtils.getDateStartPoint(-1), DateUtils.getDateEndPoint(0));
            LogUtils.info4("比对成功，重新获取到的 messageData = " + JsonUtils.objectToJson(messageData));
            return handle(messageData);
        } else {
            LogUtils.info4("最后得到的 messageData = " + JsonUtils.objectToJson(messageData));
            return messageData;
        }
    }


    public PageResult<TaskResult> getTaskResult(Integer userId, String startTime, String endTime, int numPage, int rows) {
        PageResult<TaskResult> pageResult = new PageResult<>();
        List<TaskResult> taskResultList = new ArrayList<>();
        PageHelper.startPage(numPage, rows);
        List<Working> workingList = messageDataMapper.getWorkingByUserIdAndTime(userId, startTime, endTime);
        PageInfo<Working> pageInfo = new PageInfo<>(workingList);
        for (Working working : pageInfo.getList()) {
            if (working == null) {
                continue;
            }
            MessageData messageData = this.messageDataMapper.getMessageDataByMsgId(working.getMessageId());
            TaskResult taskResult = new TaskResult(working.getMessageId(), messageData, working);
            taskResultList.add(taskResult);
        }
        pageResult.setStatus(Status.SUCCESS);
        pageResult.setData(taskResultList);
        pageResult.setTotalPages(pageInfo.getPages());
        return pageResult;
    }

    /**
     * 统计一周内每一天的操作
     *
     * @return 返回统计对象
     */
    public List<Echarts> getWorkingByTime() {
        List<Echarts> echartsList = new ArrayList<>();

        Echarts echarts1 = this.getEcharts(-6);
        Echarts echarts2 = this.getEcharts(-5);
        Echarts echarts3 = this.getEcharts(-4);
        Echarts echarts4 = this.getEcharts(-3);
        Echarts echarts5 = this.getEcharts(-2);
        Echarts echarts6 = this.getEcharts(-1);
        Echarts echarts7 = this.getEcharts(0);

        echartsList.add(echarts1);
        echartsList.add(echarts2);
        echartsList.add(echarts3);
        echartsList.add(echarts4);
        echartsList.add(echarts5);
        echartsList.add(echarts6);
        echartsList.add(echarts7);

        return echartsList;
    }

    /**
     * 统计具体某一天的数量
     *
     * @param day 某天
     * @return 返回统计对象
     */
    private Echarts getEcharts(int day) {
        List<Working> workingList = this.messageDataMapper.getWorkingByTime(DateUtils.getDateStartPoint(day), DateUtils.getDateEndPoint(day));
        Echarts echarts = new Echarts();
        echarts.setTime(DateUtils.getDateStartPoint(day).substring(0, 10));
        int total; //总数
        int rightCount = 0; // 正确数量(两个人修改的结果一样才认为正确，所以这个值是成对的)
        int errCount = 0; // 错误数量(这个值不用去重，结果错误的肯定是一条数据分配给了三个人，其中两个修改结果一样，另一个不一样)
        int unsealedCount = 0; // 未确定结果数量(三个人修改结果都不一样才会产生这个值)
        int unDone = 0; // 未完成数量
        int skip = 0; // 跳过的数量
        if (workingList == null || workingList.size() == 0) {
            total = 0;
        } else {
            Set<String> messageIdSet = new HashSet<>();
            for (Working working : workingList) {
                messageIdSet.add(working.getMessageId());
            }
            total = messageIdSet.size(); // 总数需要根据 messageId 去重
        }
        for (Working working : workingList) {
            if (working.getFeedbackResult() == FeedbackResult.YES) {
                rightCount++;
            }
            if (working.getFeedbackResult() == FeedbackResult.NO) {
                errCount++;
            }
            if (working.getFeedbackResult() == FeedbackResult.UNKNOWN) {
                unsealedCount++;
            }
            if (working.getFeedbackResult() == FeedbackResult.SKIP || working.getFeedbackResult() == FeedbackResult.TEAMMATESKIP) {
                skip++;
            }
            if (working.getFeedbackResult() == null || "".equals(String.valueOf(working.getFeedbackResult()).trim())) {
                unDone++;
            }
        }
        echarts.setTotal(total);
        echarts.setRightCount(rightCount / 2 - errCount);
        echarts.setErrCount(errCount);
        echarts.setUnsealedCount(unsealedCount / 3);
        echarts.setUnDone(unDone);
        echarts.setSkip(skip / 2); // 这里做不到实时准确, 如果两个人都跳过，或者一个跳过一个被队友跳过，这时除以2是对的，但是一个跳过一个未修改，这样做是不准确的
        if (total != 0) {
            echarts.setAccuracy((double) echarts.getRightCount() / total);
        } else {
            echarts.setAccuracy(0);
        }

        return echarts;
    }

    public List<Echarts> getWorkingByTime2() {
        List<Echarts> echartsList = new ArrayList<>();

        Echarts echarts1 = this.getEcharts2(-6);
        Echarts echarts2 = this.getEcharts2(-5);
        Echarts echarts3 = this.getEcharts2(-4);
        Echarts echarts4 = this.getEcharts2(-3);
        Echarts echarts5 = this.getEcharts2(-2);
        Echarts echarts6 = this.getEcharts2(-1);
        Echarts echarts7 = this.getEcharts2(0);

        echartsList.add(echarts1);
        echartsList.add(echarts2);
        echartsList.add(echarts3);
        echartsList.add(echarts4);
        echartsList.add(echarts5);
        echartsList.add(echarts6);
        echartsList.add(echarts7);

        return echartsList;


    }

    private Echarts getEcharts2(int day) {
        List<String> messageIdList = messageDataMapper.getDistinctWorkingByTime(DateUtils.getDateStartPoint(day), DateUtils.getDateEndPoint(day));
        Echarts echarts = new Echarts();
        echarts.setTime(DateUtils.getDateStartPoint(day).substring(0, 10));
        int rightCount = 0;
        int errCount = 0;
        int unDone = 0;
        echarts.setTotal(messageIdList.size());
        echarts.setRightCount(0);
        echarts.setErrCount(0);
        echarts.setUnDone(0);
        for (String messageId : messageIdList) {
            // 先根据messageId去巡检结果三个都错误的记录表查询有没有这个messageId，如果有的话就表明这条数据是错误的
            // 1如果没有就，看看是否只有两条并且修改结果一样或者有三条并且两个人的一样就是正确的
            // 2如果没有就，看看是否有一条是未完成的，那就是未完成的
            int count = modifyResultsMapper.getCheckUserInfoCountByMessageId(messageId);
            // 错误
            if (count > 0) {
                errCount += 1;
            } else {
                // 不是正确就是未完成
                List<Working> workingList = messageDataMapper.getWorkingByMessageId(messageId);
                // messageId， 结果 true代表正确， false代表未完成
                // 判断方法，当一遇到未完成就是未完成，没有遇到未完成就是成功
                Map<String, String> map = new HashMap<>();
                for (Working working : workingList) {
                    if (working.getFeedbackResult() == FeedbackResult.UNKNOWN) {
                        if (map.get(working.getMessageId()) == null) {
                            map.put(working.getMessageId(), "false");
                        } else {
                            map.put(working.getMessageId(), "false");
                        }
                    } else {
                        if (map.get(working.getMessageId()) == null) {
                            map.put(working.getMessageId(), "true");
                        } else {
                            if (!"false".equals(map.get(working.getMessageId()))) {
                                map.put(working.getMessageId(), "true");
                            }
                        }
                    }
                }
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if ("false".equals(entry.getValue())) {
                        unDone += 1;
                    } else {
                        rightCount += 1;
                    }
                }
                echarts.setRightCount(rightCount);
                echarts.setErrCount(errCount);
                echarts.setUnDone(unDone);
            }
        }
        return echarts;
    }

    public Map<String, String> getOneDayChetCount(Integer userId) {
        String startTime = DateUtils.getDateStartPoint(0);
        String endTime = DateUtils.getDateEndPoint(0);
        List<Working> workingList = messageDataMapper.getWorkingByUserIdAndTime(userId, startTime, endTime);
        if (workingList == null) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>();
        int successCount = 0;// 修改正确的条目数
        int errorCount = 0;// 修改错误的条目数
        int leapfrogCount = 0;// 跳过的条目数
        int total = 0;// 处理总数
        for (Working working : workingList) {
            if (working.getFeedbackResult() != null) {
                total += 1;
            }
            if (working.getFeedbackResult() == FeedbackResult.YES) {
                successCount += 1;
                continue;
            }
            if (working.getFeedbackResult() == FeedbackResult.NO) {
                errorCount += 1;
                continue;
            }
            if (working.getFeedbackResult() == FeedbackResult.SKIP) {
                leapfrogCount += 1;
            }
        }
        // 总数
        resultMap.put("total", String.valueOf(total));
        resultMap.put("successCount", String.valueOf(successCount));
        resultMap.put("errorCount", String.valueOf(errorCount));
        resultMap.put("leapfrogCount", String.valueOf(leapfrogCount));
        return resultMap;
    }
}
