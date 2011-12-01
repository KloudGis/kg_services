/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import org.kloudgis.data.model.ModelFactory;
import com.sun.jersey.api.NotFoundException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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
import org.hibernate.criterion.Criterion;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.data.pojo.Feature;
import org.kloudgis.data.pojo.LoadFeature;
import org.kloudgis.data.pojo.SearchCategory;
import org.kloudgis.data.store.DistanceOrder;
import org.kloudgis.data.store.FeatureDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.core.pojo.Records;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/features")
@Produces({"application/json"})
public class FeatureResourceBean {

    @GET
    @Path("features_at")
    public Response getFeaturesAt(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @QueryParam("lon") Double lon, @QueryParam("lat") Double lat,
            @QueryParam("layers") String layers, @QueryParam("limit") Integer limit, @QueryParam("one_pixel") Double onePixelWorld) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember == null) {
                em.close();
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
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
    public Response search(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("search_string") String search, @QueryParam("category") String cat, @QueryParam("sandbox") String sandbox) {
        if (search == null || search.length() == 0) {
            return Response.ok().build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        MemberDbEntity lMember = null;
        try {
            lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
        } catch (IOException ex) {
            em.close();
            return Response.serverError().entity(ex.getMessage()).build();
        }
        if (lMember == null) {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
        }
        FullTextEntityManager sem = Search.getFullTextEntityManager(em);
        FullTextQuery query;
        List lstPojos = new ArrayList();
        if (cat != null && cat.equals("_notes_")) {
            query = buildNoteSearchQuery(sem, search);
            if (query == null) {
                sem.close();
                return Response.serverError().entity("Could'nt build query for: " + search).build();
            }
            List<NoteDbEntity> lstR = query.getResultList();
            for (NoteDbEntity f : lstR) {
                lstPojos.add(f.toPojo());
            }
        } else {
            query = buildSearchQuery(sem, search);
            if (query == null) {
                sem.close();
                return Response.serverError().entity("Could'nt build query for: " + search).build();
            }
            List<FeatureDbEntity> lstR = query.getResultList();
            Map<String, FeatureTypeDbEntity> mapFt = ModelFactory.getFeatureTypes(em);
            if (cat.equals("_unknown")) {
                cat = null;
            }
            for (FeatureDbEntity f : lstR) {
                if (f.getFeatureType() == null) {
                    if (cat == null) {
                        lstPojos.add(f.toPojo(mapFt));
                    }
                } else if (cat != null && f.getFeatureType().equals(cat)) {
                    lstPojos.add(f.toPojo(mapFt));
                }

            }
        }
        sem.close();
        Records records = new Records();
        records.records = lstPojos;
        return Response.ok(records).build();
    }

    @GET
    @Path("count_search")
    @Produces({"application/json"})
    public Response countSearch(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("search_string") String search,
            @QueryParam("sandbox") String sandbox) {
        if (search == null || search.length() == 0) {
            return Response.ok(new SearchCategory(0, "?", "?", search, 0)).build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        MemberDbEntity lMember = null;
        try {
            lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
        } catch (IOException ex) {
            em.close();
            return Response.serverError().entity(ex.getMessage()).build();
        }
        if (lMember == null) {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
        }
        FullTextEntityManager sem = Search.getFullTextEntityManager(em);
        FullTextQuery query = buildSearchQuery(sem, search);
        if (query == null) {
            sem.close();
            return Response.serverError().entity("Could'nt build query for: " + search).build();
        }
        Map<String, FeatureTypeDbEntity> model = ModelFactory.getFeatureTypes(em);
        Map<String, Integer> mapFt = new TreeMap();
        List<FeatureDbEntity> list = query.getResultList();
        String unknown = "_unknown";
        for (FeatureDbEntity fea : list) {
            String ft = fea.getFeatureType();
            if (ft == null) {
                ft = unknown;
            }
            Integer size = mapFt.get(ft);
            if (size == null) {
                mapFt.put(ft, 1);
            } else {
                mapFt.put(ft, size + 1);
            }
        }
        Records rec = new Records();
        List<SearchCategory> lstCat = new ArrayList();
        for (String ft : mapFt.keySet()) {
            FeatureTypeDbEntity entity = model.get(ft);
            String label = ft;
            if (entity != null) {
                label = entity.getLabel();
            }
            SearchCategory cat = new SearchCategory(ft.hashCode(), ft, label, search, mapFt.get(ft));
            lstCat.add(cat);
        }
        query = buildNoteSearchQuery(sem, search);
        if (query != null) {
            int sizeNote = query.getResultSize();
            if (sizeNote > 0) {
                String key = "_notes_";
                lstCat.add(new SearchCategory(key.hashCode(), key, "_Notes", search, sizeNote));
            }
        }
        sem.close();
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
        FullTextQuery ftq = sem.createFullTextQuery(query, FeatureDbEntity.class);
        return ftq;
    }

    protected String[] getSearchFields() {
        return new String[]{"index1", "index2", "index3", "index4", "index5", "index6", "index7", "index8", "index9", "index10"};
    }

    protected FullTextQuery buildNoteSearchQuery(FullTextEntityManager sem, String search) {
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, new String[]{"title", "description", "comments.content"}, new StandardAnalyzer(Version.LUCENE_29));
        org.apache.lucene.search.Query query = null;
        try {
            query = parser.parse(search);
        } catch (ParseException ex) {
            System.out.println("Error parsing: " + ex);
            return null;
        }
        FullTextQuery ftq = sem.createFullTextQuery(query, NoteDbEntity.class);
        return ftq;
    }

    //TO remove ???
    @POST
    @Path("build_search_index")
    public Response search(@QueryParam("sandbox") String sandbox) {
        SearchFactory.buildSearchIndexFor(sandbox);
        return Response.ok().build();
    }

    @POST
    @Path("batch_add")
    public Response addBatchFeatures(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, List<LoadFeature> features) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
                if(lMember != null && !AuthorizationFactory.isSandboxOwner(lMember, sContext, auth_token, sandbox)){
                    em.close();
                    return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the sandbox owner for: " + sandbox).build();
                }
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember == null) {
                em.close();
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
            em.getTransaction().begin();
            for (LoadFeature pojo : features) {
                FeatureDbEntity entity = new FeatureDbEntity();
                entity.fromPojo(pojo);
                entity.setDateInsert(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                em.persist(entity);
            }
            em.getTransaction().commit();
            return Response.ok().entity(features.size() + " features added.").build();
        }
        return Response.notModified().entity("sandbox entity manager not found for " + sandbox).build();
    }
}
