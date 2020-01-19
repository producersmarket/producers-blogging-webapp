package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

public class DatabaseManager {

    private static final Logger logger = LogManager.getLogger();

    public static void executeUpdate(String sql, Object connectionPoolObject) throws SQLException, Exception {
        executeUpdate(sql, (ConnectionPool) connectionPoolObject);
    }

    public static void executeUpdate(String sql, ConnectionPool connectionPool) throws SQLException, Exception {
        executeUpdate(sql, new ConnectionManager(connectionPool));
    }

    public static void executeUpdate(String sql, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("executeUpdate(sql, connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

    public static void executeUpdate(String sql, int id, Object connectionPoolObject) throws SQLException, Exception {
        executeUpdate(sql, id, new ConnectionManager((ConnectionPool) connectionPoolObject));
    }

    public static void executeUpdate(String sql, int id, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("executeUpdate(sql, id, connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

}
