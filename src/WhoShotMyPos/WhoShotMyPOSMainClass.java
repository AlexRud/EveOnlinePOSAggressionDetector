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
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.gpedro.integrations.slack.SlackMessage;

/**
 *
 * @author Alex
 */
public class WhoShotMyPOSMainClass {
    
    private final Notifications notificationIDCollection = new Notifications();
    private final solarSystemData systemNames = new solarSystemData();
    private ArrayList<String> WebpageInformationStorage = new ArrayList();
    
    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198 notifications request.
    //keyID, vCode, characterID.
    //https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198&IDs=530705490 notifications text request.
    //keyID, vCode, characterID, notification id(s) from previous request.
    //https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=94818624 character info request.
    //Character name, corp name, alliance name, shield hp, time, system?
    //Luke API
    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=455684&vCode=oAPDhYH9pc063j5GWszkvwvpPwC3fPD6FX515Q1JAl79RXoBhy9GInhMNth2Dutu&characterID=151627406
    //https://hooks.slack.com/services/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
    
    private URL createURL (String url){
        URL createdUrl = null;
        try {
             createdUrl= new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WhoShotMyPOSMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createdUrl;
    }
    
    private BufferedReader createBufferedReader(URL urlToRetreive){
        BufferedReader buffReadIncoming = null;
        try {
            buffReadIncoming = new BufferedReader(new InputStreamReader(urlToRetreive.openStream()));
        } catch (IOException ex) {
            Logger.getLogger(WhoShotMyPOSMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffReadIncoming;
    }
    
    private void readInputStreamToArray(BufferedReader BufferToRead){
        try {
            String incomingLine;
            while((incomingLine = BufferToRead.readLine()) != null){
                WebpageInformationStorage.add(incomingLine);
            }   } catch (IOException ex) {
            Logger.getLogger(WhoShotMyPOSMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean checkIfNotificationIDExists(String notificationIDToCheck){
        return notificationIDCollection.containsNotificationID(notificationIDToCheck);
    }
    
    private void collectNotificationIDs(){
        for(String webpageTemp: WebpageInformationStorage){
            if(webpageTemp.contains("typeID=\"75\"")){
                String notificationID = webpageTemp.substring(webpageTemp.indexOf("=") + 2, webpageTemp.indexOf("typeID") - 2);
                if(!checkIfNotificationIDExists(notificationID)){notificationIDCollection.addNotificationID(notificationID);}
            }
        }
    } 
    
    private String extractAggressorID(String aggressorIDContainedLine){
        return aggressorIDContainedLine.substring(aggressorIDContainedLine.lastIndexOf(":") + 2);        
    }
    
    private String createAggressorIDURLContents(String aggressorID){
        return "https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid="+aggressorID;
    }
    
    private String getSolarSystemName(String solarSystemIDContainingString){
        String solarSystem = solarSystemIDContainingString.substring(solarSystemIDContainingString.lastIndexOf(":") + 2);
        int id = Integer.parseInt(solarSystem);
        return systemNames.getSystemName(id);
    }
    
    private String getShieldValue(String shieldValueContainingString){
        return shieldValueContainingString.substring(shieldValueContainingString.lastIndexOf(":") + 4, shieldValueContainingString.lastIndexOf(":") + 6) + "%";
    }
    
}
