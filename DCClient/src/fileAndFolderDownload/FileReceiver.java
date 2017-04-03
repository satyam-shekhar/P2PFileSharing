/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileAndFolderDownload;

import com.sun.javafx.font.freetype.HBGlyphLayout;
import filelisting.ConvertSize;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import peerManage.UserDetails;
import serialds.DSHandle;
import serialds.QueryObj;

/**
 *
 * @author satyam
 */
public class FileReceiver extends javax.swing.JFrame {

    /**
     * Creates new form FileReceiver
     */
    DefaultTableModel dtm;
    HashMap<String,Integer> entryInTable=new HashMap<>();
    String desFileLocation,souFileLocation,file;
    QueryObj qobj;
    JFrame curFrame;
    File currentDownFile,oldFile;
    String peer;
    int peerPort,curTotal=0,totalFiles=0,dfs=0;
    Socket sock;
    UserDetails userDetails;
    BufferedInputStream bis;
    PrintStream ps;
    DSHandle dsObj;
    boolean isFolderOrFile,cancelled=false,pause=false,stop=false,oldPath=false,sigForClose=true;
    public FileReceiver(QueryObj qobj,boolean isFolderOrFile,UserDetails userDetails) {
        initComponents();
        this.userDetails=userDetails;
        dtm=(DefaultTableModel)curFileQueue.getModel();
        this.qobj=qobj;
        this.isFolderOrFile=isFolderOrFile;
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        curFrame=this;
        this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    stopButton.doClick();
                }
            });
        peer=userDetails.nickNameToIp.get(qobj.user).getHostAddress();
        peerPort=userDetails.nickNameToPort.get(qobj.user)+3;
