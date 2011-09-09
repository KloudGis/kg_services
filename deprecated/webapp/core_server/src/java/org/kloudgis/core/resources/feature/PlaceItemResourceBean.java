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
package org.kloudgis.core.resources.feature;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.core.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
public abstract class PlaceItemResourceBean extends FeatureResourceBean {

    @Override
    protected Object[] getIntersectsFeature(Polygon polygonLonLat, EntityManager em, Integer start, Integer length, boolean bCount) {
        HibernateEntityManager hEm = (HibernateEntityManager) em;
        Session session = hEm.getSession();
        Criteria cr = session.createCriteria(getEntityDbClass());
        Point pt = polygonLonLat.getCentroid();
        pt.setSRID(polygonLonLat.getSRID());
        cr.add(SpatialRestrictions.intersects("geo", polygonLonLat));
        Integer count = null;
        if (bCount) {
            cr.setProjection(Projections.rowCount());
          //  long time  = Calendar.getInstance().getTimeInMillis();
            count = ((Number) cr.uniqueResult()).intValue();
           // System.out.println("count took " + (Calendar.getInstance().getTimeInMillis() - time));
            cr.setProjection(null);
        }
        cr.addOrder(Order.asc("title"));
        cr.setFirstResult(start);
        cr.setFetchSize(length);
        cr.setMaxResults(length);
       // long time  = Calendar.getInstance().getTimeInMillis();
        List lstF = cr.list();
       // System.out.println("lstF took " + (Calendar.getInstance().getTimeInMillis() - time));
        return new Object[]{lstF, count};
    }

    @Override
    protected HibernateEntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerData();
    }

    @Override
    protected String[] getSearchFields() {
        return new String[]{"title", "description", "category"};
    }
}
