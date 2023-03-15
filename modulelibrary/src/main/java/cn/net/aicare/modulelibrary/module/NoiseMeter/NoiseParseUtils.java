package cn.net.aicare.modulelibrary.module.NoiseMeter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 噪声解析工具类
 *
 * @author xing
 * @date 2022/11/15
 */
public class NoiseParseUtils {


    public void toParseCmd(BleNoiseTLVBean bleNoiseTLVBean, OnNoiseMeterListener listener) {
        int cmd = bleNoiseTLVBean.getCmd();
        switch (cmd) {
            case NoiseMeterConfig.GROUP_GET_SUPPORT_LIST:
                if (listener instanceof OnDeviceSupportList) {
                    toParse02Cmd(bleNoiseTLVBean, (OnDeviceSupportList) listener);
                }
                break;
            case NoiseMeterConfig.GROUP_GET_DEVICE_STATE:
                if (listener instanceof OnSynDeviceState) {
                    toParse04Cmd(bleNoiseTLVBean, (OnSynDeviceState) listener);
                }
                break;
            case NoiseMeterConfig.GROUP_GET_PARAMETERS:
                if (listener instanceof OnSetDeviceInfo) {
                    toParse06Cmd(bleNoiseTLVBean, (OnSetDeviceInfo) listener);
                }
                break;
            default:
                break;
        }
    }


    private void toParse02Cmd(BleNoiseTLVBean bleNoiseTLVBean, OnDeviceSupportList listener) {
        int type = bleNoiseTLVBean.getType() & 0xff;
        byte[] value = bleNoiseTLVBean.getValue();
        switch (type) {
            case NoiseMeterConfig.TYPE_PROTOCOL_VERSION:
                String version = (value[0] & 0xff) + "";
                if (listener != null) {
                    listener.onProtocolVersion(version);
                }
                break;
            case NoiseMeterConfig.TYPE_FREQUENCY_AC:
                boolean isSupportA = (value[0] & 0x01) == 1;
                boolean isSupportC = (value[0] & 0x02) == 2;
                if (listener != null) {
                    listener.onSupportFrequencyAc(isSupportA, isSupportC);
                }
                break;
            case NoiseMeterConfig.TYPE_TEST_RANGE:
                int pointRange = (value[0] & 0x03);
                int min = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                int max = (value[3] & 0xff) + ((value[4] & 0xff) << 8);
                if (listener != null) {
                    listener.onSupportTestRange(pointRange, min, max);
                }
                break;
            case NoiseMeterConfig.TYPE_TEST_LEVEL:
                boolean isSupportLevel = (value[0] & 0x01) == 1;
                if (listener != null) {
                    listener.onSupportTestLevel(isSupportLevel);
                }
                break;
            case NoiseMeterConfig.TYPE_MAX_MIN:
                boolean isSupportMax = (value[0] & 0x01) == 1;
                if (listener != null) {
                    listener.onSupportMaxMinMode(isSupportMax);
                }
                break;
            case NoiseMeterConfig.TYPE_TIME_FAT_SLOW:
                boolean isSupportFast = (value[0] & 0x01) == 1;
                boolean isSupportSlow = (value[0] & 0x02) == 2;
                if (listener != null) {
                    listener.onSupportFatSlow(isSupportFast, isSupportSlow);
                }
                break;
            case NoiseMeterConfig.TYPE_VALUE_HOLD:
                boolean isSupportHold = (value[0] & 0x01) == 1;
                if (listener != null) {
                    listener.onSupportHold(isSupportHold);
                }
                break;
            case NoiseMeterConfig.TYPE_WARM:
                boolean isWarm = (value[0] & 0x01) == 1;
                int warmMin = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                int warmMax = (value[3] & 0xff) + ((value[4] & 0xff) << 8);
                if (listener != null) {
                    listener.onSupportAlarm(isWarm, warmMin, warmMax);
                }
                break;
            case NoiseMeterConfig.TYPE_BACK_LIGHT:
                boolean isSupportedBgL = (value[0] & 0x01) == 1;
                if (listener != null) {
                    listener.onSupportBackLight(isSupportedBgL);
                }
                break;
            case NoiseMeterConfig.TYPE_NOISE_VALUE:
                int valuePoint = (value[0] & 0x03);
                if (listener != null) {
                    listener.onSupportNoiseValuePoint(valuePoint);
                }
                break;
            case NoiseMeterConfig.TYPE_HISTORY:
                boolean isSupportedHistory = (value[0] & 0x01) == 1;
                if (listener != null) {
                    listener.onSupportHistory(isSupportedHistory);
                }
                break;
            case NoiseMeterConfig.TYPE_POWERED_BY:
                int batteryBattery = (value[0] & 0xff);
                if (listener != null) {
                    listener.onSupportPowered(batteryBattery);
                }
                break;
            default:
        }

    }


