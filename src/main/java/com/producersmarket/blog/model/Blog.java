package com.producersmarket.blog.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Getters and Setters for a blog
 * 
 * @author dermot
 * @version 0.1 2017/09/04
 */

public class Blog {

    private String name = null;
    private String tagLine = null;
    private String description = null;
    private boolean display = false;
    private int priority = -1;
    private List<BlogPost> blogPostList = null;

    public Blog() {}

    public Blog(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTagLine() {
        return tagLine;
    }
    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
    public void setDisplay(int display) {
        if(display == 1)
            this.display = true;
        else
            this.display = false;
    }
    public boolean getDisplay() {
        return display;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
    public String getPriorityString() {
        return String.valueOf(getPriority());
    }

    public List<BlogPost> getBlogPostList() {
        return blogPostList;
    }
    public void setBlogPostList(List<BlogPost> blogPostList) {
        this.blogPostList = blogPostList;
    }

}
