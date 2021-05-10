/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.application;

import es.upm.oeg.valkyrie.gate.GateHandler;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public class CoreNLP_NERApplication extends GateApplication {
    
    
    public String NERModel= "english.all.3class.distsim.crf.ser.gz";
    
   
    
   
    private String split=File.separator;
    
    private static Logger logger = Logger.getLogger(CoreNLP_NERApplication.class);
    public boolean ReleaseMemoryPlan=false;
    
    
    public CoreNLP_NERApplication(String NERModulePath) {  
         this.NERModel= NERModulePath;
    }
    public CoreNLP_NERApplication() {  
        
    }
  
    public void initPipeline() {

        URL Stanforddir=GateHandler.getPluginURL("Stanford_CoreNLP");
        String PathNERModel=Stanforddir.getFile().toString()+File.separator+"resources"+File.separator+this.NERModel;
        
        try {
            logger.info("Initialising Stanford Resources");
            
            URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
            logger.info("ANNIE PLUGIN DIR: "+ ANNIEdir.toString());
            
            logger.info("STANFORD PLUGIN DIR: "+Stanforddir.toString());
            //  reset
            List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(super.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createDocumentReset("Deleter", new ArrayList(),  SetsToRemove);
            
            
            // Token
            FeatureMap fmTk= Factory.newFeatureMap();
            fmTk.put("inputASName", super.getAppAnnotationSetName());
            fmTk.put("outputASName", super.getAppAnnotationSetName());
            ProcessingResource Tokenizer=  GateFactoryInterface.createProcessingResource("StanfordTok","gate.stanford.Tokenizer",fmTk);
            
       
            FeatureMap fmSplitter= Factory.newFeatureMap();
            fmSplitter.put("encoding", "UTF-8");
            fmSplitter.put("gazetteerListsURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
                    + File.separator + "gazetteer" + File.separator + "lists.def").toURI().toURL());
            
            fmSplitter.put("transducerURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
                    + File.separator + "grammar" + File.separator + "main.jape").toURI().toURL());
            fmSplitter.put("inputASName", super.getAppAnnotationSetName());
            fmSplitter.put("outputASName", super.getAppAnnotationSetName());
            ProcessingResource SSplitter=  GateFactoryInterface.createProcessingResource("StanfordSS","gate.creole.splitter.SentenceSplitter",fmSplitter);

            //GateFactoryInterface.createProcessingResource("StanfordSS","gate.creole.splitter.RegexSentenceSplitter",fmSS);
            
            
            
            // NER
            FeatureMap fmNER= Factory.newFeatureMap();
            URL otherUrl = new File(PathNERModel).toURI().toURL();
            fmNER.put("modelFile",otherUrl );
            fmNER.put("inputASName", super.getAppAnnotationSetName());
            fmNER.put("outputASName", super.getAppAnnotationSetName());
            ProcessingResource NER=  GateFactoryInterface.createProcessingResource("StanfordNER","gate.stanford.NER",fmNER);
           
            logger.info("Stanford Resources loaded");
            

            // app
            
            ProcessingResource Releaser;
            if(this.ReleaseMemoryPlan){
            
             Releaser= GateFactoryInterface.createAnnotationDeleter("Stanford-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            super.setPipeline( new GatePipeline(super.getApplicationName(),reset,Tokenizer,SSplitter,NER,Releaser));
            }else{
           
                super.setPipeline( new GatePipeline(super.getApplicationName(),reset,Tokenizer,SSplitter,NER));
            }
            
            
            

            logger.info("Stanford Pipeline created");
            
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error("STANFORD APPLICATION FAILED ON INITIALIZATION");
            logger.error(ex.toString());
            ex.printStackTrace();
        } 
    }
    
    
    
    public void processCorpus(GateCorpus Corpus){
    
        try {
            super.getPipeline().processCorpus(Corpus.getCorpus());
            
            // Align
            // Corpus.copyAnnotation(this.getAppAnnotationSetName(),"ORGANIZATION", "Organization");

            
        } catch (ExecutionException ex) {
            logger.error("STANFORD APPLICATION FAILED ON EXECUTION");
            logger.error(ex);
        }
        
    }
 

    public void setNERModel(String NERModel) {
        this.NERModel = NERModel;
    }


   
    
    
    
    
}
