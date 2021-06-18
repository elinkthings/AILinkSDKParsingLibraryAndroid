package cn.net.aicare.modulelibrary.module.FoodTemp;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;

import java.math.BigDecimal;

public class FoodTempData extends BaseBleDeviceData {

    public static final int CID = FoodTempConfig.FOOD_TEMP;

    public FoodTempData(BleDevice bleDevice) {
        super(bleDevice);
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case FoodTempConfig.MCU_DEVICE:
                    // MCU上发设备信息
                    mcuDevice(hex);
                    break;
                case FoodTempConfig.MCU_RESULT:
                    // MCU上发数据
                    mcuResult(hex);
                    break;
                case FoodTempConfig.MCU_SET_TEMP_UNIT_RESULT:
                    // MCU回复设置温度单位结果
                    mcuSetTempUnitResult(hex);
                    break;
                case FoodTempConfig.STOP_ALERT_RESULT:
                    // MCU回复停止警报结果
                    mcuStopAlertResult(hex);
                    break;
                case FoodTempConfig.MCU_SET_TARGET_TEMP_RESULT:
                    // MCU回复设置目标结果
                    mcuSetTargetTempResult(hex);
                    break;
                case FoodTempConfig.MCU_SET_TIMING_RESULT:
                    // MCU回复设置定时结果
                    mcuSetTimingResult(hex);
                    break;
                case FoodTempConfig.OPEN_CLOSE_RESULT:
                    // MCU回复开关机结果
                    mcuOpenCloseResult(hex);
                    break;
                case FoodTempConfig.MCU_SYNC_TIME_RESULT:
                    // MCU同步时间结果
                    mcuSyncTimeResult(hex);
                    break;
                case FoodTempConfig.MCU_OPEN_CLOSE_PROBE_RESULT:
                    // MCU回复开启关闭探针结果
                    mcuOpenCloseProbeResult(hex);
                    break;
            }
        }
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {

    }

    private void mcuDevice(byte[] hex) {
        int probe = hex[1] & 0xff;
        int chargerState = hex[2] & 0xff;
        int battery = hex[3] & 0xff;
        int tempUnit = hex[4] & 0xff;
        int alertType = hex[5] & 0xff;
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuDevice(probe, chargerState, battery, tempUnit, alertType);
        }
    }

    public void mcuResult(byte[] hex) {
        int id = hex[1] & 0xff;
        int inDevice = hex[2] & 0xff;
        int curTempUnit = (((hex[3] & 0xff) << 8) | ((hex[4] & 0xff))) >> 15;
        int curTempSymbol = (((hex[3] & 0xff) << 8) | ((hex[4] & 0xff))) >> 14 & 0x01;
        int curTemp = (((hex[3] & 0xff) << 8) | ((hex[4] & 0xff))) & 0x3fff;
        int ambienceTempUnit = (((hex[5] & 0xff) << 8) | ((hex[6] & 0xff))) >> 15;
        int ambienceTempSymbol = (((hex[5] & 0xff) << 8) | ((hex[6] & 0xff))) >> 14 & 0x01;
        int ambienceTemp = (((hex[5] & 0xff) << 8) | ((hex[6] & 0xff))) & 0x3fff;
        int targetTempUnit = (((hex[7] & 0xff) << 8) | ((hex[8] & 0xff))) >> 15;
        int targetTempSymbol = (((hex[7] & 0xff) << 8) | ((hex[8] & 0xff))) >> 14 & 0x01;
        int targetTemp = (((hex[7] & 0xff) << 8) | ((hex[8] & 0xff))) & 0x3fff;
        int inMeat = (hex[9] & 0xff) & 0x03;
        int enableAlert = (hex[9] & 0xff) >> 2;
        int mode = hex[10] & 0xff;
        int timing = ((hex[11] & 0xff) << 8) | ((hex[12] & 0xff));
        int alertType = hex[13] & 0xff;

        if (curTempSymbol == 1) {
            curTemp *= -1;
        }
        if (ambienceTempSymbol == 1) {
            ambienceTemp *= -1;
        }
        if (targetTempSymbol == 1) {
            targetTemp *= -1;
        }

        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuResult(id, inDevice, curTemp, curTempUnit, ambienceTemp, ambienceTempUnit, targetTemp, targetTempUnit, inMeat, enableAlert, mode, timing, alertType);
        }
    }

    public void mcuSetTempUnitResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuSetTempUnitResult(result);
        }
    }

    public void mcuStopAlertResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuStopAlertResult(result);
        }
    }

    public void mcuSetTargetTempResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuSetTargetTempResult(result);
        }
    }

    public void mcuSetTimingResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuSetTimingResult(result);
        }
    }

    public void mcuOpenCloseResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuOpenCloseResult(result);
        }
    }

    public void mcuSyncTimeResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuSyncTimeResult(result);
        }
    }

    public void mcuOpenCloseProbeResult(byte[] hex) {
        int result = hex[1];
        if (mFoodTempCallback != null) {
            mFoodTempCallback.mcuOpenCloseProbeResult(result);
        }
    }

    /**
     * APP请求获取设备信息
     */
    public void appGetDevice() {
        byte[] hex = new byte[2];
        hex[0] = FoodTempConfig.APP_GET_DEVICE;
        hex[1] = 0x01;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP设置温度单位
     *
     * @param unit 0：℃；1：℉
     */
    public void appSetTempUnit(int unit) {
        byte[] hex = new byte[2];
        hex[0] = FoodTempConfig.APP_SET_TEMP_UNIT;
        hex[1] = (byte) unit;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP停止设备报警
     *
     * @param id 探针编号
     */
    public void appStopAlert(int id) {
        byte[] hex = new byte[2];
        hex[0] = FoodTempConfig.STOP_ALERT;
        hex[1] = (byte) id;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP设置目标温度
     *
     * @param id       探针编号
     * @param temp     温度
     * @param tempUnit 温度单位
     */
    public void appSetTargetTemp(int id, int temp, int tempUnit) {
        byte[] hex = new byte[4];
        hex[0] = FoodTempConfig.APP_SET_TARGET_TEMP;
        hex[1] = (byte) id;

        int symbol = temp < 0 ? 0 : 1;
        hex[2] = (byte) (((tempUnit << 15) | (symbol << 14) | temp) >> 8);
        hex[3] = (byte) (temp & 0x00ff);

        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP设置定时
     *
     * @param id     探针编号
     * @param minute 多少分钟
     */
    public void appSetTiming(int id, int minute) {
        byte[] hex = new byte[4];
        hex[0] = FoodTempConfig.APP_SET_TIMING;
        hex[1] = (byte) id;
        hex[2] = (byte) (minute >> 8);
        hex[3] = (byte) (minute & 0x00ff);

        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP控制设备开关机
     *
     * @param isOpen true：开机；false：关机
     */
    public void appOpenClose(boolean isOpen) {
        byte[] hex = new byte[2];
        hex[0] = FoodTempConfig.OPEN_CLOSE;
        hex[1] = (byte) (isOpen ? 0x01 : 0x00);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP同步时间
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @param second 秒
     * @param week   一周的第几天
     */
    public void appSyncTime(int year, int month, int day, int hour, int minute, int second, int week) {
        byte[] hex = new byte[8];
        hex[0] = FoodTempConfig.APP_SYNC_TIME;
        hex[1] = (byte) (year - 2000);
        hex[2] = (byte) month;
        hex[3] = (byte) day;
        hex[4] = (byte) hour;
        hex[5] = (byte) minute;
        hex[6] = (byte) second;
        hex[7] = (byte) week;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP开关探针
     *
     * @param id     探针编号
     * @param isOpen true：开；false：关
     */
    public void appOpenCloseProbe(int id, boolean isOpen) {
        byte[] hex = new byte[3];
        hex[0] = FoodTempConfig.APP_SYNC_TIME;
        hex[1] = (byte) (isOpen ? 0x01 : 0x00);
        hex[2] = (byte) id;
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
    public interface FoodTempCallback {
        /**
         * MCU上报设备信息
         *
         * @param probeNum     探针数量 1-8
         * @param chargerState 充电状态 0：未充电；1：在充电
         * @param battery      电量 1-100
         * @param tempUnit     温度单位 0：℃；1：℉
         * @param alertType    警报类型 0：无报警；1：设备低电
         */
        void mcuDevice(int probeNum, int chargerState, int battery, int tempUnit, int alertType);

        /**
         * MCU上发数据
         *
         * @param id               探针id 1-8
         * @param inDevice         探针是否插入设备 0：未启动；1：已启动
         * @param curTemp          当前温度
         * @param curTempUnit      当前温度单位
         * @param ambienceTemp     环境温度
         * @param ambienceTempUnit 环境温度单位
         * @param targetTemp       目标温度
         * @param targetTempUnit   目标温度单位
         * @param inMeat           探针是否插入肉里 0：未插入；1：已插入；2：不支持
         * @param enableAlert      是否启用警报 0：未启动；1：已启动；2：不支持
         * @param mode             模式
         * @param timing           定时
         * @param alertType        警报类型 0：无报警；1：温度达到报警；2：定时达到报警
         */
        void mcuResult(int id, int inDevice, int curTemp, int curTempUnit, int ambienceTemp, int ambienceTempUnit, int targetTemp, int targetTempUnit, int inMeat, int enableAlert, int mode, int timing, int alertType);

        /**
         * MCU回复设置单位结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuSetTempUnitResult(int result);

        /**
         * MCU回复停止报警结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuStopAlertResult(int result);

        /**
         * MCU回复停止报警结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuSetTargetTempResult(int result);

        /**
         * MCU回复设置定时结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuSetTimingResult(int result);

        /**
         * MCU回复开关机结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuOpenCloseResult(int result);

        /**
         * MCU回复同步时间结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuSyncTimeResult(int result);

        /**
         * MCU回复开关探针结果
         *
         * @param result 0：成功；1：失败；2：不支持
         */
        void mcuOpenCloseProbeResult(int result);
    }

    private FoodTempCallback mFoodTempCallback;

    public void setFoodTempCallback(FoodTempCallback foodTempCallback) {
        mFoodTempCallback = foodTempCallback;
    }
}
