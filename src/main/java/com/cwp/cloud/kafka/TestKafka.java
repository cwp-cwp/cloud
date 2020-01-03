package com.cwp.cloud.kafka;

import com.cwp.cloud.utils.JsonUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * 测试 Kafka 消息
 */
public class TestKafka {

    public static void main(String[] args) {

        new Thread(() -> {
            long threadId = Thread.currentThread().getId();
            System.out.println("云端接收消息线程 threadId: " + threadId);
            receiveMessage();
        }).start();

        new Thread(() -> {
            long threadId = Thread.currentThread().getId();
            System.out.println("云端接收图片消息线程 threadId: " + threadId);
            receiveImageData();
        }).start();

    }


    /**
     * 接收巡检车发送的 json 字符串
     */
    private static void receiveMessage() {
        KafkaConsumer consumer = getKafkaConsumer(); // 创建消费者
        // 消费者订阅的topic, 可同时订阅多个
        consumer.subscribe(Arrays.asList("request-BDX8985"));
        System.out.println("成功订阅文字主题 === > " + JsonUtils.objectToJson(Arrays.asList("request-BDX8985")));
        KafkaProducer<String, String> kafkaProducer = getKafkaProducer(); // 创建生产者，用于回应
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100); // 读取数据，读取超时时间为100ms
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("接收到巡检车的消息 offset = " + record.offset() + "\t topic = " + record.topic() + "\t key = " + record.key() + "\t value = " + record.value());
                // 回应消息
                kafkaProducer.send(new ProducerRecord<>(record.topic().replace("request", "response"), record.topic(), record.key()), (metadata, exception) -> {
                    if (metadata != null) {
                        System.err.println(metadata.partition() + "---" + metadata.offset());
                    }
                });
            }
        }
    }

    /**
     * 创建消费者
     */
    private static KafkaConsumer<String, String> getKafkaConsumer() {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", "192.168.1.110:9092");
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
    private static KafkaProducer<String, String> getKafkaProducer() {
        Properties props = new Properties();
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", "192.168.1.110:9092");
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
     * 接收图片
     */
    private static void receiveImageData() {
        KafkaConsumer imageConsumer = getImageKafkaConsumer(); // 创建图片消费者
        imageConsumer.subscribe(Arrays.asList("request-image-BDX8985")); // 消费者订阅的topic, 可同时订阅多个
        System.out.println("成功订阅图片主题 ===> " + JsonUtils.objectToJson(Arrays.asList("request-image-BDX8985")));
        while (true) {
            ConsumerRecords<String, byte[]> records = imageConsumer.poll(100); // 读取数据，读取超时时间为100ms
            for (ConsumerRecord<String, byte[]> record : records) {
                byte[] data = record.value();
                System.out.println("接收到的图片主题 = " + record.topic() + " \t 分区 = " + record.partition() + "\t offset = " + record.offset() + "\t 接收到的图片长度 = " + data.length + "\t 图片名 = " + record.key());
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                BufferedImage image;
                OutputStream bOut;
                String imagePath = "C:\\Users\\liheng\\Desktop\\11111";
                try {
                    image = ImageIO.read(in);
                    //TODO 和巡检车约好的 key:  粤B6666@20190921194334007@-2687_84422_181935_1568634238676_35_0_0_0_0_无_前_1.jpg
                    String[] split = record.key().split("@");
                    System.out.println("图片路径 imagePath = " + imagePath);
                    File file  = new File(imagePath);
                    if (!file.exists()) {
                        file.mkdirs();//创建文件夹
                    }
                    bOut = new FileOutputStream(imagePath + "\\" + split[2]);
                    ImageIO.write(image, "jpg", bOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建图片消费者
     */
    private static KafkaConsumer<String, byte[]> getImageKafkaConsumer() {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", "192.168.1.110:9092");
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
}
