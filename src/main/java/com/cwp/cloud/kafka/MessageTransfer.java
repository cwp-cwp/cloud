package com.cwp.cloud.kafka;

import com.cwp.cloud.bean.*;
import com.cwp.cloud.service.CheckService;
import com.cwp.cloud.service.KafkaTopicService;
import com.cwp.cloud.service.MessageDataService;
import com.cwp.cloud.utils.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import puzekcommon.utils.JsonUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 消息通信类
 * Created by chen_wp on 2019-09-19.
 */
@Component
public class MessageTransfer implements CommandLineRunner {

    private final Object LOCK = new Object();

    private List<String> threeLevelTopicList = new ArrayList<>(); // 所有三级主题
    private List<String> imageTopicList = new ArrayList<>(); // 图片主题 TODO 稳定之后考虑使用同一个主题不同分区发送文字和图片
    private List<String> allImageTopicList = new ArrayList<>(); // 接收全部图片的主题

    private boolean wordsFlag = true; // 控制循环，有新增主题时，重新订阅新文字主题
    private boolean imageFlag = true; // 控制循环，有新增主题时，重新订阅新图片主题
    private boolean allImageFlag = true; // 控制循环，有新增主题时，重新订阅新图片主题(接收全部拍到的图片的那个主题)

    private final MessageDataService messageDataService;
    private final KafkaTopicService kafkaTopicService;
    private final CheckService checkService;
    private final IdWorker idWorker;

    @Autowired
    public MessageTransfer(MessageDataService messageDataService, KafkaTopicService kafkaTopicService, CheckService checkService, IdWorker idWorker) {
        this.messageDataService = messageDataService;
        this.kafkaTopicService = kafkaTopicService;
        this.checkService = checkService;
        this.idWorker = idWorker;
    }

    @Override
    public void run(String... args) {
        this.getThreeLevelTopic();
        new Thread(() -> {
            long threadId = Thread.currentThread().getId();
            LogUtils.info("云端接收消息线程 threadId: " + threadId);
            this.receiveMessage();
        }).start();

        new Thread(() -> {
            long threadId = Thread.currentThread().getId();
            LogUtils.info5("云端接收图片消息线程 threadId: " + threadId);
            this.receiveImageData();
        }).start();

//        new Thread(() -> {
//            long threadId = Thread.currentThread().getId();
//            LogUtils.info2("云端接收全部图片消息线程 threadId: " + threadId);
//            this.receiveAllImageData();
//        }).start();
    }


