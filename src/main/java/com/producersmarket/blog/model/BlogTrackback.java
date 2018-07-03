package com.producersmarket.blog.model;

import java.util.Date;

public class BlogTrackback {

    private String title;
    private String excerpt;
    private String url;
    private String blogName;
    private long trackbackDateLong;

    /**
     * Default constructor
     */
    public BlogTrackback() {
    }

    /**
     * Trackback constructor to take a title, excerpt, url, and blog name
     *
     * @param title Title of the trackback
     * @param excerpt Excerpt from the trackback
     * @param url Url for the trackback
     * @param blogName Blog name of the trackback
     */
    public BlogTrackback(String title, String excerpt, String url, String blogName) {
        title = title;
        excerpt = excerpt;
        url = url;
        blogName = blogName;
    }

    /**
     * Get the title of the trackback
     *
     * @return Trackback title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the trackback
     *
     * @param title Trackback title
     */
    public void setTitle(String title) {
        title = title;
    }

    /**
     * Get the excerpt of the trackback
     *
     * @return Trackback excerpt
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * Set the excerpt of the trackback
     *
     * @param excerpt Trackback excerpt
     */
    public void setExcerpt(String excerpt) {
        excerpt = excerpt;
    }

    /**
     * Get the url of the trackback
     *
     * @return Trackback url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url of the trackback
     *
     * @param url Trackback url
     */
    public void setUrl(String url) {
        url = url;
    }

    /**
     * Get the blog name of the trackback
     *
     * @return Trackback blog name
     */
    public String getBlogName() {
        return blogName;
    }

    /**
     * Set the blog name of the trackback
     *
     * @param blogName Trackback blog name
     */
    public void setBlogName(String blogName) {
        blogName = blogName;
    }

    /**
     * Set the date for the trackback
     *
     * @param trackbackDateLong Trackback date as a <code>long</code> value
     */
    public void setTrackbackDateLong(long trackbackDateLong) {
        trackbackDateLong = trackbackDateLong;
    }

    /**
     * Get the date of the trackback
     *
     * @return Date of the trackback as a <code>long</code>
     */
    public long getTrackbackDateLong() {
        return trackbackDateLong;
    }

    /**
     * Get the date of the trackback
     *
     * @return Date of the trackback as a <code>java.util.Date</code>
     */
    public Date getTrackbackDate() {
        return new Date(trackbackDateLong);
    }

}
