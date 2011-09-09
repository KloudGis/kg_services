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

package org.kloudgis.feature.pojo;

import org.kloudgis.core.pojo.feature.PlaceItem;
import javax.persistence.EntityManager;
import org.kloudgis.persistence.HydroDbEntity;

/**
 *
 * @author jeanfelixg
 */

public class Hydro extends PlaceItem{

    @Override
    public HydroDbEntity toDbEntity(EntityManager em) {
        HydroDbEntity db = new HydroDbEntity();
        db.fromPojo(this);
        return db;
    }

}
