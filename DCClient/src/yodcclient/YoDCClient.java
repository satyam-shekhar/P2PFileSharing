/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcclient;

import filelisting.GetQueryResult;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import peerManage.UserDetails;

/**
 *
 * @author satyam
 */
public class YoDCClient {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //new SplashScreen().setVisible(true);
        StartingWindow sw = new StartingWindow();
        sw.setLocationRelativeTo(null);
        sw.setVisible(true);
            
            
            
//        try {
//            InetAddress iad=InetAddress.getByName("localhost");
//            UserDetails useObj=new UserDetails("Getter","localhost");
//            useObj.nickNameToIp.put("FUCK",iad);
//            new GetQueryResult(false,"FUCK",useObj).setVisible(true);
//        } catch (UnknownHostException ex) {
//            Logger.getLogger(YoDCClient.class.getName()).log(Level.SEVERE, null, ex);
//    }

}
}