package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.producersmarket.blog.database.ResetPasswordDatabaseManager;

public class PasswordResetServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();

    private String passwordResetPage = "/view/password-reset.jsp";
    private String loginPage = "/view/login.jsp";
    private String resetPasswordEmailSentPage = "/view/confirmation/reset-password-email-sent.jsp";

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        String passwordResetPage = config.getInitParameter("passwordResetPage");
        if(passwordResetPage != null) this.passwordResetPage = passwordResetPage;
        logger.debug("this.passwordResetPage = " + this.passwordResetPage);

        String loginPage = config.getInitParameter("loginPage");
        if(loginPage != null) this.loginPage = loginPage;
        logger.debug("this.loginPage = " + this.loginPage);

        String resetPasswordEmailSentPage = config.getInitParameter("resetPasswordEmailSentPage");
        if(resetPasswordEmailSentPage != null) this.resetPasswordEmailSentPage = resetPasswordEmailSentPage;
        logger.debug("this.resetPasswordEmailSentPage = " + this.resetPasswordEmailSentPage);

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String code = null;
            String pathInfo = request.getPathInfo();
            if(pathInfo != null) code = pathInfo.substring(1, pathInfo.length());

            int userId = ResetPasswordDatabaseManager.selectUserIdByPasswordResetToken(code, getConnectionPool());
            logger.debug("userId = "+userId);
            if(userId != -1) {

                request.setAttribute("code", code);
                includeUtf8(request, response, this.passwordResetPage);
                return;

            } else {

                logger.warn("User Not Found. Password reset token may have expired or been used already.");
                String header = "Whoops! Looks like this link has expired.";
                String message = "Request a new <a href='reset-password'>reset link</a>";
                request.setAttribute("header", header);
                request.setAttribute("message", message);
                includeUtf8(request, response, "/view/confirmation-message.jsp");

                return;
            }

        } catch(Exception e) {

            logger.warn("Error in "+getClass().getName()+": "+e.getMessage());
            e.printStackTrace();

        }

        //writeOut(response, ZERO);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        String code = request.getParameter("code");
        String hash = request.getParameter("hash");

        logger.debug("code = "+code);
        logger.debug("hash = "+hash);
        
        if( hash == null || hash.equals(EMPTY) ) {

            String errorMessage = "Please enter a password";
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("code", code);
            include(request, response, this.passwordResetPage, "text/html; charset=UTF-8");

        } else {

            try {
    
                int userId = ResetPasswordDatabaseManager.selectUserIdByPasswordResetToken(code, getConnectionPool());
    
                if(userId != -1) {
    
                    ResetPasswordDatabaseManager.updatePassword(userId, hash, getConnectionPool());
                    String message = "Your password has been reset.<br/>Please log in below.";
                    request.setAttribute("message", message);
                    includeUtf8(request, response, this.loginPage);
                    ResetPasswordDatabaseManager.deleteResetToken(userId, getConnectionPool());  // Delete the reset code after it has been used.
                }
    
            } catch(java.sql.SQLException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
                String errorMessage = "Error: please try again later";
                request.setAttribute("errorMessage", errorMessage);
                request.setAttribute("code", code);
                include(request, response, this.passwordResetPage, "text/html; charset=UTF-8");
            }
        }
    }

}


