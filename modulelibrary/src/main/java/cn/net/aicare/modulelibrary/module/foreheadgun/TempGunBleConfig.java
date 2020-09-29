package cn.net.aicare.modulelibrary.module.foreheadgun;

/**
 * xing<br>
 * 2019/5/11<br>
 * 额温枪指令
 */
public class TempGunBleConfig {

    /**
     * 额温枪(Forehead gun)
     */
    public final static int INFRARED_THERMOMETER =0x02;

    public static final int DIY=-1;
    /**
     * 稳定温度(额温)
     */
    public static final byte TEMP = 0x01;
    public static final byte TEMP_NOW = 0x02;
    /**
     * 耳温
     */
    public static final byte TEMP_EAR = 0x03;
    public static final byte TEMP_EAR_NOW = 0x04;
    /**
     * 环境温度
     */
    public static final byte TEMP_SURROUNDING = 0x05;
    public static final byte TEMP_SURROUNDING_NOW = 0x06;
    /**
     * 物温
     */
    public static final byte TEMP_BODY = 0x07;
    public static final byte TEMP_BODY_NOW = 0x08;
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
