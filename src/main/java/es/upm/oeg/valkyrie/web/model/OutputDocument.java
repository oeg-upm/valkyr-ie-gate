/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class OutputDocument {
  
    private List <Entity> Annotations= new ArrayList();
    
 
    public List<Entity> getAnnotations() {
        return Annotations;
    }

    public void setAnnotations(List<Entity> Annotations) {
        this.Annotations = Annotations;
    }
    
    
}
