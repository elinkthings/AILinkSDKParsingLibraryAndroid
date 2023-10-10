package cn.net.aicare.modulelibrary.module.wifibleDevice;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnBleConnectStatus;
import com.pingwang.bluetoothlib.listener.OnServerInfoListener;
import com.pingwang.bluetoothlib.listener.OnWifiInfoListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatDataUtil;
import cn.net.aicare.modulelibrary.module.ToothBrush.ToothBrushBleCmd;

/**
 * wifi+ble设备对象
 *
 * @author xing
 * @date 2023/09/28
 */
public class WifiBleDeviceData implements OnWifiInfoListener, OnBleConnectStatus, OnServerInfoListener {
    private String TAG = WifiBleDeviceData.class.getName();
    private static BleDevice mBleDevice;

    private static WifiBleDeviceData sWifiBleDeviceData = null;

    private List<WifiBleInfoBean> mWifiBleInfoBeanList;

    /**
     * wifi+ble配网回调
     */
    private OnWiFiBleCallback mOnWiFiBleCallback;


    public static WifiBleDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (sWifiBleDeviceData == null) {
                    sWifiBleDeviceData = new WifiBleDeviceData(bleDevice);
                }
            } else {
                sWifiBleDeviceData = new WifiBleDeviceData(bleDevice);
            }
        }
        return sWifiBleDeviceData;
    }


    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (sWifiBleDeviceData != null) {
            sWifiBleDeviceData = null;
        }
    }

    /**
     * 断开连接(Disconnect)
     */
    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private WifiBleDeviceData(BleDevice bleDevice) {
        mBleDevice = bleDevice;
        bleDevice.setOnWifiInfoListener(this);
        bleDevice.setOnServerInfoListener(this);
        bleDevice.setOnBleConnectListener(this);
        mWifiBleInfoBeanList = new ArrayList<>();
    }


    //---------------解析数据-------------------------


    //----------------wifi----------------------

    private int mConnectWifiStep = 0;
    private String mConnectWifiMac = "";
    private String mConnectWifiPwd = "";


    /**
     * 设置连接wifi mac
     *
     * @param mac mac
     * @param pwd 密码
     */
    public void setConnectWifiMac(String mac, String pwd) {
        mConnectWifiMac = mac;
        mConnectWifiPwd = pwd;
        setConnectWifiMac(0);
    }

    /**
     * 设置连接wifi mac
     */
    private void setConnectWifiMac(int step) {
        mConnectWifiStep = step;
        switch (step) {
            case 0:
                setConnectWifiMac(mConnectWifiMac);
                break;
            case 1:
                setPwd(mConnectWifiPwd);
                break;
            case 2:
                setConnectWifi();
                break;

        }

    }

    /**
     * 设置密码
     *
     * @param pwd 密码
     */
    public void setPwd(String pwd) {
        if (pwd.isEmpty()) {
            byte[] bytes = new byte[0];
            setConnectWifiPwd(0, bytes);
        } else {
            byte[] password = BleStrUtils.stringToBytes(pwd);
            byte[][] sendSubcontract = getSendSubcontract(password);
            if (sendSubcontract != null) {
                int size = sendSubcontract.length;
                for (int i = 0; i < size; i++) {
                    byte[] bytes = sendSubcontract[i];
                    setConnectWifiPwd((i == size - 1) ? 0 : 1, bytes);
                }
            }
        }
    }

    /**
     * 扫描wifi
     *
     * @return {@link SendBleBean}
     */
    public void getWifiList() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) CmdConfig.GET_SCAN_WIFI_RESULT;
        bytes[1] = 0x01;
        boolean b = sendBleData(bytes);
        if (b) {
            mWifiBleInfoBeanList.clear();
        }
    }


    /**
     * 获取wifi当前状态
     *
     * @return {@link SendBleBean}
     */
    public void getWifiCurrentState() {
        byte[] bytes = new byte[1];
        bytes[0] = CmdConfig.GET_BLE_CONNECT_STATUS;
        sendBleData(bytes);
    }

    /**
     * 获取设备sn号(设备ID?)
     * 用于判断设备是否在服务器注册成功
     *
     * @return {@link SendBleBean}
     */
    public void getDeviceSn() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_SN;
        sendBleData(bytes);
    }


    /**
     * 获取ip地址
     */
    public void getIp() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_SERVICE_IP;
        sendBleData(bytes);
    }

    /**
     * 获取端口号
     */
    public void getPort() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_SERVICE_PORT;
        sendBleData(bytes);
    }

    /**
     * 获取路径
     */
    public void getPath() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_SERVICE_PATH;
        sendBleData(bytes);
    }

    /**
     * 断开当前wifi连接
     * Disconnect
     *
     * @return payload数据
     */
    public void setDisconnectWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) CmdConfig.DIS_OR_CON_WIFI;
        bytes[1] = 0x00;
        sendBleData(bytes);
    }

    /**
     * 获取当前连接的wifi的名字
     * Get the name of the currently connected wifi
     *
     * @return payload数据
     */
    public void getConnectWifiName() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_WIFI_NAME;
        sendBleData(bytes);
    }

    /**
     * 获取连接的wifi密码
     *
     * @return payload数据
     */
    public void getConnectWifiPwd() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_WIFI_PWD;
        sendBleData(bytes);
    }


    /**
     * 设置连接wifi mac
     *
     * @param mac mac
     */
    public void setConnectWifiMac(String mac) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) CmdConfig.SET_WIFI_MAC;
        if (!mac.contains(":")) {
            return;
        }
        String[] s = mac.split(":");
        for (int i = 0; i < s.length; i++) {
            bytes[i + 1] = (byte) Integer.parseInt(s[s.length - 1 - i], 16);
        }
        sendBleData(bytes);

    }

    /**
     * 设置连接wifi密码
     *
     * @param subpackage 分包(1：后面还有包;0：后面没有包)
     * @param password   密码
     */
    private void setConnectWifiPwd(int subpackage, byte[] password) {
        int length = 0;
        byte[] bytes1;
        if (password != null) {
            length = password.length + 1;
            bytes1 = new byte[length + 1];
            bytes1[0] = (byte) CmdConfig.SET_WIFI_PAW;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(password, 0, bytes1, 2, password.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x86;
        }
        sendBleData(bytes1);
    }


    /**
     * 连接wifi
     */
    public void setConnectWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) CmdConfig.DIS_OR_CON_WIFI;
        bytes[1] = 0x01;
        sendBleData(bytes);
    }


    private int mServiceInfoStep = 0;
    private String mServiceIp = "";
    private int mServicePort;
    private String mServicePath = "";

    /**
     * 设置服务信息
     *
     * @param ip   知识产权
     * @param port 港口
     * @param path 路径
     */
    public void setServiceInfo(String ip, int port, String path) {
        mServiceIp = ip;
        mServicePort = port;
        mServicePath = path;
        mServiceInfoStep = 0;
        setServiceInfo(mServiceInfoStep);
    }


    private void setServiceInfo(int step) {
        mServiceInfoStep = step;
        switch (step) {
            case 0:
                setIp(mServiceIp);
                break;
            case 1:
                setPort(mServicePort);
                break;
            case 2:
                setPath(mServicePath);
                break;
            default:
                //设置完成
                if (mOnWiFiBleCallback != null) {
                    mOnWiFiBleCallback.onServerSettingInfo(true);
                }
                break;
        }
    }


    /**
     * 设置端口号
     *
     * @param port 端口号
     */
    public void setPort(int port) {
        byte[] bytes1;
        bytes1 = new byte[3];
        bytes1[0] = (byte) CmdConfig.SET_SERVICE_PORT;
        bytes1[1] = (byte) (port >> 8);
        bytes1[2] = (byte) (port & 0xFF);
        sendBleData(bytes1);
    }

    /**
     * 设置路径
     *
     * @param path path
     */
    public void setPath(String path) {
        setPath(convertToASCII(path));
    }

    /**
     * 设置路径
     *
     * @param path path
     */
    private void setPath(byte[] path) {
        byte[][] sendSubcontract = getSendSubcontract(path);
        if (sendSubcontract != null) {
            int size = sendSubcontract.length;
            for (int i = 0; i < size; i++) {
                byte[] bytes = sendSubcontract[i];
                environmentPath((i == size - 1) ? 0 : 1, bytes);
            }
        }
    }


    /**
     * 装载发送
     *
     * @param subpackage 分装
     * @param bytesPath  Path
     */
    private void environmentPath(int subpackage, byte[] bytesPath) {
        byte[] bytes1;
        if (bytesPath != null) {
            bytes1 = new byte[bytesPath.length + 2];
            bytes1[0] = (byte) CmdConfig.SET_SERVICE_PATH;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(bytesPath, 0, bytes1, 2, bytesPath.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) CmdConfig.SET_SERVICE_PATH;
        }

        sendBleData(bytes1);

    }


    /**
     * 设置ip地址
     *
     * @param ips ip地址
     */
    public void setIp(String ips) {
        setIp(convertToASCII(ips));
    }

    /**
     * 设置IP
     *
     * @param ips IP地址
     */
    private void setIp(byte[] ips) {
        byte[][] sendSubcontract = getSendSubcontract(ips);
        if (sendSubcontract != null) {
            int size = sendSubcontract.length;
            for (int i = 0; i < size; i++) {
                byte[] bytes = sendSubcontract[i];
                environmentIp((i == size - 1) ? 0 : 1, bytes);
            }
        }
    }


    /**
     * 获取发送分包合同
     * 14个字节一包,最大支持14*127=1778个字节
     *
     * @param bytes 字节
     * @return {@link byte[][]}
     */
    private byte[][] getSendSubcontract(byte[] bytes) {
        int sendSize = 14;
        if (bytes == null || bytes.length > sendSize * 127) {
            return null;
        }
        int length = bytes.length;
        int count = length / sendSize;
        if (length % sendSize != 0) {
            count += 1;
        }
        byte[][] bytesList = new byte[count][];
        for (int i = 0; i < count; i++) {
            byte[] sendB;
            if (i == count - 1) {
                sendB = new byte[length % sendSize];
            } else {
                sendB = new byte[sendSize];
            }
            System.arraycopy(bytes, i * sendSize, sendB, 0, sendB.length);
            bytesList[i] = sendB;
        }
        return bytesList;
    }

    /**
     * 装载发送ip地址
     *
     * @param subpackage 分装
     * @param bytesIp    字节ip
     */
    private void environmentIp(int subpackage, byte[] bytesIp) {
        byte[] bytes1;
        if (bytesIp != null) {
            bytes1 = new byte[bytesIp.length + 2];
            bytes1[0] = (byte) CmdConfig.SET_SERVICE_IP;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(bytesIp, 0, bytes1, 2, bytesIp.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) CmdConfig.SET_SERVICE_IP;
        }
        sendBleData(bytes1);
    }


    /**
     * 转换为ASCII码
     *
     * @param string 字符串
     * @return {@link byte[]}
     */
    private byte[] convertToASCII(String string) {
        if (TextUtils.isEmpty(string)) {
            return new byte[0];
        }
        char[] ch = string.toCharArray();
        byte[] tmp = new byte[ch.length];
        for (int i = 0; i < ch.length; i++) {
            tmp[i] = (byte) Integer.valueOf(ch[i]).intValue();
        }
        return tmp;
    }


    /**
     * 获取发送ble bean
     *
     * @param bytes 字节
     * @return {@link SendBleBean}
     */
    private boolean sendBleData(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        if (mBleDevice != null) {
            mBleDevice.sendData(sendBleBean);
            return true;
        }
        return false;
    }

    //------------wifi接口实现----------------------------------

    /**
     * 获取wifible信息豆
     *
     * @param id id
     * @return {@link WifiBleInfoBean}
     */
    private WifiBleInfoBean getWifiBleInfoBean(int id) {
        for (WifiBleInfoBean wifiBleInfoBean : mWifiBleInfoBeanList) {
            if (wifiBleInfoBean.equals((Integer) id)) {
                return wifiBleInfoBean;
            }
        }
        WifiBleInfoBean wifiBleInfoBean = new WifiBleInfoBean(id);
        mWifiBleInfoBeanList.add(wifiBleInfoBean);
        return wifiBleInfoBean;
    }

    @Override
    public void onScanWiFiStatus(int status) {
        //wifi扫描状态
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onWifiScanStatus(status);
        }
    }

    @Override
    public void onWifiListName(int no, String name) {
        getWifiBleInfoBean(no).setSsid(name.trim());
    }

    @Override
    public void onConnectedWifiName(String name) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onConnectWifiName(name);
        }
    }

    @Override
    public void onWifiListInfo(int no, String mac, int db, int type, int wifiStatus) {
        WifiBleInfoBean wifiBleInfoBean = getWifiBleInfoBean(no);
        wifiBleInfoBean.setMac(mac);
        wifiBleInfoBean.setDb(db);
        wifiBleInfoBean.setType(type);
        wifiBleInfoBean.setWifiStatus(wifiStatus);
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onWifiScanBean(wifiBleInfoBean);
        }
    }

    @Override
    public void onWifiScanFinish(int wifiNum) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onWifiScanFinish(mWifiBleInfoBeanList);
        }
    }

    @Override
    public void onSetWifiNameOrPawOrConnectCallback(int type, int status) {
        BleLog.i(TAG, "type:0x" + Integer.toHexString(type) + "  status:" + status);
        if (type == CmdConfig.SET_WIFI_MAC && status == BodyFatDataUtil.STATUS_SUCCESS) {
            if (mConnectWifiStep == 0) {
                //设置mac成功,去设置密码
                setConnectWifiMac(1);
            }
        } else if (type == CmdConfig.SET_WIFI_PAW && status == BodyFatDataUtil.STATUS_SUCCESS) {
            if (mConnectWifiStep == 1) {
                //设置密码成功,去连接
                setConnectWifiMac(2);
            }
        } else if (type == CmdConfig.DIS_OR_CON_WIFI) {
            //正在连接
            if (mOnWiFiBleCallback != null) {
                mOnWiFiBleCallback.onWifiConnecting(status);
            }
        }


        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onSetWifiNameOrPwdOrConnectCallback(type, status);
        }
    }

    @Override
    public void getSelectWifiMac(String mac) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onConnectWifiMac(mac);
        }
    }

    @Override
    public void onBleConnectStatus(int bleStatus, int wifiStatus, int workStatus) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onBleAndWifiStatus(bleStatus, wifiStatus, workStatus);
        }
    }

    @Override
    public void getSN(long sn) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onDeviceSn(String.valueOf(sn));
        }
    }

    private String mServerIp = "";

    @Override
    public void onServerIp(boolean end, String ip) {
        mServerIp = mServerIp + ip;
        if (end) {
            //结束了
            if (mOnWiFiBleCallback != null) {
                mOnWiFiBleCallback.onServerIp(mServerIp);
            }
            mServerIp = "";
        }

    }

    @Override
    public void onServerPort(int port) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onServerPort(port);
        }
    }

    @Override
    public void onServerPath(String path) {
        if (mOnWiFiBleCallback != null) {
            mOnWiFiBleCallback.onServerPath(path);
        }
    }

    @Override
    public void onServerSettingReturn(int type, int status) {
        String data = "类型:0x" + Integer.toHexString(type) + "状态:" + status;
        BleLog.i(TAG, data);
        if (status != 0x00) {
            if (mOnWiFiBleCallback != null) {
                mOnWiFiBleCallback.onServerSettingInfo(false);
            }
        }
        switch (type) {

            case CmdConfig.SET_SERVICE_IP:
                if (mServiceInfoStep == 0) {
                    mServiceInfoStep++;
                    setServiceInfo(mServiceInfoStep);
                }
                break;
            case CmdConfig.SET_SERVICE_PORT:
                if (mServiceInfoStep == 1) {
                    mServiceInfoStep++;
                    setServiceInfo(mServiceInfoStep);
                }
                break;
            case CmdConfig.SET_SERVICE_PATH:
                if (mServiceInfoStep == 2) {
                    mServiceInfoStep++;
                    setServiceInfo(mServiceInfoStep);
                }
                break;

        }

    }

    //---------------接口------

    /**
     * wifi+ble配网回调
     *
     * @author xing
     * @date 2023/10/06
     */
    public interface OnWiFiBleCallback {


        /**
         * 查询蓝牙和wifi状态
         * Check Bluetooth and wifi status
         *
         * @param bleStatus  蓝牙状态 0 无连接 1：已连接 2：配对完成
         *                   Bluetooth status0 No connection 1: Connected 2: Pairing completed
         * @param wifiStatus wifi状态 0：没连接热点；1：尝试连接热点，但是失败，连接时密码错误、热点信号不好、主动断开都会是这个状态；2：连接的热点无网络或者信号不好；3：成功连接上热点；4：有热点信息，还没连接
         *                   wifi status 0: No hotspot connection; 1: Attempt to connect to the hotspot, but failure, wrong password when connecting, bad hotspot signal, active disconnection will
         *                   be this state; 2: No connection to the hotspot or signal ; 3: Successfully connected to the hotspot; 4: There is hotspot information, not connected yet
         * @param workStatus 工作状态 0：唤醒 1：进入休眠 2：模块准备就绪
         *                   working status 0: wake up 1: go to sleep 2: module is ready
         */
        void onBleAndWifiStatus(int bleStatus, int wifiStatus, int workStatus);

        /**
         * 扫描wifi状态
         * Scan wifi status
         *
         * @param status {@link ToothBrushBleCmd#STATUS_SUCCESS} 0x00：成功 0x01：失败 0x02：不支持
         *               0x00: success 0x01: failure 0x02: not supported
         */
        void onWifiScanStatus(int status);


        /**
         * 当前连接的wifi名称
         * The name of the currently connected wifi
         *
         * @param name wifi名称  wifi name
         */
        void onConnectWifiName(String name);

        /**
         * 在wifi扫描豆
         * End of scanning wifi
         *
         * @param wifiBleInfoBean wifi ble信息
         */
        void onWifiScanBean(WifiBleInfoBean wifiBleInfoBean);

        /**
         * wifi扫描完成
         * End of scanning wifi
         *
         * @param list 列表
         */
        void onWifiScanFinish(List<WifiBleInfoBean> list);

        /**
         * 设置wifi mac ,密码和连接或断开的回调
         * Set callback for wifimac, password and connection or disconnection
         *
         * @param type   设置的类型 0x84 设置 wifimac 0x86 设置wifi密码 0x88 断开或者连接
         *               ype Set type 0x84 Set wifimac 0x86 Set wifi password 0x88 Disconnect or connect
         * @param status {@link ToothBrushBleCmd#STATUS_SUCCESS} 0x00：成功 0x01：失败 0x02：不支持
         *               0x00: success 0x01: failure 0x02: not supported
         */
        void onSetWifiNameOrPwdOrConnectCallback(int type, int status);

        /**
         * wifi连接中
         *
         * @param status 状态
         */
        void onWifiConnecting(int status);

        /**
         * 获取已设置的wifi的Mac
         * Get Mac with wifi set
         *
         * @param mac wifiMac地址
         *            wifiMac address
         */
        void onConnectWifiMac(String mac);

        /**
         * 在设备sn
         *
         * @param sn sn
         */
        void onDeviceSn(String sn);

        /**
         * 在服务器IP地址
         *
         * @param ip IP地址
         */
        void onServerIp(String ip);

        /**
         * 服务器端口
         *
         * @param port 端口
         */
        void onServerPort(int port);

        /**
         * 在服务器路径
         *
         * @param path 路径
         */
        void onServerPath(String path);

        /**
         * 服务器设置信息
         *
         * @param status 状态
         */
        void onServerSettingInfo(boolean status);


    }


    //--------------set/get-------

    public void setOnWiFiBleCallback(OnWiFiBleCallback onWiFiBleCallback) {
        mOnWiFiBleCallback = onWiFiBleCallback;
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
