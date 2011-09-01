/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.kloudgis.synch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Simple PubSub resource that demonstrate many functionality supported by
 * Atmosphere.
 *
 * @author Jeanfrancois Arcand
 */
@Path("/{topic}")
@Consumes({"application/json"})
@Produces({"application/json"})
public class MessageResourceBean {

    private static final Logger logger = LoggerFactory.getLogger(MessageResourceBean.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private Configuration configuration;
    private HttpClient httpClient;
    private static final String KG_TIMEOUT = "!kg_notify_timeout!";

    @PreDestroy
    public void destroy() {
        logger.info("Testing the @PreDestroy");
    }
    /**
     * Inject a {@link Broadcaster} based on @Path
     */
    private @PathParam("topic")
    Broadcaster topic;
    private @PathParam("topic")
    String topic_str;

    private void readConfig(ServletContext context) {
        if (configuration == null) {
            InputStream in = context.getResourceAsStream("/META-INF/configuration.json");
            ObjectMapper mapper = new ObjectMapper();
            try {
                configuration = mapper.readValue(in, Configuration.class);
                System.out.println(configuration);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean securityCheck(String auth_token, HttpServletRequest request, ServletContext context) {
        HttpSession session = request.getSession(true);
        Long time = Calendar.getInstance().getTimeInMillis();
        Number timeout = (Number) session.getAttribute(KG_TIMEOUT);
        if (auth_token != null && timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 60000L) > time)) {
            //ok
            System.out.println("Auth token still valid, " + (time - timeout.longValue()) / 1000 + " sec.");
            return true;
        } else {
            System.out.println("Revalidate Auth token");
            boolean valid = authenticate(auth_token, context);
            if (valid) {
                session.setAttribute(KG_TIMEOUT, Calendar.getInstance().getTimeInMillis());
            }
            return valid;
        }
    }

    private boolean authenticate(String auth_token, ServletContext context) {
        readConfig(context);
        if (configuration != null && auth_token != null) {

            if (httpClient == null) {
                httpClient = new HttpClient();
            }
            String url = (String) configuration.getServerURL() + "/public/notification_check?sandbox=" + topic_str;
            GetMethod getUsr = new GetMethod(url);
            getUsr.addRequestHeader("Cookie", "security-Kloudgis.org=" + auth_token);
            try {
                int status = httpClient.executeMethod(getUsr);
                if (status == 200) {
                    String response = getUsr.getResponseBodyAsString(100);
                    if (response.equals("Notification-Access-Granted")) {
                        return true;
                    }
                }
            } catch (IOException ex) {
                getUsr.releaseConnection();
            }
        }
        return false;
    }

    /**
     * Suspend the response, and register a {@link AtmosphereResourceEventListener}
     * that get notified when events occurs like client disconnection, broadcast
     * or when the response get resumed.
     *
     * @return A {@link Broadcastable} used to broadcast events.
     */
    @GET
    @Suspend(listeners = {EventsLogger.class})
    public Broadcastable subscribe() {
        return new Broadcastable(topic);
    }

    
    //TEST FOR SECURITY -- TO REMOVE
    @GET
    @Path("test")
    @Suspend(listeners = {EventsLogger.class})
    public Broadcastable subscribeT(@CookieParam(value = "security-Kloudgis.org") String strAuthToken, @Context ServletContext context, @Context HttpServletRequest request) {
        if (securityCheck(strAuthToken, request, context)) {
            return new Broadcastable(topic);
        } else {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }

    /**
     * Suspend the response, and tell the framework to resume the response                                                                                                                     \
     * when the first @Broadcast operation occurs.
     *
     * @return A {@link Broadcastable} used to broadcast events.
     */
    @GET
    @Suspend(resumeOnBroadcast = true, listeners = {EventsLogger.class})
    @Path("subscribeAndResume")
    public Broadcastable subscribeAndResume() {
        return new Broadcastable(topic);
    }

    /**
     * '
     * Broadcast XML data using JAXB
     *
     * @param message A String from an HTML form
     * @return A {@link Broadcastable} used to broadcast events.
     */
    /*  @POST
    @Broadcast
    public Broadcastable publishWithPojo(Transaction message) {
    return new Broadcastable(message, topic);
    }*/
    @POST
    @Broadcast
    public Broadcastable publishTransaction(String message) {
        return broadcast(message, "trx");
    }

    @POST
    @Path("connected")
    @Broadcast
    public Broadcastable publishWelcome(String user) {
        return broadcast(user, "connected");
    }

    @POST
    @Path("disconnected")
    @Broadcast
    public Broadcastable publishGoodbye(String user) {
        return broadcast(user, "disconnected");
    }

    @POST
    @Path("query_connected")
    @Broadcast
    public Broadcastable publishQueryConnected() {
        return broadcast(null, "query_connected");
    }

    @POST
    @Path("resume")
    @Broadcast(resumeOnBroadcast = true)
    public Broadcastable publishResume() {
        return broadcast(null, "resume");
    }

    /**
     * Broadcast message to this server and also to other server using JGroups
     *
     * @param message A String from an HTML form
     * @return A {@link Broadcastable} used to broadcast events.
     */
    /*    @POST
    @Broadcast
    /* @Cluster(
    name="chat",
    value= JGroupsFilter.class
    ) */
    /*   public Broadcastable publish(@QueryParam("message") String message) {
    return broadcast(message);
    }*/
    /**
     * Execute periodic {@link Broadcaster#broadcast(java.lang.Object)} operation and
     * resume the suspended connection after the first broadcast operation.
     *
     * @param message A String from an HTML form
     * @return A {@link Broadcastable} used to broadcast events.
     */
    /* @Schedule(period = 5, resumeOnBroadcast = true)
    @POST
    @Path("scheduleAndResume")
    public Broadcastable scheduleAndResume(@QueryParam("message") String message) {
    return broadcast(message);
    }
     */
    /**
     * Wait 5 seconds and then execute periodic {@link Broadcaster#broadcast(java.lang.Object)}
     * operations.
     *
     * @param message A String from an HTML form
     * @return A {@link Broadcastable} used to broadcast events.
     */
    /*  @Schedule(period = 10, waitFor = 5)
    @POST
    @Path("delaySchedule")
    public Broadcastable delaySchedule(@QueryParam("message") String message) {
    return broadcast(message);
    }
     */
    /**
     * Execute periodic {@link Broadcaster#broadcast(java.lang.Object)} operation.
     *
     * @param message A String from an HTML form
     * @return A {@link Broadcastable} used to broadcast events.
     */
    /*   @Schedule(period = 5)
    @POST
    @Path("schedule")
    public Broadcastable schedule(@QueryParam("message") String message) {
    return broadcast(message);
    }
     */
    /**
     * Create a new {@link Broadcastable}.
     *
     * @param m
     * @return
     */
    Broadcastable broadcast(String content, String type) {
        try {
            String json = mapper.writeValueAsString(new Message(content, type));
            return new Broadcastable(json, topic);
        } catch (Exception ex) {
        }
        return new Broadcastable("***error", topic);
    }
}
