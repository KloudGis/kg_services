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
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.LoginFactory;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.UserDbEntity;

/**
 *
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    private static String ADMIN_PU = "adminPU";
    private static PersistenceManager instance;
    public static int COMMIT_BLOCK = 30;
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
            adminFactory = createEntityManagerFactory(ADMIN_PU);
            HibernateEntityManager emAdmin = (HibernateEntityManager) adminFactory.createEntityManager();
            Criteria crit = emAdmin.getSession().createCriteria(UserDbEntity.class);
            Long lCount = ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).longValue();
            if (lCount.longValue() == 0) {
                SignupUser usr = new SignupUser();
                usr.user = "admin@kloudgis.com";
                usr.pwd = LoginFactory.hashString("kwadmin", "SHA-256");
                //for debug purpose, the hashed pwd is: 47537f03d101665fe215ba4b92c81430bfa1935e1843adfbcb53ebbd05a09576
                LoginFactory.register(usr, UserDbEntity.ROLE_ADM);
            }
            return (HibernateEntityManager) adminFactory.createEntityManager();
        }
    }


    public void closeEntityManagerFactories() {
        if (adminFactory != null) {
            adminFactory.close();
            adminFactory = null;
        }
    }

    protected EntityManagerFactory createEntityManagerFactory(String namePU) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(namePU);
        return emf;
    }

}