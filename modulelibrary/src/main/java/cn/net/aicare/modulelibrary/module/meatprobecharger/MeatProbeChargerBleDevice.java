package cn.net.aicare.modulelibrary.module.meatprobecharger;

import android.util.Log;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @auther ljl
 * on 2023/2/24
 */
public class MeatProbeChargerBleDevice extends BaseBleDeviceData implements OnMcuParameterListener, OnBleMtuListener, OnBleOtherDataListener {

    private static MeatProbeChargerBleDevice meatProbeChargerBleDevice;

    public final static int DEVICE_CID = 0x0055;

    private OnMeatProbeChargerDataListener mOnMeatProbeChargerDataListener;
    private List<ChargerProbeBean> mChargerProbeBeanList;

    @Override
    public void OnMtu(int mtu) {
        Log.e("ljl", "OnMtu: mtu is " + mtu);
        if (mtu > 100) {
            //如果mtu设置成功，则去发送超128字节的指令
            if (mOnMeatProbeChargerDataListener != null) {
                mOnMeatProbeChargerDataListener.setVersion();
                mOnMeatProbeChargerDataListener.setDeviceInfo();
            }
        }
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.onBatteryStatus(status, battery);
        }
    }

    public interface OnMeatProbeChargerDataListener {
        //mtu设置成功 APP去设置对应指令
        void setVersion();

        void setDeviceInfo();

        void onVersionInfo(String version);

        void appSyncTimeResult(int result);

        void switchUnitResult(int result);

        void setAlarmresult(String mac, int alarmResult);

        void cancelAlarmresult(String mac, int alarmResult);

        void onDeviceInfo(int supportNum, int currentNum, int chargerState, int battery, int boxUnit, List<ChargerProbeBean> chargerProbeBeanList);

        void onNoDeviceInfo(int supportNum, int currentNum, int chargingState, int battery, int boxUnit);

        void onBatteryStatus(int status, int battery);

        void onOtherData(String hexStr);

        void onDataStrA6(String hexStrA6);

        void onDataStrA7(String hexStrA7);

        void sendDataA6(String hexStrA6);

        void sendDataA7(String hexStrA7);

        void onHand(boolean status);
    }

    public void setOnMeatProbeChargerDataListener(OnMeatProbeChargerDataListener onMeatProbeChargerDataListener) {
        mOnMeatProbeChargerDataListener = onMeatProbeChargerDataListener;
    }

    public MeatProbeChargerBleDevice(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnMcuParameterListener(this);
        bleDevice.setOnBleMtuListener(this);
        bleDevice.setOnBleOtherDataListener(this);
        bleDevice.sendData(new SendBleBean(BleSendCmdUtil.getInstance().getMcuBatteryStatus()));
    }

    public static void init(BleDevice bleDevice) {
        meatProbeChargerBleDevice = null;
        meatProbeChargerBleDevice = new MeatProbeChargerBleDevice(bleDevice);
    }

    @Override
    public void onHandshake(boolean status) {
        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.onHand(status);
        }
        super.onHandshake(status);
    }

    public static MeatProbeChargerBleDevice getInstance() {
        return meatProbeChargerBleDevice;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (type != DEVICE_CID) {
            BleLog.e("CID不正确");
            return;
        }
        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.onDataStrA7(BleStrUtils.byte2HexStr(hex));
        }
        switch (hex[0]) {
            case 0x02:
                //mcu上报设备状态数据
                //数据协议版本
                int version = hex[1];
                //盒子支持的探针数量
                int supportNum = hex[2];
                //当前连接的探针数量
                int currentNum = hex[3];
                // 盒子电池状态 0->未充电  1->在充电
                int chargingState = (hex[4] << 7);
                // 电池电量百分比
                int battery = (hex[4] & 0x7F);
                // 盒子当前的温度单位
                int boxUnit = hex[5];
                // hex[6],hex[7],hex[8]字节暂时无用,判断探针数量是否大于0，是的话循环拿出每一个探针的信息
                //第一根探针 9探针编号 10-15mac地址 16-17食物温度 18-19环境温度 20电池状态 21探针插入食物状态 22-23预留
                //第二根 24探针编号 25-30mac地址 31-32食物温度 33-34环境温度 35电池状态 36探针插入食物状态 37-38预留
                if (currentNum > 0) {
                    if (mChargerProbeBeanList == null) {
                        mChargerProbeBeanList = new ArrayList<>();
                    } else {
                        mChargerProbeBeanList.clear();
                    }
                    for (int i = 0; i < currentNum; i++) {
                        //探针编号
                        int num = hex[9 + i * 15];
                        //mac 地址
                        byte[] bytes = new byte[6];
                        bytes[0] = hex[15 + i * 15];
                        bytes[1] = hex[14 + i * 15];
                        bytes[2] = hex[13 + i * 15];
                        bytes[3] = hex[12 + i * 15];
                        bytes[4] = hex[11 + i * 15];
                        bytes[5] = hex[10 + i * 15];
                        String mac = bytes2Mac(bytes);
                        // 食物温度单位
                        int foodUnit = hex[16 + i * 15] >> 7;
                        // 食物温度正负
                        int foodPositive = hex[16 + i * 15] & 0x40;
                        // 食物温度绝对值
                        int foodTemp = hex[17 + i * 15] & 0xFF + hex[16 + i * 15] & 0x3F;
                        // 环境温度单位
                        int ambientUnit = hex[18 + i * 15] >> 7;
                        // 环境温度正负
                        int ambientPositive = hex[18 + i * 15] & 0x40;
                        // 环境温度绝对值
                        int ambientTemp = hex[19 + i * 15] & 0xFF + hex[18 + i * 15] & 0x3F;
                        // 探针充电状态
                        int chargerState = hex[20 + i * 15] >> 7;
                        // 探针电量
                        int probeBattery = hex[20 + i * 15] & 0x7F;
                        // 探针插入食物状态 插入1 未插入0 不支持255
                        int insertState = hex[21 + i * 15];
                        //数据类负值
                        ChargerProbeBean chargerProbeBean = new ChargerProbeBean();
                        chargerProbeBean.setNum(num);
                        chargerProbeBean.setMac(mac);
                        chargerProbeBean.setFoodUnit(foodUnit);
                        chargerProbeBean.setFoodPositive(foodPositive);
                        chargerProbeBean.setFoodTemp(foodTemp);
                        chargerProbeBean.setAmbientUnit(ambientUnit);
                        chargerProbeBean.setAmbientPositive(ambientPositive);
                        chargerProbeBean.setAmbientTemp(ambientTemp);
                        chargerProbeBean.setChargerState(chargerState);
                        chargerProbeBean.setBattery(probeBattery);
                        chargerProbeBean.setInsertState(insertState);
                        mChargerProbeBeanList.add(chargerProbeBean);
                        if (mOnMeatProbeChargerDataListener != null) {
                            //mcu上报设备状态数据回调
                            mOnMeatProbeChargerDataListener.onDeviceInfo(supportNum, currentNum, chargingState, battery, boxUnit, mChargerProbeBeanList);
                        }
                    }
                } else {
                    //盒子当前没有连接探针
                    if (mOnMeatProbeChargerDataListener != null) {
                        mOnMeatProbeChargerDataListener.onNoDeviceInfo(supportNum, currentNum, chargingState, battery, boxUnit);
                    }
                }
                break;
            case 0x03:
                //探针数据
                //0->无探针数据 1->有探针数据
                int result = hex[1];
                if (result == 1) {
                    //探针数据解析
                    if (hex.length >= 48) {
                        //解析获取mac地址（小端序）
                        byte[] macBytes = new byte[6];
                        macBytes[0] = hex[7];
                        macBytes[1] = hex[6];
                        macBytes[2] = hex[5];
                        macBytes[3] = hex[4];
                        macBytes[4] = hex[3];
                        macBytes[5] = hex[2];
                        String mac = bytes2Mac(macBytes);
                        //数据协议版本
                        int DataVersion = hex[8] & 0xff;
                        //烧烤探针ID *1000为时间戳
                        byte[] cookingIdBytes = new byte[4];
                        cookingIdBytes[0] = hex[9];
                        cookingIdBytes[1] = hex[10];
                        cookingIdBytes[2] = hex[11];
                        cookingIdBytes[3] = hex[12];
                        long cookingId = ChargerProbeByteUtils.byteToInt(cookingIdBytes) * 1000L;
                        //食物类型
                        int foodType = hex[13];
                        //食物熟度
                        int foodRawness = hex[14];
//                        食物目标温度：℃
                        int foodTempC = (hex[15] << 8) + hex[16] & 0xff;
//                        食物目标温度：℉
//                        炉温目标下限：℃
//                        炉温目标下限：℉
//                        炉温目标上限：℃
//                        炉温目标上限：℉
//                        提醒温度对目标温度百分比 0~1.0
//                        计时开始时间戳
//                        计时结束时间戳
//                        当前温度单位
//                        食物提醒温度：℃
//                        食物提醒温度：℉
                        //
                    } else {
                        //数据长度不对，有问题
                    }
                } else {
                    //告诉app没有数据
                }

                break;
            case 0x04:
                //mcu上报单位切换结果
                int unitResult = hex[1];
                if (mOnMeatProbeChargerDataListener != null) {
                    mOnMeatProbeChargerDataListener.switchUnitResult(unitResult);
                }
                break;
            case 0x06:
                //mcu回复报警状态成功或失败 0->成功 1->失败 2->不支持
                byte[] macBytes = new byte[6];
                macBytes[0] = hex[6];
                macBytes[1] = hex[5];
                macBytes[2] = hex[4];
                macBytes[3] = hex[3];
                macBytes[4] = hex[2];
                macBytes[5] = hex[1];
                int alarmResult = hex[7];
                if (mOnMeatProbeChargerDataListener != null) {
                    mOnMeatProbeChargerDataListener.setAlarmresult(bytes2Mac(macBytes), alarmResult);
                }
                break;
            case 0x08:
                //mcu回复取消报警 0->成功 1->失败 2->不支持
                byte[] macBytes1 = new byte[6];
                macBytes1[0] = hex[6];
                macBytes1[1] = hex[5];
                macBytes1[2] = hex[4];
                macBytes1[3] = hex[3];
                macBytes1[4] = hex[2];
                macBytes1[5] = hex[1];
                int alarmResult1 = hex[7];
                if (mOnMeatProbeChargerDataListener != null) {
                    mOnMeatProbeChargerDataListener.cancelAlarmresult(bytes2Mac(macBytes1), alarmResult1);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] data) {
        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.onOtherData(BleStrUtils.byte2HexStr(data));
        }
    }

    @Override
    public void onNotifyDataA6(String uuid, byte[] hex) {

        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.onDataStrA6(BleStrUtils.byte2HexStr(hex));
        }
        switch (hex[0]) {
            case 0x37:
                int result = hex[1];
                if (mOnMeatProbeChargerDataListener != null) {
                    mOnMeatProbeChargerDataListener.appSyncTimeResult(result);
                }
                break;
            case 0x38:
                //mcu请求同步时间
                if (hex[1] == 0x01) {
                    if (meatProbeChargerBleDevice != null) {
                        meatProbeChargerBleDevice.appSyncTime();
                    }
                }
                break;
            case 0x46:
                //获取模块版本号
                byte[] bytes = new byte[hex.length - 2];
                System.arraycopy(hex, 2, bytes, 0, hex.length - 2);
                if (mOnMeatProbeChargerDataListener != null) {
                    mOnMeatProbeChargerDataListener.onVersionInfo(hexToAsciiString(BleStrUtils.byte2HexStr(bytes)));
                }
                break;
            default:
                break;
        }


    }

    /**
     * 十六进制转ASCII码
     */
    private String hexToAsciiString(String hex) {
        String hexStr = hex.replaceAll(" ", "");
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            stringBuilder.append((char) Integer.parseInt(str, 16));
        }
        return stringBuilder.toString();
    }

    /**
     * mac地址去除冒号转字节数组（小端序）
     *
     * @param mac
     * @return
     */
    private byte[] macStr2Bytes(String mac) {
        String[] macAddress = mac.split(":");
        byte[] bytes = new byte[6];
        for (int i = 5; i >= 0; i--) {
            bytes[5 - i] = (byte) Integer.parseInt(macAddress[i], 16);
        }
        return bytes;
    }


    /**
     * 字节数组转mac地址
     *
     * @param bytes
     * @return
     */
    private String bytes2Mac(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i != (bytes.length - 1)) {
                stringBuilder.append(String.format("%02X", bytes[i]).toUpperCase() + ":");
            } else {
                stringBuilder.append(String.format("%02X", bytes[i]).toUpperCase());
            }
        }
        return String.valueOf(stringBuilder);
    }


    //SendBleBean用于与蓝牙模块交互,发送的数据是A6开头
    //SendMcuBean用于与mcu交互,发送的数据是A7开头

    private void sendCmdA6(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
        Log.e("ljl", "sendCmdA6: " + BleStrUtils.byte2HexStr(bytes));
        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.sendDataA6(BleStrUtils.byte2HexStr(bytes));
        }
    }

    /**
     * 获取设备版本号,设置了mtu后才能获取到完整版本号
     */
    public void getVersion() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x46;
        Log.e("ljl", "获取版本号: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA6(bytes);
//        SendBleBean sendBleBean = new SendBleBean();
//        sendBleBean.setHex(bytes);
//        sendData(sendBleBean);
    }

    /**
     * APP同步时间
     */
    public void appSyncTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int moth = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY) {
            week = week - 1;
            if (week == 0) {
                week = 7;
            }
        }

        byte[] bytes = new byte[8];
        bytes[0] = 0x37;
        bytes[1] = (byte) (year - 2000);
        bytes[2] = (byte) moth;
        bytes[3] = (byte) day;
        bytes[4] = (byte) hour;
        bytes[5] = (byte) minute;
        bytes[6] = (byte) second;
        bytes[7] = (byte) week;

        Log.e("ljl", "同步时间: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA6(bytes);
//        SendBleBean sendBleBean = new SendBleBean();
//        sendBleBean.setHex(bytes);
//        sendData(sendBleBean);
    }

    private void sendCmdA7(byte[] bytes) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x55, bytes);
        sendData(sendMcuBean);
        Log.e("ljl", "sendCmdA7: " + BleStrUtils.byte2HexStr(bytes));
        if (mOnMeatProbeChargerDataListener != null) {
            mOnMeatProbeChargerDataListener.sendDataA7(BleStrUtils.byte2HexStr(bytes));
        }
    }

    /**
     * APP获取设备状态数据
     */
    public void getDeviceInfo() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x01;
        Log.e("ljl", "APP获取设备状态数据: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA7(bytes);
//        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(0x55, bytes);
//        sendData(sendMcuBean);
    }

    /**
     * 单位切换
     *
     * @param isUnitC true->0x00摄氏度 false->0x01华氏度
     */
    public void switchUnit(boolean isUnitC) {
        byte[] bytes = new byte[2];
        bytes[0] = 0x04;
        if (isUnitC) {
            bytes[1] = 0x00;
        } else {
            bytes[1] = 0x01;
        }
        Log.e("ljl", "单位切换: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA7(bytes);
//        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(0x55, bytes);
//        sendData(sendMcuBean);
    }

    /**
     * 发送报警状态数据
     *
     * @param mac           Mac地址
     * @param isTimeCount
     * @param isHighAmbient
     * @param isTargetOk
     */
    public void setAlarmInfo(String mac, boolean isTimeCount, boolean isHighAmbient, boolean isTargetOk) {
        byte[] bytes = new byte[8];
        bytes[0] = 0x05;
        byte[] macBytes = macStr2Bytes(mac);
        bytes[1] = macBytes[0];
        bytes[2] = macBytes[1];
        bytes[3] = macBytes[2];
        bytes[4] = macBytes[3];
        bytes[5] = macBytes[4];
        bytes[6] = macBytes[5];
        bytes[7] = (byte) (((isTargetOk ? 0x01 : 0x00))
                | ((isHighAmbient ? 0x01 : 0x00) << 1)
                | ((isTimeCount ? 0x01 : 0x00) << 2));

        Log.e("ljl", "发送报警状态数据: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA7(bytes);
//        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(0x55, bytes);
//        sendData(sendMcuBean);
    }

    /**
     * 取消警报
     *
     * @param mac
     */
    public void cancelAlarm(String mac) {
        byte[] bytes = new byte[8];
        bytes[0] = 0x07;
        byte[] macBytes = macStr2Bytes(mac);
        bytes[1] = macBytes[0];
        bytes[2] = macBytes[1];
        bytes[3] = macBytes[2];
        bytes[4] = macBytes[3];
        bytes[5] = macBytes[4];
        bytes[6] = macBytes[5];
        bytes[7] = (byte) 0xFF;

        Log.e("ljl", "取消警报: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA7(bytes);
//        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(0x55, bytes);
//        sendData(sendMcuBean);
    }

    /**
     * 获取或者清除探针数据
     *
     * @param value 0->清除探针数据 2->获取探针数据
     * @param mac   探针mac地址
     */
    public void setProbeData(int value, String mac) {
        byte[] bytes = new byte[8];
        bytes[0] = 0x03;
        bytes[1] = (byte) value;
        byte[] macBytes = macStr2Bytes(mac);
        bytes[2] = macBytes[0];
        bytes[3] = macBytes[1];
        bytes[4] = macBytes[2];
        bytes[5] = macBytes[3];
        bytes[6] = macBytes[4];
        bytes[7] = macBytes[5];

        Log.e("ljl", "获取或者清除探针数据: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA7(bytes);
//        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(0x55, bytes);
//        sendData(sendMcuBean);
    }

    /**
     * @param mac                     探针mac地址
     * @param version                 数据协议版本
     * @param cookingId               烧烤id (选择食物的时间戳)
     * @param foodType                食物类型
     * @param foodRawness             食物熟度
     * @param targetTemperature_C     食物目标温度：℃
     * @param targetTemperature_F     食物目标温度：℉
     * @param ambientMinTemperature_C 炉温目标下限：℃
     * @param ambientMinTemperature_F 炉温目标下限：℉
     * @param ambientMaxTemperature_C 炉温目标上限：℃
     * @param ambientMaxTemperature_F 炉温目标上限：℉
     * @param alarmTemperaturePercent 提醒温度对目标温度百分比 0~1.0
     * @param timeStart               计时开始时间戳
     * @param timeEnd                 计时结束时间戳
     * @param currentUnit             当前温度单位
     * @param alarmTemperature_C      食物提醒温度：℃
     * @param alarmTemperature_F      食物提醒温度：℉
     */
    public void setProbeData(String mac, int version, long cookingId, int foodType, int foodRawness,
                             int targetTemperature_C, int targetTemperature_F, int ambientMinTemperature_C,
                             int ambientMinTemperature_F, int ambientMaxTemperature_C, int ambientMaxTemperature_F,
                             double alarmTemperaturePercent, long timeStart, long timeEnd, int currentUnit,
                             int alarmTemperature_C, int alarmTemperature_F) {
        byte[] bytes = new byte[136];
        bytes[0] = 0x03;
        //设置探针数据
        bytes[1] = 0x01;
        //mac 地址
        byte[] macBytes = macStr2Bytes(mac);
        bytes[2] = macBytes[5];
        bytes[3] = macBytes[4];
        bytes[4] = macBytes[3];
        bytes[5] = macBytes[2];
        bytes[6] = macBytes[1];
        bytes[7] = macBytes[0];
        //数据协议版本号 当前固定0x02
        bytes[8] = (byte) version;
        //烧烤id (选择食物的时间戳)
        int intCookingId = (int) (cookingId / 1000);
        byte[] cookingBytes = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.intToBytes(intCookingId));
        bytes[9] = cookingBytes[0];
        bytes[10] = cookingBytes[1];
        bytes[11] = cookingBytes[2];
        bytes[12] = cookingBytes[3];
        //食物类型
        bytes[13] = (byte) foodType;
        //食物熟度
        bytes[14] = (byte) foodRawness;
//        食物目标温度：℃
        byte[] sTargetTempC = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(targetTemperature_C)));
        if (targetTemperature_C >= 0) {
            bytes[15] = sTargetTempC[0];
        } else {
            bytes[15] = (byte) (sTargetTempC[0] | 0x80);
        }
        bytes[16] = sTargetTempC[1];
//        食物目标温度：℉
        byte[] sTargetTempF = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(targetTemperature_F)));
        if (targetTemperature_F >= 0) {
            bytes[17] = sTargetTempF[0];
        } else {
            bytes[17] = (byte) (sTargetTempF[0] | 0x80);
        }
        bytes[18] = sTargetTempF[1];
