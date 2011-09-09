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
 * Class for retrieving global lists of workspaces, layers, styles, layergroups or datastores
 */
@Path( "/protected/geoserverinfo" )
@Produces( { "application/json" } )
public class GeoserverInfoBean {
    
    public GeoserverInfoBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * @param strCategory accepts one of the following values: namespaces, layers, styles, layergroups, datastores
     * @return a list of names
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException 
     */
    @GET
    @Produces( { "application/json" } )
    public ArrayList<String> getNames( String strCategory ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        if( strCategory == null || !( strCategory.equals( "namespaces" ) || strCategory.equals( "layers" ) || strCategory.equals( "styles" ) || 
                strCategory.equals( "layergroups" ) || strCategory.equals( "datastores" ) ) ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid type parameter passed." ) );
        }
        ArrayList<String> arl = null;
        if( strCategory.equals( "datastores" ) ) {
            ArrayList<String> arlWorkspaces = getNames( "namespaces" );//get all workspaces
            if( arlWorkspaces != null ) {
                arl = new ArrayList<String>();
                for( String strWorkspace : arlWorkspaces ) {
                    ArrayList<String> arlTemp = getInfo( strWorkspace, strCategory );//get all datastores for each workspace
                    if( arlTemp != null ) {
                        arl.addAll( arlTemp );
                    }
                }
            }
        } else {//workspaces, layers, styles, layergroups
            URL url = new URL( Utils.URL + strCategory );
            HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
            InputStream ins = getInputStream( httpCon );
            arl = buildNamesList( ins, strCategory );
            int iResponse = httpCon.getResponseCode();
            ins.close();
            httpCon.disconnect();
            if( iResponse >= 300 ) {
                throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
            }
        }
        return arl;
    }
    
    private ArrayList<String> getInfo( String strWorkspace, String strCategory ) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        URL url = new URL( Utils.URL + "workspaces/" + strWorkspace + "/" + strCategory );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        InputStream ins = getInputStream( httpCon );
        ArrayList<String> arl = buildNamesList( ins, strCategory );
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

    private ArrayList<String> buildNamesList( InputStream ins, String str ) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<String> arl = new ArrayList<String>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder dcb = dbf.newDocumentBuilder();
        Document doc = dcb.parse( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                String strNodeName = null;
                if( str.equals( "namespaces" ) ) {
                    strNodeName = "namespace";
                } else if( str.equals( "layers" ) ) {
                    strNodeName = "layer";
                } else if( str.equals( "styles" ) ) {
                    strNodeName = "style";
                } else if( str.equals( "layergroups" ) ) {
                    strNodeName = "layerGroup";
                } else if( str.equals( "datastores" ) ) {
                    strNodeName = "dataStore";
                }
                NodeList lst = eleRoot.getElementsByTagName( strNodeName );
                if( lst != null ){
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