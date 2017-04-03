/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
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

/**
 *
 * @author satyam
 */
public class OnlineLink implements Runnable {

    private final Socket con;
    private final String nick;
    private final InetAddress ip;
    
    OnlineLink(Socket pp, String nick, InetAddress ip) {
        this.con = pp;
        this.nick = nick;
        this.ip = ip;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("linker started");
            DataInputStream dis = new DataInputStream(con.getInputStream());
            dis.read();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        for (int i=0 ; i < IncomingConnectionHandler.add.size() ; i++) {
            if (IncomingConnectionHandler.add.get(i).getHostAddress().equals(ip.getHostAddress())) {
                IncomingConnectionHandler.add.remove(i);
                break;
            }
        }

        IncomingConnectionHandler.nickMap.remove(ip);
        IncomingConnectionHandler.nickPortMap.remove(nick);
        IncomingConnectionHandler.ipMap.remove(nick);
        
        File f = new File("E:\\myDC++\\"+nick);
        if (f.exists()) {
            f.delete();
        }
        System.out.println(nick + " BHAAG GAYA!!");
        try {
            DatagramSocket sock = new DatagramSocket();
            UserSignal sig = new UserSignal(ip,nick,false,0);
            for (InetAddress it : IncomingConnectionHandler.add) {
                //DatagramSocket
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bout);
                oos.writeObject(sig);
                DatagramPacket pack = new DatagramPacket(bout.toByteArray(),bout.toByteArray().length, it, 
                        IncomingConnectionHandler.nickPortMap.get(IncomingConnectionHandler.nickMap.get(it)
                ));
                sock.send(pack);
            }
        } catch (SocketException ex) {
            Logger.getLogger(OnlineLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OnlineLink.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
