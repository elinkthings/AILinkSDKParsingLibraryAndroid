package cn.net.aicare.modulelibrary.module.wifi;


import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.net.aicare.modulelibrary.module.wifi.Protocol.AES;
import cn.net.aicare.modulelibrary.module.wifi.Protocol.CRC8;


public class Information {
    public String Data;
    public int DataPackageSum;
    private int SsidPawdLength;
    //用于获取当前连接上的wifi信息
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    Integer randomNum;
    final static int LeadCode = 4,CrcSize = 2;
    byte[] LeadCodeTable;
    //加密后的ssid和password
    byte[] passwordEncrypt;
    byte[] ssidEncrypt;
    //password+随机数+ssid
    byte[] info;
    //组装后的信息
    public int[] message;
    public ArrayList<Integer> msg = new ArrayList<Integer>();
    //原始信息长度,用于告知小机端接收数据的长度
    int informationLength;
    //加密后需要组装后发送出去的信息长度
    int informationSize;
    //当前已经连接的网络属于哪个信道
    int channel = -1;
    //当前已经连接的网络使用什么加密方式
    int encryption;
    //是否需要加密
    String mark ="";
    public Information(WifiManager wifiManager, String password, String ssid, String key, String mark) throws Exception {
        this.wifiManager = wifiManager;
        wifiInfo = wifiManager.getConnectionInfo();
        this.mark = new String(mark);

        //获取目标AP加密方式
        encryption = this.getCipherType(wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1));
        Log.d("SSID:", wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1));
        //通过获取扫描结果从频率判断当前目标AP处理处于信道
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (scanResults.isEmpty()) {
            Log.e("debug","get scanResults is Empty");
        }
        for (ScanResult result : scanResults) {
            if (result.BSSID.equalsIgnoreCase(wifiInfo.getBSSID()) && result.SSID.equalsIgnoreCase(wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1))) {
                channel = getChannelByFrequency(result.frequency);
                Log.d("debug","channel:" + channel);
                break;
            }
        }
        if (channel == -1) {
            Log.e("debug","channel:" + channel);
            //Log.d("debug","wifiInfo.getFrequency()" + wifiInfo.getFrequency());
            //channel = getChannelByFrequency(wifiInfo.getFrequency());
        }

        if (mark.equals("1")){
            ssidEncrypt = AES.encrypt(key, ssid);
           // Log.e("ssidEncrypt = ", ssidEncrypt + "");
        //判断是否是不需要密码的AP
            if (password.length() == 0) {
                passwordEncrypt = password.getBytes();
            } else {
                passwordEncrypt = AES.encrypt(key, password);
             //   Log.e("passwordEncrypt = ", passwordEncrypt + "");
            }
        }else {
            ssidEncrypt = ssid.getBytes();
            passwordEncrypt = password.getBytes();
        }
        //计算信息长度,准备组装信息
        SsidPawdLength = (passwordEncrypt.length + ssidEncrypt.length);
        informationSize = SsidPawdLength +(SsidPawdLength % 2) + CrcSize + LeadCode;
        info = new byte[informationSize];
        Log.e("info length =",info.length + "");
        //将信息放在一起方便后面进行组装
        //随机数
        randomNum = (int) (Math.random() * 256);
        //确保为偶数,用于加密方式的区别
        randomNum = ((randomNum % 95) + 32) / 2 * 2;
        if (getCipherType(ssid) == 1){
            randomNum = randomNum + 1;
        }
        Log.d("info randomNum =",randomNum + "");
        LeadCodeTable = new byte[LeadCode];
        //Lead code
        LeadCodeTable[0] = (byte) (channel & 0xff);
        LeadCodeTable[1] = (byte) (SsidPawdLength & 0xff);
        LeadCodeTable[2] = (byte) (passwordEncrypt.length & 0xff);
        LeadCodeTable[3] = (byte) (randomNum.byteValue() & 0xff);
        Log.d("info randomNum 1 =",new Integer(LeadCodeTable[3]) + "");
        DataPackageSum = (SsidPawdLength + CrcSize);
        DataPackageSum = DataPackageSum / 2 + DataPackageSum % 2 + LeadCode;
        for(int i = 0; i < LeadCode; i++) {
                info[i] = LeadCodeTable [i];
        }
        //ssid
        //password + 随机数 +ssid
        //password
        for (int i = LeadCode ; i < (passwordEncrypt.length + LeadCode ); i++) {
            info[i] = (byte) (passwordEncrypt[i-LeadCode] & 0xff);
        }
        //ssid
        for (int i = (passwordEncrypt.length + LeadCode ); i < (informationSize - CrcSize - (SsidPawdLength % 2)); i++) {
            info[i] = (byte) (ssidEncrypt[i - passwordEncrypt.length - LeadCode] & 0xff);
        }
        if ( (SsidPawdLength % 2) > 0) {
            info[informationSize - 3] = 0;
        }
        byte PawdCrc = CRC8.CRC8(password.getBytes());
        byte SsidCrc = CRC8.CRC8(ssid.getBytes());
        info[informationSize - 2] =(byte) (PawdCrc & 0xff);
        info[informationSize - 1] =(byte) (SsidCrc & 0xff);
    }
