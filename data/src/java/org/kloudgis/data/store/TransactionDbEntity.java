/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.kloudgis.web_space.pojo.Transaction;
import org.kloudgis.web_space.pojo.TransactionAttribute;
/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "transactions")
public class TransactionDbEntity implements Serializable {

    @Id
    private Long id;
    @Column
    private Long user_id;
    @Column
    private Long parent_trx_id;
    @Column
    private Long feature_id;
    @Column
    private Timestamp create_time;
    @Column
    private Integer trx_type;
    @Column
    private String source;
    @Column
    private String featuretype;
    @Column
    private String author;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TransactionAttributeDbEntity> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }
    
    public void setUserId(Long id) {
        this.user_id = id;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }  
    
    public void setParentTrx(Long parent){
        this.parent_trx_id = parent;
    }
    
    public Transaction toPojo() {
        Transaction pojo = new Transaction();
        pojo.user_id = this.user_id;
        pojo.trx_id = this.id;
        pojo.parent_trx_id = this.parent_trx_id;
        pojo.feature_id = this.feature_id;
        pojo.time = this.create_time;
        pojo.trx_type = this.trx_type;
        pojo.source = this.source;
        pojo.featuretype = this.featuretype;
        pojo.author = this.author;
        if (details != null) {
            ArrayList<TransactionAttribute> arrlDetailPojos = new ArrayList();
            for (TransactionAttributeDbEntity at : details) {
                arrlDetailPojos.add(at.toPojo());
            }
            pojo.details = arrlDetailPojos;
        }
        return pojo;
    }

    public void fromPojo(Transaction trx, EntityManager em, boolean bCreateJoin) {
        if(trx.trx_id != null){
            this.id = trx.trx_id;
        }
        this.user_id = trx.user_id;       
        this.parent_trx_id = trx.parent_trx_id;
        this.feature_id = trx.feature_id;
        this.create_time = trx.time;
        this.trx_type = trx.trx_type;
        this.source = trx.source;
        this.featuretype = trx.featuretype;
        this.author = trx.author;
        if (bCreateJoin) {
            List<TransactionAttributeDbEntity> arrlAt = new ArrayList();
            for (TransactionAttribute tAt : trx.details) {
                TransactionAttributeDbEntity entity = new TransactionAttributeDbEntity();
                entity.fromPojo(tAt);
                entity.setTransaction(this);
                em.persist(entity);
                arrlAt.add(entity);
            }
            this.details = arrlAt;
        }
    }
}
