package cn.net.aicare.modulelibrary.module.gasDetector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 解析气体探测器数据
 *
 * @author xing
 * @date 2024/09/04
 */
public class ParseGasDetectorData {


    public void parseData(byte[] data, Set<OnGasDetectorInfoListener> listeners) {
        int cmdType = data[0] & 0xFF;
        switch (cmdType) {

            case GasDetectorBleConfig.TYPE_CURRENT_INFO:
                //实时信息(0x01)
                if (listeners != null && !listeners.isEmpty()) {
                    GasDetectorCurrentInfoBean gasDetectorCurrentInfoBean = parseCurrentInfo(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCurrentInfo(gasDetectorCurrentInfoBean);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_CO2_ALARM:
                //CO2二氧化碳报警获取/ 设置(0x02)
                if (listeners != null && !listeners.isEmpty()) {
                    int co2Density = parseCo2Alarm(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCo2Density(co2Density);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_CO2_INTERVAL:
                //CO2二氧化碳工作间隔获取/ 设置(0x03)
                if (listeners != null && !listeners.isEmpty()) {
                    int co2Interval = parseCo2Interval(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCo2Interval(co2Interval);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_CO2_SWITCH:
                //CO2二氧化碳开关获取/设置(0x04)
                if (listeners != null && !listeners.isEmpty()) {
                    int co2Switch = parseCo2Switch(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCo2Switch(co2Switch);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_CO_ALARM:
                //CO一氧化碳报警获取/设置(0x05)
                if (listeners != null && !listeners.isEmpty()) {
                    int coDensity = parseCoAlarm(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCoDensity(coDensity);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_CO_INTERVAL:
                //CO一氧化碳工作间隔获取/设置(0x06)
                if (listeners != null && !listeners.isEmpty()) {
                    int coInterval = parseCoInterval(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCoInterval(coInterval);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_CO_SWITCH:
                //CO一氧化碳开关获取/设置(0x07)
                if (listeners != null && !listeners.isEmpty()) {
                    int coSwitch = parseCoSwitch(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onCoSwitch(coSwitch);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_O2_ALARM:
                //O2报警报警获取/设置(0x08)
                if (listeners != null && !listeners.isEmpty()) {
                    int o2Density = parseO2Alarm(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onO2Density(o2Density);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_HISTORY_INTERVAL:
                // 离线历史记录间隔获取/设置(0x09)
                if (listeners != null && !listeners.isEmpty()) {
                    int historyInterval = parseHistoryInterval(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onHistorySaveInterval(historyInterval);
                        }
                    }
                }

                break;

            case GasDetectorBleConfig.TYPE_TEMP_UNIT:
                //温度单位获取/设置(0x0A)
                if (listeners != null && !listeners.isEmpty()) {
                    int tempUnit = parseTempUnit(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onTempUnit(tempUnit);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_AIR_PRESSURE_UNIT:
                //气压单位获取/设置(0x0B)
                if (listeners != null && !listeners.isEmpty()) {
                    int pressureUnit = parseAirPressureUnit(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onPressureUnit(pressureUnit);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_AIR_PRESSURE_CALIBRATION:
                //设备气压校准(0x0C)
                if (listeners != null && !listeners.isEmpty()) {
                    int[] calibration = parseAirPressureCalibration(data);
                    int hPa = calibration[0];
                    int inHg = calibration[1];
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onPressureCalibration(hPa, inHg);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_BRIGHTNESS_SET:
                //亮度获取/设置(0x0D)
                if (listeners != null && !listeners.isEmpty()) {
                    int brightness = parseBrightness(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onBrightness(brightness);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_SOUND_SWITCH_SET:
                //声音开关获取/设置(0x0E)
                if (listeners != null && !listeners.isEmpty()) {
                    int soundSwitch = parseSoundSwitch(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onSoundSwitch(soundSwitch);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_SHOCK_SWITCH_SET:
                //震动开关获取/设置(0x0F)
                if (listeners != null && !listeners.isEmpty()) {
                    int shockSwitch = parseShockSwitch(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onShockSwitch(shockSwitch);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_SYNC_TIME:
                //时间同步(0x10)
                if (listeners != null && !listeners.isEmpty()) {
                    long time = parseTime(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onSyncTime(time);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_SCREEN_OFF_TIME:
                //息屏时间获取/设置(0x11)
                if (listeners != null && !listeners.isEmpty()) {
                    int screenOffTime = parseScreenOffTime(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onScreenOffTime(screenOffTime);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_SHUTDOWN_TIME:
                //关机时间获取/设置(0x12)
                if (listeners != null && !listeners.isEmpty()) {
                    int shutdownTime = parseShutdownTime(data);
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onShutdownTime(shutdownTime);
                        }
                    }
                }
                break;
            case GasDetectorBleConfig.TYPE_OFFLINE_HISTORY:
                //离线历史记录 (0x13)
                if (listeners != null && !listeners.isEmpty()) {
                    GasDetectorHistoryInfoBean infoBean = parseHistoryInfo(data);
                    if (infoBean == null) {
                        return;
                    }
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onHistoryInfo(infoBean);
                        }
                    }
                }

                break;
            case GasDetectorBleConfig.TYPE_CLEAR_OFFLINE_HISTORY:
                //清空离线历史记录 (0x14)
                if (listeners != null && !listeners.isEmpty()) {
                    for (OnGasDetectorInfoListener listener : listeners) {
                        if (listener != null) {
                            listener.onClearHistory();
                        }
                    }
                }
                break;
            default:
                throw new RuntimeException("未知指令:");

        }

    }


    /**
     * 实时信息(0x01)
     *
     * @param data 数据
     */
    private GasDetectorCurrentInfoBean parseCurrentInfo(byte[] data) {
        // 解析实时信息
        int tempUnit = data[1] & 0x01;
        int pressureUnit = data[1] >> 1 & 0x01;

        int co2 = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        int co = (data[4] & 0xFF) << 8 | (data[5] & 0xFF);
        int o2 = (data[6] & 0xFF);
        int tempNegative = (data[7] >> 7) & 0x01;
        int temp = (data[7] & 0xFF) << 8 | (data[8] & 0xFF);
        if (tempNegative == 0x01) {
            temp = -temp;
        }
        int humidity = (data[9] & 0xFF);
        int uv = (data[10] & 0xFF);
        int pressure = (data[11] & 0xFF) << 8 | (data[12] & 0xFF);
        int battery = (data[13] & 0xFF);
        return new GasDetectorCurrentInfoBean(co2, co, o2, tempUnit, temp, humidity, uv, pressureUnit, pressure, battery);

    }

    /**
     * CO2二氧化碳报警获取/设置(0x02)
     *
     * @param data 数据
     * @return int
     */
    private int parseCo2Alarm(byte[] data) {
        // 解析CO2报警
        int status = data[1] & 0xFF;
        int density = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return density;
    }

    /**
     * CO2二氧化碳工作间隔获取/设置(0x03)
     *
     * @param data 数据
     * @return int  间隔(单位min)0代表一直
     */
    private int parseCo2Interval(byte[] data) {
        int status = data[1] & 0xFF;
        int interval = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return interval;
    }

    /**
     * CO2二氧化碳开关获取/设置(0x04)
     *
     * @param data 数据
     * @return int 0x00 开  0x01 关
     */
    private int parseCo2Switch(byte[] data) {
        int status = data[1] & 0xFF;
        //0x00 开  0x01 关
        int open = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return open;
    }

    /**
     * CO一氧化碳报警获取/设置(0x05)
     *
     * @param data 数据
     * @return int 报警浓度(单位ppm)
     */
    private int parseCoAlarm(byte[] data) {
        int status = data[1] & 0xFF;
        int density = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return density;
    }

    /**
     * CO一氧化碳工作间隔获取/设置(0x06)
     *
     * @param data 数据
     * @return int 间隔(单位min)0代表一直
     */
    private int parseCoInterval(byte[] data) {
        int status = data[1] & 0xFF;
        int interval = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return interval;
    }

    /**
     * CO一氧化碳开关获取/设置(0x07)
     *
     * @param data 数据
     * @return int 0x00 开  0x01 关
     */
    private int parseCoSwitch(byte[] data) {
        int status = data[1] & 0xFF;
        //0x00 开  0x01 关
        int open = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return open;
    }

    /**
     * O2报警报警获取/设置(0x08)
     *
     * @param data 数据
     * @return int 报警浓度(单位%)
     */
    private int parseO2Alarm(byte[] data) {
        int status = data[1] & 0xFF;
        int density = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return density;
    }

    /**
     * 离线历史记录间隔获取/设置(0x09)
     *
     * @param data 数据
     * @return int 间隔(单位min)0代表一直
     */
    private int parseHistoryInterval(byte[] data) {
        int status = data[1] & 0xFF;
        int interval = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return interval;
    }


    /**
     * 温度单位获取/设置(0x0A)
     *
     * @param data 数据
     * @return int 0x00=℃  0x01=℉
     */
    private int parseTempUnit(byte[] data) {
        int status = data[1] & 0xFF;
        //0x00=℃
        //0x01=℉
        int unit = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return unit;
    }

    /**
     * 气压单位获取/设置(0x0B)
     *
     * @param data 数据
     * @return int0x00=hPa;0x01=inHg
     */
    private int parseAirPressureUnit(byte[] data) {
        int status = data[1] & 0xFF;
        //0x00=hPa
        //0x01=inHg
        int unit = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return unit;
    }

    /**
     * 设备气压校准(0x0C)
     *
     * @param data 数据
     * @return int[] 0=hPa;1=inHg
     */
    private int[] parseAirPressureCalibration(byte[] data) {
        int hPa = (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
        int inHg = (data[3] & 0xFF) << 8 | (data[4] & 0xFF);
        return new int[]{hPa, inHg};
    }

    /**
     * 亮度获取/设置(0x0D)
     *
     * @param data 数据
     * @return int
     */
    private int parseBrightness(byte[] data) {
        int status = data[1] & 0xFF;
        //亮度(%,0-100)
        int brightness = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return brightness;
    }


    /**
     * 声音开关获取/设置(0x0E)
     *
     * @param data 数据
     * @return int 0x00 开  0x01 关
     */
    private int parseSoundSwitch(byte[] data) {
        int status = data[1] & 0xFF;
        //0x00 开
        //0x01 关
        int open = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return open;
    }

    /**
     * 震动开关获取/设置(0x0F)
     *
     * @param data 数据
     * @return int
     */
    private int parseShockSwitch(byte[] data) {
        int status = data[1] & 0xFF;
        //0x00 开
        //0x01 关
        int open = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return open;
    }

    private long parseTime(byte[] data) {
        return (data[1] & 0xFFL) << 24 | (data[2] & 0xFFL) << 16 | (data[3] & 0xFFL) << 8 | (data[4] & 0xFFL);
    }

    /**
     * 息屏时间获取/设置(0x11)
     *
     * @param data 数据
     * @return int 时间(单位,秒)0代表从不
     */
    private int parseScreenOffTime(byte[] data) {
        int status = data[1] & 0xFF;
        int time = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return time;
    }

    /**
     * 关机时间获取/设置(0x12)
     *
     * @param data 数据
     * @return int 时间(单位,小时H)0代表从不
     */
    private int parseShutdownTime(byte[] data) {
        int status = data[1] & 0xFF;
        int time = (data[2] & 0xFF);
        if (status == GasDetectorBleConfig.STATUS_GET_DATA) {
            //获取数据
        } else {
            //设置数据
        }
        return time;
    }

    private Map<Integer, GasDetectorHistoryInfoBean> map = new HashMap<>();

    /**
     * 离线历史记录 (0x13)
     *
     * @param data 数据
     * @return {@link GasDetectorHistoryInfoBean}
     */
    private GasDetectorHistoryInfoBean parseHistoryInfo(byte[] data) {
        int count = (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
        GasDetectorHistoryInfoBean bean = map.get(count);
        if (bean == null) {
            bean = new GasDetectorHistoryInfoBean();
        }
        bean.setCount(count);
        int type = data[3] & 0xFF;
        if (type == 0x01) {
            //第一段
            long time = (data[4] & 0xFFL) << 24 | (data[5] & 0xFFL) << 16 | (data[6] & 0xFFL) << 8 | (data[7] & 0xFFL);
            int tempUnit = data[8] & 0x01;
            int pressureUnit = data[8] >> 1 & 0x01;
            int co2 = (data[9] & 0xFF) << 8 | (data[10] & 0xFF);
            int co = (data[11] & 0xFF) << 8 | (data[12] & 0xFF);
            int o2 = (data[13] & 0xFF);
            bean.setTimestamp(time);
            bean.setTempUnit(tempUnit);
            bean.setPressureUnit(pressureUnit);
            bean.setCo2(co2);
            bean.setCo(co);
            bean.setO2(o2);
            map.put(count, bean);
            return null;
        } else {
            //第二段
            int allCount = (data[4] & 0xFF) << 8 | (data[5] & 0xFF);
            int tempNegative = (data[6] >> 7) & 0x01;
            int temp = (data[6] & 0xFF) << 8 | (data[7] & 0xFF);
            if (tempNegative == 0x01) {
                temp = -temp;
            }
            int humidity = (data[8] & 0xFF);
            int uv = (data[9] & 0xFF);
            int pressure = (data[10] & 0xFF) << 8 | (data[11] & 0xFF);
            bean.setAllCount(allCount);
            bean.setTemp(temp);
            bean.setHumidity(humidity);
            bean.setUv(uv);
            bean.setPressure(pressure);
            map.remove(count);
            return bean;
        }

    }


}
