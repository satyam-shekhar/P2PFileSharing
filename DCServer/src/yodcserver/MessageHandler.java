/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author satyam
 */
public class MessageHandler extends Thread {
    
    private ArrayList<InetAddress> add;
    HashMap<String,Integer> nickPortMap;
    private DatagramSocket msgSock;
    private DatagramSocket sendSock;
    private final int PORT = 4500;
    
    public MessageHandler() {
        try {
            nickPortMap=IncomingConnectionHandler.nickPortMap;
            add = IncomingConnectionHandler.add;
            msgSock = new DatagramSocket(PORT);
            sendSock = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run () {
        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buf,buf.length);
            try {
                msgSock.receive(pack);
                String msg = new String (pack.getData(),0,pack.getLength());
                System.out.println(msg);
               
                buf = msg.getBytes();
                for (InetAddress ip : add) {
                    try {
                        pack = new DatagramPacket(buf, buf.length, ip,1+nickPortMap.get(IncomingConnectionHandler.nickMap.get(ip)));
                        sendSock.send(pack);
                    } catch (IOException ex) {
                        Logger.getLogger(ConnectionBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
