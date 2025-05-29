package cn.net.aicare.modulelibrary.module.BodyFatScale;


import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleDataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 体脂秤指令
 */
public class HmiBodyFatDataUtil {


    public final static int CID = 0x0076;

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
    public final static int ERROR_CODE = 0xFF;

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

    private HmiBodyFatDataUtil() {
    }


    private static class InnerClass {
        private static HmiBodyFatDataUtil hmiBodyFatDataUtil = new HmiBodyFatDataUtil();
    }

    public static HmiBodyFatDataUtil getInstance() {
        return InnerClass.hmiBodyFatDataUtil;
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
        int temper = (b[1] & 0xFF) << 8 + b[2] & 0xFF;

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
        return ((b[1] & 0xFF) << 8) + (b[2] & 0xFF);
    }

    /**
     * 解析算法位 Parsing algorithm bit
     */
    public int getArithmetic(byte[] b) {
        if (b.length > 3)
            return (b[3] & 0xFF);
        else
            return 0;
    }

    /**
     * 解析心率 Analyze heart rate
     */
    public int getHeart(byte[] b) {
        return b[1] & 0xFF;
    }

    /**
     * 解析体脂数据 Analyze body fat data
     */
    public HmiBodyFatRecordBean getBodyFat(byte[] hex, HmiBodyFatRecordBean hmiBodyFatRecordBean) {

        if ((hex[1] & 0xFF) == 1) {
            hmiBodyFatRecordBean.setBfr((float) ((hex[2] & 0xFF) << 8 | (hex[3] & 0xFF)) * 0.1f);
            hmiBodyFatRecordBean.setSfr((float) ((hex[4] & 0xFF) << 8 | (hex[5] & 0xFF)) * 0.1f);
            hmiBodyFatRecordBean.setUvi((float) ((hex[6] & 0xFF) << 8 | (hex[7] & 0xFF)));
            hmiBodyFatRecordBean.setRom((float) ((hex[8] & 0xFF) << 8 | (hex[9] & 0xFF)) * 0.1f);
            hmiBodyFatRecordBean.setBmr((float) ((hex[10] & 0xFF) << 8 | (hex[11] & 0xFF)));
            hmiBodyFatRecordBean.setBodyAge((float) (hex[12] & 0xFF));
        } else {
            hmiBodyFatRecordBean.setBm(((hex[2] & 0xFF) << 8 | (hex[3] & 0xFF)) * 0.1f);
            hmiBodyFatRecordBean.setVwc((float) ((hex[4] & 0xFF) << 8 | (hex[5] & 0xFF)) * 0.1f);
            hmiBodyFatRecordBean.setPp((float) ((hex[6] & 0xFF) << 8 | (hex[7] & 0xFF)) * 0.1f);
            hmiBodyFatRecordBean.setHeartRate(hex[8] & 0xFF);

        }
        return hmiBodyFatRecordBean;
    }

    /**
     * 设置单位返回 Set unit return
     *
     * @return 0：设置成功 1：设置失败 2：不支持设置(set up successfully 1: set up failed 2: set up is not supported)
     */
    public int setUnitCallback(byte[] b) {

        return b[1] & 0xFF;

    }

    /**
     * 请求用户数据回调   Request user data callback
     *
     * @return 1：请求用户 3：下发用户失败(1: Request user 3: Fail to deliver user)
     */
    public int requestUserDataCallback(byte[] b) {
        return b[1] & 0xFF;
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

        return b[2] & 0xFF;
    }

    /**
     * 解析muc历史数据  Parsing muc historical data
     */
    public HmiMcuHistoryRecordBean getMcuHistoryRecord(byte[] b,
                                                       HmiMcuHistoryRecordBean hmiMcuHistoryRecordBean) {
        if (b[2] == 0x01) {
            int year = (b[3] & 0xFF) + 2000;

            int month = b[4] & 0xFF;
            int day = b[5] & 0xFF;
            int hour = b[6] & 0xFF;
            int min = b[7] & 0xFF;
            int sec = b[8] & 0xFF;
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
            int sex = (b[10] & 0xFF) >> 7;
            int age = (b[10] & 0x7f);
            int height = b[11] & 0xFF;
//            int weight = (b[12] & 0xFF) << 16 + (b[13] & 0xFF) << 8 + b[14] & 0xFF;
            int weight_hight = (b[12] & 0xFF) << 16;
            int weight_mid = (b[13] & 0xFF) << 8;
            int weight_low = b[14] & 0xFF;
            int weight = weight_hight + weight_mid + weight_low;
            int decimals = (b[15] & 0xf0) >> 4;
            int unit = (b[15] & 0x0f);

            hmiMcuHistoryRecordBean
                    .setUser(year, month, day, hour, min, sec, sex, mode, bodyId, age, height,
                            weight, unit, decimals);

        } else if (b[2] == 0x02) {
            int adc = ((b[3] & 0xFF) << 8) | (b[4] & 0xFF);
            float bfr = ((b[5] & 0xFF) << 8 | (b[6] & 0xFF)) * 0.1f;
            float sfr = (((b[7] & 0xFF) << 8) | (b[8] & 0xFF)) * 0.1f;
            float uvi = (((b[9] & 0xFF) << 8) | (b[10] & 0xFF));
            float rom = (((b[11] & 0xFF) << 8) | (b[12] & 0xFF)) * 0.1f;
            float bmr = ((b[13] & 0xFF) << 8) | (b[14] & 0xFF);
            float bodyAge = (b[15] & 0xFF);
            hmiMcuHistoryRecordBean.setBodyFatRecord_1(adc, bfr, sfr, uvi, rom, bmr, bodyAge);

        } else {
            float bm = (((b[3] & 0xFF) << 8) | (b[4] & 0xFF)) * 0.1f;
            float vwc = ((b[5] & 0xFF) << 8 | (b[6] & 0xFF)) * 0.1f;
            float pp = (((b[7] & 0xFF) << 8) | (b[8] & 0xFF)) * 0.1f;
            int heart = 0;
            if (b.length >= 10)
                heart = (b[9] & 0xFF);   //以前的设备没有心率，在这里进行长度判断，否则会出现数组越界的错误
            hmiMcuHistoryRecordBean.setBodyFatRecord_2(bm, vwc, pp, heart);
        }
        return hmiMcuHistoryRecordBean;
    }

