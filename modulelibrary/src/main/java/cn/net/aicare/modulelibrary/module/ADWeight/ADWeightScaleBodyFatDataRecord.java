package cn.net.aicare.modulelibrary.module.ADWeight;

import java.io.Serializable;

/**
 * xing<br>
 * 2019/11/13<br>
 * 体脂数据对象
 */
public class ADWeightScaleBodyFatDataRecord extends ADWeightScaleBodyFatData implements Serializable, Cloneable {

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

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mSecond;

    private int mWeight;

    private int mDecimal;

    private int mUnit;

    /**
     * 阻抗值
     */
    private int mAdc;


    public int getUserType() {
        return mUserType;
    }

    public void setUserType(int userType) {
        mUserType = userType;
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

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public int getSecond() {
        return mSecond;
    }

    public void setSecond(int second) {
        mSecond = second;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public int getDecimal() {
        return mDecimal;
    }

    public void setDecimal(int decimal) {
        mDecimal = decimal;
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
    protected ADWeightScaleBodyFatDataRecord clone() {
        return (ADWeightScaleBodyFatDataRecord) super.clone();
    }

    @Override
    public String toString() {
        String data= "{" + "mUserType=" + mUserType + ", mSex=" + mSex +
                ", mAge=" + mAge + ", mHeight=" + mHeight + ", mUserId=" + mUserId + ", mYear=" + mYear + ", mMonth=" + mMonth + ", mDay=" + mDay + ", mHour=" + mHour + ", mMinute=" + mMinute + ", mSecond=" + mSecond + ", mWeight=" + mWeight + ", mDecimal=" + mDecimal + ", mUnit=" + mUnit + ", mAdc=" + mAdc + '}';
        return super.toString()+data;

    }
}
