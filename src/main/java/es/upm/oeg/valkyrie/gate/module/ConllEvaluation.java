/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

import com.google.common.io.Files;
import es.upm.oeg.valkyrie.gate.GateHandler;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.OffsetComparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;
import org.apache.log4j.Logger;


/**
 *
 * @author pcalleja
 */
public class ConllEvaluation {
    
    
    String Sep= File.separator;
    
    String FileResultPath;
    String FormatedFilePath;
    String ConllEvaluationPath;
    String CONLLDIR;
    
    
    public GateCorpus Corpus;
    
    
     private static Logger logger = Logger.getLogger(ConllEvaluation.class);
     
     
    public ConllEvaluation(String Lang){
    
      
        try{
        
        CONLLDIR=GateHandler.getPluginsHomeDir()+Sep+"CONLL";
        
        
                
                
        File dir= new File(CONLLDIR);
        if(!dir.isDirectory()){throw new ResourceInstantiationException("NO PLUGIN");}
        FileResultPath=CONLLDIR+Sep+"ConllEvaluation.result";
        FormatedFilePath=CONLLDIR+Sep+"ConllFormated.result";
        ConllEvaluationPath=CONLLDIR+Sep+"conlleval";
        
        
        Corpus = new GateCorpus();
       
     
        if(Lang.equals("ES")){
        
            File f1 = new File(CONLLDIR+Sep+"esp.testa");
            File f2 = new File(CONLLDIR+Sep+"esptesta.conll");
            Files.copy(f1, f2);
            Corpus.createCorpusfromFile(f2, "CONLL","ANSI" );//"UTF-8"
            
        
        }
        
        if(Lang.equals("EN")){
            //eng.testa
            File f1 = new File(CONLLDIR+Sep+"eng.testa");
            File f2 = new File(CONLLDIR+Sep+"eng.conll");
            Files.copy(f1, f2);
            Corpus.createCorpusfromFile(f2, "CONLL", "ANSI");
        
        }
    
        } catch (ResourceInstantiationException | IOException ex) {
            logger.error("ANNIE APPLICATION FAILED ON INITIALIZATION",ex);
            
        } 
    }
    
    
    public void executeEvalutaion() throws IOException{
    
        
        
        exportDataForResults("Valkyr");
        Runtime.getRuntime().exec("perl "+this.ConllEvaluationPath+"<"+this.FormatedFilePath+">"+this.FileResultPath);
    
    
    }
    
    
    

    
    
    
    
