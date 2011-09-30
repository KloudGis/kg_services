/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "featuretypes")
public class FeatureTypeDbEntity implements Serializable {

    @SequenceGenerator(name = "ft_seq_gen", sequenceName = "ft_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ft_seq_gen")
    private Long id;
    @Column
    private String name;
    @Column
    private String label;
    @OneToMany(mappedBy = "ft", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<AttrTypeDbEntity> attrs;

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public List<AttrTypeDbEntity> getAttrs() {
        return attrs;
    }
}
