package com.example.tools.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 判断某个时间是否是在当前时间的七天之内
     * 判断时间oldTime是否在now的七天之内，如果是返回true,反之返回false
     *
     * @param oldTime
     * @param now
     * @return
     */
    public boolean isLatestWeek(Date oldTime, Date now) {
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(now);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -7);  //设置为7天前
        Date before7days = calendar.getTime();   //得到7天前的时间
        if (before7days.getTime() < oldTime.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param format 日期格式
     * @Description 当前日期时间
     * @Throws
     * @Return java.lang.String
     * @Date 2021-02-02 10:26:13
     **/
    public static String currentDate(String format) {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        return sd.format(c.getTime());
    }

    public static long getCurrentDate(){
        return System.currentTimeMillis();
    }

    /**
     * 将Long时间转成String时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringTime(Long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }


    /**
     * Date对象获取时间字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDateStr(Date date, String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }


}
