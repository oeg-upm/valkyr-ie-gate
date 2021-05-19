/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web;

import es.upm.oeg.valkyrie.web.model.Entity;
import java.util.List;
import es.upm.oeg.valkyrie.web.model.InputTextRaw;
import es.upm.oeg.valkyrie.web.model.OutputDocument;
import es.upm.oeg.valkyrie.web.model.OutputText;
import es.upm.oeg.valkyrie.web.processing.ConfigurationFile;
import es.upm.oeg.valkyrie.web.processing.NERCore;/*
import eu.lynxproject.interfaces.domain.LynxAnnotation;
import eu.lynxproject.interfaces.domain.LynxDocument;
import eu.lynxproject.interfaces.validation.ValidatorResponse;*/
import es.upm.oeg.valkyrie.web.processing.NERLingGazetteers;
import es.upm.oeg.valkyrie.web.processing.ValkyrIEModel;
import gate.Annotation;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExampleProperty;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import es.upm.oeg.valkyrie.gate.GateHandler;
import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import es.upm.oeg.valkyrie.gate.lr.GateDocument;
import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import es.upm.oeg.valkyrie.gate.pr.GatePipeline;
import es.upm.oeg.valkyrie.web.model.Feature;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
   
        

@Controller
public class ServicesController {

    @Autowired
    ServletContext context;
    
    @Autowired
    Environment env;

    static Logger logger = Logger.getLogger(ServicesController.class);

    public static boolean GateInitialized = false;

    
    public ValkyrIEModel ValkModel;
    
    
    
   
    
    
    
    

    
    
    
   
    
    
    
    
    
    public void initModel() throws IOException, Exception{
    
       
        
        //logger.info(env.getProperty("gate.configpath"));
        
        //ConfigurationFile FileConf= new ConfigurationFile(env.getProperty("app.configpath"));
        
        
        /*Model configuration*/
        File file = ResourceUtils.getFile("processes.conf");
        logger.info("Config path: "+ file.getAbsolutePath()); 
        
        if (!file.exists()){
        
            throw new Exception("Unable to find processes.conf");
        }
        
        
        this.ValkModel = new ValkyrIEModel();
        
        ConfigurationFile FileConf= new ConfigurationFile(file.getAbsolutePath());
        
        ValkModel.setAppAnnotationSetName("");
        
        logger.info("Configuration File:");
        
       
        
        this.ValkModel.initPipelineGuided(FileConf);
        
        
        System.out.println("pipelines in!");
    
    }
  

    
    public void initGate() throws Exception{
 
        
        
        //servletContext.getRealPath("/WEB-INF/myDIR/") 
       //String ProjectBaseDir = context.getRealPath("/WEB-INF/resources");  
        
       // File file = ResourceUtils.getFile("classpath:GateHome");
        
        
        
         
    //    InputStream inpt=classLoader.getResourceAsStream(fileName);
    /*
      Resource resource = new ClassPathResource("GateHome");

    InputStream input = resource.getInputStream();

    File file = resource.getFile();
    
        logger.info("exists"+file.exists());
        
        
        
        
        String fileName = "/GateHome";
        ClassLoader classLoader = getClass().getClassLoader();

    
    
        System.out.println("Copying ->" + input + "\n\tto ->" + "");

        try {
            Files.copy(input, Paths.get("/"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.error(ex);
             }
 
        String ProjectBaseDir= file.getAbsolutePath().replace("/GateHome/gate.xml", "");
        
        */
    
    
    
    
        /*GATE HOME*/
        File file = ResourceUtils.getFile("config");
        logger.info("Config path: "+ file.getAbsolutePath()); 
        
        if (!file.exists()){
        
            throw new Exception("Unable to find config folder");
        }
        
          
        
        boolean Visualize = false;

        GateHandler.initGate(file.getAbsolutePath(), Visualize, "ANNIE", "Tools", "Valkyr_IE"); // "OpenNLP", "Stanford_CoreNLP",
        GateInitialized=true;

        
        logger.info("GATE initialized");
        initModel();
        
        logger.info("Model initialized");
        
        
        
    }
  
    
    @PostConstruct
    public void initIt() throws Exception {
	  logger.info("Init Valkyr Models" );
          
          
          
          initGate();
          
    }
    
    
    
    
  
    
    @RequestMapping(value ="/status", method = RequestMethod.GET)
    @ResponseBody
    public String status() {
        return "UP";
    }
    
    @RequestMapping(value ="/restart", method = RequestMethod.GET)
    @ResponseBody
    public void reInit() {
        
         try {
             initModel();
         } catch (Exception ex) {
             logger.error(ex);
         }
    }
    
    
     

    
    
    
    @ApiOperation(value = "Process the text and retrieves the discovered annotations") 
    @RequestMapping(
            value = "/processText",
            consumes = "application/json;charset=UTF-8",
            produces= "application/json;charset=UTF-8",
            method = RequestMethod.POST)
    @ResponseBody
    public OutputDocument processText(@RequestBody InputTextRaw text) throws Exception {

        try {          

            // Document 
            GateDocument gateDoc= new  GateDocument("document", text.getText(), "UTF-8" );
            // Corpus   
            GateCorpus gateCorpus= new GateCorpus();
            gateCorpus.createCorpus("corpus");
            gateCorpus.addDocument(gateDoc);
             
            
            executeModels(gateCorpus);

            return generateOutput(gateCorpus);
           
            
        } catch (Exception e) {
            logger.error("Error in REST service",e);
            logger.error(e.getCause().toString());
            
        }
        OutputDocument out= new OutputDocument();
        out.getAnnotations().add(new Entity("Internal Server Error","",0,0));
        return out;
    }
    
    
    
    
       public OutputDocument generateOutput(GateCorpus gateCorpus) {

        GateDocument doc = gateCorpus.Documents.get(0);
        //HashSet <String> DetectedEntities = new HashSet();

        OutputDocument output = new OutputDocument();

        Set<String> Entities = doc.getDocumentIntance().getAnnotations().getAllTypes();

        for (String entityType : Entities) {

            if (entityType.equals("SpaceToken")) {
                continue;
            }
            if (entityType.equals("Sentence")) {
                continue;
            }
            if (entityType.equals("Token")) {
                continue;
            }

            logger.info(entityType);

            for (Annotation annot : doc.getAnnotations(entityType, "")) {
                List <Feature> featuresList=new ArrayList();
                
                for (Object key: annot.getFeatures().keySet()) {
                    String na= (String) key;
                    String val= (String) annot.getFeatures().get(key);
                    featuresList.add(new Feature(na,val));
                    
                }
                
                //DetectedEntities.add(doc.getStringAnnotation(annot)+":"+annot.getStartNode().getOffset().toString()+":"+annot.getEndNode().getOffset().toString()+":"+entityType);
                Entity en = new Entity(entityType, doc.getStringAnnotation(annot), annot.getStartNode().getOffset().intValue(), annot.getEndNode().getOffset().intValue());
                en.setFeatures(featuresList);
                output.getAnnotations().add(en);

            }
        }

        
        return output;

    }
  
    
    public void executeModels(GateCorpus gateCorpus) throws Exception{
    
    
            this.ValkModel.processCorpus(gateCorpus);

    }
        
        
    
}
