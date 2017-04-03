/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialds;

import java.io.Serializable;

/**
 *
 * @author satyam
 */
public class QueryObj implements Serializable{
    public static final long serialVersionUID = 121L;
    public String path;
    public String name;
    public String user;
    public boolean isFileOrFol;
    public int nodeNo;
    public long size;

    public QueryObj(String name, String path,String user, boolean isFileOrFol, int nodeNo, long size) {
        this.path = path;
        this.name = name;
        this.user=user;
        this.isFileOrFol = isFileOrFol;
        this.nodeNo = nodeNo;
        this.size = size;
    }
    
}
