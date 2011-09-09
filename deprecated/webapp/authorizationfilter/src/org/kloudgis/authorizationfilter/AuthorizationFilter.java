package org.kloudgis.authorizationfilter;

/*
* EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretechnologies.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 */
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
/**
 *
 * @author jeanfelixg
 */
public class AuthorizationFilter implements Filter {

    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) {

        try {
            // wrap request with class that will perform the rewrite
            MyRequestWrapper myRequest = new MyRequestWrapper((HttpServletRequest) request, filterConfig);
            chain.doFilter(myRequest, response);
        } catch (IOException io) {
            System.out.println("IOException raised in RewriteRequestHeaderFilter");
        } catch (ServletException se) {
            System.out.println("ServletException raised in RewriteRequestHeaderFilter");
        }
    }

    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    class MyRequestWrapper extends HttpServletRequestWrapper {

        FilterConfig myFilterConfig;

        public MyRequestWrapper(HttpServletRequest request, FilterConfig filterConfig) {
            super(request);
            myFilterConfig = filterConfig;
        }
/*
        public Enumeration getHeaderNames() {
            System.out.println("Get Header NAMES");
            Enumeration names = super.getHeaderNames();
            Vector newNames = new Vector();
            boolean bAuth = false;
            while (names.hasMoreElements()) {
                String n = (String) names.nextElement();
                if(n.equalsIgnoreCase("Authorization")){
                    bAuth = true;
                }
                newNames.add(n);
                System.out.println("header=" + getHeader(n));
            }
            if(!bAuth){
                return newNames.elements();
            }else{
                return names;
            }
        }*/

        public String getHeader(String name) {
            System.out.println("Get Header: " + name);
            if(name.equals("Authorization")){
                System.out.println("filter hash=" + System.identityHashCode(AuthorizationFilter.this));
                return "Basic dGVzdDp0b3Rv";
            }else{
                return super.getHeader(name);
            }
        }
    }
}
