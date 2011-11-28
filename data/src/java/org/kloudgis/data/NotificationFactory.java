/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.kloudgis.web_space.pojo.Message;
import org.kloudgis.web_space.pojo.Transaction;

/**
 *
 * @author jeanfelixg
 */
public class NotificationFactory {

    private static HttpClient client = new HttpClient();
    private static ObjectMapper mapper = new ObjectMapper();
    private static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    private static void postNotification(final String sandbox, final String topic, final String json, final String auth_token) {
        threadPool.submit(new Runnable() {

            public void run() {
                PostMethod post = new PostMethod(KGConfig.getConfiguration().notification_url + "/" + topic + "?sandbox=" + sandbox);
                post.addRequestHeader("X-Kloudgis-Authentication", auth_token);
                try {
                    post.setRequestEntity(new StringRequestEntity(json, "application/json", "UTF-8"));
                    int status = client.executeMethod(post);
                    post.releaseConnection();
                } catch (IOException e) {
                    post.releaseConnection();
                }
            }
        });
        
    }

    public static void postTransaction(String user, String sandbox, String auth_token, Transaction trx) {
        try {        
            postNotification(sandbox, "trx", mapper.writeValueAsString(new Message(trx.toMap(), "trx", user)), auth_token);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
