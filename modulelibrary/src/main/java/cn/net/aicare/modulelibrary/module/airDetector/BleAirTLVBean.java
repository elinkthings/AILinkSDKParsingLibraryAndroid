package cn.net.aicare.modulelibrary.module.airDetector;

import java.util.Arrays;

import cn.net.aicare.modulelibrary.module.base.BleTLVBean;

/**
 * @author yesp
 * @date 2022/12/09
 *
 * 空气检测仪协议数据 bean
 */
public class BleAirTLVBean extends BleTLVBean {
    /**
     * 代表协议中的CMD（例：设置/获取参数功能）
     */
    private int mCmd;
    /**
     * 0x01:获取, 0x02:设置
     */
    private int mOp=-1;

    public int getCmd() {
        return mCmd;
    }

    public void setCmd(int cmd) {
        mCmd = cmd;
    }

    public int getOp() {
        return mOp;
    }

    public void setOp(int op) {
        mOp = op;
    }

    public BleAirTLVBean() {
    }

    public BleAirTLVBean(int cmd, byte type, byte[] value) {
        super(type, value);
        mCmd = cmd;
    }

    @Override
    public String toString() {
        return "BleAirTLVBean{" +
                "mCmd=" + mCmd +
                ", mOp=" + mOp +
                ", mType=" + getType() +
                ", mLength=" + getLength() +
                ", mValue=" + Arrays.toString(getValue()) +
                '}';
    }
}
