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
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "Oriented Gazetteer (Beta)", comment = "Gazetteer specific for a document")
public class OrientedGazetteer extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    
    static final Logger logger = Logger.getLogger(OrientedGazetteer.class.getName()); 
   
    private URL GazetteerFolderURL;

    
    public URL getGazetteerFolderURL() {
        return GazetteerFolderURL;
    }

    @CreoleParameter
    public void setGazetteerFolderURL(URL GazetteerFolderURL) {
        this.GazetteerFolderURL = GazetteerFolderURL;
    }

    
    public void execute() throws ExecutionException {

        try {
            logger.info("Specific gazetter for : "+ this.document.getName() );
            
            // Get the document name
            String DocumentName = this.document.getName().split("\\.")[0];
            
            
            // Get the annotation Set Group general
            String inputASName = "";
            AnnotationSet annotSetGen = document.getAnnotations(inputASName);
            
            
            File OwnGazetteer = new File(this.GazetteerFolderURL.getFile() + File.separator + DocumentName+".def");
            
            if (!OwnGazetteer.exists()){
                logger.info("there is no gazetteer for the document "+DocumentName);
                
                return; // END
            }
            logger.info("there is gazetteer for the document "+DocumentName);
            System.out.println("there is gazetteer for the document "+DocumentName);
            FeatureMap featureMap= Factory.newFeatureMap();
            featureMap.put("encoding", "UTF-8");
            featureMap.put("caseSensitive", false);
            featureMap.put("gazetteerFeatureSeparator", "$");
            featureMap.put("annotationSetName",null);
            featureMap.put("longestMatchOnly",Boolean.TRUE);
            featureMap.put("listsURL",OwnGazetteer.toURI().toURL());    
            DefaultGazetteer GazetteerModule = (DefaultGazetteer) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer",featureMap);
            GazetteerModule.init();
            
            // Put and execute
            GazetteerModule.setDocument(document);
            GazetteerModule.execute();
            
            //  Remove
            Factory.deleteResource(GazetteerModule);
            Gate.getCreoleRegister().remove(GazetteerModule);
            
            
           
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error(ex);
        } 

    }

    public Resource init() throws ResourceInstantiationException {

        if (GazetteerFolderURL == null) {
            throw new ResourceInstantiationException("GoldStandardAnnotationName has not been set");
        }
        File folder= new File(this.GazetteerFolderURL.getFile());
        if( (!folder.exists()) || (!folder.isDirectory())  ){
            throw new ResourceInstantiationException("The folder is not found");
        }
        
        
        return this;
    }

    public void reInit() throws ResourceInstantiationException {

        if (GazetteerFolderURL == null) {
            throw new ResourceInstantiationException("GoldStandardAnnotationName has not been set");
        }
        File folder= new File(this.GazetteerFolderURL.getFile());
        if( (!folder.exists()) || (!folder.isDirectory())  ){
            throw new ResourceInstantiationException("The folder is not found");
        }
    }

    
    
   


}
