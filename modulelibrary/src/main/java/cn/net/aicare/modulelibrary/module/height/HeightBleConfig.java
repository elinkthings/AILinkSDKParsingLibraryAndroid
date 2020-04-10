package cn.net.aicare.modulelibrary.module.height;

/**
 * xing<br>
 * 2019/5/11<br>
 * 身高仪指令
 */
public class HeightBleConfig {

    /**
     * 稳定
     */
    public static final byte HEIGHT = 0x01;
    /**
     * 设置单位
     */
    public static final byte SET_UNIT = (byte) 0x81;
    public static final byte HEIGHT_UNIT_CM = (byte) 0x00;
    public static final byte HEIGHT_UNIT_INCH = (byte) 0x01;
    public static final byte HEIGHT_UNIT_FT_IN = (byte) 0x02;
    public static final byte WEIGHT_UNIT_KG = (byte) 0x00;
    public static final byte WEIGHT_UNIT_FG = (byte) 0x01;
    public static final byte WEIGHT_UNIT_LB = (byte) 0x02;
    public static final byte WEIGHT_UNIT_OZ = (byte) 0x03;
    public static final byte WEIGHT_UNIT_ST = (byte) 0x04;
    public static final byte WEIGHT_UNIT_G = (byte) 0x05;

    /**
     * 获取单位
     */
    public static final byte GET_UNIT = (byte) 0x82;
    /**
     * 错误信息(测量失败)
     */
    public static final byte GET_ERR = (byte) 0xFF;

    public static final byte ERR_CMD = (byte) 0xFF;


}
