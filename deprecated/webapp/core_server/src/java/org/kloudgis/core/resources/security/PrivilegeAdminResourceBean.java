/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.resources.security;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.security.PrivilegeDbEntity;
import org.kloudgis.core.pojo.PojoFactory;
import org.kloudgis.core.pojo.security.Privilege;

/**
 *
 * @author jeanfelixg
 */
@Path("/admin/privileges")
public class PrivilegeAdminResourceBean {

    @GET
    @Produces({"application/json"})
    public List<Privilege> getPrivileges(@Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
//        if (com.space.admin.SecurityManager.isAutorized(em, req)) {
        List<PrivilegeDbEntity> lstDb = em.createNamedQuery("Privilege.findAll").getResultList();      
        List<Privilege> lstP =  PojoFactory.toPrivilegeEntities(lstDb);
        em.close();
        return lstP;
       // }
      //  em.close();
      //  throw new UnauthorizedException();
    }

    @Path("{pId}")
    @GET
    @Produces({"application/json"})
    public Privilege getPriv(@PathParam("pId") Long pId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        PrivilegeDbEntity pDb = em.find(PrivilegeDbEntity.class, pId);    
        if (pDb != null) {
            Privilege grp = pDb.toPojo();
            em.close();
            return grp;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + pId);
    }

    @PUT
    @Path("{pId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Privilege updatePrivilege(Privilege priv, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        PrivilegeDbEntity pDb = em.find(PrivilegeDbEntity.class, priv.getId());
        if (pDb != null) {
            em.getTransaction().begin();
            pDb.updateFrom(priv, em);
            em.getTransaction().commit();
        }
        em.close();
        return priv;
    }
}
