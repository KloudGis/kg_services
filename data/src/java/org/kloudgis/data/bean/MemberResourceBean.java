/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.data.KGConfig;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.data.pojo.Member;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.core.pojo.SignupUser;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/members")
@Produces({"application/json"})
public class MemberResourceBean {

    @GET
    @Path("logged_member")
    public Response getFeature(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) throws NotFoundException {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                Member pojo = lMember.toPojo();
                em.close();
                return Response.ok(pojo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @POST
    @Path("multiple_email/{membership}")
    public Response addMembers(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token,
            @PathParam("membership") String membership, List<String> lstEmail, @QueryParam("sandbox") String sandbox) throws NotFoundException {

        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            boolean bOwner = false;
            try {
                if (lMember != null) {
                    bOwner = AuthorizationFactory.isSandboxOwner(lMember, session, auth_token, sandbox);
                }
            } catch (IOException ex) {
            }
            if (bOwner) {
                em.getTransaction().begin();
                List<Long> lstSucess = new ArrayList();
                ObjectMapper mapper = new ObjectMapper();
                for (String email : lstEmail) {
                    if (email == null || email.trim().length() == 0) {
                        continue;
                    }
                    try {
                        String[] user_ret = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().auth_url + "/user_by_email/" + email, KGConfig.getConfiguration().api_key);
                        if (user_ret != null && user_ret[1].equals("200")) {
                            SignupUser u = mapper.readValue(user_ret[0], SignupUser.class);
                            if (em.getSession().createCriteria(MemberDbEntity.class).add(Restrictions.eq("user_id", u.id)).list().isEmpty()) {
                                MemberDbEntity member = new MemberDbEntity();
                                member.setUserId(u.id);
                                member.setDescriptor(u.name);
                                member.setMembership(membership);
                                em.persist(member);
                            }
                            lstSucess.add(u.id);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                try {
                    ApiFactory.apiPost(auth_token, KGConfig.getConfiguration().sandbox_url + "/bind_users?sandbox=" + sandbox, KGConfig.getConfiguration().api_key, lstSucess);
                    em.getTransaction().commit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    em.getTransaction().rollback();
                    em.close();
                    return Response.serverError().entity("error binding users: " + ex.getMessage()).build();
                }
                em.close();
                return Response.ok().entity(lstSucess).build();

            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a the owner of sandbox: " + sandbox).build();

            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}