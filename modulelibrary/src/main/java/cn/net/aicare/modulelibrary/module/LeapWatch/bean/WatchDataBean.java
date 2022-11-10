package cn.net.aicare.modulelibrary.module.LeapWatch.bean;

import java.util.Arrays;

/**
 * xing<br>
 * 2022/6/13<br>
 * java类作用描述
 */
public class WatchDataBean implements Comparable<WatchDataBean> {

    private int mNo;
    private int mSize;
    private byte[] mBytes;

    public WatchDataBean(int no, byte[] bytes) {
        mNo = no;
        mBytes = bytes;
        mSize=bytes.length;
    }

    public int getNo() {
        return mNo;
    }

    public void setNo(int no) {
        mNo = no;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;
        mSize=bytes.length;
    }

    public int getSize() {
        return mSize;
    }



    @Override
    public String toString() {
        return "WatchDataBean{" + "mNo=" + mNo + ", mSize=" + mSize + ", mBytes=" + Arrays.toString(mBytes) + '}';
    }

    @Override
    public int compareTo(WatchDataBean o) {
        int no = o.getNo();
        return mNo - no;
    }
}
