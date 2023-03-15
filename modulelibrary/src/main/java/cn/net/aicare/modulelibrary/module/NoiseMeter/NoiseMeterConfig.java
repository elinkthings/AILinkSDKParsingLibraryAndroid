package cn.net.aicare.modulelibrary.module.NoiseMeter;

/**
 * @author xing<br>
 * @date 2022/11/15<br>
 * 噪音计 指令配置
 */
public class NoiseMeterConfig {


    /**
     * 获取
     */
    public final static int TYPE_OP_GET = 0x01;
    /**
     * 设置
     */
    public final static int TYPE_OP_SET = 0x02;

    //--------------分组


    /**
     * 请求获取设备支持的功能列表
     */
    public final static int GROUP_SET_SUPPORT_LIST =0x01;
    /**
     * 设备返回功能
     */
    public final static int GROUP_GET_SUPPORT_LIST =0x02;
    /**
     * 请求获取设备状态
     */
    public final static int GROUP_SET_DEVICE_STATE =0x03;
    /**
     * 设备状态
     */
    public final static int GROUP_GET_DEVICE_STATE =0x04;

    /**
     * 设置参数功能
     */
    public final static int GROUP_SET_PARAMETERS =0x05;
    /**
     * 获取参数功能
     */
    public final static int GROUP_GET_PARAMETERS =0x06;


    //-------------------功能


    public final static int TYPE_PROTOCOL_VERSION = 0x80;
    /**
     * 频率计权A/C
     */
    public final static int TYPE_FREQUENCY_AC = 0x01;
    /**
     * 测量总范围
     */
    public final static int TYPE_TEST_RANGE = 0x02;
    /**
     * 测量等级切换
     */
    public final static int TYPE_TEST_LEVEL = 0x03;
    /**
     * Max/Min模式
     */
    public final static int TYPE_MAX_MIN = 0x04;
    /**
     * 时间加权(Fast/Slow)
     */
    public final static int TYPE_TIME_FAT_SLOW = 0x05;
    /**
     * 数值保持(hold)
     */
    public final static int TYPE_VALUE_HOLD = 0x06;
    /**
     * 报警
     */
    public final static int TYPE_WARM = 0x07;
    /**
     * 背光
     */
    public final static int TYPE_BACK_LIGHT = 0x08;
    /**
     * 噪音值.
     */
    public final static int TYPE_NOISE_VALUE = 0x09;
    /**
     * 历史
     */
    public final static int TYPE_HISTORY = 0x0a;
    /**
     * 供电
     */
    public final static int TYPE_POWERED_BY = 0x0b;
    /**
     * 绑定设备
     */
    public final static int TYPE_BIND_DEVICE = 0x0c;
}
