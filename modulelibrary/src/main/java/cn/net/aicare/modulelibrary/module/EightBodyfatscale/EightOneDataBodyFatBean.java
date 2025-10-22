package cn.net.aicare.modulelibrary.module.EightBodyfatscale;

public class EightOneDataBodyFatBean {

    /**
     * 体重公斤
     */
    private float weightKg;     //体重，
    /**
     * 身体质量指数
     */
    private float bmi;     //体质指数，
    /**
     * 体脂率
     */
    private float bfr;     //体脂率，
    /**
     * 皮下脂肪率
     */
    private float sfr;     //皮下脂肪率，
    /**
     * 内脏脂肪率
     */
    private float uvi;     //内脏脂肪率，
    /**
     * 肌肉率
     */
    private float rom;     //肌肉率，
    /**
     * 基础代谢率
     */
    private float bmr;     //基础代谢率，
    /**
     * 骨量
     */
    private String bm;     //骨骼质量，
    /**
     * 水分
     */
    private float vwc;     //水含量，
    /**
     * 身体年龄
     */
    private int bodyAge;     //身体年龄，
    /**
     * 蛋白率
     */
    private float pp;     //蛋白率，

    /**
     * 阻抗左手
     */
    private double adcLeftHand; //左手阻抗

    /**
     * 阻抗右手
     */
    private double adcRightHand; //右手阻抗
    /**
     * 阻抗左脚
     */
    private double adcLeftFoot;  //左脚阻抗
    /**
     * 阻抗右脚
     */
    private double adcRightFoot;
    /**
     * 阻抗左身体
     */
    private double adcLeftBody;

    /**
     * 算法ID
     */
    private int arithmetic;

    /**
     * 体脂-右上
     */
    private String fatMassRightTop;     //体脂-右上
    /**
     * 体脂-右下
     */
    private String fatMassRightBottom;     //体脂-右下
    /**
     * 体脂-左上
     */
    private String fatMassLeftTop;     //体脂-左上
    /**
     * 体脂-左下
     */
    private String fatMassLeftBottom;     //体脂-左下
    /**
     * 体脂-躯干
     */
    private String fatMassBody;     //体脂-躯干
    /**
     * 肌肉-右上
     */
    private String muscleMassRightTop;     //肌肉-右上
    /**
     * 肌肉-右下
     */
    private String muscleMassRightBottom;     //肌肉-右下
    /**
     * 肌肉-左上
     */
    private String muscleMassLeftTop;     //肌肉-左上
    /**
     * 肌肉-左下
     */
    private String muscleMassLeftBottom;     //肌肉-左下
    /**
     * 肌肉-躯干
     */
    private String muscleMassBody;     //肌肉-躯干

    /**
     * 骨骼肌公斤
     */
    private String bhSkeletalMuscleKg;

    /**
     * 理想体重kg
     */
    private float idealWeightKg;
    /**
     * 去脂体重-千克
     */
    private float bodyFatFreeMassKg;

    /**
     * 肌肉公斤
     */
    private float muscleKg;


    public void setBhSkeletalMuscleKg(String bhSkeletalMuscleKg) {
        this.bhSkeletalMuscleKg = bhSkeletalMuscleKg;
    }


    public String getBhSkeletalMuscleKg() {
        return bhSkeletalMuscleKg;
    }


