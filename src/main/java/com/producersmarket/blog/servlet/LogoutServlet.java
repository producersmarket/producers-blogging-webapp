
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

            HttpSession httpSession = request.getSession(false);

            if(httpSession != null) {

                String sessionId = httpSession.getId();
                logger.debug("sessionId = "+sessionId);

                Integer userId = (Integer)httpSession.getAttribute("userId");
                logger.debug("userId = "+userId);

                httpSession.removeAttribute("userId");

                /*
                if(userId != null) {
                    try {
                        com.ispaces.manager.UserManager.updateUserLogout(userId.intValue());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                Integer sessionId = (Integer)httpSession.getAttribute("sessionId");
                logger.debug("sessionId = "+sessionId);
                if(sessionId != null) {
                    try {
                        com.ispaces.web.manager.SessionManager.updateSessionDestroyed(sessionId);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                */

                httpSession.invalidate();

            } // if(httpSession != null)

            ServletContext servletContext = getServletContext();
            logger.debug("servletContext = "+servletContext);

            String contextUrl = (String)servletContext.getAttribute("contextUrl");
            logger.debug("contextUrl = "+contextUrl);

            response.sendRedirect(contextUrl);

            return;

        //} catch(java.sql.SQLException e) {
        //    e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

}
