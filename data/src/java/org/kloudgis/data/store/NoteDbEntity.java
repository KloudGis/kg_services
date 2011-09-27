/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.kloudgis.GeometryFactory;
import org.kloudgis.pojo.Coordinate;
import org.kloudgis.data.pojo.Note;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "notes")
public class NoteDbEntity implements Serializable{
    @SequenceGenerator(name = "note_seq_gen", sequenceName = "note_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "note_seq_gen")
    private Long id;   
    @Column
    private String title;
    @Column
    private String description;    
    @Column
    private Long author;
    @Column
    private String author_descriptor;
    @Column
    private Timestamp   date_create;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Note toPojo() {
        Note pojo = new Note();
        pojo.guid = id;
        pojo.coordinate = geo == null? null : new Coordinate(geo.getCentroid().getX(), geo.getCentroid().getY());
        pojo.title = title;
        pojo.description = description;
        pojo.author = author;
        pojo.author_descriptor = author_descriptor;
        pojo.date = date_create == null ? null : date_create.getTime();
        return pojo;
        
    }
    
    public void fromPojo(Note pojo) {
        this.title = pojo.title;
        this.description = pojo.description;
        this.geo = GeometryFactory.createPoint(new com.vividsolutions.jts.geom.Coordinate(pojo.coordinate.x, pojo.coordinate.y));
        this.geo.setSRID(4326);
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
    
    public void setDate(Timestamp time){
        this.date_create = time;
    }

    public String getTitle() {
        return title;
    }

    
}
