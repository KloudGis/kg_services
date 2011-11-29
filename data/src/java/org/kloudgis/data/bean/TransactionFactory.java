/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.store.TransactionDbEntity;
import org.kloudgis.data.store.TransactionSequence;
import org.kloudgis.web_space.pojo.Transaction;

/**
 *
 * @author jeanfelixg
 */
class TransactionFactory {

    static TransactionDbEntity createTransaction(HibernateEntityManager em, Transaction trx, boolean updateFeature) {
        TransactionDbEntity entity = new TransactionDbEntity();
        entity.setId(TransactionSequence.next(em));
        entity.fromPojo(trx, em, true);
        em.persist(entity);
        if (updateFeature) {
            updateFeature(trx, em);
        }
        return entity;
    }

    private static void updateFeature(Transaction trx, HibernateEntityManager em) {
        if (trx.featuretype.equals("sys_note")) {
            NoteDbEntity entity;
            if (trx.trx_type == 1) {
                entity = new NoteDbEntity();
            } else {
                entity = em.find(NoteDbEntity.class, trx.feature_id);
            }
            if (entity != null) {
                entity.fromTransaction(trx);
            }else{
                System.out.println("*** Couldnt find note with id=" + trx.feature_id);
            }
            if (trx.trx_type == 1) {
                em.persist(entity);
            }
        }
    }
}
