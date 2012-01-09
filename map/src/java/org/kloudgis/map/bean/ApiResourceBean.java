/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.map.bean;

import java.util.List;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.kloudgis.map.KGConfig;
import org.kloudgis.map.geoserver.GeoserverFactory;

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
            return Response.serverError().entity("Geoserver: " + ex.getMessage()).build();
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
            return Response.serverError().entity("Geoserver: " + ex.getMessage()).build();
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
            GeoserverFactory.addLayer(KGConfig.getConfiguration().geoserver_url, workspaceName, storeName, layerProp.get("name") + "", layerProp.get("tableName") + "",
                    layerProp.get("minX") + "", layerProp.get("minY") + "", layerProp.get("maxX") + "", layerProp.get("maxY") + "", KGConfig.getGeoserverCredentials());
            String sld = (String) layerProp.get("sld");
            if (sld != null) {
                String styleName = workspaceName + "_" + layerProp.get("name");
                //add workspace name in the svg/img external resource path
                //set an absolute path because relative path does not work (error on geoserver version 2.1.0)
                String absolutePath = "file://" + KGConfig.getConfiguration().kloudgis_folder;
                String svg = "<OnlineResource xlink:type=\"simple\" xlink:href=\"svg";
                sld = sld.replace(svg, svg.substring(0, svg.length() - 3) + absolutePath + "/svg/" + workspaceName);
                String img = "<OnlineResource xlink:type=\"simple\" xlink:href=\"img";
                sld = sld.replace(img, img.substring(0, img.length() - 3) + absolutePath + "/img/" + workspaceName);

                GeoserverFactory.uploadStyle(KGConfig.getConfiguration().geoserver_url, sld, styleName, KGConfig.getGeoserverCredentials());
                GeoserverFactory.assignStyle(KGConfig.getConfiguration().geoserver_url, workspaceName, layerProp.get("name") + "", styleName, KGConfig.getGeoserverCredentials());
            }
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity("Geoserver: " + ex.getMessage()).build();
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
            return Response.serverError().entity("Geoserver: " + ex.getMessage()).build();
        }
    }

    @POST
    @Path("{workspace}/group_layers")
    public Response addGroupLayer(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, @PathParam("workspace") String workspaceName, List<String> layNames) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        try {
            GeoserverFactory.addGroupLayer(KGConfig.getConfiguration().geoserver_url, workspaceName, layNames, KGConfig.getGeoserverCredentials());
            GeoserverFactory.gwcLayer(KGConfig.getConfiguration().gwc_url, KGConfig.getConfiguration().geoserver_url, workspaceName, KGConfig.getGwcCredentials());
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity("Geoserver: " + ex.getMessage()).build();
        }
    }

    @POST
    @Path("truncate_cache")
    public Response truncateCacheAt(@HeaderParam(value = "X-Kloudgis-Api-Key") String api_key, Map<String, Object> mapTruncate) {
        if (api_key == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key is mandatory.").build();
        }
        if (!api_key.equals(KGConfig.getConfiguration().api_key)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Api Key doesn't match.").build();
        }
        
        Double d1 = (Double) mapTruncate.get("low_x");
        Double d2 = (Double) mapTruncate.get("low_y");
        Double d3 = (Double) mapTruncate.get("hi_x");
        Double d4 = (Double) mapTruncate.get("hi_y");
        
        String layer = (String) mapTruncate.get("layer");
        if(d1 == null || d2 == null || d3 == null || d4 == null || layer == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Bounds(4) and layer are mandatory.").build();
        }
        double[] bounds = new double[]{d1,d2,d3,d4};
        try {
            GeoserverFactory.gwcSeedReq(KGConfig.getConfiguration().gwc_url, layer, "truncate", 1, 18, bounds, KGConfig.getGwcCredentials());
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity("Geoserver: " + ex.getMessage()).build();
        }
    }
}
