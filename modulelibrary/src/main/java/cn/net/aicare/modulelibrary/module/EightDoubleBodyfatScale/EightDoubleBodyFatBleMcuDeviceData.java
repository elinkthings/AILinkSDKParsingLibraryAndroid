package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 八电极双频体脂秤(MCU算法)
 *
 * @author xing
 * @date 2024/06/11
 */
public class EightDoubleBodyFatBleMcuDeviceData extends BaseEightDoubleBodyFatData {


    private static BleDevice sBleDevice = null;
    private static EightDoubleBodyFatBleMcuDeviceData sEightDoubleBodyFatBleDeviceData = null;

    private EightDoubleBodyFatBleMcuDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mCid = EightDoubleBodyFatBleConfig.CID_MCU;
        sBleDevice = bleDevice;
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                if (mOnEightDoubleBodyFatCallback != null)
                    mOnEightDoubleBodyFatCallback.onVersion(version);
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mOnEightDoubleBodyFatCallback != null)
                    mOnEightDoubleBodyFatCallback.onSupportUnit(list);
            }
        });

    }

    /**
     * 获取实例
     *
     * @return {@link EightDoubleBodyFatBleMcuDeviceData}
     */
    public static EightDoubleBodyFatBleMcuDeviceData getInstance() {
        if (sEightDoubleBodyFatBleDeviceData == null) {
            throw new NullPointerException("EightDoubleBodyFatBleMcuDeviceData is null");
        }
        return sEightDoubleBodyFatBleDeviceData;
    }


    /**
     * 初始化
     *
     * @param bleDevice ble设备
     * @return {@link EightDoubleBodyFatBleMcuDeviceData}
     */
    public static EightDoubleBodyFatBleMcuDeviceData init(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (sBleDevice == bleDevice) {
                if (sEightDoubleBodyFatBleDeviceData == null) {
                    sEightDoubleBodyFatBleDeviceData = new EightDoubleBodyFatBleMcuDeviceData(bleDevice);

                }
            } else {
                sEightDoubleBodyFatBleDeviceData = new EightDoubleBodyFatBleMcuDeviceData(bleDevice);
            }
        }
        return sEightDoubleBodyFatBleDeviceData;
    }




    private List<EightDoubleBodyFatAdcBean> mAdcList = null;
    private EightDoubleBodyFatAdcBean mEightDoubleBodyFatAdcBean;


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mOnEightDoubleBodyFatCallback != null) {
            mOnEightDoubleBodyFatCallback.showData(BleStrUtils.byte2HexStr(hex));
        }

        if (type != mCid) {
            return;
        }

        int cmd = hex[0] & 0xFF;
        int status = hex[1] & 0xFF;

        switch (cmd) {
            case EightDoubleBodyFatBleConfig.CMD_WEIGHING:
                if (hex.length >= 7) {
                    int weight = ((hex[2] & 0xFF) << 16) | ((hex[3] & 0xFF) << 8) | (hex[4] & 0xFF);
                    int decimal = (hex[5] & 0xF0) >> 4;
                    int unit = (hex[5] & 0x0F);
                    int batteryStatus = hex[6] & 0xFF;
                    int power = hex[7] & 0xFF;
                    if (mOnEightDoubleBodyFatCallback != null) {
                        mOnEightDoubleBodyFatCallback.onWeight(status, weight, decimal, unit, batteryStatus, power);
                    }
                }
                break;

            case EightDoubleBodyFatBleConfig.CMD_IMPEDANCE:
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onImpedanceStatus(status);
                }
                break;

            case EightDoubleBodyFatBleConfig.CMD_IMPEDANCE_DATA:
                int step = hex[1] & 0xFF;
                if (mAdcList == null) {
                    mAdcList = new ArrayList<>();
                }
                if (step == 0x01) {
                    int frequency = hex[2] & 0xFF;
                    int algorithmsId = hex[3] & 0xFF;
                    mEightDoubleBodyFatAdcBean = getEightDoubleBodyFatAdcBean(frequency);
                    mEightDoubleBodyFatAdcBean.setFrequencyId(frequency);
                    mEightDoubleBodyFatAdcBean.setAlgorithmsId(algorithmsId);
                    //获取右手阻抗
                    long adcRH = (hex[4] & 0xFFL) << 24 | (hex[5] & 0xFFL) << 16 | (hex[6] & 0xFFL) << 8 | (hex[7] & 0xFFL);
                    //获取左手阻抗
                    long adcLH = (hex[8] & 0xFFL) << 24 | (hex[9] & 0xFFL) << 16 | (hex[10] & 0xFFL) << 8 | (hex[11] & 0xFFL);
                    mEightDoubleBodyFatAdcBean.setAdcRightHand(adcRH);
                    mEightDoubleBodyFatAdcBean.setAdcLeftHand(adcLH);

                } else if (step == 0x02) {
                    if (mEightDoubleBodyFatAdcBean == null) {
                        return;
                    }
                    //获取右脚阻抗
                    long adcRF = (hex[2] & 0xFFL) << 24 | (hex[3] & 0xFFL) << 16 | (hex[4] & 0xFFL) << 8 | (hex[5] & 0xFFL);
                    //获取左脚阻抗
                    long adcLF = (hex[6] & 0xFFL) << 24 | (hex[7] & 0xFFL) << 16 | (hex[8] & 0xFFL) << 8 | (hex[9] & 0xFFL);
                    long adcBody = (hex[10] & 0xFFL) << 24 | (hex[11] & 0xFFL) << 16 | (hex[12] & 0xFFL) << 8 | (hex[13] & 0xFFL);
                    mEightDoubleBodyFatAdcBean.setAdcRightFoot(adcRF);
                    mEightDoubleBodyFatAdcBean.setAdcLeftFoot(adcLF);
                    mEightDoubleBodyFatAdcBean.setAdcBody(adcBody);
                    mAdcList.add(mEightDoubleBodyFatAdcBean);

                }

                break;

            case EightDoubleBodyFatBleConfig.CMD_HEART_RATE_DATA:
                //心率
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onHeartRate(status, hex[2] & 0xFF);
                }
                break;

            case EightDoubleBodyFatBleConfig.CMD_TEMPERATURE_DATA:
                //体温
                boolean negative = (hex[2]) == 0x01;
                int temp = (hex[2] & 0xFF) << 8 | (hex[3] & 0xFF);
                if (negative) {
                    temp = -temp;
                }
                int unit = (hex[4] & 0x0F);
                int decimal = (hex[4] & 0xF0) >> 4;
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onTemperature(temp, unit, decimal);
                }
                break;


            case EightDoubleBodyFatBleConfig.CMD_SYN_USER_DATA:
                //MCU 请求用户信息
                if (status == 0x01) {
                    if (mOnEightDoubleBodyFatCallback != null) {
                        mOnEightDoubleBodyFatCallback.onSyncUser();
                    }
                }
                break;
            case EightDoubleBodyFatBleConfig.CMD_BODY_FAT_DATA:
                //MCU 发送体脂数据
                parseBodyFatData(hex);
                break;
            case EightDoubleBodyFatBleConfig.CMD_COMPLETE_DATA:
                //MCU 请求补全体脂数据
                int id = (hex[1] & 0xFF) << 8 | (hex[2] & 0xFF);
                if (id == 0xFFFF) {
                    //需要补全数据
                    if (mEightDoubleMcuBodyFatBean == null) {
                        mEightDoubleMcuBodyFatBean = new EightDoubleMcuBodyFatBean();
                    }
                    mEightDoubleMcuBodyFatBean.setAddData(true);
                }
                break;


            case EightDoubleBodyFatBleConfig.CMD_MEASUREMENT_END:
                //测量完成
                if (mOnEightDoubleBodyFatCallback != null) {
                    if (mEightDoubleMcuBodyFatBean != null) {
                        mOnEightDoubleBodyFatCallback.onBodyFatMcu(mEightDoubleMcuBodyFatBean.copy());
                        mEightDoubleMcuBodyFatBean = null;
                    } else {
                        BleLog.i("测量完成,但没有体脂数据");
                        mOnEightDoubleBodyFatCallback.onBodyFatMcu(null);
                    }

                    if (mAdcList == null) {
                        mAdcList = new ArrayList<>();
                    }
                    mOnEightDoubleBodyFatCallback.onTestCompleted(new ArrayList<>(mAdcList));
                    mAdcList = null;
                }
                replyTestCompleted();
                break;


            case 0x82:
                //MCU 回复单位设置结果
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onUnitState(status);
                }
                break;
            case EightDoubleBodyFatBleConfig.CMD_MEASUREMENT_ERR:
                //错误码
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onErrCode(status);
                }
                break;

            default:
                break;
        }

    }


    private EightDoubleMcuBodyFatBean mEightDoubleMcuBodyFatBean;

    /**
     * 解析体脂数据
     *
     * @param hex 十六进制
     */
    private void parseBodyFatData(byte[] hex) {
        int step = hex[1] & 0xFF;
        if (mEightDoubleMcuBodyFatBean == null) {
            mEightDoubleMcuBodyFatBean = new EightDoubleMcuBodyFatBean();
        }
        switch (step) {

            case 0x01:
                int bmi = (hex[2] & 0xFF) << 8 | (hex[3] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBmi(bmi * 0.1D);
                int bfr = (hex[4] & 0xFF) << 8 | (hex[5] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBodyBfr(bfr * 0.1D);
                int rom = (hex[6] & 0xFF) << 8 | (hex[7] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBodyRom(rom * 0.1D);
                int lUpperFat = (hex[8] & 0xFF) << 8 | (hex[9] & 0xFF);
                mEightDoubleMcuBodyFatBean.setLeftUpperFatMass(lUpperFat * 0.1D);
                int rUpperFat = (hex[10] & 0xFF) << 8 | (hex[11] & 0xFF);
                mEightDoubleMcuBodyFatBean.setRightUpperFatMass(rUpperFat * 0.1D);

                break;

            case 0x02:
                int bodyFat = (hex[2] & 0xFF) << 8 | (hex[3] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBodyFatMass(bodyFat * 0.1D);
                int lLowerFat = (hex[4] & 0xFF) << 8 | (hex[5] & 0xFF);
                mEightDoubleMcuBodyFatBean.setLeftLowerFatMass(lLowerFat * 0.1D);
                int rLowerFat = (hex[6] & 0xFF) << 8 | (hex[7] & 0xFF);
                mEightDoubleMcuBodyFatBean.setRightLowerFatMass(rLowerFat * 0.1D);
                int lUpperMuscle = (hex[8] & 0xFF) << 8 | (hex[9] & 0xFF);
                mEightDoubleMcuBodyFatBean.setLeftUpperMuscleMass(lUpperMuscle * 0.1D);
                int rUpperMuscle = (hex[10] & 0xFF) << 8 | (hex[11] & 0xFF);
                mEightDoubleMcuBodyFatBean.setRightUpperMuscleMass(rUpperMuscle * 0.1D);
                break;

            case 0x03:
                int bodyMuscle = (hex[2] & 0xFF) << 8 | (hex[3] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBodyMuscleMass(bodyMuscle * 0.1D);
                int lLowerMuscle = (hex[4] & 0xFF) << 8 | (hex[5] & 0xFF);
                mEightDoubleMcuBodyFatBean.setLeftLowerMuscleMass(lLowerMuscle * 0.1D);
                int rLowerMuscle = (hex[6] & 0xFF) << 8 | (hex[7] & 0xFF);
                mEightDoubleMcuBodyFatBean.setRightLowerMuscleMass(rLowerMuscle * 0.1D);
                int bodyWater = (hex[8] & 0xFF) << 8 | (hex[9] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBodyWater(bodyWater * 0.1D);
                int bm = (hex[10] & 0xFF) << 8 | (hex[11] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBm(bm * 0.1D);
                break;
            case 0x04:
                int bmr = (hex[2] & 0xFF) << 8 | (hex[3] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBmr(bmr);
                int pp = (hex[4] & 0xFF) << 8 | (hex[5] & 0xFF);
                mEightDoubleMcuBodyFatBean.setPp(pp * 0.1D);
                int uvi = (hex[6] & 0xFF) << 8 | (hex[7] & 0xFF);
                mEightDoubleMcuBodyFatBean.setUvi(uvi);
                int sfr = (hex[8] & 0xFF) << 8 | (hex[9] & 0xFF);
                mEightDoubleMcuBodyFatBean.setSfr(sfr * 0.1D);
                int height = (hex[10] & 0xFF);
                mEightDoubleMcuBodyFatBean.setHeight(height);
                int bodyAge = (hex[11] & 0xFF);
                mEightDoubleMcuBodyFatBean.setBodyAge(bodyAge);
                break;

        }


    }


    /**
     * 八电极双频阻抗
     *
     * @param frequency 频率
     * @return {@link EightDoubleBodyFatAdcBean}
     */
    private EightDoubleBodyFatAdcBean getEightDoubleBodyFatAdcBean(int frequency) {
        //查询是否有相同的频率
        EightDoubleBodyFatAdcBean adcBean = null;
        for (EightDoubleBodyFatAdcBean eightDoubleBodyFatAdcBean : mAdcList) {
            if (eightDoubleBodyFatAdcBean.getFrequencyId() == frequency) {
                adcBean = eightDoubleBodyFatAdcBean;
            }
        }
        //没有相同的频率,新建一个
        if (adcBean == null) {
            adcBean = new EightDoubleBodyFatAdcBean();
        }
        return adcBean;
    }



    /**
     * 设置用户信息
     *
     * @param userId 用户id (0~15)
     * @param type   类型 0:普通用户 1:业余运动员 2:专业运动员 3:孕妇
     * @param age    年龄(0~127)
     * @param sex    性别 0:女 1:男
     * @param height 身高(cm)
     */
    public void setUserInfo(int userId, int type, int age, int sex, int height) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[5];
        bytes[0] = (byte) EightDoubleBodyFatBleConfig.CMD_SYN_USER_DATA;
        bytes[1] = (byte) 0x02;
        bytes[2] = (byte) ((type << 4) | userId);
        bytes[3] = (byte) ((sex << 7) | age);
        bytes[4] = (byte) height;
        smb.setHex(mCid, bytes);
        sendData(smb);
    }




}
