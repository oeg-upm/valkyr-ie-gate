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
import java.net.URL;
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
public class NEROpen extends GateApplication{
    

   public String NERModel;
   public boolean ReleaseMemoryPlan=false;
    
    private static Logger logger = Logger.getLogger(NEROpen.class);
    
    
    
    public void initPipeline() {

       
        
        try {
            logger.info("Initialising OpenNLP Resources");
            
            
            URL OpenNLPDir=GateHandler.getPluginURL("OpenNLP");
            
             
            ProcessingResource Releaser= GateFactoryInterface.createAnnotationDeleter("Open-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            
            //  reset
            //List <String> SetsToRemove= new ArrayList(); SetsToRemove.add(super.getAppAnnotationSetName());
            ProcessingResource reset=GateFactoryInterface.createAnnotationDeleter("Open-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            
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
          
            String PathNERModel=OpenNLPDir.getFile().toString()+File.separator+"models"+File.separator+this.NERModel;
            fmNER.put("config",PathNERModel);
            fmNER.put("inputASName", super.getAppAnnotationSetName());
            fmNER.put("outputASName", super.getAppAnnotationSetName());
            
            ProcessingResource NER=  GateFactoryInterface.createProcessingResource("OpenNLPNER","gate.opennlp.OpenNLPNameFin",fmNER);
            
            logger.info("OpenNLP Resources loaded");
            
            // APP
            
          
            if(this.ReleaseMemoryPlan){
            
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
            this.getPipeline().processCorpus(Corpus.getCorpus());

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
