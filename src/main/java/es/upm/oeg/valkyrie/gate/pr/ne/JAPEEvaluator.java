/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.ne;

import es.upm.oeg.valkyrie.gate.evaluation.EvaluationResult;
import es.upm.oeg.valkyrie.gate.evaluation.NEREvaluation;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import java.util.HashSet;
import org.apache.log4j.Logger;


/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "Role Associator RCM", comment = "Associates a role to other entities with the same span text")
public class JAPEEvaluator extends AbstractLanguageAnalyser{

    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(JAPEEvaluator.class);
    
    private String GoldAnnotationSet;
    private String AnnotationSet;
    private String AnnotationType;
    
    
    private HashSet <String> EvaluationsSets;
    
    @Override
    public void execute() throws ExecutionException {
  
        
        
         if (corpus.get(0).equals(document)) {

            EvaluationsSets = new HashSet();
        }

        // Role Identifier
        AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
        AnnotationSet annotSetGenGold = document.getAnnotations(this.GoldAnnotationSet);

        AnnotationSet Entities = annotSetGen.get(this.getAnnotationType());
        AnnotationSet GoldEntities = annotSetGenGold.get(this.getAnnotationType());

        for (Annotation Entity : Entities) {

            try {

                String typeRule = Entity.getFeatures().get("rule").toString();
                AnnotationSet annotSetTypeRule = document.getAnnotations(typeRule);
                annotSetTypeRule.add(Entity);

            } catch (Exception e) {
            }

        }

        if (corpus.get(corpus.size() - 1).equals(document)) {

            for (String set : this.EvaluationsSets) {

                NEREvaluation eval = new NEREvaluation(set, GoldAnnotationSet);
                EvaluationResult res = eval.evaluateAnnotation(set, AnnotationType, corpus);
                res.printResultShort();

            }

        }


    }
    
   
    @Override
    public Resource init() throws ResourceInstantiationException {
        
       
        
	return this;
    }

	
    @Override
    public void reInit() throws ResourceInstantiationException {
    }

    public String getGoldAnnotationSet() {
        return GoldAnnotationSet;
    }

    @RunTime
    @CreoleParameter(defaultValue = "")
    public void setGoldAnnotationSet(String GoldAnnotationSet) {
        this.GoldAnnotationSet = GoldAnnotationSet;
    }
    

  
    
    
    
    
    
    
    
    
    public String getAnnotationSet() {
    return AnnotationSet;
    }
    
    //@Optional
    @RunTime
    @CreoleParameter(defaultValue = "")
    public void setAnnotationSet(String AnnotationSet) {
    this.AnnotationSet = AnnotationSet;
    }

    public String getAnnotationType() {
        return this.AnnotationType;
    }

    @RunTime
    @CreoleParameter()
    public void setAnnotationType(String AnnotationType) {
        this.AnnotationType = AnnotationType;
    }
    
   
    
    
    
}