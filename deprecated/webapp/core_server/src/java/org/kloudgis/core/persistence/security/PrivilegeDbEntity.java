/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.persistence.security;

import org.kloudgis.core.pojo.security.Privilege;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "GROUP_PRIVILEGES")
@NamedQueries({
@NamedQuery(name = "Privilege.findAll", query = "SELECT c FROM PrivilegeDbEntity c")})
public class PrivilegeDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column (length=50)
    private String name;
    @ManyToMany (mappedBy="privileges")
    private List<GroupDbEntity> groups;

    public PrivilegeDbEntity(){
        groups = new ArrayList();
    }

    public List<GroupDbEntity> getGroups() {
        return groups;
    }

    public Long getId(){
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


     @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof PrivilegeDbEntity)) {
            return false;
        }
        PrivilegeDbEntity other = (PrivilegeDbEntity) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public Privilege toPojo(){
        Privilege entity = new Privilege();
        entity.setId(getId());
        entity.setName(getName());
        List<GroupDbEntity> lstG = getGroups();
        List<String> lstGId = new ArrayList();
        for (GroupDbEntity gDb : lstG) {
            lstGId.add(gDb.getId() + "");
        }
        entity.setGroups(lstGId);
        return entity;
    }

    public void addGroup(GroupDbEntity grp) {
        if (!groups.contains(grp)) {
            groups.add(grp);
        }
        if (!grp.getPrivileges().contains(this)) {
            grp.getPrivileges().add(this);
        }
    }

    public void removeGroup(GroupDbEntity group) {
        groups.remove(group);
    }

    public void updateFrom(Privilege priv, EntityManager em) {
        List<String> lstGid= priv.getGroups();
        List<GroupDbEntity> lstG = new ArrayList(getGroups());
        //remove deleted
        for (GroupDbEntity gDb : lstG) {
            String sId = gDb.getId() + "";
            if (lstGid == null || !lstGid.contains(sId)) {
                removeGroup(gDb);
            }
        }
        if (lstGid != null) {
            //add new
            ArrayList<String> lstMod = new ArrayList();
            for (String sId : lstGid) {
                if (sId == null || sId.length() == 0) {
                    continue;
                } else {
                    lstMod.add(sId);
                    boolean bFound = false;
                    for (GroupDbEntity gDb : lstG) {
                        if ((gDb.getId() + "").equals(sId)) {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound) {
                        Long lgId = null;
                        try {
                            lgId = Long.parseLong(sId);
                        } catch (NumberFormatException e) {
                        }
                        if (lgId != null) {
                            GroupDbEntity gNew = em.find(GroupDbEntity.class, lgId);
                            if (gNew != null) {
                                addGroup(gNew);
                            }
                        }
                    }
                }
            }
            priv.setGroups(lstMod);
        }
    }



}
