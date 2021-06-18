package cn.net.aicare.modulelibrary.module.BLDBodyfatScale;




import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleDataUtils;

/**
 * 体脂秤指令
 */
public class BLDBodyFatDataUtil {
    /**
     * 实时体重 Change weight
     */
    public final static int WEIGHT_TESTING = 0x01;
    /**
     * 稳定体重(Stabilize weight)
     */
    public final static int WEIGHT_RESULT = 0x02;
    /**
     * 温度数据  Temperature data
     */
    public final static int TEMPERATURE = 0x03;
    /**
     * 测阻抗中  Measuring impedance
     */
    public final static int IMPEDANCE_TESTING = 0x04;
    /**
     * 测阻抗成功，带上阻抗数据
     * Successful impedance measurement, bring impedance data
     */
    public final static int IMPEDANCE_SUCCESS_DATA = 0x05;
    /**
     * 测阻抗失败 Failed to measure impedance
     */
    public final static int IMPEDANCE_FAIL = 0x06;
    /**
     * 测阻抗成功,带上阻抗数据
     * Successful impedance measurement, bring impedance data
     */
    public final static int IMPEDANCE_SUCCESS = 0x07;


    /**
     * 请求用户
     */
    public final static int REQUESR_USER_INFO = 0x08;

    /**
     * 体脂数据 Body fat data
     */
    public final static int BODY_FAT = 0x0b;

    /**
     * 测量完成
     * Measurement completed
     */
    public final static int TEST_FINISH = 0x0a;
    /**
     * 设置单位 Set unit
     */
    public final static int SET_UNIT_CALLBAKC = 0x82;
    /**
     * 请求app当前单位
     */

    public final static int REQUEST_CURRENT_UNIT= 0x83;

    public final static int ERROR_CODE = 0xff;


    /**
     * 请求下发用户 Request to send the user
     */
    public final static int REQUEST_SEND_USER = 0x01;
    /**
     * 请求下发用户Muc接收成功  The Muc of the requested sending user received successfully
     */
    public final static int REQUEST_SEND_USER_RECEIVE_SUCESS = 0x03;
    /**
     * 请求下发用户Muc接收失败  The request to send user Muc received failed
     */
    public final static int REQUEST_SEND_USER_RECEIVE_FAIL = 0x01;


    public class WeightUnit {
        /**
         * 千克
         */
        public final static int KG = 0;
        /**
         * 纯lb
         */
        public final static int LB = 1;
        /**
         * st:lb
         */
        public final static int ST = 2;
        /**
         * 斤
         */
        public final static int JIN = 3;
    }

    public class UserEum {
        /**
         * 普通模式 Normal mode
         */
        public final static int MODE_ORDINARY = 0;
        /**
         * 运动员模式 Athlete mode
         */
        public final static int MODE_ATHLETE = 1;
        /**
         * 专业运动员 professional athlete
         */
        public final static int PROFESSIONAL_ATHLETE = 2;

        /**
         * 男 Male
         */
        public final static int SEX_MAN = 1;

        /**
         * 女 Female
         */
        public final static int SEX_FEMAN = 0;
    }


    public class UnitResult {

        public final static int SUCCESS = 0; //0：设置成功
        public final static int FAIL = 1; //1：设置失败
        public final static int NOTSUPPORT = 3; //2：设置成功
    }

    public class Symbol {
        public final static int MINUS = 1; //1：负数(minus)
        public final static int POSITIVE = 0; //0：正数(positive)
    }

    private BLDBodyFatDataUtil() {
    }


    private static class InnerClass {
        private static BLDBodyFatDataUtil bodyFatDataUtil = new BLDBodyFatDataUtil();
    }

    public static BLDBodyFatDataUtil getInstance() {
        return InnerClass.bodyFatDataUtil;
    }


    /**
     * 解析体重 Analyze weight
     */
    public int getWeight(byte[] by) {

        int weightH = ((by[1] & 0x7f) << 16);
        int weightM = ((by[2] & 0xff) << 8);
        int weightL = (by[3] & 0xff);

        return weightH + weightM + weightL;

    }

    /**
     * 解析体重符号 Analyze weight Symbol
     *
     * @param b
     * @return
     */
    public int getWeightSymbol(byte b) {

        return ((b &0x80)>>7);
    }

    /**
     * 解析体重小数位 Parsing decimal places of weight
     */
    public int getWeightPoint(byte[] b) {
        return ((b[4]&0xf0)>>4);

    }


    /**
     * 解析体重单位 Analyze weight units
     */
    public int getWeightUnit(byte[] b) {
        int type = b[4] & 0x0f;
        return type;
    }


    /**
     * 解析温度 Resolution temperature
     */
    public int getTemperature(byte[] b) {

        int temper = ((b[1] & 0x7f) << 8) + b[2] & 0xff;


        return temper;
    }

    public int getTemperatureSymbol(byte b) {

        return ((b&0x80) >>7);
    }

    /**
     * 解析阻抗值 Analyze impedance value
     */

    public int getImpedance(byte[] b) {
        return ((b[1] & 0xff) << 8) + (b[2] & 0xff);
    }


    /**
     * 设置单位返回 Set unit return
     *
     * @return 0：设置成功 1：设置失败 2：不支持设置(set up successfully 1: set up failed 2: set up is not supported)
     */
    public int setUnitCallback(byte[] b) {

        return b[1] & 0xff;

    }

    /**
     * 获取单位的支持列表
     *
     * @return
     */
    public SendBleBean getUnitSupportUnit() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().getSupportUnit());
        return sendBleBean;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public SendBleBean getVersion() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().getBleVersion());
        return sendBleBean;
    }

    /**
     * 设置单位 Set unit
     * 主动调用 Active call
     *
     * @param type 单位类型 unit type
     */
    public SendMcuBean setWeightUnit(int type) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x81;
        bytes[1] = (byte) type;
        smb.setHex(0x0e, bytes);
        return smb;
    }


    /**
     * 请求下发用户
     * Muc会发送命令，接收到命令后下发
     * 被动下发
     * Request to release users
     * Muc will send a command, and then send it after receiving the command
     * Passive delivery
     *
     * @param user 用户 user
     */
    public SendMcuBean setUserInfo(BLDUser user) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0x08;
        bytes[1] = 0x02;
        int mode = user.getModeType();
        int id = user.getId();
        bytes[2] = (byte) (((mode & 0xff) << 4) + (id & 0xff));//高4位是模式，低四位是编号
        int sex = user.getSex();
        int age = user.getAge();
        bytes[3] = (byte) ((((sex & 0xff)) << 7) + (age & 0x7f));     //最高位是性别，低7位年龄
        int height = user.getHeight() * 10;

        bytes[4] = (byte) (((height & 0xff00))>>8);     //身高 cm
        bytes[5] = (byte) (height & 0xff);     //身高 cm
        sendMcuBean.setHex(0X0E, bytes);
//        DeviceConfig.WEIGHT_BODY_FAT_SCALE
        return sendMcuBean;
    }


    /**
     * 用于与蓝牙模块交互,封装模型
     * Used to interact with Bluetooth module, package model
     */

    private SendBleBean getSendBleBeam(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        return sendBleBean;
    }


    /**
     * 同步系统时间
     * Synchronize system time
     *
     * @return SendBleBean
     */
    public SendBleBean synSysTime() {
        byte[] currentTime = BleDataUtils.getInstance().getCurrentTime();
        return getSendBleBeam(BleSendCmdUtil.getInstance().setSysTime(currentTime, true));
    }


}
