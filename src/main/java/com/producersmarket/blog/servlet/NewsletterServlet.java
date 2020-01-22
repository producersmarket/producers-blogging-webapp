package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;

import javax.servlet.annotation.WebServlet;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet(
    name = "NewsletterServlet"
    , asyncSupported=true
    , urlPatterns = { 
          "/newsletter"
        , "/newsletter/*"
        , "/newsletters"
    }
)
public class NewsletterServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    
    private static final String ZERO = "0";
    private static final String FORWARD_SLASH = "/";

    /*
    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");
        super.init(config);
    }
    */

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String newsletterName = null;
            String pathInfo = request.getPathInfo();
            logger.debug("pathInfo = "+pathInfo);

            if(pathInfo != null) {

                newsletterName = pathInfo.substring(1, pathInfo.length());
                logger.debug("newsletterName = "+newsletterName);

                if(newsletterName != null && newsletterName.length() > 0) {
                    super.execute( new NewsletterNameRequest( request.startAsync(), newsletterName.toLowerCase() ) );
                } else {
                    super.execute(new NewsletterRequest(request.startAsync()));
                }
                return;
            }

        } catch(Exception e) {
            logger.warn(new StringBuilder().append("Error in: ").append(getClass().getName()).append(" ").append(e.getMessage()).toString());
            logException(e);
        }

        super.execute(new NewslettersRequest(request.startAsync()));
    }

    public class NewslettersRequest implements Runnable {

        AsyncContext asyncContext;

        public NewslettersRequest(AsyncContext asyncContext) {
            logger.debug("NewslettersRequest(asyncContext)");

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
        }

        public void run() {
            try {

                includeUtf8(
                      (HttpServletRequest)  this.asyncContext.getRequest()
                    , (HttpServletResponse) this.asyncContext.getResponse()
                    , "/view/newsletters.jsp"
                );

                this.asyncContext.complete();
            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }
        }
    }

    public class NewsletterRequest implements Runnable {

        AsyncContext asyncContext;
        //int newsletterNameId;

        public NewsletterRequest(
            AsyncContext asyncContext
          //, int newsletterNameId
        ) {

            //logger.debug(new StringBuilder().append("NewsletterNameRequest(asyncContext").append(", '").append(newsletterNameId).append("')").toString());
            logger.debug("NewsletterRequest(asyncContext)");

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            //this.newsletterNameId = newsletterNameId;
        }

        // Perform the file listing request, build the JSON string, and write it to the response object in this.asyncContext.getResponse().
        public void run() {
            //logger.debug("run()");

            try {

                newsletterRequest(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  //, this.newsletterNameId
                );

                this.asyncContext.complete();          // and complete the request.

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }

        } // public void run()

    }

    /*
     */
    public void newsletterRequest(
        HttpServletRequest request
      , HttpServletResponse response
      //, int newsletterNameId
    ) throws IOException, ServletException {

        logger.debug("newsletterRequest(request, response)");

        include(request, response, "/view/newsletter/producers-newsletter-template.jsp");

        /*
        try {

            List<NewsletterName> newsletterNameList = NewsletterNameDatabaseManager.selectNewsletterNames();

            if(newsletterNameList != null) {
                logger.debug("newsletterNameList.size() = "+newsletterNameList.size());
                request.setAttribute("newsletterNameList", newsletterNameList);
            }

            //include(request, response, "/view/newsletter.jsp");
            include(request, response, "/view/newsletter-posts.jsp");

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
        */
    }

    public class NewsletterNameRequest implements Runnable {

        AsyncContext asyncContext;
        String newsletterName;

        public NewsletterNameRequest(AsyncContext asyncContext, String newsletterName) {
            logger.debug(new StringBuilder().append("NewsletterNameRequest(asyncContext").append(", '").append(newsletterName).append("')").toString());
            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            this.newsletterName = newsletterName;
        }

        public void run() {

            try {

                newsletterNameRequest(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  , this.newsletterName
                );

                this.asyncContext.complete();          // and complete the request.

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }
        }
    }

    public void newsletterNameRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , String newsletterName
    ) throws IOException, ServletException {

        logger.debug("newsletterNameRequest(request, response, "+newsletterName+")");

        include(request, response, "/view/newsletter/"+newsletterName);

        //request.setAttribute("newsletterNameHyphenated", newsletterName);

        /*
        try {

            NewsletterName newsletterName = NewsletterNameDatabaseManager.selectNewsletterNameByHyphenatedName(newsletterName);

            if(newsletterName != null) {

                logger.debug(newsletterName.getId()+" "+newsletterName.getHyphenatedName());

                Parser parser = Parser.builder().build();
                Node document = parser.parse(newsletterName.getBody());
                //HtmlRenderer renderer = HtmlRenderer.builder().build();
                HtmlRenderer renderer = HtmlRenderer.builder()
                    .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                            return new NewsletterImageNodeRenderer(htmlNodeRendererContext);
                        }
                    }).build();

                String bodyHtml = renderer.render(document);

                logger.debug("newsletterName.getBody() = "+newsletterName.getBody());
                logger.debug("bodyHtml           = "+bodyHtml);
                logger.debug("newsletterName.getBody().length() = "+newsletterName.getBody().length());
                logger.debug("bodyHtml.length()           = "+bodyHtml.length());

                newsletterName.setBody(bodyHtml);

                request.setAttribute("newsletterName", newsletterName);

                includeUtf8(request, response, "/view/newsletter/newsletter-post.jsp");

            } else { // if(newsletterNameList != null) {

                String errorMessage = "Newsletter Post Not Found!";
                logger.warn(errorMessage);
                writeOut(response, errorMessage);

            } // if(newsletterNameList != null) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
        */

    }

}
