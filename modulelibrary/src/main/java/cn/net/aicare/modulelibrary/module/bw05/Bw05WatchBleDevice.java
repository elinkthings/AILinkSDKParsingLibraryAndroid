package cn.net.aicare.modulelibrary.module.bw05;

import android.text.TextUtils;

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
                    if (data.length < 57) {
                        return;
                    }

                    //mac地址
                    String mac = dataStr.substring(7, 19);
                    char[] charArray = mac.toCharArray();

                    StringBuilder macBuilder = new StringBuilder();
                    for (int i = charArray.length - 1; i >= 0; i--) {
                        System.out.println(" i is " + i + "   " + charArray[i]);
                        if (i % 2 == 0 && i != 0) {
                            macBuilder.append(charArray[i]).append(":");
                        } else {
                            macBuilder.append(charArray[i]);
                        }
                    }
                    //时间
                    String time = dataStr.substring(19, 33);

                    //心率值
                    int hr = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(33, 36));
                    //血氧值
                    int spo2 = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(36, 39));
                    //温度值
                    float temp = Bw05WatchDataParseUtils.parseData2Float(dataStr.substring(39, 43));
                    //当天步数
                    int curDayStep = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(43, 48));
                    //电量
                    int battery = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(48, 51));
                    //充电状态
                    int chargerState = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(51, 52));
                    //lora信号强度
                    int loraRssi = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(52, 56));

                    Bw05WatchDeviceInfo deviceInfo = new Bw05WatchDeviceInfo(macBuilder.toString(), time, hr, spo2, temp, curDayStep, battery, chargerState, loraRssi);
                    if (mListener != null) {
                        mListener.onDataStr(deviceInfo.toString());
                    }
                    break;
                case 0x06:
                    //手环本地存储数据（离线历史记录）
                    if (data.length < 22) {
                        return;
                    }
                    if (mListener != null) {
                        mListener.onDataStr(dataStr);
                    }
                    if (data.length < 51) {
                        return;
                    }
                    if (Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(19, 21)) > 0) {
                        String substring = dataStr.substring(21).replace("#", "");
                        String[] split = substring.split("-");
                        for (String dataS : split) {
                            String timeStr = dataS.substring(0, 14);
                            int dataHr = Bw05WatchDataParseUtils.parseData2Int(substring.substring(14, 17));
                            int dataSpo2 = Bw05WatchDataParseUtils.parseData2Int(substring.substring(17, 20));
                            float dataTemp = Bw05WatchDataParseUtils.parseData2Float(substring.substring(20, 24));
                            int step = Bw05WatchDataParseUtils.parseData2Int(substring.substring(24, 29));
                            Bw05WatchOffLineInfo offLineInfo = new Bw05WatchOffLineInfo(timeStr, dataHr, dataSpo2, dataTemp, step);
                            if (mListener != null) {
                                mListener.onDataStr(offLineInfo.toString());
                            }
                        }
                    }
                    break;
                case 0x07:
                    //查询、数值主题
                    if (data.length < 21) {
                        return;
                    }
                    int result = Integer.parseInt(dataStr.substring(dataStr.length() - 2, dataStr.length() - 1));
                    if (result == 0) {
                        if (mListener != null) {
                            mListener.onDataStr("不支持UI显示");
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onDataStr("当前主题编号：" + result);
                        }
                    }
                    break;
                case 0x08:
                    if (data.length < 10) {
                        return;
                    }
                    String verSionStr = dataStr.substring(7, dataStr.length() - 2);
                    if (mListener != null) {
                        mListener.onDataStr("设备版本号：" + verSionStr);
                    }
                    break;
                case 0x09:
                    //进入绑定模式
                    if (data.length < 20) {
                        return;
                    }
                    if (mListener != null) {
                        mListener.onDataStr("进入绑定模式");
                    }
                    break;
                case 0x10:
                    //硬件状态
                    if (data.length == 9) {
                        String state = dataStr.substring(dataStr.length() - 2, dataStr.length() - 1);
                        if ("2".equals(state)) {
                            if (mListener != null) {
                                mListener.onDataStr("初始化未完成");
                            }
                        }
                    }

                    if (data.length < 14) {
                        return;
                    }
                    String stepState = dataStr.substring(7, 8);
                    String sensorState = dataStr.substring(8, 9);
                    String t117bState = dataStr.substring(9, 10);
                    String t117State = dataStr.substring(10, 11);
                    String loraState = dataStr.substring(11, 12);
                    String touchState = dataStr.substring(12, 13);

                    if (mListener != null) {
                        mListener.onDataStr("计步器："+judgeState(stepState)+"   健康传感器："+judgeState(sensorState)
                                +"   温度T117B："+judgeState(t117bState)+"   温度T117："+judgeState(t117State)
                                +"   lora："+judgeState(loraState)+"   触摸："+judgeState(touchState));
                    }

                    break;
                case 0x13:
                    //设置历史上传间隔成功
                    if (mListener != null) {
                        mListener.onDataStr("设置历史上传间隔成功");
                    }
                    break;
                default:
                    break;
            }
        } else if (Bw05WatchDataParseUtils.isLegalDataOther(data)) {
            String dataStr = new String(data, StandardCharsets.UTF_8);
            if (mListener != null) {
                mListener.onDataStr("收到：" + dataStr);
            }
            //IWCP
            switch (Bw05WatchDataParseUtils.getDataType(data)) {
                case 0x04:
                    //手环上传信息
                    if (data.length < 57) {
                        return;
                    }
                    //mac地址
                    String mac = dataStr.substring(7, 19);
                    char[] charArray = mac.toCharArray();

                    StringBuilder macBuilder = new StringBuilder();
                    for (int i = charArray.length - 1; i >= 0; i--) {
                        System.out.println(" i is " + i + "   " + charArray[i]);
                        if (i % 2 == 0 && i != 0) {
                            macBuilder.append(charArray[i]).append(":");
                        } else {
                            macBuilder.append(charArray[i]);
                        }
                    }
                    //时间
                    String time = dataStr.substring(19, 33);

                    //心率值
                    int hr = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(33, 36));
                    //血氧值
                    int spo2 = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(36, 39));
                    //温度值
                    float temp = Bw05WatchDataParseUtils.parseData2Float(dataStr.substring(39, 43));
                    //当天步数
                    int curDayStep = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(43, 48));
                    //电量
                    int battery = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(48, 51));
                    //充电状态
                    int chargerState = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(51, 52));
                    //lora信号强度
                    int loraRssi = Bw05WatchDataParseUtils.parseData2Int(dataStr.substring(52, 56));

                    Bw05WatchDeviceInfo deviceInfo = new Bw05WatchDeviceInfo(macBuilder.toString(), time, hr, spo2, temp, curDayStep, battery, chargerState, loraRssi);
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

    private String judgeState(String state) {
        if (TextUtils.isEmpty(state)) {
            return "不正常";
        } else {
            if ("1".equals(state)) {
                return "正常";
            } else {
                return "不正常";
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
        cmdStr = Bw05WatchCMDConfig.APP_SET_TIME + Bw05WatchUtils.getUTCTimeStr() + Bw05WatchUtils.getCurTimeZoneStr() + Bw05WatchCMDConfig.BW_05_END_ID;
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
     * 设置历史上传间隔
     *
     * @param payLoadStr
     */
    public void setHisTime(String payLoadStr) {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_SET_HIS_TIME + payLoadStr + Bw05WatchCMDConfig.BW_05_END_ID;
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

    /**
     * 查询UI主题
     */
    public void queryTheme() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_QUERY_SET_THEME + "0" + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 查询UI主题
     */
    public void setTheme(String themeNum) {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_QUERY_SET_THEME + themeNum + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 查询设备版本号
     */
    public void queryVersion() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_QUERY_VERSION + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 设置手环进入绑定模式
     */
    public void setBindMode() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_SET_BINDING_MODE + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }

    /**
     * 查询硬件状态
     */
    public void queryHardware() {
        String cmdStr;
        cmdStr = Bw05WatchCMDConfig.APP_QUERY_HARDWARE_STATE + Bw05WatchCMDConfig.BW_05_END_ID;
        sendCmdData(cmdStr);
    }
}
