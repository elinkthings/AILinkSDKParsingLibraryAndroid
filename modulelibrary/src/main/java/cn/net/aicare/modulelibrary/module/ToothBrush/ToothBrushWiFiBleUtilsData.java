package cn.net.aicare.modulelibrary.module.ToothBrush;


import android.util.Log;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleConnectStatus;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.listener.OnWifiInfoListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * wifi+ble牙刷
 */
public class ToothBrushWiFiBleUtilsData extends BaseBleDeviceData {

    public final static int TOOTHBRUSH_WIFI_BLE = 0x12;
    private BleDevice mBleDevice = null;
    private volatile static ToothBrushWiFiBleUtilsData bodyFatBle = null;


    private ToothBrushWiFiBleUtilsData(BleDevice bleDevice, BleToothBrushCallback bleToothBrushCallback,
                                       BleToothBrushWiFiCallback bleToothBrushWiFiCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        this.bleToothBrushCallback = bleToothBrushCallback;
        this.bleToothBrushWiFiCallback = bleToothBrushWiFiCallback;


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
                Log.e("huang", " 电量 " + status + " " + battery);
                if (bleToothBrushCallback != null)
                    bleToothBrushCallback.onGetBattery(status, battery);
            }
        });
        mBleDevice.setOnBleOtherDataListener(new OnBleOtherDataListener() {
            @Override
            public void onNotifyOtherData(byte[] data) {
                Log.e("onNotifyOtherData", BleStrUtils.byte2HexStr(data));
            }
        });


        mBleDevice.setOnBleConnectListener(mOnBleConnectStatus);
        mBleDevice.setOnWifiInfoListener(onWifiInfoListener);


    }

    public static ToothBrushWiFiBleUtilsData getInstance() {
        return bodyFatBle;
    }

    /**
     * wifi+ble
     *
     * @param bleDevice                 {@link BleDevice} 蓝牙设备
     * @param bleCallback               {@link BleToothBrushCallback} 牙刷接口回调
     * @param bleToothBrushWiFiCallback {@link BleToothBrushWiFiCallback} 牙刷关于wifi部分接口回调
     */
    public static void init(BleDevice bleDevice, BleToothBrushCallback bleCallback, BleToothBrushWiFiCallback bleToothBrushWiFiCallback) {

        bodyFatBle = new ToothBrushWiFiBleUtilsData(bleDevice, bleCallback, bleToothBrushWiFiCallback);
    }

    /**
     * 初始化话
     *
     * @param bleDevice   蓝牙操作类
     * @param bleCallback 蓝牙基础回调接口
     */
    public static void init(BleDevice bleDevice, BleToothBrushCallback bleCallback) {

        init(bleDevice, bleCallback, null);
    }

    /**
     * 初始化
     *
     * @param bleDevice {@link BleDevice} 蓝牙设备
     */
    public static void init(BleDevice bleDevice) {
        init(bleDevice, null);
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
//        Log.e("huang", "蓝牙返回的A7数据" + )BleStrUtils.byte2HexStr(bytes);
        if (bleToothBrushCallback != null)
            bleToothBrushCallback.onShowData("蓝牙返回的A7: " + BleStrUtils.byte2HexStr(bytes));
        switch (bytes[0]) {
            case ToothBrushBleCmd.GET_TOOTHBRUSH_TIME_GEARS:
                if (bytes.length >= 5) {
                    int highTime = (bytes[1] & 0xff) << 8;
                    int lowTime = bytes[2] & 0xff;
                    int gears = bytes[3] & 0xff;
                    int gearsFrom = bytes[4] & 0xff;
                    if (bleToothBrushCallback != null)
                        bleToothBrushCallback.onGetDefaultGearAndDuration(highTime + lowTime, gears, gearsFrom);
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

    private OnWifiInfoListener onWifiInfoListener = new OnWifiInfoListener() {
        @Override
        public void onScanWiFiStatus(int status) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnWifiScanStatus(status);
        }

        @Override
        public void onWifiListName(int no, String name) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnWifiListName(no, name);
        }

        @Override
        public void onConnectedWifiName(String name) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnWifiCurrentConnect(name);
        }

        @Override
        public void onWifiListInfo(int no, String mac, int db, int type, int wifistatus) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnWifiListInfo(no, mac, db, type, wifistatus);
        }

        @Override
        public void onWifiScanFinish(int wifiNum) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnWifiScanFinish(wifiNum);

        }

        @Override
        public void onSetWifiNameOrPawOrConnectCallback(int type, int status) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnSetWifiNameOrPwdOrConnectCallback(type, status);
        }

        @Override
        public void getSelectWifiMac(String mac) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.getSelectWifiMac(mac);
        }

        @Override
        public void getSelectWifiPaw(String pwd) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.getSelectWifiPaw(pwd);
        }


        @Override
        public void getSN(long sn) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.getDid(sn);
        }
    };

    private OnBleConnectStatus mOnBleConnectStatus = new OnBleConnectStatus() {
        @Override
        public void onBleConnectStatus(int bleStatus, int wifiStatus, int workStatus) {
            if (bleToothBrushWiFiCallback != null)
                bleToothBrushWiFiCallback.OnBleAndWifiStatus(bleStatus, wifiStatus, workStatus);
        }
    };


    public interface BleToothBrushCallback {

        /**
         * 版本号
         *version number
         * @param Version
         */
        void onVersion(String Version);

        /**
         * 牙刷支持的档位
         *Gear supported by toothbrush
         * @param firstLevel       一级档位 First gear
         * @param secondLevel 二级档位 Second gear
         */
        void onGetSupportGears(List<Integer> firstLevel, List<Integer> secondLevel);

        /**
         * 获取电量
         * Get battery
         *
         * @param batteryStatus 电池充电状态  Battery charging status
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
         * @param type   类型 {@link ToothBrushBleCmd#SET_TOOTHBRUSH_TIME_GEARS,ToothBrushBleCmd#Set_Manual_Mode}
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


    private BleToothBrushWiFiCallback bleToothBrushWiFiCallback;

    public interface BleToothBrushWiFiCallback {


        /**
         * 查询蓝牙和wifi状态
         * Check Bluetooth and wifi status
         *
         * @param bleStatus  蓝牙状态 0 无连接 1：已连接 2：配对完成
         *                   Bluetooth status0 No connection 1: Connected 2: Pairing completed
         * @param wifiStatus wifi状态 0：没连接热点；1：尝试连接热点，但是失败，连接时密码错误、热点信号不好、主动断开都会是这个状态；2：连接的热点无网络或者信号不好；3：成功连接上热点；4：有热点信息，还没连接
         *                   wifi status 0: No hotspot connection; 1: Attempt to connect to the hotspot, but failure, wrong password when connecting, bad hotspot signal, active disconnection will
         *                   be this state; 2: No connection to the hotspot or signal ; 3: Successfully connected to the hotspot; 4: There is hotspot information, not connected yet
         * @param workStatus 工作状态 0：唤醒 1：进入休眠 2：模块准备就绪
         *                   working status 0: wake up 1: go to sleep 2: module is ready
         */
        void OnBleAndWifiStatus(int bleStatus, int wifiStatus, int workStatus);

        /**
         * 扫描wifi状态
         * Scan wifi status
         *
         * @param Status {@link ToothBrushBleCmd#STATUS_SUCCESS} 0x00：成功 0x01：失败 0x02：不支持
         *               0x00: success 0x01: failure 0x02: not supported
         */
        void OnWifiScanStatus(int Status);

        /**
         * 扫描到的wifi名称
         * Scanned wifi name
         *
         * @param no   wifi编号 跟wifi信息的编号一致
         *             wifi number is consistent with wifi information number
         * @param name wifi名称
         *             wifi name
         */
        void OnWifiListName(int no, String name);

        /**
         * 扫描到的wifi信息
         * Scanned wifi information
         *
         * @param no         wifi编号 跟wifi名称的编号一致
         *                   wifi number is consistent with the number of wifi name
         * @param mac        wifi的mac地址
         *                   wifi mac address
         * @param db         wifi的信号强度
         *                   he signal strength of wifi
         * @param type       wifi安全类型 0x00：开放 0x01：WEP 0x02：WPA_PSK 0x03：WPA2_PSK 0x04：WPA_WPA_2_PSK 0x05：WPA2_ENTERPRISE
         *                   wifi security type 0x00: open 0x01: WEP 0x02: WPA_PSK 0x03: WPA2_PSK 0x04: WPA_WPA_2_PSK 0x05: WPA2_ENTERPRISE
         * @param wifiStatus wifi的状态 0x00：陌生wifi 0x01：保存过密码的wifi 0x02：目前连接着的wifi
         *                   wifi status 0x00: unfamiliar wifi 0x01: password saved wifi 0x02: currently connected wifi
         */
        void OnWifiListInfo(int no, String mac, int db, int type, int wifiStatus);

        /**
         * 当前连接的wifi名称
         * The name of the currently connected wifi
         *
         * @param name wifi名称  wifi name
         */
        void OnWifiCurrentConnect(String name);


        /**
         * 扫描wifi结束
         * End of scanning wifi
         *
         * @param wifiNum 扫描的的wifi数量 The number of wifi scanned
         */
        void OnWifiScanFinish(int wifiNum);

        /**
         * 设置wifimac ,密码和连接或断开的回调
         * Set callback for wifimac, password and connection or disconnection
         *
         * @param type   设置的类型 0x84 设置 wifimac 0x86 设置wifi密码 0x88 断开或者连接
         *               ype Set type 0x84 Set wifimac 0x86 Set wifi password 0x88 Disconnect or connect
         * @param status {@link ToothBrushBleCmd#STATUS_SUCCESS} 0x00：成功 0x01：失败 0x02：不支持
         *               0x00: success 0x01: failure 0x02: not supported
         */
        void OnSetWifiNameOrPwdOrConnectCallback(int type, int status);

        /**
         * 获取已设置的wifi的Mac
         * Get Mac with wifi set
         *
         * @param mac wifiMac地址
         *            wifiMac address
         */
        void getSelectWifiMac(String mac);

        /**
         * 获取已设置的wifi密码
         * Get Password with wifi set
         *
         * @param paw wifi密码  Password
         */
        void getSelectWifiPaw(String paw);

        /**
         * 获取到did号
         * Get did
         *
         * @param did did
         */
        void getDid(long did);
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
     * 设置wifi地址
     *Set wifi address
     * @param mac
     * @return
     */
    public void setWifiMac(String mac) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0x84;
        String[] s = mac.split(":");

        for (int i = 0; i < s.length; i++) {
            int hex = Integer.parseInt(s[i]);
            Log.e("huang", hex + "");
            bytes[i + 1] = (byte) hex;
        }
        Log.e("huang", BleStrUtils.byte2HexStr(bytes));
        sendA6(bytes);

    }

    /**
     * 设置wifi密码
     *Set wifi password
     * @param subpackage
     * @param password
     * @return
     */
    public void setWifiPwd(int subpackage, byte[] password) {
        int length = 0;
        byte[] bytes1;
//        byte[] password = BleStrUtils.stringToBytes(pwd);
        if (password != null) {
            length = password.length + 1;
            bytes1 = new byte[length + 1];
            bytes1[0] = (byte) 0x86;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(password, 0, bytes1, 2, password.length);

        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x86;
        }
        sendA6(bytes1);
    }

    /**
     * 发起连接
     *Initiate connection
     * @return
     */
    public void connectWifi() {

        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x88;
        bytes[1] = 0x01;
        sendA6(bytes);
    }

    /**
     * 扫描wifi
     *Scan wifi
     * @return
     */
    public void scanWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x80;
        bytes[1] = 0x01;
        sendA6(bytes);
    }

    public void queryBleStatus() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x26;
        sendA6(bytes);

    }

    /**
     * 获取到设备id
     *Get device id
     * @return
     */
    public void getDeviceId() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x93;
        sendA6(bytes);
    }

    /**
     * 当前连接wifi的名称
     *The name of the currently connected wifi
     * @return
     */
    public void getConnectWifiName() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x94;
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
        Log.e("huang", "使用指令" + BleStrUtils.byte2HexStr(bytes));
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
     *Request authorization
     * @param toothbrushId 时间搓
     */
    public void requestToken(long toothbrushId) {
        byte[] timeByte = long2Bytes(toothbrushId); //long 长度8 byte  时间搓是6个byte 所以去后面6位
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
        Log.e("huang", "发送A6指令payload" + BleStrUtils.byte2HexStr(bytes));
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


    private SendMcuBean sendMcuBean;

    /**
     * 发送A7指令
     *
     * @param bytes
     */
    public void sendA7(byte[] bytes) {
        if (sendMcuBean == null)
            sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(TOOTHBRUSH_WIFI_BLE, bytes);
        Log.e("huang", "发送A7指令payload" + BleStrUtils.byte2HexStr(bytes));
        sendData(sendMcuBean);
    }
}
