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
import java.util.List;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.GeometryFactory;
import org.kloudgis.KGConfig;
import org.kloudgis.data.pojo.Feature;
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
    
    /*
    
    @GET
    @Path("features_at")
    public Response getFeaturesAt(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @QueryParam("lon") Double lon, @QueryParam("lat") Double lat,
            @QueryParam("layers") String layers, @QueryParam("limit") Integer limit) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            Point point = GeometryFactory.createPoint(new Coordinate(lon, lat));
            point.setSRID(4326);                        
            List<Feature> arrF = findFeatures(em, point, limit, auth_token, layers);
            em.close();
            Records records = new Records();
            records.records = arrF;
            return Response.ok(records).build();
        } else {
            throw new NotFoundException("Sandbox entity manager not found for id:" + sandbox + ".");
        }
    }
    
    public List<Feature> findFeatures(EntityManager em, Point point, Integer limit, String auth_token, String layers) {
        List<Feature> lstF = new ArrayList<Feature>();
        HibernateEntityManager hem = (HibernateEntityManager) em;
        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(KGConfig.getConfiguration().map_url);
        getMethod.addRequestHeader("X-Kloudgis-Authentication", auth_token);
        NameValuePair [] values = new NameValuePair[1];
        values[0] = new NameValuePair("request", "GetFeatureInfo");
        values[1] = new NameValuePair("info_format", "application/vnd.ogc.wms");
        values[2] = new NameValuePair("EXCEPTIONS", "application/vnd.ogc.wms");
        values[3] = new NameValuePair("VERSION", "1.1.1");
        values[4] = new NameValuePair("FEATURE_COUNT", limit + "");
        values[5] = new NameValuePair("SRS", "EPSG:4326");
        values[6] = new NameValuePair("QUERY_LAYERS", layers);
        
        getMethod.setQueryString(values);
        client.executeMethod(getMethod);
        return lstF
    }*/
}
