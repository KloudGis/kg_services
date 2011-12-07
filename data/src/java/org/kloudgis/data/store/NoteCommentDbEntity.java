/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.kloudgis.core.pojo.space.Transaction;
import org.kloudgis.core.pojo.space.TransactionAttribute;
import org.kloudgis.data.pojo.NoteComment;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "note_comments")
public class NoteCommentDbEntity implements Serializable {
    public final static long FT_ID = -11L;
    
    @SequenceGenerator(name = "note_comment_seq_gen", sequenceName = "note_comment_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "note_comment_seq_gen")
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
        pojo.date_create = date_create != null ? date_create.getTime(): null;
        pojo.comment = comment;
        pojo.note = note != null ? note.getId() : null;
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

    public void setNote(NoteDbEntity note) {
        this.note = note;
    }

    public Transaction toTransaction(int iType) {
        Transaction trx = new Transaction();
        trx.trx_type = iType;
        trx.source = "WEB";
        trx.feature_id = id;
        trx.ft_id = FT_ID;
        trx.time = new Timestamp(Calendar.getInstance().getTimeInMillis());
        TransactionAttribute ta1 = new TransactionAttribute("id", null, id + "");
        TransactionAttribute ta2 = new TransactionAttribute("comment", null, comment);
        TransactionAttribute ta3 = new TransactionAttribute("author", null, author_descriptor);
        TransactionAttribute ta4 = new TransactionAttribute("date_create", null, date_create + "");
        ArrayList<TransactionAttribute> arrlTa = new ArrayList(Arrays.asList(new TransactionAttribute[]{ta1, ta2, ta3, ta4}));
        trx.details = arrlTa;
        return trx;
    }
}
