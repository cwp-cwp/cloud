package com.cwp.cloud.bean.user;

import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 操作员任务列表实体类
 * Created by chen_wp on 2019-10-21.
 */
public class TaskResult {

    private String messageId; // 消息唯一标识
    private MessageData messageData;
    private Working working;

    public TaskResult() {
    }

    public TaskResult(String messageId, MessageData messageData, Working working) {
        this.messageId = messageId;
        this.messageData = messageData;
        this.working = working;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageData getMessageData() {
        return messageData;
    }

    public void setMessageData(MessageData messageData) {
        this.messageData = messageData;
    }

    public Working getWorking() {
        return working;
    }

    public void setWorking(Working working) {
        this.working = working;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }
}
