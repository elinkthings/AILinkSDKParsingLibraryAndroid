package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

/**
 * 八电极双频体脂秤配置
 *
 * @author xing
 * @date 2024/03/14
 */
public class EightDoubleBodyFatBleConfig {


    /**
     * 双频八电极体脂秤(APP算法)
     */
    public final static int CID_APP = 0x0052;
    /**
     * 双频八电极体脂秤(MCU算法)
     */
    public final static int CID_MCU = 0x0068;


    /**
     * 指令:称量体重
     */
    public static final int CMD_WEIGHING = 0x01;
    /**
     * 实时体重
     */
    public static final int STATUS_WEIGHT_REAL_TIME_WEIGH = 0x01;
    /**
     * 稳定体重
     */
    public static final int STATUS_WEIGHT_STABILIZATION_WEIGHT = 0x02;


    /**
     * 千克
     */
    public final static int STATUS_KG = 0;
    /**
     * 斤
     */
    public final static int STATUS_JIN = 1;
    /**
     * st:lb
     */
    public final static int STATUS_ST = 4;
    /**
     * 纯lb
     */
    public final static int STATUS_LB = 6;


    /**
     * 指令:阻抗测量中
     */
    public static final int CMD_IMPEDANCE = 0x02;


    /**
     * 阻抗测量中
     */
    public static final int STATUS_IMPEDANCE_MEASUREMENT = 0x04;
    /**
     * 阻抗测量失败
     */
    public static final int STATUS_IMPEDANCE_FAILED = 0x06;

    /**
     * 阻抗测量成功
     */
    public static final int STATUS_IMPEDANCE_SUCCESS = 0x07;

    /**
     * 指令:上传阻抗值
     */
    public static final int CMD_IMPEDANCE_DATA = 0x03;


    /**
     * 指令:心率数据
     */
    public static final int CMD_HEART_RATE_DATA = 0x04;


    /**
     * 指令:温度数据
     */
    public static final int CMD_TEMPERATURE_DATA = 0x05;

    /**
     * 指令:请求用户信息
     * MCU 请求用户信息
     */
    public static final int CMD_SYN_USER_DATA = 0x08;

    /**
     * 指令:体脂数据
     * MCU 发送体脂数据
     */
    public static final int CMD_BODY_FAT_DATA = 0x09;

    /**
     * 指令:补全数据
     * MCU 请求补全体脂数据
     */
    public static final int CMD_COMPLETE_DATA = 0x0E;

    /**
     * 测量完成
     */
    public static final int CMD_MEASUREMENT_END = 0x0F;

    /**
     * APP 回复测量完成
     */
    public static final int CMD_MEASUREMENT_END_FOR_APP = 0x84;

    /**
     * 测量错误
     */
    public static final int CMD_MEASUREMENT_ERR = 0xFF;




}
