package cn.net.aicare.modulelibrary.module.sphygmomanometer;

/**
 * xing<br>
 * 2019/5/11<br>
 * 额温枪指令
 */
public class SphyBleConfig {

    /**
     * 血压计(sphygmomanometer)
     *
     */
    public final static int BLOOD_PRESSURE =0x01;

    /**
     * 稳定血压数据(Stabilize blood pressure data)
     */
    public static final byte SPHY = 0x01;
    /**
     * 实时血压数据(Real-time blood pressure data)
     */
    public static final byte SPHY_NOW = 0x02;
    /**
     * 设置单位(Set unit)
     */
    public static final byte SET_UNIT = (byte) 0x81;

    /**
     * 单位(mmhg)
     */
    public static final byte SPHY_UNIT_MMHG = (byte) 0x00;
    /**
     * 单位(kpa)
     */
    public static final byte SPHY_UNIT_KPA = (byte) 0x01;

    /**
     * 获取单位(Get units)
     */
    public static final byte GET_UNIT = (byte) 0x82;
    /**
     * 血压计互发指令(Sphygmomanometer exchange instructions)
     * 0：开始测量 Start measurement
     * 1：停止测试 Stop testing
     * 2：mcu开机 mcu boot
     * 3：mcu关机 mcu shutdown
     */
    public static final byte SHPY_CMD = (byte) 0x83;
    /**
     * 开始测量(Start measurement)
     */
    public static final byte SHPY_CMD_START = (byte) 0x00;
    /**
     * 停止测量(Stop measuring)
     */
    public static final byte SHPY_CMD_STOP = (byte) 0x01;
    /**
     * 开机(Boot)
     */
    public static final byte SHPY_CMD_MCU_START = (byte) 0x02;
    /**
     * 关机(Shut down)
     */
    public static final byte SHPY_CMD_MCU_STOP = (byte) 0x03;


    /**
     * 错误信息(Error message)
     * 0：未找到高压(High voltage not found)
     * 1：无法正常加压，请检查是否插入袖带，或者重新插拔袖带气管
     * (Unable to pressurize normally, please check whether the cuff is inserted, or re-insert the cuff trachea)
     * 2：电量低(Low battery)
     *
     */
    public static final byte GET_ERR = (byte) 0xFF;


}
