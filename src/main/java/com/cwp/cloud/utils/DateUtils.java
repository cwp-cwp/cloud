package com.cwp.cloud.utils;

import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具类
 * Created by chen_wp on 2019-10-22.
 */
public class DateUtils {

    /**
     * 获取当前时间的前后时间
     *
     * @param day 前(后) day 天
     */
    public static String getDateTime(int day) {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String dateTime = formatter.format(date);
        return dateTime;
    }

    /**
     * 获取 day 天的零点
     *
     * @param day 前后 day 天
     */
    public static String getDateStartPoint(int day) {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return dateFormat.format(date);
    }

    /**
     * 获取 day 天的末点
     *
     * @param day 前后 day 天
     */
    public static String getDateEndPoint(int day) {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return dateFormat.format(date);
    }

    /**
     * 将时间字符串转换成 long 类型
     *
     * @param time 时间字符串，格式为 2019-10-22 20:30:53 982
     */
    public static Long timeToLong(String time) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        LocalDateTime parse = LocalDateTime.parse(time, dtf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
