/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr;

import es.upm.oeg.valkyrie.gate.GateHandler;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author pcalleja
 */
public class GateFactoryInterface {
    
    
    public static ProcessingResource createDocumentReset(String ProcessingResourceName, List <String> SetsToKeep,List <String>  SetsToRemove) throws ResourceInstantiationException{
    
        //  reseteador
        FeatureMap fmDelete= Factory.newFeatureMap();

        fmDelete.put("setsToRemove", SetsToRemove);
        fmDelete.put("setsToKeep", SetsToKeep);
        
	ProcessingResource DeleterPR= (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR",fmDelete);
        DeleterPR.setName(ProcessingResourceName);
    
        return DeleterPR;
    
    }
    
    public static ProcessingResource createAnnotationDeleter(String ProcessingResourceName,String AnnotationSetName, String ...  AnnotationsToRemove) throws ResourceInstantiationException{
    
        //  reseteador
        FeatureMap fmDelete= Factory.newFeatureMap();

        List <String> ListAnnotations = new ArrayList<>(Arrays.asList(AnnotationsToRemove));
        fmDelete.put("annotationTypes", ListAnnotations);
        
        List <String> ListSet= new ArrayList();ListSet.add(AnnotationSetName);
        fmDelete.put("setsToRemove",ListSet );
        fmDelete.put("setsToKeep", new ArrayList());
        
	ProcessingResource DeleterPR= (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR",fmDelete);
        DeleterPR.setName(ProcessingResourceName);
    
        return DeleterPR;
    
    }
    
    public static ProcessingResource createANNIETokeniser(String ProcessingResourceName, String encoding, File ANNIEdir ) throws ResourceInstantiationException, MalformedURLException{
    
      // tokenizador
        FeatureMap fmToken= Factory.newFeatureMap();
        fmToken.put("encoding", encoding);
        fmToken.put("rulesURL", 
                new File (ANNIEdir.getAbsolutePath()+ File.separator + "resources" 
                + File.separator + "tokeniser" + File.separator+ "DefaultTokeniser.rules").toURI().toURL());
  
	ProcessingResource TokeniserPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.SimpleTokeniser",fmToken);
	TokeniserPR.setName(ProcessingResourceName);
        return TokeniserPR;
    }
    
    
    public static ProcessingResource createANNIETokeniser(String ProcessingResourceName, String encoding ) throws ResourceInstantiationException, MalformedURLException{
    
        
        
        
        URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
        
        // tokenizador
        FeatureMap fmToken= Factory.newFeatureMap();
        fmToken.put("encoding", encoding);
        fmToken.put("rulesURL", 
                new File (ANNIEdir.getFile().toString()+ File.separator + "resources" 
                + File.separator + "tokeniser" + File.separator+ "DefaultTokeniser.rules").toURI().toURL());
  
	ProcessingResource TokeniserPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.SimpleTokeniser",fmToken);
	TokeniserPR.setName(ProcessingResourceName);
        return TokeniserPR;
    }
            
      
     public static ProcessingResource createANNIETokeniser(String ProcessingResourceName, String encoding,FeatureMap fmToken ) throws ResourceInstantiationException, MalformedURLException{
    
        
        
        
        URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
        
        // tokenizador
        if (fmToken==null){
            fmToken= Factory.newFeatureMap();
        }
        fmToken.put("encoding", encoding);
        fmToken.put("rulesURL", 
                new File (ANNIEdir.getFile().toString()+ File.separator + "resources" 
                + File.separator + "tokeniser" + File.separator+ "DefaultTokeniser.rules").toURI().toURL());
  
	ProcessingResource TokeniserPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.SimpleTokeniser",fmToken);
	TokeniserPR.setName(ProcessingResourceName);
        return TokeniserPR;
    }
     
     /*
      public static ProcessingResource createANNIETokeniser(String ProcessingResourceName, String encoding,String AnnotationSet ) throws ResourceInstantiationException, MalformedURLException{
    
        
        
        
        URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
        FeatureMap fmToken= Factory.newFeatureMap();
        // tokenizador
       
    
        fmToken.put("annotationSetName", this.getAppAnnotationSetName());
        fmToken.put("encoding", encoding);
        fmToken.put("rulesURL", 
                new File (ANNIEdir.getFile().toString()+ File.separator + "resources" 
                + File.separator + "tokeniser" + File.separator+ "DefaultTokeniser.rules").toURI().toURL());
  
	ProcessingResource TokeniserPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.SimpleTokeniser",fmToken);
	TokeniserPR.setName(ProcessingResourceName);
        return TokeniserPR;
    }
*/
     
     public static ProcessingResource createANNIETokeniser(String ProcessingResourceName, String encoding, String AnnotationSetName ) throws ResourceInstantiationException, MalformedURLException{
    
        
        
        
        URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
        
        // tokenizador
      
        FeatureMap fmToken= Factory.newFeatureMap();
        fmToken.put("annotationSetName", AnnotationSetName);
        fmToken.put("encoding", encoding);
        fmToken.put("rulesURL", 
                new File (ANNIEdir.getFile().toString()+ File.separator + "resources" 
                + File.separator + "tokeniser" + File.separator+ "DefaultTokeniser.rules").toURI().toURL());
  
	ProcessingResource TokeniserPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.SimpleTokeniser",fmToken);
	TokeniserPR.setName(ProcessingResourceName);
        return TokeniserPR;
    }
     
     
    public static ProcessingResource createANNIESplitter(String ProcessingResourceName, String encoding, File ANNIEdir ) throws ResourceInstantiationException, MalformedURLException{
        
        
        
        FeatureMap fmSplitter= Factory.newFeatureMap();
        fmSplitter.put("encoding", encoding);
        fmSplitter.put("gazetteerListsURL", new File (ANNIEdir.getAbsolutePath() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "gazetteer" + File.separator + "lists.def").toURI().toURL());
        
        fmSplitter.put("transducerURL", new File (ANNIEdir.getAbsolutePath() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "grammar" + File.separator + "main.jape").toURI().toURL());
        
	ProcessingResource SplitterPR = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter",fmSplitter);
        SplitterPR.setName(ProcessingResourceName);
        return SplitterPR;
    }
    
    
    public static ProcessingResource createANNIESplitter(String ProcessingResourceName, String encoding) throws ResourceInstantiationException, MalformedURLException{
        
         URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
        FeatureMap fmSplitter= Factory.newFeatureMap();
        fmSplitter.put("encoding", encoding);
        fmSplitter.put("gazetteerListsURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "gazetteer" + File.separator + "lists.def").toURI().toURL());
        
        fmSplitter.put("transducerURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "grammar" + File.separator + "main.jape").toURI().toURL());
        
	ProcessingResource SplitterPR = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter",fmSplitter);
        SplitterPR.setName(ProcessingResourceName);
        return SplitterPR;
    }
    
    
    public static ProcessingResource createANNIESplitter(String ProcessingResourceName, String encoding, FeatureMap fmSplitter) throws ResourceInstantiationException, MalformedURLException{
        
         URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
        if(fmSplitter== null){
         fmSplitter= Factory.newFeatureMap();
        }
        fmSplitter.put("encoding", encoding);
        fmSplitter.put("gazetteerListsURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "gazetteer" + File.separator + "lists.def").toURI().toURL());
        
        fmSplitter.put("transducerURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "grammar" + File.separator + "main.jape").toURI().toURL());
        
	ProcessingResource SplitterPR = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter",fmSplitter);
        SplitterPR.setName(ProcessingResourceName);
        return SplitterPR;
    }
    
    
    public static ProcessingResource createANNIESplitter(String ProcessingResourceName, String encoding, String InputAnnotationSetName, String OutputAnnotationSetName) throws ResourceInstantiationException, MalformedURLException{
        
         URL ANNIEdir=GateHandler.getPluginURL("ANNIE");
       
        if(ANNIEdir==null){ throw new ResourceInstantiationException("ANNIE plugin is not registered");}
        
  
        FeatureMap fmSplitter= Factory.newFeatureMap();
        fmSplitter.put("inputASName", InputAnnotationSetName);
        fmSplitter.put("outputASName", OutputAnnotationSetName);
        fmSplitter.put("encoding", encoding);
        fmSplitter.put("gazetteerListsURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "gazetteer" + File.separator + "lists.def").toURI().toURL());
        
        fmSplitter.put("transducerURL", new File (ANNIEdir.getFile().toString() + File.separator + "resources" + File.separator + "sentenceSplitter"
						+ File.separator + "grammar" + File.separator + "main.jape").toURI().toURL());
        
	ProcessingResource SplitterPR = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter",fmSplitter);
        SplitterPR.setName(ProcessingResourceName);
        return SplitterPR;
    }

    public static ProcessingResource createANNIEGazetteer(String ProcessingResourceName, String Encoding, Boolean CaseSensitive, String AnnotationSetName, String FeatureSeparator, Boolean LongestMatchOnly,  String GazetteerDir) throws MalformedURLException, ResourceInstantiationException{
        
        // GAZETTEER COMPLETE 1
        FeatureMap fmGaz =Factory.newFeatureMap();
        fmGaz.put("encoding",Encoding);
        fmGaz.put("caseSensitive", CaseSensitive);
        fmGaz.put("annotationSetName", AnnotationSetName);
        fmGaz.put("gazetteerFeatureSeparator",FeatureSeparator);
        fmGaz.put("longestMatchOnly",LongestMatchOnly);
        fmGaz.put("listsURL", new File(GazetteerDir).toURI().toURL());
        ProcessingResource Gazetteer = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer",fmGaz);
	Gazetteer.setName(ProcessingResourceName);
        return Gazetteer;
    }
    
    
    public static ProcessingResource createFlexibleGazetteer(String ProcessingResourceName, ProcessingResource NormalGazetteer, List <String> InputFeatureNames) throws ResourceInstantiationException{
        
        NormalGazetteer.init();  
        FeatureMap fmFlex = Factory.newFeatureMap(); //empty map:default params 
        fmFlex.put("gazetteerInst", NormalGazetteer);      
        fmFlex.put("inputFeatureNames", InputFeatureNames);
        
        ProcessingResource FlexiGazetteer = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.FlexibleGazetteer",fmFlex);
        FlexiGazetteer.setName(ProcessingResourceName);
        return FlexiGazetteer;
        
    }
    
    
    public static ProcessingResource createFlexibleGazetteer(String ProcessingResourceName, ProcessingResource NormalGazetteer, List <String> InputFeatureNames, String InputASName, String OutputASName) throws ResourceInstantiationException{
        
        NormalGazetteer.init();  
        FeatureMap fmFlex = Factory.newFeatureMap(); //empty map:default params 
        fmFlex.put("gazetteerInst", NormalGazetteer);      
        fmFlex.put("inputFeatureNames", InputFeatureNames);
        fmFlex.put("inputASName", InputASName);
        fmFlex.put("outputASName", OutputASName);
        
        ProcessingResource FlexiGazetteer = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.FlexibleGazetteer",fmFlex);
        FlexiGazetteer.setName(ProcessingResourceName);
        return FlexiGazetteer;
        
    }
    
    public static ProcessingResource createJAPERule(String ProcessingResourceName, String Encoding, String InputASName, String OutputASName, String JapeRuleDir) throws MalformedURLException, ResourceInstantiationException{
        
        
        /// PATRON MEDDRA Clean
        FeatureMap fmJape= Factory.newFeatureMap();
        fmJape.put("encoding", Encoding);
        fmJape.put("outputASName", OutputASName);   
        fmJape.put("inputASName", InputASName);   
        fmJape.put("grammarURL",new File(JapeRuleDir).toURI().toURL() );   
	ProcessingResource JapeRule = (ProcessingResource) Factory.createResource("gate.creole.Transducer", fmJape);
        JapeRule.setName(ProcessingResourceName);
        return JapeRule;
    }
    
    
    public static ProcessingResource createJAPERule(String ProcessingResourceName, String Encoding, String JapeRuleDir) throws MalformedURLException, ResourceInstantiationException{
        
        
        /// PATRON MEDDRA Clean
        FeatureMap fmJape= Factory.newFeatureMap();
        fmJape.put("encoding", Encoding);
        fmJape.put("outputASName", "");   
        fmJape.put("inputASName", "");   
        fmJape.put("grammarURL",new File(JapeRuleDir).toURI().toURL() );   
	ProcessingResource JapeRule = (ProcessingResource) Factory.createResource("gate.creole.Transducer", fmJape);
        JapeRule.setName(ProcessingResourceName);
        return JapeRule;
    }
    
    
    public static ProcessingResource createProcessingResource(String ProcessingResourceName, String Path, FeatureMap fm) throws MalformedURLException, ResourceInstantiationException{
       
	ProcessingResource pr = (ProcessingResource) Factory.createResource(Path, fm);
        pr.setName(ProcessingResourceName);
        return pr;
    }
    
    public static AbstractLanguageAnalyser createAbstractLanguageAnalyser(String ProcessingResourceName, String Path, FeatureMap fm) throws MalformedURLException, ResourceInstantiationException{
       
	AbstractLanguageAnalyser pr = (AbstractLanguageAnalyser) Factory.createResource(Path, fm);
        pr.setName(ProcessingResourceName);
        return pr;
    }
    
    public static ProcessingResource getProcessingResourceByName(String ProcessingResourceName) {
      
        for(ProcessingResource pr: Gate.getCreoleRegister().getPrInstances()){
        
            if(pr.getName().equals(ProcessingResourceName)){
                return pr;
            }
        }
        return null;
    }
    
   
   
	
    public static void deleteProcessingResourceByName(String ProcessingResourceName) {
      
        for(ProcessingResource pr: Gate.getCreoleRegister().getPrInstances()){
        
            if(pr.getName().equals(ProcessingResourceName)){
                Gate.getCreoleRegister().remove(pr);
            }
        }
        
    }   
    
    
    public static void deleteProcessingResourceByName2(String ProcessingResourceName) {
      
        for(ProcessingResource pr: Gate.getCreoleRegister().getPrInstances()){
        
            if(pr.getName().equals(ProcessingResourceName)){
                //Gate.getCreoleRegister().remove(pr);
                Factory.deleteResource(pr);
            }
        }
        
    } 
    
    public static void deleteProcessingResource(ProcessingResource pr) {

        Factory.deleteResource(pr);

    }
	
          
    
}
