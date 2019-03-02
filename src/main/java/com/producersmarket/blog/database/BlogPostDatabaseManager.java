package com.producersmarket.blog.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ispaces.dbcp.ConnectionManager;

/*
import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.markdown.LinkNodeRenderer;
import com.producersmarket.blog.markdown.SidebarNodeRenderer;
//import com.producersmarket.database.UserDatabaseManager;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
*/
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.model.User;

public class BlogPostDatabaseManager {
//public class BlogPostDatabaseManager extends UserDatabaseManager {

    private static final Logger logger = LogManager.getLogger();
    private static final String className = BlogPostDatabaseManager.class.getSimpleName();

    public static void updateBlogPost(BlogPost blogPost) throws SQLException, Exception {

        String sqlName = "updateBlogPost";
        ConnectionManager connectionManager = new ConnectionManager(className, sqlName);

        try {

            //PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName);

            //String sql = "UPDATE blog_post SET title = ?, subtitle = ?, body = ?, hyphenated_name = ? WHERE id = ?";
            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName);

            preparedStatement.setString(1, blogPost.getTitle());
            preparedStatement.setString(2, blogPost.getSubtitle());
            preparedStatement.setString(3, blogPost.getBody());
            preparedStatement.setString(4, blogPost.getHyphenatedName());
            //preparedStatement.setString(2, blogPost.getSubtitle());
            //preparedStatement.setString(3, blogPost.getMetaDescription());
            preparedStatement.setInt(5, blogPost.getId());

            preparedStatement.executeUpdate();

        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPost(BlogPost blogPost) throws SQLException, Exception {

        String sqlName = "insertBlogPost";
        ConnectionManager connectionManager = new ConnectionManager(className, sqlName);

        try {

            //String sql = "INSERT INTO blog_post (hyphenated_name, title, subtitle, body, updated_by, created_by, date_created) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, blogPost.getHyphenatedName());
            preparedStatement.setString(2, blogPost.getTitle());
            preparedStatement.setString(3, blogPost.getSubtitle());
            preparedStatement.setString(4, blogPost.getBody());
            preparedStatement.setInt(5, blogPost.getUserId());
            preparedStatement.setInt(6, blogPost.getUserId());
            //preparedStatement.setString(2, blogPost.getSubtitle());
            //preparedStatement.setString(3, blogPost.getMetaDescription());
            //preparedStatement.setString(5, blogPost.getHyphenatedName());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {

                blogPost.setId(resultSet.getInt(1));

                insertBlogPostHasAuthor(blogPost.getId(), blogPost.getUserId());
            }

        } finally {
            connectionManager.commit();
        }
    }

