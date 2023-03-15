package cn.net.aicare.modulelibrary.module.LeapWatch;

import android.util.Log;

import com.pingwang.bluetoothlib.device.BleDevice;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchBleAlarmBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchBleWeatherBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchBloodOxygenDataBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchBloodPressureDataBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchDailyDataBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchHeartDataBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchNoDisturbBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchSleepDataBean;
import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchSportDataBean;


public class AICareWatchData extends BaseWatchData {

    private static final String TAG = "Tag1";
    private static AICareWatchData mLeapWatchData;
    private String mMac;

    public static AICareWatchData getInstance() {
        return mLeapWatchData;
    }

    public static AICareWatchData init(BleDevice bleDevice) {
        mLeapWatchData = new AICareWatchData(bleDevice);
        return mLeapWatchData;
    }


    private AICareWatchData(BleDevice bleDevice) {
        super(bleDevice);
        if (bleDevice != null) {
            mMac = bleDevice.getMac();
        }
    }

    public String getMac() {
        return mMac;
    }

    @Override
    public void onNotifyWatchData(byte[] hex) {
        // 透传数据从这里回
        if (mOnCallbackList != null) {
//            Log.i(TAG, "接收：" + hex.length + "：[" + BleStrUtils.byte2HexStr(hex) + "]");
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onReceive(hex);
            }
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

    @Override
    public void onWatchTLVData(TLVDataBean bean) {

    }

    // ================================================================================================================================


    /**
     * 获取TLV中的V数据
     *
     * @param hex
     * @return
     */
    private byte[] getValueData(byte[] hex) {
        int length = ((hex[3] & 0xFF) << 8) + (hex[4] & 0xFF);
        byte[] value = new byte[length];
        System.arraycopy(hex, 5, value, 0, value.length);
        return value;
    }

    /**
     * 解析控制命令
     *
     * @param hex 0x01
     */
    private void decodeControlData(byte[] hex) {

        int typeStatus = hex[2];
        byte[] value = getValueData(hex);


        switch (hex[1]) {
            case 0x01:
            case 0x02:
                callbackSystemTime(typeStatus, value);
//                break;
//                callbackSystemTimeZone(hex);
                break;
            case 0x03:
                callbackSystemBattery(typeStatus, value);
                break;
            case 0x04:
                callbackSystemVersion(typeStatus, value);
                break;
            case 0x05:
                callbackSystemMac(typeStatus, value);
                break;
            case 0x06:
                callbackUser(typeStatus, value);
                break;
            case 0x07:
                callbackStepTarget(typeStatus, value);
                break;
            case 0x08:
                callbackBackLight(typeStatus, value);
                break;
            case 0x09:
                callbackSedentary(typeStatus, value);
                break;
            case 0x0A:
                callbackDisturb(typeStatus, value);
                break;
            case 0x0B:
                callbackShake(typeStatus, value);
                break;
            case 0x0C:
                callbackHandLight(typeStatus, value);
                break;
            case 0x0D:
//                callbackSleep(typeStatus, value);
                break;
            case 0x0E:
                callbackHourSystem(typeStatus, value);
                break;
            case 0x0F:
                callbackLanguage(typeStatus, value);
                break;
            case 0x10:
                callbackAlarm(typeStatus, value);
                break;
            case 0x11:
                callbackMetric(typeStatus, value);
                break;
            case 0x12:
                //天气温度单位切换
                callbackWeatherUnit(typeStatus, value);
                break;

            case 0x13:
                callbackFindPhone(typeStatus, value);
                break;
            case 0x14:
                callbackSetMessagePush(typeStatus, value);
                break;
            case 0x15:
                callbackSetAntiLost(typeStatus, value);
                break;
            case 0x16:
                callbackSetHeart(typeStatus, value);
                break;

            case 0x17:
                //左右手佩戴设置:0x0117
                callbackWear(typeStatus, value);
                break;

            case 0x18:
                //喝水提醒设置:0x0118
                callbackWater(typeStatus, value);
                break;


            case 0x19:
                //找手表功能:0x0119

                break;
            case 0x1A:
                //主题设置:0x011A

                break;

            case 0x1B:
                //恢复出厂设置:0x011B

                break;

            case 0x1C:
                //手表挂断电话:0x011C

                break;
            case 0x1D:
                //NFC开关设置:0x011D

                break;
            case 0x1E:
                //卡类型:0x011E

                break;
            case 0x1F:
                //卡信息:0x011F

                break;
            case 0x20:
                //SOS联系人设置:0x0120

                break;

        }
    }

    /**
     * 解析绑定命令
     *
     * @param hex 0x02
     */
    private void decodeBindData(byte[] hex) {
        int typeStatus = hex[2];
        byte[] value = getValueData(hex);

        switch (hex[1]) {
            case 0x01:
                callbackBind(typeStatus, value);
                break;
            case 0x02:
                callbackLogin(typeStatus, value);
                break;
            case 0x03:
                callbackPair(typeStatus, value);
                break;
        }
    }

    /**
     * 解析提醒命令
     *
     * @param hex 0x03
     */
    private void decodeRemindData(byte[] hex) {
        int typeStatus = hex[2];
        byte[] value = getValueData(hex);


        switch (hex[1]) {
            case 0x01:
                callbackPushMessage(typeStatus, value);
                break;

            case 0x02:
                // 0x02推送音乐未做

                break;
            case 0x03:
                callbackPushWeatherNow(typeStatus, value);
                break;
            case 0x04:
                callbackPushWeatherFeature(typeStatus, value);
                break;
        }
    }

    /**
     * 解析数据传输命令
     *
     * @param hex 0x04
     */
    private void decodeDataTransData(byte[] hex) {

        int typeStatus = hex[2];
        byte[] value = getValueData(hex);


        switch (hex[1]) {
            case 0x01:
                callbackHeartData(typeStatus, value);
                break;
            case 0x02:
                callbackBloodPressureData(typeStatus, value);
                break;
            case 0x03:
                callbackBloodOxygenData(typeStatus, value);
                break;
            case 0x04:
                callbackDailyData(typeStatus, value);
                break;
            case 0x05:
                callbackSleepData(typeStatus, value);
                break;
            case 0x06:
                callbackSportData(typeStatus, value);
                break;
        }
    }

    /**
     * 解析系统控制命令
     *
     * @param hex 0x05
     */
    private void decodeSystemControlData(byte[] hex) {
        int typeStatus = hex[2];
        byte[] value = getValueData(hex);

        switch (hex[1]) {
            case 0x01:
                callbackCameraControl(typeStatus, value);
                break;
            case 0x02:
                callbackSosControl(typeStatus, value);
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
     * @param value 0x01
     */
    private void callbackSystemTime(int typeStatus, byte[] value) {
        int timestamp = ((value[0] & 0xFF) << 24) | ((value[1] & 0xFF) << 16) | ((value[2] & 0xFF) << 8) | ((value[3] & 0xFF));
        int week = value[4] & 0xFF;
        int zone = ((value[5] & 0xFF) >> 7) & 0x01;
        int timeZone = (value[5] & 0x7F);
        if (zone == 1) {
            timeZone = -timeZone;
        }
        int status = -1;
        if (typeStatus == 0x00) {
            //写
            status = value[value.length - 1];
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSystemTime(status, timestamp, week, timeZone);
            }
        }
    }


    /**
     * 解析电量
     *
     * @param value 0x03
     */
    private void callbackSystemBattery(int typeStatus, byte[] value) {
        int battery = value[0] & 0xFF;
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSystemBattery(battery);
            }
        }
    }

