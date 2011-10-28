/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.auth;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.kloudgis.auth.admin.store.UserDbEntity;

/**
 *
 * @author jeanfelixg
 */
public final class AuthorizationManager {

    public UserDbEntity getUserFromAuthToken(String password_hash, EntityManager em) {
        try {
            System.out.println("Attemp to find user with token " + password_hash);
            UserDbEntity u = em.createQuery("from UserDbEntity where auth_token=:token", UserDbEntity.class).setParameter("token", password_hash).getSingleResult();
            return u;
        } catch (EntityNotFoundException e) {
            System.out.println("Auth token invalid: " + password_hash);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
