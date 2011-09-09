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
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for managing datastores.
 */
@Path( "/protected/store" )
@Produces( { "application/json" } )
public class StoreBean {
    
    public StoreBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * adds a datastore
     * @param str is the datastore object
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @POST
    @Produces( { "application/json" } )
    public Response addStore( Store str ) throws MalformedURLException, IOException {
        if( str == null || str.strName == null || str.strName.length() <= 0 || str.strHost == null || str.strHost.length() <= 0 ||
                str.strPort == null || str.strPort.length() <= 0 || str.strDatabase == null || str.strDatabase.length() <= 0 || 
                str.strUser == null || str.strUser.length() <= 0 || str.strPassword == null || str.strPassword.length() <= 0 ||
                str.strDbType == null || str.strDbType.length() <= 0 || str.strNamespace == null || str.strNamespace.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid store parameter passed." ) );
        }
        String xml = "<dataStore><name>" + str.strName + "</name><enabled>true</enabled><connectionParameters><host>" + 
                str.strHost + "</host><port>" + str.strPort + "</port><database>" + str.strDatabase + 
                "</database><user>" + str.strUser + "</user><passwd>" + str.strPassword + "</passwd><dbtype>" + 
                str.strDbType + "</dbtype><namespace>" + str.strNamespace + "</namespace></connectionParameters></dataStore>";
        URL url = new URL( Utils.URL + "workspaces/" + str.strNamespace + "/datastores" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestProperty("Content-type", "text/xml");
        httpCon.setRequestMethod( "POST" );
        httpCon.connect();
        OutputStream ost = httpCon.getOutputStream();
        ost.write( xml.getBytes() );
        ost.flush();
        int iResponse = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        if( iResponse != 201 ) {//store has NOT been created
            Store strTemp = getStore( str );
            if( strTemp != null ) {//store already exists
                throw new EntityExistsException( "Store " + str.strName + " already exists." );
            }
        }
        return Response.status( iResponse ).build();
    }
    
    /**
     * retrieves the info for the datastore passed as parameter
     * @param str is the datastore object
     * @return the datastore object
     * @throws MalformedURLException
     * @throws IOException 
     */
    @GET
    @Produces( { "application/json" } )
    public Store getStore( Store str ) throws MalformedURLException, IOException {
        if( str == null || str.strName == null || str.strName.length() <= 0 || str.strNamespace == null || str.strNamespace.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid store parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "workspaces/" + str.strNamespace + "/datastores/" + str.strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        InputStream ins = httpCon.getInputStream();
        Store store = buildStore( ins );
        int iResponse = httpCon.getResponseCode();
        ins.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return store;
    }
    
    /**
     * Deletes all the layers that belong to the datastore passed as parameter then deletes the datastore itself.
     * @param str is the datastore object
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    @DELETE
    @Produces( { "application/json" } )
    public Response deleteStore( Store str ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        if( str == null || str.strName == null || str.strName.length() <= 0 || str.strNamespace == null || str.strNamespace.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid store parameter passed." ) );
        }
        ArrayList<String> arlLayers = new StoreInfoBean().getLayers( str.strName );//get all layers that have this datastore as source
        if( arlLayers != null ) {
            LayerBean lbn = new LayerBean();
            for( String strLayer : arlLayers ) {
                lbn.deleteLayer( strLayer );
            }
        }
        URL url = new URL( Utils.URL + "workspaces/" + str.strNamespace + "/datastores/" + str.strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestMethod( "DELETE" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        return Response.status( iResponse ).build();
    }
    
    private Store buildStore( InputStream ins ) {
        Document doc = Utils.getDocument( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                Store str = new Store();
                str.strName = Utils.getTagValue( "name", eleRoot );
                str.strDbType = Utils.getTagValue( "type", eleRoot );
                str.bEnabled = Utils.parseBoolean( Utils.getTagValue( "enabled", eleRoot ) );
                NodeList lstWS = eleRoot.getElementsByTagName( "workspace" );
                if( lstWS != null && lstWS.getLength() > 0 ) {
                    Element eleWS = ( Element )lstWS.item( 0 );
                    if( eleWS != null ) {
                        NodeList lstName = eleWS.getElementsByTagName( "name" );
                        if( lstName != null && lstName.getLength() > 0 ) {
                            Node nodName = lstName.item( 0 );
                            if( nodName != null ) {
                                str.strNamespace = nodName.getTextContent();
                            }
                        }
                    }
                }
                NodeList lstConn = eleRoot.getElementsByTagName( "connectionParameters" );
                if( lstConn != null && lstConn.getLength() > 0 ) {
                    Node nodConn = lstConn.item( 0 );
                    if( nodConn != null ) {
                        NodeList lst = nodConn.getChildNodes();
                        if( lst != null ) {
                            for( int i = 0; i < lst.getLength(); i++ ) {
                                Node nod = lst.item( i );
                                if( nod instanceof Element ) {
                                    String strKey = ( ( Element )nod ).getAttribute( "key" );
                                    if( strKey.equals( "port" ) ) {
                                        str.strPort = nod.getTextContent();
                                    } else if( strKey.equals( "passwd" ) ) {
                                        str.strPassword = nod.getTextContent();
                                    } else if( strKey.equals( "dbtype" ) ) {
                                        str.strDbType = nod.getTextContent();
                                    } else if( strKey.equals( "host" ) ) {
                                        str.strHost = nod.getTextContent();
                                    } else if( strKey.equals( "user" ) ) {
                                        str.strUser = nod.getTextContent();
                                    } else if( strKey.equals( "database" ) ) {
                                        str.strDatabase = nod.getTextContent();
                                    } else if( strKey.equals( "namespace" ) ) {
                                        str.strNamespace = nod.getTextContent();
                                    }
                                }
                            }
                        }
                    }
                }
                str.bEnabled = Utils.parseBoolean( Utils.getTagValue( "__default", eleRoot ) );
                return str;
            }
        }
        return null;
    }
}