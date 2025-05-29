package cn.net.aicare.modulelibrary.module.gasDetectorPlus;

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
public class GasDetectorPlusBleDevice extends BaseBleDeviceData {

    private int cid = GasDetectorPlusBleConfig.CID;

    private static BleDevice mBleDevice;
    private static GasDetectorPlusBleDevice instance;
    private SendGasDetectorPlusData mSendGasDetectorPlusData;
    private ParseGasDetectorPlusData mParseGasDetectorPlusData;
    private Set<OnGasDetectorPlusInfoListener> mListeners = new HashSet<>();

    public static GasDetectorPlusBleDevice getInstance() {
        return instance;
    }


    public static GasDetectorPlusBleDevice init(BleDevice bleDevice) {
        instance = new GasDetectorPlusBleDevice(bleDevice);
        return instance;
    }

    public GasDetectorPlusBleDevice(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        bleDevice.setMtu(517);
        mSendGasDetectorPlusData = new SendGasDetectorPlusData();
        mParseGasDetectorPlusData = new ParseGasDetectorPlusData();

    }


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == cid) {
            BleLog.i("收到的数据:" + BleStrUtils.byte2HexStr(hex));
            try {
                mParseGasDetectorPlusData.parseData(hex, mListeners);
                mOnShowData.showData(BleStrUtils.byte2HexStr(hex));
            } catch (Exception e) {
                mOnShowData.showData("解析数据失败:" + e.getMessage() + "\n" + BleStrUtils.byte2HexStr(hex));
            }
        }

    }


    public void addOnGasDetectorInfoListener(OnGasDetectorPlusInfoListener listener) {
        mListeners.add(listener);
    }

    public void removeOnGasDetectorInfoListener(OnGasDetectorPlusInfoListener listener) {
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
     * 获取支持的气体类型(0x01)
     */
    public void getSupportedGas() {
        sendData(mSendGasDetectorPlusData.getSupportedGas());
    }

    /**
     * 支持的功能列表(0x02)
     */
    public void getSupportedFun() {
        sendData(mSendGasDetectorPlusData.getSupportedFun());
    }

    /**
     * 警报推送开关(0x03)
     */
    public void getAlarmSwitch() {
        sendData(mSendGasDetectorPlusData.getAlarmSwitch());
    }

    /**
     * 警报推送开关(0x03)
     *
     * @param status 状态
     *               0x01-开启
     *               0x00-关闭
     */
    public void setAlarmSwitch(int status) {
        sendData(mSendGasDetectorPlusData.setAlarmSwitch(status));
    }

    /**
     * 报警时长设置(0x04)
     */
    public void getAlarmTime() {
        sendData(mSendGasDetectorPlusData.getAlarmTime());
    }

    /**
     * CO2二氧化碳开关获取(0x04)
     *
     * @param index 当前选中项
     */
    public void setAlarmTime(int index) {
        sendData(mSendGasDetectorPlusData.setAlarmTime(index));
    }


    /**
     * 报警铃声设置(0x05)
     */
    public void getAlarmRing() {
        sendData(mSendGasDetectorPlusData.getAlarmRing());
    }

    /**
     * 报警铃声设置(0x05)
     *
     * @param index 当前选中项
     */
    public void setAlarmRing(int index) {
        sendData(mSendGasDetectorPlusData.setAlarmRing(index));
    }

    /**
     * 报警铃声试听(0x06)
     *
     * @param index 试听的项
     */
    public void setAlarmRingTest(int index) {
        sendData(mSendGasDetectorPlusData.setAlarmRingTest(index));
    }


    /**
     * 离线数据存储频率(0x07)
     */
    public void getHistorySaveFrequency() {
        sendData(mSendGasDetectorPlusData.getHistorySaveFrequency());
    }

    /**
     * 离线数据存储频率(0x07)
     *
     * @param frequency 时间频率 (单位min)
     */
    public void setHistorySaveFrequency(int frequency) {
        sendData(mSendGasDetectorPlusData.setHistorySaveFrequency(frequency));
    }

    /**
     * 数据上报间隔(0x08)
     */
    public void getDataReportInterval() {
        sendData(mSendGasDetectorPlusData.getDataReportInterval());
    }

    /**
     * 数据上报间隔(0x08)
     *
     * @param interval 时间间隔 (单位min)
     */
    public void setDataReportInterval(int interval) {
        sendData(mSendGasDetectorPlusData.setDataReportInterval(interval));
    }

    /**
     * 气体信息(0x09)
     */
    public void getGasInfo() {
        sendData(mSendGasDetectorPlusData.getGasInfo());
    }


    /**
     * 报警频率(0x0A)
     */
    public void getAlarmFrequency() {
        sendData(mSendGasDetectorPlusData.getAlarmFrequency());
    }

    /**
     * 离线数据存储频率(0x07)
     *
     * @param interval 时间间隔 (单位min)
     */
    public void setAlarmFrequency(int interval) {
        sendData(mSendGasDetectorPlusData.setAlarmFrequency(interval));
    }

    /**
     * 报警的气体信息(0x0B)
     */
    public void getAlarmGas() {
        sendData(mSendGasDetectorPlusData.getAlarmGas());
    }


    /**
     * 设备电量信息(0x0C)
     */
    public void getPowerInfo() {
        sendData(mSendGasDetectorPlusData.getPowerInfo());
    }

    /**
     * 单位设置(0x0D)
     */
    public void getTypeUnit(int type) {
        sendData(mSendGasDetectorPlusData.getTypeUnit(type));
    }

    /**
     * 单位设置(0x0D)
     *
     * @param type 类型
     * @param unit 单位
     */
    public void setTypeUnit(int type, int unit) {
        sendData(mSendGasDetectorPlusData.setTypeUnit(type, unit));
    }

    /**
     * 同步时间(0x0E)
     *
     * @param seconds 时间(秒)
     */
    public void setSyncTime(long seconds) {
        sendData(mSendGasDetectorPlusData.setSyncTime(seconds));
    }

    /**
     * 发送数据
     *
     * @param data 数据
     */
    public void sendData(byte[] data) {
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
