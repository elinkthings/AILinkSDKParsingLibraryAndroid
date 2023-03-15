package cn.net.aicare.modulelibrary.module.BleToothbrush;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

public class ToothbrushTestData extends BaseBleDeviceData {

    private static final int CID = BleToothbrushConfig.BLE_TOOTHBRUSH;

    public ToothbrushTestData(BleDevice bleDevice) {
        super(bleDevice);
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        switch (hex[0] & 0xFF) {
            case 0x02:
                mcuSetDefaultMode(hex);
                break;
            case 0x03:
                mcuGetDefaultMode(hex);
                break;
            case 0x06:
                mcuTry(hex);
                break;
            case 0x07:
                mcuQueryWorkStatus(hex);
                break;
            case 0x09:
                mcuSetCustom(hex);
                break;
            case 0x0A:
                mcuGetCustom(hex);
                break;
            case 0x0C:
                mcuSetGearTwo(hex);
                break;
            case 0x0D:
                mcuGetGearTwo(hex);
                break;
        }
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {
        switch (hex[0] & 0xFF) {
            case 0x36:
                mcuGetSupportMode(hex);
                break;
            case 0x7F:
                mcuRequestToken(hex);
                break;
        }
    }

    /**
     * MCU 回复支持挡位列表
     *
     * @param hex hex
     */
    private void mcuGetSupportMode(byte[] hex) {
        if (hex.length > 4 && hex[1] == 0x01) {
            int stage = hex[2] & 0xFF;
            int level = hex[3] & 0xFF;
            List<Integer> firstList = new ArrayList<>();
            List<Integer> secondList = new ArrayList<>();
            if (hex.length >= 4 + stage) {
                for (int i = 0; i < stage; i++) {
                    firstList.add((int) hex[i + 4] & 0xFF);
                }
            }
            if (hex.length >= 4 + stage + level) {
                for (int i = 0; i < level; i++) {
                    secondList.add((int) hex[i + 4 + stage] & 0xFF);
                }
            }
            if (mBleToothbrushCallback != null) {
                mBleToothbrushCallback.mcuSupportMode(firstList, secondList);
            }
        }
    }

    /**
     * MCU 回复授权
     *
     * @param hex hex
     */
    private void mcuRequestToken(byte[] hex) {
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuToken(hex[1] & 0xFF);
        }
    }

