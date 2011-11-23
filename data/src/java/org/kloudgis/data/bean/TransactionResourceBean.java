/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.core.pojo.SignupUser;
import org.kloudgis.data.AuthorizationFactory;
import org.kloudgis.data.KGConfig;
import org.kloudgis.data.NotificationFactory;
import org.kloudgis.data.persistence.PersistenceManager;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.data.store.TransactionDbEntity;
import org.kloudgis.web_space.pojo.Message;
import org.kloudgis.web_space.pojo.Transaction;
import org.kloudgis.web_space.pojo.TransactionSummary;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/transactions")
@Produces({"application/json"})
public class TransactionResourceBean {

    @POST
    public Response postTransaction(@Context HttpServletRequest req, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @QueryParam("sandbox") String sandbox, Transaction trx) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(sandbox);
        if (em != null) {
            HttpSession session = req.getSession(true);
            MemberDbEntity lMember = null;
            try {
                lMember = AuthorizationFactory.getMember(session, em, sandbox, auth_token);
            } catch (IOException ex) {
                em.close();
                return Response.serverError().entity(ex.getMessage()).build();
            }
            if (lMember != null) {               
                em.getTransaction().begin();
                TransactionDbEntity entity = new TransactionDbEntity();
                entity.fromPojo(trx, em, true);
                em.persist(entity);
                em.getTransaction().commit();
                int iNotifyStatus;
                try {
                    SignupUser user = ApiFactory.getUser(session, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
                    ObjectMapper mapper = new ObjectMapper();
                    iNotifyStatus = NotificationFactory.postNotification(sandbox, "trx", mapper.writeValueAsString(new Message(entity.toPojo().toJSON(), "trx", user != null ? user.user : "?")), auth_token);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    iNotifyStatus = 555;
                }
                return Response.ok(new TransactionSummary(entity.toPojo(), iNotifyStatus)).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not a member of sandbox: " + sandbox).build();
            }
        } else {
            throw new NotFoundException("Sandbox entity manager not found for:" + sandbox + ".");
        }
    }

    @GET
    public Response getTransactions(@QueryParam("start_id") Long lStartId, @DefaultValue("100") @QueryParam("max_rows") int iMax) {
        
        //todo
        return Response.ok().build();
    }
}
