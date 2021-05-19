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
public class Entity {
    
    private String Type;
    private String Text;
    private int Init;
    private int End;
    
    private List<Feature> Features;
    
    
    
    public Entity(String Type, String Text, int Init,int End){
    
        this.Type=Type;
        this.Text=Text;
        this.Init=Init;
        this.End=End;
                
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public int getInit() {
        return Init;
    }

    public void setInit(int Init) {
        this.Init = Init;
    }

    public int getEnd() {
        return End;
    }

    public void setEnd(int End) {
        this.End = End;
    }

    public List<Feature> getFeatures() {
        return Features;
    }

    public void setFeatures(List<Feature> Features) {
        this.Features = Features;
    }
    
    
    
}
