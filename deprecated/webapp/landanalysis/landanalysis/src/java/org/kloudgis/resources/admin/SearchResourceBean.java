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
package org.kloudgis.resources.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.kloudgis.core.resources.admin.AbstractSearchResourceBean;
import org.kloudgis.persistence.ArpenteurDbEntity;
import org.kloudgis.persistence.LimitDbEntity;
import org.kloudgis.persistence.NoteDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/admin/search")
@Produces({"application/json"})
public class SearchResourceBean extends AbstractSearchResourceBean {

    @GET
    @Path("indexall")
    @Override
    public Integer indexAllEntities() {
        indexEntities(NoteDbEntity.class, ArpenteurDbEntity.class, LimitDbEntity.class/*, RoadDbEntity.class, HydroDbEntity.class */);
        return 0;
    }
}
