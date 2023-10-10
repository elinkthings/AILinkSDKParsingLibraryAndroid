package cn.net.aicare.modulelibrary.module.BodyFatScale;


import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleDataUtils;

/**
 * 体脂秤指令
 */
public class BodyFatDataUtil {
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
     * 请求获取数据 Request data
     */
    public final static int MUC_REQUEST_USER_INFO = 0x08;
    /**
     * 体脂数据 Body fat data
     */
    public final static int BODY_FAT = 0x09;
    /**
     * 测量心率 Measuring heart rate
     */
    public final static int HEART_TESTING = 0x0b;
    /**
     * 测量心率成功
     * Successful heart rate measurement
     */
    public final static int HEART_SUCCESS = 0x0c;
    /**
     * 测量心率失败
     * Failure to measure heart rate
     */
    public final static int HEART_FAIL = 0x0d;
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
     * 秤的交互指令
     * Interactive commands for scales
     */
    public final static int SCALE_SPECIFIC_INTERACTION = 0x2B;
    /**
     * 更新用户回复
     * Update user response
     */
    public final static int UPDATE_USER_CALLBACK = 0x04;
    /**
     * 请求同步历史记录
     * Request synchronization history
     */
    public final static int RE_SYN_HISTORY_CALLBACK = 0x05;
    /**
     * mcu历史记录 mcu history
     */
    public final static int MCU_HISTORY_RECORD = 0x06;
    /**
     * app历史记录 App history
     */
    public final static int APP_HISTORY_RECORD = 0x07;
    public final static int ERROR_CODE = 0xff;

    public final static int WIFI_BLE_TYPE = 0x11;


    /**
     * 请求同步时间
     * Request synchronization time
     */
    public final static int REQUEST_SYN_TIME = 0x38;
    /**
     * 请求同步时间结果
     * Request synchronization time result
     */
    public final static int REQUEST_SYNTIME_RESULT = 0x37;


    /**
     * 千克
     */
    public final static int KG = 0;
    /**
     * 斤
     */
    public final static int JIN = 1;
    /**
     * st:lb
     */
    public final static int ST = 4;
    /**
     * lb
     */
    public final static int LB = 6;
    /**
     * 普通模式 Normal mode
     */
    public final static int MODE_ORDINARY = 0;
    /**
     * 运动员模式 Athlete mode
     */
    public final static int MODE_ATHLETE = 1;
    /**
     * 孕妇模式 Maternity mode
     */
    public final static int MODE_PREGNANT = 2;

    /**
     * 男 Male
     */
    public final static int SEX_MAN = 1;

    /**
     * 女 Female
     */
    public final static int SEX_FEMAN = 0;


    /**
     * 设置类返回状态
     * 设置wifi的mac，密码 和设置的单位 和设置时间
     * 00 成功 01 失败 02 不支持
     * <p>
     * Set class return status
     * Set wifi mac, password, set unit and set time
     * 00 Success 01 Failure 02 Not supported
     */
    public final static int STATUS_SUCCESS = 0x00;
    public final static int STATUS_FAIL = 0x01;
    public final static int STATUS_NOT_SUPPORT = 0x02;

    private BodyFatDataUtil() {
    }


    private static class InnerClass {
        private static BodyFatDataUtil bodyFatDataUtil = new BodyFatDataUtil();
    }

    public static BodyFatDataUtil getInstance() {
        return InnerClass.bodyFatDataUtil;
    }


    /**
     * 解析体重 Analyze weight
     */
    public float getWeight(byte[] by) {
        float myweight = 0.0f;
        int weight = ((by[1] & 0xFF) << 16) + ((by[2] & 0xFF) << 8) + (by[3] & 0xFF);
        int decimals = (by[4] & 0xf0) >> 4;
        if (decimals == 1) {
            myweight = weight / 10f;
        } else if (decimals == 2) {
            myweight = weight / 100f;
        } else if (decimals == 3) {
            myweight = weight / 1000f;
        } else {
            myweight = weight;
        }
        return myweight;

    }

