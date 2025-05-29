package cn.net.aicare.modulelibrary.module.bw05;

/**
 * @author ljl
 * on 2024/6/4
 */
public class Bw05WatchCMDConfig {

    public final static String BW_05_START_ID = "IW";
    public final static String BW_05_END_ID = "#";

    public final static String APP_SEND_CMD_ID = "CP";
    public final static String DEVICE_SEND_CMD_ID = "DP";

    //设置时间
    public final static String APP_SET_TIME = "IWCP00,";
    //设置阈值
    public final static String APP_SET_RANGE = "IWCP01,";
    //设置检测间隔时间
    public final static String APP_SET_CHECK_TIME = "IWCP02,";
    //设置蓝牙名称
    public final static String APP_SET_BLE_NAME = "IWCP03,";
    //查询信息
    public final static String APP_QUERY_INFO = "IWCP05";
    //查询阈值
    public final static String APP_QUERY_THRESHOLD = "IWCP06";
    //查询、设置UI主题
    public final static String APP_QUERY_SET_THEME = "IWCP07";
    //查询手环版本号
    public final static String APP_QUERY_VERSION = "IWCP08";
    //设置手环进入绑定模式
    public final static String APP_SET_BINDING_MODE = "IWCP09";
    //APP主动查询硬件状态
    public final static String APP_QUERY_HARDWARE_STATE = "IWCP10";
    //APP设置历史上传间隔
    public final static String APP_SET_HIS_TIME = "IWCP13,";
    //APP回复手环上传信息
    public final static String APP_REPLAY_INFO = "IWDP04";
    //手环回复APP设置历史上传间隔
    public final static String APP_REPLAY_SET_HIS_TIME = "IWDP13";


}
