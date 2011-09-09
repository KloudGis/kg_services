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
public class UnauthorizedException extends WebApplicationException {

    /**
     * Create a HTTP 401 (Unauthorized) exception.
     */
    public UnauthorizedException() {
        this("Unauthorized");
    }

    /**
     * Create a HTTP 401 (Unauthorized) exception.
     */
    public UnauthorizedException(String message) {
        super(Response.status(Status.UNAUTHORIZED).
                entity(new ErrorMessage(message, "UNAUTHORIZED")).type("application/json").build());
    }

}