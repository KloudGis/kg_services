/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.KGConfig;
import org.kloudgis.admin.store.SandboxDbEntity;
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
    @Path("user_id")
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
        if(sandbox == null){
            throw new EntityNotFoundException();
        }
        return Response.ok(sandbox.getOwnerId() + "").build();
    }
    
}
