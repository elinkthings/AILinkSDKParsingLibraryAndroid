package cn.net.aicare.modulelibrary.module.ADWeight;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleSettingListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDataUtils;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.List;

/**
 * xing<br>
 * 2019/7/11<br>
 * 体脂称
 */
public class ADWeightScaleDeviceData extends BaseBleDeviceData {
    private String TAG = ADWeightScaleDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private byte[] CID;
    private int mType = ADWeightScaleBleConfig.WEIGHT_BODY_FAT_SCALE_AD;
    private static BleDevice mBleDevice = null;
    private static ADWeightScaleDeviceData mDevice = null;

    public static ADWeightScaleDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mDevice == null) {
                    mDevice = new ADWeightScaleDeviceData(bleDevice);
                }
            } else {
                mDevice = new ADWeightScaleDeviceData(bleDevice);
            }
        }
        return mDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mDevice != null) {
            mOnNotifyData = null;
            mDevice = null;
        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private ADWeightScaleDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        init();
    }

    private void init() {
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    //--------------发送--------------

    /**
     * 同步时间
     */
    public void setSynTime() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance()
                .setSysTime(BleDataUtils.getInstance().getCurrentTime(), true));
        sendData(sendBleBean);
    }

    /**
     * 设置单位
     *
     * @param unitWeight
     */
    public void setUnit(int unitWeight) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = ADWeightScaleBleConfig.SET_UNIT;
        data[1] = (byte) unitWeight;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 去衣模式
     *
     * @param status 是否为去衣模式(开关)
     */
    public void setUndressing(boolean status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = ADWeightScaleBleConfig.SET_UNDRESSING;
        data[1] = status ? (byte) 0x00 : (byte) 0x01;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }


    /**
     * 同步用户数据(A7)
     */
    public void setSynUserData(ADWeightScaleUserData adWeightScaleUserData) {
        byte[] dataOne = new byte[10];
        dataOne[0] = ADWeightScaleBleConfig.SYN_USER;
        dataOne[1] = 0x02;
        dataOne[2] = 0x01;
        dataOne[3] = (byte) adWeightScaleUserData.getUserType();
        dataOne[4] =
                (byte) ((adWeightScaleUserData.getSex() * 128) + adWeightScaleUserData.getAge());
        dataOne[5] = (byte) adWeightScaleUserData.getHeight();
        int userId = adWeightScaleUserData.getUserId();
        dataOne[6] = (byte) ((userId >> 24) & 0xff);
        dataOne[7] = (byte) ((userId >> 16) & 0xff);
        dataOne[8] = (byte) ((userId >> 8) & 0xff);
        dataOne[9] = (byte) (userId & 0xff);

        SendMcuBean sendMcuBeanOne = new SendMcuBean();
        sendMcuBeanOne.setHex(CID, dataOne);
        sendData(sendMcuBeanOne);

        byte[] dataTwo = new byte[11];
        dataTwo[0] = ADWeightScaleBleConfig.SYN_USER;
        dataTwo[1] = 0x02;
        dataTwo[2] = 0x02;
        dataTwo[3] = (byte) adWeightScaleUserData.getOneClothingWeight();
        dataTwo[4] = (byte) adWeightScaleUserData.getTwoClothingWeight();
        dataTwo[5] = (byte) adWeightScaleUserData.getThreeClothingWeight();
        dataTwo[6] = (byte) adWeightScaleUserData.getFourClothingWeight();
        dataTwo[7] = (byte) adWeightScaleUserData.getFiveClothingWeight();
        dataTwo[8] = (byte) adWeightScaleUserData.getSixClothingWeight();
        dataTwo[9] = (byte) adWeightScaleUserData.getSevenClothingWeight();
        dataTwo[10] = (byte) adWeightScaleUserData.getEightClothingWeight();
        //SendMcuBean由于存在发送队列,建议不要复用对象
        SendMcuBean sendMcuBeanTwo = new SendMcuBean();
        sendMcuBeanTwo.setHex(CID, dataTwo);
        sendData(sendMcuBeanTwo);

    }

    /**
     * app主动同步用户
     */
    public void setBleSynUser(ADWeightScaleUserData adWeightScaleUserData) {
        setBleSynUser(adWeightScaleUserData, ADWeightScaleBleConfig.SET_BLE_SYN_USER);
    }

    public void setBleUpdateUser(ADWeightScaleUserData adWeightScaleUserData) {
        setBleSynUser(adWeightScaleUserData, ADWeightScaleBleConfig.SET_BLE_SYN_USER_DATA);
    }


    /**
     * 同步(更新)用户(A6)
     * 0 0x2B Type：秤专用交互
     * 1 0x01 二级指令：APP 更新用户列表给 BM 模块
     * 2 0x01 组 1
     * 3 用户特征0：普通人;1：业余运动员;2：专业运动员
     * 4 Bit7：性别0：女;1：男   Bit6~0：年龄
     * 5 身高（1cm）
     * 6 Kg 重量数据高字节
     * 7 Kg 重量数据低字节（0.1kg）
     * 8 阻抗数据高字节
     * 9 阻抗数据低字节（精度为 1Ω）
     * 10~14 USER ID （4byte)
     */
    private void setBleSynUser(ADWeightScaleUserData adWeightScaleUserData, byte cmdType) {
        byte[] dataOne = new byte[14];
        dataOne[0] = CmdConfig.GET_BODY_FAT;
        dataOne[1] = cmdType;
        dataOne[2] = 0x01;
        dataOne[3] = (byte) adWeightScaleUserData.getUserType();
        dataOne[4] =
                (byte) ((adWeightScaleUserData.getSex() * 128) + adWeightScaleUserData.getAge());
        dataOne[5] = (byte) adWeightScaleUserData.getHeight();
        int weight = adWeightScaleUserData.getWeight() * 10;//
        dataOne[6] = (byte) ((weight >> 8) & 0xff);
        dataOne[7] = (byte) (weight & 0xff);
        int adc = adWeightScaleUserData.getAdc();
        dataOne[8] = (byte) ((adc >> 8) & 0xff);
        dataOne[9] = (byte) (adc & 0xff);

        int userId = adWeightScaleUserData.getUserId();
        dataOne[10] = (byte) ((userId >> 24) & 0xff);
        dataOne[11] = (byte) ((userId >> 16) & 0xff);
        dataOne[12] = (byte) ((userId >> 8) & 0xff);
        dataOne[13] = (byte) (userId & 0xff);

        SendBleBean sendBleBeanOne = new SendBleBean();
        sendBleBeanOne.setHex(dataOne);
        sendData(sendBleBeanOne);

        byte[] dataTwo = new byte[15];
        dataTwo[0] = CmdConfig.GET_BODY_FAT;
        dataTwo[1] = cmdType;
        dataTwo[2] = 0x02;
        dataTwo[3] = (byte) adWeightScaleUserData.getOneClothingWeight();
        dataTwo[4] = (byte) adWeightScaleUserData.getTwoClothingWeight();
        dataTwo[5] = (byte) adWeightScaleUserData.getThreeClothingWeight();
        dataTwo[6] = (byte) adWeightScaleUserData.getFourClothingWeight();
        dataTwo[7] = (byte) adWeightScaleUserData.getFiveClothingWeight();
        dataTwo[8] = (byte) adWeightScaleUserData.getSixClothingWeight();
        dataTwo[9] = (byte) adWeightScaleUserData.getSevenClothingWeight();
        dataTwo[10] = (byte) adWeightScaleUserData.getEightClothingWeight();
        dataTwo[11] = (byte) ((userId >> 24) & 0xff);
        dataTwo[12] = (byte) ((userId >> 16) & 0xff);
        dataTwo[13] = (byte) ((userId >> 8) & 0xff);
        dataTwo[14] = (byte) (userId & 0xff);
        SendBleBean sendBleBeanTwo = new SendBleBean();
        sendBleBeanTwo.setHex(dataTwo);
        sendData(sendBleBeanTwo);

    }


    /**
     * 阻抗识别用户
     *
     * @param status 是否开启通过阻抗识别用户
     * @param range  允许的误差范围(如:+-50则为50)
     */
    public void setBleImpedanceDiscern(boolean status, int range) {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] data = new byte[4];
        data[0] = CmdConfig.GET_BODY_FAT;
        data[1] = ADWeightScaleBleConfig.BLE_GET_IMPEDANCE;
        data[2] = status ? (byte) 0x01 : (byte) 0x00;
        data[3] = (byte) range;
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }

    /**
     * 同步用户完成
     */
    public void setBleSynUserSuccess() {
        byte[] data = new byte[2];
        data[0] = CmdConfig.GET_BODY_FAT;
        data[1] = ADWeightScaleBleConfig.SET_BLE_SYN_USER_SUCCESS;
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(data);
        sendData(sendBleBean);
    }


    /**
     * 请求同步历史记录
     *
     * @param list 需要同步历史记录的用户ID列表
     */
    public void setBleSynUserHistoryRecord(List<Integer> list) {
        if (list != null && !list.isEmpty() && list.size() <= 8) {
            int userSize = list.size();
            byte[] data = new byte[userSize * 4];
            int dataIndex = 0;
            for (int i = 0; i < list.size(); i++) {
                int userId = list.get(i);
                data[dataIndex] = (byte) ((userId >> 24) & 0xff);
                dataIndex++;
                data[dataIndex] = (byte) ((userId >> 16) & 0xff);
                dataIndex++;
                data[dataIndex] = (byte) ((userId >> 8) & 0xff);
                dataIndex++;
                data[dataIndex] = (byte) (userId & 0xff);
                dataIndex++;
            }

            //多少组
            int groupSize = ((userSize % 3) == 0) ? userSize / 3 : (userSize / 3) + 1;
            int dataIndex1 = 0;
            for (int i = 1; i <= groupSize; i++) {
                byte[] sendData;
                if (i == groupSize) {
                    int a = userSize % 3;
                    if (a == 0) {
                        a = 3;
                    }
                    sendData = new byte[a * 4 + 3];
                    System.arraycopy(data, dataIndex1, sendData, 3, (a * 4));
                    dataIndex1 += (a * 4);
                } else {
                    sendData = new byte[15];
                    System.arraycopy(data, dataIndex1, sendData, 3, (3 * 4));
                    dataIndex1 += (3 * 4);
                }
                sendData[0] = CmdConfig.GET_BODY_FAT;
                sendData[1] = ADWeightScaleBleConfig.BLE_SYN_USER_HISTORY_RECORD;
                sendData[2] = (byte) i;

                SendBleBean sendBleBean = new SendBleBean();
                sendBleBean.setHex(sendData);
                sendData(sendBleBean);
            }
            //发送完成
            byte[] sendOk = new byte[3];
            sendOk[0] = CmdConfig.GET_BODY_FAT;
            sendOk[1] = ADWeightScaleBleConfig.BLE_SYN_USER_HISTORY_RECORD;
            sendOk[2] = (byte) 0xff;
            SendBleBean sendBleBean = new SendBleBean();
            sendBleBean.setHex(sendOk);
            sendData(sendBleBean);


        }
    }


    //--------------数据接收-------


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mType == type) {
            if (hex == null) {
                BleLog.i(TAG, "接收到的数据:null");
                return;
            }
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据:" + data);
            //校验解析
            dataCheck(hex);
        }
    }

    @Override
    public void onNotifyDataA6(byte[] data) {
        if (data[0]== CmdConfig.GET_BODY_FAT){
            dataCheckA6(data);
        }

    }

    //----------------解析数据------

    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        if (data == null) {
            return;
        }
        switch (data[0]) {
            //稳定重量
            case ADWeightScaleBleConfig.GET_WEIGHT_STABLE:
                getWeight(data);
                break;
            //实时重量
            case ADWeightScaleBleConfig.GET_WEIGHT_NOW:
                getWeightNow(data);
                break;
            //温度数据
            case ADWeightScaleBleConfig.GET_TEMPERATURE:
                getTemperature(data);
                break;
            //测阻抗中
            case ADWeightScaleBleConfig.GET_IMPEDANCE_ING:
                //测阻抗成功，带上阻抗数据
            case ADWeightScaleBleConfig.GET_IMPEDANCE_SUCCESS:
                //测阻抗失败
            case ADWeightScaleBleConfig.GET_IMPEDANCE_FAILURE:
                //测阻抗成功，带上阻抗数据，并使用 APP 算法，APP 会根据 VID,PID 来识别对应算法
            case ADWeightScaleBleConfig.GET_IMPEDANCE_SUCCESS_APP:
                getImpedanceData(data);
                break;

            //去衣模式回复
            case ADWeightScaleBleConfig.GET_UNDRESSING:
                getUndressing(data);
                break;

            //同步用户信息
            case ADWeightScaleBleConfig.SYN_USER:
                getSynUser(data);
                break;
            //体脂数据
            case ADWeightScaleBleConfig.GET_BODY_FAT_DATA:
                getBodyFatData(data);
                break;
            //体脂数据完成
            case ADWeightScaleBleConfig.GET_BODY_FAT_DATA_SUCCESS:
                getBodyFatDataSuccess();
                break;
            //获取单位
            case ADWeightScaleBleConfig.GET_UNIT:
                getUnit(data);
                break;
            //返回错误信息
            case ADWeightScaleBleConfig.GET_ERR:
                getErr(data);
                break;

            //----------------a6-------------
            //



            default:
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.onData(data, mType);
                    }
                });
                break;
        }

    }


    //----------------------A6解析------------------


    /**
     * A6指令解析
     */
    private void dataCheckA6(byte[] data) {

        switch (data[1]) {
            //更新用户信息结果
            case ADWeightScaleBleConfig.SET_BLE_SYN_USER_DATA_RETURN:
                getBleUpdateUser(data);
                break;
            //阻抗识别用户回复
            case ADWeightScaleBleConfig.BLE_GET_IMPEDANCE:
                getBleImpedance(data);
                break;
            //mcu返回历史数据
            case ADWeightScaleBleConfig.GET_BLE_SYN_USER_HISTORY_RECORD:
                getBleUserHistoryRecord(data);
                break;

            //mcu返回同步历史记录结果
            case ADWeightScaleBleConfig.BLE_SYN_USER_HISTORY_RECORD:
                getBleUserHistoryRecordResult(data);
                break;

            default:break;
        }


    }

    /**
     * 同步历史记录结果
     */
    private void getBleUserHistoryRecordResult(byte[] data) {
        if (data.length < 3) {
            BleLog.i(TAG, "数据错误");
            return;
        }
        int status = data[2] & 0xff;
        //清空,避免下次同步数据重复
        mADWeightScaleBodyFatDataRecord = null;
        switch (status) {

            //无历史记录
            case 0:
                BleLog.i(TAG, "无历史记录");
                break;
            //开始发送历史记录
            case 1:
                BleLog.i(TAG, "开始发送历史记录");
                break;
            //结束发送历史记录
            case 2:
                BleLog.i(TAG, "结束发送历史记录");
                break;

            default:break;

        }

        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getBodyFatDataRecordResult(status);
            }
        });
    }


    private ADWeightScaleBodyFatDataRecord mADWeightScaleBodyFatDataRecord;

    /**
     * 请求同步历史记录返回
     */
    private void getBleUserHistoryRecord(byte[] data) {
        if (data.length < 13) {
            BleLog.i(TAG, "数据错误");
            return;
        }

        int cmdType = data[2];

        switch (cmdType) {

            case 0x01:
                if (mADWeightScaleBodyFatDataRecord == null) {
                    mADWeightScaleBodyFatDataRecord = new ADWeightScaleBodyFatDataRecord();
                }

                int year = (data[3] & 0xff) + 2000;
                int month = data[4] & 0xff;
                int day = data[5] & 0xff;
                int hour = data[6] & 0xff;
                int minute = data[7] & 0xff;
                int second = data[8] & 0xff;
                int userType = data[9] & 0xff;
                int sex = (data[10] >> 7) & 0x01;
                int age = data[10] & 0x7f;
                int height = data[11] & 0xff;
                int weight =
                        ((data[12] & 0xff) << 16) | ((data[13] & 0xff) << 8) | (data[14] & 0xff);
                int decimal = ((data[15] & 0xf0) >> 4);
                int unit = (data[15] & 0x0f);
                mADWeightScaleBodyFatDataRecord.setYear(year);
                mADWeightScaleBodyFatDataRecord.setMonth(month);
                mADWeightScaleBodyFatDataRecord.setDay(day);
                mADWeightScaleBodyFatDataRecord.setHour(hour);
                mADWeightScaleBodyFatDataRecord.setMinute(minute);
                mADWeightScaleBodyFatDataRecord.setSecond(second);
                mADWeightScaleBodyFatDataRecord.setUserType(userType);
                mADWeightScaleBodyFatDataRecord.setSex(sex);
                mADWeightScaleBodyFatDataRecord.setAge(age);
                mADWeightScaleBodyFatDataRecord.setHeight(height);
                mADWeightScaleBodyFatDataRecord.setWeight(weight);
                mADWeightScaleBodyFatDataRecord.setDecimal(decimal);
                mADWeightScaleBodyFatDataRecord.setUnit(unit);
                break;

            case 0x02:
                if (mADWeightScaleBodyFatDataRecord == null) {
                    return;
                }

                int adc = ((data[3] & 0xff) << 8 | (data[4] & 0xff));
                float bfr = ((data[5] & 0xff) << 8 | (data[6] & 0xff)) * 0.1F;
                float sfr = ((data[7] & 0xff) << 8 | (data[8] & 0xff)) * 0.1F;
                int uvi = ((data[9] & 0xff) << 8 | (data[10] & 0xff));
                float rom = ((data[11] & 0xff) << 8 | (data[12] & 0xff)) * 0.1F;
                int bmr = ((data[13] & 0xff) << 8 | (data[14] & 0xff));
                int bodyAge = (data[15] & 0xff);
                mADWeightScaleBodyFatDataRecord.setAdc(adc);
                mADWeightScaleBodyFatDataRecord.setBfr(bfr);
                mADWeightScaleBodyFatDataRecord.setSfr(sfr);
                mADWeightScaleBodyFatDataRecord.setUvi(uvi);
                mADWeightScaleBodyFatDataRecord.setRom(rom);
                mADWeightScaleBodyFatDataRecord.setBmr(bmr);
                mADWeightScaleBodyFatDataRecord.setBodyAge(bodyAge);

                break;
            case 0x03:
                if (mADWeightScaleBodyFatDataRecord == null) {
                    return;
                }
                float bm = ((data[3] & 0xff) << 8 | (data[4] & 0xff)) * 0.1F;
                float vwc = ((data[5] & 0xff) << 8 | (data[6] & 0xff)) * 0.1F;
                float pp = ((data[7] & 0xff) << 8 | (data[8] & 0xff)) * 0.1F;
                int hr = (data[9] & 0xff);
                int userId =
                        ((data[10] & 0xff) << 24) | ((data[11] & 0xff) << 16) | ((data[12] & 0xff) << 8) | (data[13] & 0xff);
                mADWeightScaleBodyFatDataRecord.setBm(bm);
                mADWeightScaleBodyFatDataRecord.setVwc(vwc);
                mADWeightScaleBodyFatDataRecord.setPp(pp);
                mADWeightScaleBodyFatDataRecord.setHr(hr);
                mADWeightScaleBodyFatDataRecord.setUserId(userId);

                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        if (mADWeightScaleBodyFatDataRecord != null) {
                            ADWeightScaleBodyFatDataRecord ADWeightScaleBodyFatDataRecordNew =
                                    mADWeightScaleBodyFatDataRecord
                                            .clone();
                            mOnNotifyData.getBodyFatDataRecord(ADWeightScaleBodyFatDataRecordNew);
                            adWeightScaleBodyFatData = null;
                        } else {
                            mOnNotifyData.getBodyFatDataRecord(null);
                        }
                    }
                });
                break;
            default:
                break;


        }


    }

    /**
     * 同步用户结果返回
     */
    private void getBleUpdateUser(byte[] data) {
        if (data.length < 3) {
            BleLog.i(TAG, "数据错误");
            return;
        }
        //00：更新列表成功
        //01：更新个人用户成功
        //02：更新列表失败
        //03：更新个人用户失败
        int status = data[2];
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getAppUpdateUser(status);
            }
        });


    }


    //----------------------A6解析------------------


    /**
     * 体脂数据测量完成
     */
    private void getBodyFatDataSuccess() {
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                if (adWeightScaleBodyFatData != null) {
                    ADWeightScaleBodyFatData ADWeightScaleBodyFatDataNew =
                            adWeightScaleBodyFatData.clone();
                    mOnNotifyData.getBodyFatData(ADWeightScaleBodyFatDataNew);
                    adWeightScaleBodyFatData = null;
                } else {
                    mOnNotifyData.getBodyFatData(null);
                }

            }
        });


    }


    private ADWeightScaleBodyFatData adWeightScaleBodyFatData = null;

    /**
     * 返回的体脂数据
     */
    private void getBodyFatData(byte[] data) {
        if (data.length < 9) {
            BleLog.i(TAG, "数据错误");
            return;
        }

        int cmdType = data[1] & 0xff;
        switch (cmdType) {
            //第一部分数据
            case 1:
                adWeightScaleBodyFatData = new ADWeightScaleBodyFatData();
                float bfr = ((data[2] & 0xff) << 8 | (data[3] & 0xff)) * 0.1F;
                float sfr = ((data[4] & 0xff) << 8 | (data[5] & 0xff)) * 0.1F;
                int uvi = ((data[6] & 0xff) << 8 | (data[7] & 0xff));
                float rom = ((data[8] & 0xff) << 8 | (data[9] & 0xff)) * 0.1F;
                int bmr = ((data[10] & 0xff) << 8 | (data[11] & 0xff));
                int bodyAge = (data[12] & 0xff);
                adWeightScaleBodyFatData.setBfr(bfr);
                adWeightScaleBodyFatData.setSfr(sfr);
                adWeightScaleBodyFatData.setUvi(uvi);
                adWeightScaleBodyFatData.setRom(rom);
                adWeightScaleBodyFatData.setBmr(bmr);
                adWeightScaleBodyFatData.setBodyAge(bodyAge);

                break;

            //第二部分数据
            case 2:

                if (adWeightScaleBodyFatData == null) {
                    return;
                }
                float bm = ((data[2] & 0xff) << 8 | (data[3] & 0xff)) * 0.1F;
                float vwc = ((data[4] & 0xff) << 8 | (data[5] & 0xff)) * 0.1F;
                float pp = ((data[6] & 0xff) << 8 | (data[7] & 0xff)) * 0.1F;
                int hr = (data[8] & 0xff);
                adWeightScaleBodyFatData.setBm(bm);
                adWeightScaleBodyFatData.setVwc(vwc);
                adWeightScaleBodyFatData.setPp(pp);
                adWeightScaleBodyFatData.setHr(hr);
                break;
            default:
                break;

        }


    }

    /**
     * 请求同步用户
     */
    private void getSynUser(byte[] data) {
        if (data.length < 2) {
            BleLog.i(TAG, "数据错误");
            return;
        }
        int cmdType = (data[1] & 0xff);
        switch (cmdType) {
            //请求同步用户
            case 1:
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.requestSynUser();
                    }
                });
                break;
            //成功
            case 3:
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.getSynUser(true);
                    }
                });
                break;
            //失败
            case 4:
                runOnMainThread(() -> {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.getSynUser(false);
                    }
                });
                break;

            default:break;


        }

    }


    /**
     * 阻抗值解析
     */
    private void getImpedanceData(byte[] data) {
        if (data.length < 3) {
            BleLog.i(TAG, "数据错误");
            return;
        }
        int cmdType = data[0] & 0xff;
        int impedance = ((data[1] & 0xff) << 8) | (data[2] & 0xff);
        switch (cmdType) {
            //测阻抗中
            case ADWeightScaleBleConfig.GET_IMPEDANCE_ING:
                BleLog.i(TAG, "测阻抗中");
                break;
            //测阻抗成功，带上阻抗数据
            case ADWeightScaleBleConfig.GET_IMPEDANCE_SUCCESS:
                BleLog.i(TAG, "测阻抗成功");
                break;
            //测阻抗失败
            case ADWeightScaleBleConfig.GET_IMPEDANCE_FAILURE:
                BleLog.i(TAG, "测阻抗失败");
                break;
            //测阻抗成功，带上阻抗数据，并使用 APP 算法，APP 会根据 VID,PID 来识别对应算法
            case ADWeightScaleBleConfig.GET_IMPEDANCE_SUCCESS_APP:
                BleLog.i(TAG, "测阻抗成功,使用app算法");
                break;
            default:break;
        }

        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getImpedance(cmdType, impedance);
            }
        });

    }


    /**
     * 温度数据
     */
    private void getTemperature(byte[] data) {
        if (data.length < 3) {
            BleLog.i(TAG, "数据错误");
            return;
        }
        boolean negative = ((data[1] >> 7) & 0x01) == 1;//是否为负数
        double temperature = (((data[1] & 0x7f) << 8) | (data[2] & 0xff)) * 0.1;//温度,精确到0.1
        temperature = negative ? -temperature : temperature;
        double finalTemperature = temperature;
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getTemperature(finalTemperature);
            }
        });
        BleLog.i(TAG, "温度:" + finalTemperature + "℃");
    }

    /**
     * 稳定重量
     */
    private void getWeight(byte[] data) {
        if (data.length < 4) {
            BleLog.i(TAG, "数据错误");
            return;
        }
        int weight = ((data[1] & 0xff) << 16) | ((data[2] & 0xff) << 8) | (data[3] & 0xff);
        int unit = BleStrUtils.getBits(data[4], 0, 4);
        int decimal = BleStrUtils.getBits(data[4], 4, 4);
        String unitStr = "kg";
        switch (unit) {
            case ADWeightScaleBleConfig.WEIGHT_KG:
                unitStr = "kg";
                break;
            case ADWeightScaleBleConfig.WEIGHT_JIN:
                unitStr = "斤";
                break;
            case ADWeightScaleBleConfig.WEIGHT_LB:
                unitStr = "lb:oz";
                break;
            case ADWeightScaleBleConfig.WEIGHT_OZ:
                unitStr = "oz";
                break;
            case ADWeightScaleBleConfig.WEIGHT_ST:
                unitStr = "st:lb";
                break;
            case ADWeightScaleBleConfig.WEIGHT_G:
                unitStr = "g";
                break;
            case ADWeightScaleBleConfig.WEIGHT_LB_LB:
                unitStr = "LB";
                break;

            default:
                break;
        }
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getWeight(weight, decimal, unit);
            }
        });

        BleLog.i(TAG, "稳定重量:" + weight + unitStr + "|mOnNotifyData:" + mOnNotifyData);
    }

    /**
     * 实时重量
     */
    private void getWeightNow(byte[] data) {
        if (data.length < 4) {
            return;
        }
        int weight = ((data[1] & 0xff) << 16) | ((data[2] & 0xff) << 8) | (data[3] & 0xff);
        int unit = BleStrUtils.getBits(data[4], 0, 4);
        int decimal = BleStrUtils.getBits(data[4], 4, 4);
        String unitStr = "kg";
        switch (unit) {
            case ADWeightScaleBleConfig.WEIGHT_KG:
                unitStr = "kg";
                break;
            case ADWeightScaleBleConfig.WEIGHT_JIN:
                unitStr = "斤";
                break;
            case ADWeightScaleBleConfig.WEIGHT_LB:
                unitStr = "lb:oz";
                break;
            case ADWeightScaleBleConfig.WEIGHT_OZ:
                unitStr = "oz";
                break;
            case ADWeightScaleBleConfig.WEIGHT_ST:
                unitStr = "st:lb";
                break;
            case ADWeightScaleBleConfig.WEIGHT_G:
                unitStr = "g";
                break;
            case ADWeightScaleBleConfig.WEIGHT_LB_LB:
                unitStr = "LB";
                break;

            default:break;

        }
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getWeightNow(weight, decimal, unit);
            }
        });
        BleLog.i(TAG, "实时重量:" + weight + unitStr + "||decimal=" + decimal);

    }


    /**
     * 设置单位返回
     */
    private void getUnit(byte[] data) {
        if (data.length > 1) {
            int status = data[1] & 0xff;
            switch (status) {
                case CmdConfig.SETTING_SUCCESS:
                    BleLog.i(TAG, "设置单位成功");
                    break;
                case CmdConfig.SETTING_FAILURE:
                    BleLog.i(TAG, "设置单位失败");

                    break;
                case CmdConfig.SETTING_ERR:
                    BleLog.i(TAG, "设置单位错误");

                    break;
            }
            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getUnit(status);
                }
            });

        }
    }


    /**
     * 阻抗识别用户回复
     */
    private void getBleImpedance(byte[] data) {
        int status = (data[2] & 0xff);//结果
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getImpedance(status);
            }
        });
    }

    /**
     * 去衣模式回复
     */
    private void getUndressing(byte[] data) {
        int status = (data[1] & 0xff);//结果
        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getUndressing(status);
            }
        });
    }


    /**
     * 错误指令
     */
    private void getErr(byte[] data) {
        byte status = data[1];
        String statusStr = "";
        if (status == 1) {
            statusStr = "超重";
        }

        runOnMainThread(() -> {
            if (mOnNotifyData != null) {
                mOnNotifyData.getErr(status);
            }
        });

        BleLog.i(TAG, statusStr);
    }

    //----------------解析数据------


    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (!status) {
            disconnect();
        }
        BleLog.i(TAG, "握手:" + status);
    }


    public interface onNotifyData {
        /**
         * 不能识别的透传数据
         */
        default void onData(byte[] data, int type) {
        }

        /**
         * 稳定体重
         * 0：kg
         * 1：斤
         * 2：lb
         * 3：oz
         * 4：st
         * 5：g
         *
         * @param weight  原始数据
         * @param decimal 小数点位
         * @param unit    单位
         */
        default void getWeight(int weight, int decimal, int unit) {
        }

        /**
         * 实时体重
         * 0：kg
         * 1：斤
         * 2：lb
         * 3：oz
         * 4：st
         * 5：g
         *
         * @param weight  原始数据
         * @param decimal 小数点位
         * @param unit    单位
         */
        default void getWeightNow(int weight, int decimal, int unit) {
        }

        /**
         * 温度
         *
         * @param temp 摄氏度
         */
        default void getTemperature(double temp) {
        }

        /**
         * 阻抗测量
         *
         * @param status    当前状态
         * @param impedance 阻抗值
         */
        default void getImpedance(int status, double impedance) {
        }

        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void getUnit(int status) {
        }

        /**
         * 去衣<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void getUndressing(int status) {
        }

        /**
         * 阻抗<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        default void getImpedance(int status) {
        }

        /**
         * 请求同步用户
         */
        default void requestSynUser() {
        }

        /**
         * 同步用户结果
         *
         * @param status 是否成功
         */
        default void getSynUser(boolean status) {
        }


        /**
         * 返回的体脂数据
         */
        default void getBodyFatData(ADWeightScaleBodyFatData adWeightScaleBodyFatData) {
        }

        /**
         * 返回历史记录
         */
        default void getBodyFatDataRecord(ADWeightScaleBodyFatDataRecord adWeightScaleBodyFatDataRecord) {
        }

        /**
         * 返回历史记录操作结果
         * @param status
         */
        default void getBodyFatDataRecordResult(int status) {
        }

        /**
         * 错误指令
         * 0：超重<br>
         * 1：称重抓 0 期间，重量不稳定<br>
         * 2：称重抓 0 失败<br>
         */
        default void getErr(byte status) {
        }

        /**
         * 同步,更新用户信息的结果
         * 0:更新列表成功
         * 1:更新个人用户成功
         * 2:更新列表失败
         * 3:更新个人用户失败
         *
         * @param status 结果
         */
        default void getAppUpdateUser(int status) {
        }

    }


    //-----------------set/get-----------------

    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }


    public void setOnBleVersionListener(OnBleVersionListener bleVersionListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleVersionListener(bleVersionListener);
        }
    }

    public void setOnMcuParameterListener(OnMcuParameterListener mcuParameterListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnMcuParameterListener(mcuParameterListener);
        }
    }

    public void setOnCompanyListener(OnBleCompanyListener companyListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleCompanyListener(companyListener);
        }
    }

    public void setOnBleSettingListener(OnBleSettingListener onBleSettingListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleSettingListener(onBleSettingListener);
        }
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper());


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