    /**
     * 设备支持列表
     *
     * @author xing
     * @date 2022/11/14
     */
    public interface OnDeviceSupportList extends OnNoiseMeterListener {

        /**
         * 协议版本
         *
         * @param version 版本
         */
        void onProtocolVersion(String version);

        /**
         * 支持频率交流
         *
         * @param supportA 支持A
         * @param supportC 支持C
         */
        void onSupportFrequencyAc(boolean supportA, boolean supportC);

        /**
         * 测量总范围
         *
         * @param point 点
         * @param min   最小值
         * @param max   马克斯
         */
        void onSupportTestRange(int point, int min, int max);

        /**
         * 在设备测量等级
         *
         * @param supportMode 支持模式
         */
        void onSupportTestLevel(boolean supportMode);

        /**
         * 在设备最大最小模式
         *
         * @param supportMode 是否支持模式
         */
        void onSupportMaxMinMode(boolean supportMode);

        /**
         * 时间加权(Fast/Slow)
         *
         * @param fastType fast类型是否支持
         * @param slowType slow类型是否支持
         */
        void onSupportFatSlow(boolean fastType, boolean slowType);

        /**
         * 数值保持
         *
         * @param supportMode 是否支持模式
         */
        void onSupportHold(boolean supportMode);

        /**
         * 支持报警
         * 报警功能
         *
         * @param supportMode 是否支持模式
         * @param min         最小值
         * @param max         最大值
         */
        void onSupportAlarm(boolean supportMode, int min, int max);

        /**
         * 在设备背光
         *
         * @param supportMode 是否支持模式
         */
        void onSupportBackLight(boolean supportMode);

        /**
         * 设备噪声值小数点
         *
         * @param valuePoint 小数点位数
         */
        void onSupportNoiseValuePoint(int valuePoint);

        /**
         * 设备历史记录
         *
         * @param supportMode 支持模式
         */
        void onSupportHistory(boolean supportMode);

        /**
         * 在设备供电
         *
         * @param type 0x01:锂电池供电;0x02:干电池供电;0x03:市电供电
         */
        void onSupportPowered(int type);
    }


