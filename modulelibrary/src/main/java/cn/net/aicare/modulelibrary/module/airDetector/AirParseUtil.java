package cn.net.aicare.modulelibrary.module.airDetector;

import android.util.SparseArray;

import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据解析处理
 *
 * @author yesp
 */
public class AirParseUtil {
    private static final String TAG = "AirParseUtil";

    /**
     * 解析得到 TLVBean 数组
     *
     * @param cmd   cmd
     * @param bytes payload 数据
     * @return TLVBean 数组
     */
    public static List<BleAirTLVBean> getTLV(int cmd, byte[] bytes) {
        List<BleAirTLVBean> list = new ArrayList<>();
        byte[] tlvBytesAll = new byte[bytes.length - 2];
        System.arraycopy(bytes, 2, tlvBytesAll, 0, tlvBytesAll.length);
        BleLog.i(TAG, BleStrUtils.byte2HexStr(tlvBytesAll));
        for (int i = 0; i < tlvBytesAll.length; i++) {
            byte type = tlvBytesAll[i];
            int newLength = tlvBytesAll[i + 1];
            if (newLength > (tlvBytesAll.length - 2)) {
                BleLog.e("数据异常:newLength=" + newLength + "  tlvBytesAll.length=" + (tlvBytesAll.length - 2));
                return new ArrayList<>();
            }
            byte[] valueB = new byte[newLength];
            System.arraycopy(tlvBytesAll, i + 2, valueB, 0, valueB.length);
            i = newLength + 2 + i - 1;
            list.add(new BleAirTLVBean(cmd, type, valueB));
        }
        return list;
    }


