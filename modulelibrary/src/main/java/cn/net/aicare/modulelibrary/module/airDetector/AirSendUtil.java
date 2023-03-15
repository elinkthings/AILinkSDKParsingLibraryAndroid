package cn.net.aicare.modulelibrary.module.airDetector;

import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;

/**
 * 发送指令 Util
 *
 * @author yesp
 */
public class AirSendUtil {

    public static final int NUM_THREE = 3;

    /**
     * 获取支持列表
     */
    public static SendDataBean getSupportList() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x00;
        return setBytes(bytes);
    }

    /**
     * 获取设备状态
     */
    public static SendDataBean getDeviceState() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x03;
        bytes[1] = 0x00;
        return setBytes(bytes);
    }

    /**
     * 获取到设置的参数
     */
    public static SendDataBean getSettingState() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x05;
        bytes[1] = 0x01;
        return setBytes(bytes);
    }

    /**
     * 设置参数时，初始化前三个 byte
     *
     * @param bytes bytes
     */
    private static void initSettingBytes(byte[] bytes) {
        if (bytes.length >= NUM_THREE) {
            bytes[0] = 0x05;
            bytes[1] = 0x02;
            bytes[2] = 0x01;
        }
    }

    /**
     * 设置甲醛报警状态
     *
     * @param state 开关
     * @param point 小数点
     * @param value 值
     * @return SendDataBean
     */
    public static SendDataBean setWarmHCHO(int state, int point, float value) {
        byte[] bytes = new byte[3 + 5];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_TYPE_FORMALDEHYDE;
        bytes[4] = 0x03;
        int myValue = (int) Math.abs(value * Math.pow(10, point == -1 ? 0 : point));
        bytes[5] = (byte) state;
        bytes[6] = (byte) (myValue & 0xff);
        bytes[7] = (byte) ((myValue & 0xff00) >> 8);
        return setBytes(bytes);
    }

    /**
     * 设置温度报警状态
     *
     * @param point point
     * @param unit 单位
     * @param maxValue 上限
     * @param minValue 下限
     * @return SendDataBean
     */
    public static SendDataBean setWarmTemp(int point, int unit, float maxValue, float minValue) {
        byte[] bytes = new byte[3 + 7];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_TYPE_TEMP;
        bytes[4] = 0x05;
        // 小数点
        byte myPoint = (byte) (point & 0x03);
        // 下限值的正负号(0=正值,1=负值)
        if (minValue < 0) {
            myPoint = (byte) (myPoint | 0x04);
        }
        // 上限值的正负号(0=正值,1=负值)
        if (maxValue < 0) {
            myPoint = (byte) (myPoint | 0x08);
        }
        // 当前单位 0=℃. 1=℉.
        if (unit == AirConst.UNIT_F) {
            myPoint = (byte) (myPoint | 0x10);
        }
        bytes[5] = myPoint;
        double pow = Math.pow(10, point == -1 ? 0 : point);
        // 报警下限值
        int warmMin = (int) Math.abs(minValue * pow);
        bytes[6] = (byte) (warmMin & 0xff);
        bytes[7] = (byte) ((warmMin & 0xff00) >> 8);
        // 报警上限值
        int warmMax = (int) Math.abs(maxValue * pow);
        bytes[8] = (byte) (warmMax & 0xff);
        bytes[9] = (byte) ((warmMax & 0xff00) >> 8);
        return setBytes(bytes);
    }

    /**
     * 设置湿度报警状态
     *
     * @param point 小数点
     * @param maxValue 上限
     * @param minValue 下限
     * @return SendDataBean
     */
    public static SendDataBean setWarmHumidity(int point, float maxValue, float minValue) {
        byte[] bytes = new byte[3 + 6];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_TYPE_HUMIDITY;
        bytes[4] = 0x04;
        double pow = Math.pow(10, point == -1 ? 0 : point);
        // 报警下限值
        int myMin = (int) Math.abs(minValue * pow);
        bytes[5] = (byte) (myMin & 0xff);
        bytes[6] = (byte) ((myMin & 0xff00) >> 8);
        // 报警上限值
        int myMax = (int) Math.abs(maxValue * pow);
        bytes[7] = (byte) (myMax & 0xff);
        bytes[8] = (byte) ((myMax & 0xff00) >> 8);
        return setBytes(bytes);
    }

    /**
     * 设置报警值（单报警上限，开关状态，小数点）
     * 甲醛、PM2.5、PM1.0、PM10、VOC、CO2、AQI、TVOC、CO
     *
     * @param type 类型
     * @param switchSate 开关状态
     * @param value 报警值
     * @param point 小数点
     * @return SendDataBean
     */
    public static SendDataBean setWarmMaxByType(int type, int switchSate, float value, int point) {
        byte[] bytes = new byte[3 + 5];
        initSettingBytes(bytes);
        bytes[3] = (byte) type;
        bytes[4] = 0x03;
        int myValue = (int) Math.abs(value * Math.pow(10, point == -1 ? 0 : point));
        bytes[5] = (byte) (switchSate & 0x01);
        bytes[6] = (byte) (myValue & 0xff);
        bytes[7] = (byte) ((myValue & 0xff00) >> 8);
        return setBytes(bytes);
    }

    /**
     * 设置音量状态
     *
     * @param switchSate 开关状态
     * @param value value
     * @return SendDataBean
     */
    public static SendDataBean setVoice(int switchSate, int value) {
        byte[] bytes = new byte[3 + 4];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_SETTING_VOICE;
        bytes[4] = 0x02;
        bytes[5] = (byte) (switchSate & 0x01);
        bytes[6] = (byte) value;
        return setBytes(bytes);
    }

    /**
     * 设置报警时长
     *
     * @param value value
     * @return SendDataBean
     */
    public static SendDataBean setWarmDuration(int value) {
        byte[] bytes = new byte[3 + 4];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_SETTING_WARM_DURATION;
        bytes[4] = 0x02;
        bytes[5] = (byte) (value & 0xff);
        bytes[6] = (byte) ((value & 0xff00) >> 8);
        return setBytes(bytes);
    }

    /**
     * 设置 type 和 开关状态（Bit7）
     * 按键音效、报警音效、图标显示、监控显示
     *
     * @param type type
     * @param switchSate switchSate 0=关闭,1=打开
     * @return SendDataBean
     */
    public static SendDataBean setTypeAndSwitch(int type, int switchSate) {
        byte[] bytes = new byte[3 + 3];
        initSettingBytes(bytes);
        bytes[3] = (byte) type;
        bytes[4] = 0x01;
        bytes[5] = (byte) ((switchSate & 0x01) << 7);
        return setBytes(bytes);
    }

    /**
     * 修改温度单位
     *
     * @param unit unit
     * @return SendDataBean
     */
    public static SendDataBean setUnit(int unit) {
        byte[] bytes = new byte[3 + 3];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_SETTING_SWITCH_TEMP_UNIT;
        bytes[4] = 0x01;
        bytes[5] = (byte) unit;
        return setBytes(bytes);
    }

    /**
     * 设置 type 和 数值（一个byte）
     * 报警铃声、设备自检、恢复出厂设置、时间格式设置、数据显示模式
     *
     * @param type type
     * @param value value
     * @return SendDataBean
     */
    public static SendDataBean setTypeAndLengthOne(int type, int value) {
        byte[] bytes = new byte[3 + 3];
        initSettingBytes(bytes);
        bytes[3] = (byte) type;
        bytes[4] = 0x01;
        bytes[5] = (byte) value;
        return setBytes(bytes);
    }

    /**
     * 设备绑定
     *
     * @param mode mode
     *      0x01:APP 发起,请求用户按键通过绑定
     *      0x02:MCU 返回,MCU 等待用户按键
     *      0x03:MCU 返回,用户已按按键
     *      0x04:MCU 返回,用户超时(30s)没按按键
     *      0x05:APP 发起,APP 取消绑定
     * @return SendDataBean
     */
    public static SendDataBean sendBindDevice(int mode) {
        byte[] bytes = new byte[3 + 3];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_SETTING_BIND_DEVICE;
        bytes[4] = 0x01;
        bytes[5] = (byte) mode;
        return setBytes(bytes);
    }

    /**
     * 设备亮度设置
     *
     * @param auto type
     * @param value value
     * @return SendDataBean
     */
    public static SendDataBean setBrightnessEquipment(int auto, int value) {
        byte[] bytes = new byte[3 + 3];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_BRIGHTNESS_EQUIPMENT;
        bytes[4] = 0x01;
        int autoStatus = (auto & 0x01) << 7;
        int myValue = value & 0x7f;
        bytes[5] = (byte) (myValue | autoStatus);
        return setBytes(bytes);
    }

    /**
     * 参数校准
     *
     * @param type 类型
     * @param operate 操作，0=加 1， 1=减 1
     * @return SendDataBean
     */
    public static SendDataBean setCalibrationParam(int type , int operate){
        return setCalibrationParam(type , operate, 0);
    }

    /**
     * 参数校准
     *
     * @param type 类型
     * @param operate 操作，0=加 1， 1=减 1
     * @param reset 1=恢复默认值(校准值为 0)， 没有该功能时传 0
     * @return SendDataBean
     */
    public static SendDataBean setCalibrationParam(int type , int operate, int reset){
        byte[] bytes = new byte[3 + 4];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_CALIBRATION_PARAMETERS;
        bytes[4] = 0x02;
        bytes[5] = (byte) type;
        int myOperate = (operate & 0x01) << 7;
        int myReset = (reset & 0x01) << 6;
        bytes[6] = (byte) (myOperate | myReset);
        return setBytes(bytes);
    }

    /**
     *
     * @param cid 编号
     * @param hour 小时
     * @param minute 分钟
     * @param days 周期
     * @param mode 模式
     * @param switchStatus 开关
     * @return
     */
    public static SendDataBean setAlarm(int cid, int hour, int minute, int[] days, int mode, int switchStatus, boolean isDelete){
        if (days.length != 8) {
            return null;
        }
        byte[] bytes = new byte[3 + 7];
        initSettingBytes(bytes);
        bytes[3] = AirConst.AIR_ALARM_CLOCK;
        bytes[4] = 0x05;
        int status = (switchStatus & 0x01) << 7;
        int deleteStatus = ((isDelete ? 1 : 0)& 0x01) << 6;
        int curCid = cid & 0x0f;
        bytes[5] = (byte) (status | deleteStatus | curCid);
        bytes[6] = (byte) (mode & 0x0f);
        bytes[7] = (byte) (hour & 0x1f);
        bytes[8] = (byte) (minute & 0x3f);
        int day1 = (days[1] & 0x01) << 1;
        int day2 = (days[2] & 0x01) << 2;
        int day3 = (days[3] & 0x01) << 3;
        int day4 = (days[4] & 0x01) << 4;
        int day5 = (days[5] & 0x01) << 5;
        int day6 = (days[6] & 0x01) << 6;
        int day7 = (days[7] & 0x01) << 7;
        bytes[9] = (byte) (day7 | day6 | day5 | day4 | day3 | day2 | day1);
        return setBytes(bytes);
    }

    private static SendMcuBean setBytes(byte[] bytes) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(AirConst.DEVICE_CID, bytes);
        return sendMcuBean;
    }

}
