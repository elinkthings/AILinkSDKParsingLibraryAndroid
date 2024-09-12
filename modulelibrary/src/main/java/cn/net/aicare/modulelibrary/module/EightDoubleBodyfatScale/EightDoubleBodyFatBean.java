package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

/**
 * 双频八电极体脂秤体脂数据
 *
 * @author xing
 * @date 2024/06/11
 */
public class EightDoubleBodyFatBean {

    /**
     * 是否需要补全数据
     */
    private boolean addData;

    /**
     * 身体质量指数,精度 0.1
     */
    private double bmi;

    /**
     * 全身体脂率,精度 0.1%
     */
    private double bodyBfr;

    /**
     * 全身肌肉率,精度 0.1%
     */
    private double bodyRom;

    /**
     * 左上肢脂肪量,精度 0.1kg
     */
    private double leftUpperFatMass;

    /**
     * 右上肢脂肪量,精度 0.1kg
     */
    private double rightUpperFatMass;

    /**
     * 躯干脂肪量,精度 0.1kg
     */
    private double bodyFatMass;

    /**
     * 左下肢脂肪量,精度 0.1kg
     */
    private double leftLowerFatMass;

    /**
     * 右下肢脂肪量,精度 0.1kg
     */
    private double rightLowerFatMass;

    /**
     * 左上肢肌肉量,精度 0.1kg
     */
    private double leftUpperMuscleMass;

    /**
     * 右上肢肌肉量,精度 0.1kg
     */
    private double rightUpperMuscleMass;


    /**
     * 躯干肌肉量,精度 0.1kg
     */
    private double bodyMuscleMass;

    /**
     * 左下肢肌肉量,精度 0.1kg
     */
    private double leftLowerMuscleMass;

    /**
     * 右下肢肌肉量,精度 0.1kg
     */
    private double rightLowerMuscleMass;

    /**
     * 身体水分,精度 0.1%
     */
    private double bodyWater;


    /**
     * 骨重量,精度 0.1kg
     */
    private double bm;


    /**
     * 基础代谢率,精度 1kcal
     */
    private int bmr;

    /**
     * 蛋白率,精度 0.1%
     */
    private double pp;

    /**
     * 内脏脂肪指数,精度 1
     */
    private int uvi;

    /**
     * 皮下脂肪率,精度 0.1%
     */
    private double sfr;

    /**
     * 身高,单位 cm
     */
    private int height;

    /**
     * 身体年龄,单位 1岁
     */
    private int bodyAge;

    public EightDoubleBodyFatBean() {
    }

    public boolean isAddData() {
        return addData;
    }

