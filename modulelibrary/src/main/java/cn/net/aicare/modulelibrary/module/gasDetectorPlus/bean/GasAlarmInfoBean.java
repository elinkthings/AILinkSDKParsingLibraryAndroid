package cn.net.aicare.modulelibrary.module.gasDetectorPlus.bean;


import cn.net.aicare.modulelibrary.module.gasDetectorPlus.GasDetectorPlusBleConfig;

/**
 * 气体报警信息
 *
 * @author xing
 * @date 2024/10/25
 */
public class GasAlarmInfoBean {

    /**
     * 类型{@link GasDetectorPlusBleConfig#GasType}
     */
    private int type;
    /**
     * 状态
     * 0x00=未报警
     * 0x01-报警,
     */
    private int status;

    public GasAlarmInfoBean() {
    }

    public GasAlarmInfoBean(int type, int status) {
        this.type = type;
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GasAlarmInfoBean{" +
                "type=" + type +
                ", status=" + status +
                '}';
    }
}
