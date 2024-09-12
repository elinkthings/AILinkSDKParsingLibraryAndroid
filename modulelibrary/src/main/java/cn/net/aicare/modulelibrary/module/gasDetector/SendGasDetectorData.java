package cn.net.aicare.modulelibrary.module.gasDetector;

/**
 * 发送气体探测器数据
 *
 * @author xing
 * @date 2024/09/04
 */
public class SendGasDetectorData {


    /**
     * 获取实时信息(0x01)
     *
     * @return {@link byte[]}
     */
    public byte[] getCurrentInfo() {
        return new byte[]{GasDetectorBleConfig.TYPE_CURRENT_INFO};
    }

    /**
     * CO2二氧化碳报警获取(0x02)
     *
     * @return {@link byte[]}
     */
    public byte[] getCo2Alarm() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_CO2_ALARM;
        data[1] = 0x00;
        return data;
    }

    /**
     * CO2二氧化碳报警设置(0x02)
     *
     * @param density 密度
     * @return {@link byte[]}
     */
    public byte[] setCo2Alarm(int density) {
        byte[] data = new byte[4];
        data[0] = GasDetectorBleConfig.TYPE_CO2_ALARM;
        data[1] = 0x01;
        data[2] = (byte) (density >> 8);
        data[3] = (byte) density;
        return data;

    }


    /**
     * CO2二氧化碳工作间隔获取(0x03)
     *
     * @return {@link byte[]}
     */
    public byte[] getCo2Interval() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_CO2_INTERVAL;
        data[1] = 0x00;
        return data;
    }

    /**
     * CO2二氧化碳工作间隔设置(0x03)
     *
     * @param interval 时间间隔
     * @return {@link byte[]}
     */
    public byte[] setCo2Interval(int interval) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_CO2_INTERVAL;
        data[1] = 0x01;
        data[2] = (byte) interval;
        return data;
    }

    /**
     * CO2二氧化碳开关获取(0x04)
     *
     * @return {@link byte[]}
     */
    public byte[] getCo2Switch() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_CO2_SWITCH;
        data[1] = 0x00;
        return data;
    }


    /**
     * CO2二氧化碳开关设置(0x04)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     * @return {@link byte[]}
     */
    public byte[] setCo2Switch(int status) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_CO2_SWITCH;
        data[1] = 0x01;
        data[2] = (byte) status;
        return data;
    }

    /**
     * CO一氧化碳报警获取(0x05)
     *
     * @return {@link byte[]}
     */
    public byte[] getCoAlarm() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_CO_ALARM;
        data[1] = 0x00;
        return data;

    }


    /**
     * CO一氧化碳报警设置(0x05)
     *
     * @param density 报警浓度(单位ppm)
     * @return {@link byte[]}
     */
    public byte[] setCoAlarm(int density) {
        byte[] data = new byte[4];
        data[0] = GasDetectorBleConfig.TYPE_CO_ALARM;
        data[1] = 0x01;
        data[2] = (byte) (density >> 8);
        data[3] = (byte) density;
        return data;

    }

    /**
     * CO一氧化碳工作间隔获取(0x06)
     *
     * @return {@link byte[]}
     */
    public byte[] getCoInterval() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_CO_INTERVAL;
        data[1] = 0x00;
        return data;

    }


    /**
     * CO一氧化碳工作间隔设置(0x06)
     *
     * @param interval 时间间隔 (单位min)0代表一直
     * @return {@link byte[]}
     */
    public byte[] setCoInterval(int interval) {
        byte[] data = new byte[4];
        data[0] = GasDetectorBleConfig.TYPE_CO_INTERVAL;
        data[1] = 0x01;
        data[2] = (byte) (interval >> 8);
        data[3] = (byte) interval;
        return data;

    }

    /**
     * CO一氧化碳开关获取(0x07)
     *
     * @return {@link byte[]}
     */
    public byte[] getCoSwitch() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_CO_SWITCH;
        data[1] = 0x00;
        return data;

    }


    /**
     * CO一氧化碳开关设置(0x07)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     * @return {@link byte[]}
     */
    public byte[] setCoSwitch(int status) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_CO_SWITCH;
        data[1] = (byte) 0x01;
        data[2] = (byte) status;
        return data;
    }


    /**
     * O2报警报警获取(0x08)
     *
     * @return {@link byte[]}
     */
    public byte[] getO2Alarm() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_O2_ALARM;
        data[1] = 0x00;
        return data;

    }


    /**
     * O2报警报警设置(0x08)
     *
     * @param density 报警浓度(单位%)
     * @return {@link byte[]}
     */
    public byte[] setO2Alarm(int density) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_O2_ALARM;
        data[1] = (byte) 0x01;
        data[2] = (byte) density;
        return data;
    }

    /**
     * 离线历史记录间隔获取/设置(0x09)
     *
     * @return {@link byte[]}
     */
    public byte[] getOfflineHistoryInterval() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_HISTORY_INTERVAL;
        data[1] = 0x00;
        return data;

    }


    /**
     * O2报警报警设置(0x08)
     *
     * @param interval (单位min)0代表一直
     * @return {@link byte[]}
     */
    public byte[] setOfflineHistoryInterval(int interval) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_HISTORY_INTERVAL;
        data[1] = (byte) 0x01;
        data[2] = (byte) interval;
        return data;
    }

    /**
     * 温度单位获取(0x0A)
     *
     * @return {@link byte[]}
     */
    public byte[] getTempUnit() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_TEMP_UNIT;
        data[1] = 0x00;
        return data;

    }


    /**
     * 温度单位设置(0x0A)
     *
     * @param unit 单位 0x00=℃
     *             0x01=℉
     * @return {@link byte[]}
     */
    public byte[] setTempUnit(int unit) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_TEMP_UNIT;
        data[1] = (byte) 0x01;
        data[2] = (byte) unit;
        return data;
    }

    /**
     * 气压单位获取(0x0B)
     *
     * @return {@link byte[]}
     */
    public byte[] getAirPressureUnit() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_AIR_PRESSURE_UNIT;
        data[1] = 0x00;
        return data;

    }


    /**
     * 气压单位设置(0x0B)
     *
     * @param unit 单位
     *             0x00=hPa
     *             0x01=inHg
     * @return {@link byte[]}
     */
    public byte[] setAirPressureUnit(int unit) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_AIR_PRESSURE_UNIT;
        data[1] = (byte) 0x01;
        data[2] = (byte) unit;
        return data;
    }

    /**
     * 设备气压校准(0x0C)
     *
     * @param hPa  气压(hPa,0-9999)
     * @param inHg 气压(inHg,0-295)
     * @return {@link byte[]}
     */
    public byte[] setAirPressureCalibration(int hPa, int inHg) {
        byte[] data = new byte[5];
        data[0] = GasDetectorBleConfig.TYPE_AIR_PRESSURE_CALIBRATION;
        data[1] = (byte) (hPa >> 8);
        data[2] = (byte) (hPa);
        data[3] = (byte) (inHg >> 8);
        data[4] = (byte) (inHg);
        return data;
    }

    /**
     * 亮度获取(0x0D)
     *
     * @return {@link byte[]}
     */
    public byte[] getBrightness() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_BRIGHTNESS_SET;
        data[1] = 0x00;
        return data;

    }


    /**
     * 亮度设置(0x0D)
     *
     * @param brightness 亮度(%,0-100)
     * @return {@link byte[]}
     */
    public byte[] setBrightness(int brightness) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_BRIGHTNESS_SET;
        data[1] = (byte) 0x01;
        data[2] = (byte) brightness;
        return data;
    }

    /**
     * 声音开关获取(0x0E)
     *
     * @return {@link byte[]}
     */
    public byte[] getSoundSwitch() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_SOUND_SWITCH_SET;
        data[1] = 0x00;
        return data;

    }


    /**
     * 报警开关设置(0x0E)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     * @return {@link byte[]}
     */
    public byte[] setAlarmSwitch(int status) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_SOUND_SWITCH_SET;
        data[1] = (byte) 0x01;
        data[2] = (byte) status;
        return data;
    }

    /**
     * 震动开关获取(0x0F)
     *
     * @return {@link byte[]}
     */
    public byte[] getShockSwitch() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_SHOCK_SWITCH_SET;
        data[1] = 0x00;
        return data;

    }


    /**
     * 震动开关设置(0x0F)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     * @return {@link byte[]}
     */
    public byte[] setShockSwitch(int status) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_SHOCK_SWITCH_SET;
        data[1] = (byte) 0x01;
        data[2] = (byte) status;
        return data;
    }


    /**
     * 时间同步(0x10)
     *
     * @param time 时间戳(大端序,单位:s)
     * @return {@link byte[]}
     */
    public byte[] setSyncTime(long time) {
        byte[] data = new byte[5];
        data[0] = GasDetectorBleConfig.TYPE_SYNC_TIME;
        data[1] = (byte) (time >> 24);
        data[2] = (byte) (time >> 16);
        data[3] = (byte) (time >> 8);
        data[4] = (byte) (time);
        return data;

    }


    /**
     * 息屏时间获取(0x11)
     *
     * @return {@link byte[]}
     */
    public byte[] getScreenOffTime() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_SCREEN_OFF_TIME;
        data[1] = 0x00;
        return data;

    }


    /**
     * 息屏时间设置(0x11)
     *
     * @param interval 时间(单位,分钟min)0代表从不
     * @return {@link byte[]}
     */
    public byte[] setScreenOffTime(int interval) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_SCREEN_OFF_TIME;
        data[1] = (byte) 0x01;
        data[2] = (byte) interval;
        return data;
    }

    /**
     * 关机时间获取(0x12)
     *
     * @return {@link byte[]}
     */
    public byte[] getShutdownTime() {
        byte[] data = new byte[2];
        data[0] = GasDetectorBleConfig.TYPE_SHUTDOWN_TIME;
        data[1] = 0x00;
        return data;

    }


    /**
     * 关机时间设置(0x12)
     *
     * @param interval 时间(单位,分钟min)0代表从不
     * @return {@link byte[]}
     */
    public byte[] setShutdownTime(int interval) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_SHUTDOWN_TIME;
        data[1] = (byte) 0x01;
        data[2] = (byte) interval;
        return data;
    }


    /**
     * 离线历史记录 (0x13)
     *
     * @param id id
     * @return {@link byte[]}
     */
    public byte[] getOfflineHistory(int id) {
        byte[] data = new byte[3];
        data[0] = GasDetectorBleConfig.TYPE_OFFLINE_HISTORY;
        data[1] = (byte) (id >> 8);
        data[2] = (byte) (id);
        return data;

    }


    /**
     * 清空离线历史记录 (0x14)
     *
     * @return {@link byte[]}
     */
    public byte[] setClearOfflineHistory() {
        byte[] data = new byte[1];
        data[0] = GasDetectorBleConfig.TYPE_CLEAR_OFFLINE_HISTORY;
        return data;
    }

}
