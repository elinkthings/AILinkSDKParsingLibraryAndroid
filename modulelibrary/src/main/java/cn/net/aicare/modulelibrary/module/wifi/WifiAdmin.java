package cn.net.aicare.modulelibrary.module.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * wifi 管理类
 */
public class WifiAdmin {

    //WIFI相关功能对象
    //Wifi管理类
    private WifiManager wifiManager;
    //Wifi信息类
    private WifiInfo wifiInfo;
    //Wifi扫描结果类,用于存储Scan操作的结果
    private List<ScanResult> wifiList;
    //Wifi配置列表
    private List<WifiConfiguration> wifiConfigurationList;
    //构造方法
    public WifiAdmin(){
    }
    public WifiAdmin(Context context){
        //获取WIFI服务对象
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
       //获取WIFI信息对象
        wifiInfo = wifiManager.getConnectionInfo();
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }
    public void setWifiManager(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }
    public void setWifiInfo(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
    }
    public void setWifiList(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
    }
    public List<WifiConfiguration> getWifiConfigurationList() {
        return wifiConfigurationList;
    }
    public void setWifiConfigurationList(List<WifiConfiguration> wifiConfigurationList) {
        this.wifiConfigurationList = wifiConfigurationList;
    }
    public String getSSID(){
        return (wifiInfo==null)?"null":wifiInfo.getSSID();
    }
    public String getMacAddress(){
        return (wifiInfo == null)?"null":wifiInfo.getMacAddress();
    }
    public String getBSSID(){
        return (wifiInfo == null)?"null":wifiInfo.getBSSID();
    }
    public int getIpAddress(){
        return (wifiInfo==null)?0:wifiInfo.getIpAddress();
    }
    public String getWifiInfo(){
        return (wifiInfo==null)?"null":wifiInfo.toString();
    }
    //打开Wifi
    public void openWifi(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
    }
    //关闭Wifi
    public void closeWifi(){
        if(wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
    }
    //获得当前环境下Wifi列表
    public List<ScanResult> getWifiList(){
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();
        return wifiList;
    }
    //向android系统中添加一个WIFI配置信息
    public void addNetWork(WifiConfiguration wifiConfiguration){
        int wcgId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(wcgId,true);
    }
    //IP地址换算函数
    public String inttoIp(int i){
        return (i & 0xFF)+"."+((i >> 8) & 0xff)+"."+((i >> 16) & 0xff)+"."+((i >> 24) & 0xff);
    }
    //Ip地址换算函数
    public Integer iptoInt(String ip){
        String[] items = ip.split("\\.");
        return Integer.valueOf(items[0])
                | Integer.valueOf(items[1])<<8
                | Integer.valueOf(items[2])<<16
                | Integer.valueOf(items[3])<<24;
    }
}
