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

package org.kloudgis.data.pojo;

/**
 *
 * @author jeanfelixg
 */
public class Layer {

    public Long     guid;
    //rendering order (1 is the first to be draw)
    public Integer  renderOrder; 
    //select on map ?
    public Boolean  isSelectable;
    public Integer  pixelTolerance;
    public Boolean  canRender;
    public Boolean  isGroup;
    
    //layer parameters
    //user who has created the layer in the sandbox
    public Long   owner;
    
    public String   label;
    //the unique name including the workspace Ex: cite:mylayer
    public String   name;  
    //relative or complete url to geoserver
    public String   url;
    public Boolean  visibility;

}
