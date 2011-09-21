/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.api.ApiFactory;

/**
 *
 * @author jeanfelixg
 */
public class AuthorizationFactory {
    
     public static Long getUserId(HttpSession session, String auth_token) throws IOException {
        Long user_id = ApiFactory.getUserId(session, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
        return user_id;
    }
}
