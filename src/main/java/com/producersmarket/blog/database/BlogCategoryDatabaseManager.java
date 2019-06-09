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
import com.producersmarket.model.User;

public class BlogCategoryDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = BlogCategoryDatabaseManager.class.getSimpleName();

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

    //public static List<BlogCategory> selectBlogCategoriesOrderByPriority(Object connectionPoolObject) throws SQLException, Exception {
    public static List<Menuable> selectBlogCategoriesOrderByPriority(Object connectionPoolObject) throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority("+connectionPoolObject+")");

        return selectBlogCategoriesOrderByPriority((ConnectionPool) connectionPoolObject);
    }

    //public static List<BlogCategory> selectBlogCategoriesOrderByPriority(ConnectionPool connectionPool) throws SQLException, Exception {
    public static List<Menuable> selectBlogCategoriesOrderByPriority(ConnectionPool connectionPool) throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority("+connectionPool+")");

        return selectBlogCategoriesOrderByPriority(new ConnectionManager(connectionPool));
    }

    //public static List<BlogCategory> selectBlogCategoriesOrderByPriority(ConnectionManager connectionManager) throws SQLException, Exception {
    public static List<Menuable> selectBlogCategoriesOrderByPriority(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority("+connectionManager+")");

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

    public static Map<Integer, BlogCategory> selectBlogCategoryMapOrderByPriority() throws SQLException, Exception {
        logger.debug("selectBlogCategoryMapOrderByPriority()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            //String sql = "SELECT id, category FROM blog_category ORDER BY priority";
            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
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

            //String sql = "SELECT image_path FROM blog_category_has_image WHERE blog_category_id = ?";
            BlogCategory blogCategory = null;
            Iterator<BlogCategory> blogCategoryIterator = blogCategoryList.iterator();

            while(blogCategoryIterator.hasNext()) {

                blogCategory = blogCategoryIterator.next();

                //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
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

    public static void selectBlogPostCategoryIds(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostCategoryIds(blogPost, connectionManager)");

        try {

            /*
            String sql = new StringBuilder()
                .append("SELECT ")
                .append(" blog_category_id")
                .append(" FROM blog_post_has_category")
                .append(" WHERE blog_post_id = ?")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.prepareStatement("selectBlogPostCategoryIds");
            preparedStatement.setInt(1, blogPost.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                //List userList = new ArrayList();
                List<Integer> blogCategoryIdList = new ArrayList<Integer>(); // Lazy instantiate the blogCategoryIdList

                do {

                    blogCategoryIdList.add(resultSet.getInt(1));

                } while(resultSet.next());

                if(blogCategoryIdList != null) logger.debug("blogCategoryIdList.size() = "+blogCategoryIdList.size());

                //return userList;
                blogPost.setBlogCategoryIdList(blogCategoryIdList);

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //return null;
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
        }

    }

}
