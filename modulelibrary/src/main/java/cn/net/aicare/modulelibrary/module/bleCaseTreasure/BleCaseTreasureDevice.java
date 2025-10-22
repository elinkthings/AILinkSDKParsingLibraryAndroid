package cn.net.aicare.modulelibrary.module.bleCaseTreasure;

import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.net.aicare.modulelibrary.module.utils.ByteUtils;

/**
 * ble病例宝对象
 *
 * @author xing
 * @date 2025/06/03
 */
public class BleCaseTreasureDevice extends BaseBleDeviceData implements OnBleVersionListener, OnBleMtuListener {
    private final static int CID = 0x80;
    private int mMtu = 64;
    private List<OnBleCaseTreasureListener> mListeners = new ArrayList<>();

    public BleCaseTreasureDevice(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleVersionListener(this);
        bleDevice.setOnBleMtuListener(this);
        bleDevice.setMtu(517);
    }


    //------------发送--------------

    public void getVersion() {
        byte[] bleVersion46 = BleSendCmdUtil.getInstance().getBleVersion46();
        SendBleBean bean = new SendBleBean(bleVersion46);
        sendData(bean);
    }

    public void getDeviceStatus() {
        SendMcuBean bean = new SendMcuBean();
        byte[] playLoad = new byte[1];
        playLoad[0] = 0x01;
        bean.setHex(CID, playLoad);
        sendData(bean);
    }

    public void setSpeed(int speed) {
        SendMcuBean bean = new SendMcuBean();
        byte[] playLoad = new byte[3];
        playLoad[0] = 0x03;
        playLoad[1] = (byte) speed;
        playLoad[2] = (byte) (speed >> 8);
        bean.setHex(CID, playLoad);
        sendData(bean);
    }

    /**
     * _发送地图
     * index从1开始
     */
    private Map<Integer, StrByteBean> mSendMap = new HashMap<>();

    public void sendString(String charsetName, String data) {
        if (charsetName.equalsIgnoreCase("GBK")) {
            sendStringForGBK(data);
        } else {
            sendStringForUnicode(data);
        }
        //装载数据完成,发送开始指令
        startSendData();
    }

    private void sendStringForUnicode(String data) {
        mSendMap.clear();
        int playLoadLength = getSendStrHexLength(4);
        List<Byte> list = new ArrayList<>();
        int mOldType = 2;
        for (int i = 0; i < data.length(); ) {
            int codePoint = data.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            int type = 2;
            byte[] newBytes = null;
            if (charCount == 1) {
                //2个字节
                newBytes = new byte[2];
                newBytes[0] = (byte) (codePoint >> 8);
                newBytes[1] = (byte) codePoint;
            } else {
                type = 4;
                char high = data.charAt(i);
                char low = data.charAt(i + 1);
                boolean highSurrogate = Character.isHighSurrogate(high);
                boolean lowSurrogate = Character.isLowSurrogate(low);
                if (highSurrogate && lowSurrogate) {
                    int fullCodePoint = Character.toCodePoint(high, low);
                    newBytes = new byte[4];
                    newBytes[0] = (byte) (fullCodePoint >> 24);
                    newBytes[1] = (byte) (fullCodePoint >> 16);
                    newBytes[2] = (byte) (fullCodePoint >> 8);
                    newBytes[3] = (byte) (fullCodePoint >> 0);
                }

            }

            if (mOldType != type) {
                addStrByteBean(mOldType, list);
                mOldType = type;
            }
            i += charCount;
            if (newBytes == null) {
                continue;
            }
            for (byte newByte : newBytes) {
                list.add(newByte);
            }
            if (list.size() >= playLoadLength) {
                addStrByteBean(mOldType, list);
            }
        }
        while (!list.isEmpty()) {
            addStrByteBean(mOldType, list);
        }
    }


