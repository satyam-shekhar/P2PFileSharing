/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedFileAndFolder;

import serialds.DSHandle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author satyam
 */

public class FileFetcher {
    
    Queue<File> sharedFileNFolder=new LinkedList<File>();
    DSHandle dsObject;
    
    private void DFStoGetFilesNFolders(String curFile,int level){
        File f=new File(curFile);
            File[] faFiles = new File(curFile).listFiles();
            for(File file:faFiles){
                boolean isf=false;
                if(file.isDirectory()){
                    isf=true;
//                    System.out.println(file.getName());
                    dsObject.pathToIndex.put(file.getPath(),++dsObject.last);
                    dsObject.fileNFolderTree.add(new ArrayList<>());
                    DFStoGetFilesNFolders(file.getPath(),dsObject.last);
                }
                DSHandle.Entity obj=new DSHandle.Entity(file.getName(),file.getPath(),  file.length(), isf);
                dsObject.fileNFolderTree.get(level).add(obj);
            }
    }
    public DSHandle getFiles(){
        dsObject=new DSHandle();
        dsObject.pathToIndex=new HashMap();
        dsObject.fileNFolderTree.add(new ArrayList<>());
        dsObject.last=0;
        while(!sharedFileNFolder.isEmpty()){
            File curfile=sharedFileNFolder.peek();
            File f=curfile;
            boolean isf=false;
            if(f.isDirectory()){
                isf=true;
                dsObject.pathToIndex.put(curfile.getPath(),++dsObject.last);
                dsObject.fileNFolderTree.add(new ArrayList<>());
                DFStoGetFilesNFolders(curfile.getPath(),dsObject.last);
            }
            DSHandle.Entity obj=new DSHandle.Entity(sharedFileNFolder.peek().getName(),sharedFileNFolder.peek().getPath(), (int) f.length(), isf);
            dsObject.fileNFolderTree.get(0).add(obj);
            sharedFileNFolder.remove();
        }
        dsObject.pathToIndex.put("#",0);
        return dsObject;
    }
    public FileFetcher(Queue<File> shfnfo){
        this.sharedFileNFolder=shfnfo;
    }
}
