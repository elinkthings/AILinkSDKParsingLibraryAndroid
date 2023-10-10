package cn.net.aicare.modulelibrary.module.BloodOxygen;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

public class BleBloodOxygenDeviceData extends BaseBleDeviceData implements OnBleOtherDataListener, OnBleVersionListener {
    private static BleBloodOxygenDeviceData sBleBloodOxygenDeviceData;
    private DataCallback mDataCallback;

    private BleDevice mBleDevice;

    private BleBloodOxygenDeviceData(BleDevice bleDevice, DataCallback mDataCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        mBleDevice.setOnBleOtherDataListener(this);
        mBleDevice.setOnBleVersionListener(this);
        this.mDataCallback = mDataCallback;

    }


    public static void init(BleDevice bleDevice, DataCallback dataCallback) {

        sBleBloodOxygenDeviceData = new BleBloodOxygenDeviceData(bleDevice, dataCallback);

    }

    public static BleBloodOxygenDeviceData getInstance() {
        return sBleBloodOxygenDeviceData;
    }

    public void disconnect() {
        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mDataCallback != null)
            mDataCallback.onData(hex, 0);
        int cmd = hex[0] & 0xff;
        switch (cmd) {
            case BleBloodOxygenBleConfig.TESTRESULT:
                if (mDataCallback != null&&hex.length>12) {
                    int status = hex[1] & 0xff;
                    int spo2 = hex[2] & 0xff;
                    int pr = hex[3] & 0xff;
                    float pi = (hex[4] & 0xff) / 10F;
                    int battery = hex[5] & 0xff;
                    //呼吸频率 Respiratory Rate （RR/min ，大端序，一位小数点）
                    float rr = (((hex[6] & 0xff) << 8) | (hex[7] & 0xff)) / 10F;
                    //脉率曲线值（ Plethysmogram 体积描记图，大端序 ）
                    int py = (((hex[8] & 0xff) << 8) | (hex[9] & 0xff));
                    //脉率曲线波谷（柱状图，大端序）
                    int pyTrough = (((hex[10] & 0xff) << 8) | (hex[11] & 0xff));
                    int wearStatus = (hex[12] & 0xff);//佩戴状态: 0：未佩戴  1：已佩戴
                    mDataCallback.onResult(status, spo2, pr, pi, battery,rr,py,pyTrough,wearStatus);
                }
                break;
            case BleBloodOxygenBleConfig.SETRESULT:
                if (mDataCallback != null) {
                    mDataCallback.onSetResult(hex[1] & 0xff);
                }
                break;
            case BleBloodOxygenBleConfig.ERRORCODE:
                if (mDataCallback != null) {
                    mDataCallback.onErrorCode(hex[1] & 0xff);
                }
                break;
        }

    }

    @Override
    public void onBmVersion(String version) {
        if (mDataCallback != null)
            mDataCallback.onBmVersion(version);
    }

    @Override
    public void onNotifyOtherData(byte[] data) {
        if (mDataCallback != null)
            mDataCallback.onData(data, 1);
    }

    public interface DataCallback {

        default void onData(byte[] data, int type) {
        }

        /**
         * 上报测量结果信息
         * Report measurement result information
         *
         * @param status   0x01：测量中 0x02：测量结束 0x03：测量错误
         *                 0x01: Measuring 0x02: Measuring finished 0x03: Measuring error
         * @param spo2      血氧饱和度
         *                 Blood oxygen saturation (0~100%)
         * @param pr       脉率
         *                 Pulse rate 单位：bpm/min
         * @param pi       血流灌注指数
         *                 Blood perfusion index （PI）
         * @param battery  电池电量百分比
         *                 Battery charge percentage（0—100%）
         * @param rr       呼吸频率 Respiratory Rate （RR/min ，大端序，一位小数点）
         * @param py       脉率曲线值（ Plethysmogram 体积描记图，大端序 ）
         * @param pyTrough 脉率曲线波谷（柱状图，大端序）
         * @param wearStatus  佩戴状态 0：未佩戴 1：已佩戴
         *
         */
        void onResult(int status, int spo2, int pr, float pi, int battery, float rr, int py, int pyTrough,int wearStatus);

        /**
         * 设置结果：
         * Set result
         *
         * @param result 0x00：成功  0x01：失败
         *               0x00: Success  0x01: Failure
         */
        void onSetResult(int result);

        /**
         * 设备上报错误码
         * Error code reported by the device
         *
         * @param code 0：血氧饱和率不稳定 1：脉率不稳定 2：测量出错 3：设备低电
         *             0: Blood oxygen saturation rate is unstable 1: Pulse rate is unstable 2: Measurement error 3: Equipment low power
         */
        void onErrorCode(int code);

        /**
         * 版本号
         * @param version 版本号
         */
        void onBmVersion(String version);
    }

    /**
     * @param SpO2Max 血氧饱和度（SpO2）报警上限 (0-255)
     *                Blood oxygen saturation (SpO2) alarm upper limit
     * @param SpO2Min 血氧饱和度（SpO2）报警下限值 (0-255)
     *                Blood oxygen saturation (SpO2) alarm lower limit
     * @param prMax   脉率（PR）报警上限 (0-255)
     *                Pulse rate (PR) alarm upper limit
     * @param prMin   脉率（PR）报警下限 (0-255)
     *                Pulse rate (PR) alarm lower limit
     * @param piMax   血流灌注指数（PI）报警上限值 (0-255)
     *                Blood perfusion index (PI) alarm upper limit
     * @param piMin   血流灌注指数（PI）报警下限值 (0-255)
     *                Blood perfusion index (PI) alarm lower limit
     */
    public void setAlarm(int SpO2Max, int SpO2Min, int prMax, int prMin, int piMax, int piMin) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[7];
        bytes[0] = (byte) BleBloodOxygenBleConfig.SETAlARM;
        bytes[1] = (byte) SpO2Max;
        bytes[2] = (byte) SpO2Min;
        bytes[3] = (byte) prMax;
        bytes[4] = (byte) prMin;
        bytes[5] = (byte) piMax;
        bytes[6] = (byte) piMin;
        sendMcuBean.setHex(0x21, bytes);
        sendData(sendMcuBean);
    }

    public void getDeviceStatus() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) BleBloodOxygenBleConfig.GETDEVICESTATUS;
        bytes[1] = (byte) 0x01;

        sendMcuBean.setHex(0x21, bytes);
        sendData(sendMcuBean);
    }


    public void getVersion(){
        sendData(new SendBleBean(BleSendCmdUtil.getInstance().getBleVersion()));
    }

}
