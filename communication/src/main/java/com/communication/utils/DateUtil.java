package com.communication.utils;

import com.communication.BaseApplication;
import com.communication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtil {

    private DateUtil() { }

    public static final String FORMAT_DATE      = "yyyy-MM-dd";
    public static final String FORMAT_TIME      = "yyyy-MM-dd  HH:mm:ss";

    /**
     * 时间格式化
     */
    public static String formatDate(String format, Long time) {
        return formatDate(new SimpleDateFormat(format, Locale.CHINA), time);
    }

    /**
     * 时间格式化
     */
    public static String formatTime(Long time) {
        return formatDate(new SimpleDateFormat(FORMAT_TIME, Locale.CHINA), time);
    }

    /**
     * 时间格式化
     */
    public static String formatDate(SimpleDateFormat format, Long time) {
        if (null == time || time <= 0) { return ""; }
        return format.format(new Date(String.valueOf(time).length() == 13 ? time : time * 1000));
    }
    /**
     * 获取运营商
     */
    public static String getOpera(String imsi) {
        imsi = imsi.substring(0, 5);
        switch (imsi) {
            case "46000":

            case "46002":

            case "46007":

            case "41004":

                return BaseApplication.getInstance().getResources().getStringArray(R.array.operator)[0];

            case "46001":

            case "46006":
            case "46009":

                return BaseApplication.getInstance().getResources().getStringArray(R.array.operator)[1];

            case "46011":

            case "46005":
            case "46003":
                return BaseApplication.getInstance().getResources().getStringArray(R.array.operator)[2];

            default:

                return BaseApplication.getInstance().getResources().getStringArray(R.array.operator)[3];
        }
    }
    /**
     * 获取运营商
     */
    public static int getOpera1(String imsi) {
        imsi = imsi.substring(0, 5);
        switch (imsi) {
            case "46000":

            case "46002":

            case "46007":

            case "46004":

                return 1;

            case "46001":

            case "46006":

            case "46009":
                return 2;

            case "46011":

            case "46005":

            case "46003":
                return 3;

            default:

                return 4;
        }
    }
    public static long getTime(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime().getTime();
    }
    public static String getTime(Date date) {
        TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
        Calendar calendar = Calendar.getInstance(t);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int ss = calendar.get(Calendar.SECOND);

        String time = year + "-" + (month < 10 ? "0" : "") + month + '-'
                + (day < 10 ? "0" : "") + day + ' ' + (hour < 10 ? "0" : "")
                + hour + ':' + (min < 10 ? "0" : "") + min + ":"
                + (ss < 10 ? "0" : "") + ss;
        return time;
    }
    public static String getDate() {
        TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
        Calendar calendar = Calendar.getInstance(t);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = year + "-" + (month < 10 ? "0" : "") + month + '-'
                + (day < 10 ? "0" : "") + day;
        return date;
    }
    public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_FORMAT_DATE);
        }

    };
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_FORMAT_TIME = "HH:mm:ss";
    public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        }

    };
    public static final String DEFAULT_FORMAT_DATE = "yyyy/MM/dd";
    public static String getOtherDay(int diff) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.DATE, diff);
        return getDateFormat(mCalendar.getTime());
    }
    public static String getDateFormat(Date date) {
        return dateSimpleFormat(date, defaultDateFormat.get());
    }
    public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
        if (format == null)
            format = defaultDateTimeFormat.get();
        return (date == null ? "" : format.format(date));
    }



}