    /**
     * 解析体重小数位 Parsing decimal places of weight
     */
    public int getWeightPoint(byte[] b) {
        return (b[4] & 0xf0) >> 4;

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
    public float getTemperature(byte[] b) {
        float mytemper = 0.0f;
        int temper = (b[1] & 0xff) << 8 + b[2] & 0xff;

        if (((b[1] & 0x80) >> 4) == 15) {
            mytemper = 0 - temper;
        } else {
            mytemper = temper;
        }
        return mytemper / 10;
    }

    /**
     * 解析阻抗值 Analyze impedance value
     */

    public int getImpedance(byte[] b) {
        return ((b[1] & 0xff) << 8) + (b[2] & 0xff);
    }

    /**
     * 解析算法位 Parsing algorithm bit
     */
    public int getArithmetic(byte[] b) {
        if (b.length > 3)
            return (b[3] & 0xff);
        else
            return 0;
    }

    /**
     * 解析心率 Analyze heart rate
     */
    public int getHeart(byte[] b) {
        return b[1] & 0xff;
    }

    /**
     * 解析体脂数据 Analyze body fat data
     */
    public BodyFatRecord getBodyFat(byte[] hex, BodyFatRecord bodyFatRecord) {

        if ((hex[1] & 0xff) == 1) {
            bodyFatRecord.setBfr((float) ((hex[2] & 0xff) << 8 | (hex[3] & 0xff)) * 0.1f);
            bodyFatRecord.setSfr((float) ((hex[4] & 0xff) << 8 | (hex[5] & 0xff)) * 0.1f);
            bodyFatRecord.setUvi((float) ((hex[6] & 0xff) << 8 | (hex[7] & 0xff)));
            bodyFatRecord.setRom((float) ((hex[8] & 0xff) << 8 | (hex[9] & 0xff)) * 0.1f);
            bodyFatRecord.setBmr((float) ((hex[10] & 0xff) << 8 | (hex[11] & 0xff)));
            bodyFatRecord.setBodyAge((float) (hex[12] & 0xff));
        } else {
            bodyFatRecord.setBm(((hex[2] & 0xff) << 8 | (hex[3] & 0xff)) * 0.1f);
            bodyFatRecord.setVwc((float) ((hex[4] & 0xff) << 8 | (hex[5] & 0xff)) * 0.1f);
            bodyFatRecord.setPp((float) ((hex[6] & 0xff) << 8 | (hex[7] & 0xff)) * 0.1f);
            bodyFatRecord.setHeartRate(hex[8] & 0xff);

        }
        return bodyFatRecord;
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
     * 请求用户数据回调   Request user data callback
     *
     * @return 1：请求用户 3：下发用户失败(1: Request user 3: Fail to deliver user)
     */
    public int requestUserDataCallback(byte[] b) {
        return b[1] & 0xff;
    }


    /**
     * 更新当前用户或列表返回 Update current user or list back
     *
     * @param b
     * @return 00：更新列表成功 01：更新个人用户成功 02：更新列表失败 03：更新个人用户失败
     * (00: update the list successfully 01: update the individual user successfully
     * 02: update the list failure 03: update the individual user)
     */
    public int updateUserCallback(byte[] b) {

        return b[2] & 0xff;
    }

    /**
     * 解析muc历史数据  Parsing muc historical data
     */
    public McuHistoryRecordBean getMcuHistoryRecord(byte[] b,
                                                    McuHistoryRecordBean mcuHistoryRecordBean) {
        if (b[2] == 0x01) {
            int year = (b[3] & 0xff) + 2000;

            int month = b[4] & 0xff;
            int day = b[5] & 0xff;
            int hour = b[6] & 0xff;
            int min = b[7] & 0xff;
            int sec = b[8] & 0xff;
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(year);
//            stringBuffer.append(":");
//            stringBuffer.append(month);
//            stringBuffer.append(":");
//            stringBuffer.append(day);
//            stringBuffer.append(" ");
//            stringBuffer.append(hour);
//            stringBuffer.append(":");
//            stringBuffer.append(min);
//            stringBuffer.append(":");
//            stringBuffer.append(sec);
            int mode = (b[9] & 0xf0) >> 4;
            int bodyId = (b[9] & 0x0f);
            int sex = (b[10] & 0xff) >> 7;
            int age = (b[10] & 0x7f);
            int height = b[11] & 0xff;
//            int weight = (b[12] & 0xff) << 16 + (b[13] & 0xff) << 8 + b[14] & 0xff;
            int weight_hight = (b[12] & 0xff) << 16;
            int weight_mid = (b[13] & 0xff) << 8;
            int weight_low = b[14] & 0xff;
            int weight = weight_hight + weight_mid + weight_low;
            int decimals = (b[15] & 0xf0) >> 4;
            int unit = (b[15] & 0x0f);

            mcuHistoryRecordBean
                    .setUser(year, month, day, hour, min, sec, sex, mode, bodyId, age, height,
                            weight, unit, decimals);

        } else if (b[2] == 0x02) {
            int adc = ((b[3] & 0xff) << 8) | (b[4] & 0xff);
            float bfr = ((b[5] & 0xff) << 8 | (b[6] & 0xff)) * 0.1f;
            float sfr = (((b[7] & 0xff) << 8) | (b[8] & 0xff)) * 0.1f;
            float uvi = (((b[9] & 0xff) << 8) | (b[10] & 0xff));
            float rom = (((b[11] & 0xff) << 8) | (b[12] & 0xff)) * 0.1f;
            float bmr = ((b[13] & 0xff) << 8) | (b[14] & 0xff);
            float bodyAge = (b[15] & 0xff);
            mcuHistoryRecordBean.setBodyFatRecord_1(adc, bfr, sfr, uvi, rom, bmr, bodyAge);

        } else {
            float bm = (((b[3] & 0xff) << 8) | (b[4] & 0xff)) * 0.1f;
            float vwc = ((b[5] & 0xff) << 8 | (b[6] & 0xff)) * 0.1f;
            float pp = (((b[7] & 0xff) << 8) | (b[8] & 0xff)) * 0.1f;
            int heart = 0;
            if (b.length >= 10)
                heart = (b[9] & 0xff);   //以前的设备没有心率，在这里进行长度判断，否则会出现数组越界的错误
            mcuHistoryRecordBean.setBodyFatRecord_2(bm, vwc, pp, heart);
        }
        return mcuHistoryRecordBean;
    }

    /**
     * 解析app历史数据
     * Parse app historical data
     */
    public AppHistoryRecordBean getAppHistoryRecord(byte[] b,
                                                    AppHistoryRecordBean appHistoryRecordBean) {
        if (b[2] == 0x01) {
            int year = (b[3] & 0xff) + 2000; //
            int month = b[4] & 0xff;
            int day = b[5] & 0xff;
            int hour = b[6] & 0xff;
            int min = b[7] & 0xff;
            int sec = b[8] & 0xff;
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(year);
//            stringBuffer.append(":");
//            stringBuffer.append(month);
//            stringBuffer.append(":");
//            stringBuffer.append(day);
//            stringBuffer.append(" ");
//            stringBuffer.append(hour);
//            stringBuffer.append(":");
//            stringBuffer.append(min);
//            stringBuffer.append(":");
//            stringBuffer.append(sec);
            int mode = (b[9] & 0xf0) >> 4;
            int bodyId = (b[9] & 0x0f);
            int sex = (b[10] & 0xff) >> 7;
            int age = (b[10] & 0x7f);
            int height = b[11] & 0xff;
//            LogUtil.e( ((b[12] & 0xff) << 16 + (b[13] & 0xff) << 8 + b[14] & 0xff)+" "+((b[12]
//            & 0xff) << 16)+"  "+((b[13] & 0xff) << 8)+" "+( b[14] & 0xff));
            int weightHigh = (b[12] & 0xff) << 16;
            int weightMid = (b[13] & 0xff) << 8;
            int weightLow = b[14] & 0xff;
            int weight = weightHigh + weightMid + weightLow;
            int decimals = (b[15] & 0xf0) >> 4;
            int unit = (b[15] & 0x0f);
            appHistoryRecordBean
                    .setUser(year, month, day, hour, min, sec, sex, mode, bodyId, age, height,
                            weight, unit, decimals);

        } else {
            int adc = ((b[3] & 0xff) << 8) | (b[4] & 0xff);
            int arithmetic = 0;
            int heart = 0;
            if (b.length >= 7) {
                arithmetic = b[5] & 0xff;
                heart = b[6] & 0xff;
            }
            appHistoryRecordBean.setBodyFatRecord(adc, arithmetic, heart);
        }
        return appHistoryRecordBean;

    }

    /**
     * 请求同步历史记录返回
     * Request synchronization history return
     *
     * @return 00：无历史记录 01：开始发送历史记录 02：结束发送历史记录
     * (00: No history 01: Start sending history 02: End sending history)
     */
    public int requestSynHistoryCallback(byte[] b) {
        return b[2] & 0xff;
    }

    /**
     * 设置单位 Set unit
     * 主动调用 Active call
     *
     * @param type       单位类型 unit type
     * @param deviceType 设置类型 蓝牙体脂秤 0x0e  wifi+ble体脂秤 0x11
     *                   ( deviceType Setting type Bluetooth body fat scale 0x0e wifi + ble body fat scale 0x11)
     */
    public SendMcuBean setWeightUnit(int type, int deviceType) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x81;
        bytes[1] = (byte) type;
        smb.setHex(deviceType, bytes);
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
     * @param user       用户 user
     * @param deviceType 设置类型 蓝牙体脂秤 0x0e  wifi+ble体脂秤 0x11
     *                   deviceType Setting type Bluetooth body fat scale 0x0e wifi + ble body fat scale 0x11
     */
    public SendMcuBean setUserInfo(User user, int deviceType) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[5];
        bytes[0] = (byte) 0x08;
        bytes[1] = 0x02;
        int mode = user.getModeType();
        int id = user.getId();
        bytes[2] = (byte) (((mode & 0xff) << 4) + (id & 0xff));//高4位是模式，低四位是编号
        int sex = user.getSex();
        int age = user.getAge();
        bytes[3] = (byte) ((((sex & 0xff)) << 7) + (age & 0x7f));     //最高位是性别，低7位年龄
        int height = user.getHeight();

        bytes[4] = (byte) (height & 0xff);     //身高 cm
        sendMcuBean.setHex(deviceType, bytes);
//        DeviceConfig.WEIGHT_BODY_FAT_SCALE
        return sendMcuBean;
    }

    /**
     * 发送测试状态 Send test status
     *
     * @param status 设置秤的状态 Set the status of the scale
     */
    public SendMcuBean sendTestStatus(int status) {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0xFE;
        bytes[1] = (byte) status;
        smb.setHex(WIFI_BLE_TYPE, bytes);
//        DeviceConfig.WEIGHT_BODY_FAT_SCALE
        return smb;
    }

    /**
     * 更新用户列表
     * 主动下发 连接上设备后下发
     * Update user list
     * Actively distributed after connecting to the device
     */
    public SendBleBean setUserInfoList(User user) {

        byte[] bytes = new byte[16];
        bytes[0] = 0x2b;
        bytes[1] = 0x01;
        int mode = user.getModeType();

        int id = user.getId();
        bytes[2] = (byte) (((mode & 0xff) << 4) + (id & 0xff));//高4位是模式，低四位是编号
        int sex = user.getSex();
        int age = user.getAge();
        bytes[3] = (byte) ((((sex & 0xff)) << 7) + (age & 0x7f));     //最高位是性别，低7位年龄
        int height = user.getHeight();

        bytes[4] = (byte) (height & 0xff);     //身高 cm
        int myweight = (int) (user.getWeight() * Math.pow(10, 1));

        bytes[5] = (byte) ((myweight & 0xff00) >> 8);
        bytes[6] = (byte) (myweight & 0xff);
        bytes[7] = (byte) ((user.getAdc() & 0xff00) >> 8);
        bytes[8] = (byte) (user.getAdc() & 0xff);
        bytes[9] = 0x00;
        bytes[10] = 0x00;
        bytes[11] = 0x00;
        bytes[12] = 0x00;
        bytes[13] = 0x00;
        bytes[14] = 0x00;
        bytes[15] = 0x00;
//        LogUtil.e(BleStrUtils.byte2HexStr(bytes));
        return getSendBleBeam(bytes);


    }

    /**
     * 更新用户列表完成
     * 主动下发，在更新完用户列表后执行。只需执行一次
     * Update user list is complete
     * Actively issued and executed after updating the user list. Only need to execute once
     */
    public SendBleBean updateUsersComplete() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x2b;
        bytes[1] = 0x02;

        return getSendBleBeam(bytes);
    }

