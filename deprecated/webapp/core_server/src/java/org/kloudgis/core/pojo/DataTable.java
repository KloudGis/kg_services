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
@XmlRootElement(name = "datatable")
public class DataTable {

    private DataTableColumn[] columns;

    private DataTableRow[] values;

    @XmlElement(name="cols")
    public DataTableColumn[] getColumns(){
        return columns;
    }

    @XmlElement(name="rows")
    public DataTableRow[] getValues(){
        return values;
    }

    public void setValues(DataTableRow[] map){
        this.values = map;
    }

    public  void setColumns(DataTableColumn[] cols){
        this.columns = cols;
    }
}
