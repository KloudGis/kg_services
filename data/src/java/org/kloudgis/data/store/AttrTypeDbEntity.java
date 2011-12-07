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
import org.kloudgis.data.pojo.Attrtypetoto;

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
    private String label;
    @Column
    private String type;
    @Column
    private String attr_ref;
    @ManyToOne(cascade= CascadeType.ALL)
    private FeatureTypeDbEntity ft;
    
    public Long getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setFeaturetype(FeatureTypeDbEntity ft){
        this.ft = ft;
    }
    
    public Attrtypetoto toPojo(){
        Attrtypetoto pojo = new Attrtypetoto();
        pojo.guid = id;
        pojo.label = label;
        pojo.type = type;
        pojo.attr_ref = attr_ref;
        pojo.featuretype = ft != null ? ft.getId() : null;
        return pojo;
    }

    public void fromPojo(Attrtypetoto pojo) {      
       this.label = pojo.label;
       this.type = pojo.type;
       this.attr_ref = pojo.attr_ref;
    }
}
