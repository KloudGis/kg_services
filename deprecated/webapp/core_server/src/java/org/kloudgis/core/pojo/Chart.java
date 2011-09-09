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

/**
 *
 * @author jeanfelixg
 */
public class Chart {

    public String title;
    public DataTable data;

    public Chart(){};

    public Chart(String title, DataTable data){
        this.title = title;
        this.data = data;
    }
}
