package cn.net.aicare.modulelibrary.module.NoiseMeter;

import cn.net.aicare.modulelibrary.module.base.BleTLVBean;

/**
 * @author xing<br>
 * @date 2022/11/14<br>
 * 噪音计协议数据bean
 */
public class BleNoiseTLVBean extends BleTLVBean {
    /**
     * 代表协议中的CMD（例：设置/获取参数功能）
     */
    private int mCmd;
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

    public BleNoiseTLVBean() {
    }

    public BleNoiseTLVBean(int cmd, byte type, byte[] value) {
        super(type,  value);
        mCmd = cmd;
    }
}
