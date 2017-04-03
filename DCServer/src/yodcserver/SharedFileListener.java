/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import serialds.DSHandle;

/**
 *
 * @author satyam
 */
class SharedFileListener implements Runnable {

    ServerSocket sock;

    public SharedFileListener() {
        try {
            sock = new ServerSocket(3002);
        } catch (IOException ex) {
            Logger.getLogger(SharedFileListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Socket cliSock = sock.accept();
                ObjectInputStream ois = new ObjectInputStream(cliSock.getInputStream());
                DSHandle fileList = (DSHandle)ois.readObject();
                InetAddress ip = cliSock.getInetAddress();
                String nickName = IncomingConnectionHandler.nickMap.get(ip);
                File f = new File("E:\\myDC++\\" + nickName);
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(fileList);
                oos.flush();
                oos.close();
                fos.close();
                cliSock.close();
            } catch (IOException ex) {
                Logger.getLogger(SharedFileListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SharedFileListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
