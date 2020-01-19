package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.producersmarket.blog.database.LoginDatabaseManager;
import com.producersmarket.blog.model.User;

public class ExtendedLoginServlet extends com.producersmarket.blog.servlet.LoginServlet {

    private static final Logger logger = LogManager.getLogger();

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");
        super.setLoginPage(config.getInitParameter("loginPage"));
        super.setLoggedInPage(config.getInitParameter("loggedInPage"));
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");
        super.doGet(request, response);
    }

    protected User loginUser(String email, String password) {
        logger.debug("loginUser("+email+", password)");

        int userId = authenticateLogin(email, password);
        if(userId > 0) return populateUser(userId);
        return null;
    }

}