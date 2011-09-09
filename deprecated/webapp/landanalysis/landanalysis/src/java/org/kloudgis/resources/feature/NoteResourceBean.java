/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.kloudgis.resources.feature;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.resources.feature.FeatureResourceBean;
import org.kloudgis.feature.pojo.Note;
import org.kloudgis.persistence.NoteDbEntity;

@Path("/protected/features/Note")
@Produces({"application/json"})
public class NoteResourceBean extends FeatureResourceBean {

    @Override
    public Class getEntityDbClass() {
        return NoteDbEntity.class;
    }

    @Override
    protected HibernateEntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerData();
    }

    @Override
    protected String[] getSearchFields() {
        return new String[]{"title", "description"};
    }

    @Override
    protected Object[] getIntersectsFeature(Polygon polygonLonLat, EntityManager em, Integer start, Integer length, boolean bCount) {
        HibernateEntityManager hEm = (HibernateEntityManager) em;
        Session session = hEm.getSession();
        Criteria cr = session.createCriteria(NoteDbEntity.class);
        Point pt = polygonLonLat.getCentroid();
        pt.setSRID(polygonLonLat.getSRID());
        cr.add(SpatialRestrictions.intersects("geom", polygonLonLat)).addOrder(Order.asc("title"));
        Integer count = null;
        if(bCount){
            count = cr.list().size();
        }
        cr.setFirstResult(start);
        cr.setFetchSize(length);
        cr.setMaxResults(length);
        return new Object[]{cr.list(),  count};
    }

    @POST
    @Produces({"application/json"})
    public Note addFeature(Note note, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (Note) doAddFeature(note, req, sContext);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Note updateFeature(Note note, @PathParam("fId") Long fid, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (Note) doUpdateFeature(note, fid, req, sContext);
    }
}
