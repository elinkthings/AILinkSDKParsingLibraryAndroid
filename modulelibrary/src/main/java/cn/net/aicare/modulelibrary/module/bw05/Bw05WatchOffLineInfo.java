package cn.net.aicare.modulelibrary.module.bw05;

/**
 * @author ljl
 * on 2024/6/7
 */
public class Bw05WatchOffLineInfo {

    //数据采集时间
    private String timeStr;
    //心率
    private int hr;
    //血氧
    private int spo2;
    //温度
    private float temp;

    public Bw05WatchOffLineInfo() {
    }

    public Bw05WatchOffLineInfo(String timeStr, int hr, int spo2, float temp) {
        this.timeStr = timeStr;
        this.hr = hr;
        this.spo2 = spo2;
        this.temp = temp;
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

    @Override
    public String toString() {
        return "本地存储数据{" +
                "采集时间='" + timeStr + '\'' +
                ", 心率=" + hr +
                ", 血氧=" + spo2 +
                ", 温度=" + temp +
                '}';
    }
}
