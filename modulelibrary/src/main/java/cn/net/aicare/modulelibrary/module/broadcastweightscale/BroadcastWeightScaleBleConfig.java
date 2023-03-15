package cn.net.aicare.modulelibrary.module.broadcastweightscale;

/**
 * @auther ljl
 * on 2023/3/10
 */
public class BroadcastWeightScaleBleConfig {

    /**
     * 广播体重秤
     */
    public final static int BROADCAST_WEIGHT_SCALE =0x07;

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
    public static final int GET_TEST_FINISH = 0xFF;



    //-------重量
    public final static byte UNIT_KG = 0;
    /**
     * 斤
     */
    public final static byte UNIT_FG = 1;
//    public final static byte UNIT_LB = 2;
//    public final static byte UNIT_OZ = 3;
    public final static byte UNIT_ST = 4;
//    public final static byte UNIT_G = 5;
    public final static byte UNIT_LB_LB = 6;
    //-------温度
    public final static byte UNIT_C= 0;
    public final static byte UNIT_F = 1;

}
