/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.IOException;
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
import org.xml.sax.SAXException;

/**
 * Class for managing workspaces.
 */
@Path( "/protected/workspace" )
@Produces( { "application/json" } )
public class WorkspaceBean {
    
    public WorkspaceBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * adds a workspace
     * @param strName is the name of the workspace
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @POST
    @Produces( { "application/json" } )
    public Response addWorkspace( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Did not receive a valid workspace name. Name was: " + strName ) );
        }
        URL url = new URL( Utils.URL + "workspaces" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "text/xml" );
        OutputStream ost = httpCon.getOutputStream();
        ost.write( ( "<workspace><name>" + strName + "</name></workspace>" ).getBytes() );
        ost.flush();
        int iResponse = httpCon.getResponseCode();//201 = workspace has been created
        ost.close();
        httpCon.disconnect();
        if( iResponse != 201 ) {//workspace has NOT been created
            if( getWorkspace( strName ) != null ) {//workspace already exists
                throw new WebApplicationException( new EntityExistsException( "Workspace " + strName + " already exists." ) );
            }
        }
        return Response.status( iResponse ).build();
    }
    
    /**
     * retrieves the workspace info
     * @param strName is the name of the workspace
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @GET
    @Produces( { "application/json" } )
    public Workspace getWorkspace( String strName ) throws MalformedURLException, IOException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Did not receive a valid workspace name. Name was: " + strName ) );
        }
        URL url = new URL( Utils.URL + "workspaces/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return new Workspace( strName );
    }
    
    /**
     * deletes all datastores in this workspace then deletes the workspace itself
     * @param strName is the name of the workspace to be deleted
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    @DELETE
    @Produces( { "application/json" } )
    public Response deleteWorkspace( String strName ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        if( strName == null || strName.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Did not receive a valid workspace name. Name was: " + strName ) );
        }
        WorkspaceInfo wki = new WorkspaceInfo();
        wki.strWorkspaceName = strName;
        wki.strCategory = "datastores";
        ArrayList<String> arlDatastores = new WorkspaceInfoBean().getWorkspaceInfo( wki );//get all datastores in this workspace
        if( arlDatastores != null ) {
            StoreBean stb = new StoreBean();
            for( String strDS : arlDatastores ) {
                Store str = new Store();
                str.strNamespace = strName;
                str.strName = strDS;
                stb.deleteStore( str );
            }
        }
        URL url = new URL( Utils.URL + "workspaces/" + strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestMethod( "DELETE" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        return Response.status( iResponse ).build();
    }
}