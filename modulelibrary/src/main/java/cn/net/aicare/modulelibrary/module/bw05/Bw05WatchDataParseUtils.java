package cn.net.aicare.modulelibrary.module.bw05;

import java.nio.charset.StandardCharsets;

/**
 * @author ljl
 * on 2024/6/4
 */
public class Bw05WatchDataParseUtils {

    /**
     * 判断设备发过来的数据是否符合蓝牙协议
     *
     * @param hex 数据
     * @return
     */
    public static boolean isLegalData(byte[] hex) {
        String hexStr = new String(hex, StandardCharsets.UTF_8);
        if (hexStr.length() > 6) {
            if (hexStr.substring(0, 2).equalsIgnoreCase(Bw05WatchCMDConfig.BW_05_START_ID)
                    && hexStr.substring(2, 4).equalsIgnoreCase(Bw05WatchCMDConfig.DEVICE_SEND_CMD_ID)
                    && hexStr.substring(hexStr.length() - 1).equalsIgnoreCase(Bw05WatchCMDConfig.BW_05_END_ID)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断设备发过来的数据是否符合蓝牙协议
     *
     * @param hex 数据
     * @return
     */
    public static boolean isLegalDataOther(byte[] hex) {
        String hexStr = new String(hex, StandardCharsets.UTF_8);
        if (hexStr.length() > 6) {
            if (hexStr.substring(0, 2).equalsIgnoreCase(Bw05WatchCMDConfig.BW_05_START_ID)
                    && hexStr.substring(2, 4).equalsIgnoreCase(Bw05WatchCMDConfig.APP_SEND_CMD_ID)
                    && hexStr.substring(hexStr.length() - 1).equalsIgnoreCase(Bw05WatchCMDConfig.BW_05_END_ID)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取返回的数据类型
     *
     * @param hex
     * @return
     */
    public static int getDataType(byte[] hex) {
        String hexStr = new String(hex, StandardCharsets.UTF_8);
        try {
            return Integer.parseInt(hexStr.substring(4, 6));
        } catch (Exception e) {
            return 255;
        }
    }

    /**
     * 转整数
     *
     * @param data 数据
     * @return
     */
    public static int parseData2Int(String data) {
        try {
            return Integer.parseInt(data);
        } catch (Exception e) {
            return -65535;
        }
    }

    /**
     * 转浮点型
     *
     * @param data 数据
     * @return
     */
    public static float parseData2Float(String data) {
        try {
            return Float.parseFloat(data);
        } catch (Exception e) {
            return -65535f;
        }
    }

}