    /**
     * 解析系统版本
     *
     * @param typeStatus
     * @param hex        0x04
     */
    private void callbackSystemVersion(int typeStatus, byte[] hex) {
        String version = "H" + ((hex[0] & 0xFF) + (hex[1] & 0xFF) * 0.1) + "V" + ((hex[2] & 0xFF) + (hex[3] & 0xFF) * 0.1 + (hex[4] & 0xFF) * 0.01);
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSystemVersion(version);
            }
        }
    }

    /**
     * 解析系统Mac
     *
     * @param hex 0x05
     */
    private void callbackSystemMac(int typeStatus, byte[] hex) {
        //00:00:00:00:00:00
        StringBuilder mac = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            mac.append(Integer.toHexString(hex[i]));
            if (i < 5) {
                mac.append(":");
            }
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSystemMac(mac.toString());
            }
        }
    }

    /**
     * 解析用户信息
     *
     * @param hex 0x06
     */
    private void callbackUser(int typeStatus, byte[] hex) {

        int metric = hex[0];
        int sex = hex[1];
        int age = hex[2];

        int height05 = 0;
        for (int i = 0; i < 4; i++) {
            height05 += (hex[i + 3] << ((3 - i) * 8));
        }
        float height = getPreFloat(height05 * 0.5f, 1);

        int weight05 = 0;
        for (int i = 0; i < 4; i++) {
            weight05 += (hex[i + 7] << ((3 - i) * 8));
        }
        float weight = getPreFloat(weight05 * 0.5f, 1);
        int status = -1;

        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackUser(status, metric, sex, age, height, weight);
            }
        }
    }

    /**
     * 解析步数目标
     *
     * @param hex 0x07
     */
    private void callbackStepTarget(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int target = 0;
        for (int i = 0; i >= 0; i--) {
            target += ((hex[i] & 0xFF) << (i * 8));
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackStepTarget(status, target);
            }
        }
    }

    /**
     * 解析背光设置
     *
     * @param hex 0x08 TLV
     */
    private void callbackBackLight(int typeStatus, byte[] hex) {
        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int value = (hex[0] & 0xFF);
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackBackLight(status, value);
            }
        }
    }

    /**
     * 解析久坐提醒
     *
     * @param hex 0x09
     */
    private void callbackSedentary(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        boolean isOpen = (hex[0] & 0xFF) == 1;
        int startHour = hex[1] & 0xFF;
        int endHour = hex[2] & 0xFF;
        int startMinute = hex[3] & 0xFF;
        int endMinute = hex[4] & 0xFF;
        int interval = hex[5] & 0xFF;

        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSedentary(status, isOpen, startHour, startMinute, endHour, endMinute, interval);
            }
        }
    }

    /**
     * 解析勿扰模式
     *
     * @param hex 0x0A
     */
    private void callbackDisturb(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }
        boolean allOpen = (hex[0] == 1);
        List<WatchNoDisturbBean> list = new ArrayList<>();
        int count = (hex.length - 1) / 5;
        for (int i = 0; i < count; i++) {
            boolean isOpen = ((hex[i * 5 + 1] & 0xFF) == 1);
            int startHour = hex[i * 5 + 2] & 0xFF;
            int endHour = hex[i * 5 + 3] & 0xFF;
            int startMinute = hex[i * 5 + 4] & 0xFF;
            int endMinute = hex[i * 5 + 5] & 0xFF;
            list.add(new WatchNoDisturbBean(isOpen, startHour, startMinute, endHour, endMinute));
        }

        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackDisturb(status, allOpen, list);
            }
        }
    }

    /**
     * 解析振动设置
     *
     * @param hex 0x0B
     */
    private void callbackShake(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int value = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackShake(status, value);
            }
        }
    }

    /**
     * 解析抬手亮
     *
     * @param hex 0x0C
     */
    private void callbackHandLight(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        boolean isOpen = (hex[0] & 0xFF) == 1;
        int startHour = hex[1];
        int startMinute = hex[2];
        int endHour = hex[3];
        int endMinute = hex[4];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackHandLight(status, isOpen, startHour, endHour, startMinute, endMinute);
            }
        }
    }



    /**
     * 解析小时制
     *
     * @param hex 0x0E
     */
    private void callbackHourSystem(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        boolean is24 = (hex[0] & 0xFF) == 0x00;
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackHourSystem(status, is24);
            }
        }
    }

    /**
     * 解析语言
     *
     * @param hex 0x0F
     */
    private void callbackLanguage(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int value = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackLanguage(status, value);
            }
        }
    }

    /**
     * 解析闹钟
     *
     * @param value 0x10
     */
    private void callbackAlarm(int typeStatus, byte[] value) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = value[value.length - 1];
        }

        int alarmId = (value[0] & 0xFF);
        if (typeStatus == 0x00 || typeStatus == 0x20) {
            //修改和新增闹钟
            int[] repeat = new int[7];
            byte repeatB = value[1];
            for (int i = 0; i < 7; i++) {
                //i-0=周1
                repeat[i] = ((repeatB >> i) & 0x01);
            }
            boolean isOpen = ((repeatB >> 7) & 0x01) == 1;
            int hour = (value[2] & 0xFF);
            int min = (value[3] & 0xFF);
            byte[] nameB = new byte[value.length - 7];
            System.arraycopy(value, 7, nameB, 0, nameB.length);
            String name = String.valueOf(nameB);
            WatchBleAlarmBean watchAlarmBean = new WatchBleAlarmBean(alarmId, isOpen, repeat, hour, min, name);
            if (mOnCallbackList != null) {
                for (OnCallback onCallback : mOnCallbackList) {
                    onCallback.onCallbackAlarm(status, watchAlarmBean);
                }
            }
        } else if (typeStatus == 0x30) {
            //删除
            if (mOnCallbackList != null) {
                for (OnCallback onCallback : mOnCallbackList) {
                    onCallback.onCallbackAlarmDelete(status, alarmId);
                }
            }
        } else if (typeStatus == 0x10) {
            //读取闹钟列表
            List<WatchBleAlarmBean> list = new ArrayList<>();
            int valueLength = value.length;//总长度
            int alarmByteEndIndex = 0;
            int alarmByteStartIndex = 0;
            while (alarmByteEndIndex < valueLength) {
                alarmByteEndIndex = alarmInfoParse(value, alarmByteStartIndex, list);
                alarmByteStartIndex = alarmByteEndIndex + 1;
            }
            if (mOnCallbackList != null) {
                for (OnCallback onCallback : mOnCallbackList) {
                    onCallback.onCallbackAlarmList(list);
                }
            }
        }


    }


    /**
     * 闹钟解析
     *
     * @param value      byte[]
     * @param startIndex byte下标起点
     * @param list       列表
     * @return 当前结束的下标位置
     */
    private int alarmInfoParse(byte[] value, int startIndex, List<WatchBleAlarmBean> list) {
        int alarmId = (value[startIndex] & 0xFF);
        int[] repeat = new int[7];
        byte repeatB = value[startIndex + 1];
        for (int i = 0; i < 7; i++) {
            //i-0=周1
            repeat[i] = ((repeatB >> i) & 0x01);
        }
        boolean isOpen = ((repeatB >> 7) & 0x01) == 1;
        int hour = (value[startIndex + 2] & 0xFF);
        int min = (value[startIndex + 3] & 0xFF);
        int nameLength = (value[startIndex + 7] & 0xFF);//名称长度
        byte[] nameB = new byte[nameLength];
        System.arraycopy(value, startIndex + 8, nameB, 0, nameB.length);
        String name = String.valueOf(nameB);
        int alarmInfoLength = startIndex + 8 + nameLength;//第一个闹钟使用的数据长度
        WatchBleAlarmBean watchAlarmBean = new WatchBleAlarmBean(alarmId, isOpen, repeat, hour, min, name);
        list.add(watchAlarmBean);
        return alarmInfoLength;
    }


    /**
     * 解析公英制
     *
     * @param hex 0x11
     */
    private void callbackMetric(int typeStatus, byte[] hex) {
        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int value = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackMetric(status, value);
            }
        }
    }

    /**
     * 解析天气单位
     *
     * @param hex 0x12
     */
    private void callbackWeatherUnit(int typeStatus, byte[] hex) {
        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int value = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackWeatherUnit(status, value);
            }
        }
    }

    /**
     * 解析找手机
     *
     * @param hex 0x13
     */
    private void callbackFindPhone(int typeStatus, byte[] hex) {
        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int value = hex[0];
        appCallbackFindPhone(value);
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackFindPhone(value);
            }
        }

    }

    /**
     * 解析信息提醒
     *
     * @param hex 0x14
     */
    private void callbackSetMessagePush(int typeStatus, byte[] hex) {
        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        int appList = ((hex[0] & 0xFF) << 24) | ((hex[1] & 0xFF) << 16) | ((hex[2] & 0xFF) << 8) | ((hex[3] & 0xFF));

        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSetMessagePush(status, appList);
            }
        }
    }

    /**
     * 解析防丢设置
     *
     * @param hex 0x15
     */
    private void callbackSetAntiLost(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }


        int value = hex[0] & 0xFF;
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSetAntiLost(status, value);
            }
        }
    }

    /**
     * 解析设置定时心率
     *
     * @param hex 0x16
     */
    private void callbackSetHeart(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        boolean isOpen = (hex[0] & 0xFF) == 1;
        int startHour = hex[1] & 0xFF;
        int startMinute = hex[2] & 0xFF;
        int endHour = hex[3] & 0xFF;
        int endMinute = hex[4] & 0xFF;
        int interval = hex[5] & 0xFF;

        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackHeart(status, isOpen, startHour, startMinute, endHour, endMinute, interval);
            }
        }
    }


    /**
     * 解析左右手佩戴
     *
     * @param hex 0x17
     */
    private void callbackWear(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        boolean isRightHand = (hex[0] & 0xFF) == 1;

        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackWear(status, isRightHand);
            }
        }
    }

    /**
     * 解析喝水提醒
     *
     * @param hex 0x18
     */
    private void callbackWater(int typeStatus, byte[] hex) {

        int status = -1;
        if (typeStatus == 0x00) {
            status = hex[hex.length - 1];
        }

        boolean isOpen = (hex[0] & 0xFF) == 1;
        int startHour=(hex[1] & 0xFF);
        int startMinute=(hex[2] & 0xFF);
        int endHour=(hex[3] & 0xFF);
        int endMinute=(hex[4] & 0xFF);
        int interval=(hex[5] & 0xFF);

        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackWater(status, isOpen,startHour,startMinute,endHour,endMinute,interval);
            }
        }
    }


    /**
     * 解析绑定
     *
     * @param hex 0x01
     */
    private void callbackBind(int typeStatus, byte[] hex) {

        if (typeStatus == 0x20) {
            //新增

        } else if (typeStatus == 0x00) {
            //手表主动上报
            boolean bindStatus = (hex[0] & 0xFF) == 0x00;//是否确认绑定
            int bindUserId = ((hex[1] & 0xFF) << 24) | ((hex[2] & 0xFF) << 16) | ((hex[3] & 0xFF) << 8) | ((hex[4] & 0xFF));
            int length = hex.length;
            byte[] dataList = new byte[length - 6 - 1 - 4 - 1];
            System.arraycopy(hex, 5, dataList, 0, dataList.length);

        }

        int status = hex[hex.length - 1];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackBind(status);
            }
        }
    }

    /**
     * 解析登录
     *
     * @param hex 0x02
     */
    private void callbackLogin(int typeStatus, byte[] hex) {
        int status = hex[hex.length - 1];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackLogin(status);
            }
        }
    }

    /**
     * 解析配对
     *
     * @param hex 0x03
     */
    private void callbackPair(int typeStatus, byte[] hex) {
        int status = hex[hex.length - 1];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackPair(status);
            }
        }
    }

    /**
     * 解析推送消息
     *
     * @param hex 0x01
     */
    private void callbackPushMessage(int typeStatus, byte[] hex) {
        int status = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackPushMessage(status);
            }
        }
    }

    /**
     * 解析推送实时天气
     *
     * @param hex 0x03
     */
    private void callbackPushWeatherNow(int typeStatus, byte[] hex) {
        int status = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackPushWeatherNow(status);
            }
        }
    }

    /**
     * 解析推送预报天气
     *
     * @param hex 0x04
     */
    private void callbackPushWeatherFeature(int typeStatus, byte[] hex) {
        int status = hex[0];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackPushWeatherFeature(status);
            }
        }
    }

    /**
     * 解析心率数据
     *
     * @param hex 0x01
     */
    private void callbackHeartData(int typeStatus, byte[] hex) {
        int size = 6;
        int count = (hex.length) / size;
        if (typeStatus==0x30){
            //删除数据成功
            return;
        }
        List<WatchHeartDataBean> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int timestamp = ((hex[i * size + 0] & 0xFF) << 24) | ((hex[i * size + 1] & 0xFF) << 16) | ((hex[i * size + 2] & 0xFF) << 8) | ((hex[i * size + 3] & 0xFF));
            int data = hex[i * size + 4] & 0xFF;
            int reserve = hex[i * size + 5] & 0xFF;
            list.add(new WatchHeartDataBean(timestamp, data));
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackHeartData(list);
            }
        }
    }

    /**
     * 解析血压数据
     *
     * @param hex 0x02
     */
    private void callbackBloodPressureData(int typeStatus, byte[] hex) {
        int size = 10;
        int count = (hex.length) / size;
        if (typeStatus==0x30){
            //删除数据成功
            return;
        }
        List<WatchBloodPressureDataBean> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int timestamp = ((hex[i * size + 0] & 0xFF) << 24) | ((hex[i * size + 1] & 0xFF) << 16) | ((hex[i * size + 2] & 0xFF) << 8) | ((hex[i * size + 3] & 0xFF));
            int high = (hex[i * size + 4] & 0xFF) << 8 | (hex[i * size + 5] & 0xFF);
            int low = (hex[i * size + 6] & 0xFF) << 8 | (hex[i * size + 7] & 0xFF);
            int unit = (hex[i * size + 8] & 0xFF);
            int decimal = (hex[i * size + 9] & 0xFF);
            list.add(new WatchBloodPressureDataBean(timestamp, high, low, unit, decimal));
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackBloodPressureData(list);
            }
        }
    }

    /**
     * 解析血氧数据
     *
     * @param hex 0x03
     */
    private void callbackBloodOxygenData(int typeStatus, byte[] hex) {
        int size = 6;
        int length = (hex.length) / size;
        if (typeStatus==0x30){
            //删除数据成功
            return;
        }
        List<WatchBloodOxygenDataBean> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int timestamp = ((hex[i * size + 0] & 0xFF) << 24) | ((hex[i * size + 1] & 0xFF) << 16) | ((hex[i * size + 2] & 0xFF) << 8) | ((hex[i * size + 3] & 0xFF));
            int bloodOxygen = hex[i * size + 4] & 0xFF;
            int reserve = hex[i * size + 5] & 0xFF;
            list.add(new WatchBloodOxygenDataBean(timestamp, bloodOxygen));
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackBloodOxygenData(list);
            }
        }
    }

    /**
     * 解析每日数据
     *
     * @param hex 0x04
     */
    private void callbackDailyData(int typeStatus, byte[] hex) {
        int size = 16;
        int length = (hex.length) / size;
        if (typeStatus==0x30){
            //删除数据成功
            return;
        }
        List<WatchDailyDataBean> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int timestamp = ((hex[i * size + 0] & 0xFF) << 24) | ((hex[i * size + 1] & 0xFF) << 16) | ((hex[i * size + 2] & 0xFF) << 8) | ((hex[i * size + 3] & 0xFF));
            int step = ((hex[i * size + 4] & 0xFF) << 24) | ((hex[i * size + 5] & 0xFF) << 16) | ((hex[i * size + 6] & 0xFF) << 8) | ((hex[i * size + 7] & 0xFF));
            int distance = ((hex[i * size + 8] & 0xFF) << 24) | ((hex[i * size + 9] & 0xFF) << 16) | ((hex[i * size + 10] & 0xFF) << 8) | ((hex[i * size + 11] & 0xFF));
            int cal = ((hex[i * size + 12] & 0xFF) << 24) | ((hex[i * size + 13] & 0xFF) << 16) | ((hex[i * size + 14] & 0xFF) << 8) | ((hex[i * size + 15] & 0xFF));
            list.add(new WatchDailyDataBean(timestamp, step, distance, cal));
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackDailyData(list);
            }
        }
    }

    /**
     * 解析睡眠数据
     *
     * @param hex 0x05
     */
    private void callbackSleepData(int typeStatus, byte[] hex) {
        int size = 5;
        int length = (hex.length) / size;
        if (typeStatus==0x30){
            //删除数据成功
            return;
        }
        List<WatchSleepDataBean> dataList = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            int timestamp = ((hex[i * size + 0] & 0xFF) << 24) | ((hex[i * size + 1] & 0xFF) << 16) | ((hex[i * size + 2] & 0xFF) << 8) | ((hex[i * size + 3] & 0xFF));
            int status = hex[i * size + 4] & 0xFF;
            dataList.add(new WatchSleepDataBean(timestamp, status));
        }
        Collections.sort(dataList);//按时间戳排序
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSleepData(dataList);
            }
        }
    }

    /**
     * 解析运动数据
     *
     * @param hex 0x06
     */
    private void callbackSportData(int typeStatus, byte[] hex) {
        int size = 16;
        int length = (hex.length) / size;
        if (typeStatus==0x30){
            //删除数据成功
            return;
        }
        List<WatchSportDataBean> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {

            int timestamp = ((hex[i * size + 0] & 0xFF) << 24) | ((hex[i * size + 1] & 0xFF) << 16) | ((hex[i * size + 2] & 0xFF) << 8) | ((hex[i * size + 3] & 0xFF));
            int workStatus = hex[i * size + 4] & 0x07;
            int workType = (hex[i * size + 4] & 0xFF) >> 3;

            int step = ((hex[i * size + 5] & 0xFF) << 8) | ((hex[i * size + 6] & 0xFF) << 16);
            int cal = ((hex[i * size + 7] & 0xFF) << 24) | ((hex[i * size + 8] & 0xFF) << 16) | ((hex[i * size + 9] & 0xFF) << 8) | ((hex[i * size + 10] & 0xFF));
            int distance = ((hex[i * size + 11] & 0xFF) << 24) | ((hex[i * size + 12] & 0xFF) << 16) | ((hex[i * size + 13] & 0xFF) << 8) | ((hex[i * size + 14] & 0xFF));
            int heartRateAll = ((hex[i * size + 15] & 0xFF) << 24) | ((hex[i * size + 16] & 0xFF) << 16) | ((hex[i * size + 17] & 0xFF) << 8) | ((hex[i * size + 18] & 0xFF));
            int sportsTime = ((hex[i * size + 19] & 0xFF) << 24) | ((hex[i * size + 20] & 0xFF) << 16) | ((hex[i * size + 21] & 0xFF) << 8) | ((hex[i * size + 22] & 0xFF));
            list.add(new WatchSportDataBean(timestamp, workStatus, workType, step, cal, distance, heartRateAll, sportsTime));
        }
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSportData(list);
            }
        }
    }

    /**
     * 解析拍照控制
     *
     * @param hex 0x01
     */
    private void callbackCameraControl(int typeStatus, byte[] hex) {
        int status = hex[3];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackCameraControl(status);
            }
        }
    }

    /**
     * 解析SOS指令
     *
     * @param hex 0x02
     */
    private void callbackSosControl(int typeStatus, byte[] hex) {
        int status = hex[3];
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onCallbackSos(status);
            }
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(stamp));
        int week = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (week < 0) {
            week = 0x06;
        }
        int rawOffset = cal.get(Calendar.ZONE_OFFSET);
        int minute15 = rawOffset / 1000 / 60 / 15;
        int second = (int) (stamp / 1000);
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (second >> 24);
        hex[4] = (byte) (second >> 16);
        hex[5] = (byte) (second >> 8);
        hex[6] = (byte) second;
        hex[7] = (byte) week;
        byte rawOffsetB = 0;
        if (minute15 < 0) {
            rawOffsetB |= 1 << 7;
        }
        rawOffsetB |= minute15;
        hex[8] = (byte) rawOffsetB;
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
     * APP读取系统版本号
     */
    public void appGetSystemVersion() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x04;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }


    /**
     * APP读取设备MAC地址
     */
    public void appGetSystemMac() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x05;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }


    /**
     * APP设置用户信息
     *
     * @param sex    0：女 1：男
     * @param age    0到100
     * @param height 单位0.5cm  0.0CM到512CM
     * @param weight 单位0.5kg   0.0KG到512KG
     */
    public void appSetUser(int sex, int age, int height, int weight) {
        byte[] hex = new byte[14];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x06;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) 0x00;//手机系统:0-安卓 ;1-IOS
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
     * APP设置目标步数；大端序
     */
    public void appSetStepTarget(int target) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x07;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) ((target >> 24) & 0xFF);
        hex[4] = (byte) ((target >> 16) & 0xFF);
        hex[5] = (byte) ((target >> 8) & 0xFF);
        hex[6] = (byte) ((target & 0xFF));
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
     * @param time 时间,0代表常亮
     */
    public void appSetBackLight(int time) {
        byte[] hex = new byte[5];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x08;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (time);
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
     * @param startHour   0~23
     * @param startMinute 0~59
     * @param endHour     0~23
     * @param endMinute   0~59
     * @param interval    分钟
     */
    public void appSetSedentary(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x09;
        hex[2] = (byte) 0x00;

        hex[3] = (byte) (isOpen ? 1 : 0);
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
    public void appSetDisturb(boolean allOpen, List<WatchNoDisturbBean> list) {
        byte[] hex = new byte[4 + list.size() * 5];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x0A;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (allOpen ? 0x01 : 0x00);
        int start = 4;
        for (int i = 0; i < list.size(); i++) {
            WatchNoDisturbBean bean = list.get(i);
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
    public void appAddAlarm(WatchBleAlarmBean alarmBean) {
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
        hex[5] = (byte) alarmBean.getHour();
        hex[6] = (byte) alarmBean.getMinute();
        appWriteData(hex);
    }

    /**
     * APP删除闹钟
     */
    public void appDeleteAlarm(int alarmId) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x10;
        hex[2] = (byte) 0x30;
        hex[3] = (byte) alarmId;
        appReadData(hex);
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
     * APP设置天气单位
     *
     * @param unit 0x00摄氏度；0x01华氏度
     */
    public void appSetWeatherUnit(int unit) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x12;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) unit;
        appWriteData(hex);
    }

    /**
     * APP读取天气单位
     */
    public void appGetWeatherUnit() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x12;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * APP回复找手机
     */
    public void appCallbackFindPhone(int value) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x13;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) 0x00;
        hex[4] = (byte) 0x02;
        hex[5] = (byte) value;
        hex[6] = (byte) 0x00;
        appCallbackData(hex);
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
     * @param startHour   0~23
     * @param startMinute 0~59
     * @param endHour     0~23
     * @param endMinute   0~59
     * @param interval    分钟
     */
    public void appSetHeart(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x16;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 1 : 0);
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
     * APP设置佩戴方式
     *
     * @param isLeftHand 0-左手佩戴;1-右手佩戴
     */
    public void appSetWearingMethod(boolean isLeftHand) {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x17;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isLeftHand ? 0 : 1);
        appWriteData(hex);
    }


    /**
     * APP设置喝水提醒
     *
     * @param isOpen      总开关
     * @param startHour   0~23
     * @param startMinute 0~59
     * @param endHour     0~23
     * @param endMinute   0~59
     * @param interval    分钟
     */
    public void appSetDrinkWater(boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x18;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 1 : 0);
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
    public void appGetDrinkWater() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x18;
        hex[2] = (byte) 0x10;
        appReadData(hex);
    }

    /**
     * 找手表功能
     *
     * @param isOpen 总开关
     */
    public void appSetFindWatch(boolean isOpen) {
        byte[] hex = new byte[9];
        hex[0] = (byte) 0x01;
        hex[1] = (byte) 0x18;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) (isOpen ? 1 : 0);
        appWriteData(hex);
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
     * @param stamp      消息产生的时间戳
     * @param categoryID categoryID
     * @param packName   包名
     * @param title      标题
     * @param message    内容
     */
    public void appPushMessage(long stamp, int categoryID, String packName, String title, String message) {
        try {

            byte[] packNameByte = packName.getBytes(Charset.forName("UTF-8"));
            byte[] titleByte = title.getBytes(Charset.forName("UTF-8"));
            byte[] messageByte = message.getBytes(Charset.forName("UTF-8"));

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
            hex[3] = (byte) categoryID;
            hex[4] = (byte) (year - 2000);
            hex[5] = (byte) month;
            hex[6] = (byte) day;
            hex[7] = (byte) hour;
            hex[8] = (byte) minute;
            hex[9] = (byte) second;

            System.arraycopy(packNameByte, 0, hex, 10, (Math.min(packNameByte.length, 32)));
            System.arraycopy(titleByte, 0, hex, 42, (Math.min(titleByte.length, 32)));
            System.arraycopy(messageByte, 0, hex, 74, (Math.min(messageByte.length, 250)));

            appWriteData(hex);
        } catch (Exception e) {
            Log.e(TAG, "推送消息失败：" + e.toString());
        }
    }


    /**
     * APP推送天气给手表
     *
     * @param list List<WatchBleWeatherBean> 1条数据代表当天
     */
    public void appPushWeather(List<WatchBleWeatherBean> list) {
        int weatherSize = list.size();
        boolean isNow = weatherSize <= 1;//实时天气
        int weatherSizeAll = weatherSize * 0x000E;
        byte[] weatherSizeAllB = new byte[weatherSizeAll];
        int startIndex = 0;
        for (WatchBleWeatherBean watchBleWeatherBean : list) {
            byte[] weatherByte = getWeatherByte(watchBleWeatherBean);
            System.arraycopy(weatherByte, 0, weatherSizeAllB, startIndex, weatherByte.length);
        }

        byte[] hex = new byte[3 + weatherSizeAll];
        hex[0] = (byte) 0x03;
        hex[1] = (byte) (isNow ? 0x03 : 0x04);
        hex[2] = (byte) 0x00;
        System.arraycopy(weatherSizeAllB, 0, hex, 3, weatherSizeAll);
        appWriteData(hex);
    }

    /**
     * 装载天气
     *
     * @param watchBleWeatherBean WatchBleWeatherBean
     * @return byte[]
     */
    private byte[] getWeatherByte(WatchBleWeatherBean watchBleWeatherBean) {
        byte[] weatherB = new byte[14];
        int timestamp = watchBleWeatherBean.getTimestamp();
        weatherB[0] = (byte) (timestamp >> 24);
        weatherB[1] = (byte) (timestamp >> 16);
        weatherB[2] = (byte) (timestamp >> 8);
        weatherB[3] = (byte) (timestamp);
        weatherB[4] = (byte) watchBleWeatherBean.getTemp();
        weatherB[5] = (byte) watchBleWeatherBean.getTempHigh();
        weatherB[6] = (byte) watchBleWeatherBean.getTempLow();
        weatherB[7] = (byte) watchBleWeatherBean.getWeatherCode();
        weatherB[8] = (byte) watchBleWeatherBean.getWindSpd();
        weatherB[9] = (byte) watchBleWeatherBean.getHum();
        weatherB[10] = (byte) watchBleWeatherBean.getVis();
        weatherB[11] = (byte) watchBleWeatherBean.getVis();
        weatherB[12] = (byte) (watchBleWeatherBean.getPcpn() >> 8);
        weatherB[13] = (byte) (watchBleWeatherBean.getPcpn());
        return weatherB;
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
     * APP读取血压数据
     */
    public void appGetBloodPressureData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除血压数据
     */
    public void appDeleteBloodPressureData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP读取血氧数据
     */
    public void appGetBloodOxygenData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x03;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除血氧数据
     */
    public void appDeleteBloodOxygenData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x03;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP读取每日数据
     */
    public void appGetDailyData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x04;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除每日数据
     */
    public void appDeleteDailyData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x04;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP读取睡眠数据
     */
    public void appGetSleepData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x05;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除睡眠数据
     */
    public void appDeleteSleepData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x05;
        hex[2] = (byte) 0x30;
        appWriteData(hex);
    }

    /**
     * APP读取运动数据
     */
    public void appGetSportData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x06;
        hex[2] = (byte) 0x10;
        appWriteData(hex);
    }

    /**
     * APP删除运动数据
     */
    public void appDeleteSportData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x04;
        hex[1] = (byte) 0x06;
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

    /**
     * APP设置SOS
     */
    public void appSetSos() {
        byte[] hex = new byte[4];
        hex[0] = (byte) 0x05;
        hex[1] = (byte) 0x02;
        hex[2] = (byte) 0x00;
        hex[3] = (byte) 0xEF;// 固定值
        appWriteData(hex);
    }


    public interface OnWeatherListener {
        /**
         * @param lowTemp    最低温
         * @param highTemp   最高温
         * @param curTemp    当前温度
         * @param statusCode 天气代码
         * @param status     天气
         */
        void onWeather(float lowTemp, float highTemp, float curTemp, int statusCode, String status);
    }

