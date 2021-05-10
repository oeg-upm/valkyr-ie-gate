/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.oeg.valkyrie.gate.GateHandler;
import es.upm.oeg.valkyrie.gate.application.OpenNLP_NERApplication;
import es.upm.oeg.valkyrie.gate.application.Valkyr_NERApplication;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;

/**
 *
 * @author Pablo
 */
public class Valkyr_IE {
    
    
    final static Logger logger = Logger.getLogger(Valkyr_IE.class);
    
    
    Valkyr_NERApplication ValAPP;
    OpenNLP_NERApplication openNLP;
    
    public Valkyr_IE(String PathToResources) throws Exception{
    
        logger.info("Welcome to Valkyr_IE");
        
        // INIT GATE
        GateHandler.initGate(PathToResources, false, "ANNIE", "Tools","OpenNLP","Stanford_CoreNLP","Valkyr_IE");
        
        
        
        
        
        openNLP = new OpenNLP_NERApplication();
        openNLP.setAppAnnotationSetName("Open");
        openNLP.initPipeline();
        
        
        String ValPath = GateHandler.getPluginsHomeDir()+File.separator+"Valkyr_IE"+File.separator+"NE";
        ValAPP = new Valkyr_NERApplication();
        ValAPP.setAppAnnotationSetName("Valkyr-IE");
        ValAPP.ReleaseMemoryPlan=false;
        ValAPP.setGazetteerPathNoSensitive(ValPath+File.separator+"gazetteer"+File.separator+"GazetteerNoSensitive.def");
        ValAPP.setGazetteerPathSensitive(ValPath+File.separator+"gazetteer"+File.separator+"GazetteerSensitive.def");
        ValAPP.setJapePath(ValPath+File.separator+"rules"+File.separator+"NE.jape");
        ValAPP.initPipeline();
        
    
    }
    
    
    /*
     if (Document instanceof String){
        
            GateDocument doc=new GateDocument("document", Document.toString() , "UTF8");
            return doc;
        }
        
        if (Document instanceof URL){
        
            URL u= (URL) Document;
            GateDocument doc=new GateDocument(u.toString(), u , "UTF8");
            return doc;
        }
        
        if (Document instanceof File){
        
            File f= (File) Document;
            GateDocument doc=new GateDocument(f.getName(), f , "UTF8");
            return doc;
        }*/
    
    
    public String recognitionOfNamedEntities(String Text) throws Exception{
    
        // Init GateCorpus         
        GateCorpus Corpus=  new GateCorpus();
        Corpus.createCorpus("Corpus");
        
        GateDocument doc=new GateDocument("Text", Text , "UTF8");    
        Corpus.addDocument(doc);

        
        return processCorpus(Corpus);
        
    }
    
    public String recognitionOfNamedEntities(URL url) throws Exception{
    
        // Init GateCorpus         
        GateCorpus Corpus=  new GateCorpus();
        Corpus.createCorpus("Corpus");
        
        GateDocument doc=new GateDocument(url.toString(), url , "UTF8");   
        Corpus.addDocument(doc);
     

        
        return processCorpus(Corpus);
        
    }
    
    
    private String processCorpus(GateCorpus Corpus) throws JsonProcessingException{
    
        ValAPP.processCorpus(Corpus);
       openNLP.processCorpus(Corpus);
        // Person, Location, Organization
        List<String> entities = Arrays.asList("Person", "Location", "Organization");

        //Return
        GateDocument doc = Corpus.Documents.get(0);
        DocumentReport docrep = new DocumentReport(doc.getDocumentName());

        for (String entityType : entities) {
            doc.copyAnnotation("Valkyr-IE", entityType, "ResultSet", entityType);
            doc.copyAnnotation("Open", entityType, "ResultSet", entityType);

            HashSet<String> DetectedEntities = new HashSet();
            DetectedEntities.addAll(doc.getStringAnnotations(entityType, "Valkyr-IE"));
            DetectedEntities.addAll(doc.getStringAnnotations(entityType, "Open"));
            
            if(entityType.equals("Location")){
                docrep.Locations.addAll(DetectedEntities);
            }
            if(entityType.equals("Person")){
                docrep.Persons.addAll(DetectedEntities);
            }
            if(entityType.equals("Organization")){
                docrep.Organizations.addAll(DetectedEntities);
            }

        }

            ObjectMapper mapper = new ObjectMapper();
            
            // Java objects to JSON string - pretty-print
            String jsonInString2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(docrep);

            
            return jsonInString2;
    
         
    }
    
    
    
}
