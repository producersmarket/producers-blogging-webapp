package com.producersmarket.blog.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

import com.producersmarket.blog.database.BlogPostDatabaseManager;
//import com.producersmarket.blog.servlet.ParentServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet(
    name = "DeleteBlogImageServlet"
    , asyncSupported=true
    , urlPatterns = { 
        "/delete-blog-image/*"
    }
)

//public class DeleteBlogImageServlet extends ParentServlet {
//public class DeleteBlogImageServlet extends BlogPostServlet {
public class DeleteBlogImageServlet extends EditPostServlet {

    private static final Logger logger = LogManager.getLogger();
    
    private static final String ZERO = "0";
    private static final String FORWARD_SLASH = "/";
    //private Executor executor = null;
    private java.util.concurrent.ThreadPoolExecutor executor = null;

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String imagePath = request.getPathInfo();
            logger.debug("imagePath = "+imagePath);

            if(imagePath != null && imagePath.length() > 0) {

                try {

                    //this.executor.execute(new DeleteBlogPostImageRequest(request.startAsync(), imagePath));
                    //getExecutor().execute(new DeleteBlogPostImageRequest(request.startAsync(), imagePath));
                    super.execute(new DeleteBlogPostImageRequest(request.startAsync(), imagePath));
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

        includeUtf8(request, response, "/view/edit-post.jsp");
    }

    public class DeleteBlogPostImageRequest implements Runnable {

        AsyncContext asyncContext;
        String imagePath;

        // Grab the userId and path from the HttpServletRequest contained in the AsyncContext.
        public DeleteBlogPostImageRequest(
            AsyncContext asyncContext
          , String imagePath
        ) {

            logger.debug(new StringBuilder().append("DeleteBlogPostImageRequest(asyncContext").append(", '").append(imagePath).append("')").toString());

            this.asyncContext = asyncContext;
            this.asyncContext.setTimeout(1000*5);  // 5 seconds timeout
            this.imagePath = imagePath;
        }

        public void run() {

            try {

                deleteBlogPostImage(
                    (HttpServletRequest)this.asyncContext.getRequest()
                  , (HttpServletResponse)this.asyncContext.getResponse()
                  , this.imagePath
                );

                this.asyncContext.complete();          // and complete the request.

            } catch(IOException e) {
                logger.error(e);
            } catch(Exception e) {
                logger.error(e);
            }

        }
    }

    public void deleteBlogPostImage( HttpServletRequest request, HttpServletResponse response, String imagePath ) throws IOException, ServletException {
        logger.debug("deleteBlogPostImage(request, response, "+imagePath+")");

        try {

            int blogPostId = BlogPostDatabaseManager.deleteBlogPostImage(imagePath, getConnectionPool());

            super.blogPostRequest(request, response, blogPostId);

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

}
