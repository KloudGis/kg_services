/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.util.List;
import javassist.NotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.KGConfig;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
public class ApiResourceBean {

    @Path("map_access")
    @GET
    public Response hasMapAccess(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam ("sandbox") String sandbox, @QueryParam ("user_id") Long user_id) throws NotFoundException {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        boolean bAccess = false;
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            List<LayerDbEntity> lstDb = em.getSession().createCriteria(MemberDbEntity.class).add(Restrictions.eq("user_id", user_id)).list();
            if(lstDb.size() > 0){
                bAccess = true;
            }
            return Response.ok(bAccess + "").build();
        }else{
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }           
    }
}
