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

package org.kloudgis.resources.feature;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.kloudgis.persistence.LimitDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/features/Limit")
@Produces({"application/json"})
public class LimitResourceBean extends PlaceItemChartsBean {

    @Override
    public Class getEntityDbClass() {
        return LimitDbEntity.class;
    }

}
