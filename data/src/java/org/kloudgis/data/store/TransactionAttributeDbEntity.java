/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kloudgis.core.pojo.space.TransactionAttribute;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "transaction_attrs")
public class TransactionAttributeDbEntity implements Serializable {
    
    @SequenceGenerator(name = "trx_attr_seq_gen", sequenceName = "trx_attr_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "trx_attr_seq_gen")
    private Long id;
    @Column
    private String attribute;
    @Column(columnDefinition = "TEXT")
    private String original_value;
    @Column(columnDefinition = "TEXT")
    private String modified_value;
    @ManyToOne(cascade= CascadeType.PERSIST)
    private TransactionDbEntity transaction;

    public void setTransaction(TransactionDbEntity trx){
        this.transaction = trx;
    }

    public TransactionAttribute toPojo() {
        TransactionAttribute pojo = new TransactionAttribute();
        pojo.attribute = this.attribute;
        pojo.original_value = this.original_value;
        pojo.modified_value = this.modified_value;
        return pojo;
    }
    
    public void fromPojo(TransactionAttribute pojo) {
        this.attribute = pojo.attribute;
        this.original_value = pojo.original_value;
        this.modified_value = pojo.modified_value;
    }
}
