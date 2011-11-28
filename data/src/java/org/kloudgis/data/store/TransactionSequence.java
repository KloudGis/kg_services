/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "transaction_sequence")
public class TransactionSequence implements Serializable {
   
    
    @SequenceGenerator(name = "trx_seq_gen", sequenceName = "trx_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "trx_seq_gen")
    private Long id;
    
    public static Long next(EntityManager em){
        TransactionSequence ts = new TransactionSequence();
        em.persist(ts);
        return ts.id;
    }
}