    /**
     * 对parse04 cmd
     *
     * @param bleNoiseTLVBean BleNoiseTLVBean
     * @param listener        侦听器
     */
    private void toParse04Cmd(BleNoiseTLVBean bleNoiseTLVBean, OnSynDeviceState listener) {
        int type = bleNoiseTLVBean.getType() & 0xff;
        byte[] value = bleNoiseTLVBean.getValue();
        switch (type) {

            case NoiseMeterConfig.TYPE_FREQUENCY_AC:
                int acType = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSynFrequencyAc(acType);
                }
                break;
            case NoiseMeterConfig.TYPE_TEST_LEVEL:
                int level = (value[0] & 0xff);
                int min = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                int max = (value[3] & 0xff) + ((value[4] & 0xff) << 8);
                if (listener != null) {
                    listener.onSynTestLevel(level, min, max);
                }
                break;
            case NoiseMeterConfig.TYPE_MAX_MIN:
                int minMaxMode = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSynMaxMinMode(minMaxMode);
                }
                break;
            case NoiseMeterConfig.TYPE_TIME_FAT_SLOW:
                int fastSlowMode = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSynFatSlow(fastSlowMode);
                }
                break;
            case NoiseMeterConfig.TYPE_VALUE_HOLD:
                boolean isHold = (value[0] & 0x01) == 1;
                int holdValue = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                if (listener != null) {
                    listener.onSynHold(isHold, holdValue);
                }
                break;
            case NoiseMeterConfig.TYPE_WARM:
                boolean isWarmSwitch = (value[0] & 0x01) == 1;
                boolean isWarming = (value[1] & 0x01) == 1;
                if (listener != null) {
                    listener.onSynAlarm(isWarmSwitch, isWarming);
                }
                break;
            case NoiseMeterConfig.TYPE_BACK_LIGHT:
                int bgState =  value[0] & 0xFF;
                if (listener != null) {
                    listener.onSynBackLight(bgState);
                }
                break;
            case NoiseMeterConfig.TYPE_NOISE_VALUE:
                int noiseState = (value[0] & 0x03);
                int valueInt = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                if (listener != null) {
                    listener.onSynNoiseValuePoint(noiseState, valueInt);
                }
                break;

            case NoiseMeterConfig.TYPE_POWERED_BY:
                boolean isCharge = (value[0] & 0x01) == 1;
                boolean isLower = ((value[0] & 0x02) >> 1) == 1;
                int batter = (value[1] & 0xff);
                if (listener != null) {
                    listener.onSynPowered(isCharge, isLower, batter);
                }
                break;
            default:
        }

    }


    /**
     * 设备返回的状态指令
     *
     * @author xing
     * @date 2022/11/14
     */
    public interface OnSynDeviceState extends OnNoiseMeterListener {


        /**
         * 在设置频率交流
         *
         * @param value 0x01:使用 A 权;0x02:使用 C 权
         */
        void onSynFrequencyAc(int value);

        /**
         * 在设备测量等级
         *
         * @param level    当前等级
         * @param minLevel 最小等级
         * @param maxLevel 最大等级
         */
        void onSynTestLevel(int level, int minLevel, int maxLevel);

        /**
         * 在设备最大最小模式
         *
         * @param mode 0x00:正常模式;0x01:最小值测量模式;0x02:最大值测量模式
         */
        void onSynMaxMinMode(int mode);

        /**
         * 时间加权(Fast/Slow)
         *
         * @param mode 0x01:Fast 模式;0x02:Slow 模式
         */
        void onSynFatSlow(int mode);

        /**
         * 数值保持
         *
         * @param holdMode  模式;hold 模式
         * @param holdValue hold 的值(2bytes,小数点与声明一致, 正常模式时,该值无效,为 0
         */
        void onSynHold(boolean holdMode, int holdValue);

        /**
         * 在设置报警
         * 报警功能
         *
         * @param alarmStatus 报警状态
         * @param noiseAlarm  声音报警
         */
        void onSynAlarm(boolean alarmStatus, boolean noiseAlarm);

        /**
         * 在设备背光
         *
         * @param switchStatus 开关状态 0x00:未打开
         * 0x01:已打开
         */
        void onSynBackLight(int switchStatus);

        /**
         * 设备噪声值小数点
         * 数值不在量程内时,噪音值无效,显示上一次的值
         *
         * @param noiseState 噪声状态 0x00:数值有效
         *                   0x01:数值低于量程,显示 under 0x02:数值高于量程,显示 over
         * @param noiseValue 噪音值
         */
        void onSynNoiseValuePoint(int noiseState, int noiseValue);

        /**
         * 设备供电
         *
         * @param chargeState 充电状态
         * @param lowerState  低压状态
         * @param batter      面糊
         */
        void onSynPowered(boolean chargeState, boolean lowerState, int batter);
    }

    private List<NoiseMeterHistoryBean> mNoiseMeterHistoryBeans = new ArrayList<>();

    private void toParse06Cmd(BleNoiseTLVBean bleNoiseTLVBean, OnSetDeviceInfo listener) {
        int type = bleNoiseTLVBean.getType() & 0xff;
        byte[] value = bleNoiseTLVBean.getValue();
        switch (type) {

            case NoiseMeterConfig.TYPE_FREQUENCY_AC:
                int acState = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSetFrequencyAc(acState);
                }
                break;
            case NoiseMeterConfig.TYPE_TEST_LEVEL:
                int levelState = value[0] & 0xFF;
                int level = (value[1] & 0xff);
                int min = (value[2] & 0xff) + ((value[3] & 0xff) << 8);
                int max = (value[4] & 0xff) + ((value[5] & 0xff) << 8);
                if (listener != null) {
                    listener.onSetTestLevel(levelState, level, min, max);
                }
                break;
            case NoiseMeterConfig.TYPE_MAX_MIN:
                int minMaxState = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSetMaxMinMode(minMaxState);
                }
                break;
            case NoiseMeterConfig.TYPE_TIME_FAT_SLOW:
                int fatSlowState = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSetFatSlow(fatSlowState);
                }
                break;
            case NoiseMeterConfig.TYPE_VALUE_HOLD:
                boolean holdState = (value[0] & 0xFF) == 1;
                int holdValue = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                if (listener != null) {
                    listener.onSetHold(holdState, holdValue);
                }
                break;
            case NoiseMeterConfig.TYPE_WARM:
                boolean allSwitch = (value[0] & 0x01) == 1;
                boolean oneSwitch = (value[0] & 0x02) == 2;
                int alarmValue = (value[1] & 0xff) + ((value[2] & 0xff) << 8);
                if (listener != null) {
                    listener.onSetAlarm(allSwitch, oneSwitch, alarmValue);
                }
                break;
            case NoiseMeterConfig.TYPE_BACK_LIGHT:
                int backLightState = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSetBackLight(backLightState);
                }
                break;

            //历史记录
            case NoiseMeterConfig.TYPE_HISTORY:
                //总历史量
                int op = value[0] & 0xFF;
                long allSize = (value[1] & 0xFFL) | ((value[2] & 0xFFL) << 8) | ((value[3] & 0xFFL) << 16) | ((value[4] & 0xFFL) << 24);
                //当前历史量
                long size = (value[5] & 0xFFL) | ((value[6] & 0xFFL) << 8) | ((value[7] & 0xFFL) << 16) | ((value[8] & 0xFFL) << 24);
                byte[] bytes = new byte[value.length - 9];
                System.arraycopy(value, 9, bytes, 0, bytes.length);
                mNoiseMeterHistoryBeans.addAll(getHistoryList(bytes));
                if (mNoiseMeterHistoryBeans.size() >= allSize) {
                    //历史记录发送完成
                    if (listener != null) {
                        List<NoiseMeterHistoryBean> list = new ArrayList<>(mNoiseMeterHistoryBeans);
                        Collections.sort(list);//按时间戳排序
                        listener.onSetNoiseHistory(list);
                        mNoiseMeterHistoryBeans.clear();
                    }
                } else {
                    //需要继续请求记录

                }
                break;


            case NoiseMeterConfig.TYPE_BIND_DEVICE:
                int state = value[0] & 0xFF;
                if (listener != null) {
                    listener.onSetBindDevice(state);
                }
                break;
            default:
        }

    }


    private List<NoiseMeterHistoryBean> getHistoryList(byte[] bytes) {
        List<NoiseMeterHistoryBean> list = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes.length < i + 6) {
                return new ArrayList<>();
            }
            long time = (bytes[i] & 0xFFL) | ((bytes[i + 1] & 0xFFL) << 8) | ((bytes[i + 2] & 0xFFL) << 16) | ((bytes[i + 3] & 0xFFL) << 24);
            int value = (bytes[i + 4] & 0xFF) | ((bytes[i + 5] & 0xFF) << 8);
            byte stateB = bytes[i + 6];
            int ac = stateB & 0x03;
            boolean alarmState = ((stateB >> 2) & 0x01) == 1;
            int valueState = ((stateB >> 3) & 0x03);
            i += 6;
            list.add(new NoiseMeterHistoryBean(time, value, ac, alarmState, valueState));
        }
        return list;
    }


    public interface OnSetDeviceInfo extends OnNoiseMeterListener {


        /**
         * 在设置频率交流
         *
         * @param value 0x01:使用 A 权;0x02:使用 C 权
         */
        void onSetFrequencyAc(int value);

        /**
         * 测量等级及范围
         *
         * @param state    状态
         * @param level    当前等级
         * @param minLevel 最小等级
         * @param maxLevel 最大等级
         */
        void onSetTestLevel(int state, int level, int minLevel, int maxLevel);

        /**
         * 在设备最大最小模式
         *
         * @param mode 0x00:正常模式;0x01:最小值测量模式;0x02:最大值测量模式
         */
        void onSetMaxMinMode(int mode);

        /**
         * 时间加权(Fast/Slow)
         *
         * @param mode 0x01:Fast 模式;0x02:Slow 模式
         */
        void onSetFatSlow(int mode);

        /**
         * 数值保持
         *
         * @param holdState 是否为hold 模式
         * @param holdValue hold 的值(2bytes,小数点与声明一致, 正常模式时,该值无效,为 0
         */
        void onSetHold(boolean holdState, int holdValue);

        /**
         * 报警功能
         *
         * @param allSwitch     总开关;0=关闭报警 . 1=打开报警
         * @param currentSwitch 当前开关状态;0=none . 1=关闭当前报警
         * @param alarmValue    报警值:(小数点位数与前面的申明保持一致)
         */
        void onSetAlarm(boolean allSwitch, boolean currentSwitch, int alarmValue);

        /**
         * 在设备背光
         *
         * @param switchStatus 开关状态 0x00:关闭
         *                     0x01:打开
         */
        void onSetBackLight(int switchStatus);

        /**
         * 噪声历史
         *
         * @param list 列表
         */
        void onSetNoiseHistory(List<NoiseMeterHistoryBean> list);

        /**
         * 设备绑定
         *
         * @param state 状态
         */
        void onSetBindDevice(int state);
    }

}
