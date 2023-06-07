package cn.net.aicare.modulelibrary.module.meatprobe;

public class FoodConfig {

    /**
     * 食物探针
     */
    public static final int DEVICE_CID = 0x003F;

    /**
     * 摄氏度
     */
    public static final int UNIT_C = 0;

    /**
     * 华氏度
     */
    public static final int UNIT_F = 1;

    /**
     * 没有设置食物类型
     */
    public static final int FOOD_TYPE_NO = 10;

    /**
     * 牛肉
     */
    public static final int FOOD_TYPE_BEEF = 0;

    /**
     * 小牛肉
     */
    public static final int FOOD_TYPE_VEAL = 1;

    /**
     * 羊肉
     */
    public static final int FOOD_TYPE_LAMB = 2;

    /**
     * 猪肉
     */
    public static final int FOOD_TYPE_PORK = 3;

    /**
     * 鸡肉
     */
    public static final int FOOD_TYPE_CHICKEN = 4;

    /**
     * 火鸡肉
     */
    public static final int FOOD_TYPE_TURKEY = 5;

    /**
     * 鱼肉
     */
    public static final int FOOD_TYPE_FISH = 6;

    /**
     * 汉堡
     */
    public static final int FOOD_TYPE_HAMBURGER = 7;

    /**
     * 其它
     */
    public static final int FOOD_TYPE_OTHER = 8;

    /**
     * 食物类型未设置
     */
    public static final int FOOD_TYPE_NO_SETTING = 9;

    /**
     * 食品成熟度 DIY
     */
    public static final int FOOD_DEGREE_DIY = 4;

    /**
     * 三分熟
     */
    public static final int FOOD_DEGREE_MRATE = 0;

    /**
     * 五分熟
     */
    public static final int FOOD_DEGREE_MEDIUM = 1;

    /**
     * 七分熟
     */
    public static final int FOOD_DEGREE_MWELL = 2;

    /**
     * 全熟
     */
    public static final int FOOD_DEGREE_WELL = 3;



    /**
     * 报警 铃声
     */
    public static final int ALERT_TYPE_RING = 0;

    /**
     * 报警 震动
     */
    public static final int ALERT_TYPE_SHOCK = 1;

    /**
     * 报警 铃声和震动
     */
    public static final int ALERT_TYPE_RING_AND_SHOCK = 2;

    /**
     * 烧烤模式 烤炉
     */
    public static final int FOOD_COOK_MODE_OVEN = 0;

    /**
     * 烧烤
     */
    public static final int FOOD_COOK_MODE_BARBECUE = 1;

    /**
     * 正常状态
     */
    public static final int BATTERY_STATE_NORMAL = 0;

    /**
     * 充电中
     */
    public static final int BATTERY_STATE_CHARGING = 1;

    /**
     * 设备低电
     */
    public static final int BATTERY_STATE_LOW = 2;

    //--------------蓝牙状态---------------

    /**
     * 蓝牙未打开
     */
    public static final int BLE_STATE_NOT_OPEN_BLE = 0;

    /**
     * 没有定位权限
     */
    public static final int BLE_STATE_NO_LOCAL_PERMISSION = 1;

    /**
     * 没有打开定位服务
     */
    public static final int BLE_STATE_NOT_OPEN_LOCAL_SERVICE = 2;

    /**
     * 设备扫描中
     */
    public static final int BLE_STATE_SCANNING = 3;

    /**
     * 设备连接中
     */
    public static final int BLE_STATE_CONNECTING = 4;

    /**
     * 设备连接成功
     */
    public static final int BLE_STATE_CONNECT_SUCCESS = 5;

    /**
     * 设备连接失败（超时、133）
     */
    public static final int BLE_STATE_CONNECT_FAIL = 6;

    /**
     * 设备断开连接
     */
    public static final int BLE_STATE_DISCONNECT = 7;

    //----------------

    /**
     * 设置温度单位
     */
    public static final String BROADCAST_SET_TEMP_UNIT = "BROADCAST_SET_TEMP_UNIT";

    /**
     * 温度为这个值就不处理，-16383为0xFFFF的前13个bit
     */
    public static final int DISABLE_TEMP = 0xFFFF;


    /**
     * 设备高温警告(摄氏度)
     */
    public static final int DEVICE_HIGH_TEMP_C = 85;
    /**
     * 设备高温警告(华氏度)
     */
    public static final int DEVICE_HIGH_TEMP_F = 185;

    /**
     * 设备超高温警告(摄氏度)
     */
    public static final int DEVICE_SUPER_HIGH_TEMP_C = 100;
    /**
     * 设备超高温警告(华氏度)
     */
    public static final int DEVICE_SUPER_HIGH_TEMP_F = 212;

}

