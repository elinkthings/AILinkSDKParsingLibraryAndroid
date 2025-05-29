package cn.net.aicare.modulelibrary.module.demonEyes;

import android.graphics.Bitmap;

import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BaseBleDeviceData;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleMtuListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 恶魔之眼设备
 *
 * @author xing
 * @date 2024/09/23
 */
public class DemonEyesDevice extends BaseBleDeviceData implements OnBleMtuListener, OnBleOtherDataListener {

    private int cid = DemonEyesConfig.CID;
    private int mPayLoadMtu = 20;
    private static DemonEyesDevice instance;

    /**
     * 获取实例
     *
     * @return {@link DemonEyesDevice}
     */
    public static DemonEyesDevice getInstance() {
        return instance;
    }

    /**
     * 初始化
     *
     * @param bleDevice ble设备
     */
    public static DemonEyesDevice init(BleDevice bleDevice) {
        instance = new DemonEyesDevice(bleDevice);
        return instance;
    }

    private DemonEyesDevice(BleDevice bleDevice) {
        super(bleDevice);
        bleDevice.setOnBleMtuListener(this);
        bleDevice.setOnBleOtherDataListener(this);
        bleDevice.setMtu(DemonEyesConfig.MTU);
        BleLog.i("DemonEyesDevice : init done...");
    }


    @Override
    public void onMtuAvailable(int mtu) {
        BleLog.i("mtu:" + mtu);
        mPayLoadMtu = mtu - 7;
    }

