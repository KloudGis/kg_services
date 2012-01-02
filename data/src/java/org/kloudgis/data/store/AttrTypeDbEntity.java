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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kloudgis.data.pojo.Attrtype;
import org.kloudgis.data.pojo.Catalog;

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
    @Column
    private String css_class;
    @Column
    private Integer render_order;
    @ManyToOne(cascade= CascadeType.ALL)
    private FeatureTypeDbEntity ft;
    //range for numbers
    @Column 
    private Double min_value;
    @Column 
    private Double max_value;
    @Column 
    private Double step_value;
    //enumeration catalog values
    @OneToMany(mappedBy="attrtype", fetch = FetchType.EAGER)
    @OrderBy("label")
    private List<CatalogDbEntity> enum_values;
    
    
    public Long getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setFeaturetype(FeatureTypeDbEntity ft){
        this.ft = ft;
    }
    
    public Attrtype toPojo(){
        Attrtype pojo = new Attrtype();
        pojo.guid = id;
        pojo.label = label;
        pojo.type = type;
        pojo.attr_ref = attr_ref;
        pojo.featuretype = ft != null ? ft.getId() : null;
        pojo.css_class = css_class;
        pojo.render_order = render_order;
        if(enum_values != null){
            List<Catalog> lstCat = new ArrayList(enum_values.size());
            for(CatalogDbEntity cat : enum_values){
                lstCat.add(cat.toPojo());
            }
            pojo.enum_values = lstCat;
        }
        return pojo;
    }

    public void fromPojo(Attrtype pojo) {      
       this.label = pojo.label;
       this.type = pojo.type;
       this.attr_ref = pojo.attr_ref;
       this.css_class = pojo.css_class;
       this.render_order = pojo.render_order;     
    }
}
