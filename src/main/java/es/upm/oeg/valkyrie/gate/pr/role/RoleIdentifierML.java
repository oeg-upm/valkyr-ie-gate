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
@CreoleResource(name = "Role Associator", comment = "Associates a role to other entities with the same span text")
public class RoleIdentifierML extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(RoleIdentifierML.class);
    

    
    
    private String AnnotationSet;
    private RCM RoleClassModel;
    
    
    @Override
    public void execute() throws ExecutionException {
  
          // Role Identifier
       this.RoleClassModel.executeRoleML(this.document);
        
       
       // Role Correfernce
      //correference();
       
       
       // Selector
       
       
       
        
        /*
        logger.debug("Associating Role: "+this.EntityType+" "+this.EntityRole+" in "+this.document.getName());
        
        
        String inputASName = this.getAnnotationSet();
        if (inputASName == null) {
            inputASName = "";
        }
        AnnotationSet annotSetGen = document.getAnnotations(inputASName);
        
        
        AnnotationSet RoleEntities= annotSetGen.get(this.EntityRole);
        AnnotationSet TypeEntities= annotSetGen.get(this.EntityType);

       
        for(Annotation RoleAnnotation: RoleEntities){
            
            AnnotationSet EntityTypesInTheRole = TypeEntities.get(RoleAnnotation.getStartNode().getOffset(), RoleAnnotation.getEndNode().getOffset());
            if(EntityTypesInTheRole.size()==0){
                System.out.println("No annotation Inside");
                // remove role
                
            }
            
        
        }
    */


    }
    
    
    
    @Override
    public Resource init() throws ResourceInstantiationException {
	return this;
    }

	
    @Override
    public void reInit() throws ResourceInstantiationException {
    }
    

  
      
    public void correference(){
    
        String entityType= this.RoleClassModel.Entitytye.EntityTypeName;
        for(Role rol: this.RoleClassModel.RoleList){
            
            AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
            AnnotationSet RoleEntities= annotSetGen.get(rol.RoleName);
           
        for(Annotation RoleAnnotation: RoleEntities){
            
            
            searchEntityTypeSimilarities(RoleAnnotation, annotSetGen,entityType);
        
            
        
        }
        
        cleanDuplicateRoleAnnotations(annotSetGen,rol.RoleName);
        }
       
    
    
    }
    private void cleanDuplicateRoleAnnotations(AnnotationSet annotSetGen, String RoleName) {
        
        HashSet <String> EntitySet=new HashSet();
        List <Annotation> EntitiesToRemove=new ArrayList();
        AnnotationSet SetRole= annotSetGen.get(RoleName);
        
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
    
   
    
    
    public void searchEntityTypeSimilarities(Annotation RoleAnnotation,AnnotationSet annotSetGen, String EntityTypeName){
    
        String RoleText = RoleAnnotation.getFeatures().get("string").toString();
        AnnotationSet TypeEntities= annotSetGen.get(EntityTypeName);
        
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
    
   
  
    
    
    
    
    
    
    
    
    public String getAnnotationSet() {
    return AnnotationSet;
    }
    
    //@Optional
    @RunTime
    @CreoleParameter(defaultValue = "")
    public void setAnnotationSet(String AnnotationSet) {
    this.AnnotationSet = AnnotationSet;
    }

    public RCM getRoleClassModel() {
        return RoleClassModel;
    }

    @RunTime
    @CreoleParameter()
    public void setRoleClassModel(RCM RoleClassModel) {
        this.RoleClassModel = RoleClassModel;
    }
    
   

    
    
}
