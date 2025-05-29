package cn.net.aicare.modulelibrary.module.gasDetectorPlus;

/**
 * 气体检测仪配置类
 *
 * @author xing
 * @date 2024/09/04
 */
public class GasDetectorPlusBleConfig {

    public final static int CID = 0x0074;


    /**
     * 支持的气体类型(0x01)
     */
    public final static int TYPE_SUPPORTED_GAS = 0x01;
    /**
     * 支持的功能列表(0x02)
     */
    public final static int TYPE_SUPPORTED_FUN = 0x02;
    /**
     * 警报推送开关(0x03)
     */
    public final static int TYPE_ALARM_SWITCH = 0x03;
    /**
     * 报警时长设置(0x04)
     */
    public final static int TYPE_ALARM_TIME = 0x04;

    /**
     * 报警铃声设置(0x05)
     */
    public final static int TYPE_ALARM_RING = 0x05;
    /**
     * 报警铃声试听(0x06)
     */
    public final static int TYPE_ALARM_RING_TEST = 0x06;
    /**
     * 离线数据存储频率(0x07)
     */
    public final static int TYPE_HISTORY_SAVE_FREQUENCY = 0x07;

    /**
     * 数据上报间隔(0x08)
     */
    public final static int TYPE_DATA_REPORT_INTERVAL = 0x08;
    /**
     * 气体信息(0x09)
     */
    public final static int TYPE_GAS_INFO = 0x09;
    /**
     * 报警频率(0x0A)
     */
    public final static int TYPE_ALARM_FREQUENCY = 0x0A;
    /**
     * 报警的气体信息(0x0B)
     */
    public final static int TYPE_ALARM_GAS = 0x0B;


    /**
     * 设备电量信息(0x0C)
     */
    public final static int TYPE_POWER_INFO = 0x0C;

    /**
     * 单位设置(0x0D)
     */
    public final static int TYPE_UNIT_SET = 0x0D;

    /**
     * 同步时间(0x0E)
     */
    public final static int TYPE_SYNC_TIME = 0x0E;

    /**
     * 状态获取数据
     */
    public final static int STATUS_GET_DATA = 0x00;
    /**
     * 状态设置数据
     */
    public final static int STATUS_SET_DATA = 0x01;


    public @interface GasType {
        /**
         * 氧气(%VOL)
         */
        int O2 = 0;
        /**
         * 二氧化碳
         */
        int CO2 = 1;
        /**
         * 总挥发性有机化合物
         */
        int TVOC_PID = 2;
        /**
         * 总挥发性有机化合物
         */
        int TVOC = 3;
        /**
         * 一氧化碳
         */
        int CO = 4;
        /**
         * 硫化氢
         */
        int H2S = 5;
        /**
         * 一氧化氮
         */
        int NO = 6;

        /**
         * 二氧化氮
         */
        int NO2 = 7;
        /**
         * 二氧化硫
         */
        int SO2 = 8;
        /**
         * 氢气
         */
        int H2 = 9;
        /**
         * 臭氧
         */
        int O3 = 10;
        /**
         * 氨气
         */
        int NH3 = 11;
        /**
         * 氰化氢
         */
        int HCN = 12;
        /**
         * 氯化氢
         */
        int HCI = 13;
        /**
         * 过氧化氢
         */
        int H2O2 = 14;
        /**
         * 甲醛
         */
        int CH2O = 15;
        /**
         * 乙烯
         */
        int C2H4 = 16;
        /**
         * 氯气
         */
        int Cl2 = 17;
        /**
         * 二氧化氯
         */
        int ClO2 = 18;
        /**
         * 环氧乙烷
         */
        int C2H4O = 19;
        /**
         * 磷化氢
         */
        int PH3 = 20;
        /**
         * 氟化氢
         */
        int HF = 21;
        /**
         * 氟气
         */
        int F2 = 22;
        /**
         * 四氢噻吩(mg/m³)
         */
        int C4H8S = 23;
        /**
         * 砷化氢
         */
        int ASH3 = 24;
        /**
         * 甲硅烷
         */
        int SIH4 = 25;
        /**
         * 甲硫醇
         */
        int CH3SH = 26;
        /**
         * 甲醇
         */
        int CH4O = 27;
        /**
         * 乙醇
         */
        int C2H6O = 28;
        /**
         * 丁烷/正丁烷
         */
        int C4H = 29;
        /**
         * 异丙醇
         */
        int C3H8O = 30;
        /**
         * 碳酰氟
         */
        int COF2 = 31;
        /**
         * 三氯化硼
         */
        int BF3 = 32;
        /**
         * 甲烷
         */
        int CH4 = 33;
        /**
         * 丙酮
         */
        int C3H6O = 34;
        /**
         * 乙炔
         */
        int C2H2 = 35;
        /**
         * 苯(g/m³)
         */
        int C6H6 = 36;
        /**
         * 己烷
         */
        int C6H14 = 37;
        /**
         * 丙烷
         */
        int C3H8 = 38;
        /**
         * 温度
         */
        int TEMP = 39;
        /**
         * 湿度
         */
        int HUMIDITY = 40;

    }


}
