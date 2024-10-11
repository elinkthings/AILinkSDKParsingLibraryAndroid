package cn.net.aicare.modulelibrary.module.thermometer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.listener.OnSettingUnit;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * xing<br>
 * 2019/5/11<br>
 * 体温计
 */
public class TempDeviceData extends BaseBleDeviceData implements OnBleOtherDataListener {
    private String TAG = TempDeviceData.class.getName();

    private onNotifyData mOnNotifyData;
    private OnSettingUnit mOnSettingUnit;
    private int mType = 0x03;
    private byte[] CID;
    private static BleDevice mBleDevice = null;
    private static TempDeviceData mDevice = null;

    public static TempDeviceData getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (mDevice == null) {
                    mDevice = new TempDeviceData(bleDevice);

                }
            } else {
                mDevice = new TempDeviceData(bleDevice);
            }
        }
        return mDevice;
    }

    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (mDevice != null) {
            mOnNotifyData = null;
            mDevice = null;
        }
    }


    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private TempDeviceData(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = bleDevice;
        bleDevice.setOnBleOtherDataListener(this);
        bleDevice.setMtu(517);
        init();
    }


    private void init() {
        //TODO 可进行额温枪特有的初始化操作
        CID = new byte[2];
        CID[0] = (byte) ((mType >> 8) & 0xff);
        CID[1] = (byte) (mType);
    }


    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (mType == type) {
            BleLog.i(TAG, "接收到的原始数据:" + Arrays.toString(hex));
            String data = BleStrUtils.byte2HexStr(hex);
            BleLog.i(TAG, "接收到的数据:" + data);
            //解析数据
            dataCheck(hex);
        }

    }

    @Override
    public void onNotifyDataA6(byte[] hex) {
        if (hex != null && hex.length > 0) {
            switch (hex[0] & 0xFF) {
                case 0x44:
                    // 设备回复设置 Unix 时间戳结果
                    int status = hex[1] & 0xFF;
                    mOnNotifyData.mcuSetUnixStamp(status);
                    break;
            }
        }
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] data) {
        BleLog.e(TAG,"onNotifyOtherData: " + BleStrUtils.byte2HexStr(data));
    }

    //---------------解析数据------

    /**
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        switch (data[0] & 0xFF) {

            case TempBleConfig.TEMP:
                temp(data);
                break;
            case TempBleConfig.TEMP_NOW:
                tempNow(data);
                break;
            case TempBleConfig.GET_UNIT:
                getUnit(data);
                break;
            case TempBleConfig.GET_ERR:
                getErr(data);
                break;
            case 0x04:
                // 设备回复历史记录
                mcuHistory(data);
                break;
            case 0x84:
                // 设备回复测温模式
                mcuGetMode(data);
                break;
            case 0x85:
                // 设备回复设置测温模式结果
                mcuSetMode(data);
                break;
            case 0x86:
                // 设备回复高温报警值
                mcuGetTemp(data);
                break;
            case 0x87:
                // 设备回复设置高温报警值结果
                mcuSetTemp(data);
                break;
            case 0x11:
                //新版设备回复历史记录
                parseHistory(data);


                break;
            default:
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnNotifyData != null) {
                            mOnNotifyData.onData(data, mType);
                        }
                    }
                });
                break;
        }

    }

    private void tempNow(byte[] data) {
        int temp = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];

        String unitStr = "℃";
        if (unit == 1) {
            unitStr = "℉";
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.tempNow(temp, decimal, unit);
                }
            }
        });
        BleLog.i(TAG, "当前温度:" + temp + unitStr);
    }

    private void temp(byte[] data) {
        //APP回复
        replayTemp();
        //解析稳定的稳定数据
        int temp = ((data[1] & 0xff) << 8) + (data[2] & 0xff);
        byte unit = data[3];
        int decimal = data[4];

        String unitStr = "℃";
        if (unit == 1) {
            unitStr = "℉";
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mOnNotifyData != null) {
                    mOnNotifyData.temp(temp, decimal, unit);
                }
            }
        });
        BleLog.i(TAG, "当前温度:" + temp + unitStr);
    }

    public void setUnit(byte unit) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = TempBleConfig.SET_UNIT;
        data[1] = unit;
        sendMcuBean.setHex(CID, data);
        sendData(sendMcuBean);
    }

    private void getUnit(byte[] data) {
        //todo 获取设置的单位
        if (data.length > 1) {
            final byte status = data[1];
            switch (status) {
                case CmdConfig.SETTING_SUCCESS:
                    BleLog.i(TAG, "设置单位成功");
                    break;
                case CmdConfig.SETTING_FAILURE:
                    BleLog.i(TAG, "设置单位失败");

                    break;
                case CmdConfig.SETTING_ERR:
                    BleLog.i(TAG, "设置单位错误");

                    break;
            }
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnNotifyData != null) {
                        mOnNotifyData.getUnit(status);
                    }
                }
            });
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnSettingUnit != null) {
                        mOnSettingUnit.getUnit(status);
                    }
                }
            });
        }
    }

    /**
     * 0：体温过高
     * 1：体温过低
     * 2：测量出错
     */
    private void getErr(byte[] data) {
        final byte status = data[1];
        String statusStr = "体温过高";
        switch (status) {
            case 0:
                statusStr = "体温过高";
                break;
            case 1:
                statusStr = "体温过低";
                break;
            case 2:
                statusStr = "测量出错";
                break;
        }

        BleLog.i(TAG, statusStr);
        if (mOnNotifyData != null) {
            mOnNotifyData.getErr(status);
        }
    }

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (!status) {
            disconnect();
        }
        BleLog.i(TAG, "握手:" + status);
    }

    private void mcuHistory(byte[] hex) {
        Log.i(TAG, "mcuHistory：" + BleStrUtils.byte2HexStr(hex));
        int maxSize = ((hex[1] & 0xFF)) + ((hex[2] & 0xFF) << 8) + ((hex[3] & 0xFF) << 16) + ((hex[4] & 0xFF) << 24);
        int curSize = ((hex[5] & 0xFF)) + ((hex[6] & 0xFF) << 8) + ((hex[7] & 0xFF) << 16) + ((hex[8] & 0xFF) << 24);

        List<HistoryBean> list = new ArrayList<>();
        for (int i = 1; i <= curSize; i++) {
            int stamp = ((hex[i * 8 + 1] & 0xFF)) + ((hex[i * 8 + 2] & 0xFF) << 8) + ((hex[i * 8 + 3] & 0xFF) << 16) + ((hex[i * 8 + 4] & 0xFF) << 24);
            int temp = ((hex[i * 8 + 5] & 0xFF) << 8) + ((hex[i * 8 + 6] & 0xFF));
            int unit = (hex[i * 8 + 7] & 0xFF);
            int decimal = (hex[i * 8 + 8] & 0xFF);
            list.add(new HistoryBean(stamp, temp, unit, decimal));
        }
        mOnNotifyData.offlineData(maxSize, curSize, list);
    }

    private void mcuGetMode(byte[] hex) {
        int mode = hex[1] & 0xFF;
        mOnNotifyData.mcuGetMode(mode);
    }

    private void mcuSetMode(byte[] hex) {
        int status = hex[1] & 0xFF;
        mOnNotifyData.mcuSetMode(status);
    }

    private void mcuGetTemp(byte[] hex) {
        int temp = ((hex[1] & 0xFF) << 8) + (hex[2] & 0xFF);
        int unit = hex[3] & 0xFF;
        int decimal = hex[4] & 0xFF;
        mOnNotifyData.mcuGetTemp(temp, unit, decimal);
    }

    private void mcuSetTemp(byte[] hex) {
        int status = hex[1] & 0xFF;
        mOnNotifyData.mcuSetTemp(status);
    }


    /**
     * 解析历史
     *
     * @param data 数据
     */
    private void parseHistory(byte[] data) {
        // 设备回复历史记录 有两种格式的历史记录 unix时间格式和北京时间格式
        int historyL = data.length - 5;

        if ((historyL) % 8 == 0) {
            int num = historyL / 8;
            //unix时间格式的数据
            //历史数据总量 小端序
            int totalNum = (data[2] & 0xFF) + ((data[1] & 0xFF) << 8);
            //已发数量 小端序
            int sendNum = (data[4] & 0xFF) + ((data[3] & 0xFF) << 8);
            List<HistoryBean> list = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                //unix
                int unixTime = (data[5 + i * 8] & 0xFF) | ((data[6 + i * 8] & 0xFF) << 8) | ((data[7 + i * 8] & 0xFF) << 16) | ((data[8 + i * 8] & 0xFF) << 24);
                //unix 时间戳
                long unixTimeStamp = unixTime * 1000L;
                //温度原始数据
                int temp = ((data[9 + i * 8] & 0xff) << 8) + (data[10 + i * 8] & 0xff);
                //温度单位 0-℃ 1-℉
                int unit = data[11 + i * 8] & 0xff;
                //小数点
                int decimal = data[12 + i * 8] & 0xff;
                list.add(new HistoryBean(unixTimeStamp, temp, unit, decimal));
            }
            runOnMainThread(()->{
                mOnNotifyData.offlineData(totalNum, sendNum, list);
            });
        } else if ((historyL) % 11 == 0) {
            int num = historyL / 8;
            //北京时间格式的数据
            //历史数据总量 小端序
            int totalNum = (data[2] & 0xFF) + ((data[1] & 0xFF) << 8);
            //已发数量 小端序
            int sendNum = (data[4] & 0xFF) + ((data[3] & 0xFF) << 8);
            List<HistoryBean> list = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                //7个字节 年月日时分秒星期
                int year = (data[5 + i * 11] & 0xff) + 2000;
                int month = data[6 + i * 11] & 0xff;
                int day = data[7 + i * 11] & 0xff;
                int hour = data[8 + i * 11] & 0xff;
                int minute = data[9 + i * 11] & 0xff;
                int second = data[10 + i * 11] & 0xff;
                int week = data[11 + i * 11] & 0xff;

                long unixTimeStamp = -1L;
                //将年月日时分秒转换为时间戳
                String beijingTime = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day)
                        + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date parse = simpleDateFormat.parse(beijingTime);
                    unixTimeStamp = parse.getTime();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                //温度原始数据
                int temp = ((data[12 + i * 8] & 0xff) << 8) + (data[13 + i * 8] & 0xff);
                //温度单位 0-℃ 1-℉
                int unit = data[14 + i * 8] & 0xff;
                //小数点
                int decimal = data[15 + i * 8] & 0xff;
                list.add(new HistoryBean(unixTimeStamp, temp, unit, decimal));
            }
            runOnMainThread(()->{
                mOnNotifyData.offlineData(totalNum, sendNum, list);
            });
        }
    }


    /**
     * 获取历史记录
     *
     * @param size
     * @param stamp 秒
     */
    public void getHistory(int size, int stamp) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[6];
        hex[0] = (byte) 0x03;
        hex[1] = (byte) size;

        byte[] intToByteLittle = getIntToByteLittle(stamp);
        hex[2] = (byte) intToByteLittle[0];
        hex[3] = (byte) intToByteLittle[1];
        hex[4] = (byte) intToByteLittle[2];
        hex[5] = (byte) intToByteLittle[3];
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 获取历史记录 1.3协议版本
     *
     * @param op 0x00-开始获取历史记录 0x01-接收到一帧，请发下一帧 0x02-数据接收完毕 0x03-删除历史记录
     */
    public void getHistoryNew(int op) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[6];
        hex[0] = (byte) 0x10;
        hex[1] = (byte) op;
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 获取设备测温模式
     */
    public void getMode() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[2];
        hex[0] = (byte) 0x84;
        hex[1] = (byte) 0x01;
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 设置设备测温模式
     *
     * @param mode
     */
    public void setMode(int mode) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[2];
        hex[0] = (byte) 0x85;
        hex[1] = (byte) mode;
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 获取高温报警值
     */
    public void getTemp() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[2];
        hex[0] = (byte) 0x86;
        hex[1] = (byte) 0x01;
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 设置高温报警值
     *
     * @param tempStr
     * @param unit
     */
    public void setTemp(String tempStr, int unit) {
        int temp = 0;
        int decimal = 0;

        if (tempStr.contains(".")) {
            // 有小数点
            String[] split = tempStr.split("\\.");
            decimal = split[1].length();
            temp = Integer.parseInt(tempStr.replace(".", ""));
        } else {
            // 没有小数点
            decimal = 0;
            temp = Integer.parseInt(tempStr);
        }

        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[5];
        hex[0] = (byte) 0x87;
        hex[1] = (byte) (temp >> 8);
        hex[2] = (byte) (temp & 0xFF);
        hex[3] = (byte) unit;
        hex[4] = (byte) decimal;
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 设置 Unix 时间戳
     *
     * @param stamp 秒
     */
    public void setUnixStamp(int stamp) {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] hex = new byte[5];
        hex[0] = (byte) 0x44;
        byte[] intToByteLittle = getIntToByteLittle(stamp);
        hex[1] = (byte) intToByteLittle[0];
        hex[2] = (byte) intToByteLittle[1];
        hex[3] = (byte) intToByteLittle[2];
        hex[4] = (byte) intToByteLittle[3];
        sendBleBean.setHex(hex);
        sendData(sendBleBean);
    }


    /**
     * 设置 Unix 时间戳 1.3协议版本
     *
     * @param stamp ms
     */
    public void setUnixStampNew(long stamp) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[5];
        hex[0] = (byte) 0x83;
        int  second= (int) (stamp / 1000);
        byte[] intToByteLittle = getIntToByteLittle(second);
        hex[1] = (byte) intToByteLittle[3];
        hex[2] = (byte) intToByteLittle[2];
        hex[3] = (byte) intToByteLittle[1];
        hex[4] = (byte) intToByteLittle[0];
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 同步北京时间
     */
    public void setUTC8Time() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[8];
        hex[0] = (byte) 0x84;

        TimeZone beijingTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
        Calendar beijingCalendar = Calendar.getInstance(beijingTimeZone);
        int year = beijingCalendar.get(Calendar.YEAR) - 2000;
        int month = beijingCalendar.get(Calendar.MONTH) + 1; // 注意月份是从0开始计数的，所以需要+1
        int day = beijingCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = beijingCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = beijingCalendar.get(Calendar.MINUTE);
        int second = beijingCalendar.get(Calendar.SECOND);
        int week = getDayOfWeek(beijingCalendar.get(Calendar.DAY_OF_WEEK));

        hex[1] = (byte) year;
        hex[2] = (byte) month;
        hex[3] = (byte) day;
        hex[4] = (byte) hour;
        hex[5] = (byte) minute;
        hex[6] = (byte) second;
        hex[7] = (byte) week;

        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 接收到稳定数据时，回复此条
     */
    private void replayTemp() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] hex = new byte[1];
        hex[0] = 0x03;
        sendMcuBean.setHex(CID, hex);
        sendData(sendMcuBean);
    }

    /**
     * 请求电量
     */
    public void requestBattery() {
        mBleDevice.sendData(new SendBleBean(BleSendCmdUtil.getInstance().getMcuBatteryStatus()));
    }

