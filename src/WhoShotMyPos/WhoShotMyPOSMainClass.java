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
import java.time.Instant;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.gpedro.integrations.slack.SlackMessage;

/**
 *
 * @author Alex
 */
public class WhoShotMyPOSMainClass {
    
    private final Notifications notificationID = new Notifications();
    private final solarSystemData systemNames = new solarSystemData();
    
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
            outputPlace.append("Running.... " + instant + "\n");
            try {
                URL url = new URL(apiField.getText());
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("typeID=\"75\"")) {
                            String s = inputLine.substring(inputLine.indexOf("=") + 2, inputLine.indexOf("typeID") - 2);
                            if (!notificationID.containsNotificationID(s)) {
                                notificationID.addNotificationID(s);
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
                                            system = systemNames.getSystemName(id);
                                        }
                                        if (inputLine2.contains("shieldValue")) {
                                            shieldHP = inputLine2.substring(inputLine2.lastIndexOf(":") + 4, inputLine2.lastIndexOf(":") + 6) + "%";

                                        }
                                    }
                                    outputPlace.append("Time: " + timeDate + "\nAttacker: " + character + " \nAttacker Corporation: " + corp + "\nAttacker Alliance: " + alliance + "\nPOS Shield HP: " + shieldHP
                                            + "\nSystem: " + system + "\n---------------------");
                                    String output = "Time: " + timeDate + "\nAttacker: " + character + " \nAttacker Corporation: " + corp + "\nAttacker Alliance: " + alliance + "\nPOS Shield HP: " + shieldHP
                                            + "\nSystem: " + system + "\n---------------------\n";
                                    notificationID.saveNotificationIDs();
                                    api.call(new SlackMessage("#posbotspam", output));
                                }
                            } else {
                                notificationID.addNotificationID(s);
                            }
                        }

                    }
                } catch (IOException ex) {
                    Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    };
}
