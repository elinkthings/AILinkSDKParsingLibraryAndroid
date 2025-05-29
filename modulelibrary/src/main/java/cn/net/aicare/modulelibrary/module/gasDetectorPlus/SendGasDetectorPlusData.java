package cn.net.aicare.modulelibrary.module.gasDetectorPlus;

/**
 * 发送气体探测器数据
 *
 * @author xing
 * @date 2024/09/04
 */
public class SendGasDetectorPlusData {


    /**
     * 获取支持的气体类型(0x01)
     *
     * @return {@link byte[]}
     */
    public byte[] getSupportedGas() {
        return new byte[]{GasDetectorPlusBleConfig.TYPE_SUPPORTED_GAS};
    }

    /**
     * 支持的功能列表(0x02)
     *
     * @return {@link byte[]}
     */
    public byte[] getSupportedFun() {
        return new byte[]{GasDetectorPlusBleConfig.TYPE_SUPPORTED_FUN};
    }

    /**
     * 警报推送开关(0x03)
     *
     * @return {@link byte[]}
     */
    public byte[] getAlarmSwitch() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_SWITCH;
        data[1] = (byte) 0x00;
        return data;
    }

    /**
     * 警报推送开关(0x03)
     *
     * @param status 状态
     *               0x01-开启
     *               0x00-关闭
     * @return {@link byte[]}
     */
    public byte[] setAlarmSwitch(int status) {
        byte[] data = new byte[3];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_SWITCH;
        data[1] = 0x01;
        data[2] = (byte) status;
        return data;

    }


    /**
     * 报警时长设置(0x04)
     *
     * @return {@link byte[]}
     */
    public byte[] getAlarmTime() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_TIME;
        data[1] = 0x00;
        return data;
    }

    /**
     * 报警时长设置(0x04)
     *
     * @param index 当前选中项
     * @return {@link byte[]}
     */
    public byte[] setAlarmTime(int index) {
        byte[] data = new byte[3];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_TIME;
        data[1] = 0x01;
        data[2] = (byte) index;
        return data;
    }

    /**
     * 报警铃声设置(0x05)
     *
     * @return {@link byte[]}
     */
    public byte[] getAlarmRing() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_RING;
        data[1] = 0x00;
        return data;
    }


    /**
     * 报警铃声设置(0x05)
     *
     * @param index 当前选中项
     * @return {@link byte[]}
     */
    public byte[] setAlarmRing(int index) {
        byte[] data = new byte[3];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_RING;
        data[1] = 0x01;
        data[2] = (byte) index;
        return data;
    }

    /**
     * 报警铃声试听(0x06)
     *
     * @param index 试听的项
     * @return {@link byte[]}
     */
    public byte[] setAlarmRingTest(int index) {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_RING_TEST;
        data[1] = (byte) index;
        return data;

    }


    /**
     * 离线数据存储频率(0x07)
     *
     * @return {@link byte[]}
     */
    public byte[] getHistorySaveFrequency() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_HISTORY_SAVE_FREQUENCY;
        data[1] = 0x00;
        return data;

    }

    /**
     * 离线数据存储频率(0x07)
     *
     * @param interval 间隔(分钟)
     * @return {@link byte[]}
     */
    public byte[] setHistorySaveFrequency(int interval) {
        byte[] data = new byte[4];
        data[0] = GasDetectorPlusBleConfig.TYPE_HISTORY_SAVE_FREQUENCY;
        data[1] = 0x01;
        data[2] = (byte) (interval >> 8);
        data[3] = (byte) interval;
        return data;

    }

    /**
     * 数据上报间隔(0x08)
     *
     * @return {@link byte[]}
     */
    public byte[] getDataReportInterval() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_DATA_REPORT_INTERVAL;
        data[1] = 0x00;
        return data;

    }


    /**
     * 数据上报间隔(0x08)
     *
     * @param interval 时间间隔 (单位min)
     * @return {@link byte[]}
     */
    public byte[] setDataReportInterval(int interval) {
        byte[] data = new byte[4];
        data[0] = GasDetectorPlusBleConfig.TYPE_DATA_REPORT_INTERVAL;
        data[1] = 0x01;
        data[2] = (byte) (interval >> 8);
        data[3] = (byte) interval;
        return data;

    }

    /**
     * 气体信息(0x09)
     *
     * @return {@link byte[]}
     */
    public byte[] getGasInfo() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_GAS_INFO;
        data[1] = 0x00;
        return data;

    }

    /**
     * 报警频率(0x0A)
     *
     * @return {@link byte[]}
     */
    public byte[] getAlarmFrequency() {
        byte[] data = new byte[2];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_FREQUENCY;
        data[1] = 0x00;
        return data;
    }

    /**
     * 报警频率(0x0A)
     *
     * @param interval 间隔(分钟)
     * @return {@link byte[]}
     */
    public byte[] setAlarmFrequency(int interval) {
        byte[] data = new byte[4];
        data[0] = GasDetectorPlusBleConfig.TYPE_ALARM_FREQUENCY;
        data[1] = 0x01;
        data[2] = (byte) (interval >> 8);
        data[3] = (byte) interval;
        return data;

    }

    /**
     * 报警的气体信息(0x0B)
     *
     * @return {@link byte[]}
     */
    public byte[] getAlarmGas() {
        return new byte[]{GasDetectorPlusBleConfig.TYPE_ALARM_GAS};

    }

    /**
     * 设备电量信息(0x0C)
     *
     * @return {@link byte[]}
     */
    public byte[] getPowerInfo() {
        return new byte[]{GasDetectorPlusBleConfig.TYPE_POWER_INFO};

    }


    /**
     * 单位设置(0x0D)
     * 类型文档:http://showdoc.aicare.net.cn/web/#/48?page_id=2412
     *
     * @param type 气体类型
     * @return {@link byte[]}
     */
    public byte[] getTypeUnit(int type) {
        byte[] data = new byte[3];
        data[0] = GasDetectorPlusBleConfig.TYPE_UNIT_SET;
        data[1] = 0x00;
        data[2] = (byte) type;
        return data;
    }


    /**
     * 单位设置(0x0D)
     *
     * @param type 类型
     * @param unit 单位
     * @return {@link byte[]}
     */
    public byte[] setTypeUnit(int type, int unit) {
        byte[] data = new byte[4];
        data[0] = GasDetectorPlusBleConfig.TYPE_UNIT_SET;
        data[1] = 0x00;
        data[2] = (byte) type;
        data[3] = (byte) unit;
        return data;
    }

    /**
     * 同步时间(0x0E)
     *
     * @param time 时间(秒)
     * @return {@link byte[]}
     */
    public byte[] setSyncTime(long time) {
        byte[] data = new byte[5];
        data[0] = GasDetectorPlusBleConfig.TYPE_SYNC_TIME;
        data[1] = (byte) (time >> 24);
        data[2] = (byte) (time >> 16);
        data[3] = (byte) (time >> 8);
        data[4] = (byte) (time >> 0);
        return data;
    }

}
