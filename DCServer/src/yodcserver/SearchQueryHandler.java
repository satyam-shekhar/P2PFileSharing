/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import serialds.DSHandle;
import serialds.DSHandle.Entity;
import serialds.QueryObj;

/**
 *
 * @author satyam
 */
public class SearchQueryHandler implements Runnable {
    
    ServerSocket sock;
    private final int PORT = 9000; 
    DSHandle obj;
    Socket cliSock;
    ObjectOutputStream oos;
    
    public SearchQueryHandler () {
        try {
            sock = new ServerSocket (PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                cliSock = sock.accept();
            } catch (IOException ex) {
                Logger.getLogger(SearchQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            new Thread(new Runnable () {    
                @Override
                public void run() {
                    try {
                        System.out.println("Connected Query !!");
                        ObjectInputStream dis = new ObjectInputStream(cliSock.getInputStream());
                        oos = new ObjectOutputStream(cliSock.getOutputStream());
                        QueryObj request = (QueryObj)dis.readObject();
                        String key = request.name;
                        //String key = "gym";
                        System.out.println("Got query !! " + key);
                        File root = new File("E:\\myDC++\\");
                        File[] files = root.listFiles();
                        for (File f : files) {
                            FileInputStream fis = new FileInputStream(f);
                            ObjectInputStream ois = new ObjectInputStream(fis);
                            obj = (DSHandle) ois.readObject();
                            ois.close();
                            String nick = f.getName();
                            depthFirstSearch(key,"",0, nick);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SearchQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SearchQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }
    }
    
    private void depthFirstSearch (String key, String path, int root, String nick) {
        ArrayList<Entity> u = obj.fileNFolderTree.get(root);
        for (int i=0 ; i < obj.fileNFolderTree.get(root).size() ; i++) {
            Entity ent = obj.fileNFolderTree.get(root).get(i);
            int child;
            if (ent.isfolorfile)
                child = obj.pathToIndex.get(ent.path);
            else 
                child = -1;
            String name = ent.name;
            if (name.toLowerCase().contains(key.toLowerCase())) {
                /*
                send the entry by making queryobj
                */
                System.out.println(name + " " + key);
                QueryObj mail = new QueryObj(name, ent.path, nick, ent.isfolorfile , child, ent.size);
                try {
                    
                    System.out.println("writing shit");
                    oos.writeObject(mail);
                } catch (IOException ex) {
                    Logger.getLogger(SearchQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (ent.isfolorfile) {
                depthFirstSearch(key, ent.path, child, nick);
            }
            
        }
    }
}
