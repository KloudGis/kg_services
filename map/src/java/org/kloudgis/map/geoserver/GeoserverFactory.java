/*
 * @author corneliu
 */
package org.kloudgis.map.geoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.kloudgis.map.KGConfig;
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
        StringBuilder strStore = new StringBuilder();
        strStore.append("<dataStore>");
        strStore.append("<name>");
        strStore.append(datastore.toLowerCase());
        strStore.append("</name>");
        strStore.append("<enabled>");
        strStore.append("true");
        strStore.append("</enabled>");
        strStore.append("<connectionParameters>");
        strStore.append("<entry key=\"host\">");
        strStore.append(strHost);
        strStore.append("</entry>");
        strStore.append("<entry key=\"port\">");
        strStore.append(strPort);
        strStore.append("</entry>");
        strStore.append("<entry key=\"database\">");
        strStore.append(datastore.toLowerCase());
        strStore.append("</entry>");
        strStore.append("<entry key=\"user\">");
        strStore.append(user);
        strStore.append("</entry>");
        strStore.append("<entry key=\"passwd\">");
        strStore.append(pwd);
        strStore.append("</entry>");
        strStore.append("<entry key=\"namespace\">");
        strStore.append(workspace.toLowerCase());
        strStore.append("</entry>");
        strStore.append("<entry key=\"dbtype\">");
        strStore.append("postgis");
        strStore.append("</entry>");
        strStore.append("<entry key=\"min connections\">");
        strStore.append("0");
        strStore.append("</entry>");
        strStore.append("<entry key=\"max connections\">");
        strStore.append("3");
        strStore.append("</entry>");
        strStore.append("<entry key=\"Estimated extends\">");
        strStore.append("false");
        strStore.append("</entry>");
        strStore.append("<entry key=\"Loose bbox\">");
        strStore.append("false");
        strStore.append("</entry>");
        strStore.append("<entry key=\"Connection timeout\">");
        strStore.append("20");
        strStore.append("</entry>");
        strStore.append("</connectionParameters>");
        strStore.append("</dataStore>");
        pst.setRequestEntity(new StringRequestEntity(strStore.toString(), "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(pst);
        String strBody = pst.getResponseBodyAsString(1500);
        pst.releaseConnection();
        if (iResponse != HttpStatus.SC_CREATED) {
            throw new GeoserverException(iResponse, strBody);
        }
    }

    public static void addLayer(String strGeoserverURL, String strWSName, String strDSName,
            String strFTName, String tableName, String minX, String minY, String maxX, String maxY, Credentials crd) throws MalformedURLException, IOException, GeoserverException {

        PostMethod pst = new PostMethod(strGeoserverURL + "/rest/workspaces/" + strWSName.toLowerCase() + "/datastores/" + strDSName.toLowerCase() + "/featuretypes");
        pst.setRequestEntity(new StringRequestEntity("<featureType><name>" + strFTName.toLowerCase() + "</name><nativeName>" + tableName + "</nativeName><srs>EPSG:4326</srs>"
                + "<nativeBoundingBox><minx>" + minX + "</minx><maxx>" + maxX + "</maxx><miny>" + minY + "</miny><maxy>" + maxY + "</maxy></nativeBoundingBox>"
                + "<latLonBoundingBox><minx>" + minX + "</minx><maxx>" + maxX + "</maxx><miny>" + minY + "</miny><maxy>" + maxY + "</maxy></latLonBoundingBox></featureType>", "application/xml", "UTF-8"));
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(pst);
        String strBody = pst.getResponseBodyAsString(1500);
        pst.releaseConnection();
        if (iResponse != HttpStatus.SC_CREATED) {
            throw new GeoserverException(iResponse, strBody);
        }
    }

    public static void addGroupLayer(String geoserver_url, String workspaceName, List<String> layNames, Credentials crd) throws IOException {
        StringBuilder strLay = new StringBuilder();
        strLay.append("<layerGroup>");
        strLay.append("<name>");
        strLay.append(workspaceName);
        strLay.append("</name>");
        strLay.append("<layers>");
        for (String layer : layNames) {
            strLay.append("<layer>");
            strLay.append(layer);
            strLay.append("</layer>");
        }
        strLay.append("</layers>");
        strLay.append("</layerGroup>");
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        PostMethod post = new PostMethod(geoserver_url + "/rest/layergroups");
        post.setRequestEntity(new StringRequestEntity(strLay.toString(), "application/xml", "UTF-8"));
        int iResponse = htc.executeMethod(post);
        String strBody = post.getResponseBodyAsString(1500);
        if (iResponse != HttpStatus.SC_CREATED) {
            if(strBody.endsWith("already exists.")){
                PutMethod put = new PutMethod(geoserver_url + "/rest/layergroups/" + workspaceName);
                put.setRequestEntity(new StringRequestEntity(strLay.toString(), "application/xml", "UTF-8"));
                iResponse = htc.executeMethod(post);
                if(iResponse != HttpStatus.SC_OK){
                    strBody = put.getResponseBodyAsString(1500);
                    throw new GeoserverException(iResponse, strBody);
                }
            }else{
                throw new GeoserverException(iResponse, strBody);
            }
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

    public static void gwcLayer(String gwc_url, String geoServerUrl, String layerName, Credentials crd) throws IOException {
        PutMethod put = new PutMethod(gwc_url + "/rest/layers/" + layerName.toLowerCase() + ".xml");
        
        StringBuilder strXML = new StringBuilder();
        
        strXML.append("<wmsLayer>");
        strXML.append("<name>");
        strXML.append(layerName.toLowerCase());
        strXML.append("</name>");
        strXML.append("<wmsUrl>");
        strXML.append("<keyword>");
        strXML.append(geoServerUrl + "/wms");
        strXML.append("</keyword>");
        strXML.append("</wmsUrl>");
        strXML.append("<metaWidthHeight>");
        strXML.append("<int>");
        strXML.append(3);
        strXML.append("</int>");
        strXML.append("<int>");
        strXML.append(3);
        strXML.append("</int>");
        strXML.append("</metaWidthHeight>");
        strXML.append("<backendTimeout>");
        strXML.append("120");
        strXML.append("</backendTimeout>");
        strXML.append("<enabled>");
        strXML.append("true");
        strXML.append("</enabled>");
        strXML.append("<gutter>");
        strXML.append("0");
        strXML.append("</gutter>");
        strXML.append("<concurrency>");
        strXML.append("32");
        strXML.append("</concurrency>");
        strXML.append("</wmsLayer>");
        
        StringRequestEntity entity = new StringRequestEntity(strXML.toString(), "application/xml", "UTF-8");
        put.setRequestEntity(entity);
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials(AuthScope.ANY, crd);
        int iResponse = htc.executeMethod(put);
        String strBody = put.getResponseBodyAsString(1500);
        put.releaseConnection();
        if(strBody != null && strBody.contains("already exists")){
            PostMethod post = new PostMethod(gwc_url + "/rest/layers/" + layerName.toLowerCase() + ".xml");
            post.setRequestEntity(entity);
            iResponse = htc.executeMethod(post);
            strBody = post.getResponseBodyAsString(1500);
            post.releaseConnection();
        }       
        if (iResponse != HttpStatus.SC_OK) {
            throw new GeoserverException(iResponse, "GWC Exception:" + strBody);
        }
    }
}