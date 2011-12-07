/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import org.kloudgis.core.pojo.Coordinate;
import org.kloudgis.core.utils.GeometryFactory;

/**
 *
 * @author jeanfelixg
 */
public class Geometry {

    public List<Coordinate> coords;
    public String geo_type;
    public Coordinate centroid;

    public Geometry() {
    }

    public Geometry(com.vividsolutions.jts.geom.Geometry geo) {
        com.vividsolutions.jts.geom.Coordinate[] arrC = geo.getCoordinates();
        ArrayList<org.kloudgis.core.pojo.Coordinate> arrCPojo = new ArrayList(arrC.length);
        for (com.vividsolutions.jts.geom.Coordinate c : arrC) {
            arrCPojo.add(new org.kloudgis.core.pojo.Coordinate(c.x, c.y));
        }
        this.coords = arrCPojo;
        this.geo_type = geo.getGeometryType();
        if (arrC.length > 0) {
            Point ptC = geo.getCentroid();
            if (ptC != null) {
                this.centroid = new org.kloudgis.core.pojo.Coordinate(ptC.getX(), ptC.getY());
            }

        }
    }

    public com.vividsolutions.jts.geom.Geometry toJTS() {
        if(geo_type.equals("Point")){
            return GeometryFactory.createPoint(coords.get(0).toJTS());
        }else if(geo_type.equals("Linestring")){
            com.vividsolutions.jts.geom.Coordinate [] arrC = new com.vividsolutions.jts.geom.Coordinate[coords.size()];
            for(int i=0; i < coords.size(); i++){
                arrC[i] = coords.get(i).toJTS();
            }
            return GeometryFactory.createLineString(arrC);
        }else if(geo_type.equals("Polygon")){
            com.vividsolutions.jts.geom.Coordinate [] arrC = new com.vividsolutions.jts.geom.Coordinate[coords.size()];
            for(int i=0; i < coords.size(); i++){
                arrC[i] = coords.get(i).toJTS();
            }
            //TODO add support to HOLES
            return GeometryFactory.createPolygon(GeometryFactory.createLinearRing(arrC), null);
        }
        //TODO add support to Multi GEO
        return null;
    }
}
