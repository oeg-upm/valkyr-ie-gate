/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web.model;

/**
 *
 * @author Pablo
 */
public class Feature {
    
    private String Name;
    private String Value;

    public Feature(){}
    
    public Feature(String Name, String Val){
        this.Name=Name;
        this.Value=Val;
    }
    
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }
    
    
    
}
