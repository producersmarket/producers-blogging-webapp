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

//import com.ispaces.database.connection.ConnectionManager;
import com.ispaces.dbcp.ConnectionManager;

import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.model.Product;
import com.producersmarket.model.User;

public class BlogCategoryDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = BlogCategoryDatabaseManager.class.getSimpleName();

    public static List<Product> selectBlogCategoriesOrderByPriority() throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            //String sql = "SELECT id, category FROM blog_category ORDER BY priority";
            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoriesOrderByPriority");

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<Product> productList = new ArrayList<Product>();
                Product product = new Product();

                do {

                    product = new Product();

                    product.setId(resultSet.getInt(1));
                    product.setName(resultSet.getString(2));

                    logger.debug("product.getName() = "+product.getName());

                    productList.add(product);

                } while(resultSet.next());

                logger.debug("productList.size() = "+productList.size());

                selectBlogCategoryImages(productList, connectionManager);

                return productList;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<Product> selectBlogCategoriesOrderByPriority(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogCategoriesOrderByPriority(connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoriesOrderByPriority");

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<Product> productList = new ArrayList<Product>();
                Product product = new Product();

                do {

                    product = new Product();

                    product.setId(resultSet.getInt(1));
                    product.setName(resultSet.getString(2));

                    logger.debug("product.getName() = "+product.getName());

                    productList.add(product);

                } while(resultSet.next());

                logger.debug("productList.size() = "+productList.size());

                selectBlogCategoryImages(productList, connectionManager);

                return productList;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static Map<Integer, Product> selectBlogCategoryMapOrderByPriority() throws SQLException, Exception {
        logger.debug("selectBlogCategoryMapOrderByPriority()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            //String sql = "SELECT id, category FROM blog_category ORDER BY priority";
            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoryMapOrderByPriority");
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                Map<Integer, Product> blogCategoryMap = new HashMap<Integer, Product>();
                Product product = new Product();

                do {

                    product = new Product();

                    product.setId(resultSet.getInt(1));
                    product.setName(resultSet.getString(2));

                    logger.debug("product.getName() = "+product.getName());

                    blogCategoryMap.put(product.getId(), product);

                } while(resultSet.next());

                logger.debug("blogCategoryMap.size() = "+blogCategoryMap.size());

                //selectBlogCategoryImages(productList, connectionManager);

                return blogCategoryMap;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static void selectBlogCategoryImages(List<Product> productList, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogCategoryImages(productList, connectionManager)");

        try {

            //String sql = "SELECT image_path FROM blog_category_has_image WHERE blog_category_id = ?";
            Product product = null;
            Iterator<Product> productIterator = productList.iterator();

            while(productIterator.hasNext()) {

                product = productIterator.next();

                //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
                PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogCategoryImages");
                preparedStatement.setInt(1, product.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()) {

                    product.setImageUrl(resultSet.getString(1));

                    logger.debug("product.getImageUrl() = "+product.getImageUrl());
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

            /*
            String sql = new StringBuilder()
                .append("SELECT ")
                .append(" category")
                .append(" FROM blog_category bc, blog_post_has_category bphc")
                .append(" WHERE blog_post_id = ?")
                .append(" AND bc.id = bphc.blog_category_id")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.prepareStatement("selectBlogPostCategories");
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