    /**
     * 支持功能 0X02 解析
     *
     * @param list list
     */
    public static SparseArray<SupportBean> parseCmd02(List<BleAirTLVBean> list) {
        SparseArray<SupportBean> supportList = new SparseArray<>();
        for (BleAirTLVBean bean : list) {
            int type = bean.getType() & 0xff;
            SupportBean supportBean = new SupportBean();
            supportBean.setType(type);
            byte[] bytes = bean.getValue();
            switch (type) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                case AirConst.AIR_TYPE_HUMIDITY:
                case AirConst.AIR_TYPE_PM_2_5:
                case AirConst.AIR_TYPE_PM_1:
                case AirConst.AIR_TYPE_PM_10:
                case AirConst.AIR_TYPE_VOC:
                case AirConst.AIR_TYPE_CO2:
                case AirConst.AIR_TYPE_AQI:
                case AirConst.AIR_TYPE_TVOC:
                case AirConst.AIR_TYPE_CO:
                    int pointFormal = bytes[0] & 0x03;
                    double minFormal = ((bytes[1] & 0xff) + ((bytes[2] & 0xff) << 8)) / Math.pow(10, pointFormal);
                    double maxFormal = ((bytes[3] & 0xff) + ((bytes[4] & 0xff) << 8)) / Math.pow(10, pointFormal);
                    supportBean.setMin(minFormal);
                    supportBean.setMax(maxFormal);
                    supportBean.setPoint(pointFormal);
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    int pointTemp = bytes[0] & 0x03;
                    int sign = bytes[0] & 0x04;
                    double minTemp = ((bytes[1] & 0xff) + ((bytes[2] & 0xff) << 8)) / Math.pow(10, pointTemp);
                    minTemp = sign == 0 ? minTemp : -minTemp;
                    double maxTemp = ((bytes[3] & 0xff) + ((bytes[4] & 0xff) << 8)) / Math.pow(10, pointTemp);
                    supportBean.setMin(minTemp);
                    supportBean.setMax(maxTemp);
                    supportBean.setPoint(pointTemp);
                    break;
                case AirConst.AIR_SETTING_WARM:
                    supportBean.setCurValue(bytes[0] & 0x03);
                    break;
                case AirConst.AIR_SETTING_DEVICE_ERROR:
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                    supportBean.setCurValue(bytes[0] & 0x01);
                    break;
                case AirConst.AIR_SETTING_VOICE:
                case AirConst.AIR_SETTING_WARM_VOICE:
                    supportBean.setMax(bytes[0] & 0xff);
                    break;
                case AirConst.AIR_SETTING_WARM_DURATION:
                    supportBean.setMax((bytes[0] & 0xff) + ((bytes[1] & 0xff) << 8));
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    AlarmClockStatement alarmClockStatement = new AlarmClockStatement();
                    alarmClockStatement.setShowIcon(((bytes[0] >> 7) & 0x01) == 1);
                    alarmClockStatement.setSupportDelete(((bytes[0] >> 6) & 0x01) == 1);
                    alarmClockStatement.setAlarmCount(bytes[0] & 0x0f);
                    alarmClockStatement.setMode0(((bytes[1] >> 7) & 0x01) == 1);
                    alarmClockStatement.setMode1(((bytes[1] >> 6) & 0x01) == 1);
                    alarmClockStatement.setMode2(((bytes[1] >> 5) & 0x01) == 1);
                    alarmClockStatement.setMode3(((bytes[1] >> 4) & 0x01) == 1);
                    alarmClockStatement.setMode4(((bytes[1] >> 3) & 0x01) == 1);
                    supportBean.setExtentObject(alarmClockStatement);
                    break;
                case AirConst.AIR_KEY_SOUND:
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                case AirConst.AIR_ICON_DISPLAY:
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    supportBean.setCurValue((bytes[0] >> 7) & 0x01);
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    int[] arrayType = new int[bytes.length];
                    for (int i = 0; i < bytes.length; i++) {
                        arrayType[i] = bytes[i] & 0xff;
                    }
                    supportBean.setExtentObject(arrayType);
                    break;
                case AirConst.AIR_TIME_FORMAT:
                    supportBean.setCurValue(bytes[0] & 0x03);
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    int count = bytes[0] & 0x1f;
                    int mode = (bytes[0] >> 5) & 0x01;
                    int manual = (bytes[0] >> 6) & 0x01;
                    int auto = (bytes[0] >> 7) & 0x01;
                    BrightnessStatement statement = new BrightnessStatement();
                    statement.setLevelCount(count);
                    statement.setAutoAdjust(auto == 1);
                    statement.setManualAdjust(manual == 1);
                    statement.setMode(mode);
                    supportBean.setExtentObject(statement);
                    break;
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    int vid = (bytes[0] & 0xff) + ((bytes[1] & 0xff) << 8);
                    int pid = (bytes[2] & 0xff) + ((bytes[3] & 0xff) << 8);
                    int[] ids = {vid, pid};
                    supportBean.setCurValue(bytes[4] & 0xff);
                    supportBean.setExtentObject(ids);
                    break;
                case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                    // 单位支持(byte[1] 预留)
                    supportBean.setCurValue(bytes[0] & 0x01);
                    break;
                case AirConst.AIR_PROTOCOL_VERSION:
                    supportBean.setCurValue(bytes[0] & 0xff);
                    break;
                default:
                    break;
            }
            supportList.put(type, supportBean);
        }
        return supportList;
    }

    /**
     * 状态功能 0X04 解析
     *
     * @param list list
     */
    public static SparseArray<StatusBean> parseCmd04(List<BleAirTLVBean> list) {
        SparseArray<StatusBean> syncList = new SparseArray<>();
        for (BleAirTLVBean bean : list) {
            int type = bean.getType() & 0xff;
            StatusBean statusBean = new StatusBean();
            statusBean.setType(type);
            byte[] bytes = bean.getValue();
            switch (type) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                case AirConst.AIR_TYPE_HUMIDITY:
                case AirConst.AIR_TYPE_PM_2_5:
                case AirConst.AIR_TYPE_PM_1:
                case AirConst.AIR_TYPE_PM_10:
                case AirConst.AIR_TYPE_VOC:
                case AirConst.AIR_TYPE_CO2:
                case AirConst.AIR_TYPE_AQI:
                case AirConst.AIR_TYPE_TVOC:
                case AirConst.AIR_SETTING_WARM_DURATION:
                case AirConst.AIR_TYPE_CO:
                    statusBean.setValue((bytes[0] & 0xff) + ((bytes[1] & 0xff) << 8));
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    int pointTemp = bytes[0] & 0x03;
                    int sign = (bytes[0] >> 2) & 0x01;
                    //0=℃ 1=℉
                    int unit = (bytes[0] >> 3) & 0x01;
                    double temp = ((bytes[1] & 0xff) + ((bytes[2] & 0xff) << 8)) / Math.pow(10, pointTemp);
                    temp = sign == 0 ? temp : -temp;
                    statusBean.setValue(temp);
                    statusBean.setPoint(pointTemp);
                    statusBean.setUnit(unit);
                    break;
                case AirConst.AIR_SETTING_WARM:
                    statusBean.setOpen((bytes[0] & 0x01) == 1);
                    int[] warm = new int[]{bytes[1] & 0x01, (bytes[1] & 0x02) >> 1,
                            (bytes[1] & 0x04) >> 2, (bytes[1] & 0x08) >> 3,
                            (bytes[1] & 0x10) >> 4, (bytes[1] & 0x20) >> 5,
                            (bytes[1] & 0x40) >> 6, (bytes[1] & 0x80) >> 7,
                            bytes[2] & 0x01, (bytes[2] & 0x02) >> 1,
                            (bytes[2] & 0x04) >> 2, (bytes[2] & 0x08) >> 3,
                            (bytes[2] & 0x10) >> 4};
                    statusBean.setExtentObject(warm);
                    break;
                case AirConst.AIR_SETTING_VOICE:
                    statusBean.setOpen((bytes[0] & 0x01) == 1);
                    statusBean.setValue(bytes[1] & 0xff);
                    break;
                case AirConst.AIR_SETTING_WARM_VOICE:
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                case AirConst.AIR_TIME_FORMAT:
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    statusBean.setValue((bytes[0] & 0xff));
                    break;
                case AirConst.AIR_SETTING_BATTEY:
                    int[] batterStatus = {bytes[0] & 0x01, (bytes[0] & 0x02) >> 1};
                    statusBean.setValue((bytes[1] & 0xff));
                    statusBean.setExtentObject(batterStatus);
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    int alarmCount = bytes.length / 5;
                    ArrayList<AlarmClockInfoList.AlarmInfo> alarmList = new ArrayList<>();
                    for (int i = 0; i < alarmCount; i++) {
                        AlarmClockInfoList.AlarmInfo info = new AlarmClockInfoList.AlarmInfo();
                        int startIndex = i * 5;
                        info.setSwitchStatus((bytes[startIndex] >> 7) & 0x01);
                        info.setId(bytes[startIndex] & 0x0f);
                        info.setMode(bytes[startIndex + 1] & 0x0f);
                        info.setHour(bytes[startIndex + 2] & 0x1f);
                        info.setMinute(bytes[startIndex + 3] & 0x3f);
                        info.setDeleted(false);
                        int endIndex = startIndex + 4;
                        int[] days = new int[]{bytes[endIndex] & 0x01, (bytes[endIndex] & 0x02) >> 1,
                                (bytes[endIndex] & 0x04) >> 2, (bytes[endIndex] & 0x08) >> 3,
                                (bytes[endIndex] & 0x10) >> 4, (bytes[endIndex] & 0x20) >> 5,
                                (bytes[endIndex] & 0x40) >> 6, (bytes[endIndex] & 0x80) >> 7};
                        info.setAlarmDays(days);
                        alarmList.add(info);
                    }
                    statusBean.setExtentObject(new AlarmClockInfoList(alarmList));
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    int calCount = bytes.length / 3;
                    ArrayList<CalibrationListBean.CalibrationBean> calList = new ArrayList<>();
                    for (int i = 0; i < calCount; i++) {
                        int startIndex = i * 3;
                        int calType = bytes[startIndex] & 0xff;
                        double calValue = ((bytes[startIndex + 2] & 0xff) + ((bytes[startIndex + 1] & 0x7f) << 8));
                        int calSign = (bytes[startIndex + 1] >> 7) & 0x01;
                        calValue = calSign == 0 ? calValue : -calValue;
                        CalibrationListBean.CalibrationBean calBean = new CalibrationListBean.CalibrationBean(calType, calValue);
                        calList.add(calBean);
                    }
                    statusBean.setExtentObject(new CalibrationListBean(calList));
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    statusBean.setOpen(((bytes[0] >> 7) & 0x01) == 1);
                    statusBean.setValue(bytes[0] & 0x7f);
                    break;
                case AirConst.AIR_KEY_SOUND:
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                case AirConst.AIR_ICON_DISPLAY:
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    statusBean.setOpen(((bytes[0] >> 7) & 0x01) == 1);
                    break;
                default:
                    break;
            }
            syncList.append(type, statusBean);
        }
        return syncList;
    }

    /**
     * 设置功能 0X06 解析
     *
     * @param list list
     */
    public static SparseArray<StatusBean> parseCmd06(List<BleAirTLVBean> list) {
        SparseArray<StatusBean> syncList = new SparseArray<>();
        for (BleAirTLVBean bean : list) {
            int type = bean.getType() & 0xff;
            StatusBean statusBean = new StatusBean();
            statusBean.setType(type);
            byte[] bytes = bean.getValue();
            switch (type) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                case AirConst.AIR_TYPE_PM_2_5:
                case AirConst.AIR_TYPE_PM_1:
                case AirConst.AIR_TYPE_PM_10:
                case AirConst.AIR_TYPE_VOC:
                case AirConst.AIR_TYPE_CO2:
                case AirConst.AIR_TYPE_AQI:
                case AirConst.AIR_TYPE_TVOC:
                case AirConst.AIR_TYPE_CO:
                    int switchWarm = bytes[0] & 0x01;
                    int warm = (bytes[1] & 0xff) + ((bytes[2] & 0xff) << 8);
                    statusBean.setWarmMax(warm);
                    statusBean.setOpen(switchWarm == 1);
                    break;
                case AirConst.AIR_TYPE_HUMIDITY:
                    statusBean.setWarmMax((bytes[2] & 0xff) + ((bytes[3] & 0xff) << 8));
                    statusBean.setWarmMin((bytes[0] & 0xff) + ((bytes[1] & 0xff) << 8));
                    // 开关
                    if (bytes.length == 5) {
                        statusBean.setOpen((bytes[4] & 0x01) == 1);
                    }
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    int pointTemp = bytes[0] & 0x03;
                    // 下限值的正负号(0=正值,1=负值)
                    int signMin = (bytes[0] >> 2) & 0x01;
                    // 上限值的正负号(0=正值,1=负值)
                    int signMax = (bytes[0] >> 3) & 0x01;
                    // 当前单位 0=℃. 1=℉.
                    int unit = (bytes[0] >> 4) & 0x01;
                    // 开关
                    int switchTemp = (bytes[0] >> 5) & 0x01;
                    float warmMin = (float) (((bytes[1] & 0xff) + ((bytes[2] & 0xff) << 8)) / Math.pow(10, pointTemp));
                    float warmMax = (float) (((bytes[3] & 0xff) + ((bytes[4] & 0xff) << 8)) / Math.pow(10, pointTemp));
                    warmMax = signMax == 0 ? warmMax : -warmMax;
                    warmMin = signMin == 0 ? warmMin : -warmMin;
                    statusBean.setWarmMax(warmMax);
                    statusBean.setWarmMin(warmMin);
                    statusBean.setUnit(unit);
                    statusBean.setPoint(pointTemp);
                    statusBean.setOpen(switchTemp == 1);
                    break;
                case AirConst.AIR_SETTING_VOICE:
                    statusBean.setOpen((bytes[0] & 0x01) == 1);
                    statusBean.setValue(bytes[1] & 0xff);
                    break;
                case AirConst.AIR_SETTING_WARM_DURATION:
                    statusBean.setValue((bytes[0] & 0xff) + ((bytes[1] & 0xff) << 8));
                    break;
                case AirConst.AIR_SETTING_WARM:
                    if (bytes.length == 1) {
                        statusBean.setOpen((bytes[0] & 0x01) == 1);
                    }
                    break;
                case AirConst.AIR_SETTING_WARM_VOICE:
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                case AirConst.AIR_SETTING_BIND_DEVICE:
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                case AirConst.AIR_TIME_FORMAT:
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    statusBean.setValue((bytes[0] & 0xff));
                    break;
                case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                    statusBean.setUnit((bytes[0] & 0x01));
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    int alarmCount = bytes.length / 5;
                    ArrayList<AlarmClockInfoList.AlarmInfo> alarmList = new ArrayList<>();
                    for (int i = 0; i < alarmCount; i++) {
                        AlarmClockInfoList.AlarmInfo info = new AlarmClockInfoList.AlarmInfo();
                        int startIndex = i * 5;
                        info.setSwitchStatus((bytes[startIndex] >> 7) & 0x01);
                        info.setDeleted(((bytes[startIndex] >> 6) & 0x01) == 1);
                        info.setId(bytes[startIndex] & 0x0f);
                        info.setMode(bytes[startIndex + 1] & 0x0f);
                        info.setHour(bytes[startIndex + 2] & 0x1f);
                        info.setMinute(bytes[startIndex + 3] & 0x3f);
                        int endIndex = startIndex + 4;
                        int[] days = new int[]{bytes[endIndex] & 0x01, (bytes[endIndex] & 0x02) >> 1,
                                (bytes[endIndex] & 0x04) >> 2, (bytes[endIndex] & 0x08) >> 3,
                                (bytes[endIndex] & 0x10) >> 4, (bytes[endIndex] & 0x20) >> 5,
                                (bytes[endIndex] & 0x40) >> 6, (bytes[endIndex] & 0x80) >> 7};
                        info.setAlarmDays(days);
                        alarmList.add(info);
                    }
                    statusBean.setExtentObject(new AlarmClockInfoList(alarmList));
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    int calCount = bytes.length / 2;
                    ArrayList<CalibrationListBean.CalibrationBean> calList = new ArrayList<>();
                    for (int i = 0; i < calCount; i++) {
                        int startIndex = i * 2;
                        int calType = bytes[startIndex] & 0xff;
                        int calOperate = (bytes[startIndex + 1] >> 7) & 0x01;
                        int calReset = (bytes[startIndex + 1] >> 6) & 0x01;
                        CalibrationListBean.CalibrationBean calBean = new CalibrationListBean.CalibrationBean(calType, calOperate, calReset == 1);
                        calList.add(calBean);
                    }
                    statusBean.setExtentObject(new CalibrationListBean(calList));
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    statusBean.setOpen(((bytes[0] >> 7) & 0x01) == 1);
                    statusBean.setValue(bytes[0] & 0x7f);
                    break;
                case AirConst.AIR_KEY_SOUND:
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                case AirConst.AIR_ICON_DISPLAY:
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    statusBean.setOpen(((bytes[0] >> 7) & 0x01) == 1);
                    break;
                default:
                    break;
            }
            syncList.append(type, statusBean);
        }
        return syncList;
    }
}
