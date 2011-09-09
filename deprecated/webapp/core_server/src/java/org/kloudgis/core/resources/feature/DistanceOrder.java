package org.kloudgis.core.resources.feature;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public class DistanceOrder extends Order {

    private final Point p;
    private final String propertyName;

    public DistanceOrder(final String propertyName, Point point) {
        super(null, false);
        this.p = point;
        this.propertyName = propertyName;
    }

//    double boxSize = 1;
    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String col = criteriaQuery.getColumn(criteria, propertyName);
        return "distance_sphere(centroid(" + col + "), GeomFromText('" + p.toText() + "', " + p.getSRID() + ")) asc";
    }

}
