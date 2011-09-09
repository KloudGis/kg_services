package org.kloudgis.core.resources.security;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.security.UserDbEntity;
import org.kloudgis.core.pojo.PojoFactory;
import org.kloudgis.core.pojo.security.User;

/**
 *
 * @author jeanfelixg
 */
@Path("/admin/users")
public class UserAdminResourceBean {

    @GET
    @Produces({"application/json"})
    public List<User> getUsers(@Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
//        em.getTransaction().begin();
//        for(int i=1000; i< 2000; i++){
//            UserDbEntity u = new UserDbEntity();
//            u.setName("User" + i);
//            em.persist(u);
//        }
//        em.getTransaction().commit();
//        if (org.kloudgis.SecurityManager.isAutorized(em, req)) {
            List<UserDbEntity> lstDb = em.createNamedQuery("User.findAll").getResultList();
            List<User> lstU = PojoFactory.toUserEntities(lstDb);
            em.close();
            return lstU;
//        }
//        em.close();
//        throw new UnauthorizedException();
    }

    @POST
    @Produces({"application/json"})
    public User addUser(User user, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        ensureUniqueName(user, em);
        em.getTransaction().begin();
        UserDbEntity uDb = user.toDbEntity(em);
        em.persist(uDb);
        em.getTransaction().commit();
        user.guid = uDb.getId();
        em.close();
        return user;
    }

    @PUT
    @Path("{userId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public User updateUser(User user, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserDbEntity uDb = em.find(UserDbEntity.class, user.guid);
        if (uDb != null) {
            ensureUniqueName(user, em);
            em.getTransaction().begin();
            uDb.updateFrom(user, em);
            em.getTransaction().commit();
        }
        em.close();
        return user;
    }

    @Path("{userId}")
    @GET
    @Produces({"application/json"})
    public User getUser(@PathParam("userId") Long uId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserDbEntity uDb = em.find(UserDbEntity.class, uId);
        if (uDb != null) {
            User user = uDb.toPojo();
            em.close();
            return user;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + uId);
    }

    @Path("{userId}")
    @DELETE
    @Produces({"application/json"})
    public Response deleteUser(@PathParam("userId") Long uId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserDbEntity uDb = em.find(UserDbEntity.class, uId);
        if (uDb != null) {
            em.getTransaction().begin();
            em.remove(uDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + uId);
    }

    private void ensureUniqueName(User user, EntityManager em) {
        int iCpt = 1;
        String uName = user.name;
        boolean bUnique = true;
        do {
            bUnique = true;
            Query query = em.createQuery("SELECT u from UserDbEntity u where u.id != :uid AND u.user_name = :uname");
            query = query.setParameter("uid", user.guid).setParameter("uname", uName);
            List lstR = query.getResultList();
            bUnique = lstR.isEmpty();
            if (!bUnique) {
                uName = user.name + "_" + iCpt++;
            }
        } while (!bUnique);
        user.name = uName;
    }
}

