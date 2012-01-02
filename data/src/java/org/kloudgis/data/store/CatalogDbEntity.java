/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kloudgis.data.pojo.Catalog;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table (name="catalogs")
public class CatalogDbEntity implements Serializable {
    
    @SequenceGenerator(name = "cat_seq_gen", sequenceName = "cat_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cat_seq_gen")
    private Long id;
    @Column
    private String label;
    @Column
    private String key_value;
    
    @ManyToOne
    AttrTypeDbEntity attrtype;

    public Catalog toPojo() {
        Catalog pojo = new Catalog();
        pojo.guid = id;
        pojo.attrtype = attrtype.getId();
        pojo.key = key_value;
        pojo.label = label;
        return pojo;
    }
}
