package cn.net.aicare.modulelibrary.module.gasDetectorPlus.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.net.aicare.modulelibrary.module.gasDetectorPlus.GasDetectorPlusBleConfig;

/**
 * 气体探测器当前信息bean
 *
 * @author xing
 * @date 2024/09/05
 */
public class GasDetectorPlusCurrentInfoBean {
    /**
     * 时间(秒)
     */
    public long timeSeconds;
    public Map<Integer, GasInfo> mGasInfoMap = new HashMap<>();

    public GasDetectorPlusCurrentInfoBean() {
    }

    public GasDetectorPlusCurrentInfoBean(long timeSeconds, Map<Integer, GasInfo> gasInfoMap) {
        this.timeSeconds = timeSeconds;
        mGasInfoMap.putAll(gasInfoMap);
    }



    public void addGasInfo(int type, int value, int unit, int decimal) {
        GasInfo gasInfo = new GasInfo(type, value, unit, decimal);
        addGasInfo(gasInfo);
    }

    public void addGasInfo(GasInfo gasInfo) {
        mGasInfoMap.put(gasInfo.type, gasInfo);
    }

    public void removeGasInfo(int type) {
        mGasInfoMap.remove(type);
    }

    public GasInfo getGasInfo(int type) {
        return mGasInfoMap.get(type);
    }

    public Map<Integer, GasInfo> getGasInfoMap() {
        return mGasInfoMap;
    }

    public List<GasInfo> getGasInfoList() {
        List<GasInfo> list = new ArrayList<>(mGasInfoMap.values());
        list.sort(Comparator.comparingInt(o -> o.type));
        return list;
    }

    public long getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(long timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    /**
     * 气体信息
     *
     * @author xing
     * @date 2024/10/23
     */
    public static class GasInfo {

        /**
         * 类型{@link GasDetectorPlusBleConfig#GasType}
         */
        public int type;
        /**
         * 值
         */
        public int value;

        public int unit;
        public int decimal;
        public String valueStr;

        public GasInfo() {
        }

        public GasInfo(int type, int value, int unit, int decimal) {
            this.type = type;
            this.value = value;
            this.unit = unit;
            this.decimal = decimal;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getUnit() {
            return unit;
        }

        public void setUnit(int unit) {
            this.unit = unit;
        }

        public int getDecimal() {
            return decimal;
        }

        public void setDecimal(int decimal) {
            this.decimal = decimal;
        }

        public String getValueStr() {
            return valueStr;
        }

        public void setValueStr(String valueStr) {
            this.valueStr = valueStr;
        }

        @Override
        public String toString() {
            return "GasInfo{" +
                    "type=" + type +
                    ", value=" + value +
                    ", unit=" + unit +
                    ", decimal=" + decimal +
                    ", valueStr='" + valueStr + '\'' +
                    '}';
        }
    }
}
