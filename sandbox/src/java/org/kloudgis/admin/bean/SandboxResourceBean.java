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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.KGConfig;
import org.kloudgis.pojo.Records;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserSandboxDbEntity;
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
            em.close();
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

    @GET
    @Path("list_names")
    public Response getSandboxNames(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context HttpServletRequest req) {
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
                List<SandboxDbEntity> lstS = em.getSession().createCriteria(SandboxDbEntity.class).addOrder(Order.asc("name")).list();
                Map<String, String> mapNameKey = new LinkedHashMap();
                for (SandboxDbEntity sand : lstS) {
                    mapNameKey.put(sand.getName(), sand.getUniqueKey());
                }
                em.close();
                return Response.ok(mapNameKey).build();
            } catch (Exception e) {
                err = e.getMessage();
            }
            em.close();
        }
        return Response.serverError().entity(err).build();
    }

    @POST
    public Response addSandbox(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, Sandbox sandbox, @Context HttpServletRequest req) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        HttpSession session = req.getSession(true);
        Long id = null;
        try {
            id = AuthorizationFactory.getUserId(session, auth_token);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (sandbox.key == null || sandbox.key.length() == 0) {
            return Response.notModified().entity("Sandbox key is mandatory").build();
        }
        sandbox.key = morphKey(sandbox.key);
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        try {
            SandboxDbEntity entity = AuthorizationFactory.getSandboxFromKey(sandbox.key, em);
            if (entity != null) {
                em.close();
                return Response.notModified().entity("Sandbox key is already taken").build();
            }
        } catch (Exception e) {
            em.close();
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(KGConfig.getConfiguration().data_url + "/create_db");
            post.addRequestHeader("X-Kloudgis-Api-Key", KGConfig.getConfiguration().api_key);
            post.setRequestEntity(new StringRequestEntity(sandbox.key, "text/plain", "utf-8"));
            int istatus = client.executeMethod(post);
            post.releaseConnection();
            if (istatus == 200) {
                em.getTransaction().begin();
                SandboxDbEntity entity = new SandboxDbEntity();
                entity.setName(sandbox.name);
                entity.setUniqueKey(sandbox.key);
                entity.setOwnerId(sandbox.owner == null ? id : sandbox.owner);
                em.persist(entity);
                em.getTransaction().commit();
                em.close();
                return Response.ok().entity(entity.toPojo()).build();
            }
            em.close();
            return Response.status(istatus).build();
        } catch (Exception ex) {
            em.close();
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("{sandboxKey}")
    public Response deleteSandbox(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("sandboxKey") String sandboxKey, @Context HttpServletRequest req) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        HttpSession session = req.getSession(true);
        Long id = null;
        try {
            id = AuthorizationFactory.getUserId(session, auth_token);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        try {
            SandboxDbEntity entity = AuthorizationFactory.getSandboxFromKey(sandboxKey, em);
            if (entity != null) {
                Long oId = entity.getOwnerId();
                if (oId == null || oId.longValue() == id) {
                    HttpClient client = new HttpClient();
                    PostMethod post = new PostMethod(KGConfig.getConfiguration().data_url + "/drop_db");
                    post.addRequestHeader("X-Kloudgis-Api-Key", KGConfig.getConfiguration().api_key);
                    post.setRequestEntity(new StringRequestEntity(entity.getUniqueKey(), "text/plain", "utf-8"));
                    int istatus = client.executeMethod(post);
                    post.releaseConnection();
                    if (istatus == 200) {
                        em.getTransaction().begin();
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.ok().build();
                    }else{
                        em.close();
                        return Response.serverError().entity("Could drop the sandbox db (" + istatus + ")").build();
                    }
                } else {
                    em.close();
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Only the sandbox owner can add new users.").build();
                }
            } else {
                em.close();
               return Response.notModified().entity("Sandbox with key " + sandboxKey + " is not found!").build();
            }
        } catch (Exception e) {
            if(em.isOpen()){
                em.close();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("{sandboxKey}/users")
    public Response bindUser(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("sandboxKey") String sandboxKey, @Context HttpServletRequest req, List<Long> users) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        HttpSession session = req.getSession(true);
        Long id = null;
        try {
            id = AuthorizationFactory.getUserId(session, auth_token);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        try {
            SandboxDbEntity entity = AuthorizationFactory.getSandboxFromKey(sandboxKey, em);
            if (entity != null) {
                Long oId = entity.getOwnerId();
                if (oId == null || oId.longValue() == id) {
                    em.getTransaction().begin();
                    for (Long uId : users) {
                        UserSandboxDbEntity u = new UserSandboxDbEntity();
                        u.setSandbox(entity);
                        u.setUserId(uId);
                        em.persist(u);
                        entity.addUser(u);
                    }
                    em.getTransaction().commit();
                    em.close();
                    return Response.ok().build();
                } else {
                    em.close();
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Only the sandbox owner can add new users.").build();
                }
            } else {
                em.close();
                throw new EntityNotFoundException();
            }
        } catch (Exception e) {
            if(em.isOpen()){
                em.close();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    
    
    private static String morphKey(String key){
        if(key.equalsIgnoreCase("postgres") || key.equalsIgnoreCase("postgis") || key.equalsIgnoreCase("kg_auth") || key.equalsIgnoreCase("kg_sandbox")  ){
            return key + "_usr_sandbox";
        }
        return key;
    }
}
