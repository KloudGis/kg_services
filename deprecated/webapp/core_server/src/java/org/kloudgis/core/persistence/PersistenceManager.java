/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.persistence.security.UserDbEntity;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    public static final String ADMIN_PU = "adminPU";
    public static final String DATA_PU = "dataPU";
    private static final PersistenceManager singleton = new PersistenceManager();
    private LinkedHashMap<String, EntityManagerFactory> hashFactory = new LinkedHashMap<String, EntityManagerFactory>();
  
    private ArrayList<IPersistenceUnitListener> arrlListener = new ArrayList();

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        return singleton;
    }

    public HibernateEntityManager getEntityManagerAdmin() {
        if (hashFactory.get(ADMIN_PU) == null) {
            HibernateEntityManager em = getEntityManager(ADMIN_PU);
            Query query = em.createQuery("SELECT u from UserDbEntity u where u.user_name = :uname");
            query = query.setParameter("uname", "admin");

            try {
                UserDbEntity adminUsr = (UserDbEntity) query.getSingleResult();
            } catch (NoResultException e) {
                em.getTransaction().begin();
                query = em.createQuery("SELECT g from GroupDbEntity g where g.name = :gname");
                query = query.setParameter("gname", "admin");
                GroupDbEntity adminGrp = null;
                try {
                    adminGrp = (GroupDbEntity) query.getSingleResult();
                } catch (NoResultException ee) {
                }
                if (adminGrp == null) {
                    adminGrp = new GroupDbEntity();
                    adminGrp.setName("admin");
                    //add privileges...
                }
                String sAdmin = "admin";
                UserDbEntity adminUsr = new UserDbEntity();
                adminUsr.setName(sAdmin);
                adminUsr.setPassword("kwadmin");
                adminUsr.setGroup(adminGrp);
                //add the roles

                query = em.createQuery("SELECT r from UserRoleDbEntity r where r.user_name = :uname");
                query = query.setParameter("uname", "admin");

                List<UserRoleDbEntity> lstRoles = query.getResultList();
                String[] arrRoles = {"manager", "user_role", "admin_role"};
                for (String sRole : arrRoles) {
                    boolean bFound = false;
                    for (UserRoleDbEntity role : lstRoles) {
                        if (role.getUserName().equals(sAdmin) && role.getRoleName().equals(sRole)) {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound) {
                        UserRoleDbEntity newRole = new UserRoleDbEntity();
                        newRole.setUserName(sAdmin);
                        newRole.setRoleName(sRole);
                        em.persist(newRole);
                    }
                }
                em.persist(adminGrp);
                em.persist(adminUsr);
                em.getTransaction().commit();
                return em;
            }
        }
        return getEntityManager(ADMIN_PU);
    }

    public HibernateEntityManager getEntityManagerData() {
        return getEntityManager(DATA_PU);
    }

    public HibernateEntityManager getEntityManager(String namePU) {
        return (HibernateEntityManager) getEntityManagerFactory(namePU).createEntityManager();
    }

    private EntityManagerFactory getEntityManagerFactory(String namePU) {
        if (hashFactory.get(namePU) == null) {
            return createEntityManagerFactory(namePU);
        }
        return hashFactory.get(namePU);
    }

    public void closeEntityManagerFactories() {
        for (EntityManagerFactory emf : hashFactory.values()) {
            if (emf != null) {
                emf.close();
                emf = null;
            }
        }
    }

    protected EntityManagerFactory createEntityManagerFactory(String namePU) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(namePU);
        if (emf != null) {
            hashFactory.put(namePU, emf);
            fireEntityManagerCreated(namePU);
        }
        return emf;
    }

    private void fireEntityManagerCreated(String pu) {
        for(IPersistenceUnitListener ls : (List<IPersistenceUnitListener>)arrlListener.clone()){
            ls.persistenceUnitCreated(pu);
        }
    }

    public void addPersistenceUnitListener(IPersistenceUnitListener listener) {
        arrlListener.add(listener);
    }

    public void removePersistenceUnitListener(IPersistenceUnitListener listener) {
        arrlListener.remove(listener);
    }
}
