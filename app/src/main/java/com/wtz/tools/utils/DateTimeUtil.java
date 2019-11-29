package com.wtz.tools.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    /**
     * @param format e.g. "yy-MM-dd_HH-mm-ss"
     * @return DateTime
     */
    public static String getCurrentDateTime(String format) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(format);
        String nowTime = df.format(date);
        return nowTime;
    }

    /**
     * @param date   指定的日期
     * @param format 返回的日期格式 e.g."yyyy-MM-dd_HH-mm-ss"
     * @return
     */
    public static String getSpecifiedDateTime(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        String nowTime = df.format(date);
        return nowTime;
    }

    /**
     * @param timeMillis 指定的日期毫秒数
     * @param format     返回的日期格式 e.g."yyyy-MM-dd_HH-mm-ss"
     * @return
     */
    public static String getSpecifiedDateTime(String timeMillis, String format) {
        try {
            Date date = new Date(Long.parseLong(timeMillis));
            return getSpecifiedDateTime(date, format);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param dateString e.g. "2016‐03‐03 14:28:02"
     * @param format     e.g. "yyyy-MM-dd HH:mm:ss"
     * @return Date
     */
    public static Date changeStringToDate(String dateString, String format) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(dateString);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"
     * @return
     */
    public static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取指定日期时间对应的毫秒数
     *
     * @param dateTime "2016-12-08 12:00:00"
     * @param format   "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static long getDateTimeMillis(String dateTime, String format) {
        Date date = DateTimeUtil.changeStringToDate(dateTime, format);
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

    /**
     * 把剩余毫秒数转化成“时:分:秒”字符串
     *
     * @param timeMilli
     * @return
     */
    public static String changeRemainTimeToHms(long timeMilli) {
        timeMilli = timeMilli / 1000;// 毫秒数转秒数
        int second = (int) (timeMilli % 60);// 秒数除60得分钟数再取余得秒数
        int minute = (int) (timeMilli / 60 % 60);// 秒数除两个60得小时再取余得分钟数
        int hour = (int) (timeMilli / 60 / 60);// 秒数除两个60得小时数
        String hourString = formatTime(String.valueOf(hour));
        String minuteString = formatTime(String.valueOf(minute));
        String secondString = formatTime(String.valueOf(second));
        return hourString + ":" + minuteString + ":" + secondString;
    }

    private static String formatTime(String original) {
        if (original != null && original.length() < 2) {
            original = "0" + original;
        }
        return original;
    }

    public static int getWeekOfYear(Calendar calendar) {
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            // Calendar的周一实际为我们中国人的上周的星期七
            week = week - 1;
        }
        return week;
    }

    /**
     * 参数举例
     * String format = "yyyy-MM-dd";
     * String dateString = "2016-12-8";
     */
    public static int getWeekOfYear(String dateString, String format) {
        Date date = DateTimeUtil.changeStringToDate(dateString, format);
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            // Calendar的周一实际为我们中国人的上周的星期七
            week = week - 1;
        }
        return week;
    }

    public static int getWeekOfMonth(Calendar calendar) {
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            // Calendar的周一实际为我们中国人的上周的星期七
            week = week - 1;
        }
        return week;
    }

    /**
     * 参数举例
     * String format = "yyyy-MM-dd";
     * String dateString = "2016-12-8";
     */
    public static int getWeekOfMonth(String dateString, String format) {
        Date date = DateTimeUtil.changeStringToDate(dateString, format);
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            // Calendar的周一实际为我们中国人的上周的星期七
            week = week - 1;
        }
        return week;
    }

    public static int getDayOfWeek(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            // Calendar的周一实际为我们中国人的上周的星期七
            day = 7;
        } else {
            day = day - 1;
        }
        return day;
    }

    /**
     * 参数举例
     * String format = "yyyy-MM-dd";
     * String dateString = "2016-12-8";
     */
    public static int getDayOfWeek(String dateString, String format) {
        Date date = DateTimeUtil.changeStringToDate(dateString, format);
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            // Calendar的周一实际为我们中国人的上周的星期七
            day = 7;
        } else {
            day = day - 1;
        }
        return day;
    }

    /**
     * @param dateStr   日期字符串
     * @param formatStr 格式字符串
     * @return 日期字符串是否符合指定格式字符串
     */
    public static boolean isDateFormatValid(String dateStr, String formatStr) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            // 设置lenient为false，否则SimpleDateFormat会比较宽松地验证日期
            format.setLenient(false);
            format.parse(dateStr);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static boolean isYeaterday(Date oldTime, Date newTime) throws ParseException {
        if (newTime == null) {
            newTime = new Date();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String oldStr = format.format(oldTime);
        String todayStr = format.format(newTime);
        Date oldDay = format.parse(oldStr);
        Date today = format.parse(todayStr);
        if ((today.getTime() - oldDay.getTime()) > 0) {
            return true;
        }
        return false;
    }

}
