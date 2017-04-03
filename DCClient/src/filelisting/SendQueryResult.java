/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filelisting;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import peerManage.UserDetails;
import serialds.DSHandle;
import serialds.QueryObj;
import sharedFileAndFolder.FileAndFolerChooser;

/**
 *
 * @author nikhil
 */
public class SendQueryResult extends Thread{
    QueryObj getListobj;
    Socket host;
    UserDetails userObj;
    DSHandle shareFileAndFolder;
    public SendQueryResult(QueryObj obj,Socket host,UserDetails userObj){
        getListobj=obj;
        this.userObj=userObj;
        this.host=host;
    }
    public void run(){
        try {
            this.shareFileAndFolder=userObj.shareFileNfolder;
            ObjectOutputStream oos=new ObjectOutputStream(host.getOutputStream());
            ArrayList<DSHandle.Entity> ar=shareFileAndFolder.fileNFolderTree.get(shareFileAndFolder.pathToIndex.get(getListobj.path));
            for(int i=0;i<ar.size();i++){
                QueryObj obj=new QueryObj(ar.get(i).name,ar.get(i).path,userObj.curUser,ar.get(i).isfolorfile,
                        0,ar.get(i).size
                );
                if(obj.isFileOrFol)
                     obj.nodeNo=shareFileAndFolder.pathToIndex.get(obj.path);
                oos.writeObject(obj);
                System.out.println(obj.path);
            }
        } catch (IOException ex) {
            Logger.getLogger(SendQueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
