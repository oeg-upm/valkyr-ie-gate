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
import gate.util.OffsetComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "BIO Formater for Gold Standard (Beta)", comment = "Given an annotation, put the feature BIO at token level")
public class BIOFormater extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    
    static final Logger logger = Logger.getLogger(BIOFormater.class.getName()); 
   
    private String InputAnnotation;
    private String TokenFeatureBIO;
    
    private String AnnotationSetToken;
    private String AnnotationSetEntity;
    
    public void execute() throws ExecutionException {

        logger.debug("BIO FORMATTING TAG +"+this.InputAnnotation+" in "+ this.document.getName());

        // Get the annotation Set Group general
       
        AnnotationSet annotSetGenToken = document.getAnnotations(AnnotationSetToken);
        AnnotationSet annotSetGenEntity = document.getAnnotations(AnnotationSetEntity);
        if (InputAnnotation == null) {
            throw new ExecutionException("GoldStandardAnnotationName has not been set");
        }

        if (this.TokenFeatureBIO == null) {
            this.TokenFeatureBIO = "Entity";
        }

        // Entity
        AnnotationSet annotEntitySet = annotSetGenEntity.get(InputAnnotation);
        List<Annotation> annotEntityList = new ArrayList<>(annotEntitySet);
        Collections.sort(annotEntityList, new OffsetComparator());

        // Token
        AnnotationSet annotTokenSet = annotSetGenToken.get("Token");
        List<Annotation> annotTokenList = new ArrayList<>(annotTokenSet);
        Collections.sort(annotTokenList, new OffsetComparator());

        // Each token with feature = O
        for (Annotation Token : annotTokenList) {
            Token.getFeatures().put(this.TokenFeatureBIO, "O");
        }

        // Annotate entities
        for (Annotation Entity : annotEntityList) {
            AnnotationSet TokensInEntity = annotTokenSet.get(Entity.getStartNode().getOffset(), Entity.getEndNode().getOffset());
            List<Annotation> TokensInEntityList = new ArrayList<>(TokensInEntity);
            Collections.sort(TokensInEntityList, new OffsetComparator());
            int i = 0;

            for (Annotation Token : TokensInEntityList) {

                if (i == 0) {
                    Token.getFeatures().remove(this.TokenFeatureBIO);
                    Token.getFeatures().put(this.TokenFeatureBIO, "B");
                    i++;
                    
                } else {
                    Token.getFeatures().remove(this.TokenFeatureBIO);
                    Token.getFeatures().put(TokenFeatureBIO, "I");
                }

                
            }
        }

    }

    public Resource init() throws ResourceInstantiationException {
        return this;
    }
    public void reInit() throws ResourceInstantiationException {

    }

    
    public String getTokenFeatureBIO() {
        return TokenFeatureBIO;
    }
  
    public String getInputAnnotation() {
        return InputAnnotation;
    }

    @CreoleParameter
    public void setInputAnnotation(String GoldStandardAnnotationName) {
        this.InputAnnotation = GoldStandardAnnotationName;
    }

    @CreoleParameter
    public void setTokenFeatureBIO(String TokenFeatureBIO) {
        this.TokenFeatureBIO = TokenFeatureBIO;
    }

    public String getAnnotationSetToken() {
        return AnnotationSetToken;
    }

    @CreoleParameter(defaultValue = "")
    public void setAnnotationSetToken(String AnnotationSetToken) {
        this.AnnotationSetToken = AnnotationSetToken;
    }

    public String getAnnotationSetEntity() {
        return AnnotationSetEntity;
    }

    @CreoleParameter(defaultValue = "")
    public void setAnnotationSetEntity(String AnnotationSetEntity) {
        this.AnnotationSetEntity = AnnotationSetEntity;
    }


    
    
    
    
}
