/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileAndFolderDownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import peerManage.UserDetails;
import serialds.QueryObj;

/**
 *
 * @author satyam
 */
public class FileSender extends Thread {
    String desFileLocation,file;
    UserDetails userDet;
    public FileSender(UserDetails userDet){
        this.userDet=userDet;
    }
    class  send extends Thread{
        Socket sock;
        public send(Socket sock){
            this.sock=sock;
        }
        public void run(){
        try{
        ObjectInputStream ois=new ObjectInputStream(sock.getInputStream());
        QueryObj qobj=(QueryObj) ois.readObject();
        BufferedOutputStream bos=new BufferedOutputStream(sock.getOutputStream());
        int re=0;
            System.out.println("feeding request");
        File infile=new File(qobj.path);
        FileInputStream fin=new FileInputStream(infile);
        byte bytar[]=new byte[1024];
            System.out.println("File offset: "+qobj.size);
        fin.skip(qobj.size);
        while((re=fin.read(bytar))!=-1){
            bos.write(bytar,0,re);
        }
            System.out.println("file sent");
            bos.close();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"File Sending:"+e.getMessage());
        }
        }
    }
    public void run (){
        try {
            ServerSocket serSock=new ServerSocket(userDet.curPort+4);
            System.out.println("Got request from client to feed file");
            while(true){
                Socket sock=serSock.accept();
                new send(sock).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
