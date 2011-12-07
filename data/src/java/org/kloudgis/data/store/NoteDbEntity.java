/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.core.pojo.Coordinate;
import org.kloudgis.data.pojo.Note;


/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "notes")
@Indexed
public class NoteDbEntity implements Serializable {

    @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    private Long id;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String title;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String description;
    @Column
    private Long author;
    @Column
    private Long user_update;
    @Column
    @Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String author_descriptor;
    @Column
    private Timestamp date_create;
    @Column
    private Timestamp date_update;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    @IndexedEmbedded
    private List<NoteCommentDbEntity> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Note toPojo() {
        Note pojo = new Note();
        pojo.guid = id;
        pojo.coordinate = geo == null ? null : new Coordinate(geo.getCentroid().getX(), geo.getCentroid().getY());
        pojo.title = title;
        pojo.description = description;
        pojo.author = author;
        pojo.author_descriptor = author_descriptor;
        pojo.date_create = date_create == null ? null : date_create.getTime();
        pojo.date_update = date_update == null ? null : date_update.getTime();
        pojo.user_update = user_update;
        if (this.comments != null) {
            List<Long> lstComments = new ArrayList();
            for (NoteCommentDbEntity comment : comments) {
                lstComments.add(comment.getId());
            }
            pojo.comments = lstComments;
        }
        return pojo;

    }

    public void fromPojo(Note pojo) {
        this.title = pojo.title;
        this.description = pojo.description;
        this.geo = GeometryFactory.createPoint(new com.vividsolutions.jts.geom.Coordinate(pojo.coordinate.x, pojo.coordinate.y));
    }

    public Point getGeometry() {
        return (Point) geo;
    }

    public void setAuthor(Long userId) {
        this.author = userId;
    }

    public void setAuthorDescriptor(String desc) {
        this.author_descriptor = desc;
    }

    public void setDate(Timestamp time) {
        this.date_create = time;
    }

    public String getTitle() {
        return title;
    }

    public Long getAuthor() {
        return author;
    }
  
}
