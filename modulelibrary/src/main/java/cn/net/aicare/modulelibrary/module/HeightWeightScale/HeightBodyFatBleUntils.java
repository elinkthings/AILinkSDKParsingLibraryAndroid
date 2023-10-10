package cn.net.aicare.modulelibrary.module.HeightWeightScale;

import com.pingwang.bluetoothlib.device.SendMcuBean;

public class HeightBodyFatBleUntils {

    public final static int DEVICE_TYPE =0x0026;
    public final static int MODE_HEiGHT_BODY_FAT=0x01;
    public final static int MODE_BABY=0x02;
    public final static int MODE_WEIGHT=0x03;
    public final static int VOICE_OPEN=0x01;
    public final static int VOICE_CLOSE=0x02;
    public final static int SET_SUCCESS=0x00;
    public final static int SET_FAIL=0x01;
    public final static int SET_SUPPORT=0x02;




    /**
     * 下发用户
     */
    public final static int SET_USER=0x01;
    /**
     * 下发用户结果
     */
    public final static int MCU_SET_USER_RESULT=0x02;

    /**
     * 请求下发用户
     */
    public final static int MCU_REQUEST_USER=0x03;
    /**
     * 设置单位
     */
    public final static int SET_UNIT=0x04;
    /**
     * 设置单位结果
     */
    public final static int MCU_SET_UNIT_RESULT=0x05;

    /**
     * 设置工作模式
     */
    public final static int SET_WORK_MODE=0x06;


    /**
     * 设置工作模式结果
     */
    public final static int MCU_SET_WORK_MODE_RESULT=0x07;

    /**
     * 请求声音设置
     */
    public final static int REQUEST_VOICE_SET=0x08;

    /**
     * 设置声音
     */
    public final static int SET_VOICE_SET=0x09;

    /**
     * 请求声音设置结果
     */

    public final static int MUC_REQUEST_VOICE_SET_RESULT=0x0a;


    /**
     * APP 获取工作状态
     */
    public final static int GET_WORK_STATUS=0x0b;


    /**
     * Mcu回复工作状态
     */
    public final static int MCU_WORK_STATUS_RESULT=0x0C;
    /**
     * 单片机体重身体脂肪结果
     * MCU 上报身高称重状态
     */
    public final static int MCU_WEIGHT_STATUS_BODY_FAT_RESULT=0x10;
    public final static int MCU_WEIGHT_STATUS_BABY_RESULT=0x20;
    public final static int MCU_WEIGHT_STATUS_WEIGHT_RESULT=0x30;
    public final static int MCU_WEIGHT_STATUS_WEIGHT_HEIGHT_RESULT=0x40;
    public final static int MCU_WEIGHT_STATUS_HEIGHT=0x41;

    /**
     * 实时体重数据
     */
    public final static int MCU_WEIGHT_STATUS_RESULT_REAL=0x01;
    /**
     * 稳定体重数据
     */
    public final static int MCU_WEIGHT_STATUS_RESULT_STABILIZE=0x02;

    /**
     * MCU 上报阻抗数据
     */
    public final static int MCU_ADC_RESULT=0x11;
//01：测阻抗中 02：测阻抗失败 03：测阻抗成功，带上阻抗数据，并使用 APP 算法(APP 会根 据 byte11 的算法标识进行计算)
// 04：测阻抗成功，带上阻抗数据，并使用 MCU 算法。 05：测阻抗结束
    public final static int ADC_TEST_ING=0x01;
    public final static int ADC_TEST_FAIL=0x02;
    public final static int ADC_TEST_SUCCEED_APP=0x03;
    public final static int ADC_TEST_SUCCEED_MCU=0x04;
    public final static int ADC_TEST_FINISH=0x05;

    /**
     * MCU 上报心率数据
     */
    public final static int MCU_HEART=0x12;

    /**
     * MCU 发送温度数据
     */
    public final static int MCU_TEMP=0x13;

    /**
     * MCU 身高数据
     */
    public final static int MCU_HEIGHT=0x14;
    /**
     * 体脂数据
     */
    public final static int MCU_BODY_FAT=0x15;
    /**
     * MCU发送测量完成
     */
    public final static int MCU_TEST_FINISH=0x80;
    /**
     * APP回复测量完成
     */
    public final static int REPLY_TEST_FINISH=0x81;
    /**
     * 错误码
     */
    public final static int ERROR=0xff;


    /**
     * APP回复测量完成
     */
    public static SendMcuBean replyTestFinish(){
        byte[] bytes=new byte[2];
        bytes[0]= (byte) REPLY_TEST_FINISH;
        bytes[1]=0x00;
        return getSendMcuBean(bytes);
    }

    /**
     * 获取到设备状态
     * @return
     */
    public static SendMcuBean getDeviceStatus(){

        byte[] bytes=new byte[3];
        bytes[0]= (byte) GET_WORK_STATUS;
        bytes[1]=0x01;
        bytes[2]=0x00;
        return getSendMcuBean(bytes);
    }

    /**
     * 获取到声音状态
     * @return
     */
    public static SendMcuBean getVoiceStatus(){

        byte[] bytes=new byte[3];
        bytes[0]= (byte) REQUEST_VOICE_SET;
        bytes[1]=0x01;
        bytes[2]=0x00;
        return getSendMcuBean(bytes);
    }

    /**
     * 获取到声音状态
     * @return
     */
    public static SendMcuBean setVoiceStatus(int status){
        byte[] bytes=new byte[3];
        bytes[0]= (byte) SET_VOICE_SET;
        bytes[1]= (byte) status;
        bytes[2]=0x00;
        return getSendMcuBean(bytes);
    }


    /**
     * 下发工作模式
     * @return
     */
    public static SendMcuBean setWorkMode(int mode){
        byte[] bytes=new byte[3];
        bytes[0]= (byte) SET_WORK_MODE;
        bytes[1]= (byte) mode;
        bytes[2]=0x00;
        return getSendMcuBean(bytes);
    }

    /**
     * 设置单位
     * @param hUnit
     * @param wUnit
     * @return
     */
    public static SendMcuBean setUnit(int hUnit,int wUnit){
        byte[] bytes=new byte[4];
        bytes[0]= (byte) SET_UNIT;
        bytes[1]= (byte) hUnit;
        bytes[2]= (byte) wUnit;
        bytes[3]=0x00;
        return getSendMcuBean(bytes);
    }

    /**
     * 下发用户消息
     * @param sex
     * @param age
     * @param height
     * @return
     */
    public static SendMcuBean setUser(int sex,int age,int height){
        byte[] bytes=new byte[5];
        bytes[0]= (byte) SET_USER;
        bytes[1]= (byte) sex;
        bytes[2]= (byte) age;
        bytes[3]= (byte) height;
        bytes[4]=0x00;
        return getSendMcuBean(bytes);
    }

    private static SendMcuBean getSendMcuBean(byte[] bytes){
        SendMcuBean sendMcuBean=new SendMcuBean();
        sendMcuBean.setHex(DEVICE_TYPE, bytes);
        return sendMcuBean;
    }



}
