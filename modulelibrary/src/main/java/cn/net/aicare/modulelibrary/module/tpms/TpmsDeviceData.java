package cn.net.aicare.modulelibrary.module.tpms;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.config.BleDeviceConfig;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.Arrays;

/**
 * xing<br>
 * 2019/7/3<br>
 * TPMS
 */
public class TpmsDeviceData extends BaseBleDeviceData {
    private String TAG = TpmsDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private onTpmsSetting mOnTpmsSetting;
    private onTpmsInfo mTpmsInfo;
    private byte[] CID;
    private int mType = BleDeviceConfig.TPMS_CONN_DEVICE;
    private static BleDevice mBleDevice = null;
    private static TpmsDeviceData tpmsDevice = null;

    public static TpmsDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (tpmsDevice == null) {
                    tpmsDevice = new TpmsDeviceData(bleDevice);

                }
            } else {
                tpmsDevice = new TpmsDeviceData(bleDevice);
            }
        }
        return tpmsDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (tpmsDevice != null) {
            mOnNotifyData = null;
            tpmsDevice = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private TpmsDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }

    /**
     * 请求获取设备详细信息
     */
    public void getDeviceInfo() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().getDeviceInfo());
        sendData(sendBleBean);
    }

    public void setUnit(int pressureUnit, int tempUnit) {
        byte pU = 0;
        if (pressureUnit == TpmsBleConfig.PRESSURE_BAR) {
            pU = 2;
        } else if (pressureUnit == TpmsBleConfig.PRESSURE_PSI) {
            pU = 1;
        }
        byte tU = 0;
        if (tempUnit == TpmsBleConfig.TEM_F) {
            tU = 1;
        }
        byte[] bytes = new byte[]{(byte) 0x81, pU, tU};

        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, bytes);
        sendData(sendMcuBean);

    }

    /**
     * @param index 轮胎编号  1 左前轮 2右前轮 3左后轮 4左后轮
     */
    public void requestData(byte index) {

        byte[] bytes = new byte[]{TpmsBleConfig.GET_TPMS_INFO, index};

        SendMcuBean sendMcuBean = new SendMcuBean();

        sendMcuBean.setHex(CID, bytes);

        sendData(sendMcuBean);
    }


    /**
     * 设置胎压阀值
     *
     * @param beforeMax 前轮上限阀值
     * @param beforeMin 前轮下限阀值
     * @param rearMax   后轮上限阀值
     * @param rearMin   后轮下限阀值
     * @param unit      单位;0：Kpa;1：Psi;2：Bar
     * @param point     阀值数值小数点;0：不带小数点;1：带 1 位小数点;2：带 2 位小数点
     */
    public void setTpmsThreshold(int beforeMax, int beforeMin, int rearMax, int rearMin, int unit, int point) {

        byte[] sendData = new byte[11];
        sendData[0] = TpmsBleConfig.SET_TPMS_THRESHOLD;
        //大端模式(高位存在低位)
        sendData[2] = (byte) ((beforeMax >> 8) & 0xff);
        sendData[1] = (byte) (beforeMax);
        sendData[4] = (byte) ((beforeMin >> 8) & 0xff);
        sendData[3] = (byte) (beforeMin);
        sendData[6] = (byte) ((rearMax >> 8) & 0xff);
        sendData[5] = (byte) (rearMax);
        sendData[8] = (byte) ((rearMin >> 8) & 0xff);
        sendData[7] = (byte) (rearMin);
        sendData[9] = (byte) (unit);
        sendData[10] = (byte) (point);

        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, sendData);
        sendData(sendMcuBean);
    }

    /**
     * 设置温度
     *
     * @param tempMax
     * @param tempMin
     * @param tempUnit
     * @param point
     * @param alarmSwitch
     */
    public void setTpmsTemp(int tempMax, int tempMin, int tempUnit, int point, int alarmSwitch) {

        byte[] sendData = new byte[11];
        sendData[0] = TpmsBleConfig.SET_TPMS_TEMP;
        //大端模式(高位存在低位)
        sendData[2] = (byte) ((tempMax >> 8) & 0xff);
        sendData[1] = (byte) (tempMax);
        sendData[4] = (byte) ((tempMin >> 8) & 0xff);
        sendData[3] = (byte) (tempMin);
        sendData[5] = (byte) (tempUnit);
        sendData[6] = (byte) (point);
        sendData[7] = (byte) (alarmSwitch);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, sendData);
        sendData(sendMcuBean);
    }

    /**
     * 设置tpms报警状态
     *
     * @param isOpen 是否开启报警
     */
    public void setTpmsAlarm(boolean isOpen) {
        byte[] sendData = new byte[2];
        sendData[0] = TpmsBleConfig.SET_TPMS_ALARM;
        if (isOpen) {
            sendData[1] = 0x01;
        } else {
            sendData[1] = 0x00;
        }
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, sendData);
        sendData(sendMcuBean);
    }

    //-----------------------------------------

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

    @Override
    public void onNotifyDataA6(byte[] data) {
        switch (data[0]){

            case CmdConfig.GET_DEVICE_INFO:
                deviceInfo(data);
                break;

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
            //返回错误信息

            case TpmsBleConfig.GET_ERR:
                getErr(data);
                return;
            case TpmsBleConfig.GET_TPMS_INFO://主动读取胎压信息
            case TpmsBleConfig.GET_TPMS_STATUS://上传胎压状态
                parseBytes(data);
                break;

            case TpmsBleConfig.GET_TPMS_THRESHOLD:
            case TpmsBleConfig.GET_TPMS_TEMP:
            case TpmsBleConfig.GET_TPMS_ALARM:
            case TpmsBleConfig.GET_UNIT:
                getTpmsSetting(data);
                break;

        }
    }

    private void parseBytes(byte[] bytes) {
        if (mOnNotifyData == null) return;
        if (bytes.length < 13) return;
        int index = bytes[1] & 0xFF;//轮胎编号: 1 左前轮 2右前轮 3左后轮 4左后轮
        int batteryInt = bytes[2] & 0xFF;
        int batteryDecimal = bytes[3] & 0xFF;//0, 1 , 2
        float battery = (float) (batteryInt / Math.pow(10, batteryDecimal));
        int pressure = (bytes[4] & 0xFF) << 8;
        pressure = (bytes[5] & 0xFF) + pressure;//&的优先级比+低，要加上括号

        int pressureUnit = bytes[6] & 0xFF; // 0 Kpa ; 1 Psi ; 2 Bar

        int pressureDecimal = bytes[7] & 0xFF;//0, 1, 2

//        float pressure = (float) (pressureInt / Math.pow(10, pressureDecimal));

        short temp = (short) (((bytes[8] & 0xFF) << 8) | (bytes[9] & 0xFF));

        int tempUnit = bytes[10] & 0xFF;//0 C ; 1 F

        int tempDecimal = bytes[11] & 0xFF;//0 1 2

//        float TEMP = (float) (tempShort / Math.pow(10, tempDecimal));
        int status = bytes[12] & 0xFF; // 0正常 1漏气 2充气 3启动 4上电 5唤醒 6丢失
        mOnNotifyData.onTpmsData(index, pressure, pressureUnit, pressureDecimal, battery, temp, tempUnit, tempDecimal, status);
    }


    private void getTpmsSetting(byte[] data) {
        if (data.length > 1) {
            byte status = data[1];
            switch (status) {
                case CmdConfig.SETTING_SUCCESS:
                    BleLog.i(TAG, "设置成功");
                    break;
                case CmdConfig.SETTING_FAILURE:
                    BleLog.i(TAG, "设置失败");
                    break;
                case CmdConfig.SETTING_ERR:
                    BleLog.i(TAG, "不支持设置");
                    break;
            }
            runOnMainThread(() -> {
                if (mOnTpmsSetting != null) {
                    switch (data[0]) {
                        case TpmsBleConfig.GET_TPMS_THRESHOLD:
                            mOnTpmsSetting.onTpmsThreshold(status);
                            break;
                        case TpmsBleConfig.GET_TPMS_TEMP:
                            mOnTpmsSetting.onTpmsTemp(status);
                            break;
                        case TpmsBleConfig.GET_TPMS_ALARM:
                            mOnTpmsSetting.onTpmsAlarm(status);
                            break;
                        case TpmsBleConfig.GET_UNIT:
                            mOnTpmsSetting.getUnit(status);
                            break;
                    }
                }
            });

        }
    }


    /**
     * 设备信息
     *
     * @param data
     */
    private void deviceInfo(byte[] data) {
        if (data.length >= 3 && data[1] == 0x01) {
            int type = data[2] & 0xff;
            if (mTpmsInfo!=null){
                mTpmsInfo.onTpmsType(type);
            }
        }

    }


    /**
     * 错误指令
     */
    private void getErr(byte[] data) {
        byte status = data[1];
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getErr(status);
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
         * tpms 数据
         */
        void onTpmsData(int index, int pressure, int pressureUnit, int pressureDecimal, float battery, int tem, int temUnit, int temDecimal, int status);

        /**
         * 其他数据
         *
         * @param data
         * @param type
         */
        void onData(byte[] data, int type);

        /**
         * 错误指令
         * 0：超重<br>
         * 1：称重抓 0 期间，重量不稳定<br>
         * 2：称重抓 0 失败<br>
         */
        default void getErr(byte status) {
        }
    }


    public interface onTpmsSetting {

        /**
         * 设置温度返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void onTpmsTemp(byte status) {
        }

        /**
         * 设置气压返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void onTpmsThreshold(byte status) {
        }

        /**
         * 设置报警开关返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void onTpmsAlarm(byte status) {
        }



        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void getUnit(byte status) {
        }


    }


    public interface onTpmsInfo{
        /**
         * 设置类型
         * @param type 类型1：一起设置;2：分开设定
         */
        default void onTpmsType(int type) {
        }
    }



    //-----------------set/get-----------------


    public void setOnTpmsSetting(onTpmsSetting onTpmsSetting) {
        mOnTpmsSetting = onTpmsSetting;
    }

    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
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

    public void setTpmsInfo(onTpmsInfo tpmsInfo) {
        mTpmsInfo = tpmsInfo;
    }

    //---------------

    private Handler threadHandler = new Handler(Looper.getMainLooper());


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
