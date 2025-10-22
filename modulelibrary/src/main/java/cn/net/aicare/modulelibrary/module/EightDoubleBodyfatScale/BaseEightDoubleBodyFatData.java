package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

import com.elinkthings.httplibrary.eightAlgorithm.EightBodyFatScaleUtils;
import com.elinkthings.httplibrary.eightAlgorithm.UserAdcBean;
import com.elinkthings.httplibrary.eightAlgorithm.UserInfoBean;
import com.elinkthings.httplibrary.eightAlgorithm.dualEight.ReqDualEightItemBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleLog;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class BaseEightDoubleBodyFatData extends BaseBleDeviceData {

    protected OnEightDoubleBodyFatCallback mOnEightDoubleBodyFatCallback;

    protected int mCid = EightDoubleBodyFatBleConfig.CID_APP;

    public BaseEightDoubleBodyFatData(BleDevice bleDevice) {
        super(bleDevice);
    }


    public void setEightBodyFatCallback(OnEightDoubleBodyFatCallback onEightDoubleBodyFatCallback) {
        mOnEightDoubleBodyFatCallback = onEightDoubleBodyFatCallback;
    }


    /**
     * 回复测量完成
     */
    public void replyTestCompleted() {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x84;
        bytes[1] = 0x00;
        smb.setHex(mCid, bytes);
        sendData(smb);
        BleLog.i("回复测试完成.");
    }

    /**
     * 设置重量单位
     *
     * @param unit 单位
     *             0：kg
     *             1：斤
     *             4：st:lb
     *             6：lb
     */
    public void setWeightUnit(int unit) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x81;
        bytes[1] = (byte) unit;
        smb.setHex(mCid, bytes);
        sendData(smb);
    }

    /**
     * 获取单位列表
     */
    public void getUnitList() {
        sendData(new SendBleBean(BleSendCmdUtil.getInstance().getSupportUnit()));
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

    public void getDualEightBodyData(int sex, int age, float weightKg, int heightCm, List<EightDoubleBodyFatAdcBean> adcBeanList) {


        if (mEightBodyFatScaleUtils == null) {
            mEightBodyFatScaleUtils = new EightBodyFatScaleUtils();
        }
        UserInfoBean userInfoBean = new UserInfoBean(age, sex, heightCm, weightKg);
        UserAdcBean userAdcBean20 = new UserAdcBean();
        UserAdcBean userAdcBean100 = new UserAdcBean();
        for (EightDoubleBodyFatAdcBean adcBean : adcBeanList) {
            if (adcBean.getFrequencyId() == 0x01) {
                //20khz
                userAdcBean20.setAdcLeftArm(adcBean.getAdcLeftHand());
                userAdcBean20.setAdcRightArm(adcBean.getAdcRightHand());
                userAdcBean20.setAdcLeftLeg(adcBean.getAdcLeftFoot());
                userAdcBean20.setAdcRightLeg(adcBean.getAdcRightFoot());
                userAdcBean20.setAdcBody(adcBean.getAdcBody());

            } else if (adcBean.getFrequencyId() == 0x02) {
                //100khz
                userAdcBean100.setAdcLeftArm(adcBean.getAdcLeftHand());
                userAdcBean100.setAdcRightArm(adcBean.getAdcRightHand());
                userAdcBean100.setAdcLeftLeg(adcBean.getAdcLeftFoot());
                userAdcBean100.setAdcRightLeg(adcBean.getAdcRightFoot());
                userAdcBean100.setAdcBody(adcBean.getAdcBody());
            }
        }
        mEightBodyFatScaleUtils.getDualEightBodyData(mBleDevice, userInfoBean, userAdcBean20, userAdcBean100, new EightBodyFatScaleUtils.OnDualEightBodyFatListener() {
            @Override
            public void onBodyFatDataSuccess( ReqDualEightItemBean bean) {
                EightDoubleDataBodyFatBean eightDoubleDataBodyFatBean = getEightDoubleDataBodyFatBean(bean);
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onBodyFatData(eightDoubleDataBodyFatBean);
                }
            }

            @Override
            public void onBodyFatDataError(int code,  String msg) {
                if (mOnEightDoubleBodyFatCallback != null) {
                    mOnEightDoubleBodyFatCallback.onBodyFatDataError(code, msg);
                }
            }
        });
    }


    private EightDoubleDataBodyFatBean getEightDoubleDataBodyFatBean(ReqDualEightItemBean dataData) {
        EightDoubleDataBodyFatBean dataBodyFatBean = new EightDoubleDataBodyFatBean();
        dataBodyFatBean.setWeightKg(dataData.getWeightKg());
        dataBodyFatBean.setBmi(dataData.getBMI());
        dataBodyFatBean.setBfr(dataData.getBodyFatRate());
        dataBodyFatBean.setSfr(dataData.getBodyFatSubCutRate());
        dataBodyFatBean.setUvi(dataData.getVFAL());
        dataBodyFatBean.setRom(dataData.getMuscleRate());
        dataBodyFatBean.setBmr(dataData.getBMR());
        dataBodyFatBean.setBm(dataData.getBoneKg() + "");
        //需要转化为百分比
        float vwc = Float.parseFloat(String.format(Locale.US, "%.1f", (dataData.getWaterKg() / dataData.getWeightKg()) * 100D));
        dataBodyFatBean.setVwc(vwc);
        dataBodyFatBean.setBodyAge(dataData.getBodyAge());
        //需要转化为百分比
        float pp = Float.parseFloat(String.format(Locale.US, "%.1f", (dataData.getProteinKg() / dataData.getWeightKg()) * 100D));
        dataBodyFatBean.setPp(pp);
        dataBodyFatBean.setFatMassLeftTopKg(dataData.getBodyFatKgLeftArm());
        dataBodyFatBean.setFatMassRightTopKg(dataData.getBodyFatKgRightArm());
        dataBodyFatBean.setFatMassLeftBottomKg(dataData.getBodyFatKgLeftLeg());
        dataBodyFatBean.setFatMassRightBottomKg(dataData.getBodyFatKgRightLeg());
        dataBodyFatBean.setFatMassBodyKg(dataData.getBodyFatKgTrunk());
        dataBodyFatBean.setMuscleMassLeftTopKg(dataData.getMuscleKgLeftArm());
        dataBodyFatBean.setMuscleMassRightTopKg(dataData.getMuscleKgRightArm());
        dataBodyFatBean.setMuscleMassLeftBottomKg(dataData.getMuscleKgLeftLeg());
        dataBodyFatBean.setMuscleMassRightBottomKg(dataData.getMuscleKgRightLeg());
        dataBodyFatBean.setMuscleMassBodyKg(dataData.getMuscleKgTrunk());
        dataBodyFatBean.setSkeletalMuscleMass(dataBodyFatBean.getSkeletalMuscleMass());
        dataBodyFatBean.setBodyFatKgCon(dataData.getBodyFatKgCon());
        dataBodyFatBean.setMuscleKgCon(dataData.getMuscleKgCon());
        dataBodyFatBean.setWaterKg(dataData.getWaterKg());
        dataBodyFatBean.setCellMassKg(dataData.getCellMassKg());
        dataBodyFatBean.setWaterECWKg(dataData.getWaterECWKg());
        dataBodyFatBean.setWaterICWKg(dataData.getWaterICWKg());
        dataBodyFatBean.setMineralKg(dataData.getMineralKg());
        dataBodyFatBean.setWhr(dataData.getWHR());
        dataBodyFatBean.setObesity(dataData.getObesity());
        dataBodyFatBean.setDci(dataData.getDCI());
        dataBodyFatBean.setMineralKgStand(Arrays.toString(dataData.getMineralKgStand()));
        dataBodyFatBean.setWaterECWKgStand(Arrays.toString(dataData.getWaterECWKgStand()));
        dataBodyFatBean.setWaterICWKgStand(Arrays.toString(dataData.getWaterICWKgStand()));
        dataBodyFatBean.setCellMassKgStand(Arrays.toString(dataData.getCellMassKgStand()));
        dataBodyFatBean.setBodyScore(dataData.getBodyScore());
        dataBodyFatBean.setFatMassRightTopRate(dataData.getBodyFatRateRightArm());
        dataBodyFatBean.setFatMassLeftTopRate(dataData.getBodyFatRateLeftArm());
        dataBodyFatBean.setFatMassRightBottomRate(dataData.getBodyFatRateRightLeg());
        dataBodyFatBean.setFatMassLeftBottomRate(dataData.getBodyFatRateLeftLeg());
        dataBodyFatBean.setFatMassBodyRate(dataData.getBodyFatRateTrunk());
        dataBodyFatBean.setMusleMassRightTopRate(dataData.getMuscleRateRightArm());
        dataBodyFatBean.setMusleMassLeftTopRate(dataData.getMuscleRateLeftArm());
        dataBodyFatBean.setMusleMassRightBottomRate(dataData.getMuscleRateRightLeg());
        dataBodyFatBean.setMusleMassLeftBottomRate(dataData.getMuscleRateLeftLeg());
        dataBodyFatBean.setMusleMassBodyRate(dataData.getMuscleRateTrunk());
        dataBodyFatBean.setZ20KhzLeftArmDeCode(dataData.getZ20KhzLeftArmDeCode());
        dataBodyFatBean.setZ20KhzLeftLegDeCode(dataData.getZ20KhzLeftLegDeCode());
        dataBodyFatBean.setZ20KhzRightArmDeCode(dataData.getZ20KhzRightArmDeCode());
        dataBodyFatBean.setZ20KhzRightLegDeCode(dataData.getZ20KhzRightLegDeCode());
        dataBodyFatBean.setZ20KhzTrunkDeCode(dataData.getZ20KhzTrunkDeCode());
        dataBodyFatBean.setZ100KhzLeftArmDeCode(dataData.getZ100KhzLeftArmDeCode());
        dataBodyFatBean.setZ100KhzLeftLegDeCode(dataData.getZ100KhzLeftLegDeCode());
        dataBodyFatBean.setZ100KhzRightArmDeCode(dataData.getZ100KhzRightArmDeCode());
        dataBodyFatBean.setZ100KhzRightLegDeCode(dataData.getZ100KhzRightLegDeCode());
        dataBodyFatBean.setZ100KhzTrunkDeCode(dataData.getZ100KhzTrunkDeCode());
        return dataBodyFatBean;
    }


}
