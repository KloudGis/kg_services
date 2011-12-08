/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.data.Configuration;
import org.kloudgis.data.KGConfig;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.persistence.DatabaseFactory;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.core.pojo.SignupUser;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
@Produces({"application/json"})
public class ApiResourceBean {

    
    @Path("membership")
    @GET
    public Response getMemberByUserId(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @QueryParam("user_id") Long user_id) throws NotFoundException {
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
            if(lAccess != null){
                return Response.ok(lAccess.toPojo()).build();
            }else{
                return Response.serverError().entity("member not found").build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (em != null) {
                em.close();
            }
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    @Path("map_access")
    @GET
    public Response hasMapAccess(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @QueryParam("user_id") Long user_id) throws NotFoundException {
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
            ex.printStackTrace();
            if (em != null) {
                em.close();
            }
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @Path("database_prop")
    @GET
    public Response dbProperties(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) throws NotFoundException {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
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
    public Response createSandboxDb(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, String sandboxKey) {
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
    public Response dropSandboxDb(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, String sandboxKey) {
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

    @Path("add_owner_member")
    @POST
    public Response addOwnerMember(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, Long id, @QueryParam("sandbox") String sandboxKey) {
        HibernateEntityManager em = null;
        try {
            em = PersistenceManager.getInstance().getEntityManager(sandboxKey);
            String[] ret = ApiFactory.apiGet(null, KGConfig.getConfiguration().auth_url + "/user_by_id/" + id, api_key);
            if (ret != null && ret[1].equals("200")) {
                ObjectMapper mapper = new ObjectMapper();
                SignupUser user = mapper.readValue(ret[0], SignupUser.class);
                em.getTransaction().begin();
                //add the sandbox owner membership auto
                MemberDbEntity member = new MemberDbEntity();
                member.setUserId(user.id);
                member.setDescriptor(user.name);
                member.setMembership("owner");
                member.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                //1 billion + 1 to 2 billion
                member.setSeqIdMin(1000000001L);
                member.setSeqIdMax(2000000000L);
                em.persist(member);
                em.getTransaction().commit();
                em.close();
                return Response.ok().build();
            }else{
                em.close();
                return Response.serverError().entity(ret != null ? ret[0] : "?").build();
            }          
        } catch (IOException ex) {
            if(em != null){
                em.close();
            }
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}
