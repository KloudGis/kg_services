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
package org.kloudgis.resources.feature;

import com.vividsolutions.jts.geom.Geometry;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.feature.FeatureDbEntity;
import org.kloudgis.core.persistence.feature.PlaceItemDbEntity;
import org.kloudgis.core.pojo.Chart;
import org.kloudgis.core.pojo.DataTable;
import org.kloudgis.core.pojo.DataTableCell;
import org.kloudgis.core.pojo.DataTableColumn;
import org.kloudgis.core.pojo.DataTableRow;
import org.kloudgis.core.pojo.FeatureCollection;
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.core.resources.feature.PlaceItemResourceBean;
import org.kloudgis.persistence.ArpenteurDbEntity;
import org.kloudgis.persistence.HydroDbEntity;
import org.kloudgis.persistence.LotDbEntity;
import org.kloudgis.persistence.NoteDbEntity;
import org.kloudgis.persistence.RoadDbEntity;

/**
 *
 * @author jeanfelixg
 */
public abstract class PlaceItemChartsBean extends PlaceItemResourceBean {

    @GET
    @Path("{fid}/road_km")
    @Produces({"application/json"})
    public Chart getRoadChart(@PathParam("fid") Long fid, @DefaultValue("fr") @QueryParam("locale") String loc) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        double dTotal = 0;
        if (place != null) {
            NumberFormat format = NumberFormat.getInstance(Locale.CANADA_FRENCH);
            format.setMaximumFractionDigits(1);
            String geoC = "geomfromtext('" + place.getGeometry() + "', 4326)";
            String sql = "select sum(length_spheroid(intersection(r.geo," + geoC + "), 'SPHEROID[\"GRS_1980\",6378137,298.257222101]')), r.category from rue r where intersects(r.geo," + geoC + ") group by r.category";
            Query query = em.createNativeQuery(sql);
            List<Object[]> listR = query.getResultList();
            DataTableRow[] rows = new DataTableRow[listR.size()];
            int iCpt = 0;
            for (Object[] res : listR) {
                Number nLen = (Number) res[0];
                double dKm = nLen.doubleValue() / 1000;
                String strKm = format.format(dKm) + " km";
                int iKm = (int) dKm;
                if (iKm == 0) {
                    //bug in GV if in between 0 and 1
                    iKm = 1;
                }
                String cat = (String) res[1];
                String label = RoadResourceBean.getRoadLabel(cat, loc);
                rows[iCpt++] = new DataTableRow(new Object[]{new DataTableCell<String>(cat, label), new DataTableCell<Number>(iKm, strKm)});
                dTotal += nLen.doubleValue();
            }
            DataTable data = new DataTable();
            data.setColumns(new DataTableColumn[]{
                        new DataTableColumn("category", "", "string"),
                        new DataTableColumn("length", "", "number")
                    });
            data.setValues(rows);

            String totalKm = format.format(dTotal / 1000);
            String title = "Routes (" + totalKm + " km)";
            if (loc.equals("en")) {
                title = "Roads (" + totalKm + " km)";
            }
            em.close();
            return new Chart(title, data);
        }
        em.close();
        throw new EntityNotFoundException(fid + " is not found!");
    }

    @GET
    @Path("{fid}/road_km_query")
    @Produces({"application/json"})
    public FeatureCollection queryRoadChart(@PathParam("fid") Long fid, @QueryParam("item") String itemFromChart, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("50") @QueryParam("length") Integer length,
            @DefaultValue("false") @QueryParam("count") Boolean bCount) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        if (place != null) {
            Geometry geoCity = place.getGeometry();
            Session session = em.getSession();
            Criteria cr = session.createCriteria(RoadDbEntity.class);
            cr.add(SpatialRestrictions.intersects("geo", geoCity));
            cr.add(Restrictions.eq("category", itemFromChart));
            Integer count = null;
            if (bCount) {
                cr.setProjection(Projections.rowCount());
                count = ((Number) cr.uniqueResult()).intValue();
                cr.setProjection(null);
            }
            cr.setFirstResult(start);
            cr.setFetchSize(length);
            cr.setMaxResults(length);
            cr.addOrder(Order.asc("title"));
            List<FeatureDbEntity> lstR = cr.list();
            List<Feature> lstPojo = toPojo(lstR, em);
            em.close();
            return new FeatureCollection(lstPojo, count);
        }
        em.close();
        return new FeatureCollection();
    }

    @GET
    @Path("{fid}/hydro_km")
    @Produces({"application/json"})
    public Chart getHydroChart(@PathParam("fid") Long fid, @DefaultValue("fr") @QueryParam("locale") String loc) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        double dTotal = 0;
        if (place != null) {
            NumberFormat format = NumberFormat.getInstance(Locale.CANADA_FRENCH);
            format.setMaximumFractionDigits(1);
            String geoC = "geomfromtext('" + place.getGeometry() + "', 4326)";
            String sqlLine = "select sum(length_spheroid(intersection(r.geo," + geoC + "), 'SPHEROID[\"GRS_1980\",6378137,298.257222101]')), r.category from hydro r where geometrytype(geo) != 'POLYGON' and intersects(r.geo," + geoC + ") group by r.category";
            String sqlPoly = "select sum(length_spheroid(boundary(intersection(r.geo," + geoC + ")), 'SPHEROID[\"GRS_1980\",6378137,298.257222101]')), r.category from hydro r where geometrytype(geo) = 'POLYGON' and ( not(contains(r.geo, " + geoC + ")) and (intersects(r.geo," + geoC + "))) group by r.category";
            Query queryL = em.createNativeQuery(sqlLine);
            Query queryP = em.createNativeQuery(sqlPoly);
            List<Object[]> listLine = queryL.getResultList();
            List<Object[]> listPoly = queryP.getResultList();
            ArrayList<Object[]> listR = new ArrayList();
            listR.addAll(listLine);
            listR.addAll(listPoly);
            DataTableRow[] rows = new DataTableRow[listR.size()];
            int iCpt = 0;
            for (Object[] res : listR) {
                Number nLen = (Number) res[0];
                double dKm = nLen.doubleValue() / 1000;
                String strKm = format.format(dKm) + " km";
                int iKm = (int) dKm;
                if (iKm == 0) {
                    //bug in GV if in between 0 and 1
                    iKm = 1;
                }
                String cat = (String) res[1];
                String label = HydroResourceBean.getHydroLabel(cat, loc);
                rows[iCpt++] = new DataTableRow(new Object[]{new DataTableCell<String>(cat, label), new DataTableCell<Number>(iKm, strKm)});
                dTotal += nLen.doubleValue();
            }
            DataTable data = new DataTable();
            data.setColumns(new DataTableColumn[]{
                        new DataTableColumn("category", "", "string"),
                        new DataTableColumn("length", "", "number")
                    });
            data.setValues(rows);

            String totalKm = format.format(dTotal / 1000);
            String title = "Hydrographie (" + totalKm + " km)";
            if (loc.equals("en")) {
                title = "Hydrography (" + totalKm + " km)";
            }
            em.close();
            return new Chart(title, data);
        }
        em.close();
        throw new EntityNotFoundException(fid + " is not found!");
    }

    @GET
    @Path("{fid}/hydro_km_query")
    @Produces({"application/json"})
    public FeatureCollection queryHydroChart(@PathParam("fid") Long fid, @QueryParam("item") String itemFromChart, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("50") @QueryParam("length") Integer length,
            @DefaultValue("false") @QueryParam("count") Boolean bCount) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        if (place != null) {
            Geometry geoCity = place.getGeometry();
            Session session = em.getSession();
            Criteria cr = session.createCriteria(HydroDbEntity.class);
            cr.add(SpatialRestrictions.intersects("geo", geoCity));
            cr.add(Restrictions.not(SpatialRestrictions.contains("geo", geoCity)));
            cr.add(Restrictions.eq("category", itemFromChart));
            Integer count = null;
            if (bCount) {
                cr.setProjection(Projections.rowCount());
                count = ((Number) cr.uniqueResult()).intValue();
                cr.setProjection(null);
            }
            cr.setFirstResult(start);
            cr.setFetchSize(length);
            cr.setMaxResults(length);
            cr.addOrder(Order.asc("title"));
            List<FeatureDbEntity> lstR = cr.list();
            List<Feature> lstPojo = toPojo(lstR, em);
            em.close();
            return new FeatureCollection(lstPojo, count);
        }
        em.close();
        return new FeatureCollection();
    }

    @GET
    @Path("{fid}/lot_chart")
    @Produces({"application/json"})
    public Chart getLots(@PathParam("fid") Long fid, @DefaultValue("fr") @QueryParam("locale") String loc) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        int iTotal = 0;
        if (place != null) {
            Geometry geoCity = place.getGeometry();
            Session session = em.getSession();
            Criteria cr = session.createCriteria(LotDbEntity.class);
            cr.add(SpatialRestrictions.intersects("geo", geoCity));
            ProjectionList proj = Projections.projectionList();
            proj.add(Projections.rowCount());
            proj.add(Projections.groupProperty("category"));
            cr.setProjection(proj);
            cr.addOrder(Order.asc("category"));
            List<Object[]> lstR = cr.list();
            DataTableRow[] rows = new DataTableRow[lstR.size()];
            int iCpt = 0;
            for (Object[] res : lstR) {
                Number nLot = (Number) res[0];
                String strCount = nLot + " lots";
                if (nLot.intValue() == 0) {
                    //bug in GV if in between 0 and 1
                    nLot = new Integer(1);
                }
                String cat = (String) res[1];
                String label = LotResourceBean.getLotLabel(cat, loc);
                rows[iCpt++] = new DataTableRow(new Object[]{new DataTableCell<String>(cat, label), new DataTableCell<Number>(nLot, strCount)});
                iTotal += nLot.intValue();
            }
            DataTable data = new DataTable();
            data.setColumns(new DataTableColumn[]{
                        new DataTableColumn("category", "", "string"),
                        new DataTableColumn("length", "", "number")
                    });
            data.setValues(rows);
            String title = iTotal + " lots";
            if (loc.equals("en")) {
                title = iTotal + " lots";
            }
            em.close();
            return new Chart(title, data);
        }
        em.close();
        throw new EntityNotFoundException(fid + " is not found!");
    }

    @GET
    @Path("{fid}/lot_chart_query")
    @Produces({"application/json"})
    public FeatureCollection queryLotChart(@PathParam("fid") Long fid, @QueryParam("item") String itemFromChart, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("50") @QueryParam("length") Integer length,
            @DefaultValue("false") @QueryParam("count") Boolean bCount) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        if (place != null) {
            Geometry geoCity = place.getGeometry();
            Session session = em.getSession();
            Criteria cr = session.createCriteria(LotDbEntity.class);
            cr.add(SpatialRestrictions.intersects("geo", geoCity));
            cr.add(Restrictions.eq("category", itemFromChart));
            Integer count = null;
            if (bCount) {
                cr.setProjection(Projections.rowCount());
                count = ((Number) cr.uniqueResult()).intValue();
                cr.setProjection(null);
            }
            cr.setFirstResult(start);
            cr.setFetchSize(length);
            cr.setMaxResults(length);
            cr.addOrder(Order.asc("title"));
            List<FeatureDbEntity> lstR = cr.list();
            List<Feature> lstPojo = toPojo(lstR, em);
            em.close();
            return new FeatureCollection(lstPojo, count);
        }
        em.close();
        return new FeatureCollection();
    }

    @GET
    @Path("{fid}/arpenteurs")
    @Produces({"application/json"})
    public FeatureCollection getArpenteurCount(@PathParam("fid") Long fid) {
        return this.getArpenteurs(fid, 0, 1, true);
    }

    @GET
    @Path("{fid}/arpenteurs_query")
    @Produces({"application/json"})
    public FeatureCollection getArpenteurs(@PathParam("fid") Long fid, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("1") @QueryParam("length") Integer length,
            @DefaultValue("true") @QueryParam("count") Boolean bCount) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        if (place != null) {
            Geometry geoCity = place.getGeometry();
            Session session = em.getSession();
            Criteria cr = session.createCriteria(ArpenteurDbEntity.class);
            cr.add(SpatialRestrictions.within("geo", geoCity));
            Integer count = null;
            if (bCount) {
                cr.setProjection(Projections.rowCount());
                count = ((Number) cr.uniqueResult()).intValue();
                cr.setProjection(null);
            }
            cr.setFirstResult(start);
            cr.setFetchSize(length);
            cr.setMaxResults(length);
            cr.addOrder(Order.asc("firstname"));
            List<FeatureDbEntity> lstR = cr.list();
            List<Feature> lstPojo = toPojo(lstR, em);
            em.close();
            return new FeatureCollection(lstPojo, count);
        }
        em.close();
        return new FeatureCollection();
    }

    @GET
    @Path("{fid}/notes")
    @Produces({"application/json"})
    public FeatureCollection getNotesCount(@PathParam("fid") Long fid) {
        return this.getNotes(fid, 0, 1, true);
    }

    @GET
    @Path("{fid}/notes_query")
    @Produces({"application/json"})
    public FeatureCollection getNotes(@PathParam("fid") Long fid, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("1") @QueryParam("length") Integer length,
            @DefaultValue("true") @QueryParam("count") Boolean bCount) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerData();
        PlaceItemDbEntity place = (PlaceItemDbEntity) em.find(getEntityDbClass(), fid);
        if (place != null) {
            Geometry geoCity = place.getGeometry();
            Session session = em.getSession();
            Criteria cr = session.createCriteria(NoteDbEntity.class);
            cr.add(SpatialRestrictions.intersects("geom", geoCity));
            Integer count = null;
            if (bCount) {
                cr.setProjection(Projections.rowCount());
                count = ((Number) cr.uniqueResult()).intValue();
                cr.setProjection(null);
            }
            cr.setFirstResult(start);
            cr.setFetchSize(length);
            cr.setMaxResults(length);
            cr.addOrder(Order.asc("title"));
            List<FeatureDbEntity> lstR = cr.list();
            List<Feature> lstPojo = toPojo(lstR, em);
            em.close();
            return new FeatureCollection(lstPojo, count);
        }
        em.close();
        return new FeatureCollection();
    }
}
