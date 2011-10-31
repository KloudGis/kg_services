/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.kloudgis.data.GeometryFactory;
import org.kloudgis.data.pojo.Feature;
import org.kloudgis.data.pojo.LoadFeature;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "features")
@Indexed
public class FeatureDbEntity implements Serializable {

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
    @Index(name = "feature_date_in_index")
    private Timestamp date_insert;
    @Column
    @Index(name = "feature_date_up_index")
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
    @Index(name = "angle_label")
    private Double angle_label;
    @Column
    private String title_attr;
    //extras
    @Index(name = "feature_ind1_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index1;
    @Index(name = "feature_ind2_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index2;
    @Index(name = "feature_ind3_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index3;
    @Index(name = "feature_ind4_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index4;
    @Index(name = "feature_ind5_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index5;
    @Index(name = "feature_ind6_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index6;
    @Index(name = "feature_ind7_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index7;
    @Index(name = "feature_ind8_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index8;
    @Index(name = "feature_ind9_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index9;
    @Index(name = "feature_ind10_index")
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String index10;

    public void setDateInsert(Timestamp time) {
        this.date_insert = time;
    }

    public Feature toPojo(Map<String, FeatureTypeDbEntity> mapFt) {
        Feature pojo = new Feature();
        pojo.guid = id + "";
        pojo.fid = fid;
        FeatureTypeDbEntity ft = null;
        if (featuretype != null) {
            ft = mapFt.get(featuretype);
            if (ft != null) {
                pojo.ft = ft.getLabel();
            } else {
                pojo.ft = featuretype;
            }
        }
        pojo.date = date_update != null ? date_update.getTime() : null;
        if (geo != null) {
            Coordinate[] arrC = geo.getCoordinates();
            ArrayList<org.kloudgis.core.pojo.Coordinate> arrCPojo = new ArrayList(arrC.length);
            for (Coordinate c : arrC) {
                arrCPojo.add(new org.kloudgis.core.pojo.Coordinate(c.x, c.y));
            }
            pojo.coords = arrCPojo;
            pojo.geo_type = geo.getGeometryType();
            if (arrC.length > 1) {
                Point ptC = geo.getCentroid();
                if (ptC != null) {
                    pojo.centroid = new org.kloudgis.core.pojo.Coordinate(ptC.getX(), ptC.getY());
                }

            }
        }
        LinkedHashMap<String, String> mapAt = new LinkedHashMap<String, String>();
        String ind1Lbl = findLabel("index1", ft);
        mapAt.put(ind1Lbl, index1);
        mapAt.put(findLabel("index2", ft), index2);
        mapAt.put(findLabel("index3", ft), index3);
        mapAt.put(findLabel("index4", ft), index4);
        mapAt.put(findLabel("index5", ft), index5);
        mapAt.put(findLabel("index6", ft), index6);
        mapAt.put(findLabel("index7", ft), index7);
        mapAt.put(findLabel("index8", ft), index8);
        mapAt.put(findLabel("index9", ft), index9);
        mapAt.put(findLabel("index10", ft), index10);
        pojo.title_attr = title_attr != null ? title_attr : ind1Lbl;
        pojo.attrs = mapAt;
        return pojo;
    }

    public void fromPojo(LoadFeature pojo) {
        this.fid = pojo.fid;
        this.featuretype = pojo.ft;
        this.angle = pojo.angle;
        this.angle_label = pojo.angle_label;
        this.geo_type = pojo.geo_type;
        if (this.geo_type != null) {
            try {
                this.geo = GeometryFactory.readWKT(pojo.wkt);
            } catch (ParseException ex) {
            }
        }
        this.index1 = pojo.attrs.get("index1");
        this.index2 = pojo.attrs.get("index2");
        this.index3 = pojo.attrs.get("index3");
        this.index4 = pojo.attrs.get("index4");
        this.index5 = pojo.attrs.get("index5");
        this.index6 = pojo.attrs.get("index6");
        this.index7 = pojo.attrs.get("index7");
        this.index8 = pojo.attrs.get("index8");
        this.index9 = pojo.attrs.get("index9");
        this.index10 = pojo.attrs.get("index10");
    }

    private String findLabel(String attr, FeatureTypeDbEntity ft) {
        if (ft != null) {
            List<AttrTypeDbEntity> lstAt = ft.getAttrs();
            if (lstAt != null) {
                for (AttrTypeDbEntity at : lstAt) {
                    if (at.getName().equals(attr)) {
                        return at.getLabel();
                    }
                }
            }
        }
        return attr;
    }

    public String getFeatureType() {
        return featuretype;
    }
}
