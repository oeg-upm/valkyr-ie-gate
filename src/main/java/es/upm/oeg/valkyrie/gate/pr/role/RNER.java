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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Pablo
 */

@CreoleResource(name = "Role-based NER", comment = "")
public class RNER extends AbstractLanguageAnalyser{
    
    private static final long serialVersionUID = -5516939361964231508L;
    final static Logger logger = Logger.getLogger(RoleIdentifierRCM.class);
    
    
    private String AnnotationSet;

    private RCH RoleClassificationHierarchy;
    
    
    @Override
    public void execute() throws ExecutionException {
 
        try {
            
            
            // NAMED ENTITY IDENTIFIER
            
            this.RoleClassificationHierarchy.Type.execute(document);
            

            // ROLE IDENTIFIER
            for(NERole rol: this.RoleClassificationHierarchy.Roles){
            
                rol.execute(document);
            
            }
            
            System.out.println("Correferencia");
            // CORREFERNCE
       
          
            for (NERole rol : this.RoleClassificationHierarchy.Roles) {
                
                AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
                //this.cleanDuplicateRoleAnnotations(annotSetGen,  rol.Name);
                
            }
            
            correference();
            //correferenceOld();
            
          //  selectorOld();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
    }
    
    
    
    
    private List <Annotation> findEntityMentions(Annotation EntityAnnotation,AnnotationSet annotSetGen){
    
             List <Annotation> MentionsList=new ArrayList();   
             String EntityString = gate.Utils.cleanStringFor(document, EntityAnnotation.getStartNode().getOffset(),EntityAnnotation.getEndNode().getOffset());
    
             AnnotationSet Entities = annotSetGen.get(this.RoleClassificationHierarchy.Type.Name);
              
             for(Annotation annot: Entities){
             
                 if(annot.equals(EntityAnnotation)){continue;}
                 
                  String MentionEntityString = gate.Utils.cleanStringFor(document, annot.getStartNode().getOffset(),annot.getEndNode().getOffset());
                    if(EntityString.toLowerCase().equals(MentionEntityString.toLowerCase())){
                        MentionsList.add(annot);
                    }
             }
                           
             return MentionsList;
    
    
    }
    
    
    
    //AssignedRoles $\leftarrow$ retrieveEntityRoles(entity,d) 
    private List<Integer> retrieveEntityRoles(Annotation EntityAnnotation, AnnotationSet annotSetGen){

        List<Integer> lis = new ArrayList();
        for (NERole role : this.RoleClassificationHierarchy.Roles) {
            AnnotationSet RolesEntities = annotSetGen.get(role.Name, EntityAnnotation.getStartNode().getOffset(), EntityAnnotation.getEndNode().getOffset());
            if (!RolesEntities.isEmpty()) {
                lis.add(this.RoleClassificationHierarchy.Roles.indexOf(role));

            }

        }

        return lis;
    
    
    
    }
    
    
    //AssignedRoles $\leftarrow$ retrieveEntityRoles(entity,d) 
    private void associateRoles(List <Integer> Roles,  Annotation EntityAnnotation, AnnotationSet annotSetGen, boolean type){

       
        for(Integer roleNum: Roles){
            
                NERole role= this.RoleClassificationHierarchy.Roles.get(roleNum);
                AnnotationSet EntityRole = annotSetGen.get(role.Name,EntityAnnotation.getStartNode().getOffset(),EntityAnnotation.getEndNode().getOffset());
                
                System.out.println(EntityRole.size() + " "+role.Name);
                
                if(EntityRole.isEmpty()){
                    System.out.println("creating correfernce role" );
                    System.out.println(gate.Utils.cleanStringFor(document,EntityAnnotation) +"   "+role.Name);
                    FeatureMap fm = Factory.newFeatureMap();

                        try{
                             fm = EntityAnnotation.getFeatures();
                             fm.put("provenance", "correference");
                             Integer id = annotSetGen.add(
                                     EntityAnnotation.getStartNode().getOffset(),EntityAnnotation.getEndNode().getOffset(),
                                     role.Name,
                                     fm);
                        }catch( Exception e){
                            System.out.println("error creating correfernce annotation role");
                        }      
                
                     
                
                
                
                }
        
                
            
            
            }
       
    
    }
    
    
    
    
    public void correference() {

        boolean equalityOfRoles=true;
        
        String entityType = this.RoleClassificationHierarchy.Type.Name;

        AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
        AnnotationSet TypeEntities = annotSetGen.get(entityType);
        List <Annotation> NamedEntitiesList = TypeEntities.inDocumentOrder();


        for (Annotation EntityAnnot : NamedEntitiesList){
        
            List <Integer> listAssignedRoles = this.retrieveEntityRoles(EntityAnnot, annotSetGen);
            List <Annotation> listEntityMentions= this.findEntityMentions(EntityAnnot, annotSetGen);
            
            System.out.println("For "+gate.Utils.cleanStringFor(document,EntityAnnot)+"  Roles: "+listAssignedRoles.size() +" Mentions "+listEntityMentions.size() );
            if(listAssignedRoles.isEmpty()) {continue;}
            
            
            for(Annotation MentionAnnot: listEntityMentions){
                List <Integer> listAssignedMentionRoles = this.retrieveEntityRoles(MentionAnnot, annotSetGen);
                
                System.out.println( "mention " +gate.Utils.cleanStringFor(document,EntityAnnot)+"  " + MentionAnnot.getStartNode().getOffset()+"  "+MentionAnnot.getStartNode().getOffset());
                if(listAssignedMentionRoles.isEmpty()){
                    System.out.println("coping annotations empty");
                    associateRoles(listAssignedRoles,   MentionAnnot,  annotSetGen,true);
                } else{
                    if(equalityOfRoles){
                            //System.out.println("coping annotations equality");
                            associateRoles(listAssignedRoles,   MentionAnnot,  annotSetGen,false);
                    
                    }
                
                }
                
            
            }
        
        
        }
       
        System.out.println("second step");
       for (Annotation EntityAnnot : NamedEntitiesList){
        
             List <Integer> listAssignedRoles = this.retrieveEntityRoles(EntityAnnot, annotSetGen);
               
             if(listAssignedRoles.isEmpty()){
                 // PUT DEFAULT
             }
             if(listAssignedRoles.size()>1){
                 
                 List <String>priority =new ArrayList();
                 priority.add("Intermediary");priority.add("Shareholder");priority.add("Offshore");
                 removeRoles(priority, listAssignedRoles,  EntityAnnot ,  annotSetGen);
             }
            
        }
           
        
        
        
        
        

        

        

    }


    private void removeRoles(List <String> RolesPriority, List <Integer> Roles,  Annotation EntityAnnotation, AnnotationSet annotSetGen){

        System.out.println("Removing roles "+gate.Utils.cleanStringFor(document,EntityAnnotation)+"  roles: "+ Roles.size());
           
        HashMap <Integer, String> map= new HashMap();
        List <Integer> priorityRoles= new ArrayList();
        int a=0;
        for(String s: RolesPriority){
            
            for(NERole role:this.RoleClassificationHierarchy.Roles){
                if (role.Name.toLowerCase().equals(s.toLowerCase())){
                    priorityRoles.add(this.RoleClassificationHierarchy.Roles.indexOf(role));
                }
            }
                  
        }
        List <Integer> sortedRoles= new ArrayList();
        for(Integer num: priorityRoles){
            
            int pos= Roles.indexOf(num);
            if(pos>=0){
                sortedRoles.add(num);
            }
        
        }
        
        
        
        for(int i=1;i<sortedRoles.size();i++){
            NERole role= this.RoleClassificationHierarchy.Roles.get(i);
            System.out.println("Removing Role  "+role.Name+"  ");
          
            AnnotationSet EntityRole = annotSetGen.get(role.Name,EntityAnnotation.getStartNode().getOffset(),EntityAnnotation.getEndNode().getOffset());
            annotSetGen.removeAll(EntityRole);
        
        }
        

    }


    
    
    
    /*
      
  \State entities $\leftarrow$ d.NamedEntities
    \ForAll  { entity $\in$ entities}
    \State AssignedRoles $\leftarrow$ retrieveEntityRoles(entity,d)
    \State   EntityMentions $\leftarrow$ findEntityMentions(e,d)
	\ForAll  { EntityMention $\in$ EntityMentions}
    	\State AssignedMentionRoles $\leftarrow$ retrieveEntityRoles(EntityMention,d)    
    	  \If{AssignedMentionRoles==0} 
    	 \State associateRoles(Entity,AssignedRoles,d) 
    	  \ElsIf{Equality\_of\_roles} 
    	  \State associateRoles(Entity,AssignedRoles,d)
    	  \EndIf
    		
      \EndFor
    \EndFor
  
  \ForAll  { entity $\in$ entities}
     \State AssignedRoles $\leftarrow$ retrieveEntityRoles(entity,d)    
    	  \If{AssignedRoles==0 \&\& Default != null} 
    	  
    	  \State associateRole(Entity,Default\_ Role,d) 
    	  \ElsIf{AssignedRoles 	$>$  1}
    	 \State sortedRoles $\leftarrow$ sortRolesByPriority(Assignedoles,Sorted\_Roles) 
    	 \State removeAssociatedRoles(Entity,d)
    	\State associateRoles(Entity,sortedRoles.first,d)
    	  \EndIf
 \EndFor
    
    
    */
    
    public void selectorOld(){
    
        
        String entityType = this.RoleClassificationHierarchy.Type.Name;
        AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
        AnnotationSet TypeEntities = annotSetGen.get(entityType);
        
        
        for (Annotation EntityTypeAnnotation : TypeEntities) {

            String OrgName= gate.Utils.cleanStringFor(document, EntityTypeAnnotation)+" "+EntityTypeAnnotation.getStartNode().getOffset().toString(); 
            List<AnnotationSet> Roles= new ArrayList();
            
            for (NERole rol : this.RoleClassificationHierarchy.Roles) {

                AnnotationSet RoleEntities = annotSetGen.get(rol.Name);
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
    
    
    public void correferenceOld(){
    
        String entityType =  this.RoleClassificationHierarchy.Type.Name;
        
        for (NERole rol : this.RoleClassificationHierarchy.Roles) {

            AnnotationSet annotSetGen = document.getAnnotations(this.AnnotationSet);
            AnnotationSet RoleEntities = annotSetGen.get(rol.Name);
            
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
                                     rol.Name,
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

    
   
    
    public RCH getRoleClassificationHierarchy() {
        return this.RoleClassificationHierarchy;
    }

    @RunTime
    @CreoleParameter()
    public void setRoleClassificationHierarchy(RCH RoleClassificationHierarchy) {
        this.RoleClassificationHierarchy = RoleClassificationHierarchy;
    }
    
    
    
}
