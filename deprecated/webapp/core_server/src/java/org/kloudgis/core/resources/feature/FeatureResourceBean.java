/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.resources.feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.kloudgis.core.GeometryFactory;
import org.kloudgis.core.exception.ServerErrorException;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.feature.FeatureDbEntity;
import org.kloudgis.core.persistence.feature.FeatureTypeDbEntity;
import org.kloudgis.core.pojo.FeatureCollection;
import org.kloudgis.core.pojo.GeoWKT;
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.core.pojo.feature.QueryResult;

/**
 *
 * @author jeanfelixg
 */
public abstract class FeatureResourceBean {

    protected abstract HibernateEntityManager getEntityManager();

    @GET
    @Produces({"application/json"})
    public QueryResult getFeatures(
            @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("-1") @QueryParam("length") Integer length,
            @QueryParam("sort") String sort,
            @QueryParam("sortState") String sortState) {
        HibernateEntityManager em = getEntityManager();
        Criteria cr = em.getSession().createCriteria(getEntityDbClass()).setFirstResult(start);
        if (length >= 0) {
            cr.setMaxResults(length);
        }
        if (sort != null) {
            if (sortState != null && sortState.equals("DESC")) {
                cr.addOrder(Order.desc(sort));
            } else {
                cr.addOrder(Order.asc(sort));
            }

        }
        List<FeatureDbEntity> lstR = cr.list();
        List<Feature> lstEntity = toPojo(lstR, em);
        Long count = new Integer(lstR.size()).longValue();
        if (start.intValue() > 0 || length.intValue() != -1) {
            Query qCount = em.createQuery(
                    "SELECT COUNT(e) FROM " + getEntityDbClass().getSimpleName() + " e");
            count = (Long) qCount.getSingleResult();
        }
        QueryResult qResult = new QueryResult(lstEntity, count);
        em.close();
        return qResult;
    }

    protected List<Feature> toPojo(Collection<FeatureDbEntity> lstDbFea, EntityManager em) {
        List<Feature> lstPojo = new ArrayList();
        if (lstDbFea != null && lstDbFea.size() > 0) {
            for (FeatureDbEntity fDb : lstDbFea) {
               lstPojo.add(fDb.toPojo());
            }
        }
        return lstPojo;
    }

