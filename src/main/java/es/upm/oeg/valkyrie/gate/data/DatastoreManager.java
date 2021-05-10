/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.gate.data;

import es.upm.oeg.valkyrie.gate.lr.GateCorpus;
import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class DatastoreManager {
    
    
     public SerialDataStore sds;
            
     public DatastoreManager(String Dir) throws MalformedURLException, PersistenceException {

        System.out.println(new File(Dir).toURI().toURL().toString());
        sds = new SerialDataStore(new File(Dir).toURI().toURL().toString());
        sds.open();

    }

    public void openDatabase() throws PersistenceException, ResourceInstantiationException {

        Object lrId = sds.getLrIds("gate.corpora.SerialCorpusImpl").get(0);

        List<String> ids = sds.getLrIds("gate.corpora.SerialCorpusImpl");
        for (String i : ids) {
            System.out.println(i);
        }

        FeatureMap features = Factory.newFeatureMap();
        features.put(DataStore.LR_ID_FEATURE_NAME, lrId);
        features.put(DataStore.DATASTORE_FEATURE_NAME, sds);

        // read the document back 
        Corpus doc = (Corpus) Factory.createResource("gate.corpora.CorpusImpl", features);

    }
    
    public GateCorpus openGateCorpus(String CorpusName ) throws PersistenceException, ResourceInstantiationException, MalformedURLException{
    
        List<String> ids = sds.getLrIds("gate.corpora.SerialCorpusImpl");

        boolean found = false;
        String ident ="";
        for (String id : ids) {
            System.out.println(id);
            if (id.contains(CorpusName)) {
                ident=id;
                found = true;
            }
        }
        if (!found) {
            System.out.println("no entro");
            return null;
        }

        FeatureMap features2 = Factory.newFeatureMap();
        features2.put(DataStore.LR_ID_FEATURE_NAME, ident);
        features2.put(DataStore.DATASTORE_FEATURE_NAME, sds);

        Corpus corp = (Corpus) Factory.createResource("gate.corpora.SerialCorpusImpl", features2);
        
        GateCorpus corpus= new GateCorpus();
        corpus.createCorpusFromDatastore(corp);
        return corpus;
    }

}
