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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.jersey.SuspendResponse;
import org.kloudgis.core.api.ApiFactory;
import org.kloudgis.notification.EventsLogger;
import org.kloudgis.notification.KGConfig;

/**
 * Simple PubSub resource that demonstrate many functionality supported by
 * Atmosphere.
 *
 * @author Jeanfrancois Arcand
 */
@Path("/{sandboxKey}/{topic}")
public class MessageResourceBean {

    @PreDestroy
    public void destroy() {
    }
    
    private @PathParam("sandboxKey")
    String sandboxKey;

    private @PathParam("topic")
    String topic;
    
    private String buildBroadcasterId(){
        return sandboxKey + "_" + topic;
    }

    @GET
    public SuspendResponse<String> subscribe(@HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context HttpServletRequest req) {
        Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, buildBroadcasterId(), true);
        if (securityCheck(auth_token, req)) {
            return new SuspendResponse.SuspendResponseBuilder<String>().broadcaster(broadcaster).outputComments(true).addListener(new EventsLogger()).build();
        }
        throw new WebApplicationException(Status.UNAUTHORIZED);
    }

    @POST
    @Broadcast
    public Broadcastable publishMessage(String message, @HeaderParam(value = "X-Kloudgis-Authentication") String auth_token, @Context HttpServletRequest req) {
        Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, buildBroadcasterId(), false);
        if(broadcaster == null){
            //no listeners
            throw new WebApplicationException(Status.NOT_MODIFIED);
        }
        if (securityCheck(auth_token, req)) {            
            return new Broadcastable(message, "", broadcaster);
        }
        throw new WebApplicationException(Status.UNAUTHORIZED);
    }

    /**  resume kills the other streaming connections... DONT USE
    @POST
    @Path("resume")
    @Broadcast(resumeOnBroadcast = true)
    public Broadcastable publishResume() {
    return broadcast("");
    }
    
     **/
    private boolean securityCheck(String auth_token, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        try {
            Long user_id = ApiFactory.getUserId(session, auth_token, KGConfig.getConfiguration().auth_url, KGConfig.getConfiguration().api_key);
            if (user_id != null) {
                String membership = ApiFactory.getMembership(session, auth_token, KGConfig.getConfiguration().data_url + "/membership?sandbox=" + sandboxKey + "&user_id=" + user_id, KGConfig.getConfiguration().api_key);
                return membership != null;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
