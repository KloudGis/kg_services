/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.kloudgis.data.pojo.Layer;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.persistence.PersistenceManager;
import org.kloudgis.pojo.Records;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/layers")
@Produces({"application/json"})
public class LayerResourceBean {

    @GET
    public Response getLayers(@QueryParam("sandbox") String sandbox) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            List<LayerDbEntity> lstDb = em.createQuery("from LayerDbEntity", LayerDbEntity.class).getResultList();
            List<Layer> lstFT = new ArrayList(lstDb.size());
            for (LayerDbEntity fDb : lstDb) {
                Layer pojo = fDb.toPojo(em);
                lstFT.add(pojo);
            }
            em.close();
            Records rec = new Records();
            rec.records = lstFT;
            return Response.ok(rec).build();
        }else{
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
    
    @GET
    @Path("{layerId}")
    public Response getLayer(@QueryParam("sandbox") String sandbox, @PathParam("layerId") Long layerId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            LayerDbEntity layDb = em.find(LayerDbEntity.class, layerId);
            Layer pojo = null;
            if(layDb != null){
                pojo = layDb.toPojo(em);
            }else{
                em.close();
                throw new NotFoundException("Layer " + layerId + " is not found in sandbox " + sandbox + ".");
            }
            em.close();
            return Response.ok(pojo).build();
        }else{
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }
}
