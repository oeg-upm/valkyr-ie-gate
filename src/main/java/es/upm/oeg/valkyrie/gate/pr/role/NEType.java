/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.role;

import es.upm.oeg.valkyrie.gate.pr.GateFactoryInterface;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Pablo
 */
public class NEType extends Model{

    public String Name;
    
    public String AnnotationSet;
    
    public NEType(String name, String annotationSet){
        this.Name= name;
        this.AnnotationSet= annotationSet;
    
    }
    
    public void addOpenNLPModel(String PathToModel) throws MalformedURLException, ResourceInstantiationException{
    
        
            FeatureMap fmInter= Factory.newFeatureMap();
            fmInter.put("config",PathToModel );
            fmInter.put("inputASName", AnnotationSet);
            fmInter.put("outputASName", AnnotationSet);
            
            int size= super.prs.size();
            
            AbstractLanguageAnalyser Model=  GateFactoryInterface.createAbstractLanguageAnalyser("OpenRole"+this.Name+"-"+size,"gate.opennlp.OpenNLPNameFin",fmInter);
            
            super.add(Model);
    
    }
  
    
    
    public void addCoreNLPModel(String PathToModel) throws MalformedURLException, ResourceInstantiationException{
    
    
            FeatureMap fmInter= Factory.newFeatureMap();
            URL otherUrl2 = new File(PathToModel).toURI().toURL();
            fmInter.put("modelFile",otherUrl2 );
            fmInter.put("inputASName", this.AnnotationSet);
            fmInter.put("outputASName", this.AnnotationSet);
            int size= super.prs.size();
            AbstractLanguageAnalyser Model=  GateFactoryInterface.createAbstractLanguageAnalyser("CoreRole"+this.Name+"-"+size,"gate.stanford.NER",fmInter);
            
            super.add(Model);
    
    
    }
    
    
    
    public void addGazeteer(Boolean CaseSensitive, String FeatureSeparator, Boolean LongestMatchOnly,  String GazetteerDir) throws MalformedURLException, ResourceInstantiationException{
    
         int size= super.prs.size();
         // Gazetter
        ProcessingResource Gazetteer = GateFactoryInterface.createANNIEGazetteer(
              "GazzRole"+this.Name+"-"+size, "UTF-8",
                    CaseSensitive,this.AnnotationSet , FeatureSeparator,
                    LongestMatchOnly, GazetteerDir);

          super.add((AbstractLanguageAnalyser) Gazetteer);
    
    }
    
    
    public void addRule(String PathToRule) throws MalformedURLException, ResourceInstantiationException{
    
            int size= super.prs.size();
            FeatureMap fmJape= Factory.newFeatureMap();
            fmJape.put("encoding", "UTF-8");
            fmJape.put("outputASName", this.AnnotationSet);
            fmJape.put("inputASName", this.AnnotationSet);
            fmJape.put("grammarURL",new File(PathToRule).toURI().toURL() );
       
            AbstractLanguageAnalyser JapeRule;
            JapeRule =  GateFactoryInterface.createAbstractLanguageAnalyser("RuleRole"+this.Name+"-"+size,"gate.creole.Transducer", fmJape);
            
           
            super.add(JapeRule);
    
    
    
    }
    
    
    
    
}
