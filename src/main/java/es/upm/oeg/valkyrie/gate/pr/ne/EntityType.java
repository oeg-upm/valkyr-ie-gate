/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.ne;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class EntityType {
    
    public String EntityTypeName;
    public List<File> GazettersList;
    public List<File> RulesList;
    
    public EntityType(){
    
        GazettersList=new ArrayList();
        RulesList=new ArrayList();
        
    }
    
    public EntityType(String Type){
    
        EntityTypeName= Type;
        GazettersList=new ArrayList();
        RulesList=new ArrayList();
        
    }
    
    
    
    
    
    
    
}
