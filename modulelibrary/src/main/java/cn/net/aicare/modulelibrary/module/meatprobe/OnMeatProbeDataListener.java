package cn.net.aicare.modulelibrary.module.meatprobe;

/**
 * 肉类探测数据监听器
 *
 * @author xing
 * @date 2023/05/11
 */
public interface OnMeatProbeDataListener {
    /**
     * 探针实时数据
     *
     * @param probeNowBean 探针现在Bean
     */
    default void onBleNowData(String mac, ProbeNowBean probeNowBean) {
    }

    /**
     * 电池状态
     *
     * @param status  状态
     * @param battery 电池
     */
    default void onBatteryState(String mac, int status, int battery) {
    }

    /**
     * 在设置设备信息
     *
     * @param setState 设置状态
     */
    default void onSetDeviceInfo(String mac, int setState) {
    }

    /**
     * 版本信息
     *
     * @param versionInfo 版本信息
     */
    default void onMcuVersionInfo(String mac, String versionInfo) {
    }

    /**
     * 单位
     *
     * @param result 结果
     */
    default void onSwitchUnit(String mac, int result) {
    }

    /**
     * 获取信息成功
     */
    default void getInfoSuccess(String mac) {
    }

    /**
     * 获取信息失败
     */
    default void getInfoFailed(String mac) {
    }

    /**
     * 获取设备信息
     *
     * @param probeBean 探针Bean
     * @param mac       mac
     */
    default void getDeviceInfo(String mac, ProbeBean probeBean) {
    }

    /**
     * @param mac    mac
     * @param dataA7 收到的 A7 payload数据
     */
    default void onDataNotifyA7(String mac, byte[] dataA7) {
    }

    /**
     * @param mac    mac
     * @param dataA6 收到的 A6 payload数据
     */
    default void onDataNotifyA6(String mac, byte[] dataA6) {
    }

    /**
     * @param dataA6 发送的A6 payload数据
     */
    default void onDataA6(byte[] dataA6) {
    }

    /**
     * @param dataA7 发送的A7 payload数据
     */
    default void onDataA7(byte[] dataA7) {
    }
}