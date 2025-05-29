package cn.net.aicare.modulelibrary.module.bw05;

/**
 * @author ljl
 * on 2024/6/5
 * 设备信息
 */
public class Bw05WatchDeviceInfo {

    //mac地址
    private String macStr;
    //时间
    private String timeStr;
    //心率值
    private int hr;
    //血氧值
    private int spo2;
    //温度值
    private float temp;
    //当天步数
    private int curDayStep;
    //电量
    private int battery;
    //充电状态
    private int chargerState;
    //lora信号强度
    private int loraRssi;

    public Bw05WatchDeviceInfo() {
    }

    public Bw05WatchDeviceInfo(String timeStr, int hr, int spo2, float temp, int curDayStep, int battery, int chargerState, int loraRssi) {
        this.timeStr = timeStr;
        this.hr = hr;
        this.spo2 = spo2;
        this.temp = temp;
        this.curDayStep = curDayStep;
        this.battery = battery;
        this.chargerState = chargerState;
        this.loraRssi = loraRssi;
    }

    public Bw05WatchDeviceInfo(String macStr, String timeStr, int hr, int spo2, float temp, int curDayStep, int battery, int chargerState, int loraRssi) {
        this.macStr = macStr;
        this.timeStr = timeStr;
        this.hr = hr;
        this.spo2 = spo2;
        this.temp = temp;
        this.curDayStep = curDayStep;
        this.battery = battery;
        this.chargerState = chargerState;
        this.loraRssi = loraRssi;
    }

    public String getMacStr() {
        return macStr;
    }

    public void setMacStr(String macStr) {
        this.macStr = macStr;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getCurDayStep() {
        return curDayStep;
    }

    public void setCurDayStep(int curDayStep) {
        this.curDayStep = curDayStep;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getChargerState() {
        return chargerState;
    }

    public void setChargerState(int chargerState) {
        this.chargerState = chargerState;
    }

    public int getLoraRssi() {
        return loraRssi;
    }

    public void setLoraRssi(int loraRssi) {
        this.loraRssi = loraRssi;
    }

    @Override
    public String toString() {
        return "设备信息{" +
                "mac地址=" + macStr +
                ", 时间=" + timeStr +
                ", 心率值=" + hr +
                ", 血氧值=" + spo2 +
                ", 温度值=" + temp +
                ", 当天步数=" + curDayStep +
                ", 电量=" + battery +
                ", 充电状态=" + chargerState +
                ", lora信号强度=" + loraRssi +
                "}";
    }


}