    /**
     * 更新当前用户信息
     * 主动下发，测量完成后可以下发
     * Update current user information
     * Active delivery, can be delivered after the measurement is completed
     */
    public SendBleBean updataPresentUser(User users) {
        byte[] bytes = new byte[9];
        bytes[0] = 0x2B;
        bytes[1] = 0x03;
        int mode = users.getModeType();

        int id = users.getId();
        bytes[2] = (byte) (((mode & 0xff) << 4) + (id & 0xff));//高4位是模式，低四位是编号
        int sex = users.getSex();
        int age = users.getAge();
        bytes[3] = (byte) ((((sex & 0xff)) << 7) + (age & 0x7f));     //最高位是性别，低7位年龄
        int height = users.getHeight();

        bytes[4] = (byte) (height & 0xff);     //身高 cm
        int myweight = (int) (users.getWeight() * 10);
        bytes[5] = (byte) ((myweight & 0xff00) >> 8);
        bytes[6] = (byte) (myweight & 0xff);
        bytes[7] = (byte) ((users.getAdc() & 0xff00) >> 8);
        bytes[8] = (byte) (users.getAdc() & 0x00ff);
        return getSendBleBeam(bytes);
    }


    /**
     * 请求同步历史记录
     * Request synchronization history
     */
    public SendBleBean requestSynHistory() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x2b;
        bytes[1] = RE_SYN_HISTORY_CALLBACK;
        return getSendBleBeam(bytes);

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

