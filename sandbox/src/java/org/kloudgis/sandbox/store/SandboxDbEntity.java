/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.sandbox.store;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.sandbox.pojo.Sandbox;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "sandboxes")
public class SandboxDbEntity implements Serializable {

    @SequenceGenerator(name = "sandbox_seq_gen", sequenceName = "sandbox_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sandbox_seq_gen")
    private Long id;
    @Index(name = "sandbox_name_index")
    @Column(length = 100)
    private String name;
    @Index(name = "sandbox_key_index")
    @Column(length = 100)
    private String unique_key;
    @Index(name = "sandbox_owner_index")
    @Column
    private Long owner;
    @Column
    private String owner_descriptor;
    @Column
    private Timestamp date_create;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geo;
    @Column
    private Integer zoom;
    @OneToMany(mappedBy = "sandbox", cascade = CascadeType.ALL)
    List<UserSandboxDbEntity> users;

    public Sandbox toPojo() {
        Sandbox pojo = new Sandbox();
        pojo.guid = id;
        pojo.name = name;
        pojo.key = unique_key;
        pojo.owner = owner;
        pojo.ownerDescriptor = owner_descriptor;
        pojo.date_create = date_create != null ? date_create.getTime() : null;
        if (geo != null) {
            pojo.lon = geo.getCoordinate().x;
            pojo.lat = geo.getCoordinate().y;
        }
        pojo.zoom = zoom;
        return pojo;
    }

    public Long getOwnerId() {
        return owner;
    }

    public String getUniqueKey() {
        return unique_key;
    }

    public String getName() {
        return name;
    }

    public void setOwnerId(Long id) {
        this.owner = id;
    }
    
    public void setOwnerDesc(String descr) {
        this.owner_descriptor = descr;
    }
    public void setUniqueKey(String key) {
        this.unique_key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addUser(UserSandboxDbEntity us) {
        if(users == null){
            users = new ArrayList();
        }
        this.users.add(us);
    }

    public void setDateCreate(Timestamp timestamp) {
        this.date_create = timestamp;
    }

    public void setCentre(Double lon, Double lat) {
        if (lon != null && lat != null) {
            this.geo = GeometryFactory.createPoint(new Coordinate(lon, lat));
        }
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }
}
