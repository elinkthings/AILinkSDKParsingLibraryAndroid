package cn.net.aicare.modulelibrary.module.TempHumidity;

import android.bluetooth.BluetoothGatt;
import android.os.Build;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.Locale;

public class TempHumidityBleUtils extends BaseBleDeviceData {

    private static TempHumidityBleUtils mTempHumidityBleUtils;
    private BleDataCallBack bleDataCallBack;


    private TempHumidityBleUtils(BleDevice bleDevice) {
        super(bleDevice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleDevice.setConnectPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        }

    }


    @Override
    public void onNotifyData(byte[] bytes, int type) {
        BleLog.e("接收到的数据" + BleStrUtils.byte2HexStr(bytes));
        int status = bytes[0] & 0xff;
        switch (status) {
            case 0x02:
                int battery = bytes[1] & 0xff;
                long startUp1 = (bytes[5] & 0xffL) << 24;
                int startUp2 = (bytes[4] & 0xff) << 16;
                int startUp3 = (bytes[3] & 0xff) << 8;
                int startUp4 = (bytes[2] & 0xff);
                int symbol = (bytes[7] & 0x80) >> 7;

                int tempH = (bytes[7] & 0x7f) << 8;
                int tempL = (bytes[6] & 0xFf);
                float tempFloat = (tempH + tempL) / 10f;
                if (symbol == 1) {
                    tempFloat = 0 - tempFloat;
                }
                int humidityH = (bytes[9] & 0xff) << 8;
                int humidityL = (bytes[8] & 0xff);
                if (bleDataCallBack != null) {
                    bleDataCallBack.onDeviceStatus(battery, startUp1 + startUp2 + startUp3 + startUp4,
                            Float.parseFloat(String.format(Locale.US, "%.1f", tempFloat)),
                            Float.parseFloat(String.format(Locale.US, "%.1f", (humidityH + humidityL) / 10f)));
                }
                break;

            case 0x06:
                offLineRecord(bytes);
                break;
        }
    }

    public static TempHumidityBleUtils getInstance() {
        return mTempHumidityBleUtils;
    }

    public void setBleDataCallBack(BleDataCallBack bleDataCallBack) {
        this.bleDataCallBack = bleDataCallBack;
    }

    public static void init(BleDevice bleDevice) {
        mTempHumidityBleUtils = null;
        mTempHumidityBleUtils = new TempHumidityBleUtils(bleDevice);
    }

    private void offLineRecord(byte[] bytes) {
        long total1 = (bytes[4] & 0xffL) << 24;
        int total2 = (bytes[3] & 0xff) << 16;
        int total3 = (bytes[2] & 0xff) << 8;
        int total4 = (bytes[1] & 0xff);
        long sendNum1 = (bytes[8] & 0xffL) << 24;
        int sendNum2 = (bytes[7] & 0xff) << 16;
        int sendNum3 = (bytes[6] & 0xff) << 8;
        int sendNum4 = (bytes[5] & 0xff);

        byte[] historyByte = new byte[bytes.length - 9];
        System.arraycopy(bytes, 9, historyByte, 0, historyByte.length);
        BleLog.e("接收到的数据历史" + BleStrUtils.byte2HexStr(historyByte));

        for (int i = 0; i < historyByte.length / 8; i++) {

            long upTime1 = (historyByte[3 + i * 8] & 0xffL) << 24;
            int upTime2 = (historyByte[2 + i * 8] & 0xff) << 16;
            int upTime3 = (historyByte[1 + i * 8] & 0xff) << 8;
            int upTime4 = (historyByte[0 + i * 8] & 0xff);
            int symbol = (historyByte[5 + i * 8] & 0x80) >> 7;
            int tempH = (historyByte[5 + i * 8] & 0x7f) << 8;
            int tempL = (historyByte[4 + i * 8] & 0xff);
            int humidityH = (historyByte[7 + i * 8] & 0xff) << 8;
            int humidityL = (historyByte[6 + i * 8] & 0xff);
            float tempFloat = (tempH + tempL) / 10f;
            if (symbol == 1) tempFloat = 0 - tempFloat;
            float tempValue = Float.parseFloat(String.format(Locale.US, "%.1f", tempFloat));
            float humidityValue = Float.parseFloat(String.format(Locale.US, "%.1f", (humidityH + humidityL) / 10f));
            if (bleDataCallBack != null) {
                bleDataCallBack.onOffLineRecord(upTime1 + upTime2 + upTime3 + upTime4, tempValue, humidityValue);
            }
        }

        if (bleDataCallBack != null) {
            bleDataCallBack.onOffLineRecordNum(total1 + total2 + total3 + total4, sendNum1 + sendNum2 + sendNum3 + sendNum4);
        }
    }


