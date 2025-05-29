package cn.net.aicare.modulelibrary.module.gasDetectorPlus;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.net.aicare.modulelibrary.module.gasDetectorPlus.bean.GasAlarmInfoBean;
import cn.net.aicare.modulelibrary.module.gasDetectorPlus.bean.GasDetectorPlusCurrentInfoBean;

/**
 * 解析气体探测器数据
 *
 * @author xing
 * @date 2024/09/04
 */
public class ParseGasDetectorPlusData {


    public void parseData(byte[] data, Set<OnGasDetectorPlusInfoListener> listeners) {
        int cmdType = data[0] & 0xFF;
        switch (cmdType) {

            case GasDetectorPlusBleConfig.TYPE_SUPPORTED_GAS:
                //支持的气体类型(0x01)
                if (listeners != null && !listeners.isEmpty()) {
                    Map<Integer, Boolean> parseSupportedGas = parseSupportedGas(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onSupportedGas(parseSupportedGas);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_SUPPORTED_FUN:
                //支持的功能列表(0x02)
                if (listeners != null && !listeners.isEmpty()) {
                    Map<Integer, Boolean> parseSupportedFun = parseSupportedFun(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onSupportedFun(parseSupportedFun);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_ALARM_SWITCH:
                // 警报推送开关(0x03)
                if (listeners != null && !listeners.isEmpty()) {
                    int alarmSwitch = parseAlarmSwitch(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onAlarmSwitch(alarmSwitch);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_ALARM_TIME:
                //报警时长设置(0x04)
                if (listeners != null && !listeners.isEmpty()) {
                    int[] alarmTime = parseAlarmTime(data);
                    int current = alarmTime[0];
                    int[] supports = new int[alarmTime.length - 1];
                    System.arraycopy(alarmTime, 1, supports, 0, supports.length);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onAlarmTime(current, supports);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_ALARM_RING:
                //报警铃声设置(0x05)
                if (listeners != null && !listeners.isEmpty()) {
                    int[] alarmRing = parseAlarmRing(data);
                    int current = alarmRing[0];
                    int[] supports = new int[alarmRing.length - 1];
                    System.arraycopy(alarmRing, 1, supports, 0, supports.length);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onAlarmRing(current, supports);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_ALARM_RING_TEST:
                //报警铃声试听(0x06)
                if (listeners != null && !listeners.isEmpty()) {
                    int ringTest = parseAlarmRingTest(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onAlarmRingTest(ringTest);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_HISTORY_SAVE_FREQUENCY:
                //离线数据存储频率(0x07)
                if (listeners != null && !listeners.isEmpty()) {
                    int frequency = parseHistorySaveFrequency(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onHistorySaveFrequency(frequency);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_DATA_REPORT_INTERVAL:
                //数据上报间隔(0x08)
                if (listeners != null && !listeners.isEmpty()) {
                    int interval = parseDataReportInterval(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onDataReportInterval(interval);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_GAS_INFO:
                // 气体信息(0x09)
                if (listeners != null && !listeners.isEmpty()) {
                    GasDetectorPlusCurrentInfoBean currentInfoBean = parseGasInfo(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onGasInfo(currentInfoBean);
                        }
                    }
                }

                break;
            case GasDetectorPlusBleConfig.TYPE_ALARM_FREQUENCY:
                //报警频率(0x0A)
                if (listeners != null && !listeners.isEmpty()) {
                    int frequency = parseAlarmFrequency(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onAlarmFrequency(frequency);
                        }
                    }
                }

                break;

            case GasDetectorPlusBleConfig.TYPE_ALARM_GAS:
                //报警的气体信息(0x0B)
                if (listeners != null && !listeners.isEmpty()) {
                    List<GasAlarmInfoBean> alarmGas = parseAlarmGas(data);
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onAlarmGasInfo(alarmGas);
                        }
                    }
                }
                break;

            case GasDetectorPlusBleConfig.TYPE_POWER_INFO:
                //设备电量信息(0x0C)
                parsePowerInfo(data, listeners);
                break;

            case GasDetectorPlusBleConfig.TYPE_UNIT_SET:
                //单位设置(0x0D)
                parseUnitInfo(data, listeners);
                break;

            case GasDetectorPlusBleConfig.TYPE_SYNC_TIME:
                //同步时间(0x0E)
                if (listeners != null && !listeners.isEmpty()) {
                    for (OnGasDetectorPlusInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onSyncTime();
                        }
                    }
                }
                break;

            default:
                throw new RuntimeException("未知指令:");

        }

    }


    /**
     * 解析单位设置(0x0D)
     *
     * @param data      数据
     * @param listeners 听众
     */
    private void parseUnitInfo(byte[] data, Set<OnGasDetectorPlusInfoListener> listeners) {
        if (listeners != null && !listeners.isEmpty()) {
            int type = data[2] & 0xFF;
            int unit = data[3] & 0xFF;
            for (OnGasDetectorPlusInfoListener listener : listeners) {
                if (listener != null) {
                    listener.onUnitInfo(type, unit);
                }
            }
        }

    }

    /**
     * 设备电量信息(0x0C)
     *
     * @param data      数据
     * @param listeners 听众
     */
    private void parsePowerInfo(byte[] data, Set<OnGasDetectorPlusInfoListener> listeners) {
        if (listeners != null && !listeners.isEmpty()) {
            int status = data[1] & 0xFF;
            int power = data[2] & 0xFF;
            for (OnGasDetectorPlusInfoListener listener : listeners) {
                if (listener != null) {
                    listener.onPowerInfo(status, power);
                }
            }
        }

    }

    /**
     * 报警的气体信息(0x0B)
     *
     * @param data 数据
     * @return {@link List}<{@link GasAlarmInfoBean}>
     */
    private List<GasAlarmInfoBean> parseAlarmGas(byte[] data) {
        List<GasAlarmInfoBean> list = new ArrayList<>();
        for (int i = 1; i < data.length; i += 2) {
            GasAlarmInfoBean info = new GasAlarmInfoBean();
            info.setType(data[i] & 0xFF);
            info.setStatus(data[i + 1] & 0xFF);
            list.add(info);
        }
        list.sort(Comparator.comparingInt(GasAlarmInfoBean::getType));
        return list;
    }


    /**
     * 支持的气体类型(0x01)
     *
     * @param data 数据
     */
    private Map<Integer, Boolean> parseSupportedGas(byte[] data) {
        Map<Integer, Boolean> mapSupportGas = new HashMap<>();
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < 8; j++) {
                boolean support = (data[i] >> j & 0x01) == 1;
                mapSupportGas.put((i - 1) * 8 + j, support);
            }
        }

        return mapSupportGas;

    }

    /**
     * 支持的功能列表(0x02)
     * bit0	报警推送
     * bit1	报警时长
     * bit2	报警铃声
     * bit3	报警推送频率
     * bit4	温度单位切换
     * bit5	预留
     * bit6	预留
     * bit7	预留
     *
     * @param data 数据
     * @return {@link Map}<{@link Integer}, {@link Boolean}>
     */
    private Map<Integer, Boolean> parseSupportedFun(byte[] data) {
        Map<Integer, Boolean> mapSupportFun = new HashMap<>();
        for (int j = 0; j < 8; j++) {
            boolean support = (data[1] >> j & 0x01) == 1;
            mapSupportFun.put(j, support);
        }
        return mapSupportFun;
    }

    /**
     * 警报推送开关(0x03)
     *
     * @param data 数据
     * @return int  0x01-开启
     * 0x00-关闭
     */
    private int parseAlarmSwitch(byte[] data) {
        int status = data[1] & 0xFF;
        int switchStatus = (data[2] & 0xFF);
        if (status == GasDetectorPlusBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return switchStatus;
    }

    /**
     * 报警时长设置(0x04)
     *
     * @param data 数据
     * @return {@link int[]}下标0=当前选中
     */
    private int[] parseAlarmTime(byte[] data) {
        int status = data[1] & 0xFF;
        if (status == GasDetectorPlusBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        int start = 2;
        int[] selectList = new int[data.length - start];
        for (int i = start; i < data.length; i++) {
            selectList[i - start] = (data[i] & 0xFF);
        }
        return selectList;
    }

    /**
     * 报警铃声设置(0x05)
     *
     * @param data 数据
     * @return {@link int[]}下标0=当前选中
     */
    private int[] parseAlarmRing(byte[] data) {
        int status = data[1] & 0xFF;
        if (status == GasDetectorPlusBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        int start = 2;
        int[] selectList = new int[data.length - start];
        for (int i = start; i < data.length; i++) {
            selectList[i - start] = (data[i] & 0xFF);
        }
        return selectList;
    }

    /**
     * 报警铃声试听(0x06)
     *
     * @param data 数据
     * @return int 当前试听的铃声序号
     */
    private int parseAlarmRingTest(byte[] data) {
        return data[1] & 0xFF;
    }

    /**
     * 离线数据存储频率(0x07)
     *
     * @param data 数据
     * @return int 当前设置的存储频率(单位min)
     */
    private int parseHistorySaveFrequency(byte[] data) {
        int status = data[1] & 0xFF;
        int minute = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        if (status == GasDetectorPlusBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return minute;
    }

    /**
     * 数据上报间隔(0x08)
     *
     * @param data 数据
     * @return int 当前设置的上报间隔(单位min)
     */
    private int parseDataReportInterval(byte[] data) {
        int status = data[1] & 0xFF;
        int minute = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        if (status == GasDetectorPlusBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return minute;
    }

    /**
     * 气体信息(0x09)
     *
     * @param data 数据
     * @return int 间隔(单位min)0代表一直
     */
    private GasDetectorPlusCurrentInfoBean parseGasInfo(byte[] data) {
        GasDetectorPlusCurrentInfoBean bean = new GasDetectorPlusCurrentInfoBean();
        long second = (data[1] & 0xFFL) << 24 | (data[2] & 0xFFL) << 16 | (data[3] & 0xFFL) << 8 | (data[4] & 0xFFL);
        bean.setTimeSeconds(second);
        for (int i = 5; i < data.length; i += 4) {
            GasDetectorPlusCurrentInfoBean.GasInfo info = new GasDetectorPlusCurrentInfoBean.GasInfo();
            int type = data[i] & 0xFF;
            int value = (data[i + 1] & 0xFF) << 8 | (data[i + 2] & 0xFF);
            int unit = data[i + 3] & 0x0F;
            int decimal = data[i + 3] >> 4 & 0x07;
            boolean negative = (data[i + 3] & 0x80) == 0x80;
            if (negative) {
                value = -value;
            }
            info.setType(type);
            info.setValue(value);
            info.setUnit(unit);
            info.setDecimal(decimal);
            bean.addGasInfo(info);
        }
        return bean;
    }

    /**
     * 报警频率(0x0A)
     *
     * @param data 数据
     * @return int 当前设置的存储频率(单位min)
     */
    private int parseAlarmFrequency(byte[] data) {
        int status = data[1] & 0xFF;
        int minute = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        if (status == GasDetectorPlusBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return minute;
    }


}
