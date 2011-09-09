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
package org.kloudgis.core.resources.admin;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.security.UserDbEntity;
import org.kloudgis.core.pojo.security.User;

/**
 *
 * @author jeanfelixg
 */
@Path("/")
public class LoginResourceBean {

    @Path("unprotected/login/logout")
    @GET
    @Produces({"application/json"})
    public Response logout(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok().build();
    }

    @Path("protected/login/user")
    @GET
    @Produces({"application/json"})
    public User loggedUser(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String user = req.getRemoteUser();
            if (user != null) {
                HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
                Query query = em.createQuery("from UserDbEntity where email = :name").setParameter("name", user);
                List<UserDbEntity> lstU = query.getResultList();
                if (lstU.size() == 1) {
                    UserDbEntity usr = lstU.get(0);
                    User pojo = usr.toPojo(em);
                    pojo.password = null;//hide password
                    return pojo;
                }
            }
        }
        return null;
    }
}
