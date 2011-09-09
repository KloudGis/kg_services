/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.kloudgis.core.pojo.security.ErrorMessage;

/**
 *
 * @author jeanfelixg
 */
public class ServerErrorException extends WebApplicationException {

    /**
     * Create a HTTP 401 (Unauthorized) exception.
     */
    public ServerErrorException(String message) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).
                entity(new ErrorMessage(message, "ERROR")).type("application/json").build());
    }

}