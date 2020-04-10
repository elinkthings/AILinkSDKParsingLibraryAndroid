package cn.net.aicare.modulelibrary.module.height;

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

/**
 * xing<br>
 * 2019/5/11<br>
 * 身高仪
 */
public class HeightDeviceData extends BaseBleDeviceData {
    private String TAG = HeightDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private int mType = BleDeviceConfig.HEIGHT_METER;
    /**
     * 发送数据时标记自己的cid
     */
    private byte[] CID;

    private static BleDevice mBleDevice = null;
    private static HeightDeviceData mHeightDevice = null;

    public static HeightDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice==bleDevice) {
                if (mHeightDevice == null) {
                    mHeightDevice = new HeightDeviceData(bleDevice);
                }
            } else {
                mHeightDevice = new HeightDeviceData(bleDevice);
            }
        }
        return mHeightDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mHeightDevice != null) {
            mOnNotifyData = null;
            mHeightDevice = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private HeightDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {
        CID = new byte[2];
        CID[0] = (byte) ((mType>> 8 ) & 0xff);
        CID[1] = (byte) (mType);
    }


    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (mType == type||type== BleDeviceConfig.DEVICE_ALL) {
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

            case HeightBleConfig.HEIGHT:
                height(data);
                break;
            case HeightBleConfig.GET_UNIT:
                getUnit(data);
                break;
            case HeightBleConfig.GET_ERR:
                getErr(data);
                break;
            default:
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnNotifyData != null) {
                            mOnNotifyData.onData(data,mType);
                        }
                    }
                });
                break;
        }

    }

    private void height(byte[] data) {
        int height = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unitHeight = data[3];
        int decimalHeight = data[4];
        String unitStrHeight = "cm";
        switch (unitHeight) {
            case HeightBleConfig.HEIGHT_UNIT_CM:
                unitStrHeight = "cm";
                break;
            case HeightBleConfig.HEIGHT_UNIT_INCH:
                unitStrHeight = "inch";
                break;
            case HeightBleConfig.HEIGHT_UNIT_FT_IN:
                unitStrHeight = "ft-in";
                break;
        }
        String unitStrWeight = "";
        if (data.length>5){
            int weight = ((data[5] & 0xff) << 8) + (data[6] & 0xff);
            byte unitWeight = data[7];
            int decimalWeight = data[8];
            switch (unitWeight) {

                case HeightBleConfig.WEIGHT_UNIT_KG:
                    unitStrWeight = "kg";
                    break;
                case HeightBleConfig.WEIGHT_UNIT_FG:
                    unitStrWeight = "斤";
                    break;
                case HeightBleConfig.WEIGHT_UNIT_LB:
                    unitStrWeight = "lb";
                    break;
                case HeightBleConfig.WEIGHT_UNIT_OZ:
                    unitStrWeight = "oz";
                    break;
                case HeightBleConfig.WEIGHT_UNIT_ST:
                    unitStrWeight = "st";
                    break;
                case HeightBleConfig.WEIGHT_UNIT_G:
                    unitStrWeight = "g";
                    break;

            }

            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.height(height, decimalHeight, unitHeight, weight, decimalWeight
                                , unitWeight);
                    }
                }
            });
            BleLog.i(TAG,
                    "当前身高:" + height + unitStrHeight + "\n" + "当前体重:" + weight + unitStrWeight + "\n");
        }else {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.height(height, decimalHeight, unitHeight, -1, -1
                                , (byte) -1);
                    }
                }
            });
            BleLog.i(TAG,
                    "当前身高:" + height + unitStrHeight + "\n" + "当前体重:-1" + unitStrWeight + "\n");
        }




    }

    /**
     * 设置单位
     *
     * @param unitHeight 身高单位
     * @param unitWeight 体重单位
     */
    public void setUnit(byte unitHeight, byte unitWeight) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[3];
        data[0] = HeightBleConfig.SET_UNIT;
        data[1] = unitHeight;
        data[2] = unitWeight;
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
     * 0：测量失败
     */
    private void getErr(byte[] data) {
        final byte status = data[1];
        String statusStr = "测量失败";
        BleLog.i(TAG, "错误码:" + status + "=>" + statusStr);
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
       default void onData(byte[] data, int type){}

        /**
         * @param height        身高
         * @param decimalHeight 身高小数位
         * @param heightUnit    身高单位
         * @param weight        体重
         * @param decimalWeight 小数位
         * @param weightUnit    体重单位
         */
        default void height(int height, int decimalHeight, byte heightUnit, int weight,
                            int decimalWeight, byte weightUnit){}

        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void getUnit(byte status){}

        /**
         * 错误指令
         */
        default void getErr(byte status){}
    }

    //------------------set/get--------


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
