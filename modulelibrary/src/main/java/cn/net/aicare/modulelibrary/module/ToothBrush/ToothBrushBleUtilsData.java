package cn.net.aicare.modulelibrary.module.ToothBrush;


import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDataUtils;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * BLE牙刷解析演示类
 */
public class ToothBrushBleUtilsData extends BaseBleDeviceData implements OnBleOtherDataListener {

    public final static int TOOTHBRUSH_BLE = 0x002D;
    private BleDevice mBleDevice = null;
    private volatile static ToothBrushBleUtilsData toothBrushBleUtilsData = null;


    private ToothBrushBleUtilsData(BleDevice bleDevice, BleToothBrushCallback bleToothBrushCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        appSyncTime();
        this.bleToothBrushCallback = bleToothBrushCallback;
        bleDevice.setOnBleOtherDataListener(this);
        mBleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //蓝牙版本号
                if (bleToothBrushCallback != null) {
                    bleToothBrushCallback.onVersion(version);
                }
            }
        });
        bleDevice.setOnMcuParameterListener(new OnMcuParameterListener() {
            @Override
            public void onMcuBatteryStatus(int status, int battery) {
                if (bleToothBrushCallback != null) {
                    bleToothBrushCallback.onGetBattery(status, battery);
                }
            }
        });
        mBleDevice.setOnBleOtherDataListener(new OnBleOtherDataListener() {
            @Override
            public void onNotifyOtherData(byte[] data) {
            }
        });


    }

    public static ToothBrushBleUtilsData getInstance() {
        return toothBrushBleUtilsData;
    }


    /**
     * 初始化话
     *
     * @param bleDevice   蓝牙操作类
     * @param bleCallback 蓝牙基础回调接口
     */
    public static void init(BleDevice bleDevice, BleToothBrushCallback bleCallback) {
        toothBrushBleUtilsData = null;
        toothBrushBleUtilsData = new ToothBrushBleUtilsData(bleDevice, bleCallback);

    }


    /**
     * 获取到蓝牙设备对象
     *
     * @return 蓝牙设备
     */
    public BleDevice getBleDevice() {
        WeakReference weakReference = new WeakReference(mBleDevice);
        return (BleDevice) weakReference.get();
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] data) {
        byte one = data[0];
        if (one == CmdConfig.SEND_MCU_START) {
            byte sum = 0;
            for (int i = 1; i < data.length - 2; i++) {
                sum += data[i];
            }
            if (sum == data[data.length - 2]) {
                //校验和满足
                int startIndex = 4;
                //有包头,2cid,长度
                int two = data[3] & 0xFF;
                if (data.length >= (two + startIndex)) {
                    int type = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
                    byte[] returnData = new byte[two];
                    System.arraycopy(data, startIndex, returnData, 0, two);
                    onNotifyData(returnData, type);
                }
            }
        }
    }

    @Override
    public void onNotifyData(String uuid, byte[] bytes, int type) {
        try {
            if (bleToothBrushCallback != null) {
                bleToothBrushCallback.onShowData("蓝牙返回的A7: " + BleStrUtils.byte2HexStr(bytes));
            }
            switch (bytes[0]) {
                case ToothBrushBleCmd.GET_TOOTHBRUSH_TIME_GEARS:
                    if (bytes.length >= 5) {
                        int highTime = (bytes[1] & 0xff) << 8;
                        int lowTime = bytes[2] & 0xff;
                        int gears = bytes[3] & 0xff;
                        int gearsfrom = bytes[4] & 0xff;
                        if (bleToothBrushCallback != null) {
                            bleToothBrushCallback.onGetDefaultGearAndDuration(highTime + lowTime, gears, gearsfrom);
                        }
                    }
                    break;
                case ToothBrushBleCmd.SET_TOOTHBRUSH_TIME_GEARS:
                case ToothBrushBleCmd.SET_MANUAL_MODE:
                    if (bytes.length >= 2) {
                        if (bleToothBrushCallback != null) {
                            bleToothBrushCallback.onSetDefaultModeAndManualModeResult(bytes[0], bytes[1] & 0xff);
                        }

                    }
                    break;
                case ToothBrushBleCmd.GET_MANUAL_MODE:
                    if (bytes.length >= 7) {
                        int hzH = (bytes[2] & 0xff) << 8;
                        int hzl = bytes[3] & 0xff;
                        int duty = bytes[4] & 0xff;
                        int timeH = (bytes[5] & 0xff) << 8;
                        int timeL = bytes[6] & 0xff;
                        if (bleToothBrushCallback != null) {
                            bleToothBrushCallback.onGetManualParameter(timeH + timeL, hzH + hzl, duty);
                        }
                    }
                    break;
                case (byte) ToothBrushBleCmd.REPORT_CURRENT_GEARS: {
                    int mode = bytes[1] & 0xff;
                    int gear = bytes[2] & 0xff;
                    int step = bytes[3] & 0xff;
                    if (bleToothBrushCallback != null) {
                        bleToothBrushCallback.onMcuSendCurGear(mode, gear, step);
                    }
                }
                break;
                case (byte) ToothBrushBleCmd.BRUSH_TEETH_TO_COMPLETE:
                    if (bytes.length >= 9) {
                        int mode = bytes[1] & 0xff;
                        int timeH = (bytes[2] & 0xff) << 8;
                        int timeL = bytes[3] & 0xff;
                        int lTimeH = (bytes[4] & 0xff) << 8;
                        int lTimeL = bytes[5] & 0xff;
                        int rTimeH = (bytes[6] & 0xff) << 8;
                        int rTimeL = bytes[7] & 0xff;
                        int battery = bytes[8] & 0xff;
                        if (bleToothBrushCallback != null) {
                            bleToothBrushCallback.onTestFinish(timeH + timeL, lTimeH + lTimeL, rTimeH + rTimeL, mode, battery);
                        }
                    }

                    break;
                case ToothBrushBleCmd.THE_TRIAL_ORDER:
                    //试用指令
                    if (bytes.length >= 2) {
                        if (bleToothBrushCallback != null) {
                            bleToothBrushCallback.onTryOutResult(bytes[1] & 0xff);
                        }
                    }
                    break;
                case ToothBrushBleCmd.GET_SECOND_GEARS:
                    if (bytes.length >= 2) {
                        if (bleToothBrushCallback != null) {
                            bleToothBrushCallback.onTwoLevelModeDefault(bytes[1] & 0xff);
                        }
                    }
                    break;

                case (byte) ToothBrushBleCmd.REPORT_CURRENT_GEARS_BLE:
                    int mode = bytes[1] & 0xff;
                    int gear = bytes[2] & 0xff;
                    int step = bytes[3] & 0xff;
                    int workTime = (bytes[5] & 0xff) + ((bytes[4] & 0xff) << 8);
                    int defaultTime = (bytes[7] & 0xff) + ((bytes[6] & 0xff) << 8);

                    if (bleToothBrushCallback != null) {
                        bleToothBrushCallback.onBleSendCurGear(mode, gear, step, workTime, defaultTime);
                    }
                    break;


                case ToothBrushBleCmd.GET_SECONDARY_CMD:
                    //二级指令
                    boolean support = (bytes[1] & 0xFF) == 0x01;
                    boolean status = (bytes[2] & 0xFF) == 0x01;
                    if (bleToothBrushCallback != null) {
                        bleToothBrushCallback.onPreventSplash(support, status);
                    }
                    break;

                default:
                    break;

            }
        } catch (Exception e) {
            BleLog.e("指令解析异常:"+e.toString()+"  "+BleStrUtils.byte2HexStr(bytes));
            e.printStackTrace();
        }
    }


    @Override
    public void onNotifyDataA6(byte[] hex) {
        try {
            if (bleToothBrushCallback != null) {
                bleToothBrushCallback.onShowData("蓝牙返回的A6: " + BleStrUtils.byte2HexStr(hex));
            }
            switch (hex[0]) {
                case (byte) ToothBrushBleCmd.GET_TOOTHBRUSH_GEARS:
                    disposeSupportGears(hex);
                    break;
                case ToothBrushBleCmd.REQUEST_TOKEN:
                    int result = hex[1] & 0xff;
                    if (bleToothBrushCallback != null) {
                        bleToothBrushCallback.onGetTokenResult(result);
                    }
                    break;
                //牙刷请求专用指令
                case (byte) ToothBrushBleCmd.REQUEST_CODE:
                    if (hex[1] == ToothBrushBleCmd.REQUEST_OFFLINE_HISTORY_NUM) {
                        // MCU回复历史记录条数
                        mcuCallbackOfflineHistoryNum(hex);
                    } else if (hex[1] == ToothBrushBleCmd.MCU_SEND_OFFLINE_HISTORY) {
                        // MCU发送离线历史记录
                        //历史记录每次只会返回一条,需要APP主动请求下一条
                        mcuSendOfflineHistory(hex);
                    } else if (hex[1] == ToothBrushBleCmd.MCU_SEND_OFFLINE_HISTORY_STATE) {
                        int state = hex[2] & 0xff;
                        if (state == 0x00 || state == 0x01) {
                            appRequestClearOfflineHistory();
                        }
                    }
                    break;

                default:

                    break;

            }
        } catch (Exception e) {
            BleLog.e("指令解析异常:"+e.toString()+"  "+BleStrUtils.byte2HexStr(hex));
            e.printStackTrace();
        }
    }


    private void disposeSupportGears(byte[] hex) {
        if (hex.length > 4 && hex[1] == 0x01) {
            int stair = hex[2] & 0xff;
            int secondLevel = hex[3] & 0xff;
            List<Integer> stairs = new ArrayList<>();
            List<Integer> secondLevels = new ArrayList<>();
            if (hex.length >= 4 + stair) {
                for (int i = 0; i < stair; i++) {
                    stairs.add((int) hex[i + 4] & 0xff);
                }
            }
            if (hex.length >= 4 + stair + secondLevel) {
                for (int i = 0; i < secondLevel; i++) {
                    secondLevels.add((int) hex[i + 4 + stair] & 0xff);
                }

            }
            if (bleToothBrushCallback != null) {
                bleToothBrushCallback.onGetSupportGears(stairs, secondLevels);
            }
        }


    }

    private BleToothBrushCallback bleToothBrushCallback;


    public interface BleToothBrushCallback {

        /**
         * 版本号
         * version number
         *
         * @param Version 版本号
         */
       default void onVersion(String Version){}

        /**
         * 牙刷支持的档位
         * Gear supported by toothbrush
         *
         * @param staif       一级档位 First gear
         * @param secondLevel 二级档位 Second gear
         */
        default  void onGetSupportGears(List<Integer> staif, List<Integer> secondLevel){}

        /**
         * 获取电量
         * Get battery
         *
         * @param batteryStatus   电池充电状态  Battery charging status
         *                        0x00：没有充电（默认） 0x00: No charging (default)
         *                        0x01：充电中  0x01: Charging
         *                        0x02：充满电 0x02: Fully charged
         *                        0x03：充电异常 0x03: abnormal charging
         * @param batteryQuantity 电池电量百分比 （0—100） Battery power percentage (0-100)
         */
        default void onGetBattery(int batteryStatus, int batteryQuantity){}

        /**
         * 获得到默认的刷牙档位和时长
         * Get the default brushing position and duration
         *
         * @param time     刷牙时长 Brushing time
         * @param gear     刷牙档位  Brushing gear
         * @param gearFrom 刷牙档位级别  Brushing gear level
         */
        default   void onGetDefaultGearAndDuration(int time, int gear, int gearFrom){}

        /**
         * 请求授权的回调
         * Callback to request authorization
         *
         * @param result 0：没有 1：已经授权 2：不需要授权 3：授权成功 {@link ToothBrushBleCmd#NO_TOKEN}
         */
        default   void onGetTokenResult(int result){}

        /**
         * 获取手动档位的参数
         * Get the parameters of manual gear
         *
         * @param time 时长  duration
         * @param hz   频率  frequency
         * @param duty 占空比 Duty cycle
         */
        default  void onGetManualParameter(int time, int hz, int duty){}


        /**
         * MCU 上报当前工作档位和工作阶段
         *
         * @param mode 0x00：关闭电机
         *             0x01-0xfe：牙刷的模式编码（洁白模式、敏感模式等等）
         *             0xFF：手动设置档位
         * @param gear 属于档位级别：
         *             0x00：不支持级别
         *             0x01：一级档位
         *             0x02：二级档位
         * @param step 工作阶段 1-4，不支持填 0xFF（阶段切换时，牙刷需停止工作
         *             1s，以提示用户切换刷牙方向）
         */
        default  void onMcuSendCurGear(int mode, int gear, int step){}

        /**
         * 设置默认档位和手动模式结果回调
         *
         * @param type   类型 {@link ToothBrushBleCmd#SET_TOOTHBRUSH_TIME_GEARS} And {@link ToothBrushBleCmd#SET_MANUAL_MODE}
         * @param result 0：设置成功 1：设置失败 2：不支持设置
         */
        default void onSetDefaultModeAndManualModeResult(byte type, int result){}

        /**
         * 刷牙完成
         * Brushing is done
         *
         * @param totalTime 刷牙总时长  Total brushing time
         * @param leftTime  左边刷牙时间  Brushing time on the left
         * @param rightTime 右边刷牙时间   Brushing time on the right
         * @param mode      刷牙模式 Brushing mode
         * @param battery   电量 Power
         */
        default void onTestFinish(int totalTime, int leftTime, int rightTime, int mode, int battery){}

        /**
         * 试用回调
         * Trial callback
         *
         * @param result 0：设置成功  0: Setting is successful
         *               1：设置失败，原因未知  1: Setting fails, the reason is unknown
         *               2：不支持设置   2: Setting is not supported
         */
        default  void onTryOutResult(int result){}

        /**
         * 获取二级档位默认值
         * Get the default value of the second gear
         *
         * @param mode 0x00：不支持 0x00: not supported
         *             0x01-0xfe：工作档位编号 0x01-0xfe: working gear number
         *             0xFF：手动设置档位 0xFF: manual setting of gear
         */
        default void onTwoLevelModeDefault(int mode){}


        /**
         * 在祝福发送cur齿轮
         *
         * @param mode        模式：
         *                    0x00：关闭电机
         *                    0x01-0xfe：牙刷的模式编码（洁白模式、敏感模式等等）
         *                    0xFF：手动设置档位
         * @param gear        属于档位级别：
         *                    0x00：不支持级别
         *                    0x01：一级档位
         *                    0x02：二级档位
         * @param step        工作阶段 1-4，不支持填 0xFF（阶段切换时，牙刷需停止工作
         *                    1s，以提示用户切换刷牙方向）
         * @param workTime    已工作时长(单位S)
         * @param defaultTime 默认工作时长（单位 s）
         */
        default void onBleSendCurGear(int mode, int gear, int step, int workTime, int defaultTime){}


        /**
         * 设备返回历史记录条数
         *
         * @param result 结果
         * @param num    数量
         */
        default  void onMcuCallbackOfflineHistoryNum(int result, int num){}


        /**
         * 设备发送的离线历史记录
         *
         * @param createTime  创建时间
         * @param totalTime   总时间 S
         * @param leftTime    左边时间 S
         * @param rightTime   右边时间 S
         * @param mode        模式
         * @param battery     电池
         * @param defaultTime 默认时间 S
         */
        default void onMcuSendOfflineHistory(long createTime, int totalTime, int leftTime, int rightTime, int mode, int battery, int defaultTime){}


        /**
         * 设备返回防飞溅模式
         *
         * @param support 是否支持
         *                true:支持
         *                false:不支持
         * @param status  状态
         *                true:防飞溅已关闭
         *                false:防飞溅已打开
         */
        default   void onPreventSplash(boolean support, boolean status){}

        /**
         * 蓝牙返回的数据
         * Data returned by Bluetooth
         *
         * @param data
         */
        default   void onShowData(String data){}


    }


    /**
     * 获取支持的单位 A6指令
     * Supported unit
     */
    public void getSupportGears() {
        byte[] bytes = new byte[2];
        bytes[0] = ToothBrushBleCmd.GET_TOOTHBRUSH_GEARS;
        bytes[1] = 0x01;
        sendA6(bytes);
    }

    /**
     * 获取到电量
     * Get power
     */
    public void getBattery() {
        sendA6(BleSendCmdUtil.getInstance().getMcuBatteryStatus());
    }

    /**
     * 获取默认模式和时长
     * Get the default mode and duration
     */
    public void getDefaultGearAndDuration() {
        byte[] bytes = new byte[1];
        bytes[0] = ToothBrushBleCmd.GET_TOOTHBRUSH_TIME_GEARS;
        sendA7(bytes);

    }


    /**
     * 设置试用
     * Set up trial
     */
    public void setTryOut(int id, int level, int hz, int duty) {
        byte[] bytes = new byte[14];
        bytes[0] = ToothBrushBleCmd.THE_TRIAL_ORDER;
        bytes[1] = (byte) id;
        bytes[2] = (byte) level;
        bytes[3] = (byte) 0xff;
        bytes[4] = (byte) (hz >> 8);
        bytes[5] = (byte) hz;
        bytes[6] = (byte) duty;
        bytes[7] = (byte) 0;
        bytes[8] = (byte) 0;
        bytes[9] = (byte) 0;
        bytes[10] = (byte) 0;
        bytes[11] = (byte) 0;
        bytes[12] = (byte) 0;
        bytes[13] = (byte) 0;
        sendA7(bytes);


    }

    /**
     * 设置默认
     * Set the default
     */
    public void setDefault(int time, int mode, int level) {
        byte[] bytes = new byte[5];
        bytes[0] = ToothBrushBleCmd.SET_TOOTHBRUSH_TIME_GEARS;
        bytes[1] = (byte) (time >> 8);
        bytes[2] = (byte) time;
        bytes[3] = (byte) mode;
        bytes[4] = (byte) level;
        sendA7(bytes);

    }

    /**
     * 下发数据上报成功
     * Successfully sent data and reported
     */
    public void testFinishSuccess() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ToothBrushBleCmd.Issue_Data_Report_Completed;
        bytes[1] = 0x01;
        sendA7(bytes);

    }

    /**
     * APP 设置二级档位默认值
     * Set the default value of the second gear
     */
    public void setTwoLevelDefault(int modeId) {
        byte[] bytes = new byte[2];
        bytes[0] = ToothBrushBleCmd.Set_Second_GEARS;
        bytes[1] = (byte) modeId;
        sendA7(bytes);

    }

    /**
     * APP 获取二级档位默认值
     */
    public void getTwoLevelDefault() {
        byte[] bytes = new byte[1];
        bytes[0] = ToothBrushBleCmd.GET_SECOND_GEARS;
        sendA7(bytes);
    }


    /**
     * 开始刷牙
     * Get the default value of the second gear
     */
    public void starTooth() {

        byte[] bytes = new byte[1];
        bytes[0] = ToothBrushBleCmd.Start_Close_ToothBrush;
        sendA7(bytes);
    }

    /**
     * 设置手动模式的参数
     * Set the parameters of manual mode
     */
    public void setManualParameter(int hz, int duty, int time) {
        byte[] bytes = new byte[7];
        bytes[0] = ToothBrushBleCmd.SET_MANUAL_MODE;
        bytes[1] = 0x00;
        bytes[2] = (byte) (hz >> 8);
        bytes[3] = (byte) hz;
        bytes[4] = (byte) duty;
        bytes[5] = (byte) (time >> 8);
        bytes[6] = (byte) time;
        sendA7(bytes);
    }


    /**
     * 获取手动模式的参数
     * Get the parameters of manual mode
     */
    public void getManualParameter() {
        byte[] bytes = new byte[1];
        bytes[0] = ToothBrushBleCmd.GET_MANUAL_MODE;

        sendA7(bytes);


    }

    /**
     * 获取防止飞溅模式
     */
    public void getPreventSplash() {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) ToothBrushBleCmd.SET_SECONDARY_CMD;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        sendA7(bytes);
    }

    /**
     * 设置防止飞溅模式
     *
     * @param status 状态
     *               true:打开防飞溅模式
     *               false:关闭防飞溅模式
     */
    public void setPreventSplash(boolean status) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) ToothBrushBleCmd.SET_SECONDARY_CMD;
        bytes[1] = (byte) 0x01;
        bytes[2] = (byte) (status ? 0x01 : 0x00);
        sendA7(bytes);
    }


    /**
     * APP 请求授权 Payload
     * Request authorization
     *
     * @param toothbrushId 时间搓
     */
    public void requestToken(long toothbrushId) {
        //long 长度8 byte  时间搓是6个byte 所以去后面6位
        byte[] timeByte = long2Bytes(toothbrushId);
        byte[] bytes = new byte[7];
        bytes[0] = ToothBrushBleCmd.REQUEST_TOKEN;
        bytes[1] = timeByte[2];
        bytes[2] = timeByte[3];
        bytes[3] = timeByte[4];
        bytes[4] = timeByte[5];
        bytes[5] = timeByte[6];
        bytes[6] = timeByte[7];
        sendA6(bytes);

    }


    /**
     * APP请求获取离线历史记录条数
     */
    public void appRequestOfflineHistoryNum() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ToothBrushBleCmd.REQUEST_CODE;
        bytes[1] = (byte) ToothBrushBleCmd.REQUEST_OFFLINE_HISTORY_NUM;
        sendA6(bytes);
    }


    /**
     * APP请求获取离线历史记录
     */
    public void appRequestReceiveOfflineHistory() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ToothBrushBleCmd.REQUEST_CODE;
        bytes[1] = (byte) ToothBrushBleCmd.REQUEST_RECEIVE_OFFLINE_HISTORY;
        sendA6(bytes);
    }

    /**
     * APP请求取消获取离线历史记录
     */
    public void appRequestCancelOfflineHistory() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ToothBrushBleCmd.REQUEST_CODE;
        bytes[1] = (byte) ToothBrushBleCmd.REQUEST_CANCEL_OFFLINE_HISTORY;
        sendA6(bytes);
    }


    /**
     * APP请求下一条离线历史记录
     * APP请求离线历史记录后,设备只会返回一条,需要APP根据总条数获取一下条
     */
    public void appNextOfflineHistory() {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) ToothBrushBleCmd.REQUEST_CODE;
        bytes[1] = (byte) ToothBrushBleCmd.MCU_SEND_OFFLINE_HISTORY_STATE;
        bytes[2] = 0x02;
        sendA6(bytes);
    }

    /**
     * APP请求清空离线历史记录
     */
    public void appRequestClearOfflineHistory() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ToothBrushBleCmd.REQUEST_CODE;
        bytes[1] = (byte) ToothBrushBleCmd.REQUEST_CLEAR_OFFLINE_HISTORY;
        sendA6(bytes);
    }

    /**
     * APP同步系统时间
     */
    public void appSyncTime() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().setSysTime(BleDataUtils.getInstance().getCurrentTime(), true));
        sendData(sendBleBean);
    }

    /**
     * APP获取设备支持档位
     */
    public void appGetGear() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ToothBrushBleCmd.GET_TOOTHBRUSH_GEARS;
        bytes[1] = (byte) 0x01;
        sendA6(bytes);
    }


    /**
     * MCU回复请求离线历史记录条数结果
     *
     * @param hex hex
     */
    public void mcuCallbackOfflineHistoryNum(byte[] hex) {
        int result = hex[2] & 0xff;
        int num = hex[3] & 0xff;
        if (bleToothBrushCallback != null) {
            bleToothBrushCallback.onMcuCallbackOfflineHistoryNum(result, num);
        }
    }


    /**
     * MCU发送离线历史记录
     *
     * @param hex hex
     */
    public void mcuSendOfflineHistory(byte[] hex) {
        int year = (hex[2] & 0xff) + 2000;
        int month = hex[3] & 0xff;
        int day = hex[4] & 0xff;
        int hour = hex[5] & 0xff;
        int minute = hex[6] & 0xff;
        int second = hex[7] & 0xff;
        int mode = hex[8] & 0xff;

        int totalTime = ((hex[9] & 0xff) << 8) | (hex[10] & 0xff);
        int leftTime = ((hex[11] & 0xff) << 8) | (hex[12] & 0xff);
        int rightTime = ((hex[13] & 0xff) << 8) | (hex[14] & 0xff);
        int battery = hex[15] & 0xff;
        int defaultTime = -1;
        if (hex.length >= 18) {
            defaultTime = ((hex[16] & 0xff) << 8) + (hex[17] & 0xff);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        long createTime = calendar.getTimeInMillis();
        if (bleToothBrushCallback != null) {
            bleToothBrushCallback.onMcuSendOfflineHistory(createTime, totalTime, leftTime, rightTime, mode, battery, defaultTime);
        }
    }


    private byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }


    /**
     * 发送A6指令
     *
     * @param bytes
     */
    public void sendA6(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
    }


    /**
     * 发送A7指令
     *
     * @param bytes
     */
    public void sendA7(byte[] bytes) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(TOOTHBRUSH_BLE, bytes);
        sendData(sendMcuBean);
    }
}
