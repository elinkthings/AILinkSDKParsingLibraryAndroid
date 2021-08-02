package cn.net.aicare.modulelibrary.module.ADWeight;

/**
 * xing<br>
 * 2019/7/11<br>
 * 体脂称配置
 */
public class ADWeightScaleBleConfig {
    /**
     * 体重体脂称(Body fat scale)艾迪
     */
    public final static int WEIGHT_BODY_FAT_SCALE_AD =0x100E;
    /**
     * 实时重量
     */
    public static final byte GET_WEIGHT_NOW = 0x01;
    /**
     * 稳定重量
     */
    public static final byte GET_WEIGHT_STABLE = 0x02;
    /**
     * 温度数据
     */
    public static final byte GET_TEMPERATURE = 0x03;
    /**
     * 测阻抗中
     */
    public static final byte GET_IMPEDANCE_ING = 0x04;
    /**
     * 测阻抗成功，带上阻抗数据
     */
    public static final byte GET_IMPEDANCE_SUCCESS = 0x05;
    /**
     * 测阻抗失败
     */
    public static final byte GET_IMPEDANCE_FAILURE = 0x06;
    /**
     * 测阻抗成功，带上阻抗数据，并使用 APP 算法，APP 会根
     * 据 VID,PID 来识别对应算法
     */
    public static final byte GET_IMPEDANCE_SUCCESS_APP = 0x07;

    /**
     * 同步用户
     */
    public static final byte SYN_USER = 0x08;

    /**
     * 体脂数据
     */
    public static final byte GET_BODY_FAT_DATA = 0x09;
    /**
     * 体脂数据完成
     */
    public static final byte GET_BODY_FAT_DATA_SUCCESS = 0x0A;


    /**
     * 设置单位
     */
    public static final byte SET_UNIT = (byte) 0x81;
    /**
     * 获取单位
     */
    public static final byte GET_UNIT = (byte) 0x82;
    /**
     * 设置去衣模式
     */
    public static final byte SET_UNDRESSING = (byte) 0x83;

    /**
     * 回复去衣模式
     */
    public static final byte GET_UNDRESSING = (byte) 0x84;


    /**
     * 错误指令(超重,重量不稳定等)
     */
    public static final byte GET_ERR = (byte) 0xFF;

    //---------A6,二级指令------

    /**
     * 同步用户
     */
    public static final byte SET_BLE_SYN_USER = 0x01;
    /**
     * 同步用户成功
     */
    public static final byte SET_BLE_SYN_USER_SUCCESS = 0x02;

    /**
     * 同步个人信息
     */
    public static final byte SET_BLE_SYN_USER_DATA = 0x03;
    /**
     * 同步个人信息结果
     */
    public static final byte SET_BLE_SYN_USER_DATA_RETURN = 0x04;
    /**
     * 同步用户历史数据
     */
    public static final byte BLE_SYN_USER_HISTORY_RECORD = 0x05;

    /**
     * 回复用户历史数据
     */
    public static final byte GET_BLE_SYN_USER_HISTORY_RECORD = 0x06;
    /**
     * 阻抗识别用户
     */
    public static final byte BLE_GET_IMPEDANCE = (byte) 0x08;
    //-------重量
    public final static byte WEIGHT_KG = 0;
    /**
     * 斤
     */
    public final static byte WEIGHT_JIN = 1;
    /**
     * LB:OZ
     */
    public final static byte WEIGHT_LB = 2;
    public final static byte WEIGHT_OZ = 3;
    /**
     * ST:LB
     */
    public final static byte WEIGHT_ST = 4;
    public final static byte WEIGHT_G = 5;
    /**
     * LB
     */
    public final static byte WEIGHT_LB_LB = 6;


    //---------------------

//    @IntDef({SEX.MALE, SEX.FEMALE})
//    @Retention(RetentionPolicy.SOURCE)
    public @interface SEX {
        int MALE = 1;
        int FEMALE =0;
    }


//    @IntDef({USER_TYPE.ORDINARY_PERSON, USER_TYPE.AMATEUR_ATHLETE,
//            USER_TYPE.PROFESSIONAL_ATHLETES, USER_TYPE.PREGNANT_WOMAN})
//    @Retention(RetentionPolicy.SOURCE)
    public @interface USER_TYPE {
        int ORDINARY_PERSON = 0;
        int AMATEUR_ATHLETE = 1;
        int PROFESSIONAL_ATHLETES = 2;
        int PREGNANT_WOMAN = 3;
    }

}
