package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

public class WatchBloodPressureDataBean {
    private long mStamp;
    private int mHigh;// 高压
    private int mLow;// 低压
    private int mUnit;// 单位
    private int mDecimal;// 小数

    public WatchBloodPressureDataBean(long stamp, int high, int low, int unit, int decimal) {
        mStamp = stamp;
        mHigh = high;
        mLow = low;
        mUnit = unit;
        mDecimal = decimal;
    }

    public long getStamp() {
        return mStamp;
    }

    public void setStamp(long stamp) {
        mStamp = stamp;
    }

    public int getHigh() {
        return mHigh;
    }

    public void setHigh(int high) {
        mHigh = high;
    }

    public int getLow() {
        return mLow;
    }

    public void setLow(int low) {
        mLow = low;
    }

    public int getUnit() {
        return mUnit;
    }

    public void setUnit(int unit) {
        mUnit = unit;
    }

    public int getDecimal() {
        return mDecimal;
    }

    public void setDecimal(int decimal) {
        mDecimal = decimal;
    }

    @Override
    public String toString() {
        return "WatchBloodPressureDataBean{" + "mStamp=" + mStamp + ", mHigh=" + mHigh + ", mLow=" + mLow + ", mUnit=" + mUnit + ", mDecimal=" + mDecimal + '}';
    }
}
