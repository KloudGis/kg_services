/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.auth.admin.store;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "groups")
public class GroupDbEntity implements Serializable {
    
    @SequenceGenerator(name = "group_seq_gen", sequenceName = "group_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "group_seq_gen")
    private Long id;
    
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name="groups_users",
            joinColumns=@JoinColumn(name="group_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
        )
    private Set<UserDbEntity> users;
}
