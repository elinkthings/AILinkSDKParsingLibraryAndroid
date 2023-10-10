package cn.net.aicare.modulelibrary.module.RopeSkipping;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleHandshakeListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

public class RopeSkippingBleData extends BaseBleDeviceData {

    private static RopeSkippingBleData ropeSkippingBleData;

    private RopeSkipRecord ropeSkipBean;
    List<TripRopeBean> tripRopeBeanList;
    private int maxLoopNum = 0;
    private OnRopeSkipCallBack mOnRopeSkipCallBack;
    public static int[] LongXiang = new int[]{0x638A6D8A, 0x8F8C4E63, 0x6B51534E, 0x7566624F};

    private RopeSkippingBleData(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleHandshakeListener(new OnBleHandshakeListener() {
            @Override
            public void onHandshake(boolean status) {
                BleLog.i("握手状态：" + status);
                if (!status) {
                    bleDevice.disconnect();
                }
            }
        });

    }

    @Override
    public void onNotifyData(String uuid, byte[] bytes, int type) {
        BleLog.i("接收到的数据：" + BleStrUtils.byte2HexStr(bytes));
        if (mOnRopeSkipCallBack != null) {
            int cmd = bytes[0] & 0xff;
            switch (cmd) {

                case 0x10:
                    int jumpStatus = bytes[1] & 0xff;
                    int mode = bytes[2] & 0xff;
                    int jumpValueL = bytes[3] & 0xff;
                    int jumpValueH = (bytes[4] & 0xff) << 8;
                    int currentCumulativeTimeL = (bytes[5] & 0xff);
                    int currentCumulativeTimeH = (bytes[6] & 0xff) << 8;
                    int currentCumulativeJumpL = (bytes[7] & 0xff);
                    int currentCumulativeJumpH = (bytes[8] & 0xff) << 8;
                    int battery = (bytes[9] & 0xff);
                    mOnRopeSkipCallBack.onCurrentData(jumpStatus, mode,
                            jumpValueL + jumpValueH, currentCumulativeJumpL + currentCumulativeJumpH,
                            currentCumulativeTimeL + currentCumulativeTimeH, battery);
                    break;
                case 0x20:
                    int totalPackNum = (bytes[1] & 0xf0) >> 4;
                    int currentPackNum = bytes[1] & 0x0f;
                    if (currentPackNum == 0) {
                        long time1 = (bytes[5] & 0xffL) << 24;
                        int time2 = (bytes[4] & 0xff) << 16;
                        int time3 = (bytes[3] & 0xff) << 8;
                        int time4 = (bytes[2] & 0xff);
                        int mode20 = bytes[6] & 0xff;
                        int jump20ValueL = bytes[7] & 0xff;
                        int jump20ValueH = (bytes[8] & 0xff) << 8;

                        ropeSkipBean = new RopeSkipRecord();
                        ropeSkipBean.setCreateTime((time1 + time2 + time3 + time4) * 1000L);
                        ropeSkipBean.setSkipModel(mode20);
                        ropeSkipBean.setSkipModelValue(jump20ValueH + jump20ValueL);
                        tripRopeBeanList = new ArrayList<>();
                    } else if (currentPackNum == 1) {
                        int totalTimeL = bytes[2] & 0xff;
                        int totalTimeH = (bytes[3] & 0xff) << 8;
                        int totalJumpL = bytes[4] & 0xff;
                        int totalJumpH = (bytes[5] & 0xff) << 8;
                        int avgRateL = bytes[6] & 0xff;
                        int avgRateH = (bytes[7] & 0xff) << 8;
                        int fastRateL = bytes[8] & 0xff;
                        int fastRateH = (bytes[9] & 0xff) << 8;
                        if (ropeSkipBean != null) {
                            ropeSkipBean.setTotalTime(totalTimeH + totalTimeL);
                            ropeSkipBean.setTotalNum(totalJumpL + totalJumpH);
                            ropeSkipBean.setAvgNum((avgRateH + avgRateL));
                            ropeSkipBean.setMaxNum((fastRateH + fastRateL));
                        }

                        if (totalPackNum == currentPackNum) {
                            if (ropeSkipBean != null && ropeSkipBean.getTotalNum() != null) {
                                ropeSkipBean.setStopNum(0);
                                ropeSkipBean.setMaxLoopNum(ropeSkipBean.getTotalNum());
                                mOnRopeSkipCallBack.onFinish(ropeSkipBean);
                            }
                        }
                    } else {

                        byte[] tripRopeByte = new byte[bytes.length - 2];
                        System.arraycopy(bytes, 2, tripRopeByte, 0, tripRopeByte.length);

                        for (int i = 0; i < tripRopeByte.length / 4; i++) {
                            int tripTimeL = tripRopeByte[i * 4] & 0xff;
                            int tripTimeH = (tripRopeByte[i * 4 + 1] & 0xff) << 8;
                            int tripNumL = tripRopeByte[i * 4 + 2] & 0xff;
                            int tripNumH = (tripRopeByte[i * 4 + 3] & 0xff) << 8;
                            int tripNum = tripNumH + tripNumL;
                            int tripTime = tripTimeH + tripTimeL;
                            TripRopeBean tripRopeBean = new TripRopeBean();
                            tripRopeBean.setTripJump(tripNum);
                            tripRopeBean.setTripTime(tripTime);
                            if (tripNum != 0xffff && tripTime != 0xffff) {
                                tripRopeBeanList.add(tripRopeBean);
                                if (maxLoopNum <= tripNum) {
                                    maxLoopNum = tripNum;
                                }
                            }
                        }

                        if (totalPackNum == currentPackNum) {
                            if (ropeSkipBean != null && ropeSkipBean.getTotalNum() != null) {
                                if (maxLoopNum == 0) {
                                    maxLoopNum = ropeSkipBean.getTotalNum();
                                }
                                ropeSkipBean.setStopNum(tripRopeBeanList.size());
                                ropeSkipBean.setMaxLoopNum(maxLoopNum);
                                ropeSkipBean.setStopDetail(tripRopeBeanList);
                                mOnRopeSkipCallBack.onFinish(ropeSkipBean);
                            }
                        }
                    }
                    break;
                case 0x04:
                    int defaultTimeL = bytes[1] & 0xff;
                    int defaultTimeH = (bytes[2] & 0xff) << 8;
                    mOnRopeSkipCallBack.onResultTimerAndCountDownNum(2, defaultTimeH + defaultTimeL);
                    break;
                case 0x05:
                    int defaultNumL = bytes[1] & 0xff;
                    int defaultNumH = (bytes[2] & 0xff) << 8;
                    mOnRopeSkipCallBack.onResultTimerAndCountDownNum(3, defaultNumH + defaultNumL);

                    break;
                case 0x01:
                case 0x02:
                case 0x03:
                    mOnRopeSkipCallBack.onResultStatus(bytes[0] & 0xff, bytes[1] & 0xff);
                    break;
                case 0xb0:

                    int status = bytes[1] & 0xff;
                    mOnRopeSkipCallBack.onBindResult(status);
                    break;
            }

        }
    }



