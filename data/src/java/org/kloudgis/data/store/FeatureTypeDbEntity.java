/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.kloudgis.data.pojo.Featuretype;

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
    private String label;
    @Column
    private String title_attribute;
    @OneToMany(mappedBy = "ft", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<AttrTypeDbEntity> attrtypes;

    public Long getId() {
        return id;
    }    


    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public List<AttrTypeDbEntity> getAttrs() {
        return attrtypes;
    }

    public Featuretype toPojo() {
        Featuretype pojo = new Featuretype();
        pojo.guid = id;
        pojo.label = label;
        pojo.title_attribute = title_attribute;
        List<Long> arrlA = new ArrayList(attrtypes.size());
        if(this.attrtypes != null){
            for(AttrTypeDbEntity at : attrtypes){
                arrlA.add(at.getId());
            }
        }
        pojo.attrtypes = arrlA;
        return pojo;
    }

    public void setTitleAttribute(String att) {
        this.title_attribute = att;
    }

}
