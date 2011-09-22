/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table (name="feature")
public class FeatureDbEntity implements Serializable{
    
    @SequenceGenerator(name = "feature_seq_gen", sequenceName = "feature_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "feature_seq_gen")
    private Long id;
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
    
}
