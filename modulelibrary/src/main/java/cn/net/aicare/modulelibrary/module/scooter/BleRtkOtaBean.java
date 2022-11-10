package cn.net.aicare.modulelibrary.module.scooter;

import java.util.Arrays;

/**
 * xing<br>
 * 2020/12/5<br>
 * java类作用描述
 */
public class BleRtkOtaBean {

    private int mId;
    private byte[] mData;//数据包
    private byte[] mDataAll;//数据包+ID

    public BleRtkOtaBean(int id, byte[] data) {
        mId = id;
        mData = data;
        byte[] b = new byte[2];
        b[0] = (byte) ((id >> 8) & 0xff);
        b[1] = (byte) (id & 0xff);
        mDataAll=new byte[data.length+2];
        System.arraycopy(b, 0, mDataAll, 0, b.length);
        System.arraycopy(mData, 0, mDataAll, 2, mData.length);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] data) {
        mData = data;
    }

    public byte[] getDataAll() {
        return mDataAll;
    }


    @Override
    public String toString() {
        return "BleScooterOtaBean{" + "mId=" + mId + ", mData=" + Arrays.toString(mData) + ", mDataAll=" + Arrays.toString(mDataAll) + '}';
    }
}
