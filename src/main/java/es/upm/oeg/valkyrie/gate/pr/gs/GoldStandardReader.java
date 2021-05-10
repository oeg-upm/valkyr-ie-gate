/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.gs;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "GoldStandard Reader (alpha)", comment = "")
public class GoldStandardReader extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(GoldStandardReader.class);
    
    
    private URL GoldStandardDir;
    private URL GoldStandardFile;
    private String Entity;
    private String AnnotationSet;
    private Boolean oneFile;
    
    
    @Override
    public void execute() throws ExecutionException {
  
        
        try {
            

           

            String inputASName = this.getAnnotationSet();
            if (inputASName == null) {
                inputASName = "";
            }
            AnnotationSet annotSetGen = document.getAnnotations(inputASName);

            // Read the gold standard file and get the list of NamedEntities 
            List<String> listGold = null;
            if(this.oneFile){
            listGold = readGoldStandardCompleteFile();
            }else{
            listGold = readGoldStandardFile();
            }

            for (String GoldString : listGold) {

                createAnnotation(GoldString, annotSetGen);

            }

        } catch (FileNotFoundException ex) {
            logger.error(ex);

        } catch (IOException ex) {
            logger.error(ex);

        } catch (InvalidOffsetException ex) {
            logger.error(ex);

        }

    }
    
    
    private void createAnnotation(String GoldStringLine, AnnotationSet annotSetGen) throws InvalidOffsetException{
    
        
        String[] GoldEntity = GoldStringLine.split("\t");
        // id[0] - start[1] - end[2] - text[3] - type[4]
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
                this.getEntity(),
                fm);

        String TextInDocument = gate.Utils.stringFor(document, start, end).replaceAll("\n", " ");

        if (!TextInDocument.equals(GoldEntity[3])) {
         
            logger.error("The entity string for " + GoldEntity[3] +
                    " doesn't match in document for " + TextInDocument+" in "+this.document.getName());
          
        }

    }

	
    @Override
    public Resource init() throws ResourceInstantiationException {
	return this;
    }

	
    @Override
    public void reInit() throws ResourceInstantiationException {
    }
    

    public List <String> readGoldStandardFile() throws UnsupportedEncodingException, FileNotFoundException, IOException{
    
        
        String docName = this.document.getName().substring(0,
                        this.document.getName().lastIndexOf("."));
        
        File fileDir = new File(getGoldStandardDir().getPath()+File.separator+docName);
        
        logger.info("GoldFile found for "+this.document.getName());
        
        List <String> List= new ArrayList();
        
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileDir), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            if(str.length()>1){
            List.add(str);}
        }

        in.close();
        return List;
    }
    
    public List <String> readGoldStandardCompleteFile() throws UnsupportedEncodingException, FileNotFoundException, IOException{
    

        File fileDir = new File(this.GoldStandardFile.getPath());
        logger.info("Complete GoldFile found");
        List <String> List= new ArrayList();
        
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileDir), "UTF8"));

        String str;

        while ((str = in.readLine()) != null) {
            if(str.length()>1){
            
                String docColumn= str.split("\t")[0];
                if(docColumn.equals(this.document.getName())){
                List.add(str);
                }
            }
        }

        in.close();
        return List;
    }

    public URL getGoldStandardDir() {
        return GoldStandardDir;
    }

    @CreoleParameter(defaultValue = "resources\\Corpora\\Project1\\GoldStandard") 
    public void setGoldStandardDir(URL GoldStandardDir) {
        this.GoldStandardDir = GoldStandardDir;
    }
    
    

    public String getEntity() {
        return Entity;
    }

    @RunTime
    @CreoleParameter(defaultValue = "Disease") 
    public void setEntity(String Entity) {
        this.Entity = Entity;
    }
    
    
    

    public String getAnnotationSet() {
        return AnnotationSet;
    }

    //@Optional
    @RunTime
    @CreoleParameter(defaultValue = "Evaluation") 
    public void setAnnotationSet(String AnnotationSet) {
        this.AnnotationSet = AnnotationSet;
    }

    public Boolean getOneFile() {
        return oneFile;
    }

    @RunTime
    @CreoleParameter(defaultValue = "false")
    public void setOneFile(Boolean oneFile) {
        this.oneFile = oneFile;
    }

    public URL getGoldStandardFile() {
        return GoldStandardFile;
    }

    @RunTime
    @CreoleParameter(defaultValue = "")
    public void setGoldStandardFile(URL GoldStandardFile) {
        this.GoldStandardFile = GoldStandardFile;
    }
    
    
    
    
}
