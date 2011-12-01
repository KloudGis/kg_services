/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.map;

import java.util.Calendar;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.core.api.ContextCleaner;

/**
 *
 * @author jeanfelixg
 */
public class SecurityManager {

    public static SecurityManager instance;
    //query params
    public static final String KG_SANDBOX = "kg_sandbox";
    //session attributes
    private static final String SESSION_TIMEOUT = "!kg_timeout!";
    private static final String SESSION_MAP_ACCESS = "!kg_map_access!";
    private static final String SESSION_VALIDATING = "!kg_map_validating!";
    private ContextCleaner cCleaner = new ContextCleaner();

    private SecurityManager() {
    }

    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

    public boolean login(ServletContext sContext, String auth_token, String sandbox) {
        Map<String,Object> prop = cCleaner.submitContext(sContext, auth_token);
        String access_key = sandbox + "_" + SESSION_MAP_ACCESS;
        Boolean bAccess = (Boolean) prop.get(access_key);
        if (auth_token != null && auth_token.length() > 0) {
            Long timeout = (Long) prop.get(SESSION_TIMEOUT);
            Long time = Calendar.getInstance().getTimeInMillis();
            if (timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 60000L) > time)) {
                if (bAccess == null) {
                    bAccess = false;
                }
                //ok
                //System.out.println("Auth token still valid, " + (time - timeout.longValue()) / 1000 + " sec.");
            } else {
                Boolean bValidating = (Boolean) prop.get(SESSION_VALIDATING);
                if (bValidating != null && bValidating.booleanValue() == true && bAccess != null && bAccess.booleanValue() == true) {
                    //had access before and is currently revalidating... return true to avoid duplicate validation
                    System.out.println("trusted map access");
                    bAccess = true;
                } else {
                    prop.put(SESSION_VALIDATING, true);
                    System.out.println(Calendar.getInstance().getTime() + "- Validate security for " + access_key);
                    if (sandbox != null && sandbox.length() > 0) {
                        try {
                            Long user_id = ApiFactory.getUserId(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                            if (user_id != null) {
                                String[] body = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().data_url + "/map_access?sandbox=" + sandbox + "&user_id=" + user_id, KGConfig.getConfiguration().api_key);
                                if (body != null && body[1].equals("200") && body[0].equals("true")) {
                                    prop.put(access_key, true);
                                    bAccess = true;
                                } else {
                                    prop.put(access_key, false);
                                    bAccess = false;
                                }                            
                            }
                            prop.put(SESSION_TIMEOUT, Calendar.getInstance().getTimeInMillis());
                        } catch (Exception ex) {
                            System.out.println("Ex:" + ex.getMessage());
                            bAccess = false;
                        }
                    } else {
                        bAccess = false;
                    }    
                    prop.put(SESSION_VALIDATING, false);
                }
            }
        } else {
            bAccess = false;
        }
        return bAccess;
    }

    public void logout(ServletContext sContext, String auth_token) {
        if(cCleaner != null){
            cCleaner.clean(sContext, auth_token);
        }
    }
}
