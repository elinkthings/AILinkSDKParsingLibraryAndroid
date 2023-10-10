package cn.net.aicare.modulelibrary.module.CoffeeScale;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

import java.math.BigDecimal;
import java.util.List;

public class CoffeeScaleData extends BaseBleDeviceData implements OnBleVersionListener {

    private static final int CID = CoffeeScaleConfig.COFFEE_SCALE;

    public CoffeeScaleData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(this);
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuSupportUnit(list);
        }
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case CoffeeScaleConfig.ON_RESULT:
                    // MCU上发设备信息
                    if (hex.length == 10) {
                        // 原始
                        mcuResult(hex);
                    } else if (hex.length == 11) {
                        // 增加异常
                        mcuResult2(hex);
                    } else if (hex.length == 12) {
                        // 增加异常，重量2byte->3byte
                        mcuResult3(hex);
                    }
                    break;
                case CoffeeScaleConfig.MCU_POWER:
                    // MCU上发电量信息
                    mcuPower(hex);
                    break;
                case CoffeeScaleConfig.SET_TIMING:
                    // MCU上发计时
                    mcuTiming(hex);
                    break;
                case CoffeeScaleConfig.SET_ALERT:
                    // MCU上发警报
                    mcuAlert(hex);
                    break;
                case CoffeeScaleConfig.STOP_ALERT:
                    // MCU停止警报
                    mcuStopAlert(hex);
                    break;
                case CoffeeScaleConfig.SET_ZERO_CALLBACK:
                    // 归零回调
                    mcuCallbackSetZero(hex);
                    break;
                case CoffeeScaleConfig.SET_WEIGHT_UNIT_CALLBACK:
                    // 设置重量单位回调
                    mcuCallbackSetWeightUnit(hex);
                    break;
                case CoffeeScaleConfig.SET_TEMP_UNIT_CALLBACK:
                    // 设置温度单位回调
                    mcuCallbackSetTempUnit(hex);
                    break;
                case CoffeeScaleConfig.SET_AUTO_SHUTDOWN_CALLBACK:
                    // 设置自动关机回调
                    mcuCallbackSetAutoShutdown(hex);
                    break;
                case CoffeeScaleConfig.SET_TIMING_CALLBACK:
                    // 设置计时功能回调
                    mcuCallbackSetTiming(hex);
                    break;
                case CoffeeScaleConfig.SET_ALERT_CALLBACK:
                    // 设置警报回调
                    mcuCallbackSetAlert(hex);
                    break;
                case CoffeeScaleConfig.STOP_ALERT_CALLBACK:
                    // 停止警报回调
                    mcuCallbackStopAlert(hex);
                    break;
                case CoffeeScaleConfig.BREW_MODE:
                    // 退出，进入冲煮模式
                    mcuBrewMode(hex);
                    break;
            }
        }
    }

    // MCU上报设备信息
    private void mcuResult(byte[] hex) {
        int weightType = hex[1] & 0xff;// 重量类型；1：稳定重量；2：实时重量
        int weightUnit = hex[2] & 0xff;// 重量单位；0：kg；1：斤；2：lb：oz；3：oz；4：st：lb；5：g；6：lb
        int weightSymbol = (hex[3] & 0xff & 0x10) >> 4;// 重量符号；0：正；1：负
        int weightDecimal = hex[3] & 0xff & 0x0f;
        int weightSource = (weightSymbol == 0 ? 1 : -1) * ((hex[4] & 0xff) << 8 | (hex[5] & 0xff));// 重量原始值
        float weight = getPreFloat(weightSource, weightDecimal);
        int tempUnit = hex[6] & 0xff;// 温度单位；0：℃；1：℉；0xff：不支持
        int tempSymbol = (hex[7] & 0xff & 0x10) >> 4;// 温度符号；0：正；1：负
        int tempDecimal = hex[7] & 0xff & 0x0f;
        int tempSource = (tempSymbol == 0 ? 1 : -1) * ((hex[8] & 0xff) << 8 | (hex[9] & 0xff));
        float temp = getPreFloat(tempSource, tempDecimal);
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuResult(weightType, weightUnit, weightDecimal, weightSource, weight, tempUnit, tempDecimal, tempSource, temp, 0xff);
        }
    }

    // MCU上报设备信息2，增加异常
    private void mcuResult2(byte[] hex) {
        int weightType = hex[1] & 0xff;// 重量类型；1：稳定重量；2：实时重量
        int weightUnit = hex[2] & 0xff;// 重量单位；0：kg；1：斤；2：lb：oz；3：oz；4：st：lb；5：g；6：lb
        int weightSymbol = (hex[3] & 0xff & 0x10) >> 4;// 重量符号；0：正；1：负
        int weightDecimal = hex[3] & 0xff & 0x0f;
        int weightSource = (weightSymbol == 0 ? 1 : -1) * ((hex[4] & 0xff) << 8 | (hex[5] & 0xff));// 重量原始值
        float weight = getPreFloat(weightSource, weightDecimal);
        int tempUnit = hex[6] & 0xff;// 温度单位；0：℃；1：℉；0xff：不支持
        int tempSymbol = (hex[7] & 0xff & 0x10) >> 4;// 温度符号；0：正；1：负
        int tempDecimal = hex[7] & 0xff & 0x0f;
        int tempSource = (tempSymbol == 0 ? 1 : -1) * ((hex[8] & 0xff) << 8 | (hex[9] & 0xff));
        float temp = getPreFloat(tempSource, tempDecimal);
        int err = hex[10] & 0xff;
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuResult(weightType, weightUnit, weightDecimal, weightSource, weight, tempUnit, tempDecimal, tempSource, temp, err);
        }
    }

    // MCU上报设备信息3，增加异常，重量3byte
    private void mcuResult3(byte[] hex) {
        int weightType = hex[1] & 0xff;// 重量类型；1：稳定重量；2：实时重量
        int weightUnit = hex[2] & 0xff;// 重量单位；0：kg；1：斤；2：lb：oz；3：oz；4：st：lb；5：g；6：lb
        int weightSymbol = (hex[3] & 0xff & 0x10) >> 4;// 重量符号；0：正；1：负
        int weightDecimal = hex[3] & 0xff & 0x0f;
        int weightSource = (weightSymbol == 0 ? 1 : -1) * ((hex[4] & 0xff) << 16 | (hex[5] & 0xff) << 8 | (hex[6] & 0xff));// 重量原始值
        float weight = getPreFloat(weightSource, weightDecimal);
        int tempUnit = hex[7] & 0xff;// 温度单位；0：℃；1：℉；0xff：不支持
        int tempSymbol = (hex[8] & 0xff & 0x10) >> 4;// 温度符号；0：正；1：负
        int tempDecimal = hex[8] & 0xff & 0x0f;
        int tempSource = (tempSymbol == 0 ? 1 : -1) * ((hex[9] & 0xff) << 8 | (hex[10] & 0xff));
        float temp = getPreFloat(tempSource, tempDecimal);
        int err = hex[11] & 0xff;
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuResult(weightType, weightUnit, weightDecimal, weightSource, weight, tempUnit, tempDecimal, tempSource, temp, err);
        }
    }

    // MCU上报电量信息
    private void mcuPower(byte[] hex) {
        int status = hex[1];
        int power = hex[2];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuPower(status, power);
        }
    }

    // MCU上报计时
    private void mcuTiming(byte[] hex) {
        int type = hex[1];// 0：正计时；1：倒计时
        int seconds = ((hex[2] & 0xff) << 8) | (hex[3] & 0xff);
        int op = hex[4];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuTiming(seconds, type, op);
        }
    }

    // MCU发出警报
    private void mcuAlert(byte[] hex) {
        int op = hex[1];
        int seconds = ((hex[2] & 0xff) << 8) | (hex[3] & 0xff);
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuAlert(seconds, op);
        }
    }

    // 设置归零回调
    private void mcuCallbackSetZero(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackSetZero(status);
        }
    }

    // 设置重量单位回调
    private void mcuCallbackSetWeightUnit(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackSetWeightUnit(status);
        }
    }

    // 设置温度单位回调
    private void mcuCallbackSetTempUnit(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackSetTempUnit(status);
        }
    }

    // 设置自动关机回调
    private void mcuCallbackSetAutoShutdown(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackSetAutoShutdown(status);
        }
    }

    // 设置自动关机回调
    private void mcuCallbackSetTiming(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackSetTiming(status);
        }
    }

    // 设置警报回调
    private void mcuCallbackSetAlert(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackSetAlert(status);
        }
    }

    // MCU停止警报
    private void mcuStopAlert(byte[] hex) {
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuStopAlert();
        }
    }

    // 设置停止警报回调
    private void mcuCallbackStopAlert(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuCallbackStopAlert(status);
        }
    }

    // 退出，进入冲煮模式
    private void mcuBrewMode(byte[] hex) {
        int status = hex[1];
        if (mCoffeeScaleCallback != null) {
            mCoffeeScaleCallback.mcuBrewMode(status);
        }
    }

    // 归零
    public void setZero() {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.SET_ZERO;
        hex[1] = 0x01;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 设置重量单位
    public void setWeightUnit(int weightUnit) {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.SET_WEIGHT_UNIT;
        hex[1] = (byte) weightUnit;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 设置温度单位
    public void setTempUnit(int tempUnit) {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.SET_TEMP_UNIT;
        hex[1] = (byte) tempUnit;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 设置自动关机时间
    public void setAutoShutdown(int seconds) {
        byte[] hex = new byte[3];
        hex[0] = CoffeeScaleConfig.SET_AUTO_SHUTDOWN;
        hex[1] = (byte) ((seconds & 0xff00) >> 8);
        hex[2] = (byte) (seconds & 0xff);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 设置计时
     *
     * @param seconds 多少秒
     * @param type    0：正计时；1：倒计时
     * @param op      1：计时；2：暂停；3：重置
     */
    public void setTiming(int seconds, int type, int op) {
        byte[] hex = new byte[5];
        hex[0] = CoffeeScaleConfig.SET_TIMING;
        hex[1] = (byte) type;
        hex[2] = (byte) ((seconds & 0xff00) >> 8);
        hex[3] = (byte) (seconds & 0xff);
        hex[4] = (byte) op;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 请求MCU电量
     */
    public void requestBattery() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().getBlePower());
        sendData(sendBleBean);
    }

    // 回复MCU设置计时结果
    public void callbackTiming(int status) {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.SET_TIMING_CALLBACK;
        hex[1] = (byte) status;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 设置警报
     *
     * @param seconds 报警持续时间
     * @param op      0：关闭；1：打开
     */
    public void setAlert(int seconds, int op) {
        byte[] hex = new byte[4];
        hex[0] = CoffeeScaleConfig.SET_ALERT;
        hex[1] = (byte) op;
        hex[2] = (byte) ((seconds & 0xff00) >> 8);
        hex[3] = (byte) (seconds & 0xff);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 回复MCU设置警报结果
    public void callbackAlert(int status) {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.SET_ALERT_CALLBACK;
        hex[1] = (byte) status;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 停止警报
    public void stopAlert() {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.STOP_ALERT;
        hex[1] = 0x01;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 回复MCU停止警报结果
    public void callbackStopAlert(int status) {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.STOP_ALERT_CALLBACK;
        hex[1] = (byte) status;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 退出，进入冲煮模式
    public void brewMode(int status) {
        byte[] hex = new byte[2];
        hex[0] = CoffeeScaleConfig.BREW_MODE;
        hex[1] = (byte) status;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    // 保留小数位
    private float getPreFloat(int source, int decimal) {
        float f = source * 1.0F;
        for (int i = 0; i < decimal; i++) {
            f /= 10F;
        }
        return getPreFloat(f, decimal);
    }

    // 保留小数位
    private float getPreFloat(float f, int decimal) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(decimal, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    // 接口回调
    public interface CoffeeScaleCallback {
        /**
         * 设备上报数据
         *
         * @param weightType    重量类型；1：稳定体重；2：实时体重
         * @param weightUnit    重量单位；0：kg；1：斤；2：lb：oz；3：oz；4：st：lb；5：g；6：lb
         * @param weightDecimal 重量小数点
         * @param weightSource  重量原始值
         * @param weight        重量
         * @param tempUnit      温度单位；0：℃；1：℉；0xff：不支持
         * @param tempDecimal   温度小数点
         * @param tempSource    温度原始值
         * @param temp          温度
         * @param err           异常
         */
        void mcuResult(int weightType, int weightUnit, int weightDecimal, int weightSource, float weight, int tempUnit, int tempDecimal, int tempSource, float temp, int err);

        /**
         * 设备上报电量
         *
         * @param status 电池状态；0：没有充电（默认）；1：充电中；2：充满电；3：充电异常
         * @param power  电量；0~100%
         */
        void mcuPower(int status, int power);

        /**
         * 设备上报计时
         *
         * @param seconds 秒
         * @param type    0：正计时；1：倒计时
         * @param op      1：计时；2：暂停；3：重置
         */
        void mcuTiming(int seconds, int type, int op);

        /**
         * 设备报警
         *
         * @param seconds 报警持续时间
         * @param op      0：关闭；1：打开
         */
        void mcuAlert(int seconds, int op);

        /**
         * 设备停止报警
         */
        void mcuStopAlert();

        /**
         * 设置归零回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackSetZero(int status);

        /**
         * 设置重量单位回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackSetWeightUnit(int status);

        /**
         * 设置温度单位回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackSetTempUnit(int status);

        /**
         * 设置自动关机回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackSetAutoShutdown(int status);

        /**
         * 设置计时功能回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackSetTiming(int status);

        /**
         * 设置报警功能回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackSetAlert(int status);

        /**
         * 停止报警功能回调
         *
         * @param status 状态；0：成功；1：失败；2：不支持
         */
        void mcuCallbackStopAlert(int status);

        /**
         * 退出，进入冲煮模式
         *
         * @param status 0：退出，1：进入
         */
        void mcuBrewMode(int status);

        /**
         * MCU回复设备支持的单位
         *
         * @param list 单位列表
         */
        void mcuSupportUnit(List<SupportUnitBean> list);
    }

    private CoffeeScaleCallback mCoffeeScaleCallback;

    public void setCoffeeScaleCallback(CoffeeScaleCallback coffeeScaleCallback) {
        mCoffeeScaleCallback = coffeeScaleCallback;
    }
}
