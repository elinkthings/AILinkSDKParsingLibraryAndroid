package cn.net.aicare.modulelibrary.module.babyscale;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

/**
 * xing<br>
 * 2019/5/11<br>
 * 婴儿秤(Baby scale)
 */
public class BabyDeviceData extends BaseBleDeviceData {
    private String TAG = BabyDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private byte[] CID;
    private int mType = BabyBleConfig.BABY_SCALE;
    private static BleDevice mBleDevice = null;
    private static BabyDeviceData mBabyDevice = null;

    public static BabyDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mBabyDevice == null) {
                    mBabyDevice = new BabyDeviceData(bleDevice);

                }
            } else {
                mBabyDevice = new BabyDeviceData(bleDevice);
            }
        }
        return mBabyDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mBabyDevice != null) {
            mOnNotifyData = null;
            mBabyDevice = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private BabyDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {
        //TODO 可进行婴儿秤特有的初始化操作
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    //------------------发送---------------


    /**
     * 设置单位
     * Set unit
     * @param unitHeight 身高(height)
     * @param unitWeight 体重(weight)
     */
    public void setUnit(int unitHeight, int unitWeight) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[3];
        data[0] = BabyBleConfig.SET_UNIT;
        data[1] = (byte) unitHeight;
        data[2] = (byte) unitWeight;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    /**
     * 去皮(Tare)
     */
    public void setTare() {
        setCmd((byte) 0);
    }

    /**
     * 锁定(Hold)
     */
    public void setHold() {
        setCmd((byte) 1);
    }

    public void setCmd(byte type) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = BabyBleConfig.SET_CMD;
        data[1] = type;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    //-------------------接收-----------------

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mType == type) {
            if (hex == null) {
                BleLog.i(TAG, "接收到的数据:null");
                return;
            }
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据:" + data);
            //校验解析
            dataCheck(hex);
        }
    }



    //----------------解析数据------

    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        if (data == null)
            return;
        switch (data[0]) {
            //稳定重量
            case BabyBleConfig.GET_WEIGHT:
                getWeight(data);
                break;
            //实时重量
            case BabyBleConfig.GET_WEIGHT_NOW:
                getWeightNow(data);
                break;
            //身高
            case BabyBleConfig.GET_HEIGHT:
                getHeight(data);
                break;
            //控制指令,0去皮,1锁定
            case BabyBleConfig.GET_CMD:
                getCmd(data);
                break;
            //获取单位
            case BabyBleConfig.GET_UNIT:
                getUnit(data);
                break;
            //返回错误信息
            case BabyBleConfig.GET_ERR:
                getErr(data);
                break;
            default:

                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onData(data, mType);
                    }
                });

                break;
        }

    }

    /**
     * 稳定重量
     */
    private void getWeight(byte[] data) {
        int weight = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        boolean negative = ((data[4] >> 4) & 0x01) == 1;//是否为负数
        weight=negative?-weight:weight;
        int decimal = BleStrUtils.getBits(data[4], 0, 4);//从0开始,取4个bit
        byte unit = data[3];
        String unitStr = "kg";
        switch (unit) {
            case BabyBleConfig.BABY_KG:
                unitStr = "kg";
                break;
            case BabyBleConfig.BABY_FG:
                unitStr = "斤";
                break;
            case BabyBleConfig.BABY_LB:
                unitStr = "lb:oz";
                break;
            case BabyBleConfig.BABY_OZ:
                unitStr = "oz";
                break;
            case BabyBleConfig.BABY_ST:
                unitStr = "st:lb";
                break;
            case BabyBleConfig.BABY_G:
                unitStr = "g";
                break;
            case BabyBleConfig.BABY_LB_LB:
                unitStr = "LB";
                break;

        }

        int finalWeight = weight;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getWeight(finalWeight, decimal, unit);
            }
        });
    }

    /**
     * 实时重量
     */
    private void getWeightNow(byte[] data) {
        int weight = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        boolean negative = ((data[4] >> 4) & 0x01) == 1;//是否为负数
        weight=negative?-weight:weight;
        int decimal = BleStrUtils.getBits(data[4], 0, 4);//从0开始,取4个bit
        byte unit = data[3];
        String unitStr = "未知";
        switch (unit) {
            case BabyBleConfig.BABY_KG:
                unitStr = "kg";
                break;
            case BabyBleConfig.BABY_FG:
                unitStr = "斤";
                break;
            case BabyBleConfig.BABY_LB:
                unitStr = "lb:oz";
                break;
            case BabyBleConfig.BABY_OZ:
                unitStr = "oz";
                break;
            case BabyBleConfig.BABY_ST:
                unitStr = "st:lb";
                break;
            case BabyBleConfig.BABY_G:
                unitStr = "g";
                break;
            case BabyBleConfig.BABY_LB_LB:
                unitStr = "LB";
                break;

        }
        BleLog.i(TAG, "实时重量:" + weight + unitStr + "||decimal=" + decimal);
        int finalWeight = weight;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getWeightNow(finalWeight, decimal, unit);
            }
        });

    }

    /**
     * 身高
     */
    private void getHeight(byte[] data) {
        int height = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        int decimal = data[4];
        byte unit = data[3];
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getHeight(height, decimal, unit);
            }
        });
        String unitStr = "cm";
        switch (unit) {
            case BabyBleConfig.BABY_CM:
                unitStr = "cm";
                break;
            case BabyBleConfig.BABY_INCH:
                unitStr = "inch";
                break;
            case BabyBleConfig.BABY_FEET:
                unitStr = "foot";
                break;
        }

        BleLog.i(TAG, "身高:" + height + unitStr);
    }


    /**
     * 设置单位返回
     */
    private void getUnit(byte[] data) {
        //todo 获取设置的单位
        if (data.length > 1) {
            byte status = data[1];
            switch (status) {
                case CmdConfig.SETTING_SUCCESS:
                    BleLog.i(TAG, "设置单位成功");
                    break;
                case CmdConfig.SETTING_FAILURE:
                    BleLog.i(TAG, "设置单位失败");

                    break;
                case CmdConfig.SETTING_ERR:
                    BleLog.i(TAG, "设置单位错误");

                    break;
            }
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getUnit(status);
                }
            });

        }
    }


    /**
     * 指令控制(去皮,锁定)
     */
    private void getCmd(byte[] data) {
        byte status = data[2];//结果

        byte cmdType = data[1];
        if (cmdType == 0) {
            //todo 去皮
            getTare(status);
        } else if (cmdType == 1) {
            //todo 锁定
            getHold(status);
        }
    }


    /**
     * 去皮
     *
     * @param status 状态
     */
    private void getTare(byte status) {
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getTare(status);
            }
        });

    }

    /**
     * 锁定
     *
     * @param status 状态
     */
    private void getHold(byte status) {
        switch (status) {
            case 0:
                BleLog.i(TAG, "去皮成功");
                break;
            case 1:
                BleLog.i(TAG, "去皮失败");

                break;
            case 2:
                BleLog.i(TAG, "不支持");
                break;
        }
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getHold(status);
            }
        });
    }


    /**
     * 指令控制(去皮,锁定)
     */
    private void getErr(byte[] data) {
        byte status = data[1];
        String statusStr = "";
        if (status == 0) {
            statusStr = "超重";
        } else if (status == 1) {
            statusStr = "称重抓 0 期间，重量不稳定";
        } else if (status == 2) {
            statusStr = "称重抓 0 失败";
        }

        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getErr(status);
            }
        });

        BleLog.i(TAG, statusStr);
    }

    //----------------解析数据------


    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (!status) {
            disconnect();
        }
        BleLog.i(TAG, "握手:" + status);
    }


    public interface onNotifyData {
        /**
         * 不能识别的透传数据
         * Unrecognized pass-through data
         */
        default void onData(byte[] data, int type) {
        }

        /**
         * 稳定体重(Stabilize weight)
         * 0：kg
         * 1：斤
         * 2：lb
         * 3：oz
         * 4：st
         * 5：g
         *
         * @param weight  原始数据(Raw data)
         * @param decimal 小数点位(Decimal point)
         * @param unit    单位(unit)
         */
        default void getWeight(int weight, int decimal, byte unit) {
        }

        /**
         * 实时体重(Real-time weight)
         * 0：kg
         * 1：斤
         * 2：lb
         * 3：oz
         * 4：st
         * 5：g
         *
         * @param weight  原始数据(Raw data)
         * @param decimal 小数点位(Decimal point)
         * @param unit    单位(unit)
         */
        default void getWeightNow(int weight, int decimal, byte unit) {
        }

        /**
         * 身高(height)
         * 0：cm
         * 1：inch
         * 2：ft-in
         *
         * @param height  原始数据(Raw data)
         * @param decimal 小数点位(Decimal point)
         * @param unit    单位(unit)
         */
        default void getHeight(int height, int decimal, byte unit) {
        }

        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         *
         * Set unit back <br>
         * 0: set successfully <br>
         * 1: Setting failed <br>
         * 2: Setting is not supported <br>
         */
        default void getUnit(byte status) {
        }

        /**
         * 去皮 Tare<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         *
         * Tare Tare <br>
         * 0: set successfully <br>
         * 1: Setting failed <br>
         * 2: Setting is not supported <br>
         *
         */
        default void getTare(byte status) {
        }

        /**
         * 锁定 Hold<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         *
         * Lock Hold <br>
         * 0: set successfully <br>
         * 1: Setting failed <br>
         * 2: Setting is not supported <br>
         *
         */
        default void getHold(byte status) {
        }

        /**
         * 错误指令
         * 0：超重<br>
         * 1：称重抓 0 期间，重量不稳定<br>
         * 2：称重抓 0 失败<br>
         *
         * Wrong instruction
         * 0: Overweight <br>
         * 1: During weighing grab 0, the weight is unstable <br>
         * 2: Weighing grab 0 failed <br>
         *
         */
        default void getErr(byte status) {
        }
    }


    //-----------------set/get-----------------
    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }


    /**
     * 版本号接口
     * Version number interface
     */
    public void setOnBleVersionListener(OnBleVersionListener bleVersionListener) {
        if (mBleDevice != null)
            mBleDevice.setOnBleVersionListener(bleVersionListener);
    }

    /**
     * mcu相关的信息(电量,时间)
     * (mcu related information (power, time))
     */
    public void setOnMcuParameterListener(OnMcuParameterListener mcuParameterListener) {
        if (mBleDevice != null)
            mBleDevice.setOnMcuParameterListener(mcuParameterListener);
    }

    /**
     * 厂商信息(Manufacturer Information)
     */
    public void setOnCompanyListener(OnBleCompanyListener companyListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleCompanyListener(companyListener);
        }
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper());


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
