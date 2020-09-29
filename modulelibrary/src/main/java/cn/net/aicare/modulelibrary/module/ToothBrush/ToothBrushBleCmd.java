package cn.net.aicare.modulelibrary.module.ToothBrush;

public class ToothBrushBleCmd {

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
    //===================================A7
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

    public final static int The_Trial_Order = 0x06;


    /**
     * MCU 上报当前工作档位和工作阶段
     * MCU reports the current working gear and working stage
     */
    public final static int Report_Current_GEARS = 0x07;


    /**
     * APP 设置手动设置（自定义）档位
     *APP set manual setting (custom) gear
     */

    public final static int Set_Manual_Mode = 0x09;

    /**
     * APP 获取手动设置（自定义）档位
     * APP get manual setting (custom) gear
     */
    public final static int Get_Manual_Mode = 0x0A;

    /**
     * APP 启动/关闭牙刷
     * APP turn on/off toothbrush
     */
    public final static int Start_Close_ToothBrush=0x0b;

    /**
     * APP 设置二级档位默认值
     * APP sets the default value of the second gear
     */
    public final static int Set_Second_GEARS=0x0c;


    /**
     *APP 获取二级档位默认值
     * APP gets the default value of the second gear
     */
    public  final static int Get_Second_GEARS=0x0D;

    /**
     * APP 下发数据上报完成
     * APP issued data report completed
     */
    public  final static int Issue_Data_Report_Completed=0xFE;
    /**
     * app 获取刷牙最终结果
     * app gets the final result of brushing
     */
    public final static int Brush_Teeth_to_Complete=0xfd;

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
}