    @Override
    public void onHandshake(boolean status) {
//        super.onHandshake(status);
        for (OnDemonEyesListener listener : mListeners) {
            listener.onHandshake(status);
        }
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] data) {
        try {

            byte[] payload = getPayload(data);
            if (payload == null) {
                //无效数据
                BleLog.e("无效数据：" + BleStrUtils.byte2HexStrToUpperCase(data));
                return;
            }

            int cmd = payload[0] & 0xFF;
            switch (cmd) {

                case DemonEyesConfig.CMD_SHOW_CONTROL:
                    int type = payload[1] & 0xFF;
                    if (type == 0x01) {
                        //设置
                        for (OnDemonEyesListener listener : mListeners) {
                            listener.onSetShowControl(payload[2] & 0xFF);
                        }
                    } else if (type == 0x02 || type == 0x03) {
                        //获取
                        for (OnDemonEyesListener listener : mListeners) {
                            listener.onGetShowControl(payload[2] & 0xFF);
                        }
                    }

                    break;

                case DemonEyesConfig.CMD_UPDATE_IMAGE:
                    int bmType = payload[1] & 0xFF;
                    if (bmType != 0x03) {
                        //不是BM返回,暂不支持更新
                        BleLog.i("暂不支持非BM返回：" + BleStrUtils.byte2HexStrToUpperCase(data));
                        return;
                    }
                    int mode = payload[2] & 0xFF;
                    if (mode != 0x09) {
                        //不是静态图片,暂不支持更新
                        BleLog.i("暂不支持非静态图片：" + BleStrUtils.byte2HexStrToUpperCase(data));
                        return;
                    }
                    int step = payload[3] & 0xFF;

                    if (step == 0x01) {
                        //清除旧图片
                        int status = payload[4] & 0xFF;
                        for (OnDemonEyesListener listener : mListeners) {
                            listener.onClearDIYImage(status == 0x00);
                        }
                    } else if (step == 0x00) {
                        //获取图片信息
                        for (OnDemonEyesListener listener : mListeners) {
                            listener.onGetDIYImage(payload[4] & 0xFF);
                        }
                    } else if (step == 0x02) {
                        int all = (payload[4] & 0xFF) | (payload[5] & 0xFF) << 8;
                        int count = (payload[6] & 0xFF) | (payload[7] & 0xFF) << 8;
                        BleLog.i("图片总数：" + all + " 已发送：" + count);
                        if (all == count) {
                            //全部发送完成
                            mBitmapMap.clear();
                        } else {
                            byte[] bytes = mBitmapMap.get(count + 1);
                            if (bytes != null) {
                                sendDataNow(bytes);
                            }
                        }
                        for (OnDemonEyesListener listener : mListeners) {
                            listener.onSetDIYImage(all, count);
                        }
                    }

                    break;

                default:
                    BleLog.e("未知指令：" + BleStrUtils.byte2HexStrToUpperCase(data));
                    break;


            }
        } catch (Exception e) {
            BleLog.e("解析数据失败：" + e.getMessage() + "\n 指令:" + BleStrUtils.byte2HexStrToUpperCase(data));
        }

    }

    /**
     * 获取显示控制
     */
    public void getShowControl() {
        byte[] data = new byte[3];
        data[0] = DemonEyesConfig.CMD_SHOW_CONTROL;
        data[1] = 0x02;
        sendData(data);
    }

    /**
     * 设置显示控制
     *
     * @param id id 1~6 动图编号
     *           7~8 静图编号
     *           9 DIY图编号
     */
    public void setShowControl(int id) {
        byte[] data = new byte[3];
        data[0] = DemonEyesConfig.CMD_SHOW_CONTROL;
        data[1] = 0x01;
        data[2] = (byte) id;
        sendData(data);
    }

    /**
     * 设置diy图片
     *
     * @param bitmap 位图
     */
    public void setDIYImage(Bitmap bitmap) {
        byte[] bmpByte = BitmapUtils.getBmpByte(bitmap);
        Map<Integer, byte[]> integerMap = sendBitmap(bmpByte);
        byte[] bytes = integerMap.get(1);
        if (bytes != null) {
            sendData(bytes);
        }
    }

    /**
     * 设置diy图片
     */
    public void setDIYImage(Bitmap bitmap, int width, int height) {
        byte[] bmpByte = BitmapUtils.getBmpByte(bitmap, width, height);
        Map<Integer, byte[]> integerMap = sendBitmap(bmpByte);
        byte[] bytes = integerMap.get(1);
        if (bytes != null) {
            sendData(bytes);
        }
    }


    /**
     * 重试DIY图片发送
     */
    public boolean retryDIYImage() {
        if (mBitmapMap != null && !mBitmapMap.isEmpty()) {
            byte[] bytes = mBitmapMap.get(1);
            if (bytes != null) {
                sendData(bytes);
                return true;
            }
        }
        return false;
    }

    private Map<Integer, byte[]> mBitmapMap = new HashMap<>();

    /**
     * 发送位图
     *
     * @param bmpByte bmp字节
     */
    private Map<Integer, byte[]> sendBitmap(byte[] bmpByte) {
        mBitmapMap.clear();
        int bmpMtu = mPayLoadMtu - 8;
        int groupNum = bmpByte.length / bmpMtu;
        int remainder = bmpByte.length % bmpMtu;
        if (remainder != 0) {
            groupNum++;
        }
        BleLog.i("总包数量：" + groupNum);

        int index = 0;
        for (int i = 0; i < groupNum; i++) {
            int current = i + 1;
            int size = mPayLoadMtu;
            if (current == groupNum) {
                //最后一组
                if (remainder != 0) {
                    size = remainder + 8;
                    BleLog.i("最后一组大小：" + size);
                }
            }
            byte[] data = new byte[size];
            data[0] = DemonEyesConfig.CMD_UPDATE_IMAGE;
            data[1] = 0x01;
            data[2] = 0x09;
            data[3] = 0x02;
            data[4] = (byte) groupNum;
            data[5] = (byte) (groupNum >> 8);
            data[6] = (byte) current;
            data[7] = (byte) (current >> 8);
            System.arraycopy(bmpByte, index, data, 8, size - 8);
            index += size - 8;
            mBitmapMap.put(current, data);
        }
        return mBitmapMap;
    }


    /**
     * 清除diy图片
     */
    public void clearDIYImage() {
        byte[] data = new byte[4];
        data[0] = DemonEyesConfig.CMD_UPDATE_IMAGE;
        data[1] = 0x01;
        data[2] = 0x09;
        data[3] = 0x01;
        sendData(data);
    }

    /**
     * 获取diy图片信息
     */
    public void getDIYImage() {
        byte[] data = new byte[4];
        data[0] = DemonEyesConfig.CMD_UPDATE_IMAGE;
        data[1] = 0x01;
        data[2] = 0x09;
        data[3] = 0x00;
        sendData(data);

    }


    /**
     * 发送数据
     *
     * @param data 数据
     */
    private void sendData(byte[] data) {
        if (data.length > mPayLoadMtu) {
            BleLog.e("数据长度超过mtu");
            return;
        }
        byte[] dataAll = getA5Data(cid, data);
//        BleLog.e("发送的内容:" + BleStrUtils.byte2HexStrToUpperCase(dataAll));
        SendDataBean sendDataBean = getSendDataBean(dataAll);
        sendData(sendDataBean);
    }

    private void sendDataNow(byte[] data) {
        if (data.length > mPayLoadMtu) {
            BleLog.e("数据长度超过mtu");
            return;
        }
        byte[] dataAll = getA5Data(cid, data);
//        BleLog.e("发送的内容:" + BleStrUtils.byte2HexStrToUpperCase(dataAll));
        SendDataBean sendDataBean = getSendDataBean(dataAll);
        sendDataNow(sendDataBean);
    }


    /**
     * 获取a5数据
     *
     * @param cid  cid
     * @param data 数据
     * @return {@link byte[]}
     */
    private byte[] getA5Data(int cid, byte[] data) {
        //包头+cid+长度+sum+包尾=7
        byte[] a5Data = new byte[data.length + 7];
        a5Data[0] = DemonEyesConfig.A5_START;
        a5Data[1] = (byte) (cid >> 8);
        a5Data[2] = (byte) cid;
        int length = data.length;
        a5Data[3] = (byte) (length >> 8);
        a5Data[4] = (byte) length;
        System.arraycopy(data, 0, a5Data, 5, data.length);
        a5Data[a5Data.length - 2] = getSum(a5Data);
        a5Data[a5Data.length - 1] = DemonEyesConfig.A5_END;
        return a5Data;
    }

    /**
     * 获取有效载荷
     *
     * @param data 数据
     * @return {@link byte[]}
     */
    private byte[] getPayload(byte[] data) {
        byte start = data[0];
        int cid = (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
        byte sum = data[data.length - 2];
        if (start == DemonEyesConfig.A5_START && cid == DemonEyesConfig.CID && sum == getSum(data)) {
            //有效数据,解析payload
            int length = (data[3] & 0xFF) << 8 | (data[4] & 0xFF);
            byte[] payload = new byte[length];
            System.arraycopy(data, 5, payload, 0, payload.length);
            return payload;
        }
        return null;
    }


    /**
     * 获取总和
     *
     * @param a5Data a5数据
     * @return byte
     */
    private byte getSum(byte[] a5Data) {
        int sum = 0;
        for (int i = 1; i < a5Data.length - 2; i++) {
            sum += a5Data[i];
        }
        return (byte) sum;
    }

    /**
     * 获取发送数据豆
     *
     * @param data 数据
     * @return {@link SendDataBean}
     */
    private SendDataBean getSendDataBean(byte[] data) {
        return new SendDataBean(data, BleConfig.UUID_WRITE_AILINK, BleConfig.WRITE_DATA, BleConfig.UUID_SERVER_AILINK);
    }


    /**
     * 清除
     */
    public void clear() {
        mBitmapMap.clear();
        mListeners.clear();
        instance = null;
    }


    private final List<OnDemonEyesListener> mListeners = new ArrayList<>();

    public void addOnDemonEyesListener(OnDemonEyesListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    public void removeOnDemonEyesListener(OnDemonEyesListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }

    public void clearListeners() {
        mListeners.clear();
    }

    public interface OnDemonEyesListener {

        /**
         * 在设置显示控制
         *
         * @param id id 1~6 动图编号
         *           7~8 静图编号
         *           9 DIY图编号
         */
        void onSetShowControl(int id);

        /**
         * 在获取显示控制
         *
         * @param id id 1~6 动图编号
         *           7~8 静图编号
         *           9 DIY图编号
         */
        void onGetShowControl(int id);

        /**
         * 设置DIY图片
         * DemonEyesDevice对象收到当前包后会自动判断下发下一包,不需要主动调用
         *
         * @param all   总包数
         * @param count 当前包数
         */
        void onSetDIYImage(int all, int count);

        /**
         * 清除DIY图片
         *
         * @param success 是否成功
         */
        void onClearDIYImage(boolean success);

        /**
         * 获取DIY图片
         *
         * @param status 状态
         *               0x00 无图片
         *               0x01 有图片
         */
        void onGetDIYImage(int status);


        /**
         * 握手回调
         *
         * @param status
         */
        void onHandshake(boolean status);

    }


}
