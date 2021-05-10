/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.module;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.DefaultGazetteer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public class GoldEnricher {
    
    static final Logger logger = Logger.getLogger(GoldEnricher.class.getName()); 
    
    
    private URL GazetteerFolderURL;

    
    public URL getGazetteerFolderURL() {
        return GazetteerFolderURL;
    }

    public void setGazetteerFolderURL(URL GazetteerFolderURL) {
        this.GazetteerFolderURL = GazetteerFolderURL;
    }
    
    public void enrichCorpus(GateCorpus Corpus,String EntityType,File InputFile) {
    
       
        
            try {
                 for(Document doc: Corpus.getCorpus()){
                List <String> Entities = readGazetteerEntries(InputFile,doc.getName());
                enrichDocument(doc,EntityType,Entities);
                 }
            } catch (FileNotFoundException | UnsupportedEncodingException | ExecutionException ex) {
                logger.error(ex);
            } catch (IOException ex) {
            logger.error(ex);
            }
            
       
    
         
    
    }
    
    private List <String> readGazetteerEntries(File InputFile,String docName) throws UnsupportedEncodingException, IOException{
    
        
        //logger.info("Complete GoldFile found");
        List<String> List = new ArrayList();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(InputFile), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            if (str.length() > 1) {

                String docColumn = str.split("\t")[0];
                if (docColumn.equals(docName)) {
                    List.add(str.split("\t")[1]);
                }
            }
        }

        in.close();
        return List;
    
    }
    
    
    private void enrichDocument(Document doc, String EntityType,List <String> Entities) throws FileNotFoundException, UnsupportedEncodingException, ExecutionException{
    
        
        try {
            
            File FileList = new File("GoldEnricher.lst");
            File FileGazetteer = new File("GoldEnricher.def");
            
                
            PrintWriter writer = new PrintWriter(FileList, "UTF-8");
            for(String s:Entities){
            writer.println(s);
            }
            
            writer.close();
            
            PrintWriter writerDef = new PrintWriter(FileGazetteer, "UTF-8");
            writerDef.println("GoldEnricher.lst:Entity:Entity:es:"+EntityType);
            writerDef.close();
            
            
            FeatureMap featureMap= Factory.newFeatureMap();
            featureMap.put("encoding", "UTF-8");
            featureMap.put("caseSensitive", true);
            featureMap.put("gazetteerFeatureSeparator", "$");
            featureMap.put("annotationSetName",null);
            featureMap.put("longestMatchOnly",Boolean.TRUE);
            featureMap.put("listsURL",FileGazetteer.toURI().toURL());    
            DefaultGazetteer GazetteerModule = (DefaultGazetteer) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer",featureMap);
            GazetteerModule.init();
            
            GazetteerModule.setDocument(doc);
            GazetteerModule.execute();
         
            
            AnnotationSet annotSetGen= doc.getAnnotations("");
            AnnotationSet annotTarger=annotSetGen.get(EntityType);
            
            for(Annotation annot: annotTarger){
                createGoldStandardLine(doc,annot,EntityType);
            }
            
            //  Remove
            Factory.deleteResource(GazetteerModule);
            Gate.getCreoleRegister().remove(GazetteerModule);
            FileList.delete();
            FileGazetteer.delete();
            
                    
            
           
        } catch (ResourceInstantiationException | MalformedURLException ex) {
            logger.error(ex);
        } 
    
    }
    
    private String createGoldStandardLine(Document doc,Annotation Annot, String  OutputAnnotationType) {

        String line, Start, End, Text;
        String tab = "\t";

        Start = Annot.getStartNode().getOffset().toString();
        End = Annot.getEndNode().getOffset().toString();
        // Get text from an annotation
        Text = gate.Utils.cleanStringFor(doc, Annot).replaceAll("\n", " ");
        line = doc.getName() + tab + Start + tab + End + tab + Text + tab + OutputAnnotationType;
        System.out.println(line);
        //line = line + "\n";
        return line;
    }
    
    
}
