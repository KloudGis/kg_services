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
import org.kloudgis.core.persistence.security.UserDbEntity;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    public static final String ADMIN_PU = "useradminPU";
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
            String sAdmin = "admin@kloudgis.org";
            Query query = em.createQuery("SELECT u from UserDbEntity u where u.email = :m");
            query = query.setParameter("m", sAdmin);
            try {
                UserDbEntity adminUsr = (UserDbEntity) query.getSingleResult();
            } catch (NoResultException e) {
                System.out.println("User admin not found.  Let's create it.");
                em.getTransaction().begin();              
                
                UserDbEntity adminUsr = new UserDbEntity();
                adminUsr.setEmail(sAdmin);
                adminUsr.setPassword("kwadmin");
                adminUsr.setSuperUser(true, em);
                //add the roles
                query = em.createQuery("SELECT r from UserRoleDbEntity r where r.email = :email");
                query = query.setParameter("email", sAdmin);

                List<UserRoleDbEntity> lstRoles = query.getResultList();
                String[] arrRoles = {"manager", "user_role", "admin_role"};
                for (String sRole : arrRoles) {
                    boolean bFound = false;
                    for (UserRoleDbEntity role : lstRoles) {
                        if (role.getEmail().equals(sAdmin) && role.getRoleName().equals(sRole)) {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound) {
                        System.out.println("User admin ROLE not found (" + sRole + ").  Let's create it.");
                        UserRoleDbEntity newRole = new UserRoleDbEntity();
                        newRole.setEmail(sAdmin);
                        newRole.setRoleName(sRole);
                        em.persist(newRole);
                    }
                }
                em.persist(adminUsr);
                em.getTransaction().commit();
                return em;
            }
        }
        return getEntityManager(ADMIN_PU);
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
