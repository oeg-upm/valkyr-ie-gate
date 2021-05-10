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
public class InputText {
    
    
    private String text;
    private String lang;
    private String entity;

     
    public InputText() {
       
    }

    public InputText(String t, String l, String e) {
       
        this.text= t;
        this.lang= l;
        this.entity= e;
        
    }
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String Entity) {
        this.entity = Entity;
    }
    
    
}
