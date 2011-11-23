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
import org.kloudgis.sandbox.persistence.PersistenceManager;
import org.kloudgis.core.utils.StringTools;

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
            String[] ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/create_db", KGConfig.getConfiguration().api_key, sandbox.key);     
            if (ret != null && ret[1].equals("200")) {
                em.getTransaction().begin();
                SandboxDbEntity entity = new SandboxDbEntity();
                entity.setName(sandbox.name);
                entity.setUniqueKey(sandbox.key);
                entity.setOwnerId(id);
                entity.setDateCreation(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                entity.setCentre(sandbox.lon, sandbox.lat);
                entity.setZoom(sandbox.zoom);
                em.persist(entity);

                ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/workspace", KGConfig.getConfiguration().api_key, sandbox.key);
                if (ret != null && ret[1].equals("200")) {
                    ret = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().data_url + "/database_prop", KGConfig.getConfiguration().api_key);
                    if (ret != null && ret[1].equals("200")) {
                        ObjectMapper mapper = new ObjectMapper();
                        Map prop = mapper.readValue(ret[0], HashMap.class);
                        prop.put("name", sandbox.key);
                        ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/" + sandbox.key + "/store", KGConfig.getConfiguration().api_key, prop);
                        if (ret != null && ret[1].equals("200")) { 
                            ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/add_owner_member?sandbox=" + sandbox.key, KGConfig.getConfiguration().api_key, id);
                        }
                        if (ret != null && ret[1].equals("200")) {                                                      
                            em.getTransaction().commit();
                            em.close();
                            return Response.ok().entity(entity.toPojo()).build();
                        } else {
                            em.getTransaction().rollback();
                            em.close();
                            ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, sandbox.key);    
                            ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().map_url + "/workspace/" + sandbox.key, KGConfig.getConfiguration().api_key);
                            return Response.serverError().entity("Could not add the geoserver store:" + ret == null ? "?" : ret[0]).build();
                        }
                    } else {
                        em.getTransaction().rollback();
                        em.close();
                        ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, sandbox.key);  
                        ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().map_url + "/workspace/" + sandbox.key, KGConfig.getConfiguration().api_key);
                        return Response.serverError().entity("Could not get the database setting from api data:" + ret == null ? "?" : ret[0]).build();
                    }
                } else {
                    em.getTransaction().rollback();
                    em.close();
                    ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, sandbox.key);  
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
                    String[] ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().data_url + "/drop_db", KGConfig.getConfiguration().api_key, entity.getUniqueKey());                                     
                    if (ret != null && (ret[1].equals("200") || ret[0].endsWith("does not exist"))) {
                        ret = ApiFactory.apiDelete(auth_token, KGConfig.getConfiguration().map_url + "/workspace/" + sandboxKey, KGConfig.getConfiguration().api_key);
                        if (ret != null && ret[1].equals("200") || ret[0].contains("No such workspace")) {
                            em.getTransaction().begin();
                            em.remove(entity);
                            em.getTransaction().commit();
                            em.close();
                            return Response.ok().build();
                        }else{
                            em.close();
                            return Response.serverError().entity("Could delete the geoserver workspace (" + ret != null ? ret[0] : "?" + ")").build();
                        }
                    } else {
                        em.close();
                        return Response.serverError().entity("Could drop the sandbox db (" + ret != null ? ret[0] : "?" + ")").build();
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
            if (em.isOpen()) {
                em.close();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private static String morphKey(String key) {    
        key = StringTools.replaceUnicodeChars(key, '_');
        if (key.equalsIgnoreCase("postgres") || key.equalsIgnoreCase("postgis") || key.equalsIgnoreCase("kg_auth") || key.equalsIgnoreCase("kg_sandbox") || key.equalsIgnoreCase("test")) {
            return key + "_usr_sandbox";
        }
        return key.toLowerCase();
    }
}
