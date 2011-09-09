/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.resources.feature;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.feature.PriorityDbEntity;
import org.kloudgis.core.persistence.feature.PriorityDbEntity;
import org.kloudgis.core.pojo.feature.Priority;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/priorities")
@Produces({"application/json"})
public class PriorityResourceBean {

    @GET
    @Produces({"application/json"})
    public List<Priority> getPriorities() {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        List<PriorityDbEntity> lstDb = em.createQuery("from PriorityDbEntity").getResultList();
        List<Priority> lstFT = new ArrayList(lstDb.size());
        for (PriorityDbEntity fDb : lstDb) {
            lstFT.add(fDb.toPojo());
        }
        em.close();
        return lstFT;
    }

     @GET
    @Path("{fId}")
    @Produces({"application/json"})
    public Priority getPriority(@PathParam("fId") Long fId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        PriorityDbEntity fDb = em.find(PriorityDbEntity.class, fId);
        if (fDb != null) {
            Priority f = fDb.toPojo();
            em.close();
            return f;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + fId);
    }

}