    public float getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(float weightKg) {
        this.weightKg = weightKg;
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

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public float getVwc() {
        return vwc;
    }

    public void setVwc(float vwc) {
        this.vwc = vwc;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public double getAdcLeftHand() {
        return adcLeftHand;
    }

    public void setAdcLeftHand(double adcLeftHand) {
        this.adcLeftHand = adcLeftHand;
    }

    public double getAdcRightHand() {
        return adcRightHand;
    }

    public void setAdcRightHand(double adcRightHand) {
        this.adcRightHand = adcRightHand;
    }

    public double getAdcLeftFoot() {
        return adcLeftFoot;
    }

    public void setAdcLeftFoot(double adcLeftFoot) {
        this.adcLeftFoot = adcLeftFoot;
    }

    public double getAdcRightFoot() {
        return adcRightFoot;
    }

    public void setAdcRightFoot(double adcRightFoot) {
        this.adcRightFoot = adcRightFoot;
    }

    public double getAdcLeftBody() {
        return adcLeftBody;
    }

    public void setAdcLeftBody(double adcLeftBody) {
        this.adcLeftBody = adcLeftBody;
    }

    public String getFatMassRightTop() {
        return fatMassRightTop;
    }

    public void setFatMassRightTop(String fatMassRightTop) {
        this.fatMassRightTop = fatMassRightTop;
    }

    public String getFatMassRightBottom() {
        return fatMassRightBottom;
    }

    public void setFatMassRightBottom(String fatMassRightBottom) {
        this.fatMassRightBottom = fatMassRightBottom;
    }

    public String getFatMassLeftTop() {
        return fatMassLeftTop;
    }

    public void setFatMassLeftTop(String fatMassLeftTop) {
        this.fatMassLeftTop = fatMassLeftTop;
    }

    public String getFatMassLeftBottom() {
        return fatMassLeftBottom;
    }

    public void setFatMassLeftBottom(String fatMassLeftBottom) {
        this.fatMassLeftBottom = fatMassLeftBottom;
    }

    public String getFatMassBody() {
        return fatMassBody;
    }

    public void setFatMassBody(String fatMassBody) {
        this.fatMassBody = fatMassBody;
    }

    public String getMuscleMassRightTop() {
        return muscleMassRightTop;
    }

    public void setMuscleMassRightTop(String muscleMassRightTop) {
        this.muscleMassRightTop = muscleMassRightTop;
    }

    public String getMuscleMassRightBottom() {
        return muscleMassRightBottom;
    }

    public void setMuscleMassRightBottom(String muscleMassRightBottom) {
        this.muscleMassRightBottom = muscleMassRightBottom;
    }

    public String getMuscleMassLeftTop() {
        return muscleMassLeftTop;
    }

    public void setMuscleMassLeftTop(String muscleMassLeftTop) {
        this.muscleMassLeftTop = muscleMassLeftTop;
    }

    public String getMuscleMassLeftBottom() {
        return muscleMassLeftBottom;
    }

    public void setMuscleMassLeftBottom(String muscleMassLeftBottom) {
        this.muscleMassLeftBottom = muscleMassLeftBottom;
    }

    public String getMuscleMassBody() {
        return muscleMassBody;
    }

    public void setMuscleMassBody(String muscleMassBody) {
        this.muscleMassBody = muscleMassBody;
    }

    public int getArithmetic() {
        return arithmetic;
    }

    public void setArithmetic(int arithmetic) {
        this.arithmetic = arithmetic;
    }

    public float getIdealWeightKg() {
        return idealWeightKg;
    }

    public void setIdealWeightKg(float idealWeightKg) {
        this.idealWeightKg = idealWeightKg;
    }

    public float getBodyFatFreeMassKg() {
        return bodyFatFreeMassKg;
    }

    public void setBodyFatFreeMassKg(float bodyFatFreeMassKg) {
        this.bodyFatFreeMassKg = bodyFatFreeMassKg;
    }

    public float getMuscleKg() {
        return muscleKg;
    }

    public void setMuscleKg(float muscleKg) {
        this.muscleKg = muscleKg;
    }

    @Override
    public String toString() {
        return "EightBodyFatBean{" +
                "体重(kg)='" + weightKg + '\'' +
                ", bmi=" + bmi +
                ", 体脂率=" + bfr +
                ", 皮下脂肪率=" + sfr +
                ", 内脏脂肪率=" + uvi +
                ", 肌肉率=" + rom +
                ", 基础代谢率=" + bmr +
                ", 骨量='" + bm + '\'' +
                ", 水分" + vwc +
                ", 身体年龄=" + bodyAge +
                ", 蛋白率=" + pp +
                ", 阻抗左手=" + adcLeftHand +
                ", 阻抗右手=" + adcRightHand +
                ", 阻抗左脚=" + adcLeftFoot +
                ", 阻抗右脚=" + adcRightFoot +
                ", 阻抗左身体=" + adcLeftBody +
                ", 算法ID=" + arithmetic +
                ", 体脂-右上='" + fatMassRightTop + '\'' +
                ", 体脂-右下='" + fatMassRightBottom + '\'' +
                ", 体脂-左上='" + fatMassLeftTop + '\'' +
                ", 体脂-左下='" + fatMassLeftBottom + '\'' +
                ", 体脂-躯干='" + fatMassBody + '\'' +
                ", 肌肉-右上='" + muscleMassRightTop + '\'' +
                ", 肌肉-右下='" + muscleMassRightBottom + '\'' +
                ", 肌肉-左上='" + muscleMassLeftTop + '\'' +
                ", 肌肉量-左下='" + muscleMassLeftBottom + '\'' +
                ", 肌肉量-躯干='" + muscleMassBody + '\'' +
                ", 骨骼肌='" + bhSkeletalMuscleKg + '\'' +
                ", 标准体重='" + idealWeightKg + '\'' +
                ", 去脂体重='" + bodyFatFreeMassKg + '\'' +
                ", 肌肉量='" + muscleKg + '\'' +
                '}';
    }

}
