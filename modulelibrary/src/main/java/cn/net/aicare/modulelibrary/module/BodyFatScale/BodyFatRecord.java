package cn.net.aicare.modulelibrary.module.BodyFatScale;


public class BodyFatRecord {


    private float bmi;
    /**
     * 心率 Heart rate(255代表没有)
     */
    private int heartRate;

    /**
     * 体脂率 body fat rate
     */
    private float bfr;
    /**
     * 皮下脂肪率 Subcutaneous fat rate
     */
    private float sfr;
    /**
     * 内脏脂肪指数
     */
    private float uvi;
    /**
     * 肌肉率 Rate of muscle
     */
    private float rom;
    /**
     * 基础代谢率 basal metabolic rate
     */
    private float bmr;
    /**
     * 身体年龄 physical bodyAge
     */
    private float bodyAge;

    /**
     * 骨骼质量 Bone Mass kg
     */
    private float bm;
    /**
     * 水含量
     */
    private float vwc;

    /**
     * 蛋白率 protein percentage
     */
    private float pp;


    public BodyFatRecord() {
    }


    public float getBm() {
        return bm;
    }

    public void setBm(float bm) {
        this.bm = bm;
    }


    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBfr() {
        return bfr;
    }

    public void setBfr(float bfr) {
        this.bfr = bfr;
    }

    public float getSfr() {
        return sfr;
    }

    public void setSfr(float sfr) {
        this.sfr = sfr;
    }

    public float getUvi() {
        return uvi;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }

    public float getRom() {
        return rom;
    }

    public void setRom(float rom) {
        this.rom = rom;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public float getVwc() {
        return vwc;
    }

    public void setVwc(float vwc) {
        this.vwc = vwc;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public float getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(float bodyAge) {
        this.bodyAge = bodyAge;
    }


    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    @Override
    public String toString() {
        return "BodyFatRecord{" + "bmi=" + bmi + ", bfr=" + bfr + ", sfr=" + sfr + ", uvi=" + uvi + ", rom=" + rom + ", bmr=" + bmr + ", bm=" + bm + ", vwc=" + vwc + ", heartRate=" + heartRate + ", bodyAge=" + bodyAge + ", pp=" + pp + '}';
    }
}
