package cn.net.aicare.modulelibrary.module.meatprobecharger;

import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;

import java.nio.charset.StandardCharsets;

import cn.net.aicare.modulelibrary.module.meatprobe.FoodConfig;
import cn.net.aicare.modulelibrary.module.meatprobe.ProbeBean;
import cn.net.aicare.modulelibrary.module.utils.ByteUtils;

/**
 * @author ljl
 * on 2024/1/23
 */
public class MeatProbeChargerSendCmdUtil {
    public final static int DEVICE_CID_ONE = FoodConfig.MEAT_PROBE_CHARGER;

    private static MeatProbeChargerSendCmdUtil instance;

    public MeatProbeChargerSendCmdUtil() {
    }

    public static MeatProbeChargerSendCmdUtil getInstance() {
        if (instance == null) {
            synchronized (MeatProbeChargerSendCmdUtil.class) {
                if (instance == null) {
                    instance = new MeatProbeChargerSendCmdUtil();
                }
            }
        }
        return instance;
    }

    public SendDataBean getSendDataBeanA7Box(byte[] data) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(DEVICE_CID_ONE, data);
        return sendMcuBean;
    }
    /**
     * 获取探针设备信息
     *
     * @param probeMac
     * @return
     */
    public SendDataBean appGetProbeInfo(String probeMac) {
        byte[] bytes = new byte[8];
        //转换后的mac地址字节数组是小端序
        byte[] macStr2Bytes = ByteUtils.macStr2Bytes(probeMac);

        bytes[0] = 0x03;
        bytes[1] = 0x02;
        bytes[2] = macStr2Bytes[0];
        bytes[3] = macStr2Bytes[1];
        bytes[4] = macStr2Bytes[2];
        bytes[5] = macStr2Bytes[3];
        bytes[6] = macStr2Bytes[4];
        bytes[7] = macStr2Bytes[5];
        return getSendDataBeanA7Box(bytes);

    }

    /**
     * 清除探针信息（结束探针工作）
     *
     * @param probeMac
     * @return
     */
    public SendDataBean endProbeWork(String probeMac) {
        byte[] bytes = new byte[8];
        //转换后的mac地址字节数组是小端序
        byte[] macStr2Bytes = ByteUtils.macStr2Bytes(probeMac);

        bytes[0] = 0x03;
        bytes[1] = 0x00;
        bytes[2] = macStr2Bytes[0];
        bytes[3] = macStr2Bytes[1];
        bytes[4] = macStr2Bytes[2];
        bytes[5] = macStr2Bytes[3];
        bytes[6] = macStr2Bytes[4];
        bytes[7] = macStr2Bytes[5];
        return getSendDataBeanA7Box(bytes);
    }

    public SendDataBean setAmbientAlarm(String probeMac) {
        byte[] bytes = new byte[8];

        //转换后的mac地址字节数组是小端序
        byte[] macStr2Bytes = ByteUtils.macStr2Bytes(probeMac);

        bytes[0] = 0x05;
        bytes[1] = macStr2Bytes[0];
        bytes[2] = macStr2Bytes[1];
        bytes[3] = macStr2Bytes[2];
        bytes[4] = macStr2Bytes[3];
        bytes[5] = macStr2Bytes[4];
        bytes[6] = macStr2Bytes[5];
        bytes[7] = 0x02;
        return getSendDataBeanA7Box(bytes);
    }


    public SendDataBean appSetProbeInfo(String probeMac, ProbeBean probeBean) {
        return appSetProbeInfo(probeMac, probeBean.getCookingId(), probeBean.getFoodType(), probeBean.getFoodRawness(), probeBean.getTargetTemperature_C(), probeBean.getTargetTemperature_F(),
                probeBean.getAmbientMinTemperature_C(), probeBean.getAmbientMinTemperature_F(), probeBean.getAmbientMaxTemperature_C(), probeBean.getAmbientMaxTemperature_F(),
                probeBean.getAlarmTemperaturePercent(), probeBean.getTimerStart(), probeBean.getTimerEnd(), probeBean.getCurrentUnit(), probeBean.getAlarmTemperature_C(), probeBean.getAlarmTemperature_F(), probeBean.getReMark());
    }

    /**
     * 设置探针盒子里的探针信息
     *
     * @param probeMac                探针的mac地址
     * @param cookingId               烧烤ID（设置时间戳）
     * @param foodType                食物类型
     * @param foodRawness             食物熟度
     * @param targetTemperature_c     目标温度摄氏度
     * @param targetTemperature_f     目标温度华氏度
     * @param ambientMinTemperature_c 环境温度最小值摄氏度
     * @param ambientMinTemperature_f 环境温度最小值华氏度
     * @param ambientMaxTemperature_c 环境温度最大值摄氏度
     * @param ambientMaxTemperature_f 环境温度最大值华氏度
     * @param alarmTemperaturePercent 提醒温度百分比
     * @param timerStart              开始时间戳
     * @param timerEnd                结束时间戳
     * @param currentUnit             当前单位
     * @param alarmTemperature_c      提醒温度摄氏度
     * @param alarmTemperature_f      提醒温度华氏度
     * @return
     */
    private SendDataBean appSetProbeInfo(String probeMac, long cookingId, int foodType, int foodRawness,
                                         int targetTemperature_c, int targetTemperature_f, int ambientMinTemperature_c,
                                         int ambientMinTemperature_f, int ambientMaxTemperature_c, int ambientMaxTemperature_f,
                                         double alarmTemperaturePercent, long timerStart, long timerEnd, int currentUnit,
                                         int alarmTemperature_c, int alarmTemperature_f, String remark) {
        byte[] bytes = new byte[136];
        //转换后的mac地址字节数组是小端序
        byte[] macStr2Bytes = ByteUtils.macStr2Bytes(probeMac);

        bytes[0] = 0x03;
        bytes[1] = 0x01;
        bytes[2] = macStr2Bytes[0];
        bytes[3] = macStr2Bytes[1];
        bytes[4] = macStr2Bytes[2];
        bytes[5] = macStr2Bytes[3];
        bytes[6] = macStr2Bytes[4];
        bytes[7] = macStr2Bytes[5];

        //数据版本 当前0x01
        bytes[8] = 0x02;
        //烧烤id 选择食物的时间戳long型/1000 转为 int类型再转字节数组
        int intCookingId = (int) (cookingId / 1000);
        byte[] cookingBytes = ByteUtils.changeBytes(ByteUtils.intToBytes(intCookingId));
        bytes[9] = cookingBytes[0];
        bytes[10] = cookingBytes[1];
        bytes[11] = cookingBytes[2];
        bytes[12] = cookingBytes[3];
        //食物类型 牛肉 0->0x00
        bytes[13] = (byte) foodType;

        //食物熟度 三分熟
        bytes[14] = (byte) foodRawness;

        //先判断单位是℃还是℉
        short sTargetTempC = (short) targetTemperature_c;
        short sTargetTempF = (short) targetTemperature_f;
        byte[] targetBytesC_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(sTargetTempC));
        bytes[15] = targetBytesC_C[0];
        bytes[16] = targetBytesC_C[1];
        byte[] targetBytesF_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(sTargetTempF));
        bytes[17] = targetBytesF_C[0];
        bytes[18] = targetBytesF_C[1];
        // 环境温度范围下限
        short sAmbientTempMinC = (short) ambientMinTemperature_c;
        short sAmbientTempMinF = (short) ambientMinTemperature_f;
        byte[] ambientByteMinC_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(sAmbientTempMinC));
        bytes[19] = ambientByteMinC_C[0];
        bytes[20] = ambientByteMinC_C[1];
        byte[] ambientByteMinF_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(sAmbientTempMinF));
        bytes[21] = ambientByteMinF_C[0];
        bytes[22] = ambientByteMinF_C[1];
        //环境温度范围上限
        short sAmbientTempMaxC = (short) ambientMaxTemperature_c;
        short sAmbientTempMaxF = (short) ambientMaxTemperature_f;
        byte[] ambientByteMaxC_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(sAmbientTempMaxC));
        bytes[23] = ambientByteMaxC_C[0];
        bytes[24] = ambientByteMaxC_C[1];
        byte[] ambientByteMaxF_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(sAmbientTempMaxF));
        bytes[25] = ambientByteMaxF_C[0];
        bytes[26] = ambientByteMaxF_C[1];
        // 提醒温度对目标温度百分比
        byte[] alarmBytes = ByteUtils.changeBytes(ByteUtils.doubleToByte(alarmTemperaturePercent));
        bytes[27] = alarmBytes[0];
        bytes[28] = alarmBytes[1];
        bytes[29] = alarmBytes[2];
        bytes[30] = alarmBytes[3];
        bytes[31] = alarmBytes[4];
        bytes[32] = alarmBytes[5];
        bytes[33] = alarmBytes[6];
        bytes[34] = alarmBytes[7];

        //开始计时时间戳
        int intStartTime = (int) (timerStart / 1000);
        byte[] startTimeBytes = ByteUtils.changeBytes(ByteUtils.intToBytes(intStartTime));
        bytes[35] = startTimeBytes[0];
        bytes[36] = startTimeBytes[1];
        bytes[37] = startTimeBytes[2];
        bytes[38] = startTimeBytes[3];
        //结束计时时间戳
        int intEndTime = (int) (timerEnd / 1000);
        byte[] endTimeBytes = ByteUtils.changeBytes(ByteUtils.intToBytes(intEndTime));
        bytes[39] = endTimeBytes[0];
        bytes[40] = endTimeBytes[1];
        bytes[41] = endTimeBytes[2];
        bytes[42] = endTimeBytes[3];
        //当前单位
        bytes[43] = (byte) currentUnit;
        //提醒温度
        short alarmTempC = (short) alarmTemperature_c;
        short alarmTempF = (short) alarmTemperature_f;
        byte[] alarmBytesC_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(alarmTempC));
        bytes[44] = alarmBytesC_C[0];
        bytes[45] = alarmBytesC_C[1];
        byte[] alarmBytesF_C = ByteUtils.changeBytes(ByteUtils.shortToBytes(alarmTempF));
        bytes[46] = alarmBytesF_C[0];
        bytes[47] = alarmBytesF_C[1];

        if (remark != null && !remark.isEmpty()) {
            byte[] remarkBytes = remark.getBytes(StandardCharsets.UTF_8);
            if (remarkBytes.length > 32) {
                while (remarkBytes.length > 29) {
                    remark = remark.substring(0, remark.length() - 1);
                    remarkBytes = remark.getBytes(StandardCharsets.UTF_8);
                }
                remark = remark + "...";
                remarkBytes = remark.getBytes(StandardCharsets.UTF_8);
            }
            System.arraycopy(remarkBytes, 0, bytes, 48, remarkBytes.length);
        }

        return getSendDataBeanA7Box(bytes);
    }

    /**
     * 切换单位
     */
    public SendDataBean sendSwitchUnitBox(int unitType) {
        byte[] bytes = new byte[2];
        bytes[0] = 0x04;
        bytes[1] = (byte) unitType;
        return getSendDataBeanA7Box(bytes);
    }

    /**
     * 设置报警状态
     *
     * @param probeMac     探针mac地址
     * @param timeOut      计时时间是否到了
     * @param isAmbientOut 环境温度是否到了
     * @param isTarget     目标温度是否完成
     * @return
     */
    public SendDataBean sendAlarmStatus(String probeMac, boolean timeOut, boolean isAmbientOut, boolean isTarget) {
        byte[] bytes = new byte[8];
        //转换后的mac地址字节数组是小端序
        byte[] macStr2Bytes = ByteUtils.macStr2Bytes(probeMac);

        bytes[0] = 0x05;
        bytes[1] = macStr2Bytes[0];
        bytes[2] = macStr2Bytes[1];
        bytes[3] = macStr2Bytes[2];
        bytes[4] = macStr2Bytes[3];
        bytes[5] = macStr2Bytes[4];
        bytes[6] = macStr2Bytes[5];
        int alarm = 0;
        if (timeOut) {
            alarm += 4;
        }
        if (isAmbientOut) {
            alarm += 2;
        }
        if (isTarget) {
            alarm += 1;
        }
        bytes[7] = (byte) alarm;

        return getSendDataBeanA7Box(bytes);
    }

    public SendDataBean cancelAlarm(String probeMac) {
        byte[] bytes = new byte[8];
        //转换后的mac地址字节数组是小端序
        byte[] macStr2Bytes = ByteUtils.macStr2Bytes(probeMac);

        bytes[0] = 0x07;
        bytes[1] = macStr2Bytes[0];
        bytes[2] = macStr2Bytes[1];
        bytes[3] = macStr2Bytes[2];
        bytes[4] = macStr2Bytes[3];
        bytes[5] = macStr2Bytes[4];
        bytes[6] = macStr2Bytes[5];

        bytes[7] = (byte) 0xFF;

        return getSendDataBeanA7Box(bytes);
    }
}
