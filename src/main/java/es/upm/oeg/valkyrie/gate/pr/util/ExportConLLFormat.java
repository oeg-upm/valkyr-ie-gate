/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.util;

import gate.Annotation;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "ExportConLLFormat (alpha)", comment = "Export document")
public class ExportConLLFormat extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(ExportConLLFormat.class);
    
    
    private URL OutputFolder;
    
    private String EntityFeature;
    
    private boolean Gold;
   
    
    @Override
    public void execute() throws ExecutionException {

        try {
            

            String docName = this.document.getName();
           
            logger.info("Exporting: "+docName);
            
            
            AnnotationSet annotSetGen = document.getAnnotations("");
            

            // Sentence
            AnnotationSet annotSentenceSet = annotSetGen.get("Sentence");
            List<Annotation> annotSentenceList = annotSentenceSet.inDocumentOrder();
            
            StringBuffer bf = new StringBuffer();
            
            for(Annotation annotSentence: annotSentenceList){
            
                
            // Tokens
            AnnotationSet annotEntitySet = annotSetGen.get("Token",annotSentence.getStartNode().getOffset(),annotSentence.getEndNode().getOffset());
            List<Annotation> annotEntityList = annotEntitySet.inDocumentOrder();

            
            String line="", Text;
            String tab = "\t";
            for (Annotation AnnotToken : annotEntityList) {

                FeatureMap fm= AnnotToken.getFeatures();
                
                // Get text from an annotation
                Text = (String) fm.get("string");
                
                if(this.Gold){
                    
                    try{
                        String entitytag= fm.get(this.EntityFeature).toString();
                        line =  Text + tab + entitytag;
                    }catch(Exception e){}
                    
                
                } else {
                 line =  Text;
                }
                
                
                bf.append(line + "\n");
            }
            bf.append("\n");
            }

         
            

           

            // delete if exists
            File OutputFile= new File(OutputFolder.getFile() + File.separator + docName);
            if(OutputFile.exists()){
                OutputFile.delete();
            }
            
            PrintWriter writer
                    = new PrintWriter(OutputFile, "utf-8");
            writer.append(bf.toString().trim());
            writer.close();

        } catch (FileNotFoundException ex) {
            logger.error(ex.toString());
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex.toString());
        }

    }

    
    
	
    public Resource init() throws ResourceInstantiationException {
	
        if (OutputFolder == null) {
            throw new ResourceInstantiationException("There is no folder specified");
        }
        File folder= new File(this.OutputFolder.getFile());
        
        if( (!folder.exists()) || (!folder.isDirectory())  ){
            throw new ResourceInstantiationException("The folder is not found");
        }
        
        
        return this;
	}

	
    public void reInit() throws ResourceInstantiationException {
	}

        
        
    public URL getOutputFolder() {
        return OutputFolder;
    }

    @CreoleParameter 
    public void setOutputFolder(URL OutputFolder) {
        this.OutputFolder = OutputFolder;
    }
    
    
    

    public String getEntityFeature() {
        return EntityFeature;
    }

    //@Optional
    @RunTime
    @CreoleParameter(defaultValue = "Evaluation") 
    public void setEntityFeature(String EntityFeature) {
        this.EntityFeature = EntityFeature;
    }

    public boolean isGold() {
        return Gold;
    }

    @RunTime
    @CreoleParameter
    public void setGold(boolean Gold) {
        this.Gold = Gold;
    }
        
      
    
}
