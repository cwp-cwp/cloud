package com.cwp.cloud.bean.user;

import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 操作员作业表实体类
 * Created by chen_wp on 2019-10-11.
 */
public class Working {
    private long id;
    private long parentId; // 与 messageData 关联
    private String messageId; // 关联巡检结果表
    private int userId; // 用户名
    private String allocateTime; // 分配时间
    private String allocateCarNumber; // 分配的车牌
    private String auditCarNumber; // 审核车牌
    private String auditTime; // 审核时间
    private FeedbackResult feedbackResult; // 反馈结果
    private String feedbackTime; // 反馈时间
    private int times; // 第几次分配

    public Working() {
    }

    public Working(long id, long parentId, String messageId, int userId, String allocateTime, String allocateCarNumber, String auditCarNumber, String auditTime, FeedbackResult feedbackResult, String feedbackTime, int times) {
        this.id = id;
        this.parentId = parentId;
        this.messageId = messageId;
        this.userId = userId;
        this.allocateTime = allocateTime;
        this.allocateCarNumber = allocateCarNumber;
        this.auditCarNumber = auditCarNumber;
        this.auditTime = auditTime;
        this.feedbackResult = feedbackResult;
        this.feedbackTime = feedbackTime;
        this.times = times;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAllocateTime() {
        return allocateTime;
    }

    public void setAllocateTime(String allocateTime) {
        this.allocateTime = allocateTime;
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

    public String getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(String feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public FeedbackResult getFeedbackResult() {
        return feedbackResult;
    }

    public void setFeedbackResult(FeedbackResult feedbackResult) {
        this.feedbackResult = feedbackResult;
    }

    public String getAllocateCarNumber() {
        return allocateCarNumber;
    }

    public void setAllocateCarNumber(String allocateCarNumber) {
        this.allocateCarNumber = allocateCarNumber;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }


}
