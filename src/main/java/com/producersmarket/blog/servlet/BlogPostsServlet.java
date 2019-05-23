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
import com.producersmarket.blog.servlet.ParentServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@WebServlet(
    name = "BlogPostsServlet"
    , asyncSupported=true
    , urlPatterns = { "/all-posts" }
)
public class BlogPostsServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final String ZERO = "0";
    private static final String FORWARD_SLASH = "/";

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            super.execute(new BlogPostsRequest(request.startAsync()));

            return;

        } catch(Exception e) {

            logger.warn(new StringBuilder().append("Error in: ").append(getClass().getName()).append(" ").append(e.getMessage()).toString());

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /*
     * Constructor takes the AsyncContext, which contains the request/response.
     */
    public class BlogPostsRequest implements Runnable {

        AsyncContext asyncContext;

        public BlogPostsRequest(AsyncContext asyncContext) {
            logger.debug("BlogRequest(asyncContext)");

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
        }

        public void run() {

            try {

                blogPostsRequest((HttpServletRequest)this.asyncContext.getRequest(), (HttpServletResponse)this.asyncContext.getResponse());

                this.asyncContext.complete();

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }

        }
    }

    public void blogPostsRequest(
        HttpServletRequest request
      , HttpServletResponse response
    ) throws IOException, ServletException {

        logger.debug("blogPostsRequest(request, response)");

        try {

            List<BlogPost> blogPostList = BlogPostDatabaseManager.selectAllBlogPosts(getConnectionPool());

            if(blogPostList != null) {
                logger.debug("blogPostList.size() = "+blogPostList.size());
                request.setAttribute("blogPostList", blogPostList);
            }

            //include(request, response, "/view/blog-list.jsp");
            includeUtf8(request, response, "/view/user.jsp");

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

}
