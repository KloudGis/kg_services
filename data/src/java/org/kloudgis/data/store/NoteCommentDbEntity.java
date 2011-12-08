/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.kloudgis.data.pojo.NoteComment;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "note_comments")
public class NoteCommentDbEntity extends AbstractCommentDbEntity {
    
    @SequenceGenerator(name = "note_comments_seq_gen", sequenceName = "note_comments_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="note_comments_seq_gen")
    @Id
    private Long id; 
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne
    @ContainedIn
    private NoteDbEntity note;
    
    public NoteComment toPojo(){
        NoteComment pojo = new NoteComment();
        
        pojo.note = note != null ? note.getId() : null;
        return pojo;
    }


    public void setNote(NoteDbEntity note) {
        this.note = note;
    }
    
    public NoteDbEntity getNote(){
        return this.note;
    }

}
