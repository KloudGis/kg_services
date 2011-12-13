/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.pojo.Records;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.data.pojo.Bookmark;
import org.kloudgis.data.store.BookmarkDbEntity;
import org.kloudgis.data.store.MemberDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/bookmarks")
@Produces({"application/json"})
public class BookmarkResourceBean {

    @GET
    public Response getBookmarks(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) {
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
                List<BookmarkDbEntity> lstDb = em.getSession().createCriteria(BookmarkDbEntity.class).list();
                List<Bookmark> lstB = new ArrayList(lstDb.size());
                for (BookmarkDbEntity bDb : lstDb) {
                    Bookmark pojo = bDb.toPojo();
                    lstB.add(pojo);
                }
                em.close();
                Records rec = new Records();
                rec.records = lstB;
                return Response.ok(rec).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Sandbox entity manager not found for:" + sandbox + ".").build();
        }
    }

    @GET
    @Path("{bId}")
    public Response getBookmark(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @PathParam("bId") Long bookmarkId) {
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
                BookmarkDbEntity bDb = em.find(BookmarkDbEntity.class, bookmarkId);
                Bookmark pojo = null;
                if (bDb != null) {
                    pojo = bDb.toPojo();
                } else {
                    em.close();
                    return Response.status(Response.Status.BAD_REQUEST).entity("Bookmark " + bookmarkId + " is not found in sandbox " + sandbox + ".").build();
                }
                em.close();
                return Response.ok(pojo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Sandbox entity manager not found for:" + sandbox + ".").build();
        }
    }

    @DELETE
    @Path("{bId}")
    public Response deleteBookmark(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @PathParam("bId") Long bookmarkId) {
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
                BookmarkDbEntity bDb = em.find(BookmarkDbEntity.class, bookmarkId);
                if (bDb != null) {
                    boolean bAuth = false;
                    if (bDb.getUser() != null) {
                        if (bDb.getUser().longValue() == lMember.getUserId()) {
                            bAuth = true;
                        } else {
                            try {
                                bAuth = AuthorizationFactory.isSandboxOwner(lMember, sContext, auth_token, sandbox);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (bAuth) {
                        em.getTransaction().begin();
                        em.remove(bDb);
                        em.getTransaction().commit();
                        em.close();
                        return Response.ok().build();
                    } else {
                        em.close();
                        return Response.status(Response.Status.UNAUTHORIZED).entity("User do not have the rights to delete the bookmark in sandbox: " + sandbox).build();
                    }
                } else {
                    em.close();
                    return Response.status(Response.Status.BAD_REQUEST).entity("Bookmark " + bookmarkId + " is not found in sandbox " + sandbox + ".").build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Sandbox entity manager not found for:" + sandbox + ".").build();
        }
    }

    @POST
    public Response addBookmark(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, Bookmark bookmark) {
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
                em.getTransaction().begin();
                BookmarkDbEntity entity = new BookmarkDbEntity();
                entity.fromPojo(bookmark);
                entity.setDateCreate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                entity.setUserCreate(lMember.getUserId());
                entity.setUserDescriptor(lMember.getUserDescriptor());
                em.persist(entity);
                em.getTransaction().commit();
                em.close();
                return Response.ok(entity.toPojo()).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Sandbox entity manager not found for:" + sandbox + ".").build();
        }
    }
}
