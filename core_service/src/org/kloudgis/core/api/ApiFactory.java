package org.kloudgis.core.api;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
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

    private static final String SESSION_USER = "!!kg_user!!";
    private static final String SESSION_SANDBOX_OWNER = "!!kg_user_sandbox_owner_id!!";
    private static final String SESSION_MEMBERSHIP = "!!kg_user_membership!!";
    private static final String SESSION_TIMEOUT_USER = "!!kg_timeout_user!!";
    private static final String SESSION_TIMEOUT_SANDBOX_OWNER = "!!kg_timeout_sandbox_owner!!";
    private static final String SESSION_TIMEOUT_MEMBERSHIP = "!!kg_timeout_membership!!";
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
                if (!(content instanceof String)) {
                    content = mapper.writeValueAsString(content);
                }
                post.setRequestEntity(new StringRequestEntity((String) content, "application/json", "UTF-8"));
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
        SignupUser user = getUser(session, auth_token, auth_url, api_key);
        if(user != null){
            return user.id;
        }
        return null;
    }

    public static SignupUser getUser(HttpSession session, String auth_token, String auth_url, String api_key) throws IOException {
        SignupUser user = (SignupUser) session.getAttribute(SESSION_USER);
        Long timeout = (Long) session.getAttribute(SESSION_TIMEOUT_USER);
        Long time = Calendar.getInstance().getTimeInMillis();
        //30 sec timeout
        if (timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 30000L) > time)) {
            //ok
        } else {
            user = null;
        }
        if (user == null) {
            String[] body = ApiFactory.apiGet(auth_token, auth_url + "/connected_user", api_key);
            if (body != null && body[0].length() > 0 && body[1].equals("200")) {
                user = mapper.readValue(body[0], SignupUser.class);
                session.setAttribute(SESSION_USER, user);
                session.setAttribute(SESSION_TIMEOUT_USER, time);
            }
        }
        return user;
    }
    

    public static Long getSandboxOwner(HttpSession session, String auth_token, String url, String api_key) throws IOException {
        Long sandbox_owner = (Long) session.getAttribute(SESSION_SANDBOX_OWNER);
        Long timeout = (Long) session.getAttribute(SESSION_TIMEOUT_SANDBOX_OWNER);
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
                session.setAttribute(SESSION_TIMEOUT_SANDBOX_OWNER, time);
            }
        }
        return sandbox_owner;
    }

    public static String getMembership(HttpSession session, String auth_token, String url, String api_key) throws IOException {
        String membership = (String) session.getAttribute(SESSION_MEMBERSHIP);
        Long timeout = (Long) session.getAttribute(SESSION_TIMEOUT_MEMBERSHIP);
        Long time = Calendar.getInstance().getTimeInMillis();
        //30 sec timeout
        if (timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 60000L) > time)) {
            //ok
        } else {
            membership = null;
        }
        if (membership == null) {
            String[] body = ApiFactory.apiGet(auth_token, url, api_key);
            if (body != null && body[0].length() > 0 && body[1].equals("200")) {
                try {
                    HashMap mapMember = mapper.readValue(body[0], HashMap.class);
                    membership = (String) mapMember.get("access_type");
                    session.setAttribute(SESSION_MEMBERSHIP, membership);
                    session.setAttribute(SESSION_TIMEOUT_MEMBERSHIP, time);
                } catch (Exception e) {
                }
            }
        }
        return membership;
    }
}