/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.evaluation;

/**
 *
 * @author pcalleja
 */
public class EvaluationResult {
    
    
    public double StrinctPrecision;
    public double StrictRecall;
    public double StrictF;
    
    public double LatentPrecision;
    public double LatentRecall;
    public double LatentF;
    
    public String Name;
    
    
    public EvaluationResult(){
    
    
    }
    
    public void parse(Object[] array) {

        if (array.length > 10) {

            StrinctPrecision = Double.valueOf(array[5].toString());
            StrictRecall = Double.valueOf(array[6].toString());
            StrictF = Double.valueOf(array[7].toString());

            LatentPrecision = Double.valueOf(array[8].toString());
            LatentRecall = Double.valueOf(array[9].toString());
            LatentF = Double.valueOf(array[10].toString());

        }
    }
    
    
    public String logPrinted(){
    
        return Name+"\t"+"\t"+ 
               "Strict:" +" P:"+ StrinctPrecision+", R:"+this.StrictRecall+", F:"+this.StrictF+
                "\t Lenient:"+" P:"+LatentPrecision+" R:"+this.LatentRecall+" F:"+this.LatentF;
        
    }
    
    public void printResult(){
    
        System.out.println(Name+ 
                " - sP:"+StrinctPrecision+" sR:"+this.StrictRecall+" sF:"+this.StrictF+
                "\t lP:"+LatentPrecision+" lR:"+this.LatentRecall+" lF:"+this.LatentF);
    }
    
    public void printResultShort(){
    
        System.out.println(Name+"\t\t"+
                " - sP:"+StrinctPrecision+" sR:"+this.StrictRecall+
                "\t\t lP:"+LatentPrecision+" lR:"+this.LatentRecall);
    }
    
    
    public String getStrict4Latex(){
        
        String s=  StrinctPrecision+" & "+this.StrictRecall+" & "+this.StrictF;
        return s;
    
    }
    
     public String getlatent4Latex(){
        
        String s=   LatentPrecision+" & "+this.LatentRecall+" & "+this.LatentF;
        return s;
    
    }
    
}
