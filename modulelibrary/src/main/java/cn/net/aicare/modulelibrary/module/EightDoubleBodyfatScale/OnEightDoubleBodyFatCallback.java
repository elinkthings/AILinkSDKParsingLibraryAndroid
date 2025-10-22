package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;


import com.pingwang.bluetoothlib.bean.SupportUnitBean;

import java.util.List;

/**
 * 双频八电极体脂秤回调接口
 *
 * @author xing
 * @date 2024/06/11
 */
public interface OnEightDoubleBodyFatCallback {

    /**
     * 错误码
     *
     * @param state 状态
     *              1：超重
     *              ...
     */
    void onErrCode(int state);

    /**
     * 单位状态
     *
     * @param state 状态
     *              0：设置成功
     *              1：设置失败
     *              2：不支持设置
     */
    void onUnitState(int state);

    /**
     * 测试完成后
     *
     * @param adcBeanList 阻抗列表
     */
    void onTestCompleted(List<EightDoubleBodyFatAdcBean> adcBeanList);

    /**
     * 体重数据接口
     * Weight data interface
     *
     * @param state         测量状态 1实时体重 2稳定体重
     *                      Measurement status 1 Real-time weight 2 Stable weight
     * @param weight        体重
     *                      weight
     * @param unit          单位
     *                      0：kg
     *                      1：斤
     *                      4：st:lb
     *                      6：lb
     * @param decimal       小数点位
     *                      decimal
     * @param batteryStatus 电池状态
     *                      0x00:正常
     *                      0x01:正在充电中
     * @param power         电池电量(%)无电量检测,则为 0xFF
     */
    void onWeight(int state, int weight, int decimal, int unit, int batteryStatus, int power);

    /**
     * 阻抗状态
     * impedance
     *
     * @param status 状态
     *               4：测阻抗中
     *               6：测阻抗失败
     *               7：测阻抗成功，带上阻抗数据，并使用 APP 算法
     */
    void onImpedanceStatus(int status);

    /**
     * 获取到版本号
     * Get the version number
     *
     * @param version
     */
    void onVersion(String version);

    /**
     * 获取到支持的单位列表
     * Get the list of supported units
     *
     * @param list
     */
    void onSupportUnit(List<SupportUnitBean> list);


    default void showData(String data) {
    }


    /**
     * 体脂数据接口
     *
     * @param bodyFatBean 体脂数据
     */
    default void onBodyFatMcu(EightDoubleMcuBodyFatBean bodyFatBean) {
    }

    /**
     * 心率数据
     *
     * @param status    状态 01：测心率中
     *                  02：测心率成功，带上心率数据
     *                  03：测心率失败
     * @param heartRate 心率 心率数据（精度 1bpm）
     */
    default void onHeartRate(int status, int heartRate) {
    }

    /**
     * 温度数据
     *
     * @param temp    温度
     * @param unit    单位 0=摄氏度℃;  1=华氏度℉
     * @param decimal 小数
     */
    default void onTemperature(int temp, int unit, int decimal) {

    }

    /**
     * MCU 请求同步用户
     */
    default void onSyncUser(){}


    /**
     * 体脂数据接口
     *
     * @param bodyFatBean 体脂数据 null代表解析数据失败
     */
    default void onBodyFatData( EightDoubleDataBodyFatBean bodyFatBean) {
    }


    /**
     * 体脂数据计算错误
     *
     * @param type 类型 0-网络异常,1-没有权限,2-传入数据有误
     * @param msg  消息
     */
    default void onBodyFatDataError(int type, String msg){

    }

}