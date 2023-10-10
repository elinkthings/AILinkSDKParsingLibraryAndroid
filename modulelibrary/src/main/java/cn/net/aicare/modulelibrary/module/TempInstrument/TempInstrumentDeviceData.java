package cn.net.aicare.modulelibrary.module.TempInstrument;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * xing<br>
 * 2022/03/22<br>
 * 测温仪
 */
public class TempInstrumentDeviceData extends BaseBleDeviceData implements OnBleOtherDataListener {
    private String TAG = TempInstrumentDeviceData.class.getName();

    private onTempSetListener mOnTempSetListener;
    private onTempListener mOnTempListener;
    private onTempConfigListener mOnTempConfigListener;
    private int mType = TempInstrumentBleConfig.DEVICE_CID;
    private byte[] CID;
    private static BleDevice mBleDevice = null;
    private static TempInstrumentDeviceData mDevice = null;
    /**
     * 协议支持的语言类型,保存的是语言ID,按照顺序保存的
     */
    public final static List<Long> LANGUAGE_ID_ALL_TYPE_LIST = new ArrayList<Long>() {{
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_NULL);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_DI);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_DD);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_EN);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_IT);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_KO);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_ES);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_JP);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_JP1);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_FR);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_VI);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_KM);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_MS);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_IN);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_AR);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_TH);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_PL);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH0);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH1);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH2);
        add(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH3);
    }};
    /**
     * 音效
     */
    private List<Integer> mSoundTypeList = new ArrayList<>();
    /**
     * 距离
     */
    private List<Integer> mTestTempDistanceList = new ArrayList<>();

    /**
     * 自动关机
     */
    private List<Integer> mAutoCloseTypeList = new ArrayList<>();

    public static TempInstrumentDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mDevice == null) {
                    mDevice = new TempInstrumentDeviceData(bleDevice);

                }
            } else {
                mDevice = new TempInstrumentDeviceData(bleDevice);
            }
        }
        return mDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mDevice != null) {
            mOnTempSetListener = null;
            mOnTempListener = null;
            mDevice = null;
        }
    }


    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private TempInstrumentDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        mBleDevice.setOnBleOtherDataListener(this);
        init();

        mSoundTypeList.clear();
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_8);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_7);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_6);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_5);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_4);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_3);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_2);
        mSoundTypeList.add(TempInstrumentBleConfig.SoundType.SOUND_1);


        mAutoCloseTypeList.clear();
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_8);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_7);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_6);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_5);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_4);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_3);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_2);
        mAutoCloseTypeList.add(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_1);

        mTestTempDistanceList.clear();
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_1);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_2);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_3);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_4);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_5);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_6);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_7);
        mTestTempDistanceList.add(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_8);

    }


    private void init() {
        //TODO 可进行额温枪特有的初始化操作
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xFF);
        CID[1] = (byte) (mType);
    }

    //----------------------发送

    public void getDeviceVersion() {
        SendBleBean sendBleBean = new SendBleBean();
        BleSendCmdUtil sendCmdUtil = BleSendCmdUtil.getInstance();
        sendBleBean.setHex(sendCmdUtil.getBleVersion());
        sendData(sendBleBean);
    }

    public void getDeviceSupportUnit() {
        SendBleBean sendBleBean = new SendBleBean();
        BleSendCmdUtil sendCmdUtil = BleSendCmdUtil.getInstance();
        sendBleBean.setHex(sendCmdUtil.getSupportUnit());
        sendData(sendBleBean);
    }


    public void getDeviceConfig() {
        if (mTempInstrumentDeviceConfigBean == null) {
            mTempInstrumentDeviceConfigBean = new TempInstrumentDeviceConfigBean();
        }
        SendMcuBean sendMcuBean1 = new SendMcuBean();
        byte[] data1 = new byte[2];
        data1[0] = (byte) 0x80;
        data1[1] = 0x00;
        sendMcuBean1.setHex(CID, data1);
        sendData(sendMcuBean1);

        SendMcuBean sendMcuBean2 = new SendMcuBean();
        byte[] data2 = new byte[2];
        data2[0] = (byte) 0x82;
        data2[1] = 0x00;
        sendMcuBean2.setHex(CID, data2);
        sendData(sendMcuBean2);

        SendMcuBean sendMcuBean3 = new SendMcuBean();
        byte[] data3 = new byte[2];
        data3[0] = (byte) 0x84;
        data3[1] = 0x00;
        sendMcuBean3.setHex(CID, data3);
        sendData(sendMcuBean3);
    }

    /**
     * 同步单位
     *
     * @param unit 单位{@link TempInstrumentBleConfig.UnitType}
     */
    public void synUnit(@TempInstrumentBleConfig.UnitType int unit) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x10;
        data[1] = (byte) unit;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步音量
     *
     * @param level
     */
    public void synVolume(int level) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x12;
        data[1] = (byte) level;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 同步语言
     *
     * @param languageIndex 语言下标,按顺序的{@link TempInstrumentBleConfig.LanguageType}
     */
    public void synLanguage(int languageIndex) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x14;
        data[1] = (byte) languageIndex;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步自动开机状态
     *
     * @param status 0=关闭自动开机;1:打开自动开机
     */
    public void synAutoOpen(int status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x16;
        data[1] = (byte) status;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 同步温度值播报
     *
     * @param status 0=关闭温度值播报;1:打开温度值播报
     */
    public void synTempBroadcast(int status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x26;
        data[1] = (byte) status;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 同步测温距离
     *
     * @param type {@link TempInstrumentBleConfig.TestTempDistanceType}
     *             0x80=2.0M
     *             0x40=1.8M
     *             0x20=1.6M
     *             0x10=1.4M
     *             0x08=1.2M
     *             0x04=1.0M
     *             0x02=0.8M
     *             0x01=0.6M
     */
    public void synTestTempDistance(@TempInstrumentBleConfig.TestTempDistanceType int type) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x18;
        data[1] = (byte) type;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 同步体温温度补偿
     *
     * @param status 0x00=+(加);0x01=-(减)
     */
    public void synBodyTempAdd(int status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x1A;
        data[1] = (byte) status;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步计量(物温)温度补偿
     *
     * @param status 0x00=+(加);0x01=-(减)
     */
    public void synObjectTempAdd(int status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x1C;
        data[1] = (byte) status;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 同步报警温度
     *
     * @param tempC 温度,(精确到0.1);10=1.0℃
     * @param tempF 温度,(精确到0.1);10=1.0℉
     */
    public void synAlarmTemp(int tempC, int tempF) {
        if (tempC > 0 && tempC <= 0xFFFF) {
            SendMcuBean sendMcuBean = new SendMcuBean();
            byte[] data = new byte[5];
            data[0] = 0x1E;
            data[1] = (byte) ((tempC >> 8) & 0xFF);
            data[2] = (byte) tempC;
            data[3] = (byte) ((tempF >> 8) & 0xFF);
            data[4] = (byte) tempF;
            sendMcuBean.setHex(CID, data);
            sendData(sendMcuBean);
        }
    }

    /**
     * 同步提示音
     *
     * @param type 音效{@link TempInstrumentBleConfig.SoundType}
     */
    public void synBeepType(@TempInstrumentBleConfig.SoundType int type) {

        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x20;
        data[1] = (byte) type;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步报警音效
     *
     * @param type 音效{@link TempInstrumentBleConfig.SoundType}
     */
    public void synAlarmSoundType(@TempInstrumentBleConfig.SoundType int type) {

        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x24;
        data[1] = (byte) type;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步灵敏度
     *
     * @param level 灵敏度等级
     */
    public void synSensitivity(int level) {

        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x28;
        data[1] = (byte) level;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步自动关机
     *
     * @param type 自动关机{@link TempInstrumentBleConfig.AutoCloseType}
     */
    public void synAutoClose(@TempInstrumentBleConfig.AutoCloseType int type) {

        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x2A;
        data[1] = (byte) type;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 恢复默认值
     * 当 APP 下发默认值时,等待设备回复 .设备收到指令后,先恢复再回复 APP,APP 收到回复 后,APP 需要下发获取设备支持的功能指令,来获取设备当前的设置值.
     */
    public void reset() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = 0x22;
        data[1] = (byte) 0x01;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);

    }

    @Override
    public void sendData(SendDataBean sendDataBean) {
        super.sendData(sendDataBean);
        String data = BleStrUtils.byte2HexStr(sendDataBean.getHex());
        BleLog.i(TAG, "发送的数据:0x[" + data + "]");
        onNotifyData(sendDataBean.getHex(), -2);
    }

    //----------------------接收

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mType == type) {
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据:0x[" + data + "]");
            //解析数据
            dataCheck(hex);
        } else {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnTempListener != null) {
                        mOnTempListener.onData(hex, type);
                    }
                }
            });
        }

    }

    @Override
    public void onNotifyOtherData(byte[] hex) {
        String data = BleStrUtils.byte2HexStr(hex);
        BleLog.i(TAG, "接收的透传数据:" + data);
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnTempListener != null) {
                    mOnTempListener.onData(hex, -3);
                }
            }
        });
    }


