/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.auth.admin.bean;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author jeanfelixg
 */
@Path("/public")
public class SecurityResourceBean {
    
    
    /**
     * Logout the current user
     * @param req
     * @return 
     */
    @Path("logout")
    @POST
    public Response logout() {
        return Response.ok().build();
    }

}
