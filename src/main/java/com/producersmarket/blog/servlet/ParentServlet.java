package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ParentServlet extends InitServlet {

    private static final Logger logger = LogManager.getLogger();
    public static final String UTF_8 = "UTF-8";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML_UTF8 = "text/html; charset=UTF-8";
    public static final String APPLICATION_XJAVASCRIPT = "application/x-javascript";
    public static final String APPLICATION_XJAVASCRIPT_UTF8 = "application/x-javascript;charset=utf-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final String COMMA = ",";
    public static final String LEFT_SQUARE = "[";
    public static final String RIGHT_SQUARE = "]";
    public static final String DOUBLE_QUOTE = "\"";
	

    public static ServletContext servletContext;

    /**
     * Executor to process asynchronous http requests.
     */
    public static java.util.concurrent.Executor executor = null;
	
    public String doubleQuotes(String string) {
        return new StringBuilder().append(DOUBLE_QUOTE).append(string).append(DOUBLE_QUOTE).toString();
    }

    public java.util.Properties getProperties() {
        //return super.properties;
        return super.getProperties();
    }

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init(config)");

        servletContext = config.getServletContext();

        executor = new java.util.concurrent.ThreadPoolExecutor(
            10
          , 10
          , 50000L
          , java.util.concurrent.TimeUnit.MILLISECONDS
          , new java.util.concurrent.LinkedBlockingQueue<Runnable>(100)
        );
        //servletContext.setAttribute("executor", executor);

        super.init(config);
    }

    public void logException(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        logger.error(stringWriter.toString());
    }

    protected void include(HttpServletRequest request, HttpServletResponse response, String jspPath) throws IOException, ServletException {
        include(request, response, jspPath, TEXT_HTML_UTF8);
    }

    protected void include(HttpServletRequest request, HttpServletResponse response, String jspPath, String contentType) throws IOException, ServletException {
        logger.debug("include(request, response, '"+jspPath+"', '"+contentType+"')");
        //logger.debug("include("+request+", "+response+", '"+jspPath+"', '"+contentType+"')");

        response.setContentType(contentType);

        try {

            /*
             * http://stackoverflow.com/questions/27352672/how-to-use-the-org-json-java-library-with-java-7
             */
            //logger.debug("this.getServletConfig() = "+this.getServletConfig());
            //if(this.getServletConfig() != null) logger.debug("this.getServletConfig().getServletContext() = "+this.getServletConfig().getServletContext());
            //this.getServletConfig().getServletContext().getRequestDispatcher(jspPath).include(request, response);
            servletContext.getRequestDispatcher(jspPath).include(request, response);
            //request.getRequestDispatcher(jspPath).include(request, response);

        } catch(java.io.FileNotFoundException e) {

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
        logger.debug("includeUtf8(request, response, '"+path+"')");

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

        // logging
        //StringBuilder sb = new StringBuilder();
        //sb.append(RESPONSE_COLON_SPACE);
        //sb.append(path);

        //HttpSession session = request.getSession(false);
        //logger.debug("session = "+session);

        //if(session != null) {
        //    sb.append(" - ").append(LEFT_PAREN);
        //    sb.append(SPACE).append(session.getId());
        //    //sb.append(COLON).append(user.getCookieId());
        //    sb.append(SPACE).append(session.getAttribute(SESSION_HASH));
        //    sb.append(RIGHT_PAREN);
        //}

        //logger.info(sb.toString());

        try {

            //logger.debug("context = "+context);
            //logger.debug("servletContext = "+servletContext);
            //logger.debug("this.getServletConfig() = "+this.getServletConfig());
            //logger.debug("this.getServletConfig().getServletContext() = "+this.getServletConfig().getServletContext());

            //this.getServletConfig().getServletContext().getRequestDispatcher(path).include(request, response);
            //context.getRequestDispatcher(path).include(request, response);

            servletContext.getRequestDispatcher(path).include(request, response);

            try {
                //response.getOutputStream().flush(); // yes/no/why?
                response.getOutputStream().close(); // yes/no/why?
            } catch(Exception e){
                logger.error(e);
            }

        } catch(java.io.FileNotFoundException e) {

            logger.error("FileNotFoundException: include(request, response, '"+path+"', '"+contentType+"'): e.getMessage() = "+e.getMessage());
            logger.error(e);

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

    public int getUserId(HttpServletRequest request) { // throws IOException, ServletException {
        logger.debug("getUserId(request)");

        HttpSession session = request.getSession(false);
        logger.debug("session = "+session);

        if(session != null) {

            Integer userIdInteger = (Integer)session.getAttribute("userId");

            if(userIdInteger != null) return userIdInteger.intValue();

        }
        
        return -1;
    }


}
