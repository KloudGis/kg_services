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
import org.kloudgis.persistence.LotDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/features/Lot")
@Produces({"application/json"})
public class LotResourceBean extends PlaceItemResourceBean {

    @Override
    public Class getEntityDbClass() {
        return LotDbEntity.class;
    }

    public static String getLotLabel(String code, String loc) {
        if (loc.equalsIgnoreCase("en")) {
            if (code == null) {
                return "Null";
            } else if (code.equals("O")) {
                return "Part";
            } else if (code.equals("N")) {
                return "Complete";
            }
        } else {
            if (code == null) {
                return "Nulle";
            } else if (code.equals("O")) {
                return "Partie";
            } else if (code.equals("N")) {
                return "Complet";
            }
        }
        return code;
    }
}
