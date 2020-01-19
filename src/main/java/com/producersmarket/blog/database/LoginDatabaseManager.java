package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

public class LoginDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = LoginDatabaseManager.class.getSimpleName();

    public static void updateUserLoggedIn(int userId, String sessionId) throws SQLException, Exception {
        logger.debug("updateUserLoggedIn("+userId+", '"+sessionId+"')");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("updateUserLoggedIn");
            preparedStatement.setString(1, sessionId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            try {
                connectionManager.commit();
            } catch(Exception exception) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);
                logger.error(stringWriter.toString());
            }
        }
    }

    public static int selectUserIdByEmail(String email, Object connectionPoolObject) throws SQLException, Exception {
        return selectUserIdByEmail(email, (ConnectionPool) connectionPoolObject);
    }

    public static int selectUserIdByEmail(String email, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectUserIdByEmail(email, new ConnectionManager(connectionPool));
    }

    public static int selectUserIdByEmail(String email, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectUserIdByEmail("+email+", "+connectionManager+")");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserIdByEmail");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                return resultSet.getInt(1);
            }

        } finally {
            connectionManager.commit();
        }

        return -1;
    }

    public static int selectUserIdByEmail(String email) throws SQLException, Exception {
        logger.debug("selectUserIdByEmail("+email+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserIdByEmail");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                return resultSet.getInt(1);
            }

        } finally {
            connectionManager.commit();
        }

        return -1;
    }

    public static String selectPasswordHashByEmail(String email, Object connectionPoolObject) throws SQLException, Exception {
        return selectPasswordHashByEmail(email, (ConnectionPool) connectionPoolObject);
    }

    public static String selectPasswordHashByEmail(String email, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectPasswordHashByEmail(email, new ConnectionManager(connectionPool));
    }

    public static String selectPasswordHashByEmail(String email, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectPasswordHashByEmail("+email+", "+connectionManager+")");
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectPasswordHashByEmail");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString(1);
            }
        } finally {
            connectionManager.commit();
        }
        return null;
    }

    public static String selectPasswordHashByEmail(String email) throws SQLException, Exception {
        logger.debug("selectPasswordHashByEmail("+email+")");
        ConnectionManager connectionManager = new ConnectionManager(className);
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectPasswordHashByEmail");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString(1);
            }
        } finally {
            connectionManager.commit();
        }
        return null;
    }
    
    public static int selectUserEmailVerifiedByEmail(String email, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectUserEmailVerifiedByEmail(email, new ConnectionManager(connectionPool));
    }

    public static int selectUserEmailVerifiedByEmail(String email, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectUserEmailVerifiedByEmail("+email+", "+connectionManager+")");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserEmailVerifiedByEmail");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                return resultSet.getInt(1);
            }

        } finally {
            connectionManager.commit();
        }

        return -1;
    }

}
