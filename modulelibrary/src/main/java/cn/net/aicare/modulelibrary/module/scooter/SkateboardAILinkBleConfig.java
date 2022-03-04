package cn.net.aicare.modulelibrary.module.scooter;


import com.pingwang.bluetoothlib.annotation.IntDef;
import com.pingwang.bluetoothlib.annotation.Retention;
import com.pingwang.bluetoothlib.annotation.RetentionPolicy;


/**
 * xing<br>
 * 2021/12/18<br>
 * 指令
 */
public class SkateboardAILinkBleConfig {

    /**
     * 接收端类型及支持的功能
     */
    public final static int CMD_DEVICE_TYPE_FEATURES = 0x00;
    /**
     * 心跳包
     */
    public final static int CMD_HEARTBEAT_PACKAGE = 0x01;
    /**
     * 控制器状态
     */
    public final static int CMD_CONTROLLER_STATUS = 0x02;
    /**
     * 清除BM模块参数
     */
    public final static int CMD_RESET_BM_INFO = 0x03;
    /**
     * 设置档位速度
     */
    public final static int CMD_GEAR_SPEED = 0x04;
    /**
     * 设置灯状态
     */
    public final static int CMD_LIGHT_STATUS = 0x05;

    /**
     * 修改密码
     */
    public final static int CMD_CHANGE_PWD = 0x06;

    /**
     * 解锁或者锁车
     */
    public final static int CMD_UNLOCK_OR_LOCK = 0x07;

    /**
     * 售后密码查询
     */
    public final static int CMD_AFTER_SALE_PWD = 0x08;
    /**
     * 背光亮度
     */
    public final static int CMD_BACKGROUND_BRIGHTNESS = 0x09;
    /**
     * 关机
     */
    public final static int CMD_SHUT_DOWN = 0x0A;
    /**
     * 自动关机的时间设置
     */
    public final static int CMD_AUTO_SHUT_DOWN_TIME = 0x0B;
    /**
     * 单次行驶时间
     */
    public final static int CMD_SINGLE_DRIVING_TIME = 0x0C;
    /**
     * 总里程
     */
    public final static int CMD_TOTAL_MILEAGE = 0x0D;
    /**
     * 电池信息
     */
    public final static int CMD_BATTERY_INFO = 0x0E;
    /**
     * 电池厂商代码及编号（预留）
     */
    public final static int CMD_BATTERY_VENDOR = 0x0F;
    /**
     * 控制器厂商代码及编号
     */
    public final static int CMD_CONTROLLER_VENDOR = 0x10;
    /**
     * 仪表固件版本
     */
    public final static int CMD_DIAL_VERSION = 0x11;
    /**
     * BM固件版本
     */
    public final static int CMD_BM_VERSION = 0x12;


    /**
     * 开始表盘OTA
     */
    public final static int CMD_START_DIAL_OTA = 0xA0;
    /**
     * 发送表盘OTA包
     */
    public final static int CMD_SEND_DIAL_OTA = 0xA1;
    /**
     * 发送表盘OTA完成
     */
    public final static int CMD_END_DIAL_OTA = 0xA2;

    /**
     * 开始控制器OTA
     */
    public final static int CMD_START_CONTROLLER_OTA = 0xA3;
    /**
     * 发送控制器OTA包
     */
    public final static int CMD_SEND_CONTROLLER_OTA = 0xA4;
    /**
     * 发送控制器OTA完成
     */
    public final static int CMD_END_CONTROLLER_OTA = 0xA5;


    @IntDef({OTAType.OTA_BOOTLOADER, OTAType.OTA_APPLICATION, OTAType.OTA_FLASH, OTAType.OTA_BLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OTAType {
        int OTA_BOOTLOADER = 0;
        int OTA_APPLICATION = 1;
        int OTA_FLASH = 2;
        int OTA_BLE = 3;
    }

}
