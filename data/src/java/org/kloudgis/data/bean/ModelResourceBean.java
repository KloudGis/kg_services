/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.io.IOException;
import javassist.NotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationFactory;
import org.kloudgis.data.pojo.AttrType;
import org.kloudgis.data.pojo.LoadFeatureType;
import org.kloudgis.data.store.AttrTypeDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/model")
@Produces({"application/json"})
public class ModelResourceBean {

    @POST
    @Path("featuretype")
    public Response addFeaturetype(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, LoadFeatureType pojo) throws NotFoundException {
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
            boolean bOwner = false;
            try {
                if (lMember != null) {
                    bOwner = AuthorizationFactory.isSandboxOwner(lMember, session, auth_token, sandbox);
                }
            } catch (IOException ex) {
            }
            if (bOwner) {
                em.getTransaction().begin();
                FeatureTypeDbEntity entity = new FeatureTypeDbEntity();
                entity.fromLoadPojo(pojo);
                em.persist(entity);
                if (pojo.attrs != null) {
                    for (AttrType atPojo : pojo.attrs) {
                        AttrTypeDbEntity entityAt = new AttrTypeDbEntity();
                        entityAt.fromPojo(atPojo);
                        entityAt.setFeaturetype(entity);
                        em.persist(entityAt);
                    }
                }
                em.getTransaction().commit();
                em.close();
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}
