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
package org.kloudgis.core.persistence.feature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.kloudgis.core.pojo.feature.AttrType;

@Entity
@Table(name = "ATTRTYPES")
@NamedQueries({
    @NamedQuery(name = "AttrType.findAll", query = "SELECT c FROM AttrTypeDbEntity c order by c.label")})
public class AttrTypeDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String label;
    @Column(nullable = false)
    private String hint;
    @Column
    private Boolean visible = true;
    @Column
    private Boolean editable = false;
    @Column(name = "col_size")
    private Integer colSize = 100;
    @Column(name = "col_order")
    private Integer colOrder;
    @Column
    private Integer featuretype_id;
    @Column
    private Long group_id;
    @OrderBy(value="priority")
    @OneToMany(mappedBy = "attribute")
    private List<PriorityDbEntity> lstSelectionPriority;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String lbl) {
        this.label = lbl;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setEditable(Boolean bVal) {
        this.editable = bVal;
    }

    public void setVisible(Boolean bVal) {
        this.visible = bVal;
    }

    public void setColSize(Integer size) {
        this.colSize = size;
    }

    public void setColOrder(Integer size) {
        this.colOrder = size;
    }

    public Integer getOrder() {
        return colOrder;
    }

    public AttrType toPojo() {
        AttrType pojo = new AttrType();
        pojo.guid = id;
        pojo.name = name;
        pojo.label = label;
        pojo.hint = hint;
        pojo.col_size = colSize;
        pojo.visible = visible;
        pojo.editable = editable;
        if (lstSelectionPriority != null) {
            List<Long> lstIds = new ArrayList();
            for (PriorityDbEntity prio : lstSelectionPriority) {
                lstIds.add(prio.getId());
            }
            pojo.selectionPriority = lstIds;
        }
        return pojo;
    }

    void setFeatureType(FeatureTypeDbEntity ft) {
        this.featuretype_id = ft.getId();
    }
}