    public FeatureTypeDbEntity getFeatureType(String name) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        List<FeatureTypeDbEntity> lstFT = em.createQuery("SELECT f from FeatureTypeDbEntity f where f.name=:fname").setParameter("fname", name).getResultList();
        em.close();
        if (lstFT.size() > 0) {
            return lstFT.get(0);
        }
        return null;
    }

    @GET
    @Path("{fId}")
    @Produces({"application/json"})
    public Feature getFeature(@PathParam("fId") Long fId) {
        System.out.println("Get feature: " + getEntityDbClass().getSimpleName());
        EntityManager em = getEntityManager();
        FeatureDbEntity fDb = getFeatureDb(em, fId);
        if (fDb != null) {
            Feature f = fDb.toPojo();
            em.close();
            return f;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + fId);
    }

    @DELETE
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response deleteFeature(@PathParam("fId") Long fid, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        FeatureDbEntity uDb = (FeatureDbEntity) em.find(getEntityDbClass(), fid);
        if (uDb != null) {
            em.getTransaction().begin();
            em.remove(uDb);
            em.getTransaction().commit();
        }else{
            em.close();
            throw new EntityNotFoundException(fid + " Feature not found");
        }
        em.close();
        return Response.ok().build();
    }

    public Feature doAddFeature(Feature feature, HttpServletRequest req, ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        FeatureDbEntity fDb = feature.toDbEntity(em);
        em.persist(fDb);
        em.getTransaction().commit();
        feature = fDb.toPojo();
        em.close();
        return feature;
    }

    public Feature doUpdateFeature(Feature feature, Long fid, HttpServletRequest req, ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        FeatureDbEntity uDb = (FeatureDbEntity) em.find(getEntityDbClass(), fid);
        if (uDb != null) {
            em.getTransaction().begin();
            uDb.fromPojo(feature);
            em.getTransaction().commit();
            feature = uDb.toPojo();
        }else{
            em.close();
            throw new EntityNotFoundException(fid + " Feature not found");
        }
        em.close();
        return feature;
    }

    @GET
    @Path("intersects")
    @Produces({"application/json"})
    public FeatureCollection getIntersectsFeatures(@QueryParam("lowLon") Double lowLon, @QueryParam("lowLat") Double lowLat,
            @QueryParam("hiLon") Double hiLon, @QueryParam("hiLat") Double hiLat, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("50") @QueryParam("length") Integer length,
            @DefaultValue("false") @QueryParam("count") Boolean count) {
        if (lowLon == null || lowLat == null || hiLon == null || hiLat == null) {
            throw new IllegalArgumentException("null are not acceptable.");
        }
        Polygon pEnvelope = GeometryFactory.createPolygon(GeometryFactory.createLinearRing(new Coordinate[]{new Coordinate(lowLon, lowLat),
                    new Coordinate(hiLon, lowLat), new Coordinate(hiLon, hiLat), new Coordinate(lowLon, hiLat), new Coordinate(lowLon, lowLat)}), null);
        pEnvelope.setSRID(4326);
        EntityManager em = getEntityManager();
        Object[] result = getIntersectsFeature(pEnvelope, em, start, length, count);
        List<Feature> lstPojo = toPojo((Collection<FeatureDbEntity>) result[0], em);
        em.close();
        return new FeatureCollection(lstPojo, (Integer) result[1]);
    }

    protected abstract Object[] getIntersectsFeature(Polygon polygon, EntityManager em, Integer start, Integer length, boolean count);
        

    protected FeatureDbEntity getFeatureDb(EntityManager em, Long fId) {
        FeatureDbEntity fDb = (FeatureDbEntity) em.find(getEntityDbClass(), fId);
        return fDb;
    }

    public abstract Class getEntityDbClass();

    @GET
    @Path("search")
    @Produces({"application/json"})
    public FeatureCollection search(@QueryParam("searchstring") String search, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("50") @QueryParam("length") Integer length,
            @DefaultValue("false") @QueryParam("count") Boolean bCount) {
        HibernateEntityManager em = getEntityManager();
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        FullTextQuery query = buildSearchQuery(ftem, search);
        if (query == null) {
            throw new ServerErrorException("Could'nt search for '" + search + "'");
        }
        Integer count = null;
        if (bCount) {
            count = query.getResultSize();
        }
        query.setMaxResults(length);
        query.setFirstResult(start);
        List<FeatureDbEntity> lstR = query.getResultList();
        List<Feature> lstPojos = toPojo(lstR, em);
        ftem.close();
        return new FeatureCollection(lstPojos, count);
    }

    @GET
    @Path("count_search")
    @Produces({"application/json"})
    public Integer countSearch(@QueryParam("searchstring") String search) {
        if (search == null) {
            return 0;
        }
        HibernateEntityManager em = getEntityManager();
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        FullTextQuery query = buildSearchQuery(ftem, search);
        if (query == null) {
            throw new ServerErrorException("Could'nt search count for '" + search + "'");
        }
        int iResultSize = query.getResultSize();
        ftem.close();
        return iResultSize;
    }

    protected FullTextQuery buildSearchQuery(FullTextEntityManager ftem, String search) {
        System.out.println("Search " + getEntityDbClass().getName() + " in " + Arrays.asList(getSearchFields()) + " for " + search);
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, getSearchFields(), new StandardAnalyzer(Version.LUCENE_29));
        org.apache.lucene.search.Query query = null;
        try {
            query = parser.parse(search);
        } catch (ParseException ex) {
            System.out.println("Error parsing: " + ex);
            return null;
        }
        FullTextQuery ftq = ftem.createFullTextQuery(query, getEntityDbClass());
        return ftq;
    }

    protected abstract String[] getSearchFields();

    @GET
    @Path("{fId}/defaultGeometry")
    @Produces({"application/json"})
    public GeoWKT getDefaultGeo(@PathParam("fId") Long fId) {
        String wkt = null;
        EntityManager em = getEntityManager();
        FeatureDbEntity fDb = getFeatureDb(em, fId);
        if (fDb != null) {
            Geometry geo = fDb.getGeometry();
            if (geo != null) {
                wkt = geo.toText();
            }
            em.close();
            return new GeoWKT(wkt);
        } else {
            em.close();
            throw new EntityNotFoundException("Not found:" + fId);
        }

    }
}
