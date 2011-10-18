/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.persistence;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.KGConfig;
import org.kloudgis.data.bean.SearchFactory;

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
        for (EntityManagerFactory emf : mapEMF.values()) {
            emf.close();
        }
        mapEMF.clear();
    }

    public synchronized HibernateEntityManager getEntityManager(String key) {
        EntityManagerFactory emf = null;
        if (key != null) {
            emf = mapEMF.get(key);
            if (emf == null) {
                emf = createSandboxManagerFactory(key);
                mapEMF.put(key, emf);
            }
        }
        if (emf != null) {
            return (HibernateEntityManager) emf.createEntityManager();
        }
        return null;
    }

    protected synchronized EntityManagerFactory createSandboxManagerFactory(final String key) {
        Map prop = new HashMap();
        String url = KGConfig.getConfiguration().db_url;
        prop.put("hibernate.connection.url", url + "/" + key);
        prop.put("hibernate.connection.username", KGConfig.getConfiguration().db_user);
        prop.put("hibernate.connection.password", KGConfig.getConfiguration().db_pwd);
        prop.put("hibernate.search.default.indexBase", "/tmp/search_index/" + key);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DATA_PU, prop);
        if (emf != null) {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX note_gist_ix ON notes USING gist(geo)").executeUpdate();
                em.createNativeQuery("CREATE INDEX feature_gist_ix ON features USING gist(geo)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
            
            System.out.println("build search index for:" + key);
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    SearchFactory.buildSearchIndexFor(key);
                }
            });
            thread.start();
        }
        return emf;
    }

    public Map<String, String> getDefaultProperties() {
        Map<String,String> mapVal = new HashMap();
        mapVal.put("user", KGConfig.getConfiguration().db_user);
        mapVal.put("password", KGConfig.getConfiguration().db_pwd);
        return mapVal;
    }
}