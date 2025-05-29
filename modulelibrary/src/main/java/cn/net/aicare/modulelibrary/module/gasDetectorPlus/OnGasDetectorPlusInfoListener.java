package cn.net.aicare.modulelibrary.module.gasDetectorPlus;

import java.util.List;
import java.util.Map;

import cn.net.aicare.modulelibrary.module.gasDetectorPlus.bean.GasAlarmInfoBean;
import cn.net.aicare.modulelibrary.module.gasDetectorPlus.bean.GasDetectorPlusCurrentInfoBean;

/**
 * 气体探测器信息监听器
 *
 * @author xing
 * @date 2024/09/05
 */
public interface OnGasDetectorPlusInfoListener {


    /**
     * 支持的气体类型(0x01)
     *
     * @param map key:气体类型{@link GasDetectorPlusBleConfig.GasType},value:是否支持
     */
    default void onSupportedGas(Map<Integer, Boolean> map) {
    }


    /**
     * 支持的功能列表(0x02)
     *
     * @param map key:功能类型,value:是否支持
     *            0, 报警推送
     *            1, 报警时长
     *            2, 报警铃声
     *            3, 报警推送频率
     */
    default void onSupportedFun(Map<Integer, Boolean> map) {
    }

    /**
     * 警报推送开关(0x03)
     *
     * @param status 0x01-开启
     *               0x00-关闭
     */
    default void onAlarmSwitch(int status) {
    }

    /**
     * 报警时长设置(0x04)
     *
     * @param current      当前
     * @param supportTimes 支持时间
     */
    default void onAlarmTime(int current, int[] supportTimes) {
    }


    /**
     * CO一氧化碳报警获取/设置(0x05)
     *
     * @param current       当前
     * @param supportedRing 支持铃声
     */
    default void onAlarmRing(int current, int[] supportedRing) {
    }

    /**
     * CO一氧化碳工作间隔获取/设置(0x06)
     *
     * @param interval 间隔(单位min)0代表一直
     */
    default void onAlarmRingTest(int interval) {
    }

    /**
     * 离线数据存储频率(0x07)
     *
     * @param frequency 频率 min
     */
    default void onHistorySaveFrequency(int frequency) {
    }

    /**
     * 数据上报间隔(0x08)
     *
     * @param interval 时间间隔 min
     */
    default void onDataReportInterval(int interval) {
    }

    /**
     * 气体信息(0x09)
     *
     * @param bean 气体探测器当前信息bean
     */
    default void onGasInfo(GasDetectorPlusCurrentInfoBean bean) {
    }

    /**
     * 报警频率(0x0A)
     *
     * @param frequency 频率 秒
     */
    default void onAlarmFrequency(int frequency) {
    }

    /**
     * 报警的气体信息(0x0B)
     *
     * @param list 列表 每个元素是一个{@link GasAlarmInfoBean}
     */
    default void onAlarmGasInfo(List<GasAlarmInfoBean> list) {
    }


    /**
     * 设备电量信息(0x0C)
     *
     * @param status 状态 :0x00-默认;0x01-充电;0x02-低电
     * @param power  电量百分比(1~100)%
     */
    default void onPowerInfo(int status, int power) {
    }


    /**
     * 单位设置(0x0D)
     *
     * @param type 状态 类型-参考气体(温湿度)信息
     * @param unit  单位-参考气体(温湿度)单位列表
     */
    default void onUnitInfo(int type, int unit) {
    }

    /**
     * 同步时间(0x0E)
     */
    default void onSyncTime() {
    }


}
