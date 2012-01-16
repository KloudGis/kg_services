/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.sandbox;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.core.pojo.SignupUser;
import org.kloudgis.sandbox.store.SandboxDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class AuthorizationFactory {
    
    
    public static Long getUserId(@Context ServletContext sContext, String auth_token) throws IOException {
        Long user_id = ApiFactory.getUserId(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
        return user_id;
    }

    public static SignupUser getUser(@Context ServletContext sContext, String auth_token) throws IOException {
        SignupUser usr = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
        return usr;
    }

    public static SandboxDbEntity getSandboxFromKey(String key, HibernateEntityManager em) {
        try {
            return (SandboxDbEntity) em.getSession().createCriteria(SandboxDbEntity.class).add(Restrictions.eq("unique_key", key)).uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }
}
