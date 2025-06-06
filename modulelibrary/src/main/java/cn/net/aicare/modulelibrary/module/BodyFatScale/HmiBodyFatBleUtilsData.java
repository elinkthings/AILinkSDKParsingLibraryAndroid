package cn.net.aicare.modulelibrary.module.BodyFatScale;


import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleSettingListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.List;


public class HmiBodyFatBleUtilsData extends BaseBleDeviceData {


    private BleDevice mBleDevice = null;
    private volatile static HmiBodyFatBleUtilsData hmiBodyFatBleUtilsData = null;


    private HmiBodyFatBleUtilsData(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        this.mBleBodyFatCallback = bleBodyFatCallback;

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
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onSysTime(status, times);
            }
        });


        //通用指令的回调
        mBleDevice.setOnBleSettingListener(new OnBleSettingListener() {
            @Override
            public void OnSettingReturn(int cmdType, int cmdData) {
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.setTimeCallback(cmdType, cmdData);
            }
        });

    }

    public static HmiBodyFatBleUtilsData getInstance() {
        return hmiBodyFatBleUtilsData;
    }

    /**
     * wifi+ble
     *
     * @param bleDevice          {@link BleDevice} 蓝牙设备
     * @param bleBodyFatCallback {@link BleBodyFatCallback} 体脂秤接口回调
     */
    public static void init(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback) {
        hmiBodyFatBleUtilsData = new HmiBodyFatBleUtilsData(bleDevice, bleBodyFatCallback);
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


    public void setWeightUnit(int unit) {
        SendMcuBean sendMcuBean = HmiBodyFatDataUtil.getInstance().setWeightUnit(unit);
        sendData(sendMcuBean);
    }

    public void synSysTime() {
        SendBleBean bean = HmiBodyFatDataUtil.getInstance().synSysTime();
        sendData(bean);
    }

    public void synMcuTime() {
        SendBleBean bean = HmiBodyFatDataUtil.getInstance().synMcuTime();
        sendData(bean);
    }

    public void setUserInfo(HmiBodyFatUserBean userInfo) {
        SendMcuBean sendMcuBean = HmiBodyFatDataUtil.getInstance().setUserInfo(userInfo);
        sendData(sendMcuBean);
    }

    public void setUserInfoList(List<HmiBodyFatUserBean> list) {
        List<SendBleBean> sendBleBeans = HmiBodyFatDataUtil.getInstance().setUserInfoList(list);
        for (SendBleBean sendBleBean : sendBleBeans) {
            sendData(sendBleBean);
        }
    }
    public void updateUsersComplete() {
        SendBleBean bean = HmiBodyFatDataUtil.getInstance().updateUsersComplete();
        sendData(bean);
    }

    public void updateUserInfo(HmiBodyFatUserBean userInfo) {
        SendBleBean bean = HmiBodyFatDataUtil.getInstance().updatePresentUser(userInfo);
        sendData(bean);
    }


    public void requestSynHistory() {
        SendBleBean bean = HmiBodyFatDataUtil.getInstance().requestSynHistory();
        sendData(bean);
    }


    private HmiBodyFatRecordBean mHmiBodyFatRecordBean;

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        BleLog.i("HMI体脂秤:" + BleStrUtils.byte2HexStr(hex));
        int cmd = hex[0] & 0xFF;
        if (mBleBodyFatCallback != null) {
            mBleBodyFatCallback.onStatus(cmd);
        }
        switch (cmd) {
            //实时体重
            case HmiBodyFatDataUtil.WEIGHT_TESTING:
                //稳定体重
            case HmiBodyFatDataUtil.WEIGHT_RESULT:

                if (mBleBodyFatCallback != null) {
                    mBleBodyFatCallback
                            .onWeightData(cmd, HmiBodyFatDataUtil.getInstance().getWeight(hex), HmiBodyFatDataUtil.getInstance().getWeightUnit(hex), HmiBodyFatDataUtil.getInstance().getWeightPoint(hex));
                }

                break;

            //阻抗无算法位
            case HmiBodyFatDataUtil.IMPEDANCE_SUCCESS_DATA:
            case HmiBodyFatDataUtil.IMPEDANCE_SUCCESS:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onAdc(HmiBodyFatDataUtil.getInstance().getImpedance(hex), HmiBodyFatDataUtil.getInstance().getArithmetic(hex));

                break;
//            //阻抗有算法位
//
//                if (mBleBodyFatCallback != null)
//                    mBleBodyFatCallback.onAdc(HmiBodyFatDataUtil.getInstance().getImpedance(hex), HmiBodyFatDataUtil.getInstance().getArithmetic(hex));
//
//                break;
            //心率
            case HmiBodyFatDataUtil.HEART_SUCCESS:
                if (mBleBodyFatCallback != null) {
                    mBleBodyFatCallback.onHeartRate(HmiBodyFatDataUtil.getInstance().getHeart(hex));
                }

                break;
            //体脂数据
            case HmiBodyFatDataUtil.BODY_FAT:
                if (mHmiBodyFatRecordBean == null) {
                    mHmiBodyFatRecordBean = new HmiBodyFatRecordBean();
                }
                mHmiBodyFatRecordBean = HmiBodyFatDataUtil.getInstance().getBodyFat(hex, mHmiBodyFatRecordBean);
                if ((hex[1] & 0xFF) != 1) {
                    if (mBleBodyFatCallback != null) {
                        mBleBodyFatCallback.onBodyFat(mHmiBodyFatRecordBean);
                    }
                    mHmiBodyFatRecordBean = null;
                }
                break;


            case HmiBodyFatDataUtil.SET_UNIT_CALLBAKC:
                if (mBleBodyFatCallback != null) {
                    mBleBodyFatCallback.setUnitCallback(HmiBodyFatDataUtil.getInstance().setUnitCallback(hex));
                }
                break;
            case HmiBodyFatDataUtil.MUC_REQUEST_USER_INFO:
                if (mBleBodyFatCallback != null) {
                    mBleBodyFatCallback.requestUserData(HmiBodyFatDataUtil.getInstance().requestUserDataCallback(hex));
                }
                break;
            case HmiBodyFatDataUtil.ERROR_CODE:
                if (hex.length > 2) {
                    //错误码
                    if (mBleBodyFatCallback != null) {
                        mBleBodyFatCallback.onError((hex[1] & 0xFF));
                    }
                }
                break;

            default:
                break;


        }
//         }
    }


    @Override
    public void onNotifyDataA6(byte[] hex) {
        if (hex[0] == HmiBodyFatDataUtil.SCALE_SPECIFIC_INTERACTION) {
            A6Order(hex);
        } else if ((hex[0] & 0xFF) == 0x91) {
            if (mBleBodyFatCallback != null)
                mBleBodyFatCallback.onOtaCallback(hex[1] & 0xFF);

        } else if ((hex[0] & 0xFF) == 0x8c) {
            if (mBleBodyFatCallback != null) {
                byte[] name = new byte[hex.length - 1];
                System.arraycopy(hex, 1, name, 0, name.length);
                mBleBodyFatCallback.onIpData(BleStrUtils.convertUTF8ToString(name));
            }
        } else if ((hex[0] & 0xFF) == 0x8e) {
            if (mBleBodyFatCallback != null) {
                int port = (((hex[1] & 0xFF) << 8) | (hex[2] & 0xFF));
                mBleBodyFatCallback.onPortData(port);
            }
        } else if ((hex[0] & 0xFF) == 0x97) {
            if (mBleBodyFatCallback != null) {
                byte[] name = new byte[hex.length - 1];
                System.arraycopy(hex, 1, name, 0, name.length);
                mBleBodyFatCallback.onUrlData(BleStrUtils.convertUTF8ToString(name));
            }
        }

    }


    private HmiAppHistoryRecordBean mHmiAppHistoryRecordBean;
    private HmiMcuHistoryRecordBean mHmiMcuHistoryRecordBean;


    private void A6Order(byte[] bytes) {
        switch (bytes[1]) {
            //同步历史数据回调
            case HmiBodyFatDataUtil.RE_SYN_HISTORY_CALLBACK:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.requestSynHistoryCallback(HmiBodyFatDataUtil.getInstance().requestSynHistoryCallback(bytes));

                break;
            //更新用户回复
            case HmiBodyFatDataUtil.UPDATE_USER_CALLBACK:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.updateUserCallback(HmiBodyFatDataUtil.getInstance().updateUserCallback(bytes));

                break;
            case HmiBodyFatDataUtil.REQUEST_SYN_TIME:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.requestSynTime();
                break;

//            case HmiBodyFatDataUtil.SET_SYSTIME_RESULT:
//            case HmiBodyFatDataUtil.REQUEST_SYNTIME_RESULT:
//                if (mBleBodyFatCallback != null)
//                    mBleBodyFatCallback.setTimeCallback(bytes[2] & 0xFF);
//                break;

            //mcu历史记录
            case HmiBodyFatDataUtil.MCU_HISTORY_RECORD:
                if (mHmiMcuHistoryRecordBean == null)
                    mHmiMcuHistoryRecordBean = new HmiMcuHistoryRecordBean();
                mHmiMcuHistoryRecordBean = HmiBodyFatDataUtil.getInstance().getMcuHistoryRecord(bytes, mHmiMcuHistoryRecordBean);
                if (bytes[2] == 0x03) {
                    //
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onHistoryMcu(mHmiMcuHistoryRecordBean);
                    mHmiMcuHistoryRecordBean = null;

                }
                break;
            //app历史记录
            case HmiBodyFatDataUtil.APP_HISTORY_RECORD:
                if (mHmiAppHistoryRecordBean == null)
                    mHmiAppHistoryRecordBean = new HmiAppHistoryRecordBean();
                mHmiAppHistoryRecordBean = HmiBodyFatDataUtil.getInstance().getAppHistoryRecord(bytes, mHmiAppHistoryRecordBean);
                if (bytes[2] == 0x02) {
                    //app算的历史数据
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onHistoryApp(mHmiAppHistoryRecordBean);
                    mHmiAppHistoryRecordBean = null;
                }
                break;


        }
    }


    private BleBodyFatCallback mBleBodyFatCallback;

    public interface BleBodyFatCallback {
        /**
         * 单位如果返回的st:lb，体重的lb的值，需要把lb转为st:lb;
         *
         * @param status     状态 01实时体重 02 稳定体重
         * @param weight     体重  已转为浮点型
         * @param weightUnit 体重单位 {@link HmiBodyFatDataUtil#KG}
         * @param decimals   小数点位
         *                   If the unit returns st: lb, the weight of lb value, you need to convert lb to st: lb;
         * @param status     Status 01 Real-time weight 02 Stable weight
         * @param weight     has been converted to floating point
         * @param weightUnit weight unit {@link HmiBodyFatDataUtil # KG}
         * @param decimals   decimal point
         */
        void onWeightData(int status, float weight, int weightUnit, int decimals);

        /**
         * 测量状态
         *
         * @param status {@link HmiBodyFatDataUtil#WEIGHT_TESTING}
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
         * @param hmiBodyFatRecordBean {@link HmiBodyFatRecordBean}
         */
        void onBodyFat(HmiBodyFatRecordBean hmiBodyFatRecordBean);

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
         * @param hmiMcuHistoryRecordBean {@link HmiMcuHistoryRecordBean}
         */
        void onHistoryMcu(HmiMcuHistoryRecordBean hmiMcuHistoryRecordBean);

        /**
         * 历史数据
         * 需要app自己去计算体脂数据
         * historical data
         * Need app to calculate body fat data
         *
         * @param hmiAppHistoryRecordBean {@link HmiAppHistoryRecordBean}
         */
        void onHistoryApp(HmiAppHistoryRecordBean hmiAppHistoryRecordBean);

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
         * @param status {@link HmiBodyFatDataUtil#STATUS_SUCCESS} 0 成功 1 失败 2 不支持
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


        /**
         * 回调ip
         *
         * @param ip 47.113.114.70
         */
        void onIpData(String ip);

        /**
         * 回调端口
         *
         * @param port 8092
         */
        void onPortData(int port);

        /**
         * 回调Url
         *
         * @param url /index/
         */
        void onUrlData(String url);
    }


}
