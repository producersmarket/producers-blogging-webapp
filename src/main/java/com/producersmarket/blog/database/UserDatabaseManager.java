package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;
import com.ispaces.dbcp.ConnectionPool;

//import com.producersmarket.model.BlogPost;
//import com.producersmarket.model.User;
import com.producersmarket.blog.model.User;

public class UserDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = UserDatabaseManager.class.getSimpleName();

    private static final String selectUserByPasswordResetCode = "selectUserByPasswordResetCode";
    private static final String UPDATE_PASSWORD_HASH = "updatePasswordHash";
    private static final int USER_GROUP_BLOG_AUTHOR = 8;

    private static String sqlSelectUserFields = new StringBuilder()
        .append("u.id, u.name, u.business_name, u.hyphenated_name, u.location, u.theme_color")
        .append(", uhi.background_image, uhi.logo_image, uhi.promo_video")
        .toString();

    /*
    public static void updatePassword(int userId, String password) throws SQLException, Exception {
        logger.debug("updatePassword("+userId+", '"+password+"')");

        ConnectionManager connMgr = new ConnectionManager(className, UPDATE_PASSWORD_HASH);

        try {

            PreparedStatement stmt = connMgr.loadStatement(UPDATE_PASSWORD_HASH);
            stmt.setString(1, password);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } finally {
            connMgr.commit();
        }

    }
    */

    /*
    public static User selectUserByPasswordResetCode(String code) throws SQLException, Exception {
        logger.debug("selectUserByPasswordResetCode("+code+")");

        ConnectionManager connMgr = new ConnectionManager(className, selectUserByPasswordResetCode);

        try {

            PreparedStatement stmt = connMgr.loadStatement(selectUserByPasswordResetCode);
            stmt.setString(1, code);

            ResultSet resultSet = stmt.executeQuery();

            if(resultSet.next()) {

                User user = new User();
                //populateUser(user, resultSet);
                user.setId(resultSet.getInt(1));
                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));

                return user;
            }

        } finally {
            connMgr.commit();
        }

        return null;
    }
    */

    public static User selectUserByEmail(String email) throws SQLException, Exception {
        logger.debug("selectUserByEmail("+email+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            String sql = new StringBuilder()
                .append("SELECT id, name, email")
                .append(" FROM user")
                .append(" WHERE email = ?")
                .toString();
                
            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                User user = new User();
                user.setId(resultSet.getInt(1));
                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));

                return user;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    /*
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
    */

    public static User selectUserById(int userId, Object connectionPoolObject) throws SQLException, Exception {
        logger.debug("selectUserById("+userId+", "+connectionPoolObject+")");

        return selectUserById(userId, (ConnectionPool) connectionPoolObject);
    }

    public static User selectUserById(int userId, ConnectionPool connectionPool) throws SQLException, Exception {
        logger.debug("selectUserById("+userId+", "+connectionPool+")");

        return selectUserById(userId, new ConnectionManager(connectionPool));
    }

    public static User selectUserById(int userId, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectUserById("+userId+", connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserById");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                User user = new User();
                //populateUser(user, resultSet);
                user.setId(resultSet.getInt(1));
                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));

                return user;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static User selectUserById(int userId) throws SQLException, Exception {
        logger.debug("selectUserById("+userId+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            String sql = new StringBuilder()
                .append("SELECT u.id, u.name, u.email")
                .append(" FROM user u")
                .append(" WHERE u.id = ?")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                User user = new User();
                //populateUser(user, resultSet);
                user.setId(resultSet.getInt(1));
                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));

                return user;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }

    /*
    public static void selectGroupIdsByUserId(User user, ConnectionManager connectionManager) throws SQLException, Exception {

        PreparedStatement preparedStatement = connectionManager.loadStatement("selectGroupIdsByUserId");
        preparedStatement.setInt(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            List<Integer> groupIdList = new ArrayList();
            do {
                groupIdList.add(resultSet.getInt(1));
            } while(resultSet.next());
            user.setGroupIdList(groupIdList);
        }
    }
    */

    public static List<User> selectBlogAuthors(Object connectionPoolObject) throws SQLException, Exception {
        logger.debug("selectBlogAuthors("+connectionPoolObject+")");

        return selectBlogAuthors(new ConnectionManager((ConnectionPool) connectionPoolObject));
    }

    public static List<User> selectBlogAuthors(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogAuthors(connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserByGroupId");
            preparedStatement.setInt(1, 4); /// here add userGroup ID for bloggers
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                List<User> userList = null;
                User user = null;
                if(resultSet.next()) {
                    userList = new ArrayList<User>();
                    do {
                        user = new User ();
                        user.setId(resultSet.getInt(1));
                        user.setName(resultSet.getString(2));
                        user.setHyphenatedName(resultSet.getString(3));
                        userList.add( user );
                    } while(resultSet.next());
                    logger.debug("userList.size() = "+userList.size());
                }
                return userList;
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

    public static Map<Integer, String> selectAllBlogAuthors(Object connectionPoolObject) throws SQLException, Exception {
        logger.debug("selectAllBlogAuthors("+connectionPoolObject+")");

        return selectAllBlogAuthors(new ConnectionManager((ConnectionPool) connectionPoolObject));
    }

    public static Map<Integer, String> selectAllBlogAuthors(ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectAllBlogAuthors(connectionManager)");

        try {

            PreparedStatement preparedStatement = connectionManager.loadStatement("selectUserByGroupId");
            preparedStatement.setInt(1, USER_GROUP_BLOG_AUTHOR); /// here add userGroup ID for bloggers
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                Map<Integer, String> userMap = new HashMap<Integer, String>();
                do {
                    userMap.put( resultSet.getInt(1), resultSet.getString(2) );
                } while(resultSet.next());
                logger.debug( "userMap.size() = " + userMap.size() );
                return userMap;
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

    /*
    //public static List<User> selectBlogPostAuthors(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
    public static void selectBlogPostAuthors(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostAuthors(blogPost, connectionManager)");

        try {

            String sql = new StringBuilder()
                .append("SELECT ")
                .append("u.id, u.name, u.business_name, u.hyphenated_name, u.ig_username, u.location, u.theme_color, bpha.show_author")
                .append(" FROM user u, blog_post_has_author bpha")
                .append(" WHERE blog_post_id = ?")
                .append(" AND u.id = bpha.user_id")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setInt(1, blogPost.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                //List userList = new ArrayList();
                List<User> userList = null;
                User user = null;

                do {

                    boolean showAuthor = resultSet.getBoolean("show_author");
                    //logger.debug("showAuthor = "+showAuthor);

                    if(showAuthor) {

                        if(userList == null) userList = new ArrayList<User>(); // Lazy instantiate the userList

                        user = new User();
                        populateUser(user, resultSet);
                        //blogPost.setShowAuthor(resultSet.getBoolean("show_author"));
                        userList.add(user);
                    }

                } while(resultSet.next());

                if(userList != null) logger.debug("userList.size() = "+userList.size());

                //return userList;
                blogPost.setAuthorList(userList);

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //return null;
    }
    */

}
