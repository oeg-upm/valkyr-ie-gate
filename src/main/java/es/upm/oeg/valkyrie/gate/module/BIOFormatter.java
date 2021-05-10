/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

import gate.Annotation;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.util.OffsetComparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;
/**
 *
 * @author pcalleja
 */
public class BIOFormatter {
    
    
    public String AnnotationSetEntity;
    public String AnnotationSetToken;
    
    
    public void annotateCorpus(GateCorpus Corpus,  List <String> Entities) throws FileNotFoundException, UnsupportedEncodingException{
    
    
    
      
        
        for(GateDocument document: Corpus.Documents){
        
                  annotateDocument(document, Entities);
        
        }
        
   

    } 
    
    
    public void annotateDocument(GateDocument document,  List <String> Entities){
        
        
        
        // Get the annotation Set Group general
        AnnotationSet annotSetGenToken =        document.getDocumentIntance().getAnnotations(AnnotationSetToken);
        AnnotationSet annotSetGenEntity =       document.getDocumentIntance().getAnnotations(AnnotationSetEntity);

        
        // Token
        AnnotationSet annotTokenSet = annotSetGenToken.get("Token");
        List<Annotation> annotTokenList = new ArrayList<>(annotTokenSet);
        Collections.sort(annotTokenList, new OffsetComparator());

        
        // Each token with feature = O
        for (Annotation Token : annotTokenList) {
            Token.getFeatures().put("CONLL", "O");
        }

        
        for(String Entity: Entities){
        
            annotateEntityType( annotSetGenEntity,  annotTokenSet,  Entity);

        }
        
    
    }
    
    
    public void annotateEntityType(AnnotationSet annoEntitytSet, AnnotationSet annoTokenSet, String EntityType){
        
           // Entity
        AnnotationSet annotEntitySet = annoEntitytSet.get(EntityType);
        List<Annotation> annotEntityList = new ArrayList<>(annotEntitySet);
        Collections.sort(annotEntityList, new OffsetComparator());
        
       
        
         // Annotate entities
        for (Annotation Entity : annotEntityList) {
            AnnotationSet TokensInEntity = annoTokenSet.get(Entity.getStartNode().getOffset(), Entity.getEndNode().getOffset());
            List<Annotation> TokensInEntityList = new ArrayList<>(TokensInEntity);
            Collections.sort(TokensInEntityList, new OffsetComparator());
            int i = 0;

            for (Annotation Token : TokensInEntityList) {

                  Token.getFeatures().remove("CONLL");
                  Token.getFeatures().put("CONLL", EntityType);
                /*
                if (i == 0) {
                    Token.getFeatures().remove("CONLL");
                    Token.getFeatures().put("CONLL", "B-"+EntityType);
                    
                    System.out.println("entro0"+i+"  "+Token.getFeatures().get("string").toString());
                  
                    
                } else {
                    Token.getFeatures().remove("CONLL");
                    Token.getFeatures().put("CONLL", "I-"+EntityType);
                      System.out.println("entro1"+i+"  "+Token.getFeatures().get("string").toString());
                     
                }
*/
                i++;
                
            }
        }
    
    
    
    }
    
    
    public void exportDataStanfordFormat(GateCorpus Corpus, String FormatedFilePath) throws FileNotFoundException, UnsupportedEncodingException{
    
                  /////// WRITE
        StringBuffer bf = new StringBuffer();
        String line = "", Text,  SystemVal;
        String sep = "\t";
        
        for(GateDocument document: Corpus.Documents){
        
                  AnnotationSet annotSetGenToken =        document.getDocumentIntance().getAnnotations(AnnotationSetToken);
                  AnnotationSet annotTokenSet = annotSetGenToken.get("Token");
                    List<Annotation> annotTokenList = new ArrayList<>(annotTokenSet);
                    Collections.sort(annotTokenList, new OffsetComparator());
        
                    for (Annotation AnnotToken : annotTokenList) {

            FeatureMap fm = AnnotToken.getFeatures();

            try {
                // String entitytag = fm.get(this.EntityFeature).toString(); // Get text from an annotation
                Text = (String) fm.get("string").toString();
                SystemVal = (String) fm.get("CONLL").toString();

                line = Text + sep + SystemVal ;
                bf.append(line + "\n");
            } catch (Exception e) {

            }

           
        }
      
        }
        
          
        
        //annotTokenList

        // Tokens
        
        

        // delete if exists
       
        File OutputFile = new File(FormatedFilePath);
        if (OutputFile.exists()) {
            OutputFile.delete();
        }

        PrintWriter writer = new PrintWriter(OutputFile, "utf-8");
        writer.append(bf.toString().trim());
        writer.close();

    
    }
    
}
