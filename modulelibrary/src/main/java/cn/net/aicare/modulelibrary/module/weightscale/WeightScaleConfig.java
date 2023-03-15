package cn.net.aicare.modulelibrary.module.weightscale;

/**
 * @auther ljl
 * on 2023/3/3
 * 体重秤指令配置
 */
public class WeightScaleConfig {

    /**
     * mcu发送体重测量--实时体重
     */
    public final static int WEIGHT_SCALE_REAL_WEIGHT = 0x01;
    /**
     * mcu发送体重测量--稳定体重
     */
    public final static int WEIGHT_SCALE_WEIGHT = 0x02;
    /**
     * 同步bmi
     */
    public final static int WEIGHT_SCALE_SYNC_BMI = 0x09;
    /**
     * 测量完成
     */
    public final static int WEIGHT_SCALE_OK = 0x0A;


    /**
     * 设置单位
     * kg--0x00
     * 斤--0x01
     * st:lb--0x04
     * lb--0x06
     */
    public final static int WEIGHT_SCALE_KG = 0x00;

    public final static int WEIGHT_SCALE_JIN = 0x01;

    public final static int WEIGHT_SCALE_ST_LB = 0x04;

    public final static int WEIGHT_SCALE_LB = 0x06;

    /**
     * 设备返回设置单位结果
     */
    public final static int WEIGHT_SCALE_SET_UNIT = 0x82;

    /**
     * 设备发送错误码
     */
    public final static int WEIGHT_SCALE_ERROR_CODE = 0xFF;




}