    //-------------------关于wifi秤的部分内容---------------------------------------------

    /**
     * 有格式要求(There are format requirements)
     * AA:BB:CC:DD:EE:FF
     */
    public SendBleBean setWifiMac(String mac) {
        byte[] bytes = BleSendCmdUtil.getInstance().setWifiMac(mac);
        return getSendBleBeam(bytes);

    }


    /**
     * wifi密码一次只能传14个byte
     * 如果密码长度超过14个byte 就需要分包传送
     * wifi password can only transfer 14 bytes at a time
     * If the password is longer than 14 bytes, it needs to be sent in packets
     *
     * @param subpackage 为0 时，表示后面还有数据 ,为1 时，表示数据小于或等于14个byte,后面没有数据
     *                   is 0, it means there is data behind, when it is 1, it means that the data
     *                   is less than or equal to 14 bytes, and there is no data behind
     * @param password   密码数组 password array
     */
    public SendBleBean setWifiPwd(int subpackage, byte[] password) {
        byte[] bytes1 = BleSendCmdUtil.getInstance().setWifiPwd(subpackage, password);
        return getSendBleBeam(bytes1);
    }

    /**
     * 获取已设置的wifi的Mac
     * Get Mac with wifi set
     *
     * @return SendBleBean
     */
    public SendBleBean getSelectWifiMac() {
        byte[] bytes = BleSendCmdUtil.getInstance().getSelectWifiMac();
        return getSendBleBeam(bytes);
    }

