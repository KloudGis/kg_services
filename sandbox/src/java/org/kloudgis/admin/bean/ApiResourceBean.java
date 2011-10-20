/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import java.io.IOException;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.KGConfig;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserSandboxDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
public class ApiResourceBean {

    /**
     * Ping GET to test if the server is UP
     * @return 
     */
    @Path("sandbox_owner")
    @GET
    public Response sandboxOwner(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @QueryParam(value = "sandbox") String sandboxKey) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        SandboxDbEntity sandbox = (SandboxDbEntity) em.getSession().createCriteria(SandboxDbEntity.class).add(Restrictions.eq("unique_key", sandboxKey)).uniqueResult();
        em.close();
        if (sandbox == null) {
            throw new EntityNotFoundException();
        }
        return Response.ok(sandbox.getOwnerId() + "").build();
    }

    @POST
    @Path("/bind_users")
    public Response bindUser(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @QueryParam(value = "sandbox") String sandboxKey, List<Long> users) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        try {
            SandboxDbEntity entity = AuthorizationFactory.getSandboxFromKey(sandboxKey, em);
            if (entity != null) {
                em.getTransaction().begin();
                for (Long uId : users) {
                    UserSandboxDbEntity u = new UserSandboxDbEntity();
                    u.setSandbox(entity);
                    u.setUserId(uId);
                    em.persist(u);
                    entity.addUser(u);
                }
                em.getTransaction().commit();
                em.close();
                return Response.ok().build();
            } else {
                em.close();
                throw new EntityNotFoundException();
            }
        } catch (Exception e) {
            if (em.isOpen()) {
                em.close();
            }
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
