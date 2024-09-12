package cn.net.aicare.modulelibrary.module.ble_nutrition_nutrient;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

import java.util.List;

/**
 * 蓝牙营养秤(支持营养元素)
 */
public class BleNutritionNutrientData extends BaseBleDeviceData implements OnBleVersionListener {

    private static final int CID = BleNutritionNutrientConfig.CID;


    public BleNutritionNutrientData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(this);
        // 连上后主动获取支持单位
        bleDevice.sendData(new SendBleBean(BleSendCmdUtil.getInstance().getSupportUnit()));
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case BleNutritionNutrientConfig.MCU_WEIGHT:
                    mcuWeight(hex);
                    break;
                case BleNutritionNutrientConfig.MCU_UNIT_RESULT:
                    mcuUnitResult(hex);
                    break;
                case BleNutritionNutrientConfig.MCU_ERR:
                    mcuErr(hex);
                    break;
            }
        }
    }

    @Override
    public void onBmVersion(String version) {
        if (mListener != null) {
            mListener.onBmVersion(version);
        }
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        if (mListener != null) {
            mListener.onSupportUnit(list);
        }
    }



    /**
     * MCU 上报重量
     */
    private void mcuWeight(byte[] hex) {
        int no = hex[1] & 0xFF;// 流水号
        int weight = ((hex[2] & 0xFF) << 16) + ((hex[3] & 0xFF) << 8) + ((hex[4] & 0xFF));
        int unit = hex[5] & 0xFF;
        int decimal = hex[6] & 0xFF;
        int symbol = hex[7] & 0xFF;
        int type = hex[8] & 0xFF;
        if (mListener != null) {
            mListener.onWeight(no, weight, unit, decimal, symbol, type);
        }
    }

    /**
     * MCU 上报设置单位结果
     */
    private void mcuUnitResult(byte[] hex) {
        int status = hex[1] & 0xFF;
        if (mListener != null) {
            mListener.onUnitResult(status);
        }
    }

    /**
     * MCU 上报异常报警
     */
    private void mcuErr(byte[] hex) {
        int weightErr = (hex[1] & 0xFF) & 0x01;
        int batteryErr = ((hex[1] & 0xFF) >> 1) & 0x01;
        if (mListener != null) {
            mListener.onErr(weightErr, batteryErr);
        }
    }

    /**
     * APP 下发单位
     */
    public void setUnit(int unit) {
        byte[] hex = new byte[2];
        hex[0] = BleNutritionNutrientConfig.SET_UNIT;
        hex[1] = (byte) unit;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 下发归零 / 去皮
     */
    public void setZero() {
        byte[] hex = new byte[2];
        hex[0] = BleNutritionNutrientConfig.SET_ZERO;
        hex[1] = 0x01;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }



    /**
     * APP 下发营养元素
     */
    public void setNutrient(FoodNutrientBean bean) {
        byte[] hex1 = new byte[14];
        hex1[0] = BleNutritionNutrientConfig.SET_NUTRIENT;
        hex1[1] = 0x01;
        hex1[2] = (byte) (bean.getKcalInt()<<16);
        hex1[3] = (byte) (bean.getKcalInt()<<8);
        hex1[4] = (byte) (bean.getKcalInt());
        hex1[5] = (byte) (bean.getFatKcalInt()<<16);
        hex1[6] = (byte) (bean.getFatKcalInt()<<8);
        hex1[7] = (byte) (bean.getFatKcalInt());
        hex1[8] = (byte) (bean.getFatInt()<<16);
        hex1[9] = (byte) (bean.getFatInt()<<8);
        hex1[10] = (byte) (bean.getFatInt());
        hex1[11] = (byte) (bean.getSatFatInt()<<16);
        hex1[12] = (byte) (bean.getSatFatInt()<<8);
        hex1[13] = (byte) (bean.getSatFatInt());
        SendMcuBean sendMcuBean1 = new SendMcuBean();
        sendMcuBean1.setHex(CID, hex1);
        sendData(sendMcuBean1);

        byte[] hex2 = new byte[14];
        hex2[0] = BleNutritionNutrientConfig.SET_NUTRIENT;
        hex2[1] = 0x02;
        hex2[2] = (byte) (bean.getTransFatInt()<<16);
        hex2[3] = (byte) (bean.getTransFatInt()<<8);
        hex2[4] = (byte) (bean.getTransFatInt());
        hex2[5] = (byte) (bean.getCholesterolInt()<<16);
        hex2[6] = (byte) (bean.getCholesterolInt()<<8);
        hex2[7] = (byte) (bean.getCholesterolInt());
        hex2[8] = (byte) (bean.getNaInt()<<16);
        hex2[9] = (byte) (bean.getNaInt()<<8);
        hex2[10] = (byte) (bean.getNaInt());
        hex2[11] = (byte) (bean.getKInt()<<16);
        hex2[12] = (byte) (bean.getKInt()<<8);
        hex2[13] = (byte) (bean.getKInt());
        SendMcuBean sendMcuBean2 = new SendMcuBean();
        sendMcuBean2.setHex(CID, hex2);
        sendData(sendMcuBean2);

        byte[] hex3 = new byte[14];
        hex3[0] = BleNutritionNutrientConfig.SET_NUTRIENT;
        hex3[1] = 0x03;
        hex3[2] = (byte) (bean.getTotalCarbohydrateInt()<<16);
        hex3[3] = (byte) (bean.getTotalCarbohydrateInt()<<8);
        hex3[4] = (byte) (bean.getTotalCarbohydrateInt());
        hex3[5] = (byte) (bean.getFiberInt()<<16);
        hex3[6] = (byte) (bean.getFiberInt()<<8);
        hex3[7] = (byte) (bean.getFiberInt());
        hex3[8] = (byte) (bean.getSugarInt()<<16);
        hex3[9] = (byte) (bean.getSugarInt()<<8);
        hex3[10] = (byte) (bean.getSugarInt());
        hex3[11] = (byte) (bean.getProteinInt()<<16);
        hex3[12] = (byte) (bean.getProteinInt()<<8);
        hex3[13] = (byte) (bean.getProteinInt());
        SendMcuBean sendMcuBean3 = new SendMcuBean();
        sendMcuBean3.setHex(CID, hex3);
        sendData(sendMcuBean3);

    }





    public interface OnNutritionNutrientListener {

        /**
         * MCU 上报 BM 版本
         */
        void onBmVersion(String version);

        /**
         * MCU 上报设备支持的单位
         */
        void onSupportUnit(List<SupportUnitBean> list);

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
        void onWeight(int no, int weight, int unit, int decimal, int symbol, int type);

        /**
         * MCU 上报设置单位结果
         *
         * @param status 0：成功；1：失败；2：不支持
         */
        void onUnitResult(int status);

        /**
         * MCU 上报异常
         *
         * @param weightErr  0：正常；1：超重
         * @param batteryErr 0：正常；1：低电
         */
        void onErr(int weightErr, int batteryErr);


    }

    private OnNutritionNutrientListener mListener;

    public void setBleNutritionCallback(OnNutritionNutrientListener onNutritionNutrientListener) {
        mListener = onNutritionNutrientListener;
    }
}
