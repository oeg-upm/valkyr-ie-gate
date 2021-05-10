/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.application;


import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
public class Valkyr_NERApplication extends GateApplication{
    
    
    
    
    private String GazetteerPathSensitive;
    private String GazetteerPathNoSensitive;
    private String JapePath;
    
    public boolean ReleaseMemoryPlan=false;
    
    
    private static Logger logger = Logger.getLogger(Valkyr_NERApplication.class);

    public Valkyr_NERApplication()  {}
    
    public void initPipeline() {

        try {
            logger.info("Initialising Valkyr Resources");
            
            //  reset
            List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(super.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createDocumentReset("Valkyr-Deleter", new ArrayList(),  SetsToRemove);
            
            // Token
            FeatureMap fmToken= Factory.newFeatureMap();
            fmToken.put("annotationSetName", super.getAppAnnotationSetName());
            ProcessingResource Tokenizer=  GateFactoryInterface.createANNIETokeniser("Valkyr-Token","UTF-8",fmToken);
            
            // Gazetter
            ProcessingResource GazetteerSensitive=  GateFactoryInterface.createANNIEGazetteer("Valkyr-Gaze-Sensitive", "UTF-8",
                    true,  super.getAppAnnotationSetName(), ":",
                    true,  this.GazetteerPathSensitive);
            
            // Gazetter
            ProcessingResource GazetteerNoSensitive=  GateFactoryInterface.createANNIEGazetteer("Valkyr-Gaze-NoSensitive", "UTF-8",
                    false,  super.getAppAnnotationSetName(), ":",
                    true,  this.GazetteerPathNoSensitive);
            
            // Sentence
            FeatureMap fmSplitter= Factory.newFeatureMap();
            fmSplitter.put("inputASName", super.getAppAnnotationSetName());
            fmSplitter.put("outputASName", super.getAppAnnotationSetName());
            ProcessingResource SSplitter=  GateFactoryInterface.createANNIESplitter("ANNIE-Split","UTF-8",fmSplitter);
            
            // NER
            ProcessingResource NER=  GateFactoryInterface.createJAPERule("Valkyr-NER", "UTF-8", super.getAppAnnotationSetName(), super.getAppAnnotationSetName(), this.JapePath);
            logger.info("Valkyr Resources loaded");
            
            // APP
            
            // app
            
            ProcessingResource Releaser;
            if(this.ReleaseMemoryPlan){
            
             Releaser= GateFactoryInterface.createAnnotationDeleter("Valkyr-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            super.setPipeline(new GatePipeline("ValkyrNERApp",reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive ,SSplitter,NER,Releaser));
            }else{
           
                super.setPipeline(new GatePipeline("ValkyrNERApp",reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive ,SSplitter,NER));
            }
            
            
            logger.info("Valkyr Pipeline created");
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error("Valkyr APPLICATION FAILED ON INITIALIZATION");
            logger.error(ex);
            System.out.println(ex);
        }
        
    }
    
    public void processCorpus(GateCorpus Corpus){
    
         try {
            super.getPipeline().processCorpus(Corpus.getCorpus());

            
        } catch (ExecutionException ex) {
            logger.error("Valkyr APPLICATION FAILED ON EXECUTION");
            logger.error(ex);
            ex.printStackTrace();
            System.out.println(ex);
        }
    }
            
            
    public void deleteResources(){}        

  

    public void setGazetteerPathSensitive(String GazetteerPathSensitive) {
        this.GazetteerPathSensitive = GazetteerPathSensitive;
    }

    public void setGazetteerPathNoSensitive(String GazetteerPathNoSensitive) {
        this.GazetteerPathNoSensitive = GazetteerPathNoSensitive;
    }

    public void setJapePath(String JapePath) {
        this.JapePath = JapePath;
    }

   
    
    
    
    
}
