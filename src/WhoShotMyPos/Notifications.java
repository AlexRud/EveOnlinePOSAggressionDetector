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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class Notifications {

    private HashMap<String, Boolean> notificationIdHashMap = new HashMap();
    
    public Notifications(){
        loadNotificationIDs();
    }
    
    public void addNotificationID(String notificationID){
        notificationIdHashMap.put(notificationID, false);
    }
    
    public void removeNotificationID(String notificationID){
        notificationIdHashMap.remove(notificationID);
    }
    
    public int getNotificationArraySize(){
        return notificationIdHashMap.size();
    }
    
    public boolean containsNotificationID(String IDToCheck){
        return notificationIdHashMap.containsKey(IDToCheck);
    }
    
    public boolean getNotificationIDStatus(String notificationID){
        return notificationIdHashMap.get(notificationID);
    }

//    private void loadDefaults() {
//        if (notificationIdHashMap.size() > 0) {
//            apiField.setText(notificationIdHashMap.get(0).toString());
//        }
//        if (notificationID.size() > 0) {
//            slackTokenField.setText(notificationIdHashMap.get(1).toString());
//        }
//    }

    public void saveNotificationIDs() {
        try {
            File f = new File("notificationIDs.data");
            FileOutputStream f_out = new FileOutputStream(f);
            ObjectOutputStream obj_out;
            obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(notificationIdHashMap);
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
                notificationIdHashMap = (HashMap) obj_in1.readObject();
                System.out.println(notificationIdHashMap);

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
                obj_out.writeObject(notificationIdHashMap);
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
