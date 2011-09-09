/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.resources.feature;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.kloudgis.core.exception.UnauthorizedException;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.feature.AttrTypeDbEntity;
import org.kloudgis.core.persistence.feature.FeatureTypeDbEntity;
import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.pojo.PojoFactory;
import org.kloudgis.core.pojo.feature.AttrType;
import org.kloudgis.core.pojo.feature.FeatureType;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/featuretypes")
@Produces({"application/json"})
public class FeatureTypeResourceBean {

    @GET
    @Produces({"application/json"})
    public List<FeatureType> getFeatureTypes(@Context HttpServletRequest req) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        GroupDbEntity group = org.kloudgis.core.SecurityManager.getLoggedGroup(em, req);
        if (group == null) {
            throw new UnauthorizedException();
        }
        List<FeatureTypeDbEntity> lstDb = em.createNamedQuery("FeatureType.findAll").getResultList();
        List<FeatureType> lstFT = new ArrayList(lstDb.size());
        for (FeatureTypeDbEntity fDb : lstDb) {
            lstFT.add(fDb.toPojo(group, em));
        }
        em.close();
        return lstFT;
    }

    @GET
    @Path("{ftid}/columns")
    @Produces({"application/json"})
    public List<AttrType> getAttrTypes(@PathParam("ftid") Integer ftid, @Context HttpServletRequest req) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        Query query = em.createQuery("SELECT f from FeatureTypeDbEntity f where f.id = :id").setParameter("id", ftid);
        try {
            FeatureTypeDbEntity ftDb = (FeatureTypeDbEntity) query.getSingleResult();
            if (ftDb != null) {
                GroupDbEntity group = org.kloudgis.core.SecurityManager.getLoggedGroup(em, req);
                if (group == null) {
                    throw new UnauthorizedException();
                }
                List<AttrTypeDbEntity> lstC = ftDb.getAttrTypes(group, em);
                List<AttrType> lstCI = PojoFactory.toAttrType(lstC);
                em.close();
                return lstCI;
            }
        } catch (NoResultException e) {
        }
        em.close();
        throw new EntityNotFoundException(ftid + " is not found");
    }
}
