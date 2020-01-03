package com.cwp.cloud.bean.user;

public class CheckUserInfo {
    private Integer userId;
    private String userName;
    private String auditCarNumber;
    private String auditTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
}
