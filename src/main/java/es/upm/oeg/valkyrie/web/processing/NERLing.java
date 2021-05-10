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
public class NERLing extends GateApplication{
    
    
    
    private String GazetteerPathSensitive;
    private String GazetteerPathNoSensitive;
    private String JapePath;
   
    private static Logger logger = Logger.getLogger(NERLing.class);
   
    
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
            
            // Sentence
            FeatureMap fmSplitter= Factory.newFeatureMap();
            fmSplitter.put("inputASName", this.getAppAnnotationSetName());
            fmSplitter.put("outputASName", this.getAppAnnotationSetName());
            ProcessingResource SSplitter=  GateFactoryInterface.createANNIESplitter(Name+"-Split","UTF-8",fmSplitter);
            
            // NER
            ProcessingResource NER=  GateFactoryInterface.createJAPERule(
                    Name+"-NER", "UTF-8", this.getAppAnnotationSetName(), this.getAppAnnotationSetName(), this.JapePath);
            logger.info("EntityRole Resources loaded");
            
            
            
            // ROLE
            /*
            String hnerNEPath = GateHandler.getPluginsHomeDir()+File.separator+"Valkyr_IE"+File.separator+"NE";
            RCM rcms=new RCM("Organization");
            rcms.isLinguistic=true;
            
            rcms.addRoleWithRule("Intermediary",hnerNEPath+"/rules/RoleIntermediary.jape");
            rcms.addRoleWithRule("Shareholder",hnerNEPath+"/rules/RoleShareholder.jape");
            rcms.addRoleWithRule("Offshore",hnerNEPath+"/rules/RoleOffshore.jape");
            
           
            FeatureMap FMrcm= Factory.newFeatureMap();
            FMrcm.put("annotationSet", this.getAppAnnotationSetName());
            FMrcm.put("roleClassModel", rcms);
            ProcessingResource RoleIdentifier=  GateFactoryInterface.createProcessingResource(Name+"-RoleIdentifier", "org.upm.oeg.valkyr.ie.gate.pr.role.RoleIdentifierRCM", FMrcm);

            */
         
            // APP
            ProcessingResource Releaser;
            if(this.ReleaseMemoryPlan){
            
             Releaser= GateFactoryInterface.createAnnotationDeleter(Name+"-Releaser", this.getAppAnnotationSetName(), "Token","Split","SpaceToken","Sentence");
            super.setPipeline(new GatePipeline(Name,reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive ,SSplitter,NER,Releaser));
            }else{
           
                super.setPipeline(new GatePipeline(Name,reset,Tokenizer,GazetteerSensitive,GazetteerNoSensitive ,SSplitter,NER));
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

    public void setJapePath(String JapePath) {
        this.JapePath = JapePath;
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
