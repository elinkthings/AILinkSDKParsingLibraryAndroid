package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

public class WatchHeartDataBean {
    /**
     * 时间戳(s)
     */
    private long mTimestamp;
    /**
     * 次数
     */
    private int mCount;

    public WatchHeartDataBean(long timestamp, int count) {
        mTimestamp = timestamp;
        mCount = count;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }
}
