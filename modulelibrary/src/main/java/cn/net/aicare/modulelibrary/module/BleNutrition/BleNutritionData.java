package cn.net.aicare.modulelibrary.module.BleNutrition;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBlePublicHistoryListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 蓝牙营养秤
 */
public class BleNutritionData extends BaseBleDeviceData implements OnBleVersionListener, OnBlePublicHistoryListener {

    private static final int CID = BleNutritionConfig.BLE_NUTRITION;
    private static final int HISTORY_TIME_OUT_KEY = 1;
    /**
     * 历史超时
     */
    private static final int HISTORY_TIME_OUT = 5 * 1000;
    private int mHistoryTimeoutCount = 0;
    private List<BleNutritionHistoryBean> mHistoryBeanList = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HISTORY_TIME_OUT_KEY) {
                mHistoryTimeoutCount++;
                if (mHistoryTimeoutCount >= 3) {
                    if (mBleNutritionCallback != null) {
                        mBleNutritionCallback.onHistoryRecord(100, new ArrayList<>(mHistoryBeanList));
                    }
                    mHistoryBeanList.clear();
                    mHistoryTimeoutCount = 0;
                } else {
                    long time = 0;
                    if (mHistoryBeanList.size() > 0) {
                        BleNutritionHistoryBean bleNutritionHistoryBean = mHistoryBeanList.get(0);
                        time = bleNutritionHistoryBean.getTime();
                    }
                    getHistoryData(time);
                }
            }
        }
    };

    public BleNutritionData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(this);
        bleDevice.setOnBlePublicHistoryListener(this);
        // 连上后主动获取支持单位
        bleDevice.sendData(new SendBleBean(BleSendCmdUtil.getInstance().getSupportUnit()));
        appSynTime();
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == CID) {
            switch (hex[0]) {
                case BleNutritionConfig.MCU_WEIGHT:
                    mcuWeight(hex);
                    break;
                case BleNutritionConfig.MCU_UNIT_RESULT:
                    mcuUnitResult(hex);
                    break;
                case BleNutritionConfig.MCU_ERR:
                    mcuErr(hex);
                    break;
            }
        }
    }

    @Override
    public void onBmVersion(String version) {
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuBmVersion(version);
        }
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuSupportUnit(list);
        }
    }


    @Override
    public void onHistoryRecord(long totalSize, long currentSize, byte[] data) {
        mHandler.removeMessages(HISTORY_TIME_OUT_KEY);
        if (currentSize != 0) {
            parseHistoryData((int) currentSize, data);
        }
        if (totalSize == currentSize || 0 == totalSize || currentSize == 0) {
            mHistoryTimeoutCount = 0;
            if (mBleNutritionCallback != null) {
                if (mHistoryBeanList.isEmpty()) {
                    mBleNutritionCallback.onHistoryRecord(100, new ArrayList<>());
                } else {
                    mBleNutritionCallback.onHistoryRecord(100, new ArrayList<>(mHistoryBeanList));
                }
                mHistoryBeanList.clear();
            }
        } else {
            long time = 0;
            if (!mHistoryBeanList.isEmpty()) {
                Collections.sort(mHistoryBeanList, (o1, o2) -> (int) (o1.getTime() - o2.getTime()));
                BleNutritionHistoryBean bleNutritionHistoryBean = mHistoryBeanList.get(mHistoryBeanList.size() - 1);
                time = bleNutritionHistoryBean.getTime();
            }
            if (mBleNutritionCallback != null) {
                int schedule = (int) (currentSize * 100 / totalSize);
                mBleNutritionCallback.onHistoryRecord(schedule, new ArrayList<>(mHistoryBeanList));
            }
            getHistoryData(time);
            mHandler.sendEmptyMessageDelayed(HISTORY_TIME_OUT_KEY, HISTORY_TIME_OUT);
        }
    }


    //解析历史记录
    public void parseHistoryData(int currentSize, byte[] data) {
        int len = data.length / currentSize;
        for (int i = 0; i < data.length; i += len) {
            byte[] hex = new byte[len];
            System.arraycopy(data, i, hex, 0, len);
            BleNutritionHistoryBean bleNutritionHistoryBean = getHistoryBean(hex);
            mHistoryBeanList.add(bleNutritionHistoryBean);
        }
    }

    private BleNutritionHistoryBean getHistoryBean(byte[] hex) {

        long time = ((hex[3] & 0xFFL) << 24) | ((hex[2] & 0xFFL) << 16) | ((hex[1] & 0xFFL) << 8) | (hex[0] & 0xFFL);
        int weight = ((hex[4] & 0xFF) << 16) | ((hex[5] & 0xFF) << 8) | ((hex[6] & 0xFF));
        // 0x00: g；0x01: ml；0x02: lb：oz；0x03: oz；0x04 : kg；0x05: 斤；0x06: 牛奶ml；0x07: 水ml；0x08: 牛奶floz；0x09: 水floz；0x0A: lb
        int unit = hex[7] & 0xFF;
        //小数点位
        int decimal = hex[8] & 0xFF;
        //符号；0：正；1：负
        int symbol = hex[9] & 0xFF;
        return new BleNutritionHistoryBean(time, weight, unit, decimal, symbol);
    }


    @Override
    public void onClearHistoryRecord(int status) {
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.onClearHistoryRecord(status);
            if (status == 0x00) {
                mHistoryTimeoutCount = 0;
                mHistoryBeanList.clear();
            }
        }
    }


    /**
     * MCU 上报重量
     */
    private void mcuWeight(byte[] hex) {
        int no = hex[1] & 0xFF;// 流水号
        int weight = ((hex[2] & 0xFF) << 16) + ((hex[3] & 0xFF) << 8) + ((hex[4] & 0xFF));
        int unit = hex[5] & 0xFF;
        int decimal = hex[6] & 0xFF;
        int symbol = hex[7] & 0xFF;
        int type = hex[8] & 0xFF;
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuWeight(no, weight, unit, decimal, symbol, type);
        }
    }

    /**
     * MCU 上报设置单位结果
     */
    private void mcuUnitResult(byte[] hex) {
        int status = hex[1] & 0xFF;
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuUnitResult(status);
        }
    }

    /**
     * MCU 上报异常报警
     */
    private void mcuErr(byte[] hex) {
        int weightErr = (hex[1] & 0xFF) & 0x01;
        int batteryErr = ((hex[1] & 0xFF) >> 1) & 0x01;
        if (mBleNutritionCallback != null) {
            mBleNutritionCallback.mcuErr(weightErr, batteryErr);
        }
    }

    /**
     * APP 下发单位
     */
    public void setUnit(int unit) {
        byte[] hex = new byte[2];
        hex[0] = BleNutritionConfig.SET_UNIT;
        hex[1] = (byte) unit;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 下发归零
     */
    public void setZero() {
        byte[] hex = new byte[2];
        hex[0] = BleNutritionConfig.SET_ZERO;
        hex[1] = 0x01;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * APP 下发蜂鸣器开关
     */
    public void setBuzz(boolean open) {
        byte[] hex = new byte[2];
        hex[0] = BleNutritionConfig.SET_BUZZ;
        hex[1] = (byte) (open ? 0x00 : 0x01);
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 清除历史数据
     */
    public void clearHistoryData() {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().clearDeviceHistoryRecord());
        sendData(sendBleBean);
    }

    /**
     * 应用同步时间
     */
    public void appSynTime() {
        //设置APP 同步unix时间
        byte[] timeS = BleSendCmdUtil.getInstance().setDeviceTimeUnix((int) (System.currentTimeMillis() / 1000));
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(timeS);
        sendData(sendBleBean);
    }

    /**
     * 获取历史数据
     *
     * @param time 时间(seconds),0表示获取全部历史记录
     */
    public void getHistoryData(long time) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(BleSendCmdUtil.getInstance().getDeviceHistoryRecord(time));
        sendData(sendBleBean);
        mHandler.removeMessages(HISTORY_TIME_OUT_KEY);
        mHandler.sendEmptyMessageDelayed(HISTORY_TIME_OUT_KEY, HISTORY_TIME_OUT);
    }


    public interface BleNutritionCallback {

        /**
         * MCU 上报 BM 版本
         */
        void mcuBmVersion(String version);

        /**
         * MCU 上报设备支持的单位
         */
        void mcuSupportUnit(List<SupportUnitBean> list);

        /**
         * MCU 上报重量
         *
         * @param no      流水号。稳定重量不会变
         * @param weight  原始重量
         * @param unit    0x00: g；0x01: ml；0x02: lb：oz；0x03: oz；0x04 : kg；0x05: 斤；0x06: 牛奶ml；0x07: 水ml；0x08: 牛奶floz；0x09: 水floz；0x0A: lb
         * @param decimal 小数点位
         * @param symbol  符号；0：正；1：负
         * @param type    重量类型：1：实时；2：稳定
         */
        void mcuWeight(int no, int weight, int unit, int decimal, int symbol, int type);

        /**
         * MCU 上报设置单位结果
         *
         * @param status 0：成功；1：失败；2：不支持
         */
        void mcuUnitResult(int status);

        /**
         * MCU 上报异常
         *
         * @param weightErr  0：正常；1：超重
         * @param batteryErr 0：正常；1：低电
         */
        void mcuErr(int weightErr, int batteryErr);

        /**
         * 历史记录
         *
         * @param list 列表
         */
        void onHistoryRecord(int schedule, List<BleNutritionHistoryBean> list);

        /**
         * 有明确历史记录
         *
         * @param status 状态
         */
        void onClearHistoryRecord(int status);
    }

    private BleNutritionCallback mBleNutritionCallback;

    public void setBleNutritionCallback(BleNutritionCallback bleNutritionCallback) {
        mBleNutritionCallback = bleNutritionCallback;
    }
}
