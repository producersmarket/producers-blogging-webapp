package com.producersmarket.blog.model;

import java.util.Date;

public class BlogTrackback {

    private String title;
    private String excerpt;
    private String url;
    private String blogName;
    private long trackbackDateLong;

    /**
     * Default constructor, since we added another
     */
    public BlogTrackback() {
    }

    /**
     * Trackback constructor to take a title, excerpt, url, and blog name
     *
     * @param title
     * @param excerpt
     * @param url
     * @param blogName
     */
    public BlogTrackback(String title, String excerpt, String url, String blogName) {
        this.title = title;
        this.excerpt = excerpt;
        this.url = url;
        this.blogName = blogName;
    }

    /**
     * Get the title of the trackback
     *
     * @return Trackback title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the title of the trackback
     *
     * @param title Trackback title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the excerpt of the trackback
     *
     * @return Trackback excerpt
     */
    public String getExcerpt() {
        return this.excerpt;
    }

    /**
     * Set the excerpt of the trackback
     *
     * @param excerpt Trackback excerpt
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    /**
     * Get the url of the trackback
     *
     * @return Trackback url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Set the url of the trackback
     *
     * @param url Trackback url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the blog name of the trackback
     *
     * @return Trackback blog name
     */
    public String getBlogName() {
        return this.blogName;
    }

    /**
     * Set the blog name of the trackback
     *
     * @param blogName Trackback blog name
     */
    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    /**
     * Set the date for the trackback
     *
     * @param trackbackDateLong Trackback date as a <code>long</code> value
     */
    public void setTrackbackDateLong(long trackbackDateLong) {
        this.trackbackDateLong = trackbackDateLong;
    }

    /**
     * Get the date of the trackback
     *
     * @return Date of the trackback as a <code>long</code>
     */
    public long getTrackbackDateLong() {
        return this.trackbackDateLong;
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
