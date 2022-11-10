package cn.net.aicare.modulelibrary.module.scooter;

/**
 * xing<br>
 * 2020/5/15<br>
 * OTA升级接口
 */
public interface OnScooterBleOTAListener {


    /**
     * ota升级成功 OTA升级完成,设备会自动复位,如果没有复位就代表可能是失败了
     */
    default void onOtaSuccess(){}

    /**
     * ota升级失败
     *
     * @param cmd {OtaConfig}
     * @param err
     */
    default void onOtaFailure(int cmd,String err){}

    /**
     * 进度
     * @param progress 当前数据块进度
     * @param currentCount 当前数据块
     * @param maxCount 总数据块
     */
    default void onOtaProgress(float progress,int currentCount,int maxCount){}


    /**
     * OTA状态
     * @param status 状态 {OtaConfig}
     */
    default void onOtaStatus(int status){}


    /**
     * OTA过程中,请求重连
     * @param mac 地址
     */
    default void onReconnect(String mac){}

}
