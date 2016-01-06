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
import java.time.Instant;
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
        outputPlace.append("Systems Loaded\n");
        loadNotificationIDs();
        outputPlace.append("Notifications Loaded\n");   
        outputPlace.append("-----------------------\n");
        loadDefaults();
    }

    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198 notifications request.
    //keyID, vCode, characterID.
    //https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198&IDs=530705490 notifications text request.
    //keyID, vCode, characterID, notification id(s) from previous request.
    //https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=94818624 character info request.
    //Character name, corp name, alliance name, shield hp, time, system?
    //Luke API
    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=455684&vCode=oAPDhYH9pc063j5GWszkvwvpPwC3fPD6FX515Q1JAl79RXoBhy9GInhMNth2Dutu&characterID=151627406
    //https://hooks.slack.com/services/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
    
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            String timeDate = null;
            String character = null;
            String corp = null;
            String alliance = null;
            String shieldHP = null;
            String system = null;
            Instant instant = Instant.now();
            outputPlace.append("Running.... " + instant +"\n");
            try {                
                URL url = new URL(apiField.getText());
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("typeID=\"75\"")) {                            
                            String s = inputLine.substring(inputLine.indexOf("=") + 2, inputLine.indexOf("typeID") - 2);
                            if (!notificationID.contains(s)) {                                
                                notificationID.add(s);
                                timeDate = inputLine.substring(inputLine.indexOf("sentDate=") + 10, inputLine.indexOf("sentDate=") + 29);
                                String edit = apiField.getText().replace("Notifications", "NotificationTexts");
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

        apiField = new javax.swing.JTextField();
        goButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputPlace = new javax.swing.JTextArea();
        StopButton = new javax.swing.JButton();
        slackTokenField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        saveDefaults = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        apiField.setAutoscrolls(false);
        apiField.setMaximumSize(new java.awt.Dimension(290, 21));
        apiField.setMinimumSize(new java.awt.Dimension(290, 21));

        goButton.setText("Go!");
        goButton.setFocusable(false);
        goButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                goButtonMouseClicked(evt);
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

        slackTokenField.setMaximumSize(new java.awt.Dimension(250, 21));
        slackTokenField.setMinimumSize(new java.awt.Dimension(250, 21));

        jLabel1.setText("API:");

        jLabel2.setText("Slack Token:");

        saveDefaults.setText("Save Links as Defaults");
        saveDefaults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveDefaultsMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(apiField, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(slackTokenField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveDefaults)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(slackTokenField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(apiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveDefaults)
                    .addComponent(StopButton)
                    .addComponent(goButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void goButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_goButtonMouseClicked
        saveDefaults();
        api = new SlackApi(slackTokenField.getText());   
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000 * 60);
    }//GEN-LAST:event_goButtonMouseClicked

    private void StopButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StopButtonMouseClicked
        if(timer != null) {
            timer.cancel();
        }
        dispose();
    }//GEN-LAST:event_StopButtonMouseClicked

    private void saveDefaultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveDefaultsMouseClicked
        saveDefaults();
    }//GEN-LAST:event_saveDefaultsMouseClicked
         
    private void saveDefaults(){
        if(!apiField.getText().equals("") && !slackTokenField.getText().equals("")){
            notificationID.add(0, apiField.getText());
            notificationID.add(1, slackTokenField.getText());
            saveNotificationIDs();
            outputPlace.append("Defaults Saved\n");
        }
        else{
            outputPlace.append("Please fill in both boxes\n");
        }
    }
    
    private void loadDefaults(){
        if(notificationID.size() > 0){apiField.setText(notificationID.get(0).toString());}
        if(notificationID.size() > 0){slackTokenField.setText(notificationID.get(1).toString());}
    }
    
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
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            FileOutputStream f_out = null;
            try {
                File f = new File("notificationIDs.data");
                f_out = new FileOutputStream(f);
                ObjectOutputStream obj_out;
                obj_out = new ObjectOutputStream(f_out);   
                obj_out.writeObject(notificationID);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    f_out.close();
                } catch (IOException ex) {
                    Logger.getLogger(WhoShotMyPOSAPIParser.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    private javax.swing.JTextField apiField;
    private javax.swing.JButton goButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea outputPlace;
    private javax.swing.JButton saveDefaults;
    private javax.swing.JTextField slackTokenField;
    // End of variables declaration//GEN-END:variables
}
