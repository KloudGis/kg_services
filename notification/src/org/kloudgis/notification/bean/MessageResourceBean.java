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
package org.kloudgis.notification.bean;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;

import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.atmosphere.jersey.SuspendResponse;
import org.kloudgis.notification.EventsLogger;

/**
 * Simple PubSub resource that demonstrate many functionality supported by
 * Atmosphere.
 *
 * @author Jeanfrancois Arcand
 */
@Path("/{topic}")
public class MessageResourceBean {

    @PreDestroy
    public void destroy() {
    }
    
    /**
     * Inject a {@link Broadcaster} based on @Path
     */
    private @PathParam("topic")
    Broadcaster topic;
    private @PathParam("topic")
    String topic_str;

    @GET
    public SuspendResponse<String> subscribe() {
        return new SuspendResponse.SuspendResponseBuilder<String>()
                .broadcaster(topic)
                .outputComments(true)
                .addListener(new EventsLogger())
                .build();
    }
    
    @GET
    @Path("{tst2}")
    public SuspendResponse<String> subscribe2() {
        return new SuspendResponse.SuspendResponseBuilder<String>()
                .broadcaster(topic)
                .outputComments(true)
                .addListener(new EventsLogger())
                .build();
    }
    
    @POST
    @Broadcast
    public Broadcastable publishMessage(String message) {
        return broadcast(message);
    }

    
    @POST
    @Path("resume")
    @Broadcast(resumeOnBroadcast = true)
    public Broadcastable publishResume() {
        return broadcast("");
    }

  
    Broadcastable broadcast(String content) {
        try {
            return new Broadcastable(content, "", topic);
        } catch (Exception ex) {
        }
        return new Broadcastable("***error", topic);
    }
    
     /*
    private void readConfig(ServletContext context) {
        if (configuration == null) {
            InputStream in = context.getResourceAsStream("/META-INF/configuration.json");
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
        return true;
        readConfig(context);
        if (configuration != null && auth_token != null) {

            if (httpClient == null) {
                httpClient = new HttpClient();
            }
            String url = (String) configuration.auth_url + "/public/notification_check?sandbox=" + topic_str;
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
*/
}
