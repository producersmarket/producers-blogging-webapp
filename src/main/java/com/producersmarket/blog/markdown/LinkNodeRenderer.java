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

public class LinkNodeRenderer implements org.commonmark.renderer.NodeRenderer {

    private static final Logger logger = LogManager.getLogger();

    private final org.commonmark.renderer.html.HtmlWriter htmlWriter;

    public LinkNodeRenderer(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
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
