package cn.net.aicare.modulelibrary.module.ShareSocket;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

import cn.net.aicare.modulelibrary.module.ShareSocket.ShareSocketConfig;

public class ShareSocketData extends BaseBleDeviceData implements OnBleVersionListener {

    private static final int CID = ShareSocketConfig.SHARE_SOCKET;

    public ShareSocketData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(this);
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case (byte) ShareSocketConfig.SET_SOCKET_TIME_CALLBACK:
                    // 设置充电时间
                    mcuSetSocketTime(hex);
                    break;
                case (byte) ShareSocketConfig.GET_SOCKET_TIME_CALLBACK:
                    // 查询剩余充电时间
                    mcuGetSocketTime(hex);
                    break;
                case (byte) ShareSocketConfig.SWITCH:
                    // 开关
                    mcuSwitch(hex);
                    break;
            }
        }
    }

    @Override
    public void onBmVersion(String version) {
        if (mShareSocketCallback != null) {
            mShareSocketCallback.mcuVersion(version);
        }
    }

    /**
     * mcu回复设置充电时间结果
     *
     * @param hex hex
     */
    private void mcuSetSocketTime(byte[] hex) {
        int id = hex[1];
        int status = hex[2];
        if (mShareSocketCallback != null) {
            mShareSocketCallback.mcuSetSocketTime(id, status);
        }
    }

    /**
     * mcu回复获取剩余充电时间结果
     *
     * @param hex hex
     */
    private void mcuGetSocketTime(byte[] hex) {

        int id = hex[1];
        int status = hex[2];
        int hour;
        int minute;
        int second;
        if (status == 0) {
            hour = hex[3];
            minute = hex[4];
            second = hex[5];
        } else {
            hour = minute = second = -1;
        }
        if (mShareSocketCallback != null) {
            mShareSocketCallback.mcuGetSocketTime(id, status, hour, minute, second);
        }
    }

    /**
     * mcu回复开关设置结果
     *
     * @param hex hex
     */
    private void mcuSwitch(byte[] hex) {
        int id = hex[1];
        int status = hex[2];
        if (mShareSocketCallback != null) {
            mShareSocketCallback.mcuSwitch(id, status);
        }
    }

    /**
     * APP下发充电时间
     *
     * @param id     编号
     * @param hour   hour
     * @param minute minute
     * @param second second
     */
    public void appSetSocketTime(int id, int hour, int minute, int second) {
        byte[] hex = new byte[5];
        hex[0] = (byte) ShareSocketConfig.SET_SOCKET_TIME_CALLBACK;
        hex[1] = (byte) id;
        hex[2] = (byte) hour;
        hex[3] = (byte) minute;
        hex[4] = (byte) second;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP查询剩余充电时间
     *
     * @param id 编号
     */
    public void appGetSocketTime(int id) {
        byte[] hex = new byte[2];
        hex[0] = (byte) ShareSocketConfig.GET_SOCKET_TIME_CALLBACK;
        hex[1] = (byte) id;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP切换开关
     *
     * @param id     编号
     * @param isOpen 是否打开
     */
    public void appSwitch(int id, boolean isOpen) {
        byte[] hex = new byte[3];
        hex[0] = (byte) ShareSocketConfig.SWITCH;
        hex[1] = (byte) id;
        hex[2] = (byte) (isOpen ? 1 : 0);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP获取设备版本号
     */
    public void appGetVersion() {
        sendData(new SendBleBean(BleSendCmdUtil.getInstance().getBleVersion()));
    }

    public interface ShareSocketCallback {
        /**
         * 设置充电时间
         *
         * @param id     编号
         * @param status 0：成功；1：失败
         */
        void mcuSetSocketTime(int id, int status);

        /**
         * 获取剩余充电时间
         *
         * @param id     编号
         * @param status 0：成功；1：失败
         * @param hour   小时
         * @param minute 分钟
         * @param second 秒
         */
        void mcuGetSocketTime(int id, int status, int hour, int minute, int second);

        /**
         * mcu开关
         *
         * @param id     编号
         * @param status 0：成功；1：失败
         */
        void mcuSwitch(int id, int status);

        /**
         * mcu返回设备版本
         *
         * @param version 版本
         */
        void mcuVersion(String version);
    }

    private ShareSocketCallback mShareSocketCallback;

    public void setShareSocketCallback(ShareSocketCallback shareSocketCallback) {
        mShareSocketCallback = shareSocketCallback;
    }
}