    private void addStrByteBean(int type, List<Byte> list) {
        int playLoadLength = getSendStrHexLength(4);
        int byteArrayLen = Math.min(list.size(), playLoadLength);
        if (byteArrayLen == 0) {
            return;
        }
        byte[] byteArray = new byte[byteArrayLen];
        int index = 0;
        for (int i = 0; i < byteArrayLen; i++) {
            byteArray[i] = list.get(i);
            index++;
        }
        list.subList(0, index).clear();
        StrByteBean bean = new StrByteBean(type, byteArray);
        mSendMap.put(mSendMap.size() + 1, bean);
    }


    private void sendStringForGBK(String data) {
        mSendMap.clear();
        byte[] gbks = new byte[data.length() * 2];
        for (int i = 0; i < data.length(); i++) {
            String string = data.substring(i, i + 1);
            byte[] bytes = string.getBytes(Charset.forName("GBK"));
            int byteL = bytes.length;
            if (byteL < 2) {
                gbks[i * 2] = 0x00;
                gbks[i * 2 + 1] = bytes[0];
            } else {
                gbks[i * 2] = bytes[byteL - 2];
                gbks[i * 2 + 1] = bytes[byteL - 1];
            }
        }

        int index = 0;
        int playLoadLength = getSendStrHexLength(2);
        int group = gbks.length / playLoadLength;
        int last = gbks.length % playLoadLength;
        for (int i = 0; i < group; i++) {
            byte[] playLoad = new byte[playLoadLength];
            System.arraycopy(gbks, index, playLoad, 0, playLoad.length);
            mSendMap.put(mSendMap.size() + 1, new StrByteBean(playLoad));
            index += playLoad.length;
        }
        if (last > 0) {
            byte[] playLoad = new byte[last];
            System.arraycopy(gbks, index, playLoad, 0, playLoad.length);
            mSendMap.put(mSendMap.size() + 1, new StrByteBean(playLoad));
        }
    }


    private int getSendStrHexLength(int type) {
        int playLoadLength = mMtu - 6 - headL;//6是包头包尾,headL是数据帧头
        playLoadLength -= playLoadLength % type;
        return playLoadLength;
    }

    private int headL = 7;

    /**
     * 发送字符串
     *
     * @param index 指数, 从1开始
     * @return {@link SendMcuBean }
     */
    private byte[] sendString(int index) {
        StrByteBean bean = mSendMap.get(index);
        if (bean == null) {
            return null;
        }
        byte[] sendByte = new byte[bean.getHex().length + headL];
        sendByte[0] = 0x02;
        sendByte[1] = 0x01;
        sendByte[2] = (byte) (index);
        sendByte[3] = (byte) (index >> 8);
        sendByte[4] = (byte) (index >> 16);
        sendByte[5] = (byte) (index >> 24);
        sendByte[6] = (byte) bean.getType();
        System.arraycopy(bean.getHex(), 0, sendByte, headL, bean.getHex().length);
        return sendByte;
    }

    /**
     * 开始发送数据
     */
    private void startSendData() {
        int dataId = (int) (System.currentTimeMillis() / 1000);
        BleLog.i("病例ID:" + dataId);
        byte[] playLoad = new byte[6];
        playLoad[0] = 0x02;
        playLoad[1] = 0x00;
        playLoad[2] = (byte) (dataId);
        playLoad[3] = (byte) (dataId >> 8);
        playLoad[4] = (byte) (dataId >> 16);
        playLoad[5] = (byte) (dataId >> 24);
        sendData(playLoad);

    }

    private void stopSendData() {
        byte[] playLoad = new byte[2];
        playLoad[0] = 0x02;
        playLoad[1] = 0x02;
        sendData(playLoad);
    }


    private void sendData(byte[] hex) {
        SendMcuBean bean = new SendMcuBean();
        bean.setHex(CID, hex);
        sendData(bean);
        for (OnBleCaseTreasureListener listener : mListeners) {
            listener.onSendHex(hex);
        }
    }

    private void sendDataNow(byte[] hex) {
        for (OnBleCaseTreasureListener listener : mListeners) {
            listener.onSendHex(hex);
        }
        SendMcuBean bean = new SendMcuBean();
        bean.setHex(CID, hex);
        sendDataNow(bean);

    }

    //-----------返回--------------

