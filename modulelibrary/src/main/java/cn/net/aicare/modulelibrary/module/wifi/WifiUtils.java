package cn.net.aicare.modulelibrary.module.wifi;

import android.content.Context;
import android.util.Log;

import java.net.DatagramPacket;

import cn.net.aicare.modulelibrary.module.wifi.Protocol.CRC8;
import cn.net.aicare.modulelibrary.module.wifi.UDP.UDPMultiClient;
import cn.net.aicare.modulelibrary.module.wifi.UDP.UDPMultiServer;

public class WifiUtils {

    private UDPMultiClient mUDPMultiClient;
    private UDPMultiServer mMultiServer;
    private WifiAdmin mWifiAdmin;
    private OnWifiEventListener mOnWifiEventListener;
    private Information info; //数据进行封装，厂商提供的
    private int interval = 5; //发送间隔
    private int running = 0;
    private boolean loopSendEnable = false; //是否循环发送
    private String ssidstring;
    private long current;
    private Context context;

    public String getSsidstring() {
        return ssidstring;
    }

    public int getRunning() {
        return running;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setLoopSendEnable(boolean loopSendEnable) {
        this.loopSendEnable = loopSendEnable;
    }

    public WifiUtils(Context context, OnWifiEventListener onWifiEventListener) {
        this.context=context;
        mWifiAdmin = new WifiAdmin(context);
        mUDPMultiClient = new UDPMultiClient(context);
        mMultiServer = new UDPMultiServer(context);
        mWifiAdmin.openWifi();
        ssidstring = mWifiAdmin.getSSID();
        this.mOnWifiEventListener = onWifiEventListener;
    }


    public void startSendData(String ssid, String paw, String aeskey) {
        if (ssid.isEmpty() || aeskey.isEmpty()) {
            mOnWifiEventListener.onEvent(WifiConfig.SSID_KEY_NULL);
            return;
        }
        if (aeskey.length() != 16) {
            //key不对
            mOnWifiEventListener.onEvent(WifiConfig.KEY_ILLEGAL);
            return;
        }

        try {

            running = 1;
            current = System.currentTimeMillis();
            info = new Information(mWifiAdmin.getWifiManager(), paw, ssid, aeskey, "1");
            mOnWifiEventListener.onEvent(WifiConfig.INFORMATION_SUCCESSFUL);
            initStart();
        } catch (Exception e) {
            mOnWifiEventListener.onEvent(WifiConfig.ERROR_EXCEPTIONAL);
            e.printStackTrace();
        }
    }

    private ClientThread mClientThread;
    private ServerThread mServerThread;
    private ManagerThread mManagerThread;

    private void initStart() {
        //中断旧线程, 清空
        if (mClientThread != null) {
            mClientThread.interrupt();
        }
        if (mServerThread != null) {
            mServerThread.interrupt();
        }
        if (mManagerThread != null) {
            mManagerThread.interrupt();
        }
        mClientThread = null;
        mServerThread = null;
        mManagerThread = null;

        //使用独立线程类管理3个线程
        mClientThread = new ClientThread();
        mServerThread = new ServerThread();
        mManagerThread = new ManagerThread();
        mClientThread.start();
        mServerThread.start();
        mManagerThread.start();
    }

    public void end() {
        running=0;

        //销毁旧线程
        if (mClientThread != null) {
            mClientThread.interrupt();
        }
        if (mServerThread != null) {
            mServerThread.interrupt();
        }
        if (mManagerThread != null) {
            mManagerThread.interrupt();
        }

        mClientThread = null;
        mServerThread = null;
        mManagerThread=null;
    }

    public void onDestory(){
        running=0;
        if (mUDPMultiClient != null)
            mUDPMultiClient.close();

        if (mMultiServer != null)
            mMultiServer.close();

        if (mClientThread != null) {
            mClientThread.interrupt();
        }
        if (mServerThread != null) {
            mServerThread.interrupt();
        }
        if (mManagerThread != null) {
            mManagerThread.interrupt();
        }

        mClientThread = null;
        mServerThread = null;
        mManagerThread=null;

        mUDPMultiClient=null;
        mMultiServer=null;
    }


    public interface OnWifiEventListener {
        void onEvent(int code);
    }

    //将加密后的数据进行分装
    private void clientDo() {
        final int LeadCode = 4;
        int count = 1;
        byte[] SendData = new byte[info.DataPackageSum * 3];
        for (int i = 0, j = 0; i < info.info.length; j = j + 3) {
            if (i < LeadCode) {
                SendData[j] = (byte) count;
                SendData[j + 1] = (byte) (info.info[i] & 0xff);
                byte[] crc = new byte[1];
                crc[0] = (byte) (info.info[i] & 0xff);
                SendData[j + 2] = CRC8.CRC8(crc);
                i++;
            } else {
                SendData[j] = (byte) count;
                SendData[j + 1] = (byte) (info.info[i] & 0xff);
                SendData[j + 2] = (byte) (info.info[i + 1] & 0xff);
                i = i + 2;
            }
            count++;
        }
        String[] UdpIpData = new String[3];
        String[] IP = new String[info.DataPackageSum];
        for (int j = 0; j < (SendData.length / 3); j++) {
            for (int i = j * 3; i < j * 3 + 3; i = i + 3) {
                UdpIpData[0] = String.valueOf(SendData[i] & 0xff);
                UdpIpData[1] = String.valueOf(SendData[i + 1] & 0xff);
                UdpIpData[2] = String.valueOf(SendData[i + 2] & 0xff);
            }
            IP[j] = "239." + UdpIpData[0] + "." + UdpIpData[1] + "." + UdpIpData[2];
        }
        //设置发送间隔
        mUDPMultiClient.setTxInterval(interval);
        //循环发送数据
        while (running == 1) {
            byte[] Data = new byte[1];
            for (int i = 0; i < 3; i++) {
                if (running != 1) {
                    break;
                }
                for (int j = 0; j < LeadCode; j++) {
                    mUDPMultiClient.send(Data, IP[j]);
                }
            }
            for (int i = LeadCode; i < IP.length; i++) {
                if (running != 1) {
                    break;
                }
                mUDPMultiClient.send(Data, IP[i]);
                if ((i % 5) == 0) {
                    mUDPMultiClient.send(Data, IP[0]);
                }
            }
        }
    }

    public void serverDo() {
        while (running == 1) {
            DatagramPacket datagramPacket = mMultiServer.receive();
            if (datagramPacket.getData()[0] == info.randomNum) {

                if (loopSendEnable) {
                    running = 1;
                } else {
                    running = 0;
                    //接收到连接设备成功
                    mOnWifiEventListener.onEvent(WifiConfig.DEVICE_CONNECTION_SUCCESSFUL);
                }
                break;
            }
        }
    }

    private class ClientThread extends Thread {

        public void run() {
            mOnWifiEventListener.onEvent(WifiConfig.START_SEND_DATA);
            while (running==1) {
                try {
                    clientDo();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    mOnWifiEventListener.onEvent(WifiConfig.ERROR_START_SEND_DATA);
                    break;
                }
            }
        }

    }

    private class ServerThread extends Thread {
        public void run() {
            mOnWifiEventListener.onEvent(WifiConfig.START_RECEIVE_DATA);
            while (running==1) {
                try {
                    serverDo();
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    break;
                }
            }
        }
    }

    private void managerDo() {
        long now;
        while (running == 1) {
            now = System.currentTimeMillis();//获取系统当前时间
            if (now - current >= 3000) {
                mUDPMultiClient.setTxInterval(100);
                break;
            }
        }
    }

    private class ManagerThread extends Thread {
        public void run() {
            while (true) {
                try {
                    managerDo();
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    break;
                }
            }
        }
    }

}
