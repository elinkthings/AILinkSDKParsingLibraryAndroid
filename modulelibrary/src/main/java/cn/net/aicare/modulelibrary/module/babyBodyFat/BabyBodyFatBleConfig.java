package cn.net.aicare.modulelibrary.module.babyBodyFat;

/**
 * xing<br>
 * 2019/5/11<br>
 * 婴儿体质两用秤指令
 */
public class BabyBodyFatBleConfig {

    /**
     * 婴儿体脂两用秤
     */
    public final static int BABY_BODY_FAT =0x1A;

    /**
     * 稳定重量 Stable weight
     */
    public static final byte GET_WEIGHT = 0x01;
    /**
     * 实时重量 Change weight
     */
    public static final byte GET_WEIGHT_NOW = 0x02;



    /**
     * 测阻抗中  Measuring impedance
     */
    public final static int GET_IMPEDANCE_TESTING = 0x04;
    /**
     * 测阻抗成功，带上阻抗数据
     * Successful impedance measurement, bring impedance data
     */
    public final static int GET_IMPEDANCE_SUCCESS_DATA = 0x05;
    /**
     * 测阻抗失败 Failed to measure impedance
     */
    public final static int GET_IMPEDANCE_FAIL = 0x06;
    /**
     * 测阻抗成功,带上阻抗数据
     * Successful impedance measurement, bring impedance data
     */
    public final static int GET_IMPEDANCE_SUCCESS = 0x07;



    /**
     * 身长数据 height
     */
    public static final byte GET_HEIGHT = 0x08;
    /**
     * 身长数据实时 Change height
     */
    public static final byte GET_HEIGHT_NOW = 0x09;

    /**
     * 测量完成
     * Measurement completed
     */
    public static final byte GET_TEST_FINISH = 0x0A;


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
