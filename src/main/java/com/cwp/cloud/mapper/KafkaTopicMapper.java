package com.cwp.cloud.mapper;

import com.cwp.cloud.bean.KafkaTopic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息主题数据库操作接口
 * Created by chen_wp on 2019-09-20.
 */
@Repository
public interface KafkaTopicMapper {

    void saveKafkaTopic(KafkaTopic kafkaTopic);

    List<KafkaTopic> getAllThreeLevelTopic();
}
