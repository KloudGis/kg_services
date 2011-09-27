/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.GeometryFactory;
import org.kloudgis.data.pojo.Feature;
import org.kloudgis.data.store.DistanceOrder;
import org.kloudgis.data.store.FeatureDbEntity;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.persistence.PersistenceManager;
import org.kloudgis.pojo.Records;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/features")
@Produces({"application/json"})
public class FeatureResourceBean {

    @GET
    @Path("features_at")
    public Response getFeaturesAt(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @QueryParam("lon") Double lon, @QueryParam("lat") Double lat,
            @QueryParam("layers") String layers, @QueryParam("limit") Integer limit, @QueryParam("one_pixel") Double onePixelWorld) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            Point point = GeometryFactory.createPoint(new Coordinate(lon, lat));
            point.setSRID(4326);
            Set<Feature> setF = new LinkedHashSet();
            for (String id : layers.split(",")) {
                if (limit > 0) {
                    LayerDbEntity lay = em.find(LayerDbEntity.class, Long.valueOf(id));
                    if (lay != null) {
                        List<Feature> arrL = findFeatures(em, point, lay, onePixelWorld, limit);
                        int size = setF.size();
                        setF.addAll(arrL);
                        int newSize = setF.size();
                        limit -= (newSize - size);
                    }
                }
            }
            
            em.close();
            Records records = new Records();
            records.records = new ArrayList(setF);
            return Response.ok(records).build();
        } else {
            throw new NotFoundException("Sandbox entity manager not found for id:" + sandbox + ".");
        }
    }

    private List<Feature> findFeatures(HibernateEntityManager em, Point point, LayerDbEntity layer, double onePixelWorld, Integer limit) {
        List<Feature> lstF = new ArrayList<Feature>();
        int iPixels = layer.getPixelTolerance();
        Geometry inter = point;
        if (iPixels > 0) {
            inter = point.buffer(iPixels * onePixelWorld);
            inter.setSRID(point.getSRID());
        }
        Criteria criteria = em.getSession().createCriteria(FeatureDbEntity.class);
        Criterion crit = layer.getRestriction();
        if(crit != null){
            criteria.add(crit);
        }
        criteria.add(SpatialRestrictions.intersects("geo", inter));
        criteria.addOrder(new DistanceOrder("geo", point));
        criteria.setMaxResults(limit);
        List result = criteria.list();
        for (Object oR : result) {
            lstF.add(((FeatureDbEntity) oR).toPojo());
        }
        return lstF;
    }
}
