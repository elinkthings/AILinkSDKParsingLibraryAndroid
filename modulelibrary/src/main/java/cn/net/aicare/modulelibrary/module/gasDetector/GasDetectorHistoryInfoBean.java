package cn.net.aicare.modulelibrary.module.gasDetector;

/**
 * 离线历史记录信息
 *
 * @author xing
 * @date 2024/09/05
 */
public class GasDetectorHistoryInfoBean {

    /**
     * 当前记录序号(1~n)
     */
    private int count;
    /**
     * 总记录(n)
     */
    private int allCount;
    /**
     * 时间戳(大端序,单位:s)
     */
    private long timestamp;

    /**
     * CO2二氧化碳浓度(单位ppm)
     */
    private int co2;
    /**
     * CO一氧化碳浓度(单位ppm)
     */
    private int co;

    /**
     * O2浓度(%)
     */
    private int o2;

    /**
     * 温度单位
     */
    private int tempUnit;
    /**
     * 温度(℉,-40~212)
     */
    private int temp;
    /**
     * 湿度(%RH,0-99)
     */
    private int humidity;

    /**
     * 紫外线等级(Lv,0-11)
     */
    private int uv;
    /**
     * 气压单位(0:hPa,1:inHg)
     */
    private int pressureUnit;
    /**
     * 气压
     */
    private int pressure;

    public GasDetectorHistoryInfoBean() {
    }

    public GasDetectorHistoryInfoBean(int count, int allCount, long timestamp, int co2, int co, int o2, int tempUnit, int temp, int humidity, int uv, int pressureUnit, int pressure) {
        this.count = count;
        this.allCount = allCount;
        this.timestamp = timestamp;
        this.co2 = co2;
        this.co = co;
        this.o2 = o2;
        this.tempUnit = tempUnit;
        this.temp = temp;
        this.humidity = humidity;
        this.uv = uv;
        this.pressureUnit = pressureUnit;
        this.pressure = pressure;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getCo() {
        return co;
    }

    public void setCo(int co) {
        this.co = co;
    }

    public int getO2() {
        return o2;
    }

    public void setO2(int o2) {
        this.o2 = o2;
    }

    public int getTempUnit() {
        return tempUnit;
    }

    public void setTempUnit(int tempUnit) {
        this.tempUnit = tempUnit;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public int getPressureUnit() {
        return pressureUnit;
    }

    public void setPressureUnit(int pressureUnit) {
        this.pressureUnit = pressureUnit;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", allCount=" + allCount +
                ", timestamp=" + timestamp +
                ", co2=" + co2 +
                ", co=" + co +
                ", o2=" + o2 +
                ", tempUnit=" + tempUnit +
                ", temp=" + temp +
                ", humidity=" + humidity +
                ", uv=" + uv +
                ", pressureUnit=" + pressureUnit +
                ", pressure=" + pressure +
                '}';
    }
}
