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
package org.kloudgis.sandbox.bean;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.sandbox.AuthorizationFactory;
import org.kloudgis.sandbox.KGConfig;
import org.kloudgis.core.pojo.Records;
import org.kloudgis.sandbox.pojo.Sandbox;
import org.kloudgis.sandbox.store.SandboxDbEntity;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.core.pojo.SignupUser;
import org.kloudgis.core.utils.StringTools;
import org.kloudgis.sandbox.persistence.PersistenceManager;
import org.kloudgis.sandbox.store.UserSandboxDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/sandboxes")
@Produces({"application/json"})
public class SandboxResourceBean {

    @GET
    public Response getSandboxes(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context ServletContext sContext) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long id = null;
        String err = "Invalid token";
        try {
            id = AuthorizationFactory.getUserId(sContext, auth_token);
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
    public Response getSandboxMeta(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context ServletContext sContext, @PathParam("key") String key) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Long id = null;
        String err = "Invalid token";
        try {
            id = AuthorizationFactory.getUserId(sContext, auth_token);
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
            if (em.isOpen()) {
                em.close();
            }
        }
        return Response.serverError().entity(err).build();
    }

    @GET
    @Path("list_names")
    public Response getSandboxNames(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context ServletContext sContext) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long id = null;
        String err = "Invalid token";
        try {
            id = AuthorizationFactory.getUserId(sContext, auth_token);
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
    public Response addSandbox(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, Sandbox sandbox, @Context ServletContext sContext) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        SignupUser usr = null;
        try {
            usr = AuthorizationFactory.getUser(sContext, auth_token);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (usr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (sandbox.name == null || sandbox.name.length() == 0) {
            return Response.notModified().entity("Sandbox name is mandatory").build();
        }

        if (sandbox.guid == null || sandbox.guid.length() == 0) {
            sandbox.guid = sandbox.name + "_sb";
        }
        sandbox.guid = StringTools.replaceUnicodeChars(sandbox.guid.replace(" ", "_"), '_').toLowerCase();
        sandbox.guid = "u_" + usr.id + "_" + sandbox.guid;
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        try {
            SandboxDbEntity entity = AuthorizationFactory.getSandboxFromKey(sandbox.guid, em);
            if (entity != null) {
                em.close();
                return Response.status(Response.Status.BAD_REQUEST).entity("Sandbox key is already taken").build();
            }
        } catch (Exception e) {
            em.close();
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            String[] ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/create_db", KGConfig.getConfiguration().api_key, sandbox.guid);
            if (ret != null && ret[1].equals("200")) {
                em.getTransaction().begin();
                SandboxDbEntity entity = new SandboxDbEntity();
                entity.setName(sandbox.name);
                entity.setUniqueKey(sandbox.guid);
                entity.setOwnerId(usr.id);
                entity.setOwnerDesc(usr.name);
                entity.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                entity.setCentre(sandbox.lon, sandbox.lat);
                entity.setZoom(sandbox.zoom);
                //bind owner to sandbox
                UserSandboxDbEntity u = new UserSandboxDbEntity();
                u.setSandbox(entity);
                u.setUserId(usr.id);
                em.persist(u);
                entity.addUser(u);
                em.persist(entity);
                ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/workspace", KGConfig.getConfiguration().api_key, sandbox.guid);
                if (ret != null && ret[1].equals("200")) {
                    ret = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().data_url + "/database_prop", KGConfig.getConfiguration().api_key);
                    if (ret != null && ret[1].equals("200")) {
                        ObjectMapper mapper = new ObjectMapper();
                        Map prop = mapper.readValue(ret[0], HashMap.class);
                        prop.put("name", sandbox.guid);
                        ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/" + sandbox.guid + "/store", KGConfig.getConfiguration().api_key, prop);
                        if (ret != null && ret[1].equals("200")) {
                            ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/add_owner_member?sandbox=" + sandbox.guid, KGConfig.getConfiguration().api_key, usr.id);
                        }
                        if (ret != null && ret[1].equals("200")) {
                            em.getTransaction().commit();
                            em.close();
                            return Response.ok().entity(entity.toPojo()).build();
                        } else {
                            em.getTransaction().rollback();
                            em.close();
                            ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, sandbox.guid);
                            ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().map_url + "/workspace/" + sandbox.guid, KGConfig.getConfiguration().api_key);
                            return Response.serverError().entity("Could not add the geoserver store:" + ret == null ? "?" : ret[0]).build();
                        }
                    } else {
                        em.getTransaction().rollback();
                        em.close();
                        ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, sandbox.guid);
                        ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().map_url + "/workspace/" + sandbox.guid, KGConfig.getConfiguration().api_key);
                        return Response.serverError().entity("Could not get the database setting from api data:" + ret == null ? "?" : ret[0]).build();
                    }
                } else {
                    em.getTransaction().rollback();
                    em.close();
                    ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, sandbox.guid);
                    return Response.serverError().entity("Could not add the geoserver workspace:" + ret == null ? "?" : ret[0]).build();
                }
            }
            em.close();
            return Response.serverError().entity("cannot create db: " + ret == null ? "?" : ret[0]).build();
        } catch (Exception ex) {
            em.close();
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("{sandboxKey}")
    public Response deleteSandbox(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("sandboxKey") String sandboxKey, @Context ServletContext sContext) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long id = null;
        try {
            id = AuthorizationFactory.getUserId(sContext, auth_token);
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
                    String[] ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, entity.getUniqueKey());
                    if (ret != null && (ret[1].equals("200") || ret[0].endsWith("does not exist"))) {
                        ret = ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().map_url + "/workspace/" + sandboxKey, KGConfig.getConfiguration().api_key);
                        if (ret != null && ret[1].equals("200") || ret[0].contains("No such workspace")) {
                            em.getTransaction().begin();
                            em.remove(entity);
                            em.getTransaction().commit();
                            em.close();
                            return Response.ok().build();
                        } else {
                            em.close();
                            return Response.serverError().entity("Could delete the geoserver workspace (" + ret != null ? ret[0] : "?" + ")").build();
                        }
                    } else {
                        em.close();
                        return Response.serverError().entity("Could drop the sandbox db (" + ret != null ? ret[0] : "?" + ")").build();
                    }
                } else {
                    //not owner ?  Just remove the current user from the sandbox.
                    em.getTransaction().begin();
                    List<UserSandboxDbEntity> listBiding = em.getSession().createCriteria(UserSandboxDbEntity.class).add(Restrictions.eq("user_id", id)).add(Restrictions.eq("sandbox", entity)).list();
                    for (UserSandboxDbEntity bind : listBiding) {
                        em.remove(bind);
                    }
                    String[] ret = ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().data_url + "/members/current_user?sandbox=" + sandboxKey, null);
                    em.getTransaction().commit();
                    em.close();
                    return Response.ok().build();
                }
            } else {
                em.close();
                return Response.notModified().entity("Sandbox with key " + sandboxKey + " is not found!").build();
            }
        } catch (Exception e) {
            if (em.isOpen()) {
                em.close();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/bind_users")
    public Response bindUser(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam(value = "sandbox") String sandboxKey, List<Long> users, @Context ServletContext sContext) {
        if (auth_token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long id = null;
        try {
            id = AuthorizationFactory.getUserId(sContext, auth_token);
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
                return Response.notModified().entity("Sandbox with key " + sandboxKey + " is not found!").build();
            }
        } catch (Exception e) {
            if (em.isOpen()) {
                em.close();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
