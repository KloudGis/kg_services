/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.kloudgis.LoginFactory;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.UserDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/public/register")
public class RegisterResourceBean {
 
    
     /**
     * Test if an email if valid
     * @param email
     * @return "Accepted" if OK, message otherwise
     */
    @Path("test_email/{val}")
    @GET
    @Produces({"application/json"})
    public Response testEmail(@PathParam("val") String email) {
        if (email == null || email.length() == 0) {
            return Response.ok("Not Accepted - Empty").build();
        } else if (email.equals("admin@kloudgis.org")) {
            return Response.ok("Not Accepted - Reserved").build();
        } else if (!LoginFactory.isUnique(email)) {
            return Response.ok("Not Accepted - In use").build();
        }
        return Response.ok("Accepted").build();
    }
    
     /**
     * Add a new user
     * @param user_try  the new user properties
     * @return  message: success or rejected
     */
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response register(SignupUser user_try) {
        return Response.ok(LoginFactory.register(user_try, UserDbEntity.ROLE_USER)).build();
    }
}
