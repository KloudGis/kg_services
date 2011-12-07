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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.kloudgis.core.utils.GeometryFactory;
import org.kloudgis.core.pojo.Coordinate;
import org.kloudgis.core.pojo.space.Transaction;
import org.kloudgis.core.pojo.space.TransactionAttribute;
import org.kloudgis.data.pojo.Note;


/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "notes")
@Indexed
public class NoteDbEntity implements Serializable {
    public final static long FT_ID = -10L;

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

    public void fromTransaction(Transaction trx) {
        for (TransactionAttribute ta : trx.details) {
            if (ta.attribute.equals("id")) {
                this.id = trx.feature_id;
            } else if (ta.attribute.equals("title")) {
                this.title = ta.modified_value;
            } else if (ta.attribute.equals("descr")) {
                this.description = ta.modified_value;
            } else if (ta.attribute.equals("author")) {
                this.author_descriptor = ta.modified_value;
                this.author = trx.user_id;
            } else if (ta.attribute.equals("date_create")) {
                try {
                    this.date_create = Timestamp.valueOf(ta.modified_value);
                } catch (Exception e) {
                    this.date_create = null;
                }
            } else if (ta.attribute.equals("lon_lat")) {
                try {
                    this.geo = GeometryFactory.readWKT(ta.modified_value);
                } catch (Exception ex) {
                    this.geo = null;
                }
            }
        }
    }

    public Transaction toTransaction(int iType) {
        Transaction trx = new Transaction();
        trx.trx_type = iType;
        trx.source = "WEB";
        trx.feature_id = id;
        trx.ft_id = FT_ID;
        trx.time = new Timestamp(Calendar.getInstance().getTimeInMillis());
        TransactionAttribute ta1 = new TransactionAttribute("id", null, id + "");
        TransactionAttribute ta2 = new TransactionAttribute("title", null, title);
        TransactionAttribute ta3 = new TransactionAttribute("descr", null, description);
        TransactionAttribute ta4 = new TransactionAttribute("author", null, author_descriptor);
        TransactionAttribute ta5 = new TransactionAttribute("date_create", null, date_create + "");
        TransactionAttribute ta6 = new TransactionAttribute("lon_lat", null, geo != null ? geo.toText() : null);
        ArrayList<TransactionAttribute> arrlTa = new ArrayList(Arrays.asList(new TransactionAttribute[]{ta1, ta2, ta3, ta4, ta5, ta6}));
        trx.details = arrlTa;
        return trx;
    }
}
