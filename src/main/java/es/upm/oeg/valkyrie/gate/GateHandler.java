/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate;

import gate.Factory;
import gate.Gate;
import gate.Resource;
import gate.gui.MainFrame;
import gate.util.GateException;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;


/**
 *
 * @author pcalleja
 */
public class GateHandler {
    
    final static Logger logger = Logger.getLogger(GateHandler.class);
    
    private static String GazetteerPath;
    
    private static String JapeRulesPath;
    
    
    public static void initGate(String ResourcesDir,boolean ShowGui,String ... Plugins) throws InterruptedException, InvocationTargetException, GateException, MalformedURLException{
        
        logger.info("Initializing GATE in folder "+ResourcesDir);
     
        if(Gate.isInitialised())return;
        
        init(ResourcesDir);
        
        if(ShowGui){showGateGUI();}
        
        for(String Plugin: Plugins){
            logger.info("Adding Plugin "+Plugin);
            GateHandler.registrerHomePlugin(Plugin);
        }
    
    }
    
    public static void initGateWeb(File GateXML, boolean ShowGui,String ... Plugins) throws InterruptedException, InvocationTargetException, GateException, MalformedURLException{
        
        logger.info("Initializing GATE in folder "+GateXML.getAbsolutePath());
        logger.info("Initializing GATE in folder "+GateXML.getParentFile().getAbsolutePath());
     
        if(Gate.isInitialised())return;
        
         try {
            logger.info("Initializing GATE");
            Gate.setGateHome(GateXML.getParentFile()); // + File.separator + "GateHome")
            Gate.setPluginsHome(new File(Gate.getGateHome()+File.separator+"Plugins"));
            Gate.setSiteConfigFile(GateXML);
            Gate.setUserConfigFile(GateXML);
            //Gate.setUserSessionFile(new File(Gate.getGateHome() + File.separator + "gate.session"));
            FileInputStream s;
            Gate.init();
            
            
            logger.info("GATE initialised");
        } catch (GateException ex) {
           logger.error(ex); 
        }

        
        if(ShowGui){showGateGUI();}
        
        for(String Plugin: Plugins){
            logger.info("Adding Plugin "+Plugin);
            GateHandler.registrerHomePlugin(Plugin);
        }
    
    }
    
    

    public static void init(String ResourcesDir){
    
        try {
            logger.info("Initializing GATE");
            Gate.setGateHome(new File(ResourcesDir + File.separator + "GateHome"));
            Gate.setPluginsHome(new File(Gate.getGateHome()+File.separator+"Plugins"));
            Gate.setSiteConfigFile(new File(Gate.getGateHome() + File.separator + "gate.xml"));
            Gate.setUserConfigFile(new File(Gate.getGateHome()+ File.separator + "gate.xml"));
            Gate.setUserSessionFile(new File(Gate.getGateHome() + File.separator + "gate.session"));
            
            GazetteerPath = Gate.getGateHome().getAbsolutePath()+ File.separator+ "Gazetteer";
            JapeRulesPath = Gate.getGateHome().getAbsolutePath()+ File.separator+ "JAPE";
            Gate.init();
            
            
            logger.info("GATE initialised");
        } catch (GateException ex) {
           logger.error(ex); 
        }

    }
    
    
    
    
    
    public static void registrerHomePlugin(String PluginName) throws GateException, MalformedURLException{
    
        Gate.getCreoleRegister().registerDirectories(new File( Gate.getPluginsHome().getAbsolutePath() +File.separator+PluginName).toURI().toURL());
    
    }
    
    
    public static void registrerPlugin(String PluginDir) throws GateException, MalformedURLException{
    
        Gate.getCreoleRegister().registerDirectories(new File( PluginDir).toURI().toURL());
    
    }
    
    public static void showGateGUI() throws InterruptedException, InvocationTargetException{
    
    
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
            MainFrame.getInstance().setVisible(true) ;
                    }
            }) ;
    }
    
    
    public static String getPluginsHomeDir(){
        return Gate.getPluginsHome().getAbsolutePath();
    }

    public static String getGazetteerPath() {
        return GazetteerPath;
    }

    public static String getJapeRulesPath() {
        return JapeRulesPath;
    }
    
    
    public static boolean isGateInitalised(){
        return Gate.isInitialised();
    }
    
    
    public static void deleteResource(Resource rsc){
    
        Factory.deleteResource(rsc);
    
    }
    
    public static URL getPluginURL(String Name){
    
        
        Iterator it= Gate.getCreoleRegister().getDirectories().iterator();
        URL DesiredURL=null;
        while(it.hasNext()){
            URL URLAux= (URL) it.next();
            
            if(URLAux.getFile().substring(0, URLAux.getFile().length()-1).endsWith(Name)){
                DesiredURL=URLAux;
                return DesiredURL;
            }
        }
        
        return DesiredURL;
    
    }
    
}
