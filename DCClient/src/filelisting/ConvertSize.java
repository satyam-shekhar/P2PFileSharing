/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filelisting;

/**
 *
 * @author satyam
 */
public class ConvertSize {
    public String getSize(double size){
        if (size < 1024) {
            size = Math.round(size*100)/100.0;
            return String.valueOf(size) + " B";
        }
        size /= 1024.0;
        if (size < 1024) {
            size = Math.round(size*100)/100.0;
            return String.valueOf(size) + " kB";
        }
        size /= 1024.0;
        if (size < 1024) {
            size = Math.round(size*100)/100.0;
            return String.valueOf(size) + " MB";
        }
        size /= 1024.0;
        if (size < 1024) {
            size = Math.round(size*100)/100.0;
            return String.valueOf(size) + " GB";
        }
        size /= 1024.0;
        if (size < 1024) {
            size = Math.round(size*100)/100.0;
            return String.valueOf(size) + " TB";
        }
        return null;
    }
}
