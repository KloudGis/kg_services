/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.security;

import javax.persistence.EntityManager;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class UserRole {

    public Long guid;
    public String user_name;
    public String role_name;

    public UserRoleDbEntity toDbEntity(EntityManager em) {
        UserRoleDbEntity entity = new UserRoleDbEntity();
        entity.setId(guid);
        entity.setRoleName(role_name);
        entity.setUserName(user_name);
        return entity;
    }
}