//        炉温目标下限：℃
        byte[] sAmbientTempMinC = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(ambientMinTemperature_C)));
        if (ambientMinTemperature_C >= 0) {
            bytes[19] = sAmbientTempMinC[0];
        } else {
            bytes[19] = (byte) (sAmbientTempMinC[0] | 80);
        }
        bytes[20] = sAmbientTempMinC[1];
//        炉温目标下限：℉
        byte[] sAmbientTempMinF = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(ambientMinTemperature_F)));
        if (ambientMinTemperature_F >= 0) {
            bytes[21] = sAmbientTempMinF[0];
        } else {
            bytes[21] = (byte) (sAmbientTempMinF[0] | 80);
        }
        bytes[22] = sAmbientTempMinF[1];
//        炉温目标上限：℃
        byte[] sAmbientTempMaxC = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(ambientMaxTemperature_C)));
        if (ambientMaxTemperature_C >= 0) {
            bytes[23] = sAmbientTempMaxC[0];
        } else {
            bytes[23] = (byte) (sAmbientTempMaxC[0] | 80);
        }
        bytes[24] = sAmbientTempMaxC[1];
//        炉温目标上限：℉
        byte[] sAmbientTempMaxF = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(ambientMaxTemperature_F)));
        if (ambientMaxTemperature_F >= 0) {
            bytes[25] = sAmbientTempMaxF[0];
        } else {
            bytes[25] = (byte) (sAmbientTempMaxF[0] | 80);
        }
        bytes[26] = sAmbientTempMaxF[1];
