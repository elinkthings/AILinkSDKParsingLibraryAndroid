package cn.net.aicare.modulelibrary.module.CoffeeScale;

public class CoffeeScaleConfig {
    /**
     * 咖啡秤
     */
    public static final int COFFEE_SCALE = 0x24;

    /**
     * MCU上发设备信息
     */
    public static final int ON_RESULT = 0x01;

    /**
     * 下发归零指令
     */
    public static final int SET_ZERO = 0x02;

    /**
     * 下发归零指令回调
     */
    public static final int SET_ZERO_CALLBACK = 0x03;

    /**
     * 设置重量单位
     */
    public static final int SET_WEIGHT_UNIT = 0x04;

    /**
     * 设置重量单位回调
     */
    public static final int SET_WEIGHT_UNIT_CALLBACK = 0x05;

    /**
     * 设置温度单位
     */
    public static final int SET_TEMP_UNIT = 0x06;

    /**
     * 设置温度单位回调
     */
    public static final int SET_TEMP_UNIT_CALLBACK = 0x07;

    /**
     * 设置自动关机
     */
    public static final int SET_AUTO_SHUTDOWN = 0x08;

    /**
     * 设置自动关机回调
     */
    public static final int SET_AUTO_SHUTDOWN_CALLBACK = 0x09;

    /**
     * 设置计时
     */
    public static final int SET_TIMING = 0x0a;

    /**
     * 设置计时回调
     */
    public static final int SET_TIMING_CALLBACK = 0x0b;

    /**
     * 设置警报
     */
    public static final int SET_ALERT = 0x0c;

    /**
     * 设置警报回调
     */
    public static final int SET_ALERT_CALLBACK = 0x0d;

    /**
     * 停止警报
     */
    public static final int STOP_ALERT = 0x0e;

    /**
     * 停止警报回调
     */
    public static final int STOP_ALERT_CALLBACK = 0x0f;

    /**
     * MCU上发电量
     */
    public static final int MCU_POWER = 0x10;

    /**
     * 退出，进入冲煮模式
     */
    public static final int BREW_MODE = 0x11;


    /**
     * mcu上报当前重量
     */
    public static final int MCU_WEIGHT = 0x13;
}
