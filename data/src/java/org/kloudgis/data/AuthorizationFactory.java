/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.data.store.MemberDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class AuthorizationFactory {

    public static MemberDbEntity getMember(ServletContext sContext, HibernateEntityManager em, String sandbox, String auth_token) throws IOException {
        Long user_id = ApiFactory.getUserId(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
        if (user_id != null) {
            return getMember(em, user_id, sandbox, auth_token);
        } else {
            return null;
        }
    }

    public static MemberDbEntity getMember(HibernateEntityManager em, Long user_id, String sandbox, String auth_token) throws IOException {
        if (em != null) {
            List<MemberDbEntity> lstDb = em.getSession().createCriteria(MemberDbEntity.class).add(Restrictions.eq("user_id", user_id)).list();
            if (lstDb.size() > 0) {
                return lstDb.get(0);
            }
            return null;
        } else {
            throw new IOException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    public static boolean isSandboxOwner(MemberDbEntity lMember, ServletContext sContext, String auth_token, String sandbox) throws IOException{
        Long id = ApiFactory.getSandboxOwner(sContext, auth_token, KGConfig.getConfiguration().sandbox_url + "/sandbox_owner?sandbox=" + sandbox, KGConfig.getConfiguration().api_key);
        if(id != null && id.longValue() == lMember.getUserId().longValue()){
            return true;
        }
        return false;
    }
}
