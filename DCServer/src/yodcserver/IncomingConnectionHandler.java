/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author satyam
 */
public class IncomingConnectionHandler implements Runnable {
        
    public static HashMap<String,InetAddress> ipMap;
    public static HashMap<InetAddress, String> nickMap;
    public static HashMap<String, Integer> nickPortMap;
    public static ArrayList<InetAddress> add;
    private ServerSocket serv;
    private ServerSocket linker;

    public IncomingConnectionHandler() {
        ipMap = new HashMap<>();
        nickMap = new HashMap<>();
        nickPortMap = new HashMap<>();
        add = new ArrayList<>();
        try {
            serv = new ServerSocket(3000);
            linker = new ServerSocket(3600);
        } catch (IOException ex) {
            Logger.getLogger(IncomingConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    @Override
    public void run() {
        while (true) {
            try {
                Socket sp = serv.accept();
                final Socket pp = sp;
                System.out.println("connected : " + pp.getInetAddress().toString());
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            DataInputStream dis = new DataInputStream(pp.getInputStream());
                            String nickName = dis.readUTF();
                            int port=dis.readInt();
                            DataOutputStream dos = new DataOutputStream(pp.getOutputStream());
                            if (ipMap.containsKey(nickName)) {
                                dos.writeUTF("FAIL");
                                return;
                            }
                            else {
                                dos.writeUTF("OK");
                            }
//                            long fileSize = dis.readLong();
//                            
//                            byte[] buf = new byte[1];
//                            String path = "/home/vikrant/myDC/";
//                            File f = new File(path+nickName);
//                            System.out.println("NickName : " + nickName);
//                            if (!f.exists()) {
//                                f.createNewFile();
//                            }
//                            fos = new FileOutputStream(f);
//                            int re = 0;
//                            while (fileSize > 0 && (re = dis.read(buf)) != 0) {
//                                fos.write(buf);
//                                fileSize -= re;
//                            }
                            nickMap.put(pp.getInetAddress(), nickName);
                            nickPortMap.put(nickName, port);
                            ipMap.put(nickName, pp.getInetAddress());
                            System.out.println("NickName : " + nickName + " done !!!");
                            System.out.println(pp.getInetAddress());
                            new ConnectionBroadcaster(nickName, pp.getInetAddress(), true,port).start();
                            
                            Socket link = linker.accept();
                            
                            new Thread(new OnlineLink(link,nickName,link.getInetAddress())).start();
                        } catch (IOException ex) {
                            Logger.getLogger(IncomingConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }).start();
            } catch (IOException ex) {
                Logger.getLogger(IncomingConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