//---------------解析数据------


    public interface onNotifyData {
        void onData(byte[] data, int type);

        /**
         * 稳定温度
         *
         * @param temp     体温原始数据
         * @param decimal  小数位
         * @param tempUnit 体温单位
         */
        void temp(int temp, int decimal, byte tempUnit);

        /**
         * 实时温度
         *
         * @param temp     体温原始数据
         * @param decimal  小数位
         * @param tempUnit 体温单位
         */
        void tempNow(int temp, int decimal, byte tempUnit);

        /**
         * 设置单位返回<br>
         * 0：设置成功<br>
         * 1：设置失败<br>
         * 2：不支持设置<br>
         */
        void getUnit(byte status);

        /**
         * 错误指令
         */
        void getErr(byte status);

        void mcuGetMode(int mode);

        void mcuSetMode(int status);

        void mcuGetTemp(int temp, int unit, int decimal);

        void mcuSetTemp(int status);

        void mcuSetUnixStamp(int status);

        /**
         * 离线历史记录
         *
         * @param totalNum 最大值大小
         * @param sendNum 当前大小
         * @param list    列表
         */
        void offlineData(int totalNum, int sendNum, List<HistoryBean> list);
    }

    public class HistoryBean {
        private long stamp;
        private int temp;
        private int unit;
        private int decimal;

        public HistoryBean(long stamp, int temp, int unit, int decimal) {
            this.stamp = stamp;
            this.temp = temp;
            this.unit = unit;
            this.decimal = decimal;
        }

        public long getStamp() {
            return stamp;
        }

        public void setStamp(long stamp) {
            this.stamp = stamp;
        }

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public int getUnit() {
            return unit;
        }

        public void setUnit(int unit) {
            this.unit = unit;
        }

        public int getDecimal() {
            return decimal;
        }

        public void setDecimal(int decimal) {
            this.decimal = decimal;
        }
    }

    //------------------set/get--------------

    public void setOnBleVersionListener(OnBleVersionListener bleVersionListener) {
        if (mBleDevice != null)
            mBleDevice.setOnBleVersionListener(bleVersionListener);
    }

    public void setOnMcuParameterListener(OnMcuParameterListener mcuParameterListener) {
        if (mBleDevice != null)
            mBleDevice.setOnMcuParameterListener(mcuParameterListener);
    }

    public void setOnCompanyListener(OnBleCompanyListener companyListener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleCompanyListener(companyListener);
        }
    }

    public void setOnSettingUnit(OnSettingUnit onSettingUnit) {
        mOnSettingUnit = onSettingUnit;
    }

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

    /**
     * int 转byte 小端序
     */
    public byte[] getIntToByteLittle(int data) {
        byte[] result = new byte[4];
        result[3] = (byte) ((data >> 24) & 0xFF);
        result[2] = (byte) ((data >> 16) & 0xFF);
        result[1] = (byte) ((data >> 8) & 0xFF);
        result[0] = (byte) (data & 0xFF);
        return result;
    }

    public void getDeviceVersion() {
        SendBleBean sendBleBean = new SendBleBean();
        BleSendCmdUtil sendCmdUtil = BleSendCmdUtil.getInstance();
        sendBleBean.setHex(sendCmdUtil.getBleVersion());
        sendData(sendBleBean);
    }

    private int getDayOfWeek(int dayOfWeek) {
        if (dayOfWeek == 1) {
            return 7;
        } else {
            return dayOfWeek - 1;
        }
    }
}
