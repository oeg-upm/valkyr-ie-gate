/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Utils;
import java.util.ArrayList;
import java.util.List;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;

/**
 *
 * @author Pablo
 */
public class ContextStudy {
    
    public int LeftSizeWindow=30;
    public int RightSizeWindow=30;
    
    public boolean anonymize=true;
    
    public List <String> EntityList;
    
    public void processCorpus(Corpus corpus, String annotationSet, String annotation){
    
            EntityList=new ArrayList();
        
            for (Document doc:corpus){
            
            // set
            AnnotationSet annotSetGen = doc.getAnnotations(annotationSet);
            // entities
            AnnotationSet annotationsStudyset = annotSetGen.get(annotation);
            // list -
            List<Annotation> annotationsRetrieved = annotationsStudyset.inDocumentOrder();
           
            System.out.println("DOCUMENT:"+doc.getName());
            
            for(Annotation Annot: annotationsRetrieved){
                
                
                String entityText="--";
                if(!anonymize){
                   entityText = gate.Utils.cleanStringFor(doc, Annot);
                }
                
                String left=getLeftContext(doc,  Annot, this.LeftSizeWindow);
                String right= getRightContext(doc,  Annot, this.RightSizeWindow);
                
                System.out.println(left+"|"+entityText+"|"+right);
                EntityList.add(entityText);
                
            }
                
                
            }
        
            
            
    
        
    }
    
    
    public String getLeftContext(Document doc, Annotation Annot, int number){
    
        String s="";
        try{
            
            s= Utils.cleanStringFor(doc, Annot.getStartNode().getOffset()-number, Annot.getStartNode().getOffset());
        
        }catch(Exception e){
             s= getLeftContext(doc, Annot, number-2);
        }
        return s;
        
    
    }
    
      public String getRightContext(Document doc, Annotation Annot, int number){
    
        String s="";
        try{
            
            s= Utils.cleanStringFor(doc, Annot.getEndNode().getOffset(), Annot.getEndNode().getOffset()+number);
        
        }catch(Exception e){
             s= getRightContext(doc, Annot, number-2);
        }
        return s;
        
    
    }
    
    
    public void process(GateCorpus corpus, String AnnotationSet,String Annotation){
    
        this.processCorpus(corpus.getCorpus(),AnnotationSet, Annotation);
          
    
    }
    
}
