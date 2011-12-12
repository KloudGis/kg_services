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
import org.kloudgis.core.pojo.Coordinate;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.data.pojo.Bookmark;


/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "bookmarks")
public class BookmarkDbEntity implements Serializable {

    @SequenceGenerator(name = "bookmark_seq_gen", sequenceName = "bookmark_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "bookmark_seq_gen")
    private Long id;
    @Column
    @Index(name = "bookmark_date_in_index")
    private Timestamp date_create;
    @Column
    @Index(name = "bookmark_user_in_index")
    private Long user_create;
    @Column
    private String user_descriptor;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry center; 
    @Column
    private Integer zoom; 
    
    
    public Bookmark toPojo(){
        Bookmark pojo = new Bookmark();
        pojo.guid = id;
        pojo.date_create = date_create == null ? null : date_create.getTime();
        pojo.user_create = user_create;
        pojo.user_descriptor = user_descriptor;
        pojo.zoom = zoom;
        pojo.center = center == null ? null : new Coordinate(center.getCoordinate().x, center.getCoordinate().y);
        return pojo;
    }
    
    public void fromPojo(Bookmark pojo){
        this.zoom = pojo.zoom;
        this.center = pojo.center == null ? null : GeometryFactory.createPoint(new com.vividsolutions.jts.geom.Coordinate(pojo.center.x, pojo.center.y));
    }

    public void setDateCreate(Timestamp timestamp) {
        this.date_create = timestamp;
    }

    public void setUserCreate(Long userId) {
        this.user_create = userId;
    }

    public void setUserDescriptor(String userDescriptor) {
        this.user_descriptor = userDescriptor;
    }

}
