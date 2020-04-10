package cn.net.aicare.modulelibrary.module.thermometer;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.config.BleDeviceConfig;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.Arrays;

/**
 * xing<br>
 * 2019/5/11<br>
 * 体温计
 */
public class TempDeviceData extends BaseBleDeviceData {
    private String TAG = TempDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private int mType = BleDeviceConfig.THERMOMETER;
    private byte[] CID;
    private static BleDevice mBleDevice = null;
    private static TempDeviceData mDevice = null;

    public static TempDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mDevice == null) {
                    mDevice = new TempDeviceData(bleDevice);

                }
            } else {
                mDevice = new TempDeviceData(bleDevice);
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

    private TempDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
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
        if (mType == type||type== BleDeviceConfig.DEVICE_ALL) {
            BleLog.i(TAG, "接收到的原始数据:" + Arrays.toString(hex));
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

            case TempBleConfig.TEMP:
                temp(data);
                break;
            case TempBleConfig.TEMP_NOW:
                tempNow(data);
                break;
            case TempBleConfig.GET_UNIT:
                getUnit(data);
                break;
            case TempBleConfig.GET_ERR:
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

    private void tempNow(byte[] data) {
        int temp = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];

        String unitStr = "℃";
        if (unit == 1) {
            unitStr = "℉";
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.tempNow(temp, decimal, unit);
                }
            }
        });
        BleLog.i(TAG, "当前温度:" + temp + unitStr);
    }

    private void temp(byte[] data) {
        int temp = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];

        String unitStr = "℃";
        if (unit == 1) {
            unitStr = "℉";
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.temp(temp, decimal, unit);
                }
            }
        });
        BleLog.i(TAG, "当前温度:" + temp + unitStr);
    }

    public void setUnit(byte unit) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = TempBleConfig.SET_UNIT;
        data[1] = unit;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    private void getUnit(byte[] data) {
        //todo 获取设置的单位
        if (data.length > 1) {
            final byte status = data[1];
            switch (status) {
                case CmdConfig.SETTING_SUCCESS:
                    BleLog.i(TAG, "设置单位成功");
                    break;
                case CmdConfig.SETTING_FAILURE:
                    BleLog.i(TAG, "设置单位失败");

                    break;
                case CmdConfig.SETTING_ERR:
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

    /**
     * 0：体温过高
     * 1：体温过低
     * 2：测量出错
     */
    private void getErr(byte[] data) {
        final byte status = data[1];
        String statusStr = "体温过高";
        switch (status) {
            case 0:
                statusStr = "体温过高";
                break;
            case 1:
                statusStr = "体温过低";
                break;
            case 2:
                statusStr = "测量出错";
                break;


        }

        BleLog.i(TAG, statusStr);
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getErr(status);
                }
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


    public interface onNotifyData {
        void onData(byte[] data, int type);

        /**
         * 稳定温度
         *
         * @param temp     体温原始数据
         * @param decimal  小数位
         * @param tempUnit 体温单位
         */
        void temp(int temp, int decimal, byte tempUnit);

        /**
         * 实时温度
         *
         * @param temp     体温原始数据
         * @param decimal  小数位
         * @param tempUnit 体温单位
         */
        void tempNow(int temp, int decimal, byte tempUnit);

        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        void getUnit(byte status);

        /**
         * 错误指令
         */
        void getErr(byte status);
    }

    //------------------set/get--------------

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
