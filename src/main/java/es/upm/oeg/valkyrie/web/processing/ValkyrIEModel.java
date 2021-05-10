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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import es.upm.oeg.valkyrie.gate.GateHandler;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;
import es.upm.oeg.valkyrie.gate.application.GateApplication;


/**
 *
 * @author pcalleja
 */
public class ValkyrIEModel extends GateApplication{
    
    

    //private static Logger logger = Logger.getLogger(ValkyrIEModel.class);
   
    
    public boolean ReleaseMemoryPlan=true;
   
 
     Logger logger = LoggerFactory.getLogger(ValkyrIEModel.class);
   
    
    public ProcessingResource processCreator(String Command) throws ResourceInstantiationException, MalformedURLException{
    
        String [] command = Command.split("=");
        String processType= command[0].toLowerCase();
        
        
        // Reseter, Tokenizer, Splitter, Gazetteer, Jape
        
        if (processType.equals("reseter")){
        
            
            //  reset
            List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(this.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createDocumentReset("Deleter", new ArrayList(),  SetsToRemove);
            return reset;
            
        }
        
        
        if (processType.equals("tokenizer")){
        
            // Token
            FeatureMap fmToken= Factory.newFeatureMap();
            fmToken.put("annotationSetName", this.getAppAnnotationSetName());
            ProcessingResource Tokenizer=  GateFactoryInterface.createANNIETokeniser("Token","UTF-8",fmToken);
            logger.info("Tokenizer Resources loaded");

            return Tokenizer;
        }
        
        
        if (processType.equals("splitter")){
        
              // Sentence
            FeatureMap fmSplitter= Factory.newFeatureMap();
            fmSplitter.put("inputASName", this.getAppAnnotationSetName());
            fmSplitter.put("outputASName", this.getAppAnnotationSetName());
            ProcessingResource SSplitter=  GateFactoryInterface.createANNIESplitter("Split","UTF-8",fmSplitter);
            logger.info("Splitter Resources loaded");

            return SSplitter;
            
        }
        
        
        if ((processType.equals("gazetteer_s")) || (processType.equals("gazetteer"))) {
        
                // Gazetter
            String value= command[1];
            String Path= GateHandler.getPluginsHomeDir()+File.separator+value;//"Valkyr_IE"+File.separator+"NE"/gazetteer/dinamic.lst;
            
            ProcessingResource GazetteerSensitive=  GateFactoryInterface.createANNIEGazetteer("Gaze-Sensitive", "UTF-8",
                    true,  this.getAppAnnotationSetName(), ":",
                    true,  value);
            logger.info("Gazetteer S Resources loaded");

            return GazetteerSensitive;
            
            
        }
        if (processType.equals("gazetteer_ns")){
        
                // Gazetter
            String value= command[1];
            String Path= GateHandler.getPluginsHomeDir()+File.separator+value;//"Valkyr_IE"+File.separator+"NE"/gazetteer/dinamic.lst;
            
            // Gazetter
            ProcessingResource GazetteerNoSensitive=  GateFactoryInterface.createANNIEGazetteer("Gaze-NoSensitive", "UTF-8",
                    false,  this.getAppAnnotationSetName(), ":",
                    true,   value);
            logger.info("Gazetteer NS Resources loaded");

            return GazetteerNoSensitive;
        }
        
        
            
        
        
        
        if (processType.equals("jape")){
        
             // NER
            String value= command[1];
            String Path= GateHandler.getPluginsHomeDir()+File.separator+value;//"Valkyr_IE"+File.separator+"NE"/gazetteer/dinamic.lst;
            ProcessingResource jape=  GateFactoryInterface.createJAPERule(
                    "NER", "UTF-8", this.getAppAnnotationSetName(), this.getAppAnnotationSetName(), value);
            logger.info("JAPE Resources loaded");
            
            return jape;
        }
        
        
        
        logger.error("not found");
    
    
        return null;
    }
    
    
    
    
    
    
    public void initPipelineGuided(ConfigurationFile Conf) throws Exception {

        try {
            logger.info("Initialising Resources");
            
            
            
            GatePipeline pipe= new GatePipeline("Valkyr");
            
            
            for (String command: Conf.Instructions){
            
                    ProcessingResource pr= processCreator(command);
                    pipe.addProcessingResource(pr);
            
            }
            
            super.setPipeline(pipe);
            
            // APP
            
            /*
            ProcessingResource Releaser;
            if(this.ReleaseMemoryPlan){
            
            Releaser= GateFactoryInterface.createAnnotationDeleter(Name+"-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            super.setPipeline(new GatePipeline(Name,reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive ,SSplitter,NER,Releaser));
            }
            
            */
            
            
            logger.info(" Pipeline created");
            
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error(" APPLICATION FAILED ON INITIALIZATION",ex);
            logger.error(ex.toString());
           
           throw ex;
        }
        
    }
    
 
   

    @Override
    public void processCorpus(GateCorpus Corpus) {
        try {
            this.getPipeline().processCorpus(Corpus.getCorpus());

        } catch (ExecutionException ex) {
            logger.error(" APPLICATION FAILED ON EXECUTION");
            logger.error(ex.toString());
        }
    }

    @Override
    public void initPipeline() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
