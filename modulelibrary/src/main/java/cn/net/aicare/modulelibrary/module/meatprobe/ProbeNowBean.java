package cn.net.aicare.modulelibrary.module.meatprobe;

import java.io.Serializable;

/**
 * 探针实时对象Bean
 *
 *
 * @author xing
 * @date 2023/05/11
 */
public class ProbeNowBean implements Serializable {


    /**
     * 探针编号
     */
    private int mId = 0x01;

    /**
     * 实时温度单位 0->℃ 1->℉
     */
    private int mRealTimeUnit;
    /**
     * 实时温度正负值 0->温度为正 1->温度为负
     */
    private int mRealTimePositive;
    /**
     * 实时温度值
     */
    private int mRealTimeTemp = 65535;
    /**
     * 环境温度单位 0->℃ 1->℉
     */
    private int mAmbientUnit;
    /**
     * 环境温度正负值 0->温度为正 1->温度为负
     */
    private int mAmbientPositive;
    /**
     * 环境温度值 若无 则为0xFFFF
     */
    private int mAmbientTemp = 65535;
    /**
     * 目标温度单位 0->℃ 1->℉
     */
    private int mTargetUnit;
    /**
     * 目标温度正负值 0->温度为正 1->温度为负
     */
    private int mTargetPositive;
    /**
     * 目标温度值 若无 则为0xFFFF
     */
    private int mTargetTemp;
    /**
     * 探针状态 0->未插入 1->已插入 3->设备无该功能
     */
    private int mProbeState;

    /**
     * 创建时间
     */
    private long mCreationTime;

    /**
     * 充电状态
     */
    private int chargerState;

    /**
     * 探针电量
     */
    private int probeBattery;


    public ProbeNowBean() {
    }

    public ProbeNowBean(int id, int realTimeUnit, int realTimePositive, int realTimeTemp, int ambientUnit, int ambientPositive, int ambientTemp, int targetUnit, int targetPositive, int targetTemp,
                        int probeState) {
        mId = id;
        mRealTimeUnit = realTimeUnit;
        mRealTimePositive = realTimePositive;
        mRealTimeTemp = realTimeTemp;
        mAmbientUnit = ambientUnit;
        mAmbientPositive = ambientPositive;
        mAmbientTemp = ambientTemp;
        mTargetUnit = targetUnit;
        mTargetPositive = targetPositive;
        mTargetTemp = targetTemp;
        mProbeState = probeState;
    }

    public ProbeNowBean(int id, int realTimeUnit, int realTimePositive, int realTimeTemp, int ambientUnit, int ambientPositive,
                        int ambientTemp, int targetUnit, int targetPositive, int targetTemp, int probeState, long creationTime,
                        int chargerState, int probeBattery) {
        mId = id;
        mRealTimeUnit = realTimeUnit;
        mRealTimePositive = realTimePositive;
        mRealTimeTemp = realTimeTemp;
        mAmbientUnit = ambientUnit;
        mAmbientPositive = ambientPositive;
        mAmbientTemp = ambientTemp;
        mTargetUnit = targetUnit;
        mTargetPositive = targetPositive;
        mTargetTemp = targetTemp;
        mProbeState = probeState;
        mCreationTime = creationTime;
        this.chargerState = chargerState;
        this.probeBattery = probeBattery;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getRealTimeUnit() {
        return mRealTimeUnit;
    }

    public void setRealTimeUnit(int realTimeUnit) {
        mRealTimeUnit = realTimeUnit;
    }

    public int getRealTimePositive() {
        return mRealTimePositive;
    }

    public void setRealTimePositive(int realTimePositive) {
        mRealTimePositive = realTimePositive;
    }

    public int getRealTimeTemp() {
        return mRealTimeTemp;
    }

    public void setRealTimeTemp(int realTimeTemp) {
        mRealTimeTemp = realTimeTemp;
    }

    public int getAmbientUnit() {
        return mAmbientUnit;
    }

    public void setAmbientUnit(int ambientUnit) {
        mAmbientUnit = ambientUnit;
    }

    public int getAmbientPositive() {
        return mAmbientPositive;
    }

    public void setAmbientPositive(int ambientPositive) {
        mAmbientPositive = ambientPositive;
    }

    public int getAmbientTemp() {
        return mAmbientTemp;
    }

    public void setAmbientTemp(int ambientTemp) {
        mAmbientTemp = ambientTemp;
    }

    public int getTargetUnit() {
        return mTargetUnit;
    }

    public void setTargetUnit(int targetUnit) {
        mTargetUnit = targetUnit;
    }

    public int getTargetPositive() {
        return mTargetPositive;
    }

    public void setTargetPositive(int targetPositive) {
        mTargetPositive = targetPositive;
    }

    public int getTargetTemp() {
        return mTargetTemp;
    }

    public void setTargetTemp(int targetTemp) {
        mTargetTemp = targetTemp;
    }

    public int getProbeState() {
        return mProbeState;
    }

    public void setProbeState(int probeState) {
        mProbeState = probeState;
    }

    public long getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(long creationTime) {
        mCreationTime = creationTime;
    }

    public int getChargerState() {
        return chargerState;
    }

    public void setChargerState(int chargerState) {
        this.chargerState = chargerState;
    }

    public int getProbeBattery() {
        return probeBattery;
    }

    public void setProbeBattery(int probeBattery) {
        this.probeBattery = probeBattery;
    }

    @Override
    public String toString() {
        return "ProbeNowBean{" +
                "mId=" + mId +
                ", mRealTimeUnit=" + mRealTimeUnit +
                ", mRealTimePositive=" + mRealTimePositive +
                ", mRealTimeTemp=" + mRealTimeTemp +
                ", mAmbientUnit=" + mAmbientUnit +
                ", mAmbientPositive=" + mAmbientPositive +
                ", mAmbientTemp=" + mAmbientTemp +
                ", mTargetUnit=" + mTargetUnit +
                ", mTargetPositive=" + mTargetPositive +
                ", mTargetTemp=" + mTargetTemp +
                ", mProbeState=" + mProbeState +
                '}';
    }
}
