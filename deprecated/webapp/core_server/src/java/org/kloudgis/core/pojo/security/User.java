/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.security;

import java.util.List;
import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.persistence.security.UserDbEntity;
import javax.persistence.EntityManager;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class User {

    public Long guid;
    public String name;
    public String fullName;
    public String password;
    public String email;
    public String moreInfo;
    public String expireDate;
    public String group;
    public List<String> roles;

    public User() {
    }

    public User(Long id) {
        this.guid = id;
    }

    public UserDbEntity toDbEntity(EntityManager em) {
        UserDbEntity entity = new UserDbEntity();
        entity.setId(guid);
        entity.setName(name);
        entity.setFullName(fullName);
        entity.setEmail(email);
        entity.setMoreInfo(moreInfo);
        entity.setPassword(password);
        entity.setExpireDate(expireDate);
        if (roles != null) {
            //add new
            for (String sId : roles) {
                Long lgId = null;
                try {
                    lgId = Long.parseLong(sId);
                } catch (NumberFormatException e) {
                }
                if (lgId != null) {
                    UserRoleDbEntity rNew = em.find(UserRoleDbEntity.class, lgId);
                    if (rNew != null) {
                        entity.addRole(rNew);
                    }
                }
            }
        }
        if (group != null && group.length() > 0) {
            entity.setGroup(em.find(GroupDbEntity.class, Long.parseLong(group)));
        }
        return entity;
    }
}
