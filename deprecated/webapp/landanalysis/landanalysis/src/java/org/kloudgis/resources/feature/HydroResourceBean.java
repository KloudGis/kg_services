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

import org.kloudgis.core.resources.feature.PlaceItemResourceBean;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.kloudgis.persistence.HydroDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/features/Hydro")
@Produces({"application/json"})
public class HydroResourceBean extends PlaceItemResourceBean{

    @Override
    public Class getEntityDbClass() {
        return HydroDbEntity.class;
    }

    public static String getHydroLabel(String code, String loc) {
        if (loc.equalsIgnoreCase("en")) {
            if(code == null){
                return "Null";
            }else{
                return code;
            }
        } else {
            if(code == null){
                return "Nulle";
            }else{
                return code;
            }
        }

    }
}