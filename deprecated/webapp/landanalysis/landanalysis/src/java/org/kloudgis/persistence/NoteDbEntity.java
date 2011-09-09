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
import org.kloudgis.feature.pojo.Note;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "note")
@Indexed
public class NoteDbEntity extends FeatureDbEntity implements Serializable {

    @SequenceGenerator(name = "note_seq_gen", sequenceName = "note_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "note_seq_gen")
    @DocumentId
    private Long id;
    @Column(length = 100)
    @Index(name = "note_title_ix")
    @Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    private String title;
    @Column
    @Index(name = "note_description_ix")
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String description;
    @Column
    @Index(name = "note_geom_type_ix")
    private String geom_type;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geom;

    @Override
    public String getFeatureTypeName() {
        return "Note";
    }

    public Feature toPojo() {
        Note pojo = new Note();
        pojo.setFeatureId(getId());
        Geometry env = null;
        if (geom != null) {
            env = geom.getEnvelope();
        }       
        pojo.boundswkt = env == null ? null : env.toText();
        pojo.title = title;
        pojo.description = description;
        return pojo;
    }

    public void fromPojo(Feature pojo) {
        if (pojo instanceof Note) {
            Note note = (Note) pojo;
            this.id = note.guid;
            this.title = note.title;
            this.description = note.description;
            this.setGeoFromWKT(note.newgeowkt);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Geometry getGeometry() {
        return geom;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGeoFromWKT(String geowkt) {
        if (geowkt != null) {
            try {
                Geometry geo = new WKTReader().read(geowkt);
                geo.setSRID(4326);
                geom = geo;
                geom_type = geom.getGeometryType();
            } catch (ParseException ex) {
                //invalid
            }
        }
    }
}
