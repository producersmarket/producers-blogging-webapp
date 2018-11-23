package com.producersmarket.blog.markdown;

import java.util.Collections;
import java.util.HashSet;
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
        nodeTypes.add(Paragraph.class);
        nodeTypes.add(org.commonmark.node.Text.class);

        //Set<Class<? extends Node>> nodeTypes = new HashSet<Class<? extends Node>>(Arrays.asList(org.commonmark.node.Image.class, "b"));

        return nodeTypes;
    }

    @Override
    public void render(Node node) {
        logger.debug("render("+node+")");

        logger.debug("node.toString() = "+node.toString());
        logger.debug("    node instanceof Paragraph = "+(node instanceof Paragraph));
        logger.debug("    node instanceof Image = "+(node instanceof Image));
        logger.debug("    node instanceof Link = "+(node instanceof Link));
        logger.debug("    node instanceof StrongEmphasis = "+(node instanceof StrongEmphasis));

        if(node instanceof Link) {
            logger.debug("    node instanceof Link");

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
                logger.debug("        literal = "+literal);

                if(literal != null) this.htmlWriter.text(literal);

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
            logger.debug("    node instanceof Image");

            // don't render images

        } else if(node instanceof StrongEmphasis) {
            logger.debug("    node instanceof StrongEmphasis");

            // don't render strong emphasis
            Node firstChildNode = node.getFirstChild(); // Blog link may be Image or Text

            if(firstChildNode != null) {

                //logger.debug("firstChildNode instanceof Text = "+(firstChildNode instanceof Text));

                if(firstChildNode instanceof Text) {

                    String literal = ((Text)firstChildNode).getLiteral();
                    logger.debug("        literal = "+literal);

                    if(literal != null) this.htmlWriter.text(literal);

                } else if(firstChildNode instanceof StrongEmphasis) {
                    // don't render strong
                }

            }

        } else if(node instanceof Paragraph) {
            logger.debug("    node instanceof Paragraph");

            /*
            Node firstChildNode = node.getFirstChild(); // Blog link may be Image or Text
            logger.debug("firstChildNode.toString() = "+firstChildNode.toString());

            if(firstChildNode != null) {
                if(firstChildNode instanceof Text) {

                    String literal = ((Text)firstChildNode).getLiteral();
                    logger.debug("        literal = "+literal);

                    if(literal != null) this.htmlWriter.text(literal);
                }
            }
            */

            Node nextNode = node.getFirstChild(); // Blog link may be Image or Text

            do {

                logger.debug("nextNode = "+nextNode);
                logger.debug("    nextNode instanceof Paragraph = "+(nextNode instanceof Paragraph));
                logger.debug("    nextNode instanceof Image = "+(nextNode instanceof Image));
                logger.debug("    nextNode instanceof Link = "+(nextNode instanceof Link));
                logger.debug("    nextNode instanceof StrongEmphasis = "+(nextNode instanceof StrongEmphasis));
                logger.debug("    nextNode instanceof Text = "+(nextNode instanceof Text));


                if(nextNode instanceof Text) {

                    String literal = ((Text)nextNode).getLiteral();
                    logger.debug("        literal = "+literal);

                    if(literal != null) this.htmlWriter.text(literal);

                } else if(nextNode instanceof Link) {
                    logger.debug("    nextNode instanceof Link");

                    Node nextNodeFirstChild = nextNode.getFirstChild();
                    logger.debug("        nextNodeFirstChild.toString() = "+nextNodeFirstChild.toString());

                    if(nextNodeFirstChild instanceof Text) {

                        String literal = ((Text)nextNodeFirstChild).getLiteral();
                        logger.debug("        literal = "+literal);
                        if(literal != null) this.htmlWriter.text(literal);
                    }

                } else if(nextNode instanceof Paragraph) {
                    logger.debug("    nextNode instanceof Paragraph");

                    Node nextNodeFirstChild = nextNode.getFirstChild();
                    logger.debug("        nextNodeFirstChild.toString() = "+nextNodeFirstChild.toString());

                    if(nextNodeFirstChild instanceof Text) {

                        String literal = ((Text)nextNodeFirstChild).getLiteral();
                        logger.debug("        literal = "+literal);
                        if(literal != null) this.htmlWriter.text(literal);
                    }

                }

                nextNode = nextNode.getNext();

            } while(nextNode != null);

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
        }

    }

}