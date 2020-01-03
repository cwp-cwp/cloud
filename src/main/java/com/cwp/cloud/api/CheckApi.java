package com.cwp.cloud.api;

import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.OrderBy;
import com.cwp.cloud.bean.PageResult;
import com.cwp.cloud.bean.Status;
import com.cwp.cloud.bean.user.*;
import com.cwp.cloud.service.CheckService;
import com.cwp.cloud.service.MessageDataService;
import com.cwp.cloud.service.UserService;
import com.cwp.cloud.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 任务管理接口
 */
@Component
public class CheckApi {

    private final static Logger LOG = LoggerFactory.getLogger(CheckApi.class);

    private final CheckService checkService;
    private final MessageDataService messageDataService;
    private final UserService userService;

    @Autowired
    public CheckApi(CheckService checkService, MessageDataService messageDataService, UserService userService) {
        this.checkService = checkService;
        this.messageDataService = messageDataService;
        this.userService = userService;
    }

    /**
     * 根据 userId 获取任务数据
     *
     * @param userId  用户 id
     * @param orderBy 排序参数
     * @return 返回的任务数据
     */
    public List<MessageData> getMessageDataByUserId(int userId, OrderBy orderBy) {
        // 进入获取任务的方法，就分配一条数据
        this.checkService.getOneTask(userId, "");
        return this.messageDataService.getMessageDataByUserId(userId, orderBy);
    }

    /**
     * 查询任务列表
     *
     * @param userId    操作员 id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param numPage   页码
     * @param rows      每页条数
     * @return 返回分页结果
     */
    public PageResult<TaskResult> getTaskResult(Integer userId, String startTime, String endTime, int numPage, int rows) {
        LOG.info("getTaskResult() execute... userId: " + userId + " startTime: " + startTime + " endTime: " + endTime + " numPage: " + numPage + " rows: " + rows);
        return this.checkService.getTaskResult(userId, startTime, endTime, numPage, rows);
    }

    /**
     * 操作员修改车牌
     *
     * @param userId       操作员id
     * @param newCarNumber 修改的新车牌
     * @param batchNumber  批次号
     * @param parkNumber   车位号
     * @return 是否修改成功
     */
    public boolean modifyCarNumber(int userId, String newCarNumber, String batchNumber, String parkNumber) {
        try {
            this.messageDataService.modifyCarNumber(userId, newCarNumber, batchNumber, parkNumber);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询一周的统计结果
     */
    public List<Echarts> getWorkingByTime() {
        LOG.info("getWorkingByTime() execute... ");
        return this.checkService.getWorkingByTime();
    }

    public List<Echarts> getWorkingByTime2() {
        LOG.info("getWorkingByTime2() execute... ");
        return this.checkService.getWorkingByTime2();
    }

    /**
     * 查询修改结果异常的数据
     *
     * @param userId    操作员 id (可以不传)
     * @param startTime 开始时间 (可以不传)
     * @param endTime   结束时间 (可以不传)
     * @param numPage   页码
     * @param rows      每页条数
     * @return 返回分页结果
     */
    public PageResult<ModifyResults> getModifyResults(Integer userId, String startTime, String endTime, int numPage, int rows) {
        LOG.info("getModifyResults() execute... userId = " + userId + " startTime = " + startTime + " endTime = " + endTime + " numPage = " + numPage + " rows = " + rows);
        PageResult<ModifyResults> pageResult = new PageResult<>();
        /*PageHelper.startPage(numPage, rows);
        List<ModifyResults> modifyResultsList = this.messageDataService.getModifyResults(userId, startTime, endTime);
        PageInfo<ModifyResults> pageInfo = new PageInfo<>(modifyResultsList);
        pageResult.setStatus(Status.SUCCESS);
        pageResult.setData(pageInfo.getList());
        pageResult.setTotalPages(pageInfo.getPages());*/

        PageHelper.startPage(numPage, rows);
        List<ModifyResults> modifyResultsList = messageDataService.getDistinctModifyResults(startTime, endTime);
        PageInfo<ModifyResults> pageInfo = new PageInfo<>(modifyResultsList);
        for (ModifyResults modifyResults : pageInfo.getList()) {
            List<CheckUserInfo> checkUserInfoList = messageDataService.getCheckUserInfoList(modifyResults.getMessageId());
            for (CheckUserInfo checkUserInfo : checkUserInfoList) {
                User user = userService.getUserById(checkUserInfo.getUserId());
                checkUserInfo.setUserName(user.getName());
            }
            modifyResults.setCheckUserInfoList(checkUserInfoList);
        }
        pageResult.setStatus(Status.SUCCESS);
        pageResult.setData(pageInfo.getList());
        pageResult.setTotalPages(pageInfo.getPages());

        LOG.info("pageResult: " + JsonUtils.objectToJson(pageResult));

        return pageResult;
    }

    // 获取当天的修改记录
    public Map<String, String> getOneDayChetCount(Integer userId) {
        return checkService.getOneDayChetCount(userId);
    }
}
