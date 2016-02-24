/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WhoShotMyPos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class solarSystemData {
    private final HashMap<Integer, String> solarSystems = new HashMap();
    
    public static void main(String args[]){
        solarSystemData ssd = new solarSystemData();
    }
    
    public solarSystemData(){
        loadSolarSystemData();
    }
    
    private void loadSolarSystemData(){
        InputStream in = this.getClass().getResourceAsStream("/data/SolarSystemIDs.txt");
        try (Scanner scan = new Scanner(in)) {        
            while((scan.hasNextLine())){
                String[] solarSystemInformation = scan.nextLine().split("\t");
                String systemName = solarSystemInformation[1];
                int systemID = Integer.parseInt(solarSystemInformation[0]);
                solarSystems.put(systemID, systemName);
                System.out.println(systemName + " " + systemID + "\n");
            }
            
        }       
    }
    
    public String getSystemName(int solarSystemID){
        return solarSystems.get(solarSystemID);
    }
    
}
