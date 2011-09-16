package org.kloudgis.api;

import java.io.IOException;
import java.util.Calendar;
import javax.servlet.http.HttpSession;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author jeanfelixg
 */
public class ApiFactory {

    private static final String SESSION_USER_ID = "!!kg_user_id!!";
    private static final String SESSION_TIMEOUT = "!!kg_timeout!!";

    public static String apiGet(String auth_token, String url, String api_key) throws IOException, NumberFormatException {
        if (auth_token != null && url != null && api_key != null) {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(url);
            get.addRequestHeader("X-Kloudgis-Authentication", auth_token);
            get.addRequestHeader("X-Kloudgis-Api-Key", api_key);
            int iStatus = client.executeMethod(get);
            if (iStatus == 200) {
                return get.getResponseBodyAsString(1000);
            } else {
                System.out.println("Api Status is:" + iStatus);
            }
        }
        return null;
    }

    public static Long getUserId(HttpSession session, String auth_token, String auth_url, String api_key) throws IOException {
        Long user_id = (Long) session.getAttribute(SESSION_USER_ID);
        Long timeout = (Long) session.getAttribute(SESSION_TIMEOUT);
        Long time = Calendar.getInstance().getTimeInMillis();
        //30 sec timeout
        if (timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 30000L) > time)) {
            //ok
        } else {
            user_id = null;
        }
        if (user_id == null) {
            String body = ApiFactory.apiGet(auth_token, auth_url + "/user_id", api_key);
            if (body != null && body.length() > 0) {
                user_id = Long.parseLong(body);
                session.setAttribute(SESSION_USER_ID, user_id);
                session.setAttribute(SESSION_TIMEOUT, time);
            }
        }
        return user_id;
    }
}
