package cn.net.aicare.modulelibrary.module.broadcastheight;

/**
 * @author ljl
 * on 2023/6/14
 */
public class BroadCastHeightConfig {

    /**
     * 广播身高仪CID
     */
    public final static int BROAD_CAST_HEIGHT = 0x03;

    /**
     * 正在测量
     */
    public final static int MEASURING = 0x00;

    /**
     * 稳定身高体重
     */
    public final static int MEASURING_STABLE = 0x01;

    /**
     * 测量失败
     */
    public final static int MEASURING_FAILED = 0xFF;



    /*-----------------身高单位----------------*/

    /**
     * 厘米
     */
    public final static int UNIT_CM = 0;
    /**
     * 英寸
     */
    public final static int UNIT_INCH = 1;
    /**
     * 英尺-英寸
     */
    public final static int UNIT_FT_IN = 2;

    /*-----------------重量单位----------------*/

    /**
     * 千克
     */
    public final static byte UNIT_KG = 0;
    /**
     * 斤
     */
    public final static byte UNIT_JIN = 1;
    /**
     * 磅:盎司
     */
    public final static byte UNIT_LB_OZ = 2;
    /**
     * 盎司
     */
    public final static byte UNIT_OZ = 3;
    /**
     * 英石:磅
     */
    public final static byte UNIT_ST_LB = 4;
    /**
     * 克
     */
    public final static byte UNIT_G = 5;
    /**
     * 磅
     */
    public final static byte UNIT_LB = 6;
}
