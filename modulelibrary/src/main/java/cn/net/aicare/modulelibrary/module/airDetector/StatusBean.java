package cn.net.aicare.modulelibrary.module.airDetector;

/**
 * 同步数据
 *
 * @author yesp
 */
public class StatusBean {
    /**
     * 类型
     */
    private int type;
    /**
     * 当前值
     */
    private double value;
    /**
     * 小数点
     */
    private int point;
    /**
     * 单位
     */
    private int unit;

    /**
     * 开关状态
     */
    private boolean open;

    /**
     * 报警下限值
     */
    private float warmMin;

    /**
     * 报警上限值
     */
    private float warmMax;

    /**
     * 校准值
     */
    private float calValue;

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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public float getWarmMin() {
        return warmMin;
    }

    public void setWarmMin(float warmMin) {
        this.warmMin = warmMin;
    }

    public float getWarmMax() {
        return warmMax;
    }

    public void setWarmMax(float warmMax) {
        this.warmMax = warmMax;
    }

    public Object getExtentObject() {
        return extentObject;
    }

    public void setExtentObject(Object extentObject) {
        this.extentObject = extentObject;
    }

    public float getCalValue() {
        return calValue;
    }

    public void setCalValue(float calValue) {
        this.calValue = calValue;
    }
}
