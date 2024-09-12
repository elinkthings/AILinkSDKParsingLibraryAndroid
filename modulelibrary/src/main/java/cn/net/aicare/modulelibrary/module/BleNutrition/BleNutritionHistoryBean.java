package cn.net.aicare.modulelibrary.module.BleNutrition;

public class BleNutritionHistoryBean {

    /**
     * 时间(s)
     */
    private long time;
    /**
     * 重量
     */
    private long weight;
    /**
     * 单位
     */
    private int unit;
    /**
     * 小数点
     */
    private int decimal;
    /**
     * 符号 0 正数 1 负数
     */
    private int symbol;

    public BleNutritionHistoryBean() {
    }

    public BleNutritionHistoryBean(long time, long weight, int unit, int decimal, int symbol) {
        this.time = time;
        this.weight = weight;
        this.unit = unit;
        this.decimal = decimal;
        this.symbol = symbol;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "BleNutritionHistoryBean{" +
                "time=" + time +
                ", weight=" + weight +
                ", unit=" + unit +
                ", decimal=" + decimal +
                ", negative=" + symbol +
                '}';
    }
}
