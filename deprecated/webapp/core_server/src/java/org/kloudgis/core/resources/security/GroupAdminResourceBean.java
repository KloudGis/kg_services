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
import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.pojo.PojoFactory;
import org.kloudgis.core.pojo.security.Group;

/**
 *
 * @author jeanfelixg
 */
@Path("/admin/groups")
public class GroupAdminResourceBean {

    @GET
    @Produces({"application/json"})
    public List<Group> getGroups(@Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
//        if (com.space.admin.SecurityManager.isAutorized(em, req)) {
        List<GroupDbEntity> lstDb = em.createNamedQuery("Group.findAll").getResultList();
        List<Group> lstG = PojoFactory.toGroupEntities(lstDb);
        em.close();
        return lstG;
//        }
//        em.close();
//        throw new UnauthorizedException();
    }

    @POST
    @Produces({"application/json"})
    public Group addGroup(Group group, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        ensureUniqueName(group, em);
        em.getTransaction().begin();
        GroupDbEntity uDb = group.toDbEntity(em);
        em.persist(uDb);
        em.getTransaction().commit();
        group.guid = uDb.getId();
        em.close();
        return group;
    }

    @PUT
    @Path("{groupId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Group updateGroup(Group group, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        GroupDbEntity gDb = em.find(GroupDbEntity.class, group.guid);
        if (gDb != null) {
            ensureUniqueName(group, em);
            em.getTransaction().begin();
            gDb.updateFrom(group, em);
            em.getTransaction().commit();
        }
        em.close();
        return group;
    }

    @Path("{groupId}")
    @GET
    @Produces({"application/json"})
    public Group getGroup(@PathParam("groupId") Long gId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        GroupDbEntity uDb = em.find(GroupDbEntity.class, gId);      
        if (uDb != null) {
            Group grp = uDb.toPojo();
            em.close();
            return grp;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + gId);
    }

    @Path("{groupId}")
    @DELETE
    public Response deleteGroup(@PathParam("groupId") Long gId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        GroupDbEntity uDb = em.find(GroupDbEntity.class, gId);
        if (uDb != null) {
            em.getTransaction().begin();
            em.remove(uDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + gId);
    }

    private void ensureUniqueName(Group group, EntityManager em) {
        int iCpt = 1;
        String uName = group.name;
        boolean bUnique = true;
        do {
            bUnique = true;
            Query query = em.createQuery("SELECT g from GroupDbEntity g where g.id != :gid AND g.name = :gname");
            query = query.setParameter("gid", group.guid).setParameter("gname", uName);
            List lstR = query.getResultList();
            bUnique = lstR.isEmpty();
            if (!bUnique) {
                uName = group.name + "_" + iCpt++;
            }
        } while (!bUnique);
        group.name = uName;
    }
}

