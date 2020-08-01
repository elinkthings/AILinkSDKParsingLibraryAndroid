package cn.net.aicare.modulelibrary.module.wifi.UDP;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 * Created by Administrator on 2015/11/18.
 */
public class UDPMultiServer {
    private MulticastSocket udpSocket;
    private int port = 10001;
    private String multicastIp = "239.0.0.1";
    private Boolean isStop = false;
    private Boolean isClose = false;

    private WifiManager.MulticastLock lock;
    private DatagramPacket datagramPacket;
    private EditText info;
    public UDPMultiServer(Context context){
        try {
            this.udpSocket = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(multicastIp);
            this.udpSocket.joinGroup(group);
            this.udpSocket.setBroadcast(true);
            WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            lock = manager.createMulticastLock("multicastLock");
            lock.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void stop(){
        if(!this.isStop){
            this.isStop = true;
        }
    }
    public synchronized void start(){
        if(this.isStop){
            this.isStop = false;
        }
    }
    public boolean getIsStop(){
        return this.isStop;
    }
    public synchronized void resume(){
        if(this.isStop){
            this.isStop = false;
        }
    }
    public synchronized void close(){
        if (lock != null) {
            lock.release();
        }
        if(!this.isClose){
            this.udpSocket.close();
            this.isClose = true;
        }
    }
    public synchronized void acquirelock(){
        if (lock != null && !lock.isHeld()) {
            lock.acquire();
        }
    }
    protected void finallize() throws Throwable {
        close();
        super.finalize();
    }
    public DatagramPacket receive() {
        byte[] buf = new byte[1024];
        datagramPacket = new DatagramPacket(buf, buf.length);
        try {
            lock.acquire();
            udpSocket.receive(datagramPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return datagramPacket;
        }
    }
    public int getPort() {
        return port;
    }
}
