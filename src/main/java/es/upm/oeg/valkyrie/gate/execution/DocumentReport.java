/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.execution;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class DocumentReport {
    
    public String Name;
    public List <String> Persons;
    public List <String> Organizations;
    public List <String> Locations;
    
    
    
    public DocumentReport(String Name){
        this.Name= Name;
        Persons=new ArrayList();
         Organizations=new ArrayList();
          Locations=new ArrayList();
    }
    
    
    //C:\Users\Pablo\Documents\GitHub\valkyr-ie\resources\GATE\Plugins\ANNIE
    //C:/Users/Pablo/Documents/GitHub/valkyr-ie/resource/GateHome/Plugins/ANNIE/
    //C:/Users/Pablo/Documents/GitHub/valkyr-ie/resource/GateHome/Plugins/ANNIE/
    //C:/Users/Pablo/Documents/GitHub/valkyr-ie/resource/GateHome/Plugins/ANNIE/
}
