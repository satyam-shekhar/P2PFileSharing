/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author satyam
 */
public class DSHandle implements Serializable{
    public static final long serialVersionUID = 123L;
    public static class Entity implements Serializable{
        public static final long serialVersionUID = 122L;
        public String name;
        public String path;
        public long size;
        public boolean isfolorfile;
        public Entity(String name,String path,long size,boolean isFolderOrFile){
            this.name=name;
            this.path=path;
            this.size=size;
            this.isfolorfile=isFolderOrFile;
        }
    }
    public int last=0;
    public ArrayList<ArrayList<Entity>> fileNFolderTree=new ArrayList<>();
    public HashMap<String,Integer> pathToIndex;
}
