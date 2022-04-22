package cn.net.aicare.modulelibrary.module.LeapWatch;

import android.util.Log;

import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeapWatchData extends BaseBleDeviceData implements OnBleOtherDataListener, OnBleMtuListener {

    private static final String TAG = "Tag1";

    private int mMaxMtu;

    public LeapWatchData(BleDevice bleDevice) {
        super(bleDevice);
        if (bleDevice != null) {
            bleDevice.setA7Encryption(false);
            bleDevice.setOnBleOtherDataListener(this);
            bleDevice.setOnBleMtuListener(this);
            bleDevice.setMtu(517);
        }
    }

    @Override
    public void OnMtu(int mtu) {
        mMaxMtu = mtu;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        // A7数据，不会走这里
    }

    @Override
    public void onNotifyOtherData(byte[] data) {
        // 透传数据从这里回
        if (mOnCallback != null) {
            Log.i(TAG, "接收：" + data.length + "：[" + BleStrUtils.byte2HexStr(data) + "]");
            mOnCallback.onReceive(data);
        }
        // 前6位固定，后3位判断是什么指令
        // 数据至少有9位，低于说明发送的不对，直接不处理
        if (data.length < 9) {
            return;
        }
        if ((data[0] & 0xFF) != 0xDD) {
            return;
        }
        // 将payloads数据取出来
        byte[] hex = new byte[data.length - 6];
        System.arraycopy(data, 6, hex, 0, data.length - 6);
        // 全部都与上0xFF
        for (int i = 0; i < hex.length; i++) {
            hex[i] = (byte) (hex[i] & 0xFF);
        }
        // 判断是什么指令，进行解析
        try {
            switch (hex[0]) {
                case 0x01:
                    // 控制命令
                    decodeControlData(hex);
                    break;
                case 0x02:
                    // 绑定命令
                    decodeBindData(hex);
                    break;
                case 0x03:
                    // 提醒命令
                    decodeRemindData(hex);
                    break;
                case 0x04:
                    // 数据传输命令
                    decodeDataTransData(hex);
                    break;
                case 0x05:
                    // 系统控制命令
                    decodeSystemControlData(hex);
                    break;
                case 0x06:
                    // 文件传输命令
                    decodeFileTransData(hex);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    // ================================================================================================================================

    /**
     * 解析控制命令
     *
     * @param hex 0x01
     */
    private void decodeControlData(byte[] hex) {
        switch (hex[1]) {
            case 0x01:
                callbackSystemTime(hex);
                break;
            case 0x02:
                callbackSystemTimeZone(hex);
                break;
            case 0x03:
                callbackSystemBattery(hex);
                break;
            case 0x04:
                callbackSystemVersion(hex);
                break;
            case 0x05:
                callbackSystemMac(hex);
                break;
            case 0x06:
                callbackUser(hex);
                break;
            case 0x07:
                callbackStepTarget(hex);
                break;
            case 0x08:
                callbackBackLight(hex);
                break;
            case 0x09:
                callbackSedentary(hex);
                break;
            case 0x0A:
                callbackDisturb(hex);
                break;
            case 0x0B:
                callbackShake(hex);
                break;
            case 0x0C:
                callbackHandLight(hex);
                break;
            case 0x0D:
                callbackSleep(hex);
                break;
            case 0x0E:
                callbackHourSystem(hex);
                break;
            case 0x0F:
                callbackLanguage(hex);
                break;
            case 0x10:
                callbackAlarm(hex);
                break;
            case 0x11:
                callbackMetric(hex);
                break;
            // 0x12未用
            case 0x13:
                callbackSetFindPhone(hex);
                break;
            case 0x14:
                callbackSetMessagePush(hex);
                break;
            case 0x15:
                callbackSetAntiLost(hex);
                break;
            case 0x16:
                callbackSetHeart(hex);
                break;


        }
    }

    /**
     * 解析绑定命令
     *
     * @param hex 0x02
     */
    private void decodeBindData(byte[] hex) {
        switch (hex[1]) {
            case 0x01:
                callbackBind(hex);
                break;
            case 0x02:
                callbackLogin(hex);
                break;
            case 0x03:
                callbackPair(hex);
                break;
        }
    }

    /**
     * 解析提醒命令
     *
     * @param hex 0x03
     */
    private void decodeRemindData(byte[] hex) {
        switch (hex[1]) {
            case 0x01:
                callbackPushMessage(hex);
                break;
            // 0x02推送音乐未做
            case 0x03:
                callbackPushWeatherNow(hex);
                break;
            case 0x04:
                callbackPushWeatherFeature(hex);
                break;
        }
    }

    /**
     * 解析数据传输命令
     *
     * @param hex 0x04
     */
    private void decodeDataTransData(byte[] hex) {
        switch (hex[1]) {
            case 0x01:
                callbackHeart(hex);
                break;
        }
    }

    /**
     * 解析系统控制命令
     *
     * @param hex 0x05
     */
    private void decodeSystemControlData(byte[] hex) {
        switch (hex[1]) {
            case 0x01:
                callbackCameraControl(hex);
                break;
        }
    }

    /**
     * 解析文件传输命令
     *
     * @param hex 0x06
     */
    private void decodeFileTransData(byte[] hex) {
        switch (hex[1]) {
            case 0x01:
                callbackDialFileTrans(hex);
                break;
        }
    }

    // ================================================================================================================================

    /**
     * 解析系统时间
     *
     * @param hex 0x01
     */
    private void callbackSystemTime(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetSystemTime(status);
            } else {
                int year = hex[3] + 2000;
                int month = hex[4];
                int day = hex[5];
                int hour = hex[6];
                int minute = hex[7];
                int second = hex[8];
                mOnCallback.onCallbackSystemTime(year, month, day, hour, minute, second);
            }
        }
    }

    /**
     * 解析系统时区
     *
     * @param hex 0x02
     */
    private void callbackSystemTimeZone(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetSystemTimeZone(status);
            } else {
                int min15 = hex[3] ^= (1 << 7);// 有符号
                long ms = min15 * 15 * 60 * 1000;
                mOnCallback.onCallbackSystemTimeZone(ms);
            }
        }
    }

    /**
     * 解析电量
     *
     * @param hex 0x03
     */
    private void callbackSystemBattery(byte[] hex) {
        if (mOnCallback != null) {
            int battery = hex[3];
            mOnCallback.onCallbackSystemBattery(battery);
        }
    }

    /**
     * 解析系统版本
     *
     * @param hex 0x04
     */
    private void callbackSystemVersion(byte[] hex) {
        if (mOnCallback != null) {
            int version = 0;
            for (int i = 0; i < 3; i++) {
                version += (hex[i + 3] << ((3 - i) * 8));
            }
            mOnCallback.onCallbackSystemVersion(String.valueOf(version));
        }
    }

    /**
     * 解析系统Mac
     *
     * @param hex 0x05
     */
    private void callbackSystemMac(byte[] hex) {
        if (mOnCallback != null) {
            StringBuilder mac = new StringBuilder();
            for (int i = 5; i >= 0; i--) {
                mac.append(Integer.toHexString(hex[i + 3]));
                if (i > 0) {
                    mac.append(":");
                }
            }
            mOnCallback.onCallbackSystemMac(mac.toString());
        }
    }

    /**
     * 解析用户信息
     *
     * @param hex 0x06
     */
    private void callbackUser(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetUser(status);
            } else {
                int metric = hex[3];
                int sex = hex[4];
                int age = hex[5];

                int height05 = 0;
                for (int i = 0; i < 4; i++) {
                    height05 += (hex[i + 6] << ((3 - i) * 8));
                }
                float height = getPreFloat(height05 * 0.5f, 1);

                int weight05 = 0;
                for (int i = 0; i < 4; i++) {
                    weight05 += (hex[i + 10] << ((3 - i) * 8));
                }
                float weight = getPreFloat(weight05 * 0.5f, 1);

                mOnCallback.onCallbackUser(metric, sex, age, height, weight);
            }
        }
    }

    /**
     * 解析步数目标
     *
     * @param hex 0x07
     */
    private void callbackStepTarget(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetStepTarget(status);
            } else {
                int target = 0;
                for (int i = 3; i >= 0; i--) {
                    target += (hex[i + 3] << (i * 8));
                }
                mOnCallback.onCallbackStepTarget(target);
            }
        }
    }

    /**
     * 解析背光设置
     *
     * @param hex 0x08
     */
    private void callbackBackLight(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetBackLight(status);
            } else {
                int value = hex[3];
                mOnCallback.onCallbackBackLight(value);
            }
        }
    }

    /**
     * 解析久坐提醒
     *
     * @param hex 0x09
     */
    private void callbackSedentary(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetSedentary(status);
            } else {
                boolean isOpen = (hex[3] >> 7) == 1;
                int[] dayFlag = new int[7];
                for (int i = 0; i < 7; i++) {
                    dayFlag[i] = getBit(hex[3], i);
                }
                int startHour = hex[4];
                int endHour = hex[5];
                int startMinute = hex[6];
                int endMinute = hex[7];
                int interval = hex[8];

                mOnCallback.onCallbackSedentary(isOpen, dayFlag, startHour, startMinute, endHour, endMinute, interval);
            }
        }
    }

    /**
     * 解析勿扰模式
     *
     * @param hex 0x0A
     */
    private void callbackDisturb(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetDisturb(status);
            } else {
                boolean allOpen = (hex[3] == 1);

                List<LeapWatchDisturbBean> list = new ArrayList<>();
                int count = (hex.length - 4) / 5;
                for (int i = 0; i < count; i++) {
                    boolean isOpen = (hex[i * 5 + 5] == 1);
                    int startHour = hex[i * 5] + 6;
                    int endHour = hex[i * 5] + 7;
                    int startMinute = hex[i * 5] + 8;
                    int endMinute = hex[i * 5] + 9;
                    list.add(new LeapWatchDisturbBean(isOpen, startHour, startMinute, endHour, endMinute));
                }

                mOnCallback.onCallbackDisturb(allOpen, list);
            }
        }
    }

    /**
     * 解析振动设置
     *
     * @param hex 0x0B
     */
    private void callbackShake(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetShake(status);
            } else {
                int value = hex[3];
                mOnCallback.onCallbackShake(value);
            }
        }
    }

    /**
     * 解析抬手亮
     *
     * @param hex 0x0C
     */
    private void callbackHandLight(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetHandLight(status);
            } else {
                boolean isOpen = (hex[3] == 0x01);
                int startHour = hex[4];
                int endHour = hex[5];
                int startMinute = hex[6];
                int endMinute = hex[7];
                mOnCallback.onCallbackHandLight(isOpen, startHour, endHour, startMinute, endMinute);
            }
        }
    }

    /**
     * 解析睡眠
     *
     * @param hex 0x0D
     */
    private void callbackSleep(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetSleep(status);
            } else {
                int value = hex[3];
                mOnCallback.onCallbackSleep(value);
            }
        }
    }

    /**
     * 解析小时制
     *
     * @param hex 0x0E
     */
    private void callbackHourSystem(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetHourSystem(status);
            } else {
                boolean is24 = hex[3] == 0x00;
                mOnCallback.onCallbackHourSystem(is24);
            }
        }
    }

    /**
     * 解析语言
     *
     * @param hex 0x0F
     */
    private void callbackLanguage(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetLanguage(status);
            } else {
                int value = hex[3];
                mOnCallback.onCallbackLanguage(value);
            }
        }
    }

    /**
     * 解析闹钟
     *
     * @param hex 0x10
     */
    private void callbackAlarm(byte[] hex) {
        // TODO 闹钟没搞懂 之后再做
    }

    /**
     * 解析公英制
     *
     * @param hex 0x11
     */
    private void callbackMetric(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetMetric(status);
            } else {
                int value = hex[3];
                mOnCallback.onCallbackMetric(value);
            }
        }
    }

    /**
     * 解析找手机
     *
     * @param hex 0x13
     */
    private void callbackSetFindPhone(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackSetFindPhone(status);
        }
    }

    /**
     * 解析信息提醒
     *
     * @param hex 0x14
     */
    private void callbackSetMessagePush(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackSetMessagePush(status);
        }
    }

    /**
     * 解析防丢设置
     *
     * @param hex 0x15
     */
    private void callbackSetAntiLost(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackSetAntiLost(status);
        }
    }

    /**
     * 解析设置定时心率
     *
     * @param hex 0x16
     */
    private void callbackSetHeart(byte[] hex) {
        if (mOnCallback != null) {
            if (hex[2] == 0x00) {
                int status = hex[3];
                mOnCallback.onCallbackSetHeart(status);
            } else {
                boolean isOpen = (hex[3] >> 7) == 1;
                int[] dayFlag = new int[7];
                for (int i = 0; i < 7; i++) {
                    dayFlag[i] = getBit(hex[3], i);
                }
                int startHour = hex[4];
                int endHour = hex[5];
                int startMinute = hex[6];
                int endMinute = hex[7];
                int interval = hex[8];

                mOnCallback.onCallbackHeart(isOpen, dayFlag, startHour, startMinute, endHour, endMinute, interval);
            }
        }
    }

    /**
     * 解析绑定
     *
     * @param hex 0x01
     */
    private void callbackBind(byte[] hex) {

    }

    /**
     * 解析登录
     *
     * @param hex 0x02
     */
    private void callbackLogin(byte[] hex) {

    }

    /**
     * 解析配对
     *
     * @param hex 0x03
     */
    private void callbackPair(byte[] hex) {

    }

    /**
     * 解析推送消息
     *
     * @param hex 0x01
     */
    private void callbackPushMessage(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackPushMessage(status);
        }
    }

    /**
     * 解析推送实时天气
     *
     * @param hex 0x03
     */
    private void callbackPushWeatherNow(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackPushWeatherNow(status);
        }
    }

    /**
     * 解析推送预报天气
     *
     * @param hex 0x04
     */
    private void callbackPushWeatherFeature(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackPushWeatherFeature(status);
        }
    }

    /**
     * 解析心率
     *
     * @param hex 0x01
     */
    private void callbackHeart(byte[] hex) {

    }

    /**
     * 解析拍照控制
     *
     * @param hex 0x01
     */
    private void callbackCameraControl(byte[] hex) {
        if (mOnCallback != null) {
            int status = hex[3];
            mOnCallback.onCallbackCameraControl(status);
        }
    }

    /**
     * 解析表盘数据传输
     *
     * @param hex 0x01
     */
    private void callbackDialFileTrans(byte[] hex) {

    }

    // ================================================================================================================================

    /**
     * APP下发系统时间，直接发，不用管时区
     */
    public void appSetSystemTime(long stamp) {
        String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(stamp);
        String[] dateSplit = date.split("-");
        int year = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[2]);
        int hour = Integer.parseInt(dateSplit[3]);
        int minute = Integer.parseInt(dateSplit[4]);
        int second = Integer.parseInt(dateSplit[5]);

        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (year - 2000);
        hex[4] = (byte) month;
        hex[5] = (byte) day;
        hex[6] = (byte) hour;
        hex[7] = (byte) minute;
        hex[8] = (byte) second;
        appWriteData(hex);
    }

    /**
     * APP读取系统时间
     */
    public void appGetSystemTime() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP下发系统时区
     *
     * @param rawOffset TimeZone.getDefault().getRawOffset()
     */
    public void appSetSystemTimeZone(int rawOffset) {
        int minute15 = rawOffset / 1000 / 60 / 15;

        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) minute15;
        appWriteData(hex);
    }

    /**
     * APP读取系统时区
     */
    public void appGetSystemTimeZone() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP读取设备电量
     */
    public void appGetSystemBattery() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x03;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP读取设备MAC地址
     */
    public void appGetSystemMac() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x04;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP读取系统版本号
     */
    public void appGetSystemVersion() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x05;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置用户信息
     *
     * @param unit   传0
     * @param height 单位0.5cm
     * @param weight 单位0.5kg
     */
    public void appSetUser(int unit, int sex, int age, int height, int weight) {
        byte[] hex = new byte[14];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x06;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) unit;
        hex[4] = (byte) sex;
        hex[5] = (byte) age;
        hex[6] = (byte) ((height & 0xFF000000) >> 24);
        hex[7] = (byte) ((height & 0x00FF0000) >> 16);
        hex[8] = (byte) ((height & 0x0000FF00) >> 8);
        hex[9] = (byte) ((height & 0x000000FF));
        hex[10] = (byte) ((weight & 0xFF000000) >> 24);
        hex[11] = (byte) ((weight & 0x00FF0000) >> 16);
        hex[12] = (byte) ((weight & 0x0000FF00) >> 8);
        hex[13] = (byte) ((weight & 0x000000FF));
        appWriteData(hex);
    }

    /**
     * APP读取用户信息
     */
    public void appGetUser() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x06;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置目标步数；小端序？
     */
    public void appSetStepTarget(int target) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x07;
        hex[2] = (byte) 0x00;
        hex[6] = (byte) ((target & 0xFF000000) >> 24);
        hex[5] = (byte) ((target & 0x00FF0000) >> 16);
        hex[4] = (byte) ((target & 0x0000FF00) >> 8);
        hex[3] = (byte) ((target & 0x000000FF));
        appWriteData(hex);
    }

    /**
     * APP获取目标步数
     */
    public void appGetStepTarget() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x07;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置背光开关
     *
     * @param isOpen 是否打开
     */
    public void appSetBackLight(boolean isOpen) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x08;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 0x01 : 0x00);
        appWriteData(hex);
    }

    /**
     * APP读取背光开关
     */
    public void appGetBackLight() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x08;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置久坐开关
     *
     * @param isOpen      总开关
     * @param dayFlag     [0]星期一；[6]星期天
     * @param startHour   0~23
     * @param startMinute 0~59
     * @param endHour     0~23
     * @param endMinute   0~59
     * @param interval    分钟
     */
    public void appSetSedentary(boolean isOpen, int[] dayFlag, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x09;
        hex[2] = (byte) 0x00;
        int flag = 0;
        for (int i = 0; i < dayFlag.length; i++) {
            flag += (dayFlag[i] << i);
        }
        hex[3] = (byte) (flag + ((isOpen ? 1 : 0) << 7));
        hex[4] = (byte) startHour;
        hex[5] = (byte) startMinute;
        hex[6] = (byte) endHour;
        hex[7] = (byte) endMinute;
        hex[8] = (byte) interval;
        appWriteData(hex);
    }

    /**
     * APP读取久坐开关
     */
    public void appGetSedentary() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x09;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置勿扰模式
     *
     * @param allOpen 总开关
     * @param list    bean
     */
    public void appSetDisturb(boolean allOpen, List<LeapWatchDisturbBean> list) {
        byte[] hex = new byte[4 + list.size() * 5];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0A;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (allOpen ? 0x01 : 0x00);
        int start = 4;
        for (int i = 0; i < list.size(); i++) {
            LeapWatchDisturbBean bean = list.get(i);
            hex[start + 4 * i + 0] = (byte) (bean.isOpen() ? 0x01 : 0x00);
            hex[start + 4 * i + 1] = (byte) (bean.getStartHour());
            hex[start + 4 * i + 2] = (byte) (bean.getStartMinute());
            hex[start + 4 * i + 3] = (byte) (bean.getEndHour());
            hex[start + 4 * i + 4] = (byte) (bean.getEndMinute());
        }
        appWriteData(hex);
    }

    /**
     * APP读取勿扰模式
     */
    public void appGetDisturb() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0A;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置振动模式
     *
     * @param count 振动次数
     */
    public void appSetShake(int count) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0B;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) count;
        appWriteData(hex);
    }

    /**
     * APP获取振动模式
     */
    public void appGetShake() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0B;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置抬手亮屏
     *
     * @param isOpen      开关
     * @param startHour   0~23
     * @param startMinute 0~59
     * @param endHour     0~23
     * @param endMinute   0~59
     */
    public void appSetHandLight(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        byte[] hex = new byte[8];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0C;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 0x01 : 0x00);
        hex[4] = (byte) startHour;
        hex[5] = (byte) startMinute;
        hex[6] = (byte) endHour;
        hex[7] = (byte) endMinute;
        appWriteData(hex);
    }

    /**
     * APP读取抬手亮屏
     */
    public void appGetHandLight() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0C;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置睡眠辅助功能
     *
     * @param isOpen 是否开启
     */
    public void appSetSleep(boolean isOpen) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0D;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 0x01 : 0x00);
        appWriteData(hex);
    }

    /**
     * APP读取睡眠辅助功能
     */
    public void appGetSleep() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0D;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置小时制
     *
     * @param is24 是24小时制
     */
    public void appSetHourSystem(boolean is24) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0E;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (is24 ? 0x00 : 0x01);
        appWriteData(hex);
    }

    /**
     * APP读取小时制
     */
    public void appGetHourSystem() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0E;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置手表语言
     *
     * @param languageId 0x00中文；0x01英文
     */
    public void appSetLanguage(int languageId) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0F;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) languageId;
        appWriteData(hex);
    }

    /**
     * APP读取手表语言
     */
    public void appGetLanguage() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0F;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP添加闹钟
     *
     * @param alarmBean 闹钟
     */
    public void appAddAlarm(LeapWatchAlarmBean alarmBean) {
        byte[] hex = new byte[10];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x10;
        hex[2] = (byte) 0x20;
        hex[3] = (byte) alarmBean.getId();

        int flag = 0;
        for (int i = 0; i < alarmBean.getDayFlag().length; i++) {
            flag += (alarmBean.getDayFlag()[i] << i);
        }
        hex[4] = (byte) (flag + ((alarmBean.isOpen() ? 1 : 0) << 7));
        hex[5] = (byte) alarmBean.getYear();
        hex[6] = (byte) alarmBean.getMonth();
        hex[7] = (byte) alarmBean.getDay();
        hex[8] = (byte) alarmBean.getHour();
        hex[9] = (byte) alarmBean.getMinute();
        appWriteData(hex);
    }

    /**
     * APP读取闹钟
     */
    public void appGetAlarm(int alarmId) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x10;
        hex[2] = (byte) 0x10;
        hex[3] = (byte) alarmId;
        appReadData(hex);
    }

    /**
     * APP设置公英制
     *
     * @param metric 0x00公制；0x01英制
     */
    public void appSetMetric(int metric) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x11;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) metric;
        appWriteData(hex);
    }

    /**
     * APP读取公英制
     */
    public void appGetMetric() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x11;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP设置查找手机功能是否开启？
     */
    public void appSetFindPhone(boolean isOpen) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x13;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 0x00 : 0x01);
        appWriteData(hex);
    }

    /**
     * APP设置消息推送开关
     * 没什么意义，APP自己会筛选哪些消息需要推送，这个全打开就行了
     *
     * @param messages 4byte
     */
    public void appSetMessagePush(byte[] messages) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x14;
        hex[2] = (byte) 0x00;
        System.arraycopy(messages, 0, hex, 3, messages.length);
        appWriteData(hex);
    }

    /**
     * APP设置防丢功能是否开启
     */
    public void appSetAntiLost(boolean isOpen) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x15;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 0x01 : 0x00);
        appWriteData(hex);
    }

    /**
     * APP设置定时心率开关
     *
     * @param isOpen      总开关
     * @param dayFlag     [0]星期一；[6]星期天
     * @param startHour   0~23
     * @param startMinute 0~59
     * @param endHour     0~23
     * @param endMinute   0~59
     * @param interval    分钟
     */
    public void appSetHeart(boolean isOpen, int[] dayFlag, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x16;
        hex[2] = (byte) 0x00;
        int flag = 0;
        for (int i = 0; i < dayFlag.length; i++) {
            flag += (dayFlag[i] << i);
        }
        hex[3] = (byte) (flag + ((isOpen ? 1 : 0) << 7));
        hex[4] = (byte) startHour;
        hex[5] = (byte) startMinute;
        hex[6] = (byte) endHour;
        hex[7] = (byte) endMinute;
        hex[8] = (byte) interval;
        appWriteData(hex);
    }

    /**
     * APP读取定时心率开关
     */
    public void appGetHeart() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x16;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP请求绑定设备
     *
     * @param userId 用户ID
     */
    public void appBind(long userId) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x02;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x20;
        hex[3] = (byte) ((userId & 0xFF000000) >> 24);
        hex[4] = (byte) ((userId & 0x00FF0000) >> 16);
        hex[5] = (byte) ((userId & 0x0000FF00) >> 8);
        hex[6] = (byte) ((userId & 0x000000FF));
        appWriteData(hex);
    }

    /**
     * APP请求登录
     *
     * @param userId 用户ID
     */
    public void appLogin(int userId) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x02;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x20;
        hex[3] = (byte) ((userId & 0xFF000000) >> 24);
        hex[4] = (byte) ((userId & 0x00FF0000) >> 16);
        hex[5] = (byte) ((userId & 0x0000FF00) >> 8);
        hex[6] = (byte) ((userId & 0x000000FF));
        appWriteData(hex);
    }

    /**
     * APP请求配对
     */
    public void appPair() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x02;
        hex[1] = (byte) 0x03;
        hex[2] = (byte) 0x00;
        appWriteData(hex);
    }

    /**
     * APP推送消息给手表
     *
     * @param stamp    消息产生的时间戳
     * @param packName 包名
     * @param title    标题
     * @param message  内容
     */
    public void appPushMessage(long stamp, String packName, String title, String message) {
        try {
            byte[] packNameByte = packName.getBytes(StandardCharsets.UTF_8);
            byte[] titleByte = title.getBytes(StandardCharsets.UTF_8);
            byte[] messageByte = message.getBytes(StandardCharsets.UTF_8);

            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(stamp);
            String[] dateSplit = date.split("-");
            int year = Integer.parseInt(dateSplit[0]);
            int month = Integer.parseInt(dateSplit[1]);
            int day = Integer.parseInt(dateSplit[2]);
            int hour = Integer.parseInt(dateSplit[3]);
            int minute = Integer.parseInt(dateSplit[4]);
            int second = Integer.parseInt(dateSplit[5]);

            // 3是协议开头，71是包名和标题，剩下的都是内容
            byte[] hex = new byte[3 + 71 + messageByte.length];
            hex[0] = (byte) 0x03;
            hex[1] = (byte) 0x01;
            hex[2] = (byte) 0x00;
            hex[3] = (byte) 0x00;// categoryId
            hex[4] = (byte) (year - 2000);
            hex[5] = (byte) month;
            hex[6] = (byte) day;
            hex[7] = (byte) hour;
            hex[8] = (byte) minute;
            hex[9] = (byte) second;

            System.arraycopy(packNameByte, 0, hex, 10, packNameByte.length);
            System.arraycopy(titleByte, 0, hex, 42, titleByte.length);
            System.arraycopy(messageByte, 0, hex, 74, messageByte.length);

            appWriteData(hex);
        } catch (Exception e) {
            Log.e(TAG, "推送消息失败：" + e.toString());
        }
    }

    /**
     * APP推送天气给手表
     *
     * @param isNow      true实时天气，false天气预报
     * @param stamp      天气时间戳
     * @param temp       温度
     * @param highTemp   高温
     * @param lowTemp    低温
     * @param type       类型？雨天？晴天
     * @param wind       风速
     * @param humidity   湿度
     * @param visibility 可见度
     * @param uv         紫外线
     * @param rain       雨量
     */
    public void appPushWeather(boolean isNow, long stamp, int temp, int highTemp, int lowTemp, int type, int wind, int humidity, int visibility, int uv, int rain) {
        String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(stamp);
        String[] dateSplit = date.split("-");
        int year = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[2]);
        int hour = Integer.parseInt(dateSplit[3]);
        int minute = Integer.parseInt(dateSplit[4]);
        int second = Integer.parseInt(dateSplit[5]);

        byte[] hex = new byte[19];
        hex[0] = (byte) 0x03;
        hex[1] = (byte) (isNow ? 0x03 : 0x04);
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (year - 2000);
        hex[4] = (byte) month;
        hex[5] = (byte) day;
        hex[6] = (byte) hour;
        hex[7] = (byte) minute;
        hex[8] = (byte) second;
        hex[9] = (byte) temp;
        hex[10] = (byte) highTemp;
        hex[11] = (byte) lowTemp;
        hex[12] = (byte) type;
        hex[13] = (byte) wind;
        hex[14] = (byte) humidity;
        hex[15] = (byte) visibility;
        hex[16] = (byte) uv;
        hex[17] = (byte) (rain & 0xFF00);
        hex[18] = (byte) (rain & 0x00FF);
        appWriteData(hex);
    }

    /**
     * APP读取心率数据
     */
    public void appGetHeartData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除心率数据
     */
    public void appDeleteHeartData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP读取每日数据
     */
    public void appGetDailyData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除每日数据
     */
    public void appDeleteDailyData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP读取步数数据
     */
    public void appGetStepData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x03;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除步数数据
     */
    public void appDeleteStepData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x03;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP设置拍照控制
     *
     * @param op 0进入；1退出；2拍照
     */
    public void appSetCameraControl(int op) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x05;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) op;
        appWriteData(hex);
    }

    // TODO 表盘文件传输

    // ================================================================================================================================

    /**
     * 保留小数位
     *
     * @param f       float
     * @param decimal 保留几位
     * @return float
     */
    private float getPreFloat(float f, int decimal) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(decimal, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 获取byte某一位的bit
     *
     * @param b     byte
     * @param index index
     * @return bit
     */
    public int getBit(byte b, int index) {
        return ((b >> index) & (0xFF >> (8 - index)));
    }

    /**
     * 发送读
     */
    private void appReadData(byte[] data) {
        appSendDataToBle(data, true);
    }

    /**
     * 发送写
     */
    private void appWriteData(byte[] data) {
        appSendDataToBle(data, true);
    }

    private void appSendDataToBle(byte[] data, boolean isWrite) {
        byte[] hex = new byte[6 + data.length];

        // 包头固定
        hex[0] = (byte) 0xDD;
        // 这个写死 0x01
        hex[1] = (byte) 0x01;

        // 计算校验和
        int checkNum = 0;
        for (byte datum : data) {
            checkNum += (datum & 0xFF);
        }
        hex[2] = (byte) ((checkNum & 0xFF00) >> 8);
        hex[3] = (byte) ((checkNum & 0xFF));

        // payloads 长度
        hex[4] = (byte) ((data.length & 0xFF00) >> 8);
        hex[5] = (byte) ((data.length & 0xFF));

        // 数据赋值
        System.arraycopy(data, 0, hex, 6, data.length);


        if (mOnCallback != null) {
            mOnCallback.onSend(hex);
        }
        // 透传发送
        if (isWrite) {
            Log.i(TAG, "发送：" + hex.length + "：[" + BleStrUtils.byte2HexStr(hex) + "]");
            SendDataBean sendDataBean = new SendDataBean(hex, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
            sendData(sendDataBean);
        } else {
            Log.i(TAG, "发送：" + hex.length + "：[" + BleStrUtils.byte2HexStr(hex) + "]");
            SendDataBean sendDataBean = new SendDataBean(hex, BleConfig.UUID_NOTIFY_AILINK, BleConfig.READ_DATA, BleConfig.UUID_SERVER_AILINK);
            sendData(sendDataBean);
        }
    }

    public interface OnCallback {
        default void onReceive(byte[] hex) {
        }

        default void onSend(byte[] hex) {
        }

        default void onCallbackSetSystemTime(int status) {
        }

        default void onCallbackSystemTime(int year, int month, int day, int hour, int minute, int second) {
        }

        default void onCallbackSetSystemTimeZone(int status) {
        }

        default void onCallbackSystemTimeZone(long ms) {
        }

        default void onCallbackSystemBattery(int battery) {
        }

        default void onCallbackSystemVersion(String version) {
        }

        default void onCallbackSystemMac(String mac) {
        }

        default void onCallbackSetUser(int status) {
        }

        default void onCallbackUser(int metric, int sex, int age, float height, float weight) {
        }

        default void onCallbackSetStepTarget(int status) {
        }

        default void onCallbackStepTarget(int stepTarget) {
        }

        default void onCallbackSetBackLight(int status) {
        }

        default void onCallbackBackLight(int value) {
        }

        default void onCallbackSetSedentary(int status) {
        }

        default void onCallbackSedentary(boolean isOpen, int[] dayFlag, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        }

        default void onCallbackSetDisturb(int status) {
        }

        default void onCallbackDisturb(boolean allOpen, List<LeapWatchDisturbBean> list) {
        }

        default void onCallbackSetShake(int status) {
        }

        default void onCallbackShake(int value) {
        }

        default void onCallbackSetHandLight(int status) {
        }

        default void onCallbackHandLight(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        }

        default void onCallbackSetSleep(int status) {
        }

        default void onCallbackSleep(int value) {
        }

        default void onCallbackSetHourSystem(int status) {
        }

        default void onCallbackHourSystem(boolean is24) {
        }

        default void onCallbackSetLanguage(int status) {
        }

        default void onCallbackLanguage(int value) {
        }

        default void onCallbackAddAlarm(int status) {
        }

        default void onCallbackAlarm(int value) {
        }

        default void onCallbackSetMetric(int status) {
        }

        default void onCallbackMetric(int value) {
        }

        default void onCallbackSetFindPhone(int status) {
        }

        default void onCallbackFindPhone(int value) {
        }

        default void onCallbackSetMessagePush(int status) {
        }

        default void onCallbackSetAntiLost(int status) {
        }

        default void onCallbackSetHeart(int status) {
        }

        default void onCallbackHeart(boolean isOpen, int[] dayFlag, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        }

        // TODO 绑定

        // TODO 登录

        // TODO 配对

        default void onCallbackPushMessage(int status) {
        }

        // TODO 推送音乐

        default void onCallbackPushWeatherNow(int status) {
        }

        default void onCallbackPushWeatherFeature(int status) {
        }

        // TODO 心率

        default void onCallbackCameraControl(int status) {
        }

        // TODO 表盘文件传输
    }

    private OnCallback mOnCallback;

    public void setOnCallback(OnCallback onCallback) {
        mOnCallback = onCallback;
    }
}