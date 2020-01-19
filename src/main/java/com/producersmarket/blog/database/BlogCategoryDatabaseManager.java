package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.blog.model.BlogCategory;
import com.producersmarket.blog.model.Menuable;
import com.producersmarket.blog.model.User;

public class BlogCategoryDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = BlogCategoryDatabaseManager.class.getSimpleName();

    public static List<Integer> insertBlogPostCategory(int blogPostId, int blogCategoryId, Object connectionPoolObject) throws SQLException, Exception {
        return insertBlogPostCategory(blogPostId, blogCategoryId, (ConnectionPool) connectionPoolObject);
    }

    public static List<Integer> insertBlogPostCategory(int blogPostId, int blogCategoryId, ConnectionPool connectionPool) throws SQLException, Exception {
        return insertBlogPostCategory(blogPostId, blogCategoryId, new ConnectionManager(connectionPool));
    }

    public static List<Integer> insertBlogPostCategory(int blogPostId, int blogCategoryId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertBlogPostCategory("+blogPostId+", "+blogCategoryId+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("insertBlogPostCategory");
            preparedStatement.setInt(1, blogPostId);
            preparedStatement.setInt(2, blogCategoryId);
            preparedStatement.executeUpdate();

            preparedStatement = connectionManager.loadStatement("selectBlogPostCategoryIds");
            preparedStatement.setInt(1, blogPostId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                List<Integer> blogCategoryIdList = new ArrayList<Integer>();
                do {
                    blogCategoryIdList.add(resultSet.getInt(1));
                } while(resultSet.next());
                logger.debug("blogCategoryIdList = "+blogCategoryIdList);
                return blogCategoryIdList;
            }

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

    public static void insertBlogPostCategories(String insertBlogPostCategoriesSql, Object connectionPoolObject) throws SQLException, Exception {
        insertBlogPostCategories(insertBlogPostCategoriesSql, (ConnectionPool) connectionPoolObject);
    }

    public static void insertBlogPostCategories(String insertBlogPostCategoriesSql, ConnectionPool connectionPool) throws SQLException, Exception {
        insertBlogPostCategories(insertBlogPostCategoriesSql, new ConnectionManager(connectionPool));
    }

    public static void insertBlogPostCategories(String insertBlogPostCategoriesSql, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertBlogPostCategories("+insertBlogPostCategoriesSql+", connectionManager)");

        try {
            PreparedStatement preparedStatement = connectionManager.prepareStatement(insertBlogPostCategoriesSql);
            preparedStatement.executeUpdate();
        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }
    }

    public static List<Integer> insertBlogPostCategories(int blogPostId, String insertBlogPostCategoriesSql, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertBlogPostCategories("+insertBlogPostCategoriesSql+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.prepareStatement(insertBlogPostCategoriesSql);
            preparedStatement.executeUpdate();

            preparedStatement = connectionManager.loadStatement("selectBlogPostCategoryIds");
            preparedStatement.setInt(1, blogPostId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                List<Integer> blogCategoryIdList = new ArrayList<Integer>();
                do {
                    blogCategoryIdList.add(resultSet.getInt(1));
                } while(resultSet.next());
                logger.debug("blogCategoryIdList = "+blogCategoryIdList);
                return blogCategoryIdList;
            }

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
            
    public static Map<Integer, String> selectBlogCategories(Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogCategories( (ConnectionPool) connectionPoolObject );
    }

    public static Map<Integer, String> selectBlogCategories(ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogCategories( new ConnectionManager(connectionPool) );
    }

    public static Map<Integer, String> selectBlogCategories(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogCategories(connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategories");
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                Map<Integer, String> blogCategoryMap = new HashMap<Integer, String>();

                do {

                    blogCategoryMap.put(resultSet.getInt(1), resultSet.getString(2));

                } while(resultSet.next());

                logger.debug("blogCategoryMap.size() = "+blogCategoryMap.size());

                return blogCategoryMap;

            } // if(resultSet.next()) {

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    //public static List<BlogCategory> selectBlogCategoriesOrderByPriority() throws SQLException, Exception {
    public static List<Menuable> selectBlogCategoriesOrderByPriority() throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            //PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoriesOrderByPriority");
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectNonEmptyBlogCategoriesOrderByPriority");

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                //List<BlogCategory> blogCategoryList = new ArrayList<BlogCategory>();
                List<Menuable> blogCategoryList = new ArrayList<Menuable>();
                //BlogCategory blogCategory = new BlogCategory();
                Menuable blogCategory = new BlogCategory();

                do {

                    blogCategory = new BlogCategory();

                    blogCategory.setId(resultSet.getInt(1));
                    blogCategory.setName(resultSet.getString(2));
                    blogCategory.setHyphenatedName(resultSet.getString(3));
                    blogCategory.setImagePath(resultSet.getString(4));
                    blogCategory.setImagePathHover(resultSet.getString(5));

                    logger.debug("blogCategory.getName() = "+blogCategory.getName());

                    blogCategoryList.add(blogCategory);

                } while(resultSet.next());

                logger.debug("blogCategoryList.size() = "+blogCategoryList.size());

                //selectBlogCategoryImages(blogCategoryList, connectionManager);

                return blogCategoryList;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<Menuable> selectBlogCategoriesOrderByPriority(Object connectionPoolObject) throws SQLException, Exception {
        return selectBlogCategoriesOrderByPriority((ConnectionPool) connectionPoolObject);
    }

    public static List<Menuable> selectBlogCategoriesOrderByPriority(ConnectionPool connectionPool) throws SQLException, Exception {
        return selectBlogCategoriesOrderByPriority(new ConnectionManager(connectionPool));
    }

    public static List<Menuable> selectBlogCategoriesOrderByPriority(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority("+connectionManager+")");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectNonEmptyBlogCategoriesOrderByPriority");
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                //List<BlogCategory> blogCategoryList = new ArrayList<BlogCategory>();
                List<Menuable> blogCategoryList = new ArrayList<Menuable>();
                //BlogCategory blogCategory = new BlogCategory();
                Menuable blogCategory = new BlogCategory();

                do {

                    blogCategory = new BlogCategory();
                    blogCategory.setId(resultSet.getInt(1));
                    blogCategory.setName(resultSet.getString(2));
                    blogCategory.setHyphenatedName(resultSet.getString(3));
                    blogCategory.setImagePath(resultSet.getString(4));
                    blogCategory.setImagePathHover(resultSet.getString(5));

                    logger.debug("blogCategory.getName() = "+blogCategory.getName());

                    blogCategoryList.add(blogCategory);

                } while(resultSet.next());

                logger.debug("blogCategoryList.size() = "+blogCategoryList.size());

                //selectBlogCategoryImages(blogCategoryList, connectionManager);

                return blogCategoryList;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static Map<Integer, BlogCategory> selectBlogCategoryMapOrderByPriority() throws SQLException, Exception {
        logger.debug("selectBlogCategoryMapOrderByPriority()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoryMapOrderByPriority");
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                Map<Integer, BlogCategory> blogCategoryMap = new HashMap<Integer, BlogCategory>();
                BlogCategory blogCategory = new BlogCategory();

                do {

                    blogCategory = new BlogCategory();

                    blogCategory.setId(resultSet.getInt(1));
                    blogCategory.setName(resultSet.getString(2));

                    logger.debug("blogCategory.getName() = "+blogCategory.getName());

                    blogCategoryMap.put(blogCategory.getId(), blogCategory);

                } while(resultSet.next());

                logger.debug("blogCategoryMap.size() = "+blogCategoryMap.size());

                //selectBlogCategoryImages(blogCategoryList, connectionManager);

                return blogCategoryMap;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static void selectBlogCategoryImages(List<BlogCategory> blogCategoryList, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogCategoryImages(blogCategoryList, connectionManager)");

        try {

            BlogCategory blogCategory = null;
            Iterator<BlogCategory> blogCategoryIterator = blogCategoryList.iterator();

            while(blogCategoryIterator.hasNext()) {

                blogCategory = blogCategoryIterator.next();
                PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoryImages");
                preparedStatement.setInt(1, blogCategory.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()) {
                    blogCategory.setImagePath(resultSet.getString(1));
                    logger.debug("blogCategory.getImagePath() = "+blogCategory.getImagePath());
                }
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

    public static void selectBlogPostCategoryIds(BlogPost blogPost, Object connectionPoolObject) throws SQLException, Exception {
        selectBlogPostCategoryIds( blogPost, (ConnectionPool) connectionPoolObject);
    }

    public static void selectBlogPostCategoryIds(BlogPost blogPost, ConnectionPool connectionPool) throws SQLException, Exception {
        selectBlogPostCategoryIds( blogPost, new ConnectionManager(connectionPool));
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
            } // if(resultSet.next()) {
        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }
    }

    public static void selectBlogPostCategories(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostCategories(blogPost, connectionManager)");

        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostCategories");
            preparedStatement.setInt(1, blogPost.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                List<String> blogCategoryList = new ArrayList<String>(); // Lazy instantiate the blogCategoryIdList
                do {
                    blogCategoryList.add(resultSet.getString(1));
                } while(resultSet.next());
                if(blogCategoryList != null) logger.debug("blogCategoryList.size() = "+blogCategoryList.size());
                blogPost.setBlogCategoryList(blogCategoryList);
            } // if(resultSet.next()) {
        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }
    }

}
