package cn.net.aicare.modulelibrary.module.ble_0020_hu_infrared_temp;

import com.pingwang.bluetoothlib.device.SendMcuBean;

/**
 * 华普红外测温仪数据指令工具类
 *
 * @author xing
 * @date 2025/03/27
 */
public class HpInfraredTempBleDataCmdUtils {

    /**
     * 华氏度转摄氏度
     */
    public static float FToC(float f) {
        float c = (f - 32) / 1.8f;
        return c;
    }

    /**
     * 摄氏度转华氏度,可能会出现小数点精度问题,例:xx.99999999
     */
    public static float CToF(float c) {
        float f = c * 1.8F + 32;
        return f;
    }


    /**
     * 获取摄氏度
     *
     * @param value 值
     * @param unit  单位
     * @return float
     */
    public static float getC(float value, int unit) {
        if (unit == HpInfraredTempBleConfig.TEMP_F) {
            return FToC(value);
        }
        return value;
    }

    /**
     * 单位转换
     *
     * @param value    值
     * @param unit     单位
     * @param goalUnit 目标单位
     * @return float
     */
    public static float UnitConversion(int value, int unit, int goalUnit) {
        if (unit == HpInfraredTempBleConfig.TEMP_F) {
            if (goalUnit == HpInfraredTempBleConfig.TEMP_C) {
                return FToC(value);
            } else {
                return value;
            }
        } else {
            if (goalUnit == HpInfraredTempBleConfig.TEMP_F) {
                return CToF(value);
            } else {
                return value;
            }

        }
    }

    /**
     * 单位str
     *
     * @param unit 单位
     * @return {@link String }
     */
    public static String UnitStr(int unit) {
        if (unit == HpInfraredTempBleConfig.TEMP_F) {
            return "℉";
        }
        return "℃";
    }


    /**
     * 模式str
     *
     * @param mode 模式
     * @return {@link String }
     */
    public static String ModeStr(int mode) {
        String modeStr = "";
        switch (mode) {
            case 1:
                modeStr = "MAX";
                break;
            case 2:
                modeStr = "AVG";
                break;
            case 3:
                modeStr = "MIN";
                break;
            case 4:
                modeStr = "DIF";
                break;
            case 5:
                modeStr = "LAL";
                break;
            case 6:
                modeStr = "HAL";
                break;
            case 7:
                modeStr = "EMS";
                break;
            case 8:
                modeStr = "HAL";
                break;
            case 9:
                modeStr = "LAL";
                break;
            case 0x0a:
                modeStr = "EMS";
                break;
        }
        return modeStr;

    }

    ;

    /**
     * 获取设备状态
     *
     * @return {@link SendMcuBean }
     */
    public static SendMcuBean checkDeviceStatus() {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = HpInfraredTempBleConfig.SEND_DEVICE_STATUS;
        bytes[1] = 0x01;
        smb.setHex(HpInfraredTempBleConfig.CID, bytes);
        return smb;
    }

    /**
     * APP 下发按键指令
     *
     * @param type 类型
     * @return {@link SendMcuBean }
     */
    public static SendMcuBean setCmd(int type) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = HpInfraredTempBleConfig.SEND_KEY_CMD;
        bytes[1] = (byte) type;
        smb.setHex(HpInfraredTempBleConfig.CID, bytes);
        return smb;
    }

    /**
     * APP 下发发射率
     *
     * @param emissivity 发射率
     * @return {@link SendMcuBean }
     */
    public static SendMcuBean setEmissivity(int emissivity) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = HpInfraredTempBleConfig.SEND_EMISSIVITY;
        bytes[1] = (byte) emissivity;
        smb.setHex(HpInfraredTempBleConfig.CID, bytes);
        return smb;
    }

    /**
     * APP 下发自动关机时间
     *
     * @param time 时间
     * @return {@link SendMcuBean }
     */
    public static SendMcuBean setAutoShutdown(int time) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[3];
        bytes[0] = HpInfraredTempBleConfig.SEND_AUTO_SHUTDOWN;
        bytes[1] = (byte) (time >> 8);
        bytes[2] = (byte) time;
        smb.setHex(HpInfraredTempBleConfig.CID, bytes);
        return smb;
    }

    /**
     * APP 下发高低温报警值
     *
     * @param unit    单位
     * @param signH 高温报警值正负标记(0=正数;1-负数)
     * @param signL 低温报警值正负标记(0=正数;1-负数)
     * @param tempH   高温
     * @param tempL   低温
     * @return {@link SendMcuBean }
     */
    public static SendMcuBean setWarm(int unit, int signH, int signL, int tempH, int tempL) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[6];
        bytes[0] = HpInfraredTempBleConfig.SEND_WARM_VALUE;
        int H = unit * 2 + signH;
        int L = unit * 2 + signL;
        int mTempH = Math.abs(tempH);
        int mTempL = Math.abs(tempL);
        bytes[1] = (byte) ((L << 4) + H);
        bytes[2] = (byte) (mTempH >> 8);
        bytes[3] = (byte) mTempH;
        bytes[4] = (byte) (mTempL >> 8);
        bytes[5] = (byte) mTempL;
        smb.setHex(HpInfraredTempBleConfig.CID, bytes);
        return smb;
    }


    /**
     * APP 获取设备参数
     *
     * @return {@link SendMcuBean }
     */
    public static SendMcuBean getDeviceParameter() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = HpInfraredTempBleConfig.GET_DEVICE_PARAMETER;
        bytes[1] = 0x01;
        sendMcuBean.setHex(HpInfraredTempBleConfig.CID, bytes);
        return sendMcuBean;
    }

}
