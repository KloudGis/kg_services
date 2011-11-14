package org.kloudgis.notification;



import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author jeanfelixg
 */
public final class ContextListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("*** contextInitialized");
        KGConfig.parse(sce.getServletContext());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("*** contextDestroyed");
    }
}

