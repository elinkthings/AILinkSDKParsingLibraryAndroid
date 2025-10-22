package cn.net.aicare.modulelibrary.module.EightBodyfatscale;


import com.elinkthings.httplibrary.eightAlgorithm.EightBodyFatScaleUtils;
import com.elinkthings.httplibrary.eightAlgorithm.UserAdcBean;
import com.elinkthings.httplibrary.eightAlgorithm.UserInfoBean;
import com.elinkthings.httplibrary.eightAlgorithm.oneEight.ReqOneEightItemBean;
import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;


import java.util.List;

/**
 * 八电极APP算法
 *
 * @author xing
 * @date 2024/08/28
 */
public class EightBodyFatBleDeviceData extends BaseBleDeviceData {

    private EightBodyFatCallback mEightBodyFatCallback;
    private final int mCid = EightBodyFatUtil.EIGHT_BODY_FAT_SCALE;

    private EightOneBodyFatAdcBean mEightOneBodyFatAdcBean;

    public EightBodyFatBleDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                if (mEightBodyFatCallback != null)
                    mEightBodyFatCallback.onVersion(version);
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mEightBodyFatCallback != null)
                    mEightBodyFatCallback.onSupportUnit(list);
            }
        });

    }


    public void setEightBodyFatCallback(EightBodyFatCallback eightBodyFatCallback) {
        mEightBodyFatCallback = eightBodyFatCallback;
    }

    private boolean mTestAdcStatus = false;

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mEightBodyFatCallback != null) {
            mEightBodyFatCallback.showData(BleStrUtils.byte2HexStr(hex));
        }
        int cmd = hex[0] & 0xff;
        int status = hex[1] & 0xff;

        switch (cmd) {
            case EightBodyFatUtil.WEIGHING:
                //0x01

                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onWeight(status, getWeight(hex), getUnit(hex), getDecimal(hex));
                    mEightBodyFatCallback.onState(cmd, status, -1);
                }

                break;
            case EightBodyFatUtil.IMPEDANCE:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, hex[2] & 0xff);
                }
                if (status == EightBodyFatUtil.IMPEDANCE_MEASUREMENT) {
                    //测量阻抗中,初始化阻抗对象
                    mTestAdcStatus = true;
                    mEightOneBodyFatAdcBean = new EightOneBodyFatAdcBean();
                } else if (status == EightBodyFatUtil.IMPEDANCE_SUCCESS || status == EightBodyFatUtil.IMPEDANCE_SUCCESS_MCU) {
                    mTestAdcStatus = true;
                    if (mEightOneBodyFatAdcBean != null) {
                        int part = hex[2] & 0xff;//部位
                        int algorithmsId = hex[7] & 0xff;//算法id
                        int adc = getAdc(hex);
                        mEightOneBodyFatAdcBean.setAlgorithmsId(algorithmsId);
                        switch (part) {
                            case 0x00:
                                //双脚阻抗
                                mEightOneBodyFatAdcBean.setAdcFoot(adc);
                                break;
                            case 0x01:
                                //双手阻抗
                                mEightOneBodyFatAdcBean.setAdcHand(adc);
                                break;
                            case 0x02:
                                //左手阻抗
                                mEightOneBodyFatAdcBean.setAdcLeftHand(adc);
                                break;
                            case 0x03:
                                //右手阻抗
                                mEightOneBodyFatAdcBean.setAdcRightHand(adc);
                                break;
                            case 0x04:
                                //左脚阻抗
                                mEightOneBodyFatAdcBean.setAdcLeftFoot(adc);
                                break;
                            case 0x05:
                                //右脚阻抗
                                mEightOneBodyFatAdcBean.setAdcRightFoot(adc);
                                break;
                            case 0x06:
                                //左全身阻抗
                                mEightOneBodyFatAdcBean.setAdcLeftBody(adc);
                                break;
                            case 0x07:
                                //右全身阻抗
                                mEightOneBodyFatAdcBean.setAdcRightBody(adc);
                                break;
                            case 0x08:
                                //右手左脚阻抗
                                mEightOneBodyFatAdcBean.setAdcRightHandLeftFoot(adc);
                                break;
                            case 0x09:
                                //左手右脚阻抗
                                mEightOneBodyFatAdcBean.setAdcLeftHandRightFoot(adc);
                                break;
                            case 0x0A:
                                //躯干阻抗
                                mEightOneBodyFatAdcBean.setAdcBody(adc);
                                break;
                        }
                    }

                } else if (status == EightBodyFatUtil.IMPEDANCE_FINISH) {
                    if (mTestAdcStatus) {
                        mTestAdcStatus = false;
                        if (mEightBodyFatCallback != null) {
                            mEightBodyFatCallback.onImpedance(mEightOneBodyFatAdcBean);
                        }
                    }
                }

                break;
            case EightBodyFatUtil.HEART_RATE:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, -1);
                }
                if (EightBodyFatUtil.HEART_RATE_SUCCESS == status) {
                    if (mEightBodyFatCallback != null) {
                        mEightBodyFatCallback.onHeartRate(hex[2] & 0xff);
                    }
                }

                break;
            case EightBodyFatUtil.TEMP_MEASUREMENT:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, -1, -1);
                }
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onTemp(hex[1] & 0xff, getTemp(hex), getTempUnit(hex), getTempDecimal(hex));
                }
                break;
            case EightBodyFatUtil.MEASUREMENT_END:
                //测量完成
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, -1, -1);
                }
                break;
            case EightBodyFatUtil.MUC_CALL_BACK_RESULT:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, hex[2] & 0xff);
                }
                break;
            case EightBodyFatUtil.ERROR_CODE:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, 0);
                }
                break;
        }
    }


    private float getWeight(byte[] hex) {
        if (hex.length >= 6) {
            int weightHigh = (hex[2] & 0xff) << 16;
            int weightMiddle = (hex[3] & 0xff) << 8;
            int weightLow = hex[4] & 0xff;
            int weight = weightHigh + weightMiddle + weightLow;


            int decimal = (hex[5] & 0xf0) >> 4;
            if (decimal == 1) {
                float myWeight = weight / 10f;
                return myWeight;
            } else if (decimal == 2) {
                return weight / 100f;
            } else if (decimal == 3) {
                return weight / 1000f;
            } else
                return weight;
//            return weight*(float)Math.pow(10,0-decimal);
        }

        return -1;
    }

    private int getDecimal(byte[] hex) {
        if (hex.length >= 6)
            return (hex[5] & 0xf0) >> 4;
        return -1;
    }

    private int getUnit(byte[] hex) {
        if (hex.length >= 6)
            return (hex[5] & 0x0f);
        return -1;
    }

    private int getAdc(byte[] hex) {
        if (hex.length >= 9) {
            int highAdc = (hex[3] & 0xff) << 24;
            int highAdc1 = (hex[4] & 0xff) << 16;
            int lowAdc = (hex[5] & 0xff) << 8;
            int lowAdc1 = (hex[6] & 0xff);

            return highAdc + highAdc1 + lowAdc + lowAdc1;
        } else
            return 0;
    }

    private float getTemp(byte[] hex) {
        if (hex.length >= 5) {
            int tempHigh = (hex[2] & 0xff) << 8;
            int tempLow = hex[3] & 0xff;
            int temp = tempHigh + tempLow;
            int decimal = (hex[4] & 0xf0) >> 4;
            if (decimal == 1)
                return temp / 10f;
            else if (decimal == 2)
                return temp / 100f;
            else if (decimal == 3)
                return temp / 1000f;
            else return temp;
        }
        return -1;
    }

    private int getTempDecimal(byte[] hex) {
        if (hex.length >= 5)
            return (hex[4] & 0xf0) >> 4;
        return -1;
    }

    private int getTempUnit(byte[] hex) {
        if (hex.length >= 5)
            return (hex[4] & 0x0f);
        return -1;
    }


    public interface EightBodyFatCallback {
        /**
         * 测量状态
         * Measurement status
         *
         * @param type      测量的类型
         *                  Type of measurement
         * @param typeState 该类型的状态
         *                  Status of the type
         * @param result    结果
         *                  result
         */
        void onState(int type, int typeState, int result);

        /**
         * 体重数据接口
         * Weight data interface
         *
         * @param state   测量状态 1实时体重 2稳定体重
         *                Measurement status 1 Real-time weight 2 Stable weight
         * @param weight  体重
         *                weight
         * @param unit    单位
         *                unit
         * @param decimal 小数点位
         *                decimal
         */
        void onWeight(int state, float weight, int unit, int decimal);

        /**
         * 阻抗
         * impedance
         *
         * @param adcBean 阻抗豆
         */
        void onImpedance(EightOneBodyFatAdcBean adcBean);

        /**
         * 心率
         * heartRate
         *
         * @param heartRate heartRate
         */
        void onHeartRate(int heartRate);

        /**
         * 温度
         * temperature
         *
         * @param sign    正负
         *                Positive and negative
         * @param temp    温度
         *                temperature
         * @param unit    单位
         *                unit
         * @param decimal 小数点位
         *                decimal
         */
        void onTemp(int sign, float temp, int unit, int decimal);

        /**
         * 获取到版本号
         * Get the version number
         *
         * @param version
         */
        void onVersion(String version);

        /**
         * 获取到支持的单位列表
         * Get the list of supported units
         *
         * @param list
         */
        void onSupportUnit(List<SupportUnitBean> list);


        /**
         * 体脂数据接口
         *
         * @param bodyFatBean 体脂数据 null代表解析数据失败
         */
        default void onBodyFatData(EightOneDataBodyFatBean bodyFatBean) {
        }


        /**
         * 体脂数据计算错误
         *
         * @param type 类型 0-网络异常,1-服务器返回错误,2-传入数据有误
         * @param msg  消息
         */
        default void onBodyFatDataError(int type, String msg) {

        }


        void showData(String data);

    }


    public void setWeightUnit(int unit) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x03;
        bytes[2] = (byte) unit;
        bytes[3] = 0;
        smb.setHex(mCid, bytes);
        sendData(smb);
    }

    public void setTempUnit(int unit) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x02;
        bytes[2] = (byte) unit;
        bytes[3] = 0;
        smb.setHex(mCid, bytes);
        sendData(smb);
    }

    public void setCalibration() {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x01;
        bytes[2] = 0;
        bytes[3] = 0;
        smb.setHex(mCid, bytes);
        sendData(smb);
    }

    public void getUnitList() {
        byte[] sendData = new byte[]{44, 1};
        sendData(new SendBleBean(sendData));
    }

    public int getCid() {
        if (mBleDevice != null) {
            return mBleDevice.getCid();
        }
        return mCid;
    }

    public int getVid() {
        if (mBleDevice != null) {
            return mBleDevice.getVid();
        }
        return 0;
    }

    public int getPid() {
        if (mBleDevice != null) {
            return mBleDevice.getPid();
        }
        return 0;
    }

    private EightBodyFatScaleUtils mEightBodyFatScaleUtils;

    public void getOneEightBodyData(int sex, int age, float weightKg, int heightCm, EightOneBodyFatAdcBean adcBean) {
        if (mEightBodyFatScaleUtils==null) {
            mEightBodyFatScaleUtils = new EightBodyFatScaleUtils();
        }
        UserInfoBean userInfoBean = new UserInfoBean(age,sex,heightCm,weightKg);
        UserAdcBean userAdcBean = new UserAdcBean(adcBean.getAdcBody(), adcBean.getAdcLeftHand(),adcBean.getAdcLeftFoot(), adcBean.getAdcRightHand(), adcBean.getAdcRightFoot());
        mEightBodyFatScaleUtils.getOneEightBodyData(mBleDevice, userInfoBean, userAdcBean, new EightBodyFatScaleUtils.OnOneEightBodyFatListener() {
            @Override
            public void onBodyFatDataSuccess(ReqOneEightItemBean bean) {
                EightOneDataBodyFatBean eightOneDataBodyFatBean = getEightOneDataBodyFatBean(adcBean.getAlgorithmsId(), bean);
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onBodyFatData(eightOneDataBodyFatBean);
                }
            }

            @Override
            public void onBodyFatDataError(int code, String msg) {
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onBodyFatDataError(code, msg);
                }
            }
        });

    }


    private EightOneDataBodyFatBean getEightOneDataBodyFatBean(int algorithms, ReqOneEightItemBean dataData) {
        EightOneDataBodyFatBean dataBodyFatBean = new EightOneDataBodyFatBean();
        dataBodyFatBean.setWeightKg(dataData.getWeightKg());
        dataBodyFatBean.setBmi(dataData.getBMI());
        dataBodyFatBean.setBfr(dataData.getBodyFatRate());
        dataBodyFatBean.setSfr(dataData.getBodyFatSubCutRate());
        dataBodyFatBean.setUvi(dataData.getVFAL());
        dataBodyFatBean.setRom(dataData.getMuscleRate());
        dataBodyFatBean.setBmr(dataData.getBMR());
        dataBodyFatBean.setBm(dataData.getBoneKg() + "");
        dataBodyFatBean.setVwc(dataData.getWaterRate());
        dataBodyFatBean.setBodyAge(dataData.getBodyAge());
        dataBodyFatBean.setPp(dataData.getProteinRate());
        dataBodyFatBean.setFatMassBody(String.valueOf(dataData.getBodyFatKg()));
        dataBodyFatBean.setFatMassLeftTop(String.valueOf(dataData.getBodyFatKgLeftArm()));
        dataBodyFatBean.setFatMassLeftBottom(String.valueOf(dataData.getBodyFatKgLeftLeg()));
        dataBodyFatBean.setFatMassRightTop(String.valueOf(dataData.getBodyFatKgRightArm()));
        dataBodyFatBean.setFatMassRightBottom(String.valueOf(dataData.getBodyFatKgRightLeg()));
        dataBodyFatBean.setArithmetic(algorithms);
        dataBodyFatBean.setBhSkeletalMuscleKg(String.valueOf(dataData.getSkeletalMuscleKg()));
        dataBodyFatBean.setMuscleMassRightTop(String.valueOf(dataData.getMuscleKgRightArm()));
        dataBodyFatBean.setMuscleMassRightBottom(String.valueOf(dataData.getMuscleKgRightLeg()));
        dataBodyFatBean.setMuscleMassLeftTop(String.valueOf(dataData.getMuscleKgLeftArm()));
        dataBodyFatBean.setMuscleMassLeftBottom(String.valueOf(dataData.getMuscleKgLeftLeg()));
        dataBodyFatBean.setMuscleMassBody(String.valueOf(dataData.getMuscleKgTrunk()));
        dataBodyFatBean.setAdcLeftHand(dataBodyFatBean.getAdcLeftHand());
        dataBodyFatBean.setAdcRightHand(dataBodyFatBean.getAdcRightHand());
        dataBodyFatBean.setAdcLeftFoot(dataBodyFatBean.getAdcLeftFoot());
        dataBodyFatBean.setAdcRightFoot(dataBodyFatBean.getAdcRightFoot());
        dataBodyFatBean.setAdcLeftBody(dataData.getZLeftBodyDeCode());
        dataBodyFatBean.setMuscleKg(dataData.getMuscleKg());
        dataBodyFatBean.setBodyFatFreeMassKg(dataData.getBodyFatFreeMassKg());
        dataBodyFatBean.setIdealWeightKg(dataData.getIdealWeightKg());
        return dataBodyFatBean;
    }
}
