package cn.net.aicare.modulelibrary.module.BloodOxygen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class BleBloodOxygenBleConfig {


    /**
     * APP 获取设备状态
     */
    public final static int GETDEVICESTATUS = 0x01;
    /**
     * APP 设置报警值
     */
    public final static int SETAlARM = 0x04;


    /**
     * 上报设备状态
     */
    public final static int DEVICESTATUS = 0x03;

    /**
     * 上报测量结果信息
     */
    public final static int TESTRESULT = 0x03;
    /**
     * 设置结果
     */
    public final static int SETRESULT = 0x05;

    /**
     * 上报测量结果信息
     */
    public final static int ERRORCODE = 0xFF;


    /**
     * 0：血氧饱和率不稳定
     */
    public final static int ErrorCode_Saturation_Rate_Not_Stable = 0x00;
    /**
     * 1：脉率不稳定
     */
    public final static int ErrorCode_Pulse_Rate_Unstable = 0x01;
    /**
     * 2：测量出错
     */
    public final static int ErrorCode_Test_Error = 0x02;
    /**
     * 3：设备低电
     */
    public final static int ErrorCode_Low_Power = 0x03;

    /**
     * 0x00：测量中
     */
    public final static int Test_status_START = 0x00;
    /**
     * 0x01：测量结束
     */
    public final static int Test_status_Finish = 0x01;
    /**
     * 0xFF：测量错误
     */
    public final static int Test_status_ERROR = 0xFF;


    /**
     * 0x00：成功
     */
    public final static int SET_RESULT_SUCCESS = 0x00;
    /**
     * 0x01：失败
     */
    public final static int SET_RESULT_FAIL = 0x01;





}
