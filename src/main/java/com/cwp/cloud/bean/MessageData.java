package com.cwp.cloud.bean;

import com.cwp.cloud.utils.FormatClassInfo;

import java.util.List;

/**
 * 和巡检车发送过来的数据实体类相对应
 * Created by chen_wp on 2019-09-18.
 */
public class MessageData {

    private long id;
    private String messageId; // 消息唯一标识
    private String batchNumber; // 巡检批次号
    private String minBatchNumber; // 小批次号(为了提高效率，以小批次数据为单位发送给云平台)
    private String patrolCarId; // 巡检车编号
    private String patrolCarNumber; // 巡检车车牌号
    private String areaNumber; // 区域编号
    private String areaName; // 区域名称
    private String parkNumber; // 车位号
    private String parkStatus; // 车位状态
    private String carNumber; // 车牌号
    private String photographTime; // 拍照时间
    private PushStatus pushStatus; // 推送状态
    private String pushTime; // 推送时间
    private SendStatus sendStatus; // 发送状态
    private ResultType type; // 分析结果类型
    private String recordTime; // 系统调用时间
    private String newCarNumber; // 修改后的新车牌号
    private String panorama; // 全景图
    private Distribution distribution; // 是否已经分配给操作员
    private String updateTime; // 人为修改时间
    private int distributionTimes; // 记录一条数据分配给了几个操作员
    private int oneUserId; // 第一次分配的 userId
    private int twoUserId; // 第二次分配的 userId
    private int threeUserId; // 第三次分配的 userId
    private Tag tag; //巡检类型(巡检车/手机)
    private ParkStatus status; // 激光返回的有车无车状态

    private List<ParkSpaceImages> parkSpaceImages; // 图片

    public MessageData() {
    }

    public MessageData(String messageId, String batchNumber, String minBatchNumber, String patrolCarId, String patrolCarNumber, String areaNumber, String areaName, String parkNumber, String parkStatus, String carNumber, String photographTime, PushStatus pushStatus, String pushTime, SendStatus sendStatus, ResultType type, String recordTime, String newCarNumber, String panorama, Distribution distribution, String updateTime, int distributionTimes, int oneUserId, int twoUserId, int threeUserId) {
        this.messageId = messageId;
        this.batchNumber = batchNumber;
        this.minBatchNumber = minBatchNumber;
        this.patrolCarId = patrolCarId;
        this.patrolCarNumber = patrolCarNumber;
        this.areaNumber = areaNumber;
        this.areaName = areaName;
        this.parkNumber = parkNumber;
        this.parkStatus = parkStatus;
        this.carNumber = carNumber;
        this.photographTime = photographTime;
        this.pushStatus = pushStatus;
        this.pushTime = pushTime;
        this.sendStatus = sendStatus;
        this.type = type;
        this.recordTime = recordTime;
        this.newCarNumber = newCarNumber;
        this.panorama = panorama;
        this.distribution = distribution;
        this.updateTime = updateTime;
        this.distributionTimes = distributionTimes;
        this.oneUserId = oneUserId;
        this.twoUserId = twoUserId;
        this.threeUserId = threeUserId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMinBatchNumber() {
        return minBatchNumber;
    }

    public void setMinBatchNumber(String minBatchNumber) {
        this.minBatchNumber = minBatchNumber;
    }

    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getPatrolCarId() {
        return patrolCarId;
    }

    public void setPatrolCarId(String patrolCarId) {
        this.patrolCarId = patrolCarId;
    }

    public String getPatrolCarNumber() {
        return patrolCarNumber;
    }

    public void setPatrolCarNumber(String patrolCarNumber) {
        this.patrolCarNumber = patrolCarNumber;
    }

    public String getAreaNumber() {
        return areaNumber;
    }

    public void setAreaNumber(String areaNumber) {
        this.areaNumber = areaNumber;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getParkNumber() {
        return parkNumber;
    }

    public void setParkNumber(String parkNumber) {
        this.parkNumber = parkNumber;
    }

    public String getParkStatus() {
        return parkStatus;
    }

    public void setParkStatus(String parkStatus) {
        this.parkStatus = parkStatus;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getPhotographTime() {
        return photographTime;
    }

    public void setPhotographTime(String photographTime) {
        this.photographTime = photographTime;
    }

    public PushStatus getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(PushStatus pushStatus) {
        this.pushStatus = pushStatus;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public List<ParkSpaceImages> getParkSpaceImages() {
        return parkSpaceImages;
    }

    public void setParkSpaceImages(List<ParkSpaceImages> parkSpaceImages) {
        this.parkSpaceImages = parkSpaceImages;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getNewCarNumber() {
        return newCarNumber;
    }

    public void setNewCarNumber(String newCarNumber) {
        this.newCarNumber = newCarNumber;
    }

    public String getPanorama() {
        return panorama;
    }

    public void setPanorama(String panorama) {
        this.panorama = panorama;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getDistributionTimes() {
        return distributionTimes;
    }

    public void setDistributionTimes(int distributionTimes) {
        this.distributionTimes = distributionTimes;
    }

    public int getOneUserId() {
        return oneUserId;
    }

    public void setOneUserId(int oneUserId) {
        this.oneUserId = oneUserId;
    }

    public int getTwoUserId() {
        return twoUserId;
    }

    public void setTwoUserId(int twoUserId) {
        this.twoUserId = twoUserId;
    }

    public int getThreeUserId() {
        return threeUserId;
    }

    public void setThreeUserId(int threeUserId) {
        this.threeUserId = threeUserId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public ParkStatus getStatus() {
        return status;
    }

    public void setStatus(ParkStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }

}
