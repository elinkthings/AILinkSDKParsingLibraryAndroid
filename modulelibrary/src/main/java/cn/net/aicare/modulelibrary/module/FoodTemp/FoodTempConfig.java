package cn.net.aicare.modulelibrary.module.FoodTemp;

public class FoodTempConfig {
    public static final int FOOD_TEMP = 0x2b;

    /**
     * APP请求获取设备信息
     */
    public static final int APP_GET_DEVICE = 0x01;

    /**
     * MCU上发设备信息
     */
    public static final int MCU_DEVICE = 0x02;

    /**
     * MCU上发数据
     */
    public static final int MCU_RESULT = 0x03;

    /**
     * APP设置温度单位
     */
    public static final int APP_SET_TEMP_UNIT = 0x04;

    /**
     * MCU回复设置温度单位结果
     */
    public static final int MCU_SET_TEMP_UNIT_RESULT = 0x05;

    /**
     * 双端请求停止报警
     */
    public static final int STOP_ALERT = 0x06;

    /**
     * 双端回复请求停止报警结果
     */
    public static final int STOP_ALERT_RESULT = 0x07;

    /**
     * APP设置目标温度
     */
    public static final int APP_SET_TARGET_TEMP = 0x08;

    /**
     * MCU回复设置目标温度结果
     */
    public static final int MCU_SET_TARGET_TEMP_RESULT = 0x09;

    /**
     * APP设置定时时间
     */
    public static final int APP_SET_TIMING = 0x0a;

    /**
     * MCU回复设置定时结果
     */
    public static final int MCU_SET_TIMING_RESULT = 0x0b;

    /**
     * 双端开关机
     */
    public static final int OPEN_CLOSE = 0x0c;

    /**
     * 双端回复开关机结果
     */
    public static final int OPEN_CLOSE_RESULT = 0x0d;

    /**
     * APP同步设备时间
     */
    public static final int APP_SYNC_TIME = 0x10;

    /**
     * MCU回复同步设备时间结果
     */
    public static final int MCU_SYNC_TIME_RESULT = 0x11;

    /**
     * APP开启关闭探针
     */
    public static final int APP_OPEN_CLOSE_PROBE = 0x12;

    /**
     * MCU回复开启关闭探针结果
     */
    public static final int MCU_OPEN_CLOSE_PROBE_RESULT = 0x13;
}
