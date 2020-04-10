package cn.net.aicare.modulelibrary.module.ADWeight;

import java.io.Serializable;

/**
 * xing<br>
 * 2019/11/13<br>
 * 体脂数据对象
 */
public class ADWeightScaleBodyFatData implements Serializable, Cloneable {


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
    private int uvi;
    /**
     * 肌肉率 Rate of muscle
     */
    private float rom;
    /**
     * 基础代谢率 basal metabolic rate
     */
    private int bmr;
    /**
     * 身体年龄 physical bodyAge
     */
    private int bodyAge;

    /**
     * 骨骼质量 Bone Mass
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


    /**
     * 心率 Heart rate
     */
    private int hr;


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

    public int getUvi() {
        return uvi;
    }

    public void setUvi(int uvi) {
        this.uvi = uvi;
    }

    public float getRom() {
        return rom;
    }

    public void setRom(float rom) {
        this.rom = rom;
    }

    public int getBmr() {
        return bmr;
    }

    public void setBmr(int bmr) {
        this.bmr = bmr;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }

    public float getBm() {
        return bm;
    }

    public void setBm(float bm) {
        this.bm = bm;
    }

    public float getVwc() {
        return vwc;
    }

    public void setVwc(float vwc) {
        this.vwc = vwc;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    @Override
    protected ADWeightScaleBodyFatData clone() {
        try {
            return (ADWeightScaleBodyFatData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }


    @Override
    public String toString() {
        return "体脂数据{\n" + "体脂率=" + bfr + ", 皮下脂肪率=" + sfr + ", 内脏脂肪指数=" + uvi + ", " +
                "肌肉率=" + rom + ", 基础代谢率=" + bmr + ", 身体年龄=" + bodyAge + ", 骨骼质量=" + bm + "," +
                " 水含量=" + vwc + ", 蛋白率=" + pp + ", 心率=" + hr + "\n}";
    }
}