    /**
     * MCU 回复设置默认挡位和时间
     *
     * @param hex hex
     */
    private void mcuSetDefaultMode(byte[] hex) {
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuSetDefaultMode(BleStrUtils.byte2HexStr(hex), hex[1] & 0xFF);
        }
    }

    /**
     * MCU 回复获取默认挡位和时间
     *
     * @param hex hex
     */
    private void mcuGetDefaultMode(byte[] hex) {
        int highTime = (hex[1] & 0xFF) << 8;
        int lowTime = hex[2] & 0xFF;
        int gear = hex[3] & 0xFF;
        int gearLevel = hex[4] & 0xFF;
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuGetDefaultMode(highTime + lowTime, gear, gearLevel);
        }
    }

    /**
     * MCU 回复试用
     *
     * @param hex hex
     */
    private void mcuTry(byte[] hex) {
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuTry(BleStrUtils.byte2HexStr(hex), hex[1] & 0xFF);
        }
    }

    /**
     * MCU 回复查询工作状态
     *
     * @param hex hex
     */
    private void mcuQueryWorkStatus(byte[] hex) {
        int gear = hex[1] & 0xFF;
        int gearLevel = hex[2] & 0xFF;
        int stage = hex[3] & 0xFF;
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuQueryWorkStatus(gear, gearLevel, stage);
        }
    }

    /**
     * MCU 回复设置自定义挡位参数
     *
     * @param hex hex
     */
    private void mcuSetCustom(byte[] hex) {
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuSetCustom(hex[1] & 0xFF);
        }
    }

    /**
     * MCU 回复获取自定义挡位参数
     *
     * @param hex hex
     */
    private void mcuGetCustom(byte[] hex) {
        int highFrequency = (hex[2] & 0xFF) << 8;
        int lowFrequency = hex[3] & 0xFF;
        int duty = hex[4] & 0xFF;
        int highTime = (hex[5] & 0xFF) << 8;
        int lowTime = hex[6] & 0xFF;
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuGetCustom(highFrequency + lowFrequency, duty, highTime + lowTime);
        }
    }

    /**
     * MCU 回复设置二级挡位
     *
     * @param hex hex
     */
    private void mcuSetGearTwo(byte[] hex) {
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuSetGearTwo(hex[1] & 0xFF);
        }
    }

    /**
     * MCU 回复获取二级挡位
     *
     * @param hex hex
     */
    private void mcuGetGearTwo(byte[] hex) {
        if (mBleToothbrushCallback != null) {
            mBleToothbrushCallback.mcuGetGearTwo(hex[1] & 0xFF);
        }
    }

    /**
     * APP 获取牙刷支持的挡位
     */
    public void appGetSupportMode() {
        byte[] hex = new byte[2];
        hex[0] = 0x36;
        hex[1] = 0x01;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 设置绑定方式
     *
     * @param mode 0x00：直接绑定；0x01：绑定需要用户按键
     */
    public void appSetBindMode(int mode) {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0x7D;
        hex[1] = (byte) mode;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 获取绑定模式
     */
    public void appGetBindMode() {
        byte[] hex = new byte[1];
        hex[0] = (byte) 0x7E;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 请求绑定
     */
    public void appRequestToken() {
        byte[] timeByte = long2Bytes(System.currentTimeMillis()); // long 长度 8 byte  时间戳是 6个byte 所以取后面6位
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x7F;
        hex[1] = timeByte[2];
        hex[2] = timeByte[3];
        hex[3] = timeByte[4];
        hex[4] = timeByte[5];
        hex[5] = timeByte[6];
        hex[6] = timeByte[7];
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 设置三轴方向
     *
     * @param direction 0x00：把当前位置设置为直立位置；0x01：Y轴朝上；0x02：Y轴朝下；0x03：Y轴朝左；0x04：Y轴朝右
     */
    public void appSetThreeDirection(int direction) {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) direction;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 获取三轴方向
     */
    public void appGetThreeDirection() {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x02;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 获取三轴数据
     */
    public void appGetThreeData() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x05;
        hex[2] = (byte) 0x01;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 获取离线历史记录条数
     */
    public void appGetOfflineNum() {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x11;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 请求接收离线历史记录
     */
    public void appRequestReceiveOffline() {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x12;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 通知模块可以发送下一组数据
     */
    public void appNoticeOfflineNext() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x16;
        hex[2] = (byte) 0x02;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 取消接收离线历史记录
     */
    public void appCancelReceiveOffline() {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x13;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 请求清空离线历史记录
     */
    public void appRequestClearOffline() {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0xC0;
        hex[1] = (byte) 0x14;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }

    /**
     * APP 设置默认工作模式
     *
     * @param time 时间 秒
     * @param mode 模式 0x00：不修改；0xFF：手动设置；其他
     * @param gear 挡位 0x00：不支持；0x01：一级挡位；0x02：二级挡位
     */
    public void appSetDefaultMode(int time, int mode, int gear) {
        byte[] hex = new byte[5];
        hex[0] = (byte) 0x02;
        hex[1] = (byte) (time >> 8);
        hex[2] = (byte) (time & 0xFF);
        hex[3] = (byte) mode;
        hex[4] = (byte) gear;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 获取默认的工作模式
     */
    public void appGetDefaultMode() {
        byte[] hex = new byte[1];
        hex[0] = (byte) 0x03;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 试用
     *
     * @param mode      模式 0x00：不修改；0xFF：手动设置；其他
     * @param gear      挡位 0x00：不支持；0x01：一级挡位；0x02：二级挡位
     * @param frequency 频率 Hz
     * @param duty      占空比
     */
    public void appTry(int mode, int gear, int frequency, int duty) {
        byte[] hex = new byte[14];
        hex[0] = (byte) 0x06;
        hex[1] = (byte) mode;
        hex[2] = (byte) gear;
        hex[3] = (byte) 0xFF;
        hex[4] = (byte) (frequency >> 8);
        hex[5] = (byte) (frequency & 0xFF);
        hex[6] = (byte) duty;
        hex[7] = 0x00;
        hex[8] = 0x00;
        hex[9] = 0x00;
        hex[10] = 0x00;
        hex[11] = 0x00;
        hex[12] = 0x00;
        hex[13] = 0x00;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 查询牙刷工作状态
     */
    public void appQueryWorkStatus() {
        byte[] hex = new byte[1];
        hex[0] = (byte) 0x07;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 设置自定义模式
     *
     * @param frequency 频率 Hz
     * @param duty      占空比
     * @param time      时间 秒
     */
    public void appSetCustom(int frequency, int duty, int time) {
        byte[] hex = new byte[7];
        hex[0] = (byte) 0x09;
        hex[1] = (byte) 0x00;
        hex[2] = (byte) (frequency >> 8);
        hex[3] = (byte) (frequency & 0xFF);
        hex[4] = (byte) duty;
        hex[5] = (byte) (time >> 8);
        hex[6] = (byte) (time & 0xFF);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 获取自定义挡位参数
     */
    public void appGetCustom() {
        byte[] hex = new byte[1];
        hex[0] = (byte) 0x0A;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 设置二级挡位默认模式
     *
     * @param mode 模式 0x00：不修改；0xFF：手动设置；其他
     */
    public void appSetGearTwo(int mode) {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0x0C;
        hex[1] = (byte) mode;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 获取二级挡位默认模式
     */
    public void appGetGearTwo() {
        byte[] hex = new byte[1];
        hex[0] = (byte) 0x0D;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 下发数据上报完成
     *
     * @param value 0x00：失败；0x01：成功
     */
    public void appSetDone(int value) {
        byte[] hex = new byte[2];
        hex[0] = (byte) 0xFE;
        hex[1] = (byte) value;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 查询 BLE 状态
     */
    public void appQueryBleStatus() {
        byte[] hex = new byte[1];
        hex[0] = (byte) 0xFD;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    public interface BleToothbrushCallback {

        /**
         * MCU 回复支持的模式
         *
         * @param firstList  一级挡位
         * @param secondList 二级挡位
         */
        void mcuSupportMode(List<Integer> firstList, List<Integer> secondList);

        /**
         * MCU 回复授权结果
         *
         * @param result 0x00: 等待授权
         *               0x01： 已经授权（BLE 返回）
         *               0x02： 不需要授权（BLE 返回）
         *               0x03： 授权成功（MCU 返回）
         */
        void mcuToken(int result);

        /**
         * MCU 回复设置默认模式和时长
         *
         * @param result 0x00： 不支持级别
         *               0x01： 一级档位
         *               0x02： 二级档位
         */
        void mcuSetDefaultMode(String hexStr, int result);

        /**
         * MCU 回复获取默认模式和时长
         *
         * @param time      时间
         * @param gear      挡位
         * @param gearLevel 挡位级别
         */
        void mcuGetDefaultMode(int time, int gear, int gearLevel);

        /**
         * MCU 回复试用
         *
         * @param result 0： 设置成功
         *               1： 设置失败， 原因未知
         *               2： 不支持设置
         *               3： 设置失败， 电池电压不足
         *               4： 设置失败， 正在充电
         *               5： 设置失败， 正在工作
         */
        void mcuTry(String hexStr, int result);

        /**
         * MCU 回复查询工作状态
         *
         * @param gear      挡位
         * @param gearLevel 挡位级别
         * @param stage     工作阶段
         */
        void mcuQueryWorkStatus(int gear, int gearLevel, int stage);

        /**
         * MCU 回复设置自定义挡位参数
         *
         * @param status 0： 设置成功
         *               1： 设置失败
         *               2： 不支持设置
         */
        void mcuSetCustom(int status);

        /**
         * MCU 回复获取自定义挡位参数
         *
         * @param frequency 频率
         * @param duty      占空比
         * @param time      时间
         */
        void mcuGetCustom(int frequency, int duty, int time);

        /**
         * MCU 回复设置二级挡位
         *
         * @param status 0： 设置成功
         *               1： 设置失败
         *               2： 不支持设置
         */
        void mcuSetGearTwo(int status);

        /**
         * MCU 回复获取二级挡位
         *
         * @param status 0x00： 不支持
         *               0x01-0xfe： 工作档位编号
         *               0xFF： 手动设置档位
         */
        void mcuGetGearTwo(int status);
    }

    private BleToothbrushCallback mBleToothbrushCallback;

    public void setBleToothbrushCallback(BleToothbrushCallback bleToothbrushCallback) {
        mBleToothbrushCallback = bleToothbrushCallback;
    }

    private byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xFF);
        }
        return byteNum;
    }
}
