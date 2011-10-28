/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.sandbox.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.ejb.HibernateEntityManager;

/**
 *
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    private static String ADMIN_PU = "adminPU";
    private static PersistenceManager instance;
    private EntityManagerFactory adminFactory;

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public HibernateEntityManager getAdminEntityManager() {
        if (adminFactory != null) {
            return (HibernateEntityManager) adminFactory.createEntityManager();
        } else {
            adminFactory = Persistence.createEntityManagerFactory(ADMIN_PU);
            return (HibernateEntityManager) adminFactory.createEntityManager();
        }
    }


    public void closeEntityManagerFactories() {
        if (adminFactory != null) {
            adminFactory.close();
            adminFactory = null;
        }
    }

}