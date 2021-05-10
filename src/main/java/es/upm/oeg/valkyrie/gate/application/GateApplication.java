/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.application;

import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;


/**
 *
 * @author pcalleja
 */
public abstract class GateApplication {
    
    
    private String ApplicationName="Application";
    
    private  String AppAnnotationSetName="";
    
    private GatePipeline Pipeline;
    
    public abstract void initPipeline();
    
    public abstract void processCorpus(GateCorpus Corpus);
    
    public void deleteResources(){}
    
    
    public void deleteApplication(){}

    public String getApplicationName() {
        return ApplicationName;
    }

    public void setApplicationName(String ApplicationName) {
        this.ApplicationName = ApplicationName;
    }
    
    public String getAppAnnotationSetName() {
        return AppAnnotationSetName;
    }

    public void setAppAnnotationSetName(String AppAnnotationSetName) {
        this.AppAnnotationSetName = AppAnnotationSetName;
    }

    public GatePipeline getPipeline() {
        return Pipeline;
    }

    public void setPipeline(GatePipeline Pipeline) {
        this.Pipeline = Pipeline;
    }
    
    
    
}
