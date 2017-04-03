/*              
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileAndFolderDownload;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.UserDataHandler;
import peerManage.UserDetails;

/**
 *
 * @author satyam
 */
public class sendDSHandle extends Thread{
    UserDetails userDetails;
    class DSSender extends Thread{
        Socket sock;
        public DSSender(Socket sock){
            this.sock=sock;
        }
        public void run(){
            try {
                ObjectOutputStream oos=new ObjectOutputStream(sock.getOutputStream());
                oos.writeObject(userDetails.shareFileNfolder);
                System.out.println("Feeded DS hand request");
            } catch (IOException ex) {
                Logger.getLogger(sendDSHandle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public sendDSHandle(UserDetails userDetails){
        this.userDetails=userDetails;}
        public void run(){
        try {
            ServerSocket serSock=new ServerSocket(userDetails.curPort+3);
            while(true){
            Socket sock=serSock.accept();
            new DSSender(sock).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(sendDSHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
