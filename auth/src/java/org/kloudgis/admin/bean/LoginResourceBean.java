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

import java.util.Calendar;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.LoginFactory;
import org.kloudgis.admin.pojo.Credential;
import org.kloudgis.admin.pojo.LoginResponse;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/public/login")
@Produces({"application/json"})
public class LoginResourceBean {

    /**
     * try to login with the provided credentials
     * @param req  
     * @param crd   the credentials to test
     * @return  the auth token if successful
     */
    @POST   
    public Response login(@Context HttpServletRequest req, Credential crd) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = authenticate(em, crd.user, crd.pwd);
        if (u != null) {
            String hashed_token = null;
            if (crd.user != null) {
                //unique token for this users
                String token = Calendar.getInstance().getTimeInMillis() + u.getSalt() + u.getEmail();
                hashed_token = LoginFactory.hashString(token, "SHA-512");
                em.getTransaction().begin();
                u.setAuthToken(hashed_token);
                //System.out.println("user:" + crd.user + " pass:" + crd.pwd);
               // System.out.println("New token is " + hashed_token);
                em.getTransaction().commit();
                em.close();
            }else{
                hashed_token = crd.pwd;
            }
            //create a session
            HttpSession session = req.getSession(true);
            session.setAttribute("timeout", Calendar.getInstance().getTimeInMillis());
            LoginResponse response = new LoginResponse();
            response.token = hashed_token;
            response.user = u.toSimpleUser();
            return Response.ok(response).build();
        }
        em.close();
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    /**
     * Logout the current user
     * @param req
     * @return 
     */
    @Path("logout")
    @POST
    public Response logout(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        //TODO destroy the auth token from the DB(value in the cookie).

        return Response.ok().build();
    }

    /**
     * Ping GET to test if the server is UP
     * @return 
     */
    @Path("ping")
    @GET
    public Response pingServer() {
        return Response.ok(new Message("ping")).build();
    }

    /**
     * Get the logged user properties
     * @return user logged in
     */
    @Path("logged_user")
    @GET
    public SignupUser loggedUser(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token) {
        SignupUser pojo = null;
        if (auth_token != null) {
            EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
            UserDbEntity u = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
            if (u != null) {
                pojo = u.toSimpleUser();
            }
            em.close();
        }
        return pojo;
    }

    private UserDbEntity authenticate(EntityManager em, String user, String password_hash) {
        try {
            if (user != null && password_hash != null) {
                UserDbEntity u = em.createQuery("from UserDbEntity where email=:u", UserDbEntity.class).setParameter("u", user).getSingleResult();
                if (u != null) {
                    String expectedPass = LoginFactory.encryptPassword(password_hash, u.getSalt());
                    if (expectedPass != null && expectedPass.equals(u.getPasswordHash())) {
                        return u;
                    }
                    System.out.println("Auth failed");
                }
            } else if (password_hash != null) {
                return new AuthorizationManager().getUserFromAuthToken(password_hash, em);
            }
        } catch (NoResultException e) {
        }
        return null;
    }
}
