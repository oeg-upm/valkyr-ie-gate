/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.gs;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "GoldStandard Writer (alpha)", comment = "Saves the gold standard in a file, now in BC2Format")
public class GoldStandardWriter extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(GoldStandardWriter.class);
    
    
    private URL OutputURL;
    private String FileName;
    private String InputAnnotationSet;
    private String InputAnnotationType;
    private String OutputAnnotationType;
    private Boolean oneFile;
    
    private List <String> Lines;
    private HashSet <String> DocumentNames;
    
    // debug
    private Boolean debugMode;
    
    @Override
    public void execute() throws ExecutionException {

        logger.info("Saving entities for: " + this.document.getName());

        if (InputAnnotationType == null) {
            throw new ExecutionException("Entity to read has not been set");
        }
        
        
        
        // first document
        if(corpus.get(0).equals(this.document)){
               this.DocumentNames=new HashSet();
               this.Lines=new ArrayList();
        }
        
        // create gold lines
        
        this.DocumentNames.add(this.document.getName());
        // Set
        AnnotationSet annotSetGen = document.getAnnotations(this.InputAnnotationSet);
        // Entity
        AnnotationSet annotEntitySet = annotSetGen.get(InputAnnotationType);
        List<Annotation> annotEntityList = annotEntitySet.inDocumentOrder();
        
        // GoldStandard annotations
        //StringBuffer bf = new StringBuffer();
        for (Annotation Annot : annotEntityList) {
            String line= createGoldStandardLine(Annot);
            this.Lines.add(line);
        }
        
        
        
        // the last one
        if(corpus.get(corpus.size()-1).equals(this.document)){
        
            // write lines in document
            createGoldDocument();
            
        }

        

        // WRITE
       

    }
    
    
    public void createGoldDocument(){
    
         try {

            //writeInFile(bf);
            List <String> OtherLines= readOtherResultsInDocument();
            OtherLines.addAll(this.Lines);
            writeNewLines(OtherLines);

        } catch (Exception ex) {
            logger.error("Failed on writting gold standard",ex);
        }

    }
    
    private List<String> readOtherResultsInDocument() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        List <String> listResults= new ArrayList();
        
        File fileDir = new File(this.OutputURL.getFile()+File.separator+this.FileName);  
        logger.debug("Complete GoldFile found");
   
        
        BufferedReader in = new BufferedReader( new InputStreamReader(new FileInputStream(fileDir), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            if(str.length()>1){
            
                String docColumn= str.split("\t")[0];
                if(!this.DocumentNames.contains(docColumn)){
                    listResults.add(str);
                }
            }
        }

        in.close();
        return listResults;
    
    
    
    }
    
    
    private void writeNewLines(List <String> NewLines) throws FileNotFoundException{
    
    
         // WRITE IN :
            File OutputFile = null;
            FileOutputStream OutputStream=null;
         
        if (this.FileName.equals("")) {

            throw new FileNotFoundException("Gold Standard Complete File Name is not set");
        }
        OutputFile = new File(this.OutputURL.getFile() + File.separator + this.FileName);

        // MODE 
        
        OutputStream = new FileOutputStream(OutputFile, false); // false= machaca
        
      

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(OutputStream, StandardCharsets.UTF_8));
      
        for(String Line: NewLines){
            writer.append(Line+"\n");
        }
        
        writer.close();


    }

    
    @Deprecated
    private void writeInFile(StringBuffer bf) throws FileNotFoundException{
    
    
         // WRITE IN :
            File OutputFile = null;
            FileOutputStream OutputStream=null;
            
            if (this.oneFile) {
                
                
                if (this.FileName.equals("")) {
                    
                    throw new FileNotFoundException("Gold Standard Complete File Name is not set");
                }
                OutputFile = new File(this.OutputURL.getFile()+File.separator+this.FileName); 
                
                // MODE 
                if(corpus.get(0).equals(this.document)){
                OutputStream =new FileOutputStream(OutputFile, false);
                }
                OutputStream =new FileOutputStream(OutputFile, true);
                

                
            } else {
                
                String docName = this.document.getName().substring(0,this.document.getName().lastIndexOf("."));
                OutputFile = new File(OutputURL.getFile() + File.separator + docName);
                if (OutputFile.exists()) {
                    OutputFile.delete();
                }
                OutputStream =new FileOutputStream(OutputFile, false);
            }

            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(OutputStream,StandardCharsets.UTF_8));
            //PrintWriter writer = new PrintWriter(OutputFile, "utf-8");
            writer.append(bf.toString());
            writer.close();
    
    
    }

    
    private String createGoldStandardLine(Annotation Annot) {

        String line, Start, End, Text;
        String tab = "\t";

        Start = Annot.getStartNode().getOffset().toString();
        End = Annot.getEndNode().getOffset().toString();
        // Get text from an annotation
        Text = gate.Utils.stringFor(document, Annot).replaceAll("\n", " ");
   
        line = this.document.getName() + tab + Start + tab + End + tab + Text + tab + this.OutputAnnotationType;
        
        if(this.debugMode){   line = line + tab+ this.createDebugContext(Annot, Text);        }
        
        //line = line + "\n";
        return line;
    }



    @Override
    public Resource init() throws ResourceInstantiationException {

        this.Lines=new ArrayList();
        this.DocumentNames=new HashSet();
        
        if (OutputURL == null) {
            throw new ResourceInstantiationException("GoldStandard folder has not been set");
        }
        File folder = new File(this.OutputURL.getFile());
        if ((!folder.exists()) || (!folder.isDirectory())) {
            throw new ResourceInstantiationException("The folder is not found");
        }

        return this;
    }

    @Override
    public void reInit() throws ResourceInstantiationException {
        init();
    }



    public  String createDebugContext( Annotation Annot, String entity){
    
        String before=createLeftContext(Annot,23);
        String after=createRightContext(Annot,23);
        
        String contex = before+" |"+entity+"| "+after;
        return contex;
    
    
    }
    
    public String createLeftContext( Annotation Annot, int size){
    
        String before="";
   
        try {

            before = gate.Utils.cleanStringFor(this.document, Annot.getStartNode().getOffset() - size, Annot.getStartNode().getOffset());

        } catch (Exception e) {

            if(size==0){
                return before;
            }else{
                size--;
                before= createLeftContext(  Annot,  size);
            }   
        }
        
        return before;
    }
    
    public String createRightContext( Annotation Annot, int size){
    
        String after="";
   
        try {

            after = gate.Utils.cleanStringFor(this.document,Annot.getEndNode().getOffset(), Annot.getEndNode().getOffset() + size);

        } catch (Exception e) {

            if(size==0){
                return after;
            }else{
                size--;
                after= createRightContext(  Annot,  size);
            }   
        }
        
        return after;
    }
    
    
    
    
    
   
    
    /////////////////////////

    public URL getOutputURL() {
        return OutputURL;
    }
    @CreoleParameter(defaultValue = "resources/GoldStandard") 
    public void setOutputURL(URL OutputURL) {
        this.OutputURL = OutputURL;
    }

    public String getFileName() {
        return FileName;
    }

    @RunTime
    @CreoleParameter(defaultValue = "")
    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public String getInputAnnotationSet() {
        return InputAnnotationSet;
    }

    @RunTime
    @CreoleParameter(defaultValue = "Evaluation") 
    public void setInputAnnotationSet(String InputAnnotationSet) {
        this.InputAnnotationSet = InputAnnotationSet;
    }

    public String getInputAnnotationType() {
        return InputAnnotationType;
    }

    @RunTime
    @CreoleParameter
    public void setInputAnnotationType(String InputAnnotationType) {
        this.InputAnnotationType = InputAnnotationType;
    }

    public String getOutputAnnotationType() {
        return OutputAnnotationType;
    }

    @RunTime
    @CreoleParameter
    public void setOutputAnnotationType(String OutputAnnotationType) {
        this.OutputAnnotationType = OutputAnnotationType;
    }

    public Boolean getOneFile() {
        return oneFile;
    }

    @RunTime
    @CreoleParameter(defaultValue = "false")
    public void setOneFile(Boolean oneFile) {
        this.oneFile = oneFile;
    }

    public Boolean getDebugMode() {
        return debugMode;
    }

    @RunTime
    @CreoleParameter(defaultValue = "false")
    public void setDebugMode(Boolean debugMode) {
        this.debugMode = debugMode;
    }
     
    
    
}