    /**
     * 解析app历史数据
     * Parse app historical data
     */
    public HmiAppHistoryRecordBean getAppHistoryRecord(byte[] b,
                                                       HmiAppHistoryRecordBean hmiAppHistoryRecordBean) {
        if (b[2] == 0x01) {
            int year = (b[3] & 0xFF) + 2000; //
            int month = b[4] & 0xFF;
            int day = b[5] & 0xFF;
            int hour = b[6] & 0xFF;
            int min = b[7] & 0xFF;
            int sec = b[8] & 0xFF;
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
            int sex = (b[10] & 0xFF) >> 7;
            int age = (b[10] & 0x7f);
            int height = b[11] & 0xFF;
//            LogUtil.e( ((b[12] & 0xFF) << 16 + (b[13] & 0xFF) << 8 + b[14] & 0xFF)+" "+((b[12]
//            & 0xFF) << 16)+"  "+((b[13] & 0xFF) << 8)+" "+( b[14] & 0xFF));
            int weightHigh = (b[12] & 0xFF) << 16;
            int weightMid = (b[13] & 0xFF) << 8;
            int weightLow = b[14] & 0xFF;
            int weight = weightHigh + weightMid + weightLow;
            int decimals = (b[15] & 0xf0) >> 4;
            int unit = (b[15] & 0x0f);
            hmiAppHistoryRecordBean
                    .setUser(year, month, day, hour, min, sec, sex, mode, bodyId, age, height,
                            weight, unit, decimals);

        } else {
            int adc = ((b[3] & 0xFF) << 8) | (b[4] & 0xFF);
            int arithmetic = 0;
            int heart = 0;
            if (b.length >= 7) {
                arithmetic = b[5] & 0xFF;
                heart = b[6] & 0xFF;
            }
            hmiAppHistoryRecordBean.setBodyFatRecord(adc, arithmetic, heart);
        }
        return hmiAppHistoryRecordBean;

    }

    /**
     * 请求同步历史记录返回
     * Request synchronization history return
     *
     * @return 00：无历史记录 01：开始发送历史记录 02：结束发送历史记录
     * (00: No history 01: Start sending history 02: End sending history)
     */
    public int requestSynHistoryCallback(byte[] b) {
        return b[2] & 0xFF;
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
        smb.setHex(CID, bytes);
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
     * @param hmiBodyFatUserBean 用户 hmiBodyFatUserBean
     */
    public SendMcuBean setUserInfo(HmiBodyFatUserBean hmiBodyFatUserBean) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[5];
        bytes[0] = (byte) 0x08;
        bytes[1] = 0x02;
        int mode = hmiBodyFatUserBean.getModeType();
        int id = hmiBodyFatUserBean.getId();
        bytes[2] = (byte) (((mode & 0xFF) << 4) + (id & 0xFF));//高4位是模式，低四位是编号
        int sex = hmiBodyFatUserBean.getSex();
        int age = hmiBodyFatUserBean.getAge();
        bytes[3] = (byte) ((((sex & 0xFF)) << 7) + (age & 0x7f));     //最高位是性别，低7位年龄
        int height = hmiBodyFatUserBean.getHeight();

        bytes[4] = (byte) (height & 0xFF);     //身高 cm
        sendMcuBean.setHex(CID, bytes);
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
        smb.setHex(CID, bytes);
//        DeviceConfig.WEIGHT_BODY_FAT_SCALE
        return smb;
    }

