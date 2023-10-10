package cn.net.aicare.modulelibrary.module.meatprobe;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 探针管理者
 *
 * @author xing
 * @date 2023/05/11
 */
public class MeatProbeBleManage implements OnCallbackBle {

    private Map<String, MeatProbeBleData> mBleMap;
    private Map<String, Set<OnMeatProbeDataListener>> mListenerMap;
    private static MeatProbeBleManage instance;

    private MeatProbeBleManage() {
        mBleMap = new HashMap<>();
        mListenerMap = new HashMap<>();
        CallbackDisIm.getInstance().addListListener(this);
    }

    public static MeatProbeBleManage getInstance() {
        if (instance == null) {
            synchronized (MeatProbeBleManage.class) {
                if (instance == null) {
                    instance = new MeatProbeBleManage();
                }
            }
        }
        return instance;
    }


    public MeatProbeBleData getMeatProbeBleData(String mac) {
        return mBleMap.get(mac);
    }

    public MeatProbeBleData getMeatProbeBleData(BleDevice bleDevice) {
        String mac = bleDevice.getMac();
        MeatProbeBleData meatProbeBleData = mBleMap.get(mac);
        if (meatProbeBleData != null) {
            if (meatProbeBleData.getBleDevice() == bleDevice) {
                return meatProbeBleData;
            }
        }
        meatProbeBleData = new MeatProbeBleData(bleDevice);
        mBleMap.put(mac, meatProbeBleData);
        Set<OnMeatProbeDataListener> onMeatProbeDataListeners = mListenerMap.get(mac);
        if (onMeatProbeDataListeners != null) {
            for (OnMeatProbeDataListener onMeatProbeDataListener : onMeatProbeDataListeners) {
                meatProbeBleData.addOnMeatProbeDataListener(onMeatProbeDataListener);
            }
        }
        return meatProbeBleData;
    }


    public Set<String> getConnectDeviceMac() {
        return mBleMap.keySet();
    }


    /**
     * 添加侦听器,未连接成功都可以添加
     *
     * @param mac                     mac
     * @param onMeatProbeDataListener 肉类探测数据监听器
     */
    public void addOnMeatProbeDataListener(String mac, OnMeatProbeDataListener onMeatProbeDataListener) {
        Set<OnMeatProbeDataListener> onMeatProbeDataListeners = mListenerMap.get(mac);
        if (onMeatProbeDataListeners == null) {
            onMeatProbeDataListeners = new HashSet<>();
        }
        onMeatProbeDataListeners.add(onMeatProbeDataListener);
        mListenerMap.put(mac, onMeatProbeDataListeners);
        MeatProbeBleData meatProbeBleData = mBleMap.get(mac);
        if (meatProbeBleData != null) {
            for (OnMeatProbeDataListener meatProbeDataListener : onMeatProbeDataListeners) {
                meatProbeBleData.addOnMeatProbeDataListener(meatProbeDataListener);
            }
        }
    }

    /**
     * 去除侦听器,同时会去除已连接的监听
     *
     * @param mac                     mac
     * @param onMeatProbeDataListener 肉类探测数据监听器
     */
    public void removeOnMeatProbeDataListener(String mac, OnMeatProbeDataListener onMeatProbeDataListener) {
        Set<OnMeatProbeDataListener> onMeatProbeDataListeners = mListenerMap.get(mac);
        MeatProbeBleData meatProbeBleData = mBleMap.get(mac);
        if (meatProbeBleData !=null) {
            meatProbeBleData.removeOnMeatProbeDataListener(onMeatProbeDataListener);
        }
        if (onMeatProbeDataListeners != null) {
            onMeatProbeDataListeners.remove(onMeatProbeDataListener);
        }
    }



    /**
     * 发送单位
     */
    public void sendSwitchUnit(int unitType) {
        if (mBleMap != null) {
            Collection<MeatProbeBleData> values = mBleMap.values();
            for (MeatProbeBleData value : values) {
                value.sendSwitchUnit(unitType);
            }
        }
    }



    @Override
    public void bleClose() {
        mBleMap.clear();
    }

    @Override
    public void onDisConnected(String mac, int code) {
        mBleMap.remove(mac);
    }

    public void clear() {
        CallbackDisIm.getInstance().removeListener(this);
        mBleMap.clear();
        mListenerMap.clear();
    }

}
