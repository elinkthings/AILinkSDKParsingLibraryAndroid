package cn.net.aicare.modulelibrary.module.scooter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.elinkthings.bleotalibrary.listener.OnBleOTAListener;
import com.elinkthings.bleotalibrary.rtk.BleRtkOtaBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


/**
 * xing<br>
 * 2019/10/15<br>
 * ble设备对象
 */
public class SkateboardDevice extends BaseBleDeviceData implements OnBleOtherDataListener {
    private String TAG = SkateboardDevice.class.getName();

    private final static int OTA_SEND_TIMEOUT = 1;
    private onNotifyData mOnNotifyData;
    private OnBleOTAListener mOnBleOTAListener;
    private static BleDevice mBleDevice = null;
    private static SkateboardDevice sMSkateboardDevice = null;
    private int mDeviceId = -1;
    private int mCid = 0x0001;
    private boolean mAILinkBle;
    private int mOtaSendErr = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {


                case OTA_SEND_TIMEOUT:
                    mOtaSendErr++;
                    if (mOtaSendErr < 100) {
                        sendUpdateData(1, mScooterOtaManager.getKey());
                    } else {
                        getUpdateStatus();
                    }
                    break;
            }

        }
    };

    public static SkateboardDevice getInstance(BleDevice bleDevice) {
        synchronized (BleDevice.class) {
            if (mBleDevice == bleDevice) {
                if (sMSkateboardDevice == null) {
                    sMSkateboardDevice = new SkateboardDevice(bleDevice);
                }
            } else {
                sMSkateboardDevice = new SkateboardDevice(bleDevice);
            }
        }
        return sMSkateboardDevice;
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

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }

    private SkateboardDevice(BleDevice bleDevice) {
        super(bleDevice);
        BleLog.i(TAG, "初始化");
        mBleDevice = bleDevice;
        mAILinkBle = bleDevice.containsServiceUuid(BleConfig.UUID_SERVER_AILINK);
        if (!mAILinkBle) {
            bleDevice.setHandshakeStatus(false);
            bleDevice.setNotify(SkateboardBleConfig.UUID_BROADCAST, SkateboardBleConfig.UUID_NOTIFY);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                bleDevice.setMtu(200);
//            }
        }
        bleDevice.setA7Encryption(false);
        mBleDevice.setOnBleOtherDataListener(this);
    }


    public void getBleVersion() {
        SendDataBean sendDataBean = new SendDataBean(null, UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB"), BleConfig.READ_DATA, UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB"));
        sendData(sendDataBean);
    }


    /**
     * 查询心跳数据
     */
    public void getHeartbeat() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x01, (byte) 0x01, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 关灯
     */
    public void setCloseLight() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x04, 0x04, 0x02, 0x00, 0x7F};//关灯
        sendCmdData(cmd);
    }

    /**
     * 开灯
     */
    public void setOpenLight() {
//        byte[] cmd = new byte[]{0x5A, 0x23, 0x04, 0x04, 0x02, 0x3f, 0x7F, 0x55, 0x75};//开灯
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x04, 0x04, 0x02, 0x3f, 0x7F};//开灯
        sendCmdData(cmd);
    }


    /**
     * 0启动
     */
    public void setZeroStart() {
        //0x5A 0x21 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0xXX 0xXX
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x02, 0x02, 0x02, 0x02, 0x02};
        sendCmdData(cmd);
    }


    /**
     * 非0启动
     */
    public void setNoZeroStart() {
        //0x5A 0x21 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0xXX 0xXX
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x02, 0x02, 0x02, 0x00, 0x02};
        sendCmdData(cmd);
    }


    /**
     * 助力模式
     */
    public void setBoost() {
        //0x5A 0x21 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0xXX 0xXX
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_CONTROLLER_ANSWER, 0x02, 0x02, 0x02, 0x08, 0x08};
        sendCmdData(cmd);
    }

    /**
     * 非助力模式
     */
    public void setNoBoost() {
        //0x5A 0x21 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0xXX 0xXX
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_CONTROLLER_ANSWER, 0x02, 0x02, 0x02, 0x00, 0x08};
        sendCmdData(cmd);
    }

    /**
     * 单位KM
     */
    public void setUnitKm() {
        //0x5A 0x21 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0xXX 0xXX
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x02, 0x02, 0x02, (byte) 0x00, (byte) 0x80};
        sendCmdData(cmd);
    }

    /**
     * 单位英里
     */
    public void setUnitMi() {
        //0x5A 0x21 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0xXX 0xXX
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x02, 0x02, 0x02, (byte) 0x80, (byte) 0x80};
        sendCmdData(cmd);
    }


    /**
     * 定速巡航
     */
    public void setConstantSpeed() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x02, 0x02, 0x02, (byte) 0x04, (byte) 0x04};
        sendCmdData(cmd);
    }

    /**
     * 非定速巡航
     */
    public void setNoConstantSpeed() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x02, 0x02, 0x02, (byte) 0x00, (byte) 0x04};
        sendCmdData(cmd);
    }


    /**
     * 恢复出厂设置
     */
    public void setReset() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x03, (byte) 0x03, 0x02, (byte) 0x01, (byte) 0x01};
        sendCmdData(cmd);
    }

    /**
     * 清除总里程
     */
    public void setClearMileage() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x03, (byte) 0x03, 0x02, (byte) 0x02, (byte) 0x02};
        sendCmdData(cmd);
    }

    /**
     * 清除骑行时间
     */
    public void setClearCyclingTime() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x03, (byte) 0x03, 0x02, (byte) 0x04, (byte) 0x04};
        sendCmdData(cmd);
    }

    /**
     * 重置
     */
    public void setClearAll() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x03, (byte) 0x03, 0x02, (byte) 0x07, (byte) 0x07};
        sendCmdData(cmd);
    }


    /**
     * 设置档位速度
     *
     * @param gear    0-7
     * @param speedKm 1-254
     */
    public void setGearSpeed(int gear, int speedKm) {
        if (speedKm <= 0) {
            speedKm = 1;
        } else if (speedKm > 254) {
            speedKm = 254;
        }
        if (gear < 0) {
            gear = 0;
        } else if (gear > 7) {
            gear = 7;
        }
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x05, (byte) 0x05, 0x02, (byte) gear, (byte) speedKm};
        sendCmdData(cmd);
    }


    /**
     * 设置自动关机时间
     *
     * @param timeS 时间,秒
     */
    public void setAutoShutdownTime(int timeS) {
        byte highTime = (byte) (timeS >> 8);
        byte lowTime = (byte) timeS;
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x06, (byte) 0x06, 0x02, (byte) highTime, (byte) lowTime};
        sendCmdData(cmd);
    }

    /**
     * 读取自动关机的时间
     */
    public void readAutoShutdownTime() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x06, (byte) 0x06, 0x02, (byte) 0x00, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 设置控制器的实时时间
     *
     * @param hour 小时
     * @param min  分钟
     */
    public void setControllerTime(int hour, int min) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x07, (byte) 0x07, 0x02, (byte) hour, (byte) min};
        sendCmdData(cmd);
    }


    /**
     * 读取控制器的时间
     */
    public void readControllerTime() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x07, (byte) 0x07, 0x02, (byte) 0x00, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 读取控制器单次行驶里程
     */
    public void readControllerSingleMileage() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x08, (byte) 0x08, 0x02, (byte) 0x00, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 读取控制器总行驶里程
     */
    public void readControllerAllMileage() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x09, (byte) 0x09, 0x02, (byte) 0x00, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 读取控制器温度
     */
    public void readControllerTemp() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x0A, (byte) 0x0A, 0x01, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 读取控制器行驶电流
     */
    public void readControllerDrivingCurrent() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x0B, (byte) 0x0B, 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询滑板车控制器——电池电压
     * 读取电池电压
     */
    public void readControllerVoltage() {
//        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x0C, (byte) 0x0C, 0x01, (byte) 0x01};
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x0C, (byte) 0x0C, 0x01, (byte) 0x00};
        sendCmdData(cmd);
    }


    /**
     * 查询滑板车控制器——电池总容量
     * 读取总容量
     */
    public void readAllPower() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x0D, (byte) 0x0D, 0x01, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询滑板车控制器——电池剩余容量
     * 读取剩余容量
     */
    public void readLastPower() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x0E, (byte) 0x0E, 0x01, (byte) 0x00};
        sendCmdData(cmd);
    }


    /**
     * 查询滑板车控制器——电池充放电次数
     * 读取电池充放电次数
     */
    public void readBatteryDischargeNumber() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x0F, (byte) 0x0F, 0x01, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询滑板车控制器——电池厂商代码及编号
     * 读取电池代码
     */
    public void readBatteryCode() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x10, (byte) 0x10, 0x01, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询控制器——厂商代码及软硬件版本
     * 读取厂商代码及软硬件版本
     */
    public void readCompanyVersion() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x11, (byte) 0x11, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询控制器——厂商代码及软硬件版本
     * 读取厂商代码及软硬件版本
     */
    public void readControllerVersion() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_CONTROLLER, (byte) 0x11, (byte) 0x11, 0x00};
        sendCmdData(cmd);
    }


    /**
     * 查询--有齿电机霍尔脉冲数
     * 读取有齿电机霍尔脉冲数
     */
    public void readMotorPulse() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x12, (byte) 0x12, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询——有齿电机收到霍尔脉冲间隔时间（单位ms）
     * 读取有齿电机收到霍尔脉冲间隔时间（单位ms）
     */
    public void readMotorPulseInterval() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x13, (byte) 0x13, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询--电机参数
     * 读取电机参数
     */
    public void readMotorParameter() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x14, (byte) 0x14, 0x03, 0x00, 0x00, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询——轮径尺寸
     * 读取车轮大小
     */
    public void readWheelSize() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x15, (byte) 0x15, 0x02, (byte) 0x80, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询——FLASH数据版本
     * 读取FLASH数据版本
     */
    public void readFlashDataVersion() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x16, (byte) 0x16, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询从机——厂商代码及软硬件版本（软件为Bootloader版本）
     * 读取厂商代码及软硬件版本（软件为Bootloader版本）
     */
    public void readBootloaderVersion() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x17, (byte) 0x17, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询/配置--仪表背光亮度
     * 设置仪表背光亮度
     */
    public void setMeterBackLight(int percentage) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x18, (byte) 0x18, 0x03, 0x00, (byte) percentage, (byte) percentage};
        sendCmdData(cmd);
    }


    /**
     * 查询/配置--仪表背光亮度
     * 读取仪表背光亮度
     */
    public void readMeterBackLight() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x18, (byte) 0x18, 0x03, 0x00, 0x00, 0x00};
        sendCmdData(cmd);
    }

    /**
     * 查询/配置--夜间模式
     * 设置全天自动夜间模式
     */
    public void setAutoNightMode(int type, int startH, int startM, int stopH, int stopM) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x19, (byte) 0x19, 0x05, (byte) type, (byte) startH, (byte) startM, (byte) stopH,
                (byte) stopM};
        sendCmdData(cmd);
    }

    /**
     * 查询--夜间模式
     * 读取夜间模式
     */
    public void readNightMode() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x19, (byte) 0x19, 0x05, 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 来电推送
     */
    public void setCallPhone(String phoneNumber) {
        byte[] phoneByte = phoneNumber.getBytes(StandardCharsets.UTF_8);
        int byteLength = phoneByte.length + 1;
        byte[] cmd = new byte[byteLength + 5];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0xA0;
        cmd[3] = (byte) 0xA0;
        cmd[4] = (byte) byteLength;
        cmd[5] = 0x01;//1：来电接入
        System.arraycopy(phoneByte, 0, cmd, 6, phoneByte.length);
        sendCmdData(cmd);
    }

    /**
     * 短信推送
     */
    public void setSMSData(String data) {
        byte[] dataByte = data.getBytes(StandardCharsets.UTF_8);
        int byteLength = dataByte.length + 2;
        if (byteLength > 130) {
            BleLog.i("短信内容过长,需分段传输");
            return;
        }
        byte[] cmd = new byte[byteLength + 5];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0xA1;
        cmd[3] = (byte) 0xA1;
        cmd[4] = (byte) byteLength;
        cmd[5] = 0x00;//最后一包,bit7=0代表最后一包
        cmd[6] = 0x00;//数据包号
        System.arraycopy(dataByte, 0, cmd, 7, dataByte.length);
        sendCmdData(cmd);
    }

    /**
     * 消息推送
     *
     * @param id  1=来电,2=短信
     * @param max 数量,0=清空
     */
    public void setSoftwareData(int id, int max) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0xA2, (byte) 0xA2, 0x02, (byte) id, (byte) max};
        sendCmdData(cmd);
    }

    /**
     * 时间推送（每次app连接时发送一次）
     *
     * @param mAdd 0代表当前时间,
     */
    public void setTime(int mAdd) {
        int[] timeArr = getTimeArr();
        int year = getYear() - 2000;
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0xA3, (byte) 0xA3, 0x06, (byte) year, (byte) getMonth(), (byte) getDay(),
                (byte) timeArr[0], (byte) (timeArr[1] + mAdd), (byte) timeArr[2]};
        sendCmdData(cmd);
    }

    /**
     * 蓝牙密码查询
     */
    public void readBlePassword() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0xA4, (byte) 0xA4, 0x06, 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00};
        sendCmdData(cmd);
    }

    /**
     * 导航推送
     *
     * @param status                       是否开启导航
     * @param code                         方向代码
     * @param currentDirectionLastDistance 当前方式剩余路程
     * @param allDirectionLastDistance     总剩余路程
     */
    public void setNavigationMessage(boolean status, int code, int currentDirectionLastDistance, long allDirectionLastDistance) {
        int one = status ? code + 0x80 : code;
        byte[] cmd = new byte[5 + 7];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0xA5;
        cmd[3] = (byte) 0xA5;
        cmd[4] = 0x07;
        cmd[5] = (byte) one;
        cmd[6] = (byte) (currentDirectionLastDistance >> 8);
        cmd[7] = (byte) (currentDirectionLastDistance);

        cmd[8] = (byte) (allDirectionLastDistance >> 24);
        cmd[9] = (byte) (allDirectionLastDistance >> 16);
        cmd[10] = (byte) (allDirectionLastDistance >> 8);
        cmd[11] = (byte) (allDirectionLastDistance);

        sendCmdData(cmd);
    }


    /**
     * 重置指令
     */
    public void setResetCmd() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0xA6, (byte) 0xA6, 0x01, 0x01};
        sendCmdData(cmd);
    }

    /**
     * 锁车/解锁设置
     * //解锁密码有误
     *
     * @param status   是否为上锁
     * @param password 6位数字密码
     */
    public void setLockCar(boolean status, String password) {
        byte one = (byte) (status ? 0x80 : 0x00);
        byte[] cmd = new byte[5 + 7];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0xA7;
        cmd[3] = (byte) 0xA7;
        cmd[4] = 0x07;
        cmd[5] = (byte) one;

        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(bytes, 0, cmd, 6, bytes.length);
        sendCmdData(cmd);
    }

    /**
     * 修改锁车密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     */
    public void setLockCarPassword(String passwordOld, String passwordNew) {
        byte[] bytesOld = passwordOld.getBytes(StandardCharsets.UTF_8);
        byte[] bytesNew = passwordNew.getBytes(StandardCharsets.UTF_8);
        byte[] cmd = new byte[5 + bytesOld.length + bytesNew.length + 1];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0xA8;
        cmd[3] = (byte) 0xA8;
        cmd[4] = (byte) (bytesOld.length + bytesNew.length + 1);
        cmd[5] = 0x00;
        System.arraycopy(bytesOld, 0, cmd, 5, bytesOld.length);
        System.arraycopy(bytesNew, 0, cmd, 5 + bytesOld.length, bytesNew.length);
        sendCmdData(cmd);
    }


    /**
     * 售后密码查询
     */
    public void readServicePassword() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0xA9, (byte) 0xA9, 0x00};
        sendCmdData(cmd);
    }


    /**
     * 天气信息推送
     */
    public void setWeather(List<BleWeatherBean> list) {

        int length = list.size() * 8;
        byte[] cmd = new byte[length + 5];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0xAA;
        cmd[3] = (byte) 0xAA;
        cmd[4] = (byte) length;
        for (int i = 0; i < list.size(); i++) {
            if (i >= 5) {
                break;
            }
            BleWeatherBean bleWeatherBean = list.get(i);
            byte[] bleWeatherByte = getBleWeatherByte(bleWeatherBean);
            System.arraycopy(bleWeatherByte, 0, cmd, 5 + i * 8, bleWeatherByte.length);
        }
        sendCmdData(cmd);
    }

    /**
     * 获取天气byte
     *
     * @param bleWeatherBean 天气对象
     * @return bytes
     */
    private byte[] getBleWeatherByte(BleWeatherBean bleWeatherBean) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) bleWeatherBean.getCode();
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[3] = (byte) bleWeatherBean.getHumidity();
        int windDirection = bleWeatherBean.getWindDirection();
        int windSpeed = bleWeatherBean.getWindSpeed();
        int i = (windDirection << 5) | (windSpeed);
        bytes[4] = (byte) i;
        int uvIntensity = bleWeatherBean.getUVIntensity();
        int airQualityIndex = bleWeatherBean.getAirQualityIndex();
        int i1 = (uvIntensity << 4) | airQualityIndex;
        bytes[4] = (byte) i1;
        int maxTemperature = bleWeatherBean.getMaxTemperature();
        int minTemperature = bleWeatherBean.getMinTemperature();
        if (maxTemperature < 0) {
            maxTemperature += 0x80;
        }
        if (minTemperature < 0) {
            minTemperature += 0x80;
        }
        bytes[5] = (byte) maxTemperature;
        bytes[6] = (byte) minTemperature;
        return bytes;
    }

    public void getUpdateReadyState(@SkateboardBleConfig.OTAType int type) {
        getUpdateReadyState(type, SkateboardBleConfig.HOST_READ_ONLY_METER);
    }

    /**
     * 查询控制器是否准备好接收升级数据
     */
    public void getUpdateReadyState(@SkateboardBleConfig.OTAType int type, @SkateboardBleConfig.ScopeType int scope) {
        byte cmdType;
        switch (type) {

            case SkateboardBleConfig.OTAType.OTA_BOOTLOADER:
                cmdType = (byte) 0xD0;
                break;
            case SkateboardBleConfig.OTAType.OTA_APPLICATION:
                cmdType = (byte) 0xE0;
                break;
            case SkateboardBleConfig.OTAType.OTA_FLASH:
                cmdType = (byte) 0xF0;
                break;
            default:
                mScooterOtaManager = null;
                if (mOnBleOTAListener != null) {
                    mOnBleOTAListener.onOtaFailure(0, "升级失败");
                }
                return;
        }
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, (byte) scope, cmdType, cmdType, 0x00};
        sendCmdData(cmd);
    }

    private ScooterOtaManager mScooterOtaManager;
    private int mOtaType = -1;
    /**
     * 作用域,默认表盘
     */
    private int mOTAScope = SkateboardBleConfig.HOST_WRITE_RETURN_METER;

    /**
     * @param filePath
     * @param type     类型:0=Bootloader;1=Application;2=Flash
     */
    public void setUpdateData(String filePath, @SkateboardBleConfig.OTAType int type) {
        setUpdateData(filePath, type, SkateboardBleConfig.HOST_WRITE_RETURN_METER);

    }


    /**
     * @param filePath
     * @param type     类型:0=Bootloader;1=Application;2=Flash
     * @param scope    {@link SkateboardBleConfig.ScopeType}
     */
    public void setUpdateData(String filePath, @SkateboardBleConfig.OTAType int type, @SkateboardBleConfig.ScopeType int scope) {
        try {
            if (mScooterOtaManager != null) {
                //已经在升级状态了
                sendUpdateData(1, 1);
                return;
            }
            mScooterOtaManager = new ScooterOtaManager();
            mScooterOtaManager.initOtaManager(filePath);
            int blockSize = mScooterOtaManager.getBlockSize();
            setOTASize(blockSize);
            mOtaType = type;
            mOTAScope = scope;
            sendUpdateData(0, 1);
        } catch (Exception e) {
            mScooterOtaManager = null;
            e.printStackTrace();
        }

    }

    /**
     * @param fileData byte[]
     * @param type     类型:0=Bootloader;1=Application;2=Flash
     * @param scope    {@link SkateboardBleConfig.ScopeType}
     */
    public void setUpdateData(byte[] fileData, @SkateboardBleConfig.OTAType int type, @SkateboardBleConfig.ScopeType int scope) {
        try {
            if (mScooterOtaManager != null) {
                //已经在升级状态了
                sendUpdateData(1, 1);
                return;
            }
            mScooterOtaManager = new ScooterOtaManager();
            mScooterOtaManager.initOtaManager(fileData);
            int blockSize = mScooterOtaManager.getBlockSize();
            setOTASize(blockSize);
            mOtaType = type;
            mOTAScope = scope;
            sendUpdateData(0, 1);
        } catch (Exception e) {
            mScooterOtaManager = null;
            e.printStackTrace();
        }

    }

    public void setUpdateData(byte[] fileData, @SkateboardBleConfig.OTAType int type) {
        try {
            if (mScooterOtaManager != null) {
                //已经在升级状态了
                sendUpdateData(1, 1);
                return;
            }
            mScooterOtaManager = new ScooterOtaManager();
            mScooterOtaManager.initOtaManager(fileData);
            int blockSize = mScooterOtaManager.getBlockSize();
            setOTASize(blockSize);
            mOtaType = type;
            sendUpdateData(0, 1);
        } catch (Exception e) {
            mScooterOtaManager = null;
            e.printStackTrace();
        }

    }

    private int mBeanOldNumber = 0;

    public synchronized void sendUpdateData(int status, int id) {
        Log.e("huangjunbin","sendUpdateData"+ status+" id"+id);
        mHandler.removeMessages(OTA_SEND_TIMEOUT);
        if (mScooterOtaManager == null) {
            //当前不在升级状态
            return;
        }
        BleRtkOtaBean bleScooterOtaBean = null;
        if (status == 0) {
            mBeanOldNumber = 0;
            bleScooterOtaBean = mScooterOtaManager.nextBleScooterOtaBean();
        } else if (status == 1) {
            bleScooterOtaBean = mScooterOtaManager.getBleScooterOtaBeanOld(id);
            mBeanOldNumber++;
            if (mBeanOldNumber > 10) {
                mScooterOtaManager = null;
                mOtaType = -1;
                if (mOnBleOTAListener != null) {
                    mOnBleOTAListener.onOtaFailure(0, "升级失败");
                    return;
                }
            }
        }
        if (mOnBleOTAListener != null && mScooterOtaManager != null) {
            BleLog.i(TAG, "OTA百分比:" + mScooterOtaManager.getProgress());
            mOnBleOTAListener.onOtaProgress(mScooterOtaManager.getProgress(), 1, 1);
        }
        if (bleScooterOtaBean == null) {
            //数据包发送完成,进行升级校验
            Log.e("huangjunbin","bleScooterOtaBean");
            getUpdateStatus();
            return;
        }

        byte cmdType;
        switch (mOtaType) {

            case SkateboardBleConfig.OTAType.OTA_BOOTLOADER:
                cmdType = (byte) 0xD1;
                break;
            case SkateboardBleConfig.OTAType.OTA_APPLICATION:
                cmdType = (byte) 0xE1;
                break;
            case SkateboardBleConfig.OTAType.OTA_FLASH:
                cmdType = (byte) 0xF1;
                break;
            default:
                mScooterOtaManager = null;
                if (mOnBleOTAListener != null) {
                    mOnBleOTAListener.onOtaFailure(0, "升级失败");
                }
                return;

        }

        byte[] dataAll = bleScooterOtaBean.getDataAll();
        byte[] cmd = new byte[dataAll.length + 5];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = (byte) mOTAScope;
        cmd[2] = (byte) cmdType;
        cmd[3] = (byte) cmdType;
        cmd[4] = (byte) dataAll.length;
        System.arraycopy(dataAll, 0, cmd, 5, dataAll.length);
        sendCmdDataOta(cmd);
        mHandler.sendEmptyMessageDelayed(OTA_SEND_TIMEOUT, 200);
    }


    public void getUpdateStatus() {
        if (mScooterOtaManager == null) {
            //当前不在升级状态
            return;
        }
        byte[] dataAll = mScooterOtaManager.getFileCrc32();
        if (dataAll == null) {
            BleLog.e("OTACrc32校验失败");
            return;
        }
        byte cmdType;
        switch (mOtaType) {

            case SkateboardBleConfig.OTAType.OTA_BOOTLOADER:
                cmdType = (byte) 0xD2;
            case SkateboardBleConfig.OTAType.OTA_APPLICATION:
                cmdType = (byte) 0xE2;
                break;
            case SkateboardBleConfig.OTAType.OTA_FLASH:
                cmdType = (byte) 0xF2;
                break;
            default:
                mScooterOtaManager = null;
                if (mOnBleOTAListener != null) {
                    mOnBleOTAListener.onOtaFailure(0, "升级失败");
                }
                return;

        }
        byte[] cmd = new byte[dataAll.length + 5];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = (byte) mOTAScope;
        cmd[2] = (byte) cmdType;
        cmd[3] = (byte) cmdType;
        cmd[4] = (byte) dataAll.length;
        System.arraycopy(dataAll, 0, cmd, 5, dataAll.length);
        sendCmdData(cmd);

    }
    //---------------------新增协议---------------------

    /**
     * 设置语言
     *
     * @param language "0：英语
     *                 1：法语
     *                 2：葡萄牙语
     *                 3：西班牙语
     *                 0xFF：其他语言"
     */
    public void setDeviceLanguage(int language) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x30, 0x30, 0x01, (byte) language};
        sendCmdData(cmd);
    }

    /**
     * 读取仪表盘图片版本和字库版本
     */
    public void getDeviceImageFontVersion() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, 0x33, 0x33, 0x01, (byte) 0x01};
        sendCmdData(cmd);
    }

    /**
     * 生产测试
     *
     * @param status 1,进入生产测试模式,2：进入整机测试模式,3：退出测试模式
     */
    public void setProductionTest(int status) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, 0x32, 0x32, 0x01, (byte) status};
        sendCmdData(cmd);
    }

    /**
     * 读生产测试状态
     */
    public void readProductionTest() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, 0x32, 0x32, 0x00};
        sendCmdData(cmd);
    }

    /**
     * OTA包大小,用于进度显示
     *
     * @param size 大小
     */
    public void setOTASize(int size) {
        byte[] bytes = getIntToByte(size);
        byte[] cmd = new byte[bytes.length + 5];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0x35;
        cmd[3] = (byte) 0x35;
        cmd[4] = (byte) 0x04;
        System.arraycopy(bytes, 0, cmd, 5, bytes.length);
        sendCmdData(cmd);
    }

    /**
     * int 转byte
     */
    public static byte[] getIntToByte(int data) {
        byte[] result = new byte[4];
        result[3] = (byte) ((data) & 0xFF);
        result[2] = (byte) ((data >> 8) & 0xFF);
        result[1] = (byte) ((data >> 16) & 0xFF);
        result[0] = (byte) (data >> 24 & 0xFF);
        return result;
    }


    /**
     * 查询TP屏版本号
     */
    public void readTpVersion() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_READ_ONLY_METER, (byte) 0x36, (byte) 0x36, 0x00};
        sendCmdData(cmd);
    }


    /**
     * 设置配对
     */
    public void setPair() {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x34, (byte) 0x34, 0x01, 0x01};
        sendCmdData(cmd);
    }

    /**
     * 验证密码
     */
    public void checkPwd(String password) {
        byte[] cmd = new byte[5 + 6];
        cmd[0] = SkateboardBleConfig.PREFIX;
        cmd[1] = SkateboardBleConfig.HOST_WRITE_RETURN_METER;
        cmd[2] = (byte) 0x37;
        cmd[3] = (byte) 0x37;
        cmd[4] = (byte) 0x06;

        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(bytes, 0, cmd, 5, bytes.length);
        sendCmdData(cmd);
    }


    /**
     * 设置OTA文件包数
     */
    public void setOtaStepSize(int stepSize) {
        byte[] cmd = new byte[]{SkateboardBleConfig.PREFIX, SkateboardBleConfig.HOST_WRITE_RETURN_METER, (byte) 0x38, (byte) 0x38, 0x01, (byte) stepSize};
        sendCmdData(cmd);
    }

    //---------------通用发送前的装载

    /**
     * 发送数据
     *
     * @param bytes 需要发送的数据,不含CRC
     */
    private void sendCmdData(byte[] bytes) {
        int i = crc16Calc(bytes, bytes.length);
        byte[] crc = getCrc(i);
        byte[] data = new byte[bytes.length + 2];
        System.arraycopy(bytes, 0, data, 0, bytes.length);
        System.arraycopy(crc, 0, data, bytes.length, crc.length);
        SendDataBean sendDataBean;
        if (mAILinkBle) {
            sendDataBean = new SendDataBean(data, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
        } else {
            sendDataBean = new SendDataBean(data, SkateboardBleConfig.UUID_WRITE, BleConfig.WRITE_DATA, SkateboardBleConfig.UUID_BROADCAST);
        }
        sendData(sendDataBean);
        if (mOnNotifyData != null) {
            mOnNotifyData.onWriteData(data);
        }
    }


    /**
     * 发送数据
     *
     * @param bytes 需要发送的数据,不含CRC
     */
    private synchronized void sendCmdDataOta(byte[] bytes) {
        int i = crc16Calc(bytes, bytes.length);
        byte[] crc = getCrc(i);
        byte[] data = new byte[bytes.length + 2];
        System.arraycopy(bytes, 0, data, 0, bytes.length);
        System.arraycopy(crc, 0, data, bytes.length, crc.length);
        SendDataBean sendDataBean;
        if (mAILinkBle) {
            sendDataBean = new SendDataBean(data, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
        } else {
            sendDataBean = new SendDataBean(data, SkateboardBleConfig.UUID_WRITE, BleConfig.WRITE_DATA, SkateboardBleConfig.UUID_BROADCAST);
        }
        BleLog.i(TAG, "发送OTA包:" + BleStrUtils.byte2HexStr(data));
        sendDataOta(sendDataBean);
    }

    //----------

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (hex == null) {
            BleLog.i(TAG, "接收到的数据:null");
            return;
        }
        String data = BleStrUtils.byte2HexStr(hex);
        BleLog.i(TAG, "接收到的数据:" + data);
        //校验解析
        dataCheck(hex);
    }

    @Override
    public void onNotifyOtherData(byte[] data) {
        if (data == null) {
            BleLog.i(TAG, "接收到的数据:null");
            return;
        }
        String dataStr = BleStrUtils.byte2HexStr(data);
        BleLog.i(TAG, "接收到的数据:" + dataStr);
        //校验解析
        dataCheck(data);
    }



    //----------------解析数据------

    /**
     * 校验解析数据
     *
     * @param data Payload数据
     */
    private void dataCheck(byte[] data) {
        if (data == null || data.length < 6) {
            return;
        }

        int cmdStart = data[2] & 0xff;
        int cmdEnd = data[3] & 0xff;
        int status = data[5] & 0xff;
        if ((cmdStart == 0xE1 && cmdEnd == 0xE1) || (cmdStart == 0xF1 && cmdEnd == 0xF1) || (cmdStart == 0xD1 && cmdEnd == 0xD1)) {
            int id = 1;
            if (data.length > 7) {
                id = ((data[6] & 0xff) << 8) | (data[7] & 0xff);
            }

            sendUpdateData(status, id);
            //升级中
            mOtaSendErr = 1;
            return;
        } else if ((cmdStart == 0xE2 && cmdEnd == 0xE2) || (cmdStart == 0xF2 && cmdEnd == 0xF2) || (cmdStart == 0xD2 && cmdEnd == 0xD2)) {
            //升级结束应答
            switch (status) {
                //升级成功
                case 0:
                    mScooterOtaManager = null;
                    mOtaType = -1;
                    if (mOnBleOTAListener != null) {
                        mOnBleOTAListener.onOtaSuccess();
                    }
                    break;
                //升级失败
                case 1:
                    mScooterOtaManager = null;
                    mOtaType = -1;
                    if (mOnBleOTAListener != null) {
                        mOnBleOTAListener.onOtaFailure(0, "升级失败");
                    }
                    break;
                //升级中
                case 2:

                    break;

            }
            return;
        }
        if (mOnNotifyData != null) {
            mOnNotifyData.onData(data);
        }


    }

    @Override
    public void onDisConnected() {
        super.onDisConnected();
        mScooterOtaManager = null;
        mOtaType = -1;
    }


    //----------------接口------


    public interface onNotifyData {
        /**
         * 不能识别的透传数据
         */
        default void onData(byte[] data) {
        }

        default void onWriteData(byte[] data) {
        }


    }


    private int crc16Calc(byte[] data_arr, int data_len) {
        int crc16 = 0;
        int i = 0;
        for (i = 0; i < data_len; i++) {
            crc16 = getUint16((getUint16(crc16) >> 8) | (getUint16(crc16) << 8));
            crc16 = getUint16(crc16 ^ (data_arr[i] & 0xFF));
            crc16 ^= getUint16((getUint16(crc16) & 0xFF) >> 4);
            crc16 ^= getUint16((getUint16(crc16) << 8) << 4);
            crc16 ^= getUint16(((getUint16(crc16) & 0xFF) << 4) << 1);
        }
        return getUint16(crc16);
    }

    public static int getUint16(int i) {
        return i & 0x0000ffff;
    }


    private static byte[] mKey = {0x63, 0x68, 0x61, 0x6E, 0x67, 0x7A, 0x68, 0x6F, 0x75, 0x5F, 0x74, 0x61, 0x6F, 0x74, 0x61, 0x6F};

    /**
     * 待修改,暂有误
     * 锁车密码解密程序,Key解密密钥
     *
     * @param src Scr加密后的密码
     * @param dst Dst解密后的密码
     */
    public static void deCrypt(byte[] src, byte[] dst) {
        byte n = 0, i, a = 0x5a;
        long[] k = new long[mKey.length];
        byte[] tmpBuf = new byte[6], tmpBuf2 = new byte[6];
        for (int i1 = 0; i1 < mKey.length; i1++) {
            k[i1] = mKey[i1];
        }
        for (i = 0; i < 6; i++)
            tmpBuf[i] = src[i];
        while (n < 32) {
            for (i = 0; i < 6; i++) {
                tmpBuf[i] -= (k[(n + 2) % 4] >> ((n + 5) % 24));
                tmpBuf[i] ^= (k[n % 4] >> (n % 24));
                tmpBuf[i] -= i;
                tmpBuf[i] ^= n;
            }
            n++;
        }
        for (i = 0; i < 6; i++) {
            tmpBuf2[i] = (byte) (tmpBuf[i] ^ a);
            a += tmpBuf2[i];
        }
        //因为协议售后密码是按照6-1顺序传过来，所以解密后的密码要反一下
        for (i = 0; i < 6; i++) {
            dst[i] = tmpBuf2[5 - i];
        }
    }


    private byte[] getCrc(int crc) {
        byte[] b = new byte[2];
        b[0] = (byte) ((crc >> 8) & 0xff);
        b[1] = (byte) (crc & 0xff);
        return b;
    }


    private int[] getTimeArr() {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        time[0] = c.get(Calendar.HOUR_OF_DAY);
        time[1] = c.get(Calendar.MINUTE);
        time[2] = c.get(Calendar.SECOND);
        return time;
    }

    /**
     * 返回当前年
     */
    private int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 返回当前月(1~12)
     */
    private int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回当前日
     */
    private int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    //-----------------set/get-----------------
    public void setOnNotifyData(onNotifyData onNotifyData) {
        mOnNotifyData = onNotifyData;
    }

    public void setOnBleOTAListener(OnBleOTAListener onBleOTAListener) {
        mOnBleOTAListener = onBleOTAListener;
    }

    public void setOnBleVersionListener(OnBleVersionListener listener) {
        if (mBleDevice != null) {
            mBleDevice.setOnBleVersionListener(listener);
        }

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
