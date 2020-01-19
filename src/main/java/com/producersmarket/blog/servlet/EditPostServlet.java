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

import com.producersmarket.blog.database.BlogCategoryDatabaseManager;
import com.producersmarket.blog.database.BlogPostDatabaseManager;
import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.database.DatabaseManager;
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.blog.model.User;
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

    public BlogPost parsePost(HttpServletRequest request) {

        BlogPost blogPost = new BlogPost();
        String hyphenatedName = request.getParameter("hyphenatedName");
        String title = request.getParameter("title");
        String subtitle = request.getParameter("subtitle");
        String body = request.getParameter("body");

        logger.debug("hyphenatedName = "+hyphenatedName);
        logger.debug("title = "+title);
        logger.debug("subtitle = "+subtitle);
        //logger.debug("body = "+body);

        blogPost.setHyphenatedName(hyphenatedName);
        blogPost.setSubtitle( subtitle );
        blogPost.setTitle( title );
        blogPost.setBody( body );

        return blogPost;
    }

    public void parseImage(HttpServletRequest request, BlogPost blogPost) throws IOException, ServletException {

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

            if(blogImagePart.getSize() > 0) {

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
                    .append(File.separator).append(blogPost.getHyphenatedName());

                File blogImageDirectory = new File(blogImagePathBuilder.toString());
                logger.debug("blogImageDirectory.exists() = "+ blogImageDirectory.exists());
                if(!blogImageDirectory.exists()) {
                    boolean blogImageDirectoryCreated =  blogImageDirectory.mkdirs();
                    logger.debug("blogImageDirectoryCreated = " + blogImageDirectoryCreated);
                }

                // yyyy-MM-dd HH:mm:ss  (2009-12-31 23:59:59)
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMMdd-HHmmss" );
                String dateString = simpleDateFormat.format( new Date() );

                blogImagePathBuilder
                    .append(File.separator)
                    .append(blogPost.getHyphenatedName())
                    .append("-").append(dateString)
                    .append(".")
                    .append(blogImageFileNameSuffix);

                String blogImagePath = blogImagePathBuilder.toString();
                File blogImageFile = new File(blogImagePath);

                logger.debug("blogImagePath = " + blogImagePath);
                logger.debug("blogImageFile = " + blogImageFile);
                logger.debug("blogImageDirectory.exists() = "+ blogImageDirectory.exists());

                if(blogImageDirectory.exists()) {

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

                } // if(blogImageDirectory.exists())

            } // if(blogImagePart.getSize() > 0)

        } // if(blogImagePart != null)
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doPost(request, response)");

        int userId = -1;
        Integer userIdInteger = null;
        List<Integer> groupIdList = null;
        HttpSession httpSession = request.getSession (false);

        if (
            httpSession != null
            && (userIdInteger = (Integer) httpSession.getAttribute("userId")) != null
        ) {

            logger.debug("userIdInteger = "+userIdInteger);
            userId = userIdInteger.intValue();
            logger.debug("userId = "+ userId);
            groupIdList = (List<Integer>)httpSession.getAttribute("groupIdList");
            logger.debug("groupIdList = "+groupIdList);

            if( groupIdList != null && groupIdList.contains(2) ) {

                BlogPost blogPost = parsePost( request );
                blogPost.setUserId( userId );

                if (
                       EMPTY.equals( blogPost.getTitle() )
                    && EMPTY.equals( blogPost.getBody() )
                ) {
                    includeUtf8( request, response, "/view/edit-post.jsp" );
                    return;
                }

                String blogPostIdString = request.getParameter( "blogPostId" );
                logger.debug("blogPostIdString = "+ blogPostIdString);
                int blogPostId = -1;
                try {
                    blogPostId = Integer.parseInt(blogPostIdString);
                    blogPost.setId(blogPostId);
                } catch(NumberFormatException e) {
                    // No blog post ID, must be a new post
                    blogPost.createHyphenatedName();
                    //hyphenatedName = blogPost.getHyphenatedName();
                }
                logger.debug("blogPostId = "+blogPostId);
                //logger.debug("hyphenatedName = "+ hyphenatedName);

                parseImage( request, blogPost );

                try {

                    if(blogPost.getId() > 0) {
                        BlogPostDatabaseManager.updateBlogPost(blogPost, getConnectionPool());
                    } else {
                        BlogPostDatabaseManager.insertBlogPost(blogPost, getConnectionPool());
                        blogPostId = blogPost.getId();
                    }

                    logger.debug("blogPostId = "+ blogPostId);
                    request.setAttribute("blogPostId", blogPostId);

                    String[] blogCategoryIds = request.getParameterValues("blogCategories");
                    logger.debug("blogCategoryIds = " + blogCategoryIds);
                    if(blogCategoryIds != null) {
                        logger.debug("blogCategoryIds.length = " + blogCategoryIds.length);
                        ConnectionManager connectionManager = new ConnectionManager( (ConnectionPool) getConnectionPool() );

                        //try {

                            //try {
                                DatabaseManager.executeUpdate("DELETE FROM blogPost_blogCategory WHERE blogPostId = ?", blogPostId, connectionManager);
                            //} catch(java.sql.SQLException e) {
                            //    logException(e);
                            //} catch(Exception e) {
                            //    logException(e);
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

                            //try {
                                BlogCategoryDatabaseManager.insertBlogPostCategories(insertBlogPostCategoriesSql, connectionManager);
                            //} catch(java.sql.SQLException e) {
                            //    logException(e);
                            //} catch(Exception e) {
                            //    logException(e);
                            //}

                        //} catch(java.sql.SQLException e) {
                        //    logException(e);
                        //} catch(Exception e) {
                        //    logException(e);
                        //} finally {
                        //    connectionManager.commit();
                        //}

                    } // if(blogCategoryIds != null)

                    String[] authorIds = request.getParameterValues("authorIds");
                    logger.debug("authorIds = " + authorIds);
                    if(authorIds != null) {
                        logger.debug("authorIds.length = " + authorIds.length);
                        ConnectionManager connectionManager = new ConnectionManager( (ConnectionPool) getConnectionPool() );

                        try {

                            //try {
                                DatabaseManager.executeUpdate("DELETE FROM producersblog.blogPostAuthor WHERE blogPostId = ?", blogPostId, connectionManager);
                            //} catch(java.sql.SQLException e) {
                            //    logException(e);
                            //} catch(Exception e) {
                            //    logException(e);
                            //}

                            StringBuilder insertBlogPostAuthorsSqlBuilder = new StringBuilder();
                            insertBlogPostAuthorsSqlBuilder.append("INSERT INTO producersblog.blogPostAuthor (blogPostId, userId) VALUES ");
                            int x = 0;
                            for(String authorId: authorIds) {
                                if(x++ > 0) insertBlogPostAuthorsSqlBuilder.append(", ");
                                insertBlogPostAuthorsSqlBuilder.append("(").append(blogPostId).append(", ").append(authorId).append(")");
                            }
                            String insertBlogPostAuthorsSql = insertBlogPostAuthorsSqlBuilder.toString();
                            logger.debug("insertBlogPostAuthorsSql = " + insertBlogPostAuthorsSql);

                            //try {
                                //authorDatabaseManager.insertBlogPostAuthors(insertBlogPostAuthorsSql, connectionManager);
                                DatabaseManager.executeUpdate(insertBlogPostAuthorsSql, connectionManager);
                            //} catch(java.sql.SQLException e) {
                            //    logException(e);
                            //} catch(Exception e) {
                            //    logException(e);
                            //}

                        } catch(java.sql.SQLException e) {
                            logException(e);
                        } catch(Exception e) {
                            logException(e);
                        } finally {
                            connectionManager.commit();
                        }

                    } // if(authorIds != null)

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

            } // if( groupIdList != null ) {

        } // if (httpSession != null)

        response.sendError(HttpServletResponse.SC_NOT_FOUND);

    } // doPost()

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            int userId = -1;
            Integer userIdInteger = null;
            List<Integer> groupIdList = null;
            HttpSession httpSession = request.getSession (false);

            if(
                httpSession != null
                && (userIdInteger = (Integer) httpSession.getAttribute("userId")) != null
            ) {

                logger.debug("userIdInteger = " + userIdInteger);
                userId = userIdInteger.intValue();
                groupIdList = (List<Integer>) httpSession.getAttribute("groupIdList");
                logger.debug("groupIdList = " + groupIdList);

                if( groupIdList != null && groupIdList.contains(2) ) {

                    String blogToken = null;
                    String pathInfo = request.getPathInfo();
                    int blogPostId = -1;
                    logger.debug("pathInfo = "+pathInfo);

                    if( pathInfo != null ) {
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

                BlogCategoryDatabaseManager.selectBlogPostCategoryIds(blogPost, getConnectionPool());
                List<Integer> blogCategoryIdList = blogPost.getBlogCategoryIdList();
                logger.debug("blogCategoryIdList = "+ blogCategoryIdList);

                // To do: just add the blogPost to the request, it already contains the blogCategoryIdList
                request.setAttribute("blogPost", blogPost);
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
