package com.cwp.cloud.bean.user;

import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 用户考勤表实体类
 * Created by chen_wp on 2019-10-11.
 */
public class Attendance {
    private int id; // 主键自增
    private String username; // 用户名
    private String workStartTime; // 签到时间
    private String workEndTime;// 签退时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(String workStartTime) {
        this.workStartTime = workStartTime;
    }

    public String getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(String workEndTime) {
        this.workEndTime = workEndTime;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }
}
