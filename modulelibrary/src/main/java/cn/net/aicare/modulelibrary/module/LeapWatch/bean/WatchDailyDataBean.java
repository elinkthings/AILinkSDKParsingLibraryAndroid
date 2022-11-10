package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

public class WatchDailyDataBean implements Comparable<WatchDailyDataBean>{
    /**
     * 精确到秒
     */
    private long mStamp;
    private int mStep;// 步数
    private int mDistance;// 距离 1/10000m ？
    private int mCal;// 卡路里 1/10000kcal ？

    public WatchDailyDataBean(long stamp, int step, int distance, int cal) {
        mStamp = stamp;
        mStep = step;
        mDistance = distance;
        mCal = cal;
    }

    public long getStamp() {
        return mStamp;
    }

    public void setStamp(long stamp) {
        mStamp = stamp;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        mDistance = distance;
    }

    public int getCal() {
        return mCal;
    }

    public void setCal(int cal) {
        mCal = cal;
    }


    @Override
    public int compareTo(WatchDailyDataBean dailyDataBean) {
        return (int) (getStamp()-dailyDataBean.getStamp());
    }
}
