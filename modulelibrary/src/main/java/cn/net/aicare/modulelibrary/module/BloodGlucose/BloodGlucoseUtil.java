package cn.net.aicare.modulelibrary.module.BloodGlucose;

public class BloodGlucoseUtil {


    /**
     * 血糖仪
     */
    public final static int BLOOD_GLUCOSE=0x1c;
    /**
     * 查询设备状态
     */
    public static final int CHECK_STATUS = 0x01;

    /**
     * MCU上发设备状态
     */
    public static final int DEVICE_STATUS = 0x02;
    /**
     * MCU上发血糖数据结果
     */
    public static final int BLOOD_DATA=0x03;

    /**
     * 设置单位
     */
    public static final int SET_UNIT=0x04;
    /**
     * 设置单位回调
     */
    public static final int SET_UNIT_CALLBACK=0x05;

    /**
     * 错误码
     */
    public static final int ERROR_CODE=0xff;

    /**
     * 设备状态--无状态
     */
    public static final int STATUS_STATELESS = 0x00;//无状态
    /**
     * 设备状态--设备等待插入试纸
     */
    public static final int STATUS_WAIT_TEST_PAPER = 0x01;//设备等待插入试纸
    /**
     * 设备状态--设备已插入试纸，等待获取血样
     */
    public static final int STATUS_WAIT_BLOOD_SAMPLES = 0x02;//设备已插入试纸，等待获取血样
    /**
     * 设备状态--血样已获取，分析血样中
     */
    public static final int STATUS_ANALYSIS_BLOOD_SAMPLES = 0x03;//血样已获取，分析血样中
    /**
     * 设备状态--上发数据完成，测量完成
     */
    public static final int STATUS_TEST_FINISH = 0x04;//上发数据完成，测量完成

    /**
     * 错误码--电池没电
     */
    public static final int ERROR_CODE_NO_ELECTRICITY=0x01;//电池没电
    /**
     * 错误码--已使用过的试纸
     */
    public static final int ERROR_CODE_USED_TEST_PAPER=0x02;//已使用过的试纸
    /**
     * 错误码--环境温度超出使用范围
     */
    public static final int ERROR_CODE_TEMPERATURE_OUT_OF_RANGE=0x03;//环境温度超出使用范围
    /**
     * 错误码--试纸施加血样后测试未完成，被退出试纸
     */
    public static final int ERROR_CODE_WITHDRAWN_TEST_PAPER=0x04;//试纸施加血样后测试未完成，被退出试纸
    /**
     * 错误码--机器自检未通过
     */
    public static final int ERROR_CODE_SELF_CHECK_FAIL=0x05;//机器自检未通过
    /**
     * 错误码--测量结果过低，超出测量范围
     */
    public static final int ERROR_CODE_RESULT_LOWER=0x06;//测量结果过低，超出测量范围
    /**
     * 错误码--测量结果过高，超出测量范围
     */
    public static final int ERROR_CODE_RESULT_OVERTOP=0x07;//测量结果过高，超出测量范围


    /**
     * 成功
     */
    public final static int SUCCESS=0x00;

    /**
     * 失败
     */
    public final static int FAILED=0x01;

    /**
     * 不支持
     */
    public final static int NONSUPPORT=0x02;


    /**
     * 单位--mmol/L
     */
    public final static int UNIT_MMOL_L=0x01;//mmol/L

    /**
     * 单位--mg/dL
     */
    public final static int UNIT_MG_DL=0x02;//mg/dL

}
