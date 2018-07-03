package com.producersmarket.blog.markdown;

/*
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.annotation.WebServlet;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import com.producersmarket.manager.BlogPostManager;
import com.producersmarket.model.BlogPost;
import com.producersmarket.model.User;
import com.producersmarket.servlet.ParentServlet;
*/
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class BlogImageNodeRenderer implements org.commonmark.renderer.NodeRenderer {

    private static final Logger logger = LogManager.getLogger();

    private final org.commonmark.renderer.html.HtmlWriter htmlWriter;

    public BlogImageNodeRenderer(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
        //logger.debug("("+htmlNodeRendererContext+")");

        this.htmlWriter = htmlNodeRendererContext.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        //logger.debug("getNodeTypes()");

        // Return the node types we want to use this renderer for.
        return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Image.class);
    }

    @Override
    public void render(Node node) {
        //logger.debug("render("+node+")");

        // We only handle one type as per getNodeTypes, so we can just cast it here.
        org.commonmark.node.Image imageNode = (org.commonmark.node.Image)node;
        org.commonmark.node.Text textNode = (org.commonmark.node.Text)imageNode.getFirstChild();

        /*
        logger.debug("imageNode = "+imageNode);
        logger.debug("textNode = "+textNode);
        //logger.debug("imageNode.Destination() = "+imageNode.getDestination());
        //logger.debug("imageNode.getTitle() = "+imageNode.getTitle());
        //logger.debug("imageNode.getFirstChild() = "+imageNode.getFirstChild());
        */

        // Create the node attributes
        Map<String,String> attributeMap = new HashMap() {{
            put("src", imageNode.getDestination());
            //put("alt", imageNode.getTitle()); // title can be null
        }};

        if(textNode != null) { // textNode can be empty/null
            String altText = textNode.getLiteral();
            if(altText != null) attributeMap.put("alt", altText);
        }

        String imageTitle = imageNode.getTitle();              

        //this.htmlWriter.line();
        this.htmlWriter.tag("figure");
        this.htmlWriter.tag("img", attributeMap);
        if(imageTitle != null) {                
            this.htmlWriter.tag("figcaption");
            this.htmlWriter.text(imageTitle);
            this.htmlWriter.tag("/figcaption");
        }
        this.htmlWriter.tag("/figure");
        //this.htmlWriter.line();
    }
    
}
