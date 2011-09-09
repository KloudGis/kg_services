/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for retrieving feature type info (schema is most important).
 * Do not delete the feature type directly, use delete layer instead. See the javadoc of the delete method for more information.
 */
@Path( "/protected/featuretype" )
@Produces( { "application/json" } )
public class FeatureTypeBean {
    
    public FeatureTypeBean() {
        Authenticator.setDefault( new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "admin", "geoserver".toCharArray() );
            }
        });
    }
    
    /**
     * retrieves the feature type info (including the schema)
     * @param ftp is the feature type
     * @return the feature type
     * @throws MalformedURLException
     * @throws IOException 
     */
    @GET
    @Produces( { "application/json" } )
    public FeatureType getFeatureType( FeatureType ftp ) throws MalformedURLException, IOException {
        if( ftp == null || ftp.strName == null || ftp.strName.length() <= 0 || ftp.strWorkspace == null || 
                ftp.strWorkspace.length() <= 0 || ftp.strDatastore == null || ftp.strDatastore.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid feature type parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "workspaces/" + ftp.strWorkspace + "/datastores/" + ftp.strDatastore + "/featuretypes/" + ftp.strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestProperty( "Accept", "text/xml" );
        httpCon.setRequestMethod( "GET" );
        InputStream ins = httpCon.getInputStream();
        FeatureType feaType = buildFeatureType( ins );
        int iResponse = httpCon.getResponseCode();
        ins.close();
        httpCon.disconnect();
        if( iResponse >= 300 ) {
            throw new WebApplicationException( new Exception( "An error occurred. Error code: " + iResponse ) );
        }
        return feaType;
    }
    
    /**
     * Warning! Do not use this method unless the related layer does not exist anymore.
     * Use delete layer instead. Deleting the layer takes care of deleting the feature type.
     * @param ftp is the feature type to be deleted
     * @return the response from geoserver
     * @throws MalformedURLException
     * @throws IOException 
     */
    @DELETE
    @Produces( { "application/json" } )
    public Response deleteFeatureType( FeatureType ftp ) throws MalformedURLException, IOException {
        if( ftp == null || ftp.strName == null || ftp.strName.length() <= 0 || ftp.strWorkspace == null || 
                ftp.strWorkspace.length() <= 0 || ftp.strDatastore == null || ftp.strDatastore.length() <= 0 ) {
            throw new WebApplicationException( new IllegalArgumentException( "Invalid feature type parameter passed." ) );
        }
        URL url = new URL( Utils.URL + "workspaces/" + ftp.strWorkspace + "/datastores/" + ftp.strDatastore + "/featuretypes/" + ftp.strName );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setRequestMethod( "DELETE" );
        int iResponse = httpCon.getResponseCode();
        httpCon.disconnect();
        return Response.status( iResponse ).build();
    }
    
    private FeatureType buildFeatureType( InputStream ins ) {
        Document doc = Utils.getDocument( ins );
        if( doc != null ) {
            Element eleRoot = doc.getDocumentElement();
            if( eleRoot != null ) {
                FeatureType ftp = new FeatureType();
                ftp.strName = Utils.getTagValue( "name", eleRoot );
                ftp.strCRS = Utils.getTagValue( "srs", eleRoot );
                ftp.strProjectionPolicy = Utils.getTagValue( "projectionPolicy", eleRoot );
                ftp.bEnabled = Utils.parseBoolean( Utils.getTagValue( "enabled", eleRoot ) );
                NodeList lstNS = eleRoot.getElementsByTagName( "namespace" );
                if( lstNS != null && lstNS.getLength() > 0 ) {
                    Node nodNS = lstNS.item( 0 );
                    if( nodNS instanceof Element ) {
                        NodeList lstName = ( ( Element )nodNS ).getElementsByTagName( "name" );
                        if( lstName != null && lstName.getLength() > 0 ) {
                            Node nodName = lstName.item( 0 );
                            if( nodName != null ) {
                                ftp.strWorkspace = nodName.getTextContent();
                            }
                        }
                    }
                }
                NodeList lstDS = eleRoot.getElementsByTagName( "store" );
                if( lstDS != null && lstDS.getLength() > 0 ) {
                    Node nodDS = lstDS.item( 0 );
                    if( nodDS instanceof Element ) {
                        Element eleDS = ( Element )nodDS;
                        ftp.strClass = eleDS.getAttribute( "class" );
                        NodeList lstName = eleDS.getElementsByTagName( "name" );
                        if( lstName != null && lstName.getLength() > 0 ) {
                            Node nodName = lstName.item( 0 );
                            if( nodName != null ) {
                                ftp.strDatastore = nodName.getTextContent();
                            }
                        }
                    }
                }
                NodeList lstNativeBox = eleRoot.getElementsByTagName( "nativeBoundingBox" );
                if( lstNativeBox != null && lstNativeBox.getLength() > 0 ) {
                    Node nodNativeBox = lstNativeBox.item( 0 );
                    if( nodNativeBox instanceof Element ) {
                        Element eleNativeBox = ( Element )nodNativeBox;
                        NodeList lstMinX = eleNativeBox.getElementsByTagName( "minx" );
                        if( lstMinX != null && lstMinX.getLength() > 0 ) {
                            ftp.dNativeMinX = Utils.parseDouble( lstMinX.item( 0 ).getTextContent() );
                        }
                        NodeList lstMinY = eleNativeBox.getElementsByTagName( "miny" );
                        if( lstMinY != null && lstMinY.getLength() > 0 ) {
                            ftp.dNativeMinY = Utils.parseDouble( lstMinY.item( 0 ).getTextContent() );
                        }
                        NodeList lstMaxX = eleNativeBox.getElementsByTagName( "maxx" );
                        if( lstMaxX != null && lstMaxX.getLength() > 0 ) {
                            ftp.dNativeMaxX = Utils.parseDouble( lstMaxX.item( 0 ).getTextContent() );
                        }
                        NodeList lstMaxY = eleNativeBox.getElementsByTagName( "maxy" );
                        if( lstMaxY != null && lstMaxY.getLength() > 0 ) {
                            ftp.dNativeMaxY = Utils.parseDouble( lstMaxY.item( 0 ).getTextContent() );
                        }
                    }
                }
                NodeList lstLatlonBox = eleRoot.getElementsByTagName( "latLonBoundingBox" );
                if( lstLatlonBox != null && lstLatlonBox.getLength() > 0 ) {
                    Node nodLatlonBox = lstLatlonBox.item( 0 );
                    if( nodLatlonBox instanceof Element ) {
                        Element eleLatlonBox = ( Element )nodLatlonBox;
                        NodeList lstMinX = eleLatlonBox.getElementsByTagName( "minx" );
                        if( lstMinX != null && lstMinX.getLength() > 0 ) {
                            ftp.dLatlonMinX = Utils.parseDouble( lstMinX.item( 0 ).getTextContent() );
                        }
                        NodeList lstMinY = eleLatlonBox.getElementsByTagName( "miny" );
                        if( lstMinY != null && lstMinY.getLength() > 0 ) {
                            ftp.dLatlonMinY = Utils.parseDouble( lstMinY.item( 0 ).getTextContent() );
                        }
                        NodeList lstMaxX = eleLatlonBox.getElementsByTagName( "maxx" );
                        if( lstMaxX != null && lstMaxX.getLength() > 0 ) {
                            ftp.dLatlonMaxX = Utils.parseDouble( lstMaxX.item( 0 ).getTextContent() );
                        }
                        NodeList lstMaxY = eleLatlonBox.getElementsByTagName( "maxy" );
                        if( lstMaxY != null && lstMaxY.getLength() > 0 ) {
                            ftp.dLatlonMaxY = Utils.parseDouble( lstMaxY.item( 0 ).getTextContent() );
                        }
                    }
                }
                NodeList lstAttributes = eleRoot.getElementsByTagName( "attributes" );
                if( lstAttributes != null && lstAttributes.getLength() > 0 ) {
                    NodeList lstAttrs = lstAttributes.item( 0 ).getChildNodes();
                    ArrayList<Attribute> arlAttrs = new ArrayList<Attribute>();
                    if( lstAttrs != null ) {
                        for( int i = 0; i < lstAttrs.getLength(); i++ ) {
                            Node nod = lstAttrs.item( i );
                            if( nod instanceof Element ) {
                                Attribute att = new Attribute();
                                Element ele = ( Element )nod;
                                NodeList lstName = ele.getElementsByTagName( "name" );
                                if( lstName != null && lstName.getLength() > 0 ) {
                                    att.strName = lstName.item( 0 ).getTextContent();
                                }
                                NodeList lstNillable = ele.getElementsByTagName( "nillable" );
                                if( lstNillable != null && lstNillable.getLength() > 0 ) {
                                    att.bNillable = Utils.parseBoolean ( lstNillable.item( 0 ).getTextContent() );
                                }
                                NodeList lstBinding = ele.getElementsByTagName( "binding" );
                                if( lstBinding != null && lstBinding.getLength() > 0 ) {
                                    att.strClass = lstBinding.item( 0 ).getTextContent();
                                }
                                arlAttrs.add( att );
                            }
                        }
                    }
                    ftp.arlAttrs = arlAttrs;
                }
                return ftp;
            }
        }
        return null;
    }
}