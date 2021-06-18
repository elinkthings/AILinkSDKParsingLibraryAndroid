package cn.net.aicare.modulelibrary.module.findDevice;

/**
 * xing<br>
 * 2021/3/24<br>
 * 寻物器连接的设备信息
 */
public class FindConnectDeviceInfoBean {

    private int mDeviceId;
    private String mMac;

    public FindConnectDeviceInfoBean(int deviceId, String mac) {
        mDeviceId = deviceId;
        mMac = mac;
    }

    public FindConnectDeviceInfoBean() {
    }

    public int getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(int deviceId) {
        mDeviceId = deviceId;
    }

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    @Override
    public String toString() {
        return "FindConnectDeviceInfoBean{" + "mDeviceId=" + mDeviceId + ", mMac='" + mMac + '\'' + '}';
    }
}