    public OnRopeSkipCallBack getOnRopeSkipCallBack() {
        return mOnRopeSkipCallBack;
    }

    public void setOnRopeSkipCallBack(OnRopeSkipCallBack onRopeSkipCallBack) {
        mOnRopeSkipCallBack = onRopeSkipCallBack;
    }

    private List<RopeSkipRecord> offLineList;
    private boolean isLastData = false;

    @Override
    public void onNotifyDataA6(byte[] hex) {
        super.onNotifyDataA6(hex);
        int type = hex[0] & 0xff;
        switch (type) {
            case 0xF1:
                int mytype = hex[1] & 0xff;
                if (mytype == 0x01) {
                    isLastData = false;
                    offLineList = new ArrayList<>();
                    maxLoopNum = 0;
                    int numL = hex[3] & 0xff;
                    int numH =(hex[4]&0xff)<<8;
                    if (numH+numL==0){
                        mOnRopeSkipCallBack.onFinishOffHistory(null);
                    }
                } else {
                    int totalPackNum = (hex[1] & 0xf0) >> 4;
                    int currentPackNum = hex[1] & 0x0f;
                    if (currentPackNum == 0) {
                        //第一个包
                        long time1 = (hex[5] & 0xffL) << 24;
                        int time2 = (hex[4] & 0xff) << 16;
                        int time3 = (hex[3] & 0xff) << 8;
                        int time4 = (hex[2] & 0xff);
                        int mode20 = hex[6] & 0xff;
                        int jump20ValueL = hex[7] & 0xff;
                        int jump20ValueH = (hex[8] & 0xff) << 8;
                        int totalNumber = (hex[9] & 0xff) + ((hex[10] & 0xff) << 8);
                        int currentNumber = (hex[11] & 0xff) + ((hex[12] & 0xff) << 8);
                        if (totalNumber - 1 == currentNumber) {
                            //历史记录的条数
                            isLastData = true;
                        }
                        ropeSkipBean = new RopeSkipRecord();
                        ropeSkipBean.setCreateTime((time1 + time2 + time3 + time4) * 1000L);
                        ropeSkipBean.setSkipModel(mode20);
                        ropeSkipBean.setSkipModelValue(jump20ValueH + jump20ValueL);
                        tripRopeBeanList = new ArrayList<>();
                    } else if (currentPackNum == 1) {
                        int totalTimeL = hex[2] & 0xff;
                        int totalTimeH = (hex[3] & 0xff) << 8;
                        int totalJumpL = hex[4] & 0xff;
                        int totalJumpH = (hex[5] & 0xff) << 8;
                        int avgRateL = hex[6] & 0xff;
                        int avgRateH = (hex[7] & 0xff) << 8;
                        int fastRateL = hex[8] & 0xff;
                        int fastRateH = (hex[9] & 0xff) << 8;
                        if (ropeSkipBean != null) {
                            ropeSkipBean.setTotalTime(totalTimeH + totalTimeL);
                            ropeSkipBean.setTotalNum(totalJumpL + totalJumpH);
                            ropeSkipBean.setAvgNum((avgRateH + avgRateL));
                            ropeSkipBean.setMaxNum((fastRateH + fastRateL));
                            if (totalPackNum == currentPackNum) {
                                //如果没有绊绳，就只有两个包。所以要回调一次
                                ropeSkipBean.setMaxLoopNum(ropeSkipBean.getTotalNum());
                                offLineList.add(ropeSkipBean);
                                if (isLastData) {
                                    mOnRopeSkipCallBack.onFinishOffHistory(offLineList);
                                }
                            }
                        }

                    } else {

                        byte[] tripRopeByte = new byte[hex.length - 2];
                        System.arraycopy(hex, 2, tripRopeByte, 0, tripRopeByte.length);
                        for (int i = 0; i < tripRopeByte.length / 4; i++) {
                            int tripTimeL = tripRopeByte[i * 4] & 0xff;
                            int tripTimeH = (tripRopeByte[i * 4 + 1] & 0xff) << 8;
                            int tripNumL = tripRopeByte[i * 4 + 2] & 0xff;
                            int tripNumH = (tripRopeByte[i * 4 + 3] & 0xff) << 8;
                            int tripNum = tripNumH + tripNumL;
                            TripRopeBean tripRopeBean = new TripRopeBean();
                            tripRopeBean.setTripJump(tripNum);
                            tripRopeBean.setTripTime(tripTimeH + tripTimeL);
                            tripRopeBeanList.add(tripRopeBean);
                            if (maxLoopNum <= tripNum) {
                                maxLoopNum = tripNum;
                            }
                        }

                        if (totalPackNum == currentPackNum) {
                            if (ropeSkipBean != null) {
                                if (maxLoopNum == 0) {
                                    maxLoopNum = ropeSkipBean.getTotalNum();
                                }
                                ropeSkipBean.setMaxLoopNum(maxLoopNum);
                                ropeSkipBean.setStopDetail(tripRopeBeanList);
                                offLineList.add(ropeSkipBean);
                                if (isLastData) {
                                    mOnRopeSkipCallBack.onFinishOffHistory(offLineList);
                                }
                            }
                        }
                    }
                }
        }

    }

