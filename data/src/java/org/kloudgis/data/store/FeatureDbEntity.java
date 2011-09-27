/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.kloudgis.data.pojo.Attribute;
import org.kloudgis.data.pojo.Feature;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table (name="features")
public class FeatureDbEntity implements Serializable{
    
    @SequenceGenerator(name = "feature_seq_gen", sequenceName = "feature_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "feature_seq_gen")
    private Long id;
    @Column
    private Long fid;
    @Index(name = "feature_ft_index")
    @Column(length = 50)
    private String featuretype;
    @Column
    @Index(name = "feature_date_index")
    private Timestamp date_update;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;
    @Index(name = "feature_geotype_index")
    @Column(length = 50)
    private String geo_type;
    @Column
    @Index(name = "angle")
    private Double angle;
    @Column
    private String title_attr;
    //extras
    @Index(name = "feature_ind1_index")
    @Column(columnDefinition="TEXT")
    private String index1;
    @Index(name = "feature_ind2_index")
    @Column(columnDefinition="TEXT")
    private String index2;
    @Index(name = "feature_ind3_index")
    @Column(columnDefinition="TEXT")
    private String index3;
    @Index(name = "feature_ind4_index")
    @Column(columnDefinition="TEXT")
    private String index4;
    @Index(name = "feature_ind5_index")
    @Column(columnDefinition="TEXT")
    private String index5;

    public Feature toPojo() {
        Feature pojo = new Feature();
        pojo.guid = id + "";
        pojo.fid=fid;
        pojo.ft = featuretype;
        pojo.date = date_update != null ? date_update.getTime(): null;
        pojo.title_attr = title_attr != null ? title_attr: "index1";
        if(geo != null){
            Coordinate[] arrC = geo.getCoordinates();
            ArrayList<org.kloudgis.pojo.Coordinate> arrCPojo = new ArrayList(arrC.length);
            for(Coordinate c : arrC){
                arrCPojo.add(new org.kloudgis.pojo.Coordinate(c.x, c.y));
            }
            pojo.coords = arrCPojo;
            pojo.geo_type = geo.getGeometryType();
        }
        LinkedHashMap<String, String> mapAt = new LinkedHashMap<String, String>();
        mapAt.put("index1", index1);
        mapAt.put("index2", index2);
        mapAt.put("index3", index3);
        mapAt.put("index4", index4);
        mapAt.put("index5", index5);
        pojo.attrs = mapAt;
        return pojo;        
    }
    
}
