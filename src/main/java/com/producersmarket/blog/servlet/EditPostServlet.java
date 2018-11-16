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
//public class EditPostServlet extends ParentServlet {
public class EditPostServlet extends BlogPostServlet {

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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        BlogPost blogPost = new BlogPost();

        String title = request.getParameter("title");
        String subtitle = request.getParameter("subtitle");
        String body = request.getParameter("body");

        logger.debug("title = "+title);
        logger.debug("subtitle = "+subtitle);
        logger.debug("body = "+body);

        blogPost.setSubtitle(subtitle);
        blogPost.setTitle(title);
        blogPost.setBody(body);

        if(
               EMPTY.equals(title)
            && EMPTY.equals(body)
        ) {

            includeUtf8(request, response, "/view/edit-post.jsp");

            return;
        }

        // get the userId from the request
        /*    
        String userIdString = request.getParameter("userId");
        logger.debug("userIdString = "+userIdString);
        int userId = -1;
        try {
            userId = Integer.parseInt(userIdString);
            blogPost.setUserId(userId);
        } catch(NumberFormatException e) {
        }
        logger.debug("userId = "+userId);
        */

        // or get the userId from the session, better
        HttpSession httpSession = request.getSession(false);
        Object userIdObject = null;
        int userId = -1;

        if(
          httpSession != null
          && (userIdObject = httpSession.getAttribute("userId")) != null
        ) {

            //userId = Integer.valueOf((String) userIdObject);
            userId = ((Integer)userIdObject).intValue();

            logger.debug("httpSession.getId() = "+httpSession.getId());
            logger.debug("userIdObject = "+userIdObject);
            logger.debug("userId = "+userId);

            blogPost.setUserId(userId);
        }

        String blogPostIdString = request.getParameter("blogPostId");
        logger.debug("blogPostIdString = "+blogPostIdString);
        int blogPostId = -1;
        try {
            blogPostId = Integer.parseInt(blogPostIdString);
            blogPost.setId(blogPostId);
        } catch(NumberFormatException e) {
            // No blog post ID, must be a new post
            blogPost.createHyphenatedName();
        }
        logger.debug("blogPostId = "+blogPostId);


        try {

            if(blogPost.getId() > 0) {
                BlogPostDatabaseManager.updateBlogPost(blogPost);
            } else {
                BlogPostDatabaseManager.insertBlogPost(blogPost);
            }

            //include(request, response, "/view/email-sent.jsp");
            //javax.servlet.RequestDispatcher rd = this.config.getServletContext().getRequestDispatcher("/import-contacts");
            //rd.forward(request, response);

            getServletContext().getRequestDispatcher(
                new StringBuilder().append("/post/").append(blogPost.getId()).toString()
            ).forward(request, response);

            return;

        } catch(java.sql.SQLException e) {

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

                    this.executor.execute(new BlogPostIdRequest(request.startAsync(), blogPostId));

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

    public void blogPostRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , int blogPostId
    ) throws IOException, ServletException {

        logger.debug("blogPostRequest(request, response, "+blogPostId+")");

        try {

            BlogPost blogPost = BlogPostDatabaseManager.selectBlogPost(blogPostId);

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

    /*
     * https://www.javadoc.io/doc/com.atlassian.commonmark/commonmark/0.10.0
     * https://static.javadoc.io/com.atlassian.commonmark/commonmark/0.10.0/org/commonmark/node/Image.html
     * https://static.javadoc.io/com.atlassian.commonmark/commonmark/0.10.0/org/commonmark/renderer/html/HtmlWriter.html
     * <figure><img src="/content/images/organic-avocado-farm-and-packing-house-michoacan-mexico/organic-avocados-lionel-packed-box_600x800.jpg" alt="Leonel Chavez, CEO of Michocan Organics" /><figcaption>Leonel Chavez, CEO of Michocan Organics</figcaption></figure>
     * http://digitaldrummerj.me/styling-jekyll-markdown/
     * https://github.com/atlassian/commonmark-java/blob/master/commonmark/src/main/java/org/commonmark/node/Link.java
     */

}
