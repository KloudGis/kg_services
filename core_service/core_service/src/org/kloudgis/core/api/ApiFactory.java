package org.kloudgis.core.api;

import java.io.IOException;
import java.util.Calendar;
import javax.servlet.http.HttpSession;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.kloudgis.core.pojo.SignupUser;

/**
 *
 * @author jeanfelixg
 */
public class ApiFactory {

    private static final String SESSION_USER_ID = "!!kg_user_id!!";
    private static final String SESSION_SANDBOX_OWNER = "!!kg_user_sandbox_owner_id!!";
    private static final String SESSION_TIMEOUT = "!!kg_timeout!!";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String[] apiGet(String auth_token, String url, String api_key) throws IOException, NumberFormatException {
        if (url != null && api_key != null) {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(url);
            get.addRequestHeader("X-Kloudgis-Authentication", auth_token);
            get.addRequestHeader("X-Kloudgis-Api-Key", api_key);
            int iStatus = client.executeMethod(get);
            String res = get.getResponseBodyAsString(5000);
            return new String[]{res, iStatus + ""};
        }
        return null;
    }

    public static String[] apiPost(String auth_token, String url, String api_key, Object content) throws IOException {
        if (url != null && api_key != null) {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            if (content != null) {
                if(!(content instanceof String)){
                    content = mapper.writeValueAsString(content);
                }
                post.setRequestEntity(new StringRequestEntity((String)content, "application/json", "UTF-8"));
            }
            post.addRequestHeader("X-Kloudgis-Authentication", auth_token);
            post.addRequestHeader("X-Kloudgis-Api-Key", api_key);
            int iStatus = client.executeMethod(post);
            String res = post.getResponseBodyAsString(5000);
            post.releaseConnection();
            return new String[]{res, iStatus + ""};
        }
        return null;
    }

    
    public static String[] apiDelete(String auth_token, String url, String api_key) throws IOException {
        if (url != null && api_key != null) {
            HttpClient client = new HttpClient();
            DeleteMethod delete = new DeleteMethod(url);        
            delete.addRequestHeader("X-Kloudgis-Authentication", auth_token);
            delete.addRequestHeader("X-Kloudgis-Api-Key", api_key);
            int iStatus = client.executeMethod(delete);
            String res = delete.getResponseBodyAsString(5000);
            delete.releaseConnection();
            return new String[]{res, iStatus + ""};
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
            String[] body = ApiFactory.apiGet(auth_token, auth_url + "/connected_user", api_key);
            if (body != null && body[0].length() > 0 && body[1].equals("200")) {
                user_id = mapper.readValue(body[0], SignupUser.class).id;
                session.setAttribute(SESSION_USER_ID, user_id);
                session.setAttribute(SESSION_TIMEOUT, time);
            }
        }
        return user_id;
    }

    public static Long getSandboxOwner(HttpSession session, String auth_token, String url, String api_key) throws IOException {
        Long sandbox_owner = (Long) session.getAttribute(SESSION_SANDBOX_OWNER);
        Long timeout = (Long) session.getAttribute(SESSION_TIMEOUT);
        Long time = Calendar.getInstance().getTimeInMillis();
        //30 sec timeout
        if (timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 60000L) > time)) {
            //ok
        } else {
            sandbox_owner = null;
        }
        if (sandbox_owner == null) {
            String[] body = ApiFactory.apiGet(auth_token, url, api_key);
            if (body != null && body[0].length() > 0 && body[1].equals("200")) {
                sandbox_owner = Long.parseLong(body[0]);
                session.setAttribute(SESSION_SANDBOX_OWNER, sandbox_owner);
                session.setAttribute(SESSION_TIMEOUT, time);
            }
        }
        return sandbox_owner;
    }
}
