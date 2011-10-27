/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import javax.ws.rs.WebApplicationException;

public class GeoserverException extends WebApplicationException {


    public GeoserverException( int iResponseCode, String strResponse ) {
        super( new Throwable("Geoserver error: "  +strResponse), iResponseCode);
    }
}