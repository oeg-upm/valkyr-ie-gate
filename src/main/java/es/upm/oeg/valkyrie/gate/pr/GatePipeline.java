/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr;

import es.upm.oeg.valkyrie.gate.evaluation.NEREvaluation;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public  class GatePipeline {
    
    final static Logger logger = Logger.getLogger(GatePipeline.class);
    
    private SerialAnalyserController ApplicationPipeline;
    
    

    public GatePipeline(String ApplicationName) throws ResourceInstantiationException{
    
        ApplicationPipeline= (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
        ApplicationPipeline.setName(ApplicationName);
    }
    
    public GatePipeline(String ApplicationName, ProcessingResource ... prs) throws ResourceInstantiationException{
        ApplicationPipeline= (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
        ApplicationPipeline.setName(ApplicationName);
        this.addProcessingResources(prs);
    }
    
    public GatePipeline(String ApplicationName, List <ProcessingResource> prs) throws ResourceInstantiationException{
        ApplicationPipeline= (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
        ApplicationPipeline.setName(ApplicationName);
        this.addProcessingResources(prs);
    }
    
    public void prepareProcessingResources() throws Exception{}
    
    
    public void processCorpus(Corpus corpus) throws ExecutionException {
    
            ApplicationPipeline.setCorpus(corpus);
            ApplicationPipeline.execute();
            
    }
    
    public void processCorpus(GateCorpus corpus) throws ExecutionException {
    
            ApplicationPipeline.setCorpus(corpus.getCorpus());
            ApplicationPipeline.execute();
            
    }

    public void processCorpus(Document Doc) throws ExecutionException {
    
            ApplicationPipeline.setDocument(Doc);
            ApplicationPipeline.execute();
            
    }
    
    public SerialAnalyserController getSerialApplicationPipeline() {
        return ApplicationPipeline;
    }

    public void setSerialApplicationPipeline(SerialAnalyserController SerialApplicationPipeline) {
        this.ApplicationPipeline = SerialApplicationPipeline;
    }
    
    
    public void addProcessingResources(ProcessingResource... prs){
    
        for(ProcessingResource pr: prs){
        
            ApplicationPipeline.add(pr);
        }
    
    
    }
    
    public void addProcessingResources(List <ProcessingResource> prs){
    
        for(ProcessingResource pr: prs){
        
            ApplicationPipeline.add(pr);
        }
    }
    
    public void addProcessingResource(ProcessingResource pr){
        
            ApplicationPipeline.add(pr);
        
    }
    
    
    public void evaluateAnnotation(String EvaluationName,String AnnotationName, String AnnotationSetName, String AnnotationGoldSetName){
    
        NEREvaluation eval= new NEREvaluation(AnnotationSetName,AnnotationGoldSetName);
        eval.evaluateAnnotation(EvaluationName,AnnotationName,this.ApplicationPipeline.getCorpus());
    
    }
    
    
}
