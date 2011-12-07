/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

/**
 *
 * @author jeanfelixg
 */
public class ContextCleaner {
    
    private static final String CACHE = "!!servlet-cache-user!!";
    private static final String CACHE_LAST_ACCESS = "!!cache_last_access!!";

    public Map<String, Object> submitContext(ServletContext context, String auth_token) {
        Map<String, Map<String, Object>> cache = (Map) context.getAttribute(CACHE);
        if (cache == null) {
            cache = Collections.synchronizedMap(new HashMap());
            context.setAttribute(CACHE, cache);
        }
        long time = Calendar.getInstance().getTimeInMillis();
        //30 minutes
        long maxKeepAlive = 30L * 60L * 1000L;
        //remove not used cache
        ArrayList<String> arrlKeys = new ArrayList(cache.keySet());
        for (String aToken : arrlKeys) {
            Map<String, Object> properties = cache.get(aToken);
            if (properties != null) {
                Long last = (Long) properties.get(CACHE_LAST_ACCESS);
                if (last == null || (last.longValue() + maxKeepAlive) < time) {
                    clean(context, aToken);
                }
            }
        }
        Map<String, Object> prop = cache.get(auth_token);
        if (prop == null) {
            prop = Collections.synchronizedMap(new HashMap());
            cache.put(auth_token, prop);
        }

        prop.put(CACHE_LAST_ACCESS, time);
        return prop;
    }

    public void clean(ServletContext context, String auth_token) {
        Map<String, Map<String, Object>> cache = (Map) context.getAttribute(CACHE);
        if(cache != null && auth_token != null){
            System.out.println("cleaning cache for: "  +auth_token);
            cache.remove(auth_token);
        }
    }
}
