/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.execution;

import es.upm.oeg.valkyrie.gate.GateHandler;
import es.upm.oeg.valkyrie.gate.application.AnnieGATE_NERApplication;
import es.upm.oeg.valkyrie.gate.application.OpenNLP_NERApplication;
import es.upm.oeg.valkyrie.gate.application.Valkyr_NERApplication;
import java.io.File;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;




/**
 *
 * @author pcalleja
 */
public class Runner {
    
    
    
    public static String CorpusDir= "resources"+File.separator+"Corpus";
    
    public static void main(String [] args) throws Exception {

        System.out.println("Welcome to Valkyr_IE");
        
        // INIT GATE
        GateHandler.initGate("resources", true, "ANNIE", "Tools","OpenNLP","Stanford_CoreNLP","Valkyr_IE");
        
         // Init GateCorpus         
        GateCorpus Corpus=  new GateCorpus();
        Corpus.createCorpusfromFolder(CorpusDir,"Corpus","UTF-8");
        
        
        
        OpenNLP_NERApplication openNLP = new OpenNLP_NERApplication();
        openNLP.setAppAnnotationSetName("open");
        openNLP.initPipeline();
        
        AnnieGATE_NERApplication annieAPP = new AnnieGATE_NERApplication();
        annieAPP.setAppAnnotationSetName("annie");
        annieAPP.initPipeline();
        
        
        
        String ValPath = GateHandler.getPluginsHomeDir()+File.separator+"Valkyr_IE"+File.separator+"NE";
        Valkyr_NERApplication ValAPP = new Valkyr_NERApplication();
        ValAPP.setAppAnnotationSetName("Valkyr-IE");
        ValAPP.ReleaseMemoryPlan=false;
        ValAPP.setGazetteerPathNoSensitive(ValPath+File.separator+"gazetteer"+File.separator+"GazetteerNoSensitive.def");
        ValAPP.setGazetteerPathSensitive(ValPath+File.separator+"gazetteer"+File.separator+"GazetteerSensitive.def");
        ValAPP.setJapePath(ValPath+File.separator+"rules"+File.separator+"NE.jape");
        ValAPP.initPipeline();
        
        annieAPP.processCorpus(Corpus);
        openNLP.processCorpus(Corpus);
        ValAPP.processCorpus(Corpus);
        
        
        
       
        
    

    }
    
    
   
    
}
