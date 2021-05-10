/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.evaluation;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.util.AnnotationDiffer;
import gate.util.ClassificationMeasures;
import gate.util.OntologyMeasures;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pcalleja
 */
public class NEREvaluation {
    
    
    protected DefaultTableModel documentTableModel;
    
    private String AnnotationSetResults;
    private String AnnotationSetGold;
    protected ArrayList<HashMap<String, AnnotationDiffer>> differsByDocThenType = new ArrayList<HashMap<String, AnnotationDiffer>>();
    
    public static List <EvaluationResult> EvaluationResults2=new ArrayList();
    
    public NEREvaluation(String resultSet, String goldSet){
    
        this.AnnotationSetResults= resultSet;
        this.AnnotationSetGold=goldSet;
    }
    
    public  EvaluationResult evaluateAnnotation( String Name,String annotationName, Corpus corpus){
    
        Object[] measures2 = new Object[]{"F1.0-score strict", "F1.0-score lenient","F1.0-score average"};
        documentTableModel = new DefaultTableModel();
        documentTableModel.addColumn("Document");
        
        documentTableModel.addColumn("Match");
        documentTableModel.addColumn("Only A");
        documentTableModel.addColumn("Only B");
        documentTableModel.addColumn("Overlap");
        for (Object measure : measures2) {
          String measureString = ((String) measure)
            .replaceFirst("score strict", "s.")
            .replaceFirst("score lenient", "l.")
            .replaceFirst("score average", "a.")
            .replaceFirst(" BDM", "B.");
          documentTableModel.addColumn("Prec.B/A");
          documentTableModel.addColumn("Rec.B/A");
          documentTableModel.addColumn(measureString);
        }
    
        final int FSCORE_MEASURES = 0;
        ArrayList<String> documentNames = new ArrayList<String>();
        TreeSet<String> types = new TreeSet<String>();
        Set<String> features = new HashSet<String>();

        int measuresType = FSCORE_MEASURES;
        Object[] measures = new Object[]{"F1.0-score strict", "F1.0-score lenient","F1.0-score average"};
        String keySetName = AnnotationSetGold;
        String responseSetName = AnnotationSetResults;
        types.add(annotationName);
        //features.add("gender"); 
        
      
        differsByDocThenType.clear();
        
        // for each document 
        for (int row = 0; row < corpus.size(); row++) {
            
            
            boolean documentWasLoaded = corpus.isDocumentLoaded(row);
            Document document = (Document) corpus.get(row);
            documentNames.add(document.getName());
            Set<Annotation> keys = new HashSet<Annotation>();
            Set<Annotation> responses = new HashSet<Annotation>();
            // get annotations from selected annotation sets 
            keys = document.getAnnotations(keySetName);
            responses = document.getAnnotations(responseSetName);
            if (!documentWasLoaded) { // in case of datastore 
                corpus.unloadDocument(document);
                Factory.deleteResource(document);
            }

            // fscore document table 
            if (measuresType == FSCORE_MEASURES) {
                HashMap<String, AnnotationDiffer> differsByType = new HashMap<String, AnnotationDiffer>();
                AnnotationDiffer differ;
                Set<Annotation> keysIter = new HashSet<Annotation>();
                Set<Annotation> responsesIter = new HashSet<Annotation>();
                for (String type : types) {
                    if (!keys.isEmpty() && !types.isEmpty()) {
                        keysIter = ((AnnotationSet) keys).get(type);
                    }
                    if (!responses.isEmpty() && !types.isEmpty()) {
                        responsesIter = ((AnnotationSet) responses).get(type);
                    }
                    differ = new AnnotationDiffer();
                    differ.setSignificantFeaturesSet(features);
                    differ.calculateDiff(keysIter, responsesIter); // compare 
                    differsByType.put(type, differ);
                }
                
                differsByDocThenType.add(differsByType); 
                differ = new AnnotationDiffer(differsByType.values());
                List<String> measuresRow;
                
             
                // added
                measuresRow = differ.getMeasuresRow(measures,     documentNames.get(documentNames.size() - 1));
                documentTableModel.addRow(measuresRow.toArray());
                // System.out.println(Arrays.deepToString(measuresRow.toArray()));

                // classification document table 
            } 
        }
        
        
        List<AnnotationDiffer> differs = new ArrayList<AnnotationDiffer>();
        for (Map<String, AnnotationDiffer> differsByType :
              differsByDocThenType) {
          differs.addAll(differsByType.values());
        }
        AnnotationDiffer differ = new AnnotationDiffer(differs);
        EvaluationResult res= evaluateMacroMicro(differ, documentTableModel, 5,   documentTableModel.getRowCount(), measures);
        
        res.Name= Name;
        //EvaluationResults.add(res);
        //res.printResult();
        //printSummary(differ, annotationTableModel, 5,          annotationTableModel.getRowCount(), measureList.getSelectedValues());
        return res;
    }
    
    
    
