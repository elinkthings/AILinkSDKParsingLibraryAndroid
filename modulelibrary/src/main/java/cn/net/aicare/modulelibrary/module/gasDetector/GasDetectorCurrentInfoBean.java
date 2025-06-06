package cn.net.aicare.modulelibrary.module.gasDetector;

/**
 * 气体探测器当前信息bean
 *
 * @author xing
 * @date 2024/09/05
 */
public class GasDetectorCurrentInfoBean {

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
     * 温度单位:
     * 00:摄氏度
     * 01:华氏度
     */
    private int tempUnit;
    /**
     * 温度
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
     * 气压单位
     * 00:hPa
     * 01:inHg
     */
    private int pressureUnit;
    /**
     * 气压
     */
    private int pressure;
    /**
     * 电量(%,0-100)
     */
    private int battery;


    public GasDetectorCurrentInfoBean(int co2, int co, int o2, int tempUnit, int temp, int humidity, int uv, int pressureUnit, int pressure, int battery) {
        this.co2 = co2;
        this.co = co;
        this.o2 = o2;
        this.tempUnit = tempUnit;
        this.temp = temp;
        this.humidity = humidity;
        this.uv = uv;
        this.pressureUnit = pressureUnit;
        this.pressure = pressure;
        this.battery = battery;
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

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }


    @Override
    public String toString() {
        return "CO2=" + co2 + " PPM" +
                "\nCO=" + co + " PPM" +
                "\nTEMP=" + (temp)+ (tempUnit == 0 ? "℃" : "℉") +
                "\nHUM=" + humidity + "%" +
                "\nUVI=" + uv
                ;
    }
}
