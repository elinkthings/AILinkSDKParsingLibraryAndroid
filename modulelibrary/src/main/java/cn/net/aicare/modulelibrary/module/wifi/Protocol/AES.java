package cn.net.aicare.modulelibrary.module.wifi.Protocol;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 */
public class AES {

    private final static String HEX = "0123456789ABCDEF";
    /**
     * 加密
     *
     * @param key 密钥
     * @param src 加密文本
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String key, String src) throws Exception {
        byte[] rawKey = key.getBytes();//getRawKey(key.getBytes());
        byte[] result = encrypt(rawKey, src.getBytes());
        return result;
    }
    /**
     * 解密
     * @param key 密钥
     * @param encrypted 待解密文本
     * @return
     * @throws Exception
     */
    public static String decrypt(String key, String encrypted) throws Exception {
        byte[] rawKey = key.getBytes();//getRawKey(key.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }


    /**
     * 真正的加密过程
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] key, byte[] src) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src);
        //byte[] h = Base64.encode(encrypted, Base64.DEFAULT);
        return encrypted;//Base64.encode(encrypted,Base64.DEFAULT);
    }
    /**
     * 真正的解密过程
     * @param key
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] key, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] temp = Base64.decode(encrypted, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(temp);
        return decrypted;
    }

    /**
     * 以下为
     * @param txt
     * @return
     */
    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
