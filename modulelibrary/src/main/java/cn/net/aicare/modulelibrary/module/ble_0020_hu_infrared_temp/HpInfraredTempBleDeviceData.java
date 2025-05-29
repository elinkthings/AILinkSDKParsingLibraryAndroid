package cn.net.aicare.modulelibrary.module.ble_0020_hu_infrared_temp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

/**
 * 华普红外测温枪 设备对象
 *
 * @author xing
 * @date 2025/03/27
 */
public class HpInfraredTempBleDeviceData extends BaseBleDeviceData {

    private final static String TAG=HpInfraredTempBleDeviceData.class.getName();

    private static HpInfraredTempBleDeviceData hpInfraredTempBleDeviceData;
    private OnBleDeviceInfoListener mOnBleDeviceInfoListener;
    private OnBleSetStatusListener mOnBleSetStatusListener;
    private OnBleAlarmResultListener mOnBleAlarmResultListener;
    private static final int RESET_DEVICE = 1;
    private static final long RESET_DEVICE_TIME = 3 * 60 * 1000;
    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage( Message msg) {
            super.handleMessage(msg);
            if (msg.what == RESET_DEVICE) {
                resetDevice();
                startScheduledRestart();
            }
        }
    };

    /**
     * 启动定时重启
     */
    private void startScheduledRestart(){
        if (mHandler!=null && RESET_DEVICE_TIME > 0) {
            mHandler.removeMessages(RESET_DEVICE);
            mHandler.sendEmptyMessageDelayed(RESET_DEVICE, RESET_DEVICE_TIME);
        }
    }

    /**
     * 停止定时重启
     */
    private void stopScheduledRestart(){
        if (mHandler!=null) {
            mHandler.removeMessages(RESET_DEVICE);
        }
    }

    /**
     * 重置设备
     */
    private void resetDevice() {
        byte[] bytes = BleSendCmdUtil.getInstance().setBleRestart();
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
    }


    /**
     * 清除
     */
    public void clear(){
        stopScheduledRestart();
    }

    private HpInfraredTempBleDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //BLE模块版本号
                BleLog.i(TAG,"onBmVersion:"+version);
            }
        });
    }


    public static void init(BleDevice bleDevice) {
        hpInfraredTempBleDeviceData = null;
        hpInfraredTempBleDeviceData = new HpInfraredTempBleDeviceData(bleDevice);


    }

    public static HpInfraredTempBleDeviceData getInstance() {
        return hpInfraredTempBleDeviceData;
    }


    public void setOnBleDeviceInfoListener(OnBleDeviceInfoListener onBleDeviceInfoListener) {
        mOnBleDeviceInfoListener = onBleDeviceInfoListener;
    }

    public void setOnBleSetStatusListener(OnBleSetStatusListener onBleSetStatusListener) {
        mOnBleSetStatusListener = onBleSetStatusListener;
    }

    public void setOnBleAlarmResultListener(OnBleAlarmResultListener onBleAlarmResultListener) {
        mOnBleAlarmResultListener = onBleAlarmResultListener;
    }

    @Override
    public void onNotifyData(String uuid, byte[] bytes, int type) {
        BleLog.i(TAG,"收到的A7 Payload指令:" + BleStrUtils.byte2HexStr(bytes));
        int cmd = bytes[0] & 0xff;
        switch (cmd) {
            case HpInfraredTempBleConfig.GET_DEVICE_STATUS_1:
                if (bytes.length >= 11) {
                    int status = bytes[1] & 0x01;
                    int unit = (bytes[1] & 0x02) >> 1;
                    int laser = bytes[1] & 0x04;
                    int backLamp = bytes[1] & 0x08;
                    int lowBattery = (bytes[1] & 0x10) >> 4;
                    int alarmH = (bytes[1] & 0x20) >> 4;
                    int alarmL = (bytes[1] & 0x40) >> 4;
                    int flashlight = (bytes[1] & 0x80) >> 4;
                    int mode = bytes[2] & 0xff;
                    int mainTag = bytes[3] & 0x01;
                    int mainSign = bytes[3] & 0x02;
                    int mainDecimal = (bytes[3] & 0x04) >> 2;

                    int subTag = (bytes[3] & 0x10) >> 4;
                    int subSign = (bytes[3] & 0x20) >> 4;
                    int subDecimal = (bytes[3] & 0x40) >> 4;

                    int mainValueH = (bytes[4] & 0xff) << 8;
                    int mainValueL = (bytes[5] & 0xff);
                    int subValueH = (bytes[6] & 0xff) << 8;
                    int subValueL = (bytes[7] & 0xff);
                    int ems = (bytes[8] & 0xff);
                    int timeH = (bytes[9] & 0xff) << 8;
                    int timeL = (bytes[10] & 0xff);
                    if (mOnBleDeviceInfoListener != null) {
                        mOnBleDeviceInfoListener.onDeviceStatus1(status, unit, laser, backLamp, lowBattery,
                                alarmH, alarmL, flashlight, mode,
                                mainTag, mainSign, mainDecimal,
                                subTag, subSign, subDecimal, mainValueH + mainValueL,
                                subValueH + subValueL, ems, timeH + timeL);
                    }

                }
                break;
            case HpInfraredTempBleConfig.GET_DEVICE_STATUS_2:
                if (bytes.length >= 7) {
                    int status = bytes[1] & 0x01;
                    int dewTempSign = bytes[1] & 0x02;
                    int envTempSign = bytes[1] & 0x04;
                    int dewTempDecimal = bytes[1] & 0x08;
                    int envTempDecimal = (bytes[1] & 0x10) >> 4;
                    int humidity = (bytes[2] & 0xff);
                    int dewTempH = (bytes[3] & 0xff) << 8;
                    int dewTempL = (bytes[4] & 0xff);
                    int envTempH = (bytes[5] & 0xff) << 8;
                    int envTempL = (bytes[6] & 0xff);
                    if (mOnBleDeviceInfoListener != null) {
                        mOnBleDeviceInfoListener.onDeviceStatus2(status, dewTempSign, dewTempDecimal, dewTempH + dewTempL, humidity, envTempSign, envTempDecimal, envTempH + envTempL);
                    }
                }

                break;
            case HpInfraredTempBleConfig.DEVICE_PARAMETER:
                if (bytes.length >= 9) {
                    int ems = (bytes[1] & 0xff);
                    int timeH = (bytes[2] & 0xff) << 8;
                    int timeL = (bytes[3] & 0xff);
                    int HSign = (bytes[4] & 0x01);
                    int HUnit = (bytes[4] & 0x02) >> 1;
                    int LSign = (bytes[4] & 0x10) >> 4;
                    int LUnit = (bytes[4] & 0x20) >> 5;
                    int alarmH = ((bytes[5] & 0xff) << 8) + (bytes[6] & 0xff);
                    int alarmL = ((bytes[7] & 0xff) << 8) + (bytes[8] & 0xff);
                    if (mOnBleDeviceInfoListener != null) {
                        mOnBleDeviceInfoListener.onDeviceParameter(ems, timeH + timeL, HSign, HUnit, LSign, LUnit, alarmH, alarmL);
                    }
                }


                break;
            case HpInfraredTempBleConfig.GET_EMISSIVITY:
                if (bytes.length >= 2) {
                    if (mOnBleSetStatusListener != null) {
                        mOnBleSetStatusListener.onEmissivityResult(bytes[1] & 0xff);
                    }
                }
                break;
            case HpInfraredTempBleConfig.GET_AUTO_SHUTDOWN:
                //MCU返回自动关机指令结果
                if (bytes.length >= 2) {
                    if (mOnBleSetStatusListener != null) {
                        mOnBleSetStatusListener.onAutoShutDownResult(bytes[1] & 0xff);
                    }
                }
                break;
            case HpInfraredTempBleConfig.GET_WARM_VALUE:
                //MCU返回警报值结果
                if (bytes.length >= 2) {
                    if (mOnBleAlarmResultListener != null) {
                        mOnBleAlarmResultListener.onAlarmResult(bytes[1] & 0xff);
                    }
                }
                break;

            default:
                BleLog.i(TAG,"其他未识别的A7 Payload指令:" + BleStrUtils.byte2HexStr(bytes));
                break;
        }
    }

    public interface OnBleAlarmResultListener {


        /**
         * 报警结果
         *
         * @param status 状态 0x00：成功
         *               0x01：失败
         */
        void onAlarmResult(int status);
    }

    public interface OnBleDeviceInfoListener {


        /**
         * 设备状态1
         *
         * @param scanOrHold   状态 0：SCAN。1：HOLD
         * @param tempUnit     单位 0：℃ 。 1：℉
         * @param laser        激光灯 0：关闭 。1：打开
         * @param backLamp     背光灯 0：关闭 。1：打开
         * @param lowBattery   低电 0：不低电 。1：低电
         * @param alarmH       高位报警 0：不报警 。 1：正在报警
         * @param alarmL       低温报警 0：不报警 。 1：正在报警
         * @param flashlight   手电筒灯 0：关闭 。1：打开
         * @param mode         模式 0x00：REC（设备端查看历史记录时发送该模式，APP 不保存该模式的数据）
         *                     0x01：MAX;0x02：AVG;0x03：MIN;0x04：DIF;0x05：LAL;0x06：HAL;0x07：EMS（设备端查看发射率）;0x08：HAL—Warming—T;0x09：LAL—Warming—T
         *                     0x0A：EMS（设备端设置发射率）
         * @param mainTag      主区数据标识:显示标识：0：不显示。1：显示
         * @param mainSign     主区数据标识:数据正负：0：正值。 1：负值
         * @param mainDecimal  主区数据标识:数据小数点：0：无小数点。1：带 1 位小数
         * @param subTag       附区数据标识:显示标识：0：不显示。1：显示
         * @param subSign      附区数据标识:数据正负：0：正值。 1：负值
         * @param subDecimal   附区数据标识:数据小数点：0：无小数点。1：带 1 位小数
         * @param mainValue    主区数值
         * @param subValue     附区数值
         * @param ems          EMS 数值 0.10 ~1.00
         * @param autoShutdown 自动睡眠时间
         */
        void onDeviceStatus1(int scanOrHold, int tempUnit, int laser, int backLamp, int lowBattery, int alarmH, int alarmL, int flashlight, int mode, int mainTag, int mainSign, int mainDecimal, int subTag, int subSign, int subDecimal, int mainValue, int subValue, int ems, int autoShutdown);

        /**
         * 设备状态2
         *
         * @param scanOrHold     状态 0：SCAN。1：HOLD
         * @param dewTempSign    露点温度标志:0：正数 ， 1：负数
         * @param dewTempDecimal 露点温度小数点： 0：无小数， 1: 1 个小数
         * @param dewTemp        露点温度
         * @param humidity       环境湿度：单位%
         * @param envTempSign    环境温度正负：0：正数 ， 1：负数
         * @param envTempDecimal 环境温度小数点： 0：无小数， 1: 1 个小数
         * @param envTemp        环境温度
         */
        void onDeviceStatus2(int scanOrHold, int dewTempSign, int dewTempDecimal, int dewTemp, int humidity, int envTempSign, int envTempDecimal, int envTemp);

        /**
         * 设备参数
         *
         * @param emissivity   发射率：2 个小数;范围 0.10 ~ 1.00 （对应的是 10-100）
         * @param autoShutdown 自动关机时间
         * @param alarmHSign   数据标识:高温报警数据(正负值：0：正值 。1：负值)
         * @param alarmHUnit   数据标识:高温报警数据(单位：0：℃ 。1：℉)
         * @param alarmLSign   数据标识:低温报警数据(正负值：0：正值 。1：负值)
         * @param alarmLUnit   数据标识:低温报警数据(单位：0：℃ 。1：℉)
         * @param alarmH       高温报警数据
         * @param alarmL       低温报警数据
         */
        void onDeviceParameter(int emissivity, int autoShutdown, int alarmHSign, int alarmHUnit, int alarmLSign, int alarmLUnit, int alarmH, int alarmL);

    }

    public interface OnBleSetStatusListener {

        /**
         * 发射率结果
         *
         * @param status 状态 0x00：成功
         *               0x01：失败
         */
        void onEmissivityResult(int status);

        /**
         * 自动关机结果
         *
         * @param status 状态 0x00：成功
         *               0x01：失败
         */
        void onAutoShutDownResult(int status);
    }

}
