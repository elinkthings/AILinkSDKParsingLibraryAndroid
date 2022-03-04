package cn.net.aicare.modulelibrary.module.ailinkScooter;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.nio.charset.StandardCharsets;

import static java.lang.System.arraycopy;

public class AilinkScooterBleData extends BaseBleDeviceData {
    private static AilinkScooterBleData ailinkScooterBleData;
    private ScooterListener mScooterListener;

    private AilinkScooterBleData(BleDevice bleDevice) {
        super(bleDevice);
    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        int cmd = hex[0] & 0xff;
        switch (cmd) {
            case 0x00:
                int msgPush = hex[1] & 0x01;
                int weatherPush = (hex[1] & 0x02) >> 1;
                int navigation = (hex[1] & 0x04) >> 2;
                int chargeCapacityManage = (hex[1] & 0x08) >> 3;
                int chargeManage = (hex[1] & 0x10) >> 4;
                int unAutoBgLight = (hex[1] & 0x20) >> 5;
                int autoBgLight = (hex[1] & 0x40) >> 6;
                int MeterBridgeOrBlePayload = (hex[1] & 0x80) >> 7;

                int batteryNo = hex[2] & 0x01;
                int batteryInfo = (hex[2] & 0x02) >> 1;
                int controllerNo = (hex[2] & 0x04) >> 2;
                int appSwitchMachine = (hex[2] & 0x08) >> 3;
                int boostMode = (hex[2] & 0x10) >> 4;
                int zeroOrUnzeroStart = (hex[2] & 0x20) >> 5;
                int cruiseControlSwitch = (hex[2] & 0x40) >> 6;
                int cruiseControl = (hex[2] & 0x80) >> 7;

                int headingLight = hex[3] & 0x01;
                int brakeLight = (hex[3] & 0x02) >> 1;
                int dayLight = (hex[3] & 0x04) >> 2;
                int ambientLight = (hex[3] & 0x08) >> 3;
                int leftLight = (hex[3] & 0x10) >> 4;
                int rightLight = (hex[3] & 0x20) >> 5;

                int collisionWarm = hex[4] & 0x01;
                int moveWarm = (hex[4] & 0x02) >> 1;

                int autoUnLock = (hex[4] & 0x10) >> 4;
                int autoLock = (hex[4] & 0x20) >> 5;
                int findCar = (hex[4] & 0x40) >> 6;
                mScooterListener.onSupportFunction(hex, MeterBridgeOrBlePayload, autoBgLight, unAutoBgLight, chargeManage, chargeCapacityManage, navigation,
                        weatherPush, msgPush, cruiseControl, cruiseControlSwitch, zeroOrUnzeroStart, boostMode, appSwitchMachine, controllerNo, batteryInfo,
                        batteryNo, rightLight, leftLight, ambientLight, dayLight, brakeLight, headingLight, findCar, autoLock, autoUnLock, moveWarm, collisionWarm);
                break;
            case 0x01:
                headerPayload(hex);
                break;
            case 0x06:
                int result = hex[1] & 0xff;
                mScooterListener.onChangePassword(hex, result);
                break;
            case 0x07:
                int lockType = hex[1] & 0x7f;
                int passwordState = ((hex[1] & 0x80) >> 7);
                mScooterListener.onLockState(hex, lockType, passwordState);
                break;
            case 0x08:
                String paw = BleStrUtils.byte2HexStr(hex).replace("08 ", "").replace(" ", "");
                mScooterListener.onAfterPassword(hex, paw);
                break;
            case 0x0b:
                int unlockShutDownTime = ((hex[1] & 0x7f) << 8) + (hex[2] & 0xff);
                int lockShutDownTime = ((hex[3] & 0x7f) << 8) + (hex[4] & 0xff);
                mScooterListener.onAutoShutDownTime(hex, unlockShutDownTime, lockShutDownTime);
                break;
            case 0x0c:
                float mileage = (((hex[1] & 0xff) << 8) + (hex[2] & 0xff)) / 10f;
                int second = (((hex[3] & 0xff) << 8) + (hex[4] & 0xff));
                mScooterListener.onSingleMileageAndTime(hex, mileage, second);
                break;
            case 0x0d:

                long totalMileage1 = (hex[1] & 0xffL) << 24;
                long totalMileage2 = (hex[2] & 0xffL) << 16;
                long totalMileage3 = (hex[3] & 0xffL) << 8;
                long totalMileage4 = (hex[4] & 0xffL);
                float totalMileage = (totalMileage1 + totalMileage2 + totalMileage3 + totalMileage4) / 10f;
                mScooterListener.onTotalMileage(hex, totalMileage);
                break;
            case 0x0e:
                int temp = (hex[1] & 0x7f);
                int currentType = (hex[2] & 0x01);
                float current = (((hex[3] & 0x7f) << 8) + (hex[4] & 0xff)) / 10f;
                float voltage = (((hex[5] & 0xff) << 8) + (hex[6] & 0xff)) / 10f;
                int totalCapacity = (((hex[7] & 0xff) << 8) + (hex[8] & 0xff));
                int chargeAndDischargeTimes = (((hex[9] & 0xff) << 8) + (hex[10] & 0xff));
                mScooterListener.onBatteryInfo(hex, ((hex[1] & 0x80) >> 7) == 1 ? 0 - temp : temp, currentType
                        , ((hex[3] & 0x80) >> 7) == 1 ? 0 - current : current, voltage, totalCapacity, chargeAndDischargeTimes);
                break;
            case 0x0f:
                byte[] bytes = new byte[hex.length - 5];
                arraycopy(hex, 0, bytes, 1, bytes.length);
                String byteSty = BleStrUtils.byte2HexStr(bytes).replace(" ", "");
                StringBuilder stringBuilder = convertHexToString(byteSty);

                StringBuilder hardwareVersion = convertHexToString(byteSty);
                hardwareVersion.append((hex[hex.length - 4] & 0xff));
                float pre = ((hex[hex.length - 3] & 0xff) / 100f);
                hardwareVersion.append(pre == 0.0 ? ".00" : (pre + "").replace("0.", "."));

                StringBuilder softwareVersion = convertHexToString(byteSty);
                softwareVersion.append((hex[hex.length - 2] & 0xff));
                float pre1 = ((hex[hex.length - 1] & 0xff) / 100f);
                softwareVersion.append(pre1 == 0.0 ? ".00" : (pre1 + "").replace("0.", "."));
                mScooterListener.onBatteryNo(hex, stringBuilder.toString(), hardwareVersion.toString(), softwareVersion.toString());
                break;
            case 0x10:
                ControllerPayload(hex);
                break;
            case 0x11:
                MeterVersion(hex);
                break;
            case 0x13:
            case 0x12:
                int repeat = (hex[1] & 0x01);
                int monday = (hex[1] & 0x02) >> 1;
                int tuesday = (hex[1] & 0x04) >> 2;
                int wednesday = (hex[1] & 0x08) >> 3;
                int thursday = (hex[1] & 0x10) >> 4;
                int friday = (hex[1] & 0x20) >> 5;
                int saturday = (hex[1] & 0x40) >> 6;
                int sunday = (hex[1] & 0x40) >> 7;
                int startHour = (hex[2] & 0xff);
                int startMin = (hex[3] & 0xff);
                int endHour = (hex[4] & 0xff);
                int endMin = (hex[5] & 0xff);
                mScooterListener.onChargeTime(hex, repeat, monday, tuesday, wednesday, thursday, friday, saturday, sunday, startHour, startMin, endHour, endMin);
                break;
            case 0x14:
            case 0x15:
                int capacity = hex[1] & 0xff;
                mScooterListener.onChargeCapacity(hex, capacity);
                break;
            case 0x17:
                int collisionWarmFunction = (hex[1] & 0x01);
                int findCarFunction = (hex[1] & 0x40);
                int autoLockFunction = (hex[1] & 0x20);
                int autoUnLockFunction = (hex[1] & 0x10);
                int moveWarmFunction = (hex[1] & 0x02);
                mScooterListener.onCarWarmAndAutoLock(hex, findCarFunction, autoLockFunction, autoUnLockFunction, moveWarmFunction, collisionWarmFunction);
                break;


        }
    }

