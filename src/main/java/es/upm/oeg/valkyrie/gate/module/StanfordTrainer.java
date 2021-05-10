/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

//import edu.stanford.nlp.ie.crf.CRFClassifier;
//import org.upm.oeg.valkyr.ie.gate.module.BIOFormatter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;

/**
 *
 * @author pcalleja
 */
public class StanfordTrainer {
    
/*
    
    public static void createNERModule(String Name,GateCorpus Corpus, String TokensSet, String EntitySet ,List <String> Entities){
    
        String propertiesFileName=Name+".prop";
        String tsvFileName="Input"+Name+".tsv";
        String modelFileName= "ner-model."+Name+".gz";
        
        try {
            
            String Path=GateHandler.getPluginURL("Stanford_CoreNLP").toURI().getPath()+"resources/"+"ner";
            Path=Path.substring(1);
            System.out.println(Path);
            String PathPropertiesFile=Path+File.separator+propertiesFileName;
            
            
            
            cleandir(Path, tsvFileName,   modelFileName, propertiesFileName);
            createProps(Path, PathPropertiesFile,  tsvFileName,  modelFileName);
         
            BIOFormatter bio = new BIOFormatter();
            bio.AnnotationSetEntity = EntitySet;
            bio.AnnotationSetToken  = TokensSet;
            bio.annotateCorpus(Corpus, Entities);
            bio.exportDataStanfordFormat(Corpus, Path+File.separator+tsvFileName);
            
           
            System.out.println("--------> Launch exec");
            
             String [] ar= {"-prop",PathPropertiesFile};
             CRFClassifier.main(ar);

            //Runtime.getRuntime().exec("java -cp "+Path+File.separator+"stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop "+PathPropertiesFile);
            //System.out.println("java -cp "+Path+File.separator+"stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop "+PathPropertiesFile);
            System.out.println("---------->> End exec");
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(StanfordTrainer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            System.out.println(ex.toString());
        } 
        
        
    
    }
    
    public static void cleandir(String path, String tsvFileName, String modelFileName, String propertiesFileName){
    
        File f1= new File(path+File.separator+tsvFileName);
        if(f1.exists()){f1.delete();}
        File f2= new File(path+File.separator+modelFileName);
        if(f2.exists()){f2.delete();}
        File f3= new File(path+File.separator+propertiesFileName);
        if(f3.exists()){f3.delete();}
    
    }
    
    
       public static void createProps(String path, String PropertiesFile, String InputFile, String Model) throws UnsupportedEncodingException, FileNotFoundException {

        String s = "#location of the training file\n"
                + "trainFile = " + path + "/"+InputFile+"\n" //INputFile.tsv
                + "#location where you would like to save (serialize to) your\n"
                + "#classifier; adding .gz at the end automatically gzips the file,\n"
                + "#making it faster and smaller\n"
                + "serializeTo = " + path + "/"+Model+"\n" //ner-model.ser.gz
                + "\n"
                + "#structure of your training file; this tells the classifier\n"
                + "#that the word is in column 0 and the correct answer is in\n"
                + "#column 1\n"
                + "map = word=0,answer=1\n"
                + "\n"
                + "#these are the features we'd like to train with\n"
                + "#some are discussed below, the rest can be\n"
                + "#understood by looking at NERFeatureFactory\n"
                + "useClassFeature=true\n"
                + "useWord=true\n"
                + "useNGrams=true\n"
                + "#no ngrams will be included that do not contain either the\n"
                + "#beginning or end of the word\n"
                + "noMidNGrams=true\n"
                + "useDisjunctive=true\n"
                + "maxNGramLeng=6\n"
                + "usePrev=true\n"
                + "useNext=true\n"
                + "useSequences=true\n"
                + "usePrevSequences=true\n"
                + "maxLeft=1\n"
                + "#the next 4 deal with word shape features\n"
                + "useTypeSeqs=true\n"
                + "useTypeSeqs2=true\n"
                + "useTypeySequences=true\n"
                + "wordShape=chris2useLC";

        

        PrintWriter writer = new PrintWriter( PropertiesFile, "utf-8"); //"properties.prop"
        writer.append(s.trim());
        writer.close();

    }
*/

    
    
    
}
