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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.data.pojo.Note;
import org.kloudgis.data.pojo.NoteComment;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.store.NoteCommentDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/comments")
@Produces({"application/json"})
public class CommentResourceBean {
    
    @GET
    @Path("{fId}")
    public Response getFeature(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox) throws NotFoundException {
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
    public Response updateFeature(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox, NoteComment in_comment) throws NotFoundException {
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
                NoteCommentDbEntity comment = em.find(NoteCommentDbEntity.class, fId);
                try {
                    if (comment != null) {
                        if(lMember.getUserId() != comment.getAuthor()){
                            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not the author of the comment: " + sandbox).build();
                        }
                        em.getTransaction().begin();                        
                        comment.fromPojo(in_comment);
                        NoteComment pojo = comment.toPojo();
                        em.getTransaction().commit();
                        em.close();
                        return Response.ok(pojo).build();
                    }                   
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
                NoteComment pojo = comment.toPojo();
                em.close();
                return Response.ok(pojo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
    
    
    @DELETE
    @Path("{fId}")
    public Response deleteFeature(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam("fId") Long fId, @QueryParam("sandbox") String sandbox) throws NotFoundException {
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
                NoteDbEntity note = em.find(NoteDbEntity.class, fId);
                try {
                    if (note != null) {
                        if (note.getAuthor() == lMember.getUserId() || AuthorizationFactory.isSandboxOwner(lMember, session, auth_token, sandbox)) {
                            em.getTransaction().begin();
                            em.remove(note);
                            em.getTransaction().commit();
                            em.close();
                            return Response.noContent().build();
                        }else{
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
                Note pojo = note.toPojo();
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
    public Response addFeature(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, NoteComment in_comment) throws NotFoundException {
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
                em.getTransaction().begin();
                NoteCommentDbEntity note_comment = new NoteCommentDbEntity();
                note_comment.fromPojo(in_comment);
                note_comment.setAuthor(lMember.getUserId());
                note_comment.setAuthorDescriptor(lMember.getUserDescriptor());
                note_comment.setDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                try {
                    em.persist(note_comment);
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                    return Response.serverError().entity(e.getMessage()).build();
                }
                NoteComment pojo = note_comment.toPojo();
                em.close();
                return Response.ok(pojo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}
