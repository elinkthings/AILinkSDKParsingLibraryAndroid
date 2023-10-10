package cn.net.aicare.modulelibrary.module.BleNutrition;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

import java.util.List;

/**
 * 蓝牙营养秤
 */
public class BleNutritionData extends BaseBleDeviceData implements OnBleVersionListener {

    private static final int CID = BleNutritionConfig.BLE_NUTRITION;

    public BleNutritionData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(this);
        // 连上后主动获取支持单位
        bleDevice.sendData(new SendBleBean(BleSendCmdUtil.getInstance().getSupportUnit()));
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case BleNutritionConfig.MCU_WEIGHT:
                    mcuWeight(hex);
                    break;
                case BleNutritionConfig.MCU_UNIT_RESULT:
                    mcuUnitResult(hex);
                    break;
                case BleNutritionConfig.MCU_ERR:
                    mcuErr(hex);
                    break;
            }
        }
    }

    @Override
    public void onBmVersion(String version) {
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuBmVersion(version);
        }
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuSupportUnit(list);
        }
    }

    /**
     * MCU 上报重量
     */
    private void mcuWeight(byte[] hex) {
        int no = hex[1] & 0xff;// 流水号
        int weight = ((hex[2] & 0xff) << 16) + ((hex[3] & 0xff) << 8) + ((hex[4] & 0xff));
        int unit = hex[5] & 0xff;
        int decimal = hex[6] & 0xff;
        int symbol = hex[7] & 0xff;
        int type = hex[8] & 0xff;
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuWeight(no, weight, unit, decimal, symbol, type);
        }
    }

    /**
     * MCU 上报设置单位结果
     */
    private void mcuUnitResult(byte[] hex) {
        int status = hex[1] & 0xff;
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuUnitResult(status);
        }
    }

    /**
     * MCU 上报异常报警
     */
    private void mcuErr(byte[] hex) {
        int weightErr = (hex[1] & 0xff) & 0x01;
        int batteryErr = ((hex[1] & 0xff) >> 1) & 0x01;
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuErr(weightErr, batteryErr);
        }
    }

    /**
     * APP 下发单位
     */
    public void setUnit(int unit) {
        byte[] hex = new byte[2];
        hex[0] = BleNutritionConfig.SET_UNIT;
        hex[1] = (byte) unit;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 下发归零
     */
    public void setZero() {
        byte[] hex = new byte[1];
        hex[0] = BleNutritionConfig.SET_ZERO;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    public interface BleNutritionCallback {

        /**
         * MCU 上报 BM 版本
         */
        void mcuBmVersion(String version);

        /**
         * MCU 上报设备支持的单位
         */
        void mcuSupportUnit(List<SupportUnitBean> list);

        /**
         * MCU 上报重量
         *
         * @param no      流水号。稳定重量不会变
         * @param weight  原始重量
         * @param unit    0x00: g；0x01: ml；0x02: lb：oz；0x03: oz；0x04 : kg；0x05: 斤；0x06: 牛奶ml；0x07: 水ml；0x08: 牛奶floz；0x09: 水floz；0x0A: lb
         * @param decimal 小数点位
         * @param symbol  符号；0：正；1：负
         * @param type    重量类型：1：实时；2：稳定
         */
        void mcuWeight(int no, int weight, int unit, int decimal, int symbol, int type);

        /**
         * MCU 上报设置单位结果
         *
         * @param status 0：成功；1：失败；2：不支持
         */
        void mcuUnitResult(int status);

        /**
         * MCU 上报异常
         *
         * @param weightErr  0：正常；1：超重
         * @param batteryErr 0：正常；1：低电
         */
        void mcuErr(int weightErr, int batteryErr);
    }

    private BleNutritionCallback mBleNutritionCallback;

    public void setBleNutritionCallback(BleNutritionCallback bleNutritionCallback) {
        mBleNutritionCallback = bleNutritionCallback;
    }
}
