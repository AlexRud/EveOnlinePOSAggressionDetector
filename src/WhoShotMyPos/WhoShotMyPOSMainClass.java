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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class WhoShotMyPOSMainClass {

    private final NotificationInformation notificationInformation = new NotificationInformation();
    private final solarSystemData systemNames = new solarSystemData();
    private ArrayList<String> WebpageInformationStorage = new ArrayList();
    private String URL;
    
    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198 notifications request.
    //keyID, vCode, characterID.
    //https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyID=4474332&vCode=5MuDcndf6vTeYbBqhAZc7PdWGEH8XI6HfoenntMRoe50LY8mxEdWAj2uSt4mqzUR&characterID=95477198&IDs=530705490 notifications text request.
    //keyID, vCode, characterID, notification id(s) from previous request.
    //https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=94818624 character info request.
    //Character name, corp name, alliance name, shield hp, time, system?
    //Luke API
    //https://api.eveonline.com/char/Notifications.xml.aspx?keyID=455684&vCode=oAPDhYH9pc063j5GWszkvwvpPwC3fPD6FX515Q1JAl79RXoBhy9GInhMNth2Dutu&characterID=151627406
    //https://hooks.slack.com/services/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
    
//    WhoShotMyPOSMainClass(String URLToSearch){
//        this.URL = URLToSearch;
//    }
    
    public void findNotifications(String URL) {
        collectNotificationIDs(URL);
    }

    private void getNotificationTexts(String URLToSearch) {
        List<String> notificationIDs = notificationInformation.getNotificationIDsAsArrayList();
        for (String notificationID : notificationIDs) {
            readURL(NotificationIDToTextURLChange(URLToSearch, notificationID));
            getNotificationTextInformation(notificationID);
        }
    }

    private String NotificationIDToTextURLChange(String URLToChange, String notificationID) {
        String changedURL = URLToChange.replace("Notification", "NotificationTexts");
        changedURL += notificationID;
        return changedURL;
    }

    private void readURL(String URLToUse) {
        URL searchForNotifications = createURL(URLToUse);
        BufferedReader searchBuffer = createBufferedReader(searchForNotifications);
        readInputStreamToArray(searchBuffer);
    }

    private URL createURL(String url) {
        URL createdUrl = null;
        try {
            createdUrl = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WhoShotMyPOSMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createdUrl;
    }

    private BufferedReader createBufferedReader(URL urlToRetreive) {
        BufferedReader buffReadIncoming = null;
        try {
            buffReadIncoming = new BufferedReader(new InputStreamReader(urlToRetreive.openStream()));
        } catch (IOException ex) {
            Logger.getLogger(WhoShotMyPOSMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffReadIncoming;
    }

    private void readInputStreamToArray(BufferedReader BufferToRead) {
        try {
            String incomingLine;
            while ((incomingLine = BufferToRead.readLine()) != null) {
                WebpageInformationStorage.add(incomingLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(WhoShotMyPOSMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void collectNotificationIDs(String URLToSearch) {
        readURL(URLToSearch);
        for (String webpageTemp : WebpageInformationStorage) {
            if (webpageTemp.contains("typeID=\"75\"")) {
                String notificationID = webpageTemp.substring(webpageTemp.indexOf("=") + 2, webpageTemp.indexOf("typeID") - 2);
                if (!notificationInformation.containsNotificationID(notificationID)) {
                    getTimeDate(notificationID, webpageTemp);
                }
            }
        }
        getNotificationTexts(URLToSearch);
    }

    private void getNotificationTextInformation(String notificationID) {
        for (String webpageTemp : WebpageInformationStorage) {
            if (webpageTemp.contains("aggressorID")) {
                getCharacterInformation((extractAggressorID(webpageTemp)), notificationID);
            }
            if (webpageTemp.contains("solarSystemID")) {
                getSolarSystemName(webpageTemp, notificationID);
            }
            if (webpageTemp.contains("shieldValue")) {
                getShieldValue(webpageTemp, notificationID);
            }
        }
    }

    private void getCharacterInformation(String aggressorID, String notificationID) {
        readURL(createAggressorIDURLContents(aggressorID));
        for (String webpageTemp : WebpageInformationStorage) {
            if (webpageTemp.contains("characterName")) {
                getCharacterName((extractAggressorID(webpageTemp)), notificationID);
            }
            if (webpageTemp.contains("<corporation>")) {
                getCharacterCorporation(webpageTemp, notificationID);
            }
            if (webpageTemp.contains("<alliance>")) {
                getCharacterAlliance(webpageTemp, notificationID);
            }
        }
    }

    private void getTimeDate(String notificationID, String timeDateContainingLine) {
        String timeDate = timeDateContainingLine.substring(timeDateContainingLine.indexOf("sentDate=") + 10, timeDateContainingLine.indexOf("sentDate=") + 29);
        notificationInformation.editNotificationInformation(notificationID, "Time: " + timeDate, 0);
    }

    private String extractAggressorID(String aggressorIDContainedLine) {
        return aggressorIDContainedLine.substring(aggressorIDContainedLine.lastIndexOf(":") + 2);
    }

    private String createAggressorIDURLContents(String aggressorID) {
        return "https://api.eveonline.com/eve/CharacterInfo.xml.aspx?characterid=" + aggressorID;
    }

    private void getSolarSystemName(String solarSystemIDContainingString, String notificationID) {
        String solarSystem = solarSystemIDContainingString.substring(solarSystemIDContainingString.lastIndexOf(":") + 2);
        int id = Integer.parseInt(solarSystem);
        String solarSystemName = systemNames.getSystemName(id);
        notificationInformation.editNotificationInformation(notificationID, "Solar System: " + solarSystemName, 5);
    }

    private void getShieldValue(String shieldValueContainingString, String notificationID) {
        String shieldValue = shieldValueContainingString.substring(shieldValueContainingString.lastIndexOf(":") + 4, shieldValueContainingString.lastIndexOf(":") + 6) + "%";
        notificationInformation.editNotificationInformation(notificationID, "Shield Value: " + shieldValue, 4);
    }

    private void getCharacterName(String characterNameContainedLine, String notificationID) {
        String characterName = characterNameContainedLine.substring(characterNameContainedLine.indexOf(">") + 1, characterNameContainedLine.indexOf("</"));
        notificationInformation.editNotificationInformation(notificationID, "Character Name: " + characterName, 1);
    }

    private void getCharacterCorporation(String characterCorporationContainedLine, String notificationID) {
        String characterCorporation = characterCorporationContainedLine.substring(characterCorporationContainedLine.indexOf(">") + 1, characterCorporationContainedLine.indexOf("</"));
        notificationInformation.editNotificationInformation(notificationID, "Character Corporation: " + characterCorporation, 2);
    }

    private void getCharacterAlliance(String characterAllianceContainedLine, String notificationID) {
        String characterAlliance = characterAllianceContainedLine.substring(characterAllianceContainedLine.indexOf(">") + 1, characterAllianceContainedLine.indexOf("</"));
        notificationInformation.editNotificationInformation(notificationID, "Character Corporation: " + characterAlliance, 3);
    }

    public String getMessage() {
        return notificationInformation.getMessageText();        
    }    
}
