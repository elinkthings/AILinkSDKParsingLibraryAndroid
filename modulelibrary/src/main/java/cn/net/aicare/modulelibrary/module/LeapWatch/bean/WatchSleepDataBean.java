package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class WatchSleepDataBean implements Comparable<WatchSleepDataBean> {

    /**
     * 深睡
     */
    public final static int SLEEP_TYPE_DEEP = 0x01;
    /**
     * 浅睡
     */
    public final static int SLEEP_TYPE_LIGHT = 0x02;
    /**
     * 清醒
     */
    public final static int SLEEP_TYPE_WIDE_AWAKE = 0x03;
    /**
     * 进入睡眠
     */
    public final static int SLEEP_TYPE_START = 0x04;
    /**
     * 退出睡眠
     */
    public final static int SLEEP_TYPE_END = 0x05;

    /**
     * yyyy-mm-dd
     */
    private String mDayTime;

    private long mTimestamp;
    /**
     * 0x01：深睡 {@link WatchSleepDataBean#SLEEP_TYPE_DEEP}
     * 0x02：浅睡 {@link WatchSleepDataBean#SLEEP_TYPE_LIGHT}
     * 0x03：清醒  {@link WatchSleepDataBean#SLEEP_TYPE_WIDE_AWAKE}
     */
    private int mStatus;

    /**
     * 当前状态持续时长(S)
     */
    private int mDurationTime;


    public WatchSleepDataBean(long timestamp, int status) {
        setTimestamp(timestamp);
        mTimestamp = timestamp;
        mStatus = status;
    }

    public WatchSleepDataBean(long timestamp, int status, int durationTime) {
        setTimestamp(timestamp);
        mStatus = status;
        mDurationTime = durationTime;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + "-"+ "MM" + "-" + "dd", Locale.US);
        mDayTime=format.format(timestamp);
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }


    public int getDurationTime() {
        return mDurationTime;
    }

    public void setDurationTime(int durationTime) {
        mDurationTime = durationTime;
    }


    @Override
    public int compareTo(WatchSleepDataBean o) {
        return (int) (this.mTimestamp-o.getTimestamp());
    }

    @Override
    public String toString() {
        return "WatchSleepDataBean{" + "mTimestamp=" + mTimestamp + ", mStatus=" + mStatus + ", mStatusTime=" + mDurationTime + '}';
    }
}
