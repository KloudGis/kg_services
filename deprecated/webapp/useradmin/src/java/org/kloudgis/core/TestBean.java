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
package org.kloudgis.core;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import org.hibernate.SQLQuery;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.exception.SQLGrammarException;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.security.UserDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/test/createdb")
public class TestBean {

    @POST
    @Produces({"application/json"})
    public String createDb() throws WebApplicationException {
        //connection to an existing database on the server
        HibernateEntityManager emAdm = PersistenceManager.getInstance().getEntityManagerAdmin();
        SQLQuery qCreate = emAdm.getSession().createSQLQuery("CREATE DATABASE test_jeff template=postgis");
        try{
            qCreate.executeUpdate();
        }catch(SQLGrammarException e){
            System.out.println("Error !!" + e.getSQLException());
            emAdm.close();
            return e.getSQLException().getLocalizedMessage();
        }
        emAdm.close();
        Map properties = new HashMap();
        //change the connection string to the new db
        properties.put("hibernate.connection.url", "jdbc:postgresql_postGIS://192.168.12.36:5432/test_jeff");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dataPU", properties);      
        return "Yeah";
    }

    @GET
    @Produces({"application/json"})
    public String test(){
        EntityManager em = PersistenceManager.getInstance().getEntityManager("adminPU");
        UserDbEntity e = em.find(UserDbEntity.class, 1L);
        System.out.println(e.getEmail());
        em.close();
        return "Yes";
    }
}
