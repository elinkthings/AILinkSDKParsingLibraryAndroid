package cn.net.aicare.modulelibrary.module.babyscale;

/**
 * xing<br>
 * 2019/5/11<br>
 * 婴儿称指令
 */
public class BabyBleConfig {

    /**
     * 稳定重量 Stable weight
     */
    public static final byte GET_WEIGHT = 0x01;
    /**
     * 实时重量 Change weight
     */
    public static final byte GET_WEIGHT_NOW = 0x02;
    /**
     * 身长数据 height
     */
    public static final byte GET_HEIGHT = 0x03;

    /**
     * 设置单位 Set unit
     */
    public static final byte SET_UNIT = (byte) 0x81;
    /**
     * 获取单位 Get units
     */
    public static final byte GET_UNIT = (byte) 0x82;
    /**
     * 下发指令(app --> mcu)
     */
    public static final byte SET_CMD = (byte) 0x83;

    /**
     * 回复指令(mcu --> app)
     */
    public static final byte GET_CMD = (byte) 0x84;
    /**
     * 错误指令(超重,重量不稳定等)
     * Wrong instruction (overweight, unstable weight, etc.)
     */
    public static final byte GET_ERR = (byte) 0xFF;

    //-------重量
    public final static byte BABY_KG = 0;
    /**
     * 斤
     */
    public final static byte BABY_FG = 1;
    public final static byte BABY_LB = 2;
    public final static byte BABY_OZ = 3;
    public final static byte BABY_ST = 4;
    public final static byte BABY_G = 5;
    public final static byte BABY_LB_LB = 6;
    //-------身高
    public final static byte BABY_CM = 0;
    public final static byte BABY_INCH = 1;
    public final static byte BABY_FEET = 2;

}
