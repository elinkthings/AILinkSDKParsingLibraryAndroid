package cn.net.aicare.modulelibrary.module.BodyFatScale;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.elinkthings.bleotalibrary.listener.OnBleOTAListener;
import com.elinkthings.bleotalibrary.netstrap.OPLOtaManager;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleConnectStatus;
import com.pingwang.bluetoothlib.listener.OnBleSettingListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.listener.OnWifiInfoListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;



public class BodyFatBleUtilsData extends BaseBleDeviceData {


    private BleDevice mBleDevice = null;
    private volatile static BodyFatBleUtilsData bodyfatble = null;
    private OPLOtaManager mOPLOtaManager;

//    private RefreshUICallback refreshUICallback;
    //    private OnBleConnectStatus onBleConnectStatus;
//    private OnWifiInfoListener onWifiInfoListener;


    private BodyFatBleUtilsData(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback, BleBodyFatWiFiCallback bleBodyFatWiFiCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        this.mBleBodyFatCallback = bleBodyFatCallback;
        this.mBleBodyFatWiFiCallback = bleBodyFatWiFiCallback;


        mBleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //蓝牙版本号
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onVersion(version);
            }
        });
        mBleDevice.setOnMcuParameterListener(new OnMcuParameterListener() {
            @Override
            public void onMcuBatteryStatus(int status, int battery) {
                //d电量
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onMcuBatteryStatus(status, battery);
            }

            @Override
            public void onSysTime(int status, int[] times) {
                //模块返回的时间
                Log.e("Time", "模块返回的时间");
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onSysTime(status, times);
            }
        });


        if (mBleBodyFatCallback != null) {
            mBleDevice.setOnBleConnectListener(mOnBleConnectStatus);
            mBleDevice.setOnWifiInfoListener(onWifiInfoListener);

        }
        //通用指令的回调
        mBleDevice.setOnBleSettingListener(new OnBleSettingListener() {
            @Override
            public void OnSettingReturn(byte cmdType, byte cmdData) {


                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.setTimeCallback(cmdType, cmdData);
            }
        });

    }

    public static BodyFatBleUtilsData getInstance() {
        return bodyfatble;
    }

    /**
     * wifi+ble
     *
     * @param bleDevice              {@link BleDevice} 蓝牙设备
     * @param bleBodyFatCallback     {@link BleBodyFatCallback} 体脂秤接口回调
     * @param bleBodyFatWiFiCallback {@link BleBodyFatWiFiCallback} 体脂秤关于wifi部分接口回调
     */
    public static void init(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback, BleBodyFatWiFiCallback bleBodyFatWiFiCallback) {

        bodyfatble = new BodyFatBleUtilsData(bleDevice, bleBodyFatCallback, bleBodyFatWiFiCallback);
    }

    /**
     * 初始化
     *
     * @param bleDevice          {@link BleDevice} 蓝牙设备
     * @param bleBodyFatCallback {@link BleBodyFatCallback} 体脂秤接口回调
     */
    public static void init(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback) {

        init(bleDevice, bleBodyFatCallback, null);
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

    private BodyFatRecord bodyFatRecord;

    @Override
    public void onNotifyData(byte[] hex, int type) {

        int cmd = hex[0] & 0xFF;
        Log.e("NotifyData", BleStrUtils.byte2HexStr(hex));
        if (mBleBodyFatCallback != null)
            mBleBodyFatCallback.onStatus(cmd);
        switch (cmd) {
            //实时体重
            case BodyFatDataUtil.WEIGHT_TESTING:
                //稳定体重
            case BodyFatDataUtil.WEIGHT_RESULT:

                if (mBleBodyFatCallback != null) {
                    mBleBodyFatCallback
                            .onWeightData(cmd, BodyFatDataUtil.getInstance().getWeight(hex), BodyFatDataUtil.getInstance().getWeightUnit(hex), BodyFatDataUtil.getInstance().getWeightPoint(hex));
                }

                break;

            //阻抗无算法位
            case BodyFatDataUtil.IMPEDANCE_SUCCESS_DATA:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onAdc(BodyFatDataUtil.getInstance().getImpedance(hex), 0);

                break;
            //阻抗有算法位
            case BodyFatDataUtil.IMPEDANCE_SUCCESS:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onAdc(BodyFatDataUtil.getInstance().getImpedance(hex), BodyFatDataUtil.getInstance().getArithmetic(hex));

                break;
            //心率
            case BodyFatDataUtil.HEART_SUCCESS:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onHeartRate(BodyFatDataUtil.getInstance().getHeart(hex));

                break;
            //体脂数据
            case BodyFatDataUtil.BODY_FAT:
                if (bodyFatRecord == null)
                    bodyFatRecord = new BodyFatRecord();
                bodyFatRecord = BodyFatDataUtil.getInstance().getBodyFat(hex, bodyFatRecord);
                if ((hex[1] & 0xff) != 1) {
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onBodyFat(bodyFatRecord);
                    bodyFatRecord = null;
                }
                break;


            case BodyFatDataUtil.SET_UNIT_CALLBAKC:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.setUnitCallback(BodyFatDataUtil.getInstance().setUnitCallback(hex));
                break;
            case BodyFatDataUtil.MUC_REQUEST_USER_INFO:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.requestUserData(BodyFatDataUtil.getInstance().requestUserDataCallback(hex));
                break;
            case BodyFatDataUtil.ERROR_CODE:
                if (hex.length > 2) {
                    //错误码
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onError((hex[1] & 0xff));
                }
                break;


        }
//         }
    }


    @Override
    public void onNotifyDataA6(byte[] hex) {
        Log.e("huangjunbin", BleStrUtils.byte2HexStr(hex));
        if (hex[0] == BodyFatDataUtil.SCALE_SPECIFIC_INTERACTION) {
            A6Order(hex);
        } else if ((hex[0] & 0xff) == 0x91) {
            if (mBleBodyFatCallback != null)
                mBleBodyFatCallback.onOtaCallback(hex[1] & 0xff);

        } else if ((hex[0] & 0xff) == 0x8b) {
            if (mBleBodyFatCallback != null) {
                mBleBodyFatCallback.onSetIpStatus(hex[1] & 0xff);
            }
        } else if ((hex[0] & 0xff) == 0x96) {
            if (mBleBodyFatCallback != null) {
                mBleBodyFatCallback.onSetIpUrlStatus(hex[1] & 0xff);
            }
        }

    }


    public void initOtaUtil(Context context, Uri url, OnBleOTAListener listener) {
        mOPLOtaManager = null;
        mOPLOtaManager = OPLOtaManager.newBuilder(context).setFilePath(url).setOnBleOTAListener(listener).build(mBleDevice);
        mOPLOtaManager.startOta();
    }


    private AppHistoryRecordBean appHistoryRecordBean;
    private McuHistoryRecordBean mcuHistoryRecordBean;


    private void A6Order(byte[] bytes) {
        switch (bytes[1]) {
            //同步历史数据回调
            case BodyFatDataUtil.RE_SYN_HISTORY_CALLBACK:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.requestSynHistoryCallback(BodyFatDataUtil.getInstance().requestSynHistoryCallback(bytes));

                break;
            //更新用户回复
            case BodyFatDataUtil.UPDATE_USER_CALLBACK:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.updateUserCallback(BodyFatDataUtil.getInstance().updateUserCallback(bytes));

                break;
            case BodyFatDataUtil.REQUEST_SYN_TIME:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.requestSynTime();
                break;

//            case BodyFatDataUtil.SET_SYSTIME_RESULT:
//            case BodyFatDataUtil.REQUEST_SYNTIME_RESULT:
//                if (mBleBodyFatCallback != null)
//                    mBleBodyFatCallback.setTimeCallback(bytes[2] & 0xff);
//                break;

            //mcu历史记录
            case BodyFatDataUtil.MCU_HISTORY_RECORD:
                if (mcuHistoryRecordBean == null)
                    mcuHistoryRecordBean = new McuHistoryRecordBean();
                mcuHistoryRecordBean = BodyFatDataUtil.getInstance().getMcuHistoryRecord(bytes, mcuHistoryRecordBean);
                if (bytes[2] == 0x03) {
                    //
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onHistoryMcu(mcuHistoryRecordBean);
                    mcuHistoryRecordBean = null;

                }
                break;
            //app历史记录
            case BodyFatDataUtil.APP_HISTORY_RECORD:
                if (appHistoryRecordBean == null)
                    appHistoryRecordBean = new AppHistoryRecordBean();
                appHistoryRecordBean = BodyFatDataUtil.getInstance().getAppHistoryRecord(bytes, appHistoryRecordBean);
                if (bytes[2] == 0x02) {
                    //app算的历史数据
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onHistoryApp(appHistoryRecordBean);
                    appHistoryRecordBean = null;
                }
                break;


        }
    }


    private BleBodyFatCallback mBleBodyFatCallback;

    private OnWifiInfoListener onWifiInfoListener = new OnWifiInfoListener() {
        @Override
        public void onScanWiFiStatus(int status) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnWifiScanStatus(status);
        }

        @Override
        public void onWifiListName(int no, String name) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnWifiListName(no, name);
        }

        @Override
        public void onConnectedWifiName(String name) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnWifiCurrentConnect(name);
        }

        @Override
        public void onWifiListInfo(int no, String mac, int db, int type, int wifistatus) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnWifiListInfo(no, mac, db, type, wifistatus);
        }

        @Override
        public void onWifiScanFinish(int wifiNum) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnWifiScanFinish(wifiNum);

        }

        @Override
        public void onSetWifiNameOrPawOrConnectCallback(int type, int status) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnSetWifiNameOrPwdOrConnectCallback(type, status);
        }

        @Override
        public void getSelectWifiMac(String mac) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.getSelectWifiMac(mac);
        }

        @Override
        public void getSelectWifiPaw(String pwd) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.getSelectWifiPaw(pwd);
        }


        @Override
        public void getSN(long sn) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.getDid(sn);
        }
    };

    private OnBleConnectStatus mOnBleConnectStatus = new OnBleConnectStatus() {
        @Override
        public void onBleConnectStatus(int bleStatus, int wifiStatus, int workStatus) {
            if (mBleBodyFatWiFiCallback != null)
                mBleBodyFatWiFiCallback.OnBleAndWifiStatus(bleStatus, wifiStatus, workStatus);
        }
    };


    public interface BleBodyFatCallback {
        /**
         * 单位如果返回的st:lb，体重的lb的值，需要把lb转为st:lb;
         *
         * @param status     状态 01实时体重 02 稳定体重
         * @param weight     体重  已转为浮点型
         * @param weightUnit 体重单位 {@link BodyFatDataUtil#KG}
         * @param decimals   小数点位
         *                   If the unit returns st: lb, the weight of lb value, you need to convert lb to st: lb;
         * @param status     Status 01 Real-time weight 02 Stable weight
         * @param weight     has been converted to floating point
         * @param weightUnit weight unit {@link BodyFatDataUtil # KG}
         * @param decimals   decimal point
         */
        void onWeightData(int status, float weight, int weightUnit, int decimals);

        /**
         * 测量状态
         *
         * @param status {@link BodyFatDataUtil#WEIGHT_TESTING}
         *               Measurement status
         */
        void onStatus(int status);

        /**
         * 阻抗和算法
         *
         * @param adc         阻抗值
         * @param algorithmic 算法 0:无算法
         *                     
         *                    Impedance and algorithm
         * @param adc         impedance value
         * @param algorithmic algorithm 0: no algorithm
         */
        void onAdc(int adc, int algorithmic);

        /**
         * 心率
         * Heart rate
         *
         * @param heartRate 整数  integer
         */
        void onHeartRate(int heartRate);

        /**
         * 体脂数据
         * 这个是秤那边返回来的，如果算法位为0 的时候，是秤计算的体脂数据，这个接口就会有数据
         * 算法位不为0 则是app 计算体脂数据，这个接口是没有数据返回的
         * Body fat data
         * This is returned from the scale. If the algorithm bit is 0, it is the body fat data calculated by the scale.
         * If the algorithm bit is not 0, the app calculates body fat data. This interface returns no data.
         *
         * @param bodyFatRecord {@link BodyFatRecord}
         */
        void onBodyFat(BodyFatRecord bodyFatRecord);

        /**
         * 错误码
         * error code
         *
         * @param code 错误码 error code
         */
        void onError(int code);


        /**
         * 历史数据
         * 由秤计算出来的体脂数据
         * historical data
         * Body fat data calculated by the scale
         *
         * @param mcuHistoryRecordBean {@link McuHistoryRecordBean}
         */
        void onHistoryMcu(McuHistoryRecordBean mcuHistoryRecordBean);

        /**
         * 历史数据
         * 需要app自己去计算体脂数据
         * historical data
         * Need app to calculate body fat data
         *
         * @param appHistoryRecordBean {@link AppHistoryRecordBean}
         */
        void onHistoryApp(AppHistoryRecordBean appHistoryRecordBean);

        /**
         * 版本号
         * version number
         *
         * @param version 版本号
         */
        void onVersion(String version);

        /**
         * 电量
         * Electricity
         *
         * @param status  电量的状态 battery status
         * @param battery 电量的百分比 percentage of power
         */
        void onMcuBatteryStatus(int status, int battery);

        /**
         * 模块返回的时间
         * When the module returns
         *
         * @param status 0=时间无效,1=时间有效  0 = Time is invalid, 1 = Time is valid
         * @param times  times[0]年 times[1] 月 times[2] 日 times[3] 时 times[4] 分 times[5] 秒 times [0] years times [1] months times [2] days times [3] hours times [4] minutes times [5] seconds
         */
        void onSysTime(int status, int[] times);

        /**
         * 请求同步时间
         * Request synchronization time
         */
        void requestSynTime();

        /**
         * 设置时间回调
         * Set time Callback
         *
         * @param status {@link BodyFatDataUtil#STATUS_SUCCESS} 0 成功 1 失败 2 不支持
         *               0 Success 1 Failure 2 Not supported
         */
        void setTimeCallback(int type, int status);

        /**
         * 请求历史记录回调
         * Request history callback
         *
         * @param status 00：无历史记录 01：开始发送历史记录 02：结束发送历史记录
         *               00: No history record 01: Start sending history record 02: End sending history record
         */
        void requestSynHistoryCallback(int status);

        /**
         * 更新用户列和更新个人用户回调
         * 这里的用户都是主动下发的
         * Update user column and update individual user callback
         * The users here are voluntarily issued
         *
         * @param status 00：更新列表成功 01：更新个人用户成功 02：更新列表失败 03：更新个人用户失败
         *               00: Successfully update the list 01: Successfully update the individual user 02: Fail to update the list 03: Fail to update the individual user
         */
        void updateUserCallback(int status);

        /**
         * 设置单位回调
         * Set unit callback
         *
         * @param status 0：设置成功 1：设置失败 2：不支持设置
         *               0: Setting succeeded 1: Setting failed 2: Not supported setting
         */
        void setUnitCallback(int status);

        /**
         * 请求用户
         * 被动的下发的用户 与同步用户列表和更新个人用户是不同。
         * Request user
         * Passively delivered users are different from synchronizing user lists and updating individual users.
         *
         * @param status 0x01：请求用户  0x03下发成功 0x04 下发用户失败
         *               0x01: Request user 0x03 successfully delivered 0x04 failed user delivery
         */
        void requestUserData(int status);

        void onOtaCallback(int status);

        void onSetIpStatus(int status);

        void onSetIpUrlStatus(int status);

    }


    private BleBodyFatWiFiCallback mBleBodyFatWiFiCallback;

    public interface BleBodyFatWiFiCallback {


        /**
         * 查询蓝牙和wifi状态
         * Check Bluetooth and wifi status
         *
         * @param blestatus  蓝牙状态 0 无连接 1：已连接 2：配对完成
         *                   Bluetooth status0 No connection 1: Connected 2: Pairing completed
         * @param wifistatus wifi状态 0：没连接热点；1：尝试连接热点，但是失败，连接时密码错误、热点信号不好、主动断开都会是这个状态；2：连接的热点无网络或者信号不好；3：成功连接上热点；4：有热点信息，还没连接
         *                   wifi status 0: No hotspot connection; 1: Attempt to connect to the hotspot, but failure, wrong password when connecting, bad hotspot signal, active disconnection will
         *                   be this state; 2: No connection to the hotspot or signal ; 3: Successfully connected to the hotspot; 4: There is hotspot information, not connected yet
         * @param workstatus 工作状态 0：唤醒 1：进入休眠 2：模块准备就绪
         *                   working status 0: wake up 1: go to sleep 2: module is ready
         */
        void OnBleAndWifiStatus(int blestatus, int wifistatus, int workstatus);

        /**
         * 扫描wifi状态
         * Scan wifi status
         *
         * @param Status {@link BodyFatDataUtil#STATUS_SUCCESS} 0x00：成功 0x01：失败 0x02：不支持
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
         * @param wifistatus wifi的状态 0x00：陌生wifi 0x01：保存过密码的wifi 0x02：目前连接着的wifi
         *                   wifi status 0x00: unfamiliar wifi 0x01: password saved wifi 0x02: currently connected wifi
         */
        void OnWifiListInfo(int no, String mac, int db, int type, int wifistatus);

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
         * @param status {@link BodyFatDataUtil#STATUS_SUCCESS} 0x00：成功 0x01：失败 0x02：不支持
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
}
