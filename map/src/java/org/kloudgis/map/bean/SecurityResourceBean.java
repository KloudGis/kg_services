/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.map.bean;

import javax.servlet.ServletContext;
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
    public Response login(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox) {
        org.kloudgis.map.SecurityManager.getInstance().logout(sContext, auth_token);
        if(org.kloudgis.map.SecurityManager.getInstance().login(sContext, auth_token, sandbox)){
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
    public Response logout(@Context ServletContext sContext, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token) {
        org.kloudgis.map.SecurityManager.getInstance().logout(sContext, auth_token);
        return Response.ok().build();
    }

}
