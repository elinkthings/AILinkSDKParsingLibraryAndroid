package cn.net.aicare.modulelibrary.module.findDevice;

/**
 * xing<br>
 * 2021/03/24<br>
 * 寻物器
 */
public class FindBleConfig {


    /**
     * 寻物器
     */
    public final static int FIND_DEVICE = 0x0900;

    /**
     * 获取连接的设备个数
     */
    public final static int GET_CONNECT_NUMBER = 0x4A;

    /**
     * 获取连接的设备信息列表
     */
    public final static int GET_CONNECT_INFO_LIST = 0x4B;
    /**
     * 获取连接的设备信息列表发送完成
     */
    public final static int GET_CONNECT_INFO_LIST_END = 0x4C;

    /**
     * 当前连接的设备状态(主动上报)
     */
    public final static int GET_CONNECT_INFO_STATUS = 0x4D;


    /**
     * 发送消息给当前连接的设备
     */
    public final static int SET_CONNECT_DEVICE_DATA = 0x4E;

    /**
     * 扫描附近的设备
     */
    public final static int SET_NEARBY_DEVICE = 0x29;


    /**
     * 获取附近的设备
     */
    public final static int GET_NEARBY_DEVICE = 0x30;


    /**
     * 设置BLE连接
     */
    public final static int SET_CONNECT_DEVICE = 0x39;
    /**
     * 设备BLE断开连接从机
     */
    public final static int SET_DISCONNECT_DEVICE = 0x15;

}
