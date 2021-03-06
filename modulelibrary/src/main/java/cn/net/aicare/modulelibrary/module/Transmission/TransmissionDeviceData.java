package cn.net.aicare.modulelibrary.module.Transmission;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.List;

public class TransmissionDeviceData extends BaseBleDeviceData {

    private MyBleCallback mMyBleCallback;

    public TransmissionDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(new OnBleVersionListener() {
            @Override
            public void onBmVersion(String version) {
               if (mMyBleCallback!=null)
                   mMyBleCallback.onVersion(version);
            }

            @Override
            public void onSupportUnit(List<SupportUnitBean> list) {
                if (mMyBleCallback!=null)
                    mMyBleCallback.onSupportUnit(list);
            }
        });
        bleDevice.setOnBleCompanyListener(new OnBleCompanyListener() {
            @Override
            public void OnDID(int cid, int vid, int pid) {
                if (mMyBleCallback!=null)
                    mMyBleCallback.onCid(cid, vid, pid);
            }
        });
        bleDevice.setOnBleOtherDataListener(new OnBleOtherDataListener() {
            @Override
            public void onNotifyOtherData(byte[] data) {

                if (mMyBleCallback!=null){
                    mMyBleCallback.otherdata(BleStrUtils.byte2HexStr(data));
                }

            }
        });

    }


    public void setMyBleCallback(MyBleCallback mMyBleCallback) {
        this.mMyBleCallback = mMyBleCallback;
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {

        if (mMyBleCallback!=null){
            mMyBleCallback.showdata(BleStrUtils.byte2HexStr(hex));
        }

    }
//    @Override
//    public void OnDID(int cid, int vid, int pid) {
//        mList.add(TimeUtils.getTime() + "cid:" + cid + "||vid:" + vid + "||pid:" + pid);
//        mHandler.sendEmptyMessage(REFRESH_DATA);
//    }



    public interface MyBleCallback {
        void onVersion(String version);
        void showdata(String data);
        void onSupportUnit(List<SupportUnitBean> list);
        void onCid(int cid, int vid, int pid);
        void otherdata(String data);
        void sendData(String data);
    }



    public void setSendData(int cid ,byte[] bytes){
        SendMcuBean smb = new SendMcuBean();
        smb.setHex(cid,bytes);
        sendData(smb);
        if (mMyBleCallback!=null){
            mMyBleCallback.sendData(BleStrUtils.byte2HexStr(smb.getHex()));
        }
    }



    public void getCid(){
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(getDid());
        sendData(sendBleBean);

    }


    public byte[] getDid() {
        byte[] sendData = new byte[1];
        sendData[0] = CmdConfig.GET_DID;
        return  sendData;
    }
    public void getUnitList(){
        byte[] sendData = new byte[]{44, 1};
      sendData(new SendBleBean(sendData));
    }


}
