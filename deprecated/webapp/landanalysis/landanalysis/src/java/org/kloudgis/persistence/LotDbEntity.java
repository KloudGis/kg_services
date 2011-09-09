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

import org.kloudgis.core.persistence.feature.PlaceItemDbEntity;
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
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.core.pojo.feature.PlaceItem;
import org.kloudgis.feature.pojo.Lot;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "lot")
@org.hibernate.annotations.Table(appliesTo="lot",
    indexes = { @Index(name="lot_idx", columnNames = {"title", "description", "category", "subcategory"} ) } )
@Indexed
public class LotDbEntity extends PlaceItemDbEntity implements Serializable {

    @SequenceGenerator(name = "lot_seq_gen", sequenceName = "lot_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "lot_seq_gen")
    @DocumentId
    private Long id;

    @Override
    public String getFeatureTypeName() {
        return "Lot";
    }

    @Override
    public Lot toPojo() {
        Lot pojo = new Lot();
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
