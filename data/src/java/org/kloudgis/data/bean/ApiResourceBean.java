/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import javassist.NotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.KGConfig;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
public class ApiResourceBean {

    @Path("map_access")
    @GET
    public Response hasMapAccess(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam ("sandbox") String sandbox, @QueryParam ("user_id") Long user_id) throws NotFoundException {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        HibernateEntityManager em = null;
        try {
            em = PersistenceManager.getInstance().getEntityManager(sandbox);         
            boolean bAccess = AuthorizationFactory.isMember(em, user_id, sandbox, auth_token);
            return Response.ok(bAccess + "").build();
        } catch (Exception ex) {
            if(em != null){
                em.close();
            }
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}