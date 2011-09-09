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

package org.kloudgis.core.resources.security;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;
import org.kloudgis.core.pojo.PojoFactory;
import org.kloudgis.core.pojo.security.UserRole;

/**
 *
 * @author jeanfelixg
 */
@Path("/admin/roles")
public class RoleAdminResourceBean {

     @GET
    @Produces({"application/json"})
    public List<UserRole> getRoles(@Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        List<UserRoleDbEntity> lstDb = em.createNamedQuery("UserRole.findAll").getResultList();
        List<UserRole> lstP =  PojoFactory.toRoleEntities(lstDb);
        em.close();
        return lstP;
    }

    @Path("{pId}")
    @GET
    @Produces({"application/json"})
    public UserRole getRole(@PathParam("pId") Long pId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserRoleDbEntity pDb = em.find(UserRoleDbEntity.class, pId);
        if (pDb != null) {
            UserRole pojo = pDb.toPojo();
            em.close();
            return pojo;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + pId);
    }

    @Path("names")
    @GET
    @Produces({"application/json"})
    public List<String> getRoleNames() throws WebApplicationException {
        List<String> lstR = new ArrayList();
        lstR.add("user_role");
        lstR.add("admin_role");
        lstR.add("manager");
        return lstR;
    }

    @PUT
    @Path("{pId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public UserRole updateRole(UserRole role, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserRoleDbEntity pDb = em.find(UserRoleDbEntity.class, role.guid);
        if (pDb != null) {
            em.getTransaction().begin();
            pDb.updateFrom(role, em);
            em.getTransaction().commit();
        }
        em.close();
        return role;
    }

    @Path("{roleId}")
    @DELETE
    @Produces({"application/json"})
    public Response deleteRole(@PathParam("roleId") Long rId) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserRoleDbEntity uDb = em.find(UserRoleDbEntity.class, rId);
        if (uDb != null) {
            em.getTransaction().begin();
            em.remove(uDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        }
        em.close();
        throw new EntityNotFoundException("Role not found:" + rId);
    }

    @POST
    @Produces({"application/json"})
    public UserRole addRole(UserRole role, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        em.getTransaction().begin();
        UserRoleDbEntity uDb = role.toDbEntity(em);
        em.persist(uDb);
        em.getTransaction().commit();
        role.guid = uDb.getId();
        em.close();
        return role;
    }
}
