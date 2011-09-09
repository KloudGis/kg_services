/*
 * EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretechnologies.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 */
package org.kloudgis.core.resources.admin;

import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.kloudgis.core.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
public abstract class AbstractSearchResourceBean {
   
    public abstract Integer indexAllEntities();

    protected void indexEntities(Class<?>... entities) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        ftem.setFlushMode(FlushModeType.COMMIT);
        EntityTransaction trx = ftem.getTransaction();
        for (Class clazz : entities) {
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
            System.out.println("Done Indexing " + index + " " + clazz);
            ftem.getSearchFactory().optimize(clazz);
            System.out.println("Done Optimizing: " + clazz);
        }
    }
}
