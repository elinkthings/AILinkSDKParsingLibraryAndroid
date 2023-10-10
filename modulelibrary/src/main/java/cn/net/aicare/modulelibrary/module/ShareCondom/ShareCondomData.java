package cn.net.aicare.modulelibrary.module.ShareCondom;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;

public class ShareCondomData extends BaseBleDeviceData {

    private static final int CID = ShareCondomConfig.SHARE_CONDOM;

    public ShareCondomData(BleDevice bleDevice) {
        super(bleDevice);
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case (byte) ShareCondomConfig.SET_CONDOM_TIME_CALLBACK:
                    // 设置充电时间
                    mcuSetCondomTime(hex);
                    break;
                case (byte) ShareCondomConfig.GET_CONDOM_TIME_CALLBACK:
                    // 查询剩余充电时间
                    mcuGetCondomTime(hex);
                    break;
                case (byte) ShareCondomConfig.SWITCH:
                    // 开关
                    mcuSwitch(hex);
                    break;
                case (byte) ShareCondomConfig.OUT:
                    // 出仓
                    mcuOut(hex);
                    break;
                case (byte) ShareCondomConfig.RECYCLE:
                    // 回收
                    mcuRecycle(hex);
                    break;
                case (byte) ShareCondomConfig.OPEN:
                    // 打开补货门
                    mcuOpen(hex);
                    break;
            }
        }
    }

    /**
     * mcu回复设置充电时间结果
     *
     * @param hex hex
     */
    private void mcuSetCondomTime(byte[] hex) {
        int status = hex[1];
        if (mShareCondomCallback != null) {
            mShareCondomCallback.mcuSetCondomTime(status);
        }
    }

    /**
     * mcu回复获取剩余充电时间结果
     *
     * @param hex hex
     */
    private void mcuGetCondomTime(byte[] hex) {
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
        if (mShareCondomCallback != null) {
            mShareCondomCallback.mcuGetCondomTime(status, hour, minute, second);
        }
    }

    /**
     * mcu回复开关设置结果
     *
     * @param hex hex
     */
    private void mcuSwitch(byte[] hex) {
        int status = hex[1];
        if (mShareCondomCallback != null) {
            mShareCondomCallback.mcuSwitch(status);
        }
    }

    /**
     * mcu出仓
     *
     * @param hex hex
     */
    private void mcuOut(byte[] hex) {
        int status = hex[1];
        if (mShareCondomCallback != null) {
            mShareCondomCallback.mcuOut(status);
        }
    }

    /**
     * mcu回收
     *
     * @param hex hex
     */
    private void mcuRecycle(byte[] hex) {
        int status = hex[1];
        if (mShareCondomCallback != null) {
            mShareCondomCallback.mcuRecycle(status);
        }
    }

    /**
     * mcu打开补货门
     *
     * @param hex hex
     */
    private void mcuOpen(byte[] hex) {
        int status = hex[1];
        if (mShareCondomCallback != null) {
            mShareCondomCallback.mcuOpen(status);
        }
    }

    /**
     * app下发充电时间
     *
     * @param hour   hour
     * @param minute minute
     * @param second second
     */
    public void appSetCondomTime(int hour, int minute, int second) {
        byte[] hex = new byte[4];
        hex[0] = (byte) ShareCondomConfig.SET_CONDOM_TIME_CALLBACK;
        hex[1] = (byte) hour;
        hex[2] = (byte) minute;
        hex[3] = (byte) second;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * app查询剩余充电时间
     */
    public void appGetCondomTime() {
        byte[] hex = new byte[1];
        hex[0] = (byte) ShareCondomConfig.GET_CONDOM_TIME_CALLBACK;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * app切换开关
     *
     * @param isOpen 是否打开
     */
    public void appSwitch(boolean isOpen) {
        byte[] hex = new byte[2];
        hex[0] = (byte) ShareCondomConfig.SWITCH;
        hex[1] = (byte) (isOpen ? 1 : 0);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * app出仓
     */
    public void appOut() {
        byte[] hex = new byte[1];
        hex[0] = (byte) ShareCondomConfig.OUT;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * app回收
     */
    public void appRecycle() {
        byte[] hex = new byte[1];
        hex[0] = (byte) ShareCondomConfig.RECYCLE;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * app打开补货门
     */
    public void appOpen() {
        byte[] hex = new byte[1];
        hex[0] = (byte) ShareCondomConfig.OPEN;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    public interface ShareCondomCallback {
        /**
         * 设置充电时间
         *
         * @param status 0：成功；1：失败
         */
        void mcuSetCondomTime(int status);

        /**
         * 获取剩余充电时间
         *
         * @param status 0：成功；1：失败
         * @param hour   小时
         * @param minute 分钟
         * @param second 秒
         */
        void mcuGetCondomTime(int status, int hour, int minute, int second);

        /**
         * mcu开关
         *
         * @param status 0：成功；1：失败
         */
        void mcuSwitch(int status);

        /**
         * mcu出仓
         *
         * @param status 0：成功；1：不支持；2：电机或限位开关故障；3：仓位已出仓
         */
        void mcuOut(int status);

        /**
         * mcu回收
         *
         * @param status 0：成功；1：不支持；2：电机或限位开关故障；3：仓位未出仓
         */
        void mcuRecycle(int status);

        /**
         * mcu打开补货门
         *
         * @param status 0：成功；1：不支持
         */
        void mcuOpen(int status);
    }

    private ShareCondomCallback mShareCondomCallback;

    public void setShareCondomCallback(ShareCondomCallback shareCondomCallback) {
        mShareCondomCallback = shareCondomCallback;
    }
}
