/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import com.sun.jersey.api.NotFoundException;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.api.ApiFactory;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.data.store.MemberDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class AuthorizationFactory {

    public static boolean isMember(HttpSession session, HibernateEntityManager em, String sandbox, String auth_token) throws IOException {
        Long user_id = ApiFactory.getUserId(session, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
        if (user_id != null) {
            return isMember(em, user_id, sandbox, auth_token);
        } else {
            return false;
        }
    }

    public static boolean isMember(HibernateEntityManager em, Long user_id, String sandbox, String auth_token) throws IOException {
        boolean bAccess = false;
        if (em != null) {
            List<LayerDbEntity> lstDb = em.getSession().createCriteria(MemberDbEntity.class).add(Restrictions.eq("user_id", user_id)).list();
            if (lstDb.size() > 0) {
                bAccess = true;
            }
            return bAccess;
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}
