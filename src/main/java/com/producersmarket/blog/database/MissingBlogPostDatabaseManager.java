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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.markdown.LinkNodeRenderer;
import com.producersmarket.blog.markdown.SidebarNodeRenderer;
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.blog.model.User;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


public class MissingBlogPostDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = MissingBlogPostDatabaseManager.class.getSimpleName();

    public static void insertBlogPostName(String blogPostName, Object connectionPoolObject) throws SQLException, Exception {
        insertBlogPostName(blogPostName, (ConnectionPool) connectionPoolObject);
    }

    public static void insertBlogPostName(String blogPostName, ConnectionPool connectionPool) throws SQLException, Exception {
        insertBlogPostName(blogPostName, new ConnectionManager(connectionPool));
    }

    public static void insertBlogPostName(String blogPostName, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertBlogPostName("+blogPostName+", connectionManager)");

        try {

            String sql = new StringBuilder()
                .append("INSERT ")
                .append("INTO missing_blog_post")  
                .append("(hyphenated_name)")  
                .append("VALUES")
                .append("(?)")  
                .toString();

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, blogPostName);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

    }

    public static void insertBlogPostName(String blogPostName) throws SQLException, Exception {
        logger.debug("insertBlogPostName("+blogPostName+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            String sql = new StringBuilder()
                .append("INSERT ")
                .append("INTO missing_blog_post")  
                .append("(hyphenated_name)")  
                .append("VALUES")
                .append("(?)")  
                .toString();

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, blogPostName);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

    }

}
