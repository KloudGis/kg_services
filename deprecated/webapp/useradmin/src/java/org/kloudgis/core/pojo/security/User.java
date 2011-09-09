/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.security;

import java.sql.Timestamp;
import org.kloudgis.core.persistence.security.UserDbEntity;
import javax.persistence.EntityManager;

/**
 *
 * @author jeanfelixg
 */
public class User {

    public Long guid;
    public String fullName;
    public String password;
    public String email;
    public String location;
    public String compagny;
    public Boolean isSuperUser = Boolean.FALSE;
    public Timestamp userCreated;
    public String accountType;
    public Timestamp accountExpire;
    public Boolean isActive;

    public User() {
    }

    public User(Long id) {
        this.guid = id;
    }

    public UserDbEntity toDbEntity(EntityManager em) {
        UserDbEntity entity = new UserDbEntity();
        entity.setId(guid);
        entity.updateFrom(this, em);
        return entity;
    }
}
