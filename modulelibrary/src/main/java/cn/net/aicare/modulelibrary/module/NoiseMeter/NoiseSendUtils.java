package cn.net.aicare.modulelibrary.module.NoiseMeter;

/**
 * 噪声发送数据工具类
 *
 * @author xing
 * @date 2022/11/15
 */
public class NoiseSendUtils {


    /**
     * 发送获取支持列表
     *
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendSetSupportList() {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_SUPPORT_LIST);
        bleNoiseTLVBean.setType(0x00);
        return bleNoiseTLVBean;
    }

    /**
     * 发送获取设备状态
     *
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendSetDeviceState() {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_DEVICE_STATE);
        bleNoiseTLVBean.setType(0x00);
        return bleNoiseTLVBean;
    }


    /**
     * 发送A/C
     *
     * @param value 0x01:使用 A 权
     *              0x02:使用 C 权
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendAc(int value) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_FREQUENCY_AC);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        bleNoiseTLVBean.setValue(new byte[]{(byte) value});
        return bleNoiseTLVBean;
    }


    /**
     * 发送测量等级及范围
     *
     * @param level 当前的等级
     * @param min   最小值
     * @param max   最大值
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendTestLevel(int level, int min, int max) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_TEST_LEVEL);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[5];
        bytes[0] = (byte) level;
        bytes[1] = (byte) min;
        bytes[2] = (byte) (min >> 8);
        bytes[3] = (byte) max;
        bytes[4] = (byte) (max >> 8);
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }

    /**
     * 发送最小值最大值模式
     *
     * @param mode 模式 0x00:正常模式
     *             0x01:最小值测量模式 Min
     *             0x02:最大值测量模式 Max
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendMinMaxMode(int mode) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_MAX_MIN);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[1];
        bytes[0] = (byte) mode;
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }

    /**
     * 时间加权(Fast/Slow)
     *
     * @param mode 模式 0x01:Fast 模式
     *             0x02:Slow 模式
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendFastSlowMode(int mode) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_TIME_FAT_SLOW);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[1];
        bytes[0] = (byte) mode;
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }


    /**
     * 数值保持(hold)
     *
     * @param mode 模式 0x00:正常模式
     *             0x01:hold 模式
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendHoldMode(int mode,int holdValue) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_VALUE_HOLD);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[3];
        bytes[0] = (byte) mode;
        bytes[1] = (byte) holdValue;
        bytes[2] = (byte) (holdValue>>8);
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }


    /**
     * 发送报警
     * 报警
     *
     * @param allAlarmSwitch 所有报警开关
     * @param alarmSwitch    报警开关
     * @param alarmValue     报警值
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendAlarm(boolean allAlarmSwitch, boolean alarmSwitch, int alarmValue) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_WARM);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[3];
        byte byteSwitch = (byte) (alarmSwitch ? 1 : 0);
        byteSwitch = (byte) ((byteSwitch << 1) | (allAlarmSwitch ? 0 : 1));
        bytes[0] = (byte) byteSwitch;
        bytes[1] = (byte) alarmValue;
        bytes[2] = (byte) (alarmValue << 8);
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }


    /**
     * 背光
     *
     * @param mode 模式0x00:关闭
     *             0x01:打开
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendBackLight(int mode) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_BACK_LIGHT);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[1];
        bytes[0] = (byte) mode;
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }

    /**
     * 获取设备端历史
     *
     * @param time (S)该时间戳到当前时间里产生的历史(之
     *             前同步过的历史,则不会重新同步
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendGetHistory(long time) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_HISTORY);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[5];
        bytes[0] = (byte) 0x02;
        bytes[1] = (byte) time;
        bytes[2] = (byte) (time >> 8);
        bytes[3] = (byte) (time >> 16);
        bytes[4] = (byte) (time >> 24);
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }

    /**
     * 设备绑定
     *
     * @param mode 0x01:APP 发起,请求用户按键通过绑定
     *             0x02:MCU 返回,MCU 等待用户按键
     *             0x03:MCU 返回,用户已按按键
     *             0x04:MCU 返回,用户超时(30s)没按按键
     *             0x05:APP 发起,APP 取消绑定
     * @return {@link BleNoiseTLVBean}
     */
    public BleNoiseTLVBean sendBindDevice(int mode) {
        BleNoiseTLVBean bleNoiseTLVBean = new BleNoiseTLVBean();
        bleNoiseTLVBean.setCmd(NoiseMeterConfig.GROUP_SET_PARAMETERS);
        bleNoiseTLVBean.setType(NoiseMeterConfig.TYPE_BIND_DEVICE);
        bleNoiseTLVBean.setOp(NoiseMeterConfig.TYPE_OP_SET);
        byte[] bytes = new byte[1];
        bytes[0] = (byte) mode;
        bleNoiseTLVBean.setValue(bytes);
        return bleNoiseTLVBean;
    }


}
