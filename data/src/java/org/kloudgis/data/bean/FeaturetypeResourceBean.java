/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import org.kloudgis.data.pojo.Featuretype;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.MemberDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/featuretypes")
@Produces({"application/json"})
public class FeaturetypeResourceBean {
    
    
    
    @GET
    public Response getFeaturetypes(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) throws NotFoundException {
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
                List<FeatureTypeDbEntity> lstDb = em.createQuery("from FeatureTypeDbEntity", FeatureTypeDbEntity.class).getResultList();
                List<Featuretype> lstFT = new ArrayList(lstDb.size());
                for (FeatureTypeDbEntity fDb : lstDb) {
                    Featuretype pojo = fDb.toPojo();
                    lstFT.add(pojo);
                }
                em.close();
                Records rec = new Records();
                rec.records = lstFT;
                return Response.ok(rec).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
    
    
    @GET
    @Path("{ftId}")
    public Response getFeaturetype(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, @PathParam("ftId") Long ftId) throws NotFoundException {
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
                FeatureTypeDbEntity ftDb = em.find(FeatureTypeDbEntity.class, ftId);
                Featuretype pojo = null;
                if (ftDb != null) {
                    pojo = ftDb.toPojo();
                } else {
                    em.close();
                    throw new NotFoundException("Featuretype " + ftId + " is not found in sandbox " + sandbox + ".");
                }
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
