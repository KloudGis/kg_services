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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.kloudgis.data.pojo.Layer;

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

    public Long getId() {
        return id;
    }

    public int getPixelTolerance() {
        return pixel_tolerance == null ? 0 : pixel_tolerance.intValue();
    }

    public void setName(String strName) {
        name = strName;
    }

    public void setVisible(boolean bVisibility) {
        visibility = bVisibility;
    }

    public void setSelectable(boolean bSelectability) {
        selectable = bSelectability;
    }

    public void setLabel(String strLabel) {
        label = strLabel;
    }

    public void setOwner(Long lOwner) {
        owner = lOwner;
    }

    public Criterion getRestriction() {
        if (featuretype != null) {
            return Restrictions.eq("featuretype", featuretype);
        }
        return null;
    }
}