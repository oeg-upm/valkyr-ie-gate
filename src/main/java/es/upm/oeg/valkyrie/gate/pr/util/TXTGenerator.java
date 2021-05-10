/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.util;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "TXT Generator", comment = "generator")
public class TXTGenerator extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    
    static final Logger logger = Logger.getLogger(TXTGenerator.class.getName()); 
   
    private URL outputFolder;

    public URL getOutputFolder() {
        return outputFolder;
    }

    @Optional
    @CreoleParameter
    public void setOutputFolder(URL outputFolder) {
        this.outputFolder = outputFolder;
    }

    
    
    
    

    
    public void execute() throws ExecutionException {

        try {
            logger.info("Creating file"+ this.document.getName() );
            
            
            DocumentContentImpl content =(DocumentContentImpl) this.document.getContent();
            String Text = content.toString();
            
            //System.out.println(Text);
            
            
            
            File file = null;
            

            File folder= new File(this.outputFolder.toURI());
            file = new File(folder.getAbsolutePath() + File.separator + document.getName()+".txt");

            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF8"));

            out.append(Text);

            out.flush();
            out.close();
            
        } catch (Exception ex) {
            logger.error(ex);
        } 

    }

    public Resource init() throws ResourceInstantiationException {

       
        
        return this;
    }

    public void reInit() throws ResourceInstantiationException {

    }

    
    
   


}
