/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.persistence;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.KGConfig;

/**
 *
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    private static String DATA_PU = "dataPU";
    private static PersistenceManager instance;
    private Map<String, EntityManagerFactory> mapEMF = new LinkedHashMap<String, EntityManagerFactory>();

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }


    public void closeEntityManagerFactories() {
        for(EntityManagerFactory emf: mapEMF.values()){
            emf.close();
        }
        mapEMF.clear();
    }
    
    public HibernateEntityManager getEntityManager(String key){
        EntityManagerFactory emf = mapEMF.get(key);
        if(emf == null){
            emf = createSandboxManagerFactory(key);
            mapEMF.put(key, emf);
        }
        if(emf != null){
           return (HibernateEntityManager) emf.createEntityManager(); 
        }
        throw new WebApplicationException(new Throwable("Can't connect to "  + key), 500);
    }
    
    
    protected synchronized EntityManagerFactory createSandboxManagerFactory(String key) {
        Map prop = new HashMap();
        String url = KGConfig.getConfiguration().db_url;
        prop.put("hibernate.connection.url", url);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DATA_PU, prop);
        return emf;
    }

}