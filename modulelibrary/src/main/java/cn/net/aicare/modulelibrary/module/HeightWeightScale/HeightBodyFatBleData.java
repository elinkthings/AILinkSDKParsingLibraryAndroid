package cn.net.aicare.modulelibrary.module.HeightWeightScale;


import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

import java.util.List;

public class HeightBodyFatBleData extends BaseBleDeviceData {

    private static HeightBodyFatBleData mHeightBodyFatBleData;
    private BleDevice mBleDevice;
    private OnHeightBodyFatDataCallback mOnHeightBodyFatDataCallback;

    private HeightBodyFatBleData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = null;
        mBleDevice = bleDevice;
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //蓝牙版本号
                if (mOnHeightBodyFatDataCallback != null) {
                    mOnHeightBodyFatDataCallback.onVersion(version);
                }
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mOnHeightBodyFatDataCallback != null) {
                    mOnHeightBodyFatDataCallback.onSupportUnitList(list);
                }
            }
        });


    }

    public void setOnHeightBodyFatDataCallback(OnHeightBodyFatDataCallback onHeightBodyFatDataCallback) {
        mOnHeightBodyFatDataCallback = onHeightBodyFatDataCallback;
    }

    public void sendData(SendMcuBean sendMcuBean) {
        if (mBleDevice != null) {
            mBleDevice.sendData(sendMcuBean);
        }
    }

    public static void init(BleDevice bleDevice) {

        mHeightBodyFatBleData = null;
        mHeightBodyFatBleData = new HeightBodyFatBleData(bleDevice);
    }


    public static HeightBodyFatBleData getInstance() {
        return mHeightBodyFatBleData;
    }


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mOnHeightBodyFatDataCallback != null) {
            int cmd = hex[0] & 0xff;
            switch (cmd) {
                case HeightBodyFatBleUntils.MCU_SET_USER_RESULT:
                case HeightBodyFatBleUntils.MCU_SET_UNIT_RESULT:
                case HeightBodyFatBleUntils.MCU_SET_WORK_MODE_RESULT:
                case HeightBodyFatBleUntils.MUC_REQUEST_VOICE_SET_RESULT:
                    if (hex.length >= 2) {
                        mOnHeightBodyFatDataCallback.onMcuResult(cmd, hex[1] & 0xff);
                    }

                    break;
                case HeightBodyFatBleUntils.MCU_REQUEST_USER:
                    if (mOnHeightBodyFatDataCallback != null) {
                        mOnHeightBodyFatDataCallback.onMcuRequestUser();
                    }
                    break;
                case HeightBodyFatBleUntils.MCU_WORK_STATUS_RESULT:

                    if (hex.length >= 7) {
                        int mode = hex[1] & 0xff;
                        int battery = hex[2] & 0xff;
                        int batteryStatus = hex[3] & 0xff;
                        int weightUnit = hex[4] & 0xff;
                        int heightUnit = hex[5] & 0xff;
                        int voice = hex[6] & 0xff;
                        mOnHeightBodyFatDataCallback.onDeviceStatus(mode, battery, batteryStatus, weightUnit, heightUnit, voice);
                    }
                    break;
                case HeightBodyFatBleUntils.MCU_WEIGHT_STATUS_BODY_FAT_RESULT:
                    if (hex.length >= 8) {
                        int mode = hex[1] & 0xff;
                        int weightMode = hex[2] & 0xff;
                        int weightH = ((hex[3] & 0xff) << 16) & 0Xff0000;
                        int weightM = (hex[4] & 0xff) << 8;
                        int weightL = hex[5] & 0xff;
                        int weightDecimals = (hex[6] & 0xf0) >> 4;
                        int weightUnit = hex[6] & 0x0f;
                        mOnHeightBodyFatDataCallback.onWeightBodyFat(mode, weightMode, weightH + weightL + weightM, weightDecimals, weightUnit);
                    }
                    break;
                case HeightBodyFatBleUntils.MCU_WEIGHT_STATUS_WEIGHT_RESULT:
                case HeightBodyFatBleUntils.MCU_WEIGHT_STATUS_WEIGHT_HEIGHT_RESULT:
                    if (hex.length >= 8) {
                        int mode = hex[1] & 0xff;
                        int weightMode = hex[2] & 0xff;
                        int weightH = ((hex[3] & 0xff) << 16) & 0Xff0000;
                        int weightM = (hex[4] & 0xff) << 8;
                        int weightL = hex[5] & 0xff;
                        int weightDecimals = (hex[6] & 0xf0) >> 4;
                        int weightUnit = hex[6] & 0x0f;
                        mOnHeightBodyFatDataCallback.onWeight(mode, weightMode, weightH + weightL + weightM, weightDecimals, weightUnit);
                    }
                    break;

                case HeightBodyFatBleUntils.MCU_ADC_RESULT:
                    //兼容 之前版本adc 解析错误的问题
                    if (hex.length>=9){
                        int mode = hex[1] & 0xff;
                        int status = hex[2] & 0xff;
                        int adcType = hex[3] & 0xff;
                        long adcH =( (hex[4] & 0xffL) << 24)+((hex[5] & 0xffL)<<16);
                        long adcL = ((hex[6] & 0xff) << 8)+(hex[7] & 0xff);
                        int id = hex[8] & 0xff;
                        mOnHeightBodyFatDataCallback.onAdc(mode, status, adcType, adcH + adcL, id);
                    }else if (hex.length >= 8) {
                        int mode = hex[1] & 0xff;
                        int status = hex[2] & 0xff;
                        int adcType = hex[3] & 0xff;
                        int adcH = (hex[4] & 0xff) << 8;
                        int adcL = (hex[5] & 0xff);
                        int id = hex[6] & 0xff;
                        mOnHeightBodyFatDataCallback.onAdc(mode, status, adcType, adcH + adcL, id);
                    }


                    break;
                case HeightBodyFatBleUntils.MCU_HEART:
                    if (hex.length >= 5) {
                        int mode = hex[1] & 0xff;
                        int status = hex[2] & 0xff;
                        int heart = hex[3] & 0xff;
                        mOnHeightBodyFatDataCallback.onHeart(mode, status, heart);
                    }
                    break;
                case HeightBodyFatBleUntils.MCU_TEMP:
                    if (hex.length >= 7) {
                        int mode = hex[1] & 0xff;
                        int sign = hex[2] & 0xff;
                        int tempH = (hex[3] & 0xff) << 8;
                        int tempL = hex[4] & 0xff;
                        int decimals = (hex[5] & 0xf0) >> 4;
                        int unit = hex[5] & 0x0f;
                        mOnHeightBodyFatDataCallback.onTEMP(mode, sign, tempH + tempL, decimals, unit);
                    }
                    break;
                case HeightBodyFatBleUntils.MCU_HEIGHT:
                case HeightBodyFatBleUntils.MCU_WEIGHT_STATUS_HEIGHT:
                    if (hex.length >= 6) {
                        int mode = hex[1] & 0xff;
                        int heightH = (hex[2] & 0xff) << 8;
                        int heightL = hex[3] & 0xff;
                        int unit = hex[4] & 0xff;
                        int decimals = (hex[5] & 0xf0) >> 4;
                        mOnHeightBodyFatDataCallback.onHeight(mode, heightH + heightL, decimals, unit);
                    }

                    break;
                case HeightBodyFatBleUntils.MCU_BODY_FAT:
                    if (hex.length >= 14) {
                        int mode = hex[1] & 0xff;
                        if ((hex[2] & 0xff) == 0x01) {
                            int bfrH = (hex[3] & 0xff) << 8;
                            int bfrL = (hex[4] & 0xff);
                            int sfrH = (hex[5] & 0xff) << 8;
                            int sfrL = (hex[6] & 0xff);
                            int vfrH = (hex[7] & 0xff) << 8;
                            int vfrL = (hex[8] & 0xff);
                            int romH = (hex[9] & 0xff) << 8;
                            int romL = (hex[10] & 0xff);
                            int bmrH = (hex[11] & 0xff) << 8;
                            int bmrL = (hex[12] & 0xff);
                            int age = (hex[13] & 0xff);
                            mOnHeightBodyFatDataCallback.onBodyfat1(mode, bfrH + bfrL, sfrH + sfrL, vfrH + vfrL, romH + romL, bmrH + bmrL, age);
                        } else if ((hex[2] & 0xff) == 0x02) {
                            int bmH = (hex[3] & 0xff) << 8;
                            int bmL = (hex[4] & 0xff);
                            int waterH = (hex[5] & 0xff) << 8;
                            int waterL = (hex[6] & 0xff);
                            int ppH = (hex[7] & 0xff) << 8;
                            int ppL = (hex[8] & 0xff);
                            int bmiH = (hex[9] & 0xff) << 8;
                            int bmiL = (hex[10] & 0xff);
                            int heartH = (hex[11] & 0xff);
                            int obesityLevels = (hex[12] & 0xff);
                            mOnHeightBodyFatDataCallback.onBodyfat2(mode, bmH + bmL, waterH + waterL, ppH + ppL, bmiH + bmiL, heartH, obesityLevels);
                        }
                    }
                    break;
                case HeightBodyFatBleUntils.MCU_WEIGHT_STATUS_BABY_RESULT:
                    if (hex.length >= 11) {
                        int mode = hex[1] & 0xff;
                        int weightMode = hex[2] & 0xff;
                        int adultH = (hex[3] & 0xff) << 8;
                        int adultL = (hex[4] & 0xff);
                        int adultBabyH = (hex[5] & 0xff) << 8;
                        int adultBabyL = (hex[6] & 0xff);
                        int babyH = (hex[7] & 0xff) << 8;
                        int babyL = (hex[8] & 0xff);
                        int decimals = (hex[9] & 0xf0) >> 4;
                        int unit = (hex[9] & 0x0f);
                        mOnHeightBodyFatDataCallback.onWeightBaby(mode, weightMode, adultH + adultL, adultBabyH + adultBabyL,
                                babyH + babyL, decimals, unit);
                    }
                    break;

                case HeightBodyFatBleUntils.MCU_TEST_FINISH:
                    if (hex.length >= 3) {
                        mOnHeightBodyFatDataCallback.onFinish(hex[1] & 0xff);
                    }
                    break;
                case HeightBodyFatBleUntils.SET_VOICE_SET:
                    if (hex.length >= 3) {
                        mOnHeightBodyFatDataCallback.onVoice(hex[1] & 0xff);
                    }
                    break;
                case HeightBodyFatBleUntils.ERROR:
                    if (hex.length>=2){
                        mOnHeightBodyFatDataCallback.onError(hex[1] & 0xff);
                    }
            }
        }
    }


    public interface OnHeightBodyFatDataCallback {
        void onVersion(String version);

        void onSupportUnitList(List<SupportUnitBean> list);

        void onMcuResult(int type, int result);

        void onMcuRequestUser();

        void onDeviceStatus(int workMode, int battery, int batteryStatus, int weightUnit, int heightUnit, int voice);

        void onWeightBodyFat(int workMode, int weightMode, int weight, int decimals, int unit);

        void onWeightBaby(int workMode, int weightMode, int adultWeight, int adultBabyWeight, int babyWeight, int decimals, int unit);

        void onWeight(int workMode, int weightMode, int weight, int decimals, int unit);

        void onAdc(int workMode, int status, int adcType, long adc, int arithmetic);

        void onHeart(int workMode, int status, int heart);

        void onTEMP(int workMode, int sign, int temp, int decimals, int unit);

        void onHeight(int workMode, int height, int decimals, int unit);

        void onBodyfat1(int workMode, int bfr, int sfr, int vfr, int rom, int bmr, int age);

        void onBodyfat2(int workMode, int bm, int water, int pp, int bmi, int heart, int obesityLevels);

        void onVoice(int status);

        void onFinish(int workMode);
        void onError(int error);

    }

}
