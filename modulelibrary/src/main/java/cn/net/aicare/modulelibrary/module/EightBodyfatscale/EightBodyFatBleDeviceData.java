package cn.net.aicare.modulelibrary.module.EightBodyfatscale;

import android.util.Log;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.List;

public class EightBodyFatBleDeviceData extends BaseBleDeviceData {

    private EightBodyFatCallback mEightBodyFatCallback;
    private int cid=EightBodyfatUtil.WEIGHT_BODY_FAT_SCALE_WIFI_BLE;
    public EightBodyFatBleDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
               if (mEightBodyFatCallback!=null)
                    mEightBodyFatCallback.onVersion(version);
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mEightBodyFatCallback!=null)
                    mEightBodyFatCallback.onSupportUnit(list);
            }
        });

    }


    public void setEightBodyFatCallback(EightBodyFatCallback eightBodyFatCallback) {
        mEightBodyFatCallback = eightBodyFatCallback;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        Log.e("八级秤", BleStrUtils.byte2HexStr(hex));
        if (mEightBodyFatCallback!=null){
            mEightBodyFatCallback.showData(BleStrUtils.byte2HexStr(hex));
        }
        int cmd = hex[0] & 0xff;
        int status = hex[1] & 0xff;

        switch (cmd) {
            case EightBodyfatUtil.WEIGHING:

                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onWeight(status, getWeight(hex),  getUnit(hex),getDecimal(hex));
                    mEightBodyFatCallback.onState(cmd, status, -1);
                }

                break;
            case EightBodyfatUtil.IMPEDANCE:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, hex[2] & 0xff);
                }
                if (status == EightBodyfatUtil.IMPEDANCE_SUCCESS) {
                    if (mEightBodyFatCallback != null) {
                        mEightBodyFatCallback.onImpedance(getAdc(hex), hex[2] & 0xff, hex[7] & 0xff);
                    }
                }


                break;
            case EightBodyfatUtil.HEART_RATE:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, -1);
                }
                if (EightBodyfatUtil.HEART_RATE_SUCCESS == status) {
                    if (mEightBodyFatCallback != null) {
                        mEightBodyFatCallback.onHeartRate(hex[2] & 0xff);
                    }
                }

                break;
            case EightBodyfatUtil.TEMP_MEASUREMENT:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, -1, -1);
                }
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onTemp(hex[1] & 0xff, getTemp(hex), getTempUnit(hex), getTempDecimal(hex));
                }
                break;
            case EightBodyfatUtil.MEASUREMENT_END:
                //测量完成
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, -1, -1);
                }
                break;
            case EightBodyfatUtil.MUC_CALL_BACK_RESULT:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, hex[2] & 0xff);
                }
                break;
            case EightBodyfatUtil.ERROR_CODE:
                if (mEightBodyFatCallback != null) {
                    mEightBodyFatCallback.onState(cmd, status, 0);
                }
                break;
        }
    }


    private float getWeight(byte[] hex) {
        if (hex.length >= 6) {
            int weightHigh = (hex[2] & 0xff) << 16 ;
            int weightMiddle= (hex[3] & 0xff) << 8 ;
            int weightLow=hex[4]&0xff;
            int weight=weightHigh+weightMiddle+weightLow;


            int decimal = (hex[5] & 0xf0) >> 4;
            Log.e("八级秤", decimal+"  小数位");
            if (decimal == 1) {
                Log.e("八级秤", weight+"体重");
              float myWeight=  weight / 10f;
                Log.e("八级秤", myWeight+"体重");
                return myWeight;
            } else if (decimal == 2) {
                return weight / 100f;
            } else if (decimal == 3) {
                return weight / 1000f;
            } else
                return weight;
//            return weight*(float)Math.pow(10,0-decimal);
        }

        return -1;
    }

    private int getDecimal(byte[] hex) {
        if (hex.length >= 6)
            return (hex[5] & 0xf0) >> 4;
        return -1;
    }

    private int getUnit(byte[] hex) {
        if (hex.length >= 6)
            return (hex[5] & 0x0f);
        return -1;
    }

    private int getAdc(byte[] hex) {
        if (hex.length >= 9) {
            int highAdc = (hex[3] & 0xff) << 24;
            int highAdc1 = (hex[4] & 0xff) << 16;
            int lowAdc = (hex[5] & 0xff) << 8;
            int lowAdc1 = (hex[6] & 0xff);

            return highAdc + highAdc1 + lowAdc + lowAdc1;
        } else
            return 0;
    }

    private float getTemp(byte[] hex) {
        if (hex.length >= 5) {
            int tempHigh =( hex[2] & 0xff)<<8;
            int tempLow=hex[3]&0xff;
            int temp=tempHigh+tempLow;
            int decimal = (hex[4] & 0xf0) >> 4;
            if (decimal == 1)
                return temp / 10f;
            else if (decimal == 2)
                return temp / 100f;
            else if (decimal == 3)
                return temp / 1000f;
            else return temp;
        }
        return -1;
    }

    private int getTempDecimal(byte[] hex) {
        if (hex.length >= 5)
            return (hex[4] & 0xf0) >> 4;
        return -1;
    }

    private int getTempUnit(byte[] hex) {
        if (hex.length >= 5)
            return (hex[4] & 0x0f);
        return -1;
    }


    public interface EightBodyFatCallback {
        /**
         * 测量状态
         * Measurement status
         *
         * @param type      测量的类型
         *                  Type of measurement
         * @param typeState 该类型的状态
         *                  Status of the type
         * @param result    结果
         *                  result
         */
        void onState(int type, int typeState, int result);

        /**
         * 体重数据接口
         * Weight data interface
         *
         * @param state   测量状态 1实时体重 2稳定体重
         *                Measurement status 1 Real-time weight 2 Stable weight
         * @param weight  体重
         *                weight
         * @param unit    单位
         *                unit
         * @param decimal 小数点位
         *                decimal
         */
        void onWeight(int state, float weight, int unit, int decimal);

        /**
         * 阻抗
         * impedance
         * @param adc        阻抗
         *                   impedance
         * @param arithmetic 算法
         *                   arithmetic
         */
        void onImpedance(int adc, int part, int arithmetic);

        /**
         * 心率
         * heartRate
         * @param heartRate heartRate
         */
        void onHeartRate(int heartRate);

        /**
         * 温度
         * temperature
         *
         * @param sign    正负
         *                Positive and negative
         * @param temp    温度
         *                temperature
         * @param unit    单位
         *                unit
         * @param decimal 小数点位
         *                decimal
         */
        void onTemp(int sign, float temp, int unit, int decimal);

        /**
         * 获取到版本号
         * Get the version number
         * @param version
         */
        void onVersion(String version);

        /**
         * 获取到支持的单位列表
         * Get the list of supported units
         * @param list
         */
        void onSupportUnit(List<SupportUnitBean> list );


        void showData(String data );

    }



    public void setWeightUnit(int unit){
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes=new byte[4];
        bytes[0]= (byte) 0x81;
        bytes[1]=0x03;
        bytes[2]= (byte) unit;
        bytes[3]=0;
        smb.setHex(cid,bytes);
        sendData(smb);
    }

    public void setTempUnit(int unit){
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes=new byte[4];
        bytes[0]= (byte) 0x81;
        bytes[1]=0x02;
        bytes[2]= (byte) unit;
        bytes[3]=0;
        smb.setHex(cid,bytes);
        sendData(smb);
    }

    public void setCalibration(){
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes=new byte[4];
        bytes[0]= (byte) 0x81;
        bytes[1]=0x01;
        bytes[2]= 0;
        bytes[3]=0;
        smb.setHex(cid,bytes);
        sendData(smb);
    }

    public void getUnitList(){
        byte[] sendData = new byte[]{44, 1};
      sendData(new SendBleBean(sendData));
    }


}
