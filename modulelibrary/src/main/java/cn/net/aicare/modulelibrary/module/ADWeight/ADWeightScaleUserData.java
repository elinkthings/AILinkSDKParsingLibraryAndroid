package cn.net.aicare.modulelibrary.module.ADWeight;

/**
 * xing<br>
 * 2019/11/13<br>
 * 体脂秤用户数据
 */
public class ADWeightScaleUserData {

    /**
     * 用户类型
     * 0：普通人
     * 1：业余运动员
     * 2：专业运动员
     * 3：孕妇
     * (0~255)
     */
    private int mUserType;

    /**
     * 性别
     * 0:女
     * 1:男
     */
    private int mSex;

    /**
     * 年龄:0~127
     */
    private int mAge;

    /**
     * 身高:0~254(cm)
     */
    private int mHeight;

    /**
     * 用户ID
     */
    private int mUserId;

    /**
     * 1档温度重量(0~255)
     */
    private int mOneClothingWeight=1;
    /**
     * 2档温度重量(0~255)
     */
    private int mTwoClothingWeight=2;
    /**
     * 3档温度重量(0~255)
     */
    private int mThreeClothingWeight=3;
    /**
     * 4档温度重量(0~255)
     */
    private int mFourClothingWeight=4;
    /**
     * 5档温度重量(0~255)
     */
    private int mFiveClothingWeight=5;
    /**
     * 6档温度重量(0~255)
     */
    private int mSixClothingWeight=6;
    /**
     * 7档温度重量(0~255)
     */
    private int mSevenClothingWeight=7;
    /**
     * 8档温度重量(0~255)
     */
    private int mEightClothingWeight=8;

    /**
     * 阻抗值(0-65535)
     */
    private int mAdc;

    /**
     * 体重 (0~65535)
     */
    private int mWeight;

    public int getUserType() {
        return mUserType;
    }

    public void setUserType(@ADWeightScaleBleConfig.USER_TYPE int userType) {
        mUserType = userType;
    }

    public int getSex() {
        return mSex;
    }

    public void setSex(@ADWeightScaleBleConfig.SEX int sex) {
        mSex = sex;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        if (age > 0x7f)
            age = 0x7f;
        mAge = age;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = maxBig(height);
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getOneClothingWeight() {
        return mOneClothingWeight;
    }

    public void setOneClothingWeight(int oneClothingWeight) {
        mOneClothingWeight = maxBig(oneClothingWeight);
    }

    public int getTwoClothingWeight() {
        return mTwoClothingWeight;
    }

    public void setTwoClothingWeight(int twoClothingWeight) {
        mTwoClothingWeight = maxBig(twoClothingWeight);
    }

    public int getThreeClothingWeight() {
        return mThreeClothingWeight;
    }

    public void setThreeClothingWeight(int threeClothingWeight) {
        mThreeClothingWeight = maxBig(threeClothingWeight);
    }

    public int getFourClothingWeight() {
        return mFourClothingWeight;
    }

    public void setFourClothingWeight(int fourClothingWeight) {
        mFourClothingWeight = maxBig(fourClothingWeight);
    }

    public int getFiveClothingWeight() {
        return mFiveClothingWeight;
    }

    public void setFiveClothingWeight(int fiveClothingWeight) {
        mFiveClothingWeight = maxBig(fiveClothingWeight);
    }

    public int getSixClothingWeight() {
        return mSixClothingWeight;
    }

    public void setSixClothingWeight(int sixClothingWeight) {
        mSixClothingWeight = maxBig(sixClothingWeight);
    }

    public int getSevenClothingWeight() {
        return mSevenClothingWeight;
    }

    public void setSevenClothingWeight(int sevenClothingWeight) {
        mSevenClothingWeight = maxBig(sevenClothingWeight);
    }

    public int getEightClothingWeight() {
        return mEightClothingWeight;
    }

    public void setEightClothingWeight(int eightClothingWeight) {
        mEightClothingWeight = maxBig(eightClothingWeight);
    }

    public int getAdc() {
        return mAdc;
    }

    public void setAdc(int adc) {
        if (adc > 0xffff)
            adc = 0xffff;
        mAdc = adc;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        if (weight > 0xffff)
            weight = 0xffff;
        mWeight = weight;
    }

    private int maxBig(int data) {
        if (data > 0xff)
            data = 0xff;
        return data;
    }
}
