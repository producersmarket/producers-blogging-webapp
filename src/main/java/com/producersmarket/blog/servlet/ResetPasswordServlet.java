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

                //if(user.getEmail() == null) throw new Exception("No email addresses on file.");

                try {

                    //ServletContext servletContext = getServletContext();
                    ServletContext servletContext = getServletContext().getContext("/");
                    logger.debug("servletContext = "+servletContext);

                    String smtpServer = (String)servletContext.getAttribute("smtpServer");
                    String smtpPort = (String)servletContext.getAttribute("smtpPort");
                    String smtpUser = (String)servletContext.getAttribute("smtpUser");
                    String smtpPass = (String)servletContext.getAttribute("smtpPass");
                    String emailAddressSupport = (String)servletContext.getAttribute("emailAddressSupport");
                    String producersRequestEmailTo = (String)servletContext.getAttribute("producersRequestEmailTo");
                    String producersRequestEmailFrom = (String)servletContext.getAttribute("producersRequestEmailFrom");
                        
                    Properties properties = new Properties();

                    properties.put("smtpServer", smtpServer);
                    properties.put("smtpPort", smtpPort);
                    properties.put("smtpUser", smtpUser);
                    properties.put("smtpPass", smtpPass);
                    properties.put("emailAddressSupport", emailAddressSupport);
                    properties.put("producersRequestEmailTo", producersRequestEmailTo);
                    properties.put("producersRequestEmailFrom", producersRequestEmailFrom);

                    logger.debug("properties = "+properties);

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
            e.printStackTrace();
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
