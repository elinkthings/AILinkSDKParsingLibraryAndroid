package cn.net.aicare.modulelibrary.module.wifi;

public class WifiConfig {
    /**
     * SSID 为空或key 为空
     */
    public final static int  SSID_KEY_NULL=-1;
    /**
     * key不合法
     */
    public final static int  KEY_ILLEGAL=0;

    /**
     * 异常错误：生成Information 出现异常
     */
    public final static int ERROR_EXCEPTIONAL=1;
    /**
     * 生成Information 成功
     */
    public final static int INFORMATION_SUCCESSFUL=2;

    /**
     * 连接上设备
     */
    public final static int DEVICE_CONNECTION_SUCCESSFUL=3;
    /**
     * 开始发送数据
     */
    public final static int START_SEND_DATA=4;
    /**
     * 开始发送数据出现错误
     */
    public final static int ERROR_START_SEND_DATA=5;

    /**
     * 开始接收数据
     */
    public final static int START_RECEIVE_DATA=6;
    /**
     * 开始接收数据出现错误
     */
    public final static int ERROR_START_RECEIVE_DATA=7;
    /**
     * 千克
     */
    public final static int KG = 0;
    /**
     * 斤
     */
    public final static int JIN = 1;
    /**
     * st:lb
     */
    public final static int ST = 4;
    /**
     * 纯lb
     */
    public final static int LB = 6;

}
