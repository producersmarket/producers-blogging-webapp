package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

/*
import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.markdown.LinkNodeRenderer;
import com.producersmarket.blog.markdown.SidebarNodeRenderer;
//import com.producersmarket.database.UserDatabaseManager;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
*/
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.model.User;

public class BlogPostDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = BlogPostDatabaseManager.class.getSimpleName();

    public static List<BlogPost> selectBlogPosts(Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogPosts((ConnectionPool) connectionPoolObject);
    }

    public static List<BlogPost> selectBlogPosts(ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogPosts(new ConnectionManager(connectionPool));
    }

    public static List<BlogPost> selectBlogPosts(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPosts(connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPosts");

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    //populateBlogPost(relatedBlogPost, resultSet);
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectAllBlogPosts(Object connectionPoolObject) throws SQLException, Exception {
        return selectAllBlogPosts((ConnectionPool) connectionPoolObject);
    }

    public static List<BlogPost> selectAllBlogPosts(ConnectionPool connectionPool) throws SQLException, Exception {
        return selectAllBlogPosts(new ConnectionManager(connectionPool));
    }

    public static List<BlogPost> selectAllBlogPosts(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectAllBlogPosts(connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectAllBlogPosts");

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPosts() throws SQLException, Exception {
        logger.debug("selectBlogPosts()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPosts");

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static void updateBlogPost(BlogPost blogPost, Object connectionPoolObject) throws SQLException, Exception {
        updateBlogPost(blogPost, (ConnectionPool) connectionPoolObject);
    }

    public static void updateBlogPost(BlogPost blogPost, ConnectionPool connectionPool) throws SQLException, Exception {
        updateBlogPost(blogPost, new ConnectionManager(connectionPool));
    }

    public static void updateBlogPost(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("updateBlogPost("+blogPost+", "+connectionManager+")");

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("updateBlogPost");
            preparedStatement.setString(1, blogPost.getTitle());
            preparedStatement.setString(2, blogPost.getSubtitle());
            preparedStatement.setString(3, blogPost.getBody());
            preparedStatement.setString(4, blogPost.getHyphenatedName());
            preparedStatement.setInt(5, blogPost.getId());
            preparedStatement.executeUpdate();

            if(blogPost.getImagePath() != null) insertBlogPostImage(blogPost.getId(), blogPost.getImagePath(), connectionManager);

        } finally {
            connectionManager.commit();
        }
    }

    public static void updateBlogPost(BlogPost blogPost) throws SQLException, Exception {

        String sqlStatement = "updateBlogPost";
        ConnectionManager connectionManager = new ConnectionManager(className, sqlStatement);

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlStatement);
            preparedStatement.setString(1, blogPost.getTitle());
            preparedStatement.setString(2, blogPost.getSubtitle());
            preparedStatement.setString(3, blogPost.getBody());
            preparedStatement.setString(4, blogPost.getHyphenatedName());
            preparedStatement.setInt(5, blogPost.getId());
            preparedStatement.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static int deleteBlogPostImage( String imagePath, Object connectionPoolObject ) throws SQLException, Exception {
        return deleteBlogPostImage( imagePath, (ConnectionPool) connectionPoolObject );
    }

    public static int deleteBlogPostImage( String imagePath, ConnectionPool connectionPool ) throws SQLException, Exception {
        return deleteBlogPostImage( imagePath, new ConnectionManager(connectionPool) );
    }

    public static int deleteBlogPostImage( String imagePath, ConnectionManager connectionManager ) throws SQLException, Exception {
        logger.debug("deleteBlogPostImage("+ imagePath +", "+connectionManager+")");

        String sqlName = "deleteBlogPostImage";

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement( "selectBlogPostIdByImagePath" );
            preparedStatement.setString(1, imagePath);
            ResultSet resultSet = preparedStatement.executeQuery();
            int blogPostId = -1;
            if(resultSet.next()) {
                blogPostId = resultSet.getInt(1);
                logger.debug("blogPostId = "+blogPostId);
            }

            if(blogPostId > 0) {
                preparedStatement = connectionManager.loadStatement(sqlName);
                preparedStatement.setString(1, imagePath);
                preparedStatement.executeUpdate();

                return blogPostId;
            }

        } finally {
            connectionManager.commit();
        }

        return -1;
    }

    public static void deleteBlogPost(int blogPostId, Object connectionPoolObject) throws SQLException, Exception {
        deleteBlogPost(blogPostId, (ConnectionPool) connectionPoolObject);
    }

    public static void deleteBlogPost(int blogPostId, ConnectionPool connectionPool) throws SQLException, Exception {
        deleteBlogPost(blogPostId, new ConnectionManager(connectionPool));
    }

    public static void deleteBlogPost(int blogPostId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("deleteBlogPost("+blogPostId+", "+connectionManager+")");

        String sqlName = "deleteBlogPost";

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName);

            preparedStatement.setInt(1, blogPostId);
            preparedStatement.executeUpdate();

        } finally {
            connectionManager.commit();
        }
    }

    public static void updateBlogPostEnabled(int blogPostId, Object connectionPoolObject) throws SQLException, Exception {
        updateBlogPostEnabled(blogPostId, (ConnectionPool) connectionPoolObject);
    }

    public static void updateBlogPostEnabled(int blogPostId, ConnectionPool connectionPool) throws SQLException, Exception {
        updateBlogPostEnabled(blogPostId, new ConnectionManager(connectionPool));
    }

    public static void updateBlogPostEnabled(int blogPostId, ConnectionManager connectionManager) throws SQLException, Exception {

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("updateBlogPostEnabled");
            preparedStatement.setInt(1, blogPostId);
            preparedStatement.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void updateBlogPostDisabled(int blogPostId, Object connectionPoolObject) throws SQLException, Exception {
        updateBlogPostDisabled(blogPostId, (ConnectionPool) connectionPoolObject);
    }

    public static void updateBlogPostDisabled(int blogPostId, ConnectionPool connectionPool) throws SQLException, Exception {
        updateBlogPostDisabled(blogPostId, new ConnectionManager(connectionPool));
    }

    public static void updateBlogPostDisabled(int blogPostId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("updateBlogPostDisabled("+blogPostId+", "+connectionManager+")");

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("updateBlogPostDisabled");
            preparedStatement.setInt(1, blogPostId);
            preparedStatement.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPost(BlogPost blogPost, Object connectionPoolObject) throws SQLException, Exception {
        insertBlogPost(blogPost, (ConnectionPool) connectionPoolObject);
    }

    public static void insertBlogPost(BlogPost blogPost, ConnectionPool connectionPool) throws SQLException, Exception {
        insertBlogPost(blogPost, new ConnectionManager(connectionPool));
    }

    public static void insertBlogPost(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("insertBlogPost", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, blogPost.getHyphenatedName());
            preparedStatement.setString(2, blogPost.getTitle());
            preparedStatement.setString(3, blogPost.getSubtitle());
            preparedStatement.setString(4, blogPost.getBody());
            preparedStatement.setInt(5, blogPost.getUserId());
            preparedStatement.setInt(6, blogPost.getUserId());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                blogPost.setId(resultSet.getInt(1));
            }

            if(blogPost.getId() > 0) {
                insertBlogPostAuthor(blogPost.getId(), blogPost.getUserId(), connectionManager);
                if(blogPost.getImagePath() != null) insertBlogPostImage(blogPost.getId(), blogPost.getImagePath(), connectionManager);
            }

        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPost(BlogPost blogPost) throws SQLException, Exception {

        String sqlName = "insertBlogPost";
        ConnectionManager connectionManager = new ConnectionManager(className, sqlName);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, blogPost.getHyphenatedName());
            preparedStatement.setString(2, blogPost.getTitle());
            preparedStatement.setString(3, blogPost.getSubtitle());
            preparedStatement.setString(4, blogPost.getBody());
            preparedStatement.setInt(5, blogPost.getUserId());
            preparedStatement.setInt(6, blogPost.getUserId());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                blogPost.setId(resultSet.getInt(1));
                insertBlogPostAuthor(blogPost.getId(), blogPost.getUserId(), connectionManager);
            }

        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPostAuthor(int blogPostId, int userId) throws SQLException, Exception {
        logger.debug("insertBlogPostAuthor("+blogPostId+", "+userId+")");

        String sqlStatement = "insertBlogPostAuthor";
        ConnectionManager connectionManager = new ConnectionManager(className, sqlStatement);

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlStatement);
            preparedStatement.setInt(1, blogPostId);
            preparedStatement.setInt(2, userId);
        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPostAuthor(int blogPostId, int userId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertBlogPostAuthor("+blogPostId+", "+userId+", connectionManager)");

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("insertBlogPostAuthor");
            preparedStatement.setInt(1, blogPostId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPostImage(int blogPostId, String imageUrl, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertBlogPostImage("+blogPostId+", "+imageUrl+", connectionManager)");

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("insertBlogPostImage");
            preparedStatement.setInt(1, blogPostId);
            preparedStatement.setString(2, imageUrl);
            preparedStatement.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void populateBlogPost(
        BlogPost blogPost
        , ResultSet resultSet
    ) throws SQLException, Exception {

        blogPost.setId               (resultSet.getInt(1));
        blogPost.setHyphenatedName   (resultSet.getString(2));
        blogPost.setTitle            (resultSet.getString(3));
        blogPost.setSubtitle         (resultSet.getString(4));
        blogPost.setMetaDescription  (resultSet.getString(5));
        blogPost.setBody             (resultSet.getString(6));
        blogPost.setDatePublished    (resultSet.getDate(7));
        blogPost.setDatetimePublished(resultSet.getDate(8));
        blogPost.setIsDisabled       (!resultSet.getBoolean(9));
        blogPost.setPriority         (resultSet.getInt(10));

        //blogPost.setUpdatedBy        (resultSet.getInt(10));
        //blogPost.setCreatedBy        (resultSet.getInt(11));
        //blogPost.setDateUpdated      (resultSet.getTimestamp(12));
        //blogPost.setDateCreated      (resultSet.getTimestamp(13));
    }

    public static void populateBlogPost(
        BlogPost blogPost
        , ResultSet resultSet
        , ConnectionManager connectionManager
    ) throws SQLException, Exception {

        populateBlogPost(blogPost, resultSet);

        //List<BlogPost> subBlogPostList = BlogPostsManager.selectBlogPostsByBlogPostId(blogPost.getId());
        //List<BlogPost> subBlogPostList = BlogPostsManager.selectBlogPostsByBlogPostId(blogPost.getId(), connectionManager);
        //blogPost.setBlogPostList(subBlogPostList);

        selectBlogPostImages(blogPost, connectionManager);
        selectBlogPostAuthors(blogPost, connectionManager);
        selectRelatedBlogPosts(blogPost, connectionManager);
        selectKeywords(blogPost, connectionManager);
        selectBlogPostCategoryIds(blogPost, connectionManager);
    }

    public static void selectBlogPostCategoryIds(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostCategoryIds(blogPost, connectionManager)");

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostCategoryIds");
            preparedStatement.setInt(1, blogPost.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                List<Integer> blogCategoryIdList = new ArrayList<Integer>(); // Lazy instantiate the blogCategoryIdList
                do {
                    blogCategoryIdList.add(resultSet.getInt(1));
                } while(resultSet.next());
                if(blogCategoryIdList != null) logger.debug("blogCategoryIdList.size() = "+blogCategoryIdList.size());
                blogPost.setBlogCategoryIdList(blogCategoryIdList);
            }
        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

/*
+----+------------+--------------------------------------+
| 26 |        119 | uploaded/images/blog/test1/test1.png |
+----+------------+--------------------------------------+
1 row in set (0.00 sec)
*/
    public static void selectBlogPostImages(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostImages(blogPost, connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostImages");
            preparedStatement.setInt(1, blogPost.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                //blogPost.setImagePath(resultSet.getString(3));

                List<String> imageList = new ArrayList<String>();

                do {

                    imageList.add(resultSet.getString(3));

                } while(resultSet.next());

                if(imageList != null) logger.debug("imageList.size() = "+ imageList.size());

                blogPost.setImageList(imageList);

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    public static BlogPost selectBlogPostByHyphenatedName(String blogPostName, Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogPostByHyphenatedName(blogPostName, (ConnectionPool) connectionPoolObject);
    }

    public static BlogPost selectBlogPostByHyphenatedName(String blogPostName, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogPostByHyphenatedName(blogPostName, new ConnectionManager(connectionPool));
    }

    public static BlogPost selectBlogPostByHyphenatedName(String blogPostName, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostByHyphenatedName("+blogPostName+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostByHyphenatedName");
            preparedStatement.setString(1, blogPostName);

            ResultSet resultSet = preparedStatement.executeQuery();
            BlogPost blogPost = null;

            if(resultSet.next()) {

                blogPost = new BlogPost();

                //populateBlogPost(blogPost, resultSet);
                //selectBlogPostAuthors(blogPost, connectionManager);
                //selectRelatedBlogPosts(blogPost, connectionManager);
                //selectKeywords(blogPost, connectionManager);
                populateBlogPost(blogPost, resultSet, connectionManager);

                return blogPost;
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static BlogPost selectBlogPostByHyphenatedName(String blogPostName) throws SQLException, Exception {
        logger.debug("selectBlogPostByHyphenatedName("+blogPostName+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostByHyphenatedName");
            preparedStatement.setString(1, blogPostName);
            ResultSet resultSet = preparedStatement.executeQuery();
            BlogPost blogPost = null;

            if(resultSet.next()) {
                blogPost = new BlogPost();
                populateBlogPost(blogPost, resultSet, connectionManager);
                return blogPost;
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static void selectKeywords(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectKeywords(blogPost, connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectKeywords");
            preparedStatement.setInt(1, blogPost.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                List<String> keywordList = new ArrayList<String>();
                do {
                    keywordList.add(resultSet.getString(1));
                } while(resultSet.next());
                blogPost.setKeywordList(keywordList);
            }
        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    /*
     * https://github.com/commonmark/commonmark.js
     */
    public static void selectRelatedBlogPosts(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectRelatedBlogPosts(blogPost, connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectRelatedBlogPosts");
            preparedStatement.setInt(1, blogPost.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost relatedBlogPost = null;

                do {

                    relatedBlogPost = new BlogPost();

                    populateBlogPost(relatedBlogPost, resultSet);

                    selectBlogPostAuthors(relatedBlogPost, connectionManager);

                    //selectRelatedBlogPosts(relatedBlogPost, connectionManager);

                    /*
                     * Select the custom sidebar title from the blog_post_has_related_blog_posts table,
                     */
                    //String relatedBlogPostTitle = resultSet.getString(10);
                    String relatedBlogPostTitle = resultSet.getString("rp.title");
                    relatedBlogPost.setAlternativeTitle(relatedBlogPostTitle);
                    //if(relatedBlogPostTitle != null) relatedBlogPost.setTitle(relatedBlogPostTitle); // overwrite the blog post title with the title from blog_post_has_related_blog_posts

                    String relatedBlogPostImagePath = resultSet.getString("rp.imagepath");
                    if(relatedBlogPostImagePath != null) relatedBlogPost.setImagePath(relatedBlogPostImagePath);

                    blogPostList.add(relatedBlogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                blogPost.setRelatedBlogPostList(blogPostList);

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    public static List<BlogPost> selectBlogPostsByProducerId(int producerId) throws SQLException, Exception {
        logger.debug("selectBlogPostsByProducerId("+producerId+", connectionManager)");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByProducerId");

            preparedStatement.setInt(1, producerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    logger.debug(blogPost.getId() +" "+blogPost.getHyphenatedName());

                    //selectRelatedBlogPosts(relatedBlogPost, connectionManager);

                    // Select the custom sidebar title from the blog_post_has_related_blog_posts table,
                    String blogPostTitle = resultSet.getString("pp.title");
                    if(blogPostTitle != null) blogPost.setTitle(blogPostTitle); // overwrite the blog post title with the title from blog_post_has_related_blog_posts

                    String blogPostImagePath = resultSet.getString("pp.imagepath");
                    if(blogPostImagePath != null) blogPost.setImagePath(blogPostImagePath);

                    //populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPostsByUserId(int userId) throws SQLException, Exception {
        logger.debug("selectBlogPostsByUserId("+userId+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByUserId");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    //logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    //blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPostsByUserId(int userId, Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogPostsByUserId(userId, (ConnectionPool) connectionPoolObject);
    }

    public static List<BlogPost> selectBlogPostsByUserId(int userId, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogPostsByUserId(userId, new ConnectionManager(connectionPool));
    }

    public static List<BlogPost> selectBlogPostsByUserId(int userId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostsByUserId("+userId+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByUserId");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    //logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    //blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }


    public static List<BlogPost> selectBlogPostsByCategoryName(String categoryName, Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogPostsByCategoryName(categoryName, (ConnectionPool) connectionPoolObject);
    }

    public static List<BlogPost> selectBlogPostsByCategoryName(String categoryName, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogPostsByCategoryName(categoryName, new ConnectionManager(connectionPool));
    }

    public static List<BlogPost> selectBlogPostsByCategoryName(String categoryName, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostsByCategoryName("+categoryName+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByCategoryName");
            preparedStatement.setString(1, categoryName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPosts(int[] blogPostIds) throws SQLException, Exception {
        logger.debug("selectBlogPosts("+blogPostIds+")");

        logger.debug("blogPostIds = "+blogPostIds);
        logger.debug("java.util.Arrays.toString(blogPostIds) = "+java.util.Arrays.toString(blogPostIds));
        
        //ConnectionManager connectionManager = new ConnectionManager(className, com.producersmarket.servlet.InitServlet.connectionPool);
        //ConnectionManager connectionManager = new ConnectionManager();
        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "bp.id, bp.hyphenated_name, bp.title, bp.subtitle, bp.meta_description, bp.body, bp.date_published, bp.datetime_published, bp.enabled, bp.priority";

            StringBuilder sqlBuilder = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                .append(" FROM post p")
                .append(" WHERE bp.id IN (");
                for(int i = 0; i < blogPostIds.length; i++) {
                    if(i > 0) sqlBuilder.append(",");
                    sqlBuilder.append(blogPostIds[i]);
                }
                sqlBuilder.append(")");

            String sql = sqlBuilder.toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */

            String sqlFields = connectionManager.getSqlString("blog-fields");

            StringBuilder sqlBuilder = new StringBuilder().append(sqlFields);
            sqlBuilder.append("(");

            for(int i = 0; i < blogPostIds.length; i++) {
                if(i > 0) sqlBuilder.append(",");
                sqlBuilder.append(blogPostIds[i]);
            }

            sqlBuilder.append(")");

            String sql = sqlBuilder.toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    /*
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    selectRelatedBlogPosts(blogPost, connectionManager);
                    */
                    populateBlogPost(blogPost, resultSet, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static BlogPost selectBlogPost(int blogPostId) throws SQLException, Exception {
        logger.debug("selectBlogPost("+blogPostId+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPost");
            preparedStatement.setInt(1, blogPostId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                BlogPost blogPost = new BlogPost();

                /*                
                populateBlogPost(blogPost, resultSet);
                selectBlogPostAuthors(blogPost, connectionManager);
                selectRelatedBlogPosts(blogPost, connectionManager);
                selectKeywords(blogPost, connectionManager);
                */
                populateBlogPost(blogPost, resultSet, connectionManager);

                return blogPost;
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static BlogPost selectBlogPost(int blogPostId, Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogPost(blogPostId, (ConnectionPool) connectionPoolObject);
    }

    public static BlogPost selectBlogPost(int blogPostId, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogPost(blogPostId, new ConnectionManager(connectionPool));
    }

    public static BlogPost selectBlogPost(int blogPostId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPost("+blogPostId+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPost");
            preparedStatement.setInt(1, blogPostId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                BlogPost blogPost = new BlogPost();
                populateBlogPost(blogPost, resultSet, connectionManager);
                return blogPost;
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }
        return null;
    }

    public static void selectBlogPostAuthors(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostAuthors(blogPost, connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostAuthors");
            preparedStatement.setInt(1, blogPost.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<User> userList = null;
                User user = null;

                do {

                    boolean showAuthor = resultSet.getBoolean("showAuthor");

                    if(showAuthor) {

                        if(userList == null) userList = new ArrayList<User>();

                        user = new User();
                        user.setId                 (resultSet.getInt   (1));
                        user.setName               (resultSet.getString(2));
                        user.setHyphenatedName     (resultSet.getString(4));
                        userList.add(user);
                    }

                } while(resultSet.next());

                if(userList != null) logger.debug("userList.size() = "+userList.size());
                blogPost.setAuthorList(userList);

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }
    }

    public static void populateUser(User user, ResultSet resultSet) throws SQLException, Exception {

        user.setId                 (resultSet.getInt   (1));
        user.setName               (resultSet.getString(2));
        user.setBusinessName       (resultSet.getString(3));
        user.setHyphenatedName     (resultSet.getString(4));
        user.setInstagramUsername  (resultSet.getString(5));
        user.setLocation           (resultSet.getString(6));
        user.setThemeColor         (resultSet.getString(7));
        //user.setBackgroundImage    (resultSet.getString(21));
        //user.setLogoImage          (resultSet.getString(22));
        //user.setPromoVideo         (resultSet.getString(23));
    }

}
