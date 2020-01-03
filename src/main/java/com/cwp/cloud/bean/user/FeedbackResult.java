package com.cwp.cloud.bean.user;

/**
 * 记录操作员修改车牌的反馈结果是否一致
 * Created by chen_wp on 2019-10-17.
 */
public enum FeedbackResult {
    YES, // 结果正确
    NO, // 结果错误
    UNKNOWN, // 未确定结果(三个人修改结果不一样就是未确定的结果)
    SKIP, // 跳过
    TEAMMATESKIP // 队友跳过
}
