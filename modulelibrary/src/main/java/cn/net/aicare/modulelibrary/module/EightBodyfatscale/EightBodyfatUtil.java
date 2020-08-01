package cn.net.aicare.modulelibrary.module.EightBodyfatscale;

public class EightBodyfatUtil {


    /**
     * 称量体重
     */
    public static final  int WEIGHING=0x01;
    /**
     * 实时体重
     */
    public static final int  WEIGHT_REAL_TIME_WEIGH=0x01;
    /**
     * 稳定体重
     */
    public static final int WEIGHT_STABILIZATION_WEIGHT=0x02;

    /**
     *测量阻抗
     */
    public static final int  IMPEDANCE=0x02;


    /**
     * 阻抗测量中
     */
    public static final int IMPEDANCE_MEASUREMENT=0x01;
    /**
     * 阻抗测量失败
     */
    public static final int IMPEDANCE_FAILED=0x02;

    /**
     * 阻抗测量成功
     */
    public static final int IMPEDANCE_SUCCESS=0x03;
    /**
     * 阻抗测量结束
     */
    public static final int IMPEDANCE_FINISH=0x04;

    /**
     * 测量心率
     */
    public static final int HEARTRATE=0x03;

    /**
     * 心率测量中
     */
    public static final int HEARTRATE_MEASUREMENT=0x01;

    /**
     * 心率测量成功
     */
    public static final int HEARTRATE_SUCCESS=0x02;

    /**
     * 心率测量失败
     */
    public static final int HEARTRATE_FAILED=0x03;



    /**
     * 温度数据
     */

    public static final int TEMP_MEASUREMENT=0x04;

    /**
     * 测量完成
     */
    public static final int MEASUREMENTED=0x0f;

    /**
     * MCU回复操作结果
     */
    public static final int MUCCALLBACK_RESULT=0x82;

    /**
     * APP下发命令
     */
    public static final int APP_CMD=0x81;


    /**
     * 校准
     */
    public static final int APPCMD_CALIIBRATION=0x01;
    /**
     * 切换温度单位
     */
    public static final int APPCMD_TEMP_UNIT=0x02;
    /**
     * 切换体重单位
     */
    public static final int APPCMD_WEIGHT_UNIT=0x03;

    /**
     * MCU回复操作结果
     */
    public static final int ERROR_CODE=0xFF;


    /**
     * 千克
     */
    public final static int KG = 0;
    /**
     * 斤
     */
    public final static int JIN = 1;
    /**
     * st:lb
     */
    public final static int ST = 4;
    /**
     * 纯lb
     */
    public final static int LB = 6;
    public final static int C = 0x00;
    public final static int F = 0x01;

    public final static int SUCCESS=0x00;

    public final static int FAILED=0x01;

    public final static int DOING=0x02;



    public final static int IMPEDANCE_FOOT         =0x00; // 双脚阻抗
    public final static int IMPEDANCE_HAND         =0x01; // 双手阻抗
    public final static int IMPEDANCE_BODY         =0x0A; // 躯干阻抗
    public final static int IMPEDANCE_L_HAND       =0x02; // 左手阻抗
    public final static int IMPEDANCE_R_HAND       =0x03; // 右手阻抗
    public final static int IMPEDANCE_L_FOOT       =0x04; // 左脚阻抗
    public final static int IMPEDANCE_R_FOOT       =0x05; // 右脚阻抗
    public final static int IMPEDANCE_L_BODY       =0x06; // 左全身阻抗
    public final static int IMPEDANCE_R_BODY       =0x07; // 右全身阻抗
    public final static int IMPEDANCE_R_HAND_L_FOOT=0x08; // 右手左脚阻抗
    public final static int IMPEDANCE_L_HAND_R_FOOT=0x09; // 左手右脚阻抗
    public  static String lbtostlb(float lb){

        int st = (int) lb / 14;
        float mlb = lb % 14f;
        return st+":"+mlb;
    }
}
