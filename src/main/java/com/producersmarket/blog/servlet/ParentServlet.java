package com.producersmarket.blog.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.ispaces.dbcp.ConnectionPool;
import com.ispaces.dbcp.ConnectionManager;

import com.producersmarket.blog.database.SessionDatabaseManager;
import com.producersmarket.blog.database.UserDatabaseManager;
import com.producersmarket.blog.model.Constants;
import com.producersmarket.blog.model.Session;
import com.producersmarket.blog.model.User;
import com.producersmarket.blog.model.UserPassword;
import com.producersmarket.blog.util.SecurityUtil;
import com.producersmarket.blog.util.UniqueId;

public class ParentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger();
    public static final String EMPTY = "";
    public static final String UTF_8 = "UTF-8";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML_UTF8 = "text/html;charset=UTF-8";
    public static final String APPLICATION_XJAVASCRIPT = "application/x-javascript";
    public static final String APPLICATION_XJAVASCRIPT_UTF8 = "application/x-javascript;charset=utf-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final String COMMA = ",";
    public static final String LEFT_SQUARE = "[";
    public static final String RIGHT_SQUARE = "]";
    public static final String DOUBLE_QUOTE = "\"";

    private ServletContext servletContext;

    /**
     * Executor to process asynchronous http requests.
     */
    private Executor executor = null;

    public ConnectionPool getConnectionPool() {
        return (ConnectionPool) getServletContext().getAttribute("connectionPool");
    }

    public void logException(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        logger.error(stringWriter.toString());
    }
	
    public String doubleQuotes(String string) {
        return new StringBuilder().append(DOUBLE_QUOTE).append(string).append(DOUBLE_QUOTE).toString();
    }

    public Properties getProperties() {
        //return super.getProperties();
        return (Properties) getServletContext().getAttribute("properties");
    }

    public String getProperty(String name) {
        //return super.getProperty(name);
        return getProperties().getProperty(name);
    }

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init(config)");

        this.servletContext = config.getServletContext();

        this.executor = new ThreadPoolExecutor (
            10
          , 10
          , 50000L
          , java.util.concurrent.TimeUnit.MILLISECONDS
          , new java.util.concurrent.LinkedBlockingQueue<Runnable>(100)
        );
        logger.debug( "this.executor = " + this.executor );

        super.init(config);
    }

    public void debugRequest(HttpServletRequest request) {
        logger.debug("request.getHeader('Host')   = " + request.getHeader("Host"));
        logger.debug("request.getRequestURL()     = " + request.getRequestURL());
        logger.debug("request.getRequestURI()     = " + request.getRequestURI());
        logger.debug("request.getDispatcherType() = " + request.getDispatcherType());
        logger.debug("request.getScheme()         = " + request.getScheme());
        logger.debug("request.getServerName()     = " + request.getServerName());
        logger.debug("request.getServerPort()     = " + request.getServerPort());
        logger.debug("request.getContextPath()    = " + request.getContextPath());
        logger.debug("request.getPathInfo()       = " + request.getPathInfo());
        logger.debug("request.getLocale()         = " + request.getLocale());
        logger.debug("request.getQueryString()    = " + request.getQueryString());
        logger.debug("request.getContentLength()  = " + request.getContentLength());
        logger.debug("request.getRemoteAddr()     = " + request.getRemoteAddr());
        logger.debug("request.getRemoteHost()     = " + request.getRemoteHost());
        logger.debug("request.getRemoteUser()     = " + request.getRemoteUser());
        logger.debug("request.getLocalAddr()      = " + request.getLocalAddr());
        logger.debug("request.getLocalName()      = " + request.getLocalName());
    }

    protected void debugCookies(HttpServletRequest request) {

        Cookie cookie;
        Cookie[] cookies = request.getCookies();
        //logger.debug("cookies = " + cookies);

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                logger.debug("Cookie #"+(i + 1)+": "+cookie.getName()+" = "+cookie.getValue());
            }
        }
    }

    protected void include(HttpServletRequest request, HttpServletResponse response, String jspPath) throws IOException, ServletException {
        include(request, response, jspPath, TEXT_HTML_UTF8);
    }

    protected void include(HttpServletRequest request, HttpServletResponse response, String jspPath, String contentType) throws IOException, ServletException {
        logger.debug(
            new StringBuilder()
                .append("include(request, response, '")
                .append(jspPath)
                .append("', '")
                .append(contentType)
                .append("')")
                .toString()
        );

        response.setContentType(contentType);

        try {

            this.servletContext.getRequestDispatcher(jspPath).include(request, response);

        } catch(java.io.FileNotFoundException e) {

        	logException(e);

            logger.error(
              new StringBuilder()
                .append("FileNotFoundException: include(request, response, '")
                .append(jspPath)
                .append("', '")
                .append(contentType)
                .append("'): e.getMessage() = ")
                .append(e.getMessage())
                .toString()
            );
            
        }
    }

    protected void includeUtf8(HttpServletRequest request, HttpServletResponse response, String path) throws IOException, ServletException {
        includeUtf8(
          request
          , response
          , path
          , TEXT_HTML_UTF8
        );
    }

    /**
     * @param contentType e.g. "text/html;charset=UTF-8";
     */
    protected void includeUtf8(HttpServletRequest request, HttpServletResponse response, String path, String contentType) throws IOException, ServletException {
        logger.debug("includeUtf8(request, response, '"+path+"', '"+contentType+"')");

        response.setContentType(contentType);
        response.setCharacterEncoding(UTF_8);

        try {

            this.servletContext.getRequestDispatcher(path).include(request, response);

        } catch(java.io.FileNotFoundException e) {
            logger.error("FileNotFoundException: include(request, response, '"+path+"', '"+contentType+"'): e.getMessage() = "+e.getMessage());
        	logException(e);
        }

    }

    protected void writeOut(HttpServletResponse response, int responseInt) throws IOException {
        writeOut(response, String.valueOf(responseInt), TEXT_PLAIN);
    }

    protected void writeOut(HttpServletResponse response, String text) throws IOException {
        writeOut(response, text, TEXT_PLAIN);
    }

    protected void writeOut(HttpServletResponse response, String text, String contentType) throws IOException {
        logger.debug("writeOut(request, '"+text+"', '"+contentType+"')");

        response.setHeader("Content-Length", String.valueOf(text.length()));  // Set the "Content-Length" header.

        PrintWriter out = response.getWriter();
        response.setContentType(contentType);
        out.write(text);
        out.flush();
        out.close();
    }

    protected void writeJsonResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        logger.debug("writeJsonResponse(response, "+statusCode+", "+message+")");

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("status", statusCode);
        responseJsonObject.put("message", message);    
        logger.debug(responseJsonObject);

        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseJsonObject);
        printWriter.flush();
    }

    protected void writeJsonErrorResponse(HttpServletResponse response, int statusCode, Exception e) throws IOException {
        logger.debug("writeJsonErrorResponse(response, "+statusCode+", "+e+")");

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject errorJsonObject = new JSONObject();
        errorJsonObject.put("error", e);

        PrintWriter printWriter = response.getWriter();
        printWriter.print(errorJsonObject);
        printWriter.flush();
    }

    protected void writeJsonObjectResponse(HttpServletResponse response, JSONObject jsonObject) throws IOException {
        logger.debug("writeJsonObjectResponse(response, "+jsonObject+")");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonObject);
        printWriter.flush();
    }

    public int getUserId(HttpServletRequest request) { // throws IOException, ServletException {
        logger.debug("getUserId(request)");

        HttpSession session = request.getSession(false);
        logger.debug("session = "+session);
        if(session != null) {
            Integer userIdInteger = (Integer) session.getAttribute("userId");
            if(userIdInteger != null) return userIdInteger.intValue();
        }
        return -1;
    }

    protected void execute(Runnable runnable) {
        logger.debug("execute(runnable)");

        this.executor.execute(runnable);
    }

    protected Executor getExecutor() {
        logger.debug("getExecutor()");

        return this.executor;
    }

    protected void logoutUser(HttpServletRequest request, HttpServletResponse response) throws java.sql.SQLException, Exception {
        logger.debug("logoutUser(request, response)");

        HttpSession httpSession = request.getSession(false);
        if(httpSession != null) {

            String sessionId = httpSession.getId();
            Integer userId = (Integer)httpSession.getAttribute("userId");

            logger.debug("sessionId = " + sessionId);
            logger.debug("userId = " + userId);

            httpSession.removeAttribute("userId");
            logger.debug("httpSession.getAttribute(\"userId\") = " + httpSession.getAttribute("userId")); // confirm the userId is gone

            httpSession.invalidate();
            logger.debug("request.getSession(false) = " + request.getSession(false)); // confirm the session is gone

        } // if(httpSession != null)

        String cookieDomain = ( (java.util.Properties) getServletContext().getAttribute("properties") ).getProperty("session.cookie.config.domain");
        // remove cookie __Secure-Session-Id
        Cookie sessionIdCookie = getCookie( request, Constants.SESSION_ID_COOKIE_NAME );
        if(sessionIdCookie != null) {
            sessionIdCookie.setMaxAge(0);
            sessionIdCookie.setHttpOnly(true);
            sessionIdCookie.setSecure(true);
            sessionIdCookie.setPath("/");
            sessionIdCookie.setDomain(cookieDomain);
            response.addCookie(sessionIdCookie);
        }
        // remove cookie __Secure-Remember-Me
        Cookie rememberMeCookie = getCookie( request, Constants.REMEMBER_ME_COOKIE_NAME );
        if(rememberMeCookie != null) {
            rememberMeCookie.setMaxAge(0);
            rememberMeCookie.setHttpOnly(true);
            rememberMeCookie.setSecure(true);
            rememberMeCookie.setPath("/");
            rememberMeCookie.setDomain(cookieDomain);
            response.addCookie(rememberMeCookie);
        }

    }

    /*
     * Loops over the cookies until it finds the cookie by name and returns it
     */
    public Cookie getCookie( HttpServletRequest request, String cookieName ) throws IOException, ServletException {
        logger.debug("getCookie(request, "+cookieName+")");

        Cookie[] cookies = request.getCookies();

        if ( cookies != null ) {
            Cookie cookie;
            for ( int i = 0; i < cookies.length; i++ ) {
                cookie = cookies[i];

                if(cookie.getName().equals( cookieName )) {

                    logger.debug( new StringBuilder()
                        .append( "Cookie #" ).append( i + 1 )
                        .append( ": " ).append( cookie.getName() )
                        .append( " = " ).append( cookie.getValue() )
                        .toString() );

                    return cookie;
                }
            }
        }

        return null;
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("redirectToLogin(request, response)");

        StringBuilder requestUriBuilder = new StringBuilder();
        requestUriBuilder.append(request.getRequestURI());
        if(request.getQueryString() != null) requestUriBuilder.append("?").append(request.getQueryString());

        logger.debug("redirectAterLogin = " + requestUriBuilder.toString());

        //request.setAttribute("redirectAterLogin", requestUriBuilder.toString());
        response.sendRedirect("login?redirect="+requestUriBuilder.toString());
    }

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

            String jsonString = stringBuilder.toString();

            inputStream.close();

            JSONObject jsonObject = new JSONObject(jsonString);
            logger.debug(jsonObject);
            
            return jsonObject.getBoolean("success");

        } catch (Exception e) {
        	logException(e);
            return false;
        }
    }

    /**
     * 
     * Method that will allow us to authenticate a user based on its password.
     * 
     * @param email
     * @param password
     * @return
     */
    protected int authenticateLogin(String email, String password) {
        logger.debug("authenticateLogin("+email+", password)");

        try {

            UserPassword userPassword = UserDatabaseManager.selectUserPasswordByEmail(email, getConnectionPool());
            String passwordHash = userPassword.getPasswordHash();
            logger.debug("passwordHash = "+passwordHash);

            if(
                 passwordHash != null
              && passwordHash.equals(password)
            ) {
                return userPassword.getId();
            }

        } catch(Exception exception) {
        	logException(exception);
        }

        return -1;
    }

    protected User populateUser(int userId) {
        logger.debug("populateUser("+userId+")");

        try {

            User user = UserDatabaseManager.selectUserById(userId, getConnectionPool());
            logger.debug("user = "+user);
            return user;
            
        } catch(java.sql.SQLException exception) {
            logException(exception);
        } catch(Exception exception) {
            logException(exception);
        }

        return null;
    }

    /*
     * Create the session, add the cookies and save the session ID in the database.
     */
    protected HttpSession createSession (
          HttpServletRequest request
        , HttpServletResponse response
        , int userId
        , boolean rememberMe
    ) {
        logger.debug("createSession(request, response, "+userId+", "+rememberMe+")");

        // Debugging
        //super.printSessionCookieConfig( getServletContext().getSessionCookieConfig() );

        HttpSession httpSession = request.getSession(true); // create the session and sets the "JSESSIONID" cookie header
        logger.debug( "httpSession.getId() = " + httpSession.getId() );
        logger.debug( "httpSession.getMaxInactiveInterval() = " + httpSession.getMaxInactiveInterval() ); // Returns the maximum time interval, in seconds, that the servlet container will keep this session open between client accesses.

        String cookieDomain = ( (java.util.Properties) getServletContext().getAttribute("properties") ).getProperty("session.cookie.config.domain");
        logger.debug( "cookieDomain = " + cookieDomain );

        if( rememberMe ) {
            this.setRememberMeCookie( httpSession, response, cookieDomain );
            this.setSessionIdCookie( httpSession, response, cookieDomain, Constants.SIXTY_DAYS_IN_SECONDS );
            httpSession.setMaxInactiveInterval( Constants.SIXTY_DAYS_IN_SECONDS );
        } else {
            this.setSessionIdCookie( httpSession, response, cookieDomain, Constants.THREE_HOURS_IN_SECONDS );
            httpSession.setMaxInactiveInterval( Constants.THREE_HOURS_IN_SECONDS );
        }

        this.saveSession ( request, httpSession, userId ); // save the session along with the userId

        return httpSession;
    }

    /*
     * Recreate the session without saving the sessionId in the database.
     */
    protected HttpSession recreateSession (
          HttpServletRequest request
        , HttpServletResponse response
        , int userId
        , boolean rememberMe
    ) {
        logger.debug("recreateSession(request, response, "+userId+", "+rememberMe+")");

        // invalidate current session
        request.getSession(false).invalidate();

        // create new session
        HttpSession httpSession = request.getSession(true); // create the session and sets the "JSESSIONID" cookie header
        logger.debug( "httpSession.getId() = " + httpSession.getId() );
        logger.debug( "httpSession.getMaxInactiveInterval() = " + httpSession.getMaxInactiveInterval() ); // Returns the maximum time interval, in seconds, that the servlet container will keep this session open between client accesses.

        String cookieDomain = ( (java.util.Properties) getServletContext().getAttribute("properties") ).getProperty("session.cookie.config.domain");
        logger.debug( "cookieDomain = " + cookieDomain );

        // get the JSESSIONID cookie to see if we can update the expiry
        Cookie jsessionIdCookie = null;
        try {
            jsessionIdCookie = getCookie( request, Constants.JSESSIONID );
            logger.debug( "jsessionIdCookie = " + jsessionIdCookie );
        } catch(Exception e) {
            logException(e);
        }

        if( rememberMe ) {
            this.setRememberMeCookie( httpSession, response, cookieDomain );
            this.setSessionIdCookie( httpSession, response, cookieDomain, Constants.SIXTY_DAYS_IN_SECONDS );
            httpSession.setMaxInactiveInterval( Constants.SIXTY_DAYS_IN_SECONDS );
            if(jsessionIdCookie != null) jsessionIdCookie.setMaxAge( Constants.SIXTY_DAYS_IN_SECONDS ); // this doesn't seem to work, attempting to update the cookie expiry of the JSESSIONID cookie
                                                                                                        // this is why we are using the custom session ID cookie
        } else {
            this.setSessionIdCookie( httpSession, response, cookieDomain, Constants.THREE_HOURS_IN_SECONDS );
            httpSession.setMaxInactiveInterval( Constants.THREE_HOURS_IN_SECONDS );
            if(jsessionIdCookie != null) jsessionIdCookie.setMaxAge( Constants.THREE_HOURS_IN_SECONDS ); // this doesn't seem to work, attempting to update the cookie expiry of the JSESSIONID cookie
        }

        this.saveSession ( request, httpSession, userId ); // save the session along with the userId

        return httpSession;
    }

    protected void saveSession (
          HttpServletRequest request
        , HttpSession httpSession
        , int userId
    ) {
        logger.debug("saveSession(request, httpSession, "+userId+")");

        String sessionId = httpSession.getId(); // Returns a string containing the unique identifier assigned to this session.
        long creationTime = httpSession.getCreationTime(); // Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
        long lastAccessedTime = httpSession.getLastAccessedTime(); // Returns the last time the client sent a request associated with this session, as the number of milliseconds since midnight January 1, 1970 GMT, and marked by the time the container received the request.
        int maxInactiveInterval = httpSession.getMaxInactiveInterval(); // Returns the maximum time interval, in seconds, that the servlet container will keep this session open between client accesses.

        logger.debug("  sessionId = "+ sessionId);
        //logger.debug("creationTime = "+ creationTime);
        //logger.debug("lastAccessedTime = "+ lastAccessedTime);
        logger.debug("  maxInactiveInterval = "+ maxInactiveInterval);

        String serverInfo = getServletContext().getServerInfo();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String accept = request.getHeader("Accept");
        String acceptEncoding = request.getHeader("Accept-Encoding");
        String acceptCharset = request.getHeader("Accept-Charset");
        String acceptLanguage = request.getHeader("Accept-Language");
        String host = request.getHeader("Host");
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String locale = (request.getLocale()).toString();
        String characterEncoding = request.getCharacterEncoding();
        String remoteAddr = request.getRemoteAddr();
        String protocol = request.getProtocol();

        Session session = new Session();
        session.setUserId( userId);
        session.setSessionId( httpSession.getId());
        session.setServerInfo( serverInfo );
        session.setServerName( serverName );
        session.setServerPort( serverPort );
        session.setRemoteAddr( remoteAddr );
        session.setLocale( locale );
        session.setCharacterEncoding( characterEncoding );
        session.setUserAgent( userAgent );
        session.setAccept( accept );
        session.setAcceptEncoding( acceptEncoding );
        session.setAcceptCharset( acceptCharset );
        session.setAcceptLanguage( acceptLanguage );
        session.setHost( host );
        session.setReferer( referer );
        session.setProtocol( protocol );

        try {

            SessionDatabaseManager.insert(session, getConnectionPool());

        } catch(Exception exception) {
            logException(exception);
        }

        //debugCookies(request);
    }

    private String setRememberMeCookie( HttpSession httpSession, HttpServletResponse response, String cookieDomain ) {
        logger.debug("setRememberMeCookie(httpSession, response, cookieDomain)");

        String uniqueId = UniqueId.getUniqueId();
        String millis = String.valueOf(System.currentTimeMillis());
        String hashToken = SecurityUtil.makeAuthToken( millis, uniqueId );
        logger.debug("hashToken = " + hashToken);

        httpSession.setAttribute( Constants.REMEMBER_ME_COOKIE_NAME, hashToken ); // set the remember me hash token as an attribute in the session

        // __Secure-Remember-Me
        Cookie rememberMeCookie = new Cookie( Constants.REMEMBER_ME_COOKIE_NAME, hashToken );
        rememberMeCookie.setMaxAge( Constants.SIXTY_DAYS_IN_SECONDS );
        rememberMeCookie.setHttpOnly( true );
        rememberMeCookie.setSecure( true );
        rememberMeCookie.setPath( "/" );
        rememberMeCookie.setDomain( cookieDomain );
        logger.debug( "rememberMeCookie = " + rememberMeCookie.getName() );
        response.addCookie( rememberMeCookie );

        return hashToken;
    }

    private void setSessionIdCookie( HttpSession httpSession, HttpServletResponse response, String cookieDomain, int maxAge ) {
        logger.debug("setSessionIdCookie(httpSession, response, "+cookieDomain+", "+maxAge+")");

        httpSession.setAttribute( Constants.SESSION_ID_COOKIE_NAME, httpSession.getId() ); // set the remember me hash token as an attribute in the session

        // __Secure-Session-Id
        Cookie sessionIdCookie = new Cookie( Constants.SESSION_ID_COOKIE_NAME, httpSession.getId() );
        sessionIdCookie.setMaxAge( maxAge );
        sessionIdCookie.setHttpOnly( true );
        sessionIdCookie.setSecure( true );
        sessionIdCookie.setPath( "/" );
        sessionIdCookie.setDomain( cookieDomain );
        response.addCookie( sessionIdCookie );
    }

}
