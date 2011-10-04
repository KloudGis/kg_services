/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import org.kloudgis.model.ModelFactory;
import com.sun.jersey.api.NotFoundException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Criterion;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.GeometryFactory;
import org.kloudgis.data.pojo.Feature;
import org.kloudgis.data.pojo.SearchCategory;
import org.kloudgis.data.store.DistanceOrder;
import org.kloudgis.data.store.FeatureDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
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

    protected Class getEntityDbClass() {
        return FeatureDbEntity.class;
    }

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
        Criteria criteria = em.getSession().createCriteria(getEntityDbClass());
        Criterion crit = layer.getRestriction();
        if (crit != null) {
            criteria.add(crit);
        }
        criteria.add(SpatialRestrictions.intersects("geo", inter));
        criteria.addOrder(new DistanceOrder("geo", point));
        criteria.setMaxResults(limit);
        List result = criteria.list();
        Map<String, FeatureTypeDbEntity> mapFt = ModelFactory.getFeatureTypes(em);
        for (Object oR : result) {
            lstF.add(((FeatureDbEntity) oR).toPojo(mapFt));
        }
        return lstF;
    }

    @GET
    @Path("search")
    @Produces({"application/json"})
    public Response search(@QueryParam("search_string") String search, @QueryParam("sandbox") String sandbox) {
        if (search == null || search.length() == 0) {
            return Response.ok().build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        FullTextEntityManager sem = Search.getFullTextEntityManager(em);
        FullTextQuery query = buildSearchQuery(sem, search);
        if (query == null) {
            sem.close();
            return Response.serverError().entity("Could'nt build query for: " + search).build();
        }
        List<FeatureDbEntity> lstR = query.getResultList();
        List lstPojos = new ArrayList();
        Map<String, FeatureTypeDbEntity> mapFt = ModelFactory.getFeatureTypes(em);
        for (FeatureDbEntity f : lstR) {
            lstPojos.add(f.toPojo(mapFt));
        }
        sem.close();
        Records records = new Records();
        records.records = lstPojos;
        return Response.ok(records).build();
    }

    @GET
    @Path("count_search")
    @Produces({"application/json"})
    public Response countSearch(@QueryParam("search_string") String search, @QueryParam("sandbox") String sandbox) {
        if (search == null || search.length() == 0) {
            return Response.ok(new SearchCategory("features","Features",search, 0)).build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        FullTextEntityManager sem = Search.getFullTextEntityManager(em);
        FullTextQuery query = buildSearchQuery(sem, search);
        if (query == null) {
            sem.close();
            return Response.serverError().entity("Could'nt build query for: " + search).build();
        }
        int iResultSize = query.getResultSize();
        sem.close();
        Records rec = new Records();
        SearchCategory cat = new SearchCategory("features", "Features", search, iResultSize);
        List lstCat = new ArrayList(1);
        lstCat.add(cat);
        rec.records = lstCat;
        return Response.ok(rec).build();
    }

    protected FullTextQuery buildSearchQuery(FullTextEntityManager sem, String search) {
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, getSearchFields(), new StandardAnalyzer(Version.LUCENE_29));
        org.apache.lucene.search.Query query = null;
        try {
            query = parser.parse(search);
        } catch (ParseException ex) {
            System.out.println("Error parsing: " + ex);
            return null;
        }
        FullTextQuery ftq = sem.createFullTextQuery(query, getEntityDbClass());
        return ftq;
    }

    protected String[] getSearchFields() {
        return new String[]{"index1", "index2", "index3", "index4", "index5"};
    }

    @POST
    @Path("build_search_index")
    @Produces({"application/json"})
    public Response search(@QueryParam("sandbox") String sandbox) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        ftem.setFlushMode(FlushModeType.COMMIT);
        EntityTransaction trx = ftem.getTransaction();
        Class clazz = getEntityDbClass();
        System.out.println("Indexing: " + clazz);
        trx.begin();
        ftem.purgeAll(clazz);
        //Scrollable results will avoid loading too many objects in memory
        int BATCH_SIZE = 500;
        ScrollableResults results = em.getSession().createCriteria(clazz).setFetchSize(BATCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
        int index = 0;
        while (results.next()) {
            index++;
            ftem.index(results.get(0)); //index each element
            if (index % BATCH_SIZE == 0) {
                ftem.flushToIndexes(); //free memory since the queue is processed
                ftem.clear(); //free memory since the queue is processed
            }
        }
        if (index % BATCH_SIZE != 0) {
            ftem.flushToIndexes(); //free memory since the queue is processed
            ftem.clear(); //free memory since the queue is processed
        }
        trx.commit();
        ftem.getSearchFactory().optimize(clazz);
        ftem.close();
        return Response.ok().build();
    }
}
