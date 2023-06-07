package cn.net.aicare.modulelibrary.module.meatprobe;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.HashSet;
import java.util.Set;


/**
 * 食物探针解析类
 *
 * @author ljl
 * @date 2022/12/22
 */
public class MeatProbeBleData extends BaseBleDeviceData implements OnMcuParameterListener, OnBleMtuListener {
    public final static int DEVICE_CID = FoodConfig.DEVICE_CID;

    private final static int GET_BATTERY_STATUS_KEY = 1;

    private int mGetBatteryInfoTime = 5 * 60 * 1000;

    private static MeatProbeBleData mInstance;

    private Set<OnMeatProbeDataListener> mListeners;

    private BleDevice mBleDevice;
    private String mMac;
    private MeatProbeReturnCmdUtil mMeatProbeReturnCmdUtil;

    @Override
    public void OnMtu(int i) {
    }

    public void addOnMeatProbeDataListener(OnMeatProbeDataListener onMeatProbeDataListener) {
        if (mListeners != null) {
            mListeners.add(onMeatProbeDataListener);
        }
    }

    public void removeOnMeatProbeDataListener(OnMeatProbeDataListener onMeatProbeDataListener) {
        if (mListeners != null) {
            mListeners.remove(onMeatProbeDataListener);
        }
    }


    public MeatProbeBleData(BleDevice bleDevice) {
        super(bleDevice);
        mMac = bleDevice.getMac();
        bleDevice.setOnMcuParameterListener(this);
        bleDevice.setOnBleMtuListener(this);
        bleDevice.setMtu(512);
        getBattery();
        getVersionInfo();
        mBleDevice = bleDevice;
        mListeners = new HashSet<>();
        mMeatProbeReturnCmdUtil = new MeatProbeReturnCmdUtil(mMac);
    }

    public BleDevice getBleDevice() {
        return mBleDevice;
    }


    public static MeatProbeBleData getInstance() {
        return mInstance;
    }

    public static void init(BleDevice bleDevice) {
        if (getInstance() != null) {
            if (getInstance().mBleDevice == bleDevice) {
                return;
            }
        }
        mInstance = new MeatProbeBleData(bleDevice);
    }

    @Override
    public void onDisConnected() {
        super.onDisConnected();
        mBleDevice = null;
        mInstance = null;
    }

    public void onDestroy() {
        mListeners.clear();
        mListeners = null;
        if (mHandler != null) {
            mHandler.removeMessages(GET_BATTERY_STATUS_KEY);
        }
    }

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {

    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type != DEVICE_CID) {
            return;
        }
        mMeatProbeReturnCmdUtil.onBleDataA7(hex, mListeners);
    }


    @Override
    public void onNotifyDataA6(String uuid, byte[] hex) {
        super.onNotifyDataA6(uuid, hex);
        BleLog.i("收到的A6数据:" + BleStrUtils.byte2HexStr(hex));
        mMeatProbeReturnCmdUtil.onBleDataA6(hex, mListeners);

    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_BATTERY_STATUS_KEY) {
                //定时获取电量,5分钟一次
                getBattery();
                mHandler.removeMessages(GET_BATTERY_STATUS_KEY);
            }
        }
    };

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        BleLog.i("当前电量:" + battery);
        if (mListeners != null) {
            for (OnMeatProbeDataListener listener : mListeners) {
                listener.onBatteryState(mMac, status, battery);
            }
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_BATTERY_STATUS_KEY, mGetBatteryInfoTime);
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

    //--------------------------命令------------------------

    /**
     * 获取到电量
     */
    public void getBattery() {
        sendData(new SendBleBean(BleSendCmdUtil.getInstance().getMcuBatteryStatus()));
    }

    /**
     * 切换单位
     */
    public void sendSwitchUnit(int unitType) {
        sendData(MeatProbeSendCmdUtil.getInstance().sendSwitchUnit(unitType));
    }

    /**
     * 获取模块版本信息
     */
    public void getVersionInfo() {
        sendData(MeatProbeSendCmdUtil.getInstance().getVersionInfo());
    }

    /**
     * app获取设备信息
     */
    public void appGetDeviceInfo() {
        BleLog.i("app获取设备信息:" + mMac);
        sendData(MeatProbeSendCmdUtil.getInstance().appGetDeviceInfo());
    }

    /**
     * 结束探针工作
     */
    public void endWork() {
        sendData(MeatProbeSendCmdUtil.getInstance().endWork());
    }


    public void appSetDeviceInfo(ProbeBean probeBean) {
        BleLog.i("设置自定义信息:" + probeBean.toString());
        SendDataBean sendDataBean = MeatProbeSendCmdUtil.getInstance().appSetDeviceInfo(probeBean);
        sendData(sendDataBean);
    }


    /**
     * @param cookingId    烧烤id 选择食物类型时间戳
     * @param foodType     食物类型
     * @param foodRawness  食物熟度
     * @param targetTempC  目标温度C
     * @param targetTempF  目标温度F
     * @param ambientMinC  环境范围温度下限C
     * @param ambientMinF  环境范围温度下限F
     * @param ambientMaxC  环境范围温度上限C
     * @param ambientMaxF  环境范围温度上限F
     * @param alarmPercent 提醒温度对目标温度百分比
     * @param startTime    开始计时时间戳
     * @param endTime      结束计时时间戳
     * @param currentUnit  当前单位
     */
    public void appSetDeviceInfo(long cookingId, int foodType, int foodRawness, int targetTempC, int targetTempF, int ambientMinC, int ambientMinF, int ambientMaxC, int ambientMaxF,
                                 double alarmPercent, long startTime, long endTime, int currentUnit) {
        SendDataBean sendDataBean = MeatProbeSendCmdUtil.getInstance()
                .appSetDeviceInfo(cookingId, foodType, foodRawness, targetTempC, targetTempF, ambientMinC, ambientMinF, ambientMaxC, ambientMaxF, alarmPercent, startTime, endTime, currentUnit);
        sendData(sendDataBean);

    }


}
