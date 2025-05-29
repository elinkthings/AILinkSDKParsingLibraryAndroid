package cn.net.aicare.modulelibrary.module.bw05;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author ljl
 * on 2024/6/5
 */
public class Bw05WatchUtils {

    /**
     * 获取UTC 0 时区时间
     *
     * @return
     */
    public static String getUTCTimeStr() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String format = simpleDateFormat.format(calendar.getTime());
        return format;
    }

    /**
     * 获取时区
     *
     * @return
     */
    public static int getCurTimeZone() {
        TimeZone aDefault = TimeZone.getDefault();
        return (aDefault.getRawOffset() / 3600000);
    }

    /**
     * 获取符合要求的时区格式
     *
     * @return
     */
    public static String getCurTimeZoneStr() {
        TimeZone aDefault = TimeZone.getDefault();
        int order = aDefault.getRawOffset() / 3600000;
        if (Math.abs(order) < 10) {
            //小于10
            if (order < 0) {
                return "10" + Math.abs(order);
            } else {
                return "00" + order;
            }
        } else {
            //大于10
            if (order < 0) {
                return "1" + Math.abs(order);
            } else {
                return "0" + order;
            }
        }
    }
}