//---------------解析数据------

    /**
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        switch (data[0]) {

            //体温温度
            case 0x01:
                //计量温度(物温)
            case 0x02:
                temp(data);
                break;
            //同步单位返回
            case 0x11:
                getUnit(data);
                break;
            //同步音量
            case 0x13:
                getVolume(data);
                break;
            //同步语言
            case 0x15:
                getLanguage(data);
                break;
            //同步自动开机
            case 0x17:
                getAutoOpen(data);
                break;
            //同步测温距离
            case 0x19:
                getTestTempDistance(data);
                break;
            //体表温度校准
            case 0x1B:
                //物温温度校准
            case 0x1D:
                getTempAdd(data);
                break;
            //报警温度值
            case 0x1F:
                getAlarmTemp(data);
                break;
            //同步音效
            case 0x21:
                getBeepType(data);
                break;
            case 0x25:
                getAlarmSoundType(data);
                break;
            //恢复出厂设置
            case 0x23:
                getReset(data);
                break;
            //同步温度值播报
            case 0x27:
                getTempBroadcast(data);
                break;
            //同步灵敏度
            case 0x29:
                getSensitivity(data);
                break;
            //同步自动关机
            case 0x2B:
                getAutoClose(data);
                break;
            //配置信息1
            case (byte) 0x81:
                getConfig1(data);
                break;
            //配置信息2
            case (byte) 0x83:
                getConfig2(data);
                break;
            //配置信息3
            case (byte) 0x85:
                getConfig3(data);
                break;

            //错误码
            case TempInstrumentBleConfig.GET_ERR:
                getErr(data);
                break;
            default:
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnTempListener != null) {
                            mOnTempListener.onData(data, mType);
                        }
                    }
                });
                break;
        }

    }

    /**
     * 解析恢复出厂设置
     *
     * @param data
     */
    private void getReset(byte[] data) {
        if (data.length <= 1) {
            return;
        }
        int status = data[1] & 0xFF;


        //恢复出厂设置成功,需要主动去读取配置信息
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnTempSetListener != null) {
                    mOnTempSetListener.onReset(status);
                }
            }
        });

    }

    /**
     * 解析提示音类型
     *
     * @param data bytes
     */
    private void getBeepType(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int soundId = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentBeepId(soundId);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null) {
                mOnTempSetListener.onBeepType(status, soundId);
            }
        });
    }


    /**
     * 解析报警音效类型
     *
     * @param data bytes
     */
    private void getAlarmSoundType(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int soundId = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentAlarmSoundId(soundId);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null) {
                mOnTempSetListener.onAlarmSoundType(status, soundId);
            }
        });
    }

    /**
     * 解析报警温度
     *
     * @param data
     */
    private void getAlarmTemp(byte[] data) {
        if (data.length <= 5) {
            return;
        }
        float alarmTempC = (((data[1] & 0xFF) << 8) + (data[2] & 0xFF))/10F;
        float alarmTempF = (((data[3] & 0xFF) << 8) + (data[4] & 0xFF))/10F;
        int status = data[5] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentAlarmTempC(alarmTempC);
                mTempInstrumentDeviceConfigBean.setCurrentAlarmTempF(alarmTempF);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onAlarmTemp(status, alarmTempC, alarmTempF);
        });

    }


    /**
     * 解析温度补偿
     */
    private void getTempAdd(byte[] data) {
        if (data.length <= 4) {
            return;
        }
        int type = data[0] & 0xFF;
        int tempPositiveNegativeC = (data[1] >> 6) & 0x01;
        float tempC = data[1] & 0x3F;
        if (tempPositiveNegativeC == 1) {
            tempC = -tempC;
        }
        tempC/=10F;
        int tempPositiveNegativeF = (data[2] >> 6) & 0x01;
        float tempF = ((data[2] & 0x3F) << 8) | (data[3] & 0xFF);
        if (tempPositiveNegativeF == 1) {
            tempF = -tempF;
        }
        tempF/=10F;
        int status = data[4] & 0xFF;
        if (status == 1) {
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(type);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                if (type == 0x1B) {
                    mTempInstrumentDeviceConfigBean.setTempBodyCompensateValueC(tempC);
                    mTempInstrumentDeviceConfigBean.setTempBodyCompensateValueF(tempF);
                } else if (type == 0x1D) {
                    mTempInstrumentDeviceConfigBean.setTempObjectCompensateValueC(tempC);
                    mTempInstrumentDeviceConfigBean.setTempObjectCompensateValueF(tempF);
                }
            }
        }
        float finalTempC = tempC;
        float finalTempF = tempF;
        runOnMainThread(() -> {
            if (type == 0x1B) {
                //体温
                if (mOnTempSetListener != null)
                    mOnTempSetListener.onBodyTempAdd(status, finalTempC, finalTempF);
            } else if (type == 0x1D) {
                //物温
                if (mOnTempSetListener != null)
                    mOnTempSetListener.onObjectTempAdd(status, finalTempC, finalTempF);
            }
        });
    }

    /**
     * 解析测温距离
     */
    private void getTestTempDistance(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int testTempDistanceId = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentTestDistanceId(testTempDistanceId);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onTestTempDistance(status, testTempDistanceId);
        });
    }

    /**
     * 解析自动开机状态
     */
    private void getAutoOpen(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int switchStatus = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setAutoOpen(switchStatus);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onAutoOpen(status, switchStatus);
        });
    }


    /**
     * 解析自动关机
     */
    private void getAutoClose(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int currentAutoCloseId = (data[1] & 0xFF);
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentAutoCloseId(currentAutoCloseId);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onAutoClose(status, currentAutoCloseId);
        });
    }

    /**
     * 解析灵敏度
     */
    private void getSensitivity(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int level = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentSensitivityLevel(level);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onSensitivity(status, level);
        });
    }

    /**
     * 解析温度值播报
     */
    private void getTempBroadcast(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int switchStatus = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setTempBroadcast(switchStatus);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onTempBroadcast(status, switchStatus);
        });
    }

    /**
     * 解析语言
     */
    private void getLanguage(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int languageIndex = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentLanguageIndex(languageIndex);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onLanguage(status, languageIndex);
        });
    }

    /**
     * 解析音量
     */
    private void getVolume(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int volume = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentVolumeLevel(volume);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onVolume(status, volume);
        });

    }

    private TempInstrumentDeviceConfigBean mTempInstrumentDeviceConfigBean;

    /**
     * 解析配置信息1
     */
    private void getConfig1(byte[] data) {
        if (data.length <= 13) {
            return;
        }

        List<Integer> volumeList = new ArrayList<>();
        //1=支持调节音量. 0=不支持调节音量 Bit6: 1=支持 0 档(相当于音量为 0) . 0=不支持
        int volumeSwitch = ((data[1] >> 7) & 0x01);
        //支持 0 档(相当于音量为 0) . 0=不支持 0 档
        int muteStatus = ((data[1] >> 6) & 0x01);
        int volumeGearNumber = 0;
        if (volumeSwitch == 1) {
            volumeGearNumber = data[1] & 0x3F;
            for (int i = 0; i <= volumeGearNumber; i++) {
                if (i == 0 && muteStatus == 0) {
                    //不支持0挡
                    continue;
                }
                volumeList.add(i);
            }
        }
        int currentVolumeLevel = data[2] & 0xFF;

        //支持的语言类型
        List<Long> languageList = new ArrayList<>();
        byte language1 = data[3];
        byte language2 = data[4];
        byte language3 = data[5];
        byte language4 = data[6];
        byte language5 = data[7];
        for (byte i = 0; i < 8; i++) {
            int status = (language1 >> i) & 0x01;
            if (status == 1) {
                int index = i + 1;
                if (LANGUAGE_ID_ALL_TYPE_LIST.size() > index)
                    languageList.add(LANGUAGE_ID_ALL_TYPE_LIST.get(index));//第0个是无语言
            }
        }
        for (byte i = 0; i < 8; i++) {
            int status = (language2 >> i) & 0x01;
            if (status == 1) {
                int index = i + 8 + 1;
                if (LANGUAGE_ID_ALL_TYPE_LIST.size() > index)
                    languageList.add(LANGUAGE_ID_ALL_TYPE_LIST.get(index));//第0个是无语言
            }
        }
        for (byte i = 0; i < 8; i++) {
            int status = (language3 >> i) & 0x01;
            if (status == 1) {
                int index = i + 8 * 2 + 1;
                if (LANGUAGE_ID_ALL_TYPE_LIST.size() > index)
                    languageList.add(LANGUAGE_ID_ALL_TYPE_LIST.get(index));//第0个是无语言
            }
        }
        for (byte i = 0; i < 8; i++) {
            int status = (language4 >> i) & 0x01;
            if (status == 1) {
                int index = i + 8 * 3 + 1;
                if (LANGUAGE_ID_ALL_TYPE_LIST.size() > index)
                    languageList.add(LANGUAGE_ID_ALL_TYPE_LIST.get(index));//第0个是无语言
            }
        }

        for (byte i = 0; i < 8; i++) {
            int status = (language5 >> i) & 0x01;
            if (status == 1) {
                int index = i + 8 * 3 + 1;
                if (LANGUAGE_ID_ALL_TYPE_LIST.size() > index)
                    languageList.add(LANGUAGE_ID_ALL_TYPE_LIST.get(index));//第0个是无语言
            }
        }
        int currentLanguageIndex = (data[8] & 0xFF);

        List<Integer> beepList = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            int status = (data[9] >> i) & 0x01;
            if (status == 1) {
                beepList.add(mSoundTypeList.get(7 - i));
            }
        }

        int currentBeepId = data[10] & 0xFF;

        int autoOpenSwitch = (data[11] >> 7) & 0x01;
        int autoOpen = -1;
        if (autoOpenSwitch == 1) {
            autoOpen = (data[11] >> 6) & 0x01;
        }
        List<Integer> distanceList = new ArrayList<>();
        for (byte i = 0; i < 8; i++) {
            int status = (data[12] >> i) & 0x01;
            if (status == 1) {
                distanceList.add(mTestTempDistanceList.get(i));
            }
        }
        int currentTestTempDistanceId = data[13] & 0xFF;


        if (mTempInstrumentDeviceConfigBean != null) {
            mTempInstrumentDeviceConfigBean.setVolumeList(volumeList);
            mTempInstrumentDeviceConfigBean.setCurrentVolumeLevel(currentVolumeLevel);
            mTempInstrumentDeviceConfigBean.setLanguageIdTypeList(languageList);
            mTempInstrumentDeviceConfigBean.setCurrentLanguageIndex(currentLanguageIndex);
            mTempInstrumentDeviceConfigBean.setBeepList(beepList);
            mTempInstrumentDeviceConfigBean.setCurrentBeepId(currentBeepId);
            mTempInstrumentDeviceConfigBean.setAutoOpen(autoOpen);
            mTempInstrumentDeviceConfigBean.setTestTempDistanceList(distanceList);
            mTempInstrumentDeviceConfigBean.setCurrentTestDistanceId(currentTestTempDistanceId);
        }

        int finalAutoOpen = autoOpen;
        runOnMainThread(() -> {
            if (mOnTempConfigListener != null) {
                mOnTempConfigListener
                        .onDeviceConfig1(volumeList, currentVolumeLevel, languageList, currentLanguageIndex, beepList, currentBeepId, finalAutoOpen, distanceList, currentTestTempDistanceId);
            }
        });
    }

    /**
     * 解析配置信息2
     *
     * @param data
     */
    private void getConfig2(byte[] data) {
        if (data.length <= 12) {
            return;
        }

        float currentAlarmTempC = (((data[1] & 0xFF) << 8) | (data[2] & 0xFF))/10F;

        int tempBodyCompensateSwitchC = (data[3] >> 7) & 0x01;
        int tempBodyCompensatePositiveNegativeC = (data[3] >> 6) & 0x01;
        float tempBodyCompensateC = 0;
        if (tempBodyCompensateSwitchC == 1) {
            tempBodyCompensateC = data[3] & 0x3F;
            if (tempBodyCompensatePositiveNegativeC == 1) {
                //负温度
                tempBodyCompensateC = -tempBodyCompensateC;
            }
        }
        tempBodyCompensateC/=10F;

        int tempObjectCompensateSwitchC = (data[4] >> 7) & 0x01;
        int tempObjectCompensatePositiveNegativeC = (data[4] >> 6) & 0x01;
        float tempObjectCompensateC = 0;
        if (tempObjectCompensateSwitchC == 1) {
            tempObjectCompensateC = data[4] & 0x3F;
            if (tempObjectCompensatePositiveNegativeC == 1) {
                //负
                tempObjectCompensateC = -tempObjectCompensateC;
            }
        }
        tempObjectCompensateC/=10F;

        //当前温度单位
        int currentTempUnit = data[5] & 0xFF;
        //温度值播报
        int tempBroadcastSwitch = (data[6] >> 7) & 0x01;
        int tempBroadcast = -1;
        if (tempBroadcastSwitch == 1) {
            tempBroadcast = (data[6] >> 6) & 0x01;
        }


        List<Integer> alarmSoundList = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            int status = (data[7] >> i) & 0x01;
            if (status == 1) {
                alarmSoundList.add(mSoundTypeList.get(7 - i));
            }
        }

        //报警音效
        int currentAlarmSoundId = data[8] & 0xFF;


        //当前灵敏度
        List<Integer> sensitivityList = new ArrayList<>();
        //1=支持调节灵敏度. 0=不支持调节灵敏度
        int sensitivitySwitch = ((data[9] >> 7) & 0x01);
        int sensitivityGearNumber = 0;
        if (sensitivitySwitch == 1) {
            sensitivityGearNumber = data[9] & 0x3F;
            for (int i = 1; i <= sensitivityGearNumber; i++) {
                sensitivityList.add(i);
            }
        }
        //当前灵敏度等级
        int currentSensitivityLevel = data[10] & 0xFF;

        //
        List<Integer> autoCloseList = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            int status = (data[11] >> i) & 0x01;
            if (status == 1) {
                autoCloseList.add(mAutoCloseTypeList.get(7 - i));
            }
        }

        int currentAutoCloseId = (data[12] & 0xFF);


        if (mTempInstrumentDeviceConfigBean != null) {
            mTempInstrumentDeviceConfigBean.setCurrentAlarmTempC(currentAlarmTempC);
            mTempInstrumentDeviceConfigBean.setCurrentUnit(currentTempUnit);
            mTempInstrumentDeviceConfigBean.setTempBodyCompensateSwitchC(tempBodyCompensateSwitchC == 1);
            mTempInstrumentDeviceConfigBean.setTempBodyCompensateValueC(tempBodyCompensateC);
            mTempInstrumentDeviceConfigBean.setTempObjectCompensateSwitchC(tempObjectCompensateSwitchC == 1);
            mTempInstrumentDeviceConfigBean.setTempObjectCompensateValueC(tempObjectCompensateC);
            mTempInstrumentDeviceConfigBean.setTempBroadcast(tempBroadcast);
            mTempInstrumentDeviceConfigBean.setAlarmSoundList(alarmSoundList);
            mTempInstrumentDeviceConfigBean.setCurrentAlarmSoundId(currentAlarmSoundId);
            mTempInstrumentDeviceConfigBean.setSensitivityList(sensitivityList);//灵敏度列表
            mTempInstrumentDeviceConfigBean.setCurrentSensitivityLevel(currentSensitivityLevel);
            mTempInstrumentDeviceConfigBean.setAutoCloseList(autoCloseList);
            mTempInstrumentDeviceConfigBean.setCurrentAutoCloseId(currentAutoCloseId);

        }


        int finalTempBroadcast = tempBroadcast;
        float finalTempBodyCompensateC = tempBodyCompensateC;
        float finalTempObjectCompensateC = tempObjectCompensateC;
        runOnMainThread(() -> {
            if (mOnTempConfigListener != null) {
                mOnTempConfigListener
                        .onDeviceConfig2(currentAlarmTempC, tempBodyCompensateSwitchC, finalTempBodyCompensateC, tempObjectCompensateSwitchC, finalTempObjectCompensateC, currentTempUnit, finalTempBroadcast,
                                alarmSoundList, currentAlarmSoundId, sensitivityList, currentSensitivityLevel, autoCloseList, currentAutoCloseId);

            }
        });
    }


    /**
     * 解析配置信息3
     *
     * @param data
     */
    private void getConfig3(byte[] data) {
        if (data.length <= 10) {
            return;
        }
        //当前报警值华氏度
        float currentAlarmTempF = (((data[1] & 0xFF) << 8) | (data[2] & 0xFF))/10F;

        int tempBodyCompensateSwitchF = (data[3] >> 7) & 0x01;
        int tempBodyCompensatePositiveNegativeF = (data[3] >> 6) & 0x01;
        float tempBodyCompensateF = 0;
        if (tempBodyCompensateSwitchF == 1) {
            tempBodyCompensateF = ((data[3] & 0x3F) << 8) | (data[4] & 0xFF);
            if (tempBodyCompensatePositiveNegativeF == 1) {
                //负温度
                tempBodyCompensateF = -tempBodyCompensateF;
            }
        }
        tempBodyCompensateF/=10F;
        int tempObjectCompensateSwitchF = (data[5] >> 7) & 0x01;
        int tempObjectCompensatePositiveNegativeF = (data[5] >> 6) & 0x01;
        float tempObjectCompensateF = 0;
        if (tempObjectCompensateSwitchF == 1) {
            tempObjectCompensateF = ((data[5] & 0x3F) << 8) | (data[6] & 0xFF);
            if (tempObjectCompensatePositiveNegativeF == 1) {
                //负
                tempObjectCompensateF = -tempObjectCompensateF;
            }
        }
        tempObjectCompensateF/=10F;

        float alarmTempMinC = (((data[7] & 0xFF) << 8) | (data[8] & 0xFF))/10F;
        float alarmTempMaxC = (((data[9] & 0xFF) << 8) | (data[10] & 0xFF))/10F;


        if (mTempInstrumentDeviceConfigBean != null) {
            mTempInstrumentDeviceConfigBean.setCurrentAlarmTempF(currentAlarmTempF);
            mTempInstrumentDeviceConfigBean.setTempBodyCompensateSwitchF(tempBodyCompensateSwitchF == 1);
            mTempInstrumentDeviceConfigBean.setTempBodyCompensateValueF(tempBodyCompensateF );
            mTempInstrumentDeviceConfigBean.setTempObjectCompensateSwitchF(tempObjectCompensateF == 1);
            mTempInstrumentDeviceConfigBean.setTempObjectCompensateValueF(tempObjectCompensateF );
            mTempInstrumentDeviceConfigBean.setAlarmTempMinC(alarmTempMinC );
            mTempInstrumentDeviceConfigBean.setAlarmTempMaxC(alarmTempMaxC );

        }


        float finalTempObjectCompensateF = tempObjectCompensateF;
        float finalTempBodyCompensateF = tempBodyCompensateF;
        runOnMainThread(() -> {
            if (mOnTempConfigListener != null) {
                mOnTempConfigListener
                        .onDeviceConfig3(currentAlarmTempF, tempBodyCompensateSwitchF, finalTempBodyCompensateF, tempObjectCompensateSwitchF, finalTempObjectCompensateF, alarmTempMinC, alarmTempMaxC);
                if (mTempInstrumentDeviceConfigBean != null) {
                    mOnTempConfigListener.onDeviceConfig(mTempInstrumentDeviceConfigBean);
                }
            }
        });
    }


    /**
     * 温度解析
     */
    private void temp(byte[] data) {
        if (data.length <= 4) {
            return;
        }
        int type = data[0] & 0xFF;
        int temp = ((data[1] & 0xFF) << 8) + (data[2] & 0xFF);
        int unit = data[3] & 0xFF;
        int decimal = data[4] & 0xFF;

        String unitStr = "℃";
        if (unit == 1) {
            unitStr = "℉";
        }
        runOnMainThread(() -> {
            if (mOnTempListener != null) {
                mOnTempListener.onTemp(type, temp, decimal, unit);
            }
        });
        BleLog.i(TAG, (type == 1 ? "当前体温:" : "当前物温") + temp + unitStr);
    }


    private void getUnit(byte[] data) {
        if (data.length <= 2) {
            return;
        }
        int tempUnit = data[1] & 0xFF;
        int status = data[2] & 0xFF;
        if (status == 1) {
            //失败
            runOnMainThread(() -> {
                if (mOnTempSetListener != null)
                    mOnTempSetListener.synErrToConfig(data[0] & 0xFF);
            });
        } else {
            if (mTempInstrumentDeviceConfigBean != null) {
                mTempInstrumentDeviceConfigBean.setCurrentUnit(tempUnit);
            }
        }
        runOnMainThread(() -> {
            if (mOnTempSetListener != null)
                mOnTempSetListener.onUnit(status, tempUnit);
        });
    }

    /**
     * 0：体温过高
     * 1：体温过低
     * 2：测量出错
     */
    private void getErr(byte[] data) {
        final int status = data[1] & 0xFF;
        String statusStr = "体温过高";
        switch (status) {
            case TempInstrumentBleConfig.TempErrType.TEMP_ERR_HI:
                statusStr = "体温过高";
                break;
            case TempInstrumentBleConfig.TempErrType.TEMP_ERR_LO:
                statusStr = "体温过低";
                break;

        }

        BleLog.i(TAG, statusStr);
        runOnMainThread(() -> {
            if (mOnTempListener != null) {
                mOnTempListener.onErr(status);
            }
        });
    }

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (!status) {
            disconnect();
        }
        BleLog.i(TAG, "握手:" + status);
    }
