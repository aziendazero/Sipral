package com.phi.rest.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Cross-Origin Resource Sharing filter
 * Used with webpack-dev-server on port 3000
 * @author Alex
 */
public class CorsFilter implements Filter {

	protected static final Logger log = Logger.getLogger(CorsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    	
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000"); // * doesen't work : Cannot use wildcard in Access-Control-Allow-Origin when credentials flag is true.
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Expose-Headers", "Ajax-Response, Ajax-Expired, Ajax-Update-Ids, Location");  //Richfaces
    	
    	filterChain.doFilter(servletRequest, servletResponse);

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000"); // * doesen't work : Cannot use wildcard in Access-Control-Allow-Origin when credentials flag is true.
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Expose-Headers", "Ajax-Response, Ajax-Expired, Ajax-Update-Ids, Location");  //Richfaces
        
    }

    @Override
    public void destroy() {

    }
}