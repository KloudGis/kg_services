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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.KGConfig;
import org.kloudgis.pojo.Records;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.api.ApiFactory;
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
        Long id = (Long) session.getAttribute("kg_user_id");
        String err = "?";
        if (id == null) {
            try {
                String body = ApiFactory.apiGet(auth_token,KGConfig.getConfiguration().auth_url + "/user_id", KGConfig.getConfiguration().api_key);
                id = Long.parseLong(body);
                session.setAttribute("kg_user_id", id);
            } catch (Exception ex) {
                err = ex.getMessage();
            } 
        }
        if (id != null) {         
            HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
            List<SandboxDbEntity> sandboxes = em.getSession().createCriteria(SandboxDbEntity.class).add(Restrictions.eq("user_id", id)).addOrder(Order.asc("name")).list();
            List<Sandbox> list = new ArrayList();
            for (SandboxDbEntity db : sandboxes) {
                list.add(db.toPojo());
            }
            Records ret = new Records();
            ret.records = list;
            return Response.ok(ret).build();
        }else{
            return Response.serverError().entity(err).build(); 
        }
        
    }
}
