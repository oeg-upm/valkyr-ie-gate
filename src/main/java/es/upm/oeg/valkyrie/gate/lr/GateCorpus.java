/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.lr;

import es.upm.oeg.valkyrie.gate.evaluation.EvaluationResult;
import es.upm.oeg.valkyrie.gate.evaluation.NEREvaluation;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.Utils;
import gate.annotation.AnnotationSetImpl;
import gate.creole.AbstractResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.util.InvalidOffsetException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public class GateCorpus {
    
    public String CorpusName;
    private Corpus Corpus;
    public List <GateDocument> Documents;

    static Logger logger = Logger.getLogger(GateCorpus.class);
    
    
    
    
    public Corpus createCorpus(String name) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Factory.newCorpus(name);
        Documents= new ArrayList();
        String s= "for testing";
        return Corpus;
    }
    
    
    public Corpus createCorpusFromDatastore(Corpus Corp) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Corp;
        Documents= new ArrayList();
        
        Iterator it= Corpus.iterator();
        
        while (it.hasNext()){
        
            Document doc= (Document) it.next();
            Documents.add(new GateDocument(doc));
        }    
        
        
        return Corpus;
    }
    

    public Corpus createCorpusfromFolder(String folder, String name, String Encoding) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Factory.newCorpus(name);
        Documents= new ArrayList();
        
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            
            throw new ResourceInstantiationException("Bad directory :"+folder +"  "+dir.getAbsolutePath());
        }

        File[] listFiles = dir.listFiles();

        for (File f : listFiles) {

            if(!f.getName().endsWith("zip")){
               GateDocument doc = new GateDocument(f,Encoding);
               Documents.add(doc);
               Corpus.add(doc.getDocumentIntance());
            }
        }
        return Corpus;

    }
    
       
    public void saveCorpusInDatastore(SerialDataStore sds) throws PersistenceException{
    
       
        for(GateDocument doc: this.Documents){
            doc.saveDocumentInDatastore(sds);
        
        }
        Corpus persistDoc = (Corpus)sds.adopt(this.Corpus);
        sds.sync(persistDoc);
    
   
    
    }
    
    
    public Corpus createCorpusfromFolderWithFilter(String folder, String name, String Encoding, HashSet Filter) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Factory.newCorpus(name);
        Documents= new ArrayList();
        
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            
            throw new ResourceInstantiationException("Bad directory :"+folder +"  "+dir.getAbsolutePath());
        }

        File[] listFiles = dir.listFiles();

        for (File f : listFiles) {

            if(Filter.contains(f.getName())){
                continue;
            }
            if(!f.getName().endsWith("zip")){
               GateDocument doc = new GateDocument(f,Encoding);
               Documents.add(doc);
               Corpus.add(doc.getDocumentIntance());
            }
        }
        return Corpus;

    }
    
    public Corpus createCorpusfromFolder(String folder, String name, String Encoding, int limit) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Factory.newCorpus(name);
        Documents= new ArrayList();
        
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            
            throw new ResourceInstantiationException("Bad directory :"+folder +"  "+dir.getAbsolutePath());
        }

        File[] listFiles = dir.listFiles();

        int counter=0;
        for (File f : listFiles) {

            if(!f.getName().endsWith("zip")){
               GateDocument doc = new GateDocument(f,Encoding);
               Documents.add(doc);
               Corpus.add(doc.getDocumentIntance());
               counter++;
               if (counter==limit){break;};
            }
        }
        return Corpus;

    }
  
    public Corpus createCorpusfromFolder(String folder,String format, String name, String Encoding) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Factory.newCorpus(name);
        Documents= new ArrayList();
        
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            
            throw new ResourceInstantiationException("Bad directory :"+folder +"  "+dir.getAbsolutePath());
        }

        File[] listFiles = dir.listFiles();

        for (File f : listFiles) {

            if(f.getName().endsWith(format)){
               GateDocument doc = new GateDocument(f,Encoding);
               Documents.add(doc);
               Corpus.add(doc.getDocumentIntance());
            }
        }
        return Corpus;

    }
    
    public Corpus createCorpusfromFileList(List<File> files, String name, String Encoding) throws ResourceInstantiationException, MalformedURLException {

        Corpus = Factory.newCorpus(name);
        Documents= new ArrayList();
        

        for (File f : files) {

            GateDocument doc = new GateDocument(f,Encoding);
            Documents.add(doc);
            Corpus.add(doc.getDocumentIntance());

        }
        return Corpus;
    }
    
    public Corpus createCorpusfromFile(File file, String name, String Encoding) throws ResourceInstantiationException, MalformedURLException {

            Corpus = Factory.newCorpus(name);

            Documents= new ArrayList();
            GateDocument doc = new GateDocument(file,Encoding);
            Documents.add(doc);
            
            Corpus.add(doc.getDocumentIntance());
            
        return Corpus;
    }
    
    
    
    
    
     public void addDocument(GateDocument doc){
    
        this.Documents.add(doc);
        this.Corpus.add(doc.getDocumentIntance());
    
    }
     public void addDocument(File file, String Encoding)  throws ResourceInstantiationException, MalformedURLException{
    
         GateDocument doc = new GateDocument(file,Encoding);
            Documents.add(doc);
            
            Corpus.add(doc.getDocumentIntance());
    
    }
     
     

    public Corpus getCorpus() {
        return Corpus;
    }

    public void setCorpus(Corpus Corpus) {
        this.Corpus = Corpus;
    }
 
    public void deleteCorpus(){
    
        // INVERSE CLEANER 
        Factory.deleteResource(Corpus);
        Corpus=null;
        AbstractResource.flushBeanInfoCache();
    }
    
    
    public void deleteCorpusComplete(){
    
        // INVERSE CLEANER 
        for (int i= this.Corpus.size()-1;i>=0 ;i--) {

            // logger.debug("Cleaning document: "+this.DocumentName);
            // Clean annotation set
            AnnotationSetImpl set = (AnnotationSetImpl) Corpus.get(i).getAnnotations();
            set.clear();
         
            Factory.deleteResource(Corpus.get(i));
        }

        Factory.deleteResource(Corpus);
        Corpus=null;
        AbstractResource.flushBeanInfoCache();
    }
    
    public void cleanCorpus(){
    
        for(GateDocument doc: this.Documents){
            
            doc.cleanDocument();
        }
    
    }
    
    public void clearAnnotations(String AnnotationSet){
    
        for(GateDocument doc: this.Documents){
            
            doc.cleanAnnotations(AnnotationSet);
        }
    
    }
    
    public void cleanAnnotations(String AnnotationSetName, String ... AnnotationsToKeep) {
    
        for(GateDocument doc: this.Documents){
        
            doc.cleanAnnotations(AnnotationSetName,AnnotationsToKeep);
        }
    
    }
    
    public void copyAnnotationSet(String AnnotationSetName, String NewAnnotationSet) {
    
        for(GateDocument doc: this.Documents){
        
            doc.copyAnnotationSet(AnnotationSetName,NewAnnotationSet);
        }
    
    }
    
    public void copyAnnotation(String AnnotationSet, String AnnotationType, String NewAnnotationType) {
    
        for(GateDocument doc: this.Documents){
        
            doc.copyAnnotation(AnnotationSet,AnnotationType,NewAnnotationType);
        }
    
    }
    public void copyAnnotation(String AnnotationSet, String AnnotationType, String NewAnnotationSet, String NewAnnotationType) {
    
        for(GateDocument doc: this.Documents){
        
            doc.copyAnnotation(AnnotationSet,AnnotationType,NewAnnotationSet,NewAnnotationType);
        }
    
    }
    
    public void mergeAnnotationSet(String AnnotationSetName, String TargetAnnotationSet) {
    
        for(GateDocument doc: this.Documents){
        
            doc.mergeAnnotationSet(AnnotationSetName,TargetAnnotationSet);
        }
    
    }
    
  
    
     public int countAnnotationType(String annotationType, String annotationSet){
    
         int counter=0;
     
         for(Document doc: this.Corpus){
             
             counter = counter +doc.getAnnotations(annotationSet).get(annotationType).size();     
         }
         return counter;
    }
     
    public List<Annotation> getAnnotations(String annotationType, String annotationSet){
    
        List <Annotation> ListAnnotationSet= new ArrayList();
        
         for(GateDocument doc: this.Documents){
             
             ListAnnotationSet.addAll(doc.getAnnotations(annotationType, annotationSet));
         }
                 
         return ListAnnotationSet;
    }    

    
    ///////////////////////////// LOAD GOLD STANDARD
   
    public void loadGoldStandardFromFile(File GoldFile, String annotationType, String annotationSet) {

        logger.info("Reading Gold standard: " + GoldFile.getName()+" in Corpus "+this.Corpus.getName());
        for (Document doc : this.Corpus) {

            try {

                AnnotationSet annotSetGen = doc.getAnnotations(annotationSet);

                // Read the gold standard file and get the list of NamedEntities 
                List<String> listGold = null;

                listGold = readGoldStandardCompleteFile(doc.getName(), GoldFile);

                if(listGold.isEmpty()){
                logger.info("There is no annotations for "+ doc.getName() + " in "+GoldFile.getName());
                }
                for (String GoldString : listGold) {

                    createAnnotationFromGoldStandard(GoldString, annotSetGen, doc);

                }

            } catch (IOException | InvalidOffsetException ex) {
                logger.error(ex);
            }

        }
        logger.info("Gold standard loaded");

    }
    
    
    public void loadStandoffAnnotation(String StandoffFolder, String FileType, String annotationType, String annotationSet) {

        logger.info("Reading Gold standard standoff format: " + StandoffFolder+" in Corpus "+this.Corpus.getName());
        for (Document doc : this.Corpus) {
            
            System.out.println(doc.getName());
            String id = doc.getName().split("\\.")[0];
            String goldFileName=id+"."+FileType;

            try {

                AnnotationSet annotSetGen = doc.getAnnotations(annotationSet);

                File goldFile= new File(StandoffFolder+File.separator+goldFileName);
                if(!goldFile.exists()){
                logger.info("No file for "+id);
                continue;
                }
                
                // Read the gold standard file and get the list of NamedEntities 
                List<String> listGold = readLines(goldFile);

               

                if(listGold.isEmpty()){
                logger.info("There is no annotations for "+ doc.getName() + " in "+goldFile.getName());
                continue;
                }
                
                for (String GoldString : listGold) {
                    createStandoffAnnotation(GoldString, annotSetGen, doc);
                }

            } catch (IOException | InvalidOffsetException ex) {
                logger.error(ex);
            }

        }
        logger.info("Gold standard loaded");

    }
    
    
     private List<String> readLines( File GoldFile) throws UnsupportedEncodingException, FileNotFoundException, IOException {

        //logger.info("Complete GoldFile found");
        List<String> List = new ArrayList();

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(GoldFile), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            if (str.length() > 1) {
                    List.add(str);
            }
        }

        in.close();
        return List;
    }   
    
      private List<String> readGoldStandardCompleteFile(String docName, File GoldFile) throws UnsupportedEncodingException, FileNotFoundException, IOException {

        //logger.info("Complete GoldFile found");
        List<String> List = new ArrayList();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(GoldFile), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            if (str.length() > 1) {

                String docColumn = str.split("\t")[0];
                if (docColumn.equals(docName)) {
                    List.add(str);
                }
            }
        }

        in.close();
        return List;
    }
    
    
    private void createAnnotationFromGoldStandard(String GoldStringLine, AnnotationSet annotSetGen, Document document) throws InvalidOffsetException {

        String[] GoldEntity = GoldStringLine.split("\t");   // id[0] - start[1] - end[2] - text[3] - type[4]
        if (GoldEntity.length < 5) {
            logger.error("Bad format line: Length=" + GoldEntity.length + "in gold file " + GoldEntity[0] + " ");
            return;
        }

        FeatureMap fm = Factory.newFeatureMap();
        fm.put("DocId", GoldEntity[0]);
        Long start = Long.parseLong(GoldEntity[1]);
        Long end = Long.parseLong(GoldEntity[2]);
        fm.put("string", GoldEntity[3]);
        fm.put("type", GoldEntity[4]);

        Integer id = annotSetGen.add(
                start, end,
                GoldEntity[4], // MODIFICATION
                fm);

        String TextInDocument = gate.Utils.stringFor(document, start, end).replaceAll("\n", " ");

        if (!TextInDocument.equals(GoldEntity[3])) {
            
            if(TextInDocument.replaceAll("  ", " ").equals(GoldEntity[3])){
            
            
            }else{
                
                logger.error("The entity string for '" +TextInDocument+"' doesn't match in document for gold '" + GoldEntity[3]+ "' in " + document.getName()+" "+start+" "+end);
           
                System.out.println(GoldEntity[0]+"\t"+(start)+"\t"+(end)+"\t"+GoldEntity[3]+"\t"+GoldEntity[4]);
            
            }
            
        }

    }
     
    
    private void createStandoffAnnotation(String GoldStringLine, AnnotationSet annotSetGen, Document document) throws InvalidOffsetException {

        String[] GoldEntity = GoldStringLine.split("\t");   // id[0]- \t type-start-end [1] \t- text[2]
        
        if (GoldEntity.length < 3) {
            logger.error("Bad format line: Length=" + GoldEntity.length + "in gold file " + GoldEntity[0] + " ");
            return;
        }
        
        String [] offsets= GoldEntity[1].split(" ");
        
        String ide= GoldEntity[0];
        String type = offsets[0];
        String val= GoldEntity[2];
        Long start = Long.parseLong(offsets[1]);
        Long end = Long.parseLong(offsets[2]);
        
        FeatureMap fm = Factory.newFeatureMap();
        fm.put("Ide", ide);
        fm.put("string", val);
       // fm.put("type", GoldEntity[1]);

        Integer id = annotSetGen.add(
                start, end,
                type, // MODIFICATION
                fm);

        String TextInDocument = gate.Utils.cleanStringFor(document, start, end).replaceAll("\n", " ");

        if (!TextInDocument.equals(val)) {
            logger.error("The entity string for '" + TextInDocument + "' doesn't match in document for gold '" + val + "' in " + document.getName() + " " + start + " " + end);
        }

    }
     
     
     
    
    ///////////////////////////// STORE GOLD STANDARD
    
    public void storeGoldStandardInFile(File GoldFile, String InputAnnotationType, String OutputAnnotationType, String AnnotationSet, boolean Append) {

        try {

            for (Document document : Corpus) {
                // Set
                AnnotationSet annotSetGen = document.getAnnotations(AnnotationSet);
                // Entity
                AnnotationSet annotEntitySet = annotSetGen.get(InputAnnotationType);
                List<Annotation> annotEntityList = annotEntitySet.inDocumentOrder();

                // Gold standard ouput
                StringBuffer bf = new StringBuffer();
                for (Annotation Annot : annotEntityList) {
                    bf.append(createGoldStandardLine(document, Annot, OutputAnnotationType));
                }

                // Write
                writeGoldStandardFile(GoldFile,bf,Append);
            }

        } catch (FileNotFoundException ex) {
            logger.error(ex);
        }

    }

    
    
    public List <String> getGoldStandard( String InputAnnotationType, String OutputAnnotationType, String AnnotationSet) {

        
            List <String> list=new ArrayList();
            for (Document document : Corpus) {
                // Set
                AnnotationSet annotSetGen = document.getAnnotations(AnnotationSet);
                // Entity
                AnnotationSet annotEntitySet = annotSetGen.get(InputAnnotationType);
                List<Annotation> annotEntityList = annotEntitySet.inDocumentOrder();

                // Gold standard ouput
                StringBuffer bf = new StringBuffer();
                for (Annotation Annot : annotEntityList) {
                    list.add(createGoldStandardLine(document, Annot, OutputAnnotationType));
                }

                // Write
                //writeGoldStandardFile(GoldFile,bf,Append);
            }

        return list;

    }

    
     private void writeGoldStandardFile(File GoldFile, StringBuffer Bf, boolean Append ) throws FileNotFoundException{
    
         // WRITE IN :
         FileOutputStream OutputStream = null;

         // MODE Append = true, no delete
         OutputStream = new FileOutputStream(GoldFile, Append);

         // Print
         PrintWriter writer = new PrintWriter(new OutputStreamWriter(OutputStream, StandardCharsets.UTF_8));
         writer.append(Bf.toString());
         writer.close();

    }

    
    private String createGoldStandardLine(Document doc,Annotation Annot, String  OutputAnnotationType) {

        String line, Start, End, Text;
        String tab = "\t";

        Start = Annot.getStartNode().getOffset().toString();
        End = Annot.getEndNode().getOffset().toString();
        // Get text from an annotation
        Text = gate.Utils.cleanStringFor(doc, Annot).replaceAll("\n", " ");
        line = doc.getName() + tab + Start + tab + End + tab + Text + tab + OutputAnnotationType;
        line = line + "\n";
        return line;
    }
    
    
    
    
   
    
    public EvaluationResult evaluateAnnotation(String EvaluationName,String AnnotationName, String AnnotationSetName, String AnnotationGoldSetName){
    
        NEREvaluation eval= new NEREvaluation(AnnotationSetName,AnnotationGoldSetName);
        EvaluationResult res= eval.evaluateAnnotation(EvaluationName,AnnotationName,this.getCorpus());
        
        return res;
    
    }
    
    
    
    
}
