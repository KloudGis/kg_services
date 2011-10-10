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
package org.kloudgis.admin.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.pojo.Records;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/sandboxes")
@Produces({"application/json"})
public class SandboxResourceBean {

    @GET
    public Response getSandboxes(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context HttpServletRequest req) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpSession session = req.getSession(true);
        Long id = null;
        String err = "Invalid token";
        try {
            id = AuthorizationFactory.getUserId(session, auth_token);
        } catch (IOException ex) {
            err = ex.getMessage();
        }
        if (id != null) {
            HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
            List<SandboxDbEntity> sandboxes = em.getSession().createCriteria(SandboxDbEntity.class).addOrder(Order.asc("name")).createCriteria("users").add(Restrictions.eq("user_id", id)).list();
            List<Sandbox> list = new ArrayList();
            for (SandboxDbEntity db : sandboxes) {
                list.add(db.toPojo());
            }
            Records ret = new Records();
            ret.records = list;
            return Response.ok(ret).build();
        } else {
            return Response.serverError().entity(err).build();
        }

    }

    @GET
    @Path("{key}/meta")
    public Response getSandboxMeta(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context HttpServletRequest req, @PathParam("key") String key) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpSession session = req.getSession(true);
        Long id = null;
        String err = "Invalid token";
        try {
            id = AuthorizationFactory.getUserId(session, auth_token);
        } catch (IOException ex) {
            err = ex.getMessage();
        }
        if (id != null) {
            HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
            try {
                SandboxDbEntity sandbox = (SandboxDbEntity) em.getSession().createCriteria(SandboxDbEntity.class).addOrder(Order.asc("name")).add(Restrictions.eq("unique_key", key)).createCriteria("users").add(Restrictions.eq("user_id", id)).uniqueResult();
                em.close();
                return Response.ok(sandbox.toPojo()).build();
            } catch (Exception e) {
                err = e.getMessage();
            }
            em.close();
        }
        return Response.serverError().entity(err).build();
    }
}
