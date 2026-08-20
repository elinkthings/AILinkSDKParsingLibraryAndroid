package cn.net.aicare.modulelibrary.module.TempHumidity;

import android.bluetooth.BluetoothGatt;
import android.os.Build;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleSettingListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.Locale;

public class TempHumidityBleUtils extends BaseBleDeviceData implements OnBleSettingListener {

    private int mCid = 0x002e;
    private int mCidWifi = 0x0036;
    private static TempHumidityBleUtils mTempHumidityBleUtils;
    private BleDataCallBack bleDataCallBack;

    /**
     * 温度单位：摄氏度
     */
    public static final int UNIT_C = 0x00;
    /**
     * 温度单位：华氏度
     */
    public static final int UNIT_F = 0x01;

    /**
     * 闹钟查询
     */
    public static final int ALARM_CLOCK_CHECK = 0x03;
    /**
     * 闹钟添加、修改
     */
    public static final int ALARM_CLOCK_ADD = 0x00;
    /**
     * 闹钟删除
     */
    public static final int ALARM_CLOCK_DELETE = 0x01;

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        BleLog.i("握手状态:" + status);
    }


    private TempHumidityBleUtils(BleDevice bleDevice) {
        super(bleDevice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleDevice.setConnectPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        }
        bleDevice.setMtu(512);
        bleDevice.setOnBleSettingListener(this);
    }

    @Override
    public void OnSettingReturn(int cmdType, int cmdData) {
        if (cmdType == CmdConfig.SET_DEVICE_TIME_UNIX) {
            //设置同步UTC时间成功
            bleDataCallBack.onSyncTimeSuccess(cmdData == CmdConfig.SETTING_SUCCESS);
        }
    }

    @Override
    public void onNotifyData(String uuid, byte[] bytes, int type) {
        BleLog.e("接收到的数据:" + BleStrUtils.byte2HexStr(bytes));
        if (bytes == null || bytes.length == 0 || bleDataCallBack == null) {
            return;
        }
        int cmd = bytes[0] & 0xFF;
        switch (cmd) {
            case 0x81:
                // APP获取设备支持的功能
                int calibration = bytes[1] & 0x01;
                int buzzerAlarm = (bytes[1] & 0x02) >> 1;
                int alarmFunction = (bytes[1] & 0x04) >> 2;
                int reportPunctually = (bytes[1] & 0x08) >> 3;
                int nightLight = (bytes[1] & 0x10) >> 4;
                int bgLight = (bytes[1] & 0x20) >> 5;
                int findDevice = (bytes[1] & 0x40) >> 6;
                int bindDevice = (bytes[1] & 0x80) >> 7;
                bleDataCallBack.onSupportFunction(calibration, buzzerAlarm, alarmFunction,
                        reportPunctually, nightLight, bgLight, findDevice, bindDevice, bytes);
                break;

            case 0x31:
                // BM设备返回状态
                parseDeviceStatusBm(bytes);
                break;

            case 0x0B:
                // 温湿度校准偏置
                parseCalibration(bytes);
                break;

            case 0x12:
                // 设备报警配置
                parseDeviceAlarm(bytes);
                break;

            case 0x21:
                // 蜂鸣器状态 0x00:打开 0x01:关闭
                bleDataCallBack.onBuzzerState((bytes[1] & 0xFF) == 0x00);
                break;

            case 0x23:
                // 闹钟功能
                parseAlarmClock(bytes);
                break;

            case 0x25:
                // 整点报时 0x00:关闭 0x01:打开
                bleDataCallBack.onTellTimeState((bytes[2] & 0xFF) == 1);
                break;

            case 0x27:
                // 小夜灯 0x00:关闭 0x01:打开
                bleDataCallBack.onNightLightState((bytes[2] & 0xFF) == 1);
                break;

            case 0x29:
                // 背光亮度
                bleDataCallBack.onBgLightState((bytes[2] & 0xFF) == 1, bytes[3] & 0xFF);
                break;

            case 0x2B:
                // APP设置/读取单位 0x00:℃ 0x01:℉
                bleDataCallBack.onUnit(bytes[2] & 0xFF);
                break;

            case 0x2D:
                // APP寻物 0x00:正在报警 0x01:停止报警
                bleDataCallBack.onFindDevice(bytes[1] & 0xFF);
                break;

            case 0x32:
                // BM离线历史记录
                offLineRecordBm(bytes);
                break;

            case 0x41:
                // 清除BM历史数据
                bleDataCallBack.onClearHistoryData(bytes[1] & 0xFF);
                break;

            case 0x43:
                // 是否支持历史记录
                bleDataCallBack.onSupportHistoryData((bytes[1] & 0xFF) == 0x01);
                break;

            case 0x02:
                // BT设备返回状态
                parseDeviceStatusBt(bytes);
                break;

            case 0x06:
                // BT离线历史记录
                offLineRecordBt(bytes);
                break;

            case 0x11:
                // 设备类型 0x00:单BLE 0x01:BLE+WiFi 0x02:BLE+4G
                bleDataCallBack.onDeviceType(bytes[1] & 0xFF);
                break;

            case 0x15:
                // 例行上报时间
                int reportTime = ((bytes[1] & 0xFF) << 8) + (bytes[2] & 0xFF);
                bleDataCallBack.onReportTime(reportTime);
                break;

            default:
                break;
        }
    }

    /**
     * BM协议设备状态(0x31)
     */
    private void parseDeviceStatusBm(byte[] bytes) {
        int battery = bytes[1] & 0xFF;
        long startUp1 = (bytes[5] & 0xFFL) << 24;
        long startUp2 = (bytes[4] & 0xFFL) << 16;
        long startUp3 = (bytes[3] & 0xFFL) << 8;
        long startUp4 = (bytes[2] & 0xFFL);
        long time = startUp1 + startUp2 + startUp3 + startUp4;
        if ((bytes[2] & 0xFF) == 0xFF && (bytes[3] & 0xFF) == 0xFF
                && (bytes[4] & 0xFF) == 0xFF && (bytes[5] & 0xFF) == 0xFF) {
            time = System.currentTimeMillis() / 1000;
        }
        int tempL = (bytes[6] & 0xFF);
        int tempH = (bytes[7] & 0x3F) << 8;
        int symbol = (bytes[7] & 0x80) >> 7;
        int unit = (bytes[7] & 0x40) >> 6;
        float tempFloat = (tempH + tempL) / 10F;
        if (symbol == 1) {
            tempFloat = 0 - tempFloat;
        }
        int humidityH = (bytes[9] & 0xFF) << 8;
        int humidityL = (bytes[8] & 0xFF);
        float humidity = (humidityH + humidityL) / 10F;
        // 0x00:APP读取上报 0x01:普通上报 0x02:报警上报
        int status = (bytes[10] & 0xFF);
        bleDataCallBack.onDeviceStatus(battery, time, tempFloat, unit, humidity, status);
    }

    /**
     * BT协议设备状态(0x02)
     */
    private void parseDeviceStatusBt(byte[] bytes) {
        int battery = bytes[1] & 0xFF;
        long startUp1 = (bytes[5] & 0xFFL) << 24;
        int startUp2 = (bytes[4] & 0xFF) << 16;
        int startUp3 = (bytes[3] & 0xFF) << 8;
        int startUp4 = (bytes[2] & 0xFF);
        long time = startUp1 + startUp2 + startUp3 + startUp4;
        int tempL = (bytes[6] & 0xFF);
        int tempH = (bytes[7] & 0x7F) << 8;
        int symbol = (bytes[7] & 0x80) >> 7;
        int humidityH = (bytes[9] & 0xFF) << 8;
        int humidityL = (bytes[8] & 0xFF);
        float humidity = (humidityH + humidityL) / 10F;
        float tempFloat = (tempH + tempL) / 10F;
        if (symbol == 1) {
            tempFloat = 0 - tempFloat;
        }
        bleDataCallBack.onDeviceStatus(battery, time,
                Float.parseFloat(strFloat(tempFloat)), UNIT_C,
                Float.parseFloat(strFloat(humidity)), 0xFF);
    }

    private void parseCalibration(byte[] bytes) {
        int tempNegativeC = (bytes[2] & 0x80) >> 7;
        float tempCalibrationC = (bytes[2] & 0x7F) / 10F;
        if (tempNegativeC == 1) {
            tempCalibrationC = 0 - tempCalibrationC;
        }
        int tempNegativeF = (bytes[3] & 0x80) >> 7;
        float tempCalibrationF = (bytes[3] & 0x7F) / 10F;
        if (tempNegativeF == 1) {
            tempCalibrationF = 0 - tempCalibrationF;
        }
        int humidityNegative = (bytes[4] & 0x80) >> 7;
        float humidityCalibration = (bytes[4] & 0x7F) / 10F;
        if (humidityNegative == 1) {
            humidityCalibration = 0 - humidityCalibration;
        }
        bleDataCallBack.onCalibration(tempCalibrationC, tempCalibrationF, humidityCalibration);
    }

    private void parseDeviceAlarm(byte[] bytes) {
        if (bytes.length < 11) {
            return;
        }
        boolean isOpenTemp = (bytes[1] & 0xFF) == 1;
        float tempWarmH = (((bytes[2] & 0x7F) << 8) + (bytes[3] & 0xFF)) / 10F;
        float tempWarmL = (((bytes[4] & 0x7F) << 8) + (bytes[5] & 0xFF)) / 10F;
        if (((bytes[2] & 0x80) >> 7) == 1) {
            tempWarmH = 0 - tempWarmH;
        }
        if (((bytes[4] & 0x80) >> 7) == 1) {
            tempWarmL = 0 - tempWarmL;
        }
        boolean isOpenHumidity = (bytes[6] & 0xFF) == 1;
        float humidityWarmH = (((bytes[7] & 0xFF) << 8) + (bytes[8] & 0xFF)) / 10F;
        float humidityWarmL = (((bytes[9] & 0xFF) << 8) + (bytes[10] & 0xFF)) / 10F;
        bleDataCallBack.onDeviceAlarm(isOpenTemp, tempWarmH, tempWarmL,
                isOpenHumidity, humidityWarmH, humidityWarmL);
    }

    private void parseAlarmClock(byte[] bytes) {
        int type = bytes[1] & 0xFF;
        int no = bytes[2] & 0xFF;
        int mode = bytes[3] & 0xFF;
        int hour = bytes[4] & 0xFF;
        int min = bytes[5] & 0xFF;
        int second = bytes[6] & 0xFF;
        int secondAll = hour * 60 * 60 + min * 60 + second;
        int state = bytes[7] & 0xFF;
        bleDataCallBack.onAlarmClock(type, no, mode, secondAll, state);
    }

    public static TempHumidityBleUtils getInstance() {
        return mTempHumidityBleUtils;
    }

    public void setBleDataCallBack(BleDataCallBack bleDataCallBack) {
        this.bleDataCallBack = bleDataCallBack;
    }

    public static void init(BleDevice bleDevice) {
        mTempHumidityBleUtils = null;
        mTempHumidityBleUtils = new TempHumidityBleUtils(bleDevice);
    }

    /**
     * BT离线历史(0x06)
     */
    private void offLineRecordBt(byte[] bytes) {
        long total1 = (bytes[4] & 0xFFL) << 24;
        long total2 = (bytes[3] & 0xFFL) << 16;
        long total3 = (bytes[2] & 0xFFL) << 8;
        long total4 = (bytes[1] & 0xFFL);
        long totalAll = total1 + total2 + total3 + total4;

        long sendNum1 = (bytes[8] & 0xFFL) << 24;
        long sendNum2 = (bytes[7] & 0xFFL) << 16;
        long sendNum3 = (bytes[6] & 0xFFL) << 8;
        long sendNum4 = (bytes[5] & 0xFFL);
        long sendNumAll = sendNum1 + sendNum2 + sendNum3 + sendNum4;

        byte[] historyByte = new byte[bytes.length - 9];
        System.arraycopy(bytes, 9, historyByte, 0, historyByte.length);
        BleLog.e("接收到的BT历史" + BleStrUtils.byte2HexStr(historyByte));
        int groupNum = historyByte.length / 8;

        for (int i = 0; i < groupNum; i++) {
            long upTime1 = (historyByte[3 + i * 8] & 0xFFL) << 24;
            long upTime2 = (historyByte[2 + i * 8] & 0xFFL) << 16;
            long upTime3 = (historyByte[1 + i * 8] & 0xFFL) << 8;
            long upTime4 = (historyByte[0 + i * 8] & 0xFFL);
            long upTime = upTime1 + upTime2 + upTime3 + upTime4;

            int symbol = (historyByte[5 + i * 8] & 0x80) >> 7;
            int tempH = (historyByte[5 + i * 8] & 0x7F) << 8;
            int tempL = (historyByte[4 + i * 8] & 0xFF);
            float tempFloat = (tempH + tempL) / 10F;
            if (symbol == 1) {
                tempFloat = 0 - tempFloat;
            }
            float tempValue = Float.parseFloat(strFloat(tempFloat));

            int humidityH = (historyByte[7 + i * 8] & 0xFF) << 8;
            int humidityL = (historyByte[6 + i * 8] & 0xFF);
            float humidityValue = Float.parseFloat(strFloat((humidityH + humidityL) / 10F));

            if ((upTime != 0xFFFFFFFFL) && ((humidityH + humidityL) != 0xFFFF) && (tempH + tempL) != 0x3F) {
                bleDataCallBack.onOffLineRecord(upTime, tempValue, UNIT_C, humidityValue);
            }
        }
        bleDataCallBack.onOffLineRecordNum(totalAll, sendNumAll);
    }

    /**
     * BM离线历史(0x32)
     */
    private void offLineRecordBm(byte[] bytes) {
        long total1 = (bytes[4] & 0xFFL) << 24;
        long total2 = (bytes[3] & 0xFFL) << 16;
        long total3 = (bytes[2] & 0xFFL) << 8;
        long total4 = (bytes[1] & 0xFFL);
        long totalAll = total1 + total2 + total3 + total4;

        long sendNum1 = (bytes[8] & 0xFFL) << 24;
        long sendNum2 = (bytes[7] & 0xFFL) << 16;
        long sendNum3 = (bytes[6] & 0xFFL) << 8;
        long sendNum4 = (bytes[5] & 0xFFL);
        long sendNumAll = sendNum1 + sendNum2 + sendNum3 + sendNum4;

        byte[] historyByte = new byte[bytes.length - 9];
        System.arraycopy(bytes, 9, historyByte, 0, historyByte.length);
        BleLog.e("接收到的BM历史" + BleStrUtils.byte2HexStr(historyByte));
        int groupNum = historyByte.length / 8;

        for (int i = 0; i < groupNum; i++) {
            long upTime1 = (historyByte[3 + i * 8] & 0xFFL) << 24;
            long upTime2 = (historyByte[2 + i * 8] & 0xFFL) << 16;
            long upTime3 = (historyByte[1 + i * 8] & 0xFFL) << 8;
            long upTime4 = (historyByte[0 + i * 8] & 0xFFL);
            long upTime = upTime1 + upTime2 + upTime3 + upTime4;

            int symbol = (historyByte[5 + i * 8] & 0x80) >> 7;
            int unit = (historyByte[5 + i * 8] & 0x40) >> 6;
            int tempH = (historyByte[5 + i * 8] & 0x3F) << 8;
            int tempL = (historyByte[4 + i * 8] & 0xFF);
            float tempFloat = (tempH + tempL) / 10F;
            if (symbol == 1) {
                tempFloat = 0 - tempFloat;
            }
            float tempValue = Float.parseFloat(strFloat(tempFloat));

            int humidityH = (historyByte[7 + i * 8] & 0xFF) << 8;
            int humidityL = (historyByte[6 + i * 8] & 0xFF);
            float humidityValue = Float.parseFloat(strFloat((humidityH + humidityL) / 10F));

            if ((upTime != 0xFFFFFFFFL) && ((humidityH + humidityL) != 0xFFFF) && (tempH + tempL) != 0x3F) {
                bleDataCallBack.onOffLineRecord(upTime, tempValue, unit, humidityValue);
            }
        }
        bleDataCallBack.onOffLineRecordNum(totalAll, sendNumAll);
    }

    private String strFloat(float value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private void sendMcuData(byte[] bytes) {
        SendMcuBean sendBleBean = new SendMcuBean();
        sendBleBean.setHex(mCid, bytes);
        sendData(sendBleBean);
    }

    private void sendBleData(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
    }

    // ======================== 发送指令 ========================

    /**
     * 获取设备状态
     */
    public void getDeviceStatus() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 获取设备支持的功能
     */
    public void getSupportFunction() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x80;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 获取离线历史记录
     *
     * @param time 已同步时间(秒)，首次传0
     */
    public void getOffLineRecord(long time) {
        byte[] bytes = new byte[5];
        bytes[0] = 0x05;
        bytes[4] = (byte) ((time & 0xff000000L) >> 24);
        bytes[3] = (byte) ((time & 0x00ff0000L) >> 16);
        bytes[2] = (byte) ((time & 0x0000ff00L) >> 8);
        bytes[1] = (byte) (time & 0x000000ffL);
        sendMcuData(bytes);
    }

    /**
     * 发送离线历史接收状态
     *
     * @param status 0x00:全部接收完毕，停止发送
     *               0x01:接收未完成，请发下一组数据
     */
    public void sendOfflineRecordStatus(int status) {
        byte[] bytes = new byte[6];
        bytes[0] = 0x09;
        bytes[1] = (byte) status;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 获取设备报警配置
     */
    public void getDeviceAlarm() {
        getWarmConfig();
    }

    /**
     * 获取设备报警配置
     */
    public void getWarmConfig() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x12;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置设备报警温湿度
     *
     * @param openT         温度报警开关
     * @param tempH         高温度报警值(℃)
     * @param tempL         低温度报警值(℃)
     * @param openH         湿度报警开关
     * @param humidityWarmH 高湿度报警值(%)
     * @param humidityWarmL 低湿度报警值(%)
     */
    public void setWarmConfig(boolean openT, float tempH, float tempL,
                              boolean openH, int humidityWarmH, int humidityWarmL) {
        byte[] bytes = new byte[11];
        bytes[0] = 0x13;
        bytes[1] = (byte) (openT ? 0x01 : 0x00);
        boolean isFun = tempH < 0;
        int myTempH = (int) Math.abs(Math.round(tempH * 10));
        bytes[2] = (byte) (((myTempH & 0x7f00) >> 8) + (isFun ? 0x80 : 0x00));
        bytes[3] = (byte) (myTempH & 0x00ff);
        isFun = tempL < 0;
        int myTempL = (int) Math.abs(Math.round(tempL * 10));
        bytes[4] = (byte) (((myTempL & 0x7f00) >> 8) + (isFun ? 0x80 : 0x00));
        bytes[5] = (byte) (myTempL & 0x00ff);
        bytes[6] = (byte) (openH ? 0x01 : 0x00);
        humidityWarmH = humidityWarmH * 10;
        bytes[7] = (byte) ((humidityWarmH & 0xff00) >> 8);
        bytes[8] = (byte) (humidityWarmH & 0x00ff);
        humidityWarmL = humidityWarmL * 10;
        bytes[9] = (byte) ((humidityWarmL & 0xff00) >> 8);
        bytes[10] = (byte) (humidityWarmL & 0x00ff);
        sendMcuData(bytes);
    }

    /**
     * 读取温度单位
     */
    public void getUnit() {
        byte[] bytes = new byte[14];
        bytes[0] = 0x2A;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置温度单位
     *
     * @param unit 0x00:℃  0x01:℉
     */
    public void setUnit(int unit) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x2A;
        bytes[1] = 0x01;
        bytes[2] = (byte) (unit & 0xff);
        sendMcuData(bytes);
    }

    /**
     * APP寻物
     *
     * @param start true:开启寻物  false:关闭寻物
     */
    public void findDevice(boolean start) {
        findDevice(start ? 0x00 : 0x01);
    }

    /**
     * APP寻物
     *
     * @param state 0x00:开始寻物 0x01:停止寻物
     */
    public void findDevice(int state) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x2C;
        bytes[1] = (byte) state;
        bytes[2] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 查询闹钟
     */
    public void checkAlarmClock() {
        setAlarmClock(ALARM_CLOCK_CHECK, 0, 0, 0, 0);
    }

    /**
     * 设置/查询闹钟
     *
     * @param type    0x00:增加闹钟
     *                0x01:删除闹钟
     *                0x02:停止该闹钟
     *                0x03:获取设备已存在的闹钟
     * @param no      编号 1-5
     * @param mode    0x00:只触发1次 0x01:每天触发1次
     * @param seconds 当天秒数
     * @param state   0x00:已停止/已关闭 0x01:已激活
     */
    public void setAlarmClock(int type, int no, int mode, int seconds, int state) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x22;
        bytes[1] = (byte) type;
        bytes[2] = (byte) no;
        bytes[3] = (byte) mode;
        bytes[4] = (byte) (seconds / 3600);
        bytes[5] = (byte) ((seconds % 3600) / 60);
        bytes[6] = 0x00;
        bytes[7] = (byte) state;
        sendMcuData(bytes);
    }

    /**
     * 查询其他功能状态
     * type: 0x2A单位 0x24整点报时 0x26夜灯 0x28背光
     */
    public void checkOtherFunction(int type) {
        byte[] bytes = new byte[14];
        bytes[0] = (byte) type;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置其他功能状态
     * type: 0x2A单位 0x24整点报时 0x26夜灯 0x28背光
     *
     * @param state 0关 1开(单位时为单位值)
     * @param value 附加值(如背光亮度)
     */
    public void setOtherFunction(int type, int state, int value) {
        byte[] bytes = new byte[14];
        bytes[0] = (byte) type;
        bytes[1] = 0x01;
        bytes[2] = (byte) state;
        bytes[3] = (byte) value;
        sendMcuData(bytes);
    }

    /**
     * 设置背光 0x28
     *
     * @param open  是否打开
     * @param value 亮度值 0-100
     */
    public void setBacklight(boolean open, int value) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x28;
        bytes[1] = 0x01;
        bytes[2] = (byte) (open ? 0x01 : 0x00);
        bytes[3] = (byte) value;
        sendMcuData(bytes);
    }

    /**
     * 读取背光 0x28
     */
    public void getBacklight() {
        byte[] bytes = new byte[14];
        bytes[0] = 0x28;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置小夜灯 0x26
     */
    public void setNightLight(boolean open) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x26;
        bytes[1] = 0x01;
        bytes[2] = (byte) (open ? 0x01 : 0x00);
        sendMcuData(bytes);
    }

    /**
     * 读取小夜灯 0x26
     */
    public void getNightLight() {
        byte[] bytes = new byte[14];
        bytes[0] = 0x26;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置整点报时 0x24
     */
    public void setTellTime(boolean open) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x24;
        bytes[1] = 0x01;
        bytes[2] = (byte) (open ? 0x01 : 0x00);
        sendMcuData(bytes);
    }

    /**
     * 读取整点报时 0x24
     */
    public void getTellTime() {
        byte[] bytes = new byte[14];
        bytes[0] = 0x24;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 获取校准值
     */
    public void getCalibration() {
        byte[] bytes = new byte[6];
        bytes[0] = 0x0A;
        bytes[1] = 0x02;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置校准值
     *
     * @param tempC    温度偏置℃
     * @param tempF    温度偏置℉
     * @param humidity 湿度偏置%
     */
    public void setCalibration(float tempC, float tempF, float humidity) {
        byte[] bytes = new byte[6];
        bytes[0] = 0x0A;
        bytes[1] = 0x01;
        int myTempC = (int) Math.abs(tempC * 10);
        bytes[2] = (byte) ((0x7f & myTempC) | (tempC < 0 ? 0x80 : 0x00));
        int myTempF = (int) Math.abs(tempF * 10);
        bytes[3] = (byte) ((0x7f & myTempF) | (tempF < 0 ? 0x80 : 0x00));
        int myHumidity = (int) Math.abs(humidity * 10);
        bytes[4] = (byte) ((0x7f & myHumidity) | (humidity < 0 ? 0x80 : 0x00));
        bytes[5] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 读取蜂鸣器状态
     */
    public void getSound() {
        byte[] bytes = new byte[3];
        bytes[0] = 0x20;
        bytes[1] = 0x00;
        bytes[2] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 设置蜂鸣器
     *
     * @param open true:打开  false:关闭
     */
    public void setSound(boolean open) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x20;
        bytes[1] = 0x01;
        // 0x00:打开 0x01:关闭
        bytes[2] = (byte) (open ? 0x00 : 0x01);
        sendMcuData(bytes);
    }

    /**
     * 同步UTC时间
     */
    public void synTime() {
        sendBleData(BleSendCmdUtil.getInstance().setDeviceTimeUnix());
    }

    /**
     * APP获取设备信息
     */
    public void getAppInfo() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x10;
        bytes[1] = 0x00;
        sendMcuData(bytes);
    }

    /**
     * 清除BM历史数据
     */
    public void removeData() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x40;
        bytes[1] = 0x01;
        sendMcuData(bytes);
    }

    /**
     * OTA
     */
    public void ota() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x91;
        bytes[1] = 0x01;
        sendBleData(bytes);
    }


    public interface BleDataCallBack {
        /**
         * 设备状态
         *
         * @param battery  电量 0-100
         * @param time     unix时间(秒)
         * @param temp     温度
         * @param unit     0x00:℃ 0x01:℉
         * @param humidity 湿度
         * @param status   0x00:APP读取 0x01:普通上报 0x02:报警上报 0xFF:无效(BT)
         */
        void onDeviceStatus(int battery, long time, float temp, int unit, float humidity, int status);

        void onOffLineRecordNum(long total, long sendNum);

        /**
         * 离线历史
         *
         * @param time     时间
         * @param temp     温度
         * @param unit     单位
         * @param humidity 湿度
         */
        void onOffLineRecord(long time, float temp, int unit, float humidity);

        /**
         * 支持的功能 1:支持 0:不支持
         */
        void onSupportFunction(int calibration, int buzzerAlarm, int alarmFunction,
                               int reportPunctually, int nightLight, int bgLight,
                               int findDevice, int bindDevice, byte[] bytes);

        /**
         * 设备报警配置
         */
        void onDeviceAlarm(boolean openTemp, float tempHigh, float tempLow,
                           boolean openHumidity, float humidityHigh, float humidityLow);

        /**
         * 单位 0x00:℃ 0x01:℉
         */
        void onUnit(int unit);

        /**
         * 寻物状态 0x00:正在报警 0x01:停止报警
         */
        void onFindDevice(int status);

        default void onCalibration(float tempC, float tempF, float humidity) {
        }

        default void onBuzzerState(boolean isOpen) {
        }

        default void onAlarmClock(int type, int no, int mode, int second, int state) {
        }

        default void onTellTimeState(boolean isOpen) {
        }

        default void onNightLightState(boolean isOpen) {
        }

        default void onBgLightState(boolean isOpen, int value) {
        }

        default void onClearHistoryData(int result) {
        }

        default void onSupportHistoryData(boolean support) {
        }

        default void onDeviceType(int deviceType) {
        }

        default void onReportTime(int time) {
        }

        default void onSyncTimeSuccess(boolean status) {
        }
    }

}
