/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.resources.feature;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.kloudgis.core.exception.UnauthorizedException;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.feature.AttrTypeDbEntity;
import org.kloudgis.core.persistence.feature.FeatureTypeDbEntity;
import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.pojo.PojoFactory;
import org.kloudgis.core.pojo.feature.AttrType;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/attrtypes")
@Produces({"application/json"})
public class AttrTypeResourceBean {

    @GET
    @Produces({"application/json"})
    public List<AttrType> getAttrTypes(@Context HttpServletRequest req) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        GroupDbEntity group = org.kloudgis.core.SecurityManager.getLoggedGroup(em, req);
        if (group == null) {
            throw new UnauthorizedException();
        }
        List<FeatureTypeDbEntity> lstDb = em.createNamedQuery("FeatureType.findAll").getResultList();
        List<AttrType> lstAT = new ArrayList();
        for (FeatureTypeDbEntity fDb : lstDb) {
            List<AttrTypeDbEntity> lstATDb = fDb.getAttrTypes(group, em);
            lstAT.addAll(PojoFactory.toAttrType(lstATDb));
        }
        em.close();
        return lstAT;
    }

    @GET
    @Path("{fId}")
    @Produces({"application/json"})
    public AttrType getFeature(@PathParam("fId") Long fId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        AttrTypeDbEntity fDb = em.find(AttrTypeDbEntity.class, fId);
        if (fDb != null) {
            AttrType f = fDb.toPojo();
            em.close();
            return f;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + fId);
    }

}