    public void exportDataForResults(String AnnotationSetEntity) throws FileNotFoundException, UnsupportedEncodingException{
    
    
    
        GateDocument document= this.Corpus.Documents.get(0);
        
        //   logger.debug("BIO FORMATTING TAG +"+this.InputAnnotation+" in "+ this.document.getName());

        // Get the annotation Set Group general
        //Original markups
        AnnotationSet annotSetGenToken = document.getDocumentIntance().getAnnotations("Original markups");
        AnnotationSet annotSetGenEntityGold = document.getDocumentIntance().getAnnotations("Original markups");
        AnnotationSet annotSetGenEntity = document.getDocumentIntance().getAnnotations(AnnotationSetEntity);
        

        // Entity
        AnnotationSet annotEntitySet = annotSetGenEntity.get("ORG");
        List<Annotation> annotEntityList = new ArrayList<>(annotEntitySet);
        Collections.sort(annotEntityList, new OffsetComparator());
        
        
         // Entity -GOLD
        AnnotationSet annotEntityGoldSet = annotSetGenEntityGold.get("ORG");
        List<Annotation> annotEntityGoldList = new ArrayList<>(annotEntityGoldSet);
        Collections.sort(annotEntityGoldList, new OffsetComparator());

        
        // Token
        AnnotationSet annotTokenSet = annotSetGenToken.get("Token");
        List<Annotation> annotTokenList = new ArrayList<>(annotTokenSet);
        Collections.sort(annotTokenList, new OffsetComparator());

        
        // Each token with feature = O
        for (Annotation Token : annotTokenList) {
            Token.getFeatures().put("CONLL-Gold", "O");
            Token.getFeatures().put("CONLL", "O");
        }

        
        
        // Annotate entities -gold
        for (Annotation Entity : annotSetGenEntityGold) {
            AnnotationSet TokensInEntity = annotTokenSet.get(Entity.getStartNode().getOffset(), Entity.getEndNode().getOffset());
            List<Annotation> TokensInEntityList = new ArrayList<>(TokensInEntity);
            Collections.sort(TokensInEntityList, new OffsetComparator());
            int i = 0;

            for (Annotation Token : TokensInEntityList) {

                if (i == 0) {
                    Token.getFeatures().remove("CONLL-Gold");
                    Token.getFeatures().put("CONLL-Gold", "B-ORG");
                    i++;
                    
                } else {
                    Token.getFeatures().remove("CONLL-Gold");
                    Token.getFeatures().put("CONLL-Gold", "I-ORG");
                }

                
            }
        }
        
         // Annotate entities
        for (Annotation Entity : annotSetGenEntity) {
            AnnotationSet TokensInEntity = annotTokenSet.get(Entity.getStartNode().getOffset(), Entity.getEndNode().getOffset());
            List<Annotation> TokensInEntityList = new ArrayList<>(TokensInEntity);
            Collections.sort(TokensInEntityList, new OffsetComparator());
            int i = 0;

            for (Annotation Token : TokensInEntityList) {

                if (i == 0) {
                    Token.getFeatures().remove("CONLL");
                    Token.getFeatures().put("CONLL", "B-ORG");
                    i++;
                    
                } else {
                    Token.getFeatures().remove("CONLL");
                    Token.getFeatures().put("CONLL", "I-ORG");
                }

                
            }
        }
        
        
                /////// WRITE
        StringBuffer bf = new StringBuffer();
        //annotTokenList

        // Tokens
        String line = "", Text, GoldVal, SystemVal;
        String sep = " ";
        for (Annotation AnnotToken : annotEntityList) {

            FeatureMap fm = AnnotToken.getFeatures();

            try {
                // String entitytag = fm.get(this.EntityFeature).toString(); // Get text from an annotation
                Text = (String) fm.get("string").toString();
                GoldVal = (String) fm.get("CONLL-Gold").toString();
                SystemVal = (String) fm.get("CONLL").toString();

                line = Text + sep + SystemVal + sep + GoldVal;
            } catch (Exception e) {

            }

            bf.append(line + "\n");
        }
      

        // delete if exists
       
        File OutputFile = new File(this.FormatedFilePath);
        if (OutputFile.exists()) {
            OutputFile.delete();
        }

        PrintWriter writer = new PrintWriter(OutputFile, "utf-8");
        writer.append(bf.toString().trim());
        writer.close();

    }
    
    /*
    public List <String> transformDocument2FormattedList(GateDocument doc,String EntityFeature){
    
        corpus.
        
        
        List <String> ListSentencesConverted= new ArrayList();
        AnnotationSet AnnotSetGen = doc.getAnnotations();
        
        AnnotationSet AnnotSentence= AnnotSetGen.get("Sentence");
       
        List <Annotation> AnnotSentenceList= AnnotSentence.inDocumentOrder();
        
        for(Annotation annotSen: AnnotSentenceList){
            
            AnnotationSet AnnotToken= AnnotSetGen.get("Token", annotSen.getStartNode().getOffset(),annotSen.getEndNode().getOffset());
            List <Annotation> AnnotTokenList= AnnotToken.inDocumentOrder();
            
            ListSentencesConverted.add(transformSentence2FormattedList(AnnotTokenList,EntityFeature));
        }
        

        return ListSentencesConverted;
        
    }
    
    public String transformSentence2FormattedList(List <Annotation> SentenceTokens,String EntityFeature){

            StringBuffer bf = new StringBuffer();

            for (Annotation annotTok: SentenceTokens){
                String line=convertTokenFormat(annotTok, EntityFeature);
                bf.append(line+"\n");
            }
        return bf.toString();
        
    }
    
    
    private String convertTokenFormat(Annotation Token,String EntityFeature){
    
    
      
        return createString(Token,EntityFeature);
    }
    */
    
    public String createString(Annotation TokenAnnotation,String EntityFeature){
    
        String string= TokenAnnotation.getFeatures().get("string").toString();
        //String length=  TokenAnnotation.getFeatures().get("length").toString();
        //String kind= TokenAnnotation.getFeatures().get("kind").toString();
        
        
        String Entity;
        Entity= TokenAnnotation.getFeatures().get(EntityFeature).toString();
        /*
        try{
          Entity= TokenAnnotation.getFeatures().get(EntityFeature).toString();
          }catch (Exception e){
          Entity="O";
          }      
         */       
        
        
        String TokenFinal= string;
       
        
        TokenFinal=TokenFinal.concat("\t"+ Entity);

        return TokenFinal;
    
    }
    
    
    
}
