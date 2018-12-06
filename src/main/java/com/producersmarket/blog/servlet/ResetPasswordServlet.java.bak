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
import com.producersmarket.blog.database.UserDatabaseManager;
import com.producersmarket.model.User;
import com.producersmarket.mailer.ResetPasswordMailer;

public class ResetPasswordServlet extends ParentServlet {

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

            includeUtf8(request, response, "/view/reset-password.jsp");

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
            request.setAttribute("errorMessage", "Please Enter an Email Address");
            includeUtf8(request, response, "/view/login.jsp");
            return;
        }

        try {

            User user = UserDatabaseManager.selectUserByEmail(email);

            if(user != null) {

                logger.debug("user = "+user);
                logger.debug("user.getId() = "+user.getId());
                logger.debug("user.getEmail() = "+user.getEmail());

                //if(user.getEmail() == null) throw new Exception("No email addresses on file.");

                try {

                    ServletContext servletContext = getServletContext();
                    logger.debug("servletContext = "+servletContext);
                    ServletContext rootServletContext = servletContext.getContext("/");
                    logger.debug("rootServletContext = "+rootServletContext);

                    String smtpServer = (String)rootServletContext.getAttribute("smtpServer");
                    String smtpPort = (String)rootServletContext.getAttribute("smtpPort");
                    String smtpUser = (String)rootServletContext.getAttribute("smtpUser");
                    String smtpPass = (String)rootServletContext.getAttribute("smtpPass");
                    String emailAddressSupport = (String)rootServletContext.getAttribute("emailAddressSupport");
                    //String resetPasswordEmailTo = (String)rootServletContext.getAttribute("resetPasswordEmailTo");
                    String contextUrl = (String)servletContext.getAttribute("contextUrl");
                    String resetPasswordEmailFrom = (String)servletContext.getAttribute("resetPasswordEmailFrom");
                        
                    logger.debug("resetPasswordEmailFrom = "+resetPasswordEmailFrom);
                    logger.debug("contextUrl = "+contextUrl);

                    Properties properties = new Properties();

                    properties.put("smtpServer", smtpServer);
                    properties.put("smtpPort", smtpPort);
                    properties.put("smtpUser", smtpUser);
                    properties.put("smtpPass", smtpPass);
                    //properties.put("emailAddressSupport", emailAddressSupport);
                    properties.put("emailTo", email);
                    properties.put("emailFrom", resetPasswordEmailFrom);
                    properties.put("contextUrl", contextUrl);

                    //logger.debug("properties = "+properties);

                    //ResetPasswordMailer.send(user, true);
                    ResetPasswordMailer.send(properties, true);

                    include(request, response, "/view/email-sent.jsp");

                    return;

                } catch(Exception exception) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    exception.printStackTrace(printWriter);
                    logger.error(stringWriter.toString());
                }

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

        //writeOut(response, ZERO);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);

    } // doPost()

}
