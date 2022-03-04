package cn.net.aicare.modulelibrary.module.FasciaGun;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;

/**
 * 筋膜枪
 */
public class FasciaGunData extends BaseBleDeviceData {

    private static final int CID = 0x3B;

    public FasciaGunData(BleDevice bleDevice) {
        super(bleDevice);
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0] & 0xFF) {
                case 0x04:
                    // 设备回复启动/停止
                    mcuDevice(hex);
                    break;
                case 0x06:
                    // 设备回复设置工作挡位
                    mcuSetGear(hex);
                    break;
                case 0x08:
                    // 设备回复设置倒计时
                    mcuSetTime(hex);
                    break;
                case 0x09:
                    // 设备实时上报状态
                    mcuStatus(hex);
                    break;
            }
        }
    }

    private void mcuDevice(byte[] hex) {
        int mode = hex[1] & 0xFF;
        int gear = hex[2] & 0xFF;
        if (mFasciaGunCallback != null) {
            mFasciaGunCallback.mcuDevice(mode, gear);
        }
    }

    private void mcuSetGear(byte[] hex) {
        int gear = hex[1] & 0xFF;
        if (mFasciaGunCallback != null) {
            mFasciaGunCallback.mcuSetGear(gear);
        }
    }

    private void mcuSetTime(byte[] hex) {
        int mode = hex[1] & 0xFF;
        int second = ((hex[2] & 0xFF) << 8) + (hex[3] & 0xFF);
        if (mFasciaGunCallback != null) {
            mFasciaGunCallback.mcuSetTime(mode, second);
        }
    }

    private void mcuStatus(byte[] hex) {
        int workStatus = hex[1] & 0xFF;
        int useTime = ((hex[2] & 0xFF) << 8) + (hex[3] & 0xFF);
        int curGear = hex[4] & 0xFF;
        int timeStatus = hex[5] & 0xFF;
        int timeSecond = ((hex[6] & 0xFF) << 8) + (hex[7] & 0xFF);
        int pressure = hex[8] & 0xFF;
        int batteryStatus = hex[9] & 0xFF;
        int batteryNum = hex[10] & 0xFF;
        int supportGear = hex[11] & 0xFF;
        if (mFasciaGunCallback != null) {
            mFasciaGunCallback.mcuStatus(workStatus, useTime, curGear, timeStatus, timeSecond, pressure, batteryStatus, batteryNum, supportGear);
        }
    }

    /**
     * APP 查询设备支持的挡位
     * 已弃用
     */
    @Deprecated
    public void appQueryGear() {
        byte[] hex = new byte[2];
        hex[0] = 0x01;
        hex[1] = 0x00;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 启动/停止设备
     *
     * @param mode 0x01：启动；0x02：停止
     * @param gear 挡位
     */
    public void appDevice(int mode, int gear) {
        byte[] hex = new byte[3];
        hex[0] = 0x03;
        hex[1] = (byte) mode;
        hex[2] = (byte) gear;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 设置挡位
     *
     * @param gear 挡位
     */
    public void appSetGear(int gear) {
        byte[] hex = new byte[2];
        hex[0] = 0x05;
        hex[1] = (byte) gear;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 设置倒计时
     *
     * @param mode   0x00：关闭；0x01：开启
     * @param second 秒
     */
    public void appSetTime(int mode, int second) {
        byte[] hex = new byte[4];
        hex[0] = 0x07;
        hex[1] = (byte) mode;
        hex[2] = (byte) (second >> 8);
        hex[3] = (byte) (second & 0xFF);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    public interface FasciaGunCallback {

        /**
         * MCU 回复启动/停止设备
         *
         * @param mode 0x01：启动；0x02：停止
         * @param gear 挡位
         */
        void mcuDevice(int mode, int gear);

        /**
         * MCU 回复设置挡位
         *
         * @param gear 当前挡位
         */
        void mcuSetGear(int gear);

        /**
         * MCU 回复设置倒计时
         *
         * @param mode   0x00：关闭倒计时；0x01：开启倒计时
         * @param second 秒
         */
        void mcuSetTime(int mode, int second);

        /**
         * MCU 上报实时状态
         *
         * @param workStatus    工作状态 0x01：启动；0x02：停止
         * @param useTime       使用时间 秒
         * @param curGear       当前挡位
         * @param timeStatus    是否倒计时 0x00：关闭倒计时；0x01：开启倒计时
         * @param timeSecond    倒计时秒数
         * @param pressure      压力值 0xFF：不支持
         * @param batteryStatus 电池状态 0x00：未充电；0x01：在充电；0x02：充满电
         * @param batteryNum    电量
         * @param supportGear   支持的挡位 0~n
         */
        void mcuStatus(int workStatus, int useTime, int curGear, int timeStatus, int timeSecond, int pressure, int batteryStatus, int batteryNum, int supportGear);
    }

    private FasciaGunCallback mFasciaGunCallback;

    public void setFasciaGunCallback(FasciaGunCallback fasciaGunCallback) {
        mFasciaGunCallback = fasciaGunCallback;
    }
}
