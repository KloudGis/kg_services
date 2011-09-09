/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.security;

import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.persistence.security.PrivilegeDbEntity;
import org.kloudgis.core.persistence.security.UserDbEntity;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author jeanfelixg
 */
public class Group {

    public Long guid;
    public Long parent_group;
    public String name;
    public List<String> members;
    public List<String> privileges;

    public Group() {
    }

    public Group(Long id) {
        this();
        this.guid = id;
    }

    public GroupDbEntity toDbEntity(EntityManager em) {
        GroupDbEntity entity = new GroupDbEntity();
        entity.setId(guid);
        if(parent_group != null){
            GroupDbEntity gParent = em.find(GroupDbEntity.class, parent_group);
            entity.setParentGroup(gParent);
        }    
        entity.setName(name);
        if (members != null) {
            //add new
            for (String sId : members) {
                Long lgId = null;
                try {
                    lgId = Long.parseLong(sId);
                } catch (NumberFormatException e) {
                }
                if (lgId != null) {
                    UserDbEntity uNew = em.find(UserDbEntity.class, lgId);
                    if (uNew != null) {
                        entity.addMember(uNew);
                    }
                }
            }
        }
        if (privileges != null) {
            //add new
            for (String sId : privileges) {
                Long lgId = null;
                try {
                    lgId = Long.parseLong(sId);
                } catch (NumberFormatException e) {
                }
                if (lgId != null) {
                    PrivilegeDbEntity pNew = em.find(PrivilegeDbEntity.class, lgId);
                    if (pNew != null) {
                        entity.addPrivilege(pNew);
                    }
                }
            }
        }
        return entity;
    }
}
