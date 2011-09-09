/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class for retrieving a list of all layers that belong to a datastore.
 */
@Path( "/protected/storeinfo" )
@Produces( { "application/json" } )
public class StoreInfoBean {
    
    public StoreInfoBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * @param strName is the name of the datastore
     * @return a list of all layers that belong to the datastore received as parameter
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    @GET
    @Produces( { "application/json" } )
    public ArrayList<String> getLayers( String strName ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid type parameter passed." ) );
        }
        ArrayList<String> arl = null;
        ArrayList<String> arlLayers = new GeoserverInfoBean().getNames( "layers" );//get all layers
        if( arlLayers != null ) {
            LayerBean lbn = new LayerBean();
            arl = new ArrayList<String>();
            for( String strLayer : arlLayers ) {
                Layer lay = lbn.getLayer( strLayer );
                if( lay != null && lay.strDatastore != null && lay.strDatastore.equals( strName ) ) {//select the layers that belong to the datastore
                    arl.add( strLayer );
                }
            }
        }
        return arl;
    }
}