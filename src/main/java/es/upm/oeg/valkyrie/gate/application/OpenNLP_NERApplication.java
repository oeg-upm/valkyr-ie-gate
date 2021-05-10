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
public class OpenNLP_NERApplication extends GateApplication{
    

   public String NERModel;
   public boolean ReleaseMemoryPlan=false;
    
    private static Logger logger = Logger.getLogger(OpenNLP_NERApplication.class);
    
    public OpenNLP_NERApplication() {

}
    
    public void initPipeline() {

       
        
        try {
            logger.info("Initialising OpenNLP Resources");
            
            
            URL OpenNLPDir=GateHandler.getPluginURL("OpenNLP");
            
             
            
            
            //  reset
            List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(super.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createDocumentReset("Deleter", new ArrayList(),  SetsToRemove);
            
            // Token
            FeatureMap fmTk= Factory.newFeatureMap();
            //fmTk.put("model", "models/english/en-token.bin");
            fmTk.put("annotationSetName", super.getAppAnnotationSetName());
            ProcessingResource Tokenizer=  GateFactoryInterface.createProcessingResource("OpenNLPTok","gate.opennlp.OpenNlpTokenizer",fmTk);
            
            // Sentence
            FeatureMap fmSS= Factory.newFeatureMap();
            //fmSS.put("model", "models/english/en-sent.bin");
            fmSS.put("annotationSetName", super.getAppAnnotationSetName());
            ProcessingResource SSplitter=  GateFactoryInterface.createProcessingResource("OpenNLPSS","gate.opennlp.OpenNlpSentenceSplit",fmSS);
            //GateFactoryInterface.createProcessingResource("StanfordSS","gate.creole.splitter.RegexSentenceSplitter",fmSS);
            
            
            // NER
            FeatureMap fmNER= Factory.newFeatureMap();
            
            ///models/english/en-ner.conf
            if(this.NERModel!=null){
                String PathNERModel=OpenNLPDir.getFile().toString()+File.separator+"models"+File.separator+this.NERModel;
                fmNER.put("config",PathNERModel);
            }
            
            fmNER.put("inputASName", super.getAppAnnotationSetName());
            fmNER.put("outputASName", super.getAppAnnotationSetName());
            ProcessingResource NER=  GateFactoryInterface.createProcessingResource("OpenNLPNER","gate.opennlp.OpenNLPNameFin",fmNER);
            
            logger.info("OpenNLP Resources loaded");
            
            // APP
            
             ProcessingResource Releaser;
            if(this.ReleaseMemoryPlan){
            
             Releaser= GateFactoryInterface.createAnnotationDeleter("Open-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
             super.setPipeline( new GatePipeline("OpenNLPNERApp",reset,Tokenizer,SSplitter,NER,Releaser));
            }else{
           
                super.setPipeline( new GatePipeline("OpenNLPNERApp",reset,Tokenizer,SSplitter,NER));
            }
            
           
            
            logger.info("OpenNLP Pipeline created");
            
            
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error("OpenNLP APPLICATION FAILED ON INITIALIZATION");
            logger.error(ex);
        } 
        
    }

    public void processCorpus(GateCorpus Corpus) {

        try {
            super.getPipeline().processCorpus(Corpus.getCorpus());

        } catch (ExecutionException ex) {
            logger.error("OPENNLP APPLICATION FAILED ON EXECUTION");
            logger.error(ex);
        }

    }

            
    public void deleteResources(){}        

   

    public void setNERModel(String NERModel) {
        this.NERModel = NERModel;
    }

    
    
    
    
    
}
