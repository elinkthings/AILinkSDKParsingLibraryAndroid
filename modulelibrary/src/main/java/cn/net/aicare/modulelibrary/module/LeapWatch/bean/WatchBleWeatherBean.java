package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

/**
 * 手表天气对象
 */
public class WatchBleWeatherBean {
    /**
     * 时间戳
     */
    private int mTimestamp;
    /**
     * 当前温度
     */
    private int mTemp;
    /**
     * 最高温度
     */
    private int mTempHigh;
    /**
     * 最低温度
     */
    private int mTempLow;

    /**
     * 天气代码
     * 0x00	未知
     * 0x01	晴天
     * 0x02	多天
     * 0x03	阴天
     * 0x04	雨天
     * 0x05	打雷
     * 0x06	暴雨
     * 0x07	大风
     * 0x08	下雪
     * 0x09	雾
     * 0x0A	沙尘暴
     * 0x0B	雾霾
     */
    private int mWeatherCode;

    /**
     * 风速
     */
    private int mWindSpd;
    /**
     * 相对湿度
     */
    private int mHum;
    /**
     * 能见度(公里)
     */
    private int mVis;
    /**
     * 紫外线强度
     */
    private int mUv;
    /**
     * 降雨量(毫米)
     */
    private int mPcpn;

    public WatchBleWeatherBean() {
    }

    public WatchBleWeatherBean(int timestamp, int temp, int tempHigh, int tempLow, int weatherCode, int windSpd, int hum, int vis, int uv, int pcpn) {
        mTimestamp = timestamp;
        mTemp = temp;
        mTempHigh = tempHigh;
        mTempLow = tempLow;
        mWeatherCode = weatherCode;
        mWindSpd = windSpd;
        mHum = hum;
        mVis = vis;
        mUv = uv;
        mPcpn = pcpn;
    }

    public int getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(int timestamp) {
        mTimestamp = timestamp;
    }

    public int getTemp() {
        return mTemp;
    }

    public void setTemp(int temp) {
        mTemp = temp;
    }

    public int getTempHigh() {
        return mTempHigh;
    }

    public void setTempHigh(int tempHigh) {
        mTempHigh = tempHigh;
    }

    public int getTempLow() {
        return mTempLow;
    }

    public void setTempLow(int tempLow) {
        mTempLow = tempLow;
    }

    public int getWeatherCode() {
        return mWeatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        mWeatherCode = weatherCode;
    }

    public int getWindSpd() {
        return mWindSpd;
    }

    public void setWindSpd(int windSpd) {
        mWindSpd = windSpd;
    }

    public int getHum() {
        return mHum;
    }

    public void setHum(int hum) {
        mHum = hum;
    }

    public int getVis() {
        return mVis;
    }

    public void setVis(int vis) {
        mVis = vis;
    }

    public int getUv() {
        return mUv;
    }

    public void setUv(int uv) {
        mUv = uv;
    }

    public int getPcpn() {
        return mPcpn;
    }

    public void setPcpn(int pcpn) {
        mPcpn = pcpn;
    }

    @Override
    public String toString() {
        return "WatchBleWeatherBean{" + "mTimestamp=" + mTimestamp + ", mTemp=" + mTemp + ", mTempHigh=" + mTempHigh + ", mTempLow=" + mTempLow + ", mWeatherCode=" + mWeatherCode + ", mWindSpd=" + mWindSpd + ", mHum=" + mHum + ", mVis=" + mVis + ", mUv=" + mUv + ", mPcpn=" + mPcpn + '}';
    }
}
