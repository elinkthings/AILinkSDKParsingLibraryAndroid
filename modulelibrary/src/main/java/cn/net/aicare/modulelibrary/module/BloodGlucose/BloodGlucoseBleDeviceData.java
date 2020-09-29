package cn.net.aicare.modulelibrary.module.BloodGlucose;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.List;

public class BloodGlucoseBleDeviceData extends BaseBleDeviceData {

    private BloodGlucoseCallback mBloodGlucoseCallback;
    private int cid=BloodGlucoseUtil.BLOOD_GLUCOSE;
    public BloodGlucoseBleDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
                if (mBloodGlucoseCallback != null)
                    mBloodGlucoseCallback.onVersion(version);
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mBloodGlucoseCallback != null)
                    mBloodGlucoseCallback.onSupportUnit(list);
            }
        });
    }

    public void setBloodGlucoseCallback(BloodGlucoseCallback bloodGlucoseCallback) {
        mBloodGlucoseCallback = bloodGlucoseCallback;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        int cmd = hex[0] & 0xff;
        if (mBloodGlucoseCallback!=null){
            mBloodGlucoseCallback.onData(BleStrUtils.byte2HexStr(hex));
        }
        switch (cmd) {
            case BloodGlucoseUtil.DEVICE_STATUS:
                if (mBloodGlucoseCallback != null) {
                    mBloodGlucoseCallback.onDeviceStatus(hex[1] & 0xff);
                }
                break;
            case BloodGlucoseUtil.BLOOD_DATA:
                if (mBloodGlucoseCallback != null) {
                    int originalValue = getBloodData(hex);
                    int decimal=hex[5] & 0xff;
                    float value=-1;
                    if (decimal==1){
                        value=originalValue/10f;
                    }else if (decimal==2){
                        value=originalValue/100f;
                    }else {
                        value=originalValue;
                    }
                    mBloodGlucoseCallback.onResult(originalValue, value, hex[4] & 0xff,decimal );
                }

                break;
            case BloodGlucoseUtil.ERROR_CODE:
                if (mBloodGlucoseCallback!=null){
                    mBloodGlucoseCallback.onErrorCode(hex[1]&0xff);
                }
               break;

            case BloodGlucoseUtil.SET_UNIT_CALLBACK:
                if (mBloodGlucoseCallback!=null){
                    mBloodGlucoseCallback.onSetUnitResult(hex[1]&0xff);
                }

        }

    }



    public void setUnit(int unit){

        SendMcuBean smb = new SendMcuBean();
        byte[] bytes=new byte[2];
        bytes[0]=BloodGlucoseUtil.SET_UNIT;
        bytes[1]= (byte) unit;
        smb.setHex(cid,bytes);
        sendData(smb);
    }


    public void queryStatus(){
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes=new byte[2];
        bytes[0]=BloodGlucoseUtil.CHECK_STATUS;
        bytes[1]= 0x01;
        smb.setHex(cid,bytes);
        sendData(smb);
    }




    private int getBloodData(byte[] hex) {
        int hight = (hex[1] & 0xff) << 16;
        int mid = (hex[2] & 0xff) << 8;
        int low = hex[3] & 0xff;
        return hight + mid + low;
    }


    public interface BloodGlucoseCallback {
        /**
         * 版本号
          * @param version  版本号
         */
        void onVersion(String version);

        /**
         * 支持的单位列表
         * @param list 支持的单位列表
         */
        void onSupportUnit(List<SupportUnitBean> list);

        /**
         * 设备状态
         * @param status 设备状态 0x00：无状态 0x01:设备等待插入试纸 0x02:设备已插入试纸，等待获取血样 0x03:血样已获取，分析血样中... 0x04:上发数据完成，测量完成
         */
        void onDeviceStatus(int status);

        /**
         * 测试结果
         * @param originalValue  设备错过来的原始值，未做处理
         * @param value    处理后的值
         * @param unit    单位
         * @param decimal 小数点位
         */
        void onResult(int originalValue, float value, int unit, int decimal);

        /**
         * 错误码
         * @param code 错误码 0x01：电池没电 0x02：已使用过的试纸 0x03：环境温度超出使用范围 0x04：试纸施加血样后测试未完成，被退出试纸 0x05：机器自检未通过 0x06：测量结果过低，超出测量范围 0x07：测量结果过高，超出测量范围
         */
        void onErrorCode(int code);

        /**
         * 设置单位回调
         * @param result 0：设置成功 1：设置失败 2：不支持设置
         */
        void onSetUnitResult(int result);

        /**
         * 蓝牙数据
         * @param data
         */
        void onData(String data);

    }
}
