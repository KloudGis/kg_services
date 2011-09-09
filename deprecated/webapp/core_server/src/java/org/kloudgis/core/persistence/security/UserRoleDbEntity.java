/*
 * EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretechnologies.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 */
package org.kloudgis.core.persistence.security;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.kloudgis.core.pojo.security.UserRole;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "USER_ROLES")
@NamedQueries({
    @NamedQuery(name = "UserRole.findAll", query = "SELECT c FROM UserRoleDbEntity c order by c.user_name")})
public class UserRoleDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 50)
    private String user_name;
    @Column(length = 50)
    private String role_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return user_name;
    }

    public String getRoleName() {
        return role_name;
    }

    public void setUserName(String name) {
        user_name = name;
    }

    public void setRoleName(String name) {
        role_name = name;
    }

    public void setUser(UserDbEntity user) {
        if (user != null) {
            this.user_name = user.getName();
        } else {
            this.user_name = null;
        }
    }

    public UserRole toPojo() {
        UserRole pojo = new UserRole();
        pojo.guid = getId();
        pojo.role_name = getRoleName();
        pojo.user_name = getUserName();
        return pojo;
    }

    public void updateFrom(UserRole pojo, EntityManager em) {
        this.setId(pojo.guid);
        this.setRoleName(pojo.role_name);
        this.setUserName(pojo.user_name);
    }
}
