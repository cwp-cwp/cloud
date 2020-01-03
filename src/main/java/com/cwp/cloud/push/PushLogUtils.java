package com.cwp.cloud.push;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class PushLogUtils {

    private static final String logName = "push";

    private static FileOutputStream logFileOutputStream;

    static {
        logFileOutputStream = getLogFileOutputStream(logName);
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
            if (logFileOutputStream == null || !checkFile(logName)) {
                logFileOutputStream = getLogFileOutputStream(logName);
            }
            logFileOutputStream.write(log.getBytes());
            logFileOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
