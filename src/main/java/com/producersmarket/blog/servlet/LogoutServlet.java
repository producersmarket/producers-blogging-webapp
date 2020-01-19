package com.producersmarket.blog.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class LogoutServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            super.logoutUser(request, response);
            
            ServletContext servletContext = getServletContext();
            String contextUrl = (String)servletContext.getAttribute("contextUrl");

            logger.debug("servletContext = "+servletContext);
            logger.debug("contextUrl = "+contextUrl);

            response.sendRedirect(contextUrl);
            return;

        } catch(Exception e) {
            e.printStackTrace();
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

}
