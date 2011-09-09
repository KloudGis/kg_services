/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.security;

import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.persistence.security.PrivilegeDbEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jeanfelixg
 */
@XmlRootElement
public class Privilege {

    private Long id;
    private String name;
    private List<String> groups;

    public Privilege() {
    }

    @XmlElement(name = "guid")
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
     * @return the name
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    @XmlElement
    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> lstG) {
        groups = lstG;
    }

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof Privilege)) {
            return false;
        }
        Privilege other = (Privilege) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public PrivilegeDbEntity toDbEntity(EntityManager em) {
        PrivilegeDbEntity entity = new PrivilegeDbEntity();
        entity.setId(getId());
        entity.setName(getName());
        if (getGroups() != null && getGroups().size() > 0) {
            //add new
            for (String sId : getGroups()) {
                Long lgId = null;
                try {
                    lgId = Long.parseLong(sId);
                } catch (NumberFormatException e) {
                }
                if (lgId != null) {
                    GroupDbEntity gNew = em.find(GroupDbEntity.class, lgId);
                    if (gNew != null) {
                        entity.addGroup(gNew);
                    }
                }
            }
        }
        return entity;
    }
}
