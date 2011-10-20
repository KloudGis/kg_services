/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javassist.NotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.Configuration;
import org.kloudgis.KGConfig;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.DatabaseFactory;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
@Produces({"application/json"})
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
            MemberDbEntity lAccess = AuthorizationFactory.getMember(em, user_id, sandbox, auth_token);
            return Response.ok((lAccess != null) + "").build();
        } catch (Exception ex) {
            if(em != null){
                em.close();
            }
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    
    @Path("database_prop")
    @GET
    public Response dbProperties(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam ("sandbox") String sandbox) throws NotFoundException {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        HibernateEntityManager em = null;
        Map prop = new HashMap();
        Configuration conf = KGConfig.getConfiguration();
        prop.put("user", conf.db_user);
        prop.put("pwd", conf.db_pwd);
        int iStart = conf.db_url.indexOf("//") + 2;
        int iEnd = conf.db_url.lastIndexOf(":");
        prop.put("host", conf.db_url.substring(iStart, iEnd));
        prop.put("port", "5432");
        return Response.ok(prop).build();
    }
    
    @Path("create_db")
    @POST
    public Response createSandboxDb(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, String sandboxKey){
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            DatabaseFactory.createDb(sandboxKey);
        } catch (SQLException ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
        return Response.ok().build();
    }
    
    @Path("drop_db")
    @POST
    public Response dropSandboxDb(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, String sandboxKey){
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            PersistenceManager.getInstance().closeEntityManagerFactory(sandboxKey);
            DatabaseFactory.dropDb(sandboxKey);
        } catch (SQLException ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
        return Response.ok().build();
    }
}
