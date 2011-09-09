/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
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
    
}
