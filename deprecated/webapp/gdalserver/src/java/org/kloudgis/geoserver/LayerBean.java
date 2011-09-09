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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for managing layers.
 */
@Path( "/protected/layer" )
@Produces( { "application/json" } )
public class LayerBean {
    
    public LayerBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * adds a layer
     * @param lay is the layer to be added
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @POST
    @Produces( { "application/json" } )
    public Response addLayer( Layer lay ) throws MalformedURLException, IOException {
        if( lay == null || lay.strName == null || lay.strName.length() <= 0 || 
                lay.strWorkspace == null || lay.strWorkspace.length() <= 0 || 
                lay.strDatastore == null || lay.strDatastore.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid layer parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "workspaces/" + lay.strWorkspace + "/datastores/" + lay.strDatastore + "/featuretypes" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestProperty( "Content-type", "text/xml" );
        httpCon.setRequestMethod( "POST" );
        httpCon.connect();
        OutputStream ost = httpCon.getOutputStream();
        ost.write( ( "<featureType><name>" + lay.strName + "</name></featureType>" ).getBytes() );
        ost.flush();
        int iResponse = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        if( iResponse != 201 ) {//layer has NOT been created
            if( getLayer( lay.strName ) != null ) {//layer already exists
                throw new WebApplicationException( new EntityExistsException( "Layer " + lay.strName + " already exists." ) );
            }
        }
        return Response.status( iResponse ).build();
    }
    
    /**
     * retrieves the layer info
     * @param strName is the name of the layer
     * @return the layer
     * @throws MalformedURLException
     * @throws IOException 
     */
    @GET
    @Produces( { "application/json" } )
    public Layer getLayer( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Did not receive a valid layer name. Name was: " + strName ) );
        }
        URL url = new URL( Utils.URL + "layers/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        InputStream ins = httpCon.getInputStream();
        Layer lay = buildLayer( ins );
        int iResponse = httpCon.getResponseCode();
        ins.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return lay;
    }
    
    /**
     * deletes all layer groups that have this layer as member then deletes the layer itself and the feature type at the very end
     * @param strName is the name of the layer
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    @DELETE
    @Produces( { "application/json" } )
    public Response deleteLayer( String strName )  throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Did not receive a valid layer name. Name was: " + strName ) );
        }
        WorkspaceInfo wki = new WorkspaceInfo();
        LayerBean lbn = new LayerBean();
        Layer lay = lbn.getLayer( strName );//get the layer object
        if( lay != null ) {
            wki.strWorkspaceName = lay.strWorkspace;
            wki.strCategory = "layergroups";
            ArrayList<String> arlLG = new WorkspaceInfoBean().getWorkspaceInfo( wki );//get all layer groups for the workspace where the layer is
            if( arlLG != null ) {
                LayerGroupBean lgb = new LayerGroupBean();
                for( String strLG : arlLG ) {
                    LayerGroup lgr = lgb.getLayerGroup( strLG );
                    if( lgr != null && lgr.arlLayers != null && lgr.arlLayers.contains( strName ) ) {//check if the layer group contains the layer
                        lgb.deleteLayerGroup( strLG );
                    }
                }
            }
        }
        URL url = new URL( Utils.URL + "layers/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestMethod( "DELETE" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        FeatureTypeBean ftb = new FeatureTypeBean();
        FeatureType ftp = new FeatureType();
        ftp.strDatastore = lay.strDatastore;
        ftp.strWorkspace = lay.strWorkspace;
        ftp.strName = strName;
        ftb.deleteFeatureType( ftp );//for consistency reasons the related feature type must be deleted as well
        return Response.status( iResponse ).build();
    }
    
    /**
     * assign a style to a layer
     * @param lay is the layer object that contains the name of the style and the name of the layer
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @PUT
    @Produces( { "application/json" } )
    public Response assignStyle( Layer lay ) throws MalformedURLException, IOException {
        if( lay == null || lay.strName == null || lay.strName.length() <= 0 || 
                lay.strStyle == null || lay.strStyle.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid layer parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "layers/" + lay.strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestProperty("Content-type", "text/xml");
        httpCon.setRequestMethod( "PUT" );
        httpCon.connect();
        OutputStream ost = httpCon.getOutputStream();
        String strEnabled = lay.bEnabled ? "true" : "false";
        ost.write( ( "<layer><defaultStyle><name>" + lay.strStyle + "</name></defaultStyle><enabled>" + 
                strEnabled + "</enabled><styles><style><name>" + lay.strStyle + "</name></style></styles></layer>" ).getBytes() );
        ost.flush();
        int iResponse = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return Response.status( iResponse ).build();
    }
    
    private Layer buildLayer( InputStream ins ) {
        Document doc = Utils.getDocument( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                Layer lay = new Layer();
                lay.strName = Utils.getTagValue( "name", eleRoot );
                lay.strType = Utils.getTagValue( "type", eleRoot );
                lay.bEnabled = Utils.parseBoolean( Utils.getTagValue( "enabled", eleRoot ) );
                NodeList lstDefStl = eleRoot.getElementsByTagName( "defaultStyle" );
                if( lstDefStl != null && lstDefStl.getLength() > 0 ) {
                    Node nodDefStl = lstDefStl.item( 0 );
                    if( nodDefStl instanceof Element ) {
                        NodeList lstName = ( ( Element )nodDefStl ).getElementsByTagName( "name" );
                        if( lstName != null && lstName.getLength() > 0 ) {
                            Node nodName = lstName.item( 0 );
                            if( nodName != null ) {
                                lay.strDefaultStyle = nodName.getTextContent();
                            }
                        }
                    }
                }
                NodeList lstResource = eleRoot.getElementsByTagName( "resource" );
                if( lstResource != null && lstResource.getLength() > 0 ) {
                    Node nodResource = lstResource.item( 0 );
                    if( nodResource != null ) {
                        NodeList lstRes = nodResource.getChildNodes();
                        if( lstRes != null ) {
                            for( int i = 0; i < lstRes.getLength(); i++ ) {
                                Node nod = lstRes.item( i );
                                if( nod instanceof Element && nod.hasAttributes() ) {
                                    String strHref = nod.getAttributes().item( 0 ).getTextContent();
                                    if( strHref != null ) {
                                        String[] sta = strHref.split( "/" );
                                        for( int j = 0; j < sta.length; j++ ) {
                                            if( sta[j].equals( "workspaces" ) ) {
                                                lay.strWorkspace = sta[j+1];
                                            } else if( sta[j].equals( "datastores" ) ) {
                                                lay.strDatastore = sta[j+1];
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                return lay;
            }
        }
        return null;
    }
}