package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.producersmarket.blog.database.LoginDatabaseManager;

public class LoginServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();

    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String LEFT_SQUARE_BRACKET = "[";
    private static final String RIGHT_SQUARE_BRACKET = "]";

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            includeUtf8(request, response, "/view/login.jsp");

        } catch(java.io.FileNotFoundException e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        String email = request.getParameter("username");
        String password = request.getParameter("hash");
            
        logger.debug("email = "+email);
        logger.debug("password = "+password);

        if(email == null || email.equals(EMPTY)) {
            request.setAttribute("errorMessage", "Please Enter an Email Address");
            includeUtf8(request, response, "/view/login.jsp");
            return;
        }

        try {

            String passwordHash = LoginDatabaseManager.selectPasswordHashByEmail(email);
            logger.debug("passwordHash = "+passwordHash);

            if(
                 passwordHash != null
              && passwordHash.equals(password)
            ) {

                HttpSession httpSession = request.getSession(true); // create the session
                logger.debug("httpSession.getId() = "+httpSession.getId());

                /*
                User user = UserManager.selectUserByEmail(email);
                logger.debug("user = "+user);

                if(user != null) {

                    logger.debug("user.getId() = "+user.getId());

                    UserManager.updateUserLoggedIn(user.getId(), httpSession.getId());

                    // Request attributes
                    request.setAttribute("user", user);
                    //request.setAttribute("userId", user.getId());
                    //request.setAttribute("userName", firstName + SPACE + lastName);
                    //request.setAttribute("userName", name);
                    //request.setAttribute("email", email);
                    //request.setAttribute("humanApiAccessToken", humanApiAccessToken);
                    //request.setAttribute("humanApiPublicToken", humanApiPublicToken);
                    //request.setAttribute("googleId", googleIdString);
                    //request.setAttribute("googleId", id);
                    //request.setAttribute("googleAccessToken", accessToken);

                    // Session attributes
                    //session.setAttribute("user", user);
                    httpSession.setAttribute("userId", user.getId());
                    //session.setAttribute("googleId", googleIdString);
                    //session.setAttribute("googleId", id);
                    //session.setAttribute("humanApiAccessToken", humanApiAccessToken);
                    //session.setAttribute("humanApiPublicToken", humanApiPublicToken);
                    //session.setAttribute("googleAccessToken", accessToken);

                    ////includeUtf8(request, response, "/view/landing.jsp");
                    //String backendUrl = (String)getServletContext().getAttribute("backendUrl");
                    //logger.debug("backendUrl = "+backendUrl);
                    //response.sendRedirect(backendUrl + "/store");

                    includeUtf8(request, response, "/land.jsp");

                    return;
                    
                } // if(user != null) {
                */

                //includeUtf8(request, response, "/land.jsp");
                include(request, response, "/view/blog-list.jsp");

            } else { // if(passwordHash != null

                request.setAttribute("errorMessage", "User Not Found");
                includeUtf8(request, response, "/view/login.jsp");
                return;

            } // if(passwordHash != null

        } catch(java.sql.SQLException sqlException) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            sqlException.printStackTrace(printWriter);
            logger.error(stringWriter.toString());

            logException(sqlException);

        } catch(Exception exception) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //writeOut(response, "-1");
    }

    /*
        public String doubleQuotes(String string) {
        return new StringBuilder().append("\"").append(string).append("\"").toString();
    }
    */

}
