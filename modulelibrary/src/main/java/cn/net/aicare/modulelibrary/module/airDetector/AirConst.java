package cn.net.aicare.modulelibrary.module.airDetector;

/**
 * 空气检测仪常量定义
 *
 * @author yesp
 */
public class AirConst {
    /**
     * CID
     */
    public static int DEVICE_CID = 0x0053;
    /**
     * CID(NO_MQTT)
     */
    public static final int NO_MQTT_DEVICE_CID = 0x0053;
    /**
     * CID(MQTT)
     */
    public static final int MQTT_DEVICE_CID = 0x0048;

    /**
     * 甲醛
     */
    public static final int AIR_TYPE_FORMALDEHYDE = 0x01;
    /**
     * 温度
     */
    public static final int AIR_TYPE_TEMP = 0x02;
    /**
     * 湿度
     */
    public static final int AIR_TYPE_HUMIDITY = 0x03;
    /**
     * PM2.5
     */
    public static final int AIR_TYPE_PM_2_5 = 0x04;
    /**
     * PM1.0
     */
    public static final int AIR_TYPE_PM_1 = 0x05;
    /**
     * PM10
     */
    public static final int AIR_TYPE_PM_10 = 0x06;
    /**
     * VOC
     */
    public static final int AIR_TYPE_VOC = 0x07;
    /**
     * CO2
     */
    public static final int AIR_TYPE_CO2 = 0x08;
    /**
     * AQI
     */
    public static final int AIR_TYPE_AQI = 0x09;
    /**
     * TVOC
     */
    public static final int AIR_TYPE_TVOC = 0x10;
    /**
     * CO
     */
    public static final int AIR_TYPE_CO = 0x15;
    /**
     * 报警功能
     */
    public static final int AIR_SETTING_WARM = 0x0a;
    /**
     * 音量
     */
    public static final int AIR_SETTING_VOICE = 0x0b;
    /**
     * 报警时长
     */
    public static final int AIR_SETTING_WARM_DURATION = 0x0c;
    /**
     * 报警铃声
     */
    public static final int AIR_SETTING_WARM_VOICE = 0x0d;
    /**
     * 设备故障
     */
    public static final int AIR_SETTING_DEVICE_ERROR = 0x0e;
    /**
     * 设备自检
     */
    public static final int AIR_SETTING_DEVICE_SELF_TEST = 0x0f;
    /**
     * 单位切换
     */
    public static final int AIR_SETTING_SWITCH_TEMP_UNIT = 0x11;
    /**
     * 电池状态
     */
    public static final int AIR_SETTING_BATTEY = 0x12;
    /**
     * 设备绑定
     */
    public static final int AIR_SETTING_BIND_DEVICE = 0x13;
    /**
     * 心跳包
     */
    public static final int AIR_SETTING_HEART = 0x14;
    /**
     * 闹钟功能
     */
    public static final int AIR_ALARM_CLOCK = 0x16;
    /**
     * 恢复出厂设置
     */
    public static final int AIR_RESTORE_FACTORY_SETTINGS = 0x17;
    /**
     * 参数校准
     */
    public static final int AIR_CALIBRATION_PARAMETERS = 0x18;
    /**
     * 时间格式
     */
    public static final int AIR_TIME_FORMAT = 0x19;
    /**
     * 设备亮度
     */
    public static final int AIR_BRIGHTNESS_EQUIPMENT = 0x1A;
    /**
     * 按键音效
     */
    public static final int AIR_KEY_SOUND = 0x1B;
    /**
     * 报警音效
     */
    public static final int AIR_ALARM_SOUND_EFFECT = 0x1C;
    /**
     * 图标显示
     */
    public static final int AIR_ICON_DISPLAY = 0x1D;
    /**
     * 监控显示数据
     */
    public static final int AIR_MONITORING_DISPLAY_DATA = 0x1E;
    /**
     * 数据显示模式
     */
    public static final int AIR_DATA_DISPLAY_MODE = 0x1F;
    /**
     * 协议版本
     */
    public static final int AIR_PROTOCOL_VERSION = 0x80;

    // ***********************************************************  //

    /**
     * 摄氏度
     */
    public static final int UNIT_C = 0;
    /**
     * 华氏度
     */
    public static final int UNIT_F = 1;
}
