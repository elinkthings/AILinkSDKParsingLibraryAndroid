package cn.net.aicare.modulelibrary.module.LeapWatch;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.AiLinkBleCheckUtil;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.net.aicare.modulelibrary.module.LeapWatch.bean.WatchDataBean;

/**
 * xing<br>
 * 2022/6/13<br>
 * java类作用描述
 */
public abstract class BaseWatchData extends BaseBleDeviceData implements OnBleOtherDataListener, OnBleMtuListener {

    private static final String TAG = BaseWatchData.class.getName();
    /**
     * 握手指令
     */
    private final static int HANDSHAKE_FAILURE = 1;
    protected final static int MINIMAL_PACKAGE = 8;
    protected static BleDevice mBleDevice = null;
    protected int mMtuMax = 245;
    /**
     * 手表握手状态
     */
    private boolean mHandshakeStatus = true;

    /**
     * 握手前的发送数据的队列
     */
    private LinkedList<SendDataBean> mLinkedListHandshake;

    private Handler mHandlerLooper = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDSHAKE_FAILURE:
                    dataHandshake = null;
                    //握手失败
                    BleLog.e(TAG, "握手失败,超时:" + (mBleDevice != null ? mBleDevice.getMac() : ""));
                    onHandshake(false);

                    break;
            }
        }
    };


    /**
     * 发送所有数据
     *
     * @param handshakeStatus 握手是否通过
     */
    private void sendDataAll(boolean handshakeStatus) {
        mHandshakeStatus = false;
        if (handshakeStatus) {
            BleLog.i(TAG, "sendDataAll:" + mLinkedListHandshake.size());
            while (mLinkedListHandshake.size() > 0) {
                SendDataBean sendDataBean = mLinkedListHandshake.pollLast();
                if (sendDataBean != null) {
                    sendData(sendDataBean);
                }
            }
        } else {
            mLinkedListHandshake.clear();
        }
    }


    public BaseWatchData(BleDevice bleDevice) {
        super(bleDevice);
        if (bleDevice != null) {
            mBleDevice = bleDevice;
            bleDevice.setSendDataInterval(200);
            bleDevice.setResend(true);
            bleDevice.setA7Encryption(false);//不加密
            bleDevice.setOnBleOtherDataListener(this);
            bleDevice.setOnBleMtuListener(this);
            mLinkedListHandshake = new LinkedList<>();
            mHandlerLooper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean b = bleDevice.setMtu(517);
                    BleLog.i("setMtu:"+b);
                    if (!b) {
                        bleDevice.setMtu(517);
                        BleLog.i("设置MTU失败,重试一下");
                    }
                }
            }, 200);

        }
    }

    public void disconnect() {
        if (mBleDevice != null)
            mBleDevice.disconnect();
    }


    @Override
    public void onNotifyDataA6(byte[] hex) {
        super.onNotifyDataA6(hex);
    }

    @Override
    public void onHandshake(boolean status) {
        super.onHandshake(status);
        if (!status) {
            sendDataAll(false);
            disconnect();
        } else {
            //握手成功
            sendDataAll(true);

        }
    }

    @Override
    public void onNotifyData(byte[] data, int i) {
        //a7数据

    }

    /**
     * @param mtu mtu,实际使用需要-3, 3是BLE协议包头
     */
    @Override
    public void OnMtu(int mtu) {
        BleLog.i("OnMtu:" + mtu);
        mMtuMax = mtu - 3;
        sendHandshake();
    }

    private List<WatchDataBean> mWatchDataBeanList = new ArrayList<>();

    @Override
    public void onNotifyOtherData(byte[] data) {
        if (data == null || data.length < MINIMAL_PACKAGE) {
            return;
        }
        int cmdStart = data[0] & 0xFF;
        if ((cmdStart != 0xDD) && (cmdStart != 0xEE)) {
            return;
        }
        if (cmdStart == 0xEE) {
            //OTA数据
            return;
        }
        BleLog.iw(TAG, "手表接收：[" + BleStrUtils.byte2HexStr(data) + "]");
        // 将payloads数据取出来
        byte[] hex = new byte[data.length - MINIMAL_PACKAGE];
        System.arraycopy(data, MINIMAL_PACKAGE, hex, 0, hex.length);
        int sum = 0;
        for (byte b : hex) {
            sum = sum + (b & 0xFF);
        }
        int dataSum = ((data[4] & 0xFF) << 8) + (data[5] & 0xFF);
        if (dataSum != (sum & 0xFFFF)) {
            //校验和错误,丢弃
            BleLog.i(TAG, "校验和错误,丢弃:" + BleStrUtils.byte2HexStr(hex) + " MCU:" + dataSum + " APP:" + (sum & 0xFFFF));
            return;
        }

        int no = data[2] & 0xFF;
        int count = data[3] & 0xFF;
        if (count == no) {
            //数据没有分包
            if (mWatchDataBeanList.size() > 0) {
                Collections.sort(mWatchDataBeanList);
                int all = 0;
                List<byte[]> list = new ArrayList<>();
                for (WatchDataBean watchDataBean : mWatchDataBeanList) {
                    all += watchDataBean.getSize();
                    list.add(watchDataBean.getBytes());
                }
                hex = new byte[all];
                int index = 0;
                for (byte[] bytes : list) {
                    System.arraycopy(bytes, 0, hex, index, bytes.length);
                    index += bytes.length;
                }
            }
            mWatchDataBeanList.clear();
            List<TLVDataBean> tlvDataBean = getTLVDataBean(hex);
            for (TLVDataBean dataBean : tlvDataBean) {
                checkWatchData(dataBean);
            }
            onNotifyWatchData(hex);
        } else {
            WatchDataBean watchDataBean = new WatchDataBean(no, hex);
            mWatchDataBeanList.add(watchDataBean);
        }

    }


    /**
     * 是否为握手指令
     *
     * @param bean TLVDataBean
     * @return 是否为握手指令
     */
    protected int isHandshakeCmd(TLVDataBean bean) {
        byte[] typeB = bean.getTypeB();
        if (typeB[0] == 0x00 && typeB[1] == 0x01 && typeB[2] == 0x00 && (bean.getLength() == 0x11)) {
            //是第一次握手指令,设备返回
            return 1;
        } else if (typeB[0] == 0x00 && typeB[1] == 0x02 && typeB[2] == 0x00 && (bean.getLength() == 0x10)) {
            //是第二次握手指令,设备返回
            return 2;
        }
        //不是握手指令
        return 0;
    }

    /**
     * 握手随机值
     */
    private volatile byte[] dataHandshake;

    /**
     * 明文握手
     */
    private void sendHandshake() {
        if (mHandshakeStatus) {
            if (dataHandshake == null) {
                byte[] hex = new byte[19];
                hex[0] = 0x00;
                hex[1] = 0x01;
                hex[2] = 0x00;
                byte[] bytes = initHandshakeArr();
                System.arraycopy(bytes, 0, hex, 3, bytes.length);
                sendDataToWatch(hex, false, true);
                mHandshakeStatus = true;
                //设置超时
                mHandlerLooper.removeMessages(HANDSHAKE_FAILURE);
                mHandlerLooper.sendEmptyMessageDelayed(HANDSHAKE_FAILURE, 5000);
                BleLog.i("发送明文握手");
            }
        }
    }

    /**
     * 第二次握手,发送密文
     */
    private void sendHandshakeTwo(byte[] data) {
        if (data != null) {
            byte[] hex = new byte[20];
            hex[0] = 0x00;
            hex[1] = 0x02;
            hex[2] = 0x00;
            hex[19] = 0x00;
            byte[] bytes = getEncrypt(data);
            System.arraycopy(bytes, 0, hex, 3, bytes.length);
            sendDataToWatch(hex, true, true);
            BleLog.i("发送密文握手");
        }
    }

    /**
     * 校验握手数据是否正确
     */
    private boolean getHandshakeStatus(byte[] data) {
        //加密自己发送的数据
        byte[] appDataHandshake = getEncrypt(dataHandshake);
        //校验两次加密是否一致
        return Arrays.equals(appDataHandshake, data);
    }


    /**
     * 加密数据
     */
    private byte[] getEncrypt(byte[] data) {
        if (data == null) {
            return null;
        }
        //加密自己发送的数据
        byte[] appDataHandshake = com.pinwang.ailinkble.AiLinkPwdUtil.encryptAicare(data, false);
        //校验两次加密是否一致
        return appDataHandshake;
    }


    /**
     * @return 生成随机值
     */
    private byte[] initHandshakeArr() {
        dataHandshake = AiLinkBleCheckUtil.getRandomKey(16);
        return dataHandshake;
    }


    private List<TLVDataBean> getTLVDataBean(byte[] hex) {
        List<TLVDataBean> list = new ArrayList<>();
        int index = 0;
        while (hex.length > index) {
            byte[] type = new byte[3];
            type[0] = hex[index + 0];
            type[1] = hex[index + 1];
            type[2] = hex[index + 2];
            int length = ((hex[index + 3] & 0xFF) << 8) + (hex[index + 4] & 0xFF);
            byte[] value = new byte[length];
            System.arraycopy(hex, index + 5, value, 0, (Math.min(hex.length - 5, length)));
            index += 5 + length;
            TLVDataBean tlvDataBean = new TLVDataBean(hex, type, length, value);
            if (!list.contains(tlvDataBean)) {
                list.add(tlvDataBean);
            } else {
                for (TLVDataBean dataBean : list) {
                    boolean add = dataBean.add(tlvDataBean);
                    if (add) {
                        break;
                    }
                }
            }
        }

        return list;
    }


    /**
     * 检验数据和握手数据验证
     */
    private void checkWatchData(TLVDataBean bean) {
        if (mHandshakeStatus) {
            //握手状态
            if (isHandshakeCmd(bean) == 1) {
                //是握手指令
                byte[] deviceHandshakeData = new byte[16];
                System.arraycopy(bean.getValue(), 0, deviceHandshakeData, 0, deviceHandshakeData.length);
                boolean handshakeStatus = getHandshakeStatus(deviceHandshakeData);
                mHandlerLooper.removeMessages(HANDSHAKE_FAILURE);
                if (handshakeStatus) {
                    //握手成功
                    appOneHandshakeAck();
                    BleLog.i("第一次握手成功");
                } else {
                    onHandshake(false);
                }
                dataHandshake = null;
                return;
            } else if (isHandshakeCmd(bean) == 2) {
                sendHandshakeTwo(bean.getValue());
                onHandshake(true);
                mHandshakeStatus = false;
                BleLog.i("第二次握手成功");
                return;
            }
        }
        onWatchTLVData(bean);
        mWatchDataBeanList.clear();
    }


    /**
     * APP回复第一次握手ack
     */
    public void appOneHandshakeAck() {
        byte[] hex = new byte[3];
        hex[0] = (byte) 0x00;
        hex[1] = (byte) 0x01;
        hex[2] = (byte) 0x00;
        sendDataToWatch(hex, true, true);
    }


    /**
     * @param data        发送的数据,不包含length,需要补充
     * @param callbackCmd 是否为回复指令
     */
    protected void sendDataToWatch(byte[] data, boolean callbackCmd) {
        this.sendDataToWatch(data, callbackCmd, false);
    }

    /**
     * @param data         发送的数据,不包含length,需要补充
     * @param callbackCmd  是否为回复指令
     * @param handshakeCmd 是否为握手指令
     */
    protected void sendDataToWatch(byte[] data, boolean callbackCmd, boolean handshakeCmd) {
        int sendHexSize = data.length + 2;//加上2个长度
        byte[] sendData = new byte[sendHexSize];//T+L+V
        System.arraycopy(data, 0, sendData, 0, 3);//添加type
        byte[] valueLengthB = new byte[2];//内容长度B
        int valueLength = sendHexSize - 3 - 2;//内容长度(去掉T和L)
        valueLengthB[1] = (byte) valueLength;
        valueLengthB[0] = (byte) (valueLength >> 8);
        System.arraycopy(valueLengthB, 0, sendData, 3, valueLengthB.length);//添加length
        System.arraycopy(data, 3, sendData, 3 + valueLengthB.length, valueLength);//添加value


        int mtu = mMtuMax - MINIMAL_PACKAGE;
        if (mtu < 20) {
            mtu = 20;
        }
        int no = 1;
        int count = 1;
        if (sendHexSize > mtu) {
            //发送的数据包大于MTU需要分包
            count = sendHexSize / mtu;
            if (sendHexSize % mtu != 0) {
                count++;
            }
        }


        if (count > 1) {
            for (int i = 1; i <= count; i++) {

                int startIndex = (i - 1) * mtu;
                int sendDataLength = sendData.length - startIndex;
                if (sendDataLength >= mtu) {
                    sendDataLength = mtu;
                }
                byte[] hex = new byte[MINIMAL_PACKAGE + sendDataLength];
                // 包头固定
                hex[0] = (byte) 0xDD;
                if (callbackCmd) {
                    hex[1] = (byte) 0x11;
                } else {
                    hex[1] = (byte) 0x01;
                }
                // 数据赋值
                hex[2] = (byte) i;
                hex[3] = (byte) count;
                System.arraycopy(sendData, startIndex, hex, MINIMAL_PACKAGE, sendDataLength);
                // 计算校验和
                int checkNum = 0;

                for (int i1 = startIndex; i1 < (sendDataLength + startIndex); i1++) {
                    byte datum = sendData[i1];
                    checkNum += (datum & 0xFF);
                }


                hex[4] = (byte) (checkNum >> 8);
                hex[5] = (byte) (checkNum);

                // payloads 长度
                hex[6] = (byte) ((sendDataLength & 0xFF00) >> 8);
                hex[7] = (byte) ((sendDataLength & 0xFF));
                SendDataBean sendDataBean = new SendDataBean(hex, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
                sendWatchData(sendDataBean, handshakeCmd);
            }
        } else {
            byte[] hex = new byte[MINIMAL_PACKAGE + sendHexSize];
            // 包头固定
            hex[0] = (byte) 0xDD;
            if (callbackCmd) {
                hex[1] = (byte) 0x11;
            } else {
                hex[1] = (byte) 0x01;
            }
            hex[2] = (byte) no;
            hex[3] = (byte) count;


            // 计算校验和
            int checkNum = 0;
            for (byte datum : sendData) {
                checkNum += (datum & 0xFF);
            }
            hex[4] = (byte) (checkNum >> 8);
            hex[5] = (byte) (checkNum);
            // payloads 长度
            hex[6] = (byte) ((sendData.length & 0xFF00) >> 8);
            hex[7] = (byte) ((sendData.length & 0xFF));

            //无需分包
            // 数据赋值
            System.arraycopy(sendData, 0, hex, MINIMAL_PACKAGE, sendData.length);
            SendDataBean sendDataBean = new SendDataBean(hex, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
            sendWatchData(sendDataBean, handshakeCmd);
        }


    }


    protected void sendDataToWatch(List<TLVDataBean> list, boolean callbackCmd, boolean handshakeCmd) {

    }


    /**
     * @param bean
     * @param callbackCmd  是否为回复指令
     * @param handshakeCmd 是否为握手指令
     */
    protected void sendDataToWatch(TLVDataBean bean, boolean callbackCmd, boolean handshakeCmd) {
        byte[] value = bean.getValue();
        byte[] typeB = bean.getTypeB();

        int sendHexSize = value.length + 2;//加上2个长度
        byte[] sendData = new byte[sendHexSize];//T+L+V
        System.arraycopy(typeB, 0, sendData, 0, 3);//添加type
        byte[] valueLengthB = new byte[2];//内容长度B
        int valueLength = sendHexSize - 3 - 2;//内容长度(去掉T和L)
        valueLengthB[1] = (byte) valueLength;
        valueLengthB[0] = (byte) (valueLength >> 8);
        System.arraycopy(valueLengthB, 0, sendData, 3, valueLengthB.length);//添加length
        System.arraycopy(value, 0, sendData, 3 + valueLengthB.length, valueLength);//添加value

        byte[] hex = new byte[MINIMAL_PACKAGE + sendHexSize];
        int mtu = mMtuMax - MINIMAL_PACKAGE;
        if (mtu < 20) {
            mtu = 20;
        }
        int no = 1;
        int count = 1;
        if (sendHexSize > mtu) {
            //发送的数据包大于MTU需要分包
            count = sendHexSize / mtu;
            if (sendHexSize % mtu != 0) {
                count++;
            }
        }
        // 包头固定
        hex[0] = (byte) 0xDD;
        if (callbackCmd) {
            hex[1] = (byte) 0x11;
        } else {
            hex[1] = (byte) 0x01;
        }
        hex[2] = (byte) no;
        hex[3] = (byte) count;

        // 计算校验和
        int checkNum = 0;
        for (byte datum : sendData) {
            checkNum += (datum & 0xFF);
        }
        hex[4] = (byte) ((checkNum & 0xFF00) >> 8);
        hex[5] = (byte) ((checkNum & 0xFF));

        // payloads 长度
        hex[6] = (byte) ((sendData.length & 0xFF00) >> 8);
        hex[7] = (byte) ((sendData.length & 0xFF));


        if (count > 1) {
            for (int i = 1; i <= count; i++) {
                // 数据赋值
                hex[2] = (byte) i;
                int startIndex = (i - 1) * mtu;
                int sendDataLength = sendData.length - startIndex;
                if (sendDataLength >= mtu) {
                    sendDataLength = mtu;
                }
                System.arraycopy(sendData, startIndex, hex, MINIMAL_PACKAGE, sendDataLength);

                SendDataBean sendDataBean = new SendDataBean(hex, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
                sendWatchData(sendDataBean, handshakeCmd);
            }
        } else {
            //无需分包
            // 数据赋值
            System.arraycopy(sendData, 0, hex, MINIMAL_PACKAGE, sendData.length);
            SendDataBean sendDataBean = new SendDataBean(hex, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
            sendWatchData(sendDataBean, handshakeCmd);
        }

    }

    /**
     * 发送手表数据
     *
     * @param sendDataBean SendDataBean
     */
    private void sendWatchData(SendDataBean sendDataBean, boolean handshakeCmd) {
        if (!handshakeCmd && mHandshakeStatus && mLinkedListHandshake != null) {
            //握手状态,不发送指令
            BleLog.iw(TAG, "握手状态,不发送指令,加入到发送队列:[" + BleStrUtils.byte2HexStr(sendDataBean.getHex()) + "]");
            mLinkedListHandshake.addFirst(sendDataBean);
            return;
        }
        sendData(sendDataBean);
        BleLog.iw(TAG, "手表发送：[" + BleStrUtils.byte2HexStr(sendDataBean.getHex()) + "]");
    }


    public void onNotifyWatchData( byte[] data) {

    }

    public abstract void onWatchTLVData( TLVDataBean bean);

}