//    private WeatherHttpUtils mWeatherHttpUtils;

    /**
     * 同步天气
     *
     * @param cityName 城市名称
     */
    public void synchronizeTheWeather(String cityName, OnWeatherListener listener) {

//        if (mWeatherHttpUtils == null) {
//            mWeatherHttpUtils = new WeatherHttpUtils();
//        }
//        //当地时间(格式：YYYY-MM-DD)
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        String lang = LanguageUtils.getWebLanguage(0);
//        mWeatherHttpUtils.postGetForecastWeather(sdf.format(new Date()), cityName, lang, new OnHttpListenerIm() {
//            @Override
//            public <T> void onSuccess(T bean) {
//                super.onSuccess(bean);
//                if (bean instanceof WeatherForecastBean) {
//                    WeatherForecastBean data = (WeatherForecastBean) bean;
//                    if (data.getData() == null) {
//                        BleLog.e("获取天气数据异常");
//                        return;
//                    }
//                    String weatherData = data.getData().getWeatherData();
//                    WeatherForecastDataBean weather = new Gson().fromJson(weatherData, WeatherForecastDataBean.class);
//                    // 将天气设置到手环
//                    int lowTemp = Integer.parseInt(weather.getTmp_min());
//                    int highTemp = Integer.parseInt(weather.getTmp_max());
//                    int windSpd = Integer.parseInt(weather.getWind_spd());//风速
//                    int hum = Integer.parseInt(weather.getHum());//相对湿度
//                    int vis = Integer.parseInt(weather.getVis());//能见度，单位：公里
//                    int uv = Integer.parseInt(weather.getUv_index());//紫外线
//                    int pcpn = Integer.parseInt(weather.getPcpn());//雨量
//                    int curTemp = (lowTemp + highTemp) / 2;
//                    int code;
//                    SimpleDateFormat sdfHH = new SimpleDateFormat("HH", Locale.US);
//                    int curHour = Integer.parseInt(sdfHH.format(new Date()));
//                    if (curHour >= 6 && curHour < 18) {
//                        // 白天
//                        code = Integer.parseInt(weather.getCond_code_d());
//                    } else {
//                        // 夜晚
//                        code = Integer.parseInt(weather.getCond_code_n());
//                    }
//                    int reallyCode = WatchWeatherUtils.getWristbandWeatherCode(code);
//
//                    BleLog.i(String.format(Locale.US, "获取到天气：当前温度：%d，最低温度：%d，最高温度：%d，天气码：%d，天气：%s，城市名：%s", curTemp, lowTemp, highTemp, code, weather.getCond_txt_d(), cityName));
//                    if (listener != null) {
//                        if (curHour >= 6 && curHour < 18) {
//                            // 白天
//                            listener.onWeather(lowTemp, highTemp, curTemp, code, weather.getCond_txt_d());
//                        } else {
//                            // 夜晚
//                            listener.onWeather(lowTemp, highTemp, curTemp, code, weather.getCond_txt_n());
//                        }
//                    }
//                    SPWatch.getInstance().setTemperature(lowTemp + ";" + highTemp + ";" + curTemp);
//                    WatchBleWeatherBean watchBleWeatherBean = new WatchBleWeatherBean((int) (System.currentTimeMillis() / 1000), curTemp, highTemp, lowTemp, reallyCode, windSpd, hum, vis, uv, pcpn);
//                    List<WatchBleWeatherBean> list = new ArrayList<>();
//                    list.add(watchBleWeatherBean);
//                    appPushWeather(list);
//                }
//            }
//
//            @Override
//            public <T> void onFailed(T bean) {
//                super.onFailed(bean);
//                //获取天气失败
//                BleLog.e("获取天气失败");
//            }
//
//        });

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
        appSendDataToBle(data, false);
    }

    /**
     * 发送写
     */
    private void appWriteData(byte[] data) {
        appSendDataToBle(data, false);
    }


    /**
     * 发送回复
     */
    private void appCallbackData(byte[] data) {
        appSendDataToBle(data, true);
    }


    /**
     * @param data     发送的数据
     * @param callback 是否为回复
     */
    private void appSendDataToBle(byte[] data, boolean callback) {
        if (mOnCallbackList != null) {
            for (OnCallback onCallback : mOnCallbackList) {
                onCallback.onSend(data);
            }
        }


        sendDataToWatch(data, callback);
    }

    public interface OnCallback {
        default void onReceive(byte[] hex) {
        }

        default void onSend(byte[] hex) {
        }


        /**
         * @param status    -1代表返回读
         * @param timestamp 时间戳
         * @param week      星期
         * @param timeZone  时区
         */
        default void onCallbackSystemTime(int status, int timestamp, int week, int timeZone) {
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

        /**
         * 用户信息
         *
         * @param status -1代表返回读
         * @param metric
         * @param sex
         * @param age
         * @param height
         * @param weight
         */
        default void onCallbackUser(int status, int metric, int sex, int age, float height, float weight) {
        }


        /**
         * 目标
         *
         * @param status     -1代表返回读
         * @param stepTarget
         */
        default void onCallbackStepTarget(int status, int stepTarget) {
        }


        /**
         * 背光
         *
         * @param status -1代表返回读
         * @param value
         */
        default void onCallbackBackLight(int status, int value) {
        }


        /**
         * 久坐提醒
         * @param status      -1代表返回读
         * @param isOpen
         * @param startHour
         * @param startMinute
         * @param endHour
         * @param endMinute
         * @param interval
         */
        default void onCallbackSedentary(int status, boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        }


        /**
         * 勿扰
         * @param status  -1代表返回读
         * @param allOpen
         * @param list
         */
        default void onCallbackDisturb(int status, boolean allOpen, List<WatchNoDisturbBean> list) {
        }


        /**
         * 震动
         * @param status -1代表返回读
         * @param value
         */
        default void onCallbackShake(int status, int value) {
        }


        /**
         * 抬腕亮屏
         * @param status      -1代表返回读
         * @param isOpen
         * @param startHour
         * @param startMinute
         * @param endHour
         * @param endMinute
         */
        default void onCallbackHandLight(int status, boolean isOpen, int startHour, int startMinute, int endHour, int endMinute) {
        }






        /**
         * 小时制
         * @param status -1代表返回读
         * @param is24
         */
        default void onCallbackHourSystem(int status, boolean is24) {
        }


        /**
         * 语言
         * @param status -1代表返回读
         * @param value
         */
        default void onCallbackLanguage(int status, int value) {
        }

        /**
         * 修改闹钟/新增闹钟
         *
         * @param status         结果
         * @param watchAlarmBean
         */
        default void onCallbackAlarm(int status, WatchBleAlarmBean watchAlarmBean) {
        }

        /**
         * 删除闹钟
         *
         * @param status  00:设置成功
         *                01:设置失败
         * @param alarmId 0xFF:所有闹钟
         *                ID:获取指定ID的闹钟
         */
        default void onCallbackAlarmDelete(int status, int alarmId) {
        }

        /**
         * 读取闹钟
         *
         * @param list
         */
        default void onCallbackAlarmList(List<WatchBleAlarmBean> list) {
        }


        /**
         * @param status -1代表返回读
         * @param value  00:公制 ; 01:英制制
         */
        default void onCallbackMetric(int status, int value) {
        }

        /**
         * @param status -1代表返回读
         * @param value  00:摄氏度 ; 01:华氏度
         */
        default void onCallbackWeatherUnit(int status, int value) {
        }


        /**
         * @param value 00:关闭
         *              01:打开
         */
        default void onCallbackFindPhone(int value) {
        }

        /**
         * 信息提醒
         *
         * @param status  -1代表返回读
         * @param appList APP列表4Byte(0-关闭 1-打开)
         */
        default void onCallbackSetMessagePush(int status, int appList) {
        }

        /**
         * 防丢设置
         *
         * @param status -1代表返回读
         * @param value  00:关闭 ; 01:打开
         */
        default void onCallbackSetAntiLost(int status, int value) {
        }


        /**
         * 定时心率
         *
         * @param status      -1代表返回读
         * @param isOpen
         * @param startHour
         * @param startMinute
         * @param endHour
         * @param endMinute
         * @param interval
         */
        default void onCallbackHeart(int status, boolean isOpen, int startHour, int startMinute, int endHour, int endMinute, int interval) {
        }

        /**
         * 绑定
         *
         * @param status
         */
        default void onCallbackBind(int status) {
        }

        /**
         * 登录
         *
         * @param status
         */
        default void onCallbackLogin(int status) {
        }

        /**
         * 配对
         *
         * @param status 00:设置成功
         *               01:设置失败
         */
        default void onCallbackPair(int status) {
        }

        /**
         * 推送消息
         *
         * @param status
         */
        default void onCallbackPushMessage(int status) {
        }

        // TODO 推送音乐

        /**
         * 推送实时天气
         *
         * @param status
         */
        default void onCallbackPushWeatherNow(int status) {
        }

        /**
         * 推送预报天气
         *
         * @param status
         */
        default void onCallbackPushWeatherFeature(int status) {
        }

        /**
         * 心率数据
         *
         * @param list
         */
        default void onCallbackHeartData(List<WatchHeartDataBean> list) {
        }

        /**
         * 血压数据
         *
         * @param list
         */
        default void onCallbackBloodPressureData(List<WatchBloodPressureDataBean> list) {
        }

        /**
         * 血氧数据
         *
         * @param list
         */
        default void onCallbackBloodOxygenData(List<WatchBloodOxygenDataBean> list) {
        }

        /**
         * 每日数据
         *
         * @param list
         */
        default void onCallbackDailyData(List<WatchDailyDataBean> list) {
        }

        /**
         * 睡眠数据
         *
         * @param list
         */
        default void onCallbackSleepData(List<WatchSleepDataBean> list) {
        }

        /**
         * 运动数据
         *
         * @param list
         */
        default void onCallbackSportData(List<WatchSportDataBean> list) {
        }

        /**
         * 拍照控制
         *
         * @param status
         */
        default void onCallbackCameraControl(int status) {
        }

        /**
         * SOS指令
         *
         * @param status
         */
        default void onCallbackSos(int status) {
        }

        /**
         * @param status      -1代表返回读
         * @param isRightHand true为右手佩戴，false为左手佩戴
         */
        default void onCallbackWear(int status, boolean isRightHand) {
        }
        /**
         * @param status      -1代表返回读
         * @param isOpen      0-关,1-开
         * @param startHour
         * @param startMinute
         * @param endHour
         * @param endMinute
         * @param interval 间隔(15-255)
         */
        default void onCallbackWater(int status, boolean isOpen,int startHour, int startMinute, int endHour, int endMinute,int interval) {
        }

        // TODO 表盘文件传输
    }

    private List<OnCallback> mOnCallbackList;

    /**
     * 添加数据回调
     */
    public void addOnCallback(OnCallback onCallback) {
        if (mOnCallbackList == null) {
            mOnCallbackList = new ArrayList<>();
        }
        mOnCallbackList.add(onCallback);
    }

    /**
     * 移除数据回调
     */
    public void removeOnCallback(OnCallback onCallback) {
        if (mOnCallbackList != null) {
            mOnCallbackList.remove(onCallback);
        }
    }

    /**
     * 移除所有数据回调
     */
    public void removeAllOnCallback() {
        if (mOnCallbackList != null) {
            mOnCallbackList.clear();
        }
    }


    public void clear() {
        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
        removeAllOnCallback();

    }
}