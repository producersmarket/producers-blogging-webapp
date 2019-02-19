package com.producersmarket.blog.markdown;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.commonmark.renderer.NodeRenderer;

/*
 * https://github.com/atlassian/commonmark-java
 * https://www.javadoc.io/doc/com.atlassian.commonmark/commonmark/0.10.0
 */

public class CustomImagesAndLinksRenderer implements NodeRenderer {

    private static final Logger logger = LogManager.getLogger();

    private final HtmlWriter htmlWriter;

    public CustomImagesAndLinksRenderer(HtmlNodeRendererContext htmlNodeRendererContext) {
        //logger.debug("("+htmlNodeRendererContext+")");

        this.htmlWriter = htmlNodeRendererContext.getWriter();
    }

    /*
     * https://stackoverflow.com/questions/2041778/how-to-initialize-hashset-values-by-construction
     */
    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        //logger.debug("getNodeTypes()");

        // Return the node types we want to use this renderer for.
        //return Collections.<Class<? extends Node>>singleton(org.commonmark.node.Image.class);

        /*
        Set<Class<? extends Node>> imageNodeClassSet = Collections.<Class<? extends Node>>singleton(org.commonmark.node.Image.class);
        logger.debug("imageNodeClassSet.size() = "+imageNodeClassSet.size());
        Set<Class<? extends Node>> linkNodeClassSet = Collections.<Class<? extends Node>>singleton(org.commonmark.node.Link.class);
        logger.debug("imageNodeClassSet.size() = "+imageNodeClassSet.size());
        */

        Set<Class<? extends Node>> nodeTypes = new HashSet<Class<? extends Node>>();

        nodeTypes.add(Image.class);
        nodeTypes.add(Link.class);
        //nodeTypes.add(org.commonmark.node.Text.class);

        //Set<Class<? extends Node>> nodeTypes = new HashSet<Class<? extends Node>>(Arrays.asList(org.commonmark.node.Image.class, "b"));

        return nodeTypes;
    }

    @Override
    public void render(Node node) {
        logger.debug("render("+node+")");

        /*
        logger.debug("node.toString() = "+node.toString());
        logger.debug("    node instanceof Image = "+(node instanceof Image));
        logger.debug("    node instanceof Link = "+(node instanceof Link));
        */

        if(node instanceof Link) {
            //logger.debug("    node instanceof Link");

            Link linkNode = (Link)node;

            //logger.debug("linkNode.Destination() = "+linkNode.getDestination());
            //logger.debug("linkNode.getTitle() = "+linkNode.getTitle());
            //logger.debug("linkNode.getFirstChild() = "+linkNode.getFirstChild());

            Node firstChildNode = node.getFirstChild(); // Blog link may be Image or Text

            //logger.debug("firstChildNode instanceof Image = "+(firstChildNode instanceof Image));
            //logger.debug("firstChildNode instanceof Text = "+(firstChildNode instanceof Text));

            if(firstChildNode instanceof Image) {

                this.renderImageNode(firstChildNode);

            } else if(firstChildNode instanceof Text) {

                // create the node attributes
                Map<String,String> attributeMap = new HashMap() {{
                    put("href", linkNode.getDestination());
                    put("target", "_blank");
                }};

                this.htmlWriter.tag("a", attributeMap);

                org.commonmark.node.Text textNode = (Text)firstChildNode;

                if(textNode != null) { // textNode can be empty/null

                    String literal = textNode.getLiteral();
                    //logger.debug("literal = "+literal);

                    if(literal != null) this.htmlWriter.text(literal);
                }

                this.htmlWriter.tag("/a");
            }

            /*
            Node nextNode;
            while((nextNode = node.getNext()) != null) {

                logger.debug("nextNode = "+nextNode);

                if(nextNode instanceof Text) {
                    String literal = ((Text)nextNode).getLiteral();
                    if(literal != null) this.htmlWriter.text(literal);
                }

            }
            */

        } else if(node instanceof Image) {
            //logger.debug("    node instanceof Image");

            this.renderImageNode(node);

        /*
        } else if(node instanceof Text) {
            logger.debug("    node instanceof Text");

            Node firstChildNode = node.getFirstChild(); // Blog link may be Image or Text
            if(firstChildNode != null) {
                if(firstChildNode instanceof Text) {
                    String literal = ((Text)firstChildNode).getLiteral();
                    logger.debug("        literal = "+literal);
                    if(literal != null) this.htmlWriter.text(literal);
                }
            }
        */
        }

    }

    public void renderImageNode(Node node) {
        //logger.debug("renderImageNode("+node+")");

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
