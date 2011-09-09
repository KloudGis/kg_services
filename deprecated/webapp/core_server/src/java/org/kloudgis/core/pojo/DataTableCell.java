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
public class DataTableCell<T> {

    private T value;
    private String formated;

    public DataTableCell() {
    }

    public DataTableCell(T val, String label) {
        this.value = val;
        this.formated = label;
    }

    @XmlElement(name = "v")
    public T getValue() {
        return value;
    }

    @XmlElement(name = "f")
    public String getFormated() {
        return formated;
    }

    public void setValue(T val) {
        this.value = val;
    }

    public void setFormated(String label) {
        this.formated = label;
    }
}
