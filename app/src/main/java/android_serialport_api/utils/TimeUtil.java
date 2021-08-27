package android_serialport_api.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>文件描述：时间转换工具<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/11/29<p>
 * <p>更新时间：2019/11/29<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class TimeUtil {
    public static final String ACTION_CLOCK_RESET = "ACTION_CLOCK_RESET";
    public static final String ACTION_DAY_NIGHT_SET = "ACTION_DAY_NIGHT_SET";
    private static final String TAG = TimeUtil.class.getName();
    /**
     * 整型日期格式
     */
    public static final String INTEGER_DATE_FORMAT = "yyyyMMdd";
    public static final String INTEGER_ONLY_HOUR_FORMAT = "HHmmss";

    public static final String INTEGER_HOUR_FORMAT = "yyyyMMddHH";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_MONTH_DATE = "MM-dd";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_TIME_FORMAT_MS = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String DEFAULT_TIME_FORMAT_MS_1 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String NUMBER_TIME_FORMAT = "yyyyMMddHHmmss";

    public static final String NUMBER_TIME_FORMAT_MS = "yyyyMMddHHmmssSSS";
    public static final String DEFAULT_HOUR_FORMAT = "yyyy-MM-dd HH";
    public static final String DEFAULT_SECOND_FORMAT = "HH:mm:ss";
    public static final String ACTION_CLOCK_RESTART = "ACTION_CLOCK_RESTART";
    public static final String DEFAULT_HOUR_FULL_FORMAT = "HHmmss";

    /**
     * 字符串转换成日期 如果转换格式为空，则利用默认格式进行转换操作
     *
     * @param str    字符串
     * @param format 日期格式
     * @return 日期
     */
    public static Date str2Date(String str, String format) {
        if (null == str || "".equals(str)) {
            return new Date();
        }
        // 如果没有指定字符串转换的格式，则用默认格式进行转换
        if (null == format || "".equals(format)) {
            format = DEFAULT_TIME_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * Created by jambestwick@126.com
     * on 2018/3/22
     * 将日期转换成字符串
     *
     * @param date    转换日期
     * @param formart 转换格式
     * @return
     */
    public static String date2Str(Date date, String formart) {
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        return sdf.format(date);
    }

    public static String long2Str(long millTime, String formart) {
        return date2Str(new Date(millTime), formart);
    }

    public static String now_mill() {
        return long2Str(System.currentTimeMillis(), TimeUtil.DEFAULT_TIME_FORMAT_MS);
    }

    public static String now_day() {
        return date2Str(new Date(), TimeUtil.DEFAULT_HOUR_FORMAT);
    }
    public static String now_second(){
        return date2Str(new Date(), TimeUtil.NUMBER_TIME_FORMAT);
    }

    public static String monthDay(Date date) {
        return date2Str(date, TimeUtil.DEFAULT_MONTH_DATE);
    }


    public static void startClock(Context context, int hour, int requestCode) {
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mFifteenIntent = new Intent(ACTION_CLOCK_RESET);
        mFifteenIntent.putExtra("time", hour);
        PendingIntent p = PendingIntent.getBroadcast(context, requestCode, mFifteenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        /**如果超过今天的{$hour}点，那么定时器就设置为明天hour点*/
        if (systemTime > selectTime) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        String selectStr = date2Str(new Date(calendar.getTimeInMillis()), DEFAULT_TIME_FORMAT);
        LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr clock : " + selectStr);
        long clockTime = SystemClock.elapsedRealtime();
        LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr clock : " + clockTime + ",set clock" + TimeUtil.long2Str(calendar.getTimeInMillis(), TimeUtil.DEFAULT_TIME_FORMAT_MS) + ",current:" + TimeUtil.long2Str(systemTime, TimeUtil.DEFAULT_TIME_FORMAT_MS));
        /**RTC_SHUTDOWN_WAKEUP 使用标识，系统进入深度休眠还唤醒*/
        mg.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);
    }

    /**
     * @param time  开关时间
     * @param onOff 开("${true}")/关("${onOff}")
     */
    public static boolean startClock(Context context, String time, boolean onOff, int requestCode) {
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mFifteenIntent = new Intent(ACTION_DAY_NIGHT_SET);
        String[] timeSpr = time.split(":");
        mFifteenIntent.putExtra("time", time);
        mFifteenIntent.putExtra("onOff", onOff);
        PendingIntent p = PendingIntent.getBroadcast(context, requestCode, mFifteenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSpr[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeSpr[1]));
        calendar.set(Calendar.SECOND, Integer.parseInt(timeSpr[2]));
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        /**如果超过今天的{$hour}点，那么定时器就设置为明天hour点*/
        if (systemTime > selectTime) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
            LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr clock : " + TimeUtil.long2Str(selectTime, TimeUtil.DEFAULT_TIME_FORMAT_MS_1) + ",currentTime:" + TimeUtil.long2Str(systemTime, TimeUtil.DEFAULT_TIME_FORMAT_MS_1));
        }
        String selectStr = date2Str(new Date(calendar.getTimeInMillis()), DEFAULT_TIME_FORMAT);
        LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr clock : " + selectStr);
        long clockTime = SystemClock.elapsedRealtime();
        LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr clock : " + clockTime + ",set clock" + TimeUtil.long2Str(calendar.getTimeInMillis(), TimeUtil.DEFAULT_TIME_FORMAT_MS) + ",current:" + TimeUtil.long2Str(systemTime, TimeUtil.DEFAULT_TIME_FORMAT_MS));
        /**RTC_SHUTDOWN_WAKEUP 使用标识，系统进入深度休眠还唤醒*/
        mg.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);
        return true;
    }


    public static boolean passParamsMillTime(long lastMillTime, long paramsPassMill) {
        return System.currentTimeMillis() - lastMillTime >= paramsPassMill;
    }

    public static Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, +1); //今天的时间加一天
        date = calendar.getTime();
        return date;
    }

    public static Date getPreDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1); //获取前一天日期
        date = calendar.getTime();
        return date;
    }

    public static Date getStartTime(Date date) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static String hour12to24(String hh_mm_ss) {
        String hms[] = hh_mm_ss.split(":");
        hms[0] = (Integer.parseInt(hms[0]) + 12) + "";
        String s = "";
        for (int i = 0; i < hms.length; i++) {
            if (i == hms.length - 1)
                s += hms[i];
            else {
                s += hms[i] + ":";
            }
        }
        return s;
    }

    /**
     * 获取两个日期之间的日期
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期集合
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        //tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    public static Date getBeforeDay(Date plusDate, Long cycleDate) {
        long time = plusDate.getTime(); // 得到指定日期的毫秒数
        cycleDate = cycleDate * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time -= cycleDate; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }


    //生成设备界面日期字符串
    public static String getDate() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(day);
    }

    //生成日期字符串
    public static String getDateString() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(day).replace(" ", "-").replace(":", "-");
    }
    public static String getDateStr() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(day);
    }

    //生成日期字符串
    public static String getDateString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date).replace(" ", "-").replace(":", "-");
    }

    //生成日期字符串
    public static String getDateString(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return df.format(new Date(time)).replace("-", "").replace(":", "").replace(" ", "");
    }

}
