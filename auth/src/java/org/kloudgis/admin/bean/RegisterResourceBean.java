/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.kloudgis.LoginFactory;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.UserDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Path("/public/register")
@Produces({"application/json"})
public class RegisterResourceBean {
 
    
     /**
     * Test if an email if valid
     * @param email
     * @return "Accepted" if OK, message otherwise
     */
    @Path("test_email")
    @POST 
    public Response testEmail(String email) {
        Message message = new Message();
        if (email == null || email.length() == 0) {       
            message.content = "_Empty";           
        }else if (!email.contains("@")) {
            message.content = "_invalid";
        } else if (email.equals("admin@kloudgis.org")) {
            message.content = "_Reserved";
        } else if (!LoginFactory.isUnique(email)) {
            message.content = "_InUse";
        }else{
            message.content = "Accepted";
        }
        return Response.ok(message).build();
    }
    
     /**
     * Add a new user
     * @param user_try  the new user properties
     * @return  message: success or rejected
     */
    @POST
    public Response register(SignupUser user_try) {
        return Response.ok(LoginFactory.register(user_try, UserDbEntity.ROLE_USER)).build();
    }
}
