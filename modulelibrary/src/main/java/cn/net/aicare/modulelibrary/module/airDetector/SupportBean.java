package cn.net.aicare.modulelibrary.module.airDetector;

/**
 * 支持 bean
 */
public class SupportBean {

    /**
     * 类型
     */
    private int type;
    /**
     * 最小值
     */
    private double min;
    /**
     * 最大值
     */
    private double max;
    /**
     * 小数点
     */
    private int point;
    /**
     * 当前值
     */
    private double curValue;
    /**
     * 单位
     */
    private int unit;

    /**
     * 扩展信息
     */
    private Object extentObject;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public double getCurValue() {
        return curValue;
    }

    public void setCurValue(double curValue) {
        this.curValue = curValue;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public Object getExtentObject() {
        return extentObject;
    }

    public void setExtentObject(Object extentObject) {
        this.extentObject = extentObject;
    }
}