    @Override
    public void onBmVersion46(String version) {
        for (OnBleCaseTreasureListener listener : mListeners) {
            listener.onVersion(version);
        }
    }

    @Override
    public void onMtuAvailable(int mtu) {
        mMtu = mtu;
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        //A7数据
        int cmd = hex[0] & 0xFF;
        switch (cmd) {

            case 0x01:
                //设备状态
                parsingDeviceStatus(hex);
                break;
            case 0x02:
                //数据传输
                parsingSendData(hex);
                break;
            case 0x03:
                //数据传输
                for (OnBleCaseTreasureListener listener : mListeners) {
                    listener.onSetSpeed(hex[1] & 0xFF);
                }
                break;

        }
    }


    /**
     * 解析设备状态
     */
    private void parsingDeviceStatus(byte[] hex) {
        int status = hex[1] & 0xFF;
        int dataId = ByteUtils.toIntSmall(new byte[]{hex[2], hex[3], hex[4], hex[5]});
        int sendId = ByteUtils.toIntSmall(new byte[]{hex[6], hex[7], hex[8], hex[9]});
        for (OnBleCaseTreasureListener listener : mListeners) {
            listener.onDeviceStatus(status, dataId, sendId);
        }
    }

    private void parsingSendData(byte[] hex) {
        int type = hex[1] & 0xFF;
        switch (type) {
            case 0x00:
                //开始发送,设备保存病例ID成功,可以开始发送病例数据
                int dataId = ByteUtils.toIntSmall(new byte[]{hex[2], hex[3], hex[4], hex[5]});
                for (OnBleCaseTreasureListener listener : mListeners) {
                    listener.onSendDataId(dataId);
                }
                byte[] sendMcuBean = sendString(1);
                if (sendMcuBean != null) {
                    sendData(sendMcuBean);
                }
                break;
            case 0x01:
                //发送中
                int status = hex[2] & 0xFF;
                if (status == 0) {
                    //发送成功
                    int sendId = ByteUtils.toIntSmall(new byte[]{hex[3], hex[4], hex[5], hex[6]});
                    //继续发送下一帧
                    byte[] sendHex = sendString(sendId + 1);
                    if (sendHex != null) {
                        for (OnBleCaseTreasureListener listener : mListeners) {
                            listener.onSendDataSchedule(sendId, mSendMap.size());
                        }
                        sendDataNow(sendHex);
                    } else {
                        //发送完成
                        stopSendData();
                    }

                } else {
                    //发送失败
                    for (OnBleCaseTreasureListener listener : mListeners) {
                        listener.onSendFail();
                    }
                }

                break;
            case 0x02:
                //发送完成
                for (OnBleCaseTreasureListener listener : mListeners) {
                    listener.onSendDataSchedule(mSendMap.size(), mSendMap.size());
                }
                break;
        }
    }


    public void addListener(OnBleCaseTreasureListener onBleCaseTreasureListener) {
        if (!mListeners.contains(onBleCaseTreasureListener)) {
            mListeners.add(onBleCaseTreasureListener);
        }
    }

    public void removeListener(OnBleCaseTreasureListener onBleCaseTreasureListener) {
        mListeners.remove(onBleCaseTreasureListener);
    }

    public void clearListener() {
        mListeners.clear();
    }

    public interface OnBleCaseTreasureListener {
        void onVersion(String version);

        void onDeviceStatus(int status, int dataId, int sendId);

        void onSendDataSchedule(int cur, int size);

        void onSendDataId(int dataId);

        void onSendFail();

        void onSendHex(byte[] hex);

        void onSetSpeed(int status);

    }


    class StrByteBean {
        int type = 2;//2或者4
        byte[] hex;//不能超过mtu

        public StrByteBean() {
        }

        public StrByteBean(byte[] hex) {
            this.type = 2;
            this.hex = hex;
        }

        public StrByteBean(int type, byte[] hex) {
            this.type = type;
            this.hex = hex;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public byte[] getHex() {
            return hex;
        }

        public void setHex(byte[] hex) {
            this.hex = hex;
        }

        public void addHex(byte[] hex) {

        }
    }

}
