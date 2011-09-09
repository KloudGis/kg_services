/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.persistence.security;

import org.kloudgis.core.pojo.security.Group;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "GROUPS")
@NamedQueries({
    @NamedQuery(name = "Group.findAll", query = "SELECT c FROM GroupDbEntity c")})
public class GroupDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 30)
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CONNECTS_GROUP_PRIVILEGES",
    joinColumns =
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID"),
    inverseJoinColumns =
    @JoinColumn(name = "PRIVILEGES_ID", referencedColumnName = "ID"))
    @OrderBy("name ASC")
    private List<PrivilegeDbEntity> privileges;
    @OneToMany(mappedBy = "group")
    @OrderBy("user_name ASC")
    private List<UserDbEntity> members;
    @OneToOne(optional = true)
    private GroupDbEntity parent_group;

    public GroupDbEntity() {
        members = new ArrayList();
        privileges = new ArrayList();
    }

    public GroupDbEntity(Long id) {
        this();
        this.id = id;
    }

    public void addMember(UserDbEntity user) {
        if (!members.contains(user)) {
            if (user.getGroup() != null) {
                user.getGroup().getMembers().remove(user);
            }
            user.setGroup(this);
            members.add(user);
        }
    }

    public void removeMember(UserDbEntity user) {
        members.remove(user);
        user.setGroup(null);
    }

    public void addPrivilege(PrivilegeDbEntity priv) {
        if (!privileges.contains(priv)) {
            privileges.add(priv);
        }
        if (!priv.getGroups().contains(this)) {
            priv.getGroups().add(this);
        }
    }

    public void removePrivilege(PrivilegeDbEntity privilege) {
        privileges.remove(privilege);
        privilege.removeGroup(this);
    }

    public Long getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the Privileges
     */
    public List<PrivilegeDbEntity> getPrivileges() {
        return privileges;
    }

    /**
     * @return the Members
     */
    public List<UserDbEntity> getMembers() {
        return members;
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

    /**
     * @param lstPr the privileges to set
     */
    public void setPrivileges(List<PrivilegeDbEntity> lstPr) {
        this.privileges = lstPr;
    }

    /**
     * @param lstMem the privileges to set
     */
    public void setMembers(List<UserDbEntity> lstMem) {
        this.members = lstMem;
    }

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof GroupDbEntity)) {
            return false;
        }
        GroupDbEntity other = (GroupDbEntity) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public Group toPojo() {
        Group pojo = new Group();
        pojo.guid = getId();
        pojo.name = getName();
        List<UserDbEntity> lstM = getMembers();
        List<String> lstMId = new ArrayList();
        for (UserDbEntity uDb : lstM) {
            lstMId.add(uDb.getId() + "");
        }
        pojo.members = lstMId;
        List<PrivilegeDbEntity> lstP = getPrivileges();
        List<String> lstPId = new ArrayList();
        for (PrivilegeDbEntity pDb : lstP) {
            lstPId.add(pDb.getId() + "");
        }
        pojo.privileges = lstPId;
        pojo.parent_group = parent_group == null ? null : parent_group.getId();
        return pojo;
    }

    public void updateFrom(Group group, EntityManager em) {
        this.setName(group.name);
        if(group.parent_group != null){
            GroupDbEntity gParent = em.find(GroupDbEntity.class, group.parent_group);
            this.setParentGroup(gParent);
        }else{
            setParentGroup(null);
        }
        List<String> lstMId = group.members;
        List<UserDbEntity> lstM = new ArrayList(getMembers());
        //remove deleted
        for (UserDbEntity uDb : lstM) {
            String sId = uDb.getId() + "";
            if (lstMId == null || !lstMId.contains(sId)) {
                removeMember(uDb);
            }
        }
        if (lstMId != null) {
            //add new
            ArrayList<String> lstMod = new ArrayList();
            for (String sId : lstMId) {
                if (sId == null || sId.length() == 0) {
                    continue;
                } else {
                    lstMod.add(sId);
                    boolean bFound = false;
                    for (UserDbEntity uDb : lstM) {
                        if ((uDb.getId() + "").equals(sId)) {
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
                            UserDbEntity uNew = em.find(UserDbEntity.class, lgId);
                            if (uNew != null) {
                                addMember(uNew);
                            }
                        }
                    }
                }
            }
            group.members = lstMod;
        }
        List<String> lstPid= group.privileges;
        List<PrivilegeDbEntity> lstP = new ArrayList(getPrivileges());
        //remove deleted
        for (PrivilegeDbEntity pDb : lstP) {
            String sId = pDb.getId() + "";
            if (lstPid == null || !lstPid.contains(sId)) {
                removePrivilege(pDb);
            }
        }
        if (lstPid != null) {
            //add new
            ArrayList<String> lstMod = new ArrayList();
            for (String sId : lstPid) {
                if (sId == null || sId.length() == 0) {
                    continue;
                } else {
                    lstMod.add(sId);
                    boolean bFound = false;
                    for (PrivilegeDbEntity pDb : lstP) {
                        if ((pDb.getId() + "").equals(sId)) {
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
                            PrivilegeDbEntity pNew = em.find(PrivilegeDbEntity.class, lgId);
                            if (pNew != null) {
                                addPrivilege(pNew);
                            }
                        }
                    }
                }
            }
            group.privileges = lstMod;
        }
    }

    public GroupDbEntity getParentGroup() {
        return parent_group;
    }

    public void setParentGroup(GroupDbEntity parent){
        parent_group = parent;
    }

    public List<GroupDbEntity> getGroupInheritance(){
        List<GroupDbEntity> grpList = new ArrayList();
        grpList.add(this);
        GroupDbEntity grp = this;
        while(grp.getParentGroup() != null){
            grp = grp.getParentGroup();
            if(!grpList.contains(grp)){
                grpList.add(grp);
            }else{
                break;
            }
        }
        return grpList;
    }
}
