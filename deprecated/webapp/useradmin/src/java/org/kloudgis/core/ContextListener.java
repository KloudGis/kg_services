/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.kloudgis.core.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
public final class ContextListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent sce) {
        PersistenceManager.getInstance().getEntityManagerAdmin();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceManager.getInstance().closeEntityManagerFactories();
    }
}

