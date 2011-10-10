/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.kloudgis.KGConfig;

/**
 * Proxy request to the geoserver mathching the project.
 * @author jeanfelixg
 */
public class WmsProxy extends HttpServlet {

    public static final String ENCRES = "responseEncoding";
   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String server = KGConfig.getConfiguration().geoserver_url,sandbox = null;
        try {
            sandbox = getHttpParam(org.kloudgis.SecurityManager.KG_SANDBOX, request);
            String auth_token = getAuthToken(request);
            boolean bAccess = org.kloudgis.SecurityManager.getInstance().login(request, auth_token, sandbox);
            if (server == null) {
                throw new IllegalArgumentException("missing server");
            }
            if (bAccess == false) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            PostMethod postMethod = new PostMethod(server);

            Enumeration paramNames = request.getParameterNames();
            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                for (int i = 0; i < paramValues.length; i++) {
                    //System.out.println("paramName: " + paramName + " = " + paramValues[i]);
                    nvp.add(new NameValuePair(paramName, paramValues[i]));
                }
            }
            NameValuePair[] data = new NameValuePair[nvp.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = nvp.get(i);
            }
            postMethod.setRequestBody(data);

            // Execute the request
            postMethod.setFollowRedirects(false);
            HttpClient httpClient = new HttpClient();

            int status = httpClient.executeMethod(postMethod);
            response.setStatus(status);

            // Setting response headers
            setProxyResponseHeaders(response, postMethod);

            Header[] hs = postMethod.getResponseHeaders();
            request.getSession().setAttribute(ENCRES, null);
            String encoding = "";
            for (int i = 0; i < hs.length; i++) {
                Header h = hs[i];
                if (h.getName().equalsIgnoreCase("Content-Encoding")) {
                    request.getSession().setAttribute(ENCRES, h.getValue());
                    encoding = h.getValue();
                    break;
                }
            }

            // Store response only if a GetCapabilities request has been made
            if (getHttpParam("REQUEST", request).equalsIgnoreCase("GETCAPABILITIES")) {

                InputStream bodyStream = null;

                if (encoding.equalsIgnoreCase("gzip")) {
                    bodyStream = new GZIPInputStream(
                            postMethod.getResponseBodyAsStream());

                } else if (encoding.equalsIgnoreCase("deflate")) {
                    bodyStream = new DeflaterInputStream(
                            postMethod.getResponseBodyAsStream());
                } else {
                    bodyStream = new BufferedInputStream(
                            postMethod.getResponseBodyAsStream());
                }

                // Changing the response from InputStream to Byte Array.
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                byte[] buffer = new byte[4096];
                int length;
                while ((length = bodyStream.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                out.flush();
                out.close();

            } else {
                InputStream inputStreamProxyResponse = postMethod.getResponseBodyAsStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamProxyResponse);
                OutputStream outputStreamClientResponse = response.getOutputStream();
                int intNextByte;

                //System.out.print("\nCopying response: ");
                StringBuilder sb = new StringBuilder();
                int cnt = 0;
                while ((intNextByte = bufferedInputStream.read()) != -1) {
                    cnt++;
                    outputStreamClientResponse.write(intNextByte);
                }

                //System.out.println(" - > BYTE READED: " + cnt);
                outputStreamClientResponse.flush();
                outputStreamClientResponse.close();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Coud'nt proxy sandbox:"  +sandbox + " on server:" + server);
            throw new ServletException(ex.getMessage());
        }
    }

    private static synchronized void setProxyResponseHeaders(
            HttpServletResponse response,
            HttpMethod httpMethodProxyRequest) {
        Header[] headerArrayResponse = httpMethodProxyRequest.getResponseHeaders();
        for (Header header : headerArrayResponse) {
            String val = header.getValue();
            if (header.getName().equalsIgnoreCase("Transfer-Encoding")) {
                continue;
            } else {
                response.setHeader(header.getName(), val);
            }
        }
    }

    private static String getHttpParam(String parameter, javax.servlet.http.HttpServletRequest req) {
        String ret = null;
        java.util.Enumeration<String> pm = req.getParameterNames();
        while (pm.hasMoreElements()) {
            String param = pm.nextElement();
            if (param.equalsIgnoreCase(parameter)) {
                ret = req.getParameter(param);
                break;
            }
        }
        return ret;
    }

    private String getAuthToken(HttpServletRequest request) {
        Cookie[] arrC = request.getCookies();
        if (arrC != null) {
            for (Cookie cookie : arrC) {
                if (cookie.getName().equals("C-Kloudgis-Authentication")) {
                    return cookie.getValue();
                }
            }
        }
        return request.getHeader("X-Kloudgis-Authentication");
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "WMS Proxy";
    }
}
