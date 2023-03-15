package cn.net.aicare.modulelibrary.module.base;

import java.util.ArrayList;
import java.util.List;

public class BleTLVUtils {

    /**
     * 获取TLV对象
     *
     * @param bytes payload 数据
     * @return List<BleTLVBean>
     */
    public static List<BleTLVBean> getTLVBean(byte[] bytes) {
        List<BleTLVBean> list = new ArrayList<>();
        int index = 0;
        while ((index + 2) < bytes.length) {
            byte type = bytes[index];
            int newLength = bytes[index + 1];
            byte[] valueB = new byte[newLength];
            System.arraycopy(bytes, index + 2, valueB, 0, valueB.length);
            index = newLength + 2 + index;
            list.add(new BleTLVBean(type, valueB));
        }
        return list;
    }

}
