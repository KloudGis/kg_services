/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.kloudgis.data.pojo.Member;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "members")
public class MemberDbEntity implements Serializable {
    
    @SequenceGenerator(name = "members_seq_gen", sequenceName = "members_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "members_seq_gen")
    private Long id;
    @Index (name="user_index")
    @Column
    private Long user_id;
    @Index (name="user_descr_index")
    @Column
    private String user_descriptor;
    @Index (name="access_index")
    @Column
    private String access_type;
    
    public Long getId(){
        return id;
    }
    
    public Long getUserId(){
        return user_id;
    }

    public String getUserDescriptor(){
        return user_descriptor;
    }

    public Member toPojo() {
        Member pojo = new Member();
        pojo.guid = id;
        pojo.user_id = user_id;
        pojo.user_descriptor = user_descriptor;
        pojo.access_type = access_type;
        return pojo;
    }

    public void setUserId(Long id) {
        this.user_id = id;
    }

    public void setDescriptor(String user_description) {
        this.user_descriptor = user_description;
    }

    public void setMembership(String membership) {
        this.access_type = membership;
    }
}