//---------------解析数据------

    public interface onTempConfigListener {
        /**
         * @param volumeList                支持的音量档位,空集合代表不支持
         * @param currentVolumeLevel        当前的音量等级
         * @param languageList              支持的语言列表
         * @param currentLanguageId         当前的语言ID
         * @param beepList                  支持的提示音音效列表
         * @param currentBeepId             当前的音效ID
         * @param autoOpen                  -1=不支持,0=关闭自动开机(默认),1=打开自动开机
         * @param distanceList              支持的测温距离列表
         * @param currentTestTempDistanceId 当前的测温距离ID
         */
        default void onDeviceConfig1(List<Integer> volumeList, int currentVolumeLevel, List<Long> languageList, int currentLanguageId, List<Integer> beepList, int currentBeepId, int autoOpen,
                                     List<Integer> distanceList, int currentTestTempDistanceId) {
        }

        /**
         * @param currentAlarmTempC           当前报警温度(℃)
         * @param tempBodyCompensateSwitchC   是否支持体温补偿(1=支持, 0=不支持)(℃)
         * @param tempBodyCompensateC         体温补偿(℃)
         * @param tempObjectCompensateSwitchC 是否支持物温补偿(1=支持, 0=不支持)(℃)
         * @param tempObjectCompensateC       物温补偿(℃)
         * @param currentTempUnit             当前单位{@link TempInstrumentBleConfig.UnitType}
         * @param tempBroadcast               -1=不支持,0=关闭温度值播报,1=打开温度值播报
         * @param alarmSoundList              支持的报警音效列表
         * @param currentAlarmSoundId         报警音效ID
         * @param sensitivityList             支持的灵敏度列表
         * @param currentSensitivityLevel     当前选中的灵敏度等级
         * @param autoCloseList               支持的自动关机列表
         * @param currentAutoCloseId          当前选中的自动关机ID
         */
        default void onDeviceConfig2(float currentAlarmTempC, int tempBodyCompensateSwitchC, float tempBodyCompensateC, int tempObjectCompensateSwitchC, float tempObjectCompensateC
                , int currentTempUnit, int tempBroadcast, List<Integer> alarmSoundList, int currentAlarmSoundId, List<Integer> sensitivityList, int currentSensitivityLevel,
                                     List<Integer> autoCloseList, int currentAutoCloseId) {
        }


        /**
         * @param currentAlarmTempF           当前报警温度(℉)
         * @param tempBodyCompensateSwitchF   是否支持体温补偿(1=支持, 0=不支持)(℉)
         * @param tempBodyCompensateF         体温补偿(℉)
         * @param tempObjectCompensateSwitchF 是否支持物温补偿(1=支持, 0=不支持)(℉)
         * @param tempObjectCompensateF       物温补偿(℉)
         * @param alarmTempMinC               报警值下限(℃)
         * @param alarmTempMaxC               报警值上限(℃)
         */
        default void onDeviceConfig3(float currentAlarmTempF, int tempBodyCompensateSwitchF, float tempBodyCompensateF, int tempObjectCompensateSwitchF, float tempObjectCompensateF, float alarmTempMinC,
                                     float alarmTempMaxC) {
        }

        /**
         * 设备配置信息
         *
         * @param deviceConfigBean TempInstrumentDeviceConfigBean
         */
        default void onDeviceConfig(TempInstrumentDeviceConfigBean deviceConfigBean) {
        }
    }


    public interface onTempListener {
        /**
         * @param data
         * @param type -2=发送,-3=透传
         */
        void onData(byte[] data, int type);

        /**
         * 稳定温度
         *
         * @param type     0=体温;1=物温
         * @param temp     体温原始数据
         * @param decimal  小数位
         * @param tempUnit 体温单位
         */
        void onTemp(int type, int temp, int decimal, int tempUnit);

        /**
         * 错误指令;
         * 0：温度过高(高于 42.0℃显示为 Hi)
         * 1：温度过低(低于 32.0℃显示为 Lo)
         *
         * @param errType 异常类型{@link TempInstrumentBleConfig.TempErrType}
         */
        void onErr(int errType);

        /**
         * 设置单位返回<br>
         *
         * @param status   0=成功,1=失败
         * @param tempUnit 当前单位 {@link TempInstrumentBleConfig.UnitType}
         */
        void onUnit(int status, int tempUnit);
    }


    public interface onTempSetListener {


        /**
         * 恢复出厂设置
         *
         * @param status 0=成功,1=失败
         */
        void onReset(int status);

        /**
         * 提示音
         *
         * @param status  0=成功,1=失败
         * @param soundId 音效ID{@link TempInstrumentBleConfig.SoundType}
         */
        void onBeepType(int status, int soundId);

        /**
         * 报警音效类型
         *
         * @param status  0=成功,1=失败
         * @param soundId 音效ID{@link TempInstrumentBleConfig.SoundType}
         */
        void onAlarmSoundType(int status, int soundId);

        /**
         * 报警温度
         *
         * @param status 0=成功,1=失败
         * @param tempC  温度(C)
         * @param tempF  温度(F)
         */
        void onAlarmTemp(int status, float tempC, float tempF);

        /**
         * 体温温度补偿
         *
         * @param status 0x00=成功;0x01=失败;0x02=已达最大值;0x03已达最小值
         * @param tempC  温度(摄氏度)
         * @param tempF  温度(华氏度)
         */
        void onBodyTempAdd(int status, float tempC, float tempF);

        /**
         * 物温温度补偿
         *
         * @param status 0x00=成功;0x01=失败;0x02=已达最大值;0x03已达最小值
         * @param tempC  温度(摄氏度)
         * @param tempF  温度(华氏度)
         */
        void onObjectTempAdd(int status, float tempC, float tempF);

        /**
         * 测温距离
         *
         * @param status             0=成功,1=失败
         * @param testTempDistanceId 距离ID{@link TempInstrumentBleConfig.TestTempDistanceType}
         */
        void onTestTempDistance(int status, int testTempDistanceId);

        /**
         * 自动开机
         *
         * @param status       0=成功,1=失败
         * @param switchStatus 0=关闭自动开机,1=打开自动开机
         */
        void onAutoOpen(int status, int switchStatus);

        /**
         * 温度值播报
         *
         * @param status       0=成功,1=失败
         * @param switchStatus 0=关闭,1=打开
         */
        void onTempBroadcast(int status, int switchStatus);

        /**
         * 同步语言
         *
         * @param status     0=成功,1=失败
         * @param languageId 语言ID{@link TempInstrumentBleConfig.LanguageType}
         */
        void onLanguage(int status, int languageId);


        /**
         * 同步音量
         *
         * @param status      0=成功,1=失败
         * @param volumeLevel 当前音量等级
         */
        void onVolume(int status, int volumeLevel);

        /**
         * 设置单位返回<br>
         *
         * @param status   0=成功,1=失败
         * @param tempUnit 当前单位 {@link TempInstrumentBleConfig.UnitType}
         */
        void onUnit(int status, int tempUnit);


        /**
         * 设置灵敏度返回<br>
         *
         * @param status 0=成功,1=失败
         * @param level  当前等级
         */
        void onSensitivity(int status, int level);

        /**
         * 设置自动关机返回<br>
         *
         * @param status      0=成功,1=失败
         * @param autoCloseId 当前状态{@link TempInstrumentBleConfig.AutoCloseType}
         */
        void onAutoClose(int status, int autoCloseId);

        /**
         * 同步指令失败((APP 需要重新同步设置列表)
         *
         * @param cmdType 指令类型
         */
        void synErrToConfig(int cmdType);
    }


    //------------------set/get--------------


    public TempInstrumentDeviceConfigBean getTempInstrumentDeviceConfigBean() {
        return mTempInstrumentDeviceConfigBean;
    }

    public void setOnBleVersionListener(OnBleVersionListener bleVersionListener) {
        if (mBleDevice != null)
            mBleDevice.setOnBleVersionListener(bleVersionListener);
    }

    public void setOnMcuParameterListener(OnMcuParameterListener mcuParameterListener) {
        if (mBleDevice != null)
            mBleDevice.setOnMcuParameterListener(mcuParameterListener);
    }

    public void setOnCompanyListener(OnBleCompanyListener companyListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleCompanyListener(companyListener);
        }
    }


    public void setOnTempSetListener(onTempSetListener onTempSetListener) {
        mOnTempSetListener = onTempSetListener;
    }

    public void setOnTempListener(onTempListener onTempListener) {
        mOnTempListener = onTempListener;
    }

    public void setOnTempConfigListener(onTempConfigListener onTempConfigListener) {
        mOnTempConfigListener = onTempConfigListener;
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper());


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }


    /**
     * int转byte[]
     *
     * @param data >0 && <16777215
     * @return 3byte
     */
    private byte[] intTo3Byte(int data) {
        byte[] bytes = new byte[3];
        if (data < 0 || data > 0xFFFFFF) {
            return bytes;
        }
        bytes[0] = (byte) data;
        bytes[1] = (byte) ((data >> 8) & 0xFF);
        bytes[2] = (byte) ((data >> 16) & 0xFF);
        return bytes;
    }
}
