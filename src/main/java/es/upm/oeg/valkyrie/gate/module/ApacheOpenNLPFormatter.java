/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.Utils;
import gate.util.OffsetComparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;

/**
 *
 * @author pcalleja
 */
public class ApacheOpenNLPFormatter {
    
    private String InputAnnotation;
    private String TokenFeatureBIO;
    
    public String AnnotationSetToken;
    public String AnnotationSetEntity;
    
    
    public void annotateCorpus(GateCorpus Corpus, List<String> Entities) throws FileNotFoundException, UnsupportedEncodingException {

        for (GateDocument document : Corpus.Documents) {

            annotateDocument(document, Entities);

        }

    }

    
    public void annotateDocument(GateDocument document, List<String> Entities) {

        // Get the annotation Set Group general
        AnnotationSet annotSetGenToken = document.getDocumentIntance().getAnnotations(AnnotationSetToken);
        AnnotationSet annotSetGenEntity = document.getDocumentIntance().getAnnotations(AnnotationSetEntity);

        // Token
        AnnotationSet annotTokenSet = annotSetGenToken.get("Token");
        List<Annotation> annotTokenList = new ArrayList<>(annotTokenSet);
        Collections.sort(annotTokenList, new OffsetComparator());

        // Each token with feature = O
        for (Annotation Token : annotTokenList) {
            Token.getFeatures().put("CONLL", "O");
        }

        for (String Entity : Entities) {

            annotateEntityType(annotSetGenEntity, annotTokenSet, Entity);

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
            int length = 0;

            // TAKE THE FIRST ONE
            Annotation TokenFirst=  TokensInEntityList.get(0);
            TokenFirst.getFeatures().remove("CONLL");
            TokenFirst.getFeatures().put("CONLL", "<START:"+EntityType+">"); //<START:person>
            
            // AND THE LAST ONE
            Annotation TokenLast=  TokensInEntityList.get(TokensInEntityList.size()-1);
            TokenLast.getFeatures().remove("CONLL");
            TokenLast.getFeatures().put("CONLL", "<END>"); //<END>
           
        }

    }
    
    
    public void exportDataStanfordFormat(GateCorpus Corpus, String FormatedFilePath, String EntityType)
            throws FileNotFoundException, UnsupportedEncodingException {

        /////// WRITE
        StringBuffer bf = new StringBuffer();

        for (GateDocument document : Corpus.Documents) {

            AnnotationSet annotSetGenApache = document.getDocumentIntance().getAnnotations(AnnotationSetToken);
            AnnotationSet annotSetEvalEntity = document.getDocumentIntance().getAnnotations(this.AnnotationSetEntity);

            AnnotationSet annotSentenceSet = annotSetGenApache.get("Sentence");
            AnnotationSet annotEntitySet = annotSetEvalEntity.get(EntityType);

            List<Annotation> annotSentenceList = new ArrayList<>(annotSentenceSet);
            Collections.sort(annotSentenceList, new OffsetComparator());

            for (Annotation AnnotSentence : annotSentenceList) {

                String res = parseSentence(document.getDocumentIntance(), AnnotSentence, annotEntitySet, EntityType);
                bf.append(res);
            }

        }

        // delete if exists
        File OutputFile = new File(FormatedFilePath);
        if (OutputFile.exists()) {
            OutputFile.delete();
        }

        PrintWriter writer = new PrintWriter(OutputFile, "utf-8");
        writer.append(bf.toString().trim());
        writer.close();

    }

    
    private String parseSentence(Document dcmnt, Annotation Sentence, AnnotationSet annotEntitiesSet, String EntityType) {

        AnnotationSet EntitiesInSentence = annotEntitiesSet.get(Sentence.getStartNode().getOffset(), Sentence.getEndNode().getOffset());
        List<Annotation> EntitiesInEntityList = new ArrayList<>(EntitiesInSentence);
        Collections.sort(EntitiesInEntityList, new OffsetComparator());

        String SentenceStringInit= Utils.cleanStringFor(dcmnt, Sentence);
        String SentenceStringOut="";
        
        String initcode= " <START:" + EntityType.toLowerCase() + "> ";
        String endcode= " <END> ";
        
        //HashMap <String,String> mapEntities= new HashMap();
 
        for (Annotation AnnotEntity : EntitiesInEntityList) {

            try {

                String EntityText = Utils.cleanStringFor(dcmnt, AnnotEntity);
                SentenceStringOut = SentenceStringOut+SentenceStringInit.substring(0, SentenceStringInit.indexOf(EntityText))+initcode + EntityText + endcode;
                SentenceStringInit = SentenceStringInit.substring(SentenceStringInit.indexOf(EntityText)+EntityText.length());
                //mapEntities.put(Text, initcode + Text + " <END> ");
            } catch (Exception e) {

            }

        }
        
        SentenceStringOut= SentenceStringOut+SentenceStringInit;
        return (SentenceStringOut+ "\n");
        
        /*
        for(HashMap.Entry<String, String> entry : mapEntities.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            sentence=sentence.replaceAll(key, value);
            // do what you have to do here
            // In your case, another loop.
        }
        
        sentence=sentence.replaceAll("<START:organization>  <START:organization>", "<START:Organization>");

        sentence=sentence.replaceAll("<END> . <END>", "<END>");
        
         return (sentence + "\n");
     */ 
    }


}
