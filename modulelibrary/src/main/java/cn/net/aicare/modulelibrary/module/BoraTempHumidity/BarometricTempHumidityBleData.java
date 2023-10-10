package cn.net.aicare.modulelibrary.module.BoraTempHumidity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

/**
 * 气压温湿度计解析类
 *
 * @author yesp
 */
public class BarometricTempHumidityBleData extends BaseBleDeviceData {

    private static BarometricTempHumidityBleData barometricTempHumidityBleData;
    private BaroTempHygrometerListener mBaroTempHygrometerListener;
    private int cid;
    private LogInterface mLogInterface;

    @SuppressLint("MissingPermission")
    private BarometricTempHumidityBleData(BleDevice bleDevice, int cid) {
        super(bleDevice);
        this.cid = cid;
        if (bleDevice.getBluetoothGatt() != null) {
            bleDevice.setConnectPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
            bleDevice.setMtu(255);
        }

        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                if (mBaroTempHygrometerListener != null) {
                    mBaroTempHygrometerListener.onVersion(version);
                }
            }
        });

    }


    public static void init(BleDevice bleDevice, int cid) {
        barometricTempHumidityBleData = null;
        barometricTempHumidityBleData = new BarometricTempHumidityBleData(bleDevice, cid);
    }


    public static BarometricTempHumidityBleData getInstance() {
        return barometricTempHumidityBleData;
    }

    public void setNull() {
        barometricTempHumidityBleData = null;
    }

    public void setBaroTempHygrometerListener(BaroTempHygrometerListener baroTempHygrometerListener) {
        this.mBaroTempHygrometerListener = baroTempHygrometerListener;
    }

    public void setLogInterface(LogInterface logInterface){
        this.mLogInterface = logInterface;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        super.onNotifyData(hex, type);
    }

    @Override
    public void onNotifyData(String uuid, byte[] bytes, int i) {
        BleLog.i("接收到的数据" + BleStrUtils.byte2HexStr(bytes));
        if (mLogInterface != null) {
            mLogInterface.onLog(bytes);
        }
        if (mBaroTempHygrometerListener != null) {
            int status = bytes[0] & 0xff;

            try {
                switch (status) {
                    case 0x81:
                        // 7.1 APP获取设备支持的功能
                        BleParseUtils.parseCmd81(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x02:
                        // 7.2 APP获取设备状态
                        BleParseUtils.parseCmd02(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x04:
                        // 7.3 APP设备变化阀值
                        BleParseUtils.parseCmd04(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x06:
                        // 7.5 离线历史记录
                        BleParseUtils.parseCmd06(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x08:
                        // 7.7 采样率
                        BleParseUtils.parseCmd08(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x0B:
                        // 7.8 读取温湿度校准
                        BleParseUtils.parseCmd0B(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x2D:
                        // 7.9 APP寻物功能
                        BleParseUtils.parseCmd2D(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x21:
                        // 7.10 APP设置/读取设备蜂鸣器功能
                        BleParseUtils.parseCmd21(bytes, mBaroTempHygrometerListener);
                        break;
                    case 0x2E:
                        // 绑定设备
                        if ((bytes[1] & 0xFF) == 0x01) {
                            sendDataA7(BleSendUtils.bindDeviceSuccess());
                            mBaroTempHygrometerListener.onBindStatus(true);
                        } else {
                            mBaroTempHygrometerListener.onBindStatus(false);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void sendDataA7(byte[] bytes) {
        BleLog.e("发送的A7数据" + BleStrUtils.byte2HexStr(bytes));
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(cid, bytes);
        sendData(sendMcuBean);
    }

    private void sendDataA6(byte[] bytes) {
        BleLog.e("发送的A6数据" + BleStrUtils.byte2HexStr(bytes));
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
    }

    public interface BaroTempHygrometerListener {

        /**
         * 实时状态
         *
         * @param battery
         * @param time
         * @param temp
         * @param humidity
         * @param barometric
         */
        void onDeviceStatus(int battery, long time, int temp, int humidity, int barometric);

        /**
         * 支持功能
         *
         * @param calibration
         * @param buzzerAlarm
         * @param alarmFunction
         * @param reportPunctually
         * @param nightLight
         * @param bgLight
         * @param findDevice
         * @param bytes
         */
        void onFunctionList(int calibration, int buzzerAlarm, int alarmFunction, int reportPunctually,
                            int nightLight, int bgLight, int findDevice, byte[] bytes);

        /**
         * 离线记录条数
         *
         * @param total
         * @param sendNum
         */
        void onOffLineRecordNum(long total, long sendNum);

        /**
         * 离线历史记录
         *
         * @param time
         * @param temp
         * @param humidity
         * @param barometric
         */
        void onOffLineRecord(long time, int temp, int humidity, int barometric);

        /**
         * 版本号
         *
         * @param version
         */
        void onVersion(String version);

        /**
         * 设备变化阀值
         *
         * @param tempT
         * @param humidityT
         * @param barometricT
         */
        void onThreshold(int tempT, int humidityT, int barometricT);

        /**
         * 设备采样频率和保存频率
         *
         * @param samplingFrequency
         * @param saveFrequency
         * @param timerInterval
         */
        void onFrequency(int samplingFrequency, int saveFrequency, int timerInterval);

        /**
         * 寻物功能
         *
         * @param status
         */
        void onFindDevice(int status);

        /**
         * 蜂鸣器状态
         *
         * @param isOpen
         */
        void onBuzzer(boolean isOpen);

        /**
         * 气压温湿度校准值
         *
         * @param tempCal
         * @param humidityCal
         * @param barometricCal
         */
        void onCalibration(int tempCal, int humidityCal, int barometricCal);

        /**
         * 绑定状态
         * @param success
         */
        void onBindStatus(boolean success);
    }

    public interface LogInterface {
        /**
         * 接收到的数据
         *
         * @param bytes bytes
         */
        void onLog(byte[] bytes);
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (barometricTempHumidityBleData != null) {
            mBaroTempHygrometerListener = null;
            barometricTempHumidityBleData = null;
        }
    }
}
