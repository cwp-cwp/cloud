package com.cwp.cloud.bean;

import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 批次号表实体类
 * Created by chen_wp on 2019-09-26.
 */
public class BatchNumber {

    private long id;
    private String patrolCarNumber; // 巡检车车牌号
    private String number; // 批次号
    private String time; // 时间

    public BatchNumber() {
    }

    public BatchNumber(long id, String patrolCarNumber, String number, String time) {
        this.id = id;
        this.patrolCarNumber = patrolCarNumber;
        this.number = number;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPatrolCarNumber() {
        return patrolCarNumber;
    }

    public void setPatrolCarNumber(String patrolCarNumber) {
        this.patrolCarNumber = patrolCarNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }


}
