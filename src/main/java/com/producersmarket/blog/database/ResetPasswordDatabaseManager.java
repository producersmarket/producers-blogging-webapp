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

public class ResetPasswordDatabaseManager {

    private static final String className = ResetPasswordDatabaseManager.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger();

    private static final String insertResetToken = "insertResetToken";
    private static final String deleteResetToken = "deleteResetToken";
    private static final String INSERT_FORGOT_PASSWORD = "insertForgotPassword";
    private static final String DELETE_FORGOT_PASSWORD = "deleteForgotPassword";
    private static final String UPDATE_PASSWORD_HASH = "updatePasswordHash";
    private static final String selectUserIdByPasswordResetToken = "selectUserIdByPasswordResetToken";

    public static void updatePassword(int userId, String password, Object connectionPoolObject) throws SQLException, Exception {
        updatePassword(userId, password, (ConnectionPool) connectionPoolObject);
    }

    public static void updatePassword(int userId, String password, ConnectionPool connectionPool) throws SQLException, Exception {
        updatePassword(userId, password, new ConnectionManager(connectionPool));
    }

    public static void updatePassword(int userId, String password, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("updatePassword("+userId+", password, connectionManager)");
        try {
            PreparedStatement stmt = connectionManager.loadStatement(UPDATE_PASSWORD_HASH);
            stmt.setString(1, password);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void updatePassword(int userId, String password) throws SQLException, Exception {
        logger.debug("updatePassword("+userId+")");
        ConnectionManager connectionManager = new ConnectionManager(className, UPDATE_PASSWORD_HASH);
        try {
            PreparedStatement stmt = connectionManager.loadStatement(UPDATE_PASSWORD_HASH);
            stmt.setString(1, password);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static int selectUserIdByPasswordResetToken(String code) throws SQLException, Exception {
        ConnectionManager connectionManager = new ConnectionManager(className, selectUserIdByPasswordResetToken);
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserIdByPasswordResetToken");
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } finally {
            connectionManager.commit();
        }
        return -1;
    }

    public static int selectUserIdByPasswordResetToken(String code, Object connectionPoolObject) throws SQLException, Exception {
        return selectUserIdByPasswordResetToken(code, (ConnectionPool) connectionPoolObject);
    }

    public static int selectUserIdByPasswordResetToken(String code, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectUserIdByPasswordResetToken(code, new ConnectionManager(connectionPool));
    }

    public static int selectUserIdByPasswordResetToken(String code, ConnectionManager connectionManager) throws SQLException, Exception {
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserIdByPasswordResetToken");
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } finally {
            connectionManager.commit();
        }
        return -1;
    }

    public static void insertResetToken(int userId, String code) throws SQLException, Exception {
        logger.debug("insertResetToken(userId:"+userId+")");
        ConnectionManager connectionManager = new ConnectionManager(className, insertResetToken);
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("insertResetToken");
            preparedStatement.setString(1, code);
            preparedStatement.setInt(2, userId);
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

    public static void insertResetToken(String email, String code, Object connectionPoolObject) throws SQLException, Exception {
        insertResetToken(email, code, (ConnectionPool) connectionPoolObject);
    }

    public static void insertResetToken(String email, String code, ConnectionPool connectionPool) throws SQLException, Exception {
        insertResetToken(email, code, new ConnectionManager(connectionPool));
    }

    public static void insertResetToken(String email, String code, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insertResetToken("+email+", connectionManager)");
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("insertResetTokenByEmail");
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, email);
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

    public static void insertResetToken(String email, String code) throws SQLException, Exception {
        logger.debug("insertResetToken("+email+")");
        ConnectionManager connectionManager = new ConnectionManager(className, insertResetToken);
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("insertResetTokenByEmail");
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, email);
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

    public static void deleteResetToken(int userId) throws SQLException, Exception {
        logger.debug("deleteResetToken("+userId+")");
        ConnectionManager connectionManager = new ConnectionManager(className, deleteResetToken);
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("deleteResetToken");
            preparedStatement.setString(1, null);
            preparedStatement.setInt(2, userId);
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
    
    public static void deleteResetToken(String email, String code) throws SQLException, Exception {
        logger.debug("deleteResetToken("+email+")");
        ConnectionManager connectionManager = new ConnectionManager(className, deleteResetToken);
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("deleteResetToken");
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, email);
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

    public static void insert(int userId, String code) throws SQLException, Exception {
        logger.debug("insert("+userId+")");
        ConnectionManager connectionManager = new ConnectionManager(className);
        try {
            PreparedStatement stmt = connectionManager.loadStatement(INSERT_FORGOT_PASSWORD);
            stmt.setString(1, code);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void delete(String code) throws SQLException, Exception {
        logger.debug("delete code");
        ConnectionManager connectionManager = new ConnectionManager(className);
        try {
            PreparedStatement stmt = connectionManager.loadStatement(DELETE_FORGOT_PASSWORD);
            stmt.setString(1, code);
            stmt.executeUpdate();
        } finally {
            connectionManager.commit();
        }
    }

    public static void deleteResetToken(int userId, Object connectionPoolObject) throws SQLException, Exception {
        deleteResetToken(userId, (ConnectionPool) connectionPoolObject);
    }

    public static void deleteResetToken(int userId, ConnectionPool connectionPool) throws SQLException, Exception {
        deleteResetToken(userId, new ConnectionManager(connectionPool));
    }

    public static void deleteResetToken(int userId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("deleteResetToken("+userId+", connectionManager)");
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("deleteResetToken");
            preparedStatement.setString(1, null);
            preparedStatement.setInt(2, userId);
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

    public static void updateUserEmailVerified(int userId, Object connectionPoolObject) throws SQLException, Exception {
        updateUserEmailVerified(userId, (ConnectionPool) connectionPoolObject);
    }

    public static void updateUserEmailVerified(int userId, ConnectionPool connectionPool) throws SQLException, Exception {
        updateUserEmailVerified(userId, new ConnectionManager(connectionPool));
    }

    public static void updateUserEmailVerified(int userId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("updateUserEmailVerified("+userId+", connectionManager)");
        try {
            PreparedStatement preparedStatement = connectionManager.loadStatement("updateUserEmailVerified");
            preparedStatement.setString(1, null);
            preparedStatement.setInt(2, userId);
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

}