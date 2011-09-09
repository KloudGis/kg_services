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
import org.kloudgis.persistence.RoadDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/features/Road")
@Produces({"application/json"})
public class RoadResourceBean extends PlaceItemResourceBean{

    @Override
    public Class getEntityDbClass() {
        return RoadDbEntity.class;
    }

    public static String getRoadLabel(String code, String loc) {
        if (loc.equalsIgnoreCase("en")) {
            if(code == null){
                return "Null";
            }else if (code.equals("UTR")) {
                return "Utility Road";
            }else if (code.equals("CON")) {
                return "Connector Road";
            }else if (code.equals("UR")) {
                return "Unclassified Road";
            }else if (code.equals("ST")) {
                return "Street";
            }else if (code.equals("HI")) {
                return "Highway";
            }else if (code.equals("BT")) {
                return "Bridge/Tunnel";
            }else{
                return code;
            }
        } else {
            if(code == null){
                return "Nulle";
            }else if (code.equals("UTR")) {
                return "Route utilitaire";
            }else if (code.equals("CON")) {
                return "Route de connexion";
            }else if (code.equals("UR")) {
                return "Route non-classifiée";
            }else if (code.equals("ST")) {
                return "Route";
            }else if (code.equals("HI")) {
                return "Autoroute";
            }else if (code.equals("BT")) {
                return "Pont/Tunnel";
            }else{
                return code;
            }
        }

    }
}