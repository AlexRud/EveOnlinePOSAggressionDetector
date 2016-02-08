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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class Notifications {

    private ArrayList notificationIdArray = new ArrayList();
    
    public Notifications(){
        loadNotificationIDs();
    }
    
    public void addNotificationID(String notificationID){
        notificationIdArray.add(notificationID);
    }
    
    public void removeNotificationID(String notificationID){
        notificationIdArray.remove(notificationID);
    }
    
    public int getNotificationArraySize(){
        return notificationIdArray.size();
    }
    
    public boolean containsNotificationID(String IDToCheck){
        return notificationIdArray.contains(IDToCheck);
    }

//    private void loadDefaults() {
//        if (notificationIdArray.size() > 0) {
//            apiField.setText(notificationIdArray.get(0).toString());
//        }
//        if (notificationID.size() > 0) {
//            slackTokenField.setText(notificationIdArray.get(1).toString());
//        }
//    }

    public void saveNotificationIDs() {
        try {
            File f = new File("notificationIDs.data");
            FileOutputStream f_out = new FileOutputStream(f);
            ObjectOutputStream obj_out;
            obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(notificationIdArray);
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
                notificationIdArray = (ArrayList) obj_in1.readObject();
                System.out.println(notificationIdArray);

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
                obj_out.writeObject(notificationIdArray);
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
