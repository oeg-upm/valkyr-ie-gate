/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.pr;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author pcalleja
 */
public class GateGazetteer {
    
    final static Logger logger = Logger.getLogger(GateGazetteer.class);
    
    


    public static void createDEFFile(String filePath, String name, List<String> lines) {

        if (!name.endsWith(".def")) {
            name = name + ".def";
        }

        try {
            PrintWriter writer = new PrintWriter(filePath+File.separator + name, "UTF-8");

            for (String line : lines) {
                writer.println(line);
            }

            writer.close();
        } catch (Exception e) {
            logger.error(e);

        }
    }

    public static String createDEFline(String lltFile, String Major, String Minor, String lang, String Annotation) {

        String line = lltFile + ":" + Major + ":" + Minor + ":" + lang + ":" + Annotation;
        return line;
    }

    

    
    public static void createLSTFile(String filePath, String name, List<String> lines) {


        try {
            PrintWriter writer = new PrintWriter(filePath+File.separator + name, "UTF-8");

            for (String line : lines) {
                writer.println(line);
            }

            writer.close();
        } catch (Exception e) {
            logger.error(e);

        }
    }
    
    
    
    
    
   
}
