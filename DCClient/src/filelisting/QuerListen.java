/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filelisting;

import peerManage.UserDetails;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;
import serialds.QueryObj;

/**
 *
 * @author satyam
 */
public class QuerListen extends Thread{
    UserDetails userObj;
    public QuerListen(UserDetails userObj){
        this.userObj=userObj;
    }
    public void run(){
        try{
        ServerSocket sock=new ServerSocket(userObj.curPort+2);
        while(true){
            System.out.println("Listener started\n");
            Socket cli=sock.accept();
            System.out.println("Socket ac");
            ObjectInputStream ois=new ObjectInputStream(cli.getInputStream());
            QueryObj obj= (QueryObj) ois.readObject();
            new SendQueryResult(obj,cli,userObj).start();
        }
        }
        catch(Exception E){
            JOptionPane.showMessageDialog(null, E.getMessage());
        }
        
    }
}
