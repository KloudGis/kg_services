/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.persistence;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.kloudgis.core.persistence.feature.FeatureDbEntity;
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.feature.pojo.Arpenteur;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name="arpenteur")
@Indexed
public class ArpenteurDbEntity extends FeatureDbEntity implements Serializable {

    @SequenceGenerator(name = "arp_seq_gen", sequenceName = "arpenteur_fid_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "arp_seq_gen")
    @DocumentId
    private Long fid;
    @Column(length = 100)
    @Index(name = "arp_firstname_ix")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String firstname;
    @Column
    @Index(name = "arp_lastname_ix")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String lastname;
    @Column
    @Index(name = "arp_postalcode")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String postalcode;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;

    @Override
    public String getFeatureTypeName() {
        return "Arpenteur";
    }

    public Feature toPojo() {
        Arpenteur pojo = new Arpenteur();
        pojo.setFeatureId(getId());
        Geometry env = null;
        if (geo != null) {
            env = geo.getEnvelope();
        }
        pojo.boundswkt = env == null ? null : env.toText();
        pojo.firstname = firstname;
        pojo.lastname = lastname;
        pojo.postalcode = postalcode;
        return pojo;
    }

    public void fromPojo(Feature pojo) {
        if (pojo instanceof Arpenteur) {
            Arpenteur arp = (Arpenteur) pojo;
            this.fid = arp.guid;
            this.firstname = arp.firstname;
            this.lastname = arp.lastname;
            this.postalcode = arp.postalcode;
            this.setGeoFromWKT(arp.newgeowkt);
        }
    }

    @Override
    public Long getId() {
        return fid;
    }

    @Override
    public void setId(Long id) {
        this.fid = id;
    }

    @Override
    public Geometry getGeometry() {
        return geo;
    }

    public void setFirstName(String fname) {
        this.firstname = fname;
    }

    public void setLastName(String lname) {
        this.lastname = lname;
    }

    public void setPostalCode(String pc) {
        this.postalcode = pc;
    }

    public void setGeoFromWKT(String geowkt) {
        if (geowkt != null) {
            try {
                Geometry geoN = new WKTReader().read(geowkt);
                geoN.setSRID(4326);
                geo = geoN;
            } catch (ParseException ex) {
                //invalid
            }
        }
    }
}
