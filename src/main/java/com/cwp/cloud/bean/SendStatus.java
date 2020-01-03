package com.cwp.cloud.bean;

/**
 * 发送数据给云平台的状态枚举
 */
public enum SendStatus {
    SUCCESS, // 发送成功
    FAIL, // 发送失败
    WARN, // 连续发送三次失败
    UNSENT // 未发送
}
