/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peerManage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import serialds.UserSignal;
import yodcclient.StartingWindow;

/**
 *
 * @author satyam
 */
public class ConOrDisSigReceive extends Thread{
    private CommonWindow curList;
    private UserDetails userDetails;
    private DatagramSocket recSigSock;
    public ConOrDisSigReceive(CommonWindow curList,UserDetails userDetails,DatagramSocket ds){
        this.curList=curList;
        recSigSock=ds;
        this.userDetails=userDetails;
    }
    public void run(){
            try {
        while(true){
                byte buf[]=new byte[1000];
                DatagramPacket dp = new DatagramPacket(buf,buf.length);
                recSigSock.receive(dp);
                ByteArrayInputStream bais=new ByteArrayInputStream(dp.getData());
                ObjectInputStream ois=new ObjectInputStream(bais);
                UserSignal sig=(UserSignal) ois.readObject();
                System.out.println("print obj received user:");
                if(sig.isConnected){
                  userDetails.nickNameToIp.put(sig.name,sig.ip);
                  userDetails.nickNameToPort.put(sig.name,sig.port);
                  curList.dlm.addElement(sig.name);
                  curList.userToIndex.put(sig.name,curList.dlm.getSize()-1);
                }
                else{
                   //  curList.dlm.removeElementAt(curList.userToIndex.get(sig.name));
                   //  curList.userToIndex.remove(sig.name);
                    if(userDetails.nickNameToIp.containsKey(sig.name))
                         userDetails.nickNameToIp.remove(sig.name);
                    if(!curList.userToIndex.containsKey(sig.name))
                            continue;
                     curList.dlm.removeElementAt(curList.userToIndex.get(sig.name));
                     curList.userToIndex.clear();
                     for(int i=0;i<curList.dlm.getSize();i++){
                         curList.userToIndex.put(curList.dlm.getElementAt(i).toString(), i);
                     }
                    }
            }
            } catch (SocketException ex) {
                Logger.getLogger(ConOrDisSigReceive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ConOrDisSigReceive.class.getName()).log(Level.SEVERE, null, ex);
        
        
       } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConOrDisSigReceive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
