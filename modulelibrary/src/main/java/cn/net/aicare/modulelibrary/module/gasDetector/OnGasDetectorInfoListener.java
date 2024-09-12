package cn.net.aicare.modulelibrary.module.gasDetector;

/**
 * 气体探测器信息监听器
 *
 * @author xing
 * @date 2024/09/05
 */
public interface OnGasDetectorInfoListener {


    /**
     * 实时信息(0x01)
     *
     * @param currentInfoBean 当前信息bean
     */
    default void onCurrentInfo(GasDetectorCurrentInfoBean currentInfoBean) {
    }


    /**
     * CO2二氧化碳报警获取/设置(0x02)
     *
     * @param density 报警浓度(单位ppm)
     */
    default void onCo2Density(int density) {
    }

    /**
     * CO2二氧化碳工作间隔获取/ 设置(0x03)
     *
     * @param interval 间隔(单位min)0代表一直
     */
    default void onCo2Interval(int interval) {
    }

    /**
     * CO2二氧化碳开关获取/设置(0x04)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    default void onCo2Switch(int status) {
    }


    /**
     * CO一氧化碳报警获取/设置(0x05)
     *
     * @param density 报警浓度(单位ppm)
     */
    default void onCoDensity(int density) {
    }

    /**
     * CO一氧化碳工作间隔获取/设置(0x06)
     *
     * @param interval 间隔(单位min)0代表一直
     */
    default void onCoInterval(int interval) {
    }

    /**
     * CO一氧化碳开关获取/设置(0x07)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    default void onCoSwitch(int status) {
    }

    /**
     * O2报警报警获取/设置(0x08)
     *
     * @param density 报警浓度(单位%)
     */
    default void onO2Density(int density) {
    }

    /**
     * 离线历史记录间隔获取/设置(0x09)
     *
     * @param interval 间隔(单位min)0代表一直
     */
    default void onHistorySaveInterval(int interval) {
    }

    /**
     * 温度单位获取/设置(0x0A)
     *
     * @param unit 单位
     *             0x00=℃
     *             0x01=℉
     */
    default void onTempUnit(int unit) {
    }

    /**
     * 气压单位获取/设置(0x0B)
     *
     * @param unit 单位
     *             0x00=hPa
     *             0x01=inHg
     */
    default void onPressureUnit(int unit) {
    }

    /**
     * 设备气压校准(0x0C)
     *
     * @param hpa  气压(hPa,0-9999)
     * @param inHg 气压(inHg,0-295)
     */
    default void onPressureCalibration(int hpa, int inHg) {
    }

    /**
     * 亮度获取/设置(0x0D)
     *
     * @param brightness 亮度(%,0-100)
     */
    default void onBrightness(int brightness) {
    }

    /**
     * 声音开关获取/设置(0x0E)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    default void onSoundSwitch(int status) {
    }

    /**
     * 震动开关获取/设置(0x0F)
     *
     * @param status 状态
     *               0x00 开
     *               0x01 关
     */
    default void onShockSwitch(int status) {
    }

    /**
     * 时间同步(0x10)
     *
     * @param time 时间戳(大端序,单位:s)
     */
    default void onSyncTime(long time) {
    }

    /**
     * 息屏时间获取/设置(0x11)
     *
     * @param time 时间(单位,分钟min)0代表从不
     */
    default void onScreenOffTime(int time) {
    }

    /**
     * 关机时间获取/设置(0x12)
     *
     * @param time 时间(单位,小时H)0代表从不
     */
    default void onShutdownTime(int time) {
    }

    /**
     * 离线历史记录 (0x13)
     *
     * @param historyInfoBean 历史信息bean
     */
    default void onHistoryInfo(GasDetectorHistoryInfoBean historyInfoBean) {
    }

    /**
     * 清空离线历史记录 (0x14)
     */
    default void onClearHistory() {
    }

    ;
}
