/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WhoShotMyPos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class NotificationInformation {

    private HashMap<String, List<String>> notificationInformationHashMap = new HashMap();

    public NotificationInformation() {
        loadNotificationIDs();
    }

    public void addNotificationID(String notificationID, List notificationInformation) {
        notificationInformationHashMap.put(notificationID, notificationInformation);
    }

    public void removeNotificationID(String notificationID) {
        notificationInformationHashMap.remove(notificationID);
    }

    public int getNotificationArraySize() {
        return notificationInformationHashMap.size();
    }

    public boolean containsNotificationID(String IDToCheck) {
        return notificationInformationHashMap.containsKey(IDToCheck);
    }
    
    public void editNotificationInformation(String notificationId, String notificationInformation, int listPositionNumber){
        List<String> notificationInformationList = getNotificationIDInformation(notificationId);
        notificationInformationList.add(listPositionNumber, notificationInformation);
        notificationInformationHashMap.put(notificationId, notificationInformationList);
    }

    public List getNotificationIDInformation(String notificationID) {
        return notificationInformationHashMap.get(notificationID);
    }
    
    public String getNotificationIDInformationAsString(String notificiationID){
        List<String> notificationInformation = getNotificationIDInformation(notificiationID);
        StringBuilder builder = new StringBuilder();
        for(String notificationInfo:notificationInformation){            
            builder.append(notificationInfo);      
            builder.append("\n");
        }
        builder.append("--------");
        builder.append("\n");
        return builder.toString();
    }

    public List<String> getNotificationIDsAsArrayList() {
        List<String> notificationIdArray = new ArrayList();
        if (!notificationInformationHashMap.isEmpty()) {
            for (String notificationID : notificationInformationHashMap.keySet()) {
                notificationIdArray.add(notificationID);                
            }
        }
        return notificationIdArray;
    }
    
    public String getMessageText(){
        List<String> notificationIDs = getNotificationIDsAsArrayList();        
        StringBuilder builder = new StringBuilder();
        for(String notificationID : notificationIDs){
            String notificationInfo = getNotificationIDInformationAsString(notificationID);            
            builder.append(notificationInfo);
        }
        return builder.toString();
    }

//    private void loadDefaults() {
//        if (notificationInformationHashMap.size() > 0) {
//            apiField.setText(notificationInformationHashMap.get(0).toString());
//        }
//        if (notificationID.size() > 0) {
//            slackTokenField.setText(notificationInformationHashMap.get(1).toString());
//        }
//    }
    
    public void saveNotificationIDs() {
        try {
            File f = new File("notificationIDs.data");
            FileOutputStream f_out = new FileOutputStream(f);
            ObjectOutputStream obj_out;
            obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(notificationInformationHashMap);
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
                notificationInformationHashMap = (HashMap) obj_in1.readObject();
                System.out.println(notificationInformationHashMap);

                obj_in1.close();
                f_in1.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FileOutputStream f_out = null;
            try {
                File f = new File("notificationIDs.data");
                f_out = new FileOutputStream(f);
                ObjectOutputStream obj_out;
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(notificationInformationHashMap);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    f_out.close();
                } catch (IOException ex) {
                    Logger.getLogger(WhoShotMyPOSGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
