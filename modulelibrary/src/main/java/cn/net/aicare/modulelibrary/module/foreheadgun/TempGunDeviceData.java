package cn.net.aicare.modulelibrary.module.foreheadgun;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

/**
 * xing<br>
 * 2019/5/11<br>
 * 额温枪
 */
public class TempGunDeviceData extends BaseBleDeviceData {
    private String TAG = TempGunDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private int mType = TempGunBleConfig.INFRARED_THERMOMETER;
    private byte[] CID;
    /**
     * 设置单位成功
     */
    private final byte setYes = 0x00;
    /**
     * 设置单位失败
     */
    private final byte setNo = 0x01;
    /**
     * 设置单位错误
     */
    private final byte setErr = 0x02;

    private static BleDevice mBleDevice = null;
    private static TempGunDeviceData mDevice = null;
    private static String mMac;

    public static TempGunDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mDevice == null) {
                    mDevice = new TempGunDeviceData(bleDevice);
                }
            } else {
                mDevice = new TempGunDeviceData(bleDevice);
            }
        }
        return mDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mDevice != null) {
            mOnNotifyData = null;
            mDevice = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private TempGunDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        mMac = bleDevice.getMac();
        init();
    }


    private void init() {
        //TODO 可进行额温枪特有的初始化操作
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (mType == type) {
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据:" + data);

            //解析数据
            dataCheck(hex);
        }


    }



    //---------------解析数据------

    /**
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        switch (data[0]) {

            case TempGunBleConfig.TEMP:
            case TempGunBleConfig.TEMP_NOW:
            case TempGunBleConfig.TEMP_EAR:
            case TempGunBleConfig.TEMP_EAR_NOW:
            case TempGunBleConfig.TEMP_SURROUNDING:
            case TempGunBleConfig.TEMP_SURROUNDING_NOW:
            case TempGunBleConfig.TEMP_BODY:
            case TempGunBleConfig.TEMP_BODY_NOW:
                temp(data);
                break;
            case TempGunBleConfig.GET_UNIT:
                getUnit(data);
                break;
            case TempGunBleConfig.GET_ERR:
                getErr(data);
                break;
            default:
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnNotifyData != null) {
                            mOnNotifyData.onData(data, mType);
                        }
                    }
                });
                break;
        }

    }

    private void temp(byte[] data) {
        int cmdType=data[0];
        int temp = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];
        runOnMainThread(() -> {

            switch (cmdType) {
                case TempGunBleConfig.TEMP:
                    if (mOnNotifyData != null) {
                        mOnNotifyData.temp(temp, decimal, unit);
                    }
                case TempGunBleConfig.TEMP_NOW:
                    break;
                case TempGunBleConfig.TEMP_EAR:
                    if (mOnNotifyData != null) {
                        mOnNotifyData.tempEar(temp, decimal, unit);
                    }
                case TempGunBleConfig.TEMP_EAR_NOW:
                    break;
                case TempGunBleConfig.TEMP_SURROUNDING:
                    if (mOnNotifyData != null) {
                        mOnNotifyData.tempSurrounding(temp, decimal, unit);
                    }
                case TempGunBleConfig.TEMP_SURROUNDING_NOW:
                    break;
                case TempGunBleConfig.TEMP_BODY:
                    if (mOnNotifyData != null) {
                        mOnNotifyData.tempBody(temp, decimal, unit);
                    }
                case TempGunBleConfig.TEMP_BODY_NOW:
                    break;
            }


        });
        String unitStr = "℃";
        if (unit == 1) {
            unitStr = "℉";
        }

        BleLog.i(TAG, "当前温度:" + temp + unitStr+"||类型:"+cmdType);
    }

    public void setUnit(byte unit) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = TempGunBleConfig.SET_UNIT;
        data[1] = unit;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    private void getUnit(byte[] data) {
        //todo 获取设置的单位
        if (data.length > 1) {
            byte status = data[1];
            switch (status) {
                case setYes:
                    BleLog.i(TAG, "设置单位成功");
                    break;
                case setNo:
                    BleLog.i(TAG, "设置单位失败");

                    break;
                case setErr:
                    BleLog.i(TAG, "设置单位错误");

                    break;
            }
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.getUnit(status);
                    }
                }
            });

        }
    }

    private void getErr(byte[] data) {
        byte status = data[1];
        String statusStr = "温度过高";
        if (status == 1) {
            statusStr = "温度过低";
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getErr(status);
                }
            }
        });
        BleLog.i(TAG, statusStr);
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


    public interface onNotifyData {
        /**
         * 不能解析的透传数据
         */
        void onData(byte[] data, int type);

        /**
         * 错误指令
         */
        void getErr(byte status);

        /**
         * 切换单位
         */
        void getUnit(byte status);

        /**
         * 稳定温度
         *
         * @param temp    温度原始数据
         * @param decimal 小数位
         * @param unit    单位
         */
        default void temp(int temp, int decimal, byte unit) {
        }

        default void tempNow(int temp, int decimal, byte unit) {
        }

        /**
         * 稳定耳温
         *
         * @param temp    温度原始数据
         * @param decimal 小数位
         * @param unit    单位
         */
        default void tempEar(int temp, int decimal, byte unit) {
        }

        default void tempEarNow(int temp, int decimal, byte unit) {
        }

        /**
         * 稳定环境温度
         *
         * @param temp    温度原始数据
         * @param decimal 小数位
         * @param unit    单位
         */
        default void tempSurrounding(int temp, int decimal, byte unit) {
        }

        default void tempSurroundingNow(int temp, int decimal, byte unit) {
        }

        /**
         * 稳定物体温度
         *
         * @param temp    温度原始数据
         * @param decimal 小数位
         * @param unit    单位
         */
        default void tempBody(int temp, int decimal, byte unit) {
        }

        default void tempBodyNow(int temp, int decimal, byte unit) {
        }
    }


    //------------------set/get----------


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

    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper());


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }

}
