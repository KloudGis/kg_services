package org.kloudgis.api;


import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author jeanfelixg
 */
public class ApiFactory {

    public static String apiGet(String auth_token, String url, String api_key) throws IOException, NumberFormatException{
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url);
        get.addRequestHeader("X-Kloudgis-Authentication", auth_token);
        get.addRequestHeader("X-Kloudgis-Api-Key", api_key);
        int iStatus = client.executeMethod(get);
        if (iStatus == 200) {
            return get.getResponseBodyAsString(1000);
        }else{
            System.out.println("Api Status is:" + iStatus);
        }
        return null;
    }
}
