package org.kloudgis.data;



import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.kloudgis.data.persistence.PersistenceManager;

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
        PersistenceManager.getInstance().closeEntityManagerFactories();
    }
}

