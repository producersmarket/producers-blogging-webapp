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
import com.ispaces.database.connection.ConnectionManager;
//import com.producersmarket.model.BlogPost;
import com.producersmarket.model.User;

public class UserDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = UserDatabaseManager.class.getSimpleName();

    private static final String selectUserByPasswordResetCode = "selectUserByPasswordResetCode";
    private static final String UPDATE_PASSWORD_HASH = "updatePasswordHash";

    private static String sqlSelectUserFields = new StringBuilder()
        .append("u.id, u.name, u.business_name, u.hyphenated_name, u.location, u.theme_color")
        .append(", uhi.background_image, uhi.logo_image, uhi.promo_video")
        .toString();

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

    public static User selectUserByEmail(String email) throws SQLException, Exception {
        logger.debug("selectUserByEmail("+email+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            String sql = new StringBuilder()
                .append("SELECT id ")
                .append(" FROM user")
                .append(" WHERE email = ?")
                .toString();
                
            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                User user = new User();
                user.setId(resultSet.getInt(1));

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

    /*
    public static User selectUserById(int userId) throws SQLException, Exception {
        logger.debug("selectUserByHyphenatedName('"+userId+"')");

        //ConnectionManager connectionManager = new ConnectionManager(className, com.producersmarket.servlet.InitServlet.connectionPool);
        ConnectionManager connectionManager = new ConnectionManager();

        try {

            String sql = new StringBuilder()
                .append("SELECT ")
                //.append("u.id, u.name, u.business_name, u.hyphenated_name, u.location, u.theme_color, uhi.background_image, uhi.logo_image, uhi.promo_video")
                .append("u.id, u.name, u.business_name, u.hyphenated_name, u.ig_username, u.location, u.theme_color")
                .append(" FROM user u")
                //.append(" LEFT OUTER JOIN user_has_image uhi ON uhi.user_id = uis.user_id")
                .append(" WHERE u.id = ?")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                User user = new User();
                populateUser(user, resultSet);

                return user;
            }

        } finally {
            connectionManager.commit();
        }

        return null;
    }
    */

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
