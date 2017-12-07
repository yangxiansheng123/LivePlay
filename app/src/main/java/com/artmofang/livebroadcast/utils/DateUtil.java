package com.artmofang.livebroadcast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/1/11.
 */
public class DateUtil {

    public static final String DATE_FULL_STR = "yyyy-MM-dd   HH:mm:ss";
    private static SimpleDateFormat sf = null;

    public static final String DATE_FULL = "yyyy年MM月dd日";

    /**
     * 将字符串转为时间戳
     *
     * @param time
     */
    public static long getStringToDate(String time, String parseString) {
        sf = new SimpleDateFormat(parseString);
        Date date = null;
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }




    // 将字符串转为时间戳

    public static String getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;


        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);

            re_time = str.substring(0, 10);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return re_time;
    }




    // 将时间戳转为字符串
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));


        return re_StrTime;


    }


    /*时间戳转换成字符窜-2*/
    public static String getDateToString2(long time) {

        sf = new SimpleDateFormat(DATE_FULL_STR);
        return sf.format(time);
    }


    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startDate 开始日期
     * @return
     */
    public static String twoDateDistance(long startDate) {

        Date endDate = Calendar.getInstance().getTime();
        if (startDate == 0 || endDate == null) {
            return null;
        }

        long timeLong = endDate.getTime() - startDate;

        if (isYeaterday(startDate, null) == 0) {
            Date sDate = new Date(startDate);
            int hour = sDate.getHours();
            int min = sDate.getMinutes();

            String sMin = String.valueOf(min);
            if (sMin.length() == 1) {
                sMin = "0" + sMin;
            }
            return "昨天 " + hour + ":" + sMin;
        }

        if (timeLong < 60 * 1000)
//            return timeLong / 1000 + "秒前";
            return "刚刚";

        else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟前";
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 10) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return (timeLong + 1) + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(startDate);
        }
    }

    /**
     * @param oldTime 较小的时间
     * @param newTime 较大的时间 (如果为空   默认当前时间 ,表示和当前时间相比)
     * @return -1 ：同一天.    0：昨天 .   1 ：至少是前天.
     * @throws ParseException 转换异常
     * @author LuoB.
     */
    public static int isYeaterday(long oldTime, Date newTime) {
        if (newTime == null) {
            newTime = new Date();
        }
        //将下面的 理解成  yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = null;
        try {
            today = format.parse(todayStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //昨天 86400000=24*60*60*1000 一天
        if ((today.getTime() - oldTime) > 0 && (today.getTime() - oldTime) <= 86400000) {
            return 0;
        } else if ((today.getTime() - oldTime) <= 0) { //至少是今天
            return -1;
        } else { //至少是前天
            return 1;
        }

    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     * @return
     */
    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }


}
