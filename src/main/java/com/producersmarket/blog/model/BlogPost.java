package com.producersmarket.blog.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.text.SimpleDateFormat;
import com.producersmarket.model.User;

/**
 * A servlet to handle blog post requests.
 * @author dermot
 * @version 0.1 2017/09/04
 */

public class BlogPost {

    private static final int EXCERPT_LENGTH = 333;

    private int id = -1;
    private String hyphenatedName = null;
    private Blog blog = null;
    private String title = null;
    private String subtitle = null;
    private String alternativeTitle = null;
    private String permalink = null;
    private String body = null;
    private String metaDescription = null;
    private String imagePath = null;
    private String keywords = null;
    private List<String> keywordList = null;
    private String userId = null;
    private User author;
    private List<User> authorList;
    private String postedBy = null;
    private Date datePublished = null;
    private Date datetimePublished = null;
    private boolean showAuthor = false;
    private boolean display = false;
    private boolean isDisabled = false;
    private boolean isDeleted = false;
    private int priority = -1;
    public int viewCount = 0;
    private List<BlogComment> commentList = null;
    private List<BlogPost> relatedBlogPostList = null;
    private List<Integer> blogCategoryIdList = null;
    private List<String> blogCategoryList = null;
    private Date dateCreated = null;
    private Date dateDisabled = null;
    private Date dateDeleted = null;
    private String datePublishedPattern = "EEE, d MMM yyyy";

    public BlogPost() {}

    public BlogPost(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getHyphenatedName() {
        return hyphenatedName;
    }
    public void setHyphenatedName(String hyphenatedName) {
        this.hyphenatedName = hyphenatedName;
    }

    public Blog getBlog() {
        return blog;
    }
    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }
    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getPermalink() {
        return permalink;
    }
    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body){
        this.body = body;
    }

    public String getExcerpt() {
        return getExcerpt(EXCERPT_LENGTH);
    }

    public String getExcerpt(int length) {

        if(body.length() > length) {
            return this.body.substring(0, length);
        } else {
            return body;
        }
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    public String getMetaDescription() {
        return metaDescription;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    public String getKeywords() {

        //return keywords;

        if(this.keywordList != null) {
            StringBuilder sb = new StringBuilder();
            for(String keyword: this.keywordList) {
                sb.append(keyword).append(",");
            }    
            return sb.toString();
        }
        
        return null;
    }

    public void setKeywordList(List<String> keywordList) {
        this.keywordList = keywordList;
    }
    public List<String> getKeywordList() {
        return this.keywordList;
    }

    public void setAuthorList(List<User> authorList) {
        this.authorList = authorList;
    }
    public List<User> getAuthorList() {
        return this.authorList;
    }

    public void setBlogCategoryIdList(List<Integer> blogCategoryIdList) {
        this.blogCategoryIdList = blogCategoryIdList;
    }
    public List<Integer> getBlogCategoryIdList() {
        return this.blogCategoryIdList;
    }

    public void setBlogCategoryList(List<String> blogCategoryList) {
        this.blogCategoryList = blogCategoryList;
    }
    public List<String> getBlogCategoryList() {
        return this.blogCategoryList;
    }

    // String userId
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId(){
        return userId;
    }
    public String getUserIdString(){
        return userId;
    }

    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    public String getPostedBy() {
        return postedBy;
    }
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }
    public Date getDatePublished() {
        return datePublished;
    }
    public String getDatePublishedFormatted() {
        //return SimpleDateFormat(datePublishedPattern).format(datePublished);
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(this.datePublishedPattern, java.util.Locale.ENGLISH);
        return formatter.format(datePublished);
    }
    public String getDatePublishedFormatted(String pattern) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern, java.util.Locale.ENGLISH);
        return formatter.format(datePublished);
    }

    public Date getDatetimePublished() {
        return datetimePublished;
    }
    public void setDatetimePublished(Date datetimePublished) {
        this.datetimePublished = datetimePublished;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    public int getViewCount(){
        return viewCount;
    }

    public void setShowAuthor(boolean showAuthor) {
        this.showAuthor = showAuthor;
    }
    public boolean getShowAuthor() {
        return this.showAuthor;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
    public boolean getDisplay() {
        return this.display;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    public int getPriority() {
        return this.priority;
    }
    public String getPriorityString() {
        return String.valueOf(this.getPriority());
    }

    public List<BlogComment> getCommentList() {
        return this.commentList;
    }
    public void setCommentList(List<BlogComment> commentList) {
        this.commentList = commentList;
    }

    public List<BlogPost> getRelatedBlogPostList() {
        return this.relatedBlogPostList;
    }
    public void setRelatedBlogPostList(List<BlogPost> relatedBlogPostList) {
        this.relatedBlogPostList = relatedBlogPostList;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
    public boolean getIsDisabled() {
        return this.isDisabled;
    }

    /*
    public void setDateDisabled(Date dateDisabled) {
        this.dateDisabled = dateDisabled;
    }
    public Date getDateDisabled() {
        return dateDisabled;
    }
    public String getDisabledYesNo() {
        if(disabled) {
            return "Yes";
        } else {
            return "No";
        }
    }
    */

    // boolean deleted
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    public boolean getIsDeleted(){
        return this.isDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }
    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public Date getDateCreated() {
        return dateCreated;
    }

}
