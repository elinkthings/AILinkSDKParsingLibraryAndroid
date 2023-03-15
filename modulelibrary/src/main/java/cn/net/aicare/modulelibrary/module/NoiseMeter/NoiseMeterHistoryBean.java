package cn.net.aicare.modulelibrary.module.NoiseMeter;

/**
 * @author xing<br>
 * @date 2022/11/15<br>
 * 噪音计历史记录bean
 */
public class NoiseMeterHistoryBean implements Comparable<NoiseMeterHistoryBean>{

    /**
     * 时间戳
     */
    private long mTime;
    /**
     * 噪音值(原始数据)
     */
    private int mValue;

    /**
     * 1=A 权. 2=C 权
     */
    private int mAc;
    /**
     * 当时是否报警
     */
    private boolean mAlarmState;
    /**
     * 1=噪音值低于量程;2=噪音值高于量程
     */
    private int mState;

    public NoiseMeterHistoryBean(long time, int value, int ac, boolean alarmState, int state) {
        mTime = time;
        mValue = value;
        mAc = ac;
        mAlarmState = alarmState;
        mState = state;
    }

    public NoiseMeterHistoryBean() {
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

    public int getAc() {
        return mAc;
    }

    public void setAc(int ac) {
        mAc = ac;
    }

    public boolean isAlarmState() {
        return mAlarmState;
    }

    public void setAlarmState(boolean alarmState) {
        mAlarmState = alarmState;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    @Override
    public int compareTo(NoiseMeterHistoryBean o) {
        return (int) (this.mTime-o.getTime());
    }


    @Override
    public String toString() {
        return "NoiseMeterHistoryBean{" + "mTime=" + mTime + ", mValue=" + mValue + ", mAc=" + mAc + ", mAlarmState=" + mAlarmState + ", mState=" + mState + '}';
    }
}
