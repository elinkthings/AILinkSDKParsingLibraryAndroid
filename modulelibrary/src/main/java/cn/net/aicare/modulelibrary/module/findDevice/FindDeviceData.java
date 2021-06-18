package cn.net.aicare.modulelibrary.module.findDevice;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * xing<br>
 * 2021/03/24<br>
 * 寻物器(Baby scale)
 */
public class FindDeviceData extends BaseBleDeviceData {
    private String TAG = FindDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private byte[] CID;
    private int mType = FindBleConfig.FIND_DEVICE;
    private static BleDevice mBleDevice = null;
    private static FindDeviceData sFindDeviceData = null;

    public static FindDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (sFindDeviceData == null) {
                    sFindDeviceData = new FindDeviceData(bleDevice);

                }
            } else {
                sFindDeviceData = new FindDeviceData(bleDevice);
            }
        }
        return sFindDeviceData;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (sFindDeviceData != null) {
            mOnNotifyData = null;
            sFindDeviceData = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private FindDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置连接参数,设置为低功率连接方式,交互时间会比较长,会更省电
//            mBleDevice.setConnectPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);
        }
        init();
    }

    private void init() {
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    //------------------发送---------------


    /**
     * 获取设备当前连接的数量
     */
    public void getConnectDeviceNumber() {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[1];
        data[0] = FindBleConfig.GET_CONNECT_NUMBER;
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }

    /**
     * 获取当前连接的设备信息列表
     */
    public void getConnectInfoList() {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[1];
        data[0] = FindBleConfig.GET_CONNECT_INFO_LIST;
        sendBleBean.setHex(data);
        sendData(sendBleBean);
        mFindDeviceDataList.clear();
    }

    /**
     * 扫描附近的ble设备
     */
    public void scanNearbyBle() {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[1];
        data[0] = FindBleConfig.GET_CONNECT_INFO_LIST;
        sendBleBean.setHex(data);
        sendData(sendBleBean);
        mFindDeviceDataList.clear();
    }


    public void setCmd(int id, String cmd, String mac) {
        int cmdByteSize = cmd.length() % 2 == 0 ? cmd.length() / 2 : (cmd.length() / 2) + 1;
        byte[] cmdByte = new byte[cmdByteSize];
        for (int i = 0; i < cmd.length(); i += 2) {
            String byteStr = cmd.substring(i, i + 2);
            cmdByte[i] = (byte) Integer.parseInt(byteStr, 16);
        }
        byte[] macByte = new byte[6];
        if (mac.contains(":")) {
            String[] macArr = mac.split(":");
            for (int i = 0; i < macArr.length; i++) {
                macByte[macArr.length - i - 1] = (byte) Integer.parseInt(macArr[i], 16);
            }
        }
        setCmd(id, cmdByte, macByte);
    }


    public void setCmd(int id, byte[] cmd, byte[] mac) {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[2 + cmd.length + mac.length];
        data[0] = FindBleConfig.SET_CONNECT_DEVICE_DATA;
        data[1] = (byte) id;
        System.arraycopy(cmd, 0, data, 2, cmd.length);
        System.arraycopy(mac, 0, data, 2 + cmd.length, mac.length);
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }

    //-----------------------------------

    /**
     * 设置搜索附近设备
     *
     * @param cmd 0:停止扫描,
     *            1:扫描所有设备的广播内容和RSSI,10
     */
    public void setNearbyDevice(int cmd, int time, byte[] cmdByte) {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[4 + cmdByte.length];
        data[0] = FindBleConfig.SET_NEARBY_DEVICE;
        data[1] = (byte) cmd;
        data[2] = (byte) time;
        data[3] = (byte) (time >> 8);
        System.arraycopy(cmdByte, 0, data, 4, cmdByte.length);
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }

    private String mConnectMac = "";

    /**
     * 设置连接设备
     *
     * @param mac mac
     */
    public void setConnectDevice(String mac) {
        mConnectMac = mac;
        byte[] macByte = new byte[6];
        if (mac.contains(":")) {
            String[] macArr = mac.split(":");
            for (int i = 0; i < macArr.length; i++) {
                macByte[macArr.length - i - 1] = (byte) Integer.parseInt(macArr[i], 16);
            }
        }
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[1 + 6];
        data[0] = FindBleConfig.SET_CONNECT_DEVICE;
        System.arraycopy(macByte, 0, data, 1, macByte.length);
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }


    /**
     * 通知主机断开连接从机
     */
    public void setDisconnectDevice() {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[3];
        data[0] = FindBleConfig.SET_DISCONNECT_DEVICE;
        data[1] = 0x01;
        data[2] = 0x00;
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }


    //-------------------接收-----------------

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (mType == type) {
            if (hex == null) {
                BleLog.i(TAG, "接收到的数据:null");
                return;
            }
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据A7:" + data);
            //校验解析
            dataCheckA7(hex);
        }
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {
        if (hex == null) {
            BleLog.i(TAG, "接收到的数据:null");
            return;
        }
        String data = BleStrUtils.byte2HexStr(hex);
        BleLog.i(TAG, "接收到的数据A6:" + data);
        //校验解析
        dataCheckA6(hex);
    }

    //----------------解析数据------

    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheckA7(byte[] data) {
        if (data == null)
            return;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onData(data, mType);
            }
        });

    }


    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheckA6(byte[] data) {
        if (data == null)
            return;

        switch (data[0]) {

            case FindBleConfig.GET_CONNECT_NUMBER:
                parsingConnectNumber(data);

                break;

            case FindBleConfig.GET_CONNECT_INFO_LIST:
                parsingConnectDeviceInfo(data);

                break;
            //发送完成
            case FindBleConfig.GET_CONNECT_INFO_LIST_END:
                parsingConnectDeviceInfoListEnd(data);

                break;
            //当前连接的设备状态
            case FindBleConfig.GET_CONNECT_INFO_STATUS:
                parsingConnectDeviceStatus(data);

                break;

            //获取附近的设备
            case FindBleConfig.GET_NEARBY_DEVICE:
                parsingNearbyDevice(data);
                break;
            //设置连接设备
            case FindBleConfig.SET_CONNECT_DEVICE:
                parsingConnectDevice(data);
                break;

            default:
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onData(data, mType);
                    }
                });
                break;

        }


    }

    /**
     * 解析连接设备结果
     *
     * @param data
     */
    private void parsingConnectDevice(byte[] data) {
        if (data.length >= 2) {
            String mac = mConnectMac;
            int status = data[1] & 0xFF;
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onConnectDeviceStatus(mac, status);
                }
            });
        }

    }


    /**
     * 解析附近的设备
     *
     * @param data
     */
    private void parsingNearbyDevice(byte[] data) {
        if (data.length >= 8) {
            byte[] macByte = new byte[6];
            System.arraycopy(data, 1, macByte, 0, macByte.length);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : macByte) {
                stringBuilder.append((b & 0xff));
                stringBuilder.append(":");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            String mac = stringBuilder.toString();
            int rssi = data[7] & 0xFF;
            byte[] broadcastData = new byte[data.length - 8];
            System.arraycopy(data, 8, broadcastData, 0, broadcastData.length);
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onNearbyDeviceInfo(mac, rssi, broadcastData);
                }
            });
        }

    }

    /**
     * 当前连接的设备状态(主动上报)
     *
     * @param data byte[]
     */
    private void parsingConnectDeviceStatus(byte[] data) {
        if (data.length >= 3) {
            int id = data[1] & 0xFF;
            int status = data[2] & 0xFF;
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onConnectDeviceStatus(id, status);
                }
            });
        }

    }

    /**
     * 解析获取当前连接的设备信息列表
     *
     * @param data byte[]
     */
    private void parsingConnectDeviceInfo(byte[] data) {
        if (data.length >= 7) {
            int status = data[1] & 0xFF;
            byte[] macByte = new byte[6];
            System.arraycopy(data, 2, macByte, 0, macByte.length);
            StringBuilder mac = new StringBuilder();
            for (int i = macByte.length - 1; i >= 0; i--) {
                byte b=macByte[i];
                String stmp = Integer.toHexString((b & 0xFF));
                if (stmp.length() == 1)
                    mac.append("0").append(stmp);
                else
                    mac.append(stmp);
                mac.append(":");
            }
            mac.deleteCharAt(mac.length() - 1);
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onConnectDeviceInfo(status, mac.toString());
                }
            });


        }

    }

    private List<FindConnectDeviceInfoBean> mFindDeviceDataList = new ArrayList<>();

    /**
     * 解析获取当前连接的设备信息列表
     *
     * @param data byte[]
     */
    private void parsingConnectDeviceInfoList(byte[] data) {
        if (data.length >= 7) {
            int id = data[1] & 0xFF;
            byte[] macByte = new byte[6];
            System.arraycopy(data, 1, macByte, 0, macByte.length);
            StringBuilder mac = new StringBuilder();
            for (byte b : macByte) {
                mac.append((b & 0xFF));
                mac.append(":");
            }
            mac.deleteCharAt(mac.length() - 1);
            FindConnectDeviceInfoBean findConnectDeviceInfoBean = new FindConnectDeviceInfoBean(id, mac.toString());
            mFindDeviceDataList.add(findConnectDeviceInfoBean);
        }

    }

    /**
     * 解析获取当前连接的设备信息列表 发送完成
     *
     * @param data byte[]
     */
    private void parsingConnectDeviceInfoListEnd(byte[] data) {
        if (data.length >= 2) {
            int number = data[1] & 0xFF;
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onConnectDeviceInfoList(mFindDeviceDataList);
                }
            });
        }

    }

    /**
     * 解析获取当前连接的数量
     *
     * @param data
     */
    private void parsingConnectNumber(byte[] data) {
        if (data.length > 1) {
            int number = data[1] & 0xFF;
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onConnectNumber(number);
                }
            });
        }

    }


    //----------------解析数据------



    public interface onNotifyData {
        /**
         * 不能识别的透传数据
         * Unrecognized pass-through data
         */
        default void onData(byte[] data, int type) {
        }

        /**
         * 当前连接的数量
         */
        default void onConnectNumber(int number) {
        }

        /**
         * 当前连接的设备信息列表
         *
         * @param list List<FindConnectDeviceInfoBean>
         */
        default void onConnectDeviceInfoList(List<FindConnectDeviceInfoBean> list) {
        }

        /**
         * 主动上报连接的设备状态
         *
         * @param id     编号
         * @param status 设备状态（0，断开；1连接）
         */
        default void onConnectDeviceStatus(int id, int status) {
        }


        /**
         * 附近的设备信息
         *
         * @param mac           mac
         * @param rssi          rssi
         * @param broadcastData 广播数据
         */
        void onNearbyDeviceInfo(String mac, int rssi, byte[] broadcastData);

        /**
         * 附近的设备信息
         *
         * @param mac    mac
         * @param status 设备状态（0，断开；1连接,3连接超时）
         */
        void onConnectDeviceStatus(String mac, int status);

        /**
         * 当前连接的设备地址
         *
         * @param status （0，断开；1连接）
         * @param mac    地址
         */
        void onConnectDeviceInfo(int status, String mac);

    }


    //-----------------set/get-----------------
    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }


    /**
     * 版本号接口
     * Version number interface
     */
    public void setOnBleVersionListener(OnBleVersionListener bleVersionListener) {
        if (mBleDevice != null)
            mBleDevice.setOnBleVersionListener(bleVersionListener);
    }

    /**
     * mcu相关的信息(电量,时间)
     * (mcu related information (power, time))
     */
    public void setOnMcuParameterListener(OnMcuParameterListener mcuParameterListener) {
        if (mBleDevice != null)
            mBleDevice.setOnMcuParameterListener(mcuParameterListener);
    }

    /**
     * 厂商信息(Manufacturer Information)
     */
    public void setOnCompanyListener(OnBleCompanyListener companyListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleCompanyListener(companyListener);
        }
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper());


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
