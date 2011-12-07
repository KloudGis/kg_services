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
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.data.pojo.Cluster;
import org.kloudgis.data.pojo.Note;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.core.pojo.Records;
import org.kloudgis.core.pojo.SignupUser;
import org.kloudgis.data.KGConfig;
import org.kloudgis.data.NotificationFactory;
import org.kloudgis.data.pojo.NoteMessage;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/notes")
@Produces({"application/json"})
public class NoteResourceBean {

    @GET
    @Path("{fId}")
    public Response getFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                NoteDbEntity note = em.find(NoteDbEntity.class, fId);
                if (note != null) {
                    Note pojo = note.toPojo();
                    em.close();
                    return Response.ok(pojo).build();
                }
                em.close();
                throw new EntityNotFoundException("Not found:" + fId);
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @PUT
    @Path("{fId}")
    public Response updateFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox, Note in_note) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                NoteDbEntity note = em.find(NoteDbEntity.class, fId);
                try {
                    if (note != null) {
                        if (note.getAuthor() != null && lMember.getUserId().longValue() != note.getAuthor().longValue()) {
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the author of the note: " + sandbox).build();
                        }
                        em.getTransaction().begin();
                        note.fromPojo(in_note);
                        em.getTransaction().commit();
                        em.close();
                        Note pojo = note.toPojo();
                        SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                        NotificationFactory.postMessage(sandbox, auth_token, new NoteMessage(note.getId(), NoteMessage.UPDATE, user.user));
                        return Response.ok(pojo).build();
                    } else {
                        throw new EntityNotFoundException("Not found:" + fId);
                    }
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @DELETE
    @Path("{fId}")
    public Response deleteFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                NoteDbEntity note = em.find(NoteDbEntity.class, fId);
                try {
                    if (note != null) {
                        if (note.getAuthor() == null || note.getAuthor().longValue() == lMember.getUserId().longValue() || AuthorizationFactory.isSandboxOwner(lMember, sContext, auth_token, sandbox)) {
                            em.getTransaction().begin();
                            em.remove(note);
                            em.getTransaction().commit();
                            em.close();
                            SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                            NotificationFactory.postMessage(sandbox, auth_token, new NoteMessage(note.getId(), NoteMessage.DELETE, user.user ));
                            return Response.noContent().build();
                        } else {
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the author of the note nor the sandbox owner: " + sandbox).build();
                        }
                    } else {
                        throw new EntityNotFoundException("Not found:" + fId);
                    }
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @POST
    public Response addFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, Note in_note) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                em.getTransaction().begin();
                NoteDbEntity note = new NoteDbEntity();
                note.fromPojo(in_note);
                note.setAuthor(lMember.getUserId());
                note.setAuthorDescriptor(lMember.getUserDescriptor());
                note.setDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                try {
                    em.persist(note);
                    em.getTransaction().commit();                 
                    SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                    NotificationFactory.postMessage(sandbox, auth_token, new NoteMessage(note.getId(), NoteMessage.ADD, user.user));
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
                Note pojo = note.toPojo();
                em.close();
                return Response.ok(pojo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @GET
    @Path("clusters")
    public Response getNoteClusters(@Context ServletContext servlet, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token,
            @QueryParam("sw_lat") Double swlat,
            @QueryParam("sw_lon") Double swlon,
            @QueryParam("ne_lat") Double nelat,
            @QueryParam("ne_lon") Double nelon,
            @QueryParam("distance") Double distance,
            @QueryParam("sandbox") String sandbox) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {

            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(servlet, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                List<Cluster> clusters = new ArrayList();
                Cluster cluster;
                boolean clustered = false;
                Criteria criteria = em.getSession().createCriteria(NoteDbEntity.class);
                Coordinate sw = new Coordinate(swlon, swlat);
                Coordinate ne = new Coordinate(nelon, nelat);
                Coordinate[] coords = {sw, new Coordinate(sw.x, ne.y), ne, new Coordinate(ne.x, sw.y), sw};
                Polygon pBounds = GeometryFactory.createPolygon(GeometryFactory.createLinearRing(coords), null);
                pBounds.setSRID(4326);
                criteria.add(SpatialRestrictions.within("geo", pBounds));
                criteria.addOrder(Order.asc("id"));
                NoteDbEntity feature;
                List< NoteDbEntity> features = criteria.list();
                if (features != null) {
                    for (int i = 0; i < features.size(); i++) {
                        feature = features.get(i);
                        Point geo = feature.getGeometry();
                        if (geo != null) {
                            clustered = false;
                            for (int j = clusters.size() - 1; j >= 0; j--) {
                                cluster = clusters.get(j);
                                if (shouldCluster(cluster, feature, distance)) {
                                    addToCluster(cluster, feature);
                                    clustered = true;
                                    break;
                                }
                            }
                            if (!clustered) {
                                clusters.add(createCluster(feature));
                            }
                        }
                    }
                }
                em.close();
                Records rec = new Records();
                rec.records = clusters;
                return Response.ok(rec).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    private boolean shouldCluster(Cluster cluster, NoteDbEntity feature, Double distance) {
        Coordinate coordCluster = new Coordinate(cluster.lon, cluster.lat);
        Coordinate coordNote = feature.getGeometry().getCoordinate();
        return coordCluster.distance(coordNote) <= distance;
    }

    private void addToCluster(Cluster cluster, NoteDbEntity feature) {
        cluster.features.add(feature.getId());
        cluster.tip = "_Notes";
    }

    private Cluster createCluster(NoteDbEntity feature) {
        Cluster cluster = new Cluster();
        cluster.lon = feature.getGeometry().getX();
        cluster.lat = feature.getGeometry().getY();
        cluster.features.add(feature.getId());
        cluster.tip = feature.getTitle();
        cluster.guid = feature.getId();
        return cluster;
    }
}
