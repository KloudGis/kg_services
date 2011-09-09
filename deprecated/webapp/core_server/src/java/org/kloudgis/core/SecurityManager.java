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
package org.kloudgis.core;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import org.kloudgis.core.exception.UnauthorizedException;
import org.kloudgis.core.persistence.security.GroupDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class SecurityManager {

    public static GroupDbEntity getLoggedGroup(EntityManager em, HttpServletRequest req) {
        String user = req.getRemoteUser();
        //TO REMOVE!!
       // user = "admin";
        
        Query query = em.createQuery("SELECT u.group from UserDbEntity u INNER JOIN u.group where u.user_name = :name").setParameter("name", user);
        List<GroupDbEntity> lstGr = query.getResultList();
        if (lstGr.size() == 1) {
            return lstGr.get(0);
        }
        throw new UnauthorizedException("User " + user + " is not member of any group!");
    }
}
