package cn.net.aicare.modulelibrary.module.thermometer;

/**
 * xing<br>
 * 2019/5/11<br>
 * 体温计指令
 */
public class TempBleConfig {

    /**
     * 稳定温度
     */
    public static final byte TEMP = 0x01;
    public static final byte TEMP_NOW = 0x02;
    /**
     * 设置单位
     */
    public static final byte SET_UNIT = (byte) 0x81;
    public static final byte TEMP_UNIT_C = (byte) 0x00;
    public static final byte TEMP_UNIT_F = (byte) 0x01;

    /**
     * 获取单位
     */
    public static final byte GET_UNIT = (byte) 0x82;
    /**
     * 错误信息(温度过高,温度过低)
     */
    public static final byte GET_ERR = (byte) 0xFF;



}
