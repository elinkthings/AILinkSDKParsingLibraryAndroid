package cn.net.aicare.modulelibrary.module.BoraTempHumidity;

import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.Calendar;
import java.util.TimeZone;

public class BleSendUtils {


    /**
     * 获取到支持的功能列表
     */
    public static byte[] getSupportFunction() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x80;
        bytes[1] = 0x00;
        return bytes;
    }

    /**
     * 获取设备状态
     */
    public static byte[] getDeviceStatus() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x00;
        return bytes;
    }

    /**
     * 修改设备变化阀值
     */
    public static byte[] setThreshold(int temp, int humidity, int barometric) {
        byte[] bytes = new byte[5];
        bytes[0] = 0x03;
        bytes[1] = (byte) (temp & 0xFF);
        bytes[2] = (byte) (humidity & 0xFF);
        bytes[3] = (byte) (barometric & 0xFF);
        bytes[4] = 0x00;
        return bytes;
    }


    /**
     * 获取离线历史记录
     *
     * @param time 时间
     */
    public static byte[] getOfflineRecord(long time) {
        BleLog.i("读取设备离线历史记录:" + time);
        byte[] bytes = new byte[5];
        bytes[0] = 0x05;
        bytes[4] = (byte) ((time & 0xff000000L) >> 24);
        bytes[3] = (byte) ((time & 0x00ff0000L) >> 16);
        bytes[2] = (byte) ((time & 0x0000ff00L) >> 8);
        bytes[1] = (byte) (time & 0x000000ffL);
        return bytes;
    }

    /**
     * App 收到历史数据后反馈
     *
     * @param status 状态 0x00:全部接收完毕，停止发送, 0x01：接收未完成，请发下一组数据。
     */
    public static byte[] sendOfflineStatus(int status) {
        byte[] bytes = new byte[6];
        bytes[0] = 0x09;
        bytes[1] = (byte) status;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        return bytes;
    }

    /**
     * 设置采样频率，保存频率，定时器间隔
     *
     * @param samplingFrequency 采样频率
     * @param saveFrequency     保存频率
     * @param timeInterval      定时器间隔
     * @return
     */
    public static byte[] setFrequency(int samplingFrequency, int saveFrequency, int timeInterval) {
        byte[] bytes = new byte[4];
        bytes[0] = 0x07;
        bytes[1] = (byte) (samplingFrequency & 0xFF);
        bytes[2] = (byte) (saveFrequency & 0xFF);
        bytes[3] = (byte) (timeInterval & 0xFF);
        return bytes;
    }

    /**
     * 获取校准值
     *
     * @return
     */
    public static byte[] getCalibration() {
        byte[] bytes = new byte[6];
        bytes[0] = 0x0a;
        bytes[1] = 0x02;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        return bytes;
    }

    /**
     * 设置校准值
     *
     * @param tempCal
     * @param humidityCal
     * @param barometricCal
     * @return
     */
    public static byte[] setCalibration(int tempCal, int humidityCal, int barometricCal) {
        byte[] bytes = new byte[6];
        bytes[0] = 0x0a;
        bytes[1] = 0x01;
        // 温度校准值
        int myTempCal = Math.abs(tempCal);
        bytes[2] = (byte) ((0x7f & myTempCal) | (tempCal < 0 ? 0x80 : 0x00));
        // 湿度校准值
        int myHumidityCal = Math.abs(humidityCal);
        bytes[3] = (byte) ((0x7f & myHumidityCal) | (humidityCal < 0 ? 0x80 : 0x00));
        // 气压校准值
        int myBarometricCal = Math.abs(barometricCal);
        bytes[4] = (byte) ((0x7f & myBarometricCal) | (barometricCal < 0 ? 0x80 : 0x00));
        bytes[5] = 0x00;
        return bytes;
    }

    /**
     * 寻物功能
     *
     * @param state 0x00:开始寻物 0x01:停止寻物
     * @return
     */
    public static byte[] findDevice(int state) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x2c;
        bytes[1] = (byte) state;
        bytes[2] = 0x00;
        return bytes;
    }

    /**
     * 读取设备蜂鸣器
     */
    public static byte[] getSound() {
        byte[] bytes = new byte[3];
        bytes[0] = 0x20;
        bytes[1] = 0x00;
        bytes[2] = 0x00;
        return bytes;
    }

    /**
     * 设置设备蜂鸣器
     *
     * @param isOpen true:打开 false:关闭
     */
    public static byte[] setSound(boolean isOpen) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x20;
        bytes[1] = 0x01;
        bytes[2] = (byte) (isOpen ? 0x00 : 0x01);
        return bytes;
    }

    /**
     * 查询闹钟功能
     */
    public static byte[] checkAlarmClock() {
        byte[] bytes = new byte[14];
        bytes[0] = 0x22;
        bytes[1] = 0x03;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x00;
        bytes[8] = 0x00;
        bytes[9] = 0x00;
        bytes[10] = 0x00;
        bytes[11] = 0x00;
        bytes[12] = 0x00;
        bytes[13] = 0x00;
        return bytes;
    }


    public static byte[] setAlarmClock(int type, int no, int mode, int seconds, int state) {
        byte[] bytes = new byte[14];
        bytes[0] = 0x22;
        bytes[1] = (byte) type;
        bytes[2] = (byte) no;
        bytes[3] = (byte) mode;
        bytes[4] = (byte) (seconds / 3600);
        bytes[5] = (byte) ((seconds % 3600) / 60);
        bytes[6] = 0x00;
        bytes[7] = (byte) state;
        bytes[8] = 0x00;
        bytes[9] = 0x00;
        bytes[10] = 0x00;
        bytes[11] = 0x00;
        bytes[12] = 0x00;
        bytes[13] = 0x00;
        return bytes;
    }

    /**
     * 读取功能状态  2A 查询单位  24 整点报时功能	26 夜灯 28  背光
     */
    public static byte[] checkOtherFunction(int type) {
        byte[] bytes = new byte[14];
        bytes[0] = (byte) type;
        bytes[1] = 0x00;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x00;
        bytes[8] = 0x00;
        bytes[9] = 0x00;
        bytes[10] = 0x00;
        bytes[11] = 0x00;
        bytes[12] = 0x00;
        bytes[13] = 0x00;
        return bytes;
    }


    /**
     * 读取功能状态  2A设置单位 24 整点报时功能	26 夜灯 28  背光
     *
     * @param state 0 关  1开
     */
    public static byte[] setOtherFunction(int type, int state, int value) {
        byte[] bytes = new byte[14];
        bytes[0] = (byte) type;
        bytes[1] = 0x01;
        bytes[2] = (byte) state;
        bytes[3] = (byte) value;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x00;
        bytes[8] = 0x00;
        bytes[9] = 0x00;
        bytes[10] = 0x00;
        bytes[11] = 0x00;
        bytes[12] = 0x00;
        bytes[13] = 0x00;
        return bytes;
    }


    public static byte[] setWarmConfig(boolean openT, float tempH, float tempL, boolean openH, int humidityWarmH, int humidityWarmL) {
        byte[] bytes = new byte[11];
        bytes[0] = 0x13;
        bytes[1] = (byte) (openT ? 0x01 : 0x00);
        boolean isFun = tempH < 0;
        int myTempH = (int) Math.abs(Math.round((tempH * 10)));
        bytes[2] = (byte) ((byte) ((myTempH & 0x7f00) >> 8) + (isFun ? 0x80 : 0x00));
        bytes[3] = (byte) (myTempH & 0x00ff);
        isFun = tempL < 0;
        int myTempL = (int) Math.abs(Math.round((tempL * 10)));
        bytes[4] = (byte) ((byte) ((myTempL & 0x7f00) >> 8) + (isFun ? 0x80 : 0x00));
        bytes[5] = (byte) (myTempL & 0x00ff);
        bytes[6] = (byte) (openH ? 0x01 : 0x00);
        humidityWarmH = humidityWarmH * 10;
        bytes[7] = (byte) ((humidityWarmH & 0xff00) >> 8);
        bytes[8] = (byte) (humidityWarmH & 0x00ff);
        humidityWarmL = humidityWarmL * 10;

        bytes[9] = (byte) ((humidityWarmL & 0xff00) >> 8);
        bytes[10] = (byte) (humidityWarmL & 0x00ff);
        return bytes;
    }

    public static byte[] setReportTime(int time) {

        byte[] bytes = new byte[3];
        bytes[0] = 0x14;
        bytes[1] = (byte) ((time & 0xff00) >> 8);
        bytes[2] = (byte) (time & 0x00ff);
        return bytes;
    }

    public static String replenishData(byte[] hex) {
        byte[] bytes = new byte[6 + hex.length];
        bytes[0] = (byte) 0xA7;
        bytes[1] = 0x00;
        bytes[2] = 0x36;
        bytes[3] = (byte) hex.length;
        System.arraycopy(hex, 0, bytes, 4, hex.length);
        bytes[bytes.length - 2] = checkValue(bytes);
        bytes[bytes.length - 1] = 0x7A;
        return BleStrUtils.byte2HexStr_1(bytes);
    }


    private static byte checkValue(byte[] bytes) {
        int checkValue = 0;
        for (int i = 1; i < bytes.length; i++) {
            checkValue = bytes[i] + checkValue;
        }
        return (byte) (checkValue & 0xff);
    }

    public static byte[] synTimeA6() {
        TimeZone timeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTime().getTime() / 1000;
        TimeZone.setDefault(timeZone);

        byte[] bytes = new byte[5];
        bytes[0] = 0x45;
        bytes[4] = (byte) ((time & 0xff000000L) >> 24);
        bytes[3] = (byte) ((time & 0x00ff0000L) >> 16);
        bytes[2] = (byte) ((time & 0x0000ff00L) >> 8);
        bytes[1] = (byte) (time & 0x000000ffL);
        return bytes;
    }

    /**
     * APP获取设备信息
     */
    public static byte[] getAppInfo() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x10;
        bytes[1] = 0x00;
        return bytes;
    }

    /**
     * 绑定设备
     */
    public static byte[] startBindDevice() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x2E;
        bytes[1] = 0x00;
        return bytes;
    }


    /**
     * 绑定设备成功
     */
    public static byte[] bindDeviceSuccess() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x2E;
        bytes[1] = 0x02;
        return bytes;
    }
}
