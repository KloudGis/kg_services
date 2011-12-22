/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import javassist.NotFoundException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.core.pojo.SignupUser;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.data.KGConfig;
import org.kloudgis.data.NotificationFactory;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.data.pojo.FeatureComment;
import org.kloudgis.data.pojo.FeatureCommentMessage;
import org.kloudgis.data.store.FeatureCommentDbEntity;
import org.kloudgis.data.store.FeatureDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/feature_comments")
@Produces({"application/json"})
public class FeatureCommentResourceBean {

    @GET
    @Path("{fId}")
    public Response getFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox) throws NotFoundException {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                FeatureCommentDbEntity comment = em.find(FeatureCommentDbEntity.class, fId);
                if (comment != null) {
                    FeatureComment pojo = comment.toPojo();
                    em.close();
                    return Response.ok(pojo).build();
                }
                em.close();
                throw new EntityNotFoundException("Not found:" + fId);
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @PUT
    @Path("{fId}")
    public Response updateFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox, FeatureComment in_comment) throws NotFoundException {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                FeatureCommentDbEntity comment = em.find(FeatureCommentDbEntity.class, fId);
                try {
                    if (comment != null) {
                        if (comment.getAuthor() != null && lMember.getUserId().longValue() != comment.getAuthor().longValue()) {
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the author of the comment: " + sandbox).build();
                        }
                        em.getTransaction().begin();
                        comment.fromPojo(in_comment);
                        FeatureComment pojo = comment.toPojo();
                        em.getTransaction().commit();
                        em.close();
                        SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                        NotificationFactory.postMessage(sandbox, auth_token, new FeatureCommentMessage(pojo.guid, pojo.feature, FeatureCommentMessage.UPDATE, user.user));
                        return Response.ok(pojo).build();
                    } else {
                        em.close();
                        throw new EntityNotFoundException("Not found:" + fId);
                    }
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @DELETE
    @Path("{fId}")
    public Response deleteFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox) throws NotFoundException {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                FeatureCommentDbEntity note_comment = em.find(FeatureCommentDbEntity.class, fId);
                try {
                    if (note_comment != null) {
                        if (note_comment.getAuthor() == null || note_comment.getAuthor().longValue() == lMember.getUserId().longValue() || AuthorizationFactory.isSandboxOwner(lMember, sContext, auth_token, sandbox)) {
                            em.getTransaction().begin();
                            FeatureComment pojo = note_comment.toPojo();
                            em.remove(note_comment);
                            em.getTransaction().commit();
                            em.close();
                            SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                            NotificationFactory.postMessage(sandbox, auth_token, new FeatureCommentMessage(pojo.guid, pojo.feature, FeatureCommentMessage.DELETE, user.user));
                            return Response.noContent().build();
                        } else {
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the author of the comment nor the sandbox owner: " + sandbox).build();
                        }
                    }
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
                em.close();
                throw new EntityNotFoundException("Not found:" + fId);
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @POST
    public Response addFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, FeatureComment in_comment) throws NotFoundException {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(sContext, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {
                FeatureDbEntity feature;
                    feature = FeatureDbEntity.findByGuid(in_comment.feature, em);
                    if(feature == null){
                       em.close();
                    return Response.status(Response.Status.BAD_REQUEST).entity("Feature " + in_comment.feature + " is not found in sandbox " + sandbox + ".").build();
                    }
                em.getTransaction().begin();
                FeatureCommentDbEntity fcomment = new FeatureCommentDbEntity();
                fcomment.fromPojo(in_comment);
                fcomment.setAuthor(lMember.getUserId());
                fcomment.setAuthorDescriptor(lMember.getUserDescriptor());
                fcomment.setDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                fcomment.setFeature(feature);
                FeatureComment pojo = null;
                try {
                    em.persist(fcomment);
                    em.getTransaction().commit();
                    em.close();
                    pojo = fcomment.toPojo();
                    SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                    NotificationFactory.postMessage(sandbox, auth_token, new FeatureCommentMessage(pojo.guid, pojo.feature, FeatureCommentMessage.ADD, user.user));                
                    return Response.ok(pojo).build();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }              
                
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}