    public static void insertBlogPostHasAuthor(int blogPostId, int userId) throws SQLException, Exception {
        logger.debug("insertBlogPostHasAuthor("+blogPostId+", "+userId+")");

        String sqlName = "insertBlogPostHasAuthor";
        ConnectionManager connectionManager = new ConnectionManager(className, sqlName);

        try {

            //PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName);

            //String sql = "INSERT INTO blog_post_has_author (blog_post_id, user_id) VALUES (?, ?)";
            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            PreparedStatement preparedStatement = connectionManager.loadStatement(sqlName);

            preparedStatement.setInt(1, blogPostId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } finally {
            connectionManager.commit();
        }
    }


    public static void populateBlogPost(
        BlogPost blogPost
        , ResultSet resultSet
        //, ConnectionManager connectionManager
    ) throws SQLException, Exception {

        // 'id', 'hyphenated_name', 'title', 'subtitle', 'body', 'date_published', 'datetime_published', 'enabled', 'priority', 'updated_by', 'created_by', 'date_updated', 'date_created'
        //SELECT id, hyphenated_name, title, subtitle, LENGTH(body) FROM blog_post;

        blogPost.setId               (resultSet.getInt(1));
        blogPost.setHyphenatedName   (resultSet.getString(2));
        blogPost.setTitle            (resultSet.getString(3));
        blogPost.setSubtitle         (resultSet.getString(4));
        blogPost.setMetaDescription  (resultSet.getString(5));
        blogPost.setBody             (resultSet.getString(6));
        blogPost.setDatePublished    (resultSet.getDate(7));
        blogPost.setDatetimePublished(resultSet.getDate(8));
        blogPost.setIsDisabled       (!resultSet.getBoolean(9));
        blogPost.setPriority         (resultSet.getInt(10));
        //blogPost.setUpdatedBy        (resultSet.getInt(10));
        //blogPost.setCreatedBy        (resultSet.getInt(11));
        //blogPost.setDateUpdated      (resultSet.getTimestamp(12));
        //blogPost.setDateCreated      (resultSet.getTimestamp(13));

        //Date date = resultSet.getDate(8);
        //long millis = date.getTime();
        //history.setMillis(millis);

        //List<BlogPost> subBlogPostList = BlogPostsManager.selectBlogPostsByBlogPostId(blogPost.getId());
        //List<BlogPost> subBlogPostList = BlogPostsManager.selectBlogPostsByBlogPostId(blogPost.getId(), connectionManager);
        //blogPost.setBlogPostList(subBlogPostList);
    }

    public static BlogPost selectBlogPostByHyphenatedName(String blogPostName) throws SQLException, Exception {
        logger.debug("selectBlogPostByHyphenatedName("+blogPostName+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "id, hyphenated_name, title, subtitle, meta_description, body, date_published, datetime_published, enabled, priority";
            String sql = new StringBuilder()
                .append("SELECT ").append(sqlFields)  
                .append(" FROM blog_post WHERE hyphenated_name = ?")
                .toString();

            //PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostByHyphenatedName");
            preparedStatement.setString(1, blogPostName);

            ResultSet resultSet = preparedStatement.executeQuery();
            BlogPost blogPost = null;

            if(resultSet.next()) {

                blogPost = new BlogPost();

                populateBlogPost(blogPost, resultSet);
                //populateBlogPost(blogPost, resultSet, connectionManager);

                selectBlogPostAuthors(blogPost, connectionManager);

                selectRelatedBlogPosts(blogPost, connectionManager);

                selectKeywords(blogPost, connectionManager);

                return blogPost;
            }

            /*
            int userId = blogPost.getId();
            logger.debug("userId = "+userId);

            if(userId > 0) {
                User author = UserDatabaseManager.selectUserById(userId);
                blogPost.setAuthor(author);
            }
            */

            //return blogPost;

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static void selectKeywords(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectKeywords(blogPost, connectionManager)");

        try {

            /*
            String sql = new StringBuilder()
                .append("SELECT keyword")
                .append(" FROM blog_post_has_keyword")
                .append(" WHERE blog_post_id = ?")
                .toString();
            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectKeywords");
            preparedStatement.setInt(1, blogPost.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<String> keywordList = new ArrayList<String>();

                do {

                    keywordList.add(resultSet.getString(1));

                } while(resultSet.next());

                blogPost.setKeywordList(keywordList);
                //return keywordList;
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        //return null;
    }

    /*
     * https://github.com/commonmark/commonmark.js
     */
    public static void selectRelatedBlogPosts(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectRelatedBlogPosts(blogPost, connectionManager)");

        try {

            /*
            String sqlFields = "bp.id, bp.hyphenated_name, bp.title, bp.subtitle, bp.meta_description, bp.body, bp.date_published, bp.datetime_published, bp.enabled, bp.priority, bphrbp.title, bphrbp.image_path";

            String sql = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                .append(" FROM blog_post bp, blog_post_has_related_blog_posts bphrbp")
                .append(" WHERE bphrbp.blog_post_id = ?")
                .append(" AND bphrbp.related_id = bp.id")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectRelatedBlogPosts");
            preparedStatement.setInt(1, blogPost.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost relatedBlogPost = null;

                do {

                    relatedBlogPost = new BlogPost();

                    populateBlogPost(relatedBlogPost, resultSet);

                    selectBlogPostAuthors(relatedBlogPost, connectionManager);

                    //selectRelatedBlogPosts(relatedBlogPost, connectionManager);

                    /*
                     * Select the custom sidebar title from the blog_post_has_related_blog_posts table,
                     */
                    //String relatedBlogPostTitle = resultSet.getString(10);
                    String relatedBlogPostTitle = resultSet.getString("rp.title");
                    relatedBlogPost.setAlternativeTitle(relatedBlogPostTitle);
                    //if(relatedBlogPostTitle != null) relatedBlogPost.setTitle(relatedBlogPostTitle); // overwrite the blog post title with the title from blog_post_has_related_blog_posts

                    String relatedBlogPostImagePath = resultSet.getString("rp.imagepath");
                    if(relatedBlogPostImagePath != null) relatedBlogPost.setImagePath(relatedBlogPostImagePath);

                    /*
                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(relatedBlogPost.getBody());
                    //HtmlRenderer renderer = HtmlRenderer.builder().build();
                    HtmlRenderer renderer = HtmlRenderer.builder()

                        .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                            public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                                return new SidebarNodeRenderer(htmlNodeRendererContext);
                            }
                        }

                        //.nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        //    // Skip over images in the sidebar blog post HTML.
                        //    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext);
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext) {
                        //        return new org.commonmark.renderer.NodeRenderer() {
                        //            //public void run() { doStuffWith(someData); }
                        //            @Override public void render(Node node) {
                        //                //logger.debug("render("+node+")");
                        //            }
                        //            @Override public Set<Class<? extends Node>> getNodeTypes() {
                        //                //logger.debug("getNodeTypes()");
                        //                return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Image.class); // Return the node types we want to use this renderer for.
                        //            }
                        //        };
                        //    }

                        //}).nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        //    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        //        return new LinkNodeRenderer(htmlNodeRendererContext);
                        //    }
                        //}

                        //}).nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        //    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext);
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext) {
                        //        return new org.commonmark.renderer.NodeRenderer() {
                        //            //public void run() { doStuffWith(someData); }
                        //            @Override public void render(Node node) {
                        //                //logger.debug("render("+node+")");
                        //                this.html.text(node.toString());
                        //            }
                        //            @Override public Set<Class<? extends Node>> getNodeTypes() {
                        //                //logger.debug("getNodeTypes()");
                        //                return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Link.class); // Return the node types we want to use this renderer for.
                        //            }
                        //        };
                        //    }
                        //}

                        ).build();

                    String bodyHtml = renderer.render(document);

                    logger.debug("relatedBlogPost.getBody().length() = "+relatedBlogPost.getBody().length());
                    logger.debug("bodyHtml.length()                  = "+bodyHtml.length());

                    relatedBlogPost.setBody(bodyHtml);
                    */

                    blogPostList.add(relatedBlogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                blogPost.setRelatedBlogPostList(blogPostList);

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

    public static List<BlogPost> selectBlogPostsByProducerId(int producerId) throws SQLException, Exception {
        logger.debug("selectBlogPostsByProducerId("+producerId+", connectionManager)");

        //ConnectionManager connectionManager = new ConnectionManager(className, com.producersmarket.servlet.InitServlet.connectionPool);
        //ConnectionManager connectionManager = new ConnectionManager();
        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "bp.id, bp.hyphenated_name, bp.title, bp.subtitle, bp.meta_description, bp.body, bp.date_published, bp.datetime_published, bp.enabled, bp.priority, phbp.title, phbp.image_path";

            String sql = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                //.append(" FROM blog_post bp, blog_post_has_related_blog_posts bphrbp")
                .append(" FROM blog_post bp, producer_has_blog_post phbp")
                .append(" WHERE phbp.user_id = ?")
                .append(" AND phbp.blog_post_id = bp.id")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByProducerId");

            preparedStatement.setInt(1, producerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    //populateBlogPost(relatedBlogPost, resultSet);
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    logger.debug(blogPost.getId() +" "+blogPost.getHyphenatedName());

                    //selectRelatedBlogPosts(relatedBlogPost, connectionManager);

                    // Select the custom sidebar title from the blog_post_has_related_blog_posts table,
                    String blogPostTitle = resultSet.getString("pp.title");
                    if(blogPostTitle != null) blogPost.setTitle(blogPostTitle); // overwrite the blog post title with the title from blog_post_has_related_blog_posts

                    String blogPostImagePath = resultSet.getString("pp.imagepath");
                    if(blogPostImagePath != null) blogPost.setImagePath(blogPostImagePath);

                    /*
                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(blogPost.getBody());
                    //HtmlRenderer renderer = HtmlRenderer.builder().build();
                    HtmlRenderer renderer = HtmlRenderer.builder()

                        .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                            public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                                return new SidebarNodeRenderer(htmlNodeRendererContext);
                            }
                        }

                        //.nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        //    // Skip over images in the sidebar.
                        //    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext);
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext) {
                        //        return new org.commonmark.renderer.NodeRenderer() {
                        //            //public void run() { doStuffWith(someData); }
                        //            @Override public void render(Node node) {
                        //                //logger.debug("render("+node+")");
                        //            }
                        //            @Override public Set<Class<? extends Node>> getNodeTypes() {
                        //                //logger.debug("getNodeTypes()");
                        //                return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Image.class); // Return the node types we want to use this renderer for.
                        //            }
                        //        };
                        //    }
                        //}).nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        //
                        //    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        //        return new LinkNodeRenderer(htmlNodeRendererContext);
                        //    }
                        //}

                        //}).nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        //    public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext);
                        //        //return new ImageNodeRenderer(htmlNodeRendererContext) {
                        //        return new org.commonmark.renderer.NodeRenderer() {
                        //            //public void run() { doStuffWith(someData); }
                        //            @Override public void render(Node node) {
                        //                //logger.debug("render("+node+")");
                        //                this.html.text(node.toString());
                        //            }
                        //            @Override public Set<Class<? extends Node>> getNodeTypes() {
                        //                //logger.debug("getNodeTypes()");
                        //                return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Link.class); // Return the node types we want to use this renderer for.
                        //            }
                        //        };
                        //    }
                        //}

                        ).build();

                    String bodyHtml = renderer.render(document);

                    logger.debug("blogPost.getBody().length() = "+blogPost.getBody().length());
                    logger.debug("bodyHtml.length()                  = "+bodyHtml.length());

                    blogPost.setBody(bodyHtml);
                    */

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    /*
    static class LinkNodeRenderer implements org.commonmark.renderer.NodeRenderer {

        private final org.commonmark.renderer.html.HtmlWriter htmlWriter;

        LinkNodeRenderer(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
            //logger.debug("("+htmlNodeRendererContext+")");

            this.htmlWriter = htmlNodeRendererContext.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            //logger.debug("getNodeTypes()");

            // Return the node types we want to use this renderer for.
            return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Link.class);
        }

        @Override
        public void render(Node node) {
            //logger.debug("render("+node+")");

            // We only handle one type as per getNodeTypes, so we can just cast it here.
            org.commonmark.node.Link linkNode = (org.commonmark.node.Link)node;

            //logger.debug("linkNode.Destination() = "+linkNode.getDestination());
            //logger.debug("linkNode.getTitle() = "+linkNode.getTitle());
            //logger.debug("linkNode.getFirstChild() = "+linkNode.getFirstChild());

            //org.commonmark.node.Node textNode = node.getFirstChild();
            //org.commonmark.node.Text textNode = (org.commonmark.node.Text)node.getFirstChild(); // May be Image or Text
            org.commonmark.node.Node firstChildNode = node.getFirstChild(); // May be Image or Text

            if(firstChildNode instanceof org.commonmark.node.Image) {

            } else if(firstChildNode instanceof org.commonmark.node.Text) {

                String literal = ((org.commonmark.node.Text)firstChildNode).getLiteral();
                //logger.debug("literal = "+literal);

                //this.html.tag("figure");
                if(literal != null) this.htmlWriter.text(literal);
        
            }

        }

    }
    */

    public static List<BlogPost> selectBlogPostsByUserId(int userId) throws SQLException, Exception {
        logger.debug("selectBlogPostsByUserId("+userId+")");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "bp.id, hyphenated_name, title, subtitle, meta_description, body, date_published, datetime_published, enabled, priority";

            String sql = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                .append(" FROM blog_post bp, blog_post_has_author bpha")
                .append(" WHERE user_id = ?")
                .append(" AND bp.id = bpha.blog_post_id")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByUserId");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    //populateBlogPost(relatedBlogPost, resultSet);
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    //logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    //blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPosts() throws SQLException, Exception {
        logger.debug("selectBlogPosts()");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "bp.id, hyphenated_name, title, subtitle, meta_description, body, date_published, datetime_published, enabled, priority, bphi.image_path";

            String sql = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                .append(" FROM blog_post bp")
                .append(" LEFT JOIN blog_post_has_image bphi ON bp.id = bphi.blog_post_id")
                .append(" WHERE enabled = 1")
                .append(" ORDER BY bp.priority")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPosts");

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    //populateBlogPost(relatedBlogPost, resultSet);
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    logger.debug("resultSet.getString(11) = "+resultSet.getString(11));
                    blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPostsByCategoryName(String categoryName) throws SQLException, Exception {
        logger.debug("selectBlogPostsByCategoryName('"+categoryName+"')");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "bp.id, hyphenated_name, title, subtitle, meta_description, body, date_published, datetime_published, enabled, priority, bphi.image_path";
            String selectBlogCategoryId = "SELECT blog_category.id FROM blog_category WHERE category = ?";

            String sql = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                //.append(" FROM blog_post")
                //.append(" FROM blog_post bp, blog_post_has_image bphi")
                .append(" FROM blog_post_has_category bphc, blog_post bp")
                //.append(" FROM blog_post bp, blog_post_has_category bphc, blog_post_has_image bphi")
                .append(" LEFT JOIN blog_post_has_image bphi ON bp.id = bphi.blog_post_id")
                .append(" WHERE enabled = 1")
                .append(" AND bp.id = bphc.blog_post_id")
                .append(" AND bphc.blog_category_id = (").append(selectBlogCategoryId).append(")")
                //.append(" ORDER BY bp.id DESC")
                .append(" ORDER BY bp.date_created DESC")
                .toString();

            //logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostsByCategoryName");

            preparedStatement.setString(1, categoryName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    //populateBlogPost(relatedBlogPost, resultSet);
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));
                    blogPost.setImagePath        (resultSet.getString(11));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    BlogCategoryDatabaseManager.selectBlogPostCategories(blogPost, connectionManager);

                    //selectRelatedBlogPosts(blogPost, connectionManager);

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static List<BlogPost> selectBlogPosts(int[] blogPostIds) throws SQLException, Exception {
        logger.debug("selectBlogPosts("+blogPostIds+")");

        logger.debug("blogPostIds = "+blogPostIds);
        logger.debug("java.util.Arrays.toString(blogPostIds) = "+java.util.Arrays.toString(blogPostIds));
        
        //ConnectionManager connectionManager = new ConnectionManager(className, com.producersmarket.servlet.InitServlet.connectionPool);
        //ConnectionManager connectionManager = new ConnectionManager();
        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "bp.id, bp.hyphenated_name, bp.title, bp.subtitle, bp.meta_description, bp.body, bp.date_published, bp.datetime_published, bp.enabled, bp.priority";

            StringBuilder sqlBuilder = new StringBuilder()
                .append("SELECT ").append(sqlFields)
                .append(" FROM post p")
                .append(" WHERE bp.id IN (");
                for(int i = 0; i < blogPostIds.length; i++) {
                    if(i > 0) sqlBuilder.append(",");
                    sqlBuilder.append(blogPostIds[i]);
                }
                sqlBuilder.append(")");

            String sql = sqlBuilder.toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */

            String sqlFields = connectionManager.getSqlString("blog-fields");

            StringBuilder sqlBuilder = new StringBuilder().append(sqlFields);
            sqlBuilder.append("(");

            for(int i = 0; i < blogPostIds.length; i++) {
                if(i > 0) sqlBuilder.append(",");
                sqlBuilder.append(blogPostIds[i]);
            }

            sqlBuilder.append(")");

            String sql = sqlBuilder.toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                List<BlogPost> blogPostList = new ArrayList<BlogPost>();
                BlogPost blogPost = null;

                do {

                    blogPost = new BlogPost();

                    //populateBlogPost(relatedBlogPost, resultSet);
                    blogPost.setId               (resultSet.getInt(1));
                    blogPost.setHyphenatedName   (resultSet.getString(2));
                    blogPost.setTitle            (resultSet.getString(3));
                    blogPost.setSubtitle         (resultSet.getString(4));
                    blogPost.setMetaDescription  (resultSet.getString(5));
                    blogPost.setBody             (resultSet.getString(6));
                    blogPost.setDatePublished    (resultSet.getDate(7));
                    blogPost.setDatetimePublished(resultSet.getDate(8));
                    blogPost.setIsDisabled       (!resultSet.getBoolean(9));
                    blogPost.setPriority         (resultSet.getInt(10));

                    selectBlogPostAuthors(blogPost, connectionManager);

                    selectRelatedBlogPosts(blogPost, connectionManager);

                    /*
                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(blogPost.getBody());
                    //HtmlRenderer renderer = HtmlRenderer.builder().build();
                    HtmlRenderer renderer = HtmlRenderer.builder().nodeRendererFactory(
                        new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                            public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                                return new BlogImageNodeRenderer(htmlNodeRendererContext);
                            }
                        }
                    ).build();

                    String bodyHtml = renderer.render(document);

                    //logger.debug("blogPost.getBody() = "+blogPost.getBody());
                    //logger.debug("bodyHtml           = "+bodyHtml);
                    //logger.debug("blogPost.getBody().length() = "+blogPost.getBody().length());
                    //logger.debug("bodyHtml.length()           = "+bodyHtml.length());

                    blogPost.setBody(bodyHtml);
                    */

                    blogPostList.add(blogPost);

                } while(resultSet.next());

                if(blogPostList != null) logger.debug("blogPostList.size() = "+blogPostList.size());

                return blogPostList;

            } // if(resultSet.next()) {

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    public static BlogPost selectBlogPost(int blogPostId) throws SQLException, Exception {
        logger.debug("selectBlogPost("+blogPostId+", connectionManager)");

        ConnectionManager connectionManager = new ConnectionManager(className);

        try {

            /*
            String sqlFields = "id, hyphenated_name, title, subtitle, meta_description, body, date_published, datetime_published, enabled, priority";
            String sql = new StringBuilder()
                .append("SELECT ")
                .append(sqlFields)
                .append(" FROM blog_post WHERE id = ?")
                .toString();

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPost");
            preparedStatement.setInt(1, blogPostId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                BlogPost blogPost = new BlogPost();
                
                populateBlogPost(blogPost, resultSet);
                //populateBlogPost(blogPost, resultSet, connectionManager);

                selectBlogPostAuthors(blogPost, connectionManager);

                selectRelatedBlogPosts(blogPost, connectionManager);

                selectKeywords(blogPost, connectionManager);

                return blogPost;
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.debug(stringWriter.toString());
        } finally {
            connectionManager.commit();
        }

        return null;
    }

    //public static List<User> selectBlogPostAuthors(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
    public static void selectBlogPostAuthors(BlogPost blogPost, ConnectionManager connectionManager) throws SQLException, Exception {
        logger.debug("selectBlogPostAuthors(blogPost, connectionManager)");

        try {

            /*
            String sql = new StringBuilder()
                .append("SELECT ")
                .append("u.id, u.name, u.business_name, u.hyphenated_name, u.instagram_handle, u.location, u.theme_color, bpha.show_author")
                .append(" FROM user u, blog_post_has_author bpha")
                .append(" WHERE blog_post_id = ?")
                .append(" AND u.id = bpha.user_id")
                .toString();

            logger.debug(sql);

            PreparedStatement preparedStatement = connectionManager.prepareStatement(sql);
            */
            PreparedStatement preparedStatement = connectionManager.loadStatement("selectBlogPostAuthors");

            preparedStatement.setInt(1, blogPost.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                //List userList = new ArrayList();
                List<User> userList = null;
                User user = null;

                do {

                    boolean showAuthor = resultSet.getBoolean("showauthor");
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

}
