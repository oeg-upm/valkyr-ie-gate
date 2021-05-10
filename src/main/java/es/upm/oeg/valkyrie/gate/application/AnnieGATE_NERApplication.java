/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.application;

import es.upm.oeg.valkyrie.gate.GateHandler;
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
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;
/**
 *
 * @author pcalleja
 */
public class AnnieGATE_NERApplication extends GateApplication{
    
    
    private String PathNERModel="";
    private String PathTokenModel="";
    
    
    private static Logger logger = Logger.getLogger(AnnieGATE_NERApplication.class);
  
    public boolean ReleaseMemoryPlan=false;
    public AnnieGATE_NERApplication() {}
    
    public AnnieGATE_NERApplication(String ApplicatonName) {
    
        super.setApplicationName(ApplicatonName);
    }
    
    
    public void initPipeline() {

       
        
        try {
            logger.info("Initialising ANNIE Resources");
            
            
            String ANNIEdir=GateHandler.getPluginURL("ANNIE").getFile();
           // ANNIEdir.getFile()
            
            //System.out.println(ANNIEdir+"resources"+File.separator+"gazetteer"+File.separator+"lists.def");
            
            //  reset
            List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(super.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createDocumentReset("ANNIE-Deleter", new ArrayList(),  SetsToRemove);
           
            // Token
            FeatureMap fmToken= Factory.newFeatureMap();
            fmToken.put("annotationSetName", super.getAppAnnotationSetName());
            ProcessingResource Tokenizer=  GateFactoryInterface.createANNIETokeniser("ANNIE-Token","UTF-8",fmToken);
            
            // Gazetter
            
            ProcessingResource Gazetteer=  GateFactoryInterface.createANNIEGazetteer("ANNIE-Gaze", "UTF-8",
                    true,  super.getAppAnnotationSetName(), ":",
                    true,  ANNIEdir+"resources"+File.separator+"gazetteer"+File.separator+"lists.def");
            
            //"resources"+File.separator+"GateHome"+File.separator+"Plugins"+File.separator+"ANNIE"
            
            // Sentence
            FeatureMap fmSplitter= Factory.newFeatureMap();
            fmSplitter.put("inputASName", super.getAppAnnotationSetName());
            fmSplitter.put("outputASName", super.getAppAnnotationSetName());
            ProcessingResource SSplitter=  GateFactoryInterface.createANNIESplitter("ANNIE-Split","UTF-8",fmSplitter);
            
            // NER
            FeatureMap fmNER= Factory.newFeatureMap();
            fmNER.put("grammarURL",new File(ANNIEdir+"resources"+File.separator+"NE"+File.separator+"main.jape").toURI().toURL());
            fmNER.put("inputASName", super.getAppAnnotationSetName());
            fmNER.put("outputASName", super.getAppAnnotationSetName());
            
            ProcessingResource NER=  GateFactoryInterface.createProcessingResource("ANNIE-NER","gate.creole.ANNIETransducer",fmNER);
            
            logger.info("ANNIE Resources loaded");
            
            // APP
            ProcessingResource Releaser;
            
            if(this.ReleaseMemoryPlan){
            
             Releaser= GateFactoryInterface.createAnnotationDeleter("ANNIE-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
             super.setPipeline( new GatePipeline("ANNIENERApp",reset,Tokenizer,Gazetteer,SSplitter,NER,Releaser));
            }else{
           
                super.setPipeline( new GatePipeline("ANNIENERApp",reset,Tokenizer,Gazetteer,SSplitter,NER));
            }
            
            
            logger.info("ANNIE Pipeline created");
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error("ANNIE APPLICATION FAILED ON INITIALIZATION",ex);
            
        } 
        
    }
    
    public void processCorpus(GateCorpus Corpus) {
    
          
         try {
            super.getPipeline().processCorpus(Corpus.getCorpus());

            
        } catch (ExecutionException ex) {
            logger.error("ANNIE APPLICATION FAILED ON EXECUTION",ex);
        
        }

    }
            
  

    public void setPathNERModel(String PathNERModel) {
        this.PathNERModel = PathNERModel;
    }

    
    
    
    
    
}
