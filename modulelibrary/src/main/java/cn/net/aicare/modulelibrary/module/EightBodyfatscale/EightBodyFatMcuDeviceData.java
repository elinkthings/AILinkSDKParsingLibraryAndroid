package cn.net.aicare.modulelibrary.module.EightBodyfatscale;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 八电极MCU算法
 *
 * @author xing
 * @date 2024/08/28
 */
public class EightBodyFatMcuDeviceData extends BaseBleDeviceData {

    private onEightBodyFatMcuCallback mOnEightBodyFatMcuCallback;
    private final int cid = EightBodyFatUtil.EIGHT_BODY_FAT_SCALE_MCU;
    private EightBodyFatInfo mEightBodyFatInfo;

    public EightBodyFatMcuDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onVersion(version);
                }
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onSupportUnit(list);
                }
            }
        });

    }


    public void setEightBodyFatCallback(onEightBodyFatMcuCallback onEightBodyFatMcuCallback) {
        mOnEightBodyFatMcuCallback = onEightBodyFatMcuCallback;
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mOnEightBodyFatMcuCallback != null) {
            mOnEightBodyFatMcuCallback.showData(BleStrUtils.byte2HexStr(hex));
        }

        int cmd = hex[0] & 0xff;
        int status = hex[1] & 0xff;
        switch (cmd) {
            case EightBodyFatUtil.WEIGHING:
                //体重 0x01
                int weight = getWeight(hex);
                int unit = getWeightUnit(hex);
                int decimal = getDecimal(hex);
                if (status == 2) {
                    //温度体重
                    mEightBodyFatInfo = new EightBodyFatInfo();
                    mEightBodyFatInfo.setWeight(weight);
                    mEightBodyFatInfo.setWeightUnit(unit);
                    mEightBodyFatInfo.setWeightDecimal(decimal);
                }
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onWeight(status, weight, unit, decimal);
                }

                break;
            case EightBodyFatUtil.IMPEDANCE:
                //测量阻抗 0x02
                int adc = getAdc(hex);
                int part = hex[2] & 0xFF;
                EightBodyFatAdc adcBean = new EightBodyFatAdc(part, adc);
                if (status == 5) {
                    //01：测阻抗中
                    //02：测阻抗失败
                    //05：阻抗测量成功，并使用 MCU 端算法。体脂算法 ID=0。

                    if (mEightBodyFatInfo != null) {
                        mEightBodyFatInfo.addAdc(adcBean);
                    }
                }
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onImpedance(status, adcBean, 0x00);
                }
                break;
            case EightBodyFatUtil.HEART_RATE:
                //心率 0x03
                int heartRate = hex[2] & 0xFF;
                if (status == 2) {
                    //01：测心率中
                    //02：测心率成功，带上心率数据
                    //03：测心率失败
                    if (mEightBodyFatInfo != null) {
                        mEightBodyFatInfo.setHeartRate(heartRate);
                    }
                }
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onHeartRate(status, heartRate);
                }
                break;
            case EightBodyFatUtil.TEMP_MEASUREMENT:
                //温度 0x04
                //0 ：正温度1 ：负温度
                int sign = hex[1] & 0xFF;
                int temp = getTemp(hex);
                int tempUnit = getTempUnit(hex);
                int tempDecimal = getTempDecimal(hex);
                if (mEightBodyFatInfo != null) {
                    mEightBodyFatInfo.setTempSign(sign);
                    mEightBodyFatInfo.setTemp(temp);
                    mEightBodyFatInfo.setTempUnit(tempUnit);
                    mEightBodyFatInfo.setTempDecimal(tempDecimal);
                }
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onTemp(sign, temp, tempUnit, tempDecimal);
                }
                break;

            case EightBodyFatUtil.TYPE_HEIGHT:
                //身高数据 0x05
                int height = getHeight(hex);
                int heightUnit = getHeightUnit(hex);
                if (status == 2) {
                    //01：测身高中
                    //02：测身高成功
                    //03：测身高失败
                    if (mEightBodyFatInfo != null) {
                        mEightBodyFatInfo.setHeight(height);
                        mEightBodyFatInfo.setHeightUnit(heightUnit);
                    }
                }
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onHeight(status, height, heightUnit);
                }

                break;
            case EightBodyFatUtil.TYPE_SYNC_USER_INFO:
                //同步用户信息 0x08
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onSyncUserInfo();
                }
                break;
            case EightBodyFatUtil.TYPE_BODY_FAT_INFO:
                //体脂数据 0x09
                if (mEightBodyFatInfo == null) {
                    mEightBodyFatInfo = new EightBodyFatInfo();
                }
                if (status == 1) {
                    mEightBodyFatInfo.setBmi(getInt(hex, 2, 2) / 10.0);
                    mEightBodyFatInfo.setBfr(getInt(hex, 4, 2) / 10.0);
                    mEightBodyFatInfo.setRom(getInt(hex, 6, 2) / 10.0);
                    mEightBodyFatInfo.setFatMassLeftUp(getInt(hex, 8, 2) / 10.0);
                    mEightBodyFatInfo.setFatMassRightUp(getInt(hex, 10, 2) / 10.0);
                } else if (status == 2) {
                    mEightBodyFatInfo.setFatMassBody(getInt(hex, 2, 2) / 10.0);
                    mEightBodyFatInfo.setFatMassLeftDown(getInt(hex, 4, 2) / 10.0);
                    mEightBodyFatInfo.setFatMassRightDown(getInt(hex, 6, 2) / 10.0);
                    mEightBodyFatInfo.setRomMassLeftUp(getInt(hex, 8, 2) / 10.0);
                    mEightBodyFatInfo.setRomMassRightUp(getInt(hex, 10, 2) / 10.0);
                } else if (status == 3) {
                    mEightBodyFatInfo.setRomMassBody(getInt(hex, 2, 2) / 10.0);
                    mEightBodyFatInfo.setRomMassLeftDown(getInt(hex, 4, 2) / 10.0);
                    mEightBodyFatInfo.setRomMassRightDown(getInt(hex, 6, 2) / 10.0);
                    mEightBodyFatInfo.setVwc(getInt(hex, 8, 2) / 10.0);
                    mEightBodyFatInfo.setBm(getInt(hex, 10, 2) / 10.0);
                } else if (status == 4) {
                    mEightBodyFatInfo.setBmr(getInt(hex, 2, 2));
                    mEightBodyFatInfo.setPp(getInt(hex, 4, 2) / 10.0);
                    mEightBodyFatInfo.setUvi(getInt(hex, 6, 2));
                    mEightBodyFatInfo.setSfr(getInt(hex, 8, 2) / 10.0);
                    mEightBodyFatInfo.setHeightCm(getInt(hex, 10, 1));
                    mEightBodyFatInfo.setBodyAge(getInt(hex, 11, 1));
                }
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onBodyFatData(status, mEightBodyFatInfo.copy());
                }

                break;

            case EightBodyFatUtil.TYPE_COMPLETE_DATA:
                //补全数据 0x0E
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onCompletionData();
                }
                break;

            case EightBodyFatUtil.MEASUREMENT_END:
                //测量完成
                if (mOnEightBodyFatMcuCallback != null && mEightBodyFatInfo != null) {
                    mOnEightBodyFatMcuCallback.onTestSuccess(mEightBodyFatInfo.copy());
                    mEightBodyFatInfo = null;
                    //APP 回复测量完成
                    setTestSuccess();
                }
                break;
            case EightBodyFatUtil.MUC_CALL_BACK_RESULT:
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onState(status, hex[2] & 0xFF);
                }
                break;
            case EightBodyFatUtil.ERROR_CODE:
                int err = hex[1] & 0xFF;
                if (mOnEightBodyFatMcuCallback != null) {
                    mOnEightBodyFatMcuCallback.onErrCode(err);
                }
                break;
        }
    }


    /**
     * 获取int
     * hex转int, 大端序
     *
     * @param hex   十六进制
     * @param start 开始
     * @param len   len
     * @return int
     */
    private int getInt(byte[] hex, int start, int len) {
        int data = 0;
        if (hex.length >= start + len) {
            for (int i = 0; i < len; i++) {
                int bit = (len - 1 - i) * 8;
                data |= ((hex[i + start] & 0xFF) << bit);
            }
        }
        if (data == (Math.pow(2, len * 8) - 1)) {
            return 0;
        }
        return data;
    }


    /**
     * 获取重量
     *
     * @param hex 十六进制
     * @return float
     */
    private int getWeight(byte[] hex) {
        if (hex.length >= 6) {
            int weightHigh = (hex[2] & 0xFF) << 16;
            int weightMiddle = (hex[3] & 0xFF) << 8;
            int weightLow = hex[4] & 0xFF;
            return weightHigh | weightMiddle | weightLow;
        }

        return -1;
    }


    /**
     * 获取小数
     *
     * @param hex 十六进制
     * @return int
     */
    private int getDecimal(byte[] hex) {
        if (hex.length >= 6) {
            return (hex[5] & 0xF0) >> 4;
        }
        return 0;
    }

    /**
     * 获取单位
     *
     * @param hex 十六进制
     * @return int
     */
    private int getWeightUnit(byte[] hex) {
        if (hex.length >= 6) {
            return (hex[5] & 0x0F);
        }
        return 0;
    }

    /**
     * 获取高度
     *
     * @param hex 十六进制
     * @return int
     */
    private int getHeight(byte[] hex) {
        if (hex.length >= 6) {
            int heightHigh = (hex[3] & 0xFF) << 8;
            int heightLow = hex[4] & 0xFF;
            return heightHigh | heightLow;
        }
        return -1;
    }

    /**
     * 获取身高单位
     * 0x00：cm
     * 0x01：inch
     * 0x02：ft-in
     *
     * @param hex 十六进制
     * @return int
     */
    private int getHeightUnit(byte[] hex) {
        if (hex.length >= 6) {
            return (hex[2] & 0xFF);
        }
        return 0;
    }

    /**
     * 获取阻抗
     *
     * @param hex 十六进制
     * @return int
     */
    private int getAdc(byte[] hex) {
        if (hex.length >= 9) {
            int highAdc = (hex[3] & 0xFF) << 24;
            int highAdc1 = (hex[4] & 0xFF) << 16;
            int lowAdc = (hex[5] & 0xFF) << 8;
            int lowAdc1 = (hex[6] & 0xFF);
            return highAdc + highAdc1 + lowAdc + lowAdc1;
        } else {
            return 0;
        }
    }

    /**
     * 获取温度
     *
     * @param hex 十六进制
     * @return float
     */
    private int getTemp(byte[] hex) {
        if (hex.length >= 5) {
            int tempHigh = (hex[2] & 0xFF) << 8;
            int tempLow = hex[3] & 0xFF;
            return tempHigh | tempLow;
        }
        return 0;
    }

    /**
     * 获取温度小数
     *
     * @param hex 十六进制
     * @return int
     */
    private int getTempDecimal(byte[] hex) {
        if (hex.length >= 5) {
            return (hex[4] & 0xF0) >> 4;
        }
        return -1;
    }

    /**
     * 获取温度单位
     *
     * @param hex 十六进制
     * @return int
     */
    private int getTempUnit(byte[] hex) {
        if (hex.length >= 5) {
            return (hex[4] & 0x0F);
        }
        return -1;
    }


    public interface onEightBodyFatMcuCallback {
        /**
         * 操作指令
         *
         * @param opCode op代码
         *               0x01 : 校准
         *               0x02 : 温度单位切换
         *               0x03：重量单位切换
         * @param result 结果
         *               0：操作成功
         *               1：操作失败
         *               2：正在操作中
         *               result
         */
        void onState(int opCode, int result);

        /**
         * 体重数据接口
         * Weight data interface
         *
         * @param state   测量状态 1实时体重 2稳定体重
         *                Measurement status 1 Real-time weight 2 Stable weight
         * @param weight  体重,原始数据
         *                weight
         *                0：kg
         *                1：斤
         *                4:st:lb
         *                6:lb
         * @param unit    单位
         *                unit
         * @param decimal 小数点位
         *                decimal
         */
        void onWeight(int state, int weight, int unit, int decimal);

        /**
         * 身高数据接口
         *
         * @param state  状态
         *               01：测身高中
         *               02：测身高成功，带上心率数据
         *               03：测身高失败
         * @param height 身高
         * @param unit   单位
         *               0x00：cm
         *               0x01：inch
         *               0x02：ft-in
         */
        void onHeight(int state, int height, int unit);

        /**
         * 阻抗
         * impedance
         *
         * @param status          状态
         *                        01：测阻抗中
         *                        02：测阻抗失败
         *                        05：阻抗测量成功，并使用 MCU 端算法。体脂算法 ID=0。
         * @param arithmetic      算法 ID
         *                        arithmetic
         *                        0x00
         * @param eightBodyFatAdc 阻抗
         */
        void onImpedance(int status, EightBodyFatAdc eightBodyFatAdc, int arithmetic);

        /**
         * 心率
         * heartRate
         *
         * @param status    状态
         *                  01：测心率中
         *                  02：测心率成功，带上心率数据
         *                  03：测心率失败
         * @param heartRate heartRate
         */
        void onHeartRate(int status, int heartRate);

        /**
         * 温度
         * temperature
         *
         * @param sign    正负 0：正数 1：负数
         *                Positive and negative
         * @param temp    温度,原始数据
         *                temperature
         * @param unit    单位
         *                unit
         *                0：℃
         *                1：℉
         * @param decimal 小数点位
         *                decimal
         */
        void onTemp(int sign, int temp, int unit, int decimal);

        /**
         * 获取到版本号
         * Get the version number
         *
         * @param version 版本号
         */
        void onVersion(String version);

        /**
         * MCU请求同步用户信息
         */
        void onSyncUserInfo();

        /**
         * 请求补全数据
         */
        void onCompletionData();

        /**
         * 体脂数据
         *
         * @param step    步骤
         * @param bodyFat 体脂数据
         */
        void onBodyFatData(int step, EightBodyFatInfo bodyFat);

        /**
         * 测量完成
         *
         * @param bodyFat 身体脂肪
         */
        void onTestSuccess(EightBodyFatInfo bodyFat);

        /**
         * 获取到支持的单位列表
         * Get the list of supported units
         *
         * @param list 单位列表
         */
        void onSupportUnit(List<SupportUnitBean> list);

        /**
         * 错误码
         *
         * @param code 代码
         *             1：超重
         */
        void onErrCode(int code);


        void showData(String data);

    }


    /**
     * 设置重量单位
     *
     * @param unit 单位
     *             0：kg
     *             1：斤
     *             4：st:lb
     *             6：lb
     */
    public void setWeightUnit(int unit) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x03;
        bytes[2] = (byte) unit;
        bytes[3] = 0;
        smb.setHex(cid, bytes);
        sendData(smb);
    }

    /**
     * 设置身高单位
     *
     * @param unit 单位
     *             0：cm
     *             1：inch
     *             2：ft-in
     */
    public void setHeightUnit(int unit) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x04;
        bytes[2] = (byte) unit;
        bytes[3] = 0;
        smb.setHex(cid, bytes);
        sendData(smb);
    }

    /**
     * 设置温度单位
     *
     * @param unit 单位
     *             0：摄氏度℃
     *             1：华氏度℉
     */
    public void setTempUnit(int unit) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x02;
        bytes[2] = (byte) unit;
        bytes[3] = 0;
        smb.setHex(cid, bytes);
        sendData(smb);
    }

    /**
     * 设置校准
     */
    public void setCalibration() {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x81;
        bytes[1] = 0x01;
        bytes[2] = 0;
        bytes[3] = 0;
        smb.setHex(cid, bytes);
        sendData(smb);
    }

    public void getUnitList() {
        byte[] supportUnit = BleSendCmdUtil.getInstance().getSupportUnit();
        sendData(new SendBleBean(supportUnit));
    }

    /**
     * APP 回复测量完成
     */
    public void setTestSuccess() {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x84;
        bytes[1] = (byte) 0x00;
        smb.setHex(cid, bytes);
        sendData(smb);
    }

    /**
     * 设置用户信息
     *
     * @param userId   用户id
     * @param userType 用户类型
     *                 0：普通人
     *                 1：业余运动员
     *                 2：专业运动员
     *                 3：孕妇
     * @param sex      性别
     *                 0：女
     *                 1：男
     * @param age      年龄
     * @param heightCm 身高厘米
     */
    public void setUserInfo(int userId, int userType, int sex, int age, int heightCm) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[5];
        bytes[0] = (byte) 0x08;
        bytes[1] = (byte) 0x02;
        bytes[2] = (byte) ((userType << 4) | userId);
        bytes[3] = (byte) (sex << 7 | age);
        bytes[4] = (byte) heightCm;
        smb.setHex(cid, bytes);
        sendData(smb);
    }

    /**
     * 八电极阻抗数据
     *
     * @author xing
     * @date 2024/08/29
     */
    public static class EightBodyFatAdc {
        /**
         * 部位
         * 0x00 双脚阻抗 0x01 双手阻抗
         * 0x02 左手阻抗 0x03 右手阻抗
         * 0x04 左脚阻抗 0x05 右脚阻抗
         * 0x06 左全身阻抗 0x07 右全身阻抗
         * 0x08 右手左脚阻抗
         * 0x09 左手右脚阻抗 0x0A 躯干阻抗
         */
        private int part;
        /**
         * 阻抗
         */
        private int adc;

        public EightBodyFatAdc(int part, int adc) {
            this.part = part;
            this.adc = adc;
        }

        public int getPart() {
            return part;
        }

        public void setPart(int part) {
            this.part = part;
        }

        public int getAdc() {
            return adc;
        }

        public void setAdc(int adc) {
            this.adc = adc;
        }
    }


    /**
     * 八电极体脂数据对象
     *
     * @author xing
     * @date 2024/08/29
     */
    public static class EightBodyFatInfo {
        /**
         * 重量
         */
        private int weight;
        /**
         * 重量单位
         */
        private int weightUnit;
        /**
         * 体重小数点
         */
        private int weightDecimal;
        /**
         * 身高
         */
        private int height;
        /**
         * 身高单位
         */
        private int heightUnit;
        /**
         * 温度
         */
        private int temp;
        /**
         * 温度单位
         */
        private int tempUnit;
        /**
         * 温度小数点
         */
        private int tempDecimal;

        /**
         * 温度标志
         * 0 ：正温度
         * 1 ：负温度
         */
        private int tempSign;

        /**
         * 阻抗列表
         */
        private List<EightBodyFatAdc> adcList = new ArrayList<>();

        /**
         * 心率 0xFF代表不支持
         */
        private int heartRate = 0xFF;

        /**
         * 身体质量指数
         */
        private double bmi;
        /**
         * 全身体脂率
         */
        private double bfr;
        /**
         * 全身肌肉率
         */
        private double rom;
        /**
         * 左上肢脂肪量
         */
        private double fatMassLeftUp;
        /**
         * 右上肢脂肪量
         */
        private double fatMassRightUp;
        /**
         * 躯干脂肪量
         */
        private double fatMassBody;

        /**
         * 左下肢脂肪量
         */
        private double fatMassLeftDown;
        /**
         * 右下肢脂肪量
         */
        private double fatMassRightDown;

        /**
         * 左上肢肌肉量
         */
        private double romMassLeftUp;
        /**
         * 右上肢肌肉量
         */
        private double romMassRightUp;
        /**
         * 躯干肌肉量
         */
        private double romMassBody;

        /**
         * 左下肢肌肉量
         */
        private double romMassLeftDown;
        /**
         * 右下肢肌肉量
         */
        private double romMassRightDown;

        /**
         * 身体水分率
         */
        private double vwc;
        /**
         * 骨量
         */
        private double bm;
        /**
         * 基础代谢率
         */
        private int bmr;
        /**
         * 蛋白率
         */
        private double pp;
        /**
         * 内脏脂肪指数
         */
        private int uvi;
        /**
         * 皮下脂肪
         */
        private double sfr;
        /**
         * 身高(cm)
         */
        private int heightCm;
        /**
         * 身体年龄
         */
        private int bodyAge;

        /**
         * 算术ID(0xFFFF代表请求算法计算)
         */
        private int arithmeticId;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getWeightUnit() {
            return weightUnit;
        }

        public void setWeightUnit(int weightUnit) {
            this.weightUnit = weightUnit;
        }

        public int getWeightDecimal() {
            return weightDecimal;
        }

        public void setWeightDecimal(int weightDecimal) {
            this.weightDecimal = weightDecimal;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeightUnit() {
            return heightUnit;
        }

        public void setHeightUnit(int heightUnit) {
            this.heightUnit = heightUnit;
        }

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public int getTempUnit() {
            return tempUnit;
        }

        public void setTempUnit(int tempUnit) {
            this.tempUnit = tempUnit;
        }

        public int getTempDecimal() {
            return tempDecimal;
        }

        public void setTempDecimal(int tempDecimal) {
            this.tempDecimal = tempDecimal;
        }

        public int getTempSign() {
            return tempSign;
        }

        public void setTempSign(int tempSign) {
            this.tempSign = tempSign;
        }

        public List<EightBodyFatAdc> getAdcList() {
            return adcList;
        }

        public void setAdcList(List<EightBodyFatAdc> adcList) {
            this.adcList.clear();
            this.adcList.addAll(adcList);
        }

        public void addAdc(EightBodyFatAdc adc) {
            this.adcList.add(adc);
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }

        public double getBmi() {
            return bmi;
        }

        public void setBmi(double bmi) {
            this.bmi = bmi;
        }

        public double getBfr() {
            return bfr;
        }

        public void setBfr(double bfr) {
            this.bfr = bfr;
        }

        public double getRom() {
            return rom;
        }

        public void setRom(double rom) {
            this.rom = rom;
        }

        public double getFatMassLeftUp() {
            return fatMassLeftUp;
        }

        public void setFatMassLeftUp(double fatMassLeftUp) {
            this.fatMassLeftUp = fatMassLeftUp;
        }

        public double getFatMassRightUp() {
            return fatMassRightUp;
        }

        public void setFatMassRightUp(double fatMassRightUp) {
            this.fatMassRightUp = fatMassRightUp;
        }

        public double getFatMassBody() {
            return fatMassBody;
        }

        public void setFatMassBody(double fatMassBody) {
            this.fatMassBody = fatMassBody;
        }

        public double getFatMassLeftDown() {
            return fatMassLeftDown;
        }

        public void setFatMassLeftDown(double fatMassLeftDown) {
            this.fatMassLeftDown = fatMassLeftDown;
        }

        public double getFatMassRightDown() {
            return fatMassRightDown;
        }

        public void setFatMassRightDown(double fatMassRightDown) {
            this.fatMassRightDown = fatMassRightDown;
        }

        public double getRomMassLeftUp() {
            return romMassLeftUp;
        }

        public void setRomMassLeftUp(double romMassLeftUp) {
            this.romMassLeftUp = romMassLeftUp;
        }

        public double getRomMassRightUp() {
            return romMassRightUp;
        }

        public void setRomMassRightUp(double romMassRightUp) {
            this.romMassRightUp = romMassRightUp;
        }

        public double getRomMassBody() {
            return romMassBody;
        }

        public void setRomMassBody(double romMassBody) {
            this.romMassBody = romMassBody;
        }

        public double getRomMassLeftDown() {
            return romMassLeftDown;
        }

        public void setRomMassLeftDown(double romMassLeftDown) {
            this.romMassLeftDown = romMassLeftDown;
        }

        public double getRomMassRightDown() {
            return romMassRightDown;
        }

        public void setRomMassRightDown(double romMassRightDown) {
            this.romMassRightDown = romMassRightDown;
        }

        public double getVwc() {
            return vwc;
        }

        public void setVwc(double vwc) {
            this.vwc = vwc;
        }

        public double getBm() {
            return bm;
        }

        public void setBm(double bm) {
            this.bm = bm;
        }

        public int getBmr() {
            return bmr;
        }

        public void setBmr(int bmr) {
            this.bmr = bmr;
        }

        public double getPp() {
            return pp;
        }

        public void setPp(double pp) {
            this.pp = pp;
        }

        public int getUvi() {
            return uvi;
        }

        public void setUvi(int uvi) {
            this.uvi = uvi;
        }

        public double getSfr() {
            return sfr;
        }

        public void setSfr(double sfr) {
            this.sfr = sfr;
        }

        public int getHeightCm() {
            return heightCm;
        }

        public void setHeightCm(int heightCm) {
            this.heightCm = heightCm;
        }

        public int getBodyAge() {
            return bodyAge;
        }

        public void setBodyAge(int bodyAge) {
            this.bodyAge = bodyAge;
        }

        public int getArithmeticId() {
            return arithmeticId;
        }

        public void setArithmeticId(int arithmeticId) {
            this.arithmeticId = arithmeticId;
        }

        /**
         * 复制
         *
         * @return {@link EightBodyFatInfo}
         */
        public EightBodyFatInfo copy() {
            EightBodyFatInfo eightBodyFatInfo = new EightBodyFatInfo();
            eightBodyFatInfo.setWeight(weight);
            eightBodyFatInfo.setWeightUnit(weightUnit);
            eightBodyFatInfo.setWeightDecimal(weightDecimal);
            eightBodyFatInfo.setHeight(height);
            eightBodyFatInfo.setHeightUnit(heightUnit);
            eightBodyFatInfo.setTempSign(tempSign);
            eightBodyFatInfo.setTemp(temp);
            eightBodyFatInfo.setTempUnit(tempUnit);
            eightBodyFatInfo.setTempDecimal(tempDecimal);
            eightBodyFatInfo.setAdcList(adcList);
            eightBodyFatInfo.setHeartRate(heartRate);
            eightBodyFatInfo.setBmi(bmi);
            eightBodyFatInfo.setBfr(bfr);
            eightBodyFatInfo.setRom(rom);
            eightBodyFatInfo.setFatMassLeftUp(fatMassLeftUp);
            eightBodyFatInfo.setFatMassRightUp(fatMassRightUp);
            eightBodyFatInfo.setFatMassBody(fatMassBody);
            eightBodyFatInfo.setFatMassLeftDown(fatMassLeftDown);
            eightBodyFatInfo.setFatMassRightDown(fatMassRightDown);
            eightBodyFatInfo.setRomMassLeftUp(romMassLeftUp);
            eightBodyFatInfo.setRomMassRightUp(romMassRightUp);
            eightBodyFatInfo.setRomMassBody(romMassBody);
            eightBodyFatInfo.setRomMassLeftDown(romMassLeftDown);
            eightBodyFatInfo.setRomMassRightDown(romMassRightDown);
            eightBodyFatInfo.setVwc(vwc);
            eightBodyFatInfo.setBm(bm);
            eightBodyFatInfo.setBmr(bmr);
            eightBodyFatInfo.setPp(pp);
            eightBodyFatInfo.setUvi(uvi);
            eightBodyFatInfo.setSfr(sfr);
            eightBodyFatInfo.setHeightCm(heightCm);
            eightBodyFatInfo.setBodyAge(bodyAge);
            return eightBodyFatInfo;
        }

        @Override
        public String toString() {
            return "EightBodyFatInfo{" +
                    "重量=" + weight +
                    ", 重量单位=" + weightUnit +
                    ", 体重小数点=" + weightDecimal +
                    ", 身高=" + height +
                    ", 身高单位=" + heightUnit +
                    ", 温度=" + temp +
                    ", 温度单位=" + tempUnit +
                    ", 温度小数点=" + tempDecimal +
                    ", 温度标志=" + tempSign +
                    ", adcList=" + adcList.size() +
                    ", 心率=" + heartRate +
                    ", bmi=" + bmi +
                    ", 全身体脂率=" + bfr +
                    ", 全身肌肉率=" + rom +
                    ", 左上肢脂肪量=" + fatMassLeftUp +
                    ", 右上肢脂肪量=" + fatMassRightUp +
                    ", 躯干脂肪量=" + fatMassBody +
                    ", 左下肢脂肪量=" + fatMassLeftDown +
                    ", 右下肢脂肪量=" + fatMassRightDown +
                    ", 左上肢肌肉量=" + romMassLeftUp +
                    ", 右上肢肌肉量=" + romMassRightUp +
                    ", 躯干肌肉量=" + romMassBody +
                    ", 左下肢肌肉量=" + romMassLeftDown +
                    ", 右下肢肌肉量=" + romMassRightDown +
                    ", 身体水分率=" + vwc +
                    ", 骨量=" + bm +
                    ", 基础代谢率=" + bmr +
                    ", 蛋白率=" + pp +
                    ", 内脏脂肪指数=" + uvi +
                    ", 皮下脂肪=" + sfr +
                    ", 身高(cm)=" + heightCm +
                    ", 身体年龄=" + bodyAge +
                    '}';
        }
    }

}
