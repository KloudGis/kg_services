/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.kloudgis.data.pojo.NoteComment;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "note_comments")
public class NoteCommentDbEntity extends AbstractCommentDbEntity {
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
