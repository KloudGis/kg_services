/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.map;

import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.kloudgis.core.api.ApiFactory;

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

    private SecurityManager() {
    }

    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

    public boolean login(HttpServletRequest request, String auth_token, String sandbox) {
        HttpSession session = request.getSession(true);
        String access_key = sandbox + "_" + SESSION_MAP_ACCESS;
        Boolean bAccess = (Boolean) session.getAttribute(access_key);
        if (auth_token != null && auth_token.length() > 0) {
            Long timeout = (Long) session.getAttribute(SESSION_TIMEOUT);
            Long time = Calendar.getInstance().getTimeInMillis();
            if (timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 60000L) > time)) {
                if (bAccess == null) {
                    bAccess = false;
                }
                //ok
                //System.out.println("Auth token still valid, " + (time - timeout.longValue()) / 1000 + " sec.");
            } else {
                Boolean bValidating = (Boolean) session.getAttribute(SESSION_VALIDATING);
                if (bValidating != null && bValidating.booleanValue() == true && bAccess != null && bAccess.booleanValue() == true) {
                    //had access before and is currently revalidating... return true to avoid duplicate validation
                    System.out.println("trusted map access");
                    bAccess = true;
                } else {
                    session.setAttribute(SESSION_VALIDATING, true);
                    System.out.println(Calendar.getInstance().getTime() + "- Validate security for " + access_key);
                    if (sandbox != null && sandbox.length() > 0) {
                        try {
                            Long user_id = ApiFactory.getUserId(session, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                            if (user_id != null) {
                                String[] body = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().data_url + "/map_access?sandbox=" + sandbox + "&user_id=" + user_id, KGConfig.getConfiguration().api_key);
                                if (body != null && body[1].equals("200") && body[0].equals("true")) {
                                    session.setAttribute(access_key, true);
                                    bAccess = true;
                                } else {
                                    session.setAttribute(access_key, false);
                                    bAccess = false;
                                }                            
                            }
                            session.setAttribute(SESSION_TIMEOUT, Calendar.getInstance().getTimeInMillis());
                        } catch (Exception ex) {
                            System.out.println("Ex:" + ex.getMessage());
                            bAccess = false;
                        }
                    } else {
                        bAccess = false;
                    }    
                    session.setAttribute(SESSION_VALIDATING, false);
                }
            }
        } else {
            bAccess = false;
        }
        return bAccess;
    }

    public void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
