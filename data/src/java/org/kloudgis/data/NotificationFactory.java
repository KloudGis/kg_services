/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data;

import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author jeanfelixg
 */
public class NotificationFactory {

    private static HttpClient client = new HttpClient();
    
    public static int postNotification(String sandbox, String topic, String json, String auth_token) throws IOException {
        PostMethod post = new PostMethod(KGConfig.getConfiguration().notification_url + "/" + sandbox + "/" + topic );
        post.addRequestHeader("X-Kloudgis-Authentication", auth_token);
        try {
            post.setRequestEntity(new StringRequestEntity(json, "application/json", "UTF-8"));
            int status = client.executeMethod(post);
            post.releaseConnection();
            return status;
        } catch (IOException e) {
            post.releaseConnection();
            throw e;
        }
    }
    
}
