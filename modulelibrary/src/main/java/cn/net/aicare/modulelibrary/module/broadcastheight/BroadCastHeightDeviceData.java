package cn.net.aicare.modulelibrary.module.broadcastheight;


import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.utils.BleLog;
import com.pinwang.ailinkble.AiLinkPwdUtil;

import cn.net.aicare.modulelibrary.module.BroadcastScale.BroadcastScaleBleConfig;

/**
 * @author ljl
 * on 2023/6/14
 */
public class BroadCastHeightDeviceData {

    private int mType = BroadCastHeightConfig.BROAD_CAST_HEIGHT;
    private int mOldNumberId = -1;
    private static BroadCastHeightDeviceData mBroadCastHeightDeviceData;

    private OnNotifyHeightData mOnNotifyHeightData;

    public static BroadCastHeightDeviceData getInstance() {
        if (mBroadCastHeightDeviceData == null) {
            synchronized (BroadCastHeightDeviceData.class) {
                if (mBroadCastHeightDeviceData == null) {
                    mBroadCastHeightDeviceData = new BroadCastHeightDeviceData();
                }
            }
        }
        return mBroadCastHeightDeviceData;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mBroadCastHeightDeviceData != null) {
            mOnNotifyHeightData = null;
            mBroadCastHeightDeviceData = null;
        }
    }

    /**
     * @param manufacturerData 自定义厂商数据0xFF后面的数据
     * @param cid              cid 设备类型
     * @param vid              vid
     * @param pid              pid
     */
    public void onNotifyData(byte[] manufacturerData, int cid, int vid, int pid) {
        if (manufacturerData == null) {
            BleLog.i("接收到的数据:null");
            return;
        }

        if (manufacturerData.length >= 20) {
            byte sum = manufacturerData[9];
            byte[] data = new byte[10];
            System.arraycopy(manufacturerData, 10, data, 0, data.length);
            byte newSum = cmdSum(data);
            if (newSum == sum) {
                byte[] bytes;
                if (cid != 0 || vid != 0 || pid != 0) {
                    //数据需要解密
                    if (cid == BroadcastScaleBleConfig.BROADCAST_SCALE_LING_YANG_CID) {
                        bytes = AiLinkPwdUtil.decryptLingYang(data);
                    } else {
                        bytes = AiLinkPwdUtil.decryptBroadcast(cid, vid, pid, data);
                    }
                } else {
                    bytes = data;
                }
                checkData(bytes, cid, vid, pid);
            } else {
                BleLog.i("校验和错误");
            }
        }

    }

    /**
     * 校验累加,从1开始加
     */
    private static byte cmdSum(byte[] data) {
        byte sum = 0;
        for (byte datum : data) {
            sum += datum;
        }
        return sum;
    }

    /**
     * 解析数据
     *
     * @param hex 解密后的数据
     * @param cid cid
     * @param vid vid
     * @param pid pid
     */
    private void checkData(byte[] hex, int cid, int vid, int pid) {
        // 流水号
        int serialNum = hex[0];
        if (serialNum == mOldNumberId) {
            return;
        }
        mOldNumberId = serialNum;

        // 测量标识
        int flag = hex[1] & 0xff;
        // 身高原始数据
        int heightOrigin = (hex[2] & 0xff) << 8 | (hex[3] & 0xff);
        // 身高单位 0：cm；1：inch；2：ft-in
        int heightUnit = (hex[4] & 0xff) & 0x0f;
        // 身高小数点
        int heightDecimal = ((hex[4] & 0xff) & 0xf0) >> 4;
        // 体重原始数据
        int weightOrigin = (hex[5] & 0xff) << 8 | (hex[6] & 0xff);
        // 体重单位 0：kg；1：斤；2：lb:oz；3：oz；4：st:lb；5：g；6：lb
        int weightUnit = (hex[7] & 0xff) & 0x0f;
        // 体重正负号
        int weightSymbol = ((hex[7] & 0xff) & 0x10) >> 4;
        // 体重小数点
        int weightDecimal = ((hex[7] & 0xff) & 0xe0) >> 5;
        // 电量
        int battery = hex[8] & 0xff;

        runOnMainThread(() -> {
            if (mOnNotifyHeightData != null) {
                mOnNotifyHeightData.notifyData(hex, flag, heightOrigin, heightUnit, heightDecimal,
                        weightOrigin, weightUnit, weightSymbol, weightDecimal, battery);
            }
        });


    }


    public interface OnNotifyHeightData {

        /**
         *
         * @param hex 解析后的数据
         * @param flag 测量标识
         * @param heightOrigin 身高原始数据
         * @param heightUnit 身高单位
         * @param heightDecimal 身高小数点
         * @param weightOrigin 体重原始数据
         * @param weightUnit 体重单位
         * @param weightSymbol 体重正负号
         * @param weightDecimal 体重小数点
         * @param battery 体重最终值
         */
        void notifyData(byte[] hex, int flag, int heightOrigin, int heightUnit, int heightDecimal,
                        int weightOrigin, int weightUnit, int weightSymbol, int weightDecimal, int battery);
    }


    public void setOnNotifyHeightData(OnNotifyHeightData onNotifyHeightData) {
        mOnNotifyHeightData = onNotifyHeightData;
    }


    private Handler threadHandler = new Handler(Looper.getMainLooper());

    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
