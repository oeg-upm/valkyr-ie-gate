/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.role;

import es.upm.oeg.valkyrie.gate.pr.ne.EntityType;
import gate.Document;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Pablo
 */
public class RCM {
    
    public EntityType Entitytye;
    
    public List <Role> RoleList;
    
    public boolean isML=false;
    public boolean isLinguistic=false;
    
    
    public RCM(String EntityType){
    
         Entitytye=new EntityType(EntityType);
         RoleList= new ArrayList();
        
    }
    
    
            
    public void initRolesWithRules(String SetName){
    
    
        for(Role r:this.RoleList){
            r.initRules(SetName);
        }
        
    }
    
    public void executeRolesWithRules(Document dcmnt) throws ExecutionException{
    
    
        for(Role r:this.RoleList){
            r.excuteRules(dcmnt);
        }
        
    }
    
    public void executeRoleML(Document dcmnt) throws ExecutionException{
    
    
        for(Role r:this.RoleList){
            r.executeML(dcmnt);
        }
        
    }
    
    
    public void addRoleWithRule(String Name, String ... fileJapes){
    
        Role role=new Role(Name);
    
        for(String f: fileJapes) {
            role.RulesList.add(new File(f));
        
        }
        this.RoleList.add(role);
    }
            
    
    public void addRoleML(String Name, AbstractLanguageAnalyser Module){
    
        Role role=new Role(Name);
        role.initMLModule(Module);
        this.RoleList.add(role);
        
        
    }
    
    
    
}
