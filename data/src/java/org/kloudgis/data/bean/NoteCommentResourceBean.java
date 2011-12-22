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
import org.kloudgis.data.pojo.NoteComment;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.store.NoteCommentDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.data.pojo.NoteCommentMessage;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/note_comments")
@Produces({"application/json"})
public class NoteCommentResourceBean {

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
                NoteCommentDbEntity comment = em.find(NoteCommentDbEntity.class, fId);
                if (comment != null) {
                    NoteComment pojo = comment.toPojo();
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
    public Response updateFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox, NoteComment in_comment) throws NotFoundException {
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
                NoteCommentDbEntity comment = em.find(NoteCommentDbEntity.class, fId);
                try {
                    if (comment != null) {
                        if (comment.getAuthor() != null && lMember.getUserId().longValue() != comment.getAuthor().longValue()) {
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the author of the comment: " + sandbox).build();
                        }
                        em.getTransaction().begin();
                        comment.fromPojo(in_comment);
                        NoteComment pojo = comment.toPojo();
                        em.getTransaction().commit();
                        em.close();
                        SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                        NotificationFactory.postMessage(sandbox, auth_token, new NoteCommentMessage(pojo.guid, pojo.note, NoteCommentMessage.UPDATE, user.user));
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
                NoteCommentDbEntity note_comment = em.find(NoteCommentDbEntity.class, fId);
                try {
                    if (note_comment != null) {
                        if (note_comment.getAuthor() == null || note_comment.getAuthor().longValue() == lMember.getUserId().longValue() || AuthorizationFactory.isSandboxOwner(lMember, sContext, auth_token, sandbox)) {
                            em.getTransaction().begin();
                            NoteComment pojo = note_comment.toPojo();
                            em.remove(note_comment);
                            em.getTransaction().commit();
                            em.close();
                            SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                            NotificationFactory.postMessage(sandbox, auth_token, new NoteCommentMessage(pojo.guid, pojo.note, NoteCommentMessage.DELETE, user.user));
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
    public Response addFeature(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, NoteComment in_comment) throws NotFoundException {
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
                NoteDbEntity note;
                try {
                    note = em.find(NoteDbEntity.class, in_comment.note);
                } catch (EntityNotFoundException e) {
                    em.close();
                    return Response.status(Response.Status.BAD_REQUEST).entity("Note " + in_comment.note + " is not found in sandbox " + sandbox + ".").build();
                }
                em.getTransaction().begin();
                NoteCommentDbEntity note_comment = new NoteCommentDbEntity();
                note_comment.fromPojo(in_comment);
                note_comment.setAuthor(lMember.getUserId());
                note_comment.setAuthorDescriptor(lMember.getUserDescriptor());
                note_comment.setDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                note_comment.setNote(note);
                NoteComment pojo = null;
                try {
                    em.persist(note_comment);
                    em.getTransaction().commit();
                    em.close();
                    pojo = note_comment.toPojo();
                    SignupUser user = ApiFactory.getUser(sContext, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                    NotificationFactory.postMessage(sandbox, auth_token, new NoteCommentMessage(pojo.guid, pojo.note, NoteCommentMessage.ADD, user.user));                
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
