package cn.net.aicare.modulelibrary.module.BroadcastScale;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import com.pinwang.ailinkble.AiLinkPwdUtil;

/**
 * xing<br>
 * 2020/08/12<br>
 * 广播秤
 */
public class BroadcastScaleDeviceData {
    private String TAG = BroadcastScaleDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private int mType = BroadcastScaleBleConfig.BROADCAST_SCALE;
    private static BroadcastScaleDeviceData sBroadcastScaleDeviceData = null;
    private int mOldNumberId = -1;
    private int mOldStatus = -1;

    public static BroadcastScaleDeviceData getInstance() {
        if (sBroadcastScaleDeviceData == null) {
            synchronized (BroadcastScaleDeviceData.class) {
                if (sBroadcastScaleDeviceData == null) {
                    sBroadcastScaleDeviceData = new BroadcastScaleDeviceData();
                }
            }
        }
        return sBroadcastScaleDeviceData;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (sBroadcastScaleDeviceData != null) {
            mOnNotifyData = null;
            sBroadcastScaleDeviceData = null;
        }
    }


    private BroadcastScaleDeviceData() {
        init();
    }

    private void init() {
    }


    //-------------------接收-----------------

    /**
     * @param manufacturerData 自定义厂商数据0xFF后面的数据
     * @param cid              cid 设备类型
     * @param vid              vid
     * @param pid              pid
     */
    public void onNotifyData(byte[] manufacturerData, int cid, int vid, int pid) {
        if (mType == cid|| BroadcastScaleBleConfig.BROADCAST_SCALE_LING_YANG_CID==cid) {
            if (manufacturerData == null) {
                BleLog.i(TAG, "接收到的数据:null");
                return;
            }
            if (manufacturerData.length >= 20) {
                byte sum = manufacturerData[9];
                byte[] data = new byte[10];
                System.arraycopy(manufacturerData, 10, data, 0, data.length);
                byte newSum = cmdSum(data);
                if (newSum == sum) {
                    BleLog.i(TAG, "接收到的数据:原始数据:"+BleStrUtils.byte2HexStr(data));
                    byte[] bytes;
                    byte[] dataOriginal=data.clone();
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

                    BleLog.i(TAG, "接收到的数据:" + BleStrUtils.byte2HexStr(bytes));
                    if (bytes.length>1){
                        int numberId = bytes[0] & 0xff;//数据ID
                        runOnMainThread(() -> {
                            if (mOnNotifyData != null) {
                                mOnNotifyData.onData(dataOriginal,bytes,numberId);
                            }
                        });
                    }
                    dataCheck(bytes,cid,vid,pid);
                } else {
                    BleLog.i("校验和错误");
                }
            }
        }
    }


    //----------------解析数据------


    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheck(byte[] data,int cid, int vid, int pid) {
        if (data == null)
            return;


        if (data.length >= 9) {
            int numberId = data[0] & 0xff;//数据ID
            //0x00 ：开始测试
            //0x00 ：正在测量体重 （此时阻抗数值为 0）
            //0x01 ：正在测量阻抗（此时阻抗数值为 0）
            //0x02 ：阻抗测量成功
            //0x03 ：阻抗测量失败（此时阻抗数值为 0xFFFF）
            //0xFF ：测试结束
            int status = data[1] & 0xff;
            if (mOldNumberId == numberId&&mOldStatus==status) {
                //数据相同,已处理过了.不再处理
                return;
            }
            mOldNumberId = numberId;
            mOldStatus = status;


            int tempUnit = BleStrUtils.getBits(data[2], 7, 1);//温度单位 0=℃ ，1=℉

            //0000：kg
            //0001：斤
            //0100：st:lb
            //0110：lb
            int weightUnit = BleStrUtils.getBits(data[2], 3, 4);

            //00 ：无小数点
            //01 : 1 个小数点
            //10 ：2 个小数点
            //11 ：3 个小数点
            int weightDecimal = BleStrUtils.getBits(data[2], 1, 2);

            //0：实时重量，1：稳定重量
            int weightStatus = BleStrUtils.getBits(data[2], 0, 1);

            //最高位 =0 ：正重量
            //最高位 = 1 ：负重量
            int weightNegative = BleStrUtils.getBits(data[3], 7, 1);

            //体重，大端序
            int weight = ((data[3] & 0x7f) << 8) | (data[4] & 0xff);

            //阻抗，大端序
            int adc = ((data[5] & 0xff) << 8) + (data[6] & 0xff);

            //体脂秤算法 ID 编号
            int algorithmId = data[7] & 0xff;

            //最高位 = 0 ：正温度
            //最高位 = 1 ：负单位
            int tempNegative = BleStrUtils.getBits(data[8], 7, 1);
            int temp = ((data[8] & 0x7f) << 8) + (data[9] & 0xff);
            if (tempNegative == 1 && temp == 32767) {
                //无温度测量，则该值为 0xFFFF
                tempNegative = -1;
                temp = 65535;
            }
            int finalTempNegative = tempNegative;
            int finalTemp = temp;

            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.OnDID(cid, vid, pid);
                }
            });

            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getWeightData(status, tempUnit, weightUnit, weightDecimal, weightStatus, weightNegative, weight, adc, algorithmId, finalTempNegative, finalTemp);

                }
            });
        }

    }


    //----------------解析数据------


    public interface onNotifyData {
        /**
         * 不能识别的透传数据
         * Unrecognized pass-through data
         * @param dataOriginal 原始数据
         * @param data 解密后的数据
         * @param type 数据ID
         */
        default void onData(byte[] dataOriginal,byte[] data, int type) {
        }

        /**
         * 体重数据(Stabilize weight)
         *
         * @param status         0x00 ：开始测试
         *                       0x00 ：正在测量体重 （此时阻抗数值为 0）
         *                       0x01 ：正在测量阻抗（此时阻抗数值为 0）
         *                       0x02 ：阻抗测量成功
         *                       0x03 ：阻抗测量失败（此时阻抗数值为 0xFFFF）
         *                       0xFF ：测试结束
         * @param tempUnit       温度单位 0=℃ ，1=℉
         * @param weightUnit     体重单位
         * @param weightDecimal  体重小数点
         * @param weightStatus   0：实时重量，1：稳定重量
         * @param weightNegative 0 ：正重量; 1 ：负重量
         * @param weight         原始数据(Raw data)
         * @param adc            阻抗 65535表示测量阻抗失败
         * @param algorithmId    算法id
         * @param tempNegative   0 ：正温度;1 ：负单位 ;-1代表不支持
         * @param temp           温度值,精度 0.1 ;-1代表不支持
         */
        default void getWeightData(int status, int tempUnit, int weightUnit, int weightDecimal, int weightStatus, int weightNegative, int weight, int adc, int algorithmId, int tempNegative,
                                   int temp) {
        }

        default void OnDID(int cid, int vid, int pid) {
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


    //-----------------set/get-----------------
    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
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
