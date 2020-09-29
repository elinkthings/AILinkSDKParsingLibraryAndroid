package cn.net.aicare.modulelibrary.module.tpms;

/**
 * xing<br>
 * 2019/7/3<br>
 * tpms配置
 */
public class TpmsBleConfig {
    /**
     * 连接类型的TPMS(Connection type TPMS)
     */
    public final static int TPMS_CONN_DEVICE =0x0D;

    /**
     * 设置单位
     */
    public static final byte SET_UNIT = (byte) 0x81;
    /**
     * 获取单位
     */
    public static final byte GET_UNIT = (byte) 0x82;
    /**
     * app 设置胎压阀值
     */
    public static final byte SET_TPMS_THRESHOLD = (byte) 0x83;
    /**
     * 气压阀值结果
     */
    public static final byte GET_TPMS_THRESHOLD = (byte) 0x84;
    /**
     * 设置温度阀值
     */
    public static final byte SET_TPMS_TEMP = (byte) 0x85;
    /**
     * 设置温度阀值返回的结果
     */
    public static final byte GET_TPMS_TEMP = (byte) 0x86;
    /**
     * 设置报警音开关
     */
    public static final byte SET_TPMS_ALARM = (byte) 0x87;
    /**
     * 设置报警音开关的结果
     */
    public static final byte GET_TPMS_ALARM = (byte) 0x88;

    /**
     * 上传胎压状态
     */
    public static final byte GET_TPMS_STATUS = (byte) 0x01;
    /**
     * 主动读取胎压信息
     */
    public static final byte GET_TPMS_INFO = (byte) 0x02;
    /**
     * 错误指令
     */
    public static final byte GET_ERR = (byte) 0xFF;


    public static final int TPMS_STATUS_LEAK = 1;

    public static final int PRESSURE_KPA = 0;
    public static final int PRESSURE_PSI = 1;
    public static final int PRESSURE_BAR = 2;

    public static final int TEM_C = 0;
    public static final int TEM_F = 1;

    public static final float TPMS_PRESSURE_LOWER = 2;
    public static final float TPMS_PRESSURE_UPPER = 3;
    public static final int TPMS_TEMP_UPPER = 65;

}
