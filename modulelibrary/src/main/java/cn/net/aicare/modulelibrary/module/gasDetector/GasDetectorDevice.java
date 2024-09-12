package cn.net.aicare.modulelibrary.module.gasDetector;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 气体检测仪设备
 *
 * @author xing
 * @date 2024/09/04
 */
public class GasDetectorDevice extends BaseBleDeviceData {

    private int cid = GasDetectorBleConfig.CID;

    private static BleDevice mBleDevice;
    private static GasDetectorDevice instance;
    private SendGasDetectorData mSendGasDetectorData;
    private ParseGasDetectorData mParseGasDetectorData;
    private Set<OnGasDetectorInfoListener> mListeners = new HashSet<>();

    public static GasDetectorDevice getInstance() {
        return instance;
    }


    public static GasDetectorDevice init(BleDevice bleDevice) {
        instance = new GasDetectorDevice(bleDevice);
        return instance;
    }

    public GasDetectorDevice(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        mSendGasDetectorData = new SendGasDetectorData();
        mParseGasDetectorData = new ParseGasDetectorData();
    }


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == cid) {
            BleLog.i("收到的数据:" + BleStrUtils.byte2HexStr(hex));
            try {
                mParseGasDetectorData.parseData(hex, mListeners);
                mOnShowData.showData(BleStrUtils.byte2HexStr(hex));
            } catch (Exception e) {
                mOnShowData.showData("解析数据失败:" + e.getMessage() + "\n" + BleStrUtils.byte2HexStr(hex));
            }
        }

    }


    public void addOnGasDetectorInfoListener(OnGasDetectorInfoListener listener) {
        mListeners.add(listener);
    }

    public void removeOnGasDetectorInfoListener(OnGasDetectorInfoListener listener) {
        mListeners.remove(listener);
    }


    public void disconnect() {
        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        disconnect();
        if (mListeners != null) {
            mListeners.clear();
        }
        if (instance != null) {
            instance = null;
        }
        if (mOnShowData != null) {
            mOnShowData = null;
        }
    }


    /**
     * 获取实时信息(0x01)
     */
    public void getCurrentInfo() {
        sendData(mSendGasDetectorData.getCurrentInfo());
    }

    /**
     * CO2二氧化碳报警获取(0x02)
     */
    public void getCo2Alarm() {
        sendData(mSendGasDetectorData.getCo2Alarm());
    }

    /**
     * CO2二氧化碳报警设置(0x02)
     */
    public void setCo2Alarm(int density) {
        sendData(mSendGasDetectorData.setCo2Alarm(density));
    }

    /**
     * CO2二氧化碳工作间隔获取(0x03)
     */
    public void getCo2Interval() {
        sendData(mSendGasDetectorData.getCo2Interval());
    }

    /**
     * CO2二氧化碳工作间隔设置(0x03)
     *
     * @param interval 间隔(单位min)0代表一直
     */
    public void setCo2Interval(int interval) {
        sendData(mSendGasDetectorData.setCo2Interval(interval));
    }

    /**
     * CO2二氧化碳开关获取(0x04)
     */
    public void getCo2Switch() {
        sendData(mSendGasDetectorData.getCo2Switch());
    }


    /**
     * CO2二氧化碳开关设置(0x04)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    public void setCo2Switch(int status) {
        sendData(mSendGasDetectorData.setCo2Switch(status));
    }

    /**
     * CO一氧化碳报警获取(0x05)
     */
    public void getCoAlarm() {
        sendData(mSendGasDetectorData.getCoAlarm());
    }

    /**
     * CO一氧化碳报警设置(0x05)
     *
     * @param density 密度
     */
    public void setCoAlarm(int density) {
        sendData(mSendGasDetectorData.setCoAlarm(density));
    }


    /**
     * CO一氧化碳工作间隔获取(0x06)
     */
    public void getCoInterval() {
        sendData(mSendGasDetectorData.getCoInterval());
    }

    /**
     * CO一氧化碳工作间隔设置(0x06)
     *
     * @param interval 时间间隔 (单位min)0代表一直
     */
    public void setCoInterval(int interval) {
        sendData(mSendGasDetectorData.setCoInterval(interval));
    }

    /**
     * CO一氧化碳开关获取(0x07)
     */
    public void getCoSwitch() {
        sendData(mSendGasDetectorData.getCoSwitch());
    }

    /**
     * CO一氧化碳开关设置(0x07)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    public void setCoSwitch(int status) {
        sendData(mSendGasDetectorData.setCoSwitch(status));
    }

    /**
     * O2报警报警获取(0x08)
     */
    public void getO2Alarm() {
        sendData(mSendGasDetectorData.getO2Alarm());
    }

    /**
     * O2报警报警设置(0x08)
     *
     * @param density 报警浓度(单位%)
     */
    public void setO2Alarm(int density) {
        sendData(mSendGasDetectorData.setO2Alarm(density));
    }

    /**
     * 离线历史记录间隔获取/设置(0x09)
     */
    public void getOfflineHistoryInterval() {
        sendData(mSendGasDetectorData.getOfflineHistoryInterval());
    }

    /**
     * O2报警报警设置(0x08)
     *
     * @param interval 时间间隔 (单位min)0代表一直
     */
    public void setOfflineHistoryInterval(int interval) {
        sendData(mSendGasDetectorData.setOfflineHistoryInterval(interval));
    }


    /**
     * 温度单位获取(0x0A)
     */
    public void getTempUnit() {
        sendData(mSendGasDetectorData.getTempUnit());
    }

    /**
     * 温度单位设置(0x0A)
     *
     * @param unit 单位 0x00=℃
     *             0x01=℉
     */
    public void setTempUnit(int unit) {
        sendData(mSendGasDetectorData.setTempUnit(unit));
    }


    /**
     * 气压单位获取(0x0B)
     */
    public void getAirPressureUnit() {
        sendData(mSendGasDetectorData.getAirPressureUnit());
    }

    /**
     * 气压单位设置(0x0B)
     *
     * @param unit 单位
     *             0x00=hPa
     *             0x01=inHg
     */
    public void setAirPressureUnit(int unit) {
        sendData(mSendGasDetectorData.setAirPressureUnit(unit));
    }

    /**
     * 设备气压校准(0x0C)
     *
     * @param hPa  气压(hPa,0-9999)
     * @param inHg 气压(inHg,0-295)
     */
    public void setAirPressureCalibration(int hPa, int inHg) {
        sendData(mSendGasDetectorData.setAirPressureCalibration(hPa, inHg));
    }

    /**
     * 亮度获取(0x0D)
     */
    public void getBrightness() {
        sendData(mSendGasDetectorData.getBrightness());
    }

    /**
     * 亮度设置(0x0D)
     *
     * @param brightness 亮度(%,0-100)
     */
    public void setBrightness(int brightness) {
        sendData(mSendGasDetectorData.setBrightness(brightness));
    }

    /**
     * 声音开关获取(0x0E)
     */
    public void getSoundSwitch() {
        sendData(mSendGasDetectorData.getSoundSwitch());
    }

    /**
     * 报警开关设置(0x0E)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    public void setAlarmSwitch(int status) {
        sendData(mSendGasDetectorData.setAlarmSwitch(status));
    }

    /**
     * 震动开关获取(0x0F)
     */
    public void getShockSwitch() {
        sendData(mSendGasDetectorData.getShockSwitch());
    }


    /**
     * 震动开关设置(0x0F)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    public void setShockSwitch(int status) {
        sendData(mSendGasDetectorData.setShockSwitch(status));
    }

    /**
     * 时间同步(0x10)
     */
    public void setSyncTime() {
        long time = System.currentTimeMillis() / 1000;
        sendData(mSendGasDetectorData.setSyncTime(time));
    }

    /**
     * 息屏时间获取(0x11)
     */
    public void getScreenOffTime() {
        sendData(mSendGasDetectorData.getScreenOffTime());
    }

    /**
     * 息屏时间设置(0x11)
     *
     * @param interval 时间(单位,分钟min)0代表从不
     */
    public void setScreenOffSwitch(int interval) {
        sendData(mSendGasDetectorData.setScreenOffTime(interval));
    }

    /**
     * 关机时间获取(0x12)
     */
    public void getShutdownTime() {
        sendData(mSendGasDetectorData.getShutdownTime());
    }

    /**
     * 关机时间设置(0x12)
     *
     * @param interval 时间间隔(单位,分钟min)0代表从不
     */
    public void setShutdownTime(int interval) {
        sendData(mSendGasDetectorData.setShutdownTime(interval));
    }

    /**
     * 离线历史记录 (0x13)
     */
    public void getOfflineHistory(int id) {
        sendData(mSendGasDetectorData.getOfflineHistory(id));
    }

    /**
     * 清空离线历史记录 (0x14)
     */
    public void setClearOfflineHistory() {
        sendData(mSendGasDetectorData.setClearOfflineHistory());
    }


    /**
     * 发送数据
     *
     * @param data 数据
     */
    private void sendData(byte[] data) {
        SendMcuBean mcuBean = getMcuBean(data);
        sendData(mcuBean);
    }

    private SendMcuBean getMcuBean(byte[] data) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(cid, data);
        return sendMcuBean;
    }

    private void sendData(SendMcuBean sendMcuBean) {
        if (mBleDevice != null) {
            mBleDevice.sendData(sendMcuBean);
        }
    }

    private OnShowData mOnShowData;

    public void setOnShowData(OnShowData onShowData) {
        mOnShowData = onShowData;
    }

    public interface OnShowData {
        void showData(String data);
    }


}
