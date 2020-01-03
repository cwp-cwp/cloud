package com.cwp.cloud.service;

import com.cwp.cloud.bean.KafkaTopic;
import com.cwp.cloud.bean.TopicLevel;
import com.cwp.cloud.mapper.KafkaTopicMapper;
import com.cwp.cloud.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息主题业务逻辑层
 * Created by chen_wp on 2019-09-20.
 */
@Component
@Transactional
public class KafkaTopicService {

    @Autowired
    private KafkaTopicMapper kafkaTopicMapper;

    @Autowired
    private IdWorker idWorker;

    public List<KafkaTopic> getAllThreeLevelTopic() {
        return this.kafkaTopicMapper.getAllThreeLevelTopic();
    }

    public void addThreeLevelTopic(String carNumber) {
        List<KafkaTopic> allThreeLevelTopic = this.kafkaTopicMapper.getAllThreeLevelTopic();
        carNumber = carNumber.replace(" ", "");
        List<String> topicList = new ArrayList<>();
        for (KafkaTopic topic : allThreeLevelTopic) {
            topicList.add(topic.getTopicName());
        }
        if (topicList.contains(carNumber)) {
            return;
        }
        KafkaTopic kafkaTopic = new KafkaTopic();
        kafkaTopic.setId(idWorker.nextId());
        kafkaTopic.setTopicLevel(TopicLevel.THREE);
        kafkaTopic.setTopicName("request-" + carNumber); // 主题名称格式统一为: "request-巡检车车牌号", 例如  request-B6666
        this.kafkaTopicMapper.saveKafkaTopic(kafkaTopic);
    }
}
