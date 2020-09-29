package cn.net.aicare.modulelibrary.module.SmartMask;

/**
 * xing<br>
 * 2020/09/15<br>
 * 智能口罩指令
 */
public class SmartMaskBleConfig {


    /**
     * 智能口罩
     */
    public final static int SMART_MASK =0x0022;

    /**
     * 获取设备状态
     */
    public static final byte SET_STATUS = 0x01;
    /**
     * 设备上发数据状态
     */
    public static final byte GET_STATUS = 0x02;
    /**
     * 更换滤网指令
     */
    public static final byte SET_FILTER = 0x03;
    /**
     * 设备回复滤网指令
     */
    public static final byte GET_FILTER = 0x04;
    /**
     * 风扇控制指令
     */
    public static final byte SET_FAN = 0x05;
    /**
     * 设备回复风扇指令
     */
    public static final byte GET_FAN = 0x06;


}
