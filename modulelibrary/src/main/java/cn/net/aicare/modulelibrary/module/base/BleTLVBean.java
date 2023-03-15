package cn.net.aicare.modulelibrary.module.base;

import java.util.Arrays;

/**
 * @author xing<br>
 * @date 2022/11/14<br>
 * 数据bean
 */
public class BleTLVBean {

    private byte mType;

    /**
     * 长度,为0时可以省略
     */
    private int mLength;
    private byte[] mValue;

    public BleTLVBean(byte type, byte[] value) {
        mType = type;
        if (mValue != null) {
            mLength = mValue.length;
        }
        mValue = value;
    }

    public BleTLVBean() {
    }


    public byte getType() {
        return mType;
    }

    public void setType(byte type) {
        mType = type;
    }

    public void setType(int type) {
        setType((byte) type);
    }

    public int getLength() {
        return mLength;
    }

    public byte[] getValue() {
        return mValue;
    }

    public void setValue(byte[] value) {
        mValue = value;
        if (mValue != null) {
            mLength = mValue.length;
        }
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[1 + 1 + mValue.length];
        bytes[0] = mType;
        bytes[1] = (byte) mLength;
        System.arraycopy(mValue, 0, bytes, 2, mLength);
        return bytes;
    }

    @Override
    public String toString() {
        return "BleNoiseTLVBean{ mType=" + mType + ", mLength=" + mLength + ", mValue=" + Arrays.toString(mValue) + '}';
    }
}
