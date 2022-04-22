package cn.net.aicare.modulelibrary.module.wifi.UDP;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2015/11/18.
 */
public class UDPMultiClient {
    private MulticastSocket udpSockect;
    private boolean isClose;
    private int port = 9999;
    private String multicastIp = "239.0.0.1";
    private InetAddress group;
    private long TxInterval = 0;
    private WifiManager.MulticastLock lock;

    public UDPMultiClient(Context context) {
        try {
            this.udpSockect = new MulticastSocket(port);
            group = InetAddress.getByName(multicastIp);
            this.udpSockect.joinGroup(group);
            this.udpSockect.setBroadcast(true);
            this.udpSockect.setTimeToLive(2);
            this.isClose = false;
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (manager == null)
                if (udpSockect == null)
                    lock = manager.createMulticastLock("multicastLock");
            if (lock != null)
                lock.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (lock != null) {
            lock.release();
        }
        if (!this.isClose) {
            this.udpSockect.close();
            this.isClose = true;
        }
    }

    protected void finallize() throws Throwable {
        close();
        super.finalize();
    }

    public void setTxInterval(long txInterval) {
        TxInterval = txInterval;
    }

    //发送数据
    public void sendData(byte[] data, String targetHostName, int targetPort, long interval) {
        sendData(data, 0, data.length, targetHostName, targetPort, interval);
    }

    //发送数据带有偏移量offset
    public void sendData(byte[] data, int offset, int count, String targetHostName, int targetPort, long interval) {

    }

    public void send(byte[] data, String MulticastIp) {
        try {
            //this.lock.acquire();
            Thread.currentThread().sleep(TxInterval);
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(MulticastIp), port);
            this.udpSockect.send(datagramPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
