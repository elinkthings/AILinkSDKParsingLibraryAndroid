package cn.net.aicare.modulelibrary.module.wifibleDevice;

/**
 * wifi信息豆
 *
 * @author xing
 * @date 2023/10/06
 */
public class WifiBleInfoBean {

    private int mId;
    private String mMac;
    private String mSsid;
    private int mDb;
    @WifiBleConfig.SecurityType
    private int mType;
    @WifiBleConfig.WifiStatusType
    private int mWifiStatus;



    public WifiBleInfoBean(int id) {
        mId = id;
    }

    public WifiBleInfoBean(int id, String mac, String ssid, int db, int type, int wifiStatus) {
        mId = id;
        mMac = mac;
        mSsid = ssid;
        mDb = db;
        mType = type;
        mWifiStatus = wifiStatus;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    public String getSsid() {
        return mSsid;
    }

    public void setSsid(String ssid) {
        mSsid = ssid;
    }

    public int getDb() {
        return mDb;
    }

    public void setDb(int db) {
        mDb = db;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getWifiStatus() {
        return mWifiStatus;
    }

    public void setWifiStatus(int wifiStatus) {
        mWifiStatus = wifiStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WifiBleInfoBean) {
            return mId == ((WifiBleInfoBean) obj).getId();
        } else if (obj instanceof Integer) {
            return mId == (int) obj;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "WifiInfoBean{" + "mId=" + mId + ", mMac='" + mMac + '\'' + ", mSsid='" + mSsid + '\'' + ", mDb=" + mDb + ", mType=" + mType + ", mWifiStatus=" + mWifiStatus + '}';
    }
}
