package cn.net.aicare.modulelibrary.module.gasDetector;

/**
 * 气体探测器ble配置
 *
 * @author xing
 * @date 2024/09/04
 */
public class GasDetectorBleConfig {

    public final static int CID = 0x006F;


    /**
     * 实时信息(0x01)
     */
    public final static int TYPE_CURRENT_INFO = 0x01;
    /**
     * CO2二氧化碳报警获取/设置(0x02)
     */
    public final static int TYPE_CO2_ALARM = 0x02;
    /**
     * CO2二氧化碳工作间隔获取/设置(0x03)
     */
    public final static int TYPE_CO2_INTERVAL = 0x03;
    /**
     * CO2二氧化碳开关获取/设置(0x04)
     */
    public final static int TYPE_CO2_SWITCH = 0x04;


    /**
     * CO一氧化碳报警获取/设置(0x05)
     */
    public final static int TYPE_CO_ALARM = 0x05;
    /**
     * CO一氧化碳工作间隔获取/设置(0x06)
     */
    public final static int TYPE_CO_INTERVAL = 0x06;
    /**
     * CO一氧化碳开关获取/设置(0x07)
     */
    public final static int TYPE_CO_SWITCH = 0x07;

    /**
     * O2报警报警获取/设置(0x08)
     */
    public final static int TYPE_O2_ALARM = 0x08;
    /**
     * 离线历史记录间隔获取/设置(0x09)
     */
    public final static int TYPE_HISTORY_INTERVAL = 0x09;
    /**
     * 温度单位获取/设置(0x0A)
     */
    public final static int TYPE_TEMP_UNIT = 0x0A;

    /**
     * 气压单位获取/设置(0x0B)
     */
    public final static int TYPE_AIR_PRESSURE_UNIT = 0x0B;
    /**
     * 设备气压校准(0x0C)
     */
    public final static int TYPE_AIR_PRESSURE_CALIBRATION = 0x0C;
    /**
     * 亮度获取/设置(0x0D)
     */
    public final static int TYPE_BRIGHTNESS_SET = 0x0D;
    /**
     * 声音开关获取/设置(0x0E)
     */
    public final static int TYPE_SOUND_SWITCH_SET = 0x0E;
    /**
     * 震动开关获取/设置(0x0F)
     */
    public final static int TYPE_SHOCK_SWITCH_SET = 0x0F;
    /**
     * 时间同步(0x10)
     */
    public final static int TYPE_SYNC_TIME = 0x10;
    /**
     * 息屏时间获取/设置(0x11)
     */
    public final static int TYPE_SCREEN_OFF_TIME = 0x11;
    /**
     * 关机时间获取/设置(0x12)
     */
    public final static int TYPE_SHUTDOWN_TIME = 0x12;
    /**
     * 离线历史记录 (0x13)
     */
    public final static int TYPE_OFFLINE_HISTORY = 0x13;

    /**
     * 清空离线历史记录 (0x14)
     */
    public final static int TYPE_CLEAR_OFFLINE_HISTORY = 0x14;


    /**
     * 状态获取数据
     */
    public final static int STATUS_GET_DATA = 0x00;
    /**
     * 状态设置数据
     */
    public final static int STATUS_SET_DATA = 0x01;


}
