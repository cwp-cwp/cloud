package com.cwp.cloud.bean;

import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 消息主题实体类
 * Created by chen_wp on 2019-09-20.
 */
public class KafkaTopic {

    private long id;
    private TopicLevel topicLevel; // 主题级别
    private String topicName; // 主题名称

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TopicLevel getTopicLevel() {
        return topicLevel;
    }

    public void setTopicLevel(TopicLevel topicLevel) {
        this.topicLevel = topicLevel;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }
}
