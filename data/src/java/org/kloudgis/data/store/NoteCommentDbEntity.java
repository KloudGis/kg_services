/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.kloudgis.data.pojo.Note;
import org.kloudgis.data.pojo.NoteComment;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "note_comments")
public class NoteCommentDbEntity implements Serializable {
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
    private String content; 
    @ManyToOne
    @ContainedIn
    private NoteDbEntity note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public NoteComment toPojo(){
        NoteComment pojo = new NoteComment();
        pojo.guid = id;
        pojo.author = author;
        pojo.author_descriptor = author_descriptor;
        pojo.date = date_create != null ? date_create.getTime(): null;
        pojo.value = content;
        return pojo;
    }

    public Long getAuthor() {
        return author;
    }

    public void fromPojo(NoteComment in_comment) {
        this.content = in_comment.value;
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
