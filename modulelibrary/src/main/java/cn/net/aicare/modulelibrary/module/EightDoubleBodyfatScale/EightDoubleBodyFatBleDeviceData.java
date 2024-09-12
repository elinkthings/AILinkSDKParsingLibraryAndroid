package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 八电极双频体脂秤(APP算法)
 *
 * @author xing
 * @date 2024/03/14
 */
public class EightDoubleBodyFatBleDeviceData extends BaseEightDoubleBodyFatData {

    private static BleDevice sBleDevice = null;
    private static EightDoubleBodyFatBleDeviceData sEightDoubleBodyFatBleDeviceData = null;

    private EightDoubleBodyFatBleDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mCid = EightDoubleBodyFatBleConfig.CID_APP;
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
     * @return {@link EightDoubleBodyFatBleDeviceData}
     */
    public static EightDoubleBodyFatBleDeviceData getInstance() {
        if (sEightDoubleBodyFatBleDeviceData == null) {
            throw new NullPointerException("EightDoubleBodyFatBleDeviceData is null");
        }
        return sEightDoubleBodyFatBleDeviceData;
    }


    /**
     * 初始化
     *
     * @param bleDevice ble设备
     * @return {@link EightDoubleBodyFatBleDeviceData}
     */
    public static EightDoubleBodyFatBleDeviceData init(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (sBleDevice == bleDevice) {
                if (sEightDoubleBodyFatBleDeviceData == null) {
                    sEightDoubleBodyFatBleDeviceData = new EightDoubleBodyFatBleDeviceData(bleDevice);

                }
            } else {
                sEightDoubleBodyFatBleDeviceData = new EightDoubleBodyFatBleDeviceData(bleDevice);
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
        int cmd = hex[0] & 0xff;
        int status = hex[1] & 0xff;

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
                int step = hex[1] & 0xff;
                if (mAdcList == null) {
                    mAdcList = new ArrayList<>();
                }
                if (step == 0x01) {
                    int frequency = hex[2] & 0xFF;
                    mEightDoubleBodyFatAdcBean = getEightDoubleBodyFatAdcBean(frequency);
                    mEightDoubleBodyFatAdcBean.setFrequencyId(frequency);
                    mEightDoubleBodyFatAdcBean.setAlgorithmsId(hex[3] & 0xff);
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

            case EightDoubleBodyFatBleConfig.CMD_MEASUREMENT_END:
                //测量完成
                if (mOnEightDoubleBodyFatCallback != null) {
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


}
