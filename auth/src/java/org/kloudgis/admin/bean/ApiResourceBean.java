/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.KGConfig;
import org.kloudgis.admin.store.UserDbEntity;
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
    public Response connectedUserID(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        em.close();
        if(u == null){
            throw new EntityNotFoundException();
        }
        return Response.ok(u.getId() + "").build();
    }
    
}
