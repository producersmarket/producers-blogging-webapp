package com.producersmarket.blog.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.json.JSONObject;

//import com.producersmarket.database.UserDatabaseManager;
//import com.producersmarket.util.SecurityUtil;
//import com.producersmarket.util.UniqueId;
//import com.producersmarket.database.SessionDatabaseManager;
//import com.producersmarket.database.OrganizationDatabaseManager;
//import com.producersmarket.model.Constants;
//import com.producersmarket.model.Session;
import com.producersmarket.blog.model.User;
import com.producersmarket.blog.model.UserPassword;
import com.producersmarket.blog.servlet.ParentServlet;

public class LoginServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String LEFT_SQUARE_BRACKET = "[";
    private static final String RIGHT_SQUARE_BRACKET = "]";
    private String loginPage = "/view/login.jsp";
    private String loggedInPage = "/view/home.jsp";

    /**
     * Validates Google reCAPTCHA V2 or Invisible reCAPTCHA.
     * https://developers.google.com/recaptcha/docs/verify
     *
     * @param secretKey Secret key (key given for communication between your site and Google)
     * @param response reCAPTCHA response from client side. (g-recaptcha-response)
     * @return true if validation successful, false otherwise.
     */
    public boolean isCaptchaValid(String secretKey, String response) {
        logger.debug("isCaptchaValid("+secretKey+", "+response+")");

        try {

            String requestUrl = "https://www.google.com/recaptcha/api/siteverify";

            String url = new StringBuilder()
                .append("https://www.google.com/recaptcha/api/siteverify")
                .append("?secret=").append(secretKey)
                .append("&response=").append(response)
                .toString();

            logger.debug(url);

            InputStream inputStream = new URL(url).openStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int codePoint;

            while((codePoint = bufferedReader.read()) != -1) {
                stringBuilder.append((char) codePoint);
            }

            inputStream.close();

            String jsonString = stringBuilder.toString();
            logger.debug(jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);
            logger.debug(jsonObject);
            
            return jsonObject.getBoolean("success");

        } catch (Exception e) {
            logException(e);
        }
            return false;
    }

    /**
     * Validates Google reCAPTCHA V2 or Invisible reCAPTCHA.
     * https://developers.google.com/recaptcha/docs/verify
     *
     * @param secretKey Secret key (key given for communication between your site and Google)
     * @param response reCAPTCHA response from client side. (g-recaptcha-response)
     * @param remoteAddr for added security the address of the client machine can be sent
     * @return true if validation successful, false otherwise.
     */
    public boolean isCaptchaValid(String secretKey, String response, String remoteAddr) {
        logger.debug("isCaptchaValid("+secretKey+", "+response+", "+remoteAddr+")");

        try {

            String requestUrl = "https://www.google.com/recaptcha/api/siteverify";

            String url = new StringBuilder()
                .append("https://www.google.com/recaptcha/api/siteverify")
                .append("?secret=").append(secretKey)
                .append("&remoteip=").append(remoteAddr)
                .append("&response=").append(response)
                .toString();

            logger.debug(url);

            InputStream inputStream = new URL(url).openStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();

            int codePoint;
            while((codePoint = bufferedReader.read()) != -1) {
                stringBuilder.append((char) codePoint);
            }

            inputStream.close();

            String jsonString = stringBuilder.toString();
            logger.debug(jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);
            logger.debug(jsonObject);
            
            return jsonObject.getBoolean("success");

        } catch (Exception e) {
            return false;
        }
    }

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        super.init(config);
    }

    protected void setLoginPage(String loginPage) {
        logger.debug("setLoginPage("+loginPage+")");
        this.loginPage = loginPage;
    }

    protected void setLoggedInPage(String loggedInPage) {
        logger.debug("setLoggedInPage("+loggedInPage+")");
        this.loggedInPage = loggedInPage;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String username = null;
            String pathInfo = request.getPathInfo();
            if(pathInfo != null) username = pathInfo.substring(1, pathInfo.length());

            logger.debug("username = "+username);

            request.setAttribute("username", username);

            includeUtf8(request, response, loginPage);

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
        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        String rememberMe = request.getParameter("rememberMe");

        boolean isRememberMe = false;
        if(rememberMe != null) {
            try {
                isRememberMe = new Boolean(rememberMe).booleanValue();
            } catch(Exception e) {}  // Ignore exception... remember me is not checked.
        }

        logger.debug("email = "+email);
        logger.debug("password = "+password);
        logger.debug("recaptchaResponse = "+recaptchaResponse);
        logger.debug("isRememberMe = "+(isRememberMe));

        if(recaptchaResponse != null) {

            String googleSecretKey = (String) getServletContext().getAttribute("googleSecretKey");
            String googleSiteKey = (String) getServletContext().getAttribute("googleSiteKey");

            logger.debug("googleSecretKey = "+googleSecretKey);
            logger.debug("googleSiteKey = "+googleSiteKey);

            //boolean captchaValid = isCaptchaValid(googleSecretKey, recaptchaResponse, remoteAddr);
            boolean captchaValid = isCaptchaValid(googleSecretKey, recaptchaResponse);
            logger.debug("captchaValid = "+captchaValid);

            if(!captchaValid) {
                request.setAttribute("recaptchaError", "Check the box to verify you are not a robot");
                includeUtf8(request, response, this.loginPage);
                return;
            }
        }

        if(email == null || email.equals(EMPTY)) {

            request.setAttribute("usernameError", "Type your email address");
            includeUtf8(request, response, this.loginPage);
            return;

        } else if(password == null || password.equals(EMPTY)) {

            request.setAttribute("passwordError", "Type your password");
            includeUtf8(request, response, this.loginPage);            
            return;

        } else {
            email = email.toLowerCase();  // ensure email is lowercase
        }

        User user = loginUser(email, password);

        if(user != null) {

            /*
            if(!user.getEmailVerified()) {
                request.setAttribute("errorMessage", "Your email address has not beeen verified");
                request.setAttribute("activationCode", user.getActivationCode());
                request.setAttribute("email", user.getEmail());
                includeUtf8(request, response, "/view/confirmation/email-not-verified.jsp");
                return;
            }
            */

            int userId = user.getId();
            int orgId = user.getOrgId();
            List<Integer> groupIdList = user.getGroupIdList();

            logger.debug("userId = " + userId);
            logger.debug("orgId = " + orgId);
            logger.debug("groupIdList = " + groupIdList);

            HttpSession httpSession = createSession(request, response, user.getId(), isRememberMe); // create the session

            httpSession.setAttribute("userId", userId); // set the userId in the session
            httpSession.setAttribute("orgId", orgId);
            if(groupIdList != null) httpSession.setAttribute("groupIdList", groupIdList); // set the user groups ID list in the session

            String redirect = request.getParameter("redirect");
            logger.debug("redirect = "+redirect);

            if(redirect != null) {
                response.sendRedirect(redirect);
            } else {
                includeUtf8(request, response, this.loggedInPage);
            }

            return;
        }

        request.setAttribute("passwordError", "Incorrect password");
        includeUtf8(request, response, this.loginPage);

    } // doPost(HttpServletRequest request, HttpServletResponse response)

    protected User loginUser(String email, String password) {
        logger.debug("loginUser("+email+", password)");

        int userId = authenticateLogin(email, password);
        if(userId > 0) return populateUser(userId);
        return null;
    }

}
