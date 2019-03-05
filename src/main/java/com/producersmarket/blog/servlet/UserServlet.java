package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.producersmarket.blog.database.BlogPostDatabaseManager;
import com.producersmarket.blog.database.UserDatabaseManager;
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.blog.model.User;
//import com.producersmarket.model.User;

public class UserServlet extends ParentServlet {

    private static final Logger logger = LogManager.getLogger();

    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String LEFT_SQUARE_BRACKET = "[";
    private static final String RIGHT_SQUARE_BRACKET = "]";

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            HttpSession httpSession = request.getSession(false);
            logger.debug("httpSession = "+httpSession);

            Object userIdObject = null;

            if(
              httpSession != null
              && (userIdObject = httpSession.getAttribute("userId")) != null
            ) {

                logger.debug("httpSession.getId() = "+httpSession.getId());
                logger.debug("userIdObject = "+userIdObject);

                //int userId = Integer.valueOf((String) userIdObject);
                int userId = ((Integer)userIdObject).intValue();
                logger.debug("userId = "+userId);

                User user = UserDatabaseManager.selectUserById(userId, getConnectionManager());
                logger.debug("user = "+user);

                if(user != null) {

                    logger.debug("user.getId() = "+user.getId());

                    List<BlogPost> blogPostList = BlogPostDatabaseManager.selectBlogPostsByUserId(user.getId(), getConnectionManager());
                    
                    if(blogPostList != null) {
                        logger.debug("blogPostList.size() = "+blogPostList.size());
                        request.setAttribute("blogPostList", blogPostList);
                        user.setBlogPostList(blogPostList);
                        request.setAttribute("user", user);
                    }

                    includeUtf8(request, response, "/view/user.jsp");

                    return;
                    
                } // if(user != null) {

                include(request, response, "/view/blog-list.jsp");
                return;

            } else { // if(passwordHash != null

                request.setAttribute("errorMessage", "Incorrect password");
                includeUtf8(request, response, "/view/login.jsp");
                return;

            } // if(passwordHash != null

        } catch(java.sql.SQLException sqlException) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            sqlException.printStackTrace(printWriter);
            logger.error(stringWriter.toString());

            logException(sqlException);

        } catch(Exception exception) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //writeOut(response, "-1");
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /*
        public String doubleQuotes(String string) {
        return new StringBuilder().append("\"").append(string).append("\"").toString();
    }
    */

}
