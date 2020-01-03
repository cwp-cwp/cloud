package com.cwp.cloud.tools;

import com.cwp.cloud.api.CheckApi;
import com.cwp.cloud.api.TopicApi;
import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.OrderBy;
import com.cwp.cloud.bean.PageResult;
import com.cwp.cloud.bean.user.Echarts;
import com.cwp.cloud.bean.user.TaskResult;
import com.cwp.cloud.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 开发阶段的测试工具类
 * Created by chen_wp on 2019-09-23.
 */
@Controller
@RequestMapping("/cloud")
public class TestController {


    @Autowired
    private TopicApi topicApi;

    @Autowired
    private CheckApi checkApi;

    /**
     * http://127.0.0.1:8081/cloud/testHello
     */
    @RequestMapping("/testHello")
    @ResponseBody
    public Result testHello() {
        return new Result(200, "hello", null);
    }

    /**
     * http://127.0.0.1:8081/cloud/testAddThreeLevelTopic?carNumber=
     */
    @RequestMapping("/testAddThreeLevelTopic")
    @ResponseBody
    public Result testAddThreeLevelTopic(String carNumber) {
        if (this.topicApi.addThreeLevelTopic(carNumber)) {
            return new Result(200, "添加成功", null);
        }
        return new Result(500, "添加失败", null);
    }

    /**
     * http://127.0.0.1:8081/cloud/testGetMessageDataByUserId?userId=&orderBy=
     */
    @RequestMapping("/testGetMessageDataByUserId")
    @ResponseBody
    public Result testGetMessageDataByUserId(int userId, OrderBy orderBy) {
        List<MessageData> messageDataByUserId = this.checkApi.getMessageDataByUserId(userId, orderBy);
        return new Result(200, "查询成功", messageDataByUserId);
    }

    /**
     * http://127.0.0.1:8081/cloud/getTaskResult?userId=&startTime=&endTime=&numPage=&rows=
     */
    @RequestMapping("/getTaskResult")
    @ResponseBody
    public Result getTaskResult(Integer userId, String startTime, String endTime, int numPage, int rows) {
        PageResult<TaskResult> taskResult = this.checkApi.getTaskResult(userId, startTime, endTime, numPage, rows);
        return new Result(200, "查询成功", taskResult);
    }

    /**
     * http://127.0.0.1:8081/cloud/getWorkingByTime
     */
    @RequestMapping("/getWorkingByTime")
    @ResponseBody
    public Result getWorkingByTime() {
        List<Echarts> echartsList = this.checkApi.getWorkingByTime();
        return new Result(200, "查询成功", echartsList);
    }

    /**
     * http://127.0.0.1:8081/cloud/getWorkingByTime2
     */
    @RequestMapping("/getWorkingByTime2")
    @ResponseBody
    public Result getWorkingByTime2() {
        List<Echarts> echartsList = this.checkApi.getWorkingByTime2();
        return new Result(200, "查询成功", echartsList);
    }

}