    /**
     * 获取当前连接的wifi的名字
     * Get the name of the currently connected wifi
     *
     * @return SendBleBean
     */
    public SendBleBean getConnectWifiName() {
        byte[] bytes = BleSendCmdUtil.getInstance().getConnectWifiName();
        return getSendBleBeam(bytes);
    }

    /**
     * 获取连接的wifi的密码
     * Get the password of the connect wifi
     *
     * @return SendBleBean
     */
    public SendBleBean getConnectWifiPwd() {
        byte[] bytes = BleSendCmdUtil.getInstance().getConnectWifiPwd();
        return getSendBleBeam(bytes);
    }

    /**
     * 发起连接
     * Initiate the connection
     *
     * @return SendBleBean
     */
    public SendBleBean connectWifi() {

        byte[] bytes = BleSendCmdUtil.getInstance().setConnectWifi();
        return getSendBleBeam(bytes);
    }

    /**
     * 断开连接
     * Disconnect
     *
     * @return SendBleBean
     */
    public SendBleBean disconnectWifi() {
        byte[] bytes = BleSendCmdUtil.getInstance().setDisconnectWifi();
        return getSendBleBeam(bytes);
    }


    public SendBleBean checkIp() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x8c;
        return getSendBleBeam(bytes);
    }

    public SendBleBean checkPort() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x8e;
        return getSendBleBeam(bytes);
    }

    public SendBleBean checkUrl() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x97;
        return getSendBleBeam(bytes);
    }

    /**
     * 获取到设备的did
     * Get the did of the device
     *
     * @return SendBleBean
     */
    public SendBleBean getSnDeviceDid() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x93;
        return getSendBleBeam(bytes);
    }

    /**
     * 扫描wifi Scan wifi
     *
     * @return SendBleBean
     */
    public SendBleBean scanWifi() {
        byte[] bytes = BleSendCmdUtil.getInstance().setScanWifi();
        return getSendBleBeam(bytes);
    }

    /**
     * 查询模块的蓝牙，wifi和模块状态
     * Query the module's Bluetooth, wifi and module status
     *
     * @return SendBleBean
     */
    public SendBleBean queryBleStatus() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x26;
        return getSendBleBeam(bytes);

    }

    /**
     * 同步BLE时间
     * Synchronize system time
     *
     * @return SendBleBean
     */
    public SendBleBean synSysTime() {
        byte[] currentTime = BleDataUtils.getInstance().getCurrentTime();
        return getSendBleBeam(BleSendCmdUtil.getInstance().setSysTime(currentTime, true));
    }

    /**
     * 同步MCU时间
     * Synchronize time
     *
     * @return SendBleBean
     */
    public SendBleBean synTime() {
        byte[] currentTime = BleDataUtils.getInstance().getCurrentTime();
        byte[] bytes = BleSendCmdUtil.getInstance().setDeviceTime(currentTime);
        return getSendBleBeam(bytes);
    }

    public SendBleBean ota() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x91;
        bytes[1] = 0x01;
        return getSendBleBeam(bytes);

    }

    public SendBleBean reset() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x22;
        bytes[1] = 0x01;
        return getSendBleBeam(bytes);

    }

