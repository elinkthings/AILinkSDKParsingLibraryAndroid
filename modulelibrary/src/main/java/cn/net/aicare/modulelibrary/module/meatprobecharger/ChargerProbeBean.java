package cn.net.aicare.modulelibrary.module.meatprobecharger;

/**
 * @auther ljl
 * on 2023/2/24
 */
public class ChargerProbeBean {
    private int num;
    private String mac;
    private int foodUnit;
    private int foodPositive;//0正1负
    private int foodTemp;
    private int ambientUnit;
    private int ambientPositive;//0正1负
    private int ambientTemp;
    private int chargerState;
    private int battery;
    private int insertState;

    public ChargerProbeBean() {
    }

    public ChargerProbeBean(int num, String mac, int foodUnit, int foodPositive, int foodTemp,
                            int ambientUnit, int ambientPositive, int ambientTemp, int chargerState, int battery, int insertState) {
        this.num = num;
        this.mac = mac;
        this.foodUnit = foodUnit;
        this.foodPositive = foodPositive;
        this.foodTemp = foodTemp;
        this.ambientUnit = ambientUnit;
        this.ambientPositive = ambientPositive;
        this.ambientTemp = ambientTemp;
        this.chargerState = chargerState;
        this.battery = battery;
        this.insertState = insertState;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getFoodUnit() {
        return foodUnit;
    }

    public void setFoodUnit(int foodUnit) {
        this.foodUnit = foodUnit;
    }

    public int getFoodPositive() {
        return foodPositive;
    }

    public void setFoodPositive(int foodPositive) {
        this.foodPositive = foodPositive;
    }

    public int getFoodTemp() {
        return foodTemp;
    }

    public void setFoodTemp(int foodTemp) {
        this.foodTemp = foodTemp;
    }

    public int getAmbientUnit() {
        return ambientUnit;
    }

    public void setAmbientUnit(int ambientUnit) {
        this.ambientUnit = ambientUnit;
    }

    public int getAmbientPositive() {
        return ambientPositive;
    }

    public void setAmbientPositive(int ambientPositive) {
        this.ambientPositive = ambientPositive;
    }

    public int getAmbientTemp() {
        return ambientTemp;
    }

    public void setAmbientTemp(int ambientTemp) {
        this.ambientTemp = ambientTemp;
    }

    public int getChargerState() {
        return chargerState;
    }

    public void setChargerState(int chargerState) {
        this.chargerState = chargerState;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getInsertState() {
        return insertState;
    }

    public void setInsertState(int insertState) {
        this.insertState = insertState;
    }
}
