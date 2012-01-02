/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.kloudgis.data.store.FeatureDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
public class SearchFactory {

    public static void buildSearchIndexFor(String sandbox) {
        System.out.println("Indexing Notes...");
        buildIndex(NoteDbEntity.class, sandbox);
        System.out.println("Indexing Features...");
        buildIndex(FeatureDbEntity.class, sandbox);      
        System.out.println("Indexing Done...");
    }
    
    private static void buildIndex(Class clazz, String sandbox){
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        ftem.setFlushMode(FlushModeType.COMMIT);
        EntityTransaction trx = ftem.getTransaction();
        System.out.println("Indexing: " + clazz);
        trx.begin();
        ftem.purgeAll(clazz);
        //Scrollable results will avoid loading too many objects in memory
        int BATCH_SIZE = 500;
        ScrollableResults results = em.getSession().createCriteria(clazz).setFetchSize(BATCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
        int index = 0;
        while (results.next()) {
            index++;
            ftem.index(results.get(0)); //index each element
            if (index % BATCH_SIZE == 0) {
                ftem.flushToIndexes(); //free memory since the queue is processed
                ftem.clear(); //free memory since the queue is processed
            }
        }
        if (index % BATCH_SIZE != 0) {
            ftem.flushToIndexes(); //free memory since the queue is processed
            ftem.clear(); //free memory since the queue is processed
        }
        trx.commit();
        ftem.getSearchFactory().optimize(clazz);
        ftem.close();
    }
}
