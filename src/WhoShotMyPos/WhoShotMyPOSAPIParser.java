/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WhoShotMyPos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class WhoShotMyPOSAPIParser extends javax.swing.JFrame {

    Timer timer = new Timer();

    /**
     * Creates new form WhuDuShutPosGUI
     */
    public WhoShotMyPOSAPIParser() {
        initComponents();
        StopButton.setVisible(false);
    }

    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198 notifications request.
    //keyID, vCode, characterID.
    //https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198&IDs=530705490 notifications text request.
    //keyID, vCode, characterID, notification id(s) from previous request.
    //https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=94818624 character info request.
    //Character name, corp name, alliance name, shield hp, time, system?
    //public void getNotifications() {
    TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    URL url = new URL(weblink.getText());
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            if (inputLine.contains("typeID=\"75\"")) {
                                String s = inputLine.substring(inputLine.indexOf("=") + 2, inputLine.indexOf("typeID") - 2);
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
                                                        String s2 = inputLine3.substring(inputLine3.indexOf(">") + 1, inputLine3.indexOf("</"));
                                                        System.out.println(s2);
                                                        outputPlace.append(s2);
                                                    }
                                                    if (inputLine3 != null && inputLine3.contains("<corporation>")) {
                                                        String s3 = inputLine3.substring(inputLine3.indexOf(">") + 1, inputLine3.indexOf("</"));
                                                        System.out.println(s3);
                                                        outputPlace.append("\n" + s3);
                                                    }
                                                    if (inputLine3 != null && inputLine3.contains("<alliance>")) {
                                                        String s4 = inputLine3.substring(inputLine3.indexOf(">") + 1, inputLine3.indexOf("</"));
                                                        System.out.println(s4);
                                                        outputPlace.append("\n" + s4);
                                                    }                                                    
                                                }
                                                outputPlace.append("\n-----------------------\n");
                                            }
                                        }
                                    }
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

        StopButton.setText("Stop");
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
                    .addComponent(weblink)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weblink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webbuttan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(StopButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void webbuttanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_webbuttanMouseClicked
        timer.scheduleAtFixedRate(task, 0, 1000*60*30);
        webbuttan.setVisible(false);
        StopButton.setVisible(true);
    }//GEN-LAST:event_webbuttanMouseClicked

    private void StopButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StopButtonMouseClicked
        task.cancel();
        webbuttan.setVisible(true);
        StopButton.setVisible(false);
    }//GEN-LAST:event_StopButtonMouseClicked

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