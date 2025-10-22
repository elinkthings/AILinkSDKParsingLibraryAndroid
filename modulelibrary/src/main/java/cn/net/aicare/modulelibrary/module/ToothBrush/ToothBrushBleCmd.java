package cn.net.aicare.modulelibrary.module.ToothBrush;

public class ToothBrushBleCmd {


    public final static int CID=0x0012;

    /**
     * 获取牙刷支持的档位
     * Get the gear supported by the toothbrush
     */
    public final static int GET_TOOTHBRUSH_GEARS=0x36;

    /**
     * 获取设备电量
     * Get device battery
     */
    public final static int Get_battery=0x28;

    /**
     *请求授权
     * Request authorization
     */
    public final static int REQUEST_TOKEN=0x7F;

    /**
     * 牙刷请求专用指令
     */
    public final static int REQUEST_CODE = 0xC0;

    //===================================A7=================================
    /**
     * 设置默认刷牙时长和工作档位
     * Set the default brushing time and working gear
     */
    public final static int SET_TOOTHBRUSH_TIME_GEARS = 0x02;


    /**
     * 获取默认刷牙时长和工作档位
     * Get the default brushing time and working gear
     */
    public final static int GET_TOOTHBRUSH_TIME_GEARS = 0x03;


    /**
     * APP 试用指令
     *APP trial instruction
     */

    public final static int THE_TRIAL_ORDER = 0x06;


    /**
     * MCU 上报当前工作档位和工作阶段
     * MCU reports the current working gear and working stage
     */
    public final static int REPORT_CURRENT_GEARS = 0x07;


    /**
     * APP 设置手动设置（自定义）档位
     *APP set manual setting (custom) gear
     */

    public final static int SET_MANUAL_MODE = 0x09;

    /**
     * APP 获取手动设置（自定义）档位
     * APP get manual setting (custom) gear
     */
    public final static int GET_MANUAL_MODE = 0x0A;


    /**
     * APP 启动/关闭牙刷
     * APP turn on/off toothbrush
     */
    public final static int Start_Close_ToothBrush=0x0b;

    /**
     * APP 设置二级档位默认值
     * APP sets the default value of the second gear
     */
    public final static int Set_Second_GEARS=0x0C;


    /**
     *APP 获取二级档位默认值
     * APP gets the default value of the second gear
     */
    public  final static int GET_SECOND_GEARS =0x0D;

    public final static byte GET_API_KEY = 0x0E;
    public final static byte SET_API_KEY = 0x0F;

    /**
     * APP 设置二级指令标记
     * APP Secondary instruction mark
     */
    public final static byte SET_SECONDARY_CMD = 0x10;
    /**
     * 获取二级指令标记
     */
    public final static byte GET_SECONDARY_CMD = 0x11;


    /**
     * app 获取刷牙最终结果
     */
    public final static int REPORT_CURRENT_GEARS_BLE=0xFC;

    /**
     * APP 下发数据上报完成
     * APP issued data report completed
     */
    public  final static int Issue_Data_Report_Completed=0xFE;
    /**
     * app 获取刷牙最终结果
     * app gets the final result of brushing
     */
    public final static int BRUSH_TEETH_TO_COMPLETE =0xFD;

    /**
     *     0：没有授权
     *     0: No authorization
     */
    public final static int NO_TOKEN=0;
    /**
     * 1：已经授权
     * 1: Authorized
     */
    public final static int HAS_TOKEN=1;
    /**
     * 2：不需要授权
     * 2: No authorization required
     */
    public final static int WITHOUT_TOKEN=2;
    /**
     * 3：授权成功
     * 3: Authorization is successful
     */
    public final static int SUCCESSTOKEN=3;
    /**
     * 设置类返回状态
     * 设置wifi的mac，密码 和设置的单位 和设置时间
     * 00 成功 01 失败 02 不支持
     * <p>
     * Set class return status
     * Set wifi mac, password, set unit and set time
     * 00 Success 01 Failure 02 Not supported
     */
    public final static int STATUS_SUCCESS = 0x00;
    public final static int STATUS_FAIL = 0x01;
    public final static int STATUS_NOSUPORT = 0x02;

//-----------历史记录================


    /**
     * APP获取离线历史记录条数
     */
    public final static int REQUEST_OFFLINE_HISTORY_NUM = 0x11;

    /**
     * APP请求接收离线历史记录
     */
    public final static int REQUEST_RECEIVE_OFFLINE_HISTORY = 0x12;

    /**
     * APP请求取消接收离线历史记录
     */
    public final static int REQUEST_CANCEL_OFFLINE_HISTORY = 0x13;

    /**
     * APP请求清空离线历史记录
     */
    public final static int REQUEST_CLEAR_OFFLINE_HISTORY = 0x14;

    /**
     * MCU发送离线历史记录
     */
    public final static int MCU_SEND_OFFLINE_HISTORY = 0x15;

    /**
     *
     */
    public final static int MCU_SEND_OFFLINE_HISTORY_STATE = 0x16;





}
