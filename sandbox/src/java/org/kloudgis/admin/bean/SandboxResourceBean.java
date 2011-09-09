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
package org.kloudgis.admin.bean;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.admin.pojo.Records;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/sandboxes")
@Produces({"application/json"})
public class SandboxResourceBean {

    
    @GET
    public Response getSandboxes(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token){
        //ask the auth service to validate the user...
        
        //FIXEME: for now, get the all the sandboxes.
        System.out.println("auth tokem is " + auth_token);
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        List<SandboxDbEntity> sandboxes = em.getSession().createCriteria(SandboxDbEntity.class).list();
        List<Sandbox> list = new ArrayList();
        for(SandboxDbEntity db: sandboxes){
            list.add(db.toPojo());
        }
        Records ret = new Records();
        ret.records = list;
        return Response.ok(ret).build();     
    }
}
