package cn.net.aicare.modulelibrary.module.scooter;

/**
 * xing<br>
 * 2020/11/21<br>
 * 天气对象
 */
public class BleWeatherBean {

    /**
     * 天气编号
     */
    private int mCode;

    /**
     * 湿度
     */
    private int mHumidity;
    /**
     * 风向
     */
    private int mWindDirection;
    /**
     * 风速
     */
    private int mWindSpeed;
    /**
     * 紫外线强度
     */
    private int mUVIntensity;
    /**
     * 空气质量指数
     */
    private int mAirQualityIndex;
    /**
     * 最高气温（8位有符号数，单位℃）
     */
    private int mMaxTemperature;
    /**
     * 最低气温（8位有符号数，单位℃）
     */
    private int mMinTemperature;


    public BleWeatherBean(int code, int humidity, int windDirection, int windSpeed, int UVIntensity, int airQualityIndex, int maxTemperature, int minTemperature) {
        mCode = code;
        mHumidity = humidity;
        mWindDirection = windDirection;
        mWindSpeed = windSpeed;
        mUVIntensity = UVIntensity;
        mAirQualityIndex = airQualityIndex;
        mMaxTemperature = maxTemperature;
        mMinTemperature = minTemperature;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int humidity) {
        mHumidity = humidity;
    }

    public int getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(int windDirection) {
        mWindDirection = windDirection;
    }

    public int getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        mWindSpeed = windSpeed;
    }

    public int getUVIntensity() {
        return mUVIntensity;
    }

    public void setUVIntensity(int UVIntensity) {
        mUVIntensity = UVIntensity;
    }

    public int getAirQualityIndex() {
        return mAirQualityIndex;
    }

    public void setAirQualityIndex(int airQualityIndex) {
        mAirQualityIndex = airQualityIndex;
    }

    public int getMaxTemperature() {
        return mMaxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        mMaxTemperature = maxTemperature;
    }

    public int getMinTemperature() {
        return mMinTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        mMinTemperature = minTemperature;
    }

    @Override
    public String toString() {
        return "BleWeatherBean{" + "mCode=" + mCode + ", mHumidity=" + mHumidity + ", mWindDirection=" + mWindDirection + ", mWindSpeed=" + mWindSpeed + ", mUVIntensity=" + mUVIntensity + ", " +
                "mAirQualityIndex=" + mAirQualityIndex + ", mMaxTemperature=" + mMaxTemperature + ", mMinTemperature=" + mMinTemperature + '}';
    }
}