//    http://test.ailink.revice.aicare.net.cn
//    0x68 0x74 0x74 0x70 0x3a 0x2f 0x2f 0x74 0x65 0x73 0x74 0x2e 0x61 0x69 0x6c 0x69 0x6e 0x6b 0x2e 0x72 0x65 0x76 0x69 0x63 0x65 0x2e 0x61 6x69 0x63 0x61 0x72 0x65 0x2e 0x6e 0x65 0x74 0x2e 0x63 0x6e

    public SendBleBean environmentIp(int subpackage, byte[] bytesIp) {
        byte[] bytes1;
        if (bytesIp != null) {
            bytes1 = new byte[bytesIp.length + 2];
            bytes1[0] = (byte) 0x8b;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(bytesIp, 0, bytes1, 2, bytesIp.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x8b;
        }

        return getSendBleBeam(bytes1);

    }

    public SendBleBean environmentPort(int port) {
        byte[] bytes1;
        bytes1 = new byte[3];
        bytes1[0] = (byte) 0x8d;
        bytes1[1] = (byte) (port >> 8);
        bytes1[2] = (byte) (port & 0xff);
        return getSendBleBeam(bytes1);
    }


    public SendBleBean environmentUrl(int subpackage, byte[] bytesIpUrl) {
        byte[] bytes1;
        if (bytesIpUrl != null) {
            bytes1 = new byte[bytesIpUrl.length + 2];
            bytes1[0] = (byte) 0x96;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(bytesIpUrl, 0, bytes1, 2, bytesIpUrl.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x96;
        }

        return getSendBleBeam(bytes1);

    }

}
