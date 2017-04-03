/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialds;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author satyam
 */
public class UserSignal implements Serializable {
    public static final long serialVersionUID = 130L;
    public InetAddress ip;
    public String name;
    public Integer port;
    public boolean isConnected;
    public UserSignal(InetAddress ip,String name,boolean isConnected,int port){
        this.ip=ip;
        this.port=port;
        this.name=name;
        this.isConnected=isConnected;
    }
}
