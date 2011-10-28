/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.sandbox.store;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "user_sandbox")
public class UserSandboxDbEntity implements Serializable {
    
    @SequenceGenerator(name = "user_sandbox_seq_gen", sequenceName = "user_sandbox_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "user_sandbox_seq_gen")
    private Long id;
    @Index(name = "user_sandbox_user_id_index")
    @Column(length = 100)
    private Long user_id;
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="sandbox_id") 
    private SandboxDbEntity sandbox;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the user_id
     */
    public Long getUserId() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the sandbox
     */
    public SandboxDbEntity getSandbox() {
        return sandbox;
    }

    /**
     * @param sandbox the sandbox to set
     */
    public void setSandbox(SandboxDbEntity sandbox) {
        this.sandbox = sandbox;
    }
    
    

}
