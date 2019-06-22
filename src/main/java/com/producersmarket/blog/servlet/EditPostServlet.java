package com.producersmarket.blog.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

import com.producersmarket.blog.database.BlogCategoryDatabaseManager;
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

@MultipartConfig(
      fileSizeThreshold   = 1024 * 1024 * 1  // 1 MB
    , maxFileSize         = 1024 * 1024 * 50 // 50 MB file
    , maxRequestSize      = 1024 * 1024 * 50 // 50 MB file
)

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

    /**
     * Extracts file name from HTTP header content-disposition
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }

        return null;
    }

    public String extractSuffix(String fileName) throws IOException, ServletException {
        logger.debug("extractSuffix("+fileName+")");

        return fileName.substring(fileName.indexOf(".") + 1);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        BlogPost blogPost = new BlogPost();

        String hyphenatedName = request.getParameter("hyphenatedName");
        String title = request.getParameter("title");
        String subtitle = request.getParameter("subtitle");
        String body = request.getParameter("body");

        /*
        String blogCategoryIdString = request.getParameter("blogCategoryId");
        int blogCategoryId = -1;
        try {
            blogCategoryId = Integer.parseInt(blogCategoryIdString);
        } catch(NumberFormatException e) {}

        logger.debug("blogCategoryId = " + blogCategoryId);
        */
        // blog categories
        String[] blogCategoryIds = request.getParameterValues("blogCategories");
        logger.debug("blogCategoryIds = " + blogCategoryIds);
        if(blogCategoryIds != null) logger.debug("blogCategoryIds.length = " + blogCategoryIds.length);

        logger.debug("hyphenatedName = "+hyphenatedName);
        logger.debug("title = "+title);
        logger.debug("subtitle = "+subtitle);
        //logger.debug("body = "+body);

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

            userId = ( (Integer) userIdObject ).intValue();

            logger.debug("httpSession.getId() = "+ httpSession.getId());
            logger.debug("userIdObject = "+ userIdObject);
            logger.debug("userId = "+ userId);

            blogPost.setUserId( userId );
        }

        String blogPostIdString = request.getParameter("blogPostId");
        logger.debug("blogPostIdString = "+ blogPostIdString);
        int blogPostId = -1;
        try {
            blogPostId = Integer.parseInt(blogPostIdString);
            blogPost.setId(blogPostId);
            blogPost.setHyphenatedName(hyphenatedName);
        } catch(NumberFormatException e) {
            // No blog post ID, must be a new post
            blogPost.createHyphenatedName();
            hyphenatedName = blogPost.getHyphenatedName();
        }
        logger.debug("blogPostId = "+blogPostId);
        logger.debug("hyphenatedName = "+ hyphenatedName);

        // multipart upload
        Part blogImagePart = request.getPart("blogImage"); // javax.servlet.http.Part @since 3.0
        if(blogImagePart != null) {

            String blogImagePartFileName = extractFileName(blogImagePart);
            String blogImageFileNameSuffix = extractSuffix(blogImagePartFileName);

            logger.debug("blogImagePart = " + blogImagePart);
            logger.debug("blogImagePart.getName() = " + blogImagePart.getName());
            logger.debug("blogImagePart.getSize() = " + blogImagePart.getSize());
            logger.debug("blogImagePartFileName   = " + blogImagePartFileName);
            logger.debug("blogImageFileNameSuffix = " + blogImageFileNameSuffix);

            String systemDrive = System.getenv("SystemDrive"); // Windows only
            logger.debug("systemDrive = " + systemDrive);

            // logo image
            StringBuilder blogImagePathBuilder = new StringBuilder();
            if(systemDrive != null) blogImagePathBuilder.append(systemDrive);
            blogImagePathBuilder
                .append(File.separator).append("var")
                .append(File.separator).append("web")
                .append(File.separator).append("uploaded")
                .append(File.separator).append("images")
                .append(File.separator).append("blog")
                .append(File.separator).append(hyphenatedName);

            boolean blogImageDirectoryCreated =  new File(blogImagePathBuilder.toString()).mkdirs();
            logger.debug("blogImageDirectoryCreated = " + blogImageDirectoryCreated);

            blogImagePathBuilder.append(File.separator).append(hyphenatedName);
            blogImagePathBuilder.append(".").append(blogImageFileNameSuffix);

            String blogImagePath = blogImagePathBuilder.toString();
            File blogImageFile = new File(blogImagePath);

            logger.debug("blogImagePath = " + blogImagePath);
            logger.debug("blogImageFile = " + blogImageFile);

            if(blogImageDirectoryCreated) {

                try {

                    InputStream blogImageInputStream = blogImagePart.getInputStream(); // Gets the content of this part as an InputStream
                    FileOutputStream blogImageFileOutputStream = new FileOutputStream(blogImageFile);
                    byte[] buffer = new byte[1024 * 8]; // 8KB buffer
                    int bytesRead;
                    while((bytesRead = blogImageInputStream.read(buffer)) != -1) {
                        blogImageFileOutputStream.write(buffer, 0, bytesRead);
                    }

                    blogImageInputStream.close();
                    blogImageFileOutputStream.flush();
                    blogImageFileOutputStream.close();

                    blogPost.setImagePath(blogImagePath.substring(blogImagePath.indexOf("uploaded") - 1).replace("\\", "/"));
                    logger.debug("blogPost.getImagePath() = " + blogPost.getImagePath());

                } catch(IOException e) {
                    logException(e);
                }

            } // if(blogImageDirectoryCreated) {

        } // if(blogImagePart != null) {

        try {

            if(blogPost.getId() > 0) {
                BlogPostDatabaseManager.updateBlogPost(blogPost, getConnectionPool());
            } else {
                BlogPostDatabaseManager.insertBlogPost(blogPost, getConnectionPool());
                blogPostId = blogPost.getId();
            }

            logger.debug("blogPostId = "+ blogPostId);

            request.setAttribute("blogPostId", blogPostId);

            if(blogCategoryIds != null) {

                //Map<Integer, String> blogCategoriesMap = (Map<Integer, String>) getServletContext().getAttribute("blogCategoriesMap");
                //if(blogCategoriesMap == null) {
                //    blogCategoriesMap = BlogCategoriesDatabaseManager.selectblogCategories(getConnectionPool());
                //    getServletContext().setAttribute("blogCategoriesMap", blogCategoriesMap);
                //}

                StringBuilder insertBlogPostCategoriesSqlBuilder = new StringBuilder();
                insertBlogPostCategoriesSqlBuilder.append("INSERT INTO blogPost_blogCategory (blogPostId, blogCategoryId) VALUES ");

                int x = 0;
                for(String blogCategoryId: blogCategoryIds) {
                    if(x++ > 0) insertBlogPostCategoriesSqlBuilder.append(", ");
                    insertBlogPostCategoriesSqlBuilder.append("(").append(blogPostId).append(", ").append(blogCategoryId).append(")");
                }

                String insertBlogPostCategoriesSql = insertBlogPostCategoriesSqlBuilder.toString();
                logger.debug("insertBlogPostCategoriesSql = " + insertBlogPostCategoriesSql);

                try {
                    BlogCategoryDatabaseManager.insertBlogPostCategories(insertBlogPostCategoriesSql, getConnectionPool());
                } catch(java.sql.SQLException e) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    logger.debug(stringWriter.toString());
                } catch(Exception e) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    logger.debug(stringWriter.toString());
                }

            }

            /*
            try {
                List<Integer> blogCategoryIdList = BlogCategoryDatabaseManager.insertBlogPostCategory(blogPost.getId(), blogCategoryId, getConnectionPool());
                logger.debug("blogCategoryIdList = "+blogCategoryIdList);
                request.setAttribute("blogCategoryIdList", blogCategoryIdList);
            } catch(java.sql.SQLException e) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                logger.debug(stringWriter.toString());
            } catch(Exception e) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                logger.debug(stringWriter.toString());
            }
            */

            super.blogPostRequest(request, response, blogPost.getId());
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

            Map<Integer, String> blogCategoryMap = (Map<Integer, String>) getServletContext().getAttribute("blogCategoryMap");
            if(blogCategoryMap == null) {
                blogCategoryMap = BlogCategoryDatabaseManager.selectBlogCategories(getConnectionPool());
                getServletContext().setAttribute("blogCategoryMap", blogCategoryMap);
            }

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

        includeUtf8(request, response, "/view/edit-post.jsp");
    }

    public void blogPostRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , int blogPostId
    ) throws IOException, ServletException {

        logger.debug("blogPostRequest(request, response, "+blogPostId+")");

        try {

            BlogPost blogPost = BlogPostDatabaseManager.selectBlogPost(blogPostId, getConnectionPool());

            if(blogPost != null) {

                logger.debug("blogPost.getId() = "+blogPost.getId());

                request.setAttribute("blogPost", blogPost);

                //List<Integer> blogCategoryIdList = BlogCategoryDatabaseManager.selectBlogCategoryIdList(blogPost.getId(), getConnectionPool());
                BlogCategoryDatabaseManager.selectBlogPostCategoryIds(blogPost, getConnectionPool());
                List<Integer> blogCategoryIdList = blogPost.getBlogCategoryIdList();
                logger.debug("blogCategoryIdList = "+ blogCategoryIdList);
                request.setAttribute("blogCategoryIdList", blogCategoryIdList);

                includeUtf8(request, response, "/view/edit-post.jsp");

            } // if(blogPost != null) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

}
