package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

public class WatchSportDataBean {
    private long mStamp;
    private int mWorkStatus;// 0：开始；1：运动中；2：暂停；3：继续；4：结束
    private int mWorkType;// 1：跑步；2：跑步机；3：室外跑步；4：健走；5：爬山
    private int mStep;// 步数
    private int mCal;// 卡路里 1/10000kcal
    private int mDistance;// 距离 1/10000m
    private int mHeartRateAll;// 总心率
    private int mSportsTime;// 运动时长(s)


    public WatchSportDataBean(long stamp, int workStatus, int workType, int step, int cal, int distance, int heartRateAll, int sportsTime) {
        mStamp = stamp;
        mWorkStatus = workStatus;
        mWorkType = workType;
        mStep = step;
        mCal = cal;
        mDistance = distance;
        mHeartRateAll = heartRateAll;
        mSportsTime = sportsTime;
    }

    public WatchSportDataBean(long stamp, int workStatus, int workType, int step, int cal, int distance) {
        mStamp = stamp;
        mWorkStatus = workStatus;
        mWorkType = workType;
        mStep = step;
        mCal = cal;
        mDistance = distance;
    }

    public long getStamp() {
        return mStamp;
    }

    public void setStamp(long stamp) {
        mStamp = stamp;
    }

    public int getWorkStatus() {
        return mWorkStatus;
    }

    public void setWorkStatus(int workStatus) {
        mWorkStatus = workStatus;
    }

    public int getWorkType() {
        return mWorkType;
    }

    public void setWorkType(int workType) {
        mWorkType = workType;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public int getCal() {
        return mCal;
    }

    public void setCal(int cal) {
        mCal = cal;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        mDistance = distance;
    }


    public int getHeartRateAll() {
        return mHeartRateAll;
    }

    public void setHeartRateAll(int heartRateAll) {
        mHeartRateAll = heartRateAll;
    }

    public int getSportsTime() {
        return mSportsTime;
    }

    public void setSportsTime(int sportsTime) {
        mSportsTime = sportsTime;
    }

    @Override
    public String toString() {
        return "WatchSportDataBean{" + "mStamp=" + mStamp + ", mWorkStatus=" + mWorkStatus + ", mWorkType=" + mWorkType + ", mStep=" + mStep + ", mCal=" + mCal + ", mDistance=" + mDistance + ", " +
                "mHeartRateAll=" + mHeartRateAll + ", mSportsTime=" + mSportsTime + '}';
    }
}
