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
package org.kloudgis.core.pojo;

import java.util.List;
import org.kloudgis.core.pojo.feature.Feature;

/**
 *
 * @author jeanfelixg
 */
public class FeatureCollection {

    public List<Feature> list;  //this list can be partial.

    public Integer count;       //the complete list count


    public FeatureCollection(){}

    public FeatureCollection(List<Feature> lstPojo, Integer count) {
        this.list = lstPojo;
        this.count = count;
    }
}
