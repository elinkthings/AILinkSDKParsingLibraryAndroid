package cn.net.aicare.modulelibrary.module.SmartMask;

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
 * 2020/09/15<br>
 * 智能口罩(Baby scale)
 */
public class SmartMaskDeviceData extends BaseBleDeviceData {
    private String TAG = SmartMaskDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private byte[] CID;
    private int mType = SmartMaskBleConfig.SMART_MASK;
    private static BleDevice mBleDevice = null;
    private static SmartMaskDeviceData mBabyDevice = null;

    public static SmartMaskDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mBabyDevice == null) {
                    mBabyDevice = new SmartMaskDeviceData(bleDevice);

                }
            } else {
                mBabyDevice = new SmartMaskDeviceData(bleDevice);
            }
        }
        return mBabyDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mBabyDevice != null) {
            mOnNotifyData = null;
            mBabyDevice = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private SmartMaskDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {
        //TODO 可进行婴儿秤特有的初始化操作
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    //------------------发送---------------


    /**
     * 请求获取设备状态
     */
    public void setStatus() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[3];
        data[0] = SmartMaskBleConfig.SET_STATUS;
        data[1] = (byte) 0x01;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 更换滤网
     */
    public void setReplaceFilter() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = SmartMaskBleConfig.SET_FILTER;
        data[1] = (byte) 0x01;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 设置风扇状态
     *
     * @param status 0：关闭风扇;1：1 档风扇;2: 2 档风扇
     */
    public void setFanStatus(int status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = SmartMaskBleConfig.SET_FAN;
        data[1] = (byte) status;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    //-------------------接收-----------------

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (mType == type) {
            if (hex == null) {
                BleLog.i(TAG, "接收到的数据:null");
                return;
            }
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据:" + data);
            //校验解析
            dataCheck(hex);
        }
    }


    //----------------解析数据------

    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        if (data == null)
            return;
        switch (data[0]) {
            //设备状态
            case SmartMaskBleConfig.GET_STATUS:
                getStatus(data);
                break;

            //滤网设置指令
            case SmartMaskBleConfig.GET_FILTER:
                getFilter(data);
                break;

            //风扇设置指令
            case SmartMaskBleConfig.GET_FAN:
                getFan(data);
                break;

            default:
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onData(data, mType);
                    }
                });

                break;
        }

    }

    /**
     * 设备状态
     *
     * @param data Payload数据
     */
    private void getStatus(byte[] data) {
        if (data.length >= 12) {
            //空气指数(无效则该值为 0xFFFF)
            int airIndex = ((data[1] & 0xff) << 8) + (data[2] & 0xff);

            //风扇状态
            //0：关闭状态
            //1：1 档状态
            //2：2 档状态
            int fanStatus = (data[3] & 0xff);

            //电池电量，单位 %;0-100
            int power = (data[4] & 0xff);

            //电池状态
            //1：充电
            //2：非充电
            //如该值无效则为 0xFF
            int powerStatus = (data[5] & 0xff);

            //电池续航
            //大端序。单位：分钟
            //无效则该值为 0xFFFF
            int batteryRemaining = ((data[6] & 0xff) << 8) + (data[7] & 0xff);

            //呼吸频率,单位，次/min
            int breathRate = (data[8] & 0xff);

            //呼吸状态
            //1：呼气
            //2：吸气
            //无效则该值为 0xFF
            int breathState = (data[9] & 0xff);

            //滤网的总工作时长
            //大端序，单位：min
            int filterDuration = ((data[10] & 0xff) << 8) + (data[11] & 0xff);

            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onStatus(airIndex, fanStatus, power, powerStatus, batteryRemaining, breathRate, breathState, filterDuration);
                }
            });
        }
    }


    /**
     * 滤网状态回复
     */
    private void getFilter(byte[] data) {
        int status = data[1] & 0xff;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onFilter(status);
            }
        });

    }


    /**
     * 风扇设置指令
     */
    private void getFan(byte[] data) {
        byte status = data[1];//状态
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onFan(status);
            }
        });
    }


    //----------------解析数据------


    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (!status) {
            disconnect();
        }
        BleLog.i(TAG, "握手:" + status);
    }


    public interface onNotifyData {
        /**
         * 不能识别的透传数据
         * Unrecognized pass-through data
         */
        default void onData(byte[] data, int type) {
        }

        /**
         * 口罩状态
         *
         * @param airIndex         空气质量指数
         * @param fanStatus        风扇状态
         *                         0：关闭状态
         *                         1：1 档状态
         *                         2：2 档状态
         * @param power            电池电量，单位 %
         * @param powerStatus      电池状态
         *                         1：充电
         *                         2：非充电
         *                         如该值无效则为 0xFF
         * @param batteryRemaining 电池续航
         *                         大端序。单位：分钟
         *                         无效则该值为 0xFFFF
         * @param breathRate       呼吸频率
         *                         单位，次/min
         * @param breathState      呼吸状态
         *                         1：呼气
         *                         2：吸气
         *                         无效则该值为 0xFF
         * @param filterDuration   滤网的总工作时长
         *                         大端序，单位：min
         */
        default void onStatus(int airIndex, int fanStatus, int power, int powerStatus, int batteryRemaining, int breathRate, int breathState, int filterDuration) {
        }


        /**
         * 滤网设置指令
         *
         * @param status 0x00：成功
         *               0x01：失败
         */
        default void onFilter(int status) {
        }

        /**
         * 风扇状态
         *
         * @param status 0：关闭风扇
         *               1：1 档风扇
         *               2: 2 档风扇
         */
        default void onFan(int status) {
        }


    }


    //-----------------set/get-----------------
    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }


    /**
     * 版本号接口
     * Version number interface
     */
    public void setOnBleVersionListener(OnBleVersionListener bleVersionListener) {
        if (mBleDevice != null)
            mBleDevice.setOnBleVersionListener(bleVersionListener);
    }

    /**
     * mcu相关的信息(电量,时间)
     * (mcu related information (power, time))
     */
    public void setOnMcuParameterListener(OnMcuParameterListener mcuParameterListener) {
        if (mBleDevice != null)
            mBleDevice.setOnMcuParameterListener(mcuParameterListener);
    }

    /**
     * 厂商信息(Manufacturer Information)
     */
    public void setOnCompanyListener(OnBleCompanyListener companyListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleCompanyListener(companyListener);
        }
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
