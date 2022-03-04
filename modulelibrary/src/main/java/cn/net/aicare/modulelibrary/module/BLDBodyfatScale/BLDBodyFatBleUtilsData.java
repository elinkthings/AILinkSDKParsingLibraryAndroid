package cn.net.aicare.modulelibrary.module.BLDBodyfatScale;


import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleSettingListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * 百利达体脂称
 */
public class BLDBodyFatBleUtilsData extends BaseBleDeviceData {


    private BleDevice mBleDevice = null;
    private volatile static BLDBodyFatBleUtilsData bodyfatble = null;


    private BLDBodyFatBleUtilsData(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback) {
        super(bleDevice);
        mBleDevice = bleDevice;
        this.mBleBodyFatCallback = bleBodyFatCallback;


        mBleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                //蓝牙版本号
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onVersion(version);
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onSupportUnit(list);
            }
        });
        mBleDevice.setOnMcuParameterListener(new OnMcuParameterListener() {
            @Override
            public void onMcuBatteryStatus(int status, int battery) {
                //d电量
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onMcuBatteryStatus(status, battery);
            }

            @Override
            public void onSysTime(int status, int[] times) {
                //模块返回的时间

            }
        });

        mBleDevice.setOnBleOtherDataListener(new OnBleOtherDataListener() {
            @Override
            public void onNotifyOtherData(byte[] data) {
            }
        });


        //通用指令的回调
        mBleDevice.setOnBleSettingListener(new OnBleSettingListener() {
            @Override
            public void OnSettingReturn(int cmdType, int cmdData) {


            }
        });

    }

    public static BLDBodyFatBleUtilsData getInstance() {
        return bodyfatble;
    }

    /**
     * wifi+ble
     *
     * @param bleDevice          {@link BleDevice} 蓝牙设备
     * @param bleBodyFatCallback {@link BleBodyFatCallback} 体脂秤接口回调
     */
    public static void init(BleDevice bleDevice, BleBodyFatCallback bleBodyFatCallback) {

        bodyfatble = new BLDBodyFatBleUtilsData(bleDevice, bleBodyFatCallback);
    }


    /**
     * 初始化
     *
     * @param bleDevice {@link BleDevice} 蓝牙设备
     */
    public static void init(BleDevice bleDevice) {
        init(bleDevice, null);
    }


    /**
     * 获取到蓝牙设备对象
     *
     * @return 蓝牙设备
     */
    public BleDevice getBleDevice() {
        WeakReference weakReference = new WeakReference(mBleDevice);
        return (BleDevice) weakReference.get();
    }


    @Override
    public void onNotifyData(byte[] hex, int type) {

        int cmd = hex[0] & 0xFF;

        if (mBleBodyFatCallback != null)
            mBleBodyFatCallback.onStatus(cmd);
        switch (cmd) {
            //实时体重
            case BLDBodyFatDataUtil.WEIGHT_TESTING:
                //稳定体重
            case BLDBodyFatDataUtil.WEIGHT_RESULT:

                if (mBleBodyFatCallback != null) {
                    mBleBodyFatCallback
                            .onWeightData(cmd, BLDBodyFatDataUtil.getInstance().getWeightSymbol(hex[1]),
                                    BLDBodyFatDataUtil.getInstance().getWeight(hex),
                                    BLDBodyFatDataUtil.getInstance().getWeightUnit(hex),
                                    BLDBodyFatDataUtil.getInstance().getWeightPoint(hex));
                }

                break;
            case BLDBodyFatDataUtil.TEMPERATURE:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onTempData(BLDBodyFatDataUtil.getInstance().getTemperatureSymbol(hex[1]),
                            BLDBodyFatDataUtil.getInstance().getTemperature(hex));
                break;

            //阻抗无算法位
            case BLDBodyFatDataUtil.IMPEDANCE_SUCCESS_DATA:
            case BLDBodyFatDataUtil.IMPEDANCE_SUCCESS:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.onAdc(BLDBodyFatDataUtil.getInstance().getImpedance(hex), 0);

                break;


            //体脂数据
            case BLDBodyFatDataUtil.BODY_FAT:
                if (hex.length == 13) {
                    if (mBleBodyFatCallback != null) {
                        int bfrH = (hex[1] & 0xff) << 8;
                        int bfrL = (hex[2] & 0xff);
                        int uviH = (hex[3] & 0xff) << 8;
                        int uviL = (hex[4] & 0xff);
                        int muscleH = (hex[5] & 0xff) << 8;
                        int muscleL = (hex[6] & 0xff);
                        int bmrH = (hex[7] & 0xff) << 8;
                        int bmrL = (hex[8] & 0xff);
                        int bwH = (hex[9] & 0xff) << 8;
                        int bwL = (hex[10] & 0xff);
                        int age = 0;
                        int bfrS = hex[11] & 0x0f;
                        int uviS = (hex[11] & 0xf0) >> 4;
                        int muscleS = hex[12] & 0x0f;
                        int bmrS = (hex[12] & 0xf0) >> 4;
                        mBleBodyFatCallback.onBodyFat(bfrH + bfrL, uviH + uviL, muscleH + muscleL,
                                bmrH + bmrL, bwH + bwL, age, bfrS, uviS, muscleS, bmrS);
                    }
                } else if (hex.length == 14)
                    if (mBleBodyFatCallback != null) {
                        int bfrH = (hex[1] & 0xff) << 8;
                        int bfrL = (hex[2] & 0xff);
                        int uviH = (hex[3] & 0xff) << 8;
                        int uviL = (hex[4] & 0xff);
                        int muscleH = (hex[5] & 0xff) << 8;
                        int muscleL = (hex[6] & 0xff);
                        int bmrH = (hex[7] & 0xff) << 8;
                        int bmrL = (hex[8] & 0xff);
                        int bwH = (hex[9] & 0xff) << 8;
                        int bwL = (hex[10] & 0xff);
                        int age = (hex[11] & 0xff);
                        int bfrS = hex[12] & 0x0f;
                        int uviS = (hex[12] & 0xf0) >> 4;
                        int muscleS = hex[13] & 0x0f;
                        int bmrS = (hex[13] & 0xf0) >> 4;
                        mBleBodyFatCallback.onBodyFat(bfrH + bfrL, uviH + uviL, muscleH + muscleL,
                                bmrH + bmrL, bwH + bwL, age, bfrS, uviS, muscleS, bmrS);
                    }
                break;


            case BLDBodyFatDataUtil.SET_UNIT_CALLBAKC:
                if (mBleBodyFatCallback != null)
                    mBleBodyFatCallback.setUnitCallback(BLDBodyFatDataUtil.getInstance().setUnitCallback(hex));
                break;

            case BLDBodyFatDataUtil.REQUESR_USER_INFO:
                if (hex.length >= 2) {
                    if (mBleBodyFatCallback != null) {
                        mBleBodyFatCallback.requestUserData(hex[1] & 0xff);
                    }
                }
                break;
            case BLDBodyFatDataUtil.ERROR_CODE:
                if (hex.length >= 2) {
                    //错误码
                    if (mBleBodyFatCallback != null)
                        mBleBodyFatCallback.onError((hex[1] & 0xff));
                }
                break;


        }
//         }
    }


    @Override
    public void onNotifyDataA6(byte[] hex) {

    }


    private BleBodyFatCallback mBleBodyFatCallback;


    public interface BleBodyFatCallback {
        /**
         * 单位如果返回的st:lb，体重的lb的值，需要把lb转为st:lb;
         *
         * @param status     状态 01实时体重 02 稳定体重
         * @param symbol     符号 1重量数据为负数 0 重量数据为正数
         * @param weight     体重  已转为浮点型
         * @param weightUnit 体重单位 {@link BLDBodyFatDataUtil.WeightUnit#KG}
         * @param decimals   小数点位
         *                   If the unit returns st: lb, the weight of lb value, you need to convert lb to st: lb;
         * @param status     Status 01 Real-time weight 02 Stable weight
         * @param symbol     symbol 01 minus 02 positive
         * @param weight     has been converted to floating point
         * @param weightUnit weight unit {@link BLDBodyFatDataUtil.WeightUnit#KG}
         * @param decimals   decimal point
         */
        void onWeightData(int status, int symbol, int weight, int weightUnit, int decimals);

        /**
         * @param symhol 符号 1 负数(minus)  0 正数(positive)
         * @param temp   温度  temperature
         *               精度(precision) 0.1℃
         */
        void onTempData(int symhol, int temp);

        /**
         * 测量状态
         * measuring condition
         *
         * @param status {@link BLDBodyFatDataUtil#WEIGHT_TESTING}
         *               Measurement status
         */
        void onStatus(int status);

        /**
         * 阻抗和算法
         *
         * @param adc         阻抗值
         * @param algorithmic 算法 0:无算法
         *                     
         *                    Impedance and algorithm
         * @param adc         impedance value
         * @param algorithmic algorithm 0: no algorithm
         */
        void onAdc(int adc, int algorithmic);


        /**
         * 体脂数据
         * 秤计算体脂数据才有返回
         * Body fat data
         * The balance calculates the body fat data only then has returns
         *
         * @param bf                 体脂( Body Fat )
         *                           精度(precision) 0.1%，范围(scope) 5.0～75.0%
         * @param vfg                内脏脂肪等级(Visceral fat grade)
         *                           精度(precision) 0.5，范围(scope) 1.0～59.0
         * @param muscleMassKg       肌肉量(Muscle mass)
         *                           精度(precision) 0.1kg
         * @param bmv                基础代谢量(Basal metabolic volume)
         *                           精度(precision) 1kcal
         * @param Bmr                体水分率(Body moisture rate)
         *                           精度(precision) 0.1%
         * @param age                体内年龄(Body age)
         *                           范围(scope) 18-90
         * @param bfStandard         体脂判定 (Body fat determination)
         *                           0：瘦(thin) 1：-标准(-standard) 2：+标准(+standard) 3：微胖(fat) 4：肥胖(obesity)
         * @param vfgStandard        内脏脂肪等级判定 (Visceral fat level determination)
         *                           0：标准(standard) 1：稍多(Slightly more) 2：过多(nimiety)
         * @param muscleMassStandard 肌肉量判定(Muscle mass determination)
         *                           0：少(less) 1：标准(standard) 2：多(much)
         * @param bmvStandard        基础代谢量判定(Determination of basal metabolism)
         *                           0：少(less) 1：标准(standard) 2：多(much)
         */
        void onBodyFat(int bf, int vfg, int muscleMassKg, int bmv, int Bmr, int age,
                       int bfStandard, int vfgStandard, int muscleMassStandard,
                       int bmvStandard);

        /**
         * 错误码
         * error code
         *
         * @param code 错误码 error code
         */
        void onError(int code);


        /**
         * 版本号
         * version number
         *
         * @param version 版本号
         */
        void onVersion(String version);

        /**
         * 支持的单位列表
         * List of supported units
         *
         * @param list 版本号
         */
        void onSupportUnit(List<SupportUnitBean> list);

        /**
         * 电量
         * Electricity
         *
         * @param status  电量的状态 battery status
         * @param battery 电量的百分比 percentage of power
         */
        void onMcuBatteryStatus(int status, int battery);


        /**
         * 设置单位回调
         * Set unit callback
         *
         * @param status 0：设置成功 1：设置失败 2：不支持设置  {@link BLDBodyFatDataUtil.UnitResult#SUCCESS}
         *               0: Setting succeeded 1: Setting failed 2: Not supported setting
         */
        void setUnitCallback(int status);

        /**
         * 请求用户
         * 被动的下发的用户 与同步用户列表和更新个人用户是不同。
         * Request user
         * Passively delivered users are different from synchronizing user lists and updating individual users.
         *
         * @param status 0x01：请求用户  0x03下发成功 0x04 下发用户失败
         *               0x01: Request user 0x03 successfully delivered 0x04 failed user delivery
         */
        void requestUserData(int status);


    }

}
