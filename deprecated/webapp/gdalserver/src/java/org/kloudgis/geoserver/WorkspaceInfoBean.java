/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for retrieving lists of layers, layer groups or datastores that belong to a workspace.
 */
@Path( "/protected/workspaceinfo" )
@Produces( { "application/json" } )
public class WorkspaceInfoBean {
            
    public WorkspaceInfoBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * @param wki contains the name of the workspace and the category (one of the following: layers, layergroups or datastores)
     * @return a list of names that belong to the workspace passed as parameter
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException 
     */
    @GET
    @Produces( { "application/json" } )
    public ArrayList<String> getWorkspaceInfo( WorkspaceInfo wki ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        if( wki == null || wki.strWorkspaceName == null || wki.strWorkspaceName.length() <= 0 || wki.strCategory == null ||
                !( wki.strCategory.equals( "layers" ) || wki.strCategory.equals( "datastores" ) || wki.strCategory.equals( "layergroups" ) ) ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid type parameter passed." ) );
        }
        if( wki.strCategory.equals( "datastores" ) ) {
            return getInfo( wki.strWorkspaceName, wki.strCategory );
        } else if( wki.strCategory.equals( "layers" ) ) {
            ArrayList<String> arl = null;
            ArrayList<String> arlLayers = new GeoserverInfoBean().getNames( wki.strCategory );//get all layers
            if( arlLayers != null ) {
                arl = new ArrayList<String>();
                LayerBean lbn = new LayerBean();
                for( String strLayer : arlLayers ) {
                    Layer lay = lbn.getLayer( strLayer );
                    if( lay != null && lay.strWorkspace != null && lay.strWorkspace.equals( wki.strWorkspaceName ) ) {//check if the layer belongs to the workspace
                        arl.add( strLayer );
                    }
                }
            }
            return arl;
        } else {//layergroups
            ArrayList<String> arl = null;
            ArrayList<String> arlLayerGroups = new GeoserverInfoBean().getNames( wki.strCategory );//get all layergroups
            if( arlLayerGroups != null ) {
                arl = new ArrayList<String>();
                LayerGroupBean lbn = new LayerGroupBean();
                WorkspaceInfo wkiLayers = new WorkspaceInfo();
                wkiLayers.strWorkspaceName = wki.strWorkspaceName;
                wkiLayers.strCategory = "layers";
                ArrayList<String> arlLayers = getWorkspaceInfo( wkiLayers );//get all layers that belong to the workspace
                if( arlLayers != null ) {
                    for( String strLayer : arlLayers ) {
                        for( String strLayerGroup : arlLayerGroups ) {
                            LayerGroup lgr = lbn.getLayerGroup( strLayerGroup );
                            //check if the layergroup contains layers that belong to the workspace
                            if( lgr != null && lgr.arlLayers != null && lgr.arlLayers.contains( strLayer ) ) {
                                arl.add( strLayerGroup );
                            }
                        }
                    }
                }
            }
            return arl;
        }
    }
    
    private ArrayList<String> getInfo( String strWorkspace, String strCategory ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        URL url = new URL( Utils.URL + "workspaces/" + strWorkspace + "/" + strCategory );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        InputStream ins = getInputStream( httpCon );
        ArrayList<String> arl = buildNamesList( ins );
        int iResponse = httpCon.getResponseCode();
        ins.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return arl;
    }
    
    private InputStream getInputStream( HttpURLConnection httpCon ) throws ProtocolException, IOException {
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        return httpCon.getInputStream();
    }

    private ArrayList<String> buildNamesList( InputStream ins ) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<String> arl = new ArrayList<String>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder dcb = dbf.newDocumentBuilder();
        Document doc = dcb.parse( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                NodeList lst = eleRoot.getElementsByTagName( "dataStore" );
                if( lst != null ) {
                    for( int i = 0; i < lst.getLength(); i++ ) {
                        Node nod = lst.item( i );
                        if( nod instanceof Element ) {
                            arl.add( Utils.getTagValue( "name", ( Element )nod ) );
                        }
                    }
                }
            }
        }
        return arl;
    }
}