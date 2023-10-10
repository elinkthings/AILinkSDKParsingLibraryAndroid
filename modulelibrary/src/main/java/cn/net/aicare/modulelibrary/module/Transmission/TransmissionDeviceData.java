package cn.net.aicare.modulelibrary.module.Transmission;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.List;

public class TransmissionDeviceData extends BaseBleDeviceData {

    private MyBleCallback mMyBleCallback;
    private BleDevice mBleDevice;

    public TransmissionDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice=bleDevice;
//        if (mBleDevice!=null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                boolean b = mBleDevice.setMtu(30);
//                BleLog.i(" MTU:"+b);
//            }
//        }
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
                    mMyBleCallback.otherData(data, BleStrUtils.byte2HexStr(data));
                }

            }
        });

    }


    public void setMyBleCallback(MyBleCallback mMyBleCallback) {
        this.mMyBleCallback = mMyBleCallback;
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {

        if (mMyBleCallback!=null){
            mMyBleCallback.showData(BleStrUtils.byte2HexStr(hex),type);
        }

    }

    @Override
    public void onNotifyDataA6(byte[] hex) {

    }

    //    @Override
//    public void OnDID(int cid, int vid, int pid) {
//        mList.add(TimeUtils.getTime() + "cid:" + cid + "||vid:" + vid + "||pid:" + pid);
//        mHandler.sendEmptyMessage(REFRESH_DATA);
//    }



    public interface MyBleCallback {
        /**
         * 版本
         *
         * @param version 版本
         */
        void onVersion(String version);

        /**
         * 显示数据
         *
         * @param data 数据
         * @param type 类型
         */
        void showData(String data,int type);

        /**
         * 支持单位
         *
         * @param list 列表
         */
        void onSupportUnit(List<SupportUnitBean> list);

        /**
         * CID信息
         *
         * @param cid cid
         * @param vid 从视频
         * @param pid pid
         */
        void onCid(int cid, int vid, int pid);

        /**
         * 其他数据
         *
         * @param hex  十六进制
         * @param data 数据
         */
        void otherData(byte[] hex, String data);

        /**
         * 发送数据
         *
         * @param data 数据
         */
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

    public void setSendDataA6(byte[] bytes){
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
        if (mMyBleCallback!=null){
            mMyBleCallback.sendData(BleStrUtils.byte2HexStr(sendBleBean.getHex()));
        }
    }

    public void setSendDataCustomize(byte[] bytes){
        SendDataBean sendDataBean=new SendDataBean(bytes,BleConfig.UUID_WRITE_AILINK,BleConfig.WRITE_DATA,BleConfig.UUID_SERVER_AILINK);
        sendData(sendDataBean);
        if (mMyBleCallback!=null){
            mMyBleCallback.sendData(BleStrUtils.byte2HexStr(sendDataBean.getHex()));
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
