/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peerManage;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import serialds.DSHandle;

/**
 *
 * @author satyam
 */
public class UserDetails {
    public HashMap<String,InetAddress> nickNameToIp;
    public HashMap<String,Integer> nickNameToPort;
    public int curPort;
    public String curUser;
    public String serverIP;
    public DSHandle shareFileNfolder;
    public UserDetails(String nick,String ip,int port){
        curUser=nick;
        curPort=port;
        serverIP=ip;
        nickNameToIp=new HashMap<>();
        nickNameToPort=new HashMap<>();
        shareFileNfolder=new DSHandle();
    }
}
