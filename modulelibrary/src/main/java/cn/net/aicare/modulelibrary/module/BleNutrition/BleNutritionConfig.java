package cn.net.aicare.modulelibrary.module.BleNutrition;

/**
 * 蓝牙营养秤
 */
public class BleNutritionConfig {

    /**
     * CID
     */
    public static final int BLE_NUTRITION = 0x0034;

    /**
     * MCU 上报重量
     */
    public static final int MCU_WEIGHT = 0x01;

    /**
     * MCU 上报单位结果
     */
    public static final int MCU_UNIT_RESULT = 0x03;

    /**
     * MCU 上报异常报警
     */
    public static final int MCU_ERR = 0x05;

    /**
     * APP 下发单位
     */
    public static final int SET_UNIT = 0x02;

    /**
     * APP 下发归零 / 去皮
     */
    public static final int SET_ZERO = 0x04;

}
