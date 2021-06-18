package cn.net.aicare.modulelibrary.module.sphygmomanometer;

import android.os.Handler;
import android.os.Looper;

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
 * 血压计(sphygmomanometer)
 */
public class SphyDeviceData extends BaseBleDeviceData {
    private String TAG = SphyDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private int mType = SphyBleConfig.BLOOD_PRESSURE;
    private byte[] CID;
    private static BleDevice mBleDevice;

    private static SphyDeviceData mDevice = null;

    public static SphyDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mDevice == null) {
                    mDevice = new SphyDeviceData(bleDevice);
                }
            } else {
                mDevice = new SphyDeviceData(bleDevice);
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

    /**
     * 断开连接(Disconnect)
     */
    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private SphyDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {
        //可进行额温枪特有的初始化操作
        //It can carry out the unique initialization operation of the front temperature gun
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (mType == type ) {
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

            case SphyBleConfig.SPHY:
                sphyData(data);
                break;
            case SphyBleConfig.SPHY_NOW:
                sphyDataNow(data);
                break;
            case SphyBleConfig.SHPY_CMD:
                getSphyCmd(data);
                break;

            case SphyBleConfig.GET_UNIT:
                getUnit(data);
                break;
            case SphyBleConfig.GET_ERR:
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

    private void getSphyCmd(final byte[] data) {
        switch (data[1]) {
            case SphyBleConfig.SHPY_CMD_START:
                BleLog.i(TAG, "开始测量");
                break;
            case SphyBleConfig.SHPY_CMD_STOP:
                BleLog.i(TAG, "停止测试");
                break;
            case SphyBleConfig.SHPY_CMD_MCU_START:
                BleLog.i(TAG, "mcu 开机");
                break;
            case SphyBleConfig.SHPY_CMD_MCU_STOP:
                BleLog.i(TAG, "mcu 关机");
                break;
        }

        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getSphyCmd(data[1]);
                }
            }
        });

    }


    /**
     * 开始测量(Start measurement)
     */
    public void startMeasuring() {
        setSphyCmd(SphyBleConfig.SHPY_CMD_START);
    }

    /**
     * 停止测量(停止测量)
     */
    public void stopMeasuring() {
        setSphyCmd(SphyBleConfig.SHPY_CMD_STOP);
    }

    /**
     * 开机(Boot)
     */
    public void startMcu() {
        setSphyCmd(SphyBleConfig.SHPY_CMD_MCU_START);
    }

    /**
     * 关机(Shut down)
     */
    public void stoptMcu() {
        setSphyCmd(SphyBleConfig.SHPY_CMD_MCU_STOP);
    }


    /**
     * 操作指令
     */
    private void setSphyCmd(byte cmd) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = SphyBleConfig.SHPY_CMD;
        data[1] = cmd;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);

    }


    /**
     * 实时数据(Real-time data)
     */
    private void sphyDataNow(byte[] data) {
        //舒张压
        int dia = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        //收缩压
        int sys = ((data[3] & 0xff) << 8) + (data[4] & 0xff);
        //心率
        final int pul = (data[5] & 0xff);
        final int unit = (data[6] & 0xff);
        int decimal = data[7];
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.sphyDataNow(dia, sys, decimal, pul, unit);
            }
        });


    }

    /**
     * 稳定数据(Stable data)
     */
    private void sphyData(byte[] data) {
        //舒张压
        int dia = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        //收缩压
        int sys = ((data[3] & 0xff) << 8) + (data[4] & 0xff);
        //心率
        final int pul = (data[5] & 0xff);
        final int unit = (data[6] & 0xff);
        int decimal = data[7];
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.sphyData(dia, sys, decimal, pul, unit);
            }
        });

    }


    /**
     * 设置单位(Set unit)
     * @param unit
     */
    public void setUnit(byte unit) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = SphyBleConfig.SET_UNIT;
        data[1] = unit;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    private void getUnit(byte[] data) {
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

    private void getErr(byte[] data) {
        final byte status = data[1];
        String statusStr = "未找到高压";
        if (status == 1) {
            statusStr = "无法正常加压，请检查是否插入袖带，或者重新插拔袖带气管";
        } else if (status == 2) {
            statusStr = "电量低";
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
        /**
         * 不能解析的数据
         */
        void onData(byte[] data, int type);

        /**
         * 设置操作指令(Set operation instructions)
         *
         * @param cmd {@link SphyBleConfig.SHPY_CMD}
         */
        void getSphyCmd(byte cmd);

        /**
         * 实时数据(Real-time data)
         *
         * @param dia     舒张压(Diastolic blood pressure)
         * @param sys     收缩压(Systolic blood pressure)
         * @param decimal 小数位(Decimal places)
         * @param pul     心率(Heart rate)
         * @param unit    单位(unit)
         */
        void sphyDataNow(int dia, int sys, int decimal, int pul, int unit);

        /**
         * 稳定数据(Stable data)
         *
         * @param dia     舒张压(Diastolic blood pressure)
         * @param sys     收缩压(Systolic blood pressure)
         * @param decimal 小数位(Decimal places)
         * @param pul     心率(Heart rate)
         * @param unit    单位(unit)
         */
        void sphyData(int dia, int sys, int decimal, int pul, int unit);

        /**
         * 设置单位返回(Set Unit Return)
         *
         * @param unit {@link CmdConfig.SETTING_SUCCESS,CmdConfig.SETTING_FAILURE,CmdConfig.SETTING_ERR}
         */
        void getUnit(int unit);

        /**
         * 错误指令
         *
         * @param status {@link SphyBleConfig.GET_ERR}
         */
        void getErr(byte status);
    }

    //--------------set/get-------


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
