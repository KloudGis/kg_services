/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.data.pojo.Feature;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "features")
@Indexed
public class FeatureDbEntity implements Serializable {

    public static FeatureDbEntity findByGuid(String guid, HibernateEntityManager em) {
        //add it
        String[] split = guid.split(";");
        List<FeatureDbEntity> lst = em.getSession().createCriteria(FeatureDbEntity.class).
                add(Restrictions.eq("fid", Long.valueOf(split[0]))).
                add(Restrictions.eq("ft_id", Long.valueOf(split[1]))).list();
        if (lst.isEmpty()) {
            return null;
        }
        return lst.get(0);
    }

    public static String buildGuid(Long fid, Long ft_id) {
        return fid + ";" + ft_id;
    }
    //**************************************************************************
    //                              SYSTEM Columns
    //**************************************************************************
    //private id - Do not expose it.
    @SequenceGenerator(name = "system_id_gen", sequenceName = "feature_id_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "system_id_gen")
    @DocumentId
    private Long system_id;
    //feature id
    @Column(nullable = false)
    @Index(name = "fid_index")
    //featuretype id
    private Long fid;
    @Column(nullable = false)
    @Index(name = "ft_id_index")
    private Long ft_id;
    //system date: create time
    @Column
    @Index(name = "feature_date_in_index")
    private Timestamp date_create;
    @Column
    @Index(name = "feature_user_in_index")
    private Long user_create;
    //system date: last update time
    @Column
    @Index(name = "feature_date_up_index")
    private Timestamp date_update;
    @Column
    @Index(name = "feature_user_up_index")
    private Long user_update;
    //**********************************
    //  JOIN on itself
    //**********************************
    @ManyToMany
    @JoinTable(name = "joins",
    joinColumns = {
        @JoinColumn(name = "src_feature", referencedColumnName = "system_id")},
    inverseJoinColumns = {
        @JoinColumn(name = "dest_feature", referencedColumnName = "system_id")})
    private List<FeatureDbEntity> joins;
    //**********************************
    //  Reverse JOIN on itself
    //**********************************
    @ManyToMany
    @JoinTable(name = "joins",
    joinColumns = {
        @JoinColumn(name = "dest_feature", referencedColumnName = "system_id")},
    inverseJoinColumns = {
        @JoinColumn(name = "src_feature", referencedColumnName = "system_id")})
    private List<FeatureDbEntity> reverse_joins;
    //**************************************************************************
    //                              USER Columns
    //**************************************************************************
    //**********************************
    //  Geometry field
    //**********************************
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;
    //geo type (Point, Linestring, Polygon, ...)
    @Index(name = "feature_geotype_index")
    @Column(length = 50)
    private String geo_type;
    //**********************************
    //  25 text
    //**********************************
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text1;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text2;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text3;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text4;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text5;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text6;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text7;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text8;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text9;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text10;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text11;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text12;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text13;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text14;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text15;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text16;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text17;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text18;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text19;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text20;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text21;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text22;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text23;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text24;
    @Column(columnDefinition = "TEXT")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text25;
    //**********************************
    //  5 boolean
    //**********************************
    private Boolean bool1;
    private Boolean bool2;
    private Boolean bool3;
    private Boolean bool4;
    private Boolean bool5;
    //**********************************
    //  3 date
    //**********************************
    @Column
    private Timestamp date1;
    @Column
    private Timestamp date2;
    @Column
    private Timestamp date3;
    //**********************************
    //  10 numeric 
    //**********************************
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num1;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num2;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num3;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num4;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num5;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num6;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num7;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num8;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num9;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Long num10;
    //**********************************
    //  10 floating point
    //**********************************
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim1;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim2;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim3;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim4;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim5;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim6;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim7;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim8;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim9;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private Double decim10;
    //**********************************
    //  2 binary for pictures 
    //**********************************
    @Column
    private PictureEntity img1;
    @Column
    private PictureEntity img2;

    public void setDateInsert(Timestamp time) {
        this.date_create = time;
    }

    public void setId(long fid, long ft_id) {
        this.fid = fid;
        this.ft_id = ft_id;
    }

    public Long getFeatureTypeId() {
        return this.ft_id;
    }

    public void addJoin(FeatureDbEntity f2) {
        if (joins == null) {
            joins = new ArrayList();
        }
        this.joins.add(f2);
    }

    public List<FeatureDbEntity> getJoins() {
        return joins;
    }

    public List<FeatureDbEntity> getReserveJoins() {
        return reverse_joins;
    }

    public Feature toPojo() {
        Feature pojo = new Feature();
        pojo.guid = buildGuid(this.fid, this.ft_id);
        pojo.fid = this.fid;
        pojo.ft_id = this.ft_id;
        pojo.date_create = date_create != null ? date_create.getTime() : null;
        pojo.user_create = user_create;
        pojo.date_update = date_update != null ? date_update.getTime() : null;
        pojo.user_update = user_update;
        if (geo != null) {
            pojo.geo = new org.kloudgis.data.pojo.Geometry(geo);
        }
        //Joins
        List<FeatureDbEntity> lstJoins = this.getJoins();
        if (lstJoins != null && !lstJoins.isEmpty()) {
            List<String> lstGuid = new ArrayList(lstJoins.size());
            for (FeatureDbEntity fJoin : lstJoins) {
                lstGuid.add(buildGuid(fJoin.fid, fJoin.ft_id));
            }
            pojo.joins = lstGuid;
        }
        //reverse Joins
        lstJoins = this.getReserveJoins();
        if (lstJoins != null && !lstJoins.isEmpty()) {
            List<String> lstGuid = new ArrayList(lstJoins.size());
            for (FeatureDbEntity fJoin : lstJoins) {
                lstGuid.add(buildGuid(fJoin.fid, fJoin.ft_id));
            }
            pojo.reverse_joins = lstGuid;
        }
        //texts
        pojo.text1 = text1;
        pojo.text2 = text2;
        pojo.text3 = text3;
        pojo.text4 = text4;
        pojo.text5 = text5;
        pojo.text6 = text6;
        pojo.text7 = text7;
        pojo.text8 = text8;
        pojo.text9 = text1;
        pojo.text10 = text10;
        pojo.text11 = text11;
        pojo.text12 = text12;
        pojo.text13 = text13;
        pojo.text14 = text14;
        pojo.text15 = text15;
        pojo.text16 = text16;
        pojo.text17 = text17;
        pojo.text18 = text18;
        pojo.text19 = text19;
        pojo.text20 = text20;
        pojo.text21 = text21;
        pojo.text22 = text22;
        pojo.text23 = text23;
        pojo.text24 = text24;
        pojo.text25 = text25;
        //bools
        pojo.bool1 = bool1;
        pojo.bool2 = bool2;
        pojo.bool3 = bool3;
        pojo.bool4 = bool4;
        pojo.bool5 = bool5;
        //dates
        pojo.date1 = date1 == null ? null : date1.getTime();
        pojo.date2 = date2 == null ? null : date2.getTime();
        pojo.date3 = date3 == null ? null : date3.getTime();
        //numeric
        pojo.num1 = num1;
        pojo.num2 = num2;
        pojo.num3 = num3;
        pojo.num4 = num4;
        pojo.num5 = num5;
        pojo.num6 = num6;
        pojo.num7 = num7;
        pojo.num8 = num8;
        pojo.num9 = num9;
        pojo.num10 = num10;
        //decimals
        pojo.decim1 = decim1;
        pojo.decim2 = decim2;
        pojo.decim3 = decim3;
        pojo.decim4 = decim4;
        pojo.decim5 = decim5;
        pojo.decim6 = decim6;
        pojo.decim7 = decim7;
        pojo.decim8 = decim8;
        pojo.decim9 = decim9;
        pojo.decim10 = decim10;
        //images
        pojo.img1 = img1 == null ? null : img1.getAsBase64();
        pojo.img2 = img2 == null ? null : img2.getAsBase64();

        return pojo;
    }

    public void fromPojo(Feature pojo, HibernateEntityManager em) {
        this.setId(pojo.fid, pojo.ft_id);
        if (pojo.geo != null) {
            this.geo = pojo.geo.toJTS();
            this.geo_type = geo.getGeometryType();
        } else if (pojo.wkt != null) {
            try {
                this.geo = GeometryFactory.readWKT(pojo.wkt);
                this.geo_type = geo.getGeometryType();
            } catch (ParseException ex) {
                System.out.println("Couldn't read wkt:" + ex);
            }
        }
        //Joins
        List<String> lstNew = pojo.joins == null ? new ArrayList() : pojo.joins;
        List<FeatureDbEntity> lstJoinsActual = this.getJoins();
        Map<String, FeatureDbEntity> mapGuidActual = new HashMap();
        if (lstJoinsActual != null) {
            for (FeatureDbEntity fJoin : lstJoinsActual) {
                mapGuidActual.put(buildGuid(fJoin.fid, fJoin.ft_id), fJoin);
            }
        }
        //add new joins
        if(lstNew.size() > 0 && lstJoinsActual == null){
            this.joins = new ArrayList();
            lstJoinsActual = getJoins();
        }
        for (String guid : lstNew) {
            if (!mapGuidActual.containsKey(guid)) {
                try {
                    FeatureDbEntity f = FeatureDbEntity.findByGuid(guid, em);
                    lstJoinsActual.add(f);
                } catch (EntityNotFoundException e) {
                    System.out.println("couldnt add " + guid + " as join because it not found!");
                }
            }
        }
        //remove joins not relevant
        for (String guid : mapGuidActual.keySet()) {
            if (!lstNew.contains(guid)) {
                lstJoinsActual.remove(mapGuidActual.get(guid));
            }
        }

        //texts
        this.text1 = pojo.text1;
        this.text2 = pojo.text2;
        this.text3 = pojo.text3;
        this.text4 = pojo.text4;
        this.text5 = pojo.text5;
        this.text6 = pojo.text6;
        this.text7 = pojo.text7;
        this.text8 = pojo.text8;
        this.text9 = pojo.text1;
        this.text10 = pojo.text10;
        this.text11 = pojo.text11;
        this.text12 = pojo.text12;
        this.text13 = pojo.text13;
        this.text14 = pojo.text14;
        this.text15 = pojo.text15;
        this.text16 = pojo.text16;
        this.text17 = pojo.text17;
        this.text18 = pojo.text18;
        this.text19 = pojo.text19;
        this.text20 = pojo.text20;
        this.text21 = pojo.text21;
        this.text22 = pojo.text22;
        this.text23 = pojo.text23;
        this.text24 = pojo.text24;
        this.text25 = pojo.text25;
        //bools
        this.bool1 = pojo.bool1;
        this.bool2 = pojo.bool2;
        this.bool3 = pojo.bool3;
        this.bool4 = pojo.bool4;
        this.bool5 = pojo.bool5;
        //dates
        this.date1 = pojo.date1 == null ? null : new Timestamp(pojo.date1);
        this.date2 = pojo.date2 == null ? null : new Timestamp(pojo.date2);
        this.date3 = pojo.date3 == null ? null : new Timestamp(pojo.date3);
        //numeric
        this.num1 = pojo.num1;
        this.num2 = pojo.num2;
        this.num3 = pojo.num3;
        this.num4 = pojo.num4;
        this.num5 = pojo.num5;
        this.num6 = pojo.num6;
        this.num7 = pojo.num7;
        this.num8 = pojo.num8;
        this.num9 = pojo.num9;
        this.num10 = pojo.num10;
        //decimals
        this.decim1 = pojo.decim1;
        this.decim2 = pojo.decim2;
        this.decim3 = pojo.decim3;
        this.decim4 = pojo.decim4;
        this.decim5 = pojo.decim5;
        this.decim6 = pojo.decim6;
        this.decim7 = pojo.decim7;
        this.decim8 = pojo.decim8;
        this.decim9 = pojo.decim9;
        this.decim10 = pojo.decim10;
        //images
        this.img1 = pojo.img1 == null ? null : new PictureEntity(pojo.img1);
        this.img2 = pojo.img2 == null ? null : new PictureEntity(pojo.img2);
    }
}
