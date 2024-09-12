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
     * @return
     */
    public static int getCurTimeZone() {
        TimeZone aDefault = TimeZone.getDefault();
        return (aDefault.getRawOffset() / 3600000);
    }
}
