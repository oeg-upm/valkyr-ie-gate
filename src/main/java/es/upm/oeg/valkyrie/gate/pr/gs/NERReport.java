/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.gs;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "NER Report (alpha)", comment = "Creates a report for the evaluation and the entities")
public class NERReport extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(NERReport.class);
    
    
    
    
    
    private HashSet <String> GoldStandardEntitiesSet;
    
    @Override
    public void execute() throws ExecutionException {

        try {
            logger.info("Processing: ");

            String docName = this.document.getName().split("\\.")[0];
            // Get the document id

            if (GoldStandardAnnotationSetName == null) {
                throw new ExecutionException("Entity to read has not been set");
            }

            this.readGoldEntities(this.GoldStandardAnnotationSetName, this.AnnotationType);

            StringBuffer results = new StringBuffer();
            StringBuffer table2 = new StringBuffer();
            StringBuffer headers = new StringBuffer();
            table2.append("<table style=\"width:100%\">\n" + "<tr>");

            for (String annotationSetName : this.AnnotationSetsToEvaluate) {

                results.append(this.evaluateExperiment(annotationSetName, this.AnnotationType));
                results.append("\n");
                table2.append("<td colspan=\"  " + AnnotationSetsToEvaluate.size() + "\">\n" + annotationSetName + "	</td>");
                headers.append("<td>	Found	</td>\n"
                        + "	<td>	Not Found	</td>\n"
                        + "	<td>	Unknown	</td>");
            }

            table2.append("<tr>" + headers + "</tr>");
            table2.append("<tr>" + results + "</tr>");
            table2.append("</table> \n" + "<br/><br/>");
            
           

            String table1 = "<table style=\"width:100%\">\n"
                    + "<tr> <th>" + docName + "</th></tr>\n"
                    + "<tr>\n"
                    + "<td>\n"
                    +  gate.Utils.cleanStringFor(document, document.getAnnotations().get("Token"))
                    + "</td>\n"
                    + "</tr>\n"
                    + "\n"
                    + "\n"
                    + "</table>";

            
            
            /// init corpus
            Boolean NotFirst=true;
            if(corpus.get(0).equals(this.document)){
                // if is first
                NotFirst=false;
            
            }
            
            
            FileWriter OutputFile= new FileWriter(this.ResourcesDir.getFile() +File.separator+ReportFileName+".html",NotFirst);            
            PrintWriter writer = new PrintWriter(new BufferedWriter(OutputFile));
            
            if(!NotFirst){
                writer.append(getHeader());
            }
            
            writer.append(table1);
            writer.append(table2);
            writer.close();
            
            /*
            // delete if exists
            FileWriter OutputFile = new FileWriter(ResourcesDir.getFile() + File.separator + docName, true);

            PrintWriter writer = new PrintWriter(new BufferedWriter(OutputFile));
            writer.printf("utf-8");

            writer.append(bf.toString().trim());
            writer.close();*/

        } catch (FileNotFoundException ex) {
            logger.error(ex.toString());
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex.toString());
        } catch (IOException ex) {
            logger.error(ex.toString());
        }

    }

    
    
    private String getHeader(){
    
     String s="<html>\n" +
        "\n" +
        "<head>\n" +
        "<style>\n" +
        "table, th, td {\n" +
        "    border: 1px solid black;\n" +
        "    border-collapse: collapse;\n" +
        "}\n" +
        "th, td {\n" +
        "    padding: 5px;\n" +
        "    text-align: left;    \n" +
        "}\n" +
        "</style>\n" +
        "\n" +
        "</head>\n" +
        "\n" +
        "<body>";

            return s;
    }
    
    
    
    private String evaluateExperiment(String AnnotationSetName, String entity){
    
    
         AnnotationSet annotExeperiment= document.getAnnotations(AnnotationSetName);
         AnnotationSet annotExeperimentEntities= annotExeperiment.get(entity);
         List <Annotation> listAnnotation= annotExeperimentEntities.inDocumentOrder();
         
         // List -- found
         List <String> listFoundInGoldStandard = new ArrayList();
         HashSet FoundSet = new HashSet();
         // Not Found
         List <String> listNotFound = new ArrayList();
         // Unkwonw
         List <String> listUnkwownInGoldStandard = new ArrayList();
        
         for(Annotation EntityAnnot: listAnnotation){
             
             String  Start = EntityAnnot.getStartNode().getOffset().toString();
             String   End = EntityAnnot.getEndNode().getOffset().toString();
                // Get text from an annotation
              String  Text = gate.Utils.stringFor(document, EntityAnnot);
              
              if(this.GoldStandardEntitiesSet.contains(Text+"$"+Start+End)){
                  FoundSet.add(Text+"$"+Start+End);
                  listFoundInGoldStandard.add(Text);
              }else{
                  listUnkwownInGoldStandard.add(Text);
              }
         
         }
         
         
         for (String GoldEntity: this.GoldStandardEntitiesSet){
         
             if(!FoundSet.contains(GoldEntity)){
             
                    listNotFound.add(GoldEntity.split("\\$")[0]);
             }
         
         }
         
         
         StringBuffer bf=new StringBuffer();
         bf.append(CreateStringResult(listFoundInGoldStandard));
         bf.append(CreateStringResult(listNotFound));
         bf.append(CreateStringResult(listUnkwownInGoldStandard));
         
         return bf.toString();
         
    
    }
    
    
    
    private void readGoldEntities(String AnnotationSetName, String entity){
    
    
         AnnotationSet annotExeperiment= document.getAnnotations(AnnotationSetName);
         AnnotationSet annotExeperimentEntities= annotExeperiment.get(entity);
         List <Annotation> listAnnotation= annotExeperimentEntities.inDocumentOrder();
         
         GoldStandardEntitiesSet= new HashSet();
        
        
        
         for(Annotation EntityAnnot: listAnnotation){
         
             String  Start = EntityAnnot.getStartNode().getOffset().toString();
             String   End = EntityAnnot.getEndNode().getOffset().toString();
                // Get text from an annotation
              String  Text = gate.Utils.stringFor(document, EntityAnnot);
         
              GoldStandardEntitiesSet.add(Text+"$"+Start+End);
         
         }

    }
    
    
    
    public String CreateStringResult(List <String> list){
    
        StringBuffer sb= new StringBuffer();
        sb.append("<td>");
        for(String e: list){
        
            sb.append(e+" <br/>");
        
        }

        sb.append("</td>");        
        return sb.toString();
    }
    

    
    
    
    
	
    public Resource init() throws ResourceInstantiationException {
	
        if (ResourcesDir == null) {
            throw new ResourceInstantiationException("GoldStandard folder has not been set");
        }
        File folder= new File(this.ResourcesDir.getFile());
        if( (!folder.exists()) || (!folder.isDirectory())  ){
            throw new ResourceInstantiationException("The folder is not found");
        }
        
        
        return this;
	}

	
    public void reInit() throws ResourceInstantiationException {
    }

    
    
    
    
    ////////// PARAMETERS
    private URL ResourcesDir;
    
    private String ReportFileName;
    private String GoldStandardAnnotationSetName;
    private String AnnotationType;
    private List <String> AnnotationSetsToEvaluate;
    
    public URL getResourcesDir() {
        return ResourcesDir;
    }

    @CreoleParameter(defaultValue = "resources/NERreport") 
    public void setResourcesDir(URL ResourcesDir) {
        this.ResourcesDir = ResourcesDir;
    }
    public String getGoldStandardAnnotationSetName() {
        return GoldStandardAnnotationSetName;
    }

    @RunTime
    @CreoleParameter
    public void setGoldStandardAnnotationSetName(String GoldStandardAnnotationSetName) {
        this.GoldStandardAnnotationSetName = GoldStandardAnnotationSetName;
    }
    
    public String getAnnotationType() {
        return AnnotationType;
    }

    @RunTime
    @CreoleParameter
    public void setAnnotationType(String AnnotationType) {
        this.AnnotationType = AnnotationType;
    }

    

    public List<String> getAnnotationSet() {
        return AnnotationSetsToEvaluate;
    }

    //@Optional
    @RunTime
    @CreoleParameter
    public void setAnnotationSet(List<String> AnnotationSetsToEvaluate) {
        this.AnnotationSetsToEvaluate = AnnotationSetsToEvaluate;
    }  

    public String getReportFileName() {
        return ReportFileName;
    }

    //@Optional
    @RunTime
    @CreoleParameter(defaultValue = "Repo")
    public void setReportFileName(String ReportFileName) {
        this.ReportFileName = ReportFileName;
    }
        
      
    
}
