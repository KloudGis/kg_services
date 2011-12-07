/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;


import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.search.annotations.Field;
import org.kloudgis.data.pojo.AbstractComment;
import org.kloudgis.data.pojo.NoteComment;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractCommentDbEntity implements Serializable {
    
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    private Long id;    
    @Column
    private Long author;
    @Column
    private Timestamp   date_create;
    @Column
    private String author_descriptor;   
    @Column
    @Field(index=org.hibernate.search.annotations.Index.TOKENIZED)
    private String comment; 
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public AbstractComment toPojo(AbstractComment pojo){
        pojo.guid = id;
        pojo.author = author;
        pojo.author_descriptor = author_descriptor;
        pojo.date_create = date_create != null ? date_create.getTime(): null;
        pojo.comment = comment;
        return pojo;
    }

    public Long getAuthor() {
        return author;
    }

    public void fromPojo(NoteComment in_comment) {
        this.comment = in_comment.comment;
    }

    public void setAuthor(Long userId) {
        this.author = userId;
    }

    public void setAuthorDescriptor(String userDescriptor) {
        this.author_descriptor = userDescriptor;
    }

    public void setDate(Timestamp timestamp) {
        this.date_create = timestamp;
    }

}
