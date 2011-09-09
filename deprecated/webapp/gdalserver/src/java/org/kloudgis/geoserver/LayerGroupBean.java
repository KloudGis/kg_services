/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import javax.persistence.EntityExistsException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for managing layer groups.
 */
@Path( "/protected/layergroup" )
@Produces( { "application/json" } )
public class LayerGroupBean {
    
    public LayerGroupBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * add a layer group
     * @param lgr is the layer group being added
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @POST
    @Produces( { "application/json" } )
    public Response addLayerGroup( LayerGroup lgr ) throws MalformedURLException, IOException {
        if( lgr == null || lgr.strName == null || lgr.strName.length() <= 0 || lgr.arlLayers == null || lgr.arlLayers.size() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid layer group parameter(s) passed." ) );
        }
        URL url = new URL( Utils.URL + "layergroups" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestProperty("Content-type", "text/xml");
        httpCon.setRequestMethod( "POST" );
        httpCon.connect();
        OutputStream ost = httpCon.getOutputStream();
        StringBuilder stb = new StringBuilder( "<layerGroup><name>" );
        stb.append( lgr.strName );
        stb.append( "</name><layers>" );
        for( String strLayer : lgr.arlLayers ) {
            stb.append( "<layer>" );
            stb.append( strLayer );
            stb.append( "</layer>" );
        }
        stb.append( "</layers></layerGroup>" );
        ost.write( stb.toString().getBytes() );
        ost.flush();
        int iResponse = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        if( iResponse != 201 ) {//layer group has *NOT* been created
            if( getLayerGroup( lgr.strName ) != null ) {//layer group already exists
                throw new WebApplicationException( new EntityExistsException( "Layer group " + lgr.strName + " already exists." ) );
            }
        }
        return Response.status( iResponse ).build();
    }
    
    /**
     * retrieve the layer group info
     * @param strName is the name of the layer group
     * @return the layer group
     * @throws MalformedURLException
     * @throws IOException
     */
    @GET
    @Produces( { "application/json" } )
    public LayerGroup getLayerGroup( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid group name parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "layergroups/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        InputStream ins = httpCon.getInputStream();
        LayerGroup grl = buildLayerGroup( ins );
        int iResponse = httpCon.getResponseCode();
        ins.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return grl;
    }
    
    /**
     * deletes a layer group
     * @param strName is the name of the layer group
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @DELETE
    @Produces( { "application/json" } )
    public Response deleteLayerGroup( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid layer group parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "layergroups/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestMethod( "DELETE" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        return Response.status( iResponse ).build();
    }

    private LayerGroup buildLayerGroup(InputStream ins) {
        Document doc = Utils.getDocument( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                LayerGroup grl = new LayerGroup();
                grl.strName = Utils.getTagValue( "name", eleRoot );
                NodeList lstBounds = eleRoot.getElementsByTagName( "bounds" );
                if( lstBounds != null && lstBounds.getLength() > 0 ) {
                    Node nodBounds = lstBounds.item( 0 );
                    if( nodBounds instanceof Element ) {
                        Element eleBounds = ( Element )nodBounds;
                        NodeList lstMinX = eleBounds.getElementsByTagName( "minx" );
                        if( lstMinX != null && lstMinX.getLength() > 0 ) {
                            grl.dMinX = Utils.parseDouble( lstMinX.item( 0 ).getTextContent() );
                        }
                        NodeList lstMinY = eleBounds.getElementsByTagName( "miny" );
                        if( lstMinY != null && lstMinY.getLength() > 0 ) {
                            grl.dMinY = Utils.parseDouble( lstMinY.item( 0 ).getTextContent() );
                        }
                        NodeList lstMaxX = eleBounds.getElementsByTagName( "maxx" );
                        if( lstMaxX != null && lstMaxX.getLength() > 0 ) {
                            grl.dMaxX = Utils.parseDouble( lstMaxX.item( 0 ).getTextContent() );
                        }
                        NodeList lstMaxY = eleBounds.getElementsByTagName( "maxy" );
                        if( lstMaxY != null && lstMaxY.getLength() > 0 ) {
                            grl.dMaxY = Utils.parseDouble( lstMaxY.item( 0 ).getTextContent() );
                        }
                        NodeList lstCRS = eleBounds.getElementsByTagName( "crs" );
                        if( lstCRS != null && lstCRS.getLength() > 0 ) {
                            grl.strCRS = lstCRS.item( 0 ).getTextContent();
                        }
                    }
                }
                ArrayList<String> arlLayers = new ArrayList<String>();
                NodeList lst = eleRoot.getElementsByTagName( "layers" );
                if( lst != null && lst.getLength() > 0 ) {
                    NodeList lstLayers = lst.item( 0 ).getChildNodes();
                    if( lstLayers != null ) {
                        for( int i = 0; i < lstLayers.getLength(); i++ ) {
                            Node nod = lstLayers.item( i );
                            if( nod instanceof Element ) {
                                NodeList lstName = ( ( Element )nod ).getElementsByTagName( "name" );
                                if( lstName != null && lstName.getLength() > 0 ) {
                                    arlLayers.add( lstName.item( 0 ).getTextContent() );
                                }
                            }
                        }
                    }
                }
                grl.arlLayers = arlLayers;
                return grl;
            }
        }
        return null;
    }
}