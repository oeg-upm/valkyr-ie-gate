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
@CreoleResource(name = "CoRefence Role Analyzer", comment = "Associates a role to other entities with the same span text")
public class CoReferenceRoleAnalyzer extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(RoleIdentifierRCM.class);
    
    
    private String AnnotationSet;
    private RCM RoleClassModel;
    
    
    @Override
    public void execute() throws ExecutionException {
  
        
        for (Role rol : this.RoleClassModel.RoleList) {
        
            AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
            this.cleanDuplicateRoleAnnotations(annotSetGen,  rol.RoleName);
        
        }
        correference();
      

        selector();
        
        
    }
    
    
    public void selector(){
    
        
        String entityType = this.RoleClassModel.Entitytye.EntityTypeName;
        AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
        AnnotationSet TypeEntities = annotSetGen.get(entityType);
        
        
        for (Annotation EntityTypeAnnotation : TypeEntities) {

            String OrgName= gate.Utils.cleanStringFor(document, EntityTypeAnnotation)+" "+EntityTypeAnnotation.getStartNode().getOffset().toString(); 
            List<AnnotationSet> Roles= new ArrayList();
            
            for (Role rol : this.RoleClassModel.RoleList) {

                AnnotationSet RoleEntities = annotSetGen.get(rol.RoleName);
                AnnotationSet InsideRoleAnnotations  = RoleEntities.get(EntityTypeAnnotation.getStartNode().getOffset(), EntityTypeAnnotation.getEndNode().getOffset());
                
                
                if(!InsideRoleAnnotations.isEmpty()){
                    
                    Roles.add(InsideRoleAnnotations);
                }
                
                
               
              
            }
            
            if (Roles.size() > 1) {

                System.out.println("Selecting " + this.document.getName() + " " + OrgName + " - " + Roles.size());
                AnnotationSet winner = null;
                String winnerString = "";

                for (AnnotationSet RolSet : Roles) {

                    //coger el primero
                    Annotation anot = null;
                    for (Annotation anot1 : RolSet) {
                        anot = anot1;
                        break;
                    }

                    String prov = null;
                    //System.out.println(anot);

                    try {
                        //System.out.println(">>>>");
                        winnerString = anot.getType();
                        prov = anot.getFeatures().get("provenance").toString();
                        
                        
                        prov="foundprov";
                      
                        //System.out.println();
                    } catch (Exception e) {
                        
                    }

                    if (prov == null) {
                        System.out.println("Direct rule detected: getting ");
                        winner = RolSet;
                        break;
                    }

                }

                if (winner == null) {
                    winner = Roles.get(0);
                    System.out.println("Not direct rule detected, getting first in prioriy");
                    Annotation anot = null;
                    for (Annotation anot1 : winner) {
                        anot = anot1;
                        
                        break;
                    }

                    winnerString = anot.getType();

                }

                System.out.println("Winner " + winnerString);

                // delete all
                for (AnnotationSet Rol : Roles) {
                    if (!Rol.equals(winner)) {
                        annotSetGen.removeAll(Rol);
                    }

                }

            }


        }
    
    }
    
    
    public void correference(){
    
        String entityType = this.RoleClassModel.Entitytye.EntityTypeName;
        
        for (Role rol : this.RoleClassModel.RoleList) {

            AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
            AnnotationSet RoleEntities = annotSetGen.get(rol.RoleName);
            
            HashSet <String> SetofIdentifedRoles= new HashSet();
            HashSet <String> SetofIdentifedRolesNames= new HashSet();
            
            for (Annotation RoleAnnotation : RoleEntities) {
                
                 Long startRole = RoleAnnotation.getStartNode().getOffset();
                 Long endRole = RoleAnnotation.getEndNode().getOffset();
                 String RoleText= gate.Utils.cleanStringFor(document, RoleAnnotation);
                 
                 SetofIdentifedRoles.add(startRole+"-"+endRole);
                 SetofIdentifedRolesNames.add(RoleText.toLowerCase());
            }
            
            AnnotationSet TypeEntities= annotSetGen.get(entityType);
        
            for(Annotation AnnotationType: TypeEntities){
        
            //String AnnotText= AnnotationType.getFeatures().get("string").toString();
            String AnnotText= gate.Utils.cleanStringFor(document, AnnotationType).toLowerCase();
            
                if(SetofIdentifedRolesNames.contains(AnnotText)){


                     Long startType = AnnotationType.getStartNode().getOffset();
                     Long endType = AnnotationType.getEndNode().getOffset();
                     //String TextType= gate.Utils.stringFor(document, AnnotationType);

                     if(!SetofIdentifedRoles.contains(startType+"-"+endType)){
                     
                         
                           try {
          
                             FeatureMap fm = Factory.newFeatureMap();

                             fm = AnnotationType.getFeatures();
                             fm.put("provenance", "roleassociator");
                             Integer id = annotSetGen.add(
                                     startType, endType,
                                     rol.RoleName,
                                     fm);

                             // String TextInDocument = gate.Utils.stringFor(document, start, end).replaceAll("\n", " ");
                         } catch (InvalidOffsetException ex) {
                             logger.error("Error creating annotation", ex);
                         }
                         
                         
                     }


                }

            }

            

            /*
            for (Annotation RoleAnnotation : RoleEntities) {

                searchEntityTypeSimilarities(RoleAnnotation, annotSetGen, entityType);

            }

            cleanDuplicateRoleAnnotations(annotSetGen, rol.RoleName);
            */
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
    
        String RoleText= gate.Utils.stringFor(document, RoleAnnotation);
        //String RoleText = RoleAnnotation.getFeatures().get("string").toString();
        AnnotationSet TypeEntities= annotSetGen.get(EntityTypeName);
        
        for(Annotation AnnotationType: TypeEntities){
        
            //String AnnotText= AnnotationType.getFeatures().get("string").toString();
            String AnnotText= gate.Utils.stringFor(document, AnnotationType);
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
        
        if(this.RoleClassModel.isLinguistic){
        this.RoleClassModel.initRolesWithRules(this.AnnotationSet);
        }
        
        
	return this;
    }

	
    @Override
    public void reInit() throws ResourceInstantiationException {
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
