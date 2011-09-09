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

package org.kloudgis.core.exception;

import javax.persistence.EntityNotFoundException;

/**
 *
 * @author jeanfelixg
 */
public class FeatureTypeNotFoundException extends EntityNotFoundException{

    public FeatureTypeNotFoundException(String descr){
        super(descr);
    }
}