//        提醒温度对目标温度百分比 0~1.0
        byte[] alarmPercentBytes = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.doubleToByte(alarmTemperaturePercent));
        bytes[27] = alarmPercentBytes[0];
        bytes[28] = alarmPercentBytes[1];
        bytes[29] = alarmPercentBytes[2];
        bytes[30] = alarmPercentBytes[3];
        bytes[31] = alarmPercentBytes[4];
        bytes[32] = alarmPercentBytes[5];
        bytes[33] = alarmPercentBytes[6];
        bytes[34] = alarmPercentBytes[7];
//        计时开始时间戳
        int intTimeStart = (int) (timeStart / 1000);
        byte[] timeStartBytes = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.intToBytes(intTimeStart));
        bytes[35] = timeStartBytes[0];
        bytes[36] = timeStartBytes[1];
        bytes[37] = timeStartBytes[2];
        bytes[38] = timeStartBytes[3];
//        计时结束时间戳
        int intTimeEnd = (int) (timeEnd / 1000);
        byte[] timeEndBytes = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.intToBytes(intTimeEnd));
        bytes[39] = timeEndBytes[0];
        bytes[40] = timeEndBytes[1];
        bytes[41] = timeEndBytes[2];
        bytes[42] = timeEndBytes[3];
//        当前温度单位
        bytes[43] = (byte) currentUnit;
//        食物提醒温度：℃
        byte[] alarmTempBytesC = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(alarmTemperature_C)));
        if (alarmTemperature_C >= 0) {
            bytes[44] = alarmTempBytesC[0];
        } else {
            bytes[44] = (byte) (alarmTempBytesC[0] | 80);
        }
        bytes[45] = alarmTempBytesC[1];
//        食物提醒温度：℉
        byte[] alarmTempBytesF = ChargerProbeByteUtils.changeBytes(ChargerProbeByteUtils.shortToBytes((short) Math.abs(alarmTemperature_F)));
        if (alarmTemperature_F >= 0) {
            bytes[46] = alarmTempBytesF[0];
        } else {
            bytes[46] = (byte) (alarmTempBytesF[0] | 80);
        }
        bytes[47] = alarmTempBytesF[1];
        //剩余未用上字节全补0
        for (int i = 48; i < bytes.length; i++) {
            bytes[i] = 0x00;
        }
        Log.e("ljl", "设置探针参数: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA7(bytes);
//        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(0x55, bytes);
//        sendData(sendMcuBean);
    }


}
