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

import com.vividsolutions.jts.geom.Geometry;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.kloudgis.core.pojo.feature.PlaceItem;

/**
 *
 * @author jeanfelixg
 */
@MappedSuperclass
public abstract class PlaceItemDbEntity extends FeatureDbEntity{
   
    @Column
    @Field(index=org.hibernate.search.annotations.Index.TOKENIZED)
    private String title;

    @Column
    @Field(index=org.hibernate.search.annotations.Index.TOKENIZED)
    private String description;

    @Column
    private String category;

    @Column
    private String subcategory;

    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;


    protected void setupPlaceItem(PlaceItem pojo){
        pojo.setFeatureId(getId());
        Geometry env = null;
        if(geo != null){
            env = geo.getEnvelope();
        }
        pojo.boundswkt = env == null ? null : env.toText();
        pojo.title = title;
        pojo.description = description;
        pojo.category = category;
    }

    public void fromPlaceItem(PlaceItem pojo) {
        this.title = pojo.title;
        this.description = pojo.description;
        this.category = pojo.category;
    }

    public void setTitle(String ti) {
        this.title = ti;
    }

    public void setDescription(String descr) {
        this.description = descr;
    }

    public void setCategory(String cat) {
        this.category = cat;
    }

    @Override
    public Geometry getGeometry() {
        return geo;
    }

}
