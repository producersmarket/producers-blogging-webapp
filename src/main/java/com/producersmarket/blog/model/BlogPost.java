package com.producersmarket.blog.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.text.SimpleDateFormat;

import org.apache.commons.text.StringEscapeUtils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.markdown.LinkNodeRenderer;
import com.producersmarket.blog.markdown.SidebarNodeRenderer;
import com.producersmarket.blog.markdown.TextOnlyNodeRenderer;
import com.producersmarket.blog.markdown.CustomImagesAndLinksRenderer;
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.model.User;

public class BlogPost {

    private static final Logger logger = LogManager.getLogger();
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
    private List<String> imageList;
    private String keywords = null;
    private List<String> keywordList = null;
    private int userId = -1;
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

    public void createHyphenatedName() {

        //StringBuilder stringBuilder = new StringBuilder();

        if(this.title != null) {

            //stringBuilder
            this.setHyphenatedName(replaceStringsInString(this.title, " ", "-"));
            logger.debug("this.getHyphenatedName() = "+this.getHyphenatedName());
        }
    }

    public static String replaceStringsInString(String string, String pattern, String replaceStr) {

        if(string == null || pattern == null || replaceStr == null) return null;
        StringBuilder stringBuilder = new StringBuilder();

        int replaceIndex = -1;
        while((replaceIndex = string.indexOf(pattern)) > -1) {

            stringBuilder.append(string.substring(0, replaceIndex));
            stringBuilder.append(replaceStr);
            string = string.substring(replaceIndex + pattern.length());
        }

        stringBuilder.append(string);

        return stringBuilder.toString();
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

    public String getTitleEscaped() {
        //return org.apache.commons.lang.StringEscapeUtils(title);
        //return org.apache.commons.lang3.StringEscapeUtils(title);
        //return null;

        //logger.debug("StringEscapeUtils.escapeEcmaScript("+title+") = "+StringEscapeUtils.escapeEcmaScript(title));
        //logger.debug("StringEscapeUtils.escapeHtml4("+title+") = "+StringEscapeUtils.escapeHtml4(title));

        //return StringEscapeUtils.escapeEcmaScript(title);
        return StringEscapeUtils.escapeHtml4(title);
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

    public String getBodyAsHtml() {
    
        Parser parser = Parser.builder().build();
        Node document = parser.parse(this.body);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return renderer.render(document);
    }

    public String getBodyText(int length) {
        //logger.debug("getBodyText("+length+")");

        String bodyText = getBodyText();
        //logger.debug(bodyText);

        if(bodyText.length() > length) {

            String bodySubstring = bodyText.substring(0, length);
            //logger.debug(bodySubstring);

            int lastIndexOfDot = bodySubstring.lastIndexOf(".");
            int lastIndexOfExclamation = bodySubstring.lastIndexOf("!");

            //logger.debug("lastIndexOfDot = "+lastIndexOfDot);
            //logger.debug("lastIndexOfExclamation = "+lastIndexOfExclamation);

            int excerptLength = -1;

            if(lastIndexOfDot != -1) excerptLength = lastIndexOfDot;

            if(lastIndexOfExclamation != -1) {
                if(lastIndexOfExclamation > lastIndexOfDot) excerptLength = lastIndexOfExclamation;
            }

            //logger.debug("excerptLength = "+excerptLength);

            if(excerptLength != -1) {

                return bodyText.substring(0, excerptLength);

            } else {

                int indexOfSpace = bodyText.indexOf(" ", length);
                //int indexOfSpace = bodyText.indexOf(' ', length);
                //logger.debug("indexOfSpace = "+indexOfSpace);

                if(indexOfSpace != -1) {
                    //logger.debug("bodyText.substring(0, indexOfSpace) = "+bodyText.substring(0, indexOfSpace));
                    //logger.debug("return "+bodyText.substring(0, indexOfSpace));
                    return bodyText.substring(0, indexOfSpace);
                }
            }

            //logger.debug("return "+bodySubstring);
            return bodySubstring;
        }

        return bodyText;
    }

    public String getBodyText() {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(this.body);
        HtmlRenderer renderer = HtmlRenderer
            .builder()
            .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                    return new TextOnlyNodeRenderer(htmlNodeRendererContext);
                }
            })
            .build();

        return renderer.render(document);
    }

    public String getBodyAsHtmlWithImages() {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(this.body);
        HtmlRenderer renderer = HtmlRenderer
            .builder()
            .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                    return new BlogImageNodeRenderer(htmlNodeRendererContext);
                }
            })
            .build();

        return renderer.render(document);
    }

    public String getBodyAsHtmlWithCustomImagesAndLinks() {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(this.body);
        
        HtmlRenderer renderer = HtmlRenderer
            .builder()
            .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                    return new CustomImagesAndLinksRenderer(htmlNodeRendererContext);
                }
            })
            .build();

        return renderer.render(document);
    }
    public String getBodyAsHtmlForSidebar() {
    
        Parser parser = Parser.builder().build();
        Node document = parser.parse(body);
        //HtmlRenderer renderer = HtmlRenderer.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder()

            /*
            .nodeRendererFactory(
                new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        return new BlogImageNodeRenderer(htmlNodeRendererContext);
                    }
                }
            )
            */

            .nodeRendererFactory(
                new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        return new SidebarNodeRenderer(htmlNodeRendererContext);
                    }
                }
            )

        .build();

        return renderer.render(document);
    }

    public void setBody(String body){
        this.body = body;
    }

    public String getExcerpt() {
        return getExcerpt(EXCERPT_LENGTH);
    }

    public String getExcerpt(int length) {

        return getBodyText(length);
        /*
        //String excerpt = getBodyText();
        String excerpt = getBodyText(length);

        if(excerpt.length() > length) {
            return excerpt.substring(0, length);
        } else {
            return excerpt;
        }
        */
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    public String getMetaDescription() {
        return metaDescription;
    }

    public String getImagePath() {
        if(this.imagePath != null) return imagePath;
        if(this.imageList != null) return this.imageList.get(0);
        return null;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
    public List<String> getImageList() {
        return this.imageList;
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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserIdString() {
        return String.valueOf(userId);
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

        if(this.datePublished != null) {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(this.datePublishedPattern, java.util.Locale.ENGLISH);
            return formatter.format(datePublished);
        }

        return null;
    }

    public String getDatePublishedFormatted(String pattern) {
        if(this.datePublished != null) {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern, java.util.Locale.ENGLISH);
            return formatter.format(datePublished);
        }

        return null;
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
