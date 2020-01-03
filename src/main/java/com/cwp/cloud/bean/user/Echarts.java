package com.cwp.cloud.bean.user;

import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 操作员修改车牌结果统计实体类
 * Created by chen_wp on 2019-10-22.
 */
public class Echarts {

    private String time; // x坐标显示时间
    private int total; // 总数
    private int rightCount; // 正确数量
    private int errCount; // 错误数量
    private int unsealedCount; // 未确定结果的数量
    private int unDone; // 未完成数量
    private int skip; // 跳过的数量
    private double accuracy; // 正确率

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public int getErrCount() {
        return errCount;
    }

    public void setErrCount(int errCount) {
        this.errCount = errCount;
    }

    public int getUnsealedCount() {
        return unsealedCount;
    }

    public void setUnsealedCount(int unsealedCount) {
        this.unsealedCount = unsealedCount;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getUnDone() {
        return unDone;
    }

    public void setUnDone(int unDone) {
        this.unDone = unDone;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }

}
