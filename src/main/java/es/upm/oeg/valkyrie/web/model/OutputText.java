/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web.model;

import java.util.List;

/**
 *
 * @author Pablo
 */
public class OutputText {
    
    private String text;
    private String lang;
    //private String Entity;

    private List <String> entities;
    
    public String getText() {
        return text;
    }

    public void setText(String Text) {
        this.text = Text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String Lang) {
        this.lang = Lang;
    }

  
    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> Entities) {
        this.entities = Entities;
    }
    
    
    
}
