package com.cwp.cloud.mapper;

import com.cwp.cloud.bean.user.CheckUserInfo;
import com.cwp.cloud.bean.user.ModifyResults;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 修改车牌异常记录数据库操作接口
 * Created by chen_wp on 2019-10-20.
 */
@Repository
public interface ModifyResultsMapper {

    void saveModifyResults(ModifyResults modifyResults);

    List<ModifyResults> getModifyResults(@Param("userId") Integer userId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<ModifyResults> getDistinctModifyResults(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<CheckUserInfo> getCheckUserInfoList(@Param("messageId") String messageId);

    int getCheckUserInfoCountByMessageId(@Param("messageId") String messageId);
}
