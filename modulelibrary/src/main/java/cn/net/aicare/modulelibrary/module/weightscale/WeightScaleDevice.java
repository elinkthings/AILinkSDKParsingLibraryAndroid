package cn.net.aicare.modulelibrary.module.weightscale;

import android.util.Log;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.List;

/**
 * @auther ljl
 * on 2023/3/3
 */
public class WeightScaleDevice extends BaseBleDeviceData implements OnBleOtherDataListener {

    public final static int DEVICE_CID = 0x0056;

    private BleDevice mBleDevice;

    private static WeightScaleDevice mWeightScaleDevice;

    private OnWeightScaleDataListener mOnWeightScaleDataListener;

    public WeightScaleDevice(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        bleDevice.setOnBleOtherDataListener(this);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //获取版本号
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onVersion(version);
                }
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                //支持的单位列表
            }
        });
        bleDevice.setOnMcuParameterListener(new OnMcuParameterListener() {
            @Override
            public void onMcuBatteryStatus(int status, int battery) {
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onBattery(status, battery);
                }
            }

        });

    }

    public static void init(BleDevice bleDevice) {
        mWeightScaleDevice = null;
        mWeightScaleDevice = new WeightScaleDevice(bleDevice);
    }

    public static WeightScaleDevice getInstance() {
        return mWeightScaleDevice;
    }

    public interface OnWeightScaleDataListener {

        void onVersion(String version);

        void onBattery(int status, int battery);

        void onRealWeightData(int realStatus, int realWeight, int realPoint, int realUnit);

        void onStableWeightData(int stableStatus, int stableWeight, int stablePoint, int stableUnit);

        void onErrorCode(int errorCode);

        void onUnitResult(int unitResult);

        void onBmi(float bmi);

        void onMeasureOk();

        void onDataA6(String A6DataStr);

        void onDataA7(String A7DataStr);

        void onSupportUnit(List<SupportUnitBean> list);
    }

    public void setOnWeightScaleDataListener(OnWeightScaleDataListener onWeightScaleDataListener) {
        mOnWeightScaleDataListener = onWeightScaleDataListener;
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] data) {
        //其他数据
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type != DEVICE_CID) {
            BleLog.e("CID不正确");
            return;
        }

        if (mOnWeightScaleDataListener != null) {
            mOnWeightScaleDataListener.onDataA7(BleStrUtils.byte2HexStr(hex));
        }

        switch (hex[0]) {
            case WeightScaleConfig.WEIGHT_SCALE_REAL_WEIGHT:
                int realStatus = 0x01;
                //实时重量
                int realWeight = (hex[1] << 16) + (hex[2] << 8) + (hex[3] & 0xff);
                //重量数据精度--小数位数
                int realPoint = (hex[4] >> 4);
                //当前单位 0-kg 1-斤 4-st:lb 6-lb
                int realUnit = hex[4] & 0x0F;
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onRealWeightData(realStatus, realWeight, realPoint, realUnit);
                }
                break;
            case WeightScaleConfig.WEIGHT_SCALE_WEIGHT:
                int stableStatus = 0x02;
                //稳定重量
                int stableWeight = (hex[1] << 16) + (hex[2] << 8) + (hex[3] & 0xff);
                //重量数据精度--小数位数
                int stablePoint = (hex[4] >> 4);
                //当前单位 0-kg 1-斤 4-st:lb 6-lb
                int stableUnit = hex[4] & 0x0F;
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onStableWeightData(stableStatus, stableWeight, stablePoint, stableUnit);
                }
                break;
            case WeightScaleConfig.WEIGHT_SCALE_OK:
                //测量完成
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onMeasureOk();
                }
                break;
            case WeightScaleConfig.WEIGHT_SCALE_SYNC_BMI:
                //同步bmi
                int dataVersion = hex[1] & 0xff;
                float bmi = (float) ((hex[2] << 8) + (hex[3] & 0xff)) / 10;
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onBmi(bmi);
                }
                break;
            case (byte) WeightScaleConfig.WEIGHT_SCALE_SET_UNIT:
                //设置单位结果 0-成功 1-失败 2-不支持
                int unitResult = hex[1] & 0xff;
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onUnitResult(unitResult);
                }
                break;
            case (byte) WeightScaleConfig.WEIGHT_SCALE_ERROR_CODE:
                //设备上发错误码 1-超重
                int errorCode = hex[1] & 0xff;
                if (mOnWeightScaleDataListener != null) {
                    mOnWeightScaleDataListener.onErrorCode(errorCode);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {
        if (mOnWeightScaleDataListener != null) {
            mOnWeightScaleDataListener.onDataA6(BleStrUtils.byte2HexStr(hex));
        }
//        switch (hex[0]) {
//            case 0x38:
//                //请求APP同步时间
//                int quest = hex[1] & 0xff;
//                if (mOnWeightScaleDataListener != null) {
//                    mOnWeightScaleDataListener.onSyncTime(quest);
//                }
//                break;
//            case 0x2C:
//                //支持的单位 重量 0-不支持 1-支持
//                int weightStatus = hex[1] & 0xff;
//                break;
//            default:
//                break;
//        }
    }

    /**
     * 发送A7命令
     */
    private void sendCmdA7(byte[] bytes) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x56, bytes);
        sendData(sendMcuBean);
        Log.e("ljl", "sendCmdA7: " + BleStrUtils.byte2HexStr(bytes));
    }

    /**
     * 发送A6命令
     */
    private void sendCmdA6(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
        Log.e("ljl", "sendCmdA6: " + BleStrUtils.byte2HexStr(bytes));
    }

    /**
     * APP设置测量单位 A7
     * 体重单位：kg-0x00 斤-0x01 st:lb-0x04 lb-0x06
     */
    public void setScaleUnit(int unit) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x81;
        bytes[1] = (byte) unit;
        sendCmdA7(bytes);
    }

    /**
     * APP查询单位 A6
     */
    public void queryUnit() {
        sendCmdA6(BleSendCmdUtil.getInstance().getSupportUnit());
    }


    /**
     * APP同步时间
     */
    public void appSyncTime() {
        byte[] bytes=BleSendCmdUtil.getInstance().setDeviceTime();
        Log.e("ljl", "同步时间: " + BleStrUtils.byte2HexStr(bytes));
        sendCmdA6(bytes);
    }


    /**
     * 获取电量
     */
    public void getBattery() {
        sendCmdA6(BleSendCmdUtil.getInstance().getMcuBatteryStatus());
    }


}
