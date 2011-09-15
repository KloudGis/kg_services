/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
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
    public Response logout(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return Response.ok().build();
    }

}
