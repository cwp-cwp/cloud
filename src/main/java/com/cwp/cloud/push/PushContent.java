package com.cwp.cloud.push;

import com.cwp.cloud.bean.MessageData;
import com.cwp.cloud.bean.ParkSpaceImages;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PushContent implements Serializable {
    private long id;
    private String parkcode;
    private int status;
    private String plateNum;
    private String time;
    private String batchNumber;
    private List<String> pdaphoto;
    private String path;
    private String patrolCarNumber;
    private String pushUrl;

    public PushContent(MessageData messageData) {
        this.id = messageData.getId();
        this.parkcode = messageData.getParkNumber();
        this.status = "无".equals(messageData.getNewCarNumber()) ? 0 : 1; // 无车:0 有车:1
        this.plateNum = messageData.getNewCarNumber() == null ? "无" : messageData.getNewCarNumber();
        this.time = getImageTime(messageData.getParkSpaceImages(), messageData.getRecordTime());
        this.batchNumber = messageData.getBatchNumber();
        this.patrolCarNumber = messageData.getPatrolCarNumber();
        this.pdaphoto = convertImage(messageData.getParkSpaceImages(), messageData.getCarNumber());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getPatrolCarNumber() {
        return patrolCarNumber;
    }

    public void setPatrolCarNumber(String patrolCarNumber) {
        this.patrolCarNumber = patrolCarNumber;
    }

    private String getImageTime(List<ParkSpaceImages> parkSpaceImages, String recordTime) {
        String time = "";
        for (ParkSpaceImages images : parkSpaceImages) {
            /*if (!images.getImage().contains("无")) {
                if (images.getImage().split("_").length > 4) {
                    time = images.getImage().split("_")[3];
                }
            }*/
            if (images.getImage().split("_").length > 4) {
                time = images.getImage().split("_")[3];
            }
        }
        PushLogUtils.info("time: " + time);
        if (time.length() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date(Long.valueOf(time)));
        } else {
            return recordTime;
        }
    }

    private List<String> convertImage(List<ParkSpaceImages> parkSpaceImages, String carNumber) {
        List<String> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        try {
            path = ResourceUtils.getFile("classpath:../../").getPath() + "/scanImage/";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!"无".equals(carNumber)) {
            for (ParkSpaceImages images : parkSpaceImages) {
                if (!images.getImage().contains("无")) {
                    map.put(images.getImagePost(), images.getImage());
                }
            }
        } else {
            for (ParkSpaceImages images : parkSpaceImages) {
                map.put(images.getImagePost(), images.getImage());
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String base64Result = compressionImageAndToBase64(path + "/" + patrolCarNumber + "/" + batchNumber + "/" + entry.getValue());
            if (base64Result != null) {
                result.add(base64Result);
            }
        }
        return result;
    }

    private String ImageToBase64(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()) {
            PushLogUtils.info("图片转base64时图片不存在：" + imgPath);
            return null;
        }
        long time1 = System.currentTimeMillis();
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(Objects.requireNonNull(data));
    }

    // 压缩图片
    private String compressionImageAndToBase64(String filepath) {
        File file = new File(filepath);
        long srcFileSize = file.length();
        if (srcFileSize / 1024 < 100L) {
            return ImageToBase64(filepath);
        }
        if (!file.exists()) {
            PushLogUtils.info("图片压缩时图片不存在：" + filepath);
            return null;
        }
        try {
            Thumbnails.Builder builder = Thumbnails.of(filepath)
                    .scale(1f)
                    .outputQuality(0.15f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
            builder.toOutputStream(baos);
            byte[] byteArray = baos.toByteArray();
            // 对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            // 返回Base64编码过的字节数组字符串
            String baseStr = encoder.encode(Objects.requireNonNull(byteArray));
            return baseStr;
        } catch (IOException e) {
            PushLogUtils.info("压缩出现异常");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(simpleDateFormat.format(new Date(Long.valueOf("1571907893593"))));
    }
}
