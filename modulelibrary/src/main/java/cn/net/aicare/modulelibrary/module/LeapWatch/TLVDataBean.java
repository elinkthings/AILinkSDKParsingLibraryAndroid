package cn.net.aicare.modulelibrary.module.LeapWatch;

import java.util.Arrays;

/**
 * xing<br>
 * 2022/8/3<br>
 * TLV数据模型
 */
public class TLVDataBean {

    private byte[] mHex;
    private byte[] mTypeB;
    private int mLength;
    private byte[] mValue;

    public TLVDataBean() {
    }


    public TLVDataBean(byte[] hex, byte[] typeB, int length, byte[] value) {
        mHex = hex;
        mTypeB = typeB;
        mLength = length;
        mValue = value;
    }


    public byte[] getTypeB() {
        return mTypeB;
    }

    public void setTypeB(byte[] typeB) {
        mTypeB = typeB;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public byte[] getValue() {
        return mValue;
    }

    public void setValue(byte[] value) {
        mValue = value;
    }

    public byte[] getHex() {
        return mHex;
    }

    public void setHex(byte[] hex) {
        mHex = hex;
    }

    @Override
    public String toString() {
        return "TLVDataBean{" + "mHex=" + Arrays.toString(mHex) + ", mType=" + mTypeB + ", mLength=" + mLength + ", mValue=" + Arrays.toString(mValue) + '}';
    }

    @Override
    public boolean equals( Object obj) {
        if (obj instanceof TLVDataBean) {
            byte[] typeB = ((TLVDataBean) obj).getTypeB();
            int type = ((typeB[0] & 0xFF) << 8) + (typeB[1] & 0xFF);
            int typeOld = ((mTypeB[0] & 0xFF) << 8) + (mTypeB[1] & 0xFF);
            return (typeOld == type);
        }
        return super.equals(obj);
    }


    public boolean add(TLVDataBean bean) {
        byte[] typeB = bean.getTypeB();
        int type = ((typeB[0] & 0xFF) << 8) + (typeB[1] & 0xFF);
        int typeOld = ((mTypeB[0] & 0xFF) << 8) + (mTypeB[1] & 0xFF);
        if (typeOld == type) {
            byte[] value = bean.getValue();
            String data1 = Arrays.toString(value);
            String data2 = Arrays.toString(mValue);
            if (!data1.equalsIgnoreCase(data2)) {
                int length = bean.getLength();
                byte[] bytes = new byte[mLength + length];
                System.arraycopy(mValue, 0, bytes, 0, mLength);
                System.arraycopy(value, 0, bytes, mLength, length);
                setValue(bytes);
                setLength(bytes.length);
                return true;
            }
        }
        return false;
    }

}