    /**
     * 更新用户列表
     * 主动下发 连接上设备后下发
     * Update hmiBodyFatUserBean list
     * Actively distributed after connecting to the device
     */
    public List<SendBleBean> setUserInfoList(List<HmiBodyFatUserBean> list) {
        List<SendBleBean> sendBleBeans = new ArrayList<>();

        for (int i = 0; i < list.size(); i += 2) {
            HmiBodyFatUserBean userBean1 = list.get(i);
            HmiBodyFatUserBean userBean2 = list.size() > i + 1 ? list.get(i + 1) : null;
            byte[] bytes = new byte[16];
            bytes[0] = 0x2b;
            bytes[1] = 0x01;
            bytes[2] = (byte) (((userBean1.getModeType() & 0xFF) << 4) + (userBean1.getId() & 0xFF));//高4位是模式，低四位是编号
            bytes[3] = (byte) ((((userBean1.getSex() & 0xFF)) << 7) + (userBean1.getAge() & 0x7f));     //最高位是性别，低7位年龄
            bytes[4] = (byte) (userBean1.getHeight() & 0xFF);     //身高 cm
            int weight1 = (int) (userBean1.getWeight() * Math.pow(10, 1));
            bytes[5] = (byte) ((weight1 & 0xff00) >> 8);
            bytes[6] = (byte) (weight1 & 0xFF);
            bytes[7] = (byte) ((userBean1.getAdc() & 0xff00) >> 8);
            bytes[8] = (byte) (userBean1.getAdc() & 0xFF);
            if (userBean2 != null) {
                bytes[9] = (byte) (((userBean2.getModeType() & 0xFF) << 4) + (userBean2.getId() & 0xFF));//高4位是模式，低四位是编号
                bytes[10] = (byte) ((((userBean2.getSex() & 0xFF)) << 7) + (userBean2.getAge() & 0x7f));     //最高位是性别，低7位年龄
                bytes[11] = (byte) (userBean2.getHeight() & 0xFF);     //身高 cm
                int weight2 = (int) (userBean2.getWeight() * Math.pow(10, 1));
                bytes[12] = (byte) ((weight2 & 0xff00) >> 8);
                bytes[13] = (byte) (weight2 & 0xFF);
                bytes[14] = (byte) ((userBean2.getAdc() & 0xff00) >> 8);
                bytes[15] = (byte) (userBean2.getAdc() & 0xFF);
            }else {
                bytes[9] = 0x00;
                bytes[10] = 0x00;
                bytes[11] = 0x00;
                bytes[12] = 0x00;
                bytes[13] = 0x00;
                bytes[14] = 0x00;
                bytes[15] = 0x00;
            }
            SendBleBean sendBleBean = getSendBleBean(bytes);
            sendBleBeans.add(sendBleBean);
        }


        return sendBleBeans;


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

        return getSendBleBean(bytes);
    }

    /**
     * 更新当前用户信息
     * 主动下发，测量完成后可以下发
     * Update current user information
     * Active delivery, can be delivered after the measurement is completed
     */
    public SendBleBean updatePresentUser(HmiBodyFatUserBean users) {
        byte[] bytes = new byte[9];
        bytes[0] = 0x2B;
        bytes[1] = 0x03;
        int mode = users.getModeType();

        int id = users.getId();
        bytes[2] = (byte) (((mode & 0xFF) << 4) + (id & 0xFF));//高4位是模式，低四位是编号
        int sex = users.getSex();
        int age = users.getAge();
        bytes[3] = (byte) ((((sex & 0xFF)) << 7) + (age & 0x7f));     //最高位是性别，低7位年龄
        int height = users.getHeight();

        bytes[4] = (byte) (height & 0xFF);     //身高 cm
        int myweight = (int) (users.getWeight() * 10);
        bytes[5] = (byte) ((myweight & 0xff00) >> 8);
        bytes[6] = (byte) (myweight & 0xFF);
        bytes[7] = (byte) ((users.getAdc() & 0xff00) >> 8);
        bytes[8] = (byte) (users.getAdc() & 0x00ff);
        return getSendBleBean(bytes);
    }


    /**
     * 请求同步历史记录
     * Request synchronization history
     */
    public SendBleBean requestSynHistory() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x2b;
        bytes[1] = RE_SYN_HISTORY_CALLBACK;
        return getSendBleBean(bytes);

    }

    /**
     * 用于与蓝牙模块交互,封装模型
     * Used to interact with Bluetooth module, package model
     */

    private SendBleBean getSendBleBean(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        return sendBleBean;
    }

    //-------------------关于wifi秤的部分内容---------------------------------------------


    /**
     * 同步BLE时间(0x1B)
     * Synchronize system time
     *
     * @return SendBleBean
     */
    public SendBleBean synSysTime() {
        byte[] currentTime = BleDataUtils.getInstance().getCurrentTime();
        return getSendBleBean(BleSendCmdUtil.getInstance().setSysTime(currentTime, true));
    }

    /**
     * 同步MCU时间(0x37)
     * Synchronize time
     *
     * @return SendBleBean
     */
    public SendBleBean synMcuTime() {
        byte[] currentTime = BleDataUtils.getInstance().getCurrentTime();
        byte[] bytes = BleSendCmdUtil.getInstance().setDeviceTime(currentTime);
        return getSendBleBean(bytes);
    }

    public SendBleBean ota() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x91;
        bytes[1] = 0x01;
        return getSendBleBean(bytes);

    }

    public SendBleBean reset() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x22;
        bytes[1] = 0x01;
        return getSendBleBean(bytes);

    }


}
