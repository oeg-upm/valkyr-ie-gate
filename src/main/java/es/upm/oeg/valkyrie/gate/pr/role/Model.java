/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.role;

import gate.Document;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo
 */
public abstract class Model {
    
    public List <AbstractLanguageAnalyser> prs=new ArrayList();
    
    public void add(AbstractLanguageAnalyser newPr){
    
        prs.add(newPr);
    
    }
    
    public void execute(Document doc) throws ExecutionException{
    
        for(AbstractLanguageAnalyser pr:prs){
        
            pr.setDocument(doc);
            pr.execute();
        }
    
    }
    
}
