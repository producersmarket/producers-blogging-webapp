package com.producersmarket.blog.model;

import java.util.regex.Pattern;

/*
 * Project constants. This could be implmented as an Enum.
 */
public final class Constants {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String LEFT_SQUARE_BRACKET = "[";
    public static final String RIGHT_SQUARE_BRACKET = "]";
    public static final String SINGLE_QUOTE = "'";
    public static final String PERIOD = ".";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String FORWARD_SLASH = "/";

    public static final String HTTP_GET = "GET";

    public static final String CSRF_COOKIE_NAME = "__Secure-CSRF-Token";
    public static final String REMEMBER_ME_COOKIE_NAME = "__Secure-Remember-Me";
    public static final String SESSION_ID_COOKIE_NAME = "__Secure-Session-Id";
    public static final String JSESSIONID = "JSESSIONID";

    public static final int ONE_HOURS_IN_MILLISECONDS = 60 * 60 * 1000;
    public static final int THREE_HOURS_IN_SECONDS = 60 * 60 * 3; // (10800)
    public static final int SIXTY_DAYS_IN_SECONDS = 60 * 60 * 24 * 60; // 60 days ( ~2 months ) (5184000)

	private Constants() {} // private accessor to prevent this class from being constructed

}
