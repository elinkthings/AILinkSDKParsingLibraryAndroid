package cn.net.aicare.modulelibrary.module.wifi.Protocol;

import java.util.zip.Checksum;

public class CRC8  implements Checksum {

    private final short init;

    private static final short[] crcTable = new short[256];

    private short value;

    private static final short CRC_POLYNOM = 0x31;
    private static final short CRC_INITIAL = 0x00;

    static {
        for (int dividend = 0; dividend < 256; dividend++) {
            int remainder = dividend;// << 8;
            for (int bit = 0; bit < 8; ++bit)
                if ((remainder & 0x01) != 0)
                    remainder = (remainder >>> 1) ^ CRC_POLYNOM;
                else
                    remainder >>>= 1;
            crcTable[dividend] = (short) remainder;
        }
    }

    public CRC8() {
        this.value = this.init = CRC_INITIAL;
    }

    @Override
    public void update(byte[] buffer, int offset, int len) {
        for (int i = 0; i < len; i++) {
            int data = buffer[offset + i] ^ value;
            value = (short) (crcTable[data & 0xff] ^ (value << 8));
        }
    }

    /**
     * @param buffer
     */
    public void update(byte[] buffer) {
        update(buffer, 0, buffer.length);
    }

    @Override
    public void update(int b) {
        update(new byte[] { (byte) b }, 0, 1);
    }

    @Override
    public long getValue() {
        return value & 0xff;
    }

    @Override
    public void reset() {
        value = init;
    }

    public static byte CRC8(byte[] buffer)
    {
        int crci=0x00;   //起始字节FF
        for (int j = 0; j < buffer.length; j++)
        {
            crci ^= buffer[j] & 0xFF;
            for (int i = 0; i < 8; i++)
            {
                if ((crci & 1) != 0)
                {
                    crci >>= 1;
                    crci ^= 0x31;    //多项式当中的那个啥的，不同多项式不一样
                }
                else
                {
                    crci >>= 1;
                }
            }
        }
        return (byte)(crci & 0xff) ;
    }
}
