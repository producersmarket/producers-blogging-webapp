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

import com.producersmarket.blog.database.BlogPostDatabaseManager;
import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.model.User;
//import com.producersmarket.blog.servlet.ParentServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/*
@WebServlet(
    name = "BlogPostServlet"
    , asyncSupported=true
    , urlPatterns = { 
          "/post"
        , "/post/*"
    }
)
*/
//public class UnpublishPostServlet extends ParentServlet {
public class UnpublishPostServlet extends BlogPostServlet {

    private static final Logger logger = LogManager.getLogger();
    
    private static final String ZERO = "0";
    private static final String FORWARD_SLASH = "/";
    //private Executor executor = null;
    private java.util.concurrent.ThreadPoolExecutor executor = null;

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        //this.executor = Executors.newFixedThreadPool(2);  // The thread pool executor to execute the file management requests and recommit/resend the response.
        this.executor = new java.util.concurrent.ThreadPoolExecutor(
            10
          , 10
          , 50000L
          , java.util.concurrent.TimeUnit.MILLISECONDS
          , new java.util.concurrent.LinkedBlockingQueue<Runnable>(100)
        );

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String blogToken = null;
            String pathInfo = request.getPathInfo();
            int blogPostId = -1;
            logger.debug("pathInfo = "+pathInfo);

            if(pathInfo != null) {
                blogToken = pathInfo.substring(1, pathInfo.length());
            } else {
                blogToken = request.getParameter("postId");
            }
            logger.debug("blogToken = "+blogToken);

            if(blogToken != null && blogToken.length() > 0) {

                try {

                    blogPostId = Integer.parseInt(blogToken);
                    logger.debug("blogPostId = "+blogPostId);

                    this.executor.execute(new UnpublishBlogPostRequest(request.startAsync(), blogPostId));

                    return;

                } catch(NumberFormatException e) {

                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    logger.error(stringWriter.toString());

                }
            }

        } catch(Exception e) {

            logger.warn(new StringBuilder().append("Error in: ").append(getClass().getName()).append(" ").append(e.getMessage()).toString());

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //writeOut(response, ZERO);
        includeUtf8(request, response, "/view/edit-post.jsp");
    }

    public class UnpublishBlogPostRequest implements Runnable {

        AsyncContext asyncContext;
        int blogPostId;

        // Grab the userId and path from the HttpServletRequest contained in the AsyncContext.
        public UnpublishBlogPostRequest(
            AsyncContext asyncContext
          , int blogPostId
        ) {

            logger.debug(new StringBuilder().append("UnpublishBlogPostRequest(asyncContext").append(", '").append(blogPostId).append("')").toString());

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            this.blogPostId = blogPostId;
        }

        // Perform the file listing request, build the JSON string, and write it to the response object in this.asyncContext.getResponse().
        public void run() {
            //logger.debug("run()");

            try {

                unpublishBlogPost(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  , this.blogPostId
                );

                this.asyncContext.complete();          // and complete the request.

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }

        } // public void run()

    }

    public void unpublishBlogPost(
        HttpServletRequest request
      , HttpServletResponse response
      , int blogPostId
    ) throws IOException, ServletException {

        logger.debug("unpublishBlogPost(request, response, "+blogPostId+")");

        try {

            BlogPostDatabaseManager.updateBlogPostDisabled(blogPostId, getConnectionPool());

            BlogPost blogPost = BlogPostDatabaseManager.selectBlogPost(blogPostId, getConnectionPool());

            if(blogPost != null) {

                logger.debug("blogPost.getId() = "+blogPost.getId());

                request.setAttribute("blogPost", blogPost);

                includeUtf8(request, response, "/view/edit-post.jsp");

            } // if(blogPost != null) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

    /*()
    public void blogPostRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , int blogPostId
    ) throws IOException, ServletException {

        logger.debug("blogPostRequest(request, response, "+blogPostId+")");

        try {

            BlogPost blogPost = BlogPostDatabaseManager.selectBlogPost(blogPostId, getConnectionManager());

            if(blogPost != null) {

                logger.debug("blogPost.getId() = "+blogPost.getId());

                request.setAttribute("blogPost", blogPost);

                includeUtf8(request, response, "/view/edit-post.jsp");

            } // if(blogPost != null) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }
    */

}
