package cn.net.aicare.modulelibrary.module.meatprobe;

import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;

import cn.net.aicare.modulelibrary.module.utils.ByteUtils;

/**
 * 烧烤探针发送指令
 *
 * @author xing
 * @date 2023/05/10
 */
public class MeatProbeSendCmdUtil {

    public final static int DEVICE_CID = FoodConfig.DEVICE_CID;

    private static MeatProbeSendCmdUtil instance;

    private OnMeatProbeDataListener mListeners;

    private MeatProbeSendCmdUtil() {
    }

    public static MeatProbeSendCmdUtil getInstance() {
        if (instance == null) {
            synchronized (MeatProbeSendCmdUtil.class) {
                if (instance == null) {
                    instance = new MeatProbeSendCmdUtil();
                }
            }
        }
        return instance;
    }

    public void setListeners(OnMeatProbeDataListener listeners) {
        mListeners = listeners;
    }

    //-----------------设置指令

    /**
     * 食物探针新版获取版本号
     */
    public SendDataBean getMcuVersionInfo() {
        return getSendDataBeanA6(BleSendCmdUtil.getInstance().getBleVersion());
    }

    /**
     * 食物探针新版获取版本号
     */
    public SendDataBean getVersionInfo() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x46;
        return getSendDataBeanA6(bytes);
    }

    /**
     * 单位切换
     */
    /**
     * 切换单位
     */
    public SendDataBean sendSwitchUnit(int unitType) {
        byte[] bytes = new byte[2];
        bytes[0] = 0x04;
        bytes[1] = (byte) unitType;
        return getSendDataBeanA7(bytes);
    }

    /**
     * app获取设备信息
     */
    public SendDataBean appGetDeviceInfo() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x36;
        bytes[1] = 0x01;
        return getSendDataBeanA6(bytes);
    }

    /**
     * 结束工作
     *
     * @return {@link SendDataBean}
     */
    public SendDataBean endWork() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x35;
        bytes[1] = 0x00;
        return getSendDataBeanA6(bytes);

    }

    public SendDataBean appSetDeviceInfo(ProbeBean probeBean) {
        return appSetDeviceInfo(probeBean.getCookingId(), probeBean.getFoodType(), probeBean.getFoodRawness(), probeBean.getTargetTemperature_C(), probeBean.getTargetTemperature_F(),
                probeBean.getAmbientMinTemperature_C(), probeBean.getAmbientMinTemperature_F(), probeBean.getAmbientMaxTemperature_C(), probeBean.getAmbientMaxTemperature_F(),
                probeBean.getAlarmTemperaturePercent(), probeBean.getTimerStart(), probeBean.getTimerEnd(), probeBean.getCurrentUnit());
    }

    /**
     * @param cookingId    烧烤id 选择食物类型时间戳
     * @param foodType     食物类型
     * @param foodRawness  食物熟度
     * @param targetTempC  目标温度C
     * @param targetTempF  目标温度F
     * @param ambientMinC  环境范围温度下限C
     * @param ambientMinF  环境范围温度下限F
     * @param ambientMaxC  环境范围温度上限C
     * @param ambientMaxF  环境范围温度上限F
     * @param alarmPercent 提醒温度对目标温度百分比
     * @param startTime    开始计时时间戳
     * @param endTime      结束计时时间戳
     * @param currentUnit  当前单位
     */
    public SendDataBean appSetDeviceInfo(long cookingId, int foodType, int foodRawness, int targetTempC, int targetTempF, int ambientMinC, int ambientMinF, int ambientMaxC, int ambientMaxF,
                                         double alarmPercent, long startTime, long endTime, int currentUnit) {
        byte[] bytes = new byte[128];
        bytes[0] = 0x35;
        //0x00 数据无效 0x01数据有效
        bytes[1] = 0x01;
        //数据版本 当前0x01
        bytes[2] = 0x01;
        //烧烤id 选择食物的时间戳long型/1000 转为 int类型再转字节数组
        int intCookingId = (int) (cookingId / 1000);
        byte[] cookingBytes = ByteUtils.reverseByteArr(ByteUtils.intToBytes(intCookingId));
        bytes[3] = cookingBytes[0];
        bytes[4] = cookingBytes[1];
        bytes[5] = cookingBytes[2];
        bytes[6] = cookingBytes[3];
        //食物类型 牛肉 0->0x00 小牛肉 1->0x01 羊肉 2->0x02 猪肉 3->0x03 鸡肉 4-0x04 火鸡肉 5-> 鱼肉 6-0x06 汉堡包 7-0x07 其他 8-0x08
        switch (foodType) {
            case FoodConfig.FOOD_TYPE_BEEF:
                bytes[7] = 0x00;
                break;
            case FoodConfig.FOOD_TYPE_VEAL:
                bytes[7] = 0x01;
                break;
            case FoodConfig.FOOD_TYPE_LAMB:
                bytes[7] = 0x02;
                break;
            case FoodConfig.FOOD_TYPE_PORK:
                bytes[7] = 0x03;
                break;
            case FoodConfig.FOOD_TYPE_CHICKEN:
                bytes[7] = 0x04;
                break;
            case FoodConfig.FOOD_TYPE_TURKEY:
                bytes[7] = 0x05;
                break;
            case FoodConfig.FOOD_TYPE_FISH:
                bytes[7] = 0x06;
                break;
            case FoodConfig.FOOD_TYPE_HAMBURGER:
                bytes[7] = 0x07;
                break;
            case FoodConfig.FOOD_TYPE_OTHER:
                bytes[7] = 0x08;
                break;
            default:
                bytes[7] = 0x09;
                break;
        }
        //食物熟度 三分熟 0->0x00 五分熟 1->0x01 七分熟 2->0x02 全熟 3->0x03 DIY 4->0x04
        switch (foodRawness) {
            case FoodConfig.FOOD_DEGREE_MRATE:
                bytes[8] = 0x00;
                break;
            case FoodConfig.FOOD_DEGREE_MEDIUM:
                bytes[8] = 0x01;
                break;
            case FoodConfig.FOOD_DEGREE_MWELL:
                bytes[8] = 0x02;
                break;
            case FoodConfig.FOOD_DEGREE_WELL:
                bytes[8] = 0x03;
                break;
            case FoodConfig.FOOD_DEGREE_DIY:
                bytes[8] = 0x04;
                break;
            default:
                break;
        }

        //先判断单位是℃还是℉
        short sTargetTempC = (short) targetTempC;
        short sTargetTempF = (short) targetTempF;
        byte[] targetBytesC_C = ByteUtils.reverseByteArr(ByteUtils.shortToBytes(sTargetTempC));
        bytes[9] = targetBytesC_C[0];
        bytes[10] = targetBytesC_C[1];
        byte[] targetBytesF_C = ByteUtils.reverseByteArr(ByteUtils.shortToBytes(sTargetTempF));
        bytes[11] = targetBytesF_C[0];
        bytes[12] = targetBytesF_C[1];
        // 环境温度范围下限
        short sAmbientTempMinC = (short) ambientMinC;
        short sAmbientTempMinF = (short) ambientMinF;
        byte[] ambientByteMinC_C = ByteUtils.reverseByteArr(ByteUtils.shortToBytes(sAmbientTempMinC));
        bytes[13] = ambientByteMinC_C[0];
        bytes[14] = ambientByteMinC_C[1];
        byte[] ambientByteMinF_C = ByteUtils.reverseByteArr(ByteUtils.shortToBytes(sAmbientTempMinF));
        bytes[15] = ambientByteMinF_C[0];
        bytes[16] = ambientByteMinF_C[1];
        //环境温度范围上限
        short sAmbientTempMaxC = (short) ambientMaxC;
        short sAmbientTempMaxF = (short) ambientMaxF;
        byte[] ambientByteMaxC_C = ByteUtils.reverseByteArr(ByteUtils.shortToBytes(sAmbientTempMaxC));
        bytes[17] = ambientByteMaxC_C[0];
        bytes[18] = ambientByteMaxC_C[1];
        byte[] ambientByteMaxF_C = ByteUtils.reverseByteArr(ByteUtils.shortToBytes(sAmbientTempMaxF));
        bytes[19] = ambientByteMaxF_C[0];
        bytes[20] = ambientByteMaxF_C[1];
        // 提醒温度对目标温度百分比
        byte[] alarmBytes = ByteUtils.reverseByteArr(ByteUtils.doubleToByte(alarmPercent));
        bytes[21] = alarmBytes[0];
        bytes[22] = alarmBytes[1];
        bytes[23] = alarmBytes[2];
        bytes[24] = alarmBytes[3];
        bytes[25] = alarmBytes[4];
        bytes[26] = alarmBytes[5];
        bytes[27] = alarmBytes[6];
        bytes[28] = alarmBytes[7];

        //开始计时时间戳
        int intStartTime = (int) (startTime / 1000);
        byte[] startTimeBytes = ByteUtils.reverseByteArr(ByteUtils.intToBytes(intStartTime));
        bytes[29] = startTimeBytes[0];
        bytes[30] = startTimeBytes[1];
        bytes[31] = startTimeBytes[2];
        bytes[32] = startTimeBytes[3];
        //结束计时时间戳
        int intEndTime = (int) (endTime / 1000);
        byte[] endTimeBytes = ByteUtils.reverseByteArr(ByteUtils.intToBytes(intEndTime));
        bytes[33] = endTimeBytes[0];
        bytes[34] = endTimeBytes[1];
        bytes[35] = endTimeBytes[2];
        bytes[36] = endTimeBytes[3];
        //当前单位
        bytes[37] = (byte) currentUnit;

       return getSendDataBeanA6(bytes);

    }


    public SendDataBean getSendDataBeanA6(byte[] data) {
        if (mListeners != null) {
            mListeners.onDataA6(data);
        }
        return new SendBleBean(data);
    }

    public SendDataBean getSendDataBeanA7(byte[] data) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(DEVICE_CID, data);
        if (mListeners != null) {
            mListeners.onDataA7(data);
        }
        return sendMcuBean;
    }


}