     protected EvaluationResult evaluateMacroMicro(Object measureObject, DefaultTableModel tableModel, int columnGroupSize, int insertionRow, Object[] measures) {
        AnnotationDiffer differ = null;
        ClassificationMeasures classificationMeasures = null;
        OntologyMeasures ontologyMeasures = null;
        if (measureObject instanceof AnnotationDiffer) {
            differ = (AnnotationDiffer) measureObject;
        } else if (measureObject instanceof ClassificationMeasures) {
            classificationMeasures = (ClassificationMeasures) measureObject;
        } else if (measureObject instanceof OntologyMeasures) {
            ontologyMeasures = (OntologyMeasures) measureObject;
        }
        NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
        f.setMaximumFractionDigits(4);
        f.setMinimumFractionDigits(4);
        f.setRoundingMode(RoundingMode.HALF_UP);
        List<Object> values = new ArrayList<Object>();

        // average measures by document
        values.add("Macro summary");
        for (int col = 1; col < tableModel.getColumnCount(); col++) {
            if (col < columnGroupSize) {
                values.add("");
            } else {
                float sumF = 0;
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    try {
                        sumF += Float.parseFloat((String) tableModel.getValueAt(row, col));
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                }
                values.add(f.format(sumF / tableModel.getRowCount()));
            }
        }
        //System.out.println(Arrays.deepToString(values.toArray()));
        tableModel.insertRow(insertionRow, values.toArray());

        // sum counts and recalculate measures like the corpus is one document
        values.clear();
        values.add("Micro summary");
        for (int col = 1; col < columnGroupSize; col++) {
            int sum = 0;
            for (int row = 0; row < tableModel.getRowCount() - 1; row++) {
                try {
                    sum += Integer.valueOf((String) tableModel.getValueAt(row, col));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
            values.add(Integer.toString(sum));
        }
        if (measureObject instanceof OntologyMeasures) {
            List<AnnotationDiffer> differs = new ArrayList<AnnotationDiffer>(
                    ontologyMeasures.getDifferByTypeMap().values());
            differ = new AnnotationDiffer(differs);
        }
        for (Object object : measures) {
            String measure = (String) object;
            int index = measure.indexOf('-');
            double beta = (index == -1)
                    ? 1 : Double.valueOf(measure.substring(1, index));
            if (measure.endsWith("strict")) {
                values.add(f.format(differ.getPrecisionStrict()));
                values.add(f.format(differ.getRecallStrict()));
                values.add(f.format(differ.getFMeasureStrict(beta)));
            } else if (measure.endsWith("strict BDM")) {
                values.add(f.format(ontologyMeasures.getPrecisionStrictBdm()));
                values.add(f.format(ontologyMeasures.getRecallStrictBdm()));
                values.add(f.format(ontologyMeasures.getFMeasureStrictBdm(beta)));
            } else if (measure.endsWith("lenient")) {
                values.add(f.format(differ.getPrecisionLenient()));
                values.add(f.format(differ.getRecallLenient()));
                values.add(f.format(differ.getFMeasureLenient(beta)));
            } else if (measure.endsWith("lenient BDM")) {
                values.add(f.format(ontologyMeasures.getPrecisionLenientBdm()));
                values.add(f.format(ontologyMeasures.getRecallLenientBdm()));
                values.add(f.format(ontologyMeasures.getFMeasureLenientBdm(beta)));
            } else if (measure.endsWith("average")) {
                values.add(f.format(differ.getPrecisionAverage()));
                values.add(f.format(differ.getRecallAverage()));
                values.add(f.format(differ.getFMeasureAverage(beta)));
            } else if (measure.endsWith("average BDM")) {
                values.add(f.format(ontologyMeasures.getPrecisionAverageBdm()));
                values.add(f.format(ontologyMeasures.getRecallAverageBdm()));
                values.add(f.format(ontologyMeasures.getFMeasureAverageBdm(beta)));
            } else if (measure.equals("Observed agreement")) {
                values.add(f.format(classificationMeasures.getObservedAgreement()));
            } else if (measure.equals("Cohen's Kappa")) {
                float result = classificationMeasures.getKappaCohen();
                values.add(Float.isNaN(result) ? "" : f.format(result));
            } else if (measure.equals("Pi's Kappa")) {
                float result = classificationMeasures.getKappaPi();
                values.add(Float.isNaN(result) ? "" : f.format(result));
            }
        }
        tableModel.insertRow(insertionRow + 1, values.toArray());
     
        //System.out.println(Arrays.deepToString(values.toArray()));
        // System.out.println("\n\n");
         
         EvaluationResult res= new EvaluationResult();
         res.parse(values.toArray());
         return res;

    }
    
}
