package com.cwp.cloud.push;

import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.ParkSpaceImages;
import com.cwp.cloud.bean.PushStatus;
import com.cwp.cloud.bean.ResultType;
import com.cwp.cloud.service.MessageDataService;
import com.cwp.cloud.utils.DateUtils;
import com.cwp.cloud.utils.ReadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import puzekcommon.utils.JsonUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class PushManager implements CommandLineRunner {

    @Autowired
    private MessageDataService messageDataService;

    @Override
    public void run(String... args) {
        new Thread(() -> {
            long threadId = Thread.currentThread().getId();
            PushLogUtils.info("推送线程 threadId: " + threadId);
            while (true) {
                try {
                    this.push();
                } catch (Exception e) {
                    PushLogUtils.info("推送线程异常... " + e.toString());
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void push() {
        List<MessageData> list = this.messageDataService.getUnPushMessageData(DateUtils.getDateTime(-1), DateUtils.getDateTime(1));
        PushLogUtils.info("定时查询到的推送数据 list.size() = " + list.size());
        for (MessageData messageData : list) {
            boolean flag = true;
            for (ParkSpaceImages images : messageData.getParkSpaceImages()) {
                // 判断每一张图片是否全部到齐
                try {
                    String imagePath = ResourceUtils.getFile("classpath:../../").getPath() + "/scanImage/" + messageData.getPatrolCarNumber() + "/" + messageData.getBatchNumber() + "/" + images.getImage();
                    File file = new File(imagePath);
                    if (!file.exists()) {
                        flag = false;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (flag) {
                this.push(messageData);
            }
        }
    }

    /**
     * 开始推送(因为是判断某个车位的所有图片都已经传输到云平台才开始推送，所以就一条一条数据推送，做不到一整个批次的推送)
     */
    private void push(MessageData messageData) {
        List<MessageData> list = new ArrayList<>();
        list.add(messageData);
        List<PushContent> pushContents = filter(list);
        for (PushContent pushContent : pushContents) {
            pushContent.setPushUrl(this.getPushUrl(messageData.getPatrolCarNumber()));
        }
        try {
            startPost(messageData.getParkNumber(), pushContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取推送地址
     *
     * @param patrolCarNumber 巡检车车牌号
     */
    private String getPushUrl(String patrolCarNumber) {
        String pushUrl = ReadConfig.readProperties(patrolCarNumber + "-pushUrl"); // TODO 配置推送地址格式 B6666-pushUrl
        if (pushUrl == null || "".equals(pushUrl.replace(" ", ""))) {
            pushUrl = "卧槽http://120.24.54.186:6010/api/apitools/";
        }
        return pushUrl.replace(" ", "");
    }

    /**
     * 过滤异常和推送成功的数据
     */
    private List<PushContent> filter(List<MessageData> spaceDetails) {
        List<PushContent> result = new ArrayList<>();
        if (spaceDetails == null)
            return result;
        for (MessageData messageData : spaceDetails) {
            if (messageData.getType() == ResultType.ABNORMAL || messageData.getPushStatus() == PushStatus.SUCCESS)
                continue;
            result.add(new PushContent(messageData));
        }
        return result;
    }

    private List<PushResult> startPost(String parkNumber, List<PushContent> pushContent) throws Exception {
        List<PushResult> result = new ArrayList<>();
        for (PushContent p : pushContent) {
            List<PushContent> list = new ArrayList<>();
            list.add(p);
            PushResult pushResult = send(p.getPushUrl(), JsonUtil.toJson(list));
            result.add(pushResult);
            MessageData messageData = new MessageData();
            messageData.setId(p.getId());
            if (pushResult.status == 0) {
                messageData.setPushStatus(PushStatus.SUCCESS);
            } else {
                messageData.setPushStatus(PushStatus.FAIL);
            }
            messageData.setPushTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()));
            this.messageDataService.updatePushStatusAndTime(messageData);
        }
        return result;
    }

    private PushResult send(String uri, String string) throws Exception {
        try {
            String result = reSend(uri, string, 3);
            PushResult pushResult = new PushResult();
            pushResult.message = result;
            if (result.contains("success")) {
                pushResult.status = PushResult.SUCCESS;

            } else {
                pushResult.status = PushResult.FAILED;
            }
            return pushResult;
        } catch (Exception e) {
            PushResult pushResult = new PushResult();
            pushResult.message = "重试三次失败";
            pushResult.status = PushResult.FAILED;

            return pushResult;
        }
    }

    private String reSend(String url, String params, int times) throws Exception {
        PushLogUtils.info("推送地址: " + url + "    巡检推送参数:" + params.substring(0, 300));
        if (times == 0) {
            return sendJson(url, params);
        } else {
            try {
                return sendJson(url, params);
            } catch (Exception e) {
                times -= 1;
                return reSend(url, params, times);
            }
        }
    }

    private String sendJson(String url, String params) throws Exception {
        HttpURLConnection conn = null;
        String resultStr = "";
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept", "text/plain");
            conn.setRequestMethod("POST");

            PrintWriter writer = new PrintWriter(conn.getOutputStream());
            writer.print(params);
            writer.flush();

            int resp = conn.getResponseCode();
            StringBuilder sbuilder = new StringBuilder();
            if (resp != 200)
                throw new IOException("Response code=" + resp);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sbuilder.append(line);
                sbuilder.append("\n");
            }
            resultStr = sbuilder.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            conn.disconnect();
        }
        PushLogUtils.info("推送结果：" + resultStr);
        return resultStr;
    }

}
