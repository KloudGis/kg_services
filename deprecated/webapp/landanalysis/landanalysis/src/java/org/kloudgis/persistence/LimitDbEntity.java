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

package org.kloudgis.persistence;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.kloudgis.core.persistence.feature.PlaceItemDbEntity;
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.core.pojo.feature.PlaceItem;
import org.kloudgis.feature.pojo.Limit;


@Entity
@Table(name = "limit_items")
@org.hibernate.annotations.Table(appliesTo="limit_items",
    indexes = { @Index(name="limit_idx", columnNames = {"title", "description", "category", "subcategory"} ) } )
@Indexed
public class LimitDbEntity extends PlaceItemDbEntity implements Serializable {

    @SequenceGenerator(name = "limit_seq_gen", sequenceName = "limit_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "limit_seq_gen")
    @DocumentId
    private Long id;

    @Override
    public String getFeatureTypeName() {
        return "Limit";
    }

    @Override
    public Limit toPojo() {
        Limit pojo = new Limit();
        super.setupPlaceItem(pojo);
        return pojo;
    }

    @Override
    public void fromPojo(Feature pojo) {
        if(pojo instanceof PlaceItem){
            super.fromPlaceItem((PlaceItem) pojo);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

}

