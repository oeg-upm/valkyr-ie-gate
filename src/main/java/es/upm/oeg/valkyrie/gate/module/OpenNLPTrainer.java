/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
import org.upm.oeg.valkyr.ie.gate.lr.GateCorpus;
import org.upm.oeg.valkyr.ie.gate.lr.GateDocument;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.upm.oeg.valkyr.ie.gate.GateHandler;
*/
/**
 *
 * @author pcalleja
 */
public class OpenNLPTrainer {
    
    
    /*
    public static void createNERModule(String Name,GateCorpus Corpus, String TokensSet, String EntitySet ,String EntityType){
    
      
        String txtFileName="Input"+Name+".txt";
        String modelFileName= "ner-model."+Name+".bin";
 
        
        try {
            
            String Path=GateHandler.getPluginURL("OpenNLP").toURI().getPath()+"models"+File.separator+"ner";
            Path=Path.substring(1);
           
            
            
            
            //  String PathPropertiesFile=Path+File.separator+propertiesFileName;
            //cleandir(Path, txtFileName,   modelFileName, propertiesFileName);

        
            
            ApacheOpenNLPFormatter formtatter= new ApacheOpenNLPFormatter();
            formtatter.AnnotationSetEntity = EntitySet;
            formtatter.AnnotationSetToken  = TokensSet;
            formtatter.exportDataStanfordFormat(Corpus,  Path+File.separator+txtFileName, EntityType); 
           
            
            createModel(Path+File.separator+txtFileName,Path+File.separator+modelFileName);
            
            
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
    
    
      
       
       
    public static void createModel(String InputTrainingFile,String NERModelFile) {
 
        // reading training data
        InputStreamFactory in = null;
        try {
            in = new MarkableFileInputStreamFactory(new File(InputTrainingFile)); //"resources/AnnotatedSentences.txt"
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        
        ObjectStream sampleStream = null;
        try {
            sampleStream = new NameSampleDataStream(
                new PlainTextByLineStream(in, StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
 
        // setting the parameters for training
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 370); //70
        params.put(TrainingParameters.CUTOFF_PARAM, 1);
        params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT"); // PERCEPTRON
 
        // training the model using TokenNameFinderModel class 
        TokenNameFinderModel nameFinderModel = null;
        try {
            nameFinderModel = NameFinderME.train("en", null, sampleStream,
                params, TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // saving the model to "ner-custom-model.bin" file
        try {
            File output = new File(NERModelFile); //"ner-custom-model.bin"
            FileOutputStream outputStream = new FileOutputStream(output);
            nameFinderModel.serialize(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
      
        
    }
 */

}
