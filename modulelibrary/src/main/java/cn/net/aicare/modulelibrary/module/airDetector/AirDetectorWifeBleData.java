package cn.net.aicare.modulelibrary.module.airDetector;

import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleConnectStatus;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnWifiInfoListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

public class AirDetectorWifeBleData extends BaseBleDeviceData implements OnBleOtherDataListener{

    private static final String TAG = "AirDetectorBleData";

    /**
     * 设备返回功能
     */
    public final static int SUPPORT_LIST = 0x02;

    /**
     * 设备状态
     */
    public final static int DEVICE_STATE = 0x04;

    /**
     * 参数功能
     */
    public final static int PARAMETERS = 0x06;

    private static AirDetectorWifeBleData mAirDetectorWifeBleData;

    private AirParseUtil mAirParseUtil;


    private AirInterface mAirInterface;

    private ArrayMap<String, AirDataInterface> mDataInterfaceList = new ArrayMap<>();
    private String curKey;

    private OnBleOtherDataListener mOnBleOtherDataListener;

    public void setOnBleOtherDataListener(OnBleOtherDataListener onBleOtherDataListener) {
        mOnBleOtherDataListener = onBleOtherDataListener;
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] hex) {
        if (mOnBleOtherDataListener!=null) {
            mOnBleOtherDataListener.onNotifyOtherData(uuid,hex);
        }
    }

    public void setCurKey(String key){
        this.curKey = key;
    }

    private AirDetectorWifeBleData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleOtherDataListener(this);
        bleDevice.setOnWifiInfoListener(new OnWifiInfoListener() {
            @Override
            public void onWifiListInfo(int no, String mac, int db, int type, int wifiStatus) {
                if (mAirInterface != null) {
                    mAirInterface.onWiFiMac(no, mac);
                }
            }

            @Override
            public void onWifiListName(int no, String name) {
                if (mAirInterface != null) {
                    mAirInterface.onWiFiName(no, name);
                }

            }

            @Override
            public void onSetWifiNameOrPawOrConnectCallback(int type, int status) {
                if (mAirInterface != null) {
                    mAirInterface.onWiFiState(type, status);
                }

            }


        });

        bleDevice.setOnBleConnectListener(new OnBleConnectStatus() {
            @Override
            public void onBleConnectStatus(int bleStatus, int wifiStatus, int workStatus) {
                if (mAirInterface != null) {
                    mAirInterface.onWiFiConnectState(wifiStatus);
                }
            }
        });
    }

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
    }

    public void setAirInterface(AirInterface airInterface) {
        mAirInterface = airInterface;
    }


    public void setDataInterface(String key,AirDataInterface dataInterface){
        mDataInterfaceList.put(key, dataInterface);
        this.curKey = key;
    }


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type != AirConst.NO_MQTT_DEVICE_CID && type != AirConst.MQTT_DEVICE_CID) {
            BleLog.e("CID不正确");
            return;
        }
        if (mAirParseUtil == null) {
            mAirParseUtil = new AirParseUtil();
        }
        AirDataInterface dataInterface = mDataInterfaceList.get(curKey);
        BleLog.i("接收到的数据:" + BleStrUtils.byte2HexStr(hex));
        int cmd = hex[0] & 0xff;
        List<BleAirTLVBean> tlv;
        try {
            tlv = AirParseUtil.getTLV(cmd, hex);
        } catch (Exception e) {
            tlv = new ArrayList<>();
            notifyError(uuid, hex, e);
        }
        switch (cmd) {
            case SUPPORT_LIST:
                SparseArray<SupportBean> supportList;
                try {
                    supportList = AirParseUtil.parseCmd02(tlv);
                } catch (Exception e) {
                    notifyError(uuid, hex, e);
                    return;
                }
                if (dataInterface != null) {
                    dataInterface.onSupportedList(supportList);
                }
                break;
            case DEVICE_STATE:
                SparseArray<StatusBean> statusList;
                try {
                    statusList = AirParseUtil.parseCmd04(tlv);
                } catch (Exception e) {
                    notifyError(uuid, hex, e);
                    return;
                }
                if (dataInterface != null) {
                    dataInterface.onStatusList(statusList);
                }
                break;
            case PARAMETERS:
                SparseArray<StatusBean> settingList;
                try {
                    settingList = AirParseUtil.parseCmd06(tlv);
                } catch (Exception e) {
                    notifyError(uuid, hex, e);
                    return;
                }
                if (dataInterface != null) {
                    dataInterface.onSettingList(settingList);
                }
                break;
            default:
                break;
        }
    }

    private void notifyError(String uuid, byte[] hex, Exception e) {
        if (mOnBleOtherDataListener != null) {
            mOnBleOtherDataListener.onNotifyOtherData(uuid, hex);
        }
        e.printStackTrace();
    }

    public static void init(BleDevice bleDevice) {
        mAirDetectorWifeBleData = new AirDetectorWifeBleData(bleDevice);
    }

    public static AirDetectorWifeBleData getInstance() {
        return mAirDetectorWifeBleData;
    }

    public interface AirDataInterface{
        /**
         * 支持功能列表
         *
         * @param supportList supportList
         */
        void onSupportedList(SparseArray<SupportBean> supportList);

        /**
         * 状态列表
         *
         * @param statusList statusList
         */
        void onStatusList(SparseArray<StatusBean> statusList);

        /**
         * 设置列表
         *
         * @param settingList settingList
         */
        void onSettingList(SparseArray<StatusBean> settingList);
    }


    public interface AirInterface {

        void onWiFiName(int no, String name);

        void onWiFiConnectState(int state);

        void onWiFiState(int type, int state);

        void onWiFiMac(int no, String mac);

        void onDataPayload(String bytes);
    }

    @Override
    public void sendData(SendDataBean sendDataBean) {
        super.sendData(sendDataBean);
        Log.i(TAG, "发："+ BleStrUtils.byte2HexStr(sendDataBean.getHex()));
    }
}
