/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Scanner;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for managing styles.
 */
@Path( "/protected/style" )
@Produces( { "application/json" } )
public class StyleBean {
    
    public StyleBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * declares an empty style
     * @param strName is the name of the style
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @POST
    @Produces( { "application/json" } )
    public Response declareStyle( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid style name parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "styles" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestProperty( "Content-type", "text/xml" );
        httpCon.setRequestMethod( "POST" );
        httpCon.connect();
        OutputStream ost = httpCon.getOutputStream();
        ost.write( ( "<style><name>" + strName + "</name><filename>" + strName + ".sld</filename></style>" ).getBytes() );
        ost.flush();
        int iResponse = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return Response.status( iResponse ).build();
    }
    
    /**
     * fills the xml content of the style
     * @param stl is the style that receives the xml content
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @PUT
    @Produces( { "application/json" } )
    public Response uploadStyle( Style stl ) throws MalformedURLException, IOException {
        if( stl == null || stl.strName == null || stl.strName.length() <= 0 || stl.strPath == null || stl.strPath.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid style name parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "styles/" + stl.strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestProperty("Content-type", "application/vnd.ogc.sld+xml");
        httpCon.setRequestMethod( "PUT" );
        httpCon.connect();
        OutputStream ost = httpCon.getOutputStream();
        String strContent = getFileContent( stl.strPath );
        if( strContent != null ) {
            ost.write( strContent.getBytes() );
            ost.flush();
        } else {
            throw new WebApplicationException( new FileNotFoundException( "File not found: " + stl.strPath ) );
        }
        int iResponse = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return Response.status( iResponse ).build();
    }
    
    /**
     * retrieves the style info
     * @param strName is the name of the style
     * @return the style object
     * @throws MalformedURLException
     * @throws IOException 
     */
    @GET
    @Produces( { "application/json" } )
    public Style getStyle( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid style name parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "styles/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        InputStream ins = httpCon.getInputStream();
        Style stl = buildStyle( ins );
        int iResponse = httpCon.getResponseCode();
        ins.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return stl;
    }

    /**
     * deletes a style
     * @param strName is the name of the style
     * @return the style object
     * @throws MalformedURLException
     * @throws IOException 
     */
    @DELETE
    @Produces( { "application/json" } )
    public Response deleteStyle( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid style name parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "styles/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestMethod( "DELETE" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return Response.status( iResponse ).build();
    }
    
    private String getFileContent( String strFile ) throws FileNotFoundException {
        File file = new File( strFile );
        if( file.exists() ) {
            StringBuilder stb = new StringBuilder();
            Scanner scn = new Scanner( new FileInputStream( file ) );
            while( scn.hasNextLine() ) {
                stb.append( scn.nextLine() );
            }
            scn.close();
            return stb.toString();
        }
        return null;
    }
    
    private Style buildStyle( InputStream ins ) {
        Document doc = Utils.getDocument( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                Style stl = new Style();
                stl.strName = Utils.getTagValue( "name", eleRoot );
                stl.strFileName = Utils.getTagValue( "filename", eleRoot );
                NodeList lstVersion = eleRoot.getElementsByTagName( "sldVersion" );
                if( lstVersion != null && lstVersion.getLength() > 0 ) {
                    Node nodVersion = lstVersion.item( 0 );
                    if( nodVersion instanceof Element ) {
                        NodeList lst = ( ( Element )nodVersion ).getElementsByTagName( "version" );
                        if( lst != null && lst.getLength() > 0 ) {
                            Node nod = lst.item( 0 );
                            if( nod != null ) {
                                stl.strVersion = nod.getTextContent();
                            }
                        }
                    }
                }
                return stl;
            }
        }
        return null;
    }
}