/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.role;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.Transducer;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo
 */
public class Role {
    
    public String RoleName;
    public List<File> RulesList;
    
    public List<Transducer> JAPERulesList;
    
    public AbstractLanguageAnalyser MLModel;
    
    public Role(String Name){
        RoleName=Name;
        RulesList=new ArrayList();
    }
    
    
    public void initMLModule(AbstractLanguageAnalyser MLModel){
        this.MLModel= MLModel;
    }
    
    public void executeML(Document dcmnt) throws ExecutionException {
        
     
        
           MLModel.setDocument(dcmnt);
           MLModel.execute();
        
        
    }
    
    public void initRules(String SetName){
        JAPERulesList=new ArrayList();
        
        for (File f:this.RulesList){
        
            initRule(f,"UTF-8",SetName);
        
        }
    
    }
    
    public void initRule(File file, String Encoding,String SetName){
    
        try {
            
            FeatureMap fmJape= Factory.newFeatureMap();
            fmJape.put("encoding", "UTF-8");
            fmJape.put("outputASName", SetName);
            fmJape.put("inputASName", SetName);
            fmJape.put("grammarURL",file.toURI().toURL() );
       
            Transducer JapeRule;
            JapeRule = (Transducer)  Factory.createResource("gate.creole.Transducer", fmJape);
            JapeRule.setName("Rule");
                //JapeRule.setDocument(document);
                //JapeRule.execute();
            this.JAPERulesList.add(JapeRule);
            
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(Role.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceInstantiationException ex) {
            Logger.getLogger(Role.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }

    public void excuteRules(Document dcmnt) throws ExecutionException {
        
       for (Transducer f:this.JAPERulesList){
        
           f.setDocument(dcmnt);
           f.execute();
        
        }
    }
    
    
    
    
}
