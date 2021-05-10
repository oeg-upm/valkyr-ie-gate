/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web.processing;

import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import es.upm.oeg.valkyrie.gate.GateHandler;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;
import es.upm.oeg.valkyrie.gate.application.GateApplication;


/**
 *
 * @author pcalleja
 */
public class NERLingGazetteers extends GateApplication{
    
    
    
    private String GazetteerPathSensitive;
    private String GazetteerPathNoSensitive;
    private String JapePath;
    private static Logger logger = Logger.getLogger(NERLingGazetteers.class);
   
    
    public boolean ReleaseMemoryPlan=true;
   
    public String Name="NERLing";
    
    public void initPipeline() {

        try {
            logger.info("Initialising Resources");
            
            
            
            
            //  reset
            List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(this.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createDocumentReset(Name+"-Deleter", new ArrayList(),  SetsToRemove);
            
            // Token
            FeatureMap fmToken= Factory.newFeatureMap();
            fmToken.put("annotationSetName", this.getAppAnnotationSetName());
            ProcessingResource Tokenizer=  GateFactoryInterface.createANNIETokeniser(Name+"-Token","UTF-8",fmToken);
            
            // Gazetter
            ProcessingResource GazetteerSensitive=  GateFactoryInterface.createANNIEGazetteer(Name+"-Gaze-Sensitive", "UTF-8",
                    true,  this.getAppAnnotationSetName(), ":",
                    true,  this.GazetteerPathSensitive);
            
            // Gazetter
            ProcessingResource GazetteerNoSensitive=  GateFactoryInterface.createANNIEGazetteer(Name+"-Gaze-NoSensitive", "UTF-8",
                    false,  this.getAppAnnotationSetName(), ":",
                    true,  this.GazetteerPathNoSensitive);
           
            
         
         
            // APP
            ProcessingResource Releaser;
            if(this.ReleaseMemoryPlan){
            
             Releaser= GateFactoryInterface.createAnnotationDeleter(Name+"-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            super.setPipeline(new GatePipeline(Name,reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive,Releaser));
            }else{
           
                super.setPipeline(new GatePipeline(Name,reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive));
            }
            
            logger.info(Name+" Pipeline created");
            
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error(Name+" APPLICATION FAILED ON INITIALIZATION",ex);
           ex.printStackTrace();
        }
        
    }
    
 
   

    public void setGazetteerPathSensitive(String GazetteerPathSensitive) {
        this.GazetteerPathSensitive = GazetteerPathSensitive;
    }

    public void setGazetteerPathNoSensitive(String GazetteerPathNoSensitive) {
        this.GazetteerPathNoSensitive = GazetteerPathNoSensitive;
    }

    

    @Override
    public void processCorpus(GateCorpus Corpus) {
        try {
            this.getPipeline().processCorpus(Corpus.getCorpus());

        } catch (ExecutionException ex) {
            logger.error(Name+" APPLICATION FAILED ON EXECUTION");
            logger.error(ex);
        }
    }
    
}
