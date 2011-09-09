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

package org.kloudgis.core.pojo.feature;

import java.util.List;

/**
 *
 * @author jeanfelixg
 */
public class AttrType {

    public Long guid;
    public String name;
    public String label;
    public String hint;
    public Integer col_size;
    public Boolean visible;
    public Boolean editable;
    public List<Long> selectionPriority;

}
