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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.kloudgis.KGConfig;
import org.kloudgis.api.ApiFactory;

/**
 * Proxy request to the geoserver mathching the project.
 * @author jeanfelixg
 */
public class WmsProxy extends HttpServlet {

    public static final String ENCRES = "responseEncoding";
    //query params
    public static final String KG_SANDBOX = "kg_sandbox";
    //session attributes
    public static final String KG_TIMEOUT = "!kg_timeout!";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //EntityManager em = PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
        try {
            // System.out.print("Process wms request...");
            HttpSession session = request.getSession(true);
            Boolean bAccess = (Boolean) session.getAttribute("kg_map_access");
            String server = KGConfig.getConfiguration().geoserver_url;
            //authenticate with the auth token
            String auth_token = getAuthToken(request);
            // System.out.println("Auth token:" + auth);
            if (auth_token != null && auth_token.length() > 0) {
                Long timeout = (Long) session.getAttribute(KG_TIMEOUT);
                Long time = Calendar.getInstance().getTimeInMillis();
                if (server != null && timeout != null && (timeout.longValue() < time) && ((timeout.longValue() + 60000L) > time)) {
                    //ok
                    //System.out.println("Auth token still valid, " + (time - timeout.longValue()) / 1000 + " sec.");
                } else {
                    String sandbox = getHttpParam(KG_SANDBOX, request);
                    if (sandbox != null && sandbox.length() > 0) {
                        try {
                            Long user_id = (Long) session.getAttribute("kg_user_id");
                            if (user_id == null) {
                                String body = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().auth_url + "/user_id", KGConfig.getConfiguration().api_key);
                                if (body != null && body.length() > 0) {
                                    user_id = Long.parseLong(body);
                                    session.setAttribute("kg_user_id", user_id);
                                }
                            }
                            String body = ApiFactory.apiGet(auth_token, KGConfig.getConfiguration().data_url + "/map_access?sandbox=" + sandbox + "&user_id=" + user_id, KGConfig.getConfiguration().api_key);
                            if (body != null && body.equals("true")) {
                                session.setAttribute("kg_map_access", true);
                                bAccess = true;
                            } else {
                                session.setAttribute("kg_map_access", false);
                                bAccess = false;
                            }
                            session.setAttribute(KG_TIMEOUT, Calendar.getInstance().getTimeInMillis());
                        } catch (Exception ex) {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        }
                    } else {
                        bAccess = false;
                    }
                }
            } else {
                bAccess = false;
            }

            if (server == null) {
                throw new IllegalArgumentException("missing server");
            }
            if (bAccess == null || bAccess.booleanValue() == false) {
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
        return null;
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
