/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.store;

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
    private SandboxDbEntity sandboxes;

}
