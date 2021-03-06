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
//import com.producersmarket.database.UserDatabaseManager;
import com.producersmarket.model.User;
//import com.producersmarket.blog.servlet.ParentServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/*
@WebServlet(
    name = "BlogCategoryServlet"
    , asyncSupported=true
    , urlPatterns = { 
          "/category"
        , "/category/*"
    }
)
*/
public class BlogCategoryServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(BlogCategoryServlet.class.getName());
    
    private static final String ZERO = "0";
    private static final String FORWARD_SLASH = "/";
    //private Executor executor = null;
    private java.util.concurrent.ThreadPoolExecutor executor = null;

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");
        javaLogger.info("init(config)");

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
        javaLogger.info("doGet(request, response)");

        try {

            String blogCategory = null;
            String pathInfo = request.getPathInfo();
            logger.debug("pathInfo = "+pathInfo);

            if(pathInfo != null) {

                blogCategory = pathInfo.substring(1, pathInfo.length());
                logger.debug("blogCategory = "+blogCategory);

            }

            if(blogCategory != null && blogCategory.length() > 0) {

                this.executor.execute(
                    new BlogCategoryNameRequest(
                        request.startAsync()
                      , blogCategory.toLowerCase()
                    )
                );

                //return;

            } else {

                this.executor.execute(
                    new BlogRequest(
                        request.startAsync()
                    )
                );

                //return;

            } // if(pathInfo != null) {

        } catch(Exception e) {

            logger.warn(new StringBuilder().append("Error in: ").append(getClass().getName()).append(" ").append(e.getMessage()).toString());

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //writeOut(response, ZERO);
    }

    /*
     * Constructor takes the AsyncContext, which contains the request/response.
     */
    public class BlogRequest implements Runnable {

        AsyncContext asyncContext;
        //int blogPostId;

        // Grab the userId and path from the HttpServletRequest contained in the AsyncContext.
        public BlogRequest(
            AsyncContext asyncContext
          //, int blogPostId
        ) {

            //logger.debug(new StringBuilder().append("BlogPostRequest(asyncContext").append(", '").append(blogPostId).append("')").toString());
            logger.debug("BlogRequest(asyncContext)");

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            //this.blogPostId = blogPostId;
        }

        // Perform the file listing request, build the JSON string, and write it to the response object in this.asyncContext.getResponse().
        public void run() {
            //logger.debug("run()");

            try {

                blogRequest(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  //, this.blogPostId
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
    public void blogRequest(
        HttpServletRequest request
      , HttpServletResponse response
      //, int blogPostId
    ) throws IOException, ServletException {

        logger.debug("blogRequest(request, response)");

        //int blogPostId;

        try {

            /*
            int[] blogPostIds = new int[51];
            for(int i = 0; i < blogPostIds.length; i++) {
                blogPostIds[i] = (i + 1);
            }
            List<BlogPost> blogPostList = BlogPostDatabaseManager.selectBlogPosts(blogPostIds);
            */
            List<BlogPost> blogPostList = BlogPostDatabaseManager.selectBlogPosts(getConnectionPool());

            if(blogPostList != null) {
                logger.debug("blogPostList.size() = "+blogPostList.size());
                request.setAttribute("blogPostList", blogPostList);
            }

            //include(request, response, "/view/blog.jsp");
            //include(request, response, "/view/blog-posts.jsp");
            include(request, response, "/view/blog-list.jsp");

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    public class BlogCategoryNameRequest implements Runnable {

        AsyncContext asyncContext;
        String blogCategoryName;

        // Grab the userId and path from the HttpServletRequest contained in the AsyncContext.
        public BlogCategoryNameRequest(
            AsyncContext asyncContext
          , String blogCategoryName
        ) {

            logger.debug(new StringBuilder().append("BlogCategoryNameRequest(asyncContext").append(", '").append(blogCategoryName).append("')").toString());

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            this.blogCategoryName = blogCategoryName;
        }

        // Perform the file listing request, build the JSON string, and write it to the response object in this.asyncContext.getResponse().
        public void run() {
            //logger.debug("run()");

            try {

                blogCategoryRequest(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  , this.blogCategoryName
                );

                this.asyncContext.complete();          // and complete the request.

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }

        } // public void run()

    }

    public void blogCategoryRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , String blogCategoryName
    ) throws IOException, ServletException {

        logger.debug("blogCategoryRequest(request, response, '"+blogCategoryName+"')");

        try {

            List<BlogPost> blogPostList = BlogPostDatabaseManager.selectBlogPostsByCategoryName(blogCategoryName, getConnectionPool());

            if(blogPostList != null) {
                logger.debug("blogPostList.size() = "+blogPostList.size());
                request.setAttribute("blogPostList", blogPostList);
            }

            request.setAttribute("blogCategoryName", blogCategoryName);

            include(request, response, "/view/blog-list.jsp");

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

    /*
     * https://www.javadoc.io/doc/com.atlassian.commonmark/commonmark/0.10.0
     * https://static.javadoc.io/com.atlassian.commonmark/commonmark/0.10.0/org/commonmark/node/Image.html
     * https://static.javadoc.io/com.atlassian.commonmark/commonmark/0.10.0/org/commonmark/renderer/html/HtmlWriter.html
     * <figure><img src="/content/images/organic-avocado-farm-and-packing-house-michoacan-mexico/organic-avocados-lionel-packed-box_600x800.jpg" alt="Leonel Chavez, CEO of Michocan Organics" /><figcaption>Leonel Chavez, CEO of Michocan Organics</figcaption></figure>
     * http://digitaldrummerj.me/styling-jekyll-markdown/
     * https://github.com/atlassian/commonmark-java/blob/master/commonmark/src/main/java/org/commonmark/node/Link.java
     */

}