//获取目标Ap当前加密方式
    public int getCipherType(String ssid) {
        List<ScanResult> list = wifiManager.getScanResults();
        if (list.isEmpty()) {
            Log.d("WiFi", "list.isEmpty()");
            Log.d("WiFi", "ssid:" + ssid);
        }
        for (ScanResult scResult : list) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        Log.d("WiFi", "WPA");
                        return 0;
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        Log.d("WiFi", "WEP");
                        return 1;
                    } else {
                        return 2;
                    }
                }
            }
        }
        /*
        if (list.isEmpty()) {
            List<WifiConfiguration> wifiConfigurationlist = wifiManager.getConfiguredNetworks();
            for (int i = 0; i < wifiConfigurationlist.size(); i++) {
                //Log.i("WiFi", wifiConfigurationlist.get(i).SSID.toString());
                if (ssid.equalsIgnoreCase(wifiConfigurationlist.get(i).SSID)) {
                    Log.d("WiFi", "ssid.equalsIgnoreCase(wifiConfigurationlist.get(i).SSID)");
                    WifiConfiguration wifiConfiguration = wifiConfigurationlist.get(i);
                    if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                        Log.d("WiFi", "CipherType:WPA_PSK");
                        return 0;
                    } else if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)
                            || wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
                        Log.d("WiFi", "CipherType:EAP");
                        return 2;
                    } else {
                        if (wifiConfiguration.wepKeys[0] != null) {
                            Log.d("WiFi", "CipherType:WEP");
                            return 1;
                        }
                    }
                }
            }
        }*/
        return -1;
    }
//通过传入频率值得出当前信道
    public int getChannelByFrequency(int frequency) {
        int channel = -1;
        switch(frequency) {
            case 2412:
                channel = 1;
                break;
            case 2417:
                channel = 2;
                break;
            case 2422:
                channel = 3;
                break;
            case 2427:
                channel = 4;
                break;
            case 2432:
                channel = 5;
                break;
            case 2437:
                channel = 6;
                break;
            case 2442:
                channel = 7;
                break;
            case 2447:
                channel = 8;
                break;
            case 2452:
                channel = 9;
                break;
            case 2457:
                channel = 10;
                break;
            case 2462:
                channel = 11;
                break;
            case 2467:
                channel = 12;
                break;
            case 2472:
                channel = 13;
                break;
            case 2484:
                channel = 14;
                break;
            case 5745:
                channel = 149;
                break;
            case 5765:
                channel = 153;
                break;
            case 5785:
                channel = 157;
                break;
            case 5805:
                channel = 161;
                break;
            case 5825:
                channel = 165;
                break;
        }
        return channel;
    }
}
