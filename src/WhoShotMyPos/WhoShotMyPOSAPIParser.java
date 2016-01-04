/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WhoShotMyPos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

/**
 *
 * @author Alex
 */
public class WhoShotMyPOSAPIParser extends javax.swing.JFrame {
    
    Timer timer;
    SlackApi api;
    HashMap<Integer, String> systemNames = new HashMap();
    ArrayList notificationID = new ArrayList();

    /**
     * Creates new form WhuDuShutPosGUI
     */
    public WhoShotMyPOSAPIParser() {
        initComponents();
        loadSystemName();
        loadNotificationIDs();
        api = new SlackApi("https://hooks.slack.com/services/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");        
    }

    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198 notifications request.
    //keyID, vCode, characterID.
    //https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198&IDs=530705490 notifications text request.
    //keyID, vCode, characterID, notification id(s) from previous request.
    //https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=94818624 character info request.
    //Character name, corp name, alliance name, shield hp, time, system?
    //Luke API
    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=455684&vCode=oAPDhYH9pc063j5GWszkvwvpPwC3fPD6FX515Q1JAl79RXoBhy9GInhMNth2Dutu&characterID=151627406
    //public void getNotifications() {
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            String timeDate = null;
            String character = null;
            String corp = null;
            String alliance = null;
            String shieldHP = null;
            String system = null;
            try {                
                URL url = new URL(weblink.getText());
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("typeID=\"75\"")) {                            
                            String s = inputLine.substring(inputLine.indexOf("=") + 2, inputLine.indexOf("typeID") - 2);
                            if (!notificationID.contains(s)) {                                
                                notificationID.add(s);
                                timeDate = inputLine.substring(inputLine.indexOf("sentDate=") + 10, inputLine.indexOf("sentDate=") + 29);
                                String edit = weblink.getText().replace("Notifications", "NotificationTexts");
                                URL url1 = new URL(edit + "&IDs=" + s);
                                try (BufferedReader in1 = new BufferedReader(new InputStreamReader(url1.openStream()))) {
                                    String inputLine2;
                                    while ((inputLine2 = in1.readLine()) != null) {                                        
                                        if (inputLine2.contains("aggressorID")) {
                                            String s1 = inputLine2.substring(inputLine2.lastIndexOf(":") + 2);
                                            URL url2 = new URL("https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=" + s1);
                                            try (BufferedReader in2 = new BufferedReader(new InputStreamReader(url2.openStream()))) {
                                                String inputLine3;
                                                while ((inputLine3 = in2.readLine()) != null) {
                                                    if (inputLine3 != null && inputLine3.contains("characterName")) {
                                                        character = inputLine3.substring(inputLine3.indexOf(">") + 1, inputLine3.indexOf("</"));                                                        
                                                    }
                                                    if (inputLine3 != null && inputLine3.contains("<corporation>")) {
                                                        corp = inputLine3.substring(inputLine3.indexOf(">") + 1, inputLine3.indexOf("</"));                                                        
                                                    }
                                                    if (inputLine3 != null && inputLine3.contains("<alliance>")) {
                                                        alliance = inputLine3.substring(inputLine3.indexOf(">") + 1, inputLine3.indexOf("</"));
                                                    }                                                    
                                                }                                                
                                            }
                                        }
                                        if (inputLine2.contains("solarSystemID")) {
                                            String solarSystem = inputLine2.substring(inputLine2.lastIndexOf(":") + 2);
                                            int id = Integer.parseInt(solarSystem);
                                            system = systemNames.get(id);
                                        }
                                        if (inputLine2.contains("shieldValue")) {
                                            shieldHP = inputLine2.substring(inputLine2.lastIndexOf(":") + 4, inputLine2.lastIndexOf(":") + 6) +"%";  
                                            
                                        }                                        
                                    }
                                    outputPlace.append("Time: " + timeDate + "\nAttacker: " + character + " \nAttacker Corporation: " + corp + "\nAttacker Alliance: " + alliance + "\nPOS Shield HP: " + shieldHP 
                                            + "\nSystem: " + system + "\n---------------------");
                                    String output = "Time: " + timeDate + "\nAttacker: " + character + " \nAttacker Corporation: " + corp + "\nAttacker Alliance: " + alliance + "\nPOS Shield HP: " + shieldHP 
                                            + "\nSystem: " + system + "\n---------------------";
                                    saveNotificationIDs();
                                    api.call(new SlackMessage("#posbotspam" , output));
                                }
                            } else {
                                notificationID.add(s);
                            }
                        }
                        
                    }                    
                } catch (IOException ex) {
                    Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    };
    //}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        weblink = new javax.swing.JTextField();
        webbuttan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputPlace = new javax.swing.JTextArea();
        StopButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        weblink.setAutoscrolls(false);
        weblink.setMaximumSize(new java.awt.Dimension(290, 21));
        weblink.setMinimumSize(new java.awt.Dimension(290, 21));

        webbuttan.setText("Do Work");
        webbuttan.setFocusable(false);
        webbuttan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                webbuttanMouseClicked(evt);
            }
        });

        outputPlace.setEditable(false);
        outputPlace.setColumns(20);
        outputPlace.setRows(5);
        jScrollPane1.setViewportView(outputPlace);

        StopButton.setText("Close");
        StopButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                StopButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(weblink, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(webbuttan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(StopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(webbuttan)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(weblink, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StopButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void webbuttanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_webbuttanMouseClicked
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000 * 60 * 15);
    }//GEN-LAST:event_webbuttanMouseClicked

    private void StopButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StopButtonMouseClicked
        dispose();
    }//GEN-LAST:event_StopButtonMouseClicked
    
    private void loadSystemName() {
        BufferedReader br;
        try {
            String currentLine;
            br = new BufferedReader(new FileReader("SolarSystemIDs.txt"));
            while ((currentLine = br.readLine()) != null) {
                String[] nameLevel = currentLine.split("\t");
                String name = nameLevel[1].trim();
                int id = Integer.parseInt(nameLevel[0]);
                systemNames.put(id, name);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveNotificationIDs() {
        try {
            File f = new File("notificationIDs.data");
            FileOutputStream f_out = new FileOutputStream(f);
            ObjectOutputStream obj_out;
            obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(notificationID);
            System.out.println(notificationID);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    private void loadNotificationIDs() {
        FileInputStream f_in1 = null;
        File f1 = new File("notificationIDs.data");
        
        if (f1.exists()) {
            try {
                f_in1 = new FileInputStream(f1);
                ObjectInputStream obj_in1 = new ObjectInputStream(f_in1);
                notificationID = (ArrayList) obj_in1.readObject();                
                System.out.println(notificationID);
                
                obj_in1.close();
                f_in1.close();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WhoShotMyPOSAPIParser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton StopButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea outputPlace;
    private javax.swing.JButton webbuttan;
    private javax.swing.JTextField weblink;
    // End of variables declaration//GEN-END:variables
}
