/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.util;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import org.apache.log4j.Logger;
/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "Annotation Transformation (alpha)", comment = "")
public class AnnotationTransformation extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(AnnotationTransformation.class);
    
    
    private String AnnotationSetIN="";
    private String AnnotationSetOUT="";
    
    private String AnnotationTypeInitial="";
    private String AnnotationTypeFinal="";
    
    
    @Override
    public void execute() throws ExecutionException {
 

            String inputASName = this.AnnotationSetIN;
            String outputASName = this.AnnotationSetOUT;
            
            if(this.AnnotationTypeInitial.equals("")){return;}
            if(this.AnnotationTypeFinal.equals("")){return;}
            
            AnnotationSet annotSetGenIN = document.getAnnotations(inputASName);
            AnnotationSet annotSetGenOUT = document.getAnnotations(outputASName);
     
            AnnotationSet annotSet = annotSetGenIN.get(this.AnnotationTypeInitial);
            
            // Entities
            for(Annotation annot: annotSet){
            
                
                try {
                    annotSetGenOUT.add(annot.getStartNode().getOffset(),
                            annot.getEndNode().getOffset(),
                            this.AnnotationTypeFinal, annot.getFeatures());
                } catch (InvalidOffsetException ex) {
                    logger.error(ex);
                }
                
                
            }
    }
    
    

    public String getAnnotationSetIN() {
        return AnnotationSetIN;
    }

    @RunTime
    @CreoleParameter(defaultValue = "") 
    public void setAnnotationSetIN(String AnnotationSetIN) {
        this.AnnotationSetIN = AnnotationSetIN;
    }

    public String getAnnotationSetOUT() {
        return AnnotationSetOUT;
    }

    @RunTime
    @CreoleParameter(defaultValue = "") 
    public void setAnnotationSetOUT(String AnnotationSetOUT) {
        this.AnnotationSetOUT = AnnotationSetOUT;
    }

    public String getAnnotationTypeInitial() {
        return AnnotationTypeInitial;
    }

    @RunTime
    @CreoleParameter(defaultValue = "Init") 
    public void setAnnotationTypeInitial(String AnnotationTypeInitial) {
        this.AnnotationTypeInitial = AnnotationTypeInitial;
    }

    public String getAnnotationTypeFinal() {
        return AnnotationTypeFinal;
    }

    @RunTime
    @CreoleParameter(defaultValue = "Final") 
    public void setAnnotationTypeFinal(String AnnotationTypeFinal) {
        this.AnnotationTypeFinal = AnnotationTypeFinal;
    }
    
    

	
    public Resource init() throws ResourceInstantiationException {
	return this;
    }

	
    public void reInit() throws ResourceInstantiationException {
    }
    

    

    
    
    
}
