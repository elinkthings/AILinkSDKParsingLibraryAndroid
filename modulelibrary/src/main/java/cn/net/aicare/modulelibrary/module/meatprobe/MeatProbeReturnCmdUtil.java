package cn.net.aicare.modulelibrary.module.meatprobe;

import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.util.Set;

import cn.net.aicare.modulelibrary.module.utils.ByteUtils;

/**
 * 数据解析类
 *
 * @author xing
 * @date 2023/05/10
 */
public class MeatProbeReturnCmdUtil {

    public final static int DEVICE_CID = FoodConfig.DEVICE_CID;

    private static MeatProbeReturnCmdUtil instance;

    private String mMac;

    public MeatProbeReturnCmdUtil(String mac) {
        mMac = mac;
    }


    public void onBleDataA7(byte[] hex, Set<OnMeatProbeDataListener> listeners) {
        if (listeners != null) {
            for (OnMeatProbeDataListener listener : listeners) {
                listener.onDataNotifyA7(mMac, hex);
            }
        }
        switch (hex[0]) {
            case 0x03:
                ProbeNowBean probeNowBean = new ProbeNowBean();
                //探针编号 目前固定为0x01;
                int id = hex[1];
                probeNowBean.setId(id);

                // 实时温度单位 0->℃ 1->℉
                int realTimeUnit = (hex[3] >> 7) & 0x01;
                probeNowBean.setRealTimeUnit(realTimeUnit);

                // 实时温度正负值 0->温度为正 1->温度为负
                int realTimePositive = (hex[3] >> 6) & 0x01;
                probeNowBean.setRealTimePositive(realTimePositive);

                // 实时温度值
                int realTimeTemp = (hex[4] & 0xFF) + (hex[3] & 0x3F);
                probeNowBean.setRealTimeTemp(realTimeTemp);

                // 环境温度单位 0->℃ 1->℉
                int ambientUnit = (hex[5] >> 7) & 0x01;
                probeNowBean.setAmbientUnit(ambientUnit);

                // 环境温度正负值 0->温度为正 1->温度为负
                int ambientPositive = (hex[5] >> 6) & 0x01;
                probeNowBean.setAmbientPositive(ambientPositive);

                // 环境温度值 若无 则为0xFFFF
                int ambientTemp = -1;
                if ((hex[6] & 0xFF) == 255 & (hex[5] & 0xFF) == 255) {
                    ambientTemp = 10000;
                } else {
                    ambientTemp = (hex[6] & 0xFF) + (hex[5] & 0x3F);
                }
                probeNowBean.setAmbientTemp(ambientTemp);

                // 目标温度单位 0->℃ 1->℉
                int targetUnit = (hex[7] >> 7) & 0x01;
                probeNowBean.setTargetUnit(targetUnit);

                // 目标温度正负值 0->温度为正 1->温度为负
                int targetPositive = (hex[7] >> 6) & 0x01;
                probeNowBean.setTargetPositive(targetPositive);

                // 目标温度值 若无 则为0xFFFF
                int targetTemp = -1;
                if ((hex[8] & 0xFF) == 255 & (hex[7] & 0xFF) == 255) {
                    targetTemp = 10000;
                } else {
                    targetTemp = (hex[8] & 0xFF) + (hex[7] & 0x3F);
                }
                probeNowBean.setTargetTemp(targetTemp);

                // 探针状态 0->未插入 1->已插入 3->设备无该功能
                int probeState = hex[9] & 0x03;
                probeNowBean.setProbeState(probeState);
                probeNowBean.setCreationTime(System.currentTimeMillis());
                if (listeners != null) {
                    for (OnMeatProbeDataListener listener : listeners) {
                        listener.onBleNowData(mMac, probeNowBean);
                    }
                }

                break;
            case 0x04:
                //切换单位结果 0->成功 1->失败 2->不支持
                int result = hex[1] & 0x03;
                if (listeners != null) {
                    for (OnMeatProbeDataListener listener : listeners) {
                        listener.onSwitchUnit(mMac, result);
                    }
                }
                break;
            default:
                break;
        }

    }


