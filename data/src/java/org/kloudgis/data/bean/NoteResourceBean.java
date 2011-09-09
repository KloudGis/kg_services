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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.GeometryFactory;
import org.kloudgis.data.pojo.Cluster;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/notes")
@Produces({"application/json"})
public class NoteResourceBean {

    
    @GET
    @Path("clusters")
    public Response getNoteClusters(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sw_lat") Double swlat, @QueryParam("sw_lon") Double swlon, @QueryParam("ne_lat") Double nelat, @QueryParam("ne_lon") Double nelon, @QueryParam("distance") Double distance, @QueryParam("sandbox") String sandbox){
        //TODO validate the auth_token
        if (auth_token != null) {
            HibernateEntityManager em = (HibernateEntityManager) PersistenceManager.getInstance().getEntityManager(sandbox);
            if (em != null) {
                List<Cluster> clusters = new ArrayList();
                Cluster cluster;
                boolean clustered = false;
                Criteria criteria = em.getSession().createCriteria(NoteDbEntity.class);
                Coordinate sw = new Coordinate(swlon, swlat);
                Coordinate ne = new Coordinate(nelon, nelat);
                Coordinate[] coords = {sw, new Coordinate(sw.x, ne.y), ne, new Coordinate(ne.x, sw.y), sw};
                Polygon pBounds = GeometryFactory.createPolygon(GeometryFactory.createLinearRing(coords), null);
                pBounds.setSRID(4326);
                criteria.add(SpatialRestrictions.within("geom", pBounds));
                criteria.addOrder(Order.asc("id"));
                NoteDbEntity feature;
                List< NoteDbEntity> features = criteria.list();
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
                em.close();
                return Response.ok(clusters).build();
            }
        }
        return null;
    }

    private boolean shouldCluster(Cluster cluster, NoteDbEntity feature, Double distance) {
        Coordinate coordCluster = new Coordinate(cluster.lon, cluster.lat);
        Coordinate coordNote = feature.getGeometry().getCoordinate();
        return coordCluster.distance(coordNote) <= distance;
    }

    private void addToCluster(Cluster cluster, NoteDbEntity feature) {
        cluster.features.add(feature.getId());
    }

    private Cluster createCluster(NoteDbEntity feature) {
        Cluster cluster = new Cluster();
        cluster.lon = feature.getGeometry().getX();
        cluster.lat = feature.getGeometry().getY();
        cluster.features.add(feature.getId());
        cluster.guid=feature.getId();
        return cluster;
    }
}
