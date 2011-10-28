/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.map.bean;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.kloudgis.map.KGConfig;
import org.kloudgis.core.utils.FileUpload;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/graphics")
public class GraphicsResourceBean {
 
    
    @POST
    @Path("{file_type}")
    public Response addSvgFile(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @PathParam(value = "file_type") String fileType, @QueryParam("sandbox") String sandbox){
        if(sandbox == null){
            return Response.notModified("Miss the sandbox name").build();
        }
        String strFolder = KGConfig.getConfiguration().kloudgis_folder + "/" + fileType + "/" + sandbox;
        File fileParent = new File(strFolder);
        fileParent.mkdirs();
        try {
            FileUpload.processUpload(req, fileParent);
        } catch (Exception ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
        return Response.ok().build();
    }
}