    public void getOffLineRecord(long time) {
        SendMcuBean sendBleBean = new SendMcuBean();
        byte[] bytes = new byte[5];
        bytes[0] = 0x05;
        bytes[4] = (byte) ((time & 0xff000000L) >> 24);
        bytes[3] = (byte) ((time & 0x00ff0000L) >> 16);
        bytes[2] = (byte) ((time & 0x0000ff00L) >> 8);
        bytes[1] = (byte) (time & 0x000000ffL);
        sendBleBean.setHex(0x002e, bytes);
        sendData(sendBleBean);
    }


    public void getDeviceStatus() {
        SendMcuBean sendBleBean = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x00;

        sendBleBean.setHex(0x002e, bytes);

        sendData(sendBleBean);
    }

    //03 00 00 00
//    0701010A
    public void sendSlowData() {
        SendMcuBean sendBleBean = new SendMcuBean();
        byte[] bytes = new byte[4];
//        bytes[0] = 0x03;
//        bytes[1] = 0x03;
//        bytes[2] = 0x1E;
//        bytes[3] = 0x00;
        bytes[0] = 0x03;
        bytes[1] = 0x03;
        bytes[2] = 0x14;
        bytes[3] = 0x00;
        sendBleBean.setHex(0x002e, bytes);
        sendData(sendBleBean);
        SendMcuBean sendBleBean1 = new SendMcuBean();
        byte[] bytes1 = new byte[4];
//        bytes1[0] = 0x07;
//        bytes1[1] = 0x02;
//        bytes1[2] = 0x3C;
//        bytes1[3] = 0x0a;
        bytes1[0] = 0x07;
        bytes1[1] = 0x01;
        bytes1[2] = 0x1E;
        bytes1[3] = 0x14;
        sendBleBean1.setHex(0x002e, bytes1);
        sendData(sendBleBean1);


    }

    public void sendFatData() {
        SendMcuBean sendBleBean = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = 0x03;
        bytes[1] = 0x00;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        sendBleBean.setHex(0x002e, bytes);
        sendData(sendBleBean);
        SendMcuBean sendBleBean1 = new SendMcuBean();
        byte[] bytes1 = new byte[4];
        bytes1[0] = 0x07;
        bytes1[1] = 0x01;
        bytes1[2] = 0x01;
        bytes1[3] = 0x02;
        sendBleBean1.setHex(0x002e, bytes1);
        sendData(sendBleBean1);

    }


    public interface BleDataCallBack {
        void onDeviceStatus(int battery, long time,
                            float temp, float humidity);

        void onOffLineRecordNum(long total, long sendNum);

        void onOffLineRecord(long time, float temp, float humidity);
    }


    public void setReportTime(int time) {

        byte[] bytes = new byte[3];
        bytes[0] = 0x14;
        bytes[1] = (byte) ((time & 0xff00) >> 8);
        bytes[2] = (byte) (time & 0x00ff);
        SendMcuBean sendBleBean = new SendMcuBean();
        sendBleBean.setHex(0x0036, bytes);
        sendData(sendBleBean);
    }


    public void ota() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x91;
        bytes[1] = 0x01;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);

    }

}
