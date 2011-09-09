/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.kloudgis.core.pojo.security.ErrorMessage;

/**
 *
 * @author jeanfelixg
 */
@Provider
public class EntityNotFoundMapper implements
        ExceptionMapper<javax.persistence.EntityNotFoundException> {
    public Response toResponse(javax.persistence.EntityNotFoundException ex) {
        //cannot use 401 (NOT_FOUND) because sproutcore doesnt like it.
        return Response.status(Status.GONE).
            entity(new ErrorMessage(ex.getMessage(), "NOT_FOUND"))
            .type("application/json").
            build();
    }
}

