/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import serialds.UserSignal;
import static yodcserver.IncomingConnectionHandler.add;

/**
 *
 * @author satyam
 */
public class ConnectionBroadcaster extends Thread {
    
    private String nick;
    DatagramSocket sock; 
    DatagramPacket pack;
    Socket sendSock;
    InetAddress myIP;
    int port;
    boolean isConnecting;
    
    public ConnectionBroadcaster(String name, InetAddress myIP, boolean isConnecting,int port) {
        this.nick = name;
        this.myIP = myIP;
        this.port=port;
        this.isConnecting = isConnecting;
        try {
            sock = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(ConnectionBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run () {
        HashMap<String,InetAddress> ipMap = IncomingConnectionHandler.ipMap;
        String message = null;
        if (!isConnecting) {
            message = nick + "0";
        }
        else {
            message = nick + "1";
        }
        System.out.println(message);
        UserSignal sig = new UserSignal(myIP, nick, isConnecting,port);
        byte[] buf = new byte[1024];
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(buf.length);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(sig);
            oos.flush();
            buf = baos.toByteArray();
            oos.close();
            baos.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<InetAddress> add = IncomingConnectionHandler.add;
        for (InetAddress ip : add) {
            try {
                pack = new DatagramPacket(buf, buf.length, ip, IncomingConnectionHandler.nickPortMap.get(
                IncomingConnectionHandler.nickMap.get(ip)));
                
                System.out.println("IP " + ip.toString() + " welcomes you!");
                sock.send(pack);
            } catch (IOException ex) {
                Logger.getLogger(ConnectionBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        IncomingConnectionHandler.add.add(myIP);
        add = IncomingConnectionHandler.add;
        for (InetAddress ip : add) {
            try {
                UserSignal obj = new UserSignal (ip,IncomingConnectionHandler.nickMap.get(ip),true,IncomingConnectionHandler.nickPortMap.get(nick));
                ByteArrayOutputStream baos = new ByteArrayOutputStream(buf.length);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                oos.flush();
                buf = baos.toByteArray();
                oos.close();
                baos.close();
                pack = new DatagramPacket(buf,buf.length,myIP,port);
                System.out.println("Sending " + ip + " to " + myIP);
                sock.send(pack);
            } catch (IOException ex) {
                Logger.getLogger(ConnectionBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sock.close();
    }
}