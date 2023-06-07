package cn.net.aicare.modulelibrary.module.HeightWeightScale;

/**
 * 身高体脂称用户
 *
 * @author xing
 * @date 2023/05/08
 */
public class HeightBodyFatUser {

    /**
     * 性别,0=女;1=男
     */
    private int mSex;

    /**
     * 年龄
     */
    private int mAge;

    /**
     * 高度
     */
    private int mHeight;

    /**
     * 重量
     */
    private int mWeight;

    /**
     * 小数
     */
    private int mDecimals;

    private int mUnit;

    /**
     * 阻抗
     */
    private int mAdc;


    /**
     * 身高体脂称用户
     *
     * @param sex    性
     * @param age    年龄
     * @param height 高度
     * @param weight 重量
     * @param adc    adc
     */
    public HeightBodyFatUser(int sex, int age, int height, int weight, int adc) {
        mSex = sex;
        mAge = age;
        mHeight = height;
        mWeight = weight;
        mAdc = adc;
    }

    public HeightBodyFatUser(int sex, int age, int height, int weight, int decimals, int unit, int adc) {
        mSex = sex;
        mAge = age;
        mHeight = height;
        mWeight = weight;
        mDecimals = decimals;
        mUnit = unit;
        mAdc = adc;
    }

    public int getSex() {
        return mSex;
    }

    public void setSex(int sex) {
        mSex = sex;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public int getDecimals() {
        return mDecimals;
    }

    public void setDecimals(int decimals) {
        mDecimals = decimals;
    }

    public int getUnit() {
        return mUnit;
    }

    public void setUnit(int unit) {
        mUnit = unit;
    }

    public int getAdc() {
        return mAdc;
    }

    public void setAdc(int adc) {
        mAdc = adc;
    }

    @Override
    public String toString() {
        return "HeightBodyFatUser{" +
                "mSex=" + mSex +
                ", mAge=" + mAge +
                ", mHeight=" + mHeight +
                ", mWeight=" + mWeight +
                ", mDecimals=" + mDecimals +
                ", mUnit=" + mUnit +
                ", mAdc=" + mAdc +
                '}';
    }
}
