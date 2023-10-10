package cn.net.aicare.modulelibrary.module.babyBodyFat;

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
 * 2020/08/07<br>
 * 婴儿体脂两用秤
 */
public class BabyBodyFatDeviceData extends BaseBleDeviceData {
    private String TAG = BabyBodyFatDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private byte[] CID;
    private int mType = BabyBodyFatBleConfig.BABY_BODY_FAT;
    private static BleDevice mBleDevice = null;
    private static BabyBodyFatDeviceData mBabyDevice = null;
    private Object mImpedance;

    public static BabyBodyFatDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mBabyDevice == null) {
                    mBabyDevice = new BabyBodyFatDeviceData(bleDevice);

                }
            } else {
                mBabyDevice = new BabyBodyFatDeviceData(bleDevice);
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

    private BabyBodyFatDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {

        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    //------------------发送---------------


    /**
     * 设置单位
     * Set unit
     *
     * @param unitHeight 身高(height)
     * @param unitWeight 体重(weight)
     */
    public void setUnit(int unitHeight, int unitWeight) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[3];
        data[0] = BabyBodyFatBleConfig.SET_UNIT;
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
        data[0] = BabyBodyFatBleConfig.SET_CMD;
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
        }else {
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.onData(hex, mType);
                }
            });
        }
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {


    }

    //----------------解析数据------

    /**
     * 步骤
     */
    private int mStep = 0;

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
            case BabyBodyFatBleConfig.GET_WEIGHT:
                mStep = 1;
                getWeight(data);
                break;
            //实时重量
            case BabyBodyFatBleConfig.GET_WEIGHT_NOW:
                mStep = 0;
                getWeightNow(data);
                break;
            //身高稳定
            case BabyBodyFatBleConfig.GET_HEIGHT:
                getHeight(data);
                break;
            //身高实时
            case BabyBodyFatBleConfig.GET_HEIGHT_NOW:
                getHeightNow(data);
                break;
            //测阻抗中
            case BabyBodyFatBleConfig.GET_IMPEDANCE_TESTING:
                mStep = 3;
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onImpedanceTesting();
                    }
                });
                break;
            //测阻抗成功，带上阻抗数据
            case BabyBodyFatBleConfig.GET_IMPEDANCE_SUCCESS_DATA:
                mStep = 4;
                getImpedance(data);
                break;
            //测阻抗成功，带上阻抗数据，并使用 APP 算法(APP 会根
            //据 byte7 的算法标识进行计算)
            case BabyBodyFatBleConfig.GET_IMPEDANCE_SUCCESS:
                mStep = 4;
                getImpedanceAlgorithm(data);
                break;
            //测阻抗失败
            case BabyBodyFatBleConfig.GET_IMPEDANCE_FAIL:
                mStep = 4;
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onImpedanceFailure();
                    }
                });
                break;
            //测量完成
            case BabyBodyFatBleConfig.GET_TEST_FINISH:
                if (mStep < 4) {
                    BleLog.i("步骤可能有误");
                }
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onMeasurementCompleted();
                    }
                });

                break;

            //控制指令,0去皮,1锁定
            case BabyBodyFatBleConfig.GET_CMD:
                getCmd(data);
                break;
            //获取单位
            case BabyBodyFatBleConfig.GET_UNIT:
                getUnit(data);
                break;
            //返回错误信息
            case BabyBodyFatBleConfig.GET_ERR:
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
        if (data.length < 5) {
            BleLog.e("稳定重量异常");
            return;
        }
        int weight = ((data[1] & 0xff) << 16) + ((data[2] & 0xff) << 8) + (data[3] & 0xff);
        byte unit = data[4];
        boolean negative = ((data[5] >> 4) & 0x01) == 1;//是否为负数
        weight = negative ? -weight : weight;
        int decimal = BleStrUtils.getBits(data[4], 0, 4);//从0开始,取4个bit
        boolean stableStatus = true;
        int finalWeight = weight;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onWeight(finalWeight, decimal, unit, stableStatus);
            }
        });
    }

    /**
     * 实时重量
     */
    private void getWeightNow(byte[] data) {
        if (data.length < 5) {
            BleLog.e("实时重量异常");
            return;
        }
        int weight = ((data[1] & 0xff) << 16) + ((data[2] & 0xff) << 8) + (data[3] & 0xff);
        byte unit = data[4];
        boolean negative = ((data[5] >> 4) & 0x01) == 1;//是否为负数
        weight = negative ? -weight : weight;
        int decimal = BleStrUtils.getBits(data[4], 0, 4);//从0开始,取4个bit
        int finalWeight = weight;
        boolean stableStatus = false;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onWeight(finalWeight, decimal, unit, stableStatus);
            }
        });

    }

    /**
     * 身高稳定数据
     */
    private void getHeight(byte[] data) {
        if (data.length < 5) {
            BleLog.e("稳定身高异常");
            return;
        }
        int height = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];
        boolean stableStatus = true;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onHeight(height, decimal, unit, stableStatus);
            }
        });
    }

    /**
     * 身高实时数据
     */
    private void getHeightNow(byte[] data) {
        if (data.length < 5) {
            BleLog.e("实时身高异常");
            return;
        }
        int height = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];
        boolean stableStatus = false;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onHeight(height, decimal, unit, stableStatus);
            }
        });
    }


    /**
     * 阻抗测量成功
     */
    public void getImpedance(byte[] data) {
        //阻抗
        int adc= ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        int algorithm=(data[3] & 0xff);
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onImpedanceSuccess(false,adc, algorithm);
            }
        });
    }

    /**
     * 阻抗测量成功,并用算法计算
     */
    public void getImpedanceAlgorithm(byte[] data) {
        //阻抗
        int adc= ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        int algorithm=(data[3] & 0xff);
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.onImpedanceSuccess(true,adc, algorithm);
            }
        });
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


        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getErr(status);
            }
        });

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
         * 体重(Stabilize weight)
         * 0：kg
         * 1：斤
         * 2：lb
         * 3：oz
         * 4：st
         * 5：g
         *
         * @param weight       原始数据(Raw data)
         * @param decimal      小数点位(Decimal point)
         * @param unit         单位(unit)
         * @param stableStatus 是否稳定状态
         */
        default void onWeight(int weight, int decimal, byte unit, boolean stableStatus) {
        }


        /**
         * 身高(height)
         * 0：cm
         * 1：inch
         * 2：ft-in
         *
         * @param height       原始数据(Raw data)
         * @param decimal      小数点位(Decimal point)
         * @param unit         单位(unit)
         * @param stableStatus 是否稳定状态
         */
        default void onHeight(int height, int decimal, byte unit, boolean stableStatus) {
        }


        /**
         * 阻抗测量中
         */
        default void onImpedanceTesting() {
        }

        /**
         * 阻抗测量成功
         *
         * @param appAlgorithm 是否使用app算法
         * @param adc         阻抗值（精度为 1Ω）
         * @param algorithmId 体脂算法标识（如果使用 MCU 算法，则该值为 0，使用 APP
         *                    算法，则为 1~255）
         */
        default void onImpedanceSuccess(boolean appAlgorithm, int adc, int algorithmId) {
        }

        /**
         * 阻抗测量失败
         */
        default void onImpedanceFailure() {
        }

        /**
         * 测量完成
         */
        default void onMeasurementCompleted() {
        }

        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         * <p>
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
         * <p>
         * Tare Tare <br>
         * 0: set successfully <br>
         * 1: Setting failed <br>
         * 2: Setting is not supported <br>
         */
        default void getTare(byte status) {
        }

        /**
         * 锁定 Hold<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         * <p>
         * Lock Hold <br>
         * 0: set successfully <br>
         * 1: Setting failed <br>
         * 2: Setting is not supported <br>
         */
        default void getHold(byte status) {
        }

        /**
         * 错误指令
         * 0：超重<br>
         * 1：称重抓 0 期间，重量不稳定<br>
         * 2：称重抓 0 失败<br>
         * <p>
         * Wrong instruction
         * 0: Overweight <br>
         * 1: During weighing grab 0, the weight is unstable <br>
         * 2: Weighing grab 0 failed <br>
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
