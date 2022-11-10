package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

public class WatchBloodOxygenDataBean {
    private long mStamp;
    private int mBloodOxygen;

    public WatchBloodOxygenDataBean(long stamp, int bloodOxygen) {
        mStamp = stamp;
        mBloodOxygen = bloodOxygen;
    }

    public long getStamp() {
        return mStamp;
    }

    public void setStamp(long stamp) {
        mStamp = stamp;
    }

    public int getBloodOxygen() {
        return mBloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        mBloodOxygen = bloodOxygen;
    }
}
