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

public class User extends com.producersmarket.model.User {

    private List<BlogPost> blogPostList = null;

    /*
    public User() {}

    public User(String name) {
        this.name = name;
    }
    */

    public List<BlogPost> getBlogPostList() {
        return blogPostList;
    }

    public void setBlogPostList(List<BlogPost> blogPostList) {
        this.blogPostList = blogPostList;
    }

}
