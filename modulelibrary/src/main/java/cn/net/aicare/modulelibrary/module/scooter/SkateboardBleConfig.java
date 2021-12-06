package cn.net.aicare.modulelibrary.module.scooter;

import java.util.UUID;


/**
 * xing<br>
 * 2019/12/11<br>
 * 指令
 */
public class SkateboardBleConfig {


    public final static byte PREFIX= (byte) 0x5A;
    /**
     * 写入主机写应答的仪表
     */
    public final static byte HOST_WRITE_RETURN_METER= (byte) 0x23;
    /**
     * 写入主机应答的仪表
     */
    public final static byte HOST_RETURN_METER= (byte) 0x33;
    /**
     * 主机只读仪表
     */
    public final static byte HOST_READ_ONLY_METER= (byte) 0x03;
    /**
     * 写入主机写应答的电池
     */
    public final static byte HOST_WRITE_RETURN_BATTERY= (byte) 0x22;
    /**
     * 主机只读电池
     */
    public final static byte HOST_READ_ONLY_BATTERY= (byte) 0x02;
    /**
     * 写入主机写应答的控制器
     */
    public final static byte HOST_WRITE_RETURN_CONTROLLER= (byte) 0x21;
    /**
     * 主机应答的控制器
     */
    public final static byte HOST_WRITE_RETURN_CONTROLLER_ANSWER= (byte) 0x31;
    /**
     * 主机只读控制器
     */
    public final static byte HOST_READ_ONLY_CONTROLLER= (byte) 0x01;
//    @IntDef({HOST_WRITE_RETURN_METER, HOST_RETURN_METER,HOST_READ_ONLY_METER, HOST_WRITE_RETURN_BATTERY,HOST_READ_ONLY_BATTERY,HOST_WRITE_RETURN_CONTROLLER,HOST_READ_ONLY_CONTROLLER})
//    @Retention(RetentionPolicy.SOURCE)
    public @interface ScopeType {

    }
    /**
     * 服务的uuid
     */
    public final static UUID UUID_BROADCAST = UUID.fromString("0000FFE0-3C17-D293-8E48-14FE2E4DA212");

    /**
     * write
     */
    public final static UUID UUID_WRITE = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");

    /**
     * Notify
     */
    public final static UUID UUID_NOTIFY = UUID.fromString("0000FFE2-0000-1000-8000-00805F9B34FB");


//    @IntDef({OTAType.OTA_BOOTLOADER, OTAType.OTA_APPLICATION, OTAType.OTA_FLASH, OTAType.OTA_BLE})
//    @Retention(RetentionPolicy.SOURCE)
    public @interface OTAType {
        int OTA_BOOTLOADER = 0;
        int OTA_APPLICATION = 1;
        int OTA_FLASH = 2;
        int OTA_BLE = 3;
    }

}
