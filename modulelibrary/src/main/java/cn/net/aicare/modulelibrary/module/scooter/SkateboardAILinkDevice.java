package cn.net.aicare.modulelibrary.module.scooter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.lang.System.arraycopy;


/**
 * xing<br>
 * 2021/12/18<br>
 * 滑板车AILink协议
 */
public class SkateboardAILinkDevice extends BaseBleDeviceData implements OnBleOtherDataListener {
    private String TAG = SkateboardAILinkDevice.class.getName();

    private onNotifyData mOnNotifyData;
    private onNotifyChangePaw mOnNotifyChangePaw;
    private onNotifyDataMap onNotifyDataMap;
    private onNotifyDataOTA onNotifyDataOTA;
    private onNotifyCheckPaw mOnNotifyCheckPaw;
    private onNotifyDataDevice onNotifyDataDevice;
    private BleDevice mBleDevice;
    private static SkateboardAILinkDevice sMSkateboardDevice = null;
    private boolean isConnect;
    private float mCurrentSpeed = 0;


    public static SkateboardAILinkDevice getInstance() {
        return sMSkateboardDevice;
    }


    public static void init(BleDevice bleDevice) {
        sMSkateboardDevice = new SkateboardAILinkDevice(bleDevice);


    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (mOnNotifyData != null) {
            mOnNotifyData.onHandshake(status);

        }
    }


    /**
     * 退出模块时需要清空单例赋值
     */
    public void clear() {
        if (sMSkateboardDevice != null) {
            mOnNotifyData = null;
            sMSkateboardDevice = null;
        }
    }


    private SkateboardAILinkDevice(BleDevice bleDevice) {
        super(bleDevice);
        mBleDevice = null;
        mBleDevice = bleDevice;
        mBleDevice.setOnBleOtherDataListener(this);

    }

    public void disConnect() {
        mBleDevice.disconnect();
    }

    /**
     * 0启动
     */
    public void setZeroStart(boolean isZero) {
        byte[] bytes = new byte[3];
        bytes[0] = SkateboardAILinkBleConfig.CMD_CONTROLLER_STATUS;
        bytes[1] = (byte) (isZero ? 0x00 : (0x01<<1));
        bytes[2] = (0x01<<1);
        sendData(getMcuBean(bytes));
    }

    /**
     * 单位KM
     */
    public void setUnit(boolean isKm) {
        byte[] bytes = new byte[3];
        bytes[0] = SkateboardAILinkBleConfig.CMD_CONTROLLER_STATUS;
        bytes[1] = (byte) (isKm ? 0x00 : (0x01<<4));
        bytes[2] = (0x01<<4);
        sendData(getMcuBean(bytes));
    }

