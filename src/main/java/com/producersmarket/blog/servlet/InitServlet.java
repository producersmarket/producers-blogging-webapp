package com.producersmarket.blog.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
//import java.util.logging.Logger;
import java.util.Properties;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionPool;
import com.ispaces.dbcp.ConnectionManager;

import com.producersmarket.blog.database.BlogCategoryDatabaseManager;
import com.producersmarket.blog.model.BlogCategory;
import com.producersmarket.blog.model.Menuable;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet(
    name = "InitServlet"
    , urlPatterns = { "/InitServlet" }
    , loadOnStartup = 1
    //, initParams = {
    //    @WebInitParam(name = "init.properties", value = "WEB-INF/conf/init.properties" )
    //}
)
public class InitServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(InitServlet.class.getName());

    /**
     * Static reusable variables.
     */
    public static final String EMPTY = "";
    public static final String COLON = ":";
    public static final String FORWARDSLASH = "/";
    public static final String EIGHTY = "80";

    /**
     * Internal reference to the ServletContext object.
     */
    public ServletContext servletContext;

    /**
     * The init properties
     */
    public Properties properties = null;

    /**
     * A database connection pool to use upon start up.
     */
    private ConnectionPool connectionPool;

    /**
     * A database connection manager
     */
    //private ConnectionManager connectionManager;

    public ConnectionPool getConnectionPool() {
        logger.debug("getConnectionPool()");

        logger.debug("this = "+this);
        logger.debug("this.connectionPool = "+this.connectionPool);

        return this.connectionPool;
    }

    /*
    public ConnectionManager getConnectionManager() {
        logger.debug("getConnectionManager()");

        logger.debug("this = "+this);
        logger.debug("this.connectionManager = "+this.connectionManager);

        return this.connectionManager;
    }
    */

    /**
     * A boolean flag to make sure the init() method is only called once for this servlet.
     * Gets set to true when Servlet has been initialized.
     */
    //private static boolean inited = false;
    private boolean inited = false;

    //public static com.ispaces.obfuscator.UnicodeObfuscator obfuscator = null;  // 'public' - make the static obfuscator available publicly.

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");
        javaLogger.info("init("+config+")");

        logger.info("InitServlet.class.getName() = "+InitServlet.class.getName());
        logger.info("getClass().getName() = "+getClass().getName());

        servletContext = config.getServletContext();
        logger.debug("servletContext = "+servletContext);
        javaLogger.info("servletContext = "+servletContext);

        String contextInitialized = (String)servletContext.getAttribute("contextInitialized");
        logger.debug("contextInitialized = "+contextInitialized);

        if(contextInitialized == null) {

            try {

                java.io.InputStream initPropertiesIputStream = servletContext.getResourceAsStream("/WEB-INF/init.properties");

                //this.properties = (Properties)com.ispaces.util.PropertiesUtil.getProperties(initPropertiesIputStream);
                this.properties = new Properties();

                try {

                    this.properties.load(initPropertiesIputStream);

                    servletContext.setAttribute("properties", this.properties); // Make the properties available in the ServletContext

                } catch(java.io.IOException ioException) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    ioException.printStackTrace(printWriter);
                    logger.error(stringWriter.toString());
                }

                logger.debug(this.properties);

                String protocol    = properties.getProperty("protocol");
                String server      = properties.getProperty("server");
                String port        = properties.getProperty("port");
                String contextPath = properties.getProperty("context-path");

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(protocol != null ? protocol : "http");
                stringBuilder.append("://");
                stringBuilder.append(server != null ? server : "localhost");
                if(!EMPTY.equals(port) && !EIGHTY.equals(port)) stringBuilder.append(COLON).append(port);

                String serverUrl = stringBuilder.toString();

                if(contextPath != null && !contextPath.equals(EMPTY)) {
                    stringBuilder.append(contextPath);
                    if(!contextPath.equals(FORWARDSLASH)) stringBuilder.append("/");
                } else {
                    stringBuilder.append("/");
                }

                String contextUrl = stringBuilder.toString();

                logger.debug("protocol = "+protocol);
                logger.debug("server = "+server);
                logger.debug("port = "+port);
                logger.debug("contextPath = "+contextPath);
                logger.debug("serverUrl = "+serverUrl);
                logger.debug("contextUrl = "+contextUrl);

                // managing secrets using Dotenv
                Dotenv dotenv = Dotenv.load();

                String smtpServer = properties.getProperty("smtp.server");
                String smtpPort = properties.getProperty("smtp.port");
                String smtpUser = properties.getProperty("smtp.user");
                String smtpPass = dotenv.get("SMTP_PASS");
                String emailAddressSupport = properties.getProperty("email.address.support");
                String producersRequestEmailTo = properties.getProperty("producers.request.email.to");
                String producersRequestEmailFrom = properties.getProperty("producers.request.email.from");
                String resetPasswordEmailFrom = properties.getProperty("reset-password.email.from");
                //String googleSecretKey = properties.getProperty("google.secret.key");
                String googleSecretKey = dotenv.get("JSP_GOOGLE_SECRET_KEY");
                //String googleSiteKey = properties.getProperty("google.site.key");
                String googleSiteKey = dotenv.get("JSP_GOOGLE_SITE_KEY");
                
                logger.debug("smtpServer = "+smtpServer);
                logger.debug("smtpPort = "+smtpPort);
                logger.debug("smtpUser = "+smtpUser);
                logger.debug("smtpPass = "+smtpPass);
                logger.debug("emailAddressSupport = "+emailAddressSupport);
                logger.debug("producersRequestEmailTo = "+producersRequestEmailTo);
                logger.debug("producersRequestEmailFrom = "+producersRequestEmailFrom);
                logger.debug("resetPasswordEmailFrom = "+resetPasswordEmailFrom);
                logger.debug("googleSecretKey = "+googleSecretKey);
                logger.debug("googleSiteKey = "+googleSiteKey);

                properties.setProperty("serverUrl", serverUrl);
                properties.setProperty("contextUrl", contextUrl);
                
                servletContext.setAttribute("serverUrl", serverUrl);
                servletContext.setAttribute("contextUrl", contextUrl);
                servletContext.setAttribute("smtpServer", smtpServer);
                servletContext.setAttribute("smtpPort", smtpPort);
                servletContext.setAttribute("smtpUser", smtpUser);
                servletContext.setAttribute("smtpPass", smtpPass);
                servletContext.setAttribute("emailAddressSupport", emailAddressSupport);
                servletContext.setAttribute("producersRequestEmailTo", producersRequestEmailTo);
                servletContext.setAttribute("producersRequestEmailFrom", producersRequestEmailFrom);
                servletContext.setAttribute("resetPasswordEmailFrom", resetPasswordEmailFrom);
                servletContext.setAttribute("googleSecretKey", googleSecretKey);
                servletContext.setAttribute("googleSiteKey", googleSiteKey);
                servletContext.setAttribute("googleSiteKey", googleSiteKey);

                /*
                 * Database Connection Pool
                 * JDBC Properties
                 */

                /*                
                String jdbcDriver = properties.getProperty("database-driver");
                String databaseUrl = properties.getProperty("database-url");
                String databaseUsername = properties.getProperty("database-username");
                String databasePassword = properties.getProperty("database-password");

                logger.debug("jdbcDriver = "+jdbcDriver);
                logger.debug("databaseUrl = "+databaseUrl);
                logger.debug("databaseUsername = "+databaseUsername);
                logger.debug("databasePassword = "+databasePassword);

                properties.setProperty("databaseUrl", databaseUrl);
                servletContext.setAttribute("databaseUrl", databaseUrl);
                properties.setProperty("jdbcDriver", jdbcDriver);
                servletContext.setAttribute("jdbcDriver", jdbcDriver);
                properties.setProperty("databaseUsername", databaseUsername);
                servletContext.setAttribute("databaseUsername", databaseUsername);
                properties.setProperty("databasePassword", databasePassword);
                servletContext.setAttribute("databasePassword", databasePassword);

                java.util.Properties connectionPoolProperties = new java.util.Properties();
                connectionPoolProperties.setProperty("id", "-1");
                connectionPoolProperties.setProperty("url", databaseUrl);
                connectionPoolProperties.setProperty("driver", jdbcDriver);
                connectionPoolProperties.setProperty("username", databaseUsername);
                connectionPoolProperties.setProperty("password", databasePassword);
                connectionPoolProperties.setProperty("minConns", "2");
                connectionPoolProperties.setProperty("maxConns", "10");
                connectionPoolProperties.setProperty("maxAgeDays", "0.1");
                connectionPoolProperties.setProperty("maxIdleSeconds", "60");
                logger.debug("connectionPoolProperties = "+connectionPoolProperties);

                this.connectionPool = new ConnectionPool(connectionPoolProperties);
                logger.debug("this.connectionPool = "+this.connectionPool);

                //com.ispaces.database.connection.ConnectionManager.loadStatements(this.connectionPool);
                //com.ispaces.database.manager.JavaClassManager.init(this.connectionPool);
                //com.ispaces.database.manager.ConnectionPoolManager.initConnectionPoolMap(this.connectionPool);

                /*
                List<Product> blogCategoryList = BlogCategoryDatabaseManager.selectBlogCategoriesOrderByPriority();
                servletContext.setAttribute("blogCategoryList", blogCategoryList);
                if(blogCategoryList != null) logger.debug("blogCategoryList.size() = "+blogCategoryList.size());
                */

                logger.debug("getClass().getResourceAsStream(\"prepared-statements.properties\") = " + getClass().getResourceAsStream("prepared-statements.properties"));
                logger.debug("getClass().getResourceAsStream(\"/prepared-statements.properties\") = " + getClass().getResourceAsStream("/prepared-statements.properties"));
                logger.debug("Thread.currentThread().getContextClassLoader().getResourceAsStream(\"prepared-statements.properties\") = " + Thread.currentThread().getContextClassLoader().getResourceAsStream("prepared-statements.properties"));
                logger.debug("Thread.currentThread().getContextClassLoader().getResourceAsStream(\"/prepared-statements.properties\") = " + Thread.currentThread().getContextClassLoader().getResourceAsStream("/prepared-statements.properties"));

                //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                //logger.debug("classLoader = " + classLoader);
                //java.io.InputStream preparedStatementsInputStream = classLoader.getResourceAsStream("prepared-statements.properties");
                //if(preparedStatementsInputStream == null) preparedStatementsInputStream = getClass().getResourceAsStream("/prepared-statements.properties");

                java.io.InputStream preparedStatementsInputStream = servletContext.getResourceAsStream("/WEB-INF/classes/prepared-statements.properties");
                logger.debug("preparedStatementsInputStream = " + preparedStatementsInputStream);
                if(preparedStatementsInputStream == null) preparedStatementsInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("prepared-statements.properties");
                logger.debug("preparedStatementsInputStream = " + preparedStatementsInputStream);

                com.ispaces.dbcp.ConnectionManager.loadStatements(preparedStatementsInputStream);

                /*
                 * Configure the javax.servlet.SessionCookieConfig.
                 */
                //this.configureCookies(properties);
               
            } catch(Exception exception) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);
                logger.error(stringWriter.toString());
            }

            servletContext.setAttribute("contextInitialized", "true");

        } else {

            logger.debug("Servlet Already Inited!!");

        } // if(contextInitialized != null) {

        this.connectionPool = (ConnectionPool) servletContext.getAttribute("connectionPool");
        logger.debug("this.connectionPool = "+this.connectionPool);

        if(this.connectionPool == null) {

            String jdbcDriver = properties.getProperty("database-driver");
            String databaseUrl = properties.getProperty("database-url");
            String databaseUsername = properties.getProperty("database-username");
            String databasePassword = properties.getProperty("database-password");
            String databaseMinimumConnections = properties.getProperty("database-mininimum-connections");
            String databaseMaximumConnections = properties.getProperty("database-maximum-connections");
            String databaseConnectionMaximumAgeDays = properties.getProperty("database-connection-maximum-age-days");
            String databaseConnectionMaximumIdleSeconds = properties.getProperty("database-connection-maximum-idle-seconds");

            logger.debug("jdbcDriver = " + jdbcDriver);
            logger.debug("databaseUrl = " + databaseUrl);
            logger.debug("databaseUsername = " + databaseUsername);
            logger.debug("databasePassword = " + databasePassword);
            logger.debug("databaseMinimumConnections = " + databaseMinimumConnections);
            logger.debug("databaseMinimumConnections = " + databaseMinimumConnections);
            logger.debug("databaseConnectionMaximumAgeDays = " + databaseConnectionMaximumAgeDays);
            logger.debug("databaseConnectionMaximumIdleSeconds = " + databaseConnectionMaximumIdleSeconds);

            properties.setProperty("databaseUrl", databaseUrl);
            servletContext.setAttribute("databaseUrl", databaseUrl);
            properties.setProperty("jdbcDriver", jdbcDriver);
            servletContext.setAttribute("jdbcDriver", jdbcDriver);
            properties.setProperty("databaseUsername", databaseUsername);
            servletContext.setAttribute("databaseUsername", databaseUsername);
            properties.setProperty("databasePassword", databasePassword);
            servletContext.setAttribute("databasePassword", databasePassword);

            properties.setProperty("databaseMinimumConnections", databaseMinimumConnections);
            //servletContext.setAttribute("databaseMinimumConnections", databaseMinimumConnections);
            properties.setProperty("databaseMaximumConnections", databaseMaximumConnections);
            properties.setProperty("databaseConnectionMaximumAgeDays", databaseConnectionMaximumAgeDays);
            properties.setProperty("databaseConnectionMaximumIdleSeconds", databaseConnectionMaximumIdleSeconds);

            java.util.Properties connectionPoolProperties = new java.util.Properties();
            connectionPoolProperties.setProperty("id", "-1");
            connectionPoolProperties.setProperty("url", databaseUrl);
            connectionPoolProperties.setProperty("driver", jdbcDriver);
            connectionPoolProperties.setProperty("username", databaseUsername);
            connectionPoolProperties.setProperty("password", databasePassword);
            connectionPoolProperties.setProperty("minConns", databaseMinimumConnections);
            connectionPoolProperties.setProperty("maxConns", databaseMaximumConnections);
            connectionPoolProperties.setProperty("maxAgeDays", databaseConnectionMaximumAgeDays);
            connectionPoolProperties.setProperty("maxIdleSeconds", databaseConnectionMaximumIdleSeconds);
            
            logger.debug("connectionPoolProperties = " + connectionPoolProperties);

            try {

                this.connectionPool = new ConnectionPool(connectionPoolProperties);
                logger.debug("this.connectionPool = " + this.connectionPool);

                servletContext.setAttribute("connectionPool", this.connectionPool);

                //this.connectionManager = new ConnectionManager( (ConnectionPool) this.connectionPool );
                //logger.debug("this.connectionManager = " + this.connectionManager);                

            } catch(java.io.IOException ioException) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                ioException.printStackTrace(printWriter);
                logger.error(stringWriter.toString());
            }

        /*
        } else { // if(this.connectionPool == null) {

            if(this.connectionManager == null) {
                this.connectionManager = new ConnectionManager( (ConnectionPool) this.connectionPool );
                logger.debug("this.connectionManager = " + this.connectionManager);                
            }
        */

        }

        try {

            List<Menuable> blogCategoryList = BlogCategoryDatabaseManager.selectBlogCategoriesOrderByPriority(this.connectionPool);
            servletContext.setAttribute("blogCategoryList", blogCategoryList);
            if(blogCategoryList != null) logger.debug("blogCategoryList.size() = "+ blogCategoryList.size());

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        super.init(config);
    }

    public Properties getProperties() {
        logger.debug("getProperties()");

        logger.debug("this.properties = "+this.properties);
        logger.debug("this.properties.size() = "+this.properties.size());
        return this.properties;
    }

    private void configureCookies(Properties properties) {
        logger.debug("configureCookies(properties)");

        String sessionCookieConfigPath = properties.getProperty("session.cookie.config.path");
        String sessionCookieConfigDomain = properties.getProperty("session.cookie.config.domain");
        String sessionCookieConfigComment = properties.getProperty("session.cookie.config.domain");
        String sessionCookieConfigMaxAge = properties.getProperty("session.cookie.config.maxage");

        logger.debug("sessionCookieConfigPath = '"+sessionCookieConfigPath+"'");
        logger.debug("sessionCookieConfigDomain = '"+sessionCookieConfigDomain+"'");
        logger.debug("sessionCookieConfigComment = '"+sessionCookieConfigComment+"'");
        logger.debug("sessionCookieConfigMaxAge = '"+sessionCookieConfigMaxAge+"'");

        javax.servlet.SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();

        sessionCookieConfig.setPath(sessionCookieConfigPath);
        sessionCookieConfig.setDomain(sessionCookieConfigDomain);
        sessionCookieConfig.setComment(sessionCookieConfigComment);
        //sessionCookieConfig.setSecure(true);
        //sessionCookieConfig.setHttpOnly(true);
        sessionCookieConfig.setHttpOnly(false);
        //sessionCookieConfig.setMaxAge(6000);
        sessionCookieConfig.setMaxAge(Integer.parseInt(sessionCookieConfigMaxAge));
        //sessionCookieConfig.setMaxAge(Long.parseLong(sessionCookieConfigMaxAge));

        //sessionCookieConfig.setDomain(java.lang.String domain); // Sets the domain name that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        //sessionCookieConfig.setHttpOnly(boolean httpOnly); // Marks or unmarks the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired as HttpOnly.
        //sessionCookieConfig.setMaxAge(int maxAge); // Sets the lifetime (in seconds) for the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        //sessionCookieConfig.setName(java.lang.String name); // Sets the name that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        //sessionCookieConfig.setPath(java.lang.String path); // Sets the path that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        //sessionCookieConfig.setSecure(boolean secure); // Marks or unmarks the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired as secure.

        //logger.debug("configureCookies(): properties.getProperty(\"server\") = "+properties.getProperty("server"));

        /*
        String maxAgeDaysSession = (String)com.ispaces.web.servlet.InitServlet.properties.getProperty("session.max.age.days");
        logger.debug(className+".getSession(request): maxAgeDaysSession = "+maxAgeDaysSession);

        if(maxAgeDaysSession != null) {

            try {

                double maxAgeDays = (new Double(maxAgeDaysSession)).doubleValue();
                logger.debug(className+".getSession(request): maxAgeDays = "+maxAgeDays);

                // Set up the max age of the connection
                int maxAgeSeconds = (int)(maxAgeDays * DateUtil.dayInSeconds);
                logger.debug(className+".getSession(request): maxAgeSeconds = "+maxAgeSeconds);
                //logger.debug(className+".init(): maxAgeMillis = "+maxAgeMillis);
                httpSession.setMaxInactiveInterval(maxAgeSeconds);
                logger.debug(className+".getSession(request): httpSession.getMaxInactiveInterval() = "+httpSession.getMaxInactiveInterval());

            } catch(Exception e) {
                logger.error("Exception in "+className+".getSession(request): "+e.getMessage(), e);
            }
        }
        */

        /*
        sessionCookieConfig.setDomain(properties.getProperty("server")); // Sets the domain name that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        sessionCookieConfig.setPath("/"); // Sets the path that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        sessionCookieConfig.setMaxAge(60); // Sets the lifetime (in seconds) for the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        */

        this.printSessionCookieConfig(sessionCookieConfig);

    }

    private void printSessionCookieConfig(javax.servlet.SessionCookieConfig sessionCookieConfig) {
        logger.debug("sessionCookieConfig.getComment() = "+sessionCookieConfig.getComment()); // Gets the comment that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        logger.debug("sessionCookieConfig.getDomain() = "+sessionCookieConfig.getDomain()); // Gets the domain name that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        logger.debug("sessionCookieConfig.getMaxAge() = "+sessionCookieConfig.getMaxAge()); // Gets the lifetime (in seconds) of the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        logger.debug("sessionCookieConfig.getName() = "+sessionCookieConfig.getName()); // Gets the name that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        logger.debug("sessionCookieConfig.getPath() = "+sessionCookieConfig.getPath()); // Gets the path that will be assigned to any session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired.
        logger.debug("sessionCookieConfig.isHttpOnly() = "+sessionCookieConfig.isHttpOnly()); // Checks if the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired will be marked as HttpOnly.
        logger.debug("sessionCookieConfig.isSecure() = "+sessionCookieConfig.isSecure()); // Checks if the session tracking cookies created on behalf of the application represented by the ServletContext from which this SessionCookieConfig was acquired will be marked as secure even if the request that initiated the corresponding session is using plain HTTP instead of HTTPS.
    }

}
