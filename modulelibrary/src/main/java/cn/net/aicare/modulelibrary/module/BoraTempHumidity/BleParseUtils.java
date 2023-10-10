package cn.net.aicare.modulelibrary.module.BoraTempHumidity;

import com.pingwang.bluetoothlib.utils.BleLog;

public class BleParseUtils {

    /**
     * 支持功能
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd81(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        // 校准功能
        int calibration = bytes[1] & 0x01;
        // 蜂鸣器
        int buzzerAlarm = (bytes[1] & 0x02) >> 1;
        // 闹钟功能
        int alarmFunction = (bytes[1] & 0x04) >> 2;
        // 整点报时
        int reportPunctually = (bytes[1] & 0x08) >> 3;
        // 小夜灯
        int nightLight = (bytes[1] & 0x10) >> 4;
        // 背光亮度
        int bgLight = (bytes[1] & 0x20) >> 5;
        // 背光亮度
        int findDevice = (bytes[1] & 0x40) >> 6;
        baroTempHygrometerListener.onFunctionList(calibration, buzzerAlarm, alarmFunction,
                reportPunctually, nightLight, bgLight, findDevice, bytes);
    }

    /**
     * 实时状态
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd02(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        int battery = bytes[1] & 0xFF;
        long startUp1 = (bytes[5] & 0xffL) << 24;
        int startUp2 = (bytes[4] & 0xFF) << 16;
        int startUp3 = (bytes[3] & 0xFF) << 8;
        int startUp4 = (bytes[2] & 0xFF);
        // 正负号 0 正， 1 负
        int symbol = (bytes[7] & 0x80) >> 7;
        // 温度
        int tempL = (bytes[6] & 0xFF);
        int tempH = (bytes[7] & 0x7F) << 8;
        int tempFloat = (tempH + tempL);
        if (symbol == 1) {
            tempFloat = -tempFloat;
        }
        // 湿度
        int humidityL = (bytes[8] & 0xFF);
        int humidityH = (bytes[9] & 0xFF) << 8;
        // 气压
        int barometricL = (bytes[10] & 0xFF);
        int barometricH = (bytes[11] & 0xFF) << 8;
        baroTempHygrometerListener.onDeviceStatus(battery, startUp1 + startUp2 + startUp3 + startUp4,
                tempFloat, humidityH + humidityL, barometricH + barometricL);
    }

    /**
     * 设备变化阀值
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd04(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        // 温度阀值
        int thresholdTemp = bytes[1] & 0xFF;
        // 湿度阀值
        int thresholdHum = (bytes[2] & 0xFF);
        // 气压阀值
        int thresholdBar = (bytes[3] & 0xFF);
        baroTempHygrometerListener.onThreshold(thresholdTemp, thresholdHum, thresholdBar);
    }

    /**
     * 离线历史记录
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd06(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        BleLog.i("收到设备返回的离线历史记录");
        long total1 = (bytes[4] & 0xffL) << 24;
        int total2 = (bytes[3] & 0xff) << 16;
        int total3 = (bytes[2] & 0xff) << 8;
        int total4 = (bytes[1] & 0xff);
        long sendNum1 = (bytes[8] & 0xffL) << 24;
        int sendNum2 = (bytes[7] & 0xff) << 16;
        int sendNum3 = (bytes[6] & 0xff) << 8;
        int sendNum4 = (bytes[5] & 0xff);

        byte[] historyByte = new byte[bytes.length - 9];
        System.arraycopy(bytes, 9, historyByte, 0, historyByte.length);


        for (int i = 0; i < historyByte.length / 10; i++) {
            int interval = i * 10;
            long upTime1 = (historyByte[3 + interval] & 0xffL) << 24;
            int upTime2 = (historyByte[2 + interval] & 0xff) << 16;
            int upTime3 = (historyByte[1 + interval] & 0xff) << 8;
            int upTime4 = (historyByte[interval] & 0xff);
            // 温度
            int symbol = (historyByte[5 + interval] & 0x80) >> 7;
            int tempL = (historyByte[4 + interval] & 0xff);
            int tempH = (historyByte[5 + interval] & 0x7f) << 8;
            int tempFloat = (tempH + tempL);
            if (symbol == 1) {
                tempFloat = -tempFloat;
            }
            // 湿度
            int humidityL = (historyByte[6 + interval] & 0xff);
            int humidityH = (historyByte[7 + interval] & 0xff) << 8;
            // 气压
            int barometricL = (historyByte[8 + interval] & 0xFF);
            int barometricH = (historyByte[9 + interval] & 0xFF) << 8;
            baroTempHygrometerListener.onOffLineRecord(upTime1 + upTime2 + upTime3 + upTime4, tempFloat, humidityH + humidityL, barometricH + barometricL);
        }

        baroTempHygrometerListener.onOffLineRecordNum(total1 + total2 + total3 + total4, sendNum1 + sendNum2 + sendNum3 + sendNum4);
    }

    /**
     * 设备采样频率和保存频率
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd08(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        // 温湿度采样频率
        int samplingFrequency = bytes[1] & 0xFF;
        // 温湿度保存频率
        int saveFrequency = (bytes[2] & 0xFF);
        // 设备定时器间隔
        int timerInterval = (bytes[3] & 0xFF);
        baroTempHygrometerListener.onFrequency(samplingFrequency, saveFrequency, timerInterval);
    }

    /**
     * 气压温湿度校准值
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd0B(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        int tempNegativeC = (bytes[2] & 0x80) >> 7;
        int tempCalibrationC = (bytes[2] & 0x7f);

        int humidityNegative = (bytes[3] & 0x80) >> 7;
        int humidityCalibration = (bytes[3] & 0x7f);

        int barometricNegative = (bytes[4] & 0x80) >> 7;
        int barometricCalibration = (bytes[4] & 0x7f);
        baroTempHygrometerListener.onCalibration(tempNegativeC == 0 ? tempCalibrationC : -tempCalibrationC,
                humidityNegative == 0 ? humidityCalibration : -humidityCalibration,
                barometricNegative == 0 ? barometricCalibration : -barometricCalibration);
    }

    /**
     * 寻物功能
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd2D(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {

        int status = (bytes[1] & 0xff);
        baroTempHygrometerListener.onFindDevice(status);
    }

    /**
     * 蜂鸣器状态
     *
     * @param bytes
     * @param baroTempHygrometerListener
     */
    public static void parseCmd21(byte[] bytes, BarometricTempHumidityBleData.BaroTempHygrometerListener baroTempHygrometerListener) {
        int buzzerStatus = (bytes[1] & 0xff);
        baroTempHygrometerListener.onBuzzer(buzzerStatus == 0x00);
    }
}
