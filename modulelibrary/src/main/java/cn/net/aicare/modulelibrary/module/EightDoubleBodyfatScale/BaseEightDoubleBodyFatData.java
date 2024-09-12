package cn.net.aicare.modulelibrary.module.EightDoubleBodyfatScale;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleLog;

public abstract class BaseEightDoubleBodyFatData extends BaseBleDeviceData {

    protected OnEightDoubleBodyFatCallback mOnEightDoubleBodyFatCallback;

    protected int mCid = EightDoubleBodyFatBleConfig.CID_APP;

    public BaseEightDoubleBodyFatData(BleDevice bleDevice) {
        super(bleDevice);
    }


    public void setEightBodyFatCallback(OnEightDoubleBodyFatCallback onEightDoubleBodyFatCallback) {
        mOnEightDoubleBodyFatCallback = onEightDoubleBodyFatCallback;
    }


    /**
     * 回复测量完成
     */
    public void replyTestCompleted() {
        SendMcuBean smb = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x84;
        bytes[1] = 0x00;
        smb.setHex(mCid, bytes);
        sendData(smb);
        BleLog.i("回复测试完成.");
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
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x81;
        bytes[1] = (byte) unit;
        smb.setHex(mCid, bytes);
        sendData(smb);
    }

    /**
     * 获取单位列表
     */
    public void getUnitList() {
        sendData(new SendBleBean(BleSendCmdUtil.getInstance().getSupportUnit()));
    }


}
