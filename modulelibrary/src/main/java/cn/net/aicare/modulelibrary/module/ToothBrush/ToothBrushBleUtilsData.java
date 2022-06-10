package cn.net.aicare.modulelibrary.module.ToothBrush;


import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class ToothBrushBleUtilsData extends BaseBleDeviceData {

    public final static int TOOTHBRUSH_BLE = 0x2d;
    private BleDevice mBleDevice = null;
    private volatile static ToothBrushBleUtilsData toothBrushBleUtilsData = null;


    private ToothBrushBleUtilsData(BleDevice bleDevice, BleToothBrushCallback bleToothBrushCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        this.bleToothBrushCallback = bleToothBrushCallback;


        mBleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //蓝牙版本号
                if (bleToothBrushCallback != null)
                    bleToothBrushCallback.onVersion(version);
            }
        });
        bleDevice.setOnMcuParameterListener(new OnMcuParameterListener() {
            @Override
            public void onMcuBatteryStatus(int status, int battery) {
                if (bleToothBrushCallback != null)
                    bleToothBrushCallback.onGetBattery(status, battery);
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
        toothBrushBleUtilsData=null;
        toothBrushBleUtilsData=new ToothBrushBleUtilsData(bleDevice,bleCallback);

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
    public void onNotifyData(byte[] bytes, int type) {
        if (bleToothBrushCallback != null)
            bleToothBrushCallback.onShowData("蓝牙返回的A7: " + BleStrUtils.byte2HexStr(bytes));
        switch (bytes[0]) {
            case ToothBrushBleCmd.GET_TOOTHBRUSH_TIME_GEARS:
                if (bytes.length >= 5) {
                    int highTime = (bytes[1] & 0xff) << 8;
                    int lowTime = bytes[2] & 0xff;
                    int gears = bytes[3] & 0xff;
                    int gearsfrom = bytes[4] & 0xff;
                    if (bleToothBrushCallback != null)
                        bleToothBrushCallback.onGetDefaultGearAndDuration(highTime + lowTime, gears, gearsfrom);
                }
                break;
            case ToothBrushBleCmd.SET_TOOTHBRUSH_TIME_GEARS:
            case ToothBrushBleCmd.Set_Manual_Mode:
                if (bytes.length >= 2) {
                    if (bleToothBrushCallback != null)
                        bleToothBrushCallback.onSetDefaultModeAndManualModeResult(bytes[0], bytes[1] & 0xff);

                }
                break;
            case ToothBrushBleCmd.Get_Manual_Mode:
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
            case (byte) ToothBrushBleCmd.Brush_Teeth_to_Complete:
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
            case ToothBrushBleCmd.The_Trial_Order:
                //试用指令
                if (bytes.length >= 2)
                    if (bleToothBrushCallback != null) {
                        bleToothBrushCallback.onTryOutResult(bytes[1] & 0xff);
                    }
                break;
            case ToothBrushBleCmd.Get_Second_GEARS:
                if (bytes.length >= 2)
                    if (bleToothBrushCallback != null) {
                        bleToothBrushCallback.onTwoLevelModeDefault(bytes[1] & 0xff);
                    }


        }
    }


    @Override
    public void onNotifyDataA6(byte[] hex) {
        if (bleToothBrushCallback != null)
            bleToothBrushCallback.onShowData("蓝牙返回的A6: " + BleStrUtils.byte2HexStr(hex));
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
            if (bleToothBrushCallback != null)
                bleToothBrushCallback.onGetSupportGears(stairs, secondLevels);
        }


    }

    private BleToothBrushCallback bleToothBrushCallback;


    public interface BleToothBrushCallback {

        /**
         * 版本号
         * version number
         *
         * @param Version
         */
        void onVersion(String Version);

        /**
         * 牙刷支持的档位
         * Gear supported by toothbrush
         *
         * @param staif       一级档位 First gear
         * @param secondLevel 二级档位 Second gear
         */
        void onGetSupportGears(List<Integer> staif, List<Integer> secondLevel);

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
        void onGetBattery(int batteryStatus, int batteryQuantity);

        /**
         * 获得到默认的刷牙档位和时长
         * Get the default brushing position and duration
         *
         * @param time     刷牙时长 Brushing time
         * @param gear     刷牙档位  Brushing gear
         * @param gearFrom 刷牙档位级别  Brushing gear level
         */
        void onGetDefaultGearAndDuration(int time, int gear, int gearFrom);

        /**
         * 请求授权的回调
         * Callback to request authorization
         *
         * @param result 0：没有 1：已经授权 2：不需要授权 3：授权成功 {@link ToothBrushBleCmd#NO_TOKEN}
         */
        void onGetTokenResult(int result);

        /**
         * 获取手动档位的参数
         * Get the parameters of manual gear
         *
         * @param time 时长  duration
         * @param hz   频率  frequency
         * @param duty 占空比 Duty cycle
         */
        void onGetManualParameter(int time, int hz, int duty);

        /**
         * 设置默认档位和手动模式结果回调
         *
         * @param type   类型 {@link ToothBrushBleCmd#SET_TOOTHBRUSH_TIME_GEARS, ToothBrushBleCmd#Set_Manual_Mode}
         * @param result 0：设置成功 1：设置失败 2：不支持设置
         */
        void onSetDefaultModeAndManualModeResult(byte type, int result);

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
        void onTestFinish(int totalTime, int leftTime, int rightTime, int mode, int battery);

        /**
         * 试用回调
         * Trial callback
         *
         * @param result 0：设置成功  0: Setting is successful
         *               1：设置失败，原因未知  1: Setting fails, the reason is unknown
         *               2：不支持设置   2: Setting is not supported
         */
        void onTryOutResult(int result);

        /**
         * 获取二级档位默认值
         * Get the default value of the second gear
         *
         * @param mode 0x00：不支持 0x00: not supported
         *             0x01-0xfe：工作档位编号 0x01-0xfe: working gear number
         *             0xFF：手动设置档位 0xFF: manual setting of gear
         */
        void onTwoLevelModeDefault(int mode);

        /**
         * 蓝牙返回的数据
         * Data returned by Bluetooth
         *
         * @param data
         */
        void onShowData(String data);


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
    public void getdefaultGearAndDuration() {
        byte[] bytes = new byte[1];
        bytes[0] = ToothBrushBleCmd.GET_TOOTHBRUSH_TIME_GEARS;
        sendA7(bytes);

    }



    public void setOta() {
//        91 01
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x91;
        bytes[1] = 0x01;
        sendA6(bytes);
    }


    /**
     * 发起连接
     * Initiate connection
     *
     * @return
     */
    public void connectWifi() {

        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x88;
        bytes[1] = 0x01;
        sendA6(bytes);
    }




    /**
     * 设置试用
     * Set up trial
     */
    public void setTryOut(int id, int level, int hz, int duty) {
        byte[] bytes = new byte[14];
        bytes[0] = ToothBrushBleCmd.The_Trial_Order;
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
        bytes[0] = ToothBrushBleCmd.Get_Second_GEARS;
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
        bytes[0] = ToothBrushBleCmd.Set_Manual_Mode;
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
        bytes[0] = ToothBrushBleCmd.Get_Manual_Mode;

        sendA7(bytes);


    }

    /**
     * APP 请求授权 Payload
     * Request authorization
     *
     * @param toothbrushId 时间搓
     */
    public void requestToken(long toothbrushId) {
        byte[] timebyte = long2Bytes(toothbrushId); //long 长度8 byte  时间搓是6个byte 所以去后面6位
        byte[] bytes = new byte[7];
        bytes[0] = ToothBrushBleCmd.REQUEST_TOKEN;
        bytes[1] = timebyte[2];
        bytes[2] = timebyte[3];
        bytes[3] = timebyte[4];
        bytes[4] = timebyte[5];
        bytes[5] = timebyte[6];
        bytes[6] = timebyte[7];
        sendA6(bytes);

    }

    private byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    private SendBleBean sendBleBean;

    /**
     * 发送A6指令
     *
     * @param bytes
     */
    public void sendA6(byte[] bytes) {
        if (sendBleBean == null)
            sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
    }

    /**
     * 断开连接
     * Disconnect
     *
     * @return SendBleBean
     */
    public void disconnectWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x88;
        bytes[1] = 0x00;
        sendA6(bytes);
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
