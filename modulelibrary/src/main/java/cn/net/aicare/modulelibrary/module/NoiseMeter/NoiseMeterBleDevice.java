package cn.net.aicare.modulelibrary.module.NoiseMeter;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

/**
 * 噪音计ble设备
 *
 * @author xing
 * @date 2022/11/15
 */
public class NoiseMeterBleDevice extends BaseBleDeviceData {


    private static NoiseMeterBleDevice sNoiseMeter;

    public final static int DEVICE_CID = 0x0043;

    private OnNoiseDataListener mOnNoiseDataListener;


    public interface OnNoiseDataListener {
        /**
         * @param noiseData 噪音数据
         * @param over
         * @param under
         * @param fs
         * @param ac
         * @param backlight
         * @param max
         * @param lowBat
         */
        void onBleData(int noiseData, int over, int under, int fs, int ac, int backlight, int max, int lowBat, int range);
    }

    public void setOnNoiseDataListener(OnNoiseDataListener onNoiseDataListener) {
        mOnNoiseDataListener = onNoiseDataListener;
    }


    private NoiseMeterBleDevice(BleDevice bleDevice) {
        super(bleDevice);
    }


    public static void init(BleDevice bleDevice) {
        sNoiseMeter = null;
        sNoiseMeter = new NoiseMeterBleDevice(bleDevice);

    }


    public static NoiseMeterBleDevice getInstance() {
        return sNoiseMeter;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {

    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type != DEVICE_CID) {
            BleLog.e("CID不正确");
            return;
        }

        String data = BleStrUtils.byte2HexStr(hex);
        BleLog.i("接收到的Playload数据:" + data);
        //数据解析
        if (hex.length >= 4) {
            //前两位是噪音数据
            int noiseData = (((hex[0] & 0xff) << 8) + ((hex[1] & 0xff)));
            //OVER指示标志 1 OVER
            int overState = (hex[2] >> 1) & 0x01;
            //UNDER状态
            int underState = (hex[2] >> 2) & 0x01;
            //F/S模式 0->Fast 1->Slow
            int fsState = (hex[2] >> 3) & 0x01;
            //A/C加权选择 0->A 1->C
            int acState = (hex[2] >> 4) & 0x01;
            //背光 0->disable 1->enable
            int backLightState = (hex[2] >> 5) & 0x01;
            //最大值
            int maxState = (hex[2] >> 6) & 0x01;
            //低电状态
            int lowBatState = (hex[2] >> 7) & 0x01;

            //量程状态
            //Range 0->30~130 1->30~80 2->50~100 3->60~110 4->80~130
            int rangeState = (hex[3] >> 5) & 0x07;

            if (mOnNoiseDataListener != null) {
                mOnNoiseDataListener.onBleData(noiseData, overState, underState, fsState, acState, backLightState, maxState, lowBatState, rangeState);
            }

        }
    }


    /**
     * A/C键状态
     */
    public void sendAC() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x00;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x43, bytes);
        sendData(sendMcuBean);
    }

    /**
     * LIGHT
     */
    public void sendLight() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x20;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x43, bytes);
        sendData(sendMcuBean);
    }

    /**
     * F/S
     */
    public void sendFS() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x40;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x43, bytes);
        sendData(sendMcuBean);
    }

    /**
     * UP
     */
    public void sendUp() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x80;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x43, bytes);
        sendData(sendMcuBean);
    }

    /**
     * MAX
     */
    public void sendMax() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x60;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x43, bytes);
        sendData(sendMcuBean);
    }

    /**
     * DOWN
     */
    public void sendDown() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0xA0;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x43, bytes);
        sendData(sendMcuBean);
    }

}
