package com.producersmarket.blog.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BlogPostFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(BlogPostFilter.class);

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("doFilter(servletRequest, servletResponse, filterChain)");

        String blogName = null;

        if(servletRequest instanceof HttpServletRequest) {

            String pathInfo = ((HttpServletRequest)servletRequest).getPathInfo();
            logger.debug("pathInfo = "+pathInfo);

            if(pathInfo != null) {

                blogName = pathInfo.substring(1, pathInfo.length());
                logger.debug("blogName = "+blogName);
            }

            filterChain.doFilter(servletRequest, servletResponse);

        } // if(servletRequest instanceof HttpServletRequest)

        if(servletResponse instanceof HttpServletResponse) {

            int httpStatus = ((HttpServletResponse)servletResponse).getStatus();
            logger.debug("httpStatus = "+httpStatus);

        } // if(servletResponse instanceof HttpServletResponse)

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("init("+filterConfig+")");

    }

    public void destroy() {
        // nothing todo
    }

}
