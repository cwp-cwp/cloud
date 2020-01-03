package com.cwp.cloud.bean.user;

import com.cwp.cloud.utils.FormatClassInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改结果实体类
 * Created by chen_wp on 2019-10-18.
 */
public class ModifyResults {

    private long id;
    private String messageId; // 消息唯一标识
    private String patrolCarNumber; // 巡检车车牌号
    private String batchNumber; // 批次号
    private String parkNumber; // 车位号
    private String carNumber; // 原车牌号
    private int userId; // 操作员 id
    private String userName; // 操作员名字
    private String auditCarNumber; // 审核后的车牌
    private String auditTime; // 审核时间

    private List<CheckUserInfo> checkUserInfoList = new ArrayList<>();

    public ModifyResults() {
    }

    public ModifyResults(long id, String messageId, String patrolCarNumber, String batchNumber, String parkNumber, String carNumber, int userId, String userName, String auditCarNumber, String auditTime) {
        this.id = id;
        this.messageId = messageId;
        this.patrolCarNumber = patrolCarNumber;
        this.batchNumber = batchNumber;
        this.parkNumber = parkNumber;
        this.carNumber = carNumber;
        this.userId = userId;
        this.userName = userName;
        this.auditCarNumber = auditCarNumber;
        this.auditTime = auditTime;
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

    public String getPatrolCarNumber() {
        return patrolCarNumber;
    }

    public void setPatrolCarNumber(String patrolCarNumber) {
        this.patrolCarNumber = patrolCarNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getParkNumber() {
        return parkNumber;
    }

    public void setParkNumber(String parkNumber) {
        this.parkNumber = parkNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuditCarNumber() {
        return auditCarNumber;
    }

    public void setAuditCarNumber(String auditCarNumber) {
        this.auditCarNumber = auditCarNumber;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public List<CheckUserInfo> getCheckUserInfoList() {
        return checkUserInfoList;
    }

    public void setCheckUserInfoList(List<CheckUserInfo> checkUserInfoList) {
        this.checkUserInfoList = checkUserInfoList;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }
}
