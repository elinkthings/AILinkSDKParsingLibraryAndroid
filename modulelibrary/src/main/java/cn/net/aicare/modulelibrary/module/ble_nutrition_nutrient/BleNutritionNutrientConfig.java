package cn.net.aicare.modulelibrary.module.ble_nutrition_nutrient;

/**
 * 蓝牙营养秤
 */
public class BleNutritionNutrientConfig {

    /**
     * 支持下发营养元素的营养秤
     */
    public static final int CID = 0x006C;

    /**
     * MCU 上报重量
     */
    public static final int MCU_WEIGHT = 0x01;

    /**
     * MCU 上报单位结果
     */
    public static final int MCU_UNIT_RESULT = 0x03;


    /**
     * APP 下发单位
     */
    public static final int SET_UNIT = 0x02;

    /**
     * APP 下发归零 / 去皮
     */
    public static final int SET_ZERO = 0x04;


    /**
     * MCU 上报异常报警
     */
    public static final int MCU_ERR = 0x05;

    /**
     * 下发营养
     */
    public static final int SET_NUTRIENT = 0x06;

}
