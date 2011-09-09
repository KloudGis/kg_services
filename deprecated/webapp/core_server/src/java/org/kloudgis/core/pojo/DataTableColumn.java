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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jeanfelixg
 */
@XmlRootElement
public class DataTableColumn {

    @XmlElement
    private String id;
    @XmlElement
    private String label;
    @XmlElement
    private String type;

    public DataTableColumn(){}

    public DataTableColumn(String id, String label, String type){
        this.id = id;
        this.label = label;
        this.type = type;
    }
}
