package com.producersmarket.blog.markdown;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.commonmark.renderer.NodeRenderer;

public class BlogImageNodeRenderer implements NodeRenderer {

    private static final Logger logger = LogManager.getLogger();

    private final HtmlWriter htmlWriter;

    public BlogImageNodeRenderer(HtmlNodeRendererContext htmlNodeRendererContext) {
        //logger.debug("("+htmlNodeRendererContext+")");

        this.htmlWriter = htmlNodeRendererContext.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        //logger.debug("getNodeTypes()");

        // Return the node types we want to use this renderer for.
        return Collections.<Class<? extends Node>>singleton(Image.class);
    }

    @Override
    public void render(Node node) {
        //logger.debug("render("+node+")");

        // we only handle one type as per getNodeTypes so we can just cast it here
        org.commonmark.node.Image imageNode = (Image)node;
        org.commonmark.node.Text textNode = (Text)imageNode.getFirstChild();

        /*
        logger.debug("imageNode = "+imageNode);
        logger.debug("textNode = "+textNode);
        //logger.debug("imageNode.Destination() = "+imageNode.getDestination());
        //logger.debug("imageNode.getTitle() = "+imageNode.getTitle());
        //logger.debug("imageNode.getFirstChild() = "+imageNode.getFirstChild());
        */

        // create the node attributes
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