//        peerPort=5002;
            
        if(this.isFolderOrFile){
            try{
            Socket sock=new Socket(peer,peerPort);
            System.out.println("connected with peer for DSHAND");
            ObjectInputStream ois=new ObjectInputStream(sock.getInputStream());
            System.out.println("Got DSHAND");
            dsObj=(DSHandle)ois.readObject();  
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex.getMessage());
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null,ex.getMessage());
            }
            getTotalFiles("",qobj.nodeNo);
        }
        else{
            entryInTable.put(qobj.path,totalFiles++);
            dtm.addRow(new Object[]{qobj.name,new ConvertSize().getSize(qobj.size),"X"});
        }
        new startDown().start();
    }

    void getTotalFiles(String location,int nodeNo){
        for(int i=0;i<dsObj.fileNFolderTree.get(nodeNo).size();i++){
            DSHandle.Entity entObj=dsObj.fileNFolderTree.get(nodeNo).get(i);
            if(entObj.isfolorfile){
                getTotalFiles(location+"/"+entObj.name,dsObj.pathToIndex.get(entObj.path));
            }
            else{
                entryInTable.put(entObj.path,totalFiles++);
                dtm.addRow(new Object[]{entObj.name,new ConvertSize().getSize(entObj.size),"X"});
            }
        }
    }
    class startDown extends Thread{
        public startDown(){
            curTotal=0;
        }
        void downFile(QueryObj qobj,String loca){
            try {
                sigForClose=false;
                sock=new Socket(peer,peerPort+1);
        //        sock=new Socket(peer,5001);
                bis=new BufferedInputStream(sock.getInputStream());
                System.out.println("Sent rquest to peer for getting file");
                ObjectOutputStream oos=new ObjectOutputStream(sock.getOutputStream());
                currentDownFile=new File(loca);
                long off=currentDownFile.length();
                long tem=qobj.size;
                qobj.size=off;
                oos.writeObject(qobj);
                qobj.size=tem;
                int re=0;
                FileOutputStream fout=new FileOutputStream(currentDownFile,true);
                byte bytar[]=new byte[1024];
                curFileName.setText(qobj.name);
                long tot=off;
                System.out.println(qobj.size+"");
                long start=System.currentTimeMillis();
                long ini=0;
                while((re=bis.read(bytar))!=-1){
                    tot+=re;
                    long cur=System.currentTimeMillis();

                    if(cur-start>=1500)
                    {
                        double spped=(tot-ini)/((cur-start)/1000.0);
                        speedLabel.setText("Speed: " + new ConvertSize().getSize(spped) + "/s");
                        start=cur;
                        ini=tot;
                    }
                    currentDownloadedLabel.setText("Downloaded: "+new ConvertSize().getSize(tot));
                    int prog=(int)(tot*100.0/qobj.size);
                    downProgress.setValue(prog);
              //      System.out.println(prog);
                    fout.write(bytar, 0,re);
                    percentDisplay.setText(prog+"%");
                    if(stop||cancelled||pause)
                           break;
                }
                fout.close();
                curTotal++;
                if(!stop&&!cancelled&&!pause)       
                    dtm.setValueAt("Done", entryInTable.get(qobj.path), 2);
                else{
                    dtm.setValueAt("X", entryInTable.get(qobj.path), 2);
                    if(cancelled||stop){
                        currentDownFile.delete();
                        cancelled=false;
                    }
                }
                System.out.println("file downloaded "+qobj.name);
                if(curTotal==totalFiles && !pause)
                {JOptionPane.showMessageDialog(null,"Downloading Finished!!!");exi();}

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"Cannot create file :"+ex.getMessage());
            }
                sigForClose=true;

        }
        void getAllFiles(String location,int nodeNo){
            File fil=new File(location);
            if(!fil.exists())
                    fil.mkdirs();
            for(int i=0;i<dsObj.fileNFolderTree.get(nodeNo).size();i++){
                DSHandle.Entity entObj=dsObj.fileNFolderTree.get(nodeNo).get(i);
                if(entObj.isfolorfile){

                    getAllFiles(location+"/"+entObj.name,dsObj.pathToIndex.get(entObj.path));
                }
                else{
                    dfs++;
                    if(stop||pause)
                        continue;
                    File file=new File(location+"/"+entObj.name);
                    long upOff=0;
                    if(file.exists())
                            upOff=file.length();
                    else try {
                        file.createNewFile();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"Cannot create file:"+ex.getMessage());
                    }
                    QueryObj qobj=new QueryObj(entObj.name, entObj.path,"", false, 0, entObj.size);
                    downFile(qobj,file.getPath());
                }
            }
        }
        public void run(){
            try{
            JFileChooser fc=new JFileChooser();
            File f=new File(System.getProperty("user.home"));
            fc.setCurrentDirectory(f);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal=0;
            if(!oldPath)
                returnVal = fc.showOpenDialog(null);
            File file=null;
            if (oldPath||returnVal == JFileChooser.APPROVE_OPTION) {
            if(!oldPath){
                file = fc.getSelectedFile();
                oldFile=file;
                oldPath=true;
            }
            else file=oldFile;
            
            if(isFolderOrFile){
                     
                String path=file.getPath();
                getAllFiles(file.getPath(),qobj.nodeNo);


            }
            else{
                dfs++;
                File ff=new File(file.getPath()+"/"+qobj.name);
                    long upOff=0;
                    if(ff.exists())
                            upOff=ff.length();
                //    qobj.size=upOff;
                    
                downFile(qobj,file.getPath()+"/"+qobj.name);
            }

            }
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,e.getMessage());
            }
            
        }
    }
    public void exi(){
        this.dispose();
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stopButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        downProgress = new javax.swing.JProgressBar();
        curFileName = new javax.swing.JLabel();
        percentDisplay = new javax.swing.JLabel();
        resumeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        curFileQueue = new javax.swing.JTable();
        currentDownloadedLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        speedLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        stopButton.setText("Stop & Close");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("CancelCurrent");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        curFileName.setText("CurFileName");

        percentDisplay.setText("0%");

        resumeButton.setText("Resume");
        resumeButton.setEnabled(false);
        resumeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeButtonActionPerformed(evt);
            }
        });

        curFileQueue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "List of files", "File Size", "Downloaded Or Not"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(curFileQueue);

        currentDownloadedLabel.setText("Downloaded :");

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel1.setText("Donwnloading File(s)");

        speedLabel.setText("Speed :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(138, 138, 138)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(currentDownloadedLabel)
                                .addGap(66, 66, 66)
                                .addComponent(percentDisplay)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(downProgress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(curFileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(resumeButton)
                                .addGap(74, 74, 74)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(stopButton)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(pauseButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(speedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(curFileName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(downProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(percentDisplay)
                    .addComponent(currentDownloadedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(speedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resumeButton)
                    .addComponent(pauseButton)
                    .addComponent(cancelButton))
                .addGap(33, 33, 33)
                .addComponent(stopButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        pause=true;
        resumeButton.setEnabled(true);
        pauseButton.setEnabled(false);
        cancelButton.setEnabled(false);
        // TODO add your handling code here:
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        cancelled=true;
        
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        try {
            // TODO add your handling code here:
            stop=true;
            while(!sigForClose)
                sleep(100);
            if(!pauseButton.isEnabled())
                currentDownFile.delete();
            curFrame.dispose();
        } catch (InterruptedException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_stopButtonActionPerformed

    private void resumeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeButtonActionPerformed
        // TODO add your handling code here:
        while(dfs!=totalFiles)
              try {
                  sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        pause=stop=cancelled=false;
        dfs=0;
        curTotal=0;
        resumeButton.setEnabled(false);
        pauseButton.setEnabled(true);
        cancelButton.setEnabled(true);
        new startDown().start();
    }//GEN-LAST:event_resumeButtonActionPerformed

    /**
     * @param args the command line arguments
     */
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel curFileName;
    private javax.swing.JTable curFileQueue;
    private javax.swing.JLabel currentDownloadedLabel;
    private javax.swing.JProgressBar downProgress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pauseButton;
    private javax.swing.JLabel percentDisplay;
    private javax.swing.JButton resumeButton;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
