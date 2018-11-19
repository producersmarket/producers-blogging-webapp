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
public class BlogPostServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(BlogPostServlet.class.getName());
    
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");
        javaLogger.info("doGet(request, response)");

        try {

            String blogToken = null;
            String pathInfo = request.getPathInfo();
            int blogPostId = -1;
            logger.debug("pathInfo = "+pathInfo);

            if(pathInfo != null) {
                blogToken = pathInfo.substring(1, pathInfo.length());
                logger.debug("blogToken = "+blogToken);
            }

            if(blogToken != null && blogToken.length() > 0) {

                try {

                    blogPostId = Integer.parseInt(blogToken);
                    logger.debug("blogPostId = "+blogPostId);

                    this.executor.execute(new BlogPostIdRequest(request.startAsync(), blogPostId));

                } catch(NumberFormatException e) {

                    /*
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    logger.error(stringWriter.toString());
                    */

                    this.executor.execute(new BlogPostNameRequest(request.startAsync(), blogToken.toLowerCase()));
                }

                return;

            } else {

                this.executor.execute(new BlogRequest(request.startAsync()));

                return;

            } // if(pathInfo != null) {

        } catch(Exception e) {

            logger.warn(new StringBuilder().append("Error in: ").append(getClass().getName()).append(" ").append(e.getMessage()).toString());

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //writeOut(response, ZERO);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    public class BlogPostIdRequest implements Runnable {

        AsyncContext asyncContext;
        int blogPostId;

        public BlogPostIdRequest(AsyncContext asyncContext, int blogPostId) {

            logger.debug(new StringBuilder().append("BlogPostNameRequest(asyncContext").append(", '").append(blogPostId).append("')").toString());

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            this.blogPostId = blogPostId;
        }

        public void run() {
            //logger.debug("run()");

            try {

                blogPostRequest(
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

                //includeUtf8(request, response, "/view/edit-blog-post.jsp");
                includeUtf8(request, response, "/view/blog/blog-post.jsp");

            } // if(blogPost != null) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

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
            List<BlogPost> blogPostList = BlogPostDatabaseManager.selectBlogPosts();

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

    public class BlogPostNameRequest implements Runnable {

        AsyncContext asyncContext;
        String blogPostName;

        // Grab the userId and path from the HttpServletRequest contained in the AsyncContext.
        public BlogPostNameRequest(
            AsyncContext asyncContext
          , String blogPostName
        ) {

            logger.debug(new StringBuilder().append("BlogPostNameRequest(asyncContext").append(", '").append(blogPostName).append("')").toString());

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            this.blogPostName = blogPostName;
        }

        // Perform the file listing request, build the JSON string, and write it to the response object in this.asyncContext.getResponse().
        public void run() {
            //logger.debug("run()");

            try {

                blogPostRequest(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  , this.blogPostName
                );

                this.asyncContext.complete();          // and complete the request.

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }

        } // public void run()

    }

    public void blogPostRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , String blogPostName
    ) throws IOException, ServletException {

        logger.debug("blogPostRequest(request, response, '"+blogPostName+"')");

        //include(request, response, "/view/blog/"+blogPostName+".jsp");

        //request.setAttribute("blogPostNameHyphenated", blogPostName);

        try {

            BlogPost blogPost = BlogPostDatabaseManager.selectBlogPostByHyphenatedName(blogPostName);

            if(blogPost != null) {

                logger.debug(blogPost.getId()+" "+blogPost.getHyphenatedName());

                /*
                Parser parser = Parser.builder().build();
                Node document = parser.parse(blogPost.getBody());
                //HtmlRenderer renderer = HtmlRenderer.builder().build();
                HtmlRenderer renderer = HtmlRenderer.builder()
                    .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                            return new BlogImageNodeRenderer(htmlNodeRendererContext);
                        }
                    }).build();

                String bodyHtml = renderer.render(document);

                logger.debug("blogPost.getBody() = "+blogPost.getBody());
                logger.debug("bodyHtml           = "+bodyHtml);
                logger.debug("blogPost.getBody().length() = "+blogPost.getBody().length());
                logger.debug("bodyHtml.length()           = "+bodyHtml.length());

                blogPost.setBody(bodyHtml);
                */

                request.setAttribute("blogPost", blogPost);

                includeUtf8(request, response, "/view/blog/blog-post.jsp");

            } else { // if(blogPost != null) {

                String errorMessage = "Blog Post Not Found!";
                logger.warn(errorMessage);

                getServletContext().getRequestDispatcher(
                    new StringBuilder().append("/missing-post/").append(blogPostName).toString()
                ).forward(request, response);

                //writeOut(response, errorMessage);


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
