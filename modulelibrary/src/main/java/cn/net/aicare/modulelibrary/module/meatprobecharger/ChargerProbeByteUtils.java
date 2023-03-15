package cn.net.aicare.modulelibrary.module.meatprobecharger;

/**
 * @auther ljl
 * on 2023/2/25
 */
public class ChargerProbeByteUtils {
    /**
     * short 转 byte 数组
     *
     * @param num
     * @return
     */

    public static byte[] shortToBytes(short num) {
        int temp = num;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
//        byte[] bytes = reverseByteArr(b);
        return b;
    }

    /**
     * 字节数组转 short 类型
     *
     * @param b
     * @return
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff);

        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * int 转 byte数组
     *
     * @param num
     * @return
     */
    public static byte[] intToBytes(int num) {
        int temp = num;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        byte[] bytes = reverseByteArr(b);
        return bytes;
    }

    /**
     * 字节数组转 int
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b) {
//        byte[] bytes = reverseByteArr(b);
        byte[] bytes = b;
        int s = 0;
        //最低位
        int s0 = bytes[0] & 0xff;
        int s1 = bytes[1] & 0xff;
        int s2 = bytes[2] & 0xff;
        int s3 = bytes[3] & 0xff;

        //s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    /**
     * float 转 byte 数组
     *
     * @param f
     * @return
     */
    public static byte[] floatToByte(float f) {
        int l = Float.floatToIntBits(f);
        return intToBytes(l);
    }

    /**
     * 字节数组转float
     *
     * @param b
     * @return
     */
    public static float byteToFloat(byte[] b) {
        int i = byteToInt(b);
        return Float.intBitsToFloat(i);
    }

    /**
     * long 转 byte数组
     *
     * @param l
     * @return
     */
    public static byte[] longToByte(long l) {
        byte[] b = new byte[8];
        long temp = l;
        for (int i = 0; i < 8; i++) {
            b[i] = new Long(temp).byteValue();
            temp = temp >> 8;
        }
        byte[] bytes = reverseByteArr(b);
        return bytes;
    }

    /**
     * byte数组转 long
     *
     * @param b 字节数组
     * @return
     */
    public static long byteToLong(byte[] b) {
//        byte[] bytes = reverseByteArr(b);
        byte[] bytes = b;
        long s = 0;
        //最低位
        long s0 = bytes[0] & 0xff;
        long s1 = bytes[1] & 0xff;
        long s2 = bytes[2] & 0xff;
        long s3 = bytes[3] & 0xff;
        //最低位
        long s4 = bytes[4] & 0xff;
        long s5 = bytes[5] & 0xff;
        long s6 = bytes[6] & 0xff;
        long s7 = bytes[7] & 0xff;

        //s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * double 转 byte 数组
     *
     * @param d
     * @return
     */
    public static byte[] doubleToByte(double d) {
        long l = Double.doubleToLongBits(d);
        return longToByte(l);
    }

    /**
     * 字节数组转double
     *
     * @param b
     * @return
     */
    public static double byteToDouble(byte[] b) {
        long l = byteToLong(b);
        return Double.longBitsToDouble(l);
    }

    /**
     * 字节翻转
     *
     * @param bytes
     * @return
     */
    private static byte[] reverseByteArr(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("byte 字节数组不能为空");
        }
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            b[bytes.length - i - 1] = bytes[i];
        }
        return b;
    }

    /**
     * 切换大小端序
     *
     * @param a
     * @return
     */
    public static byte[] changeBytes(byte[] a) {
        byte[] b = new byte[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[b.length - i - 1];
        }
        return b;
    }

    /**
     * 十六进制字符串转 byte 数组
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStringToByteArray(String hexStr) {
        hexStr = hexStr.replaceAll(" ", "");
        int len = hexStr.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) (Character.digit(hexStr.charAt(i), 16) << 4 + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return bytes;
    }
}