    /**
     * 解析数据
     *
     * @param hex       数据
     * @param listeners 回调
     */
    public void onBleDataA6(byte[] hex, Set<OnMeatProbeDataListener> listeners) {

        if (listeners != null) {
            for (OnMeatProbeDataListener listener : listeners) {
                listener.onDataNotifyA6(mMac, hex);
            }
        }

        switch (hex[0]) {
            case 0x35:
                //设置设备信息 0->成功 1->失败 2->不支持
                int setState = hex[1] & 0xFF;
                if (listeners != null) {
                    for (OnMeatProbeDataListener listener : listeners) {
                        listener.onSetDeviceInfo(mMac, setState);
                    }
                }
                break;
            case 0x36:
                //获取设备信息 0->数据无效 1->数据有效
                if (hex[1] == 0x01) {

                    if (hex.length >= 38) {
                        ProbeBean probeBean = new ProbeBean(mMac);
                        int ver = hex[2] & 0xff;
                        //选择烧烤时间 cookingId
                        byte[] cookingIdBytes = new byte[4];
                        cookingIdBytes[0] = hex[3];
                        cookingIdBytes[1] = hex[4];
                        cookingIdBytes[2] = hex[5];
                        cookingIdBytes[3] = hex[6];
                        long cookingId = ByteUtils.byteToInt(cookingIdBytes) * 1000L;
                        probeBean.setCookingId(cookingId);

                        //食物类型
                        int foodType = hex[7] & 0xff;
                        probeBean.setFoodType(foodType);

                        //食物熟度
                        int foodRawness = hex[8] & 0xff;
                        probeBean.setFoodRawness(foodRawness);

                        //目标温度℃
                        int targetTempC = ((hex[10] & 0xFF) << 8) | (hex[9] & 0xFF);

                        probeBean.setTargetTemperature_C(targetTempC);

                        //目标温度℉
                        int targetTempF = ((hex[12] & 0xFF) << 8) | (hex[11] & 0xFF);
                        probeBean.setTargetTemperature_F(targetTempF);

                        //环境温度下限℃
                        int ambientMin_C = ((hex[14] & 0xFF) << 8) | (hex[13] & 0xFF);
                        probeBean.setAmbientMinTemperature_C(ambientMin_C);

                        //环境温度下限℉
                        int ambientMin_F = ((hex[16] & 0xFF) << 8) | (hex[15] & 0xFF);
                        probeBean.setAmbientMinTemperature_F(ambientMin_F);

                        //环境温度上限℃
                        int ambientMax_C = ((hex[18] & 0xFF) << 8) | (hex[17] & 0xFF);
                        probeBean.setAmbientMaxTemperature_C(ambientMax_C);

                        //环境温度上限℉
                        int ambientMax_F = ((hex[20] & 0xFF) << 8) | (hex[19] & 0xFF);
                        probeBean.setAmbientMaxTemperature_F(ambientMax_F);

                        //提醒温度对目标温度百分比 8字节
                        byte[] alarmBytes = new byte[8];
                        alarmBytes[0] = hex[21];
                        alarmBytes[1] = hex[22];
                        alarmBytes[2] = hex[23];
                        alarmBytes[3] = hex[24];
                        alarmBytes[4] = hex[25];
                        alarmBytes[5] = hex[26];
                        alarmBytes[6] = hex[27];
                        alarmBytes[7] = hex[28];
                        double alarmPercent = ByteUtils.byteToDouble(alarmBytes);
                        probeBean.setAlarmTemperaturePercent(alarmPercent);

                        //计时开始时间戳 4字节
                        byte[] startTimeBytes = new byte[4];
                        startTimeBytes[0] = hex[29];
                        startTimeBytes[1] = hex[30];
                        startTimeBytes[2] = hex[31];
                        startTimeBytes[3] = hex[32];
                        long startTime = ByteUtils.byteToInt(startTimeBytes) * 1000L;
                        probeBean.setTimerStart(startTime);

                        //计时结束时间戳 4字节
                        byte[] endTimeBytes = new byte[4];
                        endTimeBytes[0] = hex[33];
                        endTimeBytes[1] = hex[34];
                        endTimeBytes[2] = hex[35];
                        endTimeBytes[3] = hex[36];
                        long endTime = ByteUtils.byteToInt(endTimeBytes) * 1000L;
                        probeBean.setTimerEnd(endTime);

                        //当前单位 1字节
                        int currentUnit = hex[37] & 0xff;
                        probeBean.setCurrentUnit(currentUnit);

                        if (listeners != null) {
                            for (OnMeatProbeDataListener listener : listeners) {
                                listener.getInfoSuccess(mMac);
                                listener.getDeviceInfo(mMac, probeBean);
                            }
                        }
                    } else {
                        BleLog.e("onNotifyDataA6: 长度不对");
                    }
                } else if (hex[1] == 0x00) {
                    //数据无效
                    if (listeners != null) {
                        for (OnMeatProbeDataListener listener : listeners) {
                            listener.getInfoFailed(mMac);
                        }
                    }
                }
                break;
            case 0x46:
                //获取模块版本号
                byte[] bytes = new byte[hex.length - 2];
                System.arraycopy(hex, 2, bytes, 0, hex.length - 2);
                String asciiString = BleDensityUtil.getInstance().getAsciiString(bytes);
                BleLog.i("设备版本号:" + asciiString);
                if (listeners != null) {
                    for (OnMeatProbeDataListener listener : listeners) {
                        listener.onMcuVersionInfo(mMac, asciiString);
                    }
                }

                break;
            default:
                break;
        }
    }


}
