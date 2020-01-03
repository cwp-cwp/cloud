package com.cwp.cloud;

import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.user.User;
import com.cwp.cloud.utils.DateUtils;
import com.cwp.cloud.utils.IdWorker;
import com.cwp.cloud.utils.JsonUtils;
import org.junit.Test;
import puzekcommon.utils.JsonUtil;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestXxx {

    @Test
    public void test1() {
        // {"messageId":"5ad872be-00db-48c0-b8b3-72939af27088","clientId":"","messageType":"REQUEST","sendTime":"2019-09-20 10:53:54 158","messageData":{"id":6,"messageId":"5ad872be-00db-48c0-b8b3-72939af27088","batchNumber":"20190921194334007","minBatchNumber":"1174878848542613504","patrolCarId":"","patrolCarNumber":"","areaNumber":"","areaName":"","parkNumber":"vertical-8-4","parkStatus":"","carNumber":"鄂A80JQ7","photographTime":"2019-09-16 19:43:48 355","pushStatus":"","pushTime":null,"sendStatus":"UNSENT"}}
        String json = "{\"messageId\":\"5ad872be-00db-48c0-b8b3-72939af27088\",\"clientId\":\"\",\"messageType\":\"REQUEST\",\"sendTime\":\"2019-09-20 10:53:54 158\",\"messageData\":{\"id\":6,\"messageId\":\"5ad872be-00db-48c0-b8b3-72939af27088\",\"batchNumber\":\"20190921194334007\",\"minBatchNumber\":\"1174878848542613504\",\"patrolCarId\":\"\",\"patrolCarNumber\":\"\",\"areaNumber\":\"\",\"areaName\":\"\",\"parkNumber\":\"vertical-8-4\",\"parkStatus\":\"\",\"carNumber\":\"鄂A80JQ7\",\"photographTime\":\"2019-09-16 19:43:48 355\",\"pushStatus\":\"\",\"pushTime\":null,\"sendStatus\":\"UNSENT\"}}";
        MessageData messageData = JsonUtil.fromJson(json, MessageData.class);
        System.out.println(JsonUtils.objectToJson(messageData));
    }

    @Test
    public void test2() throws ParseException {
//        String car = "粤B6666";
//        System.out.println(car.substring(1));

//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
//
//        System.out.println(time);

//        String batchNumber = "20190926105822905";
//
//        String year = batchNumber.substring(0, 4);
//        String month = batchNumber.substring(4, 6);
//        String day = batchNumber.substring(6, 8);
//        String hour = batchNumber.substring(8, 10);
//        String minutes = batchNumber.substring(10, 12);
//        String seconds = batchNumber.substring(12, 14);
//        String millisecond = batchNumber.substring(14);
//
//        String time = year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds + " " + millisecond;
//        System.out.println(time);

    }

    @Test
    public void test3() {
        List<String> stringList = new ArrayList<>();
        stringList.add("刘德华");
        stringList.add("刘德华");
        stringList.add("刘德华");
        stringList.add("刘德华");
//        stringList.add("");
//        stringList.add(null);
//        stringList.add("张学友");
//        stringList.add("郭富城");
//        stringList.add("郭富城");
        System.err.println(stringList);

        boolean flag = true;

        for (String s : stringList) {
            if (s == null || s.trim().equals("") || !s.equals("刘德华")) {
                flag = false;
            }
        }
        System.err.println(flag);
    }

    private void getFile(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        Map<String, Object> map = new HashMap<>();
        List<String> list1 = new ArrayList<>();
        String system = System.getProperties().getProperty("os.name");
        for (File f : files) {
            if (f.isFile()) {
                if (system.contains("Windows") || system.contains("windows")) {
                    list1.add(f.toString().substring(f.toString().lastIndexOf("\\") + 1));
                } else {
                    list1.add(f.toString().substring(f.toString().lastIndexOf("/") + 1));
                }
            }
        }
        map.put("文件", list1);
        System.out.println(JsonUtils.objectToJson(map));
    }

    @Test
    public void test4() {
        String lastNewCarNumber = "粤B12345";
        String carNumber1 = "粤A22345/桂B12345";
        if (!carNumber1.contains("无")) {
            String[] split = carNumber1.split("/");
            for (String carNumber : split) {
                if (lastNewCarNumber != null && !"无".equals(lastNewCarNumber) && lastNewCarNumber.length() == carNumber.length()) {
                    int count = 0;
                    for (int i = 0; i < lastNewCarNumber.length(); i++) {
                        if (lastNewCarNumber.charAt(i) != carNumber.charAt(i)) {
                            count++;
                        }
                    }
                    if (count <= 1) {
                        System.err.println("正常... carNumber = " + carNumber);
                    }
                }
            }
        }
    }


    @Test
    public void test5() {

        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(22);

        User user2 = new User();
        user2.setId(33);

        User user3 = new User();
        user3.setId(44);

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        List<Integer> userId = getUserId(userList);
        System.err.println(userId);
    }

    private List<Integer> getUserId(List<User> checkInUser) {
        int count = checkInUser.size();
        List<Integer> userId = new ArrayList<>();
        int a = (int) (Math.random() * count + 1);
        int b = (int) (Math.random() * count + 1);
        while (a == b) {
            b = (int) (Math.random() * count + 1);
        }
        System.err.println(a);
        System.err.println(b);
        userId.add(checkInUser.get(a - 1).getId());
        userId.add(checkInUser.get(b - 1).getId());
        return userId;
    }

    @Test
    public void test6() {
        System.err.println(DateUtils.getDateStartPoint(-6)); // 2019-10-16 00:00:00 000
        System.err.println(DateUtils.getDateStartPoint(1)); // 2019-10-23 00:00:00 000

        System.err.println(DateUtils.getDateEndPoint(-6));

        System.err.println(DateUtils.getDateTime(0));

        Long l = DateUtils.timeToLong(DateUtils.getDateTime(2));
        System.err.println(l);

        String time = "2019-10-16 00:00:00 000";
        System.err.println(time.substring(0, 10));

        System.err.println(String.valueOf(""));

    }

    @Test
    public void test7() {
        System.out.println(DateUtils.getDateStartPoint(-1));
        System.out.println(DateUtils.getDateEndPoint(0));
        System.out.println(a(10));
    }

    private int a(int a) {
        if (a > 0) {
            System.err.println("a = " + a);
            return a(a - 1);
        } else {
            return a;
        }
    }

    @Test
    public void test8() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String format = simpleDateFormat.format(new Date(1573776190019L));
        System.out.println(format);
    }

    @Test
    public void test9() {
        IdWorker idWorker = new IdWorker();
        List<Long> list1 = new ArrayList<>();
        List<Long> list2 = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            long nextId = idWorker.nextId();
            if (nextId % 2 == 0) {
                list1.add(nextId);
            } else {
                list2.add(nextId);
            }
        }
        System.err.println(list1.size() + " " + list1);
        System.err.println(list2.size() + " " + list2);
    }


    @Test
    public void test() {
        IdWorker idWorker = new IdWorker();
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            idList.add(idWorker.nextId());
        }
        System.out.println(idList);
    }


}
