/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filelisting;

import fileAndFolderDownload.FileReceiver;
import filelisting.QuerListen;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import peerManage.UserDetails;
import serialds.QueryObj;

/**
 *
 * @author satyam
 */
public class GetQueryResult extends javax.swing.JFrame {

    /**
     * Creates new form DisplayResult
     */
    ArrayList<QueryObj> listElements=new ArrayList<>();
    Stack<ArrayList<QueryObj>> history=new Stack<>();
    boolean stopDisplaying=false;
    UserDetails userObj;
    String upIP;
    
    int upPort;
    DefaultTableModel dtm;
    UserDetails userDetails;
    Socket sock=null;
    public GetQueryResult(UserDetails user){
        userDetails=user;
    }
    class dispNGetter extends Thread {
        QueryObj curObj;
        boolean isSearchOrListing;
        public dispNGetter(QueryObj obj,boolean isSearchOrListing){
            curObj=obj;
            this.isSearchOrListing=isSearchOrListing;
        }
        public void run(){
            try {
                if(sock!=null)
                    sock.close();
                sock=new Socket(upIP,upPort);
                System.out.println("Connected to peer or server for file listing");
                System.out.println("GET SOCK");
 //               if(isSearchOrListing){
                    ObjectOutputStream oos=new ObjectOutputStream(sock.getOutputStream());
                    oos.writeObject(curObj);
//                }
                ObjectInputStream ois=new ObjectInputStream(sock.getInputStream());
                boolean fl=false;
                while(true){
                    QueryObj ob;
                    try{
                        ob=(QueryObj) ois.readObject();
                    }
                    catch(Exception e){
                        break;
                    }
                    if(!fl)
                    {
                        listElements=new ArrayList<>();
                        
                        while(dtm.getRowCount()!=0)
                            dtm.removeRow(0);
                        fl=true;
                    }
                    System.out.println(ob.path);
                    
                    Object rowData[]={ob.name,new ConvertSize().getSize(ob.size),ob.user};
                    if (ob.isFileOrFol) {
                        rowData[0] = "> " + rowData[0] + "/";
                    }
                    dtm.addRow(rowData);
                    System.out.println("Set model");
                    listElements.add(ob);
                    if(stopDisplaying)
                    {
                        stopDisplaying=false;
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(GetQueryResult.class.getName()).log(Level.SEVERE, null, ex);
            }
            history.push(listElements);
            
        }
    }
    public GetQueryResult(boolean isSearchOrListing,String query,UserDetails userObj) {
        initComponents();
        dtm=(DefaultTableModel) contentDisplay.getModel();
        this.userDetails=userObj;
        this.userObj=userObj;
        contentDisplay.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent ev){
            System.out.println("Mouse pressed");
            
            if(ev.getClickCount()==2){
                
                System.out.println("Mouse 2 pressed");
                int selItem=contentDisplay.getSelectedRow();
                if(selItem==-1){
                    JOptionPane.showMessageDialog(null,"Select proper item");
                    return;
                }
                QueryObj qobj=listElements.get(selItem);
                upIP=userDetails.nickNameToIp.get(qobj.user).getHostAddress();
                upPort=userObj.nickNameToPort.get(qobj.user)+2;
                if(qobj.isFileOrFol)
                    new dispNGetter(qobj,false).start();
                else{
                    int option=JOptionPane.showConfirmDialog(null,"Want to download filE??","DOW FILE",1);
                    if(option==JOptionPane.YES_OPTION) {
                        FileReceiver fr = new FileReceiver(qobj,false,userDetails);
                        fr.setLocationRelativeTo(null);
                        fr.setVisible(true);
                    }
                    
                    
                }
            }
        }
        });
        if(isSearchOrListing){
            upIP=userObj.serverIP;
            upPort=9000;
//            QueryObj obj=new QueryObj(query,"",userObj.curUser,false,0, 0);
//            new dispNGetter(obj,true).start();
        }
        else{
            upIP=userObj.nickNameToIp.get(query).getHostAddress();
            upPort=userObj.nickNameToPort.get(query)+2;
            QueryObj obj=new QueryObj("","#",this.userObj.curUser,true,0, 0);
            new dispNGetter(obj, isSearchOrListing).start();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchTF = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        downloadButton = new javax.swing.JButton();
        refreshList = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        contentDisplay = new javax.swing.JTable();
        back = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        downloadButton.setText("Download");
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        refreshList.setText("Refresh List");
        refreshList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshListActionPerformed(evt);
            }
        });

        contentDisplay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Or folder", "Size", "Peer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(contentDisplay);

        back.setText("<-");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });

        jLabel1.setText("Search :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(back))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(searchTF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(refreshList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton)
                    .addComponent(jLabel1))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(downloadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refreshList))
                    .addComponent(back))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String querString=searchTF.getText();
        upIP=userDetails.serverIP;
        upPort=9000;
        QueryObj obj=new QueryObj(querString,"",userObj.curUser,false,0, 0);
        new dispNGetter(obj,true).start();
// TODO add your handling code here:
    }//GEN-LAST:event_searchButtonActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        int ind=contentDisplay.getSelectedRow();
        if(ind==-1)
                JOptionPane.showMessageDialog(null,"Pls select file");
        else{
            QueryObj qobj=listElements.get(ind);
            FileReceiver fr = new FileReceiver(qobj,qobj.isFileOrFol,userDetails);
            fr.setLocationRelativeTo(null);
            fr.setVisible(true);
        }
        
        // TODO add your handling code here:
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void refreshListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshListActionPerformed
        while(dtm.getRowCount()!=0)
            dtm.removeRow(0);
        for(QueryObj obj:listElements){
            Object rowData[]={obj.name,new ConvertSize().getSize(obj.size),obj.user};
            if (obj.isFileOrFol) {
                rowData[0] = "> " + rowData[0] + "/";
            }
            dtm.addRow(rowData);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_refreshListActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        // TODO add your handling code here:
        while(dtm.getRowCount()!=0)
            dtm.removeRow(0);
        if(!history.isEmpty()){
            ArrayList<QueryObj> tem=history.pop();
            listElements=tem;
            
            for(QueryObj obj:tem){
                Object rowData[]={obj.name,new ConvertSize().getSize(obj.size),obj.user};
                if (obj.isFileOrFol) {
                    rowData[0] = "> " + rowData[0] + "/";
                }
                dtm.addRow(rowData);
            }
        }
    }//GEN-LAST:event_backActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton back;
    private javax.swing.JTable contentDisplay;
    private javax.swing.JButton downloadButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton refreshList;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTF;
    // End of variables declaration//GEN-END:variables
}