    public void setAddData(boolean addData) {
        this.addData = addData;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBodyBfr() {
        return bodyBfr;
    }

    public void setBodyBfr(double bodyBfr) {
        this.bodyBfr = bodyBfr;
    }

    public double getBodyRom() {
        return bodyRom;
    }

    public void setBodyRom(double bodyRom) {
        this.bodyRom = bodyRom;
    }

    public double getLeftUpperFatMass() {
        return leftUpperFatMass;
    }

    public void setLeftUpperFatMass(double leftUpperFatMass) {
        this.leftUpperFatMass = leftUpperFatMass;
    }

    public double getRightUpperFatMass() {
        return rightUpperFatMass;
    }

    public void setRightUpperFatMass(double rightUpperFatMass) {
        this.rightUpperFatMass = rightUpperFatMass;
    }

    public double getBodyFatMass() {
        return bodyFatMass;
    }

    public void setBodyFatMass(double bodyFatMass) {
        this.bodyFatMass = bodyFatMass;
    }

    public double getLeftLowerFatMass() {
        return leftLowerFatMass;
    }

    public void setLeftLowerFatMass(double leftLowerFatMass) {
        this.leftLowerFatMass = leftLowerFatMass;
    }

    public double getRightLowerFatMass() {
        return rightLowerFatMass;
    }

    public void setRightLowerFatMass(double rightLowerFatMass) {
        this.rightLowerFatMass = rightLowerFatMass;
    }

    public double getLeftUpperMuscleMass() {
        return leftUpperMuscleMass;
    }

    public void setLeftUpperMuscleMass(double leftUpperMuscleMass) {
        this.leftUpperMuscleMass = leftUpperMuscleMass;
    }

    public double getRightUpperMuscleMass() {
        return rightUpperMuscleMass;
    }

    public void setRightUpperMuscleMass(double rightUpperMuscleMass) {
        this.rightUpperMuscleMass = rightUpperMuscleMass;
    }

    public double getBodyMuscleMass() {
        return bodyMuscleMass;
    }

    public void setBodyMuscleMass(double bodyMuscleMass) {
        this.bodyMuscleMass = bodyMuscleMass;
    }

    public double getLeftLowerMuscleMass() {
        return leftLowerMuscleMass;
    }

    public void setLeftLowerMuscleMass(double leftLowerMuscleMass) {
        this.leftLowerMuscleMass = leftLowerMuscleMass;
    }

    public double getRightLowerMuscleMass() {
        return rightLowerMuscleMass;
    }

    public void setRightLowerMuscleMass(double rightLowerMuscleMass) {
        this.rightLowerMuscleMass = rightLowerMuscleMass;
    }

    public double getBodyWater() {
        return bodyWater;
    }

    public void setBodyWater(double bodyWater) {
        this.bodyWater = bodyWater;
    }

    public double getBm() {
        return bm;
    }

    public void setBm(double bm) {
        this.bm = bm;
    }

    public int getBmr() {
        return bmr;
    }

    public void setBmr(int bmr) {
        this.bmr = bmr;
    }

    public double getPp() {
        return pp;
    }

    public void setPp(double pp) {
        this.pp = pp;
    }

    public int getUvi() {
        return uvi;
    }

    public void setUvi(int uvi) {
        this.uvi = uvi;
    }

    public double getSfr() {
        return sfr;
    }

    public void setSfr(double sfr) {
        this.sfr = sfr;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }

    /**
     * 复制
     *
     * @return {@link EightDoubleBodyFatBean}
     */
    public EightDoubleBodyFatBean copy() {
        EightDoubleBodyFatBean bean = new EightDoubleBodyFatBean();
        bean.setAddData(this.addData);
        bean.setBmi(this.bmi);
        bean.setBodyBfr(this.bodyBfr);
        bean.setBodyRom(this.bodyRom);
        bean.setLeftUpperFatMass(this.leftUpperFatMass);
        bean.setRightUpperFatMass(this.rightUpperFatMass);
        bean.setBodyFatMass(this.bodyFatMass);
        bean.setLeftLowerFatMass(this.leftLowerFatMass);
        bean.setRightLowerFatMass(this.rightLowerFatMass);
        bean.setLeftUpperMuscleMass(this.leftUpperMuscleMass);
        bean.setRightUpperMuscleMass(this.rightUpperMuscleMass);
        bean.setBodyMuscleMass(this.bodyMuscleMass);
        bean.setLeftLowerMuscleMass(this.leftLowerMuscleMass);
        bean.setRightLowerMuscleMass(this.rightLowerMuscleMass);
        bean.setBodyWater(this.bodyWater);
        bean.setBm(this.bm);
        bean.setBmr(this.bmr);
        bean.setPp(this.pp);
        bean.setUvi(this.uvi);
        bean.setSfr(this.sfr);
        bean.setHeight(this.height);
        bean.setBodyAge(this.bodyAge);
        return bean;
    }


    @Override
    public String toString() {
        return "EightDoubleBodyFatBean{" +
                "addData=" + addData +
                "bmi=" + bmi +
                ", bodyBfr=" + bodyBfr +
                ", bodyRom=" + bodyRom +
                ", leftUpperFatMass=" + leftUpperFatMass +
                ", rightUpperFatMass=" + rightUpperFatMass +
                ", bodyFatMass=" + bodyFatMass +
                ", leftLowerFatMass=" + leftLowerFatMass +
                ", rightLowerFatMass=" + rightLowerFatMass +
                ", leftUpperMuscleMass=" + leftUpperMuscleMass +
                ", rightUpperMuscleMass=" + rightUpperMuscleMass +
                ", bodyMuscleMass=" + bodyMuscleMass +
                ", leftLowerMuscleMass=" + leftLowerMuscleMass +
                ", rightLowerMuscleMass=" + rightLowerMuscleMass +
                ", bodyWater=" + bodyWater +
                ", bm=" + bm +
                ", bmr=" + bmr +
                ", pp=" + pp +
                ", uvi=" + uvi +
                ", sfr=" + sfr +
                ", height=" + height +
                ", bodyAge=" + bodyAge +
                '}';
    }
}
