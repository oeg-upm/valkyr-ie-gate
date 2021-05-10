/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr.role;

import gate.Annotation;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
@CreoleResource(name = "RoleCorefence", comment = "Associates a role to other entities with the same span text")
public class RoleSelector extends AbstractLanguageAnalyser{
    
    private String EntityType;
    private String EntityRole;
    private String AnnotationSet;
    final static Logger logger = Logger.getLogger(RoleSelector.class);
    
    
    @Override
    public void execute() throws ExecutionException {
  
       
        logger.debug("Associating Role: "+this.EntityType+" "+this.EntityRole+" in "+this.document.getName());
        
        String inputASName = this.getAnnotationSet();
        if (inputASName == null) {
            inputASName = "";
        }
        AnnotationSet annotSetGen = document.getAnnotations(inputASName);
        
        
        AnnotationSet RoleEntities= annotSetGen.get(this.EntityRole);
        /*
        // GateFactoryInterface.createJAPERule(TOKEN_STRING_FEATURE_NAME, name, PLUGIN_DIR)
        /// PATRON MEDDRA Clean
        FeatureMap fmJape= Factory.newFeatureMap();
        fmJape.put("encoding", "UTF-8");
        fmJape.put("outputASName", "");   
        fmJape.put("inputASName", "");   
        fmJape.put("grammarURL",new File("").toURI().toURL() );   
	
        try {
            Transducer JapeRule;
            JapeRule = (Transducer)  Factory.createResource("gate.creole.Transducer", fmJape);
            JapeRule.setName("Hola");
            JapeRule.setDocument(document);
            JapeRule.execute();
        } catch (ResourceInstantiationException ex) {
            java.util.logging.Logger.getLogger(RoleCorefence.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
       
        for(Annotation RoleAnnotation: RoleEntities){
            
            
            searchEntityTypeSimilarities(RoleAnnotation, annotSetGen);
        
            
        
        }
        
        cleanDuplicateRoleAnnotations(annotSetGen);
        


    }
    
    private void cleanDuplicateRoleAnnotations(AnnotationSet annotSetGen) {
        
        HashSet <String> EntitySet=new HashSet();
        List <Annotation> EntitiesToRemove=new ArrayList();
        AnnotationSet SetRole= annotSetGen.get(this.EntityRole);
        
        for(Annotation EntityRole: SetRole){
           
            Long startRole = EntityRole.getStartNode().getOffset();
            Long endRole = EntityRole.getEndNode().getOffset();
        
            String line=String.valueOf(startRole)+"-"+String.valueOf(endRole);
            if(!EntitySet.contains(line)){
            
                EntitySet.add(line);
            }else{
                EntitiesToRemove.add(EntityRole);
                
            }
        }
        
        annotSetGen.removeAll(EntitiesToRemove);
        
    }
    
   
    
    
    public void searchEntityTypeSimilarities(Annotation RoleAnnotation,AnnotationSet annotSetGen){
    
        String RoleText = RoleAnnotation.getFeatures().get("string").toString();
        AnnotationSet TypeEntities= annotSetGen.get(this.EntityType);
        
        for(Annotation AnnotationType: TypeEntities){
        
            String AnnotText= AnnotationType.getFeatures().get("string").toString();
            if(compareAnnotationString(RoleText,AnnotText)){
                createAnnotation(AnnotationType,RoleAnnotation, annotSetGen);
            }
        
        }
        
        
    
    }
    
    public boolean compareAnnotationString(String AnnotationText1, String AnnotationText2){
    
        String s1= AnnotationText1.toLowerCase();
        String s2= AnnotationText2.toLowerCase();
        
        return s1.equals(s2);

    }
    
    
    private void createAnnotation(Annotation EntityTypeAnnotation, Annotation EntityRoleAnnotation, AnnotationSet annotSetGen) {
    
        try {
            Long startRole = EntityRoleAnnotation.getStartNode().getOffset();
            Long endRole = EntityRoleAnnotation.getEndNode().getOffset();
            
            Long startType = EntityTypeAnnotation.getStartNode().getOffset();
            Long endType = EntityTypeAnnotation.getEndNode().getOffset();
            
            if((startRole==startType)&&(endRole==endType)){return;}
            
            
            
            FeatureMap fm = Factory.newFeatureMap();

            fm = EntityTypeAnnotation.getFeatures();
            fm.put("provenance", "roleassociator");
            Integer id = annotSetGen.add(
                    startType, endType,
                    EntityRoleAnnotation.getType(),
                    fm);

            // String TextInDocument = gate.Utils.stringFor(document, start, end).replaceAll("\n", " ");
        } catch (InvalidOffsetException ex) {
           logger.error("Error creating annotation",ex);
        }


    }

	
    @Override
    public Resource init() throws ResourceInstantiationException {
	return this;
    }

	
    @Override
    public void reInit() throws ResourceInstantiationException {
    }
    

  
    
    
    
    
    public String getEntityType() {
    return EntityType;
    }
    
    @RunTime
    @CreoleParameter(defaultValue = "Organization")
    public void setEntityType(String EntityType) {
    this.EntityType = EntityType;
    }
    
    
    public String getEntityRole() {
    return EntityRole;
    }
    
    @RunTime
    @CreoleParameter(defaultValue = "Offshore")
    public void setEntityRole(String EntityRole) {
    this.EntityRole = EntityRole;
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

}
