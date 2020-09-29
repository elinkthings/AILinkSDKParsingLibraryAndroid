package cn.net.aicare.modulelibrary.module.BloodOxygen;

import android.os.Handler;
import android.os.Looper;

import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import com.pinwang.ailinkble.AiLinkPwdUtil;

/**
 * xing<br>
 * 2020/09/09<br>
 * 血氧仪
 */
public class BroadcastBloodOxygenDeviceData {
    private String TAG = BroadcastBloodOxygenDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private int mType = BroadcastBloodOxygenBleConfig.BROADCAST_BLOOD_OXYGEN;
    private static BroadcastBloodOxygenDeviceData sBroadcastBloodOxygenDeviceData = null;
    private int mOldNumberId = -1;

    public static BroadcastBloodOxygenDeviceData getInstance() {
        if (sBroadcastBloodOxygenDeviceData == null) {
            synchronized (BroadcastBloodOxygenDeviceData.class) {
                if (sBroadcastBloodOxygenDeviceData == null) {
                    sBroadcastBloodOxygenDeviceData = new BroadcastBloodOxygenDeviceData();
                }
            }
        }
        return sBroadcastBloodOxygenDeviceData;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (sBroadcastBloodOxygenDeviceData != null) {
            mOnNotifyData = null;
            sBroadcastBloodOxygenDeviceData = null;
        }
    }


    private BroadcastBloodOxygenDeviceData() {
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
        if (mType == cid) {
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
                    int numberId = data[0] & 0xff;//数据ID
                    if (mOldNumberId == numberId) {
                        //数据相同,已处理过了.不再处理
                        return;
                    }
                    mOldNumberId = numberId;
                    byte[] bytes;
                    if (cid != 0 || vid != 0 || pid != 0) {
                        bytes = AiLinkPwdUtil.decryptBroadcast(cid, vid, pid, data);
                    } else {
                        bytes = data;
                    }
                    runOnMainThread(()->{
                        if (mOnNotifyData!=null){
                            mOnNotifyData.OnDID(cid,vid,pid);
                        }
                    });
                    BleLog.i(TAG, "接收到的数据:" + BleStrUtils.byte2HexStr(bytes));
                    dataCheck(bytes);
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
    private void dataCheck(byte[] data) {
        if (data == null)
            return;


        if (data.length >= 9) {
            int numberId = data[0] & 0xff;//数据ID

            //0x00 ：开始测试
            //0x01 ：正在测试
            //0xFF ：测试结束
            int status = data[1] & 0xff;

            //0-100 %
            // 若该值无效，则为 0xFF
            int bloodOxygen = data[2] & 0xff;//血氧浓度百分比（SpO2 ，单位 %）

            //0-254
            //若该值无效，则为 0xFF
            int pulseRate =  data[3] & 0xff;//脉率（pulse rate ，单位 bpm）

            //
            int  plethysmogram =  data[4] & 0xff;//PI （ Plethysmogram 无单位 ，1 位小数 ）

            int power = data[5] & 0xff;//电量（power ，单位%）


            runOnMainThread(() -> {
                if (mOnNotifyData != null) {
                    mOnNotifyData.getBloodOxygenData(status, bloodOxygen, pulseRate, plethysmogram, power);
                }
            });
        }

    }


    //----------------解析数据------


    public interface onNotifyData {
        /**
         * 血氧数据(Blood oxygen data)
         *
         * @param status         0x00 ：开始测试
         *                       0x01 ：正在测试
         *                       0xFF ：测试结束
         * @param bloodOxygen    血氧百分比
         * @param pulseRate      脉率（pulse rate ，单位 bpm）
         * @param plethysmogram  PI
         * @param power 电量百分比
         */
        default void getBloodOxygenData(int status, int bloodOxygen, int pulseRate, int plethysmogram, int power) {
        }

        default void OnDID(int cid, int vid, int pid)  {
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
