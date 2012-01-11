/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import org.kloudgis.data.model.ModelFactory;
import com.sun.jersey.api.NotFoundException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
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
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.data.pojo.Feature;
import org.kloudgis.data.pojo.SearchCategory;
import org.kloudgis.data.store.helper.DistanceOrder;
import org.kloudgis.data.store.FeatureDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.core.pojo.Records;
import org.kloudgis.data.KGConfig;
import org.kloudgis.data.versioning.VersionEvent;
import org.kloudgis.data.versioning.VersionsFactory;

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
        for (Object oR : result) {
            lstF.add(((FeatureDbEntity) oR).toPojo());
        }
        return lstF;
    }

    @GET
    @Path("{fId}")
    public Response getFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") String fid_ft_id, @QueryParam("sandbox") String sandbox) {
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
                FeatureDbEntity feature = FeatureDbEntity.findByGuid(fid_ft_id, em);
                em.close();
                try {
                    if (feature != null) {
                        Feature pojo = feature.toPojo();
                        return Response.ok(pojo).build();
                    } else {
                        throw new EntityNotFoundException("Not found:" + fid_ft_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
    public Response addFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, Feature in_feature) {
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
                try {
                    if (AuthorizationFactory.hasWriteAccess(lMember, sContext, auth_token, sandbox)) {
                        em.getTransaction().begin();
                        FeatureDbEntity newFeature = new FeatureDbEntity();
                        newFeature.fromPojo(in_feature, em);
                        if (newFeature.getFid() == null) {
                            newFeature.generateFid(em);
                        } else {
                            if (FeatureDbEntity.findByGuid(FeatureDbEntity.buildGuid(newFeature.getFid(), newFeature.getFeatureTypeId()), em) != null) {
                                em.getTransaction().rollback();
                                em.close();
                                return Response.status(Response.Status.BAD_REQUEST).entity("Fid already in use:" + newFeature.getFid()).build();
                            }
                        }
                        newFeature.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                        newFeature.setUserCreate(lMember.getUserId());
                        em.persist(newFeature);
                        Feature pojo = newFeature.toPojo();
                        VersionsFactory.addVersion(VersionEvent.ADD, pojo, em, lMember);
                        em.getTransaction().commit();
                        if (newFeature.getGeometry() != null) {
                            Geometry geo = FeatureDbEntity.getGeoAs900913(newFeature, em);
                            if (geo != null) {
                                LayerDbEntity layer = ModelFactory.getMainLayer(em);
                                if (layer != null) {
                                    Envelope env = geo.getEnvelopeInternal();
                                    truncateCache(auth_token, layer.getName(), env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
                                }
                            }
                        }
                        em.close();
                        return Response.ok(pojo).build();
                    } else {
                        em.close();
                        return Response.status(Response.Status.UNAUTHORIZED).entity("User do not have write access in : " + sandbox).build();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (em.isOpen()) {
                        em.close();
                    }
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
    public Response deleteFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @PathParam("fId") String fid_ft_id) {
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
                try {
                    FeatureDbEntity feature = FeatureDbEntity.findByGuid(fid_ft_id, em);
                    try {
                        if (feature != null) {
                            if (AuthorizationFactory.hasWriteAccess(lMember, sContext, auth_token, sandbox)) {
                                Feature pojoBefore = feature.toPojo();
                                em.getTransaction().begin();
                                VersionsFactory.addVersion(VersionEvent.DELETE, pojoBefore, em, lMember);
                                if (feature.getGeometry() != null) {
                                    Geometry geo = FeatureDbEntity.getGeoAs900913(feature, em);
                                    if (geo != null) {
                                        LayerDbEntity layer = ModelFactory.getMainLayer(em);
                                        if (layer != null) {
                                            Envelope env = geo.getEnvelopeInternal();
                                            truncateCache(auth_token, layer.getName(), env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
                                        }
                                    }
                                }
                                em.remove(feature);
                                em.getTransaction().commit();
                                em.close();
                                return Response.ok().build();
                            } else {
                                em.close();
                                return Response.status(Response.Status.UNAUTHORIZED).entity("User do not have write access in : " + sandbox).build();
                            }
                        } else {
                            throw new EntityNotFoundException("Not found:" + fid_ft_id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (em.isOpen()) {
                            em.close();
                        }
                        return Response.serverError().entity(e.getMessage()).build();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (em.isOpen()) {
                        em.close();
                    }
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    private String truncateCache(String auth_token, String layer, double lowX, double lowY, double hiX, double hiY) {
        try {
            Map mapProp = new HashMap();
            mapProp.put("layer", layer);
            mapProp.put("low_x", lowX);
            mapProp.put("low_y", lowY);
            mapProp.put("hi_x", hiX);
            mapProp.put("hi_y", hiY);
            String[] ret = ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().map_url + "/truncate_cache", KGConfig.getConfiguration().api_key, mapProp);
            if (ret != null && ret[1].equals("200")) {
                return null;
            } else {
                return ret != null ? ret[0] : "api map error";
            }
        } catch (IOException ex) {
            return "Api map error";
        }
    }

    @PUT
    @Path("{fId}")
    public Response updateFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") String fid_ft_id, @QueryParam("sandbox") String sandbox, Feature in_feature) {
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
                FeatureDbEntity feature = FeatureDbEntity.findByGuid(fid_ft_id, em);
                try {
                    if (feature != null) {
                        if (AuthorizationFactory.hasWriteAccess(lMember, sContext, auth_token, sandbox)) {
                            Feature pojoBefore = feature.toPojo();
                            em.getTransaction().begin();
                            VersionsFactory.addVersion(VersionEvent.UPDATE, pojoBefore, em, lMember);
                            feature.fromPojo(in_feature);
                            feature.setDateUpdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                            feature.setUserUpdate(lMember.getUserId());
                            em.getTransaction().commit();
                            em.close();
                            Feature pojo = feature.toPojo();
                            //   SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                            //   NotificationFactory.postMessage(sandbox, auth_token, new NoteMessage(feature.getId(), NoteMessage.UPDATE, user.user));
                            return Response.ok(pojo).build();
                        } else {
                            em.close();
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User do not have write access in : " + sandbox).build();
                        }
                    } else {
                        throw new EntityNotFoundException("Not found:" + fid_ft_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (em.isOpen()) {
                        em.close();
                    }
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
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
            for (FeatureDbEntity f : lstR) {
                if (f.getFeatureTypeId() != null && f.getFeatureTypeId().toString().equals(cat)) {
                    lstPojos.add(f.toPojo());
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
            return Response.ok(new SearchCategory("-999", "?", search, 0)).build();
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
        Map<Long, Integer> mapFt = new TreeMap();
        List<FeatureDbEntity> list = query.getResultList();
        for (FeatureDbEntity fea : list) {
            Long ftId = fea.getFeatureTypeId();
            Integer size = mapFt.get(ftId);
            if (size == null) {
                mapFt.put(ftId, 1);
            } else {
                mapFt.put(ftId, size + 1);
            }
        }
        Records rec = new Records();
        List<SearchCategory> lstCat = new ArrayList();
        Map<Long, FeatureTypeDbEntity> model = ModelFactory.getFeatureTypes(em);
        for (Long ftId : mapFt.keySet()) {
            FeatureTypeDbEntity ftEntity = model.get(ftId);
            String label = ftEntity != null ? ftEntity.getLabel() : "";
            SearchCategory cat = new SearchCategory(ftId + "", label, search, mapFt.get(ftId));
            lstCat.add(cat);
        }
        query = buildNoteSearchQuery(sem, search);
        if (query != null) {
            int sizeNote = query.getResultSize();
            if (sizeNote > 0) {
                String key = "_notes_";
                lstCat.add(new SearchCategory(key, key, search, sizeNote));
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
        ArrayList<String> arrlF = new ArrayList();
        //text
        for (int i = 1; i < 26; i++) {
            arrlF.add("text" + i);
        }
        //num
        for (int i = 1; i < 11; i++) {
            arrlF.add("num" + i);
        }
        //decimal
        for (int i = 1; i < 11; i++) {
            arrlF.add("decim" + i);
        }
        arrlF.add("comments.comment");
        return arrlF.toArray(new String[arrlF.size()]);
    }

    protected FullTextQuery buildNoteSearchQuery(FullTextEntityManager sem, String search) {
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, new String[]{"title", "description", "comments.comment"}, new StandardAnalyzer(Version.LUCENE_29));
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
    public Response addBatchFeatures(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, List<Feature> features) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
                if (lMember != null && !AuthorizationFactory.isSandboxOwner(lMember, sContext, auth_token, sandbox)) {
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
            for (Feature pojo : features) {
                FeatureDbEntity entity = new FeatureDbEntity();
                entity.fromPojo(pojo, em);
                entity.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                entity.setUserCreate(lMember.getUserId());
                em.persist(entity);
            }
            em.getTransaction().commit();
            return Response.ok().entity(features.size() + " features added.").build();
        }
        return Response.notModified().entity("sandbox entity manager not found for " + sandbox).build();
    }
}
