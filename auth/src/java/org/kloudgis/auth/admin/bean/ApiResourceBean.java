/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.auth.admin.bean;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.auth.AuthorizationManager;
import org.kloudgis.auth.KGConfig;
import org.kloudgis.auth.admin.store.UserDbEntity;
import org.kloudgis.auth.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
@Produces({"application/json"})
public class ApiResourceBean {

    /**
     * Ping GET to test if the server is UP
     * @return 
     */
    @Path("connected_user")
    @GET
    public Response connectedUser(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        em.close();
        if (u == null) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        return Response.ok(u.toSimpleUser()).build();
    }

    @Path("user_by_id/{id}")
    @GET
    public Response getUser(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @PathParam("id") Long id) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        if (id == null) {
            throw new EntityNotFoundException();
        }
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = em.find(UserDbEntity.class, id);
        em.close();
        if (u == null) {
            throw new EntityNotFoundException();
        }
        return Response.ok(u.toSimpleUser()).build();
    }

    @Path("user_by_email/{email}")
    @GET
    public Response getUserByEmail(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @PathParam("email") String email) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        if (email == null) {
            throw new EntityNotFoundException();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        List<UserDbEntity> lstU = em.getSession().createCriteria(UserDbEntity.class).add(Restrictions.eq("email", email)).list();
        em.close();
        if (lstU != null && lstU.size() > 0) {
            return Response.ok(lstU.get(0).toSimpleUser()).build();
        }
        throw new EntityNotFoundException();
    }
}
