/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.map.bean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author jeanfelixg
 */
@Path("/public")
public class SecurityResourceBean {
    
    
    
    @Path("login")
    @POST
    public Response login(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) {
        org.kloudgis.map.SecurityManager.getInstance().logout(req);
        if(org.kloudgis.map.SecurityManager.getInstance().login(req, auth_token, sandbox)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    
    /**
     * Logout the current user
     * @param req
     * @return 
     */
    @Path("logout")
    @POST
    public Response logout(@Context HttpServletRequest req) {
        org.kloudgis.map.SecurityManager.getInstance().logout(req);
        return Response.ok().build();
    }

}
