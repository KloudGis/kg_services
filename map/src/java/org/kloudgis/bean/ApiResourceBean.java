/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.bean;

import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.kloudgis.KGConfig;
import org.kloudgis.geoserver.GeoserverFactory;

/**
 *
 * @author jeanfelixg
 */
@Path("/api")
public class ApiResourceBean {

    @POST
    @Path("workspace")
    public Response addWorkspace(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, String workspaceName) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            GeoserverFactory.addWorkspace(KGConfig.getConfiguration().geoserver_url, workspaceName, KGConfig.getGeoserverCredentials());
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @POST
    @Path("{workspace}/store")
    public Response addStore(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @PathParam("workspace") String workspaceName, Map storeProp) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            GeoserverFactory.addStore(KGConfig.getConfiguration().geoserver_url, workspaceName, storeProp.get("name") + "", storeProp.get("user") + "", storeProp.get("pwd") + "", storeProp.get("host") + "", storeProp.get("port") + "", KGConfig.getGeoserverCredentials());
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @POST
    @Path("{workspace}/{store}/layer")
    public Response addLayer(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @PathParam("workspace") String workspaceName, @PathParam("store") String storeName, Map layerProp) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            GeoserverFactory.addLayer(KGConfig.getConfiguration().geoserver_url, workspaceName, storeName, layerProp.get("name") + "", layerProp.get("tableName")  + "", 
                    layerProp.get("minX")  + "", layerProp.get("minY")  + "",layerProp.get("maxX")  + "", layerProp.get("maxY") + "",  KGConfig.getGeoserverCredentials());
            String sld = (String) layerProp.get("sld");
            if(sld != null){
                String styleName = workspaceName + "_" + layerProp.get("name");
                GeoserverFactory.uploadStyle(KGConfig.getConfiguration().geoserver_url, sld, styleName,  KGConfig.getGeoserverCredentials());
                GeoserverFactory.assignStyle(KGConfig.getConfiguration().geoserver_url, workspaceName, layerProp.get("name") + "", styleName, KGConfig.getGeoserverCredentials());
            }
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("workspace/{workspace}")
    public Response deleteWorkspace(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @PathParam("workspace") String workspaceName) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            GeoserverFactory.deleteWorkspace(KGConfig.getConfiguration().geoserver_url, workspaceName, KGConfig.getGeoserverCredentials());
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}
