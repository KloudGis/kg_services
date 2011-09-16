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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table (name="feature_attr")
public class FeatureAttributeDbEntity implements Serializable {
    
    @SequenceGenerator(name = "feature_attr_seq_gen", sequenceName = "feature_attr_sandbox_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "feature_attr_seq_gen")
    private Long id;
    @Index(name = "feature_attr_name_index")
    @Column(length = 100)
    private String attr_name;
    @Index(name = "feature_attr_value_index")
    @Column(length = 100)
    private String attr_value;
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="feature_id") 
    private FeatureDbEntity feature;
}
