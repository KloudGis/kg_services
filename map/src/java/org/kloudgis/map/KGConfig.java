package org.kloudgis.map;


import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jeanfelixg
 */
public class KGConfig {
    
    private static Configuration config;

    static void parse(ServletContext context) {
        InputStream in = context.getResourceAsStream("/META-INF/configuration.json");
            ObjectMapper mapper = new ObjectMapper();
            try {
                config = mapper.readValue(in, Configuration.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }
    
    public static Configuration getConfiguration(){
        return config;
    }
        
    public static Credentials getGeoserverCredentials(){
        return new UsernamePasswordCredentials(config.geoserver_user, config.geoserver_pwd);
    }

    public static Credentials getGwcCredentials() {
        return new UsernamePasswordCredentials(config.gwc_user, config.gwc_pwd);
    }
    
}
