/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.data.KGConfig;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.data.pojo.Layer;
import org.kloudgis.data.pojo.LoadLayer;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.core.pojo.Records;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/layers")
@Produces({"application/json"})
public class LayerResourceBean {

    @GET
    public Response getLayers(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                List<LayerDbEntity> lstDb = em.createQuery("from LayerDbEntity", LayerDbEntity.class).getResultList();
                List<Layer> lstFT = new ArrayList(lstDb.size());
                for (LayerDbEntity fDb : lstDb) {
                    Layer pojo = fDb.toPojo(em);
                    lstFT.add(pojo);
                }
                em.close();
                Records rec = new Records();
                rec.records = lstFT;
                return Response.ok(rec).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @GET
    @Path("{layerId}")
    public Response getLayer(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @PathParam("layerId") Long layerId) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                LayerDbEntity layDb = em.find(LayerDbEntity.class, layerId);
                Layer pojo = null;
                if (layDb != null) {
                    pojo = layDb.toPojo(em);
                } else {
                    em.close();
                    throw new NotFoundException("Layer " + layerId + " is not found in sandbox " + sandbox + ".");
                }
                em.close();
                return Response.ok(pojo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @POST
    public Response addLayer(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, LoadLayer pojo) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            boolean bOwner = false;
            try {
                if (lMember != null) {
                    bOwner = AuthorizationFactory.isSandboxOwner(lMember, session, auth_token, sandbox);
                }
            } catch (IOException ex) {
            }
            if (bOwner) {
                em.getTransaction().begin();
                LayerDbEntity entity = new LayerDbEntity();
                entity.setOwner(lMember.getUserId());
                String layerN = "features_" + pojo.name.toLowerCase();
                entity.setName(sandbox.toLowerCase() + ":" + layerN);
                entity.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                entity.fromLoadPojo(pojo);
                em.persist(entity);

                Map<String, String> prop = new HashMap();
                prop.put("name", layerN);
                //fixe me hardcoded table name
                prop.put("tableName", "features");

                prop.put("sld", pojo.sld);
                prop.put("minX", pojo.minX + "");
                prop.put("minY", pojo.minY + "");
                prop.put("maxX", pojo.maxX + "");
                prop.put("maxY", pojo.maxY + "");

                String[] res = null;
                try {
                    res = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/" + sandbox + "/" + sandbox + "/layer", KGConfig.getConfiguration().api_key, prop);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (res != null && res[1].equals("200")) {
                    em.getTransaction().commit();
                    em.close();
                    return Response.ok().build();
                } else {
                    em.getTransaction().rollback();
                    return Response.serverError().entity("Geoserver status:" + res[1] + ". Descr:" + res[0]).build();
                }

            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a the owner of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @POST
    @Path("group_layers")
    public Response groupLayers(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            boolean bOwner = false;
            try {
                if (lMember != null) {
                    bOwner = AuthorizationFactory.isSandboxOwner(lMember, session, auth_token, sandbox);
                }
            } catch (IOException ex) {
            }
            if (bOwner) {
                sandbox = sandbox.toLowerCase();
                LayerDbEntity layGroup = null;
                List<LayerDbEntity> lstLayerGroups = em.getSession().createCriteria(LayerDbEntity.class).add(Restrictions.eq("is_group", true)).list();
                
                for (LayerDbEntity lay : lstLayerGroups) {
                    if (lay.getName().equals(sandbox)) {
                        layGroup = lay;
                        break;
                    }
                }
                if (layGroup == null) {
                    em.getTransaction().begin();
                    layGroup = new LayerDbEntity();
                    layGroup.setOwner(lMember.getUserId());
                    layGroup.setName(sandbox);
                    layGroup.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));                 
                    LoadLayer pojo = new LoadLayer();
                    pojo.visibility = true;
                    pojo.isSelectable = false;
                    pojo.canGroup = false;
                    layGroup.fromLoadPojo(pojo);
                    layGroup.setGroup();
                    em.persist(layGroup);
                    em.getTransaction().commit();
                }
                List<LayerDbEntity> lstLayerCanGroup = em.getSession().createCriteria(LayerDbEntity.class).add(Restrictions.eq("can_group", true)).addOrder(Order.asc("render_order")).list();
                em.close();
                Set<String> layerNames = new LinkedHashSet();
                for(LayerDbEntity lay: lstLayerCanGroup){
                    layerNames.add(lay.getName());
                }
                try {
                    String[] ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/" + sandbox + "/group_layers", KGConfig.getConfiguration().api_key, layerNames);
                    if(ret != null && ret[1].equals("200")){
                        return Response.ok().build();
                    }else{
                        return Response.serverError().entity(ret != null ? ret[0]: "api map error").build();
                    }
                } catch (IOException ex) {
                    return Response.serverError().entity(ex.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a the owner of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}
