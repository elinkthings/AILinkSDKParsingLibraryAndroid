package cn.net.aicare.modulelibrary.module.meatprobe;


import java.io.Serializable;

/**
 * 探针Bean
 * 探针数据对象(自定义)
 *
 * @author xing
 * @date 2023/05/11
 */
public class ProbeBean implements Serializable {

    /**
     * mac地址
     */
    private String mac;
    /**
     * 数据版本 当前默认0x01
     */
    private int version = 0x02;

    /**
     * //烧烤Id（选择食物的时间戳）
     */
    private long cookingId;

    /**
     * //食物类型 {@link FoodConfig#FOOD_TYPE_BEEF}
     */
    private int foodType = FoodConfig.FOOD_TYPE_NO_SETTING;

    /**
     * //食物熟度 {@link FoodConfig#FOOD_DEGREE_MRATE}
     */
    private int foodRawness = -1;

    /**
     * //目标温度℃
     */
    private int targetTemperature_C;

    /**
     * //目标温度℉
     */
    private int targetTemperature_F;

    /**
     * //环境温度下限℃
     */
    private int ambientMinTemperature_C;

    /**
     * //环境温度下限℉
     */
    private int ambientMinTemperature_F;

    /**
     * //环境温度上限℃
     */
    private int ambientMaxTemperature_C;

    /**
     * //环境温度上限℉
     */
    private int ambientMaxTemperature_F;

    /**
     *  //提醒温度对目标温度百分比0.8~1 如果获取到的小于0.8则等于0.8，大于1则等于1
     */
    private double alarmTemperaturePercent = 0.95;
    /**
     * 开始时间戳,用在计时器上
     */
    private long timerStart;

    /**
     * //结束时间戳,用在计时器上
     */
    private long timerEnd;
    /**
     * 当前单位
     */
    private int currentUnit;

    public ProbeBean(String mac) {
        this.mac = mac;
    }

    public ProbeBean(String mac, int version, long cookingId, int foodType, int foodRawness, int targetTemperature_C, int targetTemperature_F, int ambientMinTemperature_C,
                     int ambientMinTemperature_F, int ambientMaxTemperature_C, int ambientMaxTemperature_F, double alarmTemperaturePercent, long timerStart, long timerEnd, int currentUnit) {
        this.mac = mac;
        this.version = version;
        this.cookingId = cookingId;
        this.foodType = foodType;
        this.foodRawness = foodRawness;
        this.targetTemperature_C = targetTemperature_C;
        this.targetTemperature_F = targetTemperature_F;
        this.ambientMinTemperature_C = ambientMinTemperature_C;
        this.ambientMinTemperature_F = ambientMinTemperature_F;
        this.ambientMaxTemperature_C = ambientMaxTemperature_C;
        this.ambientMaxTemperature_F = ambientMaxTemperature_F;
        this.alarmTemperaturePercent = alarmTemperaturePercent;
        this.timerStart = timerStart;
        this.timerEnd = timerEnd;
        this.currentUnit = currentUnit;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getCookingId() {
        return cookingId;
    }

    public void setCookingId(long cookingId) {
        this.cookingId = cookingId;
    }

    public int getFoodType() {
        return foodType;
    }

    public void setFoodType(int foodType) {
        this.foodType = foodType;
    }

    public int getFoodRawness() {
        return foodRawness;
    }

    public void setFoodRawness(int foodRawness) {
        this.foodRawness = foodRawness;
    }

    public int getTargetTemperature_C() {
        return targetTemperature_C;
    }

    public void setTargetTemperature_C(int targetTemperature_C) {
        this.targetTemperature_C = targetTemperature_C;
    }

    public int getTargetTemperature_F() {
        return targetTemperature_F;
    }

    public void setTargetTemperature_F(int targetTemperature_F) {
        this.targetTemperature_F = targetTemperature_F;
    }

    public int getAmbientMinTemperature_C() {
        return ambientMinTemperature_C;
    }

    public void setAmbientMinTemperature_C(int ambientMinTemperature_C) {
        this.ambientMinTemperature_C = ambientMinTemperature_C;
    }

    public int getAmbientMinTemperature_F() {
        return ambientMinTemperature_F;
    }

    public void setAmbientMinTemperature_F(int ambientMinTemperature_F) {
        this.ambientMinTemperature_F = ambientMinTemperature_F;
    }

    public int getAmbientMaxTemperature_C() {
        return ambientMaxTemperature_C;
    }

    public void setAmbientMaxTemperature_C(int ambientMaxTemperature_C) {
        this.ambientMaxTemperature_C = ambientMaxTemperature_C;
    }

    public int getAmbientMaxTemperature_F() {
        return ambientMaxTemperature_F;
    }

    public void setAmbientMaxTemperature_F(int ambientMaxTemperature_F) {
        this.ambientMaxTemperature_F = ambientMaxTemperature_F;
    }

    public double getAlarmTemperaturePercent() {
        return alarmTemperaturePercent;
    }

    public void setAlarmTemperaturePercent(double alarmTemperaturePercent) {
        this.alarmTemperaturePercent = alarmTemperaturePercent;
    }

    public long getTimerStart() {
        return timerStart;
    }

    public void setTimerStart(long timerStart) {
        this.timerStart = timerStart;
    }

    public long getTimerEnd() {
        return timerEnd;
    }

    public void setTimerEnd(long timerEnd) {
        this.timerEnd = timerEnd;
    }

    public int getCurrentUnit() {
        return currentUnit;
    }

    public void setCurrentUnit(int currentUnit) {
        this.currentUnit = currentUnit;
    }

    @Override
    public String toString() {
        return "ProbeBean{" + "mac='" + mac + '\'' + ", version=" + version + ", cookingId=" + cookingId + ", foodType=" + foodType + ", foodRawness=" + foodRawness + ", targetTemperature_C=" + targetTemperature_C + ", targetTemperature_F=" + targetTemperature_F + ", ambientMinTemperature_C=" + ambientMinTemperature_C + ", ambientMinTemperature_F=" + ambientMinTemperature_F + ", ambientMaxTemperature_C=" + ambientMaxTemperature_C + ", ambientMaxTemperature_F=" + ambientMaxTemperature_F + ", alarmTemperaturePercent=" + alarmTemperaturePercent + ", timerStart=" + timerStart + ", timerEnd=" + timerEnd + ", currentUnit=" + currentUnit + '}';
    }
}
