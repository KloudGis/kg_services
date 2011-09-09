/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.persistence.security;

import org.kloudgis.core.pojo.security.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT c FROM UserDbEntity c order by c.user_name")})
public class UserDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 50)
    private String user_name;
    @Column(length = 150)
    private String fullName;
    @Column(length = 50)
    private String password;
    @Column(length = 50)
    private String email;
    @Column(length = 50)
    private String moreInfo;
    @Column(length = 50)
    private String expireDate;
    @NotFound(action=NotFoundAction.IGNORE)
    @ManyToOne(optional = true)
    private GroupDbEntity group;
    @OneToMany (cascade=CascadeType.REMOVE)
    @JoinColumn(name="user_name", referencedColumnName= "user_name")
    private List<UserRoleDbEntity> roles;

    public UserDbEntity() {
    }

    public UserDbEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return user_name;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the moreInfo
     */
    public String getMoreInfo() {
        return moreInfo;
    }

    public String getExpireDate() {
        return this.expireDate;
    }

    public String toString() {
        return "User=" + id + "";
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
        this.user_name = name;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param moreInfo the moreInfo to set
     */
    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public void setExpireDate(String expirationDate) {
        this.expireDate = expirationDate;
    }

    public GroupDbEntity getGroup() {
        return group;
    }

    public void setGroup(GroupDbEntity gr) {
        this.group = gr;
    }

    public void addRole(UserRoleDbEntity rNew) {
        if (!roles.contains(rNew)) {
            roles.add(rNew);
        }
        rNew.setUser(this);
    }

    public void removeRole(UserRoleDbEntity role, EntityManager em){
        roles.remove(role);
    }

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof UserDbEntity)) {
            return false;
        }
        UserDbEntity other = (UserDbEntity) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public User toPojo() {
        User entity = new User();
        entity.guid = getId();
        entity.name = getName();
        entity.fullName = getFullName();
        entity.email = getEmail();
        entity.moreInfo = getMoreInfo();
        entity.password = getPassword();
        entity.expireDate = getExpireDate();
        entity.group = (getGroup() != null ? getGroup().getId() + "" : null);
        List<String> lstR = new ArrayList();
        for (UserRoleDbEntity role : roles) {
            lstR.add(role.getId() + "");
        }
        entity.roles = lstR;
        return entity;
    }

    public void updateFrom(User user, EntityManager em) {
        this.setName(user.name);
        this.setFullName(user.fullName);
        this.setEmail(user.email);
        this.setMoreInfo(user.moreInfo);
        this.setPassword(user.password);
        this.setExpireDate(user.expireDate);
        GroupDbEntity gr = null;
        if (user.group != null) {
            try {
                gr = em.find(GroupDbEntity.class, Long.parseLong(user.group));
            } catch (NumberFormatException e) {
            }
        }
        GroupDbEntity actual = getGroup();
        if(actual != null){
            actual.removeMember(this);
        }
        if (gr != null) {
            gr.addMember(this);
        }
        List<String> lstRid= user.roles;
        List<UserRoleDbEntity> lstR = new ArrayList(roles);
        //remove deleted
        for (UserRoleDbEntity db : lstR) {
            String sId = db.getId() + "";
            if (lstRid == null || !lstRid.contains(sId)) {
                removeRole(db, em);
            }
        }
        if (lstRid != null) {
            //add new
            ArrayList<String> lstMod = new ArrayList();
            for (String sId : lstRid) {
                if (sId == null || sId.length() == 0) {
                    continue;
                } else {
                    lstMod.add(sId);
                    boolean bFound = false;
                    for (UserRoleDbEntity db : lstR) {
                        if ((db.getId() + "").equals(sId)) {
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
                            UserRoleDbEntity pNew = em.find(UserRoleDbEntity.class, lgId);
                            if (pNew != null) {
                                addRole(pNew);
                            }
                        }
                    }
                }
            }
            user.roles = lstMod;
        }
    }
}
