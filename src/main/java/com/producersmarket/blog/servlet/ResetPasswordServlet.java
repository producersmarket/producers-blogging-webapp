package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.producersmarket.blog.database.LoginDatabaseManager;
import com.producersmarket.blog.mailer.ResetPasswordMailer;

public class ResetPasswordServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String LEFT_SQUARE_BRACKET = "[";
    private static final String RIGHT_SQUARE_BRACKET = "]";
    private String loginPage = "/view/login.jsp";
    public String resetPasswordPage = "/view/reset-password.jsp";
    private String resetPasswordEmailSentPage = "/view/confirmation/reset-password-email-sent.jsp";

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        String loginPage = config.getInitParameter("loginPage");
        if(loginPage != null) this.loginPage = loginPage;
        logger.debug("this.loginPage = " + this.loginPage);

        String resetPasswordPage = config.getInitParameter("resetPasswordPage");
        if(resetPasswordPage != null) this.resetPasswordPage = resetPasswordPage;
        logger.debug("this.resetPasswordPage = " + this.resetPasswordPage);

        String resetPasswordEmailSentPage = config.getInitParameter("resetPasswordEmailSentPage");
        if(resetPasswordEmailSentPage != null) this.resetPasswordEmailSentPage = resetPasswordEmailSentPage;
        logger.debug("this.resetPasswordEmailSentPage = " + this.resetPasswordEmailSentPage);

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String username = null;
            String pathInfo = request.getPathInfo();
            if(pathInfo != null) username = pathInfo.substring(1, pathInfo.length());

            logger.debug("username = "+username);

            request.setAttribute("email", username);

            //includeUtf8(request, response, "/view/reset-password.jsp");
            includeUtf8(request, response, this.resetPasswordPage);

        } catch(java.io.FileNotFoundException e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        String email = request.getParameter("email");

        logger.debug("email = "+email);

        if(email == null || email.equals(EMPTY)) {

            request.setAttribute("emailError", "Please enter an email address");
            includeUtf8(request, response, this.resetPasswordPage);

            return;

        } else {
            email = email.toLowerCase();
        }

        try {

            int userId = LoginDatabaseManager.selectUserIdByEmail(email, getConnectionPool());
            logger.debug("userId = "+userId);
            if(userId != -1) {

                try {

                    ServletContext servletContext = getServletContext();
                    logger.debug("servletContext = " + servletContext);

                    String smtpServer             = (String) servletContext.getAttribute("smtpServer");
                    String smtpPort               = (String) servletContext.getAttribute("smtpPort");
                    String smtpUser               = (String) servletContext.getAttribute("smtpUser");
                    String smtpPass               = (String) servletContext.getAttribute("smtpPass");
                    String emailAddressSupport    = (String) servletContext.getAttribute("emailAddressSupport");
                    String contextUrl             = (String) servletContext.getAttribute("contextUrl");
                    String resetPasswordEmailFrom = (String) servletContext.getAttribute("resetPasswordEmailFrom");
                        
                    Properties properties = new Properties();
                    properties.put("smtpServer", smtpServer);
                    properties.put("smtpPort", smtpPort);
                    properties.put("smtpUser", smtpUser);
                    properties.put("smtpPass", smtpPass);
                    properties.put("emailTo", email);
                    properties.put("emailFrom", resetPasswordEmailFrom);
                    properties.put("contextUrl", contextUrl);
                    
                    //logger.debug(properties);

                    ResetPasswordMailer.send(properties, new com.ispaces.dbcp.ConnectionManager((com.ispaces.dbcp.ConnectionPool)getConnectionPool()), false);
                    request.setAttribute("properties", properties);
                    include(request, response, this.resetPasswordEmailSentPage);
                    return;

                } catch(Exception exception) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    exception.printStackTrace(printWriter);
                    logger.error(stringWriter.toString());
                }

            } else { // if(passwordHash != null
                request.setAttribute("usernameError", "Username does not exist");
                request.setAttribute("emailError", "Email address does not exist");
                includeUtf8(request, response, this.resetPasswordPage);
                return;
            }

        } catch(java.sql.SQLException e) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());

        } catch(Exception exception) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } // doPost()

}
