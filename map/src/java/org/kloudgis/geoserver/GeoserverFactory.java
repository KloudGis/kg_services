/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class GeoserverFactory {

    public static void addWorkspace(String strGeoserverURL, String strName, Credentials crd)
            throws MalformedURLException, IOException, GeoserverException {
        PostMethod pst = new PostMethod(strGeoserverURL + "/rest/workspaces");
        String content = "<workspace><name>" + strName.toLowerCase() + "</name></workspace>";
        pst.setRequestEntity(new StringRequestEntity(content, "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(pst);
        String strBody = pst.getResponseBodyAsString(1500);
        pst.releaseConnection();
        if (iResponse != HttpStatus.SC_CREATED) {
            throw new GeoserverException(iResponse, strBody);
        }
    }

    public static void addStore(String strGeoserverURL, String workspace, String datastore, String user, String pwd, String strHost, String strPort, Credentials crd)
            throws MalformedURLException, IOException, GeoserverException {
        PostMethod pst = new PostMethod(strGeoserverURL + "/rest/workspaces/" + workspace.toLowerCase() + "/datastores");
        pst.setRequestEntity(new StringRequestEntity("<dataStore><name>" + datastore.toLowerCase() + "</name><enabled>true</enabled><connectionParameters><host>"
                + strHost + "</host><port>" + strPort + "</port><database>" + datastore.toLowerCase() + "</database><user>" + user
                + "</user><passwd>" + pwd + "</passwd><dbtype>postgis</dbtype><namespace>" + workspace.toLowerCase()
                + "</namespace></connectionParameters></dataStore>", "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(pst);
        String strBody = pst.getResponseBodyAsString(1500);
        pst.releaseConnection();
        if (iResponse != HttpStatus.SC_CREATED) {
            throw new GeoserverException(iResponse, strBody);
        }
    }
    
    public static void addLayer( String strGeoserverURL, String strWSName, String strDSName,
            String strFTName, String tableName, String minX, String minY, String maxX, String maxY, Credentials crd) throws MalformedURLException, IOException, GeoserverException {

        PostMethod pst = new PostMethod(strGeoserverURL + "/rest/workspaces/" + strWSName.toLowerCase() + "/datastores/" + strDSName.toLowerCase() + "/featuretypes");
        pst.setRequestEntity(new StringRequestEntity("<featureType><name>" + strFTName.toLowerCase() + "</name><nativeName>"+ tableName +"</nativeName><srs>EPSG:4326</srs>" + 
                "<nativeBoundingBox><minx>" + minX + "</minx><maxx>" + maxX + "</maxx><miny>" + minY + "</miny><maxy>" + maxY + "</maxy></nativeBoundingBox>" + 
                "<latLonBoundingBox><minx>" + minX + "</minx><maxx>" + maxX + "</maxx><miny>" + minY + "</miny><maxy>" + maxY + "</maxy></latLonBoundingBox></featureType>", "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(pst);
        String strBody = pst.getResponseBodyAsString(1500);
        pst.releaseConnection();
        if (iResponse != HttpStatus.SC_CREATED) {
            throw new GeoserverException(iResponse, strBody);
        }

    }

    public static ArrayList<String> getStores(String strGeoserverURL, String strWorkspace, Credentials crd) throws IOException {
        ArrayList<String> arrlS = new ArrayList();
        GetMethod get = new GetMethod(strGeoserverURL + "/rest/workspaces/" + strWorkspace.toLowerCase() + "/datastores");
        get.setRequestHeader("Accept", "application/xml");
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(get);
        if (iResponse == 200) {
            InputStream stream = get.getResponseBodyAsStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder dcb = dbf.newDocumentBuilder();
                Document doc = dcb.parse(stream);
                NodeList nodes = doc.getElementsByTagName("dataStore");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element elem = (Element) nodes.item(i);
                    NodeList nodeNames = elem.getElementsByTagName("name");
                    if (nodeNames.getLength() > 0) {
                        Element elemName = (Element) nodes.item(0);
                        arrlS.add(elemName.getTextContent());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrlS;
    }

    public static void deleteWorkspace(String strGeoserverURL, String strName, Credentials crd) throws IOException {
        DeleteMethod del = new DeleteMethod(strGeoserverURL + "/rest/workspaces/" + strName.toLowerCase() + "?recurse=true");
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(del);
        String strBody = del.getResponseBodyAsString(1500);
        del.releaseConnection();
        if (iResponse != HttpStatus.SC_OK) {
            throw new GeoserverException(iResponse, strBody);
        }
    }

    public static void deleteStore(String strGeoserverURL, String strWorkspace, String strName, Credentials crd) throws IOException {
        DeleteMethod del = new DeleteMethod(strGeoserverURL + "/rest/workspaces/" + strWorkspace.toLowerCase() + "/datastores/" + strName + "?recurse=true");
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(del);
        String strBody = del.getResponseBodyAsString(1500);
        del.releaseConnection();
        if (iResponse != HttpStatus.SC_OK) {
            throw new GeoserverException(iResponse, strBody);
        }
    }

    public static List<String> listStyles(String strGeoserverURL, Credentials crd)
            throws MalformedURLException, IOException, ParserConfigurationException, SAXException, GeoserverException {
        List<String> lst = null;
        GetMethod gtm = new GetMethod(strGeoserverURL + "/rest/styles");
        gtm.setRequestHeader(new Header("Accept", "application/xml"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(gtm);
        String strBody = gtm.getResponseBodyAsString();
        if (iResponse == HttpStatus.SC_OK) {
            InputStream ins = gtm.getResponseBodyAsStream();
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dcb = dbf.newDocumentBuilder();
            doc = dcb.parse(ins);
            Element eleRoot = doc.getDocumentElement();
            NodeList lstStyle = eleRoot.getElementsByTagName("style");
            lst = new ArrayList<String>();
            for (int i = 0; i < lstStyle.getLength(); i++) {
                Node nodStyle = lstStyle.item(i);
                if (nodStyle instanceof Element) {
                    NodeList lstName = ((Element) nodStyle).getElementsByTagName("name");
                    for (int j = 0; j < lstName.getLength(); j++) {
                        Node nodName = lstName.item(j);
                        if (nodName instanceof Element) {
                            lst.add(nodName.getTextContent());
                        }
                    }
                }
            }
            ins.close();
        }
        gtm.releaseConnection();
        if (iResponse != HttpStatus.SC_OK) {
            throw new GeoserverException(iResponse, strBody);
        }
        return lst;
    }

    public static void uploadStyle(String strGeoserverURL, String sld, String strStyleName, Credentials crd)
            throws MalformedURLException, IOException, GeoserverException {
        PostMethod pst = new PostMethod(strGeoserverURL + "/rest/styles");
        pst.setRequestEntity(new StringRequestEntity("<style><name>" + strStyleName + "</name><filename>" + strStyleName + ".sld</filename></style>",
                "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(pst);
        String strBody = pst.getResponseBodyAsString(1500);
        pst.releaseConnection();
        if (iResponse == HttpStatus.SC_CREATED || strBody.endsWith("already exists.")) {
            PutMethod ptm = new PutMethod(strGeoserverURL + "/rest/styles/" + strStyleName);
            ptm.setRequestEntity(new StringRequestEntity(sld, "application/vnd.ogc.sld+xml", "UTF-8"));
            iResponse = htc.executeMethod(ptm);
            strBody = ptm.getResponseBodyAsString(1500);
            ptm.releaseConnection();
            if (iResponse != HttpStatus.SC_OK) {
                throw new GeoserverException(iResponse, strBody);
            }
        } else {
            throw new GeoserverException(iResponse, strBody);
        }
    }

    public static void assignStyle(String strGeoserverURL, String strWorkspaceName, String strLayerName, String strStyleName, Credentials crd)
            throws MalformedURLException, IOException, GeoserverException {
        PutMethod ptm = new PutMethod(strGeoserverURL + "/rest/layers/" + strWorkspaceName.toLowerCase() + ":" + strLayerName.toLowerCase());
        ptm.setRequestEntity(new StringRequestEntity("<layer><defaultStyle><name>" + strStyleName + "</name></defaultStyle><enabled>"
                + "true</enabled><styles><style><name>" + strStyleName + "</name></style></styles></layer>", "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(ptm);
        String strBody = ptm.getResponseBodyAsString(1500);
        ptm.releaseConnection();
        if (iResponse != HttpStatus.SC_OK) {
            throw new GeoserverException(iResponse, strBody);
        }
    }
}