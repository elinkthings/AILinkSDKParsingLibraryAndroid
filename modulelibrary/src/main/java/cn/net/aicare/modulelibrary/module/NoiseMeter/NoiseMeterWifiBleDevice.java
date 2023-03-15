package cn.net.aicare.modulelibrary.module.NoiseMeter;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 噪音计ble设备
 *
 * @author xing
 * @date 2022/11/15
 */
public class NoiseMeterWifiBleDevice extends BaseBleDeviceData implements OnBleOtherDataListener {


    private static NoiseMeterWifiBleDevice sNoiseMeter;

    public final static int DEVICE_CID = 0x0050;
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

    /**
     * 噪声解析工具类
     */
    private NoiseParseUtils mNoiseParseUtils;

    private NoiseParseUtils.OnDeviceSupportList mOnDeviceSupportList;
    private NoiseParseUtils.OnSynDeviceState mOnSynDeviceState;
    private NoiseParseUtils.OnSetDeviceInfo mOnSetDeviceInfo;
    private OnBleOtherDataListener mOnBleOtherDataListener;

    public void setOnBleOtherDataListener(OnBleOtherDataListener onBleOtherDataListener) {
        mOnBleOtherDataListener = onBleOtherDataListener;
    }

    private NoiseMeterWifiBleDevice(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleOtherDataListener(this);
    }


    public static void init(BleDevice bleDevice) {
        sNoiseMeter = null;
        sNoiseMeter = new NoiseMeterWifiBleDevice(bleDevice);

    }


    public static NoiseMeterWifiBleDevice getInstance() {
        return sNoiseMeter;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {

    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] hex) {
        if (mOnBleOtherDataListener!=null) {
            mOnBleOtherDataListener.onNotifyOtherData(uuid,hex);
        }
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type != DEVICE_CID) {
            BleLog.e("CID不正确");
            return;
        }
        if (mNoiseParseUtils == null) {
            mNoiseParseUtils = new NoiseParseUtils();
        }
        String data = BleStrUtils.byte2HexStr(hex);
        BleLog.i("接收到的Playload数据:" + data);
        int cmd = hex[0] & 0xff;
        List<BleNoiseTLVBean> tlv;
        try {
            tlv = BleNoiseTLVUtils.getTLV(cmd, hex);
        } catch (Exception e) {
            tlv = new ArrayList<>();
            BleLog.i("数据解析异常:" + data);
            if (mOnBleOtherDataListener!=null) {
                mOnBleOtherDataListener.onNotifyOtherData(uuid,hex);
            }
            e.printStackTrace();
        }

        switch (cmd) {
            case SUPPORT_LIST:
                //0x01； 02
                for (BleNoiseTLVBean bleNoiseTLVBean : tlv) {
                    mNoiseParseUtils.toParseCmd(bleNoiseTLVBean, mOnDeviceSupportList);
                }
                break;
            case DEVICE_STATE:
                //0x01； 04
                for (BleNoiseTLVBean bleNoiseTLVBean : tlv) {
                    mNoiseParseUtils.toParseCmd(bleNoiseTLVBean, mOnSynDeviceState);
                }
                break;
            case PARAMETERS:
                //0x01；
                for (BleNoiseTLVBean bleNoiseTLVBean : tlv) {

                    mNoiseParseUtils.toParseCmd(bleNoiseTLVBean, mOnSetDeviceInfo);
                }
                break;
            default:

        }
    }


    /**
     * 发送单个TLV数据
     *
     * @param bleNoiseTLVBean TLV数据
     */
    public void sendTLVData(BleNoiseTLVBean bleNoiseTLVBean) {
        int cmd = bleNoiseTLVBean.getCmd();
        int op = bleNoiseTLVBean.getOp();
        int length = bleNoiseTLVBean.getLength();
        byte[] value = bleNoiseTLVBean.getValue();
        byte type = bleNoiseTLVBean.getType();
        //GTLV=1+1+1+N
        byte[] bytes;
        if (length == 0) {
            bytes = new byte[2];
            bytes[0] = (byte) cmd;
            bytes[1] = (byte) type;
        } else {
            bytes = new byte[length + 5];
            bytes[0] = (byte) cmd;
            if (op >= 0) {
                bytes[1] = (byte) op;
            }
            bytes[2] = (byte) 0x01;
            bytes[3] = (byte) type;
            bytes[4] = (byte) length;
            System.arraycopy(value, 0, bytes, 5, value.length);
        }

        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(DEVICE_CID, bytes);
        sendData(sendMcuBean);
    }


    public void setOnDeviceSupportList(NoiseParseUtils.OnDeviceSupportList onDeviceSupportList) {
        mOnDeviceSupportList = onDeviceSupportList;
    }

    public void setOnSynDeviceState(NoiseParseUtils.OnSynDeviceState onSynDeviceState) {
        mOnSynDeviceState = onSynDeviceState;
    }

    public void setOnSetDeviceInfo(NoiseParseUtils.OnSetDeviceInfo onSetDeviceInfo) {
        mOnSetDeviceInfo = onSetDeviceInfo;
    }
}
