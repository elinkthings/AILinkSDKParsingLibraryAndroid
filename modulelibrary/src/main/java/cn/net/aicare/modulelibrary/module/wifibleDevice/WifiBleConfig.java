package cn.net.aicare.modulelibrary.module.wifibleDevice;

import com.pingwang.bluetoothlib.annotation.IntDef;
import com.pingwang.bluetoothlib.annotation.Retention;
import com.pingwang.bluetoothlib.annotation.RetentionPolicy;

/**
 * wifi ble配置
 *
 * @author xing
 * @date 2023/10/06
 */
public class WifiBleConfig {

    @IntDef({SecurityType.TYPE_OPEN, SecurityType.TYPE_WEP,
            SecurityType.TYPE_WPA_PSK, SecurityType.TYPE_WPA2_PSK,
            SecurityType.TYPE_WPA_WPA_2_PSK, SecurityType.TYPE_WPA2_ENTERPRISE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SecurityType{

        /**
         * 开放类型
         */
        int TYPE_OPEN = 0x00;
        int TYPE_WEP = 0x01;
        int TYPE_WPA_PSK = 0x02;
        int TYPE_WPA2_PSK = 0x03;
        int TYPE_WPA_WPA_2_PSK = 0x04;
        int TYPE_WPA2_ENTERPRISE = 0x05;

    }

    @IntDef({WifiStatusType.TYPE_NEW, WifiStatusType.TYPE_SAVE,
            WifiStatusType.TYPE_CONNECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WifiStatusType{
        /**
         * 陌生 wifi(新的wifi)
         */
        int TYPE_NEW=0x00;
        /**
         * 保存过密码的 wifi
         */
        int TYPE_SAVE=0x01;
        /**
         * 目前连接着的 wifi
         */
        int TYPE_CONNECT=0x02;
    }

}