    /**
     * 读取控制器总行驶里程
     */
    public void getTotalMileage() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_TOTAL_MILEAGE;
        sendData(getMcuBean(bytes));
    }


    /**
     * 售后密码查询
     */
    public void readServicePassword() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_AFTER_SALE_PWD;
        sendData(getMcuBean(bytes));
    }

    /**
     * 关灯
     */
    public void setBigLight(int openOrClose) {
        byte[] bytes = new byte[3];
        bytes[0] = SkateboardAILinkBleConfig.CMD_LIGHT_STATUS;
        bytes[1] = (byte) openOrClose;
        bytes[2] = 0x01;
        sendData(getMcuBean(bytes));
    }

    /**
     * 验证密码
     * @param password
     */
    public void checkPaw(String password) {
        byte[] cmd = new byte[8];
        cmd[0] = SkateboardAILinkBleConfig.CMD_UNLOCK_OR_LOCK;
        cmd[1] = 0x02;
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        arraycopy(bytes, 0, cmd, 2, bytes.length);
        sendData(getMcuBean(cmd));
    }


    /**
     * 设置档位速度
     *
     * @param gear 1-7
     */
    public void setGearSpeed(int gear) {

        byte[] bytes = new byte[3];
        bytes[0] = SkateboardAILinkBleConfig.CMD_GEAR_SPEED;
        bytes[1] = (byte) (0x08 + (gear & 0x07));
        bytes[2] = 0x00;
        sendData(getMcuBean(bytes));

    }


    /**
     * 清除BM模块参数
     * 清除单次里程时间
     */
    public void clearCurrentDistanceAndTime() {
        byte[] bytes = new byte[3];
        bytes[0] = SkateboardAILinkBleConfig.CMD_RESET_BM_INFO;
        bytes[1] = 0x01;
        bytes[2] = 0x01;
        sendData(getMcuBean(bytes));

    }

    /**
     * 锁车/解锁设置
     * //解锁密码有误
     *
     * @param password 6位数字密码
     */
    public void setLockCar(boolean isLock, String password) {
        if (mCurrentSpeed > 0) {
            return;
        }
        byte[] cmd = new byte[8];
        cmd[0] = SkateboardAILinkBleConfig.CMD_UNLOCK_OR_LOCK;
        cmd[1] = (byte) (isLock ? 0x01 : 0x00);
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        arraycopy(bytes, 0, cmd, 2, bytes.length);
        sendData(getMcuBean(cmd));
    }


    /**
     * 修改锁车密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     */
    public void changeLockCarPassword(String passwordOld, String passwordNew) {
        byte[] bytesOld = passwordOld.getBytes(StandardCharsets.UTF_8);
        byte[] bytesNew = passwordNew.getBytes(StandardCharsets.UTF_8);
        byte[] cmd = new byte[13];
        cmd[0] = SkateboardAILinkBleConfig.CMD_CHANGE_PWD;
        arraycopy(bytesOld, 0, cmd, 1, bytesOld.length);
        arraycopy(bytesNew, 0, cmd, 7, bytesNew.length);
        sendData(getMcuBean(cmd));
    }


    /**
     * 重置指令
     * 回复出厂设置
     */
    public void setResetCmd() {
        byte[] bytes = new byte[3];
        bytes[0] = SkateboardAILinkBleConfig.CMD_RESET_BM_INFO;
        bytes[1] = (byte) 0x80;
        bytes[2] = (byte) 0x80;
        sendData(getMcuBean(bytes));
    }

    /**
     * 查询滑板车控制器——电池电压
     * 读取电池电压
     */
    public void readControllerVoltage() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_BATTERY_INFO;

        sendData(getMcuBean(bytes));
    }


    /**
     * 查询控制器厂商代码及编号
     * 读取厂商代码及软硬件版本
     */
    public void readControllerCodeNo() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_CONTROLLER_VENDOR;

        sendData(getMcuBean(bytes));
    }

    //查询仪表固件版本
    public void readMeterVersion() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_DIAL_VERSION;

        sendData(getMcuBean(bytes));
    }


    public void checkDeviceSupport() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_DEVICE_TYPE_FEATURES;

        sendData(getMcuBean(bytes));
    }


    /**
     * 读取蓝牙版本号
     */
    public void getBleVersion() {
        byte[] bytes = new byte[1];
        bytes[0] = SkateboardAILinkBleConfig.CMD_BM_VERSION;

        sendData(getMcuBean(bytes));
    }


    public void setNotify(UUID uuidService, UUID... uuidNotify) {
        mBleDevice.setNotify(uuidService, uuidNotify);
    }

    public BleDevice getBleDevice() {
        return mBleDevice;
    }

    //----------

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (hex == null) {
            return;
        } else {
            dataCheckAilnk(hex);
        }
    }

    @Override
    public void onNotifyOtherData(byte[] data) {
        if (onNotifyDataOTA != null) onNotifyDataOTA.onNotifyOtherData(data);

    }


    @Override
    public void onReadResult(UUID uuid, boolean result) {
        super.onReadResult(uuid, result);
    }


    private void dataCheckAilnk(byte[] data) {
        int type = data[0] & 0xff;

        switch (type) {
            case SkateboardAILinkBleConfig.CMD_HEARTBEAT_PACKAGE:

                int battery = data[1] & 0x7f;
                int currentSpeedH = (data[2] & 0x7f) << 8;
                int currentSpeedL = (data[3] & 0xff);
                int unit = (data[4] & 0x10) >> 4;
                int startUp = (data[4] & 0x02) >> 1;
                int lock = (data[4] & 0x01);
                int gears = (data[5] & 0x0f);
                int bigLight = (data[7] & 0x01);
                int error_1 = (data[8] & 0x01);
                int error_2 = (data[8] & 0x02) >> 1;
                int error_3 = (data[8] & 0x04) >> 2;
                int error_4 = (data[8] & 0x08) >> 3;
                int error_5 = (data[8] & 0x10) >> 4;
                int error_6 = (data[8] & 0x20) >> 5;
                int error_7 = (data[8] & 0x40) >> 6;
                float singleRidingMil = (((data[9] & 0xff) << 8) + (data[10] & 0xff)) / 10f;

                int singleRidingTime = ((data[11] & 0xff) << 8) + (data[12] & 0xff);
                mCurrentSpeed = (currentSpeedH + currentSpeedL) / 10f;
                if (onNotifyDataMap != null) {
                    onNotifyDataMap.deviceStatus(lock, battery, mCurrentSpeed, unit, gears, bigLight, singleRidingMil == 0 ? 0.0f : singleRidingMil, singleRidingTime);
                } else if (mOnNotifyData != null) {
                    mOnNotifyData.onError(error_1, error_2, error_3, error_4, error_5, error_6, error_7);
                    mOnNotifyData.deviceStatus(mCurrentSpeed,battery, unit, gears, startUp, lock, bigLight);
                }
            case SkateboardAILinkBleConfig.CMD_CONTROLLER_STATUS: //切换单位 //设置助力模式

                break;
            case SkateboardAILinkBleConfig.CMD_TOTAL_MILEAGE://总里程
                long h3 = (data[1] & 0xFFL) << 24;
                long h2 = (data[2] & 0xFFL) << 16;
                long h1 = (data[3] & 0xFFL) << 8;
                long h = (data[4] & 0xFFL);
                if (mOnNotifyData != null) {
                    mOnNotifyData.onTotalDistance((h3 + h2 + h1 + h) / 10f);
                }
                break;
            case SkateboardAILinkBleConfig.CMD_AFTER_SALE_PWD://售后密码

                String paw = BleStrUtils.byte2HexStr(data).replace("08 ", "").replace(" ", "");
                if (mOnNotifyData != null) {
                    mOnNotifyData.onAfterPassword(paw);
                }
                break;
            case SkateboardAILinkBleConfig.CMD_UNLOCK_OR_LOCK://锁车及解锁
                int pawType = data[1] & 0x0f;
                if (pawType == 2) {
                    //验证密码
                    if (mOnNotifyCheckPaw != null)
                        mOnNotifyCheckPaw.onCheckPaw((data[1] & 0x80) >> 7);
                    if (onNotifyDataOTA != null)
                        onNotifyDataOTA.onCheckPaw((data[1] & 0x80) >> 7);
                    else if (mOnNotifyData != null) mOnNotifyData.onCheckPaw((data[1] & 0x80) >> 7);
                } else if (pawType == 0) {
                    //解锁
                    if (onNotifyDataMap != null) onNotifyDataMap.onLock((data[1] & 0x80) >> 7);
                    if (mOnNotifyData != null) mOnNotifyData.onLock((data[1] & 0x80) >> 7);
                }
                break;
            case SkateboardAILinkBleConfig.CMD_CHANGE_PWD://修改密码
                int status = data[1] & 0x01;
                if (mOnNotifyChangePaw != null) {
                    mOnNotifyChangePaw.onStatus(status);
                }
                break;
            case SkateboardAILinkBleConfig.CMD_BATTERY_INFO://电池信息
                boolean belowZero = ((data[1] & 0x80)) == 0 ? false : true;
                int temp = data[1] & 0x7f;
                boolean isDischarge = ((data[2] & 0x80)) == 0 ? false : true;
                float electricCurrent = (((data[3] & 0x7f) << 8) + (data[4] & 0xff)) / 10f;
                float voltage = (((data[5] & 0xff) << 8) + (data[6] & 0xff)) / 10f;
                float totalCapacity = (((data[7] & 0xff) << 8) + (data[8] & 0xff)) / 10f;
                int discharge = (((data[9] & 0xff) << 8) + (data[10] & 0xff));
                if (onNotifyDataDevice != null) {
                    onNotifyDataDevice.onDeviceDetail(belowZero ? 0 - temp : temp, isDischarge ? 0 - electricCurrent : electricCurrent, voltage, totalCapacity, discharge);

                }
                break;
            case SkateboardAILinkBleConfig.CMD_CONTROLLER_VENDOR:
                ControllerPayload(data);

                break;
            case SkateboardAILinkBleConfig.CMD_DIAL_VERSION:
                byte[] bytes = new byte[8];
                bytes[0] = data[1];
                bytes[1] = data[2];
                bytes[2] = data[3];
                bytes[3] = data[4];
                bytes[4] = data[5];
                bytes[5] = data[6];
                bytes[6] = data[7];
                bytes[7] = data[8];
                String byteSty = BleStrUtils.byte2HexStr(bytes).replace(" ", "");
                StringBuilder stringBuilder = convertHexToString(byteSty);
                stringBuilder.append(" ");
                stringBuilder.append((data[9] & 0xff));
                float pre = ((data[10] & 0xff) / 100f);
                stringBuilder.append(pre == 0.0 ? ".00" : (pre+"").replace("0.","."));
                int next = (data[11] & 0xff);
                float next1 = (data[12] & 0xff) / 100f;
                stringBuilder.append(next);
                stringBuilder.append(next1 == 0.0 ? ".00" : (next1+"").replace("0.","."));


                if (mOnNotifyData != null) {
                    mOnNotifyData.onMeterVersion(stringBuilder.toString(), next + next1);
                }
                break;
            case SkateboardAILinkBleConfig.CMD_DEVICE_TYPE_FEATURES:
//                boolean autoBgLight = ((data[1] & 0x40) >> 7) == 1;
//                boolean manualLight = ((data[1] & 0x20) >> 6) == 1;
//                int supportGrade = (data[1] & 0x0f);
                boolean controller = ((data[2] & 0x04) >> 2) == 1;
                boolean batteryInfo = ((data[2] & 0x02) >> 1) == 1;
                boolean batteryNo = ((data[2] & 0x01)) == 1;
                if (onNotifyDataDevice != null) {
                    onNotifyDataDevice.onDeviceSupportInfo(batteryInfo, batteryNo, controller);
                }
                break;
        }


    }
    private void ControllerPayload(byte[] data) {
        byte[] bytes = new byte[4];
        bytes[0] = data[1];
        bytes[1] = data[2];
        bytes[2] = data[3];
        bytes[3] = data[4];
        String byteSty = BleStrUtils.byte2HexStr(bytes).replace(" ", "");
        StringBuilder stringBuilder = convertHexToString(byteSty);
        stringBuilder.append(" ");
        stringBuilder.append((data[5] & 0xff));
        float pre = ((data[6] & 0xff) / 100f);
        stringBuilder.append(pre == 0.0 ? ".00" : (pre + "").replace("0.", "."));
        int next = (data[7] & 0xff);
        float next1 = (data[8] & 0xff) / 100f;
        stringBuilder.append(next);
        stringBuilder.append(next1 == 0.0 ? ".00" : (next1 + "").replace("0.", "."));


        if (mOnNotifyData != null) {
            mOnNotifyData.onControllerVersion(stringBuilder.toString());
        }

    }



    //----------------接口------


    public interface onNotifyData {

        default void onControllerVersion(String version) {
        }
        default void onMeterVersion(String version, float code) {
        }

        ;

        default void deviceStatus(float speed,int battery,
                                  int unit,
                                  int gears,
                                  int startUp,
                                  int lock,
                                  int bigLight
        ) {
        }

        default void onLock(int status) {
        }

        default void onTotalDistance(float total) {
        }


        default void onAfterPassword(String afterPassword) {
        }

        default void onError(int error_1,
                             int error_2,
                             int error_3,
                             int error_4,
                             int error_5,
                             int error_6,
                             int error_7) {
        }

        default void onHandshake(boolean status) {

        }

        default void onCheckPaw(int status) {

        }
    }

    public interface onNotifyDataMap {


        default void deviceStatus(int lock, int battery,
                                  float currentSpeed,
                                  int unit,
                                  int gears,
                                  int bigLight, float distance, int time) {

        }

        default void onLock(int status) {
        }


    }

    public interface onNotifyDataOTA {
        default void onNotifyOtherData(byte[] data) {
        }


        default void onCheckPaw(int status) {

        }
    }

    public interface onNotifyChangePaw {
        default void onStatus(int status) {
        }
    }

    public interface onNotifyCheckPaw {

        default void onCheckPaw(int status) {
        }


    }


    public interface onNotifyDataDevice {

        default void onDeviceDetail(int batteryTemp, float electricCurrent, float voltage, float totalCapacity, int discharge) {
        }

        default void onDeviceSupportInfo(boolean supportBatteryInfo, boolean supportBatteryNo, boolean supportVersion) {
        }
    }


    //-----------------set/get-----------------
    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }

    public void setmOnNotifyChangePaw(onNotifyChangePaw mOnNotifyChangePaw) {
        this.mOnNotifyChangePaw = mOnNotifyChangePaw;
    }

    public void setOnNotifyDataMap(SkateboardAILinkDevice.onNotifyDataMap onNotifyDataMap) {
        this.onNotifyDataMap = onNotifyDataMap;
    }

    public void setOnNotifyDataOTA(SkateboardAILinkDevice.onNotifyDataOTA onNotifyDataOTA) {
        this.onNotifyDataOTA = onNotifyDataOTA;
    }

    public void setmOnNotifyCheckPaw(onNotifyCheckPaw mOnNotifyCheckPaw) {
        this.mOnNotifyCheckPaw = mOnNotifyCheckPaw;
    }

    public void setOnNotifyDataDevice(SkateboardAILinkDevice.onNotifyDataDevice onNotifyDataDevice) {
        this.onNotifyDataDevice = onNotifyDataDevice;
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


            }

        }
    };

    private SendMcuBean getMcuBean(byte[] bytes) {
        SendMcuBean sendMcuBean = new SendMcuBean();
//        sendMcuBean.setHex(DeviceConfig.SMART_SCOOTER_AILINK, bytes);
        return sendMcuBean;

    }


    private StringBuilder convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb;
    }

}
