package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

import com.producersmarket.blog.model.Session;

public class SessionDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = SessionDatabaseManager.class.getSimpleName();


    public static List<String> selectSessionIdsByUserId(int userId, Object connectionPool) throws SQLException, Exception {
        return selectSessionIdsByUserId(userId, (ConnectionPool) connectionPool);
    }

    public static List<String> selectSessionIdsByUserId(int userId, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectSessionIdsByUserId(userId, new ConnectionManager(connectionPool));
    }

    public static List<String> selectSessionIdsByUserId(int userId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectSessionIdsByUserId("+userId+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectSessionIdsByUserId");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<String> sessionIdList = new ArrayList<String>();

                do {

                    sessionIdList.add( resultSet.getString(1) );

                } while(resultSet.next());

                return sessionIdList; 
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static int selectUserIdBySessionId(String sessionId, Object connectionPool) throws SQLException, Exception {
        return selectUserIdBySessionId(sessionId, (ConnectionPool) connectionPool);
    }

    public static int selectUserIdBySessionId(String sessionId, ConnectionPool connectionPool) throws SQLException, Exception {
        return selectUserIdBySessionId(sessionId, new ConnectionManager(connectionPool));
    }

    public static int selectUserIdBySessionId(String sessionId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectUserIdBySessionId("+sessionId+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserIdBySessionId");
            preparedStatement.setString(1, sessionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                return resultSet.getInt(1);
            }

        } finally {
            connectionManager.commit();
        }

        return -1;
    }

    public static void insert(Session session, Object connectionPoolObject) throws SQLException, Exception {
        insert(session, (ConnectionPool) connectionPoolObject);
    }

    public static void insert(Session session, ConnectionPool connectionPool) throws SQLException, Exception {
        insert(session, new ConnectionManager(connectionPool));
    }

    public static void insert(Session session, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("insert("+session+", "+connectionManager+")");

        String statementName = "insertSession";

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement(statementName);
            preparedStatement.setInt(1, session.getUserId());
            preparedStatement.setString(2, session.getSessionId());
            preparedStatement.setString(3, session.getUserAgent());
            preparedStatement.setString(4, session.getRemoteAddr());
            preparedStatement.setString(5, session.getHost());
            preparedStatement.setString(6, session.getProtocol());
            preparedStatement.setString(7, session.getLocale());
            preparedStatement.setString(8, session.getCharacterEncoding());
            preparedStatement.setString(9, session.getAccept());
            preparedStatement.setString(10, session.getAcceptEncoding());
            preparedStatement.setString(11, session.getAcceptLanguage());
            preparedStatement.setString(12, session.getAcceptCharset());
            preparedStatement.setString(13, session.getReferer());
            preparedStatement.setString(14, session.getServerName());
            preparedStatement.setInt(15, session.getServerPort());
            preparedStatement.setString(16, session.getServerInfo());
            preparedStatement.setInt(17, session.getUserId());
            preparedStatement.setInt(18, session.getUserId());
            preparedStatement.executeUpdate();

        } catch(Exception e) {

            logger.error(new StringBuilder().append(e.getMessage()).append(" ").append(statementName));

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

    public static void insert(Session session) throws SQLException, Exception {
        logger.debug("insert("+session+")");

        ConnectionManager connectionManager = new ConnectionManager(className);
        String statementName = "insertSession";

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement(statementName);
            preparedStatement.setInt(1, session.getUserId());
            preparedStatement.setString(2, session.getSessionId());
            preparedStatement.setString(3, session.getUserAgent());
            preparedStatement.setString(4, session.getRemoteAddr());
            preparedStatement.setString(5, session.getHost());
            preparedStatement.setString(6, session.getProtocol());
            preparedStatement.setString(7, session.getLocale());
            preparedStatement.setString(8, session.getCharacterEncoding());
            preparedStatement.setString(9, session.getAccept());
            preparedStatement.setString(10, session.getAcceptEncoding());
            preparedStatement.setString(11, session.getAcceptLanguage());
            preparedStatement.setString(12, session.getAcceptCharset());
            preparedStatement.setString(13, session.getReferer());
            preparedStatement.setString(14, session.getServerName());
            preparedStatement.setInt(15, session.getServerPort());
            preparedStatement.setString(16, session.getServerInfo());
            preparedStatement.setInt(17, session.getUserId());
            preparedStatement.setInt(18, session.getUserId());
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            logger.error(new StringBuilder().append(e.getMessage()).append(" ").append(statementName));
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

}
