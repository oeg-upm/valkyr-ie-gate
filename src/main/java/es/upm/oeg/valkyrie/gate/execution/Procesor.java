/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.execution;

import java.net.URL;

/**
 *
 * @author Pablo
 */
public class Procesor {
    
    
    
    
    public static void main (String [] args) throws Exception{
    
    
        Valkyr_IE val = new Valkyr_IE ("resources"); 
        
        val.recognitionOfNamedEntities(new URL("https://www.nytimes.com/2020/02/17/business/china-coronavirus-economy.html"));
    
    }
    
    
}