    /**
     * 接收巡检车发送的 json 字符串
     */
    private void receiveMessage() {
        KafkaConsumer<String, String> consumer = this.getKafkaConsumer(); // 创建消费者
        // 消费者订阅的topic, 可同时订阅多个
        consumer.subscribe(threeLevelTopicList);
        LogUtils.info("成功订阅文字主题 === > " + JsonUtils.objectToJson(threeLevelTopicList));
        KafkaProducer<String, String> kafkaProducer = this.getKafkaProducer(); // 创建生产者，用于回应
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(100); // 读取数据，读取超时时间为100ms
                for (ConsumerRecord<String, String> record : records) {
                    LogUtils.info("接收到巡检车的消息 offset = " + record.offset() + "\t topic = " + record.topic() + "\t key = " + record.key() + "\t value = " + record.value());
                    // 保存到数据库
                    Message message = JsonUtil.fromJson(record.value(), Message.class);
                    MessageData messageData = message.getMessageData();
                    messageData.setId(idWorker.nextId());
                    messageData.setSendStatus(SendStatus.SUCCESS);
                    LogUtils.info("messageData ===> " + JsonUtil.toJson(messageData));
//                    List<String> messageIdList = this.messageDataService.getMessageIdByBatchNumAndPatCarNum(messageData.getBatchNumber(), messageData.getPatrolCarNumber());
//                    if (messageIdList != null && messageIdList.size() > 0 && messageIdList.contains(messageData.getMessageId())) {
//                        continue;
//                    }
                    MessageData dataByMsgId = this.messageDataService.getMessageDataByMsgId(messageData.getMessageId());
                    if (dataByMsgId != null) {
                        continue;
                    }
                    this.saveMessageData(messageData);
                    // 回应消息
                    kafkaProducer.send(new ProducerRecord<>(record.topic().replace("request", "response"), record.topic(), record.key()), (metadata, exception) -> {
                        if (metadata != null) {
                            LogUtils.info(metadata.partition() + "---" + metadata.offset());
                        }
                    });
                }
                if (!wordsFlag) {
                    if (consumer != null) {
                        consumer.close();
                        consumer = this.getKafkaConsumer();
                        consumer.subscribe(threeLevelTopicList);
                        LogUtils.info("重新订阅的文字主题 = " + JsonUtils.objectToJson(threeLevelTopicList));
                        wordsFlag = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存接收到的消息
     *
     * @param messageData 消息对象
     */
    private void saveMessageData(MessageData messageData) {
        // TODO 对于状态为异常的数据，拿今天的上一个批次的那一条数据的 newCarNumber 和新接收到的这一条数据的 carNumber 比对，如果只有一个字符不一样，就认为新接收到的这一条数据的车牌还是上一个批次的 newCarNumber, 这样就可以减少异常的数量
        if (messageData.getType() == ResultType.ABNORMAL) {
            String lastNewCarNumber = this.messageDataService.getLastNewCarNumber(messageData.getPatrolCarNumber(), messageData.getParkNumber(), DateUtils.getDateTime(-1), DateUtils.getDateTime(1));
            LogUtils.info("上一次的 newCarNumber = " + lastNewCarNumber);
            if (messageData.getCarNumber() != null) {
                if (messageData.getCarNumber().trim().equals("无") && lastNewCarNumber != null && lastNewCarNumber.trim().equals("无")) { // 上一次是 "无", 这一次也是 "无", 就认为这一次是 "无"
                    messageData.setNewCarNumber(lastNewCarNumber);
                    messageData.setType(ResultType.NORMAL);
                } else if (messageData.getCarNumber().trim().equals("无") && messageData.getStatus() == ParkStatus.无车) { // 摄像头识别和激光判断都是 "无", 那么就认为是 "无"
                    messageData.setNewCarNumber("无");
                    messageData.setType(ResultType.NORMAL);
                } else {
                    String[] split = messageData.getCarNumber().split("/");
                    for (String carNumber : split) {
                        if (lastNewCarNumber != null && !"无".equals(lastNewCarNumber) && lastNewCarNumber.length() == carNumber.length()) {
                            int count = 0;
                            for (int i = 0; i < lastNewCarNumber.length(); i++) {
                                if (lastNewCarNumber.charAt(i) != carNumber.charAt(i)) {
                                    count++;
                                }
                            }
                            if (count <= 1) {
                                messageData.setNewCarNumber(lastNewCarNumber);
                                messageData.setType(ResultType.NORMAL);
                            }
                        }
                    }
                }
            }
        }
        if (messageData.getCarNumber().length() > 100) {
            return;
        }
        this.messageDataService.saveMessageData(messageData);
        List<String> batchNumByPatrolCarNum = this.messageDataService.getBatchNumByPatrolCarNumBetweenTime(messageData.getPatrolCarNumber(), DateUtils.getDateTime(-1), DateUtils.getDateTime(1));
        if (!batchNumByPatrolCarNum.contains(messageData.getBatchNumber())) {
            BatchNumber batchNumber = new BatchNumber(idWorker.nextId(), messageData.getPatrolCarNumber(), messageData.getBatchNumber(), this.getTime(messageData.getBatchNumber()));
            this.messageDataService.saveBatchNumber(batchNumber);
        }
    }

    /**
     * 将 batchNumber 转换成时间格式
     */
    private String getTime(String batchNumber) {
        String time = null;
        if (batchNumber.length() >= 17) {
            String year = batchNumber.substring(0, 4);
            String month = batchNumber.substring(4, 6);
            String day = batchNumber.substring(6, 8);
            String hour = batchNumber.substring(8, 10);
            String minutes = batchNumber.substring(10, 12);
            String seconds = batchNumber.substring(12, 14);
            String millisecond = batchNumber.substring(14, 17);
            time = year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds + " " + millisecond;
        }
        return time;
    }

    /**
     * 创建消费者
     */
    private KafkaConsumer<String, String> getKafkaConsumer() {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", this.getKafkaIpAndPort());
//        props.put("bootstrap.servers", "192.168.154.129:9092");
        // 制定consumer group
        props.put("group.id", "test");
        // 是否自动确认offset
        props.put("enable.auto.commit", "true");
        // 自动确认offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        // key的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value的序列化类
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 定义consumer
        return new KafkaConsumer<>(props);
    }

    /**
     * 创建生产者
     */
    private KafkaProducer<String, String> getKafkaProducer() {
        Properties props = new Properties();
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", this.getKafkaIpAndPort());
//        props.put("bootstrap.servers", "192.168.154.129:9092");
        // 等待所有副本节点的应答
        props.put("acks", "all");
        // 消息发送最大尝试次数
        props.put("retries", 0);
        // 一批消息处理大小
        props.put("batch.size", 20971520);
        props.put("max.request.size", 2097152);
        // 增加服务端请求延时
        props.put("linger.ms", 1);
        // 发送缓存区内存大小
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    /**
     * 接收全部的图片
     */
    private void receiveAllImageData() {
        KafkaConsumer<String, byte[]> imageConsumer = this.getImageKafkaConsumer(); // 创建图片消费者
        imageConsumer.subscribe(allImageTopicList); // 消费者订阅的topic, 可同时订阅多个
        LogUtils.info2("成功订阅图片主题 ===> " + JsonUtils.objectToJson(allImageTopicList));
        ByteArrayInputStream byteArrayInputStream = null;
        BufferedImage bufferedImage;
        OutputStream outputStream = null;
        String imagePath;
        while (true) {
            ConsumerRecords<String, byte[]> records = imageConsumer.poll(100); // 读取数据，读取超时时间为100ms
            for (ConsumerRecord<String, byte[]> record : records) {
                byte[] data = record.value();
                LogUtils.info2("接收到的图片主题 = " + record.topic() + " \t 分区 = " + record.partition() + "\t offset = " + record.offset() + "\t 接收到的图片长度 = " + data.length + "\t 图片名 = " + record.key());
                try {
                    byteArrayInputStream = new ByteArrayInputStream(data);
                    bufferedImage = ImageIO.read(byteArrayInputStream);
                    //TODO 和巡检车约好的 key:  B6666@20190921194334007@-2687_84422_181935_1568634238676_35_0_0_0_0_无_前_1.jpg
                    String[] split = record.key().split("@");
                    imagePath = ResourceUtils.getFile("classpath:../../").getPath() + "/allScanImage/" + split[0] + "/" + split[1];
//                    imagePath = ResourceUtils.getFile("classpath:../../").getPath() + "/scanImage/" + split[0] + "/" + split[1];
                    LogUtils.info2("图片路径 imagePath = " + imagePath);
                    File file;
                    if (split[2].split("/").length > 1) {
                        file = new File(imagePath + "/" + split[2].split("/")[0]);
                    } else {
                        file = new File(imagePath);
                    }
                    if (!file.exists()) {
                        file.mkdirs();//创建文件夹
                    }
                    outputStream = new FileOutputStream(imagePath + "/" + split[2]);
                    if (bufferedImage != null && outputStream != null) {
                        ImageIO.write(bufferedImage, "jpg", outputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    this.close(byteArrayInputStream, outputStream);
                }
            }
            if (!allImageFlag) {
                if (imageConsumer != null) {
                    imageConsumer.close();
                    imageConsumer = this.getImageKafkaConsumer();
                    imageConsumer.subscribe(allImageTopicList);
                    LogUtils.info2("重新订阅的图片主题 = " + JsonUtils.objectToJson(allImageTopicList));
                    allImageFlag = true;
                }
            }
        }
    }

    /**
     * 接收图片
     */
    private void receiveImageData() {
        KafkaConsumer<String, byte[]> imageConsumer = this.getImageKafkaConsumer(); // 创建图片消费者
        imageConsumer.subscribe(imageTopicList); // 消费者订阅的topic, 可同时订阅多个
        LogUtils.info5("成功订阅图片主题 ===> " + JsonUtils.objectToJson(imageTopicList));
        ByteArrayInputStream byteArrayInputStream = null;
        BufferedImage bufferedImage;
        OutputStream outputStream = null;
        String imagePath;
        while (true) {
            ConsumerRecords<String, byte[]> records = imageConsumer.poll(100); // 读取数据，读取超时时间为100ms
            for (ConsumerRecord<String, byte[]> record : records) {
                byte[] data = record.value();
                LogUtils.info5("接收到的图片主题 = " + record.topic() + " \t 分区 = " + record.partition() + "\t offset = " + record.offset() + "\t 接收到的图片长度 = " + data.length + "\t 图片名 = " + record.key());
                try {
                    byteArrayInputStream = new ByteArrayInputStream(data);
                    bufferedImage = ImageIO.read(byteArrayInputStream);
                    //TODO 和巡检车约好的 key:  B6666@20190921194334007@-2687_84422_181935_1568634238676_35_0_0_0_0_无_前_1.jpg
                    String[] split = record.key().split("@");
                    imagePath = ResourceUtils.getFile("classpath:../../").getPath() + "/scanImage/" + split[0] + "/" + split[1];
                    LogUtils.info5("图片路径 imagePath = " + imagePath);
                    File file;
                    if (split[2].split("/").length > 1) {
                        file = new File(imagePath + "/" + split[2].split("/")[0]);
                    } else {
                        file = new File(imagePath);
                    }
                    if (!file.exists()) {
                        file.mkdirs();//创建文件夹
                    }
                    outputStream = new FileOutputStream(imagePath + "/" + split[2]);
                    if (bufferedImage != null && outputStream != null) {
                        ImageIO.write(bufferedImage, "jpg", outputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    this.close(byteArrayInputStream, outputStream);
                }
            }
            if (!imageFlag) {
                if (imageConsumer != null) {
                    imageConsumer.close();
                    imageConsumer = this.getImageKafkaConsumer();
                    imageConsumer.subscribe(imageTopicList);
                    LogUtils.info5("重新订阅的图片主题 = " + JsonUtils.objectToJson(imageTopicList));
                    imageFlag = true;
                }
            }
        }
    }

    private void close(ByteArrayInputStream byteArrayInputStream, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (byteArrayInputStream != null) {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 创建图片消费者
     */
    private KafkaConsumer<String, byte[]> getImageKafkaConsumer() {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", this.getKafkaIpAndPort());
//        props.put("bootstrap.servers", "192.168.154.129:9092");
        // 制定consumer group
        props.put("group.id", "test");
        // 是否自动确认offset
        props.put("enable.auto.commit", "true");
        // 自动确认offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        // key的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value的序列化类
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        // 定义consumer
        return new KafkaConsumer<>(props);
    }


    /**
     * 定时从数据库中获取所有三级主题
     */
    @Scheduled(fixedRate = 3000)
    private void getThreeLevelTopic() {
        synchronized (LOCK) {
            List<KafkaTopic> treeLevelTopic = this.kafkaTopicService.getAllThreeLevelTopic();
            for (KafkaTopic t : treeLevelTopic) {
                if (this.threeLevelTopicList.contains(t.getTopicName())) {
                    continue;
                }
                this.threeLevelTopicList.add(t.getTopicName());
                // TODO 图片主题统一命名 request-image-车牌号 (例如 request-image-B6666)
                String imageTopic = t.getTopicName().split("-")[0] + "-image-" + t.getTopicName().split("-")[1];
                // TODO 接收全部图片的主题统一命名: request-allImage-车牌号 (例如 request-allImage-B6666)
                String allImageTopic = t.getTopicName().split("-")[0] + "-allImage-" + t.getTopicName().split("-")[1];
                this.imageTopicList.add(imageTopic);
                this.allImageTopicList.add(allImageTopic);
                wordsFlag = false; // 发现有新增主题，改变 flag, 重新订阅主题
                imageFlag = false;
                allImageFlag = false;
            }
        }
    }

    private String getKafkaIpAndPort() {
        String kafkaIpPort = ReadConfig.readProperties("kafkaIpPort"); // Kafka 服务地址
        if (kafkaIpPort == null || "".equals(kafkaIpPort.replace(" ", ""))) {
            kafkaIpPort = "127.0.0.1:9092";
        }
        return kafkaIpPort.replace(" ", "");
    }

    private String getBatchSize() {
        String batchSize = ReadConfig.readProperties("batchSize"); // 一批消息处理大小
        if (batchSize == null || "".equals(batchSize.replace(" ", ""))) {
            batchSize = "209715200";
        }
        return batchSize.replace(" ", "");
    }

    private String getMaxRequestSize() {
        String maxRequestSize = ReadConfig.readProperties("maxRequestSize"); // 最大请求大小
        if (maxRequestSize == null || "".equals(maxRequestSize.replace(" ", ""))) {
            maxRequestSize = "209715200";
        }
        return maxRequestSize.replace(" ", "");
    }

    private String getBufferMemory() {
        String maxRequestSize = ReadConfig.readProperties("bufferMemory"); // 发送缓存区内存大小
        if (maxRequestSize == null || "".equals(maxRequestSize.replace(" ", ""))) {
            maxRequestSize = "335544320";
        }
        return maxRequestSize.replace(" ", "");
    }

}
