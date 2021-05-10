/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr;

import es.upm.oeg.valkyrie.gate.application.GateApplication;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;
import gate.Annotation;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public class JAPEEvaluator extends GateApplication {
    
    

    
    public List <String>SetNames;
    public String OriginalSet;
    private static Logger logger = Logger.getLogger(JAPEEvaluator.class);
  

    
    
    public JAPEEvaluator() {
    
        super.setApplicationName("Rule Evaluator");
    }
    
    public JAPEEvaluator(String ApplicatonName) {
    
        super.setApplicationName(ApplicatonName);
    }
    
    
    public void initPipeline(String OriginalSet) {

        try {
            this.SetNames=new ArrayList();
            this.OriginalSet=OriginalSet;
            logger.info("Initialising  Resources");

            super.setPipeline(new GatePipeline("RULE-Evaluator"));

            logger.info("Pipeline created");
        } catch (ResourceInstantiationException ex) {
            logger.error(" APPLICATION FAILED ON INITIALIZATION", ex);

        }

    }

    
    public void addRULE(String name, String set, String Path){
    
        try {
            // NER
            FeatureMap fmNER= Factory.newFeatureMap();
            fmNER.put("grammarURL",new File(Path).toURI().toURL());
            fmNER.put("inputASName", this.OriginalSet);
            fmNER.put("outputASName", set);
            
            ProcessingResource Rule=  GateFactoryInterface.createProcessingResource(name,"gate.creole.ANNIETransducer",fmNER);
            this.SetNames.add(set);
            
            super.getPipeline().addProcessingResource(Rule);
            
        } catch (MalformedURLException | ResourceInstantiationException ex) {
           logger.error(ex);
           logger.error(ex.getMessage());
        }
    
    }
    
    
    
    public void processCorpus(GateCorpus Corpus) {
    
          
         try {
            super.getPipeline().processCorpus(Corpus.getCorpus());

            
        } catch (ExecutionException ex) {
            logger.error("APPLICATION FAILED ON EXECUTION",ex);
        
        }

    }
    
    public void evaluate(GateCorpus Corpus,String AnnotationType, String GoldSet){
    
        
        for(String set: this.SetNames){
        
            Corpus.evaluateAnnotation("Eval_"+set, AnnotationType, set, GoldSet).printResult();
        }
        
       // Corpus.evaluateAnnotation("Eval2", "Organization", "nerRole2", "EvaluationComplete").printResult();
    
    
    };
            
    
    
    public void uncoveredEvaluator(GateCorpus corpus, String Entity, String GoldToFind, String Uncovered){
        
        
        
        for(GateDocument doc: corpus.Documents){
        
            HashSet GoldSet= new HashSet();
          HashSet <String> goldlines= new HashSet();
          List <String> ocurrences=new ArrayList();
           List <Annotation> listgold= doc.getAnnotations(Entity, GoldToFind);
           
           for(Annotation annot:listgold){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
              
               if(!GoldSet.contains(text)){
                   //System.out.println("Gold:"+text);
                   GoldSet.add(text);
               }
               
               
           }
           
           
           // HARDCODED
            List <Annotation> auxList= doc.getAnnotations("Organization", "EvaluationOffshore");
           HashSet <String> set= new HashSet();
           for(Annotation annot:auxList){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               if(!set.contains(text)){
                   System.out.println("Offshore:"+text);
                   set.add(text);
               }
           }
           
           auxList= doc.getAnnotations("Organization", "EvaluationIntermediary");
           set= new HashSet();
           for(Annotation annot:auxList){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               if(!set.contains(text)){
                   System.out.println("Intermediary:"+text);
                   set.add(text);
               }
           }
            auxList= doc.getAnnotations("Organization", "EvaluationShareholder");
           set= new HashSet();
           for(Annotation annot:auxList){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               if(!set.contains(text)){
                   System.out.println("Shareholder:"+text);
                   set.add(text);
               }
           }
           
           // -- HARDCODED
           
           
           //jape.uncoveredEvaluator(CorpusExper.TestCorpus, "Organization", "EvaluationComplete", "nerRole");
           
           List <Annotation> listUncovered= doc.getAnnotations(Entity, Uncovered);
             for(Annotation annot:listUncovered){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               if(GoldSet.contains(text)){continue;}
               
               try{
               Long offset1= annot.getStartNode().getOffset();
               if(offset1>15.0){offset1= offset1-15; }
               Long offset2= annot.getEndNode().getOffset()+15;
               //if(offset1>5.0){offset1= offset1-5; }
               String context = gate.Utils.cleanStringFor(doc.getDocumentIntance(),offset1, offset2);
               String gold =createGoldStandardLine(doc.getDocumentIntance(),annot, Entity);
                   ocurrences.add(text + " \t\t " + context);
                   goldlines.add(gold);
               }catch(Exception e){
                   logger.error(doc.getDocumentName()+ " "+text+" OUT OF SCOPE");
               }
           }
             for(String s: goldlines){
                 System.out.println(s);
             }
             for(String s: ocurrences){
                 System.out.println(s);
             }
             
             
             System.out.println("\n\n");
            
            
            
        
        
        }
    
    
    }
    
    
    public void notFoundRoleEvaluator(GateCorpus corpus, String Entity, String GoldAnnotationSet, String FindingAnnotationSet){
        
        
        
        for(GateDocument doc: corpus.Documents){
        
           HashSet GoldSet= new HashSet();
           List <Annotation> listgold= doc.getAnnotations(Entity, GoldAnnotationSet);
           for(Annotation annot:listgold){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               System.out.println("To Identify  " + Entity +" :"+text);
               if(!GoldSet.contains(text)){
                   //System.out.println("Gold:"+text);
                   GoldSet.add(text);
               }
           }
           
           
           // 
            List <Annotation> auxList= doc.getAnnotations(Entity, FindingAnnotationSet);
           HashSet <String> FoundSet= new HashSet();
           for(Annotation annot:auxList){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               System.out.println("Found  " + Entity +" :"+text);
               if(!FoundSet.contains(text)){
                   
                   FoundSet.add(text);
               }
           }
          
        
           
           //jape.uncoveredEvaluator(CorpusExper.TestCorpus, "Organization", "EvaluationComplete", "nerRole");
           List <String> ocurrences = new ArrayList();
           List <Annotation> listUncovered= doc.getAnnotations(Entity, GoldAnnotationSet);
             for(Annotation annot:listUncovered){
               String text= gate.Utils.cleanStringFor(doc.getDocumentIntance(), annot);
               //if(GoldSet.contains(text)){continue;}
               
               try{
               Long offset1= annot.getStartNode().getOffset();
               if(offset1>20.0){
                   offset1= offset1-20; 
                 }
               Long offset2= annot.getEndNode().getOffset()+20;
               //if(offset1>5.0){offset1= offset1-5; }
               String context = gate.Utils.cleanStringFor(doc.getDocumentIntance(),offset1, offset2);
               
                   ocurrences.add(text + " \t\t " + context);
              
               }catch(Exception e){
                   logger.error(doc.getDocumentName()+ " "+text+" OUT OF SCOPE");
               }
           }
             
             for(String s: ocurrences){
                 System.out.println(s);
             }
             
             
             System.out.println("\n\n");
            
            
        
        }
    
    
    }
    
    
  
    private String createGoldStandardLine(Document doc,Annotation Annot, String  OutputAnnotationType) {

        String line, Start, End, Text;
        String tab = "\t";

        Start = Annot.getStartNode().getOffset().toString();
        End = Annot.getEndNode().getOffset().toString();
        // Get text from an annotation
        Text = gate.Utils.cleanStringFor(doc, Annot).replaceAll("\n", " ");
        line = doc.getName() + tab +  Text; // + tab + OutputAnnotationType;
        //line = line + "\n";
        return line;
    }
  

    @Override
    public void initPipeline() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
    
    
    
}
