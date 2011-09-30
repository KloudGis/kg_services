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

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table (name="attrtypes")
public class AttrTypeDbEntity implements Serializable {
    
    @SequenceGenerator(name = "at_seq_gen", sequenceName = "at_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "at_seq_gen")
    private Long id;
    @Column
    private String name;
    @Column
    private String label;
    @ManyToOne(cascade= CascadeType.ALL)
    private FeatureTypeDbEntity ft;

    public String getName() {
        return name;
    }
    
    public String getLabel() {
        return label;
    }
}
