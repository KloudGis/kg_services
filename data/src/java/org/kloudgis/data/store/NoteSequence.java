/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "note_sequence")
public class NoteSequence implements Serializable {
    
    @SequenceGenerator(name = "note_seq_gen", sequenceName = "note_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "note_seq_gen")
    private Long id;
    
    public static Long next(EntityManager em){
        NoteSequence ns = new NoteSequence();
        em.persist(ns);
        return ns.id;
    }
    
}
