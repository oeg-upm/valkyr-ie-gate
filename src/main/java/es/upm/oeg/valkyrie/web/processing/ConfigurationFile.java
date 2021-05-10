/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web.processing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Pablo
 */
public class ConfigurationFile {
    
    
    
    public List <String> Instructions;
    
    public ConfigurationFile(String Path) throws IOException{
        Instructions= new ArrayList();
    	BufferedReader reader;

			reader = new BufferedReader(new FileReader(Path));
			String line = reader.readLine();
			while (line != null) {
                                if(line.length()>2){
                                Instructions.add(line);
                                }
				// read next line
				line = reader.readLine();
			}
			reader.close();
		
	}
    
    
    
    
    
}
