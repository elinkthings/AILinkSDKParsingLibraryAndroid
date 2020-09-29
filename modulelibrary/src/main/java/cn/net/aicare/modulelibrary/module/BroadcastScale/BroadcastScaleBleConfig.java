package cn.net.aicare.modulelibrary.module.BroadcastScale;

/**
 * xing<br>
 * 2019/5/11<br>
 * 广播秤指令
 */
public class BroadcastScaleBleConfig {




    /**
     * 广播秤
     */
    public final static int BROADCAST_SCALE =0x01;

    /**
     * 凌阳定制的cid
     */
    public final static int BROADCAST_SCALE_LING_YANG_CID =0x16;


    /**
     * 稳定重量 Stable weight
     */
    public static final int GET_WEIGHT = 0x01;
    /**
     * 实时重量 Change weight
     */
    public static final int GET_WEIGHT_NOW = 0x02;

    /**
     * 正在测量体重 （此时阻抗数值为 0）
     */
    public final static int GET_WEIGHT_TESTING = 0x00;

    /**
     * 测阻抗中  Measuring impedance
     */
    public final static int GET_IMPEDANCE_TESTING = 0x01;
    /**
     * 测阻抗成功，带上阻抗数据
     * Successful impedance measurement, bring impedance data
     */
    public final static int GET_IMPEDANCE_SUCCESS = 0x02;
    /**
     * 测阻抗失败 Failed to measure impedance
     */
    public final static int GET_IMPEDANCE_FAIL = 0x03;
    /**
     * 测量完成
     * Measurement completed
     */
    public static final int GET_TEST_FINISH = (byte) 0xFF;



    //-------重量
    public final static byte UNIT_KG = 0;
    /**
     * 斤
     */
    public final static byte UNIT_FG = 1;
    public final static byte UNIT_LB = 2;
    public final static byte UNIT_OZ = 3;
    public final static byte UNIT_ST = 4;
    public final static byte UNIT_G = 5;
    public final static byte UNIT_LB_LB = 6;
    //-------温度
    public final static byte UNIT_C= 0;
    public final static byte UNIT_F = 1;


}
