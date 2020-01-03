package com.cwp.cloud.mapper;

import com.cwp.cloud.bean.BatchNumber;
import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.ParkSpaceImages;
import com.cwp.cloud.bean.user.Working;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息数据库操作接口
 * Created by chen_wp on 2019-09-20.
 */
@Repository
public interface MessageDataMapper {

    void saveMessageData(MessageData messageData);

//    void saveParkSpaceImages(MessageData messageData);
    void saveParkSpaceImages(ParkSpaceImages parkSpaceImages);

    List<MessageData> getMessageData2(@Param("patrolCarNumber") String patrolCarNumber, @Param("areaName") String areaName, @Param("batchNumber") String batchNumber, @Param("parkNumber") String parkNumber, @Param("type") String type, @Param("pushStatus") String pushStatus, @Param("tag") String tag, @Param("startTime") String startTime, @Param("endTime") String endTime);

    void updateCarNumber(@Param("patrolCarNumber") String patrolCarNumber, @Param("areaName") String areaName, @Param("batchNumber") String batchNumber, @Param("parkNumber") String parkNumber, @Param("newCarNumber") String newCarNumber);

    void saveBatchNumber(BatchNumber batchNumber);

    List<String> getBatchNumByPatrolCarNumBetweenTime(@Param("patrolCarNumber") String patrolCarNumber, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<MessageData> getAllMessageDataByBatchNumber(@Param("batchNumber") String batchNumber);

    void updatePushStatusAndTime(MessageData messageData);

    String getLastNewCarNumber(@Param("patrolCarNumber") String patrolCarNumber, @Param("parkNumber") String parkNumber, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<MessageData> getMessageDataByUserId(@Param("userId") int userId, @Param("orderBy") String orderBy);

    List<ParkSpaceImages> getImagesByBatchNumAndParkNum(@Param("batchNumber") String batchNumber, @Param("parkNumber") String parkNumber);

    List<MessageData> getUnPushMessageData(@Param("startTime") String startTime, @Param("endTime") String endTime);

    int saveWorking(Working working);

    String getMessageIdByBatchNumAndParkNum(@Param("batchNumber") String batchNumber, @Param("parkNumber") String parkNumber);

    void updateAuditCarNumAndAuditTime(@Param("messageId") String messageId, @Param("userId") int userId, @Param("newCarNumber") String newCarNumber, @Param("time") String time);

    List<Working> getWorkingByMsgIdAndUserId(@Param("messageId") String messageId, @Param("userId") int userId);

    void updateCarNumberByMessageId(@Param("messageId") String messageId, @Param("newCarNumber") String newCarNumber, @Param("time") String time);

    void updateFeedbackResult(@Param("messageId") String messageId, @Param("feedbackResult") String feedbackResult, @Param("time") String time);

    Integer getAnotherUserByMsgIdAnUserId(@Param("messageId") String messageId, @Param("userId") int userId);

    String getCarNumberByMessageId(@Param("messageId") String messageId);

    void updateFeedbackResultByUser(@Param("messageId") String messageId, @Param("userId1") int userId1, @Param("userId2") int userId2, @Param("feedbackResult") String feedbackResult, @Param("time") String time);

    MessageData getMessageDataByMsgId(@Param("messageId") String messageId);

    List<Working> getWorkingByUserIdAndTime(@Param("userId") Integer userId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Working> getWorkingByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<String> getDistinctWorkingByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Working> getWorkingByMessageId(@Param("messageId") String messageId);

    void updateFeedbackResultByUser2(@Param("messageId") String messageId, @Param("userId1") int userId1, @Param("userId2") int userId2, @Param("feedbackResult") String feedbackResult, @Param("time") String time);

    List<String> getMessageIdByBatchNumAndPatCarNum(@Param("batchNumber") String batchNumber, @Param("patrolCarNumber") String patrolCarNumber);

    // 获取一条未分配过的数据
    MessageData getOneUnDistributionMessageData(@Param("startTime") String startTime, @Param("endTime") String endTime);

    // 获取一条已经分配过一次，但不是分配给当前用户的一条数据
    MessageData getOneDistrMsgDataByUserId(@Param("userId") Integer userId, @Param("batchNumber") String batchNumber, @Param("startTime") String startTime, @Param("endTime") String endTime);

    // 更新分配字段，分配次数，第二次分配的UserId
    void updateDisTrAndTimeAndTwoUserId(@Param("messageId") String messageId, @Param("distribution") String distribution, @Param("distributionTimes") int distributionTimes, @Param("userId") Integer userId);

    // 更新分配字段，分配次数，第一次分配的UserId
    void updateDisTrAndTimeAndOneUserId(@Param("messageId") String messageId, @Param("distribution") String distribution, @Param("distributionTimes") int distributionTimes, @Param("userId") Integer userId);

    // 获取管理员的任务(这些任务肯定是两个操作员修改结果不一致的任务，才分配给了管理员)
    List<Working> getWorkingFromAdmin(@Param("startTime") String startTime, @Param("endTime") String endTime);

    // 将管理员的那一条任务更新为另外一个操作员
    void updateWorkingUserId(@Param("messageId") String messageId, @Param("userId") Integer userId);

    // 更新分配字段，分配次数，第三次分配的UserId
    void updateDisTrAndTimeAndThreeUserId(@Param("messageId") String messageId, @Param("distribution") String distribution, @Param("distributionTimes") int distributionTimes, @Param("userId") Integer userId);

    void updateOwnFeedbackResult(@Param("messageId") String messageId, @Param("userId") int userId, @Param("feedbackResult") String feedbackResult, @Param("time") String time);

    // 查询上一次的 newCarNumber
    String getBeforeNewCarNumber(@Param("patrolCarNumber") String patrolCarNumber, @Param("parkNumber") String parkNumber, @Param("photographTime") String photographTime, @Param("startTime") String startTime, @Param("endTime") String endTime);

}