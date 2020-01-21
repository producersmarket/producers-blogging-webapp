package com.producersmarket.blog.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;

import javax.servlet.annotation.WebServlet;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.producersmarket.blog.database.BlogPostDatabaseManager;
import com.producersmarket.blog.database.MissingBlogPostDatabaseManager;
import com.producersmarket.blog.markdown.BlogImageNodeRenderer;
import com.producersmarket.blog.model.BlogPost;
import com.producersmarket.blog.model.User;
//import com.producersmarket.blog.servlet.ParentServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/*
@WebServlet(
    name = "BlogPostServlet"
    , asyncSupported=true
    , urlPatterns = { 
          "/post"
        , "/post/*"
    }
)
*/
public class MissingBlogPostServlet extends com.producersmarket.blog.servlet.ParentServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final String ZERO = "0";
    private static final String FORWARD_SLASH = "/";
    //private ServletConfig servletConfig = null;
    private String contextUrl = null;
    private Map<String, String> redirectMap = null;

    public void init(ServletConfig servletConfig) throws ServletException {
        logger.debug("init("+servletConfig+")");

        //this.servletConfig = servletConfig;
        this.contextUrl = (String)servletConfig.getServletContext().getAttribute("contextUrl");

        logger.debug("this.contextUrl = "+this.contextUrl);

        this.redirectMap = new HashMap<String, String>() {{
                    put("what-is-nutrient-density", "Nutrient-Density");
                    put("amsterdam-produce-show-and-conference", "Amsterdam-Produce-Show-and-Conference-2017");
                    put("another-visit-to-the-berkeley-bowl", "Another-Visit-to-the-Berkeley-Bowl-Legendary-Supermarket-in-Berkeley-California");
                    //put("coopessa-now-global-gap-certified", "Coopessa-Now-Global-GAP-Certified-Organic-Certified-Pineapple-from-Costa-Rica");
                    put("designing-a-mexican-organic-avocado-brand", "Designing-a-Mexican-Organic-Avocado-Brand-Michoacan-Organics");
                    put("flores-el-capiro-visit-to-a-world-class-cut-flower-grower-in-antioquia-colombia", "Flores-El-Capiro-Visit-To-World-Class-Cut-Flower-Grower-Antioquia-Colombia");
                    put("garrido-coffee-panama-geisha-coffee-pt-2", "Garrido-Coffee-Panama-Geisha-Coffee");
                    put("garrido-coffee-pt-1", "Garrido-Coffee-Achieving-a-96-Score");
                    put("hass-avocado-in-colombia", "Hass-Avocado-in-Colombia-World-Avocado-Congress-and-Avocado-Swimming-Pools");
                    put("michoacan-organics-avocados-store-photos", "Michoacan-Organics-Avocados-Photos-from-the-Store-with-Founder-Leonel-Chavez");
                    put("michoacan-organics-avocado-logistics", "Michoacan-Organics-Organic-Avocado-Logistics-Farm-in-Mexico-to-Store-in-the-US");
                    put("organic-lime-workshop-in-veracruz-mexico", "Organic-Lime-Workshop-in-Veracruz-Mexico-with-Michoacan-Organics-and-Dr-Jose-Maria-Anguiano-Cardenas");
                    put("simply-natural-organic-mango-farm-and-investment", "Simply-Natural-Organic-Mango-Farm-and-Investment-3-Years-Later");
                    put("singing-frogs-farm", "Singing-Frogs-Farm-Visit-to-an-Organic-Veggies-Farm-in-Northern-California");
                    put("the-amazing-abundance-of-mango", "The-Amazing-Abundance-of-Mango-in-Panama-and-the-Tropics");
                    put("visit-to-cano-cristales-in-la-macarena-colombia", "Visit-to-Cano-Cristales-in-La-Macarena-Colombia-Tourism-Stop-in-Colombia");
                    put("visit-to-organic-avocado-farm-and-packing-house-in-michoacan-mexico-part1", "Visit-to-Organic-Avocado-Farm-and-Packing-House-in-Michoacan-Mexico-Part1-Leonel-and-the-Packing-House");
                    put("visit-to-organic-avocado-farm-and-packing-house-in-michoacan-mexico-part2", "Visit-to-Organic-Avocado-Farm-and-Packing-House-in-Michoacan-Mexico-Part2-Breakfast-at-Rinconcito");
                    put("visit-to-organic-avocado-farm-and-packing-house-in-michoacan-mexico-part3", "Visit-to-Organic-Avocado-Farm-and-Packing-House-in-Michoacan-Mexico-Part3-Marias-Organic-Avocado-Farm");
                    put("visit-to-organic-avocado-farm-and-packing-house-in-michoacan-mexico-part4", "Visit-to-Organic-Avocado-Farm-and-Packing-House-in-Michoacan-Mexico-Part4-Leonels-Farm");
                    put("visit-to-the-berkeley-bowl-1", "Visit-to-the-Berkeley-Bowl-Market-Study-and-Nourishment");
                    put("why-there-is-no-lime-industry-in-america-anymore", "Why-There-is-No-Lime-Industry-in-America-Anymore-The-Opportunity-in-Limes-Time-Magazine");
                    put("leading-organic-iqf-pineapple-grower", "Visit-to-LyL-Proyectos-A-Leading-Organic-IQF-Pineapple-Grower");
                    put("organic-fruit-nursery-in-costa-rica", "Visit-to-Zill-Nursery-Organic-Fruit-Nursery-in-Costa-Rica");
                    put("a-visit-to-roadside-fruit-markets", "Fruit Abundance-in-Costa-Rica-A-Visit-to-Roadside-Fruit-Markets");
                    put("coopeassa-time-for-first-harvest", "Coopeassa-Organic-Fair-Trade-Pineapple-from-Costa-Rica-Now-Harvesting");
                    put("an-ecological-balance-in-organic-avocado", "Cultivation-in-Michoacan-Mexico-An-Ecological-Balance-in-Organic-Avocado");
                    put("el-valle-hike", "A-Hike-up-India-Dormida-Mountain-El-Valle-Panama");
                    put("follow-the-palo", "Follow-the-Palo-Sustainably-Sourced-Palo-Santo-Products-from-Ecuador");
                    put("intermittent fasting", "Intermittent-Fasting");
                    put("coopessa-now-global-gap-certified-organic-certified-pineapple-from-costa-rica", "Coopessa-now-Global-GAP-Certified");

                }};


        super.init(servletConfig);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("doGet(request, response)");

        try {

            String blogPostName = null;
            String pathInfo = request.getPathInfo();
            logger.debug("pathInfo = "+pathInfo);

            if(pathInfo != null) {

                blogPostName = pathInfo.substring(1, pathInfo.length());
                logger.debug("blogPostName = "+blogPostName);

                if(blogPostName != null && blogPostName.length() > 0) {

                    blogPostRequest(
                        request
                      , response
                      , blogPostName.toLowerCase()
                    );

                    return;
                }

            } // if(pathInfo != null) {

        } catch(Exception e) {

            logger.warn(new StringBuilder().append("Error in: ").append(getClass().getName()).append(" ").append(e.getMessage()).toString());

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

        writeOut(response, ZERO);
    }

    public void blogPostRequest(
        HttpServletRequest request
      , HttpServletResponse response
      , String blogPostName
    ) throws IOException, ServletException {

        logger.debug("blogPostRequest(request, response, '"+blogPostName+"')");

        //include(request, response, "/view/blog/"+blogPostName+".jsp");

        //request.setAttribute("blogPostNameHyphenated", blogPostName);

        try {

            BlogPost blogPost = BlogPostDatabaseManager.selectBlogPostByHyphenatedName(blogPostName, getConnectionPool());

            if(blogPost != null) {

                logger.debug(blogPost.getId()+" "+blogPost.getHyphenatedName());

                Parser parser = Parser.builder().build();
                Node document = parser.parse(blogPost.getBody());
                //HtmlRenderer renderer = HtmlRenderer.builder().build();
                HtmlRenderer renderer = HtmlRenderer.builder()
                    .nodeRendererFactory(new org.commonmark.renderer.html.HtmlNodeRendererFactory() {
                        public org.commonmark.renderer.NodeRenderer create(org.commonmark.renderer.html.HtmlNodeRendererContext htmlNodeRendererContext) {
                            return new BlogImageNodeRenderer(htmlNodeRendererContext);
                        }
                    }).build();

                String bodyHtml = renderer.render(document);

                /*
                logger.debug("blogPost.getBody() = "+blogPost.getBody());
                logger.debug("bodyHtml           = "+bodyHtml);
                logger.debug("blogPost.getBody().length() = "+blogPost.getBody().length());
                logger.debug("bodyHtml.length()           = "+bodyHtml.length());
                */

                blogPost.setBody(bodyHtml);

                request.setAttribute("blogPost", blogPost);

                includeUtf8(request, response, "/view/blog/blog-post.jsp");

            } else { // if(blogPostList != null) {

                String errorMessage = "Blog Post Not Found! "+blogPostName;
                logger.debug(errorMessage);
                logger.warn(errorMessage);
                logger.error(errorMessage);

                MissingBlogPostDatabaseManager.insertBlogPostName(blogPostName, getConnectionPool());

                String redirectName = redirectMap.get(blogPostName);
                logger.debug("redirectName = "+redirectName);

                if(redirectName != null) {

                    String redirectLocation = new StringBuilder()
                            .append(this.contextUrl)
                            .append("/post/")
                            .append(redirectName)
                            .toString();

                    logger.debug("redirectLocation = "+redirectLocation);

                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    response.setHeader("Location", redirectLocation);
                    response.setHeader("Connection", "close");

                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }

        } catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error(stringWriter.toString());
        }

    }

    /*
     * https://www.javadoc.io/doc/com.atlassian.commonmark/commonmark/0.10.0
     * https://static.javadoc.io/com.atlassian.commonmark/commonmark/0.10.0/org/commonmark/node/Image.html
     * https://static.javadoc.io/com.atlassian.commonmark/commonmark/0.10.0/org/commonmark/renderer/html/HtmlWriter.html
     * <figure><img src="/content/images/organic-avocado-farm-and-packing-house-michoacan-mexico/organic-avocados-lionel-packed-box_600x800.jpg" alt="Leonel Chavez, CEO of Michocan Organics" /><figcaption>Leonel Chavez, CEO of Michocan Organics</figcaption></figure>
     * http://digitaldrummerj.me/styling-jekyll-markdown/
     * https://github.com/atlassian/commonmark-java/blob/master/commonmark/src/main/java/org/commonmark/node/Link.java
     */

}
