package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/*
import com.ispaces.manager.ForgotPasswordManager;
import com.ispaces.manager.UserManager;
import com.ispaces.model.User;
import com.ispaces.util.ResourceBundles;
import com.ispaces.util.SecurityUtil;
*/
import com.producersmarket.blog.database.UserDatabaseManager;
import com.producersmarket.blog.database.ResetPasswordDatabaseManager;
import com.producersmarket.model.User;

public class PasswordResetServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();

    /*
    private static final String USERNAME = "username";
    private static final String FORGOT_PASSWORD_EMAIL_SENT_CONFIRMATION = "forgot.password.email.sent.confirmation";
    private static final String RESET_PASSWORD_EMAIL_SENT = "reset.password.email.sent";
    private static final String FORGOT_PASSWORD_EMAIL_INVALID = "forgot.password.email.invalid";
    private static final String FORGOT_PASSWORD_USER_NOT_FOUND = "forgot.password.user.not.found";
    */

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String code = null;
            String pathInfo = request.getPathInfo();
            if(pathInfo != null) code = pathInfo.substring(1, pathInfo.length());

            //logger.debug("doGet(request, response): code = '"+code+QUOTE);

            User user = UserDatabaseManager.selectUserByPasswordResetCode(code);

            if(user != null) {

                if(user.getEmail() == null) throw new Exception("No email address on file.");

                request.setAttribute("code", code);
                
                include(request, response, "/view/password-reset.jsp", "text/html; charset=UTF-8");

                return;

            } else {

                logger.warn("User Not Found. Password reset token may have expired or been used already. Possible hack attempt.");

                /*
                java.util.ResourceBundle rb = null;
                rb = (java.util.ResourceBundle)request.getAttribute("rb");
                if(rb == null) {
                    HttpSession session = request.getSession(false);
                    rb = (java.util.ResourceBundle)session.getAttribute("rb");
                }
                if(rb == null) rb = (java.util.ResourceBundle)getServletContext().getAttribute("rb");
                if(rb == null) rb = java.util.ResourceBundle.getBundle("iSpaces", com.ispaces.web.servlet.InitServlet.DEFAULT_LOCALE);
                */

                /*
                String lang = (String)request.getAttribute("lang");
                if(lang == null) {
                    lang = (String)getServletContext().getAttribute("lang");
                    if(lang == null) {
                        //lang = com.ispaces.servlet.InitServlet.DEFAULT_LOCALE.getLanguage();
                        lang = java.util.Locale.getDefault().getLanguage();
                        if(lang == null) lang = "en";
                    }
                }

                ResourceBundles rb = new ResourceBundles(lang);

                String FORGOT_PASSWORD_USER_NOT_FOUND = "forgot.password.user.not.found";
                */

                //String message = rb.getString(FORGOT_PASSWORD_USER_NOT_FOUND);
                String message = "Password reset token has expired or been used.";

                /*
                PrintWriter out = response.getWriter();
                response.setContentType(TEXT_PLAIN);
                out.write(message);
                out.close();
                */

                request.setAttribute("message", message);

                //include(request, response, DIR_VIEW + "confirmationMessage.jsp");
                include(request, response, "/view/confirmation-message.jsp");

                return;
            }

        } catch(Exception e) {

            logger.warn("Error in ResetPasswordControl: "+e.getMessage());
            e.printStackTrace();

        }

        //writeOut(response, ZERO);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        String code = request.getParameter("code");
        String hash = request.getParameter("hash");

        //String password = request.getParameter("password");
        //String confirmPassword = request.getParameter("confirmPassword");

        logger.debug("code = '"+code+"'");
        logger.debug("hash = '"+hash+"'");
        //logger.debug("doPost(request, response): password = '"+password+"', confirmPassword = '"+confirmPassword+"'");

        /*
        if(
            password != null
            && confirmPassword != null
            && !password.equals(EMPTY)
            && confirmPassword != null
            && password.equals(confirmPassword)
        ) {
        */

            try {

                User user = UserDatabaseManager.selectUserByPasswordResetCode(code);

                if(user != null) {

                    /*
                    String passwordHash = SecurityUtil.hashPassword(password);
                    logger.debug("passwordHash = "+passwordHash);

                    // Some passwords are less than 40 characters
                    int passwordHashLength = passwordHash.length();
                    logger.debug("passwordHashLength = "+passwordHashLength);
                    if(passwordHashLength < 40) {
                        if(passwordHashLength == 39) {
                            passwordHash = "0"+passwordHash;
                        } else if(passwordHashLength == 38) {
                            passwordHash = "00"+passwordHash;
                        }
                        logger.debug("passwordHash = "+passwordHash);
                    }
                    */

                    UserDatabaseManager.updatePassword(user.getId(), hash);

                    //String message = "Your password has been reset.<br/>You can log in below.";
                    String message = "Your password has been reset.<br/>Please log in below.";

                    request.setAttribute("message", message);

                    //response.sendRedirect(com.ispaces.web.servlet.InitServlet.init.getProperty("contextUrl"));
                    //response.sendRedirect(com.ispaces.web.servlet.InitServlet.init.getProperty("contextUrl")+"/admin/login");
                    //include(request, response, DIR_VIEW+"admin/login.jsp", "text/html; charset=UTF-8");
                    include(request, response, "/view/login.jsp");

                    //ResetPasswordDatabaseManager.deleteActivationCode(user.getId(), code);  // Delete the reset code after it has been used.
                    ResetPasswordDatabaseManager.deleteActivationCode(user.getId());  // Delete the reset code after it has been used.
                }

            } catch(java.sql.SQLException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }

        /*
        } else {

            //doGet(request, response);

            String errorMessage = "Passwords do not match";
            if(password.equals(EMPTY)) errorMessage = "Enter your new password";

            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("code", code);
            include(request, response, DIR_VIEW+"resetpassword.jsp", "text/html; charset=UTF-8");

        }
        */

    }

}


