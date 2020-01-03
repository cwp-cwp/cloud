package com.cwp.cloud.api;

import com.cwp.cloud.service.KafkaTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息主题管理 api
 * Created by chen_wp on 2019-09-20.
 */
@Component
public class TopicApi {

    private final static Logger LOG = LoggerFactory.getLogger(TopicApi.class);

    private final KafkaTopicService kafkaTopicService;

    @Autowired
    public TopicApi(KafkaTopicService kafkaTopicService) {
        this.kafkaTopicService = kafkaTopicService;
    }

    /**
     * 添加三级主题
     *
     * @param carNumber 巡检车车牌号
     * @return 是否添加成功
     */
    public boolean addThreeLevelTopic(String carNumber) {
        LOG.info("addThreeLevelTopic() execute... carNumber = " + carNumber);
        try {
            if (carNumber == null || "".equals(carNumber.replace(" ", "")) || carNumber.length() < 2) {
                throw new RuntimeException("输入的车牌号不合法, 请重新输入...");
            }
            this.kafkaTopicService.addThreeLevelTopic(carNumber);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
