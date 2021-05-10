/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.lr;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.annotation.AnnotationSetImpl;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import gate.Utils;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public class GateDocument {
    
    
    private String DocumentName;
    private Document DocumentIntance=null;
    
    private File OriginalFile;
    private String Content;
    
    private String Enconding;
    
    //public GateAnnotationSet EvaluationSet;
    
    final static Logger logger = Logger.getLogger(GateDocument.class);

    public GateDocument(String DocName, String DocText, String Encoding ) throws ResourceInstantiationException, MalformedURLException  {
    
       DocumentName=DocName;
       Content=DocText;
       Enconding=Encoding;
       DocumentIntance = Factory.newDocument(Content);
       DocumentIntance.setName(DocumentName);
       
    }
    public GateDocument(String DocName, File File, String Encoding) throws ResourceInstantiationException, MalformedURLException  {
    
       OriginalFile=File;
       DocumentName=DocName;
       Enconding=Encoding;
       DocumentIntance = Factory.newDocument(OriginalFile.toURI().toURL(),Encoding);
       DocumentIntance.setName(DocumentName);
    }  
    
    
     public GateDocument(String DocName, URL Path, String Encoding) throws ResourceInstantiationException, MalformedURLException  {
    
       //OriginalFile=File;
       DocumentName=DocName;
       Enconding=Encoding;
       DocumentIntance = Factory.newDocument(Path,Encoding);
       DocumentIntance.setName(DocumentName);
    }  
    
    
    public GateDocument(File File, String Encoding) throws ResourceInstantiationException, MalformedURLException {
    
       OriginalFile=File;
       DocumentName=File.getName();
       Enconding=Encoding;
       DocumentIntance = Factory.newDocument(OriginalFile.toURI().toURL(),Encoding);
       DocumentIntance.setName(DocumentName);
       
    }
    
    public GateDocument(Document Doc) throws ResourceInstantiationException, MalformedURLException {
    
       //OriginalFile=File;
       DocumentName=Doc.getName();
       Enconding="UTF-8";
       DocumentIntance = Doc;
       
       gate.corpora.DocumentImpl doc= (gate.corpora.DocumentImpl) Doc;
       
       
    }
    
  
    
    public void saveDocumentInDatastore(SerialDataStore sds) throws PersistenceException{
    Document persistDoc = (Document)sds.adopt(this.DocumentIntance);
      sds.sync(persistDoc);
    
    }
    
     public GateDocument(Document doc, String Encoding ) throws ResourceInstantiationException, MalformedURLException  {
    
       DocumentName=doc.getName();
      
       Enconding=Encoding;
       DocumentIntance = doc;
    }

    public void createGateDocument2() throws ResourceInstantiationException, MalformedURLException {

        if (OriginalFile != null) {
            DocumentIntance = Factory.newDocument(OriginalFile.toURI().toURL());
            DocumentIntance.setName(DocumentName);
        } else {
            DocumentIntance = Factory.newDocument(Content);
            DocumentIntance.setName(DocumentName);
        }
    }

    public void cleanDocument() {

        logger.debug("Cleaning document: "+this.DocumentName);
        // Clean annotation set
        AnnotationSetImpl set = (AnnotationSetImpl) getDocumentIntance().getAnnotations();
        set.clear();
       
        // Clean the document. Remove
        Factory.deleteResource(DocumentIntance);
        DocumentIntance = null;
    }
    
    public void cleanAnnotations(String AnnotationSetName) {

        logger.debug("Cleaning document: "+this.DocumentName);
        // Clean annotation set
        AnnotationSetImpl set = (AnnotationSetImpl) getDocumentIntance().getAnnotations(AnnotationSetName);
        
        set.clear();
    }
    
    public void cleanAnnotations(String AnnotationSetName, String ... AnnotationsToKeep) {
      
        
        
        logger.debug("Cleaning document: "+this.DocumentName);
        
        HashSet<String> SetTypesToKeep = new HashSet<String>(Arrays.asList(AnnotationsToKeep));
        AnnotationSetImpl annotationSet = (AnnotationSetImpl) getDocumentIntance().getAnnotations(AnnotationSetName);
        
        List <String> list= new ArrayList(annotationSet.getAllTypes());
        for(String annotationtype:list){
           
            if(!SetTypesToKeep.contains(annotationtype)){   
                 annotationSet.removeAll(annotationSet.get(annotationtype));
                 
            }
        }
    }
    
    
    
    public void copyAnnotationSet(String AnnotationSetName, String NewAnnotationSet) {
      
        
        
        AnnotationSetImpl annotationSet = (AnnotationSetImpl) getDocumentIntance().getAnnotations(AnnotationSetName);
        AnnotationSetImpl annotationSetTarget = (AnnotationSetImpl) getDocumentIntance().getAnnotations(NewAnnotationSet);
        annotationSetTarget.clear();
        
        annotationSetTarget.addAll(annotationSet);
        
       
    }
    
    public void copyAnnotation(String AnnotationSet, String AnnotationType, String NewAnnotationType) {
      
        
        
        AnnotationSetImpl annotationSet = (AnnotationSetImpl) getDocumentIntance().getAnnotations(AnnotationSet);
        AnnotationSet set= annotationSet.get(AnnotationType);
        
        for(Annotation annot: set){
        
            annotationSet.add(annot.getStartNode(), annot.getEndNode(), NewAnnotationType, annot.getFeatures());
        }
        
        
       
    }
    
    public void copyAnnotation(String AnnotationSet, String AnnotationType, String NewAnnotationSet, String NewAnnotationType) {
      
        
        
        AnnotationSetImpl annotationSetSource = (AnnotationSetImpl) getDocumentIntance().getAnnotations(AnnotationSet);
        AnnotationSet SetOfAnnotations= annotationSetSource.get(AnnotationType);
        
        AnnotationSetImpl annotationSetTarget = (AnnotationSetImpl) getDocumentIntance().getAnnotations(NewAnnotationSet);
        
        for(Annotation annot: SetOfAnnotations){
        
            annotationSetTarget.add(annot.getStartNode(), annot.getEndNode(), NewAnnotationType, annot.getFeatures());
        }
        
        
       
    }
    
    public void mergeAnnotationSet(String AnnotationSetName, String TargetAnnotationSet) {
      
        
        
        AnnotationSetImpl annotationSet = (AnnotationSetImpl) getDocumentIntance().getAnnotations(AnnotationSetName);
        AnnotationSetImpl annotationSetTarget = (AnnotationSetImpl) getDocumentIntance().getAnnotations(TargetAnnotationSet);
       
        
        annotationSetTarget.addAll(annotationSet);
        
       
    }
    

    public File getOriginalFile() {
        return OriginalFile;
    }

    public void setOriginalFile(File OriginalFile) {
        this.OriginalFile = OriginalFile;
    }
    
    
    public Document getDocumentIntance() {
        return DocumentIntance;
    }
    
    public String getDocumentName() {
        return DocumentName;
    }
    
    /*
    public void storeAnnotations(String annotationType, String annotationSet){
    
   
        AnnotationSet AnnotSet = this.DocumentIntance.getAnnotations(annotationSet).get(annotationType);
        this.EvaluationSet=new GateAnnotationSet("Eval");
        
        for(Annotation annot: AnnotSet){
            this.EvaluationSet.AnnotationSet.add(annot);
        }
    }
*/
    /*
    public void loadAnnotations(String annotationSet){
   
        this.DocumentIntance.getAnnotations(annotationSet).addAll(this.EvaluationSet.AnnotationSet);  
    
    }
*/
    
    
    public List <String> getStringAnnotations(String annotationType, String annotationSet){

        List <String> AnnotationList= new ArrayList();
          for (Annotation annot: this.getDocumentIntance().getAnnotations(annotationSet).get(annotationType)){
                     
                     AnnotationList.add(Utils.cleanStringFor(DocumentIntance, annot));
              }
            
         return AnnotationList;
    }   
    
    
    public String getStringAnnotation(Annotation annotation){

           
          String val = Utils.cleanStringFor(DocumentIntance, annotation);
           
            
         return val;
    }  
    
    public List <Annotation> getAnnotations(String annotationType, String annotationSet){

       
        List <Annotation> AnnotationList= new ArrayList();
          for (Annotation annot: this.getDocumentIntance().getAnnotations(annotationSet).get(annotationType)){
                    
                     AnnotationList.add(annot);
              }
            
         return AnnotationList;
    }
    
    
    public static  GateDocument documentCreatorHandler(Object Document) throws ResourceInstantiationException, MalformedURLException{
        
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
        }
        
        throw new ResourceInstantiationException("No type for this document");
      
    }
   
}
