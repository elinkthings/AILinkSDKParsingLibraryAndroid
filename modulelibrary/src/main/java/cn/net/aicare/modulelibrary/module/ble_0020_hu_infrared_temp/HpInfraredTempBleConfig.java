package cn.net.aicare.modulelibrary.module.ble_0020_hu_infrared_temp;

/**
 * 华普红外测温枪配置
 *
 * @author xing
 * @date 2025/03/27
 */
public class HpInfraredTempBleConfig {
    /**
     * 华普红外测温仪
     */
    public final static int CID = 0x0020;
    /**
     * 获取设备状态
     */
    public static final int SEND_DEVICE_STATUS = 0x01;

    /**
     * 上报设备状态
     */
    public static final int GET_DEVICE_STATUS_1 = 0x02;
    public static final int GET_DEVICE_STATUS_2 = 0x0A;

    /**
     * APP 下发按键指令
     */
    public static final int SEND_KEY_CMD = 0x03;

    /**
     * APP 下发发射率
     */
    public static final int SEND_EMISSIVITY = 0x04;

    /**
     * MCU返回发射率
     */
    public static final int GET_EMISSIVITY = 0x05;

    /**
     * APP 下发自动关机时间
     */
    public static final int SEND_AUTO_SHUTDOWN = 0x06;

    /**
     * MCU返回自动关机指令结果
     */
    public static final int GET_AUTO_SHUTDOWN = 0x07;

    /**
     * APP 下发高低温报警值
     */
    public static final int SEND_WARM_VALUE = 0x08;

    /**
     * MCU返回警报值结果
     */
    public static final int GET_WARM_VALUE = 0x09;


    /**
     * APP 获取设备参数
     */
    public static final int GET_DEVICE_PARAMETER = 0x0A;
    /**
     * mcu返回设备参数
     */
    public static final int DEVICE_PARAMETER = 0x0B;

    //-----------按键相关指令---------
    /**
     * cmd唤醒
     */
    public static final int CMD_WAKEUP = 0x01;
    /**
     * cmd测量
     */
    public static final int CMD_TEST = 0x02;
    /**
     * CMD停止测量
     */
    public static final int CMD_STOP_TEST = 0x03;
    /**
     * CMD重置
     */
    public static final int CMD_RESET = 0x04;
    /**
     * CMD激光灯开关
     */
    public static final int CMD_OPEN_LASER = 0x05;
    /**
     * CMD背光灯开光
     */
    public static final int CMD_CLOSE_LASER = 0x06;
    /**
     * CMD电筒开关
     */
    public static final int CMD_OPEN_TORCH = 0x07;
    /**
     * cmd MODE 按键
     */
    public static final int CMD_MODE = 0x08;
    /**
     * cmd EMS 模式
     */
    public static final int CMD_EMS = 0x09;
    /**
     * cmd UP
     */
    public static final int CMD_UP = 0x0A;
    /**
     * cmd Down
     */
    public static final int CMD_DOWN = 0x0B;
    /**
     *  cmd ℃
     */
    public static final int CMD_C = 0x0C;
    /**
     * cmd ℉
     */
    public static final int CMD_F = 0x0D;

    public static final int TEMP_F = 1;//℉
    public static final int TEMP_C = 0;//℃
    public static final int PLUS = 0;//正
    public static final int MINUS = 1;//负
}
