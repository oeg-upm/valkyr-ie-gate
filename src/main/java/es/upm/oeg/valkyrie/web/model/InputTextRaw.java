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
public class InputTextRaw {
    
    
    private String text;
    

     
    public InputTextRaw() {
       
    }

    public InputTextRaw(String t, String l, String e) {
       
        this.text= t;
        
        
    }
    public String getText() {
        return text;
    }

    public void setText(String Text) {
        this.text = Text;
    }

    
}
