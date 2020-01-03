package com.cwp.cloud.api;

import com.cwp.cloud.bean.*;
import com.cwp.cloud.service.MessageDataService;
import com.cwp.cloud.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息数据操作接口
 * Created by chen_wp on 2019-09-23.
 */
@Component
public class MessageDataApi {

    private final static Logger LOG = LoggerFactory.getLogger(MessageDataApi.class);

    private final MessageDataService messageDataService;

    @Autowired
    public MessageDataApi(MessageDataService messageDataService) {
        this.messageDataService = messageDataService;
    }

    /**
     * 获取停车详细信息
     *
     * @param patrolCarNumber 巡检车车牌号
     * @param areaName        区域名称
     * @param batchNumber     巡检批次
     * @param parkNumber      车位号
     * @param type            类型 (NORMAL/ABNORMAL)
     * @param pushStatus      推送状态(SUCCESS/FAIL)
     * @param tag             巡检类型(巡检车/手机)
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param numPage         页码
     * @param rows            每页条数
     * @return 返回分页结果
     */
    public PageResult<MessageData> getMessageData(String patrolCarNumber, String areaName, String batchNumber, String parkNumber, String type, String pushStatus, Tag tag, String startTime, String endTime, int numPage, int rows) {
        LOG.info("getMessageData() execute... patrolCarNumber: " + patrolCarNumber + " areaName: " + areaName + " batchNumber: " + batchNumber + " parkNumber: " + parkNumber + " type: " + type + " pushStatus: " + pushStatus + " tar = " + tag + " startTime: " + startTime + " endTime: " + endTime + " numPage: " + numPage + " rows: " + rows);

        PageResult<MessageData> pageResult = new PageResult<>();
        PageHelper.startPage(numPage, rows);
        List<MessageData> messageDataList = this.messageDataService.getMessageData2(patrolCarNumber, areaName, batchNumber, parkNumber, type, pushStatus, tag, startTime, endTime);
        for (MessageData messageData : messageDataList) {
            if (messageData.getBatchNumber() == null || messageData.getParkNumber() == null) {
                continue;
            }
            List<ParkSpaceImages> parkSpaceImagesList = messageDataService.getImagesByBatchNumAndParkNum(messageData.getBatchNumber(), messageData.getParkNumber());
            messageData.setParkSpaceImages(parkSpaceImagesList);
            if (messageData.getParkSpaceImages() == null || messageData.getParkSpaceImages().size() == 0) {
                continue;
            }
        }
        PageInfo<MessageData> pageInfo = new PageInfo<>(messageDataList);
        pageResult.setStatus(Status.SUCCESS);
        pageResult.setData(pageInfo.getList());
        pageResult.setTotalPages(pageInfo.getPages());
        LOG.info("pageResult: " + JsonUtils.objectToJson(pageResult));
        return pageResult;
    }

    /**
     * 修改车牌号
     *
     * @param patrolCarNumber 巡检车车牌号
     * @param areaName        区域名称
     * @param batchNumber     批次号
     * @param parkNumber      车位号
     * @param newCarNumber    修改的新车牌号
     * @return 修改是否成功
     */
    public boolean updateCarNumber(String patrolCarNumber, String areaName, String batchNumber, String parkNumber, String newCarNumber) {
        LOG.info("updateCarNumber() execute... patrolCarNumber: " + patrolCarNumber + " areaName: " + areaName + " batchNumber: " + batchNumber + " parkNumber: " + parkNumber + " newCarNumber: " + newCarNumber);
        try {
            this.messageDataService.updateCarNumber(patrolCarNumber, areaName, batchNumber, parkNumber, newCarNumber);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据巡检车车牌号获取批次号
     *
     * @param patrolCarNumber 巡检车车牌号 (可以不传, 不传就查询全部)
     * @param startTime       开始时间 (可以不传)
     * @param endTime         结束时间 (可以不传)
     * @return 返回批次号列表
     */
    public List<String> getBatchNumByPatrolCarNum(String patrolCarNumber, String startTime, String endTime) {
        LOG.info("getBatchNumByPatrolCarNum() execute... patrolCarNumber: " + patrolCarNumber + " startTime: " + startTime + " endTime: " + endTime);
        return this.messageDataService.getBatchNumByPatrolCarNumBetweenTime(patrolCarNumber, startTime, endTime);
    }

    /**
     * 根据批次号获取所有巡检结果
     *
     * @param batchNumber 批次号
     * @return 返回巡检结果列表
     */
    public List<MessageData> getAllMessageDataByBatchNumber(String batchNumber) {
        LOG.info("getAllMessageDataByBatchNumber() execute... batchNumber: " + batchNumber);
        return this.messageDataService.getAllMessageDataByBatchNumber(batchNumber);
    }

    /**
     * 修改推送状态和推送时间
     *
     * @param messageData 消息实体类
     * @return 是否成功
     */
    public boolean updatePushStatusAndTime(MessageData messageData) {
        try {
            this.messageDataService.updatePushStatusAndTime(messageData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
