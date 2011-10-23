/*
 * EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretechnologies.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import java.security.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.kloudgis.data.pojo.Layer;
import org.kloudgis.data.pojo.LoadLayer;
import org.kloudgis.model.LayerFilter;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "layers")
public class LayerDbEntity implements Serializable {

    @SequenceGenerator(name = "layer_seq_gen", sequenceName = "layer_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "layer_seq_gen")
    private Long id;
    @Column
    private Integer render_order;
    @Column(length = 100)
    private String name;
    @Column
    private Long owner;
    @Column(length = 100)
    private String label;
    @Column
    private Timestamp date_creation;
    @Column(length = 254)
    private String url;
    @Column
    private Integer buffer;
    @Column
    private Boolean visibility;
    @Column
    private Boolean selectable;
    @Column
    private Integer pixel_tolerance;
    @Column(length = 100)
    private String featuretype;
    @Column(columnDefinition = "TEXT")
    private String jsonFitler;
    @Column(columnDefinition = "TEXT")
    private String sld;
    private static ObjectMapper mapper = new ObjectMapper();

    public Layer toPojo(EntityManager em) {

        Layer pojo = new Layer();
        pojo.guid = id;
        pojo.renderOrder = render_order;
        pojo.isSelectable = selectable;
        pojo.pixelTolerance = pixel_tolerance;

        pojo.name = name;
        pojo.owner = owner;
        pojo.label = label;
        pojo.url = url;
        pojo.buffer = buffer;
        pojo.visibility = visibility;
        return pojo;
    }
    
    public void fromLoadPojo(LoadLayer pojo) {
        this.render_order = pojo.renderOrder;
        this.selectable = pojo.isSelectable;
        this.pixel_tolerance = pojo.pixelTolerance;
      
        this.label = pojo.label;
        this.url = "/api_map/wms";
        this.buffer = pojo.buffer;
        this.visibility = pojo.visibility;
        
        this.featuretype = pojo.featuretype;
        this.jsonFitler = pojo.filter;
        this.sld = pojo.sld;       
    }

    public Long getId() {
        return id;
    }

    public int getPixelTolerance() {
        return pixel_tolerance == null ? 0 : pixel_tolerance.intValue();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String strName) {
        name = strName;
    }

    public void setVisible(boolean bVisibility) {
        visibility = bVisibility;
    }
    
    public void setDateCreate(Timestamp time){
        this.date_creation = time;
    }

    public void setSelectable(boolean bSelectability) {
        selectable = bSelectability;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public void setLabel(String strLabel) {
        label = strLabel;
    }

    public void setOwner(Long lOwner) {
        owner = lOwner;
    }

    public Criterion getRestriction() {
        Criterion crit = null;
        if(featuretype != null){
            crit = Restrictions.eq("featuretype", featuretype);
        }
        if (jsonFitler != null) {
            try {
                LayerFilter filter = mapper.readValue(jsonFitler, LayerFilter.class);
                if (filter != null) {
                    Criterion critFilter = buildCriterion(filter);
                    if(crit != null){
                        return Restrictions.and(crit, critFilter);
                    }else{
                        return critFilter;
                    }                  
                }
            } catch (Exception ex) {
                System.out.println("Filter ex:" + ex);
            }
        }
        return crit;
    }

    private static Criterion buildCriterion(LayerFilter filter) {
        if (filter.leftFilter != null) {
            Criterion critLeft = buildCriterion(filter.leftFilter);
            Criterion critRight = null;
            if (filter.rightFilter != null) {
                critRight = buildCriterion(filter.rightFilter);
            }
            if (critLeft != null && filter.operator.equals("not")) {
                return Restrictions.not(critLeft);
            } else if (critLeft != null && critRight != null) {
                if (filter.operator.equals("or")) {
                    return Restrictions.or(critLeft, critRight);
                } else {
                    return Restrictions.and(critLeft, critRight);
                }
            }
            return null;
        } else {
            if (filter.operator.equals("eq")) {
                return Restrictions.eq(filter.attribute, filter.value);
            } else if (filter.operator.equals("ne")) {
                return Restrictions.ne(filter.attribute, filter.value);
            } else if (filter.operator.equals("like")) {
                return Restrictions.like(filter.attribute, filter.value);
            } else if (filter.operator.equals("isNull")) {
                return Restrictions.isNull(filter.attribute);
            } else if (filter.operator.equals("ge")) {
                return Restrictions.ge(filter.attribute, filter.value);
            } else if (filter.operator.equals("le")) {
                return Restrictions.le(filter.attribute, filter.value);
            } else if (filter.operator.equals("in")) {
                return Restrictions.in(filter.attribute, filter.value.split("|"));
            }
            return null;
        }
    }
    
    /*
    public static void main(String a[]) throws IOException{
        LayerFilter lf = new LayerFilter();
        lf.operator = "eq";
        lf.attribute = "index1";
        lf.value = "toto";
        String json = mapper.writeValueAsString(lf);
        System.out.println(json);
        LayerFilter lf2 = new LayerFilter();
        lf2.operator = "like";
        lf2.attribute = "index2";
        lf2.value = "to%";
        LayerFilter lfC = new LayerFilter();
        lfC.operator = "or";
        lfC.leftFilter = lf;
        lfC.rightFilter = lf2;
        json = mapper.writeValueAsString(lfC);
        System.out.println(json);  
        LayerFilter lfTest = mapper.readValue(json, LayerFilter.class);
        Criterion crit = buildCriterion(lfTest);
        System.out.println(crit);
    }*/

}