    public static void init(BleDevice bleDevice) {
        ropeSkippingBleData = null;

        ropeSkippingBleData = new RopeSkippingBleData(bleDevice);
    }

    public static RopeSkippingBleData getInstance() {
        return ropeSkippingBleData;
    }


    /**
     * 同步时间搓
     *
     * @param currentTimeSecond
     */
    public void synTime(long currentTimeSecond) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        long time = currentTimeSecond / 1000;
        byte[] bytes = new byte[5];
        bytes[0] = 0x00;
        bytes[1] = (byte) (time & 0x000000ffL);
        bytes[2] = (byte) ((time & 0x0000ff00L) >> 8);
        bytes[3] = (byte) ((time & 0x00ff0000L) >> 16);
        bytes[4] = (byte) ((time & 0xff000000L) >> 24);
        sendMcuBean.setHex(0x2f, bytes);
        sendData(sendMcuBean);
    }


    public void startOrStopMode(int mode, int status) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) mode;
        bytes[1] = (byte) status;
        sendMcuBean.setHex(0x2f, bytes);
        sendData(sendMcuBean);
    }

    public void setTimerNum(int second) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[3];
        bytes[0] = 0x04;
        bytes[2] = (byte) ((second & 0xffff) >> 8);
        bytes[1] = (byte) ((second & 0x00ff));
        sendMcuBean.setHex(0x2f, bytes);
        sendData(sendMcuBean);
    }

    public void setCountDownNum(int num) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[3];
        bytes[0] = 0x05;
        bytes[2] = (byte) ((num & 0xffff) >> 8);
        bytes[1] = (byte) ((num & 0x00ff));
        sendMcuBean.setHex(0x2f, bytes);
        sendData(sendMcuBean);

    }

    public void offlineHistory(int type) {
        SendBleBean sendBleBean = new SendBleBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0xF1;
        bytes[1] = (byte) type;
        sendBleBean.setHex(bytes);
        sendData(sendBleBean);
    }

    public void questBind() {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0xB0;
        bytes[1] = (byte) 0x01;
        sendMcuBean.setHex(0x02f, bytes);
        sendData(sendMcuBean);
    }

}
