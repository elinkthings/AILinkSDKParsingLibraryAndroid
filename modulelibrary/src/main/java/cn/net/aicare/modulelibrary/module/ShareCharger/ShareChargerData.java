package cn.net.aicare.modulelibrary.module.ShareCharger;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;

public class ShareChargerData extends BaseBleDeviceData {

    private static final int CID = ShareChargerConfig.SHARE_CHARGER;

    public ShareChargerData(BleDevice bleDevice) {
        super(bleDevice);
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case (byte) ShareChargerConfig.SET_CHARGER_TIME_CALLBACK:
                    // 设置充电时间
                    mcuSetChargerTime(hex);
                    break;
                case (byte) ShareChargerConfig.GET_CHARGER_TIME_CALLBACK:
                    // 查询剩余充电时间
                    mcuGetChargerTime(hex);
                    break;
                case (byte) ShareChargerConfig.SWITCH:
                    // 开关
                    mcuSwitch(hex);
                    break;
            }
        }
    }

    /**
     * mcu回复设置充电时间结果
     *
     * @param hex hex
     */
    private void mcuSetChargerTime(byte[] hex) {
        int status = hex[1];
        if (mShareChargerCallback != null) {
            mShareChargerCallback.mcuSetChargerTime(status);
        }
    }

    /**
     * mcu回复获取剩余充电时间结果
     *
     * @param hex hex
     */
    private void mcuGetChargerTime(byte[] hex) {
        int status = hex[1];
        int hour;
        int minute;
        int second;
        if (status == 0) {
            hour = hex[2];
            minute = hex[3];
            second = hex[4];
        } else {
            hour = minute = second = -1;
        }
        if (mShareChargerCallback != null) {
            mShareChargerCallback.mcuGetChargerTime(status, hour, minute, second);
        }
    }

    /**
     * mcu回复开关设置结果
     *
     * @param hex hex
     */
    private void mcuSwitch(byte[] hex) {
        int status = hex[1];
        if (mShareChargerCallback != null) {
            mShareChargerCallback.mcuSwitch(status);
        }
    }

    /**
     * APP下发充电时间
     *
     * @param hour   hour
     * @param minute minute
     * @param second second
     */
    public void appSetChargerTime(int hour, int minute, int second) {
        byte[] hex = new byte[4];
        hex[0] = (byte) ShareChargerConfig.SET_CHARGER_TIME_CALLBACK;
        hex[1] = (byte) hour;
        hex[2] = (byte) minute;
        hex[3] = (byte) second;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP查询剩余充电时间
     */
    public void appGetChargerTime() {
        byte[] hex = new byte[1];
        hex[0] = (byte) ShareChargerConfig.GET_CHARGER_TIME_CALLBACK;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP切换开关
     * @param isOpen 是否打开
     */
    public void appSwitch(boolean isOpen) {
        byte[] hex = new byte[2];
        hex[0] = (byte) ShareChargerConfig.SWITCH;
        hex[1] = (byte) (isOpen ? 1 : 0);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    public interface ShareChargerCallback {
        /**
         * 设置充电时间
         *
         * @param status 0：成功；1：失败
         */
        void mcuSetChargerTime(int status);

        /**
         * 获取剩余充电时间
         *
         * @param status 0：成功；1：失败
         * @param hour   小时
         * @param minute 分钟
         * @param second 秒
         */
        void mcuGetChargerTime(int status, int hour, int minute, int second);

        /**
         * mcu开关
         *
         * @param status 0：成功；1：失败
         */
        void mcuSwitch(int status);
    }

    private ShareChargerCallback mShareChargerCallback;

    public void setShareChargerCallback(ShareChargerCallback shareChargerCallback) {
        mShareChargerCallback = shareChargerCallback;
    }
}
