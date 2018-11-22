package com.producersmarket.blog.markdown;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
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

public class TextOnlyNodeRenderer implements NodeRenderer {

    private static final Logger logger = LogManager.getLogger();

    private final HtmlWriter htmlWriter;

    public TextOnlyNodeRenderer(HtmlNodeRendererContext htmlNodeRendererContext) {
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
        nodeTypes.add(StrongEmphasis.class);

        //Set<Class<? extends Node>> nodeTypes = new HashSet<Class<? extends Node>>(Arrays.asList(org.commonmark.node.Image.class, "b"));

        return nodeTypes;
    }

    @Override
    public void render(Node node) {
        //logger.debug("render("+node+")");

        //logger.debug("node instanceof Image = "+(node instanceof Image));
        //logger.debug("node instanceof Link = "+(node instanceof Link));

        if(node instanceof Link) {

            // We only handle one type as per getNodeTypes, so we can just cast it here.
            Link linkNode = (Link)node;

            //logger.debug("linkNode.Destination() = "+linkNode.getDestination());
            //logger.debug("linkNode.getTitle() = "+linkNode.getTitle());
            //logger.debug("linkNode.getFirstChild() = "+linkNode.getFirstChild());

            Node firstChildNode = node.getFirstChild(); // Blog link may be Image or Text

            //logger.debug("firstChildNode instanceof Image = "+(firstChildNode instanceof Image));
            //logger.debug("firstChildNode instanceof Text = "+(firstChildNode instanceof Text));

            if(firstChildNode instanceof Image) {

                // don't render images

            } else if(firstChildNode instanceof Text) {

                // render hyperlinks as just text without the link
                String literal = ((Text)firstChildNode).getLiteral();
                //logger.debug("literal = "+literal);

                if(literal != null) this.htmlWriter.text(literal);

            }

        } else if(node instanceof Image) {

            // don't render images

        } else if(node instanceof StrongEmphasis) {

            // don't render strong emphasis
            Node firstChildNode = node.getFirstChild(); // Blog link may be Image or Text

            if(firstChildNode != null) {

                //logger.debug("firstChildNode instanceof Text = "+(firstChildNode instanceof Text));

                if(firstChildNode instanceof Text) {

                    String literal = ((Text)firstChildNode).getLiteral();
                    logger.debug("literal = "+literal);

                    if(literal != null) this.htmlWriter.text(literal);

                } else if(firstChildNode instanceof StrongEmphasis) {
                    // don't render strong
                }

            }
            
        }


    }

}
