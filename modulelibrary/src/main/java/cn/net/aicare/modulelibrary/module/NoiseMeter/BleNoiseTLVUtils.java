package cn.net.aicare.modulelibrary.module.NoiseMeter;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pinwang.ailinkble.AiLinkPwdUtil;

import java.util.ArrayList;
import java.util.List;

public class BleNoiseTLVUtils {

    /**
     * 获取到A7加密数据 包含头尾的
     * //a7 01 48 06 0d 67 81 ef b3 03 e9 7a
     *
     * @param payload       payload
     * @param macByte mac字节
     * @return {@link byte[]}
     */
    public static byte[] getA7EncryptByte(byte[] payload, byte[] macByte,byte[] cid) {

        byte[] mcuEncrypt = AiLinkPwdUtil.mcuEncrypt(new byte[]{cid[0],cid[1]}, macByte, payload);
        int length = mcuEncrypt.length;
        byte sum = 0;
        //加上长度,包头,包尾,校验和,2个cid
        byte[] cmdByte = new byte[length + 6];
        cmdByte[0] = CmdConfig.SEND_MCU_START;
        cmdByte[1] = cid[0];
        cmdByte[2] = cid[1];
        cmdByte[3] = (byte) length;
        System.arraycopy(mcuEncrypt, 0, cmdByte, 4, mcuEncrypt.length);
        sum = cmdSum(cmdByte);
        cmdByte[cmdByte.length - 2] = sum;
        cmdByte[cmdByte.length - 1] = CmdConfig.SEND_MCU_END;
        return cmdByte;
//
    }


    /**
     * 得到a6完整包
     *
     * @param payload 有效载荷
     * @return {@link byte[]}
     */
    public static byte[] getA6CompletePackage(byte[] payload){
        byte[] cmdByte = new byte[payload.length + 3];
        cmdByte[0] = CmdConfig.SEND_MCU_START;
        cmdByte[1] = (byte) payload.length;

        System.arraycopy(payload, 0, cmdByte, 2, payload.length);
        byte sum = cmdSum(cmdByte);
        cmdByte[cmdByte.length - 2] = sum;
        cmdByte[cmdByte.length - 1] = CmdConfig.SEND_MCU_END;
        return cmdByte;


    }




    /**
     * String 类型的mac地址转换为byte数组
     * 获得mac字节
     *
     * @param mac mac
     * @return {@link byte[]}
     */
    public static byte[] getMacBytes(String mac) {
        byte[] macByte = new byte[6];
        if (mac.contains(":")) {
            String[] macArr = mac.split(":");
            for (int i = 0; i < macArr.length; i++) {
                macByte[macArr.length - i - 1] = (byte) Integer.parseInt(macArr[i], 16);
            }
        }
        return macByte;
    }

    public static byte cmdSum(byte[] data) {
        byte sum = 0;
        for (int i = 1; i < data.length; i++) {
            sum += data[i];
        }

        return sum;
    }



    /**
     *
     * @param bytes payload 数据
     * @return
     */
    public static List<BleNoiseTLVBean> getTLV(int group, byte[] bytes) {
        List<BleNoiseTLVBean> list = new ArrayList<>();
//        int cmd = bytes[0] & 0xFF;
//        int allGroup = bytes[1] & 0xFF;
        byte[] tlvBytesAll = new byte[bytes.length - 2];
        System.arraycopy(bytes, 2, tlvBytesAll, 0, tlvBytesAll.length);
        for (int i = 0; i < tlvBytesAll.length; i++) {
            byte type=tlvBytesAll[i];
            int newLength = tlvBytesAll[i + 1]&0xFF;
            if (newLength>(tlvBytesAll.length-2)){
                BleLog.e("数据异常:newLength="+newLength+"  tlvBytesAll.length="+(tlvBytesAll.length-2));
                return new ArrayList<>();
            }
            byte[] valueB = new byte[newLength];
            System.arraycopy(tlvBytesAll, i+2, valueB, 0, valueB.length);
            i = newLength + 2 + i - 1;
            list.add(new BleNoiseTLVBean(group,type,valueB) );
        }
        return list;
    }

    /**
     * 检查返回数据格式
     *
     *
     * @param data 数据 原始数据 带头尾
     * @return int 0xfffff 为不合规数据
     */
    public static int checkReturnDataFormat(byte[] data) {
        if (data.length < 3) {
            return 0xffff;
        }
        byte one = data[0];
        //长度
        int two = data[1] & 0xFF;
        byte three = data[2];
        byte last = data[data.length - 1];
        int cmdLen;
        switch (one) {
            case CmdConfig.SEND_BLE_START:

                if (CmdConfig.SEND_BLE_END == last) {
                    cmdLen = 2;
                } else {
                    return 0xffff;
                }

                break;

            case CmdConfig.SEND_MCU_START:
                if (CmdConfig.SEND_MCU_END == last) {
                    cmdLen = 2;
                } else {
                    return 0xffff;
                }
                break;
            default:
                return 0xffff;
        }
        byte sum = 0;
        for (int i = 1; i < data.length - cmdLen; i++) {
            sum += data[i];
        }
        if (sum == data[data.length - cmdLen]){
            return one;
        }else {
            return 0xffff;
        }

    }


    /**
     * a6的payload
     *
     * @param hex 十六进制
     * @return {@link byte[]}
     */
    public static byte[] toProtocolA6(byte[] hex) {
        if (hex != null) {
            byte[] payload;
            payload = returnBleDataFormat(hex);
            if (payload != null && payload.length >= 1) {
                //A6数据

                return payload;
            }
        }
        return null;

    }

    /**
     * 获取到A7的Payload
     *
     * @param hex     十六进制
     * @param macByte mac字节
     * @return {@link byte[]}
     */
    public static byte[] toProtocolA7(byte[] hex, byte[] macByte) {
        if (hex != null) {
            byte[] payload;
            payload = returnMcuDataFormat(hex);
            if (payload != null && payload.length >= 1) {
                //A7数据
                int type = ((hex[1] & 0xff) << 8) + (hex[2] & 0xff);
                byte[] CID = new byte[]{hex[1], hex[2]};
                byte[] data;

                data = AiLinkPwdUtil.mcuEncrypt(CID, macByte, payload);
                return data;
            }
        }
        return null;

    }




    private static byte[] returnBleDataFormat(byte[] data) {
        byte[] returnData = null;
        int two = data[1] & 255;
        if (data.length >= two) {
            returnData = new byte[two];
            System.arraycopy(data, 2, returnData, 0, two);
        }

        return returnData;
    }

    private static byte[] returnMcuDataFormat(byte[] data) {
        byte[] returnData = null;
        int two = data[3] & 255;
        if (data.length >= two) {
            returnData = new byte[two];
            System.arraycopy(data, 4, returnData, 0, two);
        }

        return returnData;
    }
}
