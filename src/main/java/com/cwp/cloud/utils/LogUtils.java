package com.cwp.cloud.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class LogUtils {

    private static final String cloudLogName = "cloud";
    private static final String allImageLogName = "allImage";
    private static final String imageLogName = "image";
    private static final String modifyLogName = "modify";
    private static final String distributionLogName = "distribution";

    private static FileOutputStream cloudLogFileOutputStream;
    private static FileOutputStream allImageLogFileOutputStream;
    private static FileOutputStream imageLogFileOutputStream;
    private static FileOutputStream modifyLogFileOutputStream;
    private static FileOutputStream distributionLogFileOutputStream;

    static {
        cloudLogFileOutputStream = getLogFileOutputStream(cloudLogName);
        allImageLogFileOutputStream = getLogFileOutputStream(allImageLogName);
        imageLogFileOutputStream = getLogFileOutputStream(imageLogName);
        modifyLogFileOutputStream = getLogFileOutputStream(modifyLogName);
        distributionLogFileOutputStream = getLogFileOutputStream(distributionLogName);
    }

    private static FileOutputStream getLogFileOutputStream(String logName) {
        FileOutputStream logFileOutputStream = null;
        try {
            String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
            File file = new File("/var/log/cloud", fileName);
            if (!file.exists()) {
                file.mkdirs();
            }
            logFileOutputStream = new FileOutputStream(file.getPath() + "/" + logName + ".log", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return logFileOutputStream;
    }

    private static boolean checkFile(String logName) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
        File file = new File("/var/log/cloud/" + fileName, logName + ".log");
        return file.exists();
    }

    /**
     * 记录日志
     */
    public static void info(String log) {
        try {
            log = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()) + " ] \t" + log;
            if (cloudLogFileOutputStream == null || !checkFile(cloudLogName)) {
                cloudLogFileOutputStream = getLogFileOutputStream(cloudLogName);
            }
            cloudLogFileOutputStream.write(log.getBytes());
            cloudLogFileOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void info2(String log) {
        try {
            log = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()) + " ] \t" + log;
            if (allImageLogFileOutputStream == null || !checkFile(allImageLogName)) {
                allImageLogFileOutputStream = getLogFileOutputStream(allImageLogName);
            }
            allImageLogFileOutputStream.write(log.getBytes());
            allImageLogFileOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void info3(String log) {
        try {
            log = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()) + " ] \t" + log;
            if (modifyLogFileOutputStream == null || !checkFile(modifyLogName)) {
                modifyLogFileOutputStream = getLogFileOutputStream(modifyLogName);
            }
            modifyLogFileOutputStream.write(log.getBytes());
            modifyLogFileOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void info4(String log) {
        try {
            log = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()) + " ] \t" + log;
            if (distributionLogFileOutputStream == null || !checkFile(distributionLogName)) {
                distributionLogFileOutputStream = getLogFileOutputStream(distributionLogName);
            }
            distributionLogFileOutputStream.write(log.getBytes());
            distributionLogFileOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void info5(String log) {
        try {
            log = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()) + " ] \t" + log;
            if (imageLogFileOutputStream == null || !checkFile(imageLogName)) {
                imageLogFileOutputStream = getLogFileOutputStream(imageLogName);
            }
            imageLogFileOutputStream.write(log.getBytes());
            imageLogFileOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
