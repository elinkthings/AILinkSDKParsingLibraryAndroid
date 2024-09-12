package cn.net.aicare.modulelibrary.module.bw05;

import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;

import java.nio.charset.StandardCharsets;

/**
 * @author ljl
 * on 2024/6/4
 */
public class Bw05WatchBleDevice extends BaseBleDeviceData implements OnBleOtherDataListener, OnBleMtuListener {

    private static Bw05WatchBleDevice bw05WatchBleDevice;

    private OnBw05WatchDataListener mListener;

    public Bw05WatchBleDevice(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setA7Encryption(false);
        bleDevice.setOnBleOtherDataListener(this);
        bleDevice.setOnBleMtuListener(this);
        bleDevice.setMtu(517);

    }

    @Override
    public void OnMtu(int mtu) {

    }

    public static void init(BleDevice bleDevice) {
        bw05WatchBleDevice = null;
        bw05WatchBleDevice = new Bw05WatchBleDevice(bleDevice);
    }


    public static Bw05WatchBleDevice getInstance() {
        return bw05WatchBleDevice;
    }

    public void setOnBw05WatchDataListener(OnBw05WatchDataListener listener) {
        mListener = listener;
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] data) {
        if (!uuid.equalsIgnoreCase(BleConfig.UUID_WRITE_AILINK.toString()) && !uuid.equalsIgnoreCase(BleConfig.UUID_NOTIFY_AILINK.toString())) {
            return;
        }

        if (Bw05WatchDataParseUtils.isLegalData(data)) {
            //IWDP
            String dataStr = new String(data, StandardCharsets.UTF_8);
            if (mListener != null) {
                mListener.onDataStr("收到：" + dataStr);
            }
            switch (Bw05WatchDataParseUtils.getDataType(data)) {
                case 0x00:
                    //设置时间成功
                    if (mListener != null) {
                        mListener.onDataStr("设置时间成功");
                    }
                    break;
                case 0x01:
                    //设置阈值成功
                    if (mListener != null) {
                        mListener.onDataStr("设置阈值成功");
                    }
                    break;
                case 0x02:
                    //设置检测间隔成功
                    if (mListener != null) {
                        mListener.onDataStr("设置检测间隔成功");
                    }
                    break;
                case 0x03:
                    //设置蓝牙名称成功
                    if (mListener != null) {
                        mListener.onDataStr("设置蓝牙名称成功");
                    }
                    break;
                case 0x05:
                    //手环回复查询信息
                    if (data.length < 31) {
                        return;
                    }
                    //心率值
                    int hr = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(7, 10));
                    //血氧值
                    int spo2 = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(10, 13));
                    //温度值
                    float temp = Bw05WatchDataParseUtils.parseData2Float(dataStr.substring(13, 17));
                    //当天步数
                    int curDayStep = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(17, 22));
                    //电量
                    int battery = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(22, 25));
                    //充电状态
                    int chargerState = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(25, 26));
                    //lora信号强度
                    int loraRssi = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(26, 30));

                    Bw05WatchDeviceInfo deviceInfo = new Bw05WatchDeviceInfo("--", hr, spo2, temp, curDayStep, battery, chargerState, loraRssi);
                    if (mListener != null) {
                        mListener.onDataStr(deviceInfo.toString());
                    }
                    break;
                case 0x06:
                    //手环本地存储数据（离线历史记录）
                    if (data.length < 10) {
                        return;
                    }
                    if (mListener != null) {
                        mListener.onDataStr(dataStr);
                    }
                    if (Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(7, 9)) > 0) {
                        int dataCount = Integer.parseInt(dataStr.substring(7, 9));
                        for (int i = 0; i < dataCount; i++) {
                            String timeStr = dataStr.substring(9 + 24 * i, 23 + 24 * i);
                            int dataHr = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(23 + 24 * i, 26 + 24 * i));
                            int dataSpo2 = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(26 + 24 * i, 29 + 24 * i));
                            float dataTemp = Bw05WatchDataParseUtils.parseData2Float(dataStr.substring(29 + 24 * i, 33 + 24 * i));
                            Bw05WatchOffLineInfo offLineInfo = new Bw05WatchOffLineInfo(timeStr, dataHr, dataSpo2, dataTemp);
                            if (mListener != null) {
                                mListener.onDataStr(offLineInfo.toString());
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        } else if (Bw05WatchDataParseUtils.isLegalDataOther(data)) {
            //IWCP
            switch (Bw05WatchDataParseUtils.getDataType(data)) {
                case 0x04:
                    //手环上传信息
                    if (data.length < 45) {
                        return;
                    }
                    String dataStr = new String(data, StandardCharsets.UTF_8);
                    //数据时间
                    String timeStr = dataStr.substring(7, 21);
                    //心率值
                    int hr = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(21, 24));
                    //血氧值
                    int spo2 = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(24, 27));
                    //温度值
                    float temp = Bw05WatchDataParseUtils.parseData2Float(dataStr.substring(27, 31));
                    //当天步数
                    int curDayStep = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(31, 36));
                    //电量
                    int battery = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(36, 39));
                    //充电状态
                    int chargerState = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(39, 40));
                    //lora信号强度
                    int loraRssi = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(40, 44));

                    Bw05WatchDeviceInfo deviceInfo = new Bw05WatchDeviceInfo(timeStr, hr, spo2, temp, curDayStep, battery, chargerState, loraRssi);
                    if (mListener != null) {
                        mListener.onDataStr(deviceInfo.toString());
                    }
                    replayDeviceInfo();
                    break;
                default:
                    break;
            }
        }
    }


    /*------------------------------------------指令---------------------------------------------------*/

    private void sendCmdData(String cmdStr) {
        byte[] bytes = cmdStr.getBytes(StandardCharsets.UTF_8);
        SendDataBean dataBean = new SendDataBean(bytes, BleConfig.UUID_WRITE_AILINK, 2, BleConfig.UUID_SERVER_AILINK);
        sendData(dataBean);
        if (mListener != null) {
            mListener.onDataStr("发送指令" + cmdStr);
        }
    }

    /**
     * APP设置时间
     */
    public void setTime() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_SET_TIME + Bw05WatchUtils.getUTCTimeStr() + Bw05WatchUtils.getCurTimeZone() + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 设置阈值
     *
     * @param payLoadStr 这是处理后的阈值数据
     */
    public void setRangeValue(String payLoadStr) {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_SET_RANGE + payLoadStr + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 设置检测时间间隔
     *
     * @param payLoadStr
     */
    public void setCheckTime(String payLoadStr) {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_SET_CHECK_TIME + payLoadStr + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 设置蓝牙名称
     *
     * @param belNameStr
     */
    public void setBleName(String belNameStr) {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_SET_BLE_NAME + belNameStr + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 查询设备信息
     */
    public void queryInfo() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_QUERY_INFO + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 查询阈值
     */
    public void queryThreshold() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_QUERY_THRESHOLD + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * APP响应手环上传信息
     */
    public void replayDeviceInfo() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_REPLAY_INFO + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }


}