    public static void init(BleDevice bleDevice) {
        ailinkScooterBleData = null;
        ailinkScooterBleData = new AilinkScooterBleData(bleDevice);
    }

    public static AilinkScooterBleData getInstance() {
        return ailinkScooterBleData;
    }

    /**
     * 查询接收端类型及支持的功能
     */
    public void getSupportedFunction() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x00;
        sendData(bytes);

    }

    /**
     * 心跳数据交互
     * Heartbeat data interaction
     */
    public void sendHearData() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x01;
        sendData(bytes);

    }

    /**
     * 切换单位  0：公里制，1：英里制
     *
     * @param unit
     */
    public void sendUnitData(int unit) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x02;
        bytes[1] = (byte) (unit & 0x10);
        bytes[2] = 0x10;
        sendData(bytes);
    }

    /**
     * 切换启动模式  0：零启动，1：非零启动
     *
     * @param mode
     */
    public void sendStartModeData(int mode) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x02;
        bytes[1] = (byte) (mode & 0x02);
        bytes[2] = 0x02;
        sendData(bytes);
    }

    /**
     * 定速巡航功能  0：关闭巡航，1：开启巡航
     *
     * @param function
     */
    public void sendCruiseControlFunctionData(int function) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x02;
        bytes[1] = (byte) (function & 0x04);
        bytes[2] = 0x04;
        sendData(bytes);
    }

    /**
     * 清除功能
     *
     * @param type 0x01 清除单次里程时间 0x02 清除总里程 0x04 清除除里程和时间外的其它控制器及仪表参数 0x80 1：恢复出厂设置
     */
    public void sendClearData(int type) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x03;
        bytes[1] = (byte) type;
        bytes[2] = (byte) type;
        sendData(bytes);
    }


    /**
     * 设置模式档位速度
     *
     * @param gear (0-7)
     */
    public void sendGearData(int gear) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x04;
        bytes[1] = (byte) ((gear & 0x07) + 0x08);
        bytes[2] = 0x00;
        sendData(bytes);

    }


    /**
     * 设置灯光关闭
     *
     * @param type 0x01前车灯开关
     */
    public void sendLightCloseData(int type) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x05;
        bytes[1] = 0x00;
        bytes[2] = (byte) type;
        sendData(bytes);

    }

    /**
     * 设置灯光打开
     *
     * @param type 0x01 前车灯开关, 0x02 刹车灯开关 0x04 日行灯开关 0x08 氛围灯开关
     *             0x10 左转灯开关, 0x20 右转灯开关
     */
    public void sendLightOpenData(int type) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x05;
        bytes[1] = (byte) type;
        bytes[2] = (byte) type;
        sendData(bytes);

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
        cmd[0] = 0x06;
        arraycopy(bytesOld, 0, cmd, 1, bytesOld.length);
        arraycopy(bytesNew, 0, cmd, 7, bytesNew.length);
        sendData(cmd);
    }


    /**
     * 锁车/解锁设置
     * //解锁密码有误
     *
     * @param password 6位数字密码
     * @param type     type  0：解锁； 1：上锁； 2：验证密码； 3：开机；4：关机；
     */
    public void setLockCarPassword(int type, String password) {

        byte[] cmd = new byte[8];
        cmd[0] = 0x07;
        cmd[1] = (byte) type;
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        arraycopy(bytes, 0, cmd, 2, bytes.length);
        sendData(cmd);
    }

    /**
     * 查询售后密码
     */
    public void readServicePassword() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x08;
        sendData(bytes);
    }

    /**
     * 设置自动背光
     */
    public void sendBgAuto() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x09;
        bytes[1] = 0x10;
        sendData(bytes);
    }

    /**
     * 手动调节背光 （10-100）
     *
     * @param value
     */
    public void sendBgUnAuto(int value) {
        byte[] bytes = new byte[1];
        bytes[0] = 0x09;
        bytes[1] = (byte) (value & 0x0f);
        sendData(bytes);
    }

    /**
     * 设置开关机时间
     *
     * @param unlockTime
     * @param lockTime
     */
    public void sendAutoShutDownTime(int unlockTime, int lockTime) {
        byte[] bytes = new byte[5];
        bytes[0] = 0x0a;
        bytes[1] = (byte) ((unlockTime & 0xff00) >> 8);
        bytes[2] = (byte) (unlockTime & 0xff);
        bytes[3] = (byte) ((lockTime & 0xff00) >> 8);
        bytes[4] = (byte) (lockTime & 0xff);
        sendData(bytes);
    }

    /**
     * 查询自动关机时间
     */
    public void sendGetShutDownTime() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x0b;
        sendData(bytes);
    }

    /**
     * 查询单次行驶里程时间
     */
    public void sendSingleMileageTime() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x0c;
        sendData(bytes);
    }

    /**
     * 查询查询总里程
     */
    public void sendTotalMileage() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x0d;
        sendData(bytes);
    }


    /**
     * 查询电池信息
     */
    public void sendBatteryInfo() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x0e;
        sendData(bytes);
    }

    /**
     * 查询电池厂商代码及编号
     */
    public void sendBatteryNo() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x0f;
        sendData(bytes);
    }


    /**
     * 查询控制器厂商代码及编号
     */
    public void sendControllerNo() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x10;
        sendData(bytes);
    }


    /**
     * 查询仪表固件版本
     */
    public void sendMeterVersion() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x11;
        sendData(bytes);
    }


    /**
     * @param repeat    1：设置单次充电时间  0：设置重复充电时间
     * @param monday    1：设置充电  0：不设置
     * @param tuesday   1：设置充电  0：不设置
     * @param wednesday 1：设置充电  0：不设置
     * @param thursday  1：设置充电  0：不设置
     * @param friday    1：设置充电  0：不设置
     * @param saturday  1：设置充电  0：不设置
     * @param sunday    1：设置充电  0：不设置
     * @param startHour 始时间小时(24小时制)
     * @param startMin  开始时间分钟
     * @param endHour   结束时间小时(24小时制)
     * @param endMin    结束时间分钟
     */
    public void sendChargeTime(int repeat, int monday, int tuesday,
                               int wednesday,
                               int thursday,
                               int friday,
                               int saturday,
                               int sunday, int startHour, int startMin, int endHour, int endMin) {
        byte[] bytes = new byte[6];
        bytes[0] = 0x12;
        bytes[1] = (byte) ((repeat & 0x01) + (monday & 0x02) + (tuesday & 0x04) + (wednesday & 0x08) + (thursday & 0x10) + (friday & 0x20) + (saturday & 0x40) + (sunday & 0x80));
        bytes[2] = (byte) (startHour & 0xff);
        bytes[3] = (byte) (startMin & 0xff);
        bytes[4] = (byte) (endHour & 0xff);
        bytes[5] = (byte) (endMin & 0xff);
        sendData(bytes);
    }

    /**
     * 读取充电时间
     */
    public void sendCheckChargeTime() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x13;
        sendData(bytes);
    }

    /**
     * 设置充电容量
     *
     * @param capacity 电池电量到达设置百分比
     */
    public void sendSetChargeCapacity(int capacity) {
        byte[] bytes = new byte[2];
        bytes[0] = 0x14;
        bytes[1] = (byte) capacity;
        sendData(bytes);
    }

    /**
     * 获取充电容量
     */
    public void sendGetChargeCapacity() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x15;
        sendData(bytes);
    }

    /**
     * 寻车功能
     *
     * @param state 0：关闭 1：打开
     */
    public void sendFindCarFunction(int state) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x16;
        bytes[1] = (byte) (0x40 & state);
        bytes[2] = 0x40;
        sendData(bytes);
    }


    /**
     * 自动锁车
     *
     * @param state 0：关闭 1：打开
     */
    public void sendAutoLockCar(int state) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x16;
        bytes[1] = (byte) (0x20 & state);
        bytes[2] = 0x20;
        sendData(bytes);
    }


    /**
     * 自动解锁
     *
     * @param state 0：关闭 1：打开
     */
    public void sendAutoUnLockCar(int state) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x16;
        bytes[1] = (byte) (0x10 & state);
        bytes[2] = 0x10;
        sendData(bytes);
    }


    /**
     * 移位报警开关
     *
     * @param state 0：关闭 1：打开
     */
    public void sendMoveWarm(int state) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x16;
        bytes[1] = (byte) (0x02 & state);
        bytes[2] = 0x02;
        sendData(bytes);
    }

    /**
     * 碰撞报警开关
     *
     * @param state 0：关闭 1：打开
     */
    public void sendCollisionWarm(int state) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x16;
        bytes[1] = (byte) (0x01 & state);
        bytes[2] = 0x01;
        sendData(bytes);
    }

    /**
     * 读取车辆报警、自动锁
     *
     * @param
     */
    public void sendCarWarmAndLock() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x17;
        sendData(bytes);
    }

    /**
     * 寻车
     *
     * @param
     */
    public void sendFindCar() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x18;
        bytes[1] = 0x01;
        sendData(bytes);
    }


    /**
     *  导航数据
     * @param state  导航状态 0：未开启  1：开启
     * @param direction  1，直行 2，向左转弯 3，向右转弯 4，左侧掉头 5，右侧掉头
     * @param directionMileage 当前方向剩余路程，单位M
     * @param mileage 总剩余路程，单位M
     */
    public void sendNavigation(int state,int direction, int directionMileage, long mileage) {
        byte[] bytes = new byte[9];
        bytes[0] = 0x19;
        bytes[1] = (byte) state;
        bytes[2] = (byte) direction;
        bytes[3] = (byte) ((directionMileage & 0xff00) >> 8);
        bytes[4] = (byte) (directionMileage & 0xff);

        bytes[5] = (byte) ((mileage & 0xff000000L) >> 24);
        bytes[6] = (byte) ((mileage & 0x00ff0000L) >> 16);
        bytes[7] = (byte) ((mileage & 0x0000ff00L) >> 8);
        bytes[8] = (byte) ((mileage & 0x000000ffL));

        sendData(bytes);
    }


    public void setScooterListener(ScooterListener scooterListener) {
        mScooterListener = scooterListener;
    }


    private void headerPayload(byte[] hex) {
        int batteryState = (hex[1] & 0x80) >> 7;
        int battery = (hex[1] & 0x7f);
        float currentSpeed = ((hex[2] & 0x7f) << 8) + (hex[3] & 0xff) / 10f;
        int deviceState = (hex[4] & 0x80) >> 7;
        int cruiseControlState = (hex[4] & 0x20) >> 5;
        int unit = (hex[4] & 0x10) >> 4;
        int cruiseControlSwitchState = (hex[4] & 0x04) >> 2;
        int startMode = (hex[4] & 0x02) >> 1;
        int lockState = (hex[4] & 0x01);
        int maxSpeed = (hex[5] & 0xf8) >> 3;
        int currentGear = (hex[5] & 0x07);
        int supportGear = (hex[6] & 0xf0) >> 4;
        int handlerbarState = (hex[6] & 0x08) >> 3;
        int electronicBrakeState = (hex[6] & 0x04) >> 2;
        int mechanicalBrakeState = (hex[6] & 0x02) >> 1;
        int motorState = (hex[6] & 0x01);

        int headingLight = hex[7] & 0x01;
        int brakeLight = (hex[7] & 0x02) >> 1;
        int dayLight = (hex[7] & 0x04) >> 2;
        int ambientLight = (hex[7] & 0x08) >> 3;
        int leftLight = (hex[7] & 0x10) >> 4;
        int rightLight = (hex[7] & 0x20) >> 5;

        int errorCommunication = hex[7] & 0x01;
        int errorHandlerBar = (hex[7] & 0x02) >> 1;
        int errorBrake = (hex[7] & 0x04) >> 2;
        int errorMotorHall = (hex[7] & 0x08) >> 3;
        int errorMotor = (hex[7] & 0x10) >> 4;
        int errorControl = (hex[7] & 0x20) >> 5;
        int errorBattery = (hex[7] & 0x40) >> 6;
        float singleMileage = ((hex[8] & 0x7f) << 8) + (hex[9] & 0xff) / 10f;
        int singleTime = ((hex[10] & 0x7f) << 8) + (hex[11] & 0xff);
        int updateState = ((hex[12] & 0xff));
        mScooterListener.onHeartData(hex, batteryState, battery, currentSpeed, deviceState, cruiseControlState, unit, cruiseControlSwitchState,
                startMode, lockState, maxSpeed, 0, currentGear, supportGear, handlerbarState, electronicBrakeState, mechanicalBrakeState,
                motorState, rightLight, leftLight, ambientLight, dayLight, brakeLight, headingLight, errorBattery, errorControl, errorMotor, errorMotorHall, errorBrake, errorHandlerBar, errorCommunication, singleMileage, singleTime, updateState);

    }

    private void ControllerPayload(byte[] data) {
        byte[] bytes = new byte[4];
        bytes[0] = data[1];
        bytes[1] = data[2];
        bytes[2] = data[3];
        bytes[3] = data[4];
        String byteSty = BleStrUtils.byte2HexStr(bytes).replace(" ", "");
        StringBuilder stringBuilder = convertHexToString(byteSty);

        StringBuilder hardwareVersion = convertHexToString(byteSty);
        hardwareVersion.append((data[5] & 0xff));
        float pre = ((data[6] & 0xff) / 100f);
        hardwareVersion.append(pre == 0.0 ? ".00" : (pre + "").replace("0.", "."));
        StringBuilder softwareVersion = convertHexToString(byteSty);
        int next = (data[7] & 0xff);
        float next1 = (data[8] & 0xff) / 100f;
        softwareVersion.append(next);
        softwareVersion.append(next1 == 0.0 ? ".00" : (next1 + "").replace("0.", "."));
        mScooterListener.onControllerVersion(data, stringBuilder.toString(), hardwareVersion.toString(), softwareVersion.toString());


    }


    private void MeterVersion(byte[] hex) {
        byte[] bytes = new byte[hex.length - 5];
        arraycopy(hex, 0, bytes, 1, bytes.length);
        String byteSty = BleStrUtils.byte2HexStr(bytes).replace(" ", "");
        StringBuilder stringBuilder = convertHexToString(byteSty);

        StringBuilder hardwareVersion = convertHexToString(byteSty);
        hardwareVersion.append((hex[hex.length - 4] & 0xff));
        float pre = ((hex[hex.length - 3] & 0xff) / 100f);
        hardwareVersion.append(pre == 0.0 ? ".00" : (pre + "").replace("0.", "."));

        StringBuilder softwareVersion = convertHexToString(byteSty);
        softwareVersion.append((hex[hex.length - 2] & 0xff));
        float pre1 = ((hex[hex.length - 1] & 0xff) / 100f);
        softwareVersion.append(pre1 == 0.0 ? ".00" : (pre1 + "").replace("0.", "."));
        mScooterListener.onMeterVersion(hex, stringBuilder.toString(), hardwareVersion.toString(), softwareVersion.toString());

    }

    private void sendData(byte[] bytes) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(0x33, bytes);
        sendData(sendMcuBean);
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

    public interface ScooterListener {
        /**
         * 0:不支持 1:支持
         *
         * @param payload
         * @param MeterBridgeOrBlePayload 0:仪表桥接 1:BLE透传
         * @param autoBgLight             自动背光
         * @param unAutoBgLight           手动背光
         * @param chargeManage            充电时间管理
         * @param chargeCapacityManage    充电容量管理
         * @param navigation              导航数据
         * @param weatherPush             天气推送
         * @param msgPush                 消息推送
         * @param cruiseControl           定速巡航功能
         * @param cruiseControlSwitch     定速巡航开关
         * @param zeroOrUnzeroStart       零启动、非零启动切换
         * @param boostMode               助力模式
         * @param appSwitchMachine        APP开关机
         * @param controllerNo            控制器厂商代码编号
         * @param batteryInfo             电池信息
         * @param batteryNo               电池厂商代码编号
         * @param rightLight              右转灯开关
         * @param leftLight               左转灯开关
         * @param ambientLight            氛围灯开关
         * @param dayLight                日行灯开关
         * @param brakeLight              刹车灯开关
         * @param headingLight            前车灯开关
         * @param findCar                 寻车功能
         * @param autoLock                自动锁车
         * @param autoUnLock              自动解锁
         * @param moveWarm                车辆移位报警
         * @param collisionWarm           车辆碰撞报警
         */
        void onSupportFunction(byte[] payload, int MeterBridgeOrBlePayload, int autoBgLight, int unAutoBgLight,
                               int chargeManage, int chargeCapacityManage, int navigation, int weatherPush, int msgPush, int cruiseControl,
                               int cruiseControlSwitch, int zeroOrUnzeroStart, int boostMode,
                               int appSwitchMachine, int controllerNo, int batteryInfo, int batteryNo, int rightLight, int leftLight, int ambientLight,
                               int dayLight, int brakeLight, int headingLight, int findCar, int autoLock, int autoUnLock, int moveWarm, int collisionWarm);

        /**
         * @param batteryState             充电状态，0：未充电；1：充电中
         * @param battery                  电池电量百分比(0-100）
         * @param currentSpeed             车辆实时速度(0.1)
         * @param deviceState              开关机状态 0：开机状态，1：关机状态
         * @param cruiseControlState       巡航状态， 0：未巡航，1：巡航中
         * @param unit                     当前单位，0：公里制，1：英里制
         * @param cruiseControlSwitchState 巡航功能开关状态，0:巡航关闭，1：巡航开启
         * @param startMode                启动方式,0:零启动，1：非零启动
         * @param lockState                锁车状态,0:解锁，1：锁车
         * @param maxSpeed                 车辆支持的最高速度，单位KM/H
         * @param minGearStartZero         最低档位是否从0开始：0：表示档位从1开始， 1：表示档位从0开始
         * @param currentGear              当前档位
         * @param supportGear              支持的档位个数
         * @param handlerbarState          转把有效状态，0：无效，1：有效
         * @param electronicBrakeState     电子刹车状态，0：未刹车，1：刹车中
         * @param mechanicalBrakeState     机械刹车状态，0：未刹车，1：刹车中
         * @param motorState               电机运行状态，0：电机未运行；1：电机运行
         * @param rightLight               右转灯开关，0：关闭，1：开启
         * @param leftLight                左转灯开关，0：关闭，1：开启
         * @param ambientLight             氛围灯开关，0：关闭，1：开启
         * @param dayLight                 日行灯开关，0：关闭，1：开启
         * @param brakeLight               刹车灯开关，0：关闭，1：开启
         * @param headingLight             前车灯开关，0：关闭，1：开启
         * @param errorBattery             电池故障，0：无故障，1：有故障
         * @param errorControl             控制器故障，0：无故障，1：有故障
         * @param errorMotor               电机相线或者MOS管短路故障，0：无故障，1：有故障
         * @param errorMotorHall           电机霍尔故障，0：无故障，1：有故障
         * @param errorBrake               刹车故障，0：无故障，1：有故障
         * @param errorHandlerBar          转把故障，0：无故障，1：有故障
         * @param errorCommunication       通讯故障，0：无故障，1：有故障
         * @param singleMileage            单次行驶里程
         * @param singleTime               单次行驶时间
         * @param updateState              升级状态 0：未升级1：仪表升级中3：控制器升级中
         */
        void onHeartData(byte[] payload, int batteryState, int battery, float currentSpeed, int deviceState, int cruiseControlState,
                         int unit, int cruiseControlSwitchState, int startMode, int lockState, int maxSpeed, int minGearStartZero,
                         int currentGear, int supportGear, int handlerbarState, int electronicBrakeState,
                         int mechanicalBrakeState, int motorState, int rightLight, int leftLight, int ambientLight,
                         int dayLight, int brakeLight, int headingLight, int errorBattery, int errorControl, int errorMotor, int errorMotorHall, int errorBrake,
                         int errorHandlerBar, int errorCommunication, float singleMileage, int singleTime, int updateState);


        /**
         * 修改密码回调
         *
         * @param payload
         * @param result  0：成功  1：失败  2：不支持
         */
        void onChangePassword(byte[] payload, int result);

        /**
         * 解锁及开关机
         *
         * @param payload
         * @param result         0：解锁； 1：上锁； 2：验证密码
         * @param passwordResult 0：密码正确，1：密码错误(锁车时，不校验密码，此bit位无效)
         */
        void onLockState(byte[] payload, int result, int passwordResult);

        /**
         * 售后密码
         *
         * @param payload
         * @param afterPassword
         */
        void onAfterPassword(byte[] payload, String afterPassword);

        /**
         * 查询自动关机时间
         *
         * @param payload
         * @param unlockTime 开锁状态下关机时间
         * @param lockTime   上锁状态下关机时间
         */
        void onAutoShutDownTime(byte[] payload, int unlockTime, int lockTime);


        /**
         * 查询单次行驶里程时间
         *
         * @param payload
         * @param mileage 单次行驶里程(单位:0.1km)
         * @param second  单次行驶时间(单位:秒)
         */
        void onSingleMileageAndTime(byte[] payload, float mileage, int second);

        /**
         * 查询总里程
         *
         * @param payload
         * @param mileage
         */
        void onTotalMileage(byte[] payload, float mileage);


        /**
         * @param payload
         * @param temp                    电池温度 0位正，1为负，单位℃
         * @param currentType             0，实时电流；1：电池限流
         * @param current                 电流  单位0.1A
         * @param voltage                 电压 0.1V
         * @param totalCapacity           电池总容量(单位mAh)
         * @param chargeAndDischargeTimes 电池充放电次数
         */
        void onBatteryInfo(byte[] payload, int temp, int currentType, float current, float voltage, int totalCapacity, int chargeAndDischargeTimes);

        /**
         * @param payload
         * @param no              厂商代码编号 （数字或字母的ASCII码）
         * @param hardwareVersion 硬件版本
         * @param softwareVersion 软件版本
         */
        void onBatteryNo(byte[] payload, String no, String hardwareVersion, String softwareVersion);

        /**
         * 控制器版本号
         *
         * @param payload
         * @param no              项目代码 （数字或字母的ASCII码）
         * @param hardwareVersion 硬件版本
         * @param softwareVersion 软件版本
         */
        void onControllerVersion(byte[] payload, String no, String hardwareVersion, String softwareVersion);

        /**
         * @param payload
         * @param no              项目代码 （数字或字母的ASCII码）
         * @param hardwareVersion 硬件版本
         * @param softwareVersion 软件版本
         */
        void onMeterVersion(byte[] payload, String no, String hardwareVersion, String softwareVersion);

        /**
         * 充电时间
         *
         * @param payload
         * @param repeat    1：设置单次充电时间 0：设置重复充电时间
         * @param monday
         * @param tuesday
         * @param wednesday
         * @param thursday
         * @param friday
         * @param saturday
         * @param sunday
         * @param startHour 充电开始时间小时(24小时制)
         * @param startMin  充电开始时间分钟
         * @param endHour   充电结束时间小时(24小时制)
         * @param endMin    充电结束时间分钟
         */
        void onChargeTime(byte[] payload, int repeat, int monday, int tuesday,
                          int wednesday,
                          int thursday,
                          int friday,
                          int saturday,
                          int sunday, int startHour, int startMin, int endHour, int endMin);

        /**
         * 充电容量
         *
         * @param payload
         * @param capacity
         */
        void onChargeCapacity(byte[] payload, int capacity);

        /**
         * @param payload
         * @param findCar       寻车功能 0：关闭  1：打开
         * @param autoLock      自动锁车 0：关闭  1：打开
         * @param autoUnlock    自动解锁 0：关闭  1：打开
         * @param moveCar       移位报警开关 0：关闭  1：打开
         * @param collisionWarm 碰撞报警开关 0：关闭  1：打开
         */
        void onCarWarmAndAutoLock(byte[] payload, int findCar, int autoLock, int autoUnlock, int moveCar, int collisionWarm);
    }
}